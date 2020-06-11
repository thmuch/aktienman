/**
 @author Thomas Much
 @version 1999-02-17
*/

import java.io.*;
import java.util.*;



public final class FileUtil {

public static final String EXT_PORTFOLIO = ".lst";
public static final String EXT_POPUP     = ".pop";
public static final String EXT_CONFIG    = ".cfg";

private static final String pathsep = System.getProperty("path.separator");
private static final String filesep = System.getProperty("file.separator");

private static String lastFindPath = "";



private FileUtil() {}


private static String getHomeDirectory() {
	return System.getProperty("user.home") + filesep;
}


public static String getWorkingDirectory() {
	return System.getProperty("user.dir") + filesep;
}


private static String getDefaultFile() {
	String s = AktienMan.AMNAME;

	if (SysUtil.isLinux())
	{
		s = "." + s.toLowerCase();
	}

	return getHomeDirectory() + s;
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
