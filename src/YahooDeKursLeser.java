/**
 @author Thomas Much
 @version 2003-02-01
*/

import java.io.*;
import java.net.*;
import java.util.*;




public class YahooDeKursLeser implements Runnable {

private static final String YAHOO_URL      = "http://de.finance.yahoo.com/d/quotes.csv?f=sxc4l1d1t1pohgvn&s=";
private static final String YAHOO_ENCODING = "ISO-8859-1";

private KursReceiver receiver;
private KursQuelle quelle,first;
private Vector requests;
private boolean sofortZeichnen;




public YahooDeKursLeser(KursQuelle quelle, KursReceiver receiver, Vector requests, boolean sofortZeichnen, KursQuelle first) {

	this.quelle = quelle;
	this.receiver = receiver;
	this.requests = requests;
	this.sofortZeichnen = sofortZeichnen;
	this.first = first;
}



public void run() {

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;
	
	String request = getRequestURL();

	System.out.println("ANFRAGE: "+request); // TODO

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
		AktienMan.errlog("Fehler im Yahoo.de-Kursleser", e);
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
				AktienMan.errlog("Fehler im Yahoo.de-Kursleser", e);
			}
		}

		Connections.releaseConnection();

		sendErrorRequests();
		requests = null;
	}
}



private void readOne(final String line) {

	if (line.length() == 0) return;

	System.out.println(" --> "+line); // TODO
	
	StrTokenizer st = new StrTokenizer(line,';');

	// 500340.F;Frankfurt;EUR;73,20;1/23/2003;17:47;6187;ADIDAS SALOMON // TODO: an neue Abfrage anpassen
	
	// TODO: kann man bei Yahoo auch _immer_ das nicht-WKN-Symbol abfragen?
	
	try
	{
		String symbol = st.nextToken();
		String boerse = st.nextToken();
		
		if ((boerse.length() == 0) || "N/A".equalsIgnoreCase(boerse)) return;

		String waehrung = st.nextToken();
		
		if (waehrung.length() == 0) return;
		
		String kurs = st.nextToken();
		String date = st.nextToken();
		String time = st.nextToken();
		String vortag = st.nextToken();
		String open = st.nextToken();
		String high = st.nextToken();
		String low = st.nextToken();
		String volume = st.nextToken();
		String name = st.nextToken();
		
		if (symbol.startsWith("DE"))
		{
			// TODO: bei ISINs ist das nicht mehr einfach so erlaubt...
			symbol = symbol.substring(2);
		}
		
		String wkn;
		
		int punkt = symbol.indexOf('.');
		
		if (punkt < 0)
		{
			wkn = symbol;
			symbol = "";
		}
		else
		{
			wkn = symbol.substring(0,punkt);
			symbol = symbol.substring(punkt+1);
		}
		
		String boerseID = getBoerse(boerse,symbol);
		
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
		
		long kurswert;

		try
		{
			kurswert = Waehrungen.doubleToLong(kurs);
		}
		catch (NumberFormatException e)
		{
			kurswert = BenutzerAktie.VALUE_NA;
		}

		long vortageskurs;

		try
		{
			vortageskurs = Waehrungen.doubleToLong(vortag);
		}
		catch (NumberFormatException e)
		{
			vortageskurs = BenutzerAktie.VALUE_NA;
		}
		
		long eroeffnungskurs;

		try
		{
			eroeffnungskurs = Waehrungen.doubleToLong(open);
		}
		catch (NumberFormatException e)
		{
			eroeffnungskurs = BenutzerAktie.VALUE_NA;
		}

		long hoechstkurs;

		try
		{
			hoechstkurs = Waehrungen.doubleToLong(high);
		}
		catch (NumberFormatException e)
		{
			hoechstkurs = BenutzerAktie.VALUE_NA;
		}

		long tiefstkurs;

		try
		{
			tiefstkurs = Waehrungen.doubleToLong(low);
		}
		catch (NumberFormatException e)
		{
			tiefstkurs = BenutzerAktie.VALUE_NA;
		}
		
		long handelsvolumen;
		
		try
		{
			handelsvolumen = Long.parseLong(volume);
		}
		catch (NumberFormatException e)
		{
			handelsvolumen = 0L;
		}
		
		int i = 0;
		
		while (i < requests.size())
		{
			String all = (String)requests.elementAt(i);
			
			int i2 = all.indexOf(";");
			int i3 = all.indexOf(";",i2+1);
			
			String reqWkn    = all.substring(i2+1,i3);
			String reqBoerse = all.substring(i3+1);

			if (wkn.equalsIgnoreCase(reqWkn) && boerseID.equalsIgnoreCase(reqBoerse))
			{
				if (kurswert > 0L)
				{
					int waehrungID = getWaehrung(waehrung);
					
					if (waehrungID == Waehrungen.GBP)
					{
						kurswert        = pence2Pound(kurswert);
						vortageskurs    = pence2Pound(vortageskurs);
						eroeffnungskurs = pence2Pound(eroeffnungskurs);
						hoechstkurs     = pence2Pound(hoechstkurs);
						tiefstkurs      = pence2Pound(tiefstkurs);
					}
					
					/* TODO: kurz? */
					receiver.listeNeuerAktienkurs(wkn,null,"",boerseID,name,kurswert,kursdatum,
																vortageskurs,eroeffnungskurs,
																hoechstkurs,tiefstkurs,handelsvolumen,
																waehrungID,sofortZeichnen);
				}
				else
				{
					// TODO: in BenutzerAktie o.Š. anders lšsen
					receiver.listeAktienkursNA(wkn,"",boerseID,name,sofortZeichnen);
				}

				requests.removeElementAt(i);
			}
			else
			{
				i++;
			}
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Unerwartete Yahoo.de-Antwort (" + line + ")", e);
	}
}



private String getRequestURL() {

	StringBuffer req = new StringBuffer(YAHOO_URL);
	
	for (int i = 0; i < requests.size(); i++)
	{
		String all = (String)requests.elementAt(i);

		int i2 = all.indexOf(";");
		int i3 = all.indexOf(";",i2+1);
		
		String wkn     = all.substring(i2+1,i3);
		String boerse  = all.substring(i3+1);

		if (i > 0) req.append("+");
		
		req.append( wkn );
		req.append( yahooBoerse(boerse) );
	}

	return req.toString();
}



private void sendErrorRequests() {

	for (int i = 0; i < requests.size(); i++)
	{
		String all = (String)requests.elementAt(i);
		
		int i2 = all.indexOf(";");
		int i3 = all.indexOf(";",i2+1);
		
		String request = all.substring(0,i2);
		String wkn     = all.substring(i2+1,i3);
		String boerse  = all.substring(i3+1);

		receiver.listeAnfrageFehler(request,wkn,boerse,sofortZeichnen,first,quelle);
	}
}



private static String yahooBoerse(final String boerse) {

	if (Boersenliste.ID_XETRA.equalsIgnoreCase(boerse))
	{
		return ".DE";
	}
	else if (Boersenliste.ID_FRANKFURT.equalsIgnoreCase(boerse))
	{
		return ".F";
	}
	else if (Boersenliste.ID_FONDS_DE.equalsIgnoreCase(boerse))
	{
		return ".DE";
	}
	else if (Boersenliste.ID_BERLIN.equalsIgnoreCase(boerse))
	{
		return ".BE";
	}
	else if (Boersenliste.ID_BREMEN.equalsIgnoreCase(boerse))
	{
		return ".BM";
	}
	else if (Boersenliste.ID_DUESSELDORF.equalsIgnoreCase(boerse))
	{
		return ".D";
	}
	else if (Boersenliste.ID_HAMBURG.equalsIgnoreCase(boerse))
	{
		return ".H";
	}
	else if (Boersenliste.ID_HANNOVER.equalsIgnoreCase(boerse))
	{
		return ".HA";
	}
	else if (Boersenliste.ID_MUENCHEN.equalsIgnoreCase(boerse))
	{
		return ".MU";
	}
	else if (Boersenliste.ID_STUTTGART.equalsIgnoreCase(boerse))
	{
		return ".SG";
	}
	
	return "";
}



private static String getBoerse(final String yahoo, final String symbol) {

	if ("Xetra".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_XETRA;
	}
	else if ("Fund".equalsIgnoreCase(yahoo) && "DE".equalsIgnoreCase(symbol))
	{
		return Boersenliste.ID_FONDS_DE;
	}
	else if ("Frankfurt".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_FRANKFURT;
	}
	else if ("Berlin".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_BERLIN;
	}
	else if ("Bremen".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_BREMEN;
	}
	else if ("D\u00fcsseldorf".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_DUESSELDORF;
	}
	else if ("Hamburg".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_HAMBURG;
	}
	else if ("Hannover".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_HANNOVER;
	}
	else if ("M\u00fcnchen".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_MUENCHEN;
	}
	else if ("Stuttgart".equalsIgnoreCase(yahoo))
	{
		return Boersenliste.ID_STUTTGART;
	}
	
	return Boersenliste.ID_NONE;
}



private static int getWaehrung(final String yahoo) {

	if ("EUR".equals(yahoo))
	{
		return Waehrungen.EUR;
	}
	else if ("USD".equals(yahoo))
	{
		return Waehrungen.USD;
	}
	else if ("GBp".equals(yahoo))
	{
		// Achtung: Yahoo liefert Pence! Wir sagen hier trotzdem, dass das Pound sind,
		//   rechnen das aber oben um, damit der Rest des Programms das nicht merkt.
		return Waehrungen.GBP;
	}
	else if ("CHF".equals(yahoo))
	{
		return Waehrungen.CHF;
	}
	else if ("JPY".equals(yahoo))
	{
		return Waehrungen.JPY;
	}
	
	return Waehrungen.NONE;
}



private static long pence2Pound(long pence) {

	return (pence + 50L) / 100L;
}


}



