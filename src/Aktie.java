/**
 @author Thomas Much
 @version 1998-10-27
*/

import java.io.*;



public class Aktie extends Listeneintrag implements Serializable {

static final long serialVersionUID = 1978090500000L;

private int wkn;
private int waehrung;



public Aktie(String name, String kurz, int wkn) {
	this(name,kurz,wkn,Waehrungen.DEM);
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
	return new Integer(getWKN()).toString();
}


public int getWaehrung() {
	return waehrung;
}


public Object getKey() {
	return new Integer(wkn);
}


public String toString() {
	return BenutzerAktie.getKurzName(getName());
}

}
