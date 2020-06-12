/**
 @author Thomas Much
 @version 2000-03-14
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

	if (!AktienMan.hauptdialog.mainr()) return;

	chartviewer = new DeubaChartViewer(wkn+"."+boerse,isFonds,type,nextID);
	
	try
	{
		URL url = new URL(AktienMan.url.getDeubaKursURL(wkn,boerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;

		String str_charts    = AktienMan.url.getString(URLs.STR_DEUBA_CHARTS);
		String str_charthref = AktienMan.url.getString(URLs.STR_DEUBA_CHARTHREF);
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf(str_charts) > 0)
			{
				int i = s.indexOf(str_charthref);
				
				if (i > 0)
				{
					int i2 = s.indexOf("\"", i + str_charthref.length());
					
					if (i2 > i)
					{
						String rel = s.substring(i + str_charthref.length(), i2);
						
						chartviewer.setDeubaRelURL(rel);
						
						new DeubaChartLoader(chartviewer,AktienMan.url.getDeubaChartURL(rel,type),type).start();

						valid = true;
						break;
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Deuba-Chart-URL fehlerhaft.");
	}
	catch (Exception e) {}
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
			catch (Exception e) {}
			finally
			{
				in = null;
			}
		}
	}
}

}
