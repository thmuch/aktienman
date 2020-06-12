/**
 @author Thomas Much
 @version 2002-12-16
*/

import java.io.*;
import java.net.*;



public class ComdirectLeser extends Thread {

private static final String READ_LIST = "LISTE";

private static final int STATUS_WAIT4NAME     =  0;
private static final int STATUS_WAIT4WKN      =  1;
private static final int STATUS_WAIT4SYMBOL   =  3;
private static final int STATUS_READFONDSKURS =  5;
private static final int STATUS_READKURS      =  6;
private static final int STATUS_WAIT4ZEIT     =  7;
private static final int STATUS_WAIT4REST     =  9;
private static final int STATUS_READVOLUMEN   = 10;
private static final int STATUS_READVORTAG    = 11;
private static final int STATUS_READEROEFF    = 12;
private static final int STATUS_READHOECHST   = 13;
private static final int STATUS_READTIEFST    = 14;
private static final int STATUS_FINISHED      = 15;

private KursReceiver receiver;
private String request,baWKN,baBoerse;
private boolean sofortZeichnen;
private int nextID;




public ComdirectLeser(KursReceiver receiver, String request, String baWKN, String baBoerse, int nextID) {

	this(receiver,request,baWKN,baBoerse,false,nextID);
}



public ComdirectLeser(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, int nextID) {

	super();

	this.receiver = receiver;
	this.request = request;
	this.baWKN = baWKN;
	this.baBoerse = baBoerse;
	this.sofortZeichnen = sofortZeichnen;
	this.nextID = nextID;
}



private String fixKurs(String kstr) {

	int i;
	
	kstr = kstr.trim();
	
	if ((i = kstr.indexOf(" ")) > 0)
	{
		kstr = kstr.substring(0,i);
	}

	if ((i = kstr.indexOf("&")) > 0)
	{
		kstr = kstr.substring(0,i);
	}
	
	while (kstr.length() > 0)
	{
		if (Character.isDigit(kstr.charAt(kstr.length() - 1))) break;
		
		kstr = kstr.substring(0,kstr.length() - 1);
	}
	
	while (((i = kstr.indexOf(".")) > 0) && (i < kstr.indexOf(",")))
	{
		kstr = kstr.substring(0,i) + kstr.substring(i+1);
	}
	
	return kstr;
}



private String readKursliste(BufferedReader in, String wkn, String boerse) throws Exception {

	String s;
	
	String str_wkn    = AktienMan.url.getString(URLs.STR_CD_LISTEWKNLI) + wkn + AktienMan.url.getString(URLs.STR_CD_LISTEWKNRE);
	String str_boerse = AktienMan.url.getString(URLs.STR_CD_LISTEBOERSELI) + boerse + AktienMan.url.getString(URLs.STR_CD_LISTEBOERSERE);
	String str_reset  = AktienMan.url.getString(URLs.STR_CD_LISTERESET);
	String str_quote  = AktienMan.url.getString(URLs.STR_CD_LISTEQUOTE);

	int status = STATUS_WAIT4WKN;

	while ((s = in.readLine()) != null)
	{
		s = s.trim();

		if (s.length() == 0) continue;
		
		switch (status)
		{
		case STATUS_WAIT4WKN:

			if (s.indexOf(str_wkn) >= 0)
			{
				status = STATUS_WAIT4SYMBOL;
			}
			break;
		
		case STATUS_WAIT4SYMBOL:
		
			if (s.indexOf(str_reset) >= 0)
			{
				status = STATUS_WAIT4WKN;
			}
			else
			{
				System.out.println("    "+s); /* TODO */

				int i = s.indexOf(str_quote);
				
				if (i >= 0)
				{
					int leftquote  = s.lastIndexOf('"',i);
					int rightquote = s.indexOf('"',i);
					
					if ((leftquote >= 0) && (rightquote > leftquote))
					{
						String neueurl = s.substring(leftquote+1,rightquote);

						if (neueurl.indexOf(str_boerse) >= 0)
						{
							return AktienMan.url.getBase(URLs.BASE_COMDIRECT) + neueurl;
						}
					}
				}
			}
			break;
		}
	}
	
	return null;
}



private String readKurs(BufferedReader in, String reqBoerse, boolean readListe) throws Exception {

	String str_fehler    = AktienMan.url.getString(URLs.STR_CD_KURSFEHLER);
	String str_aktkurs   = AktienMan.url.getString(URLs.STR_CD_KURS);
	String str_fondskurs = AktienMan.url.getString(URLs.STR_CD_FONDSKURS);
	String str_dfk       = AktienMan.url.getString(URLs.STR_CD_KURSDFK);
	String str_zeit      = AktienMan.url.getString(URLs.STR_CD_KURSZEIT);
	String str_volumen   = AktienMan.url.getString(URLs.STR_CD_KURSVOLUMEN);
	String str_ende      = AktienMan.url.getString(URLs.STR_CD_KURSENDE);
	String str_titel     = AktienMan.url.getString(URLs.STR_CD_KURSTITEL);
	String str_wkn       = AktienMan.url.getString(URLs.STR_CD_KURSWKN);
	String str_symbol    = AktienMan.url.getString(URLs.STR_CD_KURSSYMBOL);
	String str_vortag    = AktienMan.url.getString(URLs.STR_CD_KURSVORTAG);
	String str_eroeff    = AktienMan.url.getString(URLs.STR_CD_KURSEROEFF);
	String str_hoechst   = AktienMan.url.getString(URLs.STR_CD_KURSHOECHST);
	String str_tiefst    = AktienMan.url.getString(URLs.STR_CD_KURSTIEFST);

	String str_quote     = AktienMan.url.getString(URLs.STR_CD_LISTEQUOTE);
	String str_boerse    = AktienMan.url.getString(URLs.STR_CD_LISTEBOERSELI) + reqBoerse + AktienMan.url.getString(URLs.STR_CD_LISTEBOERSERE);
	
	String s;
	String name = "", platz = "", wkn = "", kursdatum = "", kurz = "";
	long kurs = BenutzerAktie.VALUE_MISSING;
	long eroeffnungskurs = BenutzerAktie.VALUE_NA;
	long hoechstkurs = BenutzerAktie.VALUE_NA;
	long tiefstkurs = BenutzerAktie.VALUE_NA;
	long vortageskurs = BenutzerAktie.VALUE_NA;
	long handelsvolumen = 0L;
	int volcount = 0;
	int status = STATUS_WAIT4NAME;
	boolean found = false;
	boolean valid = false;
	
	while (((s = in.readLine()) != null) && (status != STATUS_FINISHED))
	{
		s = s.trim();

		if (s.length() == 0) continue;
		
		if (s.indexOf(str_ende) >= 0)
		{
			status = STATUS_FINISHED;
			continue;
		}
		else if (status < STATUS_WAIT4REST)
		{
			int iq = s.indexOf(str_quote);
			
			if (iq >= 0)
			{
				int leftquote  = s.lastIndexOf('"',iq);
				int rightquote = s.indexOf('"',iq);
				
				if ((leftquote >= 0) && (rightquote > leftquote))
				{
					String neueurl = s.substring(leftquote+1,rightquote);
					
					if (neueurl.indexOf(str_boerse) >= 0)
					{
						System.out.println("    Neue URL: "+AktienMan.url.getBase(URLs.BASE_COMDIRECT) + neueurl); /* TODO */

						/* neue URL mit korrekten Quote-Werten lesen */
						return AktienMan.url.getBase(URLs.BASE_COMDIRECT) + neueurl;
					}
				}
			}
		}

		switch (status)
		{
		case STATUS_WAIT4NAME:

			if (s.indexOf(str_fehler) >= 0)
			{
				if (readListe)
				{
					/* Quelle soll als Kursliste interpretiert werden */
					return READ_LIST;
				}
				else
				{
					receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
					status = STATUS_FINISHED;
					found = true;
				}
			}
			else if (s.indexOf(str_titel) >= 0)
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					name = s.substring(i+1,i2).trim();

					status = STATUS_WAIT4WKN;
					valid = true;
				}
				else
				{
					status = STATUS_FINISHED;
				}
			}
			break;
		
		case STATUS_WAIT4WKN:
			{
				int wknpos = s.indexOf(str_wkn);
				
				if (wknpos > 0)
				{
					int i = s.indexOf(";", wknpos);
					int i2 = s.indexOf("&", i + 1);
					
					if ((i > 0) && (i2 > i))
					{
						wkn = s.substring(i+1,i2).trim();
					}
					
					if (s.indexOf(str_dfk,i2) > 0)
					{
						platz = "DFK";
						
						status = STATUS_WAIT4ZEIT;
						break;
					}
					else
					{
						int sympos = s.indexOf(str_symbol,i2);
						
						if (sympos > 0)
						{
							i = s.indexOf(";", sympos);
							i2 = s.indexOf("<", i + 1);
							
							if ((i > 0) && (i2 > i))
							{
								String symbol = s.substring(i+1,i2).trim();
								
								i = symbol.indexOf(".");
								
								if (i > 0)
								{
									kurz = symbol.substring(0,i);
									platz = symbol.substring(i+1);

									status = STATUS_WAIT4ZEIT;
									break;
								}
							}
						}
					}

					status = STATUS_FINISHED;
				}
			}
			break;

		case STATUS_WAIT4ZEIT:
			{
				int zeitpos = s.indexOf(str_zeit);
				
				if (zeitpos >= 0)
				{
					int i = s.indexOf(">", zeitpos);
					int i2 = s.indexOf("&", i+1);
					
					if ((i > 0) && (i2 > i))
					{
						kursdatum = s.substring(i+1,i2).trim();
						
						i = s.indexOf("\">", i2);
						i2 = s.indexOf("&", i+1);
						
						if ((i > 0) && (i2 > i))
						{
							kursdatum += " " + s.substring(i+2,i2).trim();
						}
					}
				}

				status = STATUS_WAIT4REST;
			}
			break;
		
		case STATUS_READVOLUMEN:
			{
				if (++volcount == 9)
				{
					int i = s.indexOf(";");
					int i2 = s.indexOf("&",i+1);

					if ((i > 0) && (i2 > i))
					{
						String volumen = s.substring(i+1,i2).trim();
						
						boolean mio = false;
						
						i = volumen.indexOf(" Mio.");
						
						if (i > 0)
						{
							volumen = volumen.substring(0,i).trim();
							mio = true;
						}

						i = volumen.indexOf(".");
						
						while (i >= 0)
						{
							volumen = volumen.substring(0,i) + volumen.substring(i+1);
							i = volumen.indexOf(".");
						}

						try
						{
							handelsvolumen = Long.parseLong(volumen);
							
							if (mio) handelsvolumen *= 1000000L;
						}
						catch (NumberFormatException e) {}
					}
					
					status = STATUS_WAIT4REST;
				}
			}
			break;

		case STATUS_WAIT4REST:
		
			if (s.indexOf(str_fondskurs) > 0)
			{
				status = STATUS_READFONDSKURS;
			}
			else if (s.indexOf(str_aktkurs) > 0)
			{
				status = STATUS_READKURS;
			}
			else if (s.indexOf(str_volumen) >= 0)
			{
				status = STATUS_READVOLUMEN;
			}
			else if (s.indexOf(str_vortag) > 0)
			{
				status = STATUS_READVORTAG;
			}
			else if (s.indexOf(str_eroeff) > 0)
			{
				status = STATUS_READEROEFF;
			}
			else if (s.indexOf(str_hoechst) > 0)
			{
				status = STATUS_READHOECHST;
			}
			else if (s.indexOf(str_tiefst) > 0)
			{
				status = STATUS_READTIEFST;
			}
			break;
		
		case STATUS_READFONDSKURS:
		case STATUS_READKURS:
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					String kstr = fixKurs(s.substring(i+1,i2));
					
					if (kstr.equalsIgnoreCase(ComdirectQuelle.VALUENA))
					{
						kurs = BenutzerAktie.VALUE_NA;
					}
					else
					{
						try
						{
							kurs = Waehrungen.doubleToLong(kstr);
						}
						catch (NumberFormatException e)
						{
							kurs = BenutzerAktie.VALUE_NA;
						}
					}
					
					if (status == STATUS_READFONDSKURS)
					{
						status = STATUS_FINISHED;
					}
					else
					{
						status = STATUS_WAIT4REST;
					}
				}
				else
				{
					status = STATUS_FINISHED;
				}
			}
			break;

		case STATUS_READVORTAG:
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					String vortag = fixKurs(s.substring(i+1,i2));

					if (vortag.equalsIgnoreCase(ComdirectQuelle.VALUENA))
					{
						vortageskurs = BenutzerAktie.VALUE_NA;
					}
					else
					{
						try
						{
							vortageskurs = Waehrungen.doubleToLong(vortag);
						}
						catch (NumberFormatException e)
						{
							vortageskurs = BenutzerAktie.VALUE_NA;
						}
					}
				}

				status = STATUS_WAIT4REST;
			}
			break;

		case STATUS_READEROEFF:
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					String eroeffstr = fixKurs(s.substring(i+1,i2));

					if (eroeffstr.equalsIgnoreCase(ComdirectQuelle.VALUENA))
					{
						eroeffnungskurs = BenutzerAktie.VALUE_NA;
					}
					else
					{
						try
						{
							eroeffnungskurs = Waehrungen.doubleToLong(eroeffstr);
						}
						catch (NumberFormatException e)
						{
							eroeffnungskurs = BenutzerAktie.VALUE_NA;
						}
					}
				}

				status = STATUS_WAIT4REST;
			}
			break;

		case STATUS_READHOECHST:
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					String hoechst = fixKurs(s.substring(i+1,i2));

					if (hoechst.equalsIgnoreCase(ComdirectQuelle.VALUENA))
					{
						hoechstkurs = BenutzerAktie.VALUE_NA;
					}
					else
					{
						try
						{
							hoechstkurs = Waehrungen.doubleToLong(hoechst);
						}
						catch (NumberFormatException e)
						{
							hoechstkurs = BenutzerAktie.VALUE_NA;
						}
					}
				}

				status = STATUS_WAIT4REST;
			}
			break;

		case STATUS_READTIEFST:
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i+1);
				
				if ((i > 0) && (i2 > i))
				{
					String tiefst = fixKurs(s.substring(i+1,i2));

					if (tiefst.equalsIgnoreCase(ComdirectQuelle.VALUENA))
					{
						tiefstkurs = BenutzerAktie.VALUE_NA;
					}
					else
					{
						try
						{
							tiefstkurs = Waehrungen.doubleToLong(tiefst);
						}
						catch (NumberFormatException e)
						{
							tiefstkurs = BenutzerAktie.VALUE_NA;
						}
					}
				}

				status = STATUS_WAIT4REST;
			}
			break;
		}
	}
	
	if (valid)
	{
		if (baWKN.equalsIgnoreCase(wkn) && reqBoerse.equalsIgnoreCase(platz))
		{
			if (kurs > 0L)
			{
				receiver.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,
															vortageskurs,eroeffnungskurs,
															hoechstkurs,tiefstkurs,handelsvolumen,
															Waehrungen.getOnlineWaehrung(),sofortZeichnen);
			}
			else
			{
				receiver.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
			}

			found = true;
		}
	}
	
	if (!found)
	{
		receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	
	System.out.println("  Status == "+status+" "+valid+" "+found+"  "+name+" "+platz+" "+wkn+" "+kurz+"  "+kurs); /* TODO */

	return null;
}



public void run() {

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

		int sp = request.indexOf(".");

		String spwkn    = request.substring(0,sp);
		String spboerse = request.substring(sp+1);
		
		System.out.println("CD: "+AktienMan.url.getComdirectKursURL(spwkn,spboerse)); /* TODO */

		URL url = new URL(AktienMan.url.getComdirectKursURL(spwkn,spboerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String neueURL = readKurs(in,spboerse,true);
		
		if (neueURL != null)
		{
			if (neueURL.equals(READ_LIST))
			{
				neueURL = readKursliste(in,baWKN,spboerse);
				
				if (neueURL == null)
				{
					throw new Exception();
				}

				System.out.println("  Liste -> "+neueURL); /* TODO */
			}

			try
			{
				in.close();
			}
			catch (Exception e) {}
			finally
			{
				in = null;
			}
			
			url = new URL(neueURL);

			in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			if (readKurs(in,spboerse,false) != null)
			{
				throw new Exception();
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-URL fehlerhaft.");
		receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	catch (Exception e)
	{
		receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	finally
	{
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

		Connections.releaseConnection();
	}
}

}
