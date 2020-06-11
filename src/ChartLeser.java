/**
 @author Thomas Much
 @version 1998-11-16
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public class ChartLeser extends Thread {

private String request;
private String monate;
private ChartViewer chartviewer;



public ChartLeser(String request, String monate) {
	super();
	this.request = request;
	this.monate = monate;
}


public void run() {
	chartviewer = new ChartViewer(null,"Chart "+request,monate,400,330,ChartViewer.TYPE_COMDIRECT);

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
					
					String s1 = s.substring(i,i2) + "COMDIRECT";
					String s2 = "GIF&" + s.substring(i3+1,i4);
					
					in.close();

					chartviewer.setComdirectStrings(s1,s2);

					new ChartLoader(chartviewer,s1+monate+s2).start();
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

}
