/**
 @author Thomas Much
 @version 2003-01-28

 2003-01-28
 	die Maxkurs-Methoden entfernt; der Maximalkursdialog verwendet nun die normalen Routinen
 	priority, getPriority
*/




public abstract class KursQuelle {

private String name;
private long id;
private int priority;




protected KursQuelle(String name, long id, int priority) {

	this.name     = name;
	this.id       = id;
	this.priority = priority;
}



public String getName() {

	return name;
}



public int getPriority() {

	return priority;
}



public long getID() {

	return id;
}



public abstract void flush();



public abstract void sendRequest(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, KursQuelle first);



public synchronized void sendRequest(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen) {

	sendRequest(receiver,request,baWKN,baBoerse,sofortZeichnen,this);
}



public synchronized void sendRequest(KursReceiver receiver, String request, String baWKN, String baBoerse) {

	sendRequest(receiver,request,baWKN,baBoerse,false,this);
}



public synchronized void sendSingleRequest(KursReceiver receiver, String request, String baWKN, String baBoerse, boolean sofortZeichnen, KursQuelle first) {

	sendRequest(receiver,request,baWKN,baBoerse,sofortZeichnen,first);
	flush();
}



public synchronized void sendSingleRequest(KursReceiver receiver, String request, String baWKN, String baBoerse) {

	sendSingleRequest(receiver, request,baWKN,baBoerse,true,this);
}


}
