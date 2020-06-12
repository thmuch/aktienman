/**
 @author Thomas Much
 @version 2000-03-13
*/

import java.awt.*;




public final class Connections {

private static final int STANDARDMAXCONNECTIONS = 10;

private static Object lock = new Object();

private static int maxConnections = -1;
private static int connections = 0;




public static Choice getPopup() {

	Choice popup = new Choice();

	popup.add("4");
	popup.add("6");
	popup.add("8");
	popup.add("10");
	popup.add("12");
	popup.add("unbeschr\u00e4nkt");
	
	int maxconn = getMaxConnections();
	
	if ((maxconn >= 4) && (maxconn <= 12))
	{
		popup.select((maxconn - 4) / 2);
	}
	else
	{
		popup.select(5);
	}
	
	return popup;
}



public static synchronized void setMaxConnections(int popupIndex) {

	if ((popupIndex >= 0) && (popupIndex < 5))
	{
		maxConnections = 4 + 2 * popupIndex;
	}
	else
	{
		maxConnections = Integer.MAX_VALUE;
	}

	AktienMan.properties.setInt("Connections.Max",maxConnections);

	synchronized (lock)
	{
		lock.notifyAll();
	}
}



private synchronized static int getMaxConnections() {

	if (maxConnections <= 0)
	{
		maxConnections = AktienMan.properties.getInt("Connections.Max",STANDARDMAXCONNECTIONS);
	}
	
	return maxConnections;
}



public static void getConnection() {

	synchronized (lock)
	{
		while (connections >= getMaxConnections())
		{
			try
			{
				lock.wait();
			}
			catch (Exception e) {}
		}
		
		connections++;
		
		if (AktienMan.DEBUG)
		{
			System.out.println("+++ Neue Connection (jetzt "+connections+")");
		}
	}
}



public static void releaseConnection() {

	synchronized (lock)
	{
		connections--;

		if (AktienMan.DEBUG)
		{
			System.out.println("--  nur noch "+connections+" Connections");
		}
		
		if (connections < 0) connections = 0;
		
		lock.notifyAll();
	}
}

}
