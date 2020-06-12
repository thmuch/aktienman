/**
 @author Thomas Much
 @version 2003-01-20
*/

import java.io.*;



public final class XMLUtil {

public static final String ENCODING = "UTF-8";



private XMLUtil() {}



public static void writePrologue(PrintWriter out) {

	out.println("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\" standalone=\"yes\" ?>");
}



public static void checkValidPrologue(BufferedReader in) throws Exception {

	String s = nextDataLine(in);
	
	if ((s == null) || (!s.startsWith("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"")))
	{
		throw new Exception("Ungültiger Dateianfang: " + s);
	}
}



public static String nextDataLine(BufferedReader in) throws IOException {

	while (true)
	{
		String s = in.readLine();
		
		if (s == null) return null;
		
		s = s.trim();
		
		if ((s.length() > 0) && (!s.startsWith("<!--")))
		{
			return s;
		}
	}
}



public static String getValue(String s, boolean nullIfEmpty) throws Exception {

	int start = s.indexOf('>');
	int ende  = s.lastIndexOf('<');
	
	if ((start < 0) || (start >= ende)) throw new Exception("Ungültiges Element: " + s);
	
	String ret = s.substring(start+1,ende);
	
	if (nullIfEmpty && (ret.length() == 0)) return null;
	
	return ret;
}



public static long getLongValue(String s) throws Exception {

	return Long.parseLong( getValue(s,true) );
}



public static int getIntValue(String s) throws Exception {

	return Integer.parseInt( getValue(s,true) );
}



public static void writeComment(PrintWriter out, String s) {

	out.println("<!-- " + escapeString(s) + " -->");
}



public static String escapeString(String s) {

	if (s == null) return "";

	int len = s.length();
	
	int i = 0;
	
	while (i < len)
	{
		switch (s.charAt(i))
		{
		case '<':

			s = s.substring(0,i) + "&lt;" + s.substring(i+1);
			len += 3;
			i += 4;
			break;

		case '>':

			s = s.substring(0,i) + "&gt;" + s.substring(i+1);
			len += 3;
			i += 4;
			break;

		case '&':

			s = s.substring(0,i) + "&amp;" + s.substring(i+1);
			len += 4;
			i += 5;
			break;
		
		default:

			i++;
			break;
		}
	}

	return s;
}



public static String unescapeString(String s) {

	if (s == null) return null;

	int len = s.length();
	
	int i = 0;
	
	while (i < len)
	{
		if (s.charAt(i) == '&')
		{
			if ((i+3 < len) && (s.charAt(i+1) == 'g') && (s.charAt(i+2) == 't') && (s.charAt(i+3) == ';'))
			{
				s = s.substring(0,i) + ">" + s.substring(i+4);
				len -= 3;
			}
			else if ((i+3 < len) && (s.charAt(i+1) == 'l') && (s.charAt(i+2) == 't') && (s.charAt(i+3) == ';'))
			{
				s = s.substring(0,i) + "<" + s.substring(i+4);
				len -= 3;
			}
			else if ((i+4 < len) && (s.charAt(i+1) == 'a') && (s.charAt(i+2) == 'm') && (s.charAt(i+3) == 'p') && (s.charAt(i+4) == ';'))
			{
				s = s.substring(0,i) + "&" + s.substring(i+5);
				len -= 4;
			}
		}

		i++;
	}

	return s;
}


}
