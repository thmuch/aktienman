/**
 @author Thomas Much
 @version 1999-01-29
*/



public class DeutscheBankQuelle extends KursQuelle {

public static final String VALUENA = "n/a";



public DeutscheBankQuelle() {
	super("Deutsche Bank",KursQuellen.QUELLE_DEUTSCHEBANK,KursQuellen.QUELLE_COMDIRECT);
}


public synchronized void flush() {}


public synchronized void sendRequest(String request, String baWKN,
                                      String baBoerse, boolean sofortZeichnen, boolean firstCall) {
	new DeutscheBankLeser(request,baWKN,baBoerse,sofortZeichnen,getNextID(firstCall)).start();
}


public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, int index, String request, boolean firstCall) {
	new DeutscheBankMaxkursLeser(parent,index,request,getNextID(firstCall)).start();
}

}
