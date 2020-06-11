/**
 @author Thomas Much
 @version 1999-06-20
*/

import java.net.*;
import java.io.*;




public final class DeubaChartLoader extends Thread {

private String filename;
private ChartViewer chartviewer;
private boolean doReload;




public DeubaChartLoader(ChartViewer chartviewer, String filename, int type) {
	super();
	this.chartviewer = chartviewer;
	this.filename = filename;

	doReload = (type == ChartViewer.TYPE_INTRA);
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
			if (s.indexOf(" src=") >= 0)
			{
				if (s.indexOf(" usemap=") > 0)
				{
					int i = s.indexOf("http://");
					int i2 = s.indexOf("\"",i);
					
					String imgsrc = s.substring(i,i2);

					new ChartLoader(chartviewer,imgsrc,doReload).start();

					valid = true;
					break;
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Deuba-Netchart-URL fehlerhaft.");
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
