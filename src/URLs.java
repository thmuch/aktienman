/**
 @author Thomas Much
 @version 1999-05-23
*/


public class URLs {

public static final String AMHOMEPAGE      = "http://www.aktienman.de";

public static final String URLCLASSURL     = AMHOMEPAGE + "/classes/";
public static final String URLCLASSNAME    = "NewURLs";

public static final int URL_AMUPDATE       =  1;
public static final int URL_AMDOWNLOAD     =  2;

public static final int URL_KAMERA         = 10;

public static final int URL_DAXREALTIME    = 20;
public static final int URL_CHARTINTRADAY  = 21;

public static final int URL_POPUPLISTEN    = 30;
public static final int LISTE_DAX30        = 31;
public static final int LISTE_DAX100       = 32;
public static final int LISTE_NMARKT       = 33;
public static final int LISTE_EURO50       = 34;
public static final int LISTE_AUSLAND      = 35;

public static final int URL_KURSECOMDIRECT = 40;
public static final int URL_CHARTCOMDIRECT = 41;

public static final int URL_KURSEDEUBA     = 50;



public String get(int urlNr) {

	switch (urlNr)
	{
	case URL_AMUPDATE:
		return AMHOMEPAGE + "/info/update.txt";
	
	case URL_AMDOWNLOAD:
		return AMHOMEPAGE + "/archive/";
	
	case URL_KAMERA:
		return "http://www.exchange.de/parkett/parkett.jpg";
	
	case URL_DAXREALTIME:
		return "http://www.exchange.de/realtime/dax_d.html";
	
	case URL_CHARTINTRADAY:
		return "http://www.exchange.de/realtime/";
	
	case URL_POPUPLISTEN:
		return "http://informer2e.teledata.de:9004/cd/";

	case LISTE_DAX30:
		return "dax30_print.html";

	case LISTE_DAX100:
		return "dax100_print.html";

	case LISTE_NMARKT:
		return "neuer-markt_print.html";

	case LISTE_EURO50:
		return "euro50_print.html";

	case LISTE_AUSLAND:
		return "ausland_print.html";

	case URL_KURSECOMDIRECT:
		return COMDIRECT_BASE + "suchen_erg?searchfor=";

	case URL_CHARTCOMDIRECT:
		return COMDIRECT_BASE + "chart";

	case URL_KURSEDEUBA:
		return "http://deuba.teledata.de:9030/deuba/search/searchswitch.html?searchfor=";

	default:
		return "";
	}
}



private static final String COMDIRECT_BASE = "http://cdwt.teledata.de:9004/cd/";

// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=USD&expr2=EUR&margin_fixed=0
// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=EUR&expr2=USD&margin_fixed=0

}
