/**
 @author Thomas Much
 @version 1999-01-15
*/



public final class Bank24 extends Bank {



public Bank24() {
	super("Bank24");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren = Waehrungen.exchange(Waehrungen.PRECISION*20L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	
	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);
	
	if (dmwert < Waehrungen.PRECISION*25001L)
	{
		gebuehren += (wert*42L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*50001L)
	{
		gebuehren += (wert*40L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*75001L)
	{
		gebuehren += (wert*30L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*20L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*250001L)
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
	long gebuehren = Waehrungen.exchange(Waehrungen.PRECISION*20L,Waehrungen.DEM,Waehrungen.getVerkaufsWaehrung());
	
	long dmwert = Waehrungen.exchange(wert,Waehrungen.getVerkaufsWaehrung(),Waehrungen.DEM);
	
	if (dmwert < Waehrungen.PRECISION*15000L)
	{
		gebuehren += (wert*294L + Waehrungen.PRECISION*500L) / (Waehrungen.PRECISION*1000L);
	}
	else if (dmwert < Waehrungen.PRECISION*50001L)
	{
		gebuehren += (wert*28L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*75001L)
	{
		gebuehren += (wert*21L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*100000L)
	{
		gebuehren += (wert*14L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (dmwert < Waehrungen.PRECISION*250001L)
	{
		gebuehren += (wert*105L + Waehrungen.PRECISION*500L) / (Waehrungen.PRECISION*1000L);
	}
	else
	{
		gebuehren += (wert*7L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}

	return gebuehren + getMaklerCourtage(wert);
}

}
