/**
 @author Thomas Much
 @version 2003-04-09

 2003-04-09
 	KURZHTML
 2003-02-27
 	isValid
    getExchangeRateEUR2X, getExchangeRate, getExchangeDate
 	setExchangeRate, loadExchangeRates, saveExchangeRates
 	CHF, GBP, USD und JPY als Währungen verfügbar
 	name2Index, index2Name
*/

import java.awt.*;
import java.io.*;
import java.util.*;




public final class Waehrungen implements Runnable {

public static final int NONE = -1;
public static final int DEM  =  0;
public static final int EUR  =  1;
public static final int ATS  =  2;
public static final int CHF  =  3;
public static final int USD  =  4;
public static final int GBP  =  5;
public static final int JPY  =  6;

private static final int MIN  = DEM;
private static final int MAX  = JPY;
public static final int COUNT = MAX+1;

public static final long PRECISION = 100L;


private static final String[] IDS      = { "DEM", "EUR",    "ATS",     "CHF", "USD",  "GBP",       "JPY"    };
private static final String[] NAMES    = { "DM",  "Euro",   "\u00f6S", "sFr", "US-$", "GB-\u00a3", "Yen"    };
private static final String[] KUERZEL  = { "DM",  "\u20ac", "\u00f6S", "Fr",  "$",    "\u00a3",    "\u00a5" };
private static final String[] KURZHTML = { "DM",  "&euro;", "&ouml;S", "Fr",  "$",    "&#163;",    "&#165;" };

private static final long CONVPREC  =   100000000L;
private static final long CONVROUND =  CONVPREC/2L;
private static final int  NACHKOMMA =   (int)Math.round(Math.log(CONVPREC) / Math.log(10));

/* feste Wechselkurse */

private static final long DEM2EUR   =    51129200L;
private static final long EUR2DEM   =   195583000L;

private static final long ATS2EUR   =     7267280L;
private static final long EUR2ATS   =  1376030000L;

private static final String FIXDATE = "31.12.1998";

/* veränderliche Wechselkurse (<http://de.finance.yahoo.com/m3>) */

private static final long CHF2EUR   =    68111700L;
private static final long EUR2CHF   =   146805100L;

private static final long USD2EUR   =    93135900L;
private static final long EUR2USD   =   107370000L;

private static final long GBP2EUR   =   152500700L;
private static final long EUR2GBP   =    65611800L; 

private static final long JPY2EUR   =      772848L;
private static final long EUR2JPY   = 12936330000L;

private static final String VARDATE = "03.02.2003";


private static final double DFAKTOR = (double)PRECISION;

private static int listenWaehrung = NONE;

private static long[] rateEUR2X, rateX2EUR;
private static String[] dateEUR2X, dateX2EUR;
private static int rateSaveCount = 0;
private static boolean checked = false;



static
{
	// Workaround für JDK 1.1, sonst wird die Klasse entladen...
	new Thread(new Waehrungen()).start();
	
	loadExchangeRates();
}




private Waehrungen() {}



public void run() {

	while(true)
	{
		try
		{
			Thread.sleep(1000000L);
		}
		catch (InterruptedException e) {}	
	}
}



public static Choice getChoice() {

	Choice choice = new Choice();

	for (int i = DEM; i <= MAX; i++)
	{
		choice.addItem(IDS[i]+" ("+KUERZEL[i]+")");
	}
	
	return choice;
}



public static long doubleToLong(String s) throws NumberFormatException {

	return doubleToLong(NumUtil.getDouble(s));
}



public static long doubleToLong(double d) {

	return Math.round(d*DFAKTOR);
}



public static double longToDouble(long l) {

	return (double)l/DFAKTOR;
}



private static double sgn(double d) {

	if (d > 0.0)
	{
		return 1.0;
	}
	else if (d < 0.0)
	{
		return -1.0;
	}
	else
	{
		return 0.0;
	}
}



private static long sgn(long l) {

	if (l > 0L)
	{
		return 1L;
	}
	else if (l < 0L)
	{
		return -1L;
	}
	else
	{
		return 0L;
	}
}



public static boolean isValid(int waehrung) {

	return ((waehrung >= MIN) && (waehrung <= MAX));
}



public static String getString(long wert, int waehrung) {

	if (isValid(waehrung))
	{
		return NumUtil.get00String(wert) + " " + getKuerzel(waehrung);
	}
	else
	{
		return "<W\u00e4hrung?>";
	}
}



public static long exchange(long valFrom, int wFrom, int wTo) {

	if (wFrom == wTo)
	{
		return valFrom;
	}
	else if (wFrom == EUR)
	{
		if ((wTo >= MIN) && (wTo <= MAX))
		{
			long xrate;
			
			synchronized(rateEUR2X)
			{
				xrate = rateEUR2X[wTo];
			}

			if (xrate > 0L)
			{
				return (valFrom * xrate + sgn(valFrom) * CONVROUND) / CONVPREC;
			}
		}
	}
	else if (wTo == EUR)
	{
		if ((wFrom >= MIN) && (wFrom <= MAX))
		{
			long xrate;
			
			synchronized(rateX2EUR)
			{
				xrate = rateX2EUR[wFrom];
			}

			if (xrate > 0L)
			{
				return (valFrom * xrate + sgn(valFrom) * CONVROUND) / CONVPREC;
			}
		}
	}
	else
	{
		if ((wFrom >= MIN) && (wFrom <= MAX) && (wTo >= MIN) && (wTo <= MAX))
		{
			return exchange(exchange(valFrom,wFrom,EUR),EUR,wTo);
		}
	}

	/* Fehler: mind. eine der Währungen ist unbekannt */
//	AktienMan.errlog("Unbekannte W\u00e4hrungsumrechnung: "+wFrom+" -> "+wTo+" ("+valFrom+")",null);
	return 0L;
}



public synchronized static void setExchangeRate(int wFrom, int wTo, double factor, String date) {

	long val;
	
	if (factor <= 0.0)
	{
		val = -1L;
	}
	else
	{
		val = Math.round(factor * CONVPREC);
	}

	System.out.println("  Wechselkurs: "+wFrom+" -> "+wTo+" "+factor+" "+val+"  ("+date+")  ("+rateSaveCount+")"); // TODO

	if (wFrom == EUR)
	{
		if ((wTo >= MIN) && (wTo <= MAX))
		{
			synchronized(rateEUR2X)
			{
				rateEUR2X[wTo] = val;
				dateEUR2X[wTo] = date;
			}
		}
	}
	else if (wTo == EUR)
	{
		if ((wFrom >= MIN) && (wFrom <= MAX))
		{
			synchronized(rateX2EUR)
			{
				rateX2EUR[wFrom] = val;
				dateX2EUR[wFrom] = date;
			}
		}
	}

	rateSaveCount--;

	if (rateSaveCount <= 0)
	{
		saveExchangeRates();
	}
	
	if (AktienMan.wechselkurse != null)
	{
		AktienMan.wechselkurse.update();
	}
}



public static String getExchangeRateEUR2X(int wTo) {

	if ((wTo < MIN) || (wTo > MAX) || (wTo == EUR)) return "";

	String ret = "1 EUR = ";

	synchronized(rateEUR2X)
	{
		ret += NumUtil.getString( (double)rateEUR2X[wTo] / (double)CONVPREC);
		
		ret += " " + index2Id(wTo) + " (" + dateEUR2X[wTo] + ")";
	}
	
	return ret;
}



public static String getExchangeRate(int wFrom, int wTo) {

	if (wFrom != wTo)
	{
		if (wFrom == EUR)
		{
			if ((wTo >= MIN) && (wTo <= MAX))
			{
				synchronized(rateEUR2X)
				{
					return NumUtil.getPrecisionString( (double)rateEUR2X[wTo] / (double)CONVPREC, NACHKOMMA) + " " + index2Id(wTo);
				}
			}
		}
		else if (wTo == EUR)
		{
			if ((wFrom >= MIN) && (wFrom <= MAX))
			{
				synchronized(rateX2EUR)
				{
					return NumUtil.getPrecisionString( (double)rateX2EUR[wFrom] / (double)CONVPREC, NACHKOMMA) + " " + index2Id(EUR);
				}
			}
		}
	}
	
	return "";
}



public static String getExchangeDate(int wFrom, int wTo) {

	if (wFrom != wTo)
	{
		if (wFrom == EUR)
		{
			if ((wTo >= MIN) && (wTo <= MAX))
			{
				synchronized(rateEUR2X)
				{
					return dateEUR2X[wTo];
				}
			}
		}
		else if (wTo == EUR)
		{
			if ((wFrom >= MIN) && (wFrom <= MAX))
			{
				synchronized(rateX2EUR)
				{
					return dateX2EUR[wFrom];
				}
			}
		}
	}

	return "";
}



public synchronized static void check(boolean force) {

	if (force || !checked)
	{
		try
		{
			rateSaveCount = WechselkursLeser.start();
			checked = true;
		}
		catch (Exception e) {}
	}
}



private synchronized static void loadExchangeRates() {

	rateEUR2X = new long[COUNT];
	rateX2EUR = new long[COUNT];

	dateEUR2X = new String[COUNT];
	dateX2EUR = new String[COUNT];
	
	synchronized(rateEUR2X)
	{
		synchronized(rateX2EUR)
		{
			rateEUR2X[EUR] = CONVPREC;
			rateEUR2X[DEM] = EUR2DEM;
			rateEUR2X[ATS] = EUR2ATS;
			rateEUR2X[CHF] = EUR2CHF;
			rateEUR2X[USD] = EUR2USD;
			rateEUR2X[GBP] = EUR2GBP;
			rateEUR2X[JPY] = EUR2JPY;

			rateX2EUR[EUR] = CONVPREC;
			rateX2EUR[DEM] = DEM2EUR;
			rateX2EUR[ATS] = ATS2EUR;
			rateX2EUR[CHF] = CHF2EUR;
			rateX2EUR[USD] = USD2EUR;
			rateX2EUR[GBP] = GBP2EUR;
			rateX2EUR[JPY] = JPY2EUR;
			
			for (int i = DEM; i < CHF; i++)
			{
				dateEUR2X[i] = FIXDATE;
				dateX2EUR[i] = FIXDATE;
			}
			
			for (int i = CHF; i <= JPY; i++)
			{
				dateEUR2X[i] = VARDATE;
				dateX2EUR[i] = VARDATE;
			}

			BufferedReader xmlin = null;
			
			try
			{
				xmlin = new BufferedReader(
							new InputStreamReader(
								new FileInputStream(FileUtil.getExchangeRateFile()),XMLUtil.ENCODING));
				
				XMLUtil.checkValidPrologue(xmlin);
				
				String s = XMLUtil.nextDataLine(xmlin);
				
				if (!s.startsWith("<exchangeRates")) throw new Exception("Unerwartetes Tag: " + s);

				s = XMLUtil.nextDataLine(xmlin);

				while (s.startsWith("<rate"))
				{
					int wFrom   = NONE;
					int wTo     = NONE;
					long val    = -1L;
					String date = "";
					
					s = XMLUtil.nextDataLine(xmlin);
					
					while (!s.startsWith("</rate"))
					{
						if (s.startsWith("<date"))
						{
							date = XMLUtil.getValue(s,false);
						}
						else if (s.startsWith("<from"))
						{
							wFrom = id2Index( XMLUtil.getValue(s,true) );
						}
						else if (s.startsWith("<to"))
						{
							wTo = id2Index( XMLUtil.getValue(s,true) );
						}
						else if (s.startsWith("<value"))
						{
							val = XMLUtil.getLongValue(s);
						}

						s = XMLUtil.nextDataLine(xmlin);
					}
					
					System.out.println("Wechselkurs geladen: "+wFrom+" -> "+wTo+" = "+val+" ("+date+")"); // TODO
					
					if (wFrom == EUR)
					{
						if ((wTo >= CHF) && (wTo <= MAX))
						{
							rateEUR2X[wTo] = val;
							dateEUR2X[wTo] = date;
						}
					}
					else if (wTo == EUR)
					{
						if ((wFrom >= CHF) && (wFrom <= MAX))
						{
							rateX2EUR[wFrom] = val;
							dateX2EUR[wFrom] = date;
						}
					}
				
					s = XMLUtil.nextDataLine(xmlin);
				}

				if (!s.startsWith("</exchangeRates")) throw new Exception("Unerwartetes Tag: " + s);
			}
			catch (Exception e) {}
			finally
			{
				if (xmlin != null)
				{
					try
					{
						xmlin.close();
					}
					catch (IOException e) {}
				}
			}
			
		}
	}
}



private static void saveExchangeRates() {

	synchronized(rateEUR2X)
	{
		synchronized(rateX2EUR)
		{
			PrintWriter xmlout = null;

			try
			{
				xmlout = new PrintWriter(
							new BufferedWriter(
								new OutputStreamWriter(
									new FileOutputStream(FileUtil.getExchangeRateFile()),XMLUtil.ENCODING)),false);
				
				XMLUtil.writePrologue(xmlout);
				XMLUtil.writeComment(xmlout,"ACHTUNG: Veraendern Sie diese Datei in keiner Weise, AktienMan kann derzeit noch keine beliebigen XML-Dokumente einlesen!");
				XMLUtil.writeComment(xmlout,new Date().toString());

				xmlout.println("<exchangeRates>");
				xmlout.println();
				
				for (int i = CHF; i <= MAX; i++)
				{
					xmlout.println("<rate>");

					xmlout.println("<from>EUR</from>");
					xmlout.println("<to>" + index2Id(i) + "</to>");
					xmlout.println("<value>" + rateEUR2X[i] + "</value>");
					xmlout.println("<date>" + dateEUR2X[i] + "</date>");

					xmlout.println("</rate>");
					xmlout.println();

					xmlout.println("<rate>");

					xmlout.println("<from>" + index2Id(i) + "</from>");
					xmlout.println("<to>EUR</to>");
					xmlout.println("<value>" + rateX2EUR[i] + "</value>");
					xmlout.println("<date>" + dateX2EUR[i] + "</date>");

					xmlout.println("</rate>");
					xmlout.println();
				}

				xmlout.println("</exchangeRates>");

				xmlout.flush();
			}
			catch (Exception e) {}
			finally
			{
				if (xmlout != null) xmlout.close();
			}
		}
	}
}



public synchronized static int getStandardKaufwaehrung() {

	return AktienMan.properties.getInt("Konfig.StdWaehrung",EUR);
}



public synchronized static int getVerkaufsWaehrung() {

	return getListenWaehrung();
}



public synchronized static int getListenWaehrung() {

	if (listenWaehrung <= NONE)
	{
		listenWaehrung = AktienMan.properties.getInt("Konfig.Listenwaehrung",EUR);
	}
	
	return listenWaehrung;
}



public synchronized static void setListenWaehrung(int neu) {

	AktienMan.properties.setInt("Konfig.Listenwaehrung",neu);
	listenWaehrung = neu;
}



public static String getKuerzel(int waehrung) {

	if ((waehrung >= 0) && (waehrung < KUERZEL.length))
	{
		return KUERZEL[waehrung];
	}
	else
	{
		return "";
	}
}



public static String getName(int waehrung) {

	if ((waehrung >= 0) && (waehrung < NAMES.length))
	{
		return NAMES[waehrung];
	}
	else
	{
		return "";
	}
}



public static int id2Index(String s) {

	if (s != null)
	{
		for (int i = 0; i < IDS.length; i++)
		{
			if (s.equals(IDS[i])) return i;
		}
	}
	
	return NONE;
}



public static String index2Id(int index) {

	if ((index >= 0) && (index < IDS.length))
	{
		return IDS[index];
	}
	else
	{
		return "";
	}
}


}
