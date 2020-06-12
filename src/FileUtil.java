/**
 @author Thomas Much
 @version 2001-11-05
*/

import java.io.*;
import java.util.*;




public final class FileUtil {

public static final String EXT_PORTFOLIO = ".lst";
public static final String EXT_POPUP     = ".pop";
public static final String EXT_CONFIG    = ".cfg";
public static final String EXT_INDIZES   = ".ind";

private static final String pathsep = System.getProperty("path.separator");
private static final String filesep = System.getProperty("file.separator");

private static final String userDir  = System.getProperty("user.dir") + filesep;

private static String lastFindPath = "";
private static String homeDir = "";
private static String moduleDir = null;



static {

	String einstr = getWorkingDirectory() + Lang.CONFIGDIR;
	
	if (new File(einstr).exists())
	{
		homeDir = einstr + filesep;
	}
	else
	{
		homeDir = System.getProperty("user.home") + filesep;
	
		if (SysUtil.isMacOSX())
		{
			String newhome = homeDir + "Library" + filesep + "Preferences" + filesep;
			
			String oldname = homeDir + getDefaultUnixFilename();
			String newname = newhome + getDefaultFilename();
			
			new File(oldname).renameTo(new File(newname));

			new File(oldname + EXT_PORTFOLIO).renameTo(new File(newname + EXT_PORTFOLIO));
			new File(oldname + EXT_POPUP).renameTo(new File(newname + EXT_POPUP));
			new File(oldname + EXT_CONFIG).renameTo(new File(newname + EXT_CONFIG));
			new File(oldname + EXT_INDIZES).renameTo(new File(newname + EXT_INDIZES));
			
			homeDir = newhome;
		}
	}
	
	String modstr = getWorkingDirectory() + Lang.MODULEDIR;
	
	if (new File(modstr).exists())
	{
		moduleDir = modstr + filesep;
	}
}




private FileUtil() {}



private static String getHomeDirectory() {

	return homeDir;
}



public static String getWorkingDirectory() {

	return userDir;
}



public static String getModuleDirectory() {

	return moduleDir;
}



private static String getDefaultFile() {

	return getHomeDirectory() + ((SysUtil.isAUnix()) ? getDefaultUnixFilename() : getDefaultFilename());
}



private static String getDefaultUnixFilename() {

	return "." + getDefaultFilename().toLowerCase();
}



private static String getDefaultFilename() {

	return AktienMan.AMNAME;
}



public static String getAMDirectory(boolean addFilesep) {

	String s = getDefaultFile();
	
	if (addFilesep) s += filesep;

	return s;
}



public static void createAMDirectory() {

	File amd = new File(getAMDirectory(false));

	if (!amd.exists()) amd.mkdir();
}



public static String getConfigFile() {

	return getDefaultFile() + EXT_CONFIG;
}



public static String getIndexFile() {

	return getDefaultFile() + EXT_INDIZES;
}



public static String getPopupFile() {

	return getDefaultFile() + EXT_POPUP;
}



public static String getDefaultPortfolioFile() {

	return getDefaultFile() + EXT_PORTFOLIO;
}



public static String findLocalFile(String filename) {

	if (lastFindPath.length() > 0)
	{
		File f = new File(lastFindPath+filename);
		
		if (f.exists())
		{
			return lastFindPath+filename;
		}
	}
	
	StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"),pathsep);
	
	while (st.hasMoreTokens()) {
		String s = st.nextToken();
		
		if ((!s.endsWith(".zip")) && (!s.endsWith(".jar")))
		{
			if (!s.endsWith(filesep))
			{
				s += filesep;
			}
			
			File f = new File(s + filename);
			
			if (f.length() > 0L)
			{
				lastFindPath = s;				
				return lastFindPath+filename;
			}
		}
	}

	return filename;
}

}
