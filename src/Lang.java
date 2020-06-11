/**
 @author Thomas Much
 @version 1998-11-25
*/



public class Lang {

public static final String OK     = "      OK      ";
public static final String CANCEL = " Abbrechen ";

public static final String YES    = "     Ja     ";
public static final String NO     = "   Nein   ";

public static final String CHANGE = "  \u00c4ndern  ";
public static final String DELETE = "  L\u00f6schen  ";

public static final String LOADKAMERA = "DAX-Kamera wird geladen...";
public static final String LOADCHART  = "Chart wird geladen...";

public static final String EDITMENUTITLE = "Bearbeiten";

/* #Ablaufdatum */
public static final String DEMOVERSION = "DEMO-VERSION, L\u00c4UFT AM 21.12.1998 AB!";



public static String getFileMenuTitle() {
	if (AktienMan.isMacOS())
	{
		return "Ablage";
	}
	else
	{
		return "Datei";
	}
}

}
