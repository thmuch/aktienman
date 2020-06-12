/**
 @author Thomas Much
 @version 2003-02-28
 
 2003-02-28
 	das Einlesen erfolgt nun gepuffert
*/

import java.net.*;
import java.awt.*;
import java.io.*;




public final class DAXKameraLeser extends Thread {

private static final long TIMEOUT = 120000L;

private DAXKamera kamera;

private boolean stopped = false;




public DAXKameraLeser(DAXKamera kamera) {

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
		if (kamera.getImage() == null) kamera.setStatus(DAXKamera.S_LOADING);
		
		in = null;

		try
		{
			Connections.getConnection();

			URL url = new URL(AktienMan.url.get(URLs.URL_KAMERA));

			URLConnection curl = url.openConnection();

			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(new BufferedInputStream(curl.getInputStream()));
			
			in.readFully(daten);

			if (!stopped)
			{
				kamera.setKameraDaten(daten);
				
				Image daxImage = AktienMan.hauptdialog.getToolkit().createImage(daten);
				
				kamera.setImage(daxImage);
				
				daxImage.getWidth(kamera);
				daxImage.getHeight(kamera);
				
				kamera.neuZeichnen();
			}
		}
		catch (NegativeArraySizeException e)
		{
			kamera.setStatus(DAXKamera.S_OFFLINE);
		}
		catch (Exception e)
		{
			kamera.setStatus(DAXKamera.S_ERROR);
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

			Connections.releaseConnection();
			
			System.gc();
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
