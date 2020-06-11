/**
 @author Thomas Much
 @version 1998-11-02
*/

import java.io.*;
import java.net.*;



public class ComdirectLeser extends Thread {

private String request;



public ComdirectLeser(String request) {
	super();
	this.request = request;
}


public void run() {
	if (!(new ADate().before(new ADate(1998,12,1)))) return;

	try
	{
		URL url = new URL(URLs.COMDIRECT+request);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		String name = null, platz = null, wkn = null, kursdatum = "", kurz = "";
		long kurs = 0L;
		int i,i2,i3, status = 0;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				i = s.indexOf("NAME=\"searchfor\" VALUE=");
				
				if (i > 0)
				{
					i2 = s.indexOf('"',i+20);
					i3 = s.indexOf('.',i2);
					
					wkn = s.substring(i2+1,i3);
					
					i2 = s.indexOf('"',i3);
					
					platz = s.substring(i3+1,i2);

					AktienMan.hauptdialog.listeAnfrageFalsch(wkn,platz);
					break;
				}

				i = s.indexOf(".html?show=");
				
				if (i > 0)
				{
					i2 = s.indexOf('=',i);
					i3 = s.indexOf('.',i2);

					kurz = s.substring(i2+1,i3);

					i2 = s.indexOf('>',i3);
					i3 = s.indexOf('<',i2);

					name = s.substring(i2+1,i3);

					platz = null;
					wkn = null;
					kurs = 0L;
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
							AktienMan.hauptdialog.listeNeuerAktienkurs(wkn,kurz,platz,name,kurs,kursdatum,Waehrungen.getOnlineWaehrung());
						}
						else
						{
							AktienMan.hauptdialog.listeAktienkursNA(wkn,kurz,platz,name);
						}
					}
					
					status = 0;
				}
				else
				{
					i = s.indexOf("<TD>");

					if (i >= 0)
					{
						if (status == 1)
						{
							i2 = s.indexOf(">",i+4);
							i3 = s.indexOf("<",i2);
							
							wkn = s.substring(i2+1,i3);

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);
							
							platz = s.substring(i2+1,i3);
						}
						else if (status == 2)
						{
							i2 = s.indexOf(">",i+4);
							i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3);
							
							if (kstr.equalsIgnoreCase("n/a"))
							{
								AktienMan.hauptdialog.listeAktienkursNA(wkn,kurz,platz,name);
								
								status = 0;
								continue;
							}
							
							try
							{
								kurs = Waehrungen.doubleToLong(kstr);
							}
							catch (NumberFormatException e)
							{
								kurs = 0L;
							}
						}
						else if (status == 3)
						{
							i2 = s.indexOf(">",i+4);
							i3 = s.indexOf("<",i2);
							
							kursdatum = s.substring(i2+1,i3);

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							kursdatum = kursdatum + " " + s.substring(i2+1,i3);
						}

						status++;
					}
				}
			}
		}
		
		in.close();
	}
	catch (MalformedURLException e)
	{
		System.out.println("Comdirect-URL fehlerhaft.");
	}
	catch (IOException e)
	{
		System.out.println("Fehler beim Einlesen der Comdirect-Daten.");
	}
}

}
