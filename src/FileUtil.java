/**
 @author Thomas Much
 @version 1999-01-19
*/

import java.io.*;



public final class FileUtil {

public static final String EXT_PORTFOLIO = ".lst";
public static final String EXT_POPUP     = ".pop";
public static final String EXT_CONFIG    = ".cfg";



private FileUtil() {}


private static String getHomeDirectory() {
	return System.getProperty("user.home") + System.getProperty("file.separator");
}


public static String getWorkingDirectory() {
	return System.getProperty("user.dir") + System.getProperty("file.separator");
}


private static String getDefaultFile() {
	String s = AktienMan.AMNAME;

	if (SysUtil.isLinux())
	{
		s = "." + s.toLowerCase();
	}

	return getHomeDirectory() + s;
}


public static String getAMDirectory(boolean filesep) {
	String s = getDefaultFile();
	
	if (filesep) s += System.getProperty("file.separator");

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

}
