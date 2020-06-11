/**
 @author Thomas Much
 @version 1999-03-14
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;



public final class IntradayChartLeser extends Thread {

private String boerse;
private BenutzerAktie ba;
private ChartViewer chartviewer;



public IntradayChartLeser(String boerse, BenutzerAktie ba) {
	super();
	this.boerse = boerse;
	this.ba = ba;
}


public void run() {
	BufferedReader in = null;
	boolean valid = false;
	
	String wkn = ba.getWKNString();

	chartviewer = new ChartViewer(null,"Intraday "+ba.getName(true),"",670,285,ChartViewer.TYPE_INTRADAY);
	
	try
	{
		URL url = new URL(URLs.DAXREALTIME);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf("<TR>") >= 0)
			{
				int i = s.indexOf(wkn);
				
				if (i > 0)
				{
					int i2 = s.indexOf(">",s.indexOf(">",i)+1);
					int i3 = s.indexOf("<",i2);
					
					new ChartLoader(chartviewer,URLs.CHART_DAXINTRADAY + s.substring(i2+1,i3).trim() + "." + boerse + ".EUR.gif",true).start();
					valid = true;
					break;
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Exchange-URL fehlerhaft.");
	}
	catch (IOException e) {}
	finally
	{
		if (!valid)
		{
			chartviewer.setStatusError();
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
	}
}

}
