/**
 @author Thomas Much
 @version 2003-02-27
*/



public class ISIN {



private ISIN() {}



public static boolean isValid(String isin) {

	if ((isin == null) || (isin.length() != 12)) return false;

	/* TODO */

	return true;
}


}
