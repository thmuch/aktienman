// 1998-08-21 tm

import java.util.*;
import java.io.*;



public class AProperties extends Properties {

private String filename;
private String comment;

private String folder = System.getProperty("user.home");
private String filesep = System.getProperty("file.separator");



public AProperties(String filename, String comment) {
	super();
	
	this.filename = filename;
	this.comment = comment;
	
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
	String s = getProperty(key,"");

	if (s == null)
	{
		return -1;
	}
	else
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}
}


public void setInt(String key, int value) {
	put(key,new Integer(value).toString());
}


public boolean getBoolean(String key) {
	String s = getProperty(key,"");

	if (s == null)
	{
		return false;
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
		in = new FileInputStream(folder+filesep+filename);
		load(in);
	}
	catch (FileNotFoundException e)
	{
		in = null;
	}
	catch (IOException e)
	{
	}
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
		out = new FileOutputStream(folder+filesep+filename);
		save(out,comment);
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
