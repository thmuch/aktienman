/**
 @author Thomas Much
 @version 1999-07-19
*/

/**
 AktienMan Portfolio-Management-Software
 Copyright (c)1998,99 by Thomas Much (thomas@snailshell.de)
 Hauptprogramm (main)
*/

import java.awt.*;



public final class AktienMan {

public static final String AMNAME         = "AktienMan";
public static final String AMVERSION      = "1.50";
public static final String AMFENSTERTITEL = AMNAME + " - ";

public static ADate compDate              = new ADate(1999,7,19); /* Compilierdatum */
public static final int RELEASE           = 15; /* 1.50 19.07.1999 */
public static final boolean DEBUG         = false; /**/

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

public static URLs url = null;




public synchronized static void checkURLs() {
	if (url == null)
	{
		NetUtil.loadRawURL(URLs.MC_WORKAROUND);
		
		try
		{
			URLClassLoader loader = new URLClassLoader(URLs.URLCLASSURL);

			url = (URLs)loader.loadClass(URLs.URLCLASSNAME,true).newInstance();
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
}


private static void registerCheck() {}


private static void main(int a) throws Exception {
	/* #Demoversion */
	if ((new ADate().before(compDate)) && (!hauptdialog.main())) throw new Exception();
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
