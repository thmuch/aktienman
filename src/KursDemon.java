/**
 @author Thomas Much
 @version 1999-01-15
*/



public final class KursDemon extends Thread {

private static final int STANDARDTIMEOUTSEKUNDEN = 300;

private static KursDemon kursdemon = null;
private static String boerse = "";
private static long timeout = 0L;



public KursDemon() {
	super();
	setDaemon(true);
}


public void run() {
	try
	{
		while (true)
		{
			try
			{
				sleep(getTimeoutMillis());
			}
			catch (InterruptedException e) {}
			
			AktienMan.hauptdialog.listeAktualisierenAusfuehren(getBoerse());
		}
	}
	catch (Exception e) {}
}


private synchronized static void setKursDemon(KursDemon rd) {
	kursdemon = rd;
}


public synchronized static void createKursDemon() {
	if (kursdemon == null)
	{
		setKursDemon(new KursDemon());
		setBoerse("");
		kursdemon.start();
	}
}


public synchronized static void deleteKursDemon() {
	if (kursdemon != null)
	{
		kursdemon.stop();
		setKursDemon(null);
	}
}


private synchronized static String getBoerse() {
	String b = boerse;
	
	boerse = "";
	
	return b;
}


private synchronized static void setBoerse(String b) {
	boerse = b;
}


public synchronized static boolean canCallKursDemon(String boerse) {
	if (kursdemon != null)
	{
		setBoerse(boerse);
		
		kursdemon.interrupt();

		return true;
	}
	else
	{
		return false;
	}
}


public synchronized static long getTimeoutMillis() {
	if (timeout <= 0L)
	{
		timeout = 1000L * (long)AktienMan.properties.getInt("Konfig.KursTimeoutSekunden",STANDARDTIMEOUTSEKUNDEN);
	}
	
	return timeout;
}


public synchronized static String getTimeoutMinutenString() {
	return new Integer((int)(getTimeoutMillis() / 60000L)).toString();
}


public synchronized static void setTimeoutMinuten(int minuten) {
	AktienMan.properties.setInt("Konfig.KursTimeoutSekunden",minuten*60);
	timeout = (long)minuten * 60000L;
}

}
