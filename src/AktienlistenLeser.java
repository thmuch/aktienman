/**
 @author Thomas Much
 @version 1998-10-27
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;
import java.util.*;



public class AktienlistenLeser extends Thread {

private static final int STATUS_EMPTY    = 0;
private static final int STATUS_STARTING = 1;
private static final int STATUS_READING  = 2;

private String request,listenname,ignore;
private int index;
private AktienAktualisieren aadialog;
private boolean mdax;
private Aktienliste aktienliste;



public AktienlistenLeser(String listenname, String request, String ignore,
							AktienAktualisieren aadialog, int index, boolean mdax) {
	super();
	this.listenname = listenname;
	this.request = request;
	this.ignore = ignore.toUpperCase();
	this.aadialog = aadialog;
	this.index = index;
	this.mdax = mdax;
}


public void run() {
	try
	{
		URL url = new URL(URLs.POPUPLISTEN+request);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s;
		int status = STATUS_EMPTY;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				if (status == STATUS_EMPTY)
				{
					if (s.indexOf("<PRE>") >= 0)
					{
						status = ((ignore.length() > 0) ? STATUS_STARTING : STATUS_READING);
						aktienliste = new Aktienliste();
					}
				}
				else if (s.indexOf("</PRE>") >= 0)
				{
					aktienliste.updateChoice();
					
					switch (index)
					{
					case 0:
						AktienMan.listeDAX = aktienliste;
						break;
					
					case 1:
						AktienMan.listeMDAX = aktienliste;
						break;
					
					case 2:
						AktienMan.listeNMarkt = aktienliste;
						break;
					
					case 3:
						AktienMan.listeEuroSTOXX = aktienliste;
						break;
					
					case 4:
						AktienMan.listeAusland = aktienliste;
						break;
					}

					aadialog.finishCounting(index);
					
					if (mdax)
					{
						new AktienlistenLeser("MDAX",URLs.LISTE_DAX100,"DAX 100",aadialog,1,false).start();
					}

					break;
				}
				else
				{
					if (status == STATUS_STARTING)
					{
						if (s.toUpperCase().indexOf(ignore) < 0) status = STATUS_READING;
					}
					
					if (status == STATUS_READING)
					{
						StringTokenizer st = new StringTokenizer(s,";");
						
						String name   = st.nextToken();
						String wknstr = st.nextToken();

						try
						{
							int wkn = Integer.parseInt(wknstr);
							
							if (index == 1)
							{
								if (AktienMan.listeDAX.isMember(wkn)) continue;
							}
							
							aktienliste.add(new Aktie(name,"",wkn));
						
							aadialog.incCount(index);
						}
						catch (NumberFormatException e) {}
					}
				}
			}
		}
		
		in.close();
	}
	catch (MalformedURLException e)
	{
		System.out.println("Aktienlisten-URL fehlerhaft.");
	}
	catch (IOException e) {}
}


}
