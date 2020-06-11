/**
 @author Thomas Much
 @version 1999-06-28
*/




public class DeutscheBankQuelle extends KursQuelle {

public static final String VALUENA = "n/a";




public DeutscheBankQuelle() {

	super("Deutsche Bank",KursQuellen.QUELLE_DEUTSCHEBANK,KursQuellen.QUELLE_COMDIRECT);
}



public synchronized void flush() {}



public synchronized void sendRequest(String request, String baWKN,
                                      String baBoerse, boolean sofortZeichnen, boolean firstCall) {

	new DeutscheBankLeser(AktienMan.hauptdialog,request,baWKN,baBoerse,sofortZeichnen,getNextID(firstCall)).start();
}



public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, String request, String baWKN,
																	String baBoerse, boolean firstCall) {

	new DeutscheBankLeser(parent,request,baWKN,baBoerse,true,getNextID(firstCall)).start();
}

}
