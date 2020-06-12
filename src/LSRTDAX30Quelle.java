/**
 @author Thomas Much
 @version 2000-08-09
*/

import java.io.*;
import java.util.*;




public class LSRTDAX30Quelle extends KursQuelle {

public static final int TYPE_BID = 0;
public static final int TYPE_ASK = 1;

private static boolean useLSRT = false;

private boolean requestPending = false;
private boolean requestSofortZeichnen = false;
private int type;
private Vector requests = new Vector();




static {

	String moduleDir = FileUtil.getModuleDirectory();
	
	if (moduleDir != null)
	{
		if (new File(moduleDir + "LSRealtime").exists())
		{
			useLSRT = true;
		}
	}
}



public LSRTDAX30Quelle(int type) {

	super("Lang&Schwarz RT-DAX30 ("+(type==TYPE_BID?"BID":"ASK")+")",
			type==TYPE_BID ? KursQuellen.QUELLE_LSRTDAX30BID : KursQuellen.QUELLE_LSRTDAX30ASK,
			KursQuellen.QUELLE_NONE);

	this.type = type;
}



public void flush() {

	if (requestPending)
	{
		new LSRTDAX30Leser(AktienMan.hauptdialog,type,requestSofortZeichnen,requests).start();
	
		requestPending = false;
		requestSofortZeichnen = false;
		
		requests = new Vector();
	}
}



public synchronized void sendRequest(String request, String baWKN, String baBoerse, boolean sofortZeichnen, boolean firstCall) {

	requests.addElement(request+":"+baWKN+":"+baBoerse);

	requestPending = true;
	
	if (sofortZeichnen)
	{
		requestSofortZeichnen = true;
	}
}



public synchronized void sendSingleMaxkursRequest(AktieMaximalkurs parent, String request, String baWKN, String baBoerse, boolean firstCall) {

	KursQuellen.getPlatzKursQuelle().sendSingleMaxkursRequest(parent,request,baWKN,baBoerse,firstCall);
}



public static boolean canUseLSRT() {

	return useLSRT;
}

}
