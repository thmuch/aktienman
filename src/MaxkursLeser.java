/**
 @author Thomas Much
 @version 1998-11-01
*/

import java.io.*;
import java.net.*;



public class MaxkursLeser extends Thread {

private AktieMaximalkurs parent;
private String request;
private int index;



public MaxkursLeser(AktieMaximalkurs parent, int index, String request) {
	super();
	
	this.parent = parent;
	this.index = index;
	this.request = request;
}


public void run() {
	try
	{
		URL url = new URL(URLs.COMDIRECT+request);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		int status = 0;
		long kurs = BenutzerAktie.VALUE_NA;
		
		while ((s = in.readLine()) != null)
		{
			if (status == 0)
			{
				if (s.indexOf("NAME=\"searchfor\" VALUE=") > 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA,"");
					break;
				}

				if (s.indexOf(".html?show=") > 0) {
					status = 1;
					kurs = BenutzerAktie.VALUE_NA;
				}
			}
			else
			{
				if (s.indexOf("</TR>") >= 0)
				{
					parent.setKurs(index,BenutzerAktie.VALUE_NA,"");
					status = 0;
				}
				else
				{
					int i = s.indexOf("<TD>");

					if (i >= 0)
					{
						if (status == 2)
						{
							int i2 = s.indexOf(">",i+4);
							int i3 = s.indexOf("<",i2);
							
							String kstr = s.substring(i2+1,i3);
							
							if (kstr.equalsIgnoreCase("n/a"))
							{
								parent.setKurs(index,BenutzerAktie.VALUE_NA,"");
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
							
							String datum = s.substring(i2+1,i3);

							i2 = s.indexOf(">",i3);
							i3 = s.indexOf("<",i2);

							datum = datum + " " + s.substring(i2+1,i3);
							
							parent.setKurs(index,kurs,datum);
							break;
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
		parent.setKurs(index,BenutzerAktie.VALUE_ERROR,"");
	}
}

}
