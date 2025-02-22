/**
 @author Thomas Much
 @version 2003-04-03
*/



public class URLs {

public static final String AMHOMEPAGE        = "http://www.aktienman.de";

//public static final String URLCLASSURL       = AMHOMEPAGE + "/classes/";
//public static final String URLCLASSNAME      = "NewURLs";
//public static final String MC_WORKAROUND     = AMHOMEPAGE + "/info/index.html";

public static final int URL_AMUPDATE         =  1;
public static final int URL_AMDOWNLOAD       =  2;

public static final int URL_KAMERA           = 10;
public static final int URL_KAMERADJI        = 11;
public static final int URL_KAMERANASDAQ     = 12;
public static final int URL_KAMERASP500      = 13;

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

/*public static final int URL_BBBINDEXD        = 61;
public static final int URL_BBBINDEXEU       = 62;
public static final int URL_BBBINDEXUS       = 63;
public static final int URL_BBBINDEXASIA     = 64;  TODO */

public static final int URL_LSDAX30REALTIME  = 70;


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
public static final int CHART_60             =  6;
public static final int CHART_120            =  7;

public static final int CHART_COUNT          =  8;
public static final int CHART_NONE           = -1;


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
public static final int STR_CD_FONDSKURS     = 22;
public static final int STR_CD_KURSDFK       = 29;

public static final int STR_CD_LISTEWKNLI    = 23;
public static final int STR_CD_LISTEWKNRE    = 24;
public static final int STR_CD_LISTERESET    = 25;
public static final int STR_CD_LISTEQUOTE    = 26;
public static final int STR_CD_LISTEBOERSELI = 27;
public static final int STR_CD_LISTEBOERSERE = 28;

public static final int STR_CD_CHARTS        = 30;
public static final int STR_CD_CHARTIMAGE    = 32;
public static final int STR_CD_CHARTSRC      = 33;
public static final int STR_CD_CHARTREPLACE  = 34;

public static final int STR_DEUBA_KURSFEHLER = 40;
public static final int STR_DEUBA_KURSTITEL  = 41;
public static final int STR_DEUBA_KURSENDE   = 42;
public static final int STR_DEUBA_KURSZEILE  = 43;
public static final int STR_DEUBA_KURSSYMBOL = 44;

public static final int STR_DEUBA_CHARTS     = 50;
public static final int STR_DEUBA_CHARTIMAGE = 52;
public static final int STR_DEUBA_CHARTSRC   = 53;

/*public static final int STR_BBB_INDEXTITLE   = 60;
public static final int STR_BBB_INDEXSET     = 61;
public static final int STR_BBB_INDEXENDE    = 62;*/

public static final int STR_LSDAX_KURSANFANG = 70;
public static final int STR_LSDAX_KURSENDE   = 71;
public static final int STR_LSDAX_KURSVALID  = 72;
public static final int STR_LSDAX_JSKANFANG  = 73;
public static final int STR_LSDAX_JSKENDE    = 74;
public static final int STR_LSDAX_JSKVALID   = 75;

/*public static final int STR_INDEX_DAX30      = 80;
public static final int STR_INDEX_NEMAX50    = 81;
public static final int STR_INDEX_DOWINDUST  = 82;
public static final int STR_INDEX_SP500      = 83;
public static final int STR_INDEX_NASDAQ100  = 84;
public static final int STR_INDEX_NIKKEI225  = 85;
public static final int STR_INDEX_DAX100     = 86;
public static final int STR_INDEX_MDAX       = 87;
public static final int STR_INDEX_SDAX       = 88;
public static final int STR_INDEX_EURSTOXX50 = 89;
public static final int STR_INDEX_STOXX50    = 90;
public static final int STR_INDEX_NYSECOMP   = 91; TODO */

public static final int NUM_LSDAX_NAMELEN    = 10;

public static final int NUM_ICCOM_PIXWIDTH   = 20;
public static final int NUM_ICCOM_PIXHEIGHT  = 21;
public static final int NUM_ICCOM_YOFFSET    = 22;
public static final int NUM_ICCOM_YEND       = 23;
public static final int NUM_ICCOM_XOFFSET    = 24;
public static final int NUM_ICCOM_XEND       = 25;

public static final int NUM_ICDB_PIXWIDTH    = 26;
public static final int NUM_ICDB_PIXHEIGHT   = 27;
public static final int NUM_ICDB_YOFFSET     = 28;
public static final int NUM_ICDB_YEND        = 29;
public static final int NUM_ICDB_XOFFSET     = 30;
public static final int NUM_ICDB_XEND        = 31;

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
		return "Aktuell<";
	
	case STR_CD_FONDSKURS:
		return "R&uuml;cknahmepreis<";

	case STR_CD_KURSDFK:
		return "Deutsche Fonds";

	case STR_CD_KURSZEIT:
		return "<td align";

	case STR_CD_KURSVOLUMEN:
		return "<tr align=right class='bcolorYellowB'>";