/*
pollstock.url=http://quote.yahoo.com:80/d/quotes.csv?f=sl1d1t1c&e=.cs
v&s=SUNW+LU+CIEN+NT+GLW+JDSU+AVNX+A+BRCM+TERN+CNXT+LOR+GSTRF+QCOM++PCS+MOT+
LVLT+MFNX+GBLX+NOPT+WCOM+NOVL+XLA+PRCM+STOR+ADI+AMCC+ATML+CUBED+LSI+NSM+TXN
+XLNX
*/

// http://de.finance.yahoo.com/d/quotes.csv?f=snl1d1t1c1ohgv&s=555750.F+555750.DE

// http://quote.yahoo.com:80/d/quotes.csv?s=sunw+msft&f=sl1d1t1&e=.csv

/*
# Set to use "st1l9cv" in query string.
# Here is list of known variables that can be passed to CGI
# s  = Symbol                   l9 = Last Trade
# d1 = Date of Last Trade       t1 = Time of Last Trade
# c1 = Change                   c  = Change - Percent Change
# o  = Open Trade               h  = High Trade
# g  = Low Trade                v  = Volume
# a  = Ask Price                b  = Bid Price
# j  = 52 week low              k  = 52 week high
# n  = Name of company          p  = Previous close
# x  = Name of Stock Exchange

l1 letzter Wert
c4 WŠhrung: EUR, USD
*/



