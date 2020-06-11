// 1998-09-15 tm



public class BankAdvance extends Bank {



public BankAdvance() {
	super("Advance Bank");
}


public boolean hasInternetTrade() {
	return false;
}


public long getTelefonGebuehren(long wert) {
	long gebuehren;
	
	if (wert < Waehrungen.PRECISION*8000L)
	{
		gebuehren = Waehrungen.PRECISION*40L;
	}
	else
	{
		gebuehren = (wert*5L + Waehrungen.PRECISION*5L) / (Waehrungen.PRECISION*10L);
	}

	return gebuehren + getMaklerCourtage(wert);
}

}
