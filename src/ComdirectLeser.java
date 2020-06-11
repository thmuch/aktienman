/**
 @author Thomas Much
 @version 1999-05-23
*/

import java.io.*;
import java.net.*;



public class ComdirectLeser extends Thread {

private String request,baWKN,baBoerse;
private boolean sofortZeichnen;
private int nextID;



public ComdirectLeser(String request, String baWKN, String baBoerse, int nextID) {
	this(request,baWKN,baBoerse,false,nextID);
}


public ComdirectLeser(String request, String baWKN, String baBoerse, boolean sofortZeichnen, int nextID) {
	super();

	this.request = request;
	this.baWKN = baWKN;
	this.baBoerse = baBoerse;
	this.sofortZeichnen = sofortZeichnen;
	this.nextID = nextID;
}


public void run() {
	BufferedReader in = null;
	
	AktienMan.checkURLs();

	try
	{
		URL url = new URL(AktienMan.url.get(URLs.URL_KURSECOMDIRECT) + request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		String name = null, platz = null, wkn = null, kursdatum = "", kurz = "";
		long kurs = BenutzerAktie.VALUE_MISSING;
		long eroeffnungskurs = BenutzerAktie.VALUE_MISSING;
		long hoechstkurs = BenutzerAktie.VALUE_MISSING;
		long tiefstkurs = BenutzerAktie.VALUE_MISSING;
		long vortageskurs = BenutzerAktie.VALUE_MISSING;
		long handelsvolumen = 0L;
		int i,i2,i3, status = 0;
		boolean found = false;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				i = s.indexOf("NAME=\"searchfor\" VALUE=");
				
				if (i > 0)
				{
					i2 = s.indexOf('"',i+20);
					i3 = s.indexOf('.',i2);
					
					wkn = s.substring(i2+1,i3).trim();
					
					i2 = s.indexOf('"',i3);
					
					platz = s.substring(i3+1,i2).trim();

					AktienMan.hauptdialog.listeAnfrageFalsch(wkn,platz,sofortZeichnen);
					found = true;
					break;
				}

				i = s.indexOf(".html?");
				
				if (i > 0)
				{
					i2 = s.indexOf('=',i);
					i3 = s.indexOf('.',i2);

					kurz = s.substring(i2+1,i3).trim();

					i2 = s.indexOf('>',i3);
					i3 = s.indexOf('<',i2);

					name = s.substring(i2+1,i3).trim();

					platz = null;
					wkn = null;
					kurs = BenutzerAktie.VALUE_MISSING;
					eroeffnungskurs = BenutzerAktie.VALUE_MISSING;
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
					if ((wkn != null) && (platz != null))
					{
						if (kurs > 0L)
						{
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							AktienMan.hauptdialog.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,
																		vortageskurs,eroeffnungskurs,
																		hoechstkurs,tiefstkurs,handelsvolumen,
																		kurswaehrung,sofortZeichnen);
							found = true;
						}
						else
						{
							AktienMan.hauptdialog.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
							found = true;
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
							
							wkn = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);
							
							platz = s.substring(i2+1,i3).trim();
						}
						else if (status == 2)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3).trim();
							
							if (kstr.equalsIgnoreCase(ComdirectQuelle.VALUENA))
							{
								AktienMan.hauptdialog.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
								found = true;
								
								status = 0;
								continue;
							}
							
							try
							{
								kurs = Waehrungen.doubleToLong(kstr);
							}
							catch (NumberFormatException e)
							{
								kurs = BenutzerAktie.VALUE_MISSING;
							}
						}
						else if (status == 3)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);
							
							kursdatum = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							kursdatum = kursdatum + " " + s.substring(i2+1,i3).trim();
						}
						else if (status == 4)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);
							
							String eroeffstr = s.substring(i2+1,i3).trim();

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
									eroeffnungskurs = BenutzerAktie.VALUE_MISSING;
								}
							}
						}
						else if (status == 5)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);

							String hoechst = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							String tiefst = s.substring(i2+1,i3).trim();
							
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
									hoechstkurs = BenutzerAktie.VALUE_MISSING;
								}
							}

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
									tiefstkurs = BenutzerAktie.VALUE_MISSING;
								}
							}
						}
						else if (status == 6)
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
						else if (status == 8)
						{
							i2 = s.indexOf(">",s.indexOf(">",i)+1);
							i3 = s.indexOf("<",i2);

							String vortag = s.substring(i2+1,i3).trim();
							
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
									vortageskurs = BenutzerAktie.VALUE_MISSING;
								}
							}
						}

						status++;
					}
				}
			}
		}
		
		if (!found)
		{
			AktienMan.hauptdialog.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-URL fehlerhaft.");
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
