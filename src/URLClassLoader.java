/**
 @author Thomas Much
 @version 1999-05-24
*/

import java.util.*;
import java.io.*;
import java.net.*;



public class URLClassLoader extends ClassLoader {

private Hashtable cache = new Hashtable();

private String host;



public URLClassLoader(String host) {
	super();
	this.host = host;
}


public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
	Class c = (Class)cache.get(name);

	if (c == null)
	{
		byte[] data = loadClassData(name);
		
		if (data == null)
		{
			if (AktienMan.DEBUG)
			{
				System.out.println("Klasse auf Server nicht gefunden, wir suchen \""+name+".class\" lokal.");
			}

			return super.findSystemClass(name);
		}
		
		c = defineClass(name,data,0,data.length);
		
		cache.put(name,c);
	}
	
	if (resolve)
	{
		resolveClass(c);
	}
	
	return c;
}


private byte[] loadClassData(String name) {

	if (name.startsWith("java.") || (name.indexOf("..") >= 0))
	{
		return null;
	}

	DataInputStream in = null;

	try
	{
		URL url = new URL(host + name + ".class");

		URLConnection curl = url.openConnection();
			
		byte[] daten = new byte[curl.getContentLength()];
			
		in = new DataInputStream(curl.getInputStream());

		in.readFully(daten);
		
		return daten;
	}
	catch (Exception e)
	{
		return null;
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

}
