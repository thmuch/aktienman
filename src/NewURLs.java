/**
 @author Thomas Much
 @version 2002-01-13
*/




public class NewURLs extends URLs {




/*public String getBase(int bnr) {

	switch (bnr)
	{
	case BASE_COMDIRECT:

		return "http://informer2.comdirect.de";

	case BASE_BBBANK:

		return "http://bbbank.teledata.de/bbbank/";
	}

	return super.getBase(bnr);
}*/



/*public String getComdirectKursURL(String wkn, String boerse) {

	return get(URL_KURSECOMDIRECT) + checkWKN(wkn) + "&searchButton_tol=Tolerant&XsearchWPArt=UKN&XsearchBoersen=" + boerse;
}*/



/*public String get(int urlNr) {
	
	if (urlNr == URL_KAMERA)
	{
		return getBase(BASE_EXCHANGE) + "parkett/parkett.jpg";
	}
	
	return super.get(urlNr);
}*/



/*public String getString(int strNr) {

	switch (strNr)
	{
	case STR_CD_KURSENDE:

		return "Chart Analyser";

	case STR_CD_LISTEBOERSERE:

		return "";

	case STR_CD_KURSVOLUMEN:

		return "Gehandelter Nennwert";
	}

	return super.getString(strNr);
} */



/*public String checkWKN(String wkn) {

	if (wkn.equals("555750"))
	{
		return "DTE";
	}

	return super.checkWKN(wkn);
} */



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
	case 675:

		return false;
	}

	return super.isValidNr(nr);
}

}
