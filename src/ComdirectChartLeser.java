/**
 @author Thomas Much
 @version 1999-05-23
*/

import java.net.*;
import java.io.*;



public final class ComdirectChartLeser extends Thread {

private String request;
private String monate;
private ChartViewer chartviewer;



public ComdirectChartLeser(String request, String monate) {
	super();
	this.request = request;
	this.monate = monate;
}


public void run() {
	BufferedReader in = null;
	boolean valid = false;
	
	AktienMan.checkURLs();

	chartviewer = new ChartViewer(null,"Chart "+request,monate,400,330,ChartViewer.TYPE_COMDIRECT,false);
	
	try
	{
		URL url = new URL(AktienMan.url.get(URLs.URL_KURSECOMDIRECT) + request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			int i = s.indexOf(".html?");
			/**/
//	<TD><FONT SIZE="2"><A HREF="chart6.html?sb=1&show=DBK.STU">DEUTSCHE BANK AG AKTIEN O.N.</A></FONT></TD>
			
			if (i > 0)
			{
				int i2 = s.indexOf("\"",i);

				String s1 = AktienMan.url.get(URLs.URL_CHARTCOMDIRECT);
				String s2 = s.substring(i,i2);
				
				chartviewer.setComdirectStrings(s1,s2);

				new ComdirectChartLoader(chartviewer,s1+monate+s2).start();

				valid = true;
				break;
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-Chart-URL fehlerhaft.");
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
