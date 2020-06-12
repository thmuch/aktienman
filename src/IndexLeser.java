/**
 @author Thomas Much
 @version 2003-03-27

 2003-03-27
 	Anpassung an TecDAX und NIKKEI225
 2003-02-21
 	erste Version
*/

import java.net.*;
import java.io.*;




public final class IndexLeser implements Runnable {

private static final long TIMEOUT = 55000L;

private static final String YAHOO_URL      = "http://de.finance.yahoo.com/d/quotes.csv?f=sl1d1t1p&s=";
private static final String YAHOO_ENCODING = "ISO-8859-1";

private static final String   request;
private static final String[] INDICES = { "^GDAXI",             "^GDAXHI",             "^TECDAX",             "^NEMAX50K",            "^MDAXI",            "^SDAXI",            "^STOXX50E",                "^STOXX50",             "^DJI",                  "^GSPC",              "^NDX",                   "^IXIC",               "^INIK",               "^N225"                  };
private static final int[]    IDS     = { IndexQuelle.ID_DAX30, IndexQuelle.ID_DAX100, IndexQuelle.ID_TECDAX, IndexQuelle.ID_NEMAX50, IndexQuelle.ID_MDAX, IndexQuelle.ID_SDAX, IndexQuelle.ID_EUROSTOXX50, IndexQuelle.ID_STOXX50, IndexQuelle.ID_DOWJONES, IndexQuelle.ID_SP500, IndexQuelle.ID_NASDAQ100, IndexQuelle.ID_NASDAQ, IndexQuelle.ID_NIKKEI, IndexQuelle.ID_NIKKEI225 };


private long tID;



static {

	StringBuffer req = new StringBuffer(YAHOO_URL);
	
	for (int i = 0; i < INDICES.length; i++)
	{
		if (i > 0) req.append('+');

		req.append( INDICES[i] );
	}
	
	request = req.toString();
}




public IndexLeser(long tID) {

	this.tID = tID;
}



public void run() {

	AktienMan.checkURLs();

	try
	{
		BufferedReader in;
		
		do
		{
			System.out.println("INDEX-ANFRAGE: "+request); // TODO

			in = null;
			
			try
			{
				Connections.getConnection();

				URL url = new URL( request );
				
				in = new BufferedReader(new InputStreamReader(url.openStream(), YAHOO_ENCODING));

				String s;
				
				while ((s = in.readLine()) != null)
				{
					readOne( s.trim() );
				}
								
				IndexQuelle.updateFinished();
			}
			catch (Exception e)
			{
				AktienMan.errlog("Fehler im Indexleser", e);
			}
			finally
			{
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (Exception e)
					{
						AktienMan.errlog("Fehler im Indexleser", e);
					}
					finally
					{
						in = null;
					}
				}
				
				Connections.releaseConnection();
			}
			
			if (IndexQuelle.autoIndexOn())
			{
				try
				{
					Thread.sleep(TIMEOUT);
				}
				catch (Exception e) {}
			}

		} while (IndexQuelle.autoIndexOn());
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Indexleser", e);
	}
	finally
	{
		IndexQuelle.clearThread(tID);
	}
}



private void readOne(final String line) {

	if (line.length() == 0) return;

	System.out.println(" --> "+line); // TODO

	StrTokenizer st = new StrTokenizer(line,';');
	
	try
	{
		String symbol = st.nextToken();
		String wert   = st.nextToken();
		String date   = st.nextToken();
		String time   = st.nextToken();
		String vortag = st.nextToken();
		
		int index = -1;
		
		for (int i = 0; i < INDICES.length; i++)
		{
			if (symbol.equals( INDICES[i] ))
			{
				index = IDS[i];
				break;
			}
		}
		
		if (index < 0) return;

		String datum = ((time.length() == 4) ? ("0"+time) : time) + " ";
		
		int d1 = date.indexOf('/');
		int d2 = date.indexOf('/',d1+1);
		
		if ((d1 >= 0) && (d2 > d1))
		{
			String monat = date.substring(0,d1);
			String tag   = date.substring(d1+1,d2);
			
			datum += ((tag.length() == 1) ? ("0"+tag) : tag) + "." + ((monat.length() == 1) ? ("0"+monat) : monat) + "." + date.substring(d2+3);
		}
		else
		{
			datum += date;
		}

		long punkte;
		
		try
		{
			punkte = Waehrungen.doubleToLong(wert);
		}
		catch (NumberFormatException e)
		{
			punkte = BenutzerAktie.VALUE_NA;
		}

		long punkteVortag;

		try
		{
			punkteVortag = Waehrungen.doubleToLong(vortag);
		}
		catch (NumberFormatException e)
		{
			punkteVortag = BenutzerAktie.VALUE_NA;
		}

		IndexQuelle.checkIndex(index,punkte,punkteVortag,datum);
	}
	catch (Exception e)
	{
		AktienMan.errlog("Unerwartete Indexleser-Antwort (" + line + ")", e);
	}
}


}
