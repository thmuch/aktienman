// 1998-09-12 tm

import java.net.*;
import java.awt.*;
import java.io.*;



public class DAXKameraLeser extends Thread {

private static final long TIMEOUT = 120000L;



public DAXKameraLeser() {
	super();
}


public void run() {
	while (true)
	{
		if (AktienMan.daxImage == null) AktienMan.daxKamera.setStatus(DAXKamera.S_LOADING);

		try
		{
			URL url = new URL(URLs.KAMERA);
			URLConnection curl = url.openConnection();
			curl.setUseCaches(false);
			
			byte[] daten = new byte[curl.getContentLength()];
			
			DataInputStream in = new DataInputStream(curl.getInputStream());
			
			in.readFully(daten);

			in.close();

			AktienMan.daxImage = AktienMan.hauptdialog.getToolkit().createImage(daten);
			
			AktienMan.daxImage.getWidth(AktienMan.daxKamera);
			AktienMan.daxImage.getHeight(AktienMan.daxKamera);
			
			AktienMan.daxKamera.neuZeichnen();
		}
		catch (MalformedURLException e)
		{
			System.out.println("URL der DAX-Kamera fehlerhaft.");
		}
		catch (IOException e)
		{
			AktienMan.daxKamera.setStatus(DAXKamera.S_ERROR);
		}
		catch (NegativeArraySizeException e)
		{
			AktienMan.daxKamera.setStatus(DAXKamera.S_OFFLINE);
		}

		try
		{
			sleep(TIMEOUT);
		}
		catch (InterruptedException e) {}
	}
}

}
