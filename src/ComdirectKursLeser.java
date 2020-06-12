/**
 @author Thomas Much
 @version 2003-03-04
*/

import java.io.*;
import java.net.*;
import java.util.*;




public class ComdirectKursLeser implements Runnable {

private static final String COMDIRECT_ENCODING = "ISO-8859-15";

private static final int STATUS_FINISHED       =  0;
private static final int STATUS_WAIT4SYMBOL    =  1;
private static final int STATUS_WAIT4NAME      =  2;
private static final int STATUS_READCURRVAL    =  3;
private static final int STATUS_READVORTAGDIFF =  4;
private static final int STATUS_WAIT4ISINWKN   =  5;
private static final int STATUS_READDATETIME   =  6;
private static final int STATUS_WAIT4REST      =  7;
private static final int STATUS_READWAEHRUNG   =  8;
private static final int STATUS_WAIT4VOLUMEN   =  9;
private static final int STATUS_READVOLUMEN    = 10;
private static final int STATUS_READEROEFFNUNG = 11;
private static final int STATUS_WAIT4HOCH      = 12;
private static final int STATUS_READHOCH       = 13;
private static final int STATUS_WAIT4TIEF      = 14;
private static final int STATUS_READTIEF       = 15;

private KursReceiver receiver;
private KursQuelle quelle,first;
private boolean sofortZeichnen;
private String request,wkn,boerse;



public ComdirectKursLeser(KursQuelle quelle, KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, KursQuelle first) {

	this.quelle = quelle;
	this.receiver = receiver;
	this.request = request;
	this.wkn = baWKN;
	this.boerse = baBoerse;
	this.sofortZeichnen = sofortZeichnen;
	this.first = first;
}



public void run() {

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	// wir brauchen hier keine Umwandlungsroutinen, da die interne Bšrsen-ID der Comdirect-Bšrse entspricht
	final String request = "http://informer2.comdirect.de/de/suche/main.html?sid=&alias=wertpapiersuche&searchfor="+wkn+"&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen="+boerse;

	System.out.println("ANFRAGE: "+request); // TODO

	BufferedReader in = null;

	try
	{
		Connections.getConnection();
		
		URL url = new URL(request);

		in = new BufferedReader(new InputStreamReader( NetUtil.getRedirectedInputStream(url), COMDIRECT_ENCODING));
		
		String name = "", isin = null, symbol = "", wkn = "", boerse = "", kursdatum = "";

		long kurswert   = BenutzerAktie.VALUE_MISSING;
		long vortag     = BenutzerAktie.VALUE_NA;
		long eroeffnung = BenutzerAktie.VALUE_NA;
		long hoechst    = BenutzerAktie.VALUE_NA;
		long tiefst     = BenutzerAktie.VALUE_NA;
		long stueck     = 0L;
		int  waehrung   = Waehrungen.NONE;
		
		boolean success = false;
		int volcount    = 0;
		
		int status = STATUS_WAIT4SYMBOL;
		
		String s;
		
		while ((status != STATUS_FINISHED) && ((s = in.readLine()) != null))
		{
			s = s.trim();
			
			if (s.length() == 0) continue;
			
			switch(status)
			{
				case STATUS_WAIT4SYMBOL:
				{
					if (s.indexOf("Komfortsuche") >= 0)
					{
						// Fehler, WKN und/oder Bšrse nicht gefunden
						status = STATUS_FINISHED;
						break;
					}

					int i = s.indexOf("sSymbol=");
					
					if (i >= 0)
					{
						int i2 = s.indexOf('&',i);
						
						if (i2 > 0)
						{
							symbol = s.substring(i+8,i2).trim();
							
							i = symbol.indexOf('.');
							
							if (i > 0)
							{
								boerse = symbol.substring(i+1);
								symbol = symbol.substring(0,i);
								
								status = STATUS_WAIT4NAME;
							}
							else
							{
								status = STATUS_FINISHED;
							}
						}
						else
						{
							status = STATUS_FINISHED;
						}
					}
				}
				break;
				
				case STATUS_WAIT4NAME:
				{
					int i = s.indexOf("<th>");
					
					if (i >= 0)
					{
						int i2 = s.indexOf("</th>",i);
						
						if (i2 > 0)
						{
							name = s.substring(i+4,i2).trim();
							status = STATUS_READCURRVAL;
						}
						else
						{
							status = STATUS_FINISHED;
						}
					}
				}
				break;

				case STATUS_READCURRVAL:
				{
					int i = s.indexOf("alt=\"\">");
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						try
						{
							kurswert = Waehrungen.doubleToLong( removeNbsp(s.substring(i+7,i2)) );

							status = STATUS_READVORTAGDIFF;
						}
						catch (NumberFormatException e)
						{
							kurswert = BenutzerAktie.VALUE_NA;

							status = STATUS_FINISHED;
						}
					}
					else
					{
						status = STATUS_FINISHED;
					}
				}
				break;

				case STATUS_READVORTAGDIFF:
				{
					int i = s.indexOf("<nobr>");
					
					if (i >= 0)
					{
						int i2 = s.indexOf('<',i+6);
						
						if (i2 > i)
						{
							try
							{
								long vortagdiff = Waehrungen.doubleToLong( removeNbsp(s.substring(i+6,i2)) );
								
								if (kurswert > 0)
								{
									vortag = kurswert - vortagdiff;
								}
							}
							catch (NumberFormatException e) {}
						}
					}

					status = STATUS_WAIT4ISINWKN;
				}
				break;

				case STATUS_WAIT4ISINWKN:
				{
					int i = s.indexOf("ISIN:");
					
					if (i >= 0)
					{
						int i2 = s.indexOf("WKN:",i);
						int i3 = s.indexOf("B&ouml;rse:",i2);
						
						if (i3 > i2)
						{
							isin = removeNbsp( s.substring(i+5,i2) );
							wkn  = removeNbsp( s.substring(i2+4,i3) );

							status = STATUS_READDATETIME;
						}
						else
						{
							status = STATUS_FINISHED;
						}
					}
				}
				break;

				case STATUS_READDATETIME:
				{
					int i  = s.indexOf("\">");
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						kursdatum = removeNbsp( s.substring(i+2,i2) );
						
						i  = s.indexOf("\">",i2);
						i2 = s.indexOf('<',i);
						
						if (i2 > i)
						{
							kursdatum = removeNbsp( s.substring(i+2,i2) ) + " " + kursdatum;
						}
						
						success = true;

						status = STATUS_WAIT4REST;
					}
					else
					{
						status = STATUS_FINISHED;
					}
				}
				break;
				
				case STATUS_WAIT4REST:
				{
					if ((s.indexOf("W&auml;hrung") > 0) || (s.indexOf("W\u00e4hrung") > 0))
					{
						status = STATUS_READWAEHRUNG;
					}
					else if (s.indexOf("Er&ouml;ffnung") > 0)
					{
						status = STATUS_READEROEFFNUNG;
					}
				}
				break;

				case STATUS_READWAEHRUNG:
				{
					int i  = s.indexOf('>');
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						waehrung = getWaehrung( removeNbsp(s.substring(i+1,i2)) );
					}
					
					if ("DFK".equals(boerse))
					{
						status = STATUS_FINISHED;
					}
					else
					{
						status = STATUS_WAIT4VOLUMEN;
					}
				}
				break;

				case STATUS_WAIT4VOLUMEN:
				{
					if (s.indexOf("bcolorYellowB") > 0)
					{
						status = STATUS_READVOLUMEN;
					}
				}
				break;

				case STATUS_READVOLUMEN:
				{
					if (++volcount == 9)
					{
						int i  = s.indexOf('>');
						int i2 = s.indexOf('<',i);
						
						if (i2 > i)
						{
							stueck = getStueckzahl( removeNbsp(s.substring(i+1,i2)) );
						}

						status = STATUS_FINISHED;
					}
				}
				break;

				case STATUS_READEROEFFNUNG:
				{
					int i  = s.indexOf('>');
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						try
						{
							eroeffnung = Waehrungen.doubleToLong( removeNbsp(s.substring(i+1,i2)) );
						}
						catch (NumberFormatException e) {}
					}

					status = STATUS_WAIT4HOCH;
				}
				break;

