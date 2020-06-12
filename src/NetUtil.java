/**
 @author Thomas Much
 @version 2003-04-08

 2003-04-08
 	loadRawURL hat als neuen Parameter nun die maximale Anzahl der zu lesenden Bytes
 2003-03-04
 	getRedirectedInputStream
 2003-02-26
 	Daten werden nun gepuffert und ohne Cache-Zugriff geladen
*/

import java.io.*;
import java.net.*;



public final class NetUtil {



private NetUtil() {}



public static byte[] loadRawURL(final String urlname, boolean useCache, int maxlen) {

	DataInputStream in = null;

	try
	{
		URL url = new URL(urlname);

		URLConnection curl = url.openConnection();

		curl.setUseCaches(useCache);

		int contlen = curl.getContentLength();

		int len = contlen;
		
		if ((maxlen >= 0) && (maxlen < contlen))
		{
			len = maxlen;
		}

		byte[] daten = new byte[len];

		in = new DataInputStream(new BufferedInputStream(curl.getInputStream()));

		in.readFully(daten);
		
		System.out.println("URL "+urlname+" geladen ("+len+"/"+contlen+" Bytes)"); // TODO
		
		return daten;
	}
	catch (Exception e)
	{
		return null;
	}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{
				AktienMan.errlog("Fehler beim Laden der URL "+urlname,e);
			}
			finally
			{
				in = null;
			}
		}
	}
}



public synchronized static InputStream getRedirectedInputStream(final URL url) throws IOException {

	boolean oldFollow = HttpURLConnection.getFollowRedirects();
	
	try
	{
		HttpURLConnection.setFollowRedirects(false);

		URLConnection conn = url.openConnection();
		
		if (conn instanceof HttpURLConnection)
		{
			int response = ((HttpURLConnection)conn).getResponseCode();

			boolean moved = (300 <= response) && (response <= 399);
			
			if (moved)
			{
				String loc = conn.getHeaderField("Location");
				
				System.out.println("Response "+response+", redirect "+loc); // TODO
				
				URL redirect;

				if (loc.startsWith("http"))
				{
					redirect = new URL(loc);
				}
				else
				{
					int port = url.getPort();
					
					if (port >= 0)
					{
						redirect = new URL(url.getProtocol(), url.getHost(), port, loc);
					}
					else
					{
						redirect = new URL(url.getProtocol(), url.getHost(), loc);
					}
				}

				System.out.println("--> "+redirect); // TODO

				conn = redirect.openConnection();
			}
		}
		
		return conn.getInputStream();
	}
	finally
	{
		HttpURLConnection.setFollowRedirects(oldFollow);
	}
}


}
