/**
 @author Thomas Much
 @version 1999-07-18
*/




public class NewURLs extends URLs {




public String getString(int strNr) {

	if (strNr == STR_DEUBA_CHARTSRC)
	{
		return " src=\'";
	}

	return super.getString(strNr);
}



public String checkWKN(String wkn) {

	if (wkn.equals("519003"))
	{
		return "BMW3";
	}
	else if (wkn.equals("703703"))
	{
		return "RWE3";
	}

	return super.checkWKN(wkn);
}

}
