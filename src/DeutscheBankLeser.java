/**
 @author Thomas Much
 @version 2000-03-22
*/

import java.io.*;
import java.net.*;




public class DeutscheBankLeser extends Thread {

private static final int STATUS_WAIT4TITLE     = 0;
private static final int STATUS_READKURS       = 1;
private static final int STATUS_READVORTAG     = 2;
private static final int STATUS_READVOLUMEN    = 3;
private static final int STATUS_READHOCHTIEF   = 4;
private static final int STATUS_READDATUM      = 5;
private static final int STATUS_WAIT4ENDOFDATA = 6;
private static final int STATUS_FINISHED       = 7;

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

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

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
		String name = "", platz = "", wkn = "", kursdatum = "", kurz = "";
		long kurs = BenutzerAktie.VALUE_MISSING;
		long hoechstkurs = BenutzerAktie.VALUE_NA;
		long tiefstkurs = BenutzerAktie.VALUE_NA;
		long vortageskurs = BenutzerAktie.VALUE_NA;
		long handelsvolumen = 0L;
		boolean found = false;
		
		int status = STATUS_WAIT4TITLE;
		
		while (((s = in.readLine()) != null) && (status != STATUS_FINISHED))
		{
			s = s.trim();
		
			if (s.length() == 0) continue;
			
			if (status == STATUS_WAIT4TITLE)
			{
				int i = s.indexOf(str_fehler);
				
				if (i > 0)
				{
					receiver.listeAnfrageFehler(request,baWKN,baBoerse,sofortZeichnen,nextID);
					status = STATUS_FINISHED;
					found = true;
				}
				else
				{
					i = s.indexOf(str_titel);
					
					if (i > 0)
					{
						int i2 = s.indexOf(str_symbol,i);
						int i3 = s.indexOf(".",i2);

						kurz = s.substring(i2+str_symbol.length(),i3).trim();

						i2 = s.indexOf("\"",i3);
						
						platz = s.substring(i3+1,i2).trim();

						i2 = lastline.indexOf(">",lastline.indexOf(">")+1);
						i3 = lastline.indexOf("<",i2);

						name = lastline.substring(i2+1,i3).trim();
						
						wkn = "";
						kursdatum = "";
						kurs = BenutzerAktie.VALUE_MISSING;
						hoechstkurs = BenutzerAktie.VALUE_NA;
						tiefstkurs = BenutzerAktie.VALUE_NA;
						vortageskurs = BenutzerAktie.VALUE_NA;
						handelsvolumen = 0L;

						status = STATUS_READKURS;
					}
				}
			}
			else
			{
				if (s.indexOf(str_ende) >= 0)
				{
					int i2 = lastline.indexOf(">");
					int i3 = lastline.indexOf("<",i2+1);

					wkn = lastline.substring(i2+1,i3).trim();
					
					if (baWKN.equalsIgnoreCase(wkn) && spboerse.equalsIgnoreCase(platz))
					{
						if (kurs > 0L)
						{
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							receiver.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,
																		vortageskurs,BenutzerAktie.VALUE_NA,
																		hoechstkurs,tiefstkurs,handelsvolumen,
																		kurswaehrung,sofortZeichnen);
						}
						else
						{
							receiver.listeAktienkursNA(wkn,kurz,platz,name,sofortZeichnen);
						}
						
						status = STATUS_FINISHED;
						found = true;
					}
					else
					{
						status = STATUS_WAIT4TITLE;
					}
				}
				else
				{
					int i = s.indexOf(str_zeile);
					
					if (i >= 0)
					{
						switch (status)
						{
						case STATUS_READKURS:
							{
								int i2 = s.indexOf(">",s.indexOf(">",i)+1);
								int i3 = s.indexOf("<",i2);
								
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
									catch (Exception e)
									{
										kurs = BenutzerAktie.VALUE_MISSING;
									}
								}
								
								status = STATUS_READVORTAG;
							}
							break;

						case STATUS_READVORTAG:
							{
								int i2 = s.indexOf(">",s.indexOf(">",i)+1);
								int i3 = s.indexOf("<",i2);

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
									catch (Exception e)
									{
										vortageskurs = BenutzerAktie.VALUE_NA;
									}
								}
								
								status = STATUS_READVOLUMEN;
							}
							break;

						case STATUS_READVOLUMEN:
							{
								int i2 = s.indexOf(">",s.indexOf(">",i)+1);
								int i3 = s.indexOf("<",i2);

								String volumen = s.substring(i2+1,i3).trim();

								try
								{
									handelsvolumen = Long.parseLong(volumen);
								}
								catch (Exception e)
								{
									handelsvolumen = 0L;
								}
								
								status = STATUS_READHOCHTIEF;
							}
							break;

						case STATUS_READHOCHTIEF:
							{
								int i2 = s.indexOf(">",s.indexOf(">",i)+1);
								int i3 = s.indexOf("<",i2);

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
									catch (Exception e)
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
									catch (Exception e)
									{
										tiefstkurs = BenutzerAktie.VALUE_NA;
									}
								}
								
								status = STATUS_READDATUM;
							}
							break;

						case STATUS_READDATUM:
							{
								int i2 = s.indexOf(">",s.indexOf(">",i)+1);
								int i3 = s.indexOf("<",i2);
								
								kursdatum = s.substring(i2+1,i3).trim();

								i2 = s.indexOf(">",i3);
								i3 = s.indexOf("<",i2);

								kursdatum = kursdatum + " " + s.substring(i2+1,i3).trim();
								
								status = STATUS_WAIT4ENDOFDATA;
							}
							break;
						}
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
