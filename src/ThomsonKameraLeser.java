/**
 @author Thomas Much
 @version 2003-02-28
 
 2003-02-28
 	das Einlesen erfolgt nun gepuffert
*/

import java.net.*;
import java.awt.*;
import java.io.*;




public final class ThomsonKameraLeser extends Thread {

private static final long TIMEOUT = 120000L;

private DAXKamera kamera;
private int type,index;

private boolean stopped = false;




public ThomsonKameraLeser(DAXKamera kamera, int type, int index) {

	this.kamera = kamera;
	this.type   = type;
	this.index  = index;
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
		in = null;

		try
		{
			Connections.getConnection();

			URL url = new URL(AktienMan.url.get(type));

			URLConnection curl = url.openConnection();

			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			in = new DataInputStream(new BufferedInputStream(curl.getInputStream()));
			
			in.readFully(daten);

			if (!stopped)
			{
				Image image = AktienMan.hauptdialog.getToolkit().createImage(daten);
				
				kamera.setImage(index,image);
				
				image.getWidth(kamera);
				image.getHeight(kamera);
				
				kamera.neuZeichnen();
			}
		}
		catch (Exception e) {}
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
