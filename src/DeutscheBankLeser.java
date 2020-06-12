/**
 @author Thomas Much
 @version 1999-12-09
*/

import java.io.*;
import java.net.*;




public class DeutscheBankLeser extends Thread {

private KursReceiver receiver;
private String request,baWKN,baBoerse;
private boolean sofortZeichnen;
private int nextID;




public DeutscheBankLeser(KursReceiver receiver, String request, String baWKN, String baBoerse, int nextID) {
	this(receiver,request,baWKN,baBoerse,false,nextID);
}



public DeutscheBankLeser(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, int nextID) {
	super();

	this.receiver = receiver;
	this.request = request;
	this.baWKN = baWKN;
	this.baBoerse = baBoerse;
	this.sofortZeichnen = sofortZeichnen;
	this.nextID = nextID;
}



public void run() {
	BufferedReader in = null;
	
	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	try
	{
		int sp = request.indexOf(".");

		String spwkn    = request.substring(0,sp);
		String spboerse = request.substring(sp+1);

		URL url = new URL(AktienMan.url.getDeubaKursURL(spwkn,spboerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String str_fehler = AktienMan.url.getString(URLs.STR_DEUBA_KURSFEHLER);
		String str_titel  = AktienMan.url.getString(URLs.STR_DEUBA_KURSTITEL);
		String str_ende   = AktienMan.url.getString(URLs.STR_DEUBA_KURSENDE);
		String str_zeile  = AktienMan.url.getString(URLs.STR_DEUBA_KURSZEILE);
		String str_symbol = AktienMan.url.getString(URLs.STR_DEUBA_KURSSYMBOL);
		
		String s, lastline = "";
		String name = null, platz = null, wkn = null, kursdatum = "", kurz = "";
		long kurs = BenutzerAktie.VALUE_MISSING;
		long hoechstkurs = BenutzerAktie.VALUE_NA;
		long tiefstkurs = BenutzerAktie.VALUE_NA;
		long vortageskurs = BenutzerAktie.VALUE_NA;
		long handelsvolumen = 0L;
		int i,i2,i3, status = 0;
		boolean found = false;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				i = s.indexOf(str_fehler);
				
				if (i > 0)
				{
					receiver.listeAnfrageFalsch(baWKN,baBoerse,sofortZeichnen);
					found = true;
					break;
				}

				i = s.indexOf(str_titel);
				
				if (i > 0)
				{
					i2 = s.indexOf(str_symbol,i);
					i3 = s.indexOf(".",i2);

					kurz = s.substring(i2+str_symbol.length(),i3).trim();

					i2 = s.indexOf("\"",i3);
					
					platz = s.substring(i3+1,i2).trim();

					i2 = lastline.indexOf(">",lastline.indexOf(">")+1);
					i3 = lastline.indexOf("<",i2);

					name = lastline.substring(i2+1,i3).trim();

					wkn = null;
					kurs = BenutzerAktie.VALUE_MISSING;
					hoechstkurs = BenutzerAktie.VALUE_NA;
					tiefstkurs = BenutzerAktie.VALUE_NA;
					vortageskurs = BenutzerAktie.VALUE_NA;
					handelsvolumen = 0L;
					kursdatum = "";

					status = 1;
				}
			}
			else
			{
				if (s.indexOf(str_ende) >= 0)
				{
					i2 = lastline.indexOf(">");
					i3 = lastline.indexOf("<",i2+1);

					wkn = lastline.substring(i2+1,i3).trim();
					
					if ((wkn != null) && (platz != null))
					{
						if (kurs > 0L)
						{
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							receiver.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,
																		vortageskurs,BenutzerAktie.VALUE_NA,
																		hoechstkurs,tiefstkurs,handelsvolumen,
																		kurswaehrung,sofortZeichnen);
							found = true;
						}
						else
						{
							receiver.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
							found = true;
						}
					}
					
					status = 0;
				}
				else
				{
					i = s.indexOf(str_zeile);

					if (i >= 0)
					{
						if (status == 1)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3).trim();
							
							if (kstr.equalsIgnoreCase(DeutscheBankQuelle.VALUENA))
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
									kurs = BenutzerAktie.VALUE_MISSING;
								}
							}
						}
						else if (status == 2)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);

							String vortag = s.substring(i2+1,i3).trim();
							
							if (vortag.equalsIgnoreCase(DeutscheBankQuelle.VALUENA))
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
						else if (status == 3)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);

							String volumen = s.substring(i2+1,i3).trim();

							try
							{
								handelsvolumen = Long.parseLong(volumen);
							}
							catch (NumberFormatException e)
							{
								handelsvolumen = 0L;
							}
						}
						else if (status == 4)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);

							String hoechst = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							String tiefst = s.substring(i2+1,i3).trim();
							
							if (hoechst.equalsIgnoreCase(DeutscheBankQuelle.VALUENA))
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

							if (tiefst.equalsIgnoreCase(DeutscheBankQuelle.VALUENA))
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
						else if (status == 5)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);
							
							kursdatum = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							kursdatum = kursdatum + " " + s.substring(i2+1,i3).trim();
						}

						status++;
					}
				}
			}
			
			lastline = s;
		}
		
		if (!found)
		{
			receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("DeutscheBank-URL fehlerhaft.");
		receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	catch (NullPointerException e)
	{
		receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	catch (IOException e)
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
			catch (IOException e) {}
		
			in = null;
		}
	}
}

}
