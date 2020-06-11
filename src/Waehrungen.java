/**
 @author Thomas Much
 @version 1999-01-15
*/



public final class Waehrungen extends Aktienliste {

public static final int NONE = -1;
public static final int DEM  =  0;
public static final int EUR  =  1;
// public static final int USD = 2;

public static final long PRECISION = 100L;

private static final long CONVPREC  = 1000000L;
private static final long CONVROUND = CONVPREC/2L;
private static final long DEM2EUR   =  511292L;
private static final long EUR2DEM   = 1955830L;

private static final double DFAKTOR = (double)PRECISION;
// private static final double WINFIX = 0.00001;

private static int listenWaehrung = NONE;



public synchronized void setupList() {
	add(new Waehrung("DM","DEM",DEM));
	add(new Waehrung("Euro","EUR",EUR));
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


private static long sgn(long l) {
	if (l > 0L)
	{
		return 1L;
	}
	else if (l < 0L)
	{
		return -1L;
	}
	else
	{
		return 0L;
	}
}


public static String getKuerzel(int waehrung) {
	if (waehrung == EUR)
	{
		/* return "\u20ac"; MacOS 8.5, Win98 */
		return "EUR";
	}
	else if (waehrung == DEM)
	{
		return "DM";
	}
	else
	{
		return "";
	}
}


public static String getString(long wert, int waehrung) {
	return AktienMan.get00String(wert) + " " + getKuerzel(waehrung);
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
				return (valFrom * DEM2EUR + sgn(valFrom) * CONVROUND) / CONVPREC;
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
				return (valFrom * EUR2DEM + sgn(valFrom) * CONVROUND) / CONVPREC;
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


public synchronized static int getOnlineWaehrung() {
	return EUR;
}


public synchronized static int getStandardKaufwaehrung() {
	return AktienMan.properties.getInt("Konfig.StdWaehrung",EUR);
}


public synchronized static int getVerkaufsWaehrung() {
	return getListenWaehrung();
}


public synchronized static int getListenWaehrung() {
	if (listenWaehrung <= NONE)
	{
		listenWaehrung = AktienMan.properties.getInt("Konfig.Listenwaehrung",EUR);
	}
	
	return listenWaehrung;
}


public synchronized static void setListenWaehrung(int neu) {
	AktienMan.properties.setInt("Konfig.Listenwaehrung",neu);
	listenWaehrung = neu;
}

}
