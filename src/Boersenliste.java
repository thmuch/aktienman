/**
 @author Thomas Much
 @version 1999-06-28
*/

import java.awt.*;




public final class Boersenliste extends Aktienliste {

private static final int STANDARDBOERSE = 3;




public synchronized void setupList() {
	add(new Boersenplatz("Berlin","BER"));
	add(new Boersenplatz("Bremen","BRE"));
	add(new Boersenplatz("D\u00fcsseldorf","DUS"));
	add(new Boersenplatz("Frankfurt","FSE"));
	add(new Boersenplatz("Hamburg","HAM"));
	add(new Boersenplatz("Hannover","HAN"));
	add(new Boersenplatz("M\u00fcnchen","MUN"));
	add(new Boersenplatz("Stuttgart","STU"));
	add(new Boersenplatz("Xetra-Handel","ETR"));
	add(new Boersenplatz("Fonds","DFK",true));
}



public synchronized Boersenplatz getAt(int index) {
	return (Boersenplatz)elementAt(index);
}



public synchronized Aktie getAktie(int index) {
	return null;
}



public synchronized int getBoersenIndex(String kurz) {

	for (int i=0; i < size(); i++)
	{
		if (getAt(i).getKurz().equalsIgnoreCase(kurz)) return i;
	}

	return 0;
}



public synchronized int getStandardBoerse() {
	return AktienMan.properties.getInt("Konfig.StdBoerse",STANDARDBOERSE);
}



public synchronized Choice getChoiceNoFonds() {

	Choice c = super.getChoice(true);
	
	c.remove(size() - 1);
	
	return c;
}

}
