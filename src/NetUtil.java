/**
 @author Thomas Much
 @version 2000-03-12
*/

import java.io.*;
import java.net.*;



public final class NetUtil {



private NetUtil() {}



public static byte[] loadRawURL(String urlname) {

	DataInputStream in = null;

	try
	{
		URL url = new URL(urlname);

		URLConnection curl = url.openConnection();
			
		byte[] daten = new byte[curl.getContentLength()];
			
		in = new DataInputStream(curl.getInputStream());

		in.readFully(daten);
		
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
			catch (Exception e) {}
			finally
			{
				in = null;
			}
		}
	}
}

}
