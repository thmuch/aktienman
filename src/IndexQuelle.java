/**
 @author Thomas Much
 @version 1999-06-30
*/

import java.util.*;
import java.awt.*;




public final class IndexQuelle {

private static Panel indexPanel = null;
private static Thread leser = null;

private static Vector canvasliste = new Vector();




public synchronized static void call() {

	Thread t = getThread();
	
	if (t == null)
	{
		t = new BBBankIndexLeser();
		
		setThread(t);
		
		t.start();
	}
	else
	{
		t.interrupt();
	}
}



private synchronized static Thread getThread() {

	return leser;
}



private synchronized static void setThread(Thread thread) {

	leser = thread;
}



public synchronized static void clearThread() {

	setThread(null);
}



public static synchronized void addCanvas(IndexCanvas canvas) {

	canvasliste.addElement(canvas);
}



public synchronized static void setPanel(Panel panel) {

	indexPanel = panel;
}



public static synchronized void checkIndex(String symbol, long punkte, long vortag, String datum) {

	for (int i = 0; i < canvasliste.size(); i++)
	{
		IndexCanvas ic = (IndexCanvas)canvasliste.elementAt(i);
		
		if (ic.hasSymbol(symbol))
		{
			ic.setValues(punkte,vortag,datum);
			ic.repaint();
		}
	}
}



public synchronized static void updateFinished() {

	if (indexPanel != null)
	{
		indexPanel.validate();
		indexPanel.repaint();
	}
	
	saveValues();
}



public static boolean autoIndexOn() {

	return AktienMan.properties.getBoolean("Konfig.Index",true);
}



public synchronized static void loadValues() {
	/**/
}



private synchronized static void saveValues() {
	/**/
}

}
