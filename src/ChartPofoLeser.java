/**
 @author Thomas Much
 @version 2000-11-11
*/

import java.net.*;
import java.io.*;




public abstract class ChartPofoLeser extends Thread {

private static final int STANDARDTIMEOUTSEKUNDEN = 300;

private static long timeoutMillis = 0L;

private String wkn,boerse;
private int index,type;
private IntradayChartsPortfolio parent;
private long timeout;




public ChartPofoLeser(IntradayChartsPortfolio parent, int index, String wkn, String boerse, long timeout, int type) {

	super();

	this.parent = parent;
	this.index  = index;
	this.wkn    = wkn;
	this.boerse = boerse;

	this.timeout = timeout;
	
	this.type = type;
}



protected String getWKN() {

	return wkn;
}



protected String getBoerse() {

	return boerse;
}



protected int getIndex() {

	return index;
}



protected long getTimeout() {

	long old = timeout;
	
	timeout = getTimeoutMillis();
	
	return old;
}



protected int getType() {

	return type;
}



private boolean doLoop() {

	return (getType() == URLs.CHART_INTRA);
}



public synchronized void parentClosed() {

	parent = null;
}



protected synchronized IntradayChartsPortfolio getParent() {

	return parent;
}



protected void loadChart(String imgURL) {

	if (imgURL.length() == 0) return;

	DataInputStream in;
	boolean valid;

	do
	{
		in = null;
		valid = false;
		
		try
		{
			Connections.getConnection();

			URL url = new URL(imgURL);
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);
			
			synchronized(this)
			{
				IntradayChartsPortfolio parent = getParent();
				
				if (parent != null)
				{
					parent.setChartImage(getIndex(),AktienMan.hauptdialog.getToolkit().createImage(daten));
					valid = true;
				}
			}
		}
		catch (MalformedURLException e)
		{
			System.out.println("URL des Charts fehlerhaft.");
		}
		catch (Exception e) {}
		finally
		{
			if (!valid)
			{
				synchronized(this)
				{
					IntradayChartsPortfolio parent = getParent();
				
					if (parent != null)
					{
						parent.setChartError(getIndex());
					}
				}
			}
			
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e) {}
			
				in = null;
			}

			Connections.releaseConnection();
		}

		if (getParent() != null)
		{
			try
			{
				sleep(getTimeout());
			}
			catch (InterruptedException e) {}
		}
		
		if ((!doLoop()) || (!getIntradayTimeout()))
		{
			parentClosed();
		}

	} while (getParent() != null);
}



public synchronized static long getTimeoutMillis() {

	if (timeoutMillis <= 0L)
	{
		timeoutMillis = 1000L * (long)AktienMan.properties.getInt("Konfig.IntradayTimeoutSekunden",STANDARDTIMEOUTSEKUNDEN);
	}
	
	return timeoutMillis;
}



public synchronized static String getTimeoutMinutenString() {

	return "" + ((int)(getTimeoutMillis() / 60000L));
}



public synchronized static void setTimeoutMinuten(int minuten) {

	AktienMan.properties.setInt("Konfig.IntradayTimeoutSekunden",minuten*60);
	timeoutMillis = (long)minuten * 60000L;
}



public synchronized static boolean getIntradayTimeout() {

	return AktienMan.properties.getBoolean("Konfig.IntraTimeout",true);
}



public synchronized static void setIntradayTimeout(boolean intra) {

	AktienMan.properties.setBoolean("Konfig.IntraTimeout",intra);
}

}
