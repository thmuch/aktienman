/**
 @author Thomas Much
 @version 2003-04-09
*/

/**
 AktienMan Portfolio-Management-Software
 Copyright (c)1998-2003 by Thomas Much (thomas@snailshell.de)
 Hauptprogramm (main)
*/

import java.awt.*;



public final class AktienMan {

public static final String AMNAME          = "AktienMan";
public static final String AMVERSION       = "1.99";
public static final String AMFENSTERTITEL  = AMNAME + " - ";
public static final String HOMEPAGE        = "http://www.aktienman.de/";

public static final ADate compDate         = new ADate(2003,4,9); /* Compilierdatum */
public static final int RELEASE            = 23; /* 1.98 (2002-10-10) */
public static final boolean DEBUG          = true; /**/

public static Aktienliste listeDAX30       = new Aktienliste();
public static Aktienliste listeMDAX        = new Aktienliste();
public static Aktienliste listeTecDAX      = new Aktienliste();
public static Aktienliste listeEuroSTOXX50 = new Aktienliste();
public static Aktienliste listeSTOXX50     = new Aktienliste();

public static Boersenliste boersenliste    = new Boersenliste();
public static Bankenliste bankenliste      = new Bankenliste();

public static AProperties properties = null;

public static DAXKamera daxKamera = null;
public static Konfiguration konfiguration = null;
public static KonfigurationWarnungen konfigurationWarnungen = null;
public static KonfigurationIndizes konfigurationIndizes = null;
public static NeueAktie neueaktie = null;
public static AktieLoeschen aktieloeschen = null;
public static AktieSplitten aktiesplitten = null;
public static AktieVerkaufen aktieverkaufen = null;
public static AktieAendern aktieaendern = null;
public static VerkaufserloesLoeschen erloesloeschen = null;
public static VerkaufserloesSetzen erloessetzen = null;
public static PortfolioLoeschen portfolioloeschen = null;
public static PortfolioNeu portfolioneu = null;
public static PortfolioUmbenennen portfolioumbenennen = null;
public static PortfolioCopyKaufkurs portfoliocopykaufkurs = null;
public static Wechselkurse wechselkurse = null;
public static About about = null;

public static Hauptdialog hauptdialog = null;
public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

public static URLs url = null;

private static TextWarnalert peng = null;

private static final Object errlock = new Object();




public synchronized static void checkURLs() {

	if (url == null)
	{
		NetUtil.loadRawURL(HOMEPAGE+"update/workaround",false,100);

		try
		{
			URLClassLoader loader = new URLClassLoader(HOMEPAGE+"classes/");

			url = (URLs)loader.loadClass("NewURLs",true).newInstance();
		}
		catch (Exception e) {}
		finally
		{
			if (url == null)
			{
				url = new URLs();
			}
		}
	}
}



public static void doOnlineChecks() {

	UpdateChecker.check();
	Waehrungen.check(false);
}



private static void registerCheck() {}



public static void errlog(String msg, Exception e) {

	synchronized(errlock)
	{
		System.err.print("[AKTIENMAN] " + msg);
		
		if (e != null)
		{
			System.err.println(":");
			e.printStackTrace(System.err);
		}

		System.err.println();
	}
}



private static void main(int a) throws Exception {
	/* #Demoversion */
	if ((new ADate().before(compDate)) && (!hauptdialog.main())) throw new Exception();
}



public static void main(String a) {

	try
	{
		main(0);

		hauptdialog.setVisible(true);

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

		new TextWarnalert(null,"Diese AktienMan-Demo-Version ist abgelaufen.| |Bitte besorgen Sie sich eine neue Demoversion|oder registrieren Sie diesen Version!",true);
	}
}



public static synchronized void main(boolean b) {

	if (peng == null)
	{
		peng = new TextWarnalert(null,"Ihre AktienMan-Registrierung ist ung\u00fcltig!|Bitte kontaktieren Sie den Support (support@aktienman.de).",b|!b);
	}
}



public static void main(String args[]) {

	StartupDialog sd = new StartupDialog();
	
	FileUtil.createAMDirectory();
	
	properties = new AProperties();
	
	Plugins.reload();
	
	AktienAktualisieren.loadPopups();

	registerCheck();

	hauptdialog = new Hauptdialog();
	
	if (sd != null)
	{
		sd.setVisible(false);
		sd.dispose();
	}
	
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
