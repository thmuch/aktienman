/**
 @author Thomas Much
 @version 2002-10-09

 2002-10-09
   readKursURL findet Comdirect-Chartseiten nun wieder
 2002-01-14
   readChartURL findet nun wieder die Chart-URL; das URL-Ende wird mit dem letzten
     Zeichen von str_chartsrc gesucht
*/

import java.net.*;
import java.io.*;
import java.awt.*;




public class ChartPofoComdirectLeser extends ChartPofoLeser {




public ChartPofoComdirectLeser(IntradayChartsPortfolio parent, int index, String wkn, String boerse, long timeout,int type) {

	super(parent,index,wkn,boerse,timeout,type);
}



public void run() {

	AktienMan.checkURLs();
	
	if (!AktienMan.hauptdialog.mainr()) return;
	
	loadChart(readChartURL(readKursURL()));
}



private String readChartURL(String kursURL) {

	if (kursURL.length() == 0) return "";

	BufferedReader in = null;

	boolean valid = false;
	String imgURL = "";
	
	/* €nderungen mit ComdirectChartLoader.run abgleichen! */

	try
	{
		URL url = new URL(kursURL);

		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;

		String str_chartimage = AktienMan.url.getString(URLs.STR_CD_CHARTIMAGE);
		String str_chartsrc   = AktienMan.url.getString(URLs.STR_CD_CHARTSRC);

		char urlende = (str_chartsrc.length() > 0) ? str_chartsrc.charAt(str_chartsrc.length()-1) : '"';
		
		while ((s = in.readLine()) != null)
		{
			if (s.indexOf(str_chartimage) > 0)
			{
				int i = s.indexOf(str_chartsrc);
				
				if (i > 0)
				{
					int i2 = s.indexOf(urlende, i + str_chartsrc.length());
					
					if (i2 > i)
					{
						imgURL = s.substring(i + str_chartsrc.length(), i2);

						valid = true;
						break;
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-Netchart-URL fehlerhaft.");
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

	/* €nderungen mit ComdirectChartLeser.run abgleichen! */

	try
	{
		URL url = new URL(AktienMan.url.getComdirectKursURL(getWKN(),getBoerse()));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		
		String str_charts       = AktienMan.url.getString(URLs.STR_CD_CHARTS);
		String str_chartreplace = AktienMan.url.getString(URLs.STR_CD_CHARTREPLACE);
		
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
					String relURL = s.substring(leftquote+1,rightquote);

					i = relURL.indexOf(str_charts);
					relURL = relURL.substring(0,i) + str_chartreplace + relURL.substring(i+str_charts.length());
					
					kursURL = AktienMan.url.getComdirectChartURL(relURL,getType(),URLs.CHART_LINIE);

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
