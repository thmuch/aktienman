/**
 @author Thomas Much
 @version 1999-03-13
*/



public final class NumUtil {

private static final char DEZSEPARATOR = ',';



private NumUtil() {}


public static double getDouble(String str) throws NumberFormatException {
	return Double.valueOf(str.trim().replace(DEZSEPARATOR,'.')).doubleValue();
}


public static String getString(double d) {
	return new Double(d).toString().replace('.',DEZSEPARATOR);
}


public static String get00String(long l) {
	String s = getString(Waehrungen.longToDouble(l));
	int i = s.indexOf(DEZSEPARATOR);
	
	if (i < 0)
	{
		s = s + DEZSEPARATOR + "00";
	}
	else if (i == s.length()-1)
	{
		s = s + "00";
	}
	else if (i == s.length()-2)
	{
		s = s + "0";
	}
	
	return s;
}

}
