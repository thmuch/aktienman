/**
 @author Thomas Much
 @version 2001-10-30
*/

import java.net.*;
import java.io.*;




public final class BBBankIndexLeser extends Thread {

private static final long TIMEOUT = 55000L;

private static final int STATUS_FINISHED   = -1;
private static final int STATUS_WAIT4DATA  =  0;
private static final int STATUS_READSYMBOL =  1;
private static final int STATUS_COUNTLINES =  2;
private static final int STATUS_PUNKTE     =  3;
private static final int STATUS_ZEIT       =  4;
private static final int STATUS_VORTAG     = 10;


private long tID;
private int  type;




public BBBankIndexLeser(long tID, int type) {

	this.tID  = tID;
	this.type = type;
}



private String fixWert(String val) {

	int i;
	
	while (((i = val.indexOf(".")) > 0) && (i < val.indexOf(",")))
	{
		val = val.substring(0,i) + val.substring(i+1);
	}
	
	return val.trim();
}



public void run() {

	AktienMan.checkURLs();

	try
	{
		BufferedReader in;
		
		do
		{
			in = null;
			
			try
			{
				Connections.getConnection();

				URL url = new URL(AktienMan.url.get(type));
				
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				String str_data  = AktienMan.url.getString(URLs.STR_BBB_INDEXSET);
				String str_title = AktienMan.url.getString(URLs.STR_BBB_INDEXTITLE);
				String str_ende  = AktienMan.url.getString(URLs.STR_BBB_INDEXENDE);

				String s, symbol = "", datum = "";
				long punkte = 0L, vortag = 0L;

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
							status = STATUS_READSYMBOL;
						}
					}
					else if (status == STATUS_READSYMBOL)
					{
						int i = s.indexOf(str_title);

						if (i >= 0)
						{

							int i2 = s.indexOf("=",i);
							int i3 = s.indexOf("&",i2);
							
							symbol = s.substring(i2+1,i3).trim();

							status = STATUS_COUNTLINES;
						}
					}
					else if (status >= STATUS_COUNTLINES)
					{
						status++;
						
						switch (status)
						{
						case STATUS_PUNKTE:
							{
								int i = s.indexOf(">");
								int i2 = s.indexOf("<",i);

								try
								{
									punkte = Waehrungen.doubleToLong(fixWert(s.substring(i+1,i2).trim()));
								}
								catch (Exception e)
								{
									punkte = BenutzerAktie.VALUE_NA;
								}
							}

 							break;
							
						case STATUS_ZEIT:
							{
								int i = s.indexOf(">");
								int i2 = s.indexOf("<",i);
								
								datum = s.substring(i+1,i2).trim();
							}

							break;
							
						case STATUS_VORTAG:
							{
								int i = s.indexOf(">");
								int i2 = s.indexOf("<",i);

								try
								{
									vortag = Waehrungen.doubleToLong(fixWert(s.substring(i+1,i2).trim()));
								}
								catch (Exception e)
								{
									vortag = BenutzerAktie.VALUE_NA;
								}
							}

							IndexQuelle.checkIndex(symbol,punkte,vortag,datum);

							status = STATUS_READSYMBOL;
							break;
						}
					}
					
				}
				
				IndexQuelle.updateFinished();
			}
			catch (MalformedURLException e)
			{
				System.out.println("BBBank-Index-URL fehlerhaft.");
			}
			catch (Exception e) {}
			finally
			{
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (Exception e) {}
					finally
					{
						in = null;
					}
				}
				
				Connections.releaseConnection();
			}
			
			if (IndexQuelle.autoIndexOn())
			{
				try
				{
					sleep(TIMEOUT);
				}
				catch (Exception e) {}
			}

		} while (IndexQuelle.autoIndexOn());
	}
	catch (Exception e) {}
	finally
	{
		IndexQuelle.clearThread(tID);
	}
}

}
