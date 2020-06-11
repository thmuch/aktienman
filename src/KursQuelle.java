/**
 @author Thomas Much
 @version 1999-01-29
*/



public abstract class KursQuelle {

private String name;
private int id, nextID;



public KursQuelle(String name, int id, int nextID) {
	this.name = name;
	this.id = id;
	this.nextID = nextID;
}


public String getName() {
	return name;
}


public int getID() {
	return id;
}


public int getNextID(boolean firstCall) {
	return (firstCall ? nextID : KursQuellen.QUELLE_NONE);
}


public abstract void flush();


public abstract void sendRequest(String request, String baWKN,
                                  String baBoerse, boolean sofortZeichnen, boolean firstCall);


public synchronized void sendRequest(String request, String baWKN, String baBoerse) {
	sendRequest(request,baWKN,baBoerse,false,true);
}


public synchronized void sendSingleRequest(String request, String baWKN,
                                            String baBoerse, boolean sofortZeichnen, boolean firstCall) {
	sendRequest(request,baWKN,baBoerse,sofortZeichnen,firstCall);
	flush();
}


public synchronized void sendSingleRequest(String request, String baWKN, String baBoerse) {
	sendSingleRequest(request,baWKN,baBoerse,true,true);
}


public abstract void sendSingleMaxkursRequest(AktieMaximalkurs parent, int index, String request, boolean firstCall);


public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, int index, String request) {
	sendSingleMaxkursRequest(parent,index,request,true);
}


// verfügbare Börsenplätze abfragen

}
