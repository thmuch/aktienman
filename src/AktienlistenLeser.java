/**
 @author Thomas Much
 @version 1999-05-06
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;
import java.util.*;



public final class AktienlistenLeser extends Thread {

private static final int STATUS_WAIT4TABLE = 0;
private static final int STATUS_WAIT4TR    = 1;
private static final int STATUS_WAIT4TD    = 2;
private static final int STATUS_WAIT4WKN   = 3;

private String request,listenname,ignore;
private int index;
private AktienAktualisieren aadialog;
private Aktienliste aktienliste;



public AktienlistenLeser(String listenname, String request, String ignore,
							AktienAktualisieren aadialog, int index) {
	super();
	this.listenname = listenname;
	this.request = request;
	this.ignore = ignore.toUpperCase();
	this.aadialog = aadialog;
	this.index = index;
}


public void run() {
	BufferedReader in = null;

	try
	{
		URL url = new URL(URLs.POPUPLISTEN+request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s, name = "";
		int status = STATUS_WAIT4TABLE;
		int trcount = 0;
		boolean skip = false;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				if (status == STATUS_WAIT4TABLE)
				{
					if (s.indexOf("<TABLE") >= 0)
					{
						status = STATUS_WAIT4TR;
						trcount = 0;
						
						aktienliste = new Aktienliste();
					}
				}
				else if (s.indexOf("</TABLE>") >= 0)
				{
					aktienliste.updateChoice();
					
					switch (index)
					{
					case AktienAktualisieren.INDEX_DAX:
						AktienMan.listeDAX = aktienliste;
						break;
					
					case AktienAktualisieren.INDEX_MDAX:
						AktienMan.listeMDAX = aktienliste;
						break;
					
					case AktienAktualisieren.INDEX_NMARKT:
						AktienMan.listeNMarkt = aktienliste;
						break;
					
					case AktienAktualisieren.INDEX_EUROSTOXX:
						AktienMan.listeEuroSTOXX = aktienliste;
						break;
					
					case AktienAktualisieren.INDEX_AUSLAND:
						AktienMan.listeAusland = aktienliste;
						break;
					}

					aadialog.finishCounting(index);
					
					if (index == AktienAktualisieren.INDEX_DAX)
					{
						new AktienlistenLeser("MDAX",URLs.LISTE_DAX100,"DAX 100",aadialog,AktienAktualisieren.INDEX_MDAX).start();
					}

					break;
				}
				else
				{
					if (status == STATUS_WAIT4TR)
					{
						if (s.indexOf("<TR>") >= 0)
						{
							if (++trcount > 1)
							{
								status = STATUS_WAIT4TD;

								skip = (ignore.length() > 0);
							}
						}
					}
					else if (status == STATUS_WAIT4TD)
					{
						if (s.indexOf("<TD") >= 0)
						{
							int i = s.indexOf(">",s.indexOf(">") + 1) + 1;
							
							name = s.substring(i,s.indexOf("<",i));
							
							if (skip)
							{
								if (name.toUpperCase().indexOf(ignore) < 0)
								{
									skip = false;
								}
								else
								{
									status = STATUS_WAIT4TR;
									continue;
								}
							}

							status = STATUS_WAIT4WKN;
						}
					}
					else if (status == STATUS_WAIT4WKN)
					{
						if (s.indexOf("<TD") >= 0)
						{
							int i = s.indexOf(">",s.indexOf(">") + 1) + 1;
							
							String wknstr = s.substring(i,s.indexOf("<",i));
							
							try
							{
								int wkn = Integer.parseInt(wknstr);
								
								if (index == AktienAktualisieren.INDEX_MDAX)
								{
									if (AktienMan.listeDAX.isMember(wkn))
									{
										status = STATUS_WAIT4TR;
										continue;
									}
								}
								
								aktienliste.add(new Aktie(name,"",wkn));
							
								aadialog.incCount(index);
							}
							catch (NumberFormatException e) {}
							
							status = STATUS_WAIT4TR;
						}
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("Aktienlisten-URL fehlerhaft.");
		aadialog.setError(index);
	}
	catch (NullPointerException e)
	{
		aadialog.setError(index);
	}
	catch (IOException e)
	{
		aadialog.setError(index);
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
