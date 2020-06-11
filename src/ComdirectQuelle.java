/**
 @author Thomas Much
 @version 1999-01-29
*/



public class ComdirectQuelle extends KursQuelle {

public static final String VALUENA = "n/a";



public ComdirectQuelle() {
	super("Comdirect",KursQuellen.QUELLE_COMDIRECT,KursQuellen.QUELLE_DEUTSCHEBANK);
}


public synchronized void flush() {}


public synchronized void sendRequest(String request, String baWKN,
                                      String baBoerse, boolean sofortZeichnen, boolean firstCall) {
	new ComdirectLeser(request,baWKN,baBoerse,sofortZeichnen,getNextID(firstCall)).start();
}


public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, int index, String request, boolean firstCall) {
	new ComdirectMaxkursLeser(parent,index,request,getNextID(firstCall)).start();
}

}
