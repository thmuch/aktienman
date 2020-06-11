/**
 @author Thomas Much
 @version 1999-06-14
*/

import java.util.*;
import java.awt.*;




public final class KursQuellen extends Vector {

public static final int QUELLE_NONE         = -1;
public static final int QUELLE_COMDIRECT    =  0;
public static final int QUELLE_DEUTSCHEBANK =  1;
//public static final int QUELLE_YAHOO_DE     =  2;

private static final int STANDARDQUELLE = QUELLE_COMDIRECT;

private static KursQuellen quellen = new KursQuellen();

private static int kursquelle = QUELLE_NONE;




public KursQuellen() {
	super(5);
	setupList();
}



public synchronized void setupList() {
	add(new ComdirectQuelle());
	add(new DeutscheBankQuelle());
	//add(new YahooDeQuelle());
}



public synchronized void add(KursQuelle eintrag) {
	addElement(eintrag);
}



public synchronized KursQuelle getAt(int index) {
	return (KursQuelle)elementAt(index);
}



public synchronized static Choice getChoice() {
	Choice choice = new Choice();
	
	for (int i=0; i < quellen.size(); i++)
	{
		choice.addItem(quellen.getAt(i).getName());
	}
	
	return choice;
}



public synchronized static KursQuelle getKursQuelle(int index) {
	return quellen.getAt(index);
}



public synchronized static KursQuelle getKursQuelle() {
	return getKursQuelle(getKursQuelleIndex());
}



public synchronized static KursQuelle getFondsQuelle() {
	return getKursQuelle();
}



public synchronized static int getKursQuelleIndex() {
	if (kursquelle <= QUELLE_NONE)
	{
		kursquelle = AktienMan.properties.getInt("Konfig.Kursquelle",STANDARDQUELLE);
	}
	
	return kursquelle;
}



public synchronized static void setKursQuelleIndex(int neu) {
	AktienMan.properties.setInt("Konfig.Kursquelle",neu);
	kursquelle = neu;
}

}
