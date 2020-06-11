/**
 @author Thomas Much
 @version 1999-01-14
*/



public final class HTMLUtil {



private HTMLUtil() {}


public static String toNbspHTML(String s) {
	return fixSpaces(toHTML(s));
}


public static String fixSpaces(String s) {
	int i = s.indexOf(" ");
	
	while (i >= 0)
	{
		s = s.substring(0,i) + "&nbsp;" + s.substring(i+1);
		i = s.indexOf(" ");
	}

	return s;
}


public static String toHTML(String s) {
	int i = 0;
	
	while (i < s.length())
	{
		char c = s.charAt(i);
		
		if (c == '<')
		{
			s = s.substring(0,i) + "&lt;" + s.substring(i+1);
			i += 4;
		}
		else if (c == '>')
		{
			s = s.substring(0,i) + "&gt;" + s.substring(i+1);
			i += 4;
		}
		else if (c == '&')
		{
			s = s.substring(0,i) + "&amp;" + s.substring(i+1);
			i += 5;
		}
		else if (c == '\u00e4')
		{
			s = s.substring(0,i) + "&auml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00f6')
		{
			s = s.substring(0,i) + "&ouml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00fc')
		{
			s = s.substring(0,i) + "&uuml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00c4')
		{
			s = s.substring(0,i) + "&Auml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00d6')
		{
			s = s.substring(0,i) + "&Ouml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00dc')
		{
			s = s.substring(0,i) + "&Uuml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00df')
		{
			s = s.substring(0,i) + "&szlig;" + s.substring(i+1);
			i += 7;
		}
		else
		{
			i++;
		}
	}

	return s;
}


}
