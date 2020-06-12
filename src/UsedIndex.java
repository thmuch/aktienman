/**
 @author Thomas Much
 @version 2000-08-11
*/

import java.io.*;




public final class UsedIndex implements Serializable {

static final long serialVersionUID = 1972011800003L;

private int index;
private long punkte,vortag;
private String datum;




public UsedIndex(int index) {

	this(index,0L,0L,"");
}



public UsedIndex(int index, long punkte, long vortag, String datum) {

	this.index = index;
	
	setValues(punkte,vortag,datum);
}



public synchronized void setValues(long punkte, long vortag, String datum) {

	this.punkte = punkte;
	this.vortag = vortag;
	this.datum = datum.trim();
}



public int getIndex() {

	return index;
}



public synchronized long getPunkte() {

	return punkte;
}



public synchronized long getVortag() {

	return vortag;
}



public synchronized String getDatum() {

	return datum;
}

}
