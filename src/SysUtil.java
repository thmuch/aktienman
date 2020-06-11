/**
 @author Thomas Much
 @version 1999-03-25
*/



public final class SysUtil {

private static final String OS_MAC     = "MACOS";
private static final String OS_WINDOWS = "WINOS";
private static final String OS_LINUX   = "LINUX";



private SysUtil() {}


public static boolean isMacOS() {
	return ((System.getProperty("java.vendor").indexOf("Apple") >= 0) || (System.getProperty("os.name").indexOf("Mac OS") >= 0));
}


public static boolean isLinux() {
	return (System.getProperty("os.name").indexOf("Linux") >= 0);
}


public static boolean isWindows() {
	return (System.getProperty("os.name").indexOf("Windows") >= 0);
}


public static String getOSString() {
	if (isMacOS())
	{
		return OS_MAC;
	}
	else if (isWindows())
	{
		return OS_WINDOWS;
	}
	else
	{
		return OS_LINUX;
	}
}

}
