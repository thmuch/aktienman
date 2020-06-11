/**
 @author Thomas Much
 @version 1999-01-29
*/

import java.io.*;
import java.net.*;



public class DeutscheBankMaxkursLeser extends Thread {

private AktieMaximalkurs parent;
private String request;
private int index,nextID;



public DeutscheBankMaxkursLeser(AktieMaximalkurs parent, int index, String request, int nextID) {
	super();
	
	this.parent = parent;
	this.index = index;
	this.request = request;
	this.nextID = nextID;
}


public void run() {
	BufferedReader in = null;

	try
	{
		URL url = new URL(URLs.KURSE_DEUTSCHEBANK+request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		long kurs = BenutzerAktie.VALUE_NA;
		long handelsvolumen = 0L;
		int status = 0;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				if (s.indexOf(" keine Ergebnisse ") > 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA);
					break;
				}

				if (s.indexOf(".html?show=") > 0)
				{
					status = 1;
					kurs = BenutzerAktie.VALUE_NA;
				}
			}
			else
			{
				if (s.indexOf("</TR>") >= 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA);
					status = 0;
				}
				else
				{
					int i = s.indexOf("<TD");

					if (i >= 0)
					{
						if (status == 1)
						{
							int i2 = s.indexOf(">",s.indexOf(">",i)+1);
							int i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3).trim();
							
							if (kstr.equalsIgnoreCase(DeutscheBankQuelle.VALUENA))
							{
								parent.setKurs(index,BenutzerAktie.VALUE_NA);
								break;
							}

							try
							{
								kurs = Waehrungen.doubleToLong(kstr);
							}
							catch (NumberFormatException e)
							{
								kurs = BenutzerAktie.VALUE_NA;
							}
						}
						else if (status == 3)
						{
							int i2 = s.indexOf(">",s.indexOf(">",i)+1);
							int i3 = s.indexOf("<",i2);

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
						else if (status == 5)
						{
							int i2 = s.indexOf(">",s.indexOf(">",i)+1);
							int i3 = s.indexOf("<",i2);
							
							String kursdatum = s.substring(i2+1,i3).trim();

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							kursdatum = kursdatum + " " + s.substring(i2+1,i3).trim();
							
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							parent.setKurs(index,kurs,kursdatum,kurswaehrung,handelsvolumen);
							break;
						}

						status++;
					}
				}
			}
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("DeutscheBank-URL fehlerhaft.");
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
