/**
 @author Thomas Much
 @version 1999-12-09
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public final class DAXKameraLeser extends Thread {

private static final long TIMEOUT = 120000L;

private DAXKamera kamera;

private boolean stopped = false;




public DAXKameraLeser(DAXKamera kamera) {
	super();
	this.kamera = kamera;
}


public void stopLoading() {
	stopped = true;
}


public void run() {
	DataInputStream in;
	
	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	while (!stopped)
	{
		if (AktienMan.daxImage == null) AktienMan.daxKamera.setStatus(DAXKamera.S_LOADING);
		
		in = null;

		try
		{
			URL url = new URL(AktienMan.url.get(URLs.URL_KAMERA));
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);

			if (!stopped)
			{
				kamera.setKameraDaten(daten);
				
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
