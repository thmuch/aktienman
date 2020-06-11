/**
 @author Thomas Much
 @version 1998-11-05
*/



public class Lang {

public static final String OK     = "      OK      ";
public static final String CANCEL = " Abbrechen ";

public static final String YES    = "     Ja     ";
public static final String NO     = "   Nein   ";

public static final String CHANGE = "  \u00c4ndern  ";

public static final String LOADIMAGE = "Bild wird geladen...";

public static final String EDITMENUTITLE = "Bearbeiten";



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
