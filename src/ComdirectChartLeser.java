/**
 @author Thomas Much
 @version 1999-06-21
*/

import java.net.*;
import java.io.*;



public final class ComdirectChartLeser extends Thread {

private String wkn,boerse;
private int nextID,type;
private boolean isFonds;
private ComdirectChartViewer chartviewer;




public ComdirectChartLeser(String wkn, String boerse, boolean isFonds, int type, int nextID) {
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

	chartviewer = new ComdirectChartViewer(wkn+"."+boerse,isFonds,type,nextID);
	
	try
	{
		URL url = new URL(AktienMan.url.getComdirectKursURL(wkn,boerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		String str_charts    = AktienMan.url.getString(URLs.STR_CD_CHARTS);
		String str_charthref = AktienMan.url.getString(URLs.STR_CD_CHARTHREF);
		
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
						
						chartviewer.setComdirectRelURL(rel);
						
						int charttype = ((isFonds) || (type == ChartViewer.TYPE_INTRA)) ? URLs.CHART_LINIE : URLs.CHART_STANDARD;
						
						new ComdirectChartLoader(chartviewer,AktienMan.url.getComdirectChartURL(rel,chartviewer.getTypeComdirectString(type),charttype),type).start();

						valid = true;
						break;
					}
				}
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
