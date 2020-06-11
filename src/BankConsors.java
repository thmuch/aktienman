// 1998-09-16 tm



public class BankConsors extends Bank {



public BankConsors() {
	super("Consors");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren = Waehrungen.PRECISION*13L;
	
	if (wert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*21L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*200000L)
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
	long gebuehren = Waehrungen.PRECISION*9L;
	
	if (wert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*21L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*200000L)
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