	case STR_CD_KURSENDE:
		return "Impressum";

	case STR_CD_KURSTITEL:
		return "<th>";
	
	case STR_CD_KURSWKN:
		return "WKN:";
	
	case STR_CD_KURSSYMBOL:
		return "Symbol:";

	case STR_CD_KURSVORTAG:
		return "Schluss Vortag";
		
	case STR_CD_KURSEROEFF:
		return "Er&ouml;ffnung";
		
	case STR_CD_KURSHOECHST:
		return "Hoch";
		
	case STR_CD_KURSTIEFST:
		return "Tief";
	
	case STR_CD_LISTEWKNLI:
		return ">";

	case STR_CD_LISTEWKNRE:
		return "<";

	case STR_CD_LISTERESET:
		return "</tr>";

	case STR_CD_LISTEQUOTE:
		return "/fokus/";

	case STR_CD_LISTEBOERSELI:
		return ".";

	case STR_CD_LISTEBOERSERE:
		return "";

	case STR_CD_CHARTS:
		return "/fokus/";

	case STR_CD_CHARTREPLACE:
		return "/charts/";
	
	case STR_CD_CHARTIMAGE:
		return "cdchart";

	case STR_CD_CHARTSRC:
		return " src='";
	
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

	case STR_DEUBA_CHARTIMAGE:
		return " usemap=\'";

	case STR_DEUBA_CHARTSRC:
		return " src=\'";
	
/*	case STR_BBB_INDEXTITLE:
		return "chart.html?symm=";

	case STR_BBB_INDEXSET:
		return "<b>Name</b>";

	case STR_BBB_INDEXENDE:
		return "Bitte beachten"; */

	case STR_LSDAX_KURSANFANG:
		return "<PRE>";

	case STR_LSDAX_KURSENDE:
		return "</PRE>";
	
	case STR_LSDAX_KURSVALID:
		return "<a href=\"javascript:";

	case STR_LSDAX_JSKANFANG:
		return "tab = new Array()";

	case STR_LSDAX_JSKENDE:
		return "</script>";
	
	case STR_LSDAX_JSKVALID:
		return "new Array(";
	
/*	case STR_INDEX_DAX30:
		return "DAX.ETR";
	
	case STR_INDEX_NEMAX50:
		return "NMPX.ETR";
	
	case STR_INDEX_DOWINDUST:
		return "INDU.IND";
	
	case STR_INDEX_SP500:
		return "INX.IND";
	
	case STR_INDEX_NASDAQ100:
		return "NDX.X.IND";
	
	case STR_INDEX_NIKKEI225:
		return "NIKKEI225.TWI";
		
	case STR_INDEX_DAX100:
		return "HDAX.ETR";
		
	case STR_INDEX_MDAX:
		return "MDAX.ETR";
		
	case STR_INDEX_SDAX:
		return "SDXP.ETR";
		
	case STR_INDEX_EURSTOXX50:
		return "SX5T.DJX";
		
	case STR_INDEX_STOXX50:
		return "SX5P.DJX";
		
	case STR_INDEX_NYSECOMP:
		return "NYA.X.IND"; TODO */
	}
	
	return "";
}



public int getNumber(int nnr) {

	switch (nnr)
	{
	case NUM_LSDAX_NAMELEN:
		return 24;
	
	case NUM_ICCOM_PIXWIDTH:
		return 400/*413*/;

	case NUM_ICCOM_PIXHEIGHT:
		return 365/*461*/;

	case NUM_ICCOM_YOFFSET:
		return 0/*27*/;

	case NUM_ICCOM_YEND:
		return 255/*232*/;

	case NUM_ICCOM_XOFFSET:
		return 0;

	case NUM_ICCOM_XEND:
		return 400/*405*/;

	case NUM_ICDB_PIXWIDTH:
		return 400;

	case NUM_ICDB_PIXHEIGHT:
		return 412;

	case NUM_ICDB_YOFFSET:
		return 32;

	case NUM_ICDB_YEND:
		return 258;

	case NUM_ICDB_XOFFSET:
		return 0;

	case NUM_ICDB_XEND:
		return 399;
	}
	
	return -1;
}



