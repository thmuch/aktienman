/**
 @author Thomas Much
 @version 2000-09-18
*/

import java.io.*;
import java.net.*;
import java.util.*;




public class LSRTDAX30Leser extends Thread {

private static final int NEXT_ID = KursQuellen.QUELLE_COMDIRECT;

private static final int STATUS_WAIT4PRE    = 0;
private static final int STATUS_WAIT4KURS   = 1;
private static final int STATUS_WAIT4JSKURS = 2;
private static final int STATUS_FINISHED    = 3;

private KursReceiver receiver;
private boolean sofortZeichnen;
private int type;
private Vector requests;




public LSRTDAX30Leser(KursReceiver receiver, int type, Vector requests) {

	this(receiver,type,false,requests);
}



public LSRTDAX30Leser(KursReceiver receiver, int type, boolean sofortZeichnen, Vector requests) {

	super();

	this.receiver = receiver;
	this.type = type;
	this.sofortZeichnen = sofortZeichnen;
	this.requests = requests;
}



public void run() {

	AktienMan.checkURLs();

	if (!AktienMan.hauptdialog.mainr()) return;

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

		URL url = new URL(AktienMan.url.get(URLs.URL_LSDAX30REALTIME));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String str_anfang   = AktienMan.url.getString(URLs.STR_LSDAX_KURSANFANG);
		String str_jsanfang = AktienMan.url.getString(URLs.STR_LSDAX_JSKANFANG);
		
		int num_namelen   = AktienMan.url.getNumber(URLs.NUM_LSDAX_NAMELEN);
		int kurswaehrung  = Waehrungen.getOnlineWaehrung();

		String s, str_ende = "", str_valid = "";

		int status = STATUS_WAIT4PRE;

		while (((s = in.readLine()) != null) && (status != STATUS_FINISHED))
		{
			s = s.trim();
		
			if (s.length() == 0) continue;

			if (status == STATUS_WAIT4PRE)
			{
				if (s.indexOf(str_anfang) >= 0)
				{
					status = STATUS_WAIT4KURS;
					
					str_ende  = AktienMan.url.getString(URLs.STR_LSDAX_KURSENDE);
					str_valid = AktienMan.url.getString(URLs.STR_LSDAX_KURSVALID);
				}
				else if (s.indexOf(str_jsanfang) >= 0)
				{
					status = STATUS_WAIT4JSKURS;

					str_ende  = AktienMan.url.getString(URLs.STR_LSDAX_JSKENDE);
					str_valid = AktienMan.url.getString(URLs.STR_LSDAX_JSKVALID);
				}
			}
			else if (s.indexOf(str_ende) >= 0)
			{
				status = STATUS_FINISHED;
			}
			else if (s.indexOf(str_valid) >= 0)
			{
				if (status == STATUS_WAIT4JSKURS)
				{
					parseJSKurs(s,kurswaehrung);
				}
				else
				{
					parseHTMLKurs(s,num_namelen,kurswaehrung);
				}
			}
		}
		
		sendErrorRequests();
	}
	catch (MalformedURLException e)
	{
		System.out.println("LSRTDAX30-URL fehlerhaft.");
		sendErrorRequests();
	}
	catch (Exception e)
	{
		sendErrorRequests();
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
	
	requests = null;
}



private void parseHTMLKurs(String s, int num_namelen, int kurswaehrung) {

	int i  = s.indexOf(">");
	int i2 = s.indexOf("<",i+1);
	
	String wkn = s.substring(i+1,i2).trim();
	
	i = s.indexOf(">",i2+1);
	
	String name = s.substring(i+1,i+num_namelen).trim();
	
	s = s.substring(i+num_namelen).trim();
	
	i = s.indexOf(" ");
	
	String strbid = s.substring(0,i);
	
	s = s.substring(i).trim();

	i = s.indexOf(" ");
	
	String strask = s.substring(0,i);
	
	s = s.substring(i).trim();
	s = s.substring(s.indexOf(" ")).trim(); // Währung entfernen
	
	i = s.indexOf(" ");

	String date = s.substring(0,i-2);

	String time = s.substring(i).trim();
	time = time.substring(0,time.length() - 3);
	
	long kurs = BenutzerAktie.VALUE_MISSING;

	try
	{
		kurs = Waehrungen.doubleToLong((type == LSRTDAX30Quelle.TYPE_BID) ? strbid : strask);
	}
	catch (Exception e)
	{
		kurs = BenutzerAktie.VALUE_MISSING;
	}
	
	/* gefundenen Kurs an _allen_ Börsenplätzen aktualisieren */

	for (int q = 0; q < AktienMan.boersenliste.size(); q++)
	{
		String platz = AktienMan.boersenliste.getAt(q).getKurz();
		
		receiver.listeNeuerAktienkurs(wkn,"",platz,name,kurs,date+" "+time,
										BenutzerAktie.VALUE_NA,BenutzerAktie.VALUE_NA,
										BenutzerAktie.VALUE_NA,BenutzerAktie.VALUE_NA,
										0,kurswaehrung,sofortZeichnen);
	}
	
	/* gefundenen Kurs aus requests-Vector entfernen */

	int l = 0;
	
	while (l < requests.size())
	{
		String all = (String)requests.elementAt(l);
		
		int l2 = all.indexOf(":");
		int l3 = all.indexOf(":",l2+1);
		
		if (wkn.equals(all.substring(l2+1,l3)))
		{
			requests.removeElementAt(l);
		}
		else
		{
			l++;
		}
	}
}



private void parseJSKurs(String s, int kurswaehrung) {

	ADate jetzt = new ADate();

	String date = (jetzt.getDay() < 10 ? "0" : "") + jetzt.getDay() + "." + (jetzt.getMonth() < 10 ? "0" : "") + jetzt.getMonth() + ".";

	int i = s.indexOf("(");
	
	while (i > 0)
	{
		int i2 = s.indexOf(",",i+1);

		String wkn = s.substring(i+1,i2).trim();
		
		i  = s.indexOf("\"",i2+1);
		i2 = s.indexOf("\"",i+1);
	
		String name = s.substring(i+1,i2).trim();

		i  = s.indexOf("\"",i2+1);
		i2 = s.indexOf("\"",i+1);

		String strbid = s.substring(i+1,i2).trim();

		i  = s.indexOf("\"",i2+1);
		i2 = s.indexOf("\"",i+1);

		String strask = s.substring(i+1,i2).trim();

		i = s.indexOf(",",i2+1); // ein paar Sachen überspringen...
		i = s.indexOf(",",i+1);
		i = s.indexOf(",",i+1);
		i = s.indexOf(",",i+1);

		i = s.indexOf("\"",i+1);
		i2 = s.indexOf("\"",i+1);

		String time = s.substring(i+1,i2).trim();
		time = time.substring(0,time.length() - 3);

		long kurs = BenutzerAktie.VALUE_MISSING;

		try
		{
			kurs = Waehrungen.doubleToLong((type == LSRTDAX30Quelle.TYPE_BID) ? strbid : strask);
		}
		catch (Exception e)
		{
			kurs = BenutzerAktie.VALUE_MISSING;
		}

		/* gefundenen Kurs an _allen_ Börsenplätzen aktualisieren */

		for (int q = 0; q < AktienMan.boersenliste.size(); q++)
		{
			String platz = AktienMan.boersenliste.getAt(q).getKurz();
			
			receiver.listeNeuerAktienkurs(wkn,"",platz,name,kurs,date+" "+time,
											BenutzerAktie.VALUE_NA,BenutzerAktie.VALUE_NA,
											BenutzerAktie.VALUE_NA,BenutzerAktie.VALUE_NA,
											0,kurswaehrung,sofortZeichnen);
		}
		
		/* gefundenen Kurs aus requests-Vector entfernen */

		int l = 0;
		
		while (l < requests.size())
		{
			String all = (String)requests.elementAt(l);
			
			int l2 = all.indexOf(":");
			int l3 = all.indexOf(":",l2+1);
			
			if (wkn.equals(all.substring(l2+1,l3)))
			{
				requests.removeElementAt(l);
			}
			else
			{
				l++;
			}
		}
		
		i = s.indexOf("(",i2);
	}
}



private void sendErrorRequests() {

	for (int i = 0; i < requests.size(); i++)
	{
		String all = (String)requests.elementAt(i);
		
		int i2 = all.indexOf(":");
		int i3 = all.indexOf(":",i2+1);
		
		String request = all.substring(0,i2);
		String wkn     = all.substring(i2+1,i3);
		String boerse  = all.substring(i3+1);

		receiver.listeAnfrageFehler(request,wkn,boerse,sofortZeichnen,NEXT_ID);
	}
}

}
