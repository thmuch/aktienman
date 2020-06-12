/**
 @author Thomas Much
 @version 2003-02-03
*/

import java.io.*;
import java.net.*;




public class WechselkursLeser implements Runnable {

private static final String YAHOO_URL      = "http://de.finance.yahoo.com/d/quotes.csv?f=sl1d1t1&s=";
private static final String YAHOO_ENCODING = "ISO-8859-1";
// Alternative: http://www.ecb.int/stats/eurofxref/eurofxref-daily.xml

private static final int VON = Waehrungen.CHF;
private static final int BIS = Waehrungen.JPY;

private String request;
private int count;




private WechselkursLeser(String request, int count) {

	this.request = request;
	this.count   = count;
}



public void run() {

	AktienMan.checkURLs();

	System.out.println("WECHSELKURS-ANFRAGE: "+request); // TODO

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

		URL url = new URL(request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream(), YAHOO_ENCODING));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			readOne( s.trim() );
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Wechselkursleser", e);
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
				AktienMan.errlog("Fehler im Wechselkursleser", e);
			}
		}

		Connections.releaseConnection();
		
		while (count > 0)
		{
			Waehrungen.setExchangeRate(Waehrungen.NONE,Waehrungen.NONE,-1.0,null);
			count--;
		}
	}
	
	// wir aktualisieren die Anzeige hier, damit es keinen Deadlock gibt...
	AktienMan.hauptdialog.listeUpdate(false,true,false,false);
}



private void readOne(final String line) {

	if (line.length() == 0) return;

	System.out.println(" --> "+line); // TODO

	// EURCHF=X;1,467209;2/3/2003;19:19

	StrTokenizer st = new StrTokenizer(line,';');
	
	try
	{
		String symbol = st.nextToken();
		String xkurs = st.nextToken();
		String date = st.nextToken();
		String time = st.nextToken();
		
		int i = symbol.indexOf("=X");
		
		if (i != 6) return;
		
		int wFrom = Waehrungen.id2Index(symbol.substring(0,3));
		int wTo   = Waehrungen.id2Index(symbol.substring(3,6));

		String kursdatum = ((time.length() == 4) ? ("0"+time) : time) + " ";
		
		int d1 = date.indexOf('/');
		int d2 = date.indexOf('/',d1+1);
		
		if ((d1 >= 0) && (d2 > d1))
		{
			String monat = date.substring(0,d1);
			String tag   = date.substring(d1+1,d2);
			
			kursdatum += ((tag.length() == 1) ? ("0"+tag) : tag) + "." + ((monat.length() == 1) ? ("0"+monat) : monat) + "." + date.substring(d2+3);
		}
		else
		{
			kursdatum += date;
		}

		double xwert;

		try
		{
			xwert = NumUtil.getDouble(xkurs);
		}
		catch (NumberFormatException e)
		{
			xwert = -1.0;
		}
		
		Waehrungen.setExchangeRate(wFrom,wTo,xwert,kursdatum);
		count--;
	}
	catch (Exception e)
	{
		AktienMan.errlog("Unerwartete Wechselkursleser-Antwort (" + line + ")", e);
	}
}



public static int start() {

	StringBuffer request = new StringBuffer(YAHOO_URL);
	
	for (int i = VON; i <= BIS; i++)
	{
		if (i > VON) request.append('+');
		
		String id = Waehrungen.index2Id(i);
		
		request.append("EUR");
		request.append(id);
		request.append("=X+");

		request.append(id);
		request.append("EUR=X");
	}
	
	int count = 2 * (BIS + 1 - VON);
	
	Runnable leser = new WechselkursLeser( request.toString(), count );
	
	new Thread(leser).start();
	
	return count;
}


}
