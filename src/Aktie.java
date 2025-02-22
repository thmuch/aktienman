/**
 @author Thomas Much
 @version 1999-06-13
*/

import java.io.*;



public class Aktie extends Listeneintrag implements Serializable {

static final long serialVersionUID = 1978090500000L;

private int wkn;
private int waehrung;



public Aktie(String name, String kurz, int wkn) {
	this(name,kurz,wkn,Waehrungen.EUR/* TODO: wozu brauchen wir das �berhaupt?*/);
}


public Aktie(String name, String kurz, int wkn, int waehrung) {
	super(name,kurz);
	this.wkn = wkn;
	this.waehrung = waehrung;
}


public int getWKN() {
	return wkn;
}


public String getWKNString() {
	return "" + getWKN();
}


public Object getKey() {
	return new Integer(wkn);
}


public String toString() {
	return Aktienname.getKurzName(getName());
}

}
