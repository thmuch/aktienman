/**
 @author Thomas Much
 @version 2003-02-26
*/



public final class Plugins {



private Plugins() {}



public static void reload() {

	if (AktienMan.DEBUG)
	{
		System.out.println("AktienMan-Plugins werden geladen...");
	}
	
	/* TODO: alle *.class-Dateien aus plugins/ laden */

	/* TODO: folgende Module nur laden, wenn die entsprechenden Klassen noch nicht dynamisch geladen wurden! */	

	/* Kursquellen */
	
	load("YahooDeKursQuelle");
	load("ComdirectKursQuelle");

	/* Chartquellen */

	load("YahooDeChartQuelle");
	load("ComdirectChartQuelle");

/*	if (LSRTDAX30Quelle.canUseLSRT())
	{
		add(new LSRTDAX30Quelle(LSRTDAX30Quelle.TYPE_BID));
		add(new LSRTDAX30Quelle(LSRTDAX30Quelle.TYPE_ASK));
	} TODO: durch YahooDeRTQuelle ersetzen */
}



private static void load(String className) {

	try
	{
		Class.forName(className);
	}
	catch (Exception e)
	{
		AktienMan.errlog("Fehler beim Laden des Plugins \"" + className + "\"", e);
	}
}


}
