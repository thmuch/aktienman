/**
 @author Thomas Much
 @version 2000-03-27
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
	
	if (!AktienMan.hauptdialog.mainr()) return;

	chartviewer = new ComdirectChartViewer(wkn+"."+boerse,isFonds,type,nextID);
	
	try
	{
		URL url = new URL(AktienMan.url.getComdirectKursURL(wkn,boerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		String str_charts    = AktienMan.url.getString(URLs.STR_CD_CHARTS);
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();

			if (s.length() == 0) continue;
			
			int i = s.indexOf(str_charts);
			
			if (i > 0)
			{
				int leftquote  = s.lastIndexOf('"',i);
				int rightquote = s.indexOf('"',i);
				
				if ((leftquote >= 0) && (rightquote > leftquote))
				{
					String rel = s.substring(leftquote+1,rightquote);
					
					chartviewer.setComdirectRelURL(rel);
					
					int charttype = ((isFonds) || (type == URLs.CHART_INTRA)) ? URLs.CHART_LINIE : URLs.CHART_STANDARD;
					
					new ComdirectChartLoader(chartviewer,AktienMan.url.getComdirectChartURL(rel,type,charttype),type).start();

					valid = true;
					break;
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-Chart-URL fehlerhaft.");
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
