/**
 @author Thomas Much
 @version 2001-11-05
*/




public final class SysUtil {

private static final String OS_MAC     = "MACOS";
private static final String OS_MACX    = "MOSX";
private static final String OS_WINDOWS = "WINOS";
private static final String OS_LINUX   = "LINUX";
private static final String OS_2       = "OS2";

private static final boolean ismacos;
private static final boolean ismacosx;
private static final boolean islinux;
private static final boolean iswindows;
private static final boolean isos2;




static {

	String osname = System.getProperty("os.name");
	String vendor = System.getProperty("java.vendor");

	ismacosx  = (vendor.indexOf("Apple") >= 0) && (osname.indexOf("Mac OS X") >= 0);
	ismacos   = (!ismacosx) && ((vendor.indexOf("Apple") >= 0) || (osname.indexOf("Mac OS") >= 0));
	islinux   = (osname.indexOf("Linux") >= 0);
	iswindows = (osname.indexOf("Windows") >= 0);
	isos2     = (osname.indexOf("OS/2") >= 0);

	if (AktienMan.DEBUG)
	{
		System.out.println("java.version: "+System.getProperty("java.version"));
		System.out.println("java.vendor:  "+vendor);
		System.out.println("os.name:      "+osname);
		System.out.println("os.arch:      "+System.getProperty("os.arch"));
		System.out.println("os.version:   "+System.getProperty("os.version"));
		System.out.println("mrj.version:  "+System.getProperty("mrj.version"));
		System.out.println();
		System.out.println("Erkanntes System: "+getOSString());
		System.out.println();
	}
}



private SysUtil() {}



public static boolean isMacOS() {

	return ismacos;
}



public static boolean isMacOSX() {

	return ismacosx;
}



public static boolean isAMac() {

	return (ismacos || ismacosx);
}



public static boolean isLinux() {

	return islinux;
}



public static boolean isAUnix() {

	return (islinux /* || ismacosx */);
}



public static boolean isWindows() {

	return iswindows;
}



public static boolean isOS2() {

	return isos2;
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
