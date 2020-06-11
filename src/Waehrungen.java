// 1998-10-11 tm

import java.text.*;



public class Waehrungen extends Aktienliste {

public static final int DEM = 0;
public static final int EUR = 1;
public static final int USD = 2;

public static final long PRECISION = 100L;

private static final long CONVPREC  = 1000000L;
private static final long CONVROUND = CONVPREC/2L;
private static final long DEM2EUR   =  509200L;
private static final long EUR2DEM   = 1963900L;

private static final int STANDARDWAEHRUNG = DEM;
private static final double DFAKTOR = (double)PRECISION;
private static NumberFormat geldform = NumberFormat.getCurrencyInstance();
private static final double WINFIX = 0.00001;
private static int aktuellesJahr = new ADate().getYear();



public synchronized void setupList() {
	add(new Waehrung("DM","DEM",DEM));
	// add(new Waehrung("Euro","EUR",EUR));
	// add(new Waehrung("US-$","USD",USD));
}


public synchronized Waehrung getAt(int index) {
	return (Waehrung)elementAt(index);
}


public synchronized Aktie getAktie(int index) {
	return null;
}


public static long doubleToLong(String s) {
	return doubleToLong(AktienMan.getDouble(s));
}


public static long doubleToLong(double d) {
	return Math.round(d*DFAKTOR);
}


public static double longToDouble(long l) {
	return (double)l/DFAKTOR;
}


private static double sgn(double d) {
	if (d > 0.0)
	{
		return 1.0;
	}
	else if (d < 0.0)
	{
		return -1.0;
	}
	else
	{
		return 0.0;
	}
}


public static String getString(long wert, int waehrung) {
	double d = longToDouble(wert);

	// waehrung auswerten

	return geldform.format(d + sgn(d)*WINFIX);
}


public static long exchange(long valFrom, int wFrom, int wTo) {
	if (wFrom == wTo)
	{
		return valFrom;
	}
	else
	{
		if (wFrom == DEM)
		{
			if (wTo == EUR)
			{
				return (valFrom * DEM2EUR + CONVROUND) / CONVPREC;
			}
			else
			{
				return 0L;
			}
		}
		else if (wFrom == EUR)
		{
			if (wTo == DEM)
			{
				return (valFrom * EUR2DEM + CONVROUND) / CONVPREC;
			}
			else
			{
				return 0L;
			}
		}
		else
		{
			return 0L;
		}
	}
}


public static int getOnlineWaehrung() {
	return (aktuellesJahr >= 1999) ? EUR : DEM;
}


public static int getStandardWaehrung() {
	int stdw = AktienMan.properties.getInt("Konfig.StdWaehrung");
	
	return ((stdw < 0) ? STANDARDWAEHRUNG : stdw);
}

}
