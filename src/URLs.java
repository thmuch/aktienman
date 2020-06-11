/**
 @author Thomas Much
 @version 1999-06-21
*/



public class URLs {

public static final String AMHOMEPAGE      = "http://www.aktienman.de";

public static final String URLCLASSURL     = AMHOMEPAGE + "/classes/";
public static final String URLCLASSNAME    = "NewURLs";

public static final String MC_WORKAROUND   = AMHOMEPAGE + "/info/index.html";

public static final int URL_AMUPDATE       =  1;
public static final int URL_AMDOWNLOAD     =  2;

public static final int URL_KAMERA         = 10;

public static final int URL_DAXREALTIME    = 20;
public static final int URL_CHARTINTRADAY  = 21;

public static final int URL_DAX30          = 31;
public static final int URL_DAX100         = 32;
public static final int URL_NMARKT         = 33;
public static final int URL_EURO50         = 34;
public static final int URL_AUSLAND        = 35;

public static final int URL_KURSECOMDIRECT = 40;

public static final int URL_KURSEDEUBA     = 50;
public static final int URL_CHARTDEUBA     = 51;


public static final int CHART_STANDARD     = -1;
public static final int CHART_LINIE        =  0;
public static final int CHART_BALKEN       =  1;
public static final int CHART_OPENHICLOSE  =  2;
public static final int CHART_CANDLESTICK  =  3;


public static final int STR_CD_KURSFEHLER  = 10;
public static final int STR_CD_KURS        = 11;
public static final int STR_CD_KURSZEIT    = 12;
public static final int STR_CD_KURSVOLUMEN = 13;
public static final int STR_CD_KURSENDE    = 14;
public static final int STR_CD_KURSTITEL   = 15;
public static final int STR_CD_KURSWKN     = 16;
public static final int STR_CD_KURSSYMBOL  = 17;
public static final int STR_CD_KURSVORTAG  = 18;
public static final int STR_CD_KURSEROEFF  = 19;
public static final int STR_CD_KURSHOECHST = 20;
public static final int STR_CD_KURSTIEFST  = 21;

public static final int STR_CD_CHARTS      = 30;
public static final int STR_CD_CHARTHREF   = 31;
public static final int STR_CD_CHARTIMAGE  = 32;
public static final int STR_CD_CHARTSRC    = 33;


protected static final int BASE_EXCHANGE   =  1;
protected static final int BASE_LISTEN     =  2;
protected static final int BASE_DEUBA      =  3;
protected static final int BASE_COMDIRECT  =  4;




public String getString(int strNr) {

	switch (strNr)
	{
	case STR_CD_KURSFEHLER:
		return "<title>Komfortsuche</title>";
	
	case STR_CD_KURS:
		return "Aktueller Kurs";

	case STR_CD_KURSZEIT:
		return "Kurszeit";

	case STR_CD_KURSVOLUMEN:
		return "Gehandelte St&uuml;ck";

	case STR_CD_KURSENDE:
		return "Java Trader";

	case STR_CD_KURSTITEL:
		return "<th ";
	
	case STR_CD_KURSWKN:
		return ">WKN<";
	
	case STR_CD_KURSSYMBOL:
		return ">Symbol<";

	case STR_CD_KURSVORTAG:
		return "Letzter bzw. Schlu&szlig;";
		
	case STR_CD_KURSEROEFF:
		return "Er&ouml;ffnungskurs";
		
	case STR_CD_KURSHOECHST:
		return "Tagesh&ouml;chstkurs";
		
	case STR_CD_KURSTIEFST:
		return "Tagestiefstkurs";
		
	case STR_CD_CHARTS:
		return "/charts/";

	case STR_CD_CHARTHREF:
		return " href=\"";
	
	case STR_CD_CHARTIMAGE:
		return "cdcharttcl";

	case STR_CD_CHARTSRC:
		return " src=\"";
	}
	
	return "";
}



protected String getBase(int bnr) {

	switch (bnr)
	{
	case BASE_EXCHANGE:
		return "http://www.exchange.de/";
	
	case BASE_LISTEN:
		return "http://bbbank.teledata.de:9056/bbbank/";
	
	case BASE_DEUBA:
		return "http://deuba.teledata.de:9030/deuba/";
	
	case BASE_COMDIRECT:
		return "http://informer2.comdirect.de:9004";
	}
	
	return "";
}



public String get(int urlNr) {

	switch (urlNr)
	{
	case URL_AMUPDATE:
		return AMHOMEPAGE + "/info/update.txt";
	
	case URL_AMDOWNLOAD:
		return AMHOMEPAGE + "/archive/";
	
	case URL_KAMERA:
		return getBase(BASE_EXCHANGE) + "parkett/parkett.jpg";
	
	case URL_DAXREALTIME:
		return getBase(BASE_EXCHANGE) + "realtime/dax_d.html";
	
	case URL_CHARTINTRADAY:
		return getBase(BASE_EXCHANGE) + "realtime/";
	
	case URL_DAX30:
		return getBase(BASE_LISTEN) + "KL_DAX30FFM.htm";

	case URL_DAX100:
		return getBase(BASE_LISTEN) + "KL_DAX100.htm";

	case URL_NMARKT:
		return getBase(BASE_LISTEN) + "KL_NeuerMarkt.htm";

	case URL_EURO50:
		return getBase(BASE_LISTEN) + "KL_EuroStoxx.htm";

	case URL_AUSLAND:
		return getBase(BASE_LISTEN) + "KL_Individuell.htm";

	case URL_KURSECOMDIRECT:
		return getBase(BASE_COMDIRECT) + "/de/suche/main.html?searchfor=";

	case URL_KURSEDEUBA:
		return getBase(BASE_DEUBA) + "search/searchswitch.html?searchfor=";
	
	case URL_CHARTDEUBA:
		return getBase(BASE_DEUBA) + "chart/chart";
	}

	return "";
}



public String checkWKN(String wkn) {

	if (wkn.equals("843002"))
	{
		return "MUV2";
	}

	return wkn;
}



public String getExchangeIntradayChartURL(String kuerzel, String boerse) {

	return get(URL_CHARTINTRADAY) + kuerzel + "." + boerse + ".EUR.gif";
}



public String getDeubaKursURL(String wkn, String boerse) {

	return get(URL_KURSEDEUBA) + checkWKN(wkn) + "." + boerse + "&searchforb=0&searchforicat=0&Search_ex=Pr%E4zise+suchen";
}



public String getComdirectKursURL(String wkn, String boerse) {

	return get(URL_KURSECOMDIRECT) + checkWKN(wkn) + "&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen=" + boerse;
}



protected String getComdirectChartType(int charttype) {

	switch (charttype)
	{
	case CHART_LINIE:
		return "1";

	case CHART_BALKEN:
		return "3";

	case CHART_CANDLESTICK:
		return "6";
	}
	
	return "4";
}



public String getComdirectChartURL(String rel, String monate, int charttype) {

	int i = rel.indexOf("&");
	
	if (i > 0)
	{
		rel = rel.substring(0,i);
	}
	
	return getBase(BASE_COMDIRECT) + rel + "&sRange=" + monate + "&charttype=" + getComdirectChartType(charttype) + "&dbrushwidth=1&gd1=38&gd2=200&" + /*benchmark=DAX.ETR&*/ "infos=3&indtype1=40&indtype2=0&volumen=2";
}



// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=USD&expr2=EUR&margin_fixed=0
// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=EUR&expr2=USD&margin_fixed=0

}
