/**
 @author Thomas Much
 @version 1999-01-19
*/



public final class RedrawDemon extends Thread {

private static final long TIMEOUT = 4000L;

private static RedrawDemon redrawdemon = null;

private int redrawRequests;
private boolean redrawTo00,oldRedrawTo00;
private int saveRequests;
private int infoRequests;



public RedrawDemon() {
	super();
	setDaemon(true);

	clearRedrawRequests();
	clearSaveRequests();
	clearInfoRequests();
}


public void run() {
	try
	{
		while (true)
		{
			try
			{
				sleep(TIMEOUT);
			}
			catch (InterruptedException e) {}
			
			if (checkAndClearRedrawRequests())
			{
				AktienMan.hauptdialog.listeRedraw(oldRedrawTo00);
			}

			if (checkAndClearInfoRequests())
			{
				AktienMan.hauptdialog.listeUpdateInfo();
			}

			if (checkAndClearSaveRequests())
			{
				AktienMan.hauptdialog.saveBenutzerAktien();
			}
		}
	}
	catch (Exception e) {}
	finally
	{
		setRedrawDemon(null);
	}
}


private void clearRedrawRequests() {
	redrawRequests = 0;
	redrawTo00 = false;
}


private void clearSaveRequests() {
	saveRequests = 0;
}


private void clearInfoRequests() {
	infoRequests = 0;
}


public synchronized void clearAllRequests() {
	clearRedrawRequests();
	clearSaveRequests();
	clearInfoRequests();
}


public synchronized void incRedrawRequests(boolean to00) {
	redrawRequests++;
	if (to00) redrawTo00 = true;
}


public synchronized void incSaveRequests() {
	saveRequests++;
}


public synchronized void incInfoRequests() {
	infoRequests++;
}


private synchronized boolean checkAndClearRedrawRequests() {
	if (redrawRequests > 0)
	{
		oldRedrawTo00 = redrawTo00;
		clearRedrawRequests();
		return true;
	}
	else
	{
		return false;
	}
}


private synchronized boolean checkAndClearSaveRequests() {
	if (saveRequests > 0)
	{
		clearSaveRequests();
		return true;
	}
	else
	{
		return false;
	}
}


private synchronized boolean checkAndClearInfoRequests() {
	if (infoRequests > 0)
	{
		clearInfoRequests();
		return true;
	}
	else
	{
		return false;
	}
}


private synchronized static void setRedrawDemon(RedrawDemon rd) {
	redrawdemon = rd;
}


public synchronized static RedrawDemon getRedrawDemon() {
	if (redrawdemon == null)
	{
		setRedrawDemon(new RedrawDemon());
		redrawdemon.start();
	}
	
	return redrawdemon;
}

}
