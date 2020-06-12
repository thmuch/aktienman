/**
 @author Thomas Much
 @version 1999-12-12
*/



public class URLs {

public static final String AMHOMEPAGE        = "http://www.aktienman.de";

public static final String URLCLASSURL       = AMHOMEPAGE + "/classes/";
public static final String URLCLASSNAME      = "NewURLs";

public static final String MC_WORKAROUND     = AMHOMEPAGE + "/info/index.html";

public static final int URL_AMUPDATE         =  1;
public static final int URL_AMDOWNLOAD       =  2;

public static final int URL_KAMERA           = 10;

public static final int URL_DAXREALTIME      = 20;
public static final int URL_CHARTINTRADAY    = 21;

public static final int URL_DAX30            = 31;
public static final int URL_DAX100           = 32;
public static final int URL_NMARKT           = 33;
public static final int URL_EURO50           = 34;
public static final int URL_AUSLAND          = 35;

public static final int URL_KURSECOMDIRECT   = 40;

public static final int URL_KURSEDEUBA       = 50;
public static final int URL_CHARTDEUBA       = 51;

public static final int URL_INDEXBBBANK      = 60;


public static final int CHART_STANDARD       = -1;
public static final int CHART_LINIE          =  0;
public static final int CHART_BALKEN         =  1;
public static final int CHART_OPENHICLOSE    =  2;
public static final int CHART_CANDLESTICK    =  3;

public static final int CHART_INTRA          =  0;
public static final int CHART_3              =  1;
public static final int CHART_6              =  2;
public static final int CHART_12             =  3;
public static final int CHART_24             =  4;
public static final int CHART_36             =  5;

public static final int CHART_NONE           = -1;
public static final int CHART_COUNT          =  6;


public static final int STR_CD_KURSFEHLER    = 10;
public static final int STR_CD_KURS          = 11;
public static final int STR_CD_KURSZEIT      = 12;
public static final int STR_CD_KURSVOLUMEN   = 13;
public static final int STR_CD_KURSENDE      = 14;
public static final int STR_CD_KURSTITEL     = 15;
public static final int STR_CD_KURSWKN       = 16;
public static final int STR_CD_KURSSYMBOL    = 17;
public static final int STR_CD_KURSVORTAG    = 18;
public static final int STR_CD_KURSEROEFF    = 19;
public static final int STR_CD_KURSHOECHST   = 20;
public static final int STR_CD_KURSTIEFST    = 21;

public static final int STR_CD_CHARTS        = 30;
public static final int STR_CD_CHARTHREF     = 31;
public static final int STR_CD_CHARTIMAGE    = 32;
public static final int STR_CD_CHARTSRC      = 33;

public static final int STR_DEUBA_KURSFEHLER = 40;
public static final int STR_DEUBA_KURSTITEL  = 41;
public static final int STR_DEUBA_KURSENDE   = 42;
public static final int STR_DEUBA_KURSZEILE  = 43;
public static final int STR_DEUBA_KURSSYMBOL = 44;

public static final int STR_DEUBA_CHARTS     = 50;
public static final int STR_DEUBA_CHARTHREF  = 51;
public static final int STR_DEUBA_CHARTIMAGE = 52;
public static final int STR_DEUBA_CHARTSRC   = 53;

public static final int STR_BBB_INDEXTITLE   = 60;
public static final int STR_BBB_INDEXSET     = 61;
public static final int STR_BBB_INDEXENDE    = 62;
public static final int STR_BBB_INDEXVALUE   = 63;


protected static final int BASE_EXCHANGE     =  1;
protected static final int BASE_BBBANK       =  2;
protected static final int BASE_DEUBA        =  3;
protected static final int BASE_COMDIRECT    =  4;




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
	
	case STR_DEUBA_KURSFEHLER:
		return " keine Ergebnisse ";
	
	case STR_DEUBA_KURSTITEL:
		return ".html?";

	case STR_DEUBA_KURSENDE:
		return "</TR>";
		
	case STR_DEUBA_KURSZEILE:
		return "<TD";

	case STR_DEUBA_KURSSYMBOL:
		return "&showi=";

	case STR_DEUBA_CHARTS:
		return ".html?";

	case STR_DEUBA_CHARTHREF:
		return " href=\"";

	case STR_DEUBA_CHARTIMAGE:
		return " usemap=\'";

	case STR_DEUBA_CHARTSRC:
		return " src=\'";
	
	case STR_BBB_INDEXTITLE:
		return "\"KA_Charts.htm?";

	case STR_BBB_INDEXSET:
		return "set+";

	case STR_BBB_INDEXENDE:
		return "</tr>";

	case STR_BBB_INDEXVALUE:
		return "\"2\">";
	}
	
	return "";
}



