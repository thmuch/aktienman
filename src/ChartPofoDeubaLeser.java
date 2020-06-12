/**
 @author Thomas Much
 @version 2000-11-11
*/

import java.net.*;
import java.io.*;




public class ChartPofoDeubaLeser extends ChartPofoLeser {




public ChartPofoDeubaLeser(IntradayChartsPortfolio parent, int index, String wkn, String boerse, long timeout, int type) {

	super(parent,index,wkn,boerse,timeout,type);
}



public void run()
{
	AktienMan.checkURLs();
	
	if (!AktienMan.hauptdialog.mainr()) return;

	loadChart(readChartURL(readKursURL()));
}



private String readChartURL(String kursURL) {

	if (kursURL.length() == 0) return "";
	
	BufferedReader in = null;

	boolean valid = false;
	String imgURL = "";
	
	try
	{
		URL url = new URL(kursURL);

		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;

		String str_chartimage = AktienMan.url.getString(URLs.STR_DEUBA_CHARTIMAGE);
		String str_chartsrc   = AktienMan.url.getString(URLs.STR_DEUBA_CHARTSRC);
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf(str_chartimage) > 0)
			{
				int i = s.indexOf(str_chartsrc);
				
				if (i > 0)
				{
					int i2 = s.indexOf("\'", i + str_chartsrc.length());
					
					if (i2 > i)
					{
						imgURL = AktienMan.url.get(URLs.URL_CHARTDEUBA) + s.substring(i + str_chartsrc.length(), i2);

						valid = true;
						break;
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Deuba-Netchart-URL fehlerhaft.");
	}
	catch (Exception e) {}
	finally
	{
		if (!valid)
		{
			getParent().setChartError(getIndex());
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

	return imgURL;
}



private String readKursURL() {

	BufferedReader in = null;

	boolean valid = false;
	String kursURL = "";

	try
	{
		URL url = new URL(AktienMan.url.getDeubaKursURL(getWKN(),getBoerse()));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;

		String str_charts = AktienMan.url.getString(URLs.STR_DEUBA_CHARTS);
		
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
					
					kursURL = AktienMan.url.getDeubaChartURL(rel,getType());

					valid = true;
					break;
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
			getParent().setChartError(getIndex());
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
	
	return kursURL;
}

}
