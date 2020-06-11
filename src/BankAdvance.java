/**
 @author Thomas Much
 @version 1999-01-15
*/



public final class BankAdvance extends Bank {



public BankAdvance() {
	super("Advance Bank");
}


public boolean hasInternetTrade() {
	return false;
}


public long getTelefonGebuehren(long wert) {
	long gebuehren;
	
	if (wert < Waehrungen.exchange(Waehrungen.PRECISION*8000L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung()))
	{
		gebuehren = Waehrungen.exchange(Waehrungen.PRECISION*40L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	}
	else
	{
		gebuehren = (wert*5L + Waehrungen.PRECISION*5L) / (Waehrungen.PRECISION*10L);
	}

	return gebuehren + getMaklerCourtage(wert);
}

}