protected String getBase(int bnr) {

	switch (bnr)
	{
	case BASE_EXCHANGE:
		return "http://www.exchange.de/";
	
	case BASE_BBBANK:
		return "http://bbbank.teledata.de:9056/bbbank/";
	
	case BASE_DEUBA:
		return "http://deuba.teledata.de:9030";
	
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
		return getBase(BASE_BBBANK) + "KL_DAX30FFM.htm";

	case URL_DAX100:
		return getBase(BASE_BBBANK) + "KL_DAX100.htm";

	case URL_NMARKT:
		return getBase(BASE_BBBANK) + "KL_NeuerMarkt.htm";

	case URL_EURO50:
		return getBase(BASE_BBBANK) + "KL_EuroStoxx.htm";

	case URL_AUSLAND:
		return getBase(BASE_BBBANK) + "KL_Individuell.htm";

	case URL_KURSECOMDIRECT:
		return getBase(BASE_COMDIRECT) + "/de/suche/main.html?searchfor=";

	case URL_KURSEDEUBA:
		return getBase(BASE_DEUBA) + "/deuba/search/searchswitch.html?searchfor=";
	
	case URL_CHARTDEUBA:
		return getBase(BASE_DEUBA) + "/db/";

	case URL_INDEXBBBANK:
		return getBase(BASE_BBBANK) + "KL_Indices.htm";
	}

	return "";
}



public String checkWKN(String wkn) {

	if (wkn.equals("843002"))
	{
		return "MUV2";
	}
	else if (wkn.equals("710000"))
	{
		return "DCX";
	}
	else if (wkn.equals("750000"))
	{
		return "TKA";
	}
	else if (wkn.equals("519000"))
	{
		return "BMW";
	}
	else if (wkn.equals("519003"))
	{
		return "BMW3";
	}
	else if (wkn.equals("703703"))
	{
		return "RWE3";
	}
	else if (wkn.equals("695200"))
	{
		return "PRS";
	}
	else if (wkn.equals("850000"))
	{
		return "GMC";
	}
	else if (wkn.equals("871111"))
	{
		return "SSY";
	}
	else if (wkn.equals("920578"))
	{
		return "LYLX";
	}
	else if (wkn.equals("920566"))
	{
		return "LYLK";
	}
	else if (wkn.equals("849084"))
	{
		return "DE0008490848";
	}

	return wkn;
}



public String getExchangeIntradayChartURL(String kuerzel, String boerse) {

	return get(URL_CHARTINTRADAY) + kuerzel + "." + boerse + ".EUR.gif";
}



public String getDeubaKursURL(String wkn, String boerse) {

	return get(URL_KURSEDEUBA) + checkWKN(wkn) + "." + boerse + "&searchforb=0&searchforicat=0&Search_ex=Pr%E4zise+suchen";
}



public String getDeubaChartMonths(int type) {

	switch (type)
	{
	case CHART_INTRA:
		return "i";

	case CHART_3:
		return "3";

	case CHART_6:
		return "6";

	case CHART_12:
		return "12";
	}
	
	return "24";
}



public String getDeubaChartURL(String rel, int type) {

	while ((rel.length() > 0) && ((rel.charAt(0) == '.') || (rel.charAt(0) == '/')))
	{
		rel = rel.substring(1);
	}
	
	int i  = rel.indexOf("timespan=");
	int i2 = rel.indexOf("&",i);
	
	if ((i > 0) && (i2 > i))
	{
		rel = rel.substring(0,i+9) + getDeubaChartMonths(type) + rel.substring(i2);
	}

	return getBase(BASE_DEUBA) + "/" + rel;
}



public String getComdirectKursURL(String wkn, String boerse) {

	String wkneu = checkWKN(wkn);
	
	if (!wkneu.equals(wkn))
	{
		wkneu += "." + boerse;
	}

	return get(URL_KURSECOMDIRECT) + wkneu + "&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen=" + boerse;
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



public String getComdirectChartMonths(int type) {

	switch (type)
	{
	case CHART_INTRA:
		return "1";

	case CHART_3:
		return "2";

	case CHART_6:
		return "3";

	case CHART_12:
		return "4";
	}
	
	return "5";
}



public String getComdirectChartURL(String rel, int type, int charttype) {

	int i = rel.indexOf("&");
	
	if (i > 0)
	{
		rel = rel.substring(0,i);
	}
	
	return getBase(BASE_COMDIRECT) + rel + "&sRange=" + getComdirectChartMonths(type) + "&charttype=" + getComdirectChartType(charttype) + "&dbrushwidth=1&gd1=38&gd2=200&" + /*benchmark=DAX.ETR&*/ "infos=3&indtype1=40&indtype2=0&volumen=2";
}



public boolean isValidNr(long nr) {

	return true;
}


// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=USD&expr2=EUR&margin_fixed=0
// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=EUR&expr2=USD&margin_fixed=0

}
