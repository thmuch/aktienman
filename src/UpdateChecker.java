/**
 @author Thomas Much
 @version 2003-02-03
*/

import java.net.*;
import java.io.*;
import java.util.*;



public final class UpdateChecker implements Runnable {

private static boolean checked = false;




private UpdateChecker() {}



public void run() {

	BufferedReader in = null;
	boolean valid = false;
	
	AktienMan.checkURLs();
	
	try
	{
		URL url = new URL(AktienMan.url.get(URLs.URL_AMUPDATE));
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(s,",");
				
				if (st.nextToken().trim().equalsIgnoreCase(SysUtil.getOSString()))
				{
					String nrs    = st.nextToken().trim();
					String archiv = st.nextToken().trim();
					String text   = st.nextToken().trim();
					
					int newrelease = 0;
					try
					{
						newrelease = Integer.parseInt(nrs);
					}
					catch (NumberFormatException e) {}

					if (newrelease > AktienMan.RELEASE)
					{
						if (AktienMan.properties.getInt("Update.Release") < newrelease)
						{
							new Updater(newrelease,archiv,text);
						}
					}
					
					break;
				}
			}
		}
		
		valid = true;
	}
	catch (MalformedURLException e)
	{
		System.out.println("Update-URL fehlerhaft.");
	}
	catch (NullPointerException e) {}
	catch (IOException e) {}
	finally
	{
		if (!valid) checked = false;

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



public synchronized static void check() {

	if (!checked)
	{
		try
		{
			new Thread(new UpdateChecker()).start();
			checked = true;
		}
		catch (Exception e) {}
	}
}


}
