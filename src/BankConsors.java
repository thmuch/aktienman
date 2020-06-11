/**
 @author Thomas Much
 @version 1999-01-15
*/



public final class BankConsors extends Bank {



public BankConsors() {
	super("Consors");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren = Waehrungen.exchange(Waehrungen.PRECISION*13L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());

	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);
	
	if (dmwert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*21L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*200000L)
	{
		gebuehren += (wert*15L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else
	{
		gebuehren += (wert*10L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}

	return gebuehren + getMaklerCourtage(wert);
}


public long getInternetGebuehren(long wert) {
	long gebuehren = Waehrungen.exchange(Waehrungen.PRECISION*9L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());

	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);
	
	if (dmwert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*21L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*200000L)
	{
		gebuehren += (wert*15L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else
	{
		gebuehren += (wert*10L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}

	return gebuehren + getMaklerCourtage(wert);
}

}
