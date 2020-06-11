/**
 @author Thomas Much
 @version 1999-06-20
*/

import java.net.*;
import java.io.*;




public final class DeubaChartLeser extends Thread {

private String wkn,boerse;
private int nextID,type;
private boolean isFonds;
private DeubaChartViewer chartviewer;




public DeubaChartLeser(String wkn, String boerse, boolean isFonds, int type, int nextID) {
	super();
	this.wkn = wkn;
	this.boerse = boerse;
	this.type = type;
	this.nextID = nextID;
	this.isFonds = isFonds;
}



public void run() {
	BufferedReader in = null;
	boolean valid = false;
	
	AktienMan.checkURLs();

	chartviewer = new DeubaChartViewer(wkn+"."+boerse,isFonds,type,nextID);
	
	try
	{
		URL url = new URL(AktienMan.url.getDeubaKursURL(wkn,boerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		while ((s = in.readLine()) != null)
		{
			int i = s.indexOf(".html?");
			
			if (i > 0)
			{
				int i2 = s.indexOf("\"",i);

				String s1 = AktienMan.url.get(URLs.URL_CHARTDEUBA);
				String s2 = s.substring(i,i2);
				
				chartviewer.setDeubaStrings(s1,s2);

				new DeubaChartLoader(chartviewer,s1+chartviewer.getTypeDeubaString(type)+s2,type).start();

				valid = true;
				break;
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Deuba-Chart-URL fehlerhaft.");
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
