/**
 @author Thomas Much
 @version 1999-06-19
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;
import java.util.*;




public final class AktienlistenLeser extends Thread {

private String listenname,ignore;
private int index,request;
private AktienAktualisieren aadialog;




public AktienlistenLeser(String listenname, int request, String ignore,
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
		Aktienliste aktienliste = new Aktienliste();

		URL url = new URL(AktienMan.url.get(request));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s, name = "";

		boolean skip = (ignore.length() > 0);
		boolean readWKN = false;
		boolean valid = false;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				if (readWKN)
				{
					int von = s.indexOf(">", s.indexOf(">") + 1);
					
					if (von > 0)
					{
						int bis = s.indexOf("<", von + 1);
						
						String wknstr = s.substring(von+1,bis).trim();

						try
						{
							int wkn = Integer.parseInt(wknstr);
							
							if (index == AktienAktualisieren.INDEX_MDAX)
							{
								if (AktienMan.listeDAX.isMember(wkn))
								{
									readWKN = false;
									continue;
								}
							}
							
							aktienliste.add(new Aktie(name,"",wkn));
						
							aadialog.incCount(index);
							
							valid = true;
						}
						catch (NumberFormatException e) {}
					}

					readWKN = false;
				}
				else
				{
					int i = s.indexOf("KA_Charts.htm?");
					
					if (i > 0)
					{
						int von = s.indexOf(">", s.indexOf(">",i) + 1);

						int bis = s.indexOf("<", von + 1);

						name = s.substring(von+1,bis).trim();
						
						if (skip)
						{
							if (name.toUpperCase().indexOf(ignore) < 0)
							{
								skip = false;
							}
							else
							{
								continue;
							}
						}
						
						readWKN = true;
					}
				}
			}
		}
		
		if (valid)
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
				new AktienlistenLeser("MDAX",URLs.URL_DAX100,"DAX 100",aadialog,AktienAktualisieren.INDEX_MDAX).start();
			}
		}
		else
		{
			aadialog.setError(index);
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
