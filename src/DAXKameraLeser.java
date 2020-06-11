/**
 @author Thomas Much
 @version 1998-12-07
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public class DAXKameraLeser extends Thread {

private static final long TIMEOUT = 120000L;

private boolean stopped = false;



public DAXKameraLeser() {
	super();
}


public void stopLoading() {
	stopped = true;
}


public void run() {
	DataInputStream in;

	while (!stopped)
	{
		if (AktienMan.daxImage == null) AktienMan.daxKamera.setStatus(DAXKamera.S_LOADING);
		
		in = null;

		try
		{
			URL url = new URL(URLs.KAMERA);
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);

			if (!stopped)
			{
				AktienMan.daxImage = AktienMan.hauptdialog.getToolkit().createImage(daten);
				
				AktienMan.daxImage.getWidth(AktienMan.daxKamera);
				AktienMan.daxImage.getHeight(AktienMan.daxKamera);
				
				AktienMan.daxKamera.neuZeichnen();
			}
		}
		catch (MalformedURLException e)
		{
			System.out.println("URL der DAX-Kamera fehlerhaft.");
			AktienMan.daxKamera.setStatus(DAXKamera.S_ERROR);
		}
		catch (IOException e)
		{
			AktienMan.daxKamera.setStatus(DAXKamera.S_ERROR);
		}
		catch (NegativeArraySizeException e)
		{
			AktienMan.daxKamera.setStatus(DAXKamera.S_OFFLINE);
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

		if (!stopped)
		{
			try
			{
				sleep(TIMEOUT);
			}
			catch (InterruptedException e) {}
		}
	}
}

}
