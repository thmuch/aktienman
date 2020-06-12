/**
 @author Thomas Much
 @version 2000-11-10
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

private String ignore;
private int index,request;
private AktienAktualisieren aadialog;




public AktienlistenLeser(int request, String ignore, AktienAktualisieren aadialog, int index) {

	this.request = request;
	this.ignore = ignore.toUpperCase();
	this.aadialog = aadialog;
	this.index = index;
}



public void run() {

	BufferedReader in = null;

	try
	{
		Connections.getConnection();

		Aktienliste aktienliste = new Aktienliste();

		URL url = new URL(AktienMan.url.get(request));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));

		String str_data  = AktienMan.url.getString(URLs.STR_BBB_INDEXSET);
		String str_title = AktienMan.url.getString(URLs.STR_BBB_INDEXTITLE);
		String str_ende  = AktienMan.url.getString(URLs.STR_BBB_INDEXENDE);

		String s, name = "";

		boolean skip = (ignore.length() > 0);
		boolean readWKN = false;
		boolean valid = false;
		
		int status = STATUS_WAIT4DATA;
		
		while (((s = in.readLine()) != null) && (status != STATUS_FINISHED))
		{
			if (s.indexOf(str_ende) >= 0)
			{
				status = STATUS_FINISHED;
			}
			else if (status == STATUS_WAIT4DATA)
			{
				if (s.indexOf(str_data) >= 0)
				{
					status = STATUS_READNAME;
				}
			}
			else if (status == STATUS_READNAME)
			{
				int i = s.indexOf(str_title);

				if (i >= 0)
				{
					int i2 = s.indexOf(">",i);
					int i3 = s.indexOf("<",i2);
					
					name = s.substring(i2+1,i3).trim();
					
					if ((!skip) || (name.toUpperCase().indexOf(ignore) < 0))
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
				AktienMan.listeDAX = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_MDAX:
				AktienMan.listeMDAX = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_NEMAX50:
				AktienMan.listeNMarkt = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_EUROSTOXX50:
				AktienMan.listeEuroSTOXX = aktienliste;
				break;
			
			case AktienAktualisieren.INDEX_STOXX50:
				AktienMan.listeAusland = aktienliste;
				break;
			}

			aadialog.finishCounting(index);
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

		Connections.releaseConnection();
	}
}

}
