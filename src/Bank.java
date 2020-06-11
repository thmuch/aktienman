/**
 @author Thomas Much
 @version 1999-01-03
*/



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
	long courtage = (wert*8L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);

	long minimum = Waehrungen.exchange((Waehrungen.PRECISION * 3L) / 2L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	
	return (courtage <= minimum) ? minimum : courtage;
}


public String getGebuehrenString(long wert, boolean internet) {
	long g = getGebuehren(wert,internet);
	
	return (g == 0L) ? "keine" : Waehrungen.getString(g,Waehrungen.getVerkaufsWaehrung());
}


public String toString() {
	return getName();
}

}
