/**
 @author Thomas Much
 @version 1999-05-04
*/

import java.io.*;
import java.net.*;



public class ComdirectMaxkursLeser extends Thread {

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
	
	try
	{
		URL url = new URL(URLs.KURSE_COMDIRECT+request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s, datum = "";
		int status = 0;
		long kurs = BenutzerAktie.VALUE_NA;
		boolean found = false;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				if (s.indexOf("NAME=\"searchfor\" VALUE=") > 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA);
					found = true;
					break;
				}

				if (s.indexOf(".html?") > 0) {
					status = 1;
					kurs = BenutzerAktie.VALUE_NA;
				}
			}
			else
			{
				if (s.indexOf("</TR>") >= 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA);
					found = true;
					status = 0;
				}
				else
				{
					int i = s.indexOf("<TD");

					if (i >= 0)
					{
						if (status == 2)
						{
							int i2 = s.indexOf(">",i+4);
							int i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3);
							
							if (kstr.equalsIgnoreCase(ComdirectQuelle.VALUENA))
							{
								parent.setKurs(index,BenutzerAktie.VALUE_NA);
								found = true;
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
							int i2 = s.indexOf(">",i+4);
							int i3 = s.indexOf("<",i2);
							
							datum = s.substring(i2+1,i3);

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							datum = datum + " " + s.substring(i2+1,i3);
						}
						else if (status == 6)
						{
							int i2 = s.indexOf(">",s.indexOf(">",i)+1);
							int i3 = s.indexOf("<",i2);

							String volumen = s.substring(i2+1,i3).trim();
							
							long handelsvolumen;
							try
							{
								handelsvolumen = Long.parseLong(volumen);
							}
							catch (NumberFormatException e)
							{
								handelsvolumen = 0L;
							}
							
							int kurswaehrung = Waehrungen.getOnlineWaehrung();
							
							parent.setKurs(index,kurs,datum,kurswaehrung,handelsvolumen);
							found = true;
							break;
						}

						status++;
					}
				}
			}
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
