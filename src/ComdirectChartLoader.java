/**
 @author Thomas Much
 @version 1999-02-23
*/

import java.net.*;
import java.io.*;



public final class ComdirectChartLoader extends Thread {

private String filename;
private ChartViewer chartviewer;



public ComdirectChartLoader(ChartViewer chartviewer, String filename) {
	super();
	this.chartviewer = chartviewer;
	this.filename = filename;
}


public void run() {
	BufferedReader in = null;
	boolean valid = false;
	
	try
	{
		URL url = new URL(filename);

		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf("<IMG SRC=") >= 0)
			{
				if ((s.indexOf(" USEMAP=") > 0) && (s.indexOf("<!--") < 0))
				{
					int i = s.indexOf("http://");
					int i2 = s.indexOf("\"",i);
					
					String imgsrc = s.substring(i,i2);

					new ChartLoader(chartviewer,imgsrc,false).start();

					valid = true;
					break;
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-Netchart-URL fehlerhaft.");
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
