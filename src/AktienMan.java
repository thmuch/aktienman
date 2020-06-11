/**
 @author Thomas Much
 @version 1999-03-02
*/

/**
 AktienMan Portfolio-Management-Software
 Copyright (c)1998,99 by Thomas Much (thomas@snailshell.de)
 Hauptprogramm (main)
*/

import java.awt.*;



public final class AktienMan {

public static final String AMNAME         = "AktienMan";
public static final String AMVERSION      = "1.23 (Euro MP)";
public static final String AMFENSTERTITEL = AMNAME + " - ";

public static final char DEZSEPARATOR     = ',';

public static ADate compDate              = new ADate(1999,3,2); /* Compilierdatum */
public static final int RELEASE           = 8; /* 1.23 02.03.99 */
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
public static PortfolioLoeschen portfolioloeschen = null;
public static PortfolioNeu portfolioneu = null;
public static PortfolioUmbenennen portfolioumbenennen = null;
public static About about = null;

public static Hauptdialog hauptdialog = null;
public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();



public static double getDouble(String str) throws NumberFormatException {
	return Double.valueOf(str.trim().replace(DEZSEPARATOR,'.')).doubleValue();
}


public static String getString(double d) {
	return new Double(d).toString().replace('.',DEZSEPARATOR);
}


public static void doOnlineChecks() {
	UpdateChecker.check();
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


private static void registerCheck() {}


private static void main(int a) throws Exception {
	ADate heute = new ADate();
	ADate morgen = new ADate(1999,5,8); /* #Ablaufdatum */

	/* #Demoversion */
	if ((heute.before(compDate) || heute.after(morgen)) && (!hauptdialog.main())) throw new Exception();
}


public static void main(String a) {
	try
	{
		main(0);

		hauptdialog.show();

		if (properties.getBoolean("Konfig.Aktualisieren")) hauptdialog.listeAktualisieren();

		if (properties.getBoolean("Konfig.KursTimeout")) KursDemon.createKursDemon();
		
		if (properties.getBoolean("Konfig.Kamera"))
		{
			hauptdialog.callKamera();
			hauptdialog.toFront();
		}
	}
	catch (Exception e)
	{
		System.out.println("Diese AktienMan-Demo-Version ist abgelaufen.");
		System.out.println("");
		System.out.println("Bitte besorgen Sie sich eine neue Demoversion");
		System.out.println("oder registrieren Sie diese Version!");

		new Warnalert(null,"Diese AktienMan-Demo-Version ist abgelaufen.| |Bitte besorgen Sie sich eine neue Demoversion|oder registrieren Sie diesen Version!",true);
	}
}


public static void main(String args[]) {
	StartupDialog sd = new StartupDialog();
	
	FileUtil.createAMDirectory();
	
	properties = new AProperties();
	
	AktienAktualisieren.loadPopups();

	registerCheck();

	hauptdialog = new Hauptdialog();
	
	if (sd != null) sd.dispose();
	
	if (!hauptdialog.main())
	{
		/* #Demoversion */
		new RegAM();
	}
	else
	{
		main("");
	}
}

}
