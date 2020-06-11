/**
 @author Thomas Much
 @version 1999-06-28
*/




public class ComdirectQuelle extends KursQuelle {

public static final String VALUENA = "n/a";




public ComdirectQuelle() {

	super("Comdirect",KursQuellen.QUELLE_COMDIRECT,KursQuellen.QUELLE_DEUTSCHEBANK);
}



public synchronized void flush() {}



public synchronized void sendRequest(String request, String baWKN,
                                      String baBoerse, boolean sofortZeichnen, boolean firstCall) {

	new ComdirectLeser(AktienMan.hauptdialog,request,baWKN,baBoerse,sofortZeichnen,getNextID(firstCall)).start();
}



public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, String request, String baWKN,
																	String baBoerse, boolean firstCall) {

	new ComdirectLeser(parent,request,baWKN,baBoerse,true,getNextID(firstCall)).start();
}

}
