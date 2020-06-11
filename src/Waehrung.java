/**
 @author Thomas Much
 @version 1998-09-07
*/

import java.io.*;



public class Waehrung extends Listeneintrag implements Serializable {

static final long serialVersionUID = 1985101300000L;

private int waehrung;



public Waehrung(String name, String kurz, int waehrung) {
	super(name,kurz);
	this.waehrung = waehrung;
}


public int getWaehrung() {
	return waehrung;
}


public String toString() {
	if (getKurz().length() == 0)
	{
		return getName();
	}
	else
	{
		return getKurz();
	}
}

}
