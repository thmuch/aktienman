/**
 @author Thomas Much
 @version 2003-02-26
*/

import java.net.*;
import java.io.*;
import java.util.*;




public final class UpdateManager {

private static boolean checkDone = false;
private static AFrame dialog = null;



private UpdateManager() {}



public synchronized static void checkForUpdates(boolean always)
{
	if (checkDone && !always) return;

	if (dialog != null)
	{
		dialog.toFront();
		return;
	}

	checkDone = true;

	AktienMan.checkURLs();
	
	/* TODO: Thread? */

	BufferedReader in = null;

	boolean valid = false;
	String error = "";

	try
	{
		URL url = new URL(AktienMan.HOMEPAGE+"update/check");
		
		in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String s;
		
		while ((s = in.readLine()) != null)
		{
			s = s.trim();
			
			if (s.length() > 0)
			{
				/* TODO */
			}
		}
		
		/* TODO */
		error = "Update-Datei fehlerhaft";
	}
	catch (Exception e)
	{
		error = e.toString();
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
		}
	}

	if (valid)
	{
		displayUpdates();
	}
	else if (always)
	{
		// Fehlermeldungen nur anzeigen, wenn die Update-Suche manuell ausgelöst wurde
		displayError(error);
	}
}



private synchronized static void clearDialogRef()
{
	dialog = null;
}



private static void displayError(String error)
{
	class UpdateErrorAlert extends TextWarnalert
	{
		public UpdateErrorAlert(String msg)
		{
			super(null,msg);
		}

		public void closed()
		{
			clearDialogRef();
		}
	}

	dialog = new UpdateErrorAlert("Fehler bei der Suche nach Updates:|Die Update-Informationen können derzeit nicht abgefragt werden.|Bitte versuchen Sie es zu einem späteren Zeitpunkt noch einmal.|( "+error+" )");
}



private static void displayUpdates()
{
	/* TODO */
}


}
