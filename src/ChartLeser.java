/**
 @author Thomas Much
 @version 1998-10-30
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;



public class ChartLeser extends Thread {

private String request;
private String monate;



public ChartLeser(String request, String monate) {
	super();
	this.request = request;
	this.monate = monate;
}


public void run() {
	try
	{
		URL url = new URL(URLs.TELEDATA+request);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf("asgw?COMDIRECTGIF") > 0)
			{
				int i = s.indexOf("http://");
				
				if (i > 0)
				{
					int i2 = s.indexOf("COMDIRECTGIF");
					int i3 = s.indexOf(';',i2);
					int i4 = s.indexOf('"',i3);

					readImage(s.substring(i,i2) + "COMDIRECT" + monate + "GIF&" + s.substring(i3+1,i4));

					in.close();
					return;
				}
			}
		}
		
		in.close();
		
		// Fehler!
	}
	catch (MalformedURLException e)
	{
		System.out.println("Teledata-URL fehlerhaft.");
	}
	catch (IOException e) {}
}


private void readImage(String filename) {
	try
	{
		URL url = new URL(filename);
		URLConnection curl = url.openConnection();
		curl.setUseCaches(false);
		
		byte[] daten = new byte[curl.getContentLength()];
		
		DataInputStream in = new DataInputStream(curl.getInputStream());
		
		in.readFully(daten);

		in.close();

		new ChartViewer(AktienMan.hauptdialog.getToolkit().createImage(daten),"Chart "+request,0);
	}
	catch (MalformedURLException e)
	{
		System.out.println("URL des Charts fehlerhaft.");
	}
	catch (IOException e) {}
	catch (NegativeArraySizeException e) {}
}

}
