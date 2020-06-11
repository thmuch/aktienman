// 1998-09-16 tm



public class BankComdirect extends Bank {



public BankComdirect() {
	super("Comdirect");
}


public long getTelefonGebuehren(long wert) {
	long gebuehren;

	if (wert < Waehrungen.PRECISION*15000L)
	{
		gebuehren = (wert*49L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*30000L)
	{
		gebuehren = (wert*40L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*50001L)
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
	
	return gebuehren + getMaklerCourtage(wert);
}


public long getInternetGebuehren(long wert) {
	long gebuehren;
	
	if (wert < Waehrungen.PRECISION*15000L)
	{
		gebuehren = (wert*441L + Waehrungen.PRECISION*500L) / (Waehrungen.PRECISION*1000L);
	}
	else if (wert < Waehrungen.PRECISION*30000L)
	{
		gebuehren = (wert*36L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*50001L)
	{
		gebuehren = (wert*27L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}
	else if (wert < Waehrungen.PRECISION*150000L)
	{
		gebuehren = (wert*216L + Waehrungen.PRECISION*500L) / (Waehrungen.PRECISION*1000L);
	}
	else
	{
		gebuehren = (wert*9L + Waehrungen.PRECISION*50L) / (Waehrungen.PRECISION*100L);
	}

	return gebuehren + getMaklerCourtage(wert);
}

}
