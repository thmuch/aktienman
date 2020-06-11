/**
 @author Thomas Much
 @version 1999-06-30
*/




public class NewURLs extends URLs {




/*public String checkWKN(String wkn) {

	if (wkn.equals("710000"))
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
	else if (wkn.equals("695200"))
	{
		return "PRS";
	}
	else if (wkn.equals("850000"))
	{
		return "GMC";
	}
	else if (wkn.equals("920578"))
	{
		return "LYLX";
	}
	else if (wkn.equals("920566"))
	{
		return "LYLK";
	}

	return super.checkWKN(wkn);
}



public String getComdirectKursURL(String wkn, String boerse) {

	String wkneu = checkWKN(wkn);
	
	if (!wkneu.equals(wkn))
	{
		wkneu += "." + boerse;
	}

	return get(URL_KURSECOMDIRECT) + wkneu + "&searchButton=Exakt&XsearchWPArt=UKN&XsearchBoersen=" + boerse;
} */

}
