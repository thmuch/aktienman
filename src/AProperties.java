/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.util.*;
import java.io.*;



public final class AProperties extends Properties {

private static final String KOMMENTAR = AktienMan.AMNAME + " " + AktienMan.AMVERSION + " Konfigurationsdatei";

private String filename = FileUtil.getConfigFile();



public AProperties() {
	super();
	loadParameters();
}


public String getString(String key) {
	String s = getProperty(key,"");

	if (s == null)
	{
		return "";
	}
	else
	{
		return s;
	}
}


public void setString(String key, String value) {
	put(key,value);
}


public int getInt(String key) {
	return getInt(key,-1);
}


public int getInt(String key, int defval) {
	String s = getProperty(key,"");

	if (s == null)
	{
		return defval;
	}
	else
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return defval;
		}
	}
}


public void setInt(String key, int value) {
	put(key,new Integer(value).toString());
}


public boolean getBoolean(String key) {
	return getBoolean(key,false);
}


public boolean getBoolean(String key, boolean defval) {
	String s = getProperty(key,"");

	if (s == null)
	{
		return defval;
	}
	else if (s.length() == 0)
	{
		return defval;
	}
	else
	{
		return s.equalsIgnoreCase("true");
	}
}


public void setBoolean(String key, boolean value) {
	put(key,new Boolean(value).toString());
}


public void loadParameters() {
	FileInputStream in = null;
	
	try
	{
		in = new FileInputStream(filename);
		load(in);
	}
	catch (FileNotFoundException e)
	{
		in = null;
	}
	catch (IOException e) {}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e) {}
		
			in = null;
		}
	}
}


public void saveParameters() {
	FileOutputStream out = null;
	
	try
	{
		out = new FileOutputStream(filename);

		save(out,KOMMENTAR);

		out.flush();
	}
	catch (IOException e) {}
	finally
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e) {}
		
			out = null;
		}
	}
}

}
