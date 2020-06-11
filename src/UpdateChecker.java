/**
 @author Thomas Much
 @version 1998-11-30
*/

import java.net.*;
import java.io.*;
import java.util.*;



public class UpdateChecker extends Thread {

private static boolean checked = false;



public UpdateChecker() {
	super();
}


public void run() {
	boolean valid = false;
	
	try
	{
		URL url = new URL(URLs.AMUPDATE);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(s,",");
				
				if (st.nextToken().trim().equalsIgnoreCase(AktienMan.getOSString()))
				{
					String nrs    = st.nextToken().trim();
					String archiv = st.nextToken().trim();
					String text   = st.nextToken().trim();
					
					int newrelease;
					try
					{
						newrelease = Integer.parseInt(nrs);
					}
					catch (NumberFormatException e)
					{
						newrelease = 0;
					}

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
		in.close();
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
	}
}


public synchronized static void check() {
	if (!checked)
	{
		checked = true;
		
		try
		{
			new UpdateChecker().start();
		}
		catch (Exception e)
		{
			checked = false;
		}
	}
}

}
