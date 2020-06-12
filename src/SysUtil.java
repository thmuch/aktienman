/**
 @author Thomas Much
 @version 2000-11-10
*/




public final class SysUtil {

private static final String OS_MAC     = "MACOS";
private static final String OS_MACX    = "MOSX";
private static final String OS_WINDOWS = "WINOS";
private static final String OS_LINUX   = "LINUX";
private static final String OS_2       = "OS2";




private SysUtil() {}



public static boolean isMacOS() {

	return ((System.getProperty("java.vendor").indexOf("Apple") >= 0) || (System.getProperty("os.name").indexOf("Mac OS") >= 0));
}



public static boolean isMacOSX() {

	return isMacOS();
	/* TODO */
}



public static boolean isLinux() {

	return (System.getProperty("os.name").indexOf("Linux") >= 0);
}



public static boolean isWindows() {

	return (System.getProperty("os.name").indexOf("Windows") >= 0);
}



public static boolean isOS2() {

	return (System.getProperty("os.name").indexOf("OS/2") >= 0);
}



public static String getOSString() {

	if (isMacOS())
	{
		return OS_MAC;
	}
	else if (isMacOSX())
	{
		return OS_MACX;
	}
	else if (isWindows())
	{
		return OS_WINDOWS;
	}
	else if (isOS2())
	{
		return OS_2;
	}
	else
	{
		return OS_LINUX;
	}
}

}
