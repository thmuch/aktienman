/**
 @author Thomas Much
 @version 1999-05-25
*/

import java.util.*;




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

	return NetUtil.loadRawURL(host + name + ".class",true,-1);
}

}
