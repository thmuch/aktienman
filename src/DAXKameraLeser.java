/**
 @author Thomas Much
 @version 2000-11-10
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
			
			in = new DataInputStream(curl.getInputStream());
			
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
		catch (MalformedURLException e)
		{
			System.out.println("URL der DAX-Kamera fehlerhaft.");
			kamera.setStatus(DAXKamera.S_ERROR);
		}
		catch (IOException e)
		{
			kamera.setStatus(DAXKamera.S_ERROR);
		}
		catch (NegativeArraySizeException e)
		{
			kamera.setStatus(DAXKamera.S_OFFLINE);
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
