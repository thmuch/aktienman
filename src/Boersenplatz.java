/**
 @author Thomas Much
 @version 1999-01-03
*/

import java.io.*;



public class Boersenplatz extends Listeneintrag implements Serializable {

static final long serialVersionUID = 1975101300000L;

private boolean fondsOnly = false;
private int waehrung = Waehrungen.EUR;



public Boersenplatz(String name, String kurz) {
	this(name,kurz,false,Waehrungen.EUR);
}


public Boersenplatz(String name, String kurz, boolean fondsOnly) {
	this(name,kurz,fondsOnly,Waehrungen.EUR);
}


public Boersenplatz(String name, String kurz, int waehrung) {
	this(name,kurz,false,waehrung);
}


public Boersenplatz(String name, String kurz, boolean fondsOnly, int waehrung) {
	super(name,kurz);
	this.fondsOnly = fondsOnly;
	this.waehrung = waehrung;
}


public boolean isFondsOnly() {
	return fondsOnly;
}


public int getWaehrung() {
	return waehrung;
}

}
