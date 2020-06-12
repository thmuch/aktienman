/**
 @author Thomas Much
 @version 1999-06-13
*/

import java.net.*;
import java.awt.*;
import java.io.*;




public final class ChartLoader extends Thread {

private static final long TIMEOUT = 120000L;

private boolean stopped = false;

private String filename;
private boolean reload;
private ChartViewer chartviewer;




public ChartLoader(ChartViewer chartviewer, String filename, boolean reload) {

	super();

	this.chartviewer = chartviewer;
	this.filename = filename;
	this.reload = reload;
}



public void stopLoading() {

	stopped = true;
}



public void run() {

	DataInputStream in;

	chartviewer.setChartLoader(this);

	do
	{
		in = null;
		
		if (reload)
		{
			chartviewer.setStatusEmpty();
		}
		
		try
		{
			URL url = new URL(filename);
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);

			if (!stopped)
			{
				chartviewer.setImage(AktienMan.hauptdialog.getToolkit().createImage(daten),daten);
			}
		}
		catch (MalformedURLException e)
		{
			System.out.println("URL des Charts fehlerhaft.");
			chartviewer.setStatusError();
		}
		catch (IOException e)
		{
			chartviewer.setStatusError();
		}
		catch (NegativeArraySizeException e)
		{
			chartviewer.setStatusError();
		}
		finally
		{
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

		if (!stopped && reload)
		{
			try
			{
				sleep(TIMEOUT);
			}
			catch (InterruptedException e) {}
		}

	} while (!stopped && reload);
}

}
