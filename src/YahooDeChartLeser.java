/**
 @author Thomas Much
 @version 2003-02-25
*/

import java.io.*;
import java.net.*;
import java.awt.*;




public class YahooDeChartLeser implements Runnable {

private static final String YAHOO_URL      = "http://de.finance.yahoo.com/q?d=c&s=";
private static final String YAHOO_ENCODING = "ISO-8859-1";

private final String wkn,boerse;
private final int time,type;
private ChartReceiver receiver;
private ChartQuelle quelle,first;




public YahooDeChartLeser(ChartQuelle quelle, ChartReceiver receiver, String wkn, String boerse, int time, int type, ChartQuelle first) {

	this.quelle = quelle;
	this.receiver = receiver;
	this.wkn = wkn;
	this.boerse = boerse;
	this.time = time;
	this.type = type;
	this.first = first;
}



public void run() {

	/* TODO: reload-Flag, stoppbar */

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	Connections.getConnection();
	
	String url = findChartURL();

	if (url != null)
	{
		if ((url.length() == 0) || (!loadChartImage(url)))
		{
			receiver.setError(wkn,boerse,time,type,ChartReceiver.STATUS_ERROR,first,quelle);
		}
	}

	Connections.releaseConnection();
}



private String findChartURL() {

	String request = getRequestURL();
	
	if (request == null) return null;

	System.out.println("CHART-ANFRAGE: "+request);  // TODO

	BufferedReader in = null;

	try
	{
		URL url = new URL(request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream(), YAHOO_ENCODING));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() == 0) continue;
			
			int i = s.indexOf("chart.");
			
			if (i > 0)
			{
				int left = s.lastIndexOf('"',i);
				int right = s.indexOf('"',i);
				
				if ((left >= 0) && (right > left))
				{
					return s.substring(left+1,right);
				}
			}
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Yahoo.de-Chartleser (url)", e);
	}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{
				AktienMan.errlog("Fehler im Yahoo.de-Chartleser (url)", e);
			}
		}
	}

	receiver.setError(wkn,boerse,time,type,ChartReceiver.STATUS_ERROR,first,quelle);
	return null;
}



private boolean loadChartImage(String chartURL) {

	boolean success = false;

	System.out.println("CHART-IMAGE: "+chartURL);  // TODO

	InputStream in = null;

	try
	{
		URL url = new URL(chartURL);

		URLConnection curl = url.openConnection();

		curl.setUseCaches(false);
		
		int dataLen = curl.getContentLength();

		System.out.println("  ContentLength "+dataLen+"  ContentType "+curl.getContentType());  // TODO

		if ("image/gif".equals(curl.getContentType()))
		{
			byte[] daten;
			
			in = new BufferedInputStream(curl.getInputStream());
			
			if (dataLen > 0)
			{
				daten = new byte[dataLen];
				
				in = new DataInputStream(in);
				
				((DataInputStream)in).readFully(daten);
			}
			else
			{
				daten = loadImageData(in);
			}

			Image image = Toolkit.getDefaultToolkit().createImage(daten);
			
			receiver.setImage(wkn,boerse,time,type,image,daten,first,quelle);
			
			success = true;
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Yahoo.de-Chartleser (image)", e);
	}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{
				AktienMan.errlog("Fehler im Yahoo.de-Chartleser (image)", e);
			}
		}
	}
	
	return success;
}



private byte[] loadImageData(InputStream in) throws IOException {

	byte[] buffer = new byte[2048];
	byte[] data = null;
	
	int count;
	
	while ((count = in.read(buffer)) != -1)
	{
		System.out.println("  gelesene Bytes: "+count); // TODO

		int datalen = (data == null) ? 0 : data.length;

		byte[] newdata = new byte[datalen + count];
		
		if (datalen > 0)
		{
			// dann ist auch data != null sichergestellt
			System.arraycopy(data,0,newdata,0,datalen);
		}
		
		System.arraycopy(buffer,0,newdata,datalen,count);
		
		data = newdata;
	}
	
	return data;
}



private String getRequestURL() {

	String time = yahooTime();
	
	if (time == null)
	{
		receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR_TIME,first,quelle);
		return null;
	}
	
	String type = yahooType();

	if (type == null)
	{
		receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR_TYPE,first,quelle);
		return null;
	}

	StringBuffer req = new StringBuffer(YAHOO_URL);

	req.append( wkn );
	req.append( yahooBoerse() );
	req.append( "&t=" );
	req.append( time );
	req.append( "&q=" );
	req.append( type );
	
	return req.toString();
}



private String yahooType() {

	switch(type)
	{
	case ChartQuellen.TYPE_OHLC:
	
		return "b";

	case ChartQuellen.TYPE_KERZEN:
	
		return "c";
	
	case ChartQuellen.TYPE_LINIEN:

		return "l";
	}
	
	return null;
}



private String yahooTime() {

	switch(time)
	{
	case ChartQuellen.TIME_1D:
	
		return "1d";
	
	case ChartQuellen.TIME_5D:
	
		return "5d";
	
	case ChartQuellen.TIME_3M:
	
		return "3m";
	
	case ChartQuellen.TIME_6M:
	
		return "6m";
	
	case ChartQuellen.TIME_1Y:
	
		return "1y";

	case ChartQuellen.TIME_2Y:
	
		return "2y";
	
	case ChartQuellen.TIME_5Y:
	
		return "5y";
	
	case ChartQuellen.TIME_MAX:
	
		return "my";
	}
	
	return null;
}



private String yahooBoerse() {

	if (Boersenliste.ID_XETRA.equalsIgnoreCase(boerse))
	{
		return ".DE";
	}
	else if (Boersenliste.ID_FRANKFURT.equalsIgnoreCase(boerse))
	{
		return ".F";
	}
	else if (Boersenliste.ID_FONDS_DE.equalsIgnoreCase(boerse))
	{
		return ".DE";
	}
	else if (Boersenliste.ID_BERLIN.equalsIgnoreCase(boerse))
	{
		return ".BE";
	}
	else if (Boersenliste.ID_BREMEN.equalsIgnoreCase(boerse))
	{
		return ".BM";
	}
	else if (Boersenliste.ID_DUESSELDORF.equalsIgnoreCase(boerse))
	{
		return ".D";
	}
	else if (Boersenliste.ID_HAMBURG.equalsIgnoreCase(boerse))
	{
		return ".H";
	}
	else if (Boersenliste.ID_HANNOVER.equalsIgnoreCase(boerse))
	{
		return ".HA";
	}
	else if (Boersenliste.ID_MUENCHEN.equalsIgnoreCase(boerse))
	{
		return ".MU";
	}
	else if (Boersenliste.ID_STUTTGART.equalsIgnoreCase(boerse))
	{
		return ".SG";
	}
	
	return "";
}


}
