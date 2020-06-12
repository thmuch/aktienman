/**
 @author Thomas Much
 @version 2002-01-13
 
 2002-01-13
   run findet nun wieder die Chart-URL; das URL-Ende wird mit dem letzten Zeichen
     von str_chartsrc gesucht
*/

import java.net.*;
import java.io.*;




public final class ComdirectChartLoader extends Thread {

private String filename;
private ChartViewer chartviewer;
private boolean doReload;




public ComdirectChartLoader(ChartViewer chartviewer, String filename, int type) {

	super();

	this.chartviewer = chartviewer;
	this.filename = filename;

	doReload = (type == URLs.CHART_INTRA);
}



public void run() {

	BufferedReader in = null;
	boolean valid = false;
	
	/* €nderungen mit ChartPofoComdirectLeser.readChartURL abgleichen! */
	
	try
	{
		URL url = new URL(filename);

		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;

		String str_chartimage = AktienMan.url.getString(URLs.STR_CD_CHARTIMAGE);
		String str_chartsrc   = AktienMan.url.getString(URLs.STR_CD_CHARTSRC);

		char urlende = (str_chartsrc.length() > 0) ? str_chartsrc.charAt(str_chartsrc.length()-1) : '"';

		while ((s = in.readLine()) != null)
		{
			if (s.indexOf(str_chartimage) > 0)
			{
				int i = s.indexOf(str_chartsrc);
				
				if (i > 0)
				{
					int i2 = s.indexOf(urlende, i + str_chartsrc.length());
					
					if (i2 > i)
					{
						String imgsrc = s.substring(i + str_chartsrc.length(), i2);

						new ChartLoader(chartviewer,imgsrc,doReload).start();

						valid = true;
						break;
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.err.println("Comdirect-Netchart-URL fehlerhaft.");
	}
	catch (Exception e)
	{
		System.err.println("FEHLER (ComdirectChartLoader): " + e);
	}
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
			catch (Exception e) {}
			finally
			{
				in = null;
			}
		}
	}
}

}
