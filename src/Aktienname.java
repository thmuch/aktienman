/**
 @author Thomas Much
 @version 1999-03-21
*/



public final class Aktienname {



private Aktienname() {}


public static String getKurzName(String langname) {
	String n = langname.trim();
	
	String s = n.toUpperCase() + " ";
	
	int i = s.indexOf("(");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" AG ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" CO.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(".CO.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" INC.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" CORP.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" KG");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("-AG");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(".AG");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" N.V.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" NV ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" NV,");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" S.A.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" SA ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("-SA ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(".SAACCIONES");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" PLC");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" PCL ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("S.P.A.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" LTD.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" SHS ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" ACTIONS ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" SHARES ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" AGINHABER");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" AGAKTIEN");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("NAMENSAKT");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("NAMENS-AKT");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" AKTIEN ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" MIJ.");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" HOLDING");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(".HLDG ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" ABAKTIER ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf("VORZUGSAKTIEN");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" GMBH");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" WERTPAPIER ");
	if (i > 0) return getKurzName(n.substring(0,i));

	i = s.indexOf(" EFFEKTEN ");
	if (i > 0) return getKurzName(n.substring(0,i));
	
	return n;
}

}
