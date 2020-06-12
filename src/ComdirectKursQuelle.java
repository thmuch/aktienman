/**
 @author Thomas Much
 @version 2003-02-26
*/

import java.util.*;




public class ComdirectKursQuelle extends KursQuelle {



static
{
	if (AktienMan.DEBUG)
	{
		System.out.println("  Plugin: ComdirectKursQuelle wird initialisiert.");
	}

	KursQuellen.register( new ComdirectKursQuelle() );
}




private ComdirectKursQuelle() {

	super("Comdirect",KursQuellen.ID_COMDIRECT,KursQuellen.PRIORITY_COMDIRECT);
}



public void flush() {}



public synchronized void sendRequest(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, KursQuelle first) {

	Runnable leser = new ComdirectKursLeser(this,receiver,request,baWKN,baBoerse,sofortZeichnen,first);

	new Thread(leser).start();
}


}
