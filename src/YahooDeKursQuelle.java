/**
 @author Thomas Much
 @version 2003-01-28
*/

import java.util.*;




public class YahooDeKursQuelle extends KursQuelle {

private static final int MAXPOOL = 15;

private int requestsPending = 0;
private boolean requestSofortZeichnen = false;
private KursQuelle requestFirst = null;
private KursReceiver requestReceiver = null;
private Vector requests = new Vector();



static
{
	if (AktienMan.DEBUG)
	{
		System.out.println("  Plugin: YahooDeKursQuelle wird initialisiert.");
	}

	KursQuellen.register( new YahooDeKursQuelle() );
}




private YahooDeKursQuelle() {

	super("Yahoo.de",KursQuellen.ID_YAHOO_DE,KursQuellen.PRIORITY_YAHOO_DE);
}



public synchronized void flush() {

	if (requestsPending > 0)
	{
		Runnable leser = new YahooDeKursLeser(this,requestReceiver,requests,requestSofortZeichnen,requestFirst);
	
		new Thread(leser).start();
	
		requestsPending = 0;
		requestSofortZeichnen = false;
		requestFirst = null;
		requestReceiver = null;
		
		requests = new Vector();
	}
}



public synchronized void sendRequest(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, KursQuelle first) {

	if (((requestReceiver != null) && (requestReceiver != receiver)) || ((requestFirst != null) && (requestFirst != first)))
	{
		flush();
	}

	requests.addElement(request+";"+baWKN+";"+baBoerse);
	
	requestsPending++;

	requestFirst = first;
	requestReceiver = receiver;

	if (sofortZeichnen)
	{
		requestSofortZeichnen = true;
	}
	
	if (requestsPending >= MAXPOOL)
	{
		flush();
	}
}


}
