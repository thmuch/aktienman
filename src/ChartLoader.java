/**
 @author Thomas Much
 @version 1998-11-15
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public class ChartLoader extends Thread {

private String filename;
private ChartViewer chartviewer;



public ChartLoader(ChartViewer chartviewer, String filename) {
	super();
	this.chartviewer = chartviewer;
	this.filename = filename;
}


public void run() {
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
}

}
