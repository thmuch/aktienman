/**
 @author Thomas Much
 @version 1999-06-21
*/

import java.io.*;
import java.net.*;



public class ComdirectMaxkursLeser extends Thread {

private static final int STATUS_WAIT4KURS    = 0;
private static final int STATUS_READKURS     = 1;
private static final int STATUS_WAIT4ZEIT    = 2;
private static final int STATUS_READZEIT     = 3;
private static final int STATUS_WAIT4VOLUMEN = 4;
private static final int STATUS_READVOLUMEN  = 5;
private static final int STATUS_FINISHED     = 6;

private AktieMaximalkurs parent;
private String request;
private int index,nextID;



public ComdirectMaxkursLeser(AktieMaximalkurs parent, int index, String request, int nextID) {
	super();
	
	this.parent = parent;
	this.index = index;
	this.request = request;
	this.nextID = nextID;
}


public void run() {
	BufferedReader in = null;
	
	AktienMan.checkURLs();
	
	try
	{
		int sp = request.indexOf(".");

		String spwkn    = request.substring(0,sp);
		String spboerse = request.substring(sp+1);

		URL url = new URL(AktienMan.url.getComdirectKursURL(spwkn,spboerse));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String str_fehler  = AktienMan.url.getString(URLs.STR_CD_KURSFEHLER);
		String str_aktkurs = AktienMan.url.getString(URLs.STR_CD_KURS);
		String str_zeit    = AktienMan.url.getString(URLs.STR_CD_KURSZEIT);
		String str_volumen = AktienMan.url.getString(URLs.STR_CD_KURSVOLUMEN);
		String str_ende    = AktienMan.url.getString(URLs.STR_CD_KURSENDE);
		
		String s, datum = "";
		int status = STATUS_WAIT4KURS;
		long kurs = BenutzerAktie.VALUE_NA;
		long handelsvolumen = 0L;
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
			
			switch (status)
			{
			case STATUS_WAIT4KURS:
				if (s.indexOf(str_fehler) >= 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA);
					status = STATUS_FINISHED;
					found = true;
				}
				else if (s.indexOf(str_aktkurs) >= 0)
				{
					status = STATUS_READKURS;
				}
				break;
			
			case STATUS_READKURS:
				{
					int i = s.indexOf(">");
					int i2 = s.indexOf("<",i+1);
					
					if ((i > 0) && (i2 > i))
					{
						String kstr = s.substring(i+1,i2).trim();
						
						if ((i = kstr.indexOf(" ")) > 0)
						{
							kstr = kstr.substring(0,i);
						}

						if ((i = kstr.indexOf("&")) > 0)
						{
							kstr = kstr.substring(0,i);
						}
						
						if (kstr.equalsIgnoreCase(ComdirectQuelle.VALUENA))
						{
							parent.setKurs(index,BenutzerAktie.VALUE_NA);
							status = STATUS_FINISHED;
							found = true;
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
							
							status = STATUS_WAIT4ZEIT;
							valid = true;
						}
					}
					else
					{
						status = STATUS_FINISHED;
					}
				}
				break;
			
			case STATUS_WAIT4ZEIT:
				if (s.indexOf(str_zeit) >= 0)
				{
					status = STATUS_READZEIT;
				}
				break;
			
			case STATUS_READZEIT:
				{
					int i = s.indexOf(">");
					int i2 = s.indexOf("<",i+1);
					
					if ((i > 0) && (i2 > i))
					{
						datum = s.substring(i+1,i2).trim();
						
						i = datum.indexOf(",");
						
						if (i >= 0)
						{
							datum = datum.substring(0,i) + datum.substring(i+1);
						}
					}
					
					status = STATUS_WAIT4VOLUMEN;
				}
				break;
			
			case STATUS_WAIT4VOLUMEN:
				if (s.indexOf(str_volumen) >= 0)
				{
					status = STATUS_READVOLUMEN;
				}
				break;

			case STATUS_READVOLUMEN:
				{
					int i = s.indexOf(">");
					int i2 = s.indexOf("<",i+1);

					if ((i > 0) && (i2 > i))
					{
						String volumen = s.substring(i+1,i2).trim();
						
						i = volumen.indexOf(".");
						
						while (i >= 0)
						{
							volumen = volumen.substring(0,i) + volumen.substring(i+1);
							i = volumen.indexOf(".");
						}

						try
						{
							handelsvolumen = Long.parseLong(volumen);
						}
						catch (NumberFormatException e) {}
					}
					
					status = STATUS_FINISHED;
				}
				break;
			}
		}
		
		if (valid)
		{
			parent.setKurs(index,kurs,datum,Waehrungen.getOnlineWaehrung(),handelsvolumen);
			found = true;
		}
		
		if (!found)
		{
			parent.setKurs(index,BenutzerAktie.VALUE_ERROR,nextID);
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-URL fehlerhaft.");
		parent.setKurs(index,BenutzerAktie.VALUE_ERROR,nextID);
	}
	catch (IOException e)
	{
		parent.setKurs(index,BenutzerAktie.VALUE_ERROR,nextID);
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