/*

http://de.finance.yahoo.com/d/quotes.csv?f=sxd3l1nvc1c4&s=555750.DE+AAPL+655971.DE

555750.DE;Xetra;14:21;13,20;DT TELEKOM N    ;11461546;+0,30;EUR
AAPL;NasdaqNM;22:00;13,88;APPLE COMP INC  ;6100;0,00;USD
655971.DE;Fund;22 Jan;98,12;FlexInvestGarant;N/A;+0,12;EUR

*/


/* Bšrsen: DE, F, BE, BM, D, H, HA, MU, SG

555750.DE;Xetra;14:26;13,23;DT TELEKOM N    ;11610120;+0,33;EUR
555750.F;Frankfurt;14:28;13,23;DT TELEKOM N    ;182967;+0,28;EUR
555750.BE;Berlin;14:18;13,21;DT TELEKOM N    ;5276;+0,35;EUR
555750.BM;Bremen;14:26;13,23;DT TELEKOM N    ;1238;+0,36;EUR
555750.D;Düsseldorf;14:17;13,25;DT TELEKOM N    ;4369;+0,25;EUR
555750.H;Hamburg;14:26;13,23;DT TELEKOM N    ;51326;+0,40;EUR
555750.HA;Hannover;13:02;13,06;DT TELEKOM N    ;4919;+0,13;EUR
555750.MU;München;12:50;13,08;DT TELEKOM N    ;4497;+0,19;EUR
555750.SG;Stuttgart;14:14;13,22;DT TELEKOM N    ;27525;+0,34;EUR

*/


/*
 --> 500340.F;Frankfurt;EUR;73,20;1/23/2003;17:47;6187;ADIDAS SALOMON
 --> DE610670.F;Frankfurt;EUR;33,500;1/23/2003;12:41;299;ABN AMEX BIOT ZT
 --> 655971.DE;Fund;EUR;98,09;1/22/2003;0:59;N/A;FlexInvestGarant
 --> 702100.MU;MŸnchen;EUR;2,63;1/23/2003;9:03;0;SAG SOLARSTROM
 --> 555750.F;Frankfurt;EUR;12,68;1/23/2003;17:59;329829;DT TELEKOM N
 --> 575980.F;Frankfurt;EUR;3,14;1/23/2003;12:00;2700;INIT INNOVATION
 --> 870737.DE;Xetra;EUR;13,27;1/23/2003;18:03;5742070;NOKIA
 --> 555770.DE;Xetra;EUR;5,84;1/23/2003;18:00;1733340;T-ONLINE N
 --> 531370.F;Frankfurt;EUR;8,00;1/23/2003;16:20;905;CARL ZEISS MEDI
*/