				case STATUS_WAIT4HOCH:
				{
					if (s.indexOf("Hoch") > 0)
					{
						status = STATUS_READHOCH;
					}
				}
				break;

				case STATUS_READHOCH:
				{
					int i  = s.indexOf('>');
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						try
						{
							hoechst = Waehrungen.doubleToLong( removeNbsp(s.substring(i+1,i2)) );
						}
						catch (NumberFormatException e) {}
					}

					status = STATUS_WAIT4TIEF;
				}
				break;

				case STATUS_WAIT4TIEF:
				{
					if (s.indexOf("Tief") > 0)
					{
						status = STATUS_READTIEF;
					}
				}
				break;

				case STATUS_READTIEF:
				{
					int i  = s.indexOf('>');
					int i2 = s.indexOf('<',i);
					
					if (i2 > i)
					{
						try
						{
							tiefst = Waehrungen.doubleToLong( removeNbsp(s.substring(i+1,i2)) );
						}
						catch (NumberFormatException e) {}
					}

					status = STATUS_WAIT4REST;
				}
				break;
			}
		}

		if (kurswert != BenutzerAktie.VALUE_MISSING)
		{
			// TODO
			System.out.println();
			System.out.println(" Name:      "+name);
			System.out.println(" Symbol:    "+symbol);
			System.out.println(" ISIN:      "+isin);
			System.out.println(" WKN:       "+wkn);
			System.out.println(" Bšrse:     "+boerse);
			System.out.println(" Kurswert:  "+kurswert+" ("+kursdatum+")");
			System.out.println(" Vortag:    "+vortag);
			System.out.println(" Eršffnung: "+eroeffnung);
			System.out.println(" Hšchst:    "+hoechst);
			System.out.println(" Tiefst:    "+tiefst);
			System.out.println(" StŸck:     "+stueck);
			System.out.println(" WŠhrung:   "+waehrung);
		}

		if (success)
		{
			receiver.listeNeuerAktienkurs(wkn,isin,symbol,boerse,name,kurswert,kursdatum,
											vortag,eroeffnung,hoechst,tiefst,stueck,
											waehrung,sofortZeichnen);
		}
		else
		{
			receiver.listeAnfrageFehler(this.request,this.wkn,this.boerse,sofortZeichnen,first,quelle);
		}
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler im Comdirect-Kursleser", e);

		receiver.listeAnfrageFehler(this.request,this.wkn,this.boerse,sofortZeichnen,first,quelle);
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
				AktienMan.errlog("Fehler im Comdirect-Kursleser", e);
			}
		}

		Connections.releaseConnection();
	}
}



