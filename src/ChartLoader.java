/**
 @author Thomas Much
 @version 1998-11-21
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public class ChartLoader extends Thread {

private static final long TIMEOUT = 120000L;

private String filename;
private boolean reload;
private ChartViewer chartviewer;



public ChartLoader(ChartViewer chartviewer, String filename, boolean reload) {
	super();
	this.chartviewer = chartviewer;
	this.filename = filename;
	this.reload = reload;
}


public void run() {
	chartviewer.setChartLoader(this);

	do
	{
		try
		{
			URL url = new URL(filename);
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			DataInputStream in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);

			in.close();
			
			chartviewer.setImage(AktienMan.hauptdialog.getToolkit().createImage(daten));
		}
		catch (MalformedURLException e)
		{
			System.out.println("URL des Charts fehlerhaft.");
		}
		catch (IOException e) {}
		catch (NegativeArraySizeException e) {}

		if (reload)
		{
			try
			{
				sleep(TIMEOUT);
			}
			catch (InterruptedException e) {}
		}

	} while (reload);
}

}
