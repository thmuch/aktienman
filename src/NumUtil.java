/**
 @author Thomas Much
 @version 2003-02-16

 2003-02-16
 	getPrecisionString
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



public static String getPrecisionString(double d, int precision) {

	String s = getString(d);
	
	int i = s.indexOf(DEZSEPARATOR);
	int count;
	
	if (i < 0)
	{
		s += DEZSEPARATOR;
		
		count = precision;		
	}
	else
	{
		count = precision - (s.length() - i - 1);
	}

	for (int j = 0; j < count; j++) s += "0";

	return s;
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