public String getBase(int bnr) {

	switch (bnr)
	{
	case BASE_EXCHANGE:
		return "http://www.exchange.de/";
	
	case BASE_BBBANK:
		return "http://bbbank.teledata.de/bbbank/";
	
	case BASE_DEUBA:
//		return "http://deuba.teledata.de:9030";
		return "http://deuba.aktienman.de:9030";

	case BASE_COMDIRECT:
		return "http://informer2.comdirect.de";
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

	case URL_KAMERADJI:
		return "http://rtq.thomsoninvest.net/graphics/intraday/djia_indicy.gif";

	case URL_KAMERANASDAQ:
		return "http://rtq.thomsoninvest.net/graphics/intraday/nasdaq_indicy.gif";

	case URL_KAMERASP500:
		return "http://rtq.thomsoninvest.net/graphics/intraday/sp500_indicy.gif";

	case URL_DAXREALTIME:
		return getBase(BASE_EXCHANGE) + "realtime/dax_d.html";
	
	case URL_CHARTINTRADAY:
		return getBase(BASE_EXCHANGE) + "realtime/";
	
	case URL_DAX30:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=dax30&sType=default&bNoIdx=1&kunde=99999ext";

	case URL_DAX100:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=mdax&sType=default&bNoIdx=1&kunde=99999ext";

	case URL_NMARKT:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=nemax&sType=default&bNoIdx=1&kunde=99999ext";

	case URL_EURO50:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=eurostoxx50&sType=default&bNoIdx=1&kunde=99999ext";

	case URL_AUSLAND:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=stoxx50&sType=default&bNoIdx=1&kunde=99999ext";

	case URL_KURSECOMDIRECT:
		return getBase(BASE_COMDIRECT) + "/de/suche/main.html?searchfor=";

	case URL_KURSEDEUBA:
		return getBase(BASE_DEUBA) + "/deuba/search/searchswitch.html?searchfor=";
	
	case URL_CHARTDEUBA:
		return getBase(BASE_DEUBA) + "/db/";

/*	case URL_BBBINDEXD:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=indizes-germany&sType=index&bNoIdx=0&kunde=99999ext";

	case URL_BBBINDEXEU:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=indizes-euro&sType=index&bNoIdx=0&kunde=99999ext";

	case URL_BBBINDEXUS:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=indizes-us&sType=index&bNoIdx=0&kunde=99999ext";

	case URL_BBBINDEXASIA:
		return getBase(BASE_BBBANK) + "kursliste.html?sKl=indizes-asia&sType=index&bNoIdx=0&kunde=99999ext"; TODO */

	case URL_LSDAX30REALTIME:
		return "http://quotecenter.ls-d.de/lang/nbody_nm.cfm?lus=0.23665493&CFID=929612&CFTOKEN=11833536&papier=DAX";
	}

	return "";
}



public String checkWKN(String wkn) {

/*	if (wkn.equals("849084"))
	{
		return "DE0008490848";
	} */

	return wkn;
}



public String getExchangeIntradayChartURL(String kuerzel, String boerse) {

	return get(URL_CHARTINTRADAY) + kuerzel + "." + boerse + ".EUR.gif";
}



public String getDeubaKursURL(String wkn, String boerse) {

	return get(URL_KURSEDEUBA) + checkWKN(wkn) + "." + boerse + "&searchforb=0&searchforicat=0&Search_ex=Pr%E4zise+suchen";
}



/* public String getDeubaChartMonths(int type) {

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
*/


/*public String getDeubaChartURL(String rel, int type) {

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
}*/



/*public String getComdirectKursURL(String wkn, String boerse) {

//	String wkneu = checkWKN(wkn);
	//	if (!wkneu.equals(wkn))
//	{
//		wkneu += "." + boerse;
//	}

//	return get(URL_KURSECOMDIRECT) + checkWKN(wkn) + "&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen=" + boerse;

	return get(URL_KURSECOMDIRECT) + checkWKN(wkn) + "&searchButton_tol=Tolerant&XsearchWPArt=UKN&XsearchBoersen=" + boerse;
}*/



/*protected String getComdirectChartType(int charttype) {

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
}*/



/* public String getComdirectChartMonths(int type) {

	switch (type)
	{
	case CHART_INTRA:
		return "iD";

	case CHART_3:
		return "3M";

	case CHART_60:
		return "5Y";

	case CHART_120:
		return "10Y";
	}
	
	return "1Y";
} */



/*public String getComdirectChartURL(String rel, int type, int charttype) {

	int i = rel.indexOf("&");
	
	if (i > 0)
	{
		rel = rel.substring(0,i);
	}
	
	return getBase(BASE_COMDIRECT) + rel + "&sTimeframe=" + getComdirectChartMonths(type);
}*/



public boolean isValidNr(long nr) {

	switch ((int)nr)
	{
	case 8:
	case 32:
	case 40:
	case 42:
	case 92:
	case 93:
	case 150:
	case 151:
	case 165:
	case 186:
	case 229:
	case 270:
	case 271:
	case 272:
	case 339:
	case 485:
	case 595:
	case 613:
	case 619:
//	case 672: reaktiviert am 22.3.02 (Deaktivierung 1.3.02)
	case 675:
	case 701: // 2002-05-09
	case 712:
	case 758:
	case 764: // 2002-07-09
	case 770:
	case 792: // 2002-08-14
	case 809: // 2002-12-16
	case 810: // 2002-12-16
	case 813: // 2002-12-16
	case 815: // 2002-12-16
	case 829:

		return false;
	}

	return true;
}


// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=USD&expr2=EUR&margin_fixed=0
// http://www.oanda.com/converter/classic?lang=de&value=1&date=15.03.1999&date_fmt=normal&exch2=EUR&expr2=USD&margin_fixed=0

}
