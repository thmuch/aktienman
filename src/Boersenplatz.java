/**
 @author Thomas Much
 @version 1998-11-03
*/

import java.io.*;



public class Boersenplatz extends Listeneintrag implements Serializable {

static final long serialVersionUID = 1975101300000L;

private boolean fondsOnly = false;



public Boersenplatz(String name, String kurz) {
	this(name,kurz,false);
}


public Boersenplatz(String name, String kurz, boolean fondsOnly) {
	super(name,kurz);
	this.fondsOnly = fondsOnly;
}


public boolean isFondsOnly() {
	return fondsOnly;
}

}
