/**
 @author Thomas Much
 @version 1998-11-16
*/

/**
 AktienMan Portfolio-Management-Software
 Copyright (c)1998 by Thomas Much (thomas@snailshell.de)
 Hauptprogramm (main)
*/

import java.awt.*;



public class AktienMan {

public static final String AMNAME         = "AktienMan";
public static final String AMVERSION      = "0.92-pre";
public static final String AMFENSTERTITEL = AMNAME + " - ";

public static final char DEZSEPARATOR     = ',';

public static ADate compDate              = new ADate(1998,11,16); /* Compilierdatum */
public static final int PORTFOLIOVER      = 0;

public static Aktienliste listeDAX        = new Aktienliste();
public static Aktienliste listeMDAX       = new Aktienliste();
public static Aktienliste listeNMarkt     = new Aktienliste();
public static Aktienliste listeEuroSTOXX  = new Aktienliste();
public static Aktienliste listeAusland    = new Aktienliste();

public static Boersenliste boersenliste   = new Boersenliste();
public static Waehrungen waehrungen       = new Waehrungen();
public static Bankenliste bankenliste     = new Bankenliste();

public static AProperties properties = null;

public static Image daxImage = null;
public static DAXKamera daxKamera = null;
public static Konfiguration konfiguration = null;
public static NeueAktie neueaktie = null;
public static AktieLoeschen aktieloeschen = null;
public static AktieVerkaufen aktieverkaufen = null;
public static AktieAendern aktieaendern = null;
public static VerkaufserloesLoeschen erloesloeschen = null;
public static VerkaufserloesSetzen erloessetzen = null;
public static About about = null;

public static Hauptdialog hauptdialog;
public static Dimension screenSize;




public static boolean isMacOS() {
	return ((System.getProperty("java.vendor").indexOf("Apple") >= 0) || (System.getProperty("os.name").indexOf("Mac OS") >= 0));
}


public static boolean isLinux() {
	return (System.getProperty("os.name").indexOf("Linux") >= 0);
}


public static boolean isWindows() {
	return !(isMacOS() || isLinux());
}


public static String getFilenameConfig() {
	String s = AMNAME + ".cfg";

	if (isLinux())
	{
		s = "." + s.toLowerCase();
	}

	return s;
}


public static String getFilenameList() {
	String s = AMNAME + ".lst";

	if (isLinux())
	{
		s = "." + s.toLowerCase();
	}

	return s;
}


public static String getFilenamePopups() {
	String s = AMNAME + ".pop";

	if (isLinux())
	{
		s = "." + s.toLowerCase();
	}

	return s;
}


public static double getDouble(String str) throws NumberFormatException {
	return Double.valueOf(str.trim().replace(DEZSEPARATOR,'.')).doubleValue();
}


public static String getString(double d) {
	return new Double(d).toString().replace('.',DEZSEPARATOR);
}


public static String get00String(long l) {
	String s = getString(Waehrungen.longToDouble(l));
	int i = s.indexOf(DEZSEPARATOR);
	
	if (i < 0)
	{
		s = s + DEZSEPARATOR + "00";
	}
	else if (i == s.length()-1)
	{
		s = s + "00";
	}
	else if (i == s.length()-2)
	{
		s = s + "0";
	}
	
	return s;
}


private static void registerCheck() {
	/* falls nicht registriert: Dialog; sonst Abbruch */
}


private static void readConfig() throws Exception {
	ADate heute = new ADate();
	ADate morgen = new ADate(1998,11,30); /* Ablaufdatum */

	if ((heute.before(compDate)) || (heute.after(morgen))) throw new Exception();
}


public static void main(String args[]) {
	properties = new AProperties(getFilenameConfig(),AMNAME+" "+AMVERSION+" Konfigurationsdatei");
	
	AktienAktualisieren.loadPopups();

	registerCheck();

	hauptdialog = new Hauptdialog();
	
	try
	{
		readConfig();

		hauptdialog.show();

		if (properties.getBoolean("Konfig.Aktualisieren")) hauptdialog.listeAktualisieren();
		
		if (properties.getBoolean("Konfig.Kamera"))
		{
			hauptdialog.callKamera();
			hauptdialog.toFront();
		}
		
		hauptdialog.callAbout(); /* nur bei Preview/Demo */

		if ((listeDAX.getChoice().getItemCount() < 1) ||
			(listeMDAX.getChoice().getItemCount() < 1) ||
			(listeNMarkt.getChoice().getItemCount() < 1) ||
			(listeEuroSTOXX.getChoice().getItemCount() < 1) ||
			(listeAusland.getChoice().getItemCount() < 1))
		{
			new Warnalert(hauptdialog,"Bitte gehen Sie online und rufen dann den Men\u00fcpunkt|\"Aktienlisten aktualisieren\" im Men\u00fc \""+Lang.EDITMENUTITLE+"\" auf!");
		}
	}
	catch (Exception e)
	{
		System.out.println("Diese AktienMan-Preview-Version ist abgelaufen.");
		System.out.println("");
		System.out.println("Bitte besorgen Sie sich eine neue Demoversion,");
		System.out.println("die Sie dann auch registrieren k\u00f6nnen!");

		new Warnalert(null,"Diese AktienMan-Preview-Version ist abgelaufen.| |Bitte besorgen Sie sich eine neue Demoversion,|die Sie dann auch registrieren k\u00f6nnen!",true);
	}
}

}
