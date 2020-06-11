/**
 @author Thomas Much
 @version 1998-10-30
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;



public class IntradayChartLeser extends Thread {

private String boerse;
private BenutzerAktie ba;



public IntradayChartLeser(String boerse, BenutzerAktie ba) {
	super();
	this.boerse = boerse;
	this.ba = ba;
}


public void run() {
	String wkn = ba.getWKNString();
	
	try
	{
		URL url = new URL(URLs.DAXREALTIME);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf("<TR>") >= 0)
			{
				int i = s.indexOf(wkn);
				
				if (i > 0)
				{
					int i2 = s.indexOf(">",s.indexOf(">",i)+1);
					int i3 = s.indexOf("<",i2);
					
					readImage(s.substring(i2+1,i3).trim());

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
		System.out.println("Exchange-URL fehlerhaft.");
	}
	catch (IOException e) {}
}


private void readImage(String kuerzel) {
	String filename = URLs.DAXINTRADAY + kuerzel + "." + boerse + ".DEM.gif";

	try
	{
		URL url = new URL(filename);
		URLConnection curl = url.openConnection();
		curl.setUseCaches(false);
		
		byte[] daten = new byte[curl.getContentLength()];
		
		DataInputStream in = new DataInputStream(curl.getInputStream());
		
		in.readFully(daten);

		in.close();

		new ChartViewer(AktienMan.hauptdialog.getToolkit().createImage(daten),"Intraday "+ba.getName(true),620);
	}
	catch (MalformedURLException e)
	{
		System.out.println("URL des Intraday-Charts fehlerhaft.");
	}
	catch (IOException e) {}
	catch (NegativeArraySizeException e) {}
}

}
