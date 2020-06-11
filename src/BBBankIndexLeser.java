/**
 @author Thomas Much
 @version 1999-06-30
*/

import java.net.*;
import java.io.*;




public final class BBBankIndexLeser extends Thread {

private static final long TIMEOUT = 55000L;




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
				URL url = new URL(AktienMan.url.get(URLs.URL_INDEXBBBANK));
				
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				String str_title = AktienMan.url.getString(URLs.STR_BBB_INDEXTITLE);
				String str_set   = AktienMan.url.getString(URLs.STR_BBB_INDEXSET);
				String str_ende  = AktienMan.url.getString(URLs.STR_BBB_INDEXENDE);
				String str_value = AktienMan.url.getString(URLs.STR_BBB_INDEXVALUE);

				String s, symbol = "", datum = "";
				long punkte = 0L, vortag = 0L;
				int status = 0;
				
				while ((s = in.readLine()) != null)
				{
					if (status == 0)
					{
						if (s.indexOf(str_title) > 0)
						{
							int i = s.indexOf(str_set);
							
							if (i > 0)
							{
								int i2 = s.indexOf("\"",i);
								
								if (i2 > i)
								{
									symbol = s.substring(i+str_set.length(),i2).trim();

									status = 1;
								}
							}
						}
					}
					else
					{
						if (s.indexOf(str_ende) >= 0)
						{
							status = 0;
						}
						else
						{
							status++;
							
							switch (status)
							{
							case 2:
								{
									int i = s.indexOf(str_value);
									int i2 = s.indexOf("<",i);
									
									if ((i > 0) && (i2 > i))
									{
										String pstr = s.substring(i+str_value.length(),i2).trim();

										try
										{
											punkte = Waehrungen.doubleToLong(pstr);
										}
										catch (NumberFormatException e)
										{
											punkte = BenutzerAktie.VALUE_NA;
										}
									}
									else
									{
										punkte = BenutzerAktie.VALUE_NA;
									}
								}
								break;
								
							case 3:
								{
									int i = s.indexOf(str_value);
									int i2 = s.indexOf("<",i);
									
									if ((i > 0) && (i2 > i))
									{
										datum = s.substring(i+str_value.length(),i2).trim();
									}
									else
									{
										datum = "";
									}
								}
								break;
								
							case 4:
								{
									int i = s.indexOf("<");
									
									if (i > 0)
									{
										String zeit = s.substring(0,i).trim();
										
										i = zeit.indexOf(":", zeit.indexOf(":") + 1);
										
										if (i > 0)
										{
											zeit = zeit.substring(0,i);
										}
										
										datum = datum + " " + zeit;
									}
								}
								break;
								
							case 7:
								{
									int i = s.indexOf(str_value);
									int i2 = s.indexOf("<",i);
									
									if ((i > 0) && (i2 > i))
									{
										String vstr = s.substring(i+str_value.length(),i2).trim();

										try
										{
											vortag = Waehrungen.doubleToLong(vstr);
										}
										catch (NumberFormatException e)
										{
											vortag = BenutzerAktie.VALUE_NA;
										}
									}
									else
									{
										vortag = BenutzerAktie.VALUE_NA;
									}
									
									IndexQuelle.checkIndex(symbol,punkte,vortag,datum);
							
									status = 0;
								}
								break;
							}
						}
					}
				}
				
				IndexQuelle.updateFinished();
			}
			catch (MalformedURLException e)
			{
				System.out.println("BBBank-Index-URL fehlerhaft.");
			}
			catch (IOException e) {}
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
			
			if (IndexQuelle.autoIndexOn())
			{
				try
				{
					sleep(TIMEOUT);
				}
				catch (InterruptedException e) {}
			}

		} while (IndexQuelle.autoIndexOn());
	}
	catch (Exception e) {}
	finally
	{
		IndexQuelle.clearThread();
	}
}

}
