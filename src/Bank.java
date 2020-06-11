// 1998-09-15 tm



public class Bank extends Listeneintrag {



public Bank(String name) {
	super(name,"");
}


public long getGebuehren(long wert, boolean internet) {
	return (internet) ? getInternetGebuehren(wert) : getTelefonGebuehren(wert);
}


public long getTelefonGebuehren(long wert) {
	return 0L;
}


public long getInternetGebuehren(long wert) {
	return getTelefonGebuehren(wert);
}


public boolean hasInternetTrade() {
	return true;
}


public long getMaklerCourtage(long wert) {
	return (wert*8L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
}


public String getGebuehrenString(long wert, boolean internet) {
	long g = getGebuehren(wert,internet);
	
	return (g == 0L) ? "keine" : Waehrungen.getString(g,Waehrungen.DEM);
}


public String toString() {
	return getName();
}

}
