/**
 @author Thomas Much
 @version 2003-03-04
*/

import java.io.*;
import java.net.*;
import java.awt.*;




public class ComdirectChartLeser implements Runnable {

private final String wkn,boerse;
private final int time,type;
private ChartReceiver receiver;
private ChartQuelle quelle,first;




public ComdirectChartLeser(ChartQuelle quelle, ChartReceiver receiver, String wkn, String boerse, int time, int type, ChartQuelle first) {

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

	try
	{
		Connections.getConnection();
		
		String time = comdirectTime();

		if (time == null)
		{
			receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR_TIME,first,quelle);
			return;
		}
		
		String type = comdirectType();

		if (type == null)
		{
			receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR_TYPE,first,quelle);
			return;
		}
		
		String symbol = findSymbol();

		if (symbol != null)
		{
			String url = findChartURL(symbol,time,type);
			
			if (url != null)
			{
				if ((url.length() == 0) || (!loadChartImage(url)))
				{
					receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR,first,quelle);
				}
			}
		}
	}
	finally
	{
		Connections.releaseConnection();
	}
}



private boolean loadChartImage(String chartURL) {

	boolean success = false;

	System.out.println("CHART-IMAGE: "+chartURL);  // TODO

	DataInputStream in = null;

	try
	{
		URL url = new URL(chartURL);

		URLConnection curl = url.openConnection();

		curl.setUseCaches(false);
		
		int dataLen = curl.getContentLength();

		System.out.println("  ContentLength "+dataLen+"  ContentType "+curl.getContentType());  // TODO

		if ("image/gif".equals(curl.getContentType()))
		{
			if (dataLen > 0)
			{
				in = new DataInputStream(new BufferedInputStream(curl.getInputStream()));

				byte[] daten = new byte[dataLen];
				
				((DataInputStream)in).readFully(daten);

				Image image = Toolkit.getDefaultToolkit().createImage(daten);
				
				receiver.setImage(wkn,boerse,time,type,image,daten,first,quelle);
				
				success = true;
			}
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Comdirect-Chartleser (image)", e);
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
				AktienMan.errlog("Fehler im Comdirect-Chartleser (image)", e);
			}
		}
	}
	
	return success;
}



private String findChartURL(String symbol, String time, String type) {

	final String request = "http://informer2.comdirect.de/de/default/_pages/charts/main.html?sSymbol="+symbol+"&sTimeframe="+time+"&useSettings=0&showSettings=&sid=&hiddenTimeFrame=&selected=chart&disclaimer=ok&alias=chartseite&sOrdType=price&sScale=linear&sMarket="+symbol+"&iType="+type+"&sHistoryCorrection=0&sAv1=38&sAvfree1=&sAv2=200&sAv2free2=&sAv2count=1&iInd0=1&sBench1=na&sBenchcount=1&sBench2=&sBench2count=1&showBenchmarkSearch=&iInd1=1&iInd2=6&iIndcount=1&sSettings=na";

	System.out.println("CHART-ANFRAGE: "+request);  // TODO

	BufferedReader in = null;

	try
	{
		URL url = new URL(request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() == 0) continue;
			
			int i = s.indexOf("cdchart.");
			
			if (i > 0)
			{
				int left = s.lastIndexOf('"',i);
				int right = s.indexOf('"',i);
				
				if (right > left)
				{
					return s.substring(left+1,right);
				}
			}
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Comdirect-Chartleser (url)", e);
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
				AktienMan.errlog("Fehler im Comdirect-Chartleser (url)", e);
			}
		}
	}

	receiver.setError(wkn,boerse,this.time,this.type,ChartReceiver.STATUS_ERROR,first,quelle);
	return null;
}



private String findSymbol() {

	// wir brauchen hier keine Umwandlungsroutinen, da die interne Bšrsen-ID der Comdirect-Bšrse entspricht
	final String request = "http://informer2.comdirect.de/de/suche/main.html?sid=&alias=wertpapiersuche&searchfor="+wkn+"&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen="+boerse;

	System.out.println("SYMBOL-ANFRAGE: "+request);  // TODO

	BufferedReader in = null;

	try
	{
		URL url = new URL(request);
		
		in = new BufferedReader(new InputStreamReader( NetUtil.getRedirectedInputStream(url) ));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() == 0) continue;
			
			int i = s.indexOf("sSymbol=");
			
			if (i > 0)
			{
				int i2 = s.indexOf('&',i);
				
				if (i2 < 0)
				{
					return s.substring(i+8);
				}
				else
				{
					return s.substring(i+8,i2);
				}
			}
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Comdirect-Chartleser (symbol)", e);
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
				AktienMan.errlog("Fehler im Comdirect-Chartleser (symbol)", e);
			}
		}
	}

	receiver.setError(wkn,boerse,time,type,ChartReceiver.STATUS_ERROR,first,quelle);
	return null;
}



private String comdirectType() {

	switch(type)
	{
	case ChartQuellen.TYPE_LINIEN:

		return "1";
	
	case ChartQuellen.TYPE_BALKEN:

		return "2";

	case ChartQuellen.TYPE_OHLC:
	
		return "3";

	case ChartQuellen.TYPE_KERZEN:
	
		return "4";
	}
	
	return null;
}



private String comdirectTime() {

	switch(time)
	{
	case ChartQuellen.TIME_1D:
	
		return "iD";
	
	case ChartQuellen.TIME_5D:
	
		return "5D";
	
	case ChartQuellen.TIME_10D:
	
		return "10D";
	
	case ChartQuellen.TIME_3M:
	
		return "3M";
	
	case ChartQuellen.TIME_6M:
	
		return "6M";
	
	case ChartQuellen.TIME_1Y:
	
		return "1Y";
	
	case ChartQuellen.TIME_5Y:
	
		return "5Y";
	
	case ChartQuellen.TIME_MAX:
	
		return "10Y";
	}
	
	return null;
}


}
