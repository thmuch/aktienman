/**
 @author Thomas Much
 @version 2000-11-12
*/



public final class BankComdirect extends Bank {




public BankComdirect() {

	super("Comdirect");
}



public long getTelefonGebuehren(long wert) {

	long gebuehren = calculate(wert) + Waehrungen.exchange((35L*Waehrungen.PRECISION)/10L,Waehrungen.EUR,Waehrungen.getVerkaufsWaehrung());

/*	long gebuehren = calculate(wert);
	
	long minimum = Waehrungen.exchange(Waehrungen.PRECISION*20L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	
	if (gebuehren < minimum) gebuehren = minimum; */
	
	return gebuehren + getMaklerCourtage(wert);
}



public long getInternetGebuehren(long wert) {

	long gebuehren = calculate(wert);

/*	long gebuehren = (calculate(wert) * 9L + 5L) / 10L;

	long minimum = Waehrungen.exchange(Waehrungen.PRECISION*18L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());

	if (gebuehren < minimum) gebuehren = minimum; */

	return gebuehren + getMaklerCourtage(wert);
}



private long calculate(long wert) {

	long gebuehren;

	long eurwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.EUR);
	
	if (eurwert <= Waehrungen.PRECISION*5000L)
	{
		gebuehren = 9L;
	}
	else if (eurwert <= Waehrungen.PRECISION*10000L)
	{
		gebuehren = 18L;
	}
	else if (eurwert <= Waehrungen.PRECISION*15000L)
	{
		gebuehren = 36L;
	}
	else
	{
		gebuehren = 54L;
	}
	
	return Waehrungen.exchange(gebuehren*Waehrungen.PRECISION,Waehrungen.EUR,Waehrungen.getVerkaufsWaehrung());

/*	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);

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
	
	return gebuehren; */
}

}
