/**
 @author Thomas Much
 @version 1998-12-07
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
	BufferedReader in = null;
	boolean valid = false;

	chartviewer = new ChartViewer(null,"Chart "+request,monate,400,330,ChartViewer.TYPE_COMDIRECT);

	try
	{
		URL url = new URL(URLs.TELEDATA+request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

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
					
					chartviewer.setComdirectStrings(s1,s2);

					new ChartLoader(chartviewer,s1+monate+s2,false).start();
					valid = true;
					break;
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Teledata-URL fehlerhaft.");
	}
	catch (IOException e) {}
	finally
	{
		if (!valid)
		{
			chartviewer.setStatusError();
		}
		
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
}

}
