/**
 @author Thomas Much
 @version 1998-12-07
*/

import java.net.*;
import java.awt.*;
import java.io.*;



public class UpdateLoader extends Thread {

private String filename;
private String archiv;
private UpdateDisplay udisplay;

private boolean stopped = false;



public UpdateLoader(UpdateDisplay udisplay, String archiv, String filename) {
	super();
	this.udisplay = udisplay;
	this.archiv = archiv;
	this.filename = filename;
}


public void stopDownload() {
	stopped = true;
}


public void run() {
	DataInputStream in = null;
	byte[] daten = null;
	int laenge = 0, gelesen = 0;
	boolean valid = false;
	
	try
	{
		URL url = new URL(URLs.AMDOWNLOAD+archiv);
		URLConnection curl = url.openConnection();
		curl.setUseCaches(false);
		
		laenge = curl.getContentLength();

		int zuLesen = laenge;
		
		daten = new byte[laenge];
		
		in = new DataInputStream(curl.getInputStream());
		
		while ((!stopped) && (zuLesen > 0))
		{
			if (zuLesen >= 1024)
			{
				in.readFully(daten,gelesen,1024);
				gelesen += 1024;
				zuLesen -= 1024;
			}
			else
			{
				in.readFully(daten,gelesen,zuLesen);
				gelesen += zuLesen;
				zuLesen = 0;
			}
			
			udisplay.setProgress(gelesen/1024,(gelesen*100)/laenge);
		}
		
		if (!stopped)
		{
			valid = save(daten);
		}
	}
	catch (MalformedURLException e)
	{
		System.out.println("URL des Update-Archivs fehlerhaft.");
	}
	catch (IOException e) {}
	catch (NegativeArraySizeException e) {}
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

		if (stopped)
		{
			udisplay.setStatus(UpdateDisplay.STATUS_CANCELLED);
		}
		else if (valid)
		{
			udisplay.setStatus(UpdateDisplay.STATUS_FINISHED);
		}
		else
		{
			udisplay.setStatus(UpdateDisplay.STATUS_ERROR);
		}
	}
}


private boolean save(byte[] daten) {
	DataOutputStream out = null;
	boolean valid = false;

	File f = new File(filename);
	
	if (f.exists())
	{
		File backup = new File(filename + ".bak");
		
		if (backup.exists()) backup.delete();
		
		f.renameTo(backup);
	}
	
	try
	{
		out = new DataOutputStream(new FileOutputStream(filename));

		out.write(daten,0,daten.length);
		out.flush();

		valid = true;
	}
	catch (IOException e) {}
	finally
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e) {}
		
			out = null;
		}
	}
	
	return valid;
}

}