private static String removeNbsp(String s) {

	int i = s.indexOf("&nbsp;");
	
	while (i >= 0)
	{
		s = s.substring(0,i) + s.substring(i+6);
		
		i = s.indexOf("&nbsp;");
	}

	return s.trim();
}



private static long getStueckzahl(String volumen) {

	long stueck = 0L;

	boolean mio = false;
	
	int i = volumen.indexOf(" Mio.");
	
	if (i > 0)
	{
		volumen = volumen.substring(0,i).trim();
		mio = true;
	}
	
	// Tausenderpunkte entfernen

	i = volumen.indexOf(".");
	
	while (i >= 0)
	{
		volumen = volumen.substring(0,i) + volumen.substring(i+1);
		i = volumen.indexOf(".");
	}
	
	i = volumen.indexOf(",");
	
	if (i >= 0)
	{
		// Komma durch Punkt ersetzen ("8,3 Mio.")
		volumen = volumen.substring(0,i) + "." + volumen.substring(i+1);
		
		try
		{
			double wert = Double.valueOf(volumen).doubleValue();
		
			if (mio) wert *= 1000000.0;
			
			stueck = Math.round(wert);
		}
		catch (NumberFormatException e) {}
	}
	else
	{
		try
		{
			stueck = Long.parseLong(volumen);
			
			if (mio) stueck *= 1000000L;
		}
		catch (NumberFormatException e) {}
	}
	
	return stueck;
}



private static int getWaehrung(final String cd) {

	if ("EUR".equals(cd))
	{
		return Waehrungen.EUR;
	}
	else if ("EURO".equals(cd))
	{
		return Waehrungen.EUR;
	}
	else if ("USD".equals(cd))
	{
		return Waehrungen.USD;
	}
	
	return Waehrungen.NONE;
}


}
