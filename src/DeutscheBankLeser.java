/**
 @author Thomas Much
 @version 1999-01-29
*/

import java.io.*;
import java.net.*;



public class DeutscheBankLeser extends Thread {

private String request,baWKN,baBoerse;
private boolean sofortZeichnen;
private int nextID;



public DeutscheBankLeser(String request, String baWKN, String baBoerse, int nextID) {
	this(request,baWKN,baBoerse,false,nextID);
}


public DeutscheBankLeser(String request, String baWKN, String baBoerse, boolean sofortZeichnen, int nextID) {
	super();

	this.request = request;
	this.baWKN = baWKN;
	this.baBoerse = baBoerse;
	this.sofortZeichnen = sofortZeichnen;
	this.nextID = nextID;
}


public void run() {
	BufferedReader in = null;

	/* #Ablaufdatum */
	/* #Demoversion */
	if (!(new ADate().before(new ADate(1999,5,9))) && !AktienMan.hauptdialog.main()) return;

	try
	{
		URL url = new URL(URLs.KURSE_DEUTSCHEBANK+request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s, lastline = "";
		String name = null, platz = null, wkn = null, kursdatum = "", kurz = "";
		long kurs = BenutzerAktie.VALUE_MISSING;
		long hoechstkurs = BenutzerAktie.VALUE_MISSING;
		long tiefstkurs = BenutzerAktie.VALUE_MISSING;
		long vortageskurs = BenutzerAktie.VALUE_MISSING;
		long handelsvolumen = 0L;
		int i,i2,i3, status = 0;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				i = s.indexOf(" keine Ergebnisse ");
				
				if (i > 0)
				{
					AktienMan.hauptdialog.listeAnfrageFalsch(baWKN,baBoerse,sofortZeichnen);
					break;
				}

				i = s.indexOf(".html?show=");
				
				if (i > 0)
				{
					i2 = s.indexOf("&showi=",i);
					i3 = s.indexOf(".",i2);

					kurz = s.substring(i2+7,i3).trim();

					i2 = s.indexOf("\"",i3);
					
					platz = s.substring(i3+1,i2).trim();

					i2 = lastline.indexOf(">",lastline.indexOf(">")+1);
					i3 = lastline.indexOf("<",i2);

					name = lastline.substring(i2+1,i3).trim();

					wkn = null;
					kurs = BenutzerAktie.VALUE_MISSING;
					hoechstkurs = BenutzerAktie.VALUE_MISSING;
					tiefstkurs = BenutzerAktie.VALUE_MISSING;
					vortageskurs = BenutzerAktie.VALUE_MISSING;
					handelsvolumen = 0L;
					kursdatum = "";

					status = 1;
				}
			}
			else
			{
				if (s.indexOf("</TR>") >= 0)
				{
					i2 = lastline.indexOf(">");
					i3 = lastline.indexOf("<",i2+1);

					wkn = lastline.substring(i2+1,i3).trim();
					
					if ((wkn != null) && (platz != null))
					{
						if (kurs > 0L)
						{
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							AktienMan.hauptdialog.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,
																		vortageskurs,BenutzerAktie.VALUE_NA,
																		hoechstkurs,tiefstkurs,handelsvolumen,
																		kurswaehrung,sofortZeichnen);
						}
						else
						{
							AktienMan.hauptdialog.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
						}
					}
					
					status = 0;
				}
				else
				{
					i = s.indexOf("<TD");

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
									vortageskurs = BenutzerAktie.VALUE_MISSING;
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
									hoechstkurs = BenutzerAktie.VALUE_MISSING;
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
									tiefstkurs = BenutzerAktie.VALUE_MISSING;
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
	}
	catch (MalformedURLException e)
	{
		System.out.println("DeutscheBank-URL fehlerhaft.");
		AktienMan.hauptdialog.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	catch (NullPointerException e)
	{
		AktienMan.hauptdialog.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
	}
	catch (IOException e)
	{
		AktienMan.hauptdialog.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
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
