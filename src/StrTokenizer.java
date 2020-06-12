/**
 @author Thomas Much
 @version 2003-01-24
*/



public class StrTokenizer {

private String str;
private char delim;




public StrTokenizer(String str, char delim) {

	this.str = str;
	this.delim = delim;
}



public String nextToken() {

	if (str == null) return null;
	
	String ret;
	
	int i = str.indexOf(delim);

	if (i < 0)
	{
		ret = str;
		str = null;
	}
	else
	{
		ret = str.substring(0,i);
		str = str.substring(i+1);
	}

	return ret;
}



public boolean hasMoreTokens() {

	return (str != null);
}


}
