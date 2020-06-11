/**
 @author Thomas Much
 @version 1998-11-10
*/



public class BankComdirect extends Bank {



public BankComdirect() {
	super("Comdirect");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren = calculate(wert);
	
	if (gebuehren < Waehrungen.PRECISION*20L) gebuehren = Waehrungen.PRECISION*20L;
	
	return gebuehren + getMaklerCourtage(wert);
}


public long getInternetGebuehren(long wert) {
	long gebuehren = (calculate(wert) * 9L + 5L) / 10L;
	
	if (gebuehren < Waehrungen.PRECISION*18L) gebuehren = Waehrungen.PRECISION*18L;

	return gebuehren + getMaklerCourtage(wert);
}


private long calculate(long wert) {
	long gebuehren;

	if (wert < Waehrungen.PRECISION*15000L)
	{
		gebuehren = (wert*49L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*30000L)
	{
		gebuehren = (wert*40L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*50000L)
	{
		gebuehren = (wert*30L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*150000L)
	{
		gebuehren = (wert*24L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else
	{
		gebuehren = (wert*10L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	
	return gebuehren;
}

}
