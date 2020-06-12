/**
 @author Thomas Much
 @version 2003-04-03
*/

import java.net.*;
import java.awt.*;
import java.io.*;
import java.awt.*;
import java.util.*;




public final class AktienlistenLeser extends Thread {

private static final int STATUS_FINISHED  = -1;
private static final int STATUS_WAIT4DATA =  0;
private static final int STATUS_READNAME  =  1;
private static final int STATUS_READWKN   =  2;

private static final String[] liste  = { "dax30", "mdax", "TecDAX-etr", "eurostoxx50", "stoxx50" };
private static final String[] ignore = { "DAX",   "MDAX", "TecDAX",     "STOXX",       "STOXX"   };

private int index;
private AktienAktualisieren aadialog;




public AktienlistenLeser(int index, AktienAktualisieren aadialog) {

	this.index = index;
	this.aadialog = aadialog;
}



public void run() {

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

		Aktienliste aktienliste = new Aktienliste();
		
		String request = "http://bbbank.teledata.de/bbbank/kursliste.html?sKl=" + liste[index] + "&sType=default&bNoIdx=1&kunde=";
		
		System.out.println("AKTIENLISTE: " + request); // TODO

		URL url = new URL(request);
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String s, name = "";

		boolean skip = (ignore[index].length() > 0);
		boolean readWKN = false;
		boolean valid = false;
		
		int status = STATUS_WAIT4DATA;
		
		while (((s = in.readLine()) != null) && (status != STATUS_FINISHED))
		{
			if (s.indexOf("Bitte beachten") >= 0)
			{
				status = STATUS_FINISHED;
			}
			else if (status == STATUS_WAIT4DATA)
			{
				if (s.indexOf("<b>Name</b>") >= 0)
				{
					status = STATUS_READNAME;
				}
			}
			else if (status == STATUS_READNAME)
			{
				int i = s.indexOf("chart.html?symm=");

				if (i >= 0)
				{
					int i2 = s.indexOf(">",i);
					int i3 = s.indexOf("<",i2);
					
					name = s.substring(i2+1,i3).trim();
					
					if ((!skip) || (name.indexOf(ignore[index]) < 0))
					{
						status = STATUS_READWKN;
					}
				}
			}
			else if (status == STATUS_READWKN)
			{
				int i = s.indexOf(">");
				int i2 = s.indexOf("<",i);
				
				String wknstr = s.substring(i+1,i2).trim();

				try
				{
					int wkn = Integer.parseInt(wknstr);
					
					aktienliste.add(new Aktie(name,"",wkn));
				
					aadialog.incCount(index);
					
					valid = true;
				}
				catch (Exception e) {}
				
				status = STATUS_READNAME;
			}

		}
		
		if (valid)
		{
			aktienliste.updateChoice();
			
			switch (index)
			{
			case AktienAktualisieren.INDEX_DAX30:
				AktienMan.listeDAX30 = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_MDAX:
				AktienMan.listeMDAX = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_TECDAX:
				AktienMan.listeTecDAX = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_EUROSTOXX50:
				AktienMan.listeEuroSTOXX50 = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_STOXX50:
				AktienMan.listeSTOXX50 = aktienliste;
				break;
			}

			aadialog.finishCounting(index);
		}
		else
		{
			aadialog.setError(index);
		}
	}
	catch (Exception e)
	{
		aadialog.setError(index);

		AktienMan.errlog("Fehler im Aktienlistenleser", e);
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
		}

		Connections.releaseConnection();

		System.out.println("Aktienliste " + liste[index] + " fertig."); // TODO
	}
}

}
