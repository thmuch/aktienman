/**
 @author Thomas Much
 @version 2003-01-23

 2003-01-23
 	ID_*
 	getBoerse
*/

import java.awt.*;




public final class Boersenliste extends Aktienliste /* TODO: besser Containment? */ {

public static final String ID_NONE        = "";
public static final String ID_BERLIN      = "BER";
public static final String ID_BREMEN      = "BRE";
public static final String ID_DUESSELDORF = "DUS";
public static final String ID_FRANKFURT   = "FSE";
public static final String ID_HAMBURG     = "HAM";
public static final String ID_HANNOVER    = "HAN";
public static final String ID_MUENCHEN    = "MUN";
public static final String ID_STUTTGART   = "STU";
public static final String ID_XETRA       = "ETR";
public static final String ID_FONDS_DE    = "DFK";


private static final int STANDARDBOERSE = 3;




public synchronized void setupList() {

	add(new Boersenplatz("Berlin",ID_BERLIN));
	add(new Boersenplatz("Bremen",ID_BREMEN));
	add(new Boersenplatz("D\u00fcsseldorf",ID_DUESSELDORF));
	add(new Boersenplatz("Frankfurt",ID_FRANKFURT));
	add(new Boersenplatz("Hamburg",ID_HAMBURG));
	add(new Boersenplatz("Hannover",ID_HANNOVER));
	add(new Boersenplatz("M\u00fcnchen",ID_MUENCHEN));
	add(new Boersenplatz("Stuttgart",ID_STUTTGART));
	add(new Boersenplatz("Xetra-Handel",ID_XETRA));
	add(new Boersenplatz("Fonds",ID_FONDS_DE,true));
}



public synchronized Boersenplatz getAt(int index) {

	return (Boersenplatz)elementAt(index);
}



public synchronized Aktie getAktie(int index) {

	return null;
}



public synchronized Boersenplatz getBoerse(String kurz) {

	if ((kurz != null) && (kurz.length() > 0))
	{
		for (int i=0; i < size(); i++)
		{
			Boersenplatz bp = getAt(i);
			
			if (bp.getKurz().equalsIgnoreCase(kurz)) return bp;
		}
	}

	return getAt(STANDARDBOERSE);
}



public synchronized int getBoersenIndex(String kurz, int defval) {

	for (int i=0; i < size(); i++)
	{
		if (getAt(i).getKurz().equalsIgnoreCase(kurz)) return i;
	}

	return defval;
}



public synchronized int getStandardBoerse() {

	return AktienMan.properties.getInt("Konfig.StdBoerse",STANDARDBOERSE);
}



public synchronized Choice getChoiceNoFonds() {

	Choice c = super.getChoice(true);
	
	c.remove(size() - 1);
	
	return c;
}



public synchronized int getCountNoFonds() {

	return size() - 1;
}

}
