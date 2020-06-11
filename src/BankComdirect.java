/**
 @author Thomas Much
 @version 1999-01-03
*/



public class BankComdirect extends Bank {



public BankComdirect() {
	super("Comdirect");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren = calculate(wert);
	
	long minimum = Waehrungen.exchange(Waehrungen.PRECISION*20L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	
	if (gebuehren < minimum) gebuehren = minimum;
	
	return gebuehren + getMaklerCourtage(wert);
}


public long getInternetGebuehren(long wert) {
	long gebuehren = (calculate(wert) * 9L + 5L) / 10L;

	long minimum = Waehrungen.exchange(Waehrungen.PRECISION*18L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());

	if (gebuehren < minimum) gebuehren = minimum;

	return gebuehren + getMaklerCourtage(wert);
}


private long calculate(long wert) {
	long gebuehren;

	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);

	if (dmwert < Waehrungen.PRECISION*15000L)
	{
		gebuehren = (wert*49L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*30000L)
	{
		gebuehren = (wert*40L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*50000L)
	{
		gebuehren = (wert*30L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*150000L)
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
