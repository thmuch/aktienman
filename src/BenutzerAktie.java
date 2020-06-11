/**
 @author Thomas Much
 @version 1998-11-29
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;



public class BenutzerAktie implements Serializable {

static final long serialVersionUID = 1997061300002L;

public static final long VALUE_MISSING =  0L;
public static final long VALUE_ERROR   = -1L;
public static final long VALUE_NA      = -2L;

public static final int HTMLCOLS = 12;

private static final String STR_MISSING = "<aktualisieren>";
private static final String STR_ERROR   = "<Fehler>";
private static final String STR_NA      = "n/a";

private static final int HEADROWS = 2;

private static final int ZEILENABSTAND = 0;
private static final int HEADERABSTAND = 3;
private static final int FOOTERABSTAND = 3;

private static ADate heute = new ADate();

private transient static long aktsumme = 0L;
private transient static long kaufsumme = 0L;
private transient static Label lupdate = null;
private transient static Color farbeHintergrund;

private transient Color farbeSteuerfrei;
private transient Color farbeName;
private transient Color farbeSelected;
private transient boolean selected = false;
private transient AktieInfo infodialog = null;
private transient BALabel l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12;

private String name,wkn,kursdatum;
private Boersenplatz boersenplatz;
private ADate kaufdatum;
private ADate steuerfrei;
private long kaufkurs,hochkurs,tiefkurs,gewinngrenze,prozgrenze,stueckzahl;

private long kurs = VALUE_MISSING;
private long vortageskurs = VALUE_MISSING;
private long eroeffnungskurs = VALUE_MISSING;
private long hoechstkurs = VALUE_MISSING;
private long tiefstkurs = VALUE_MISSING;
private long handelsvolumen = 0L;

private ADate watchstart = null;
private long watchhoechst = VALUE_MISSING;
private long watchtiefst = VALUE_MISSING;
private String watchhdate = null;
private String watchtdate = null;

private int waehrung = Waehrungen.DEM;
private boolean nurdiese = false;
private boolean usegrenze = true;
private boolean watchonly = false;



public BenutzerAktie(String name, String wkn, Boersenplatz platz, boolean nurdiese,
						ADate kaufdatum, long kaufkurs, long stueckzahl,
						long hochkurs, long tiefkurs, long grenze, int waehrung,
						boolean usegrenze, boolean watchonly) {
	this.name = name;
	this.wkn = wkn;
	this.kaufdatum = kaufdatum;
	this.kaufkurs = kaufkurs;
	this.stueckzahl = stueckzahl;
	this.hochkurs = hochkurs;
	this.tiefkurs = tiefkurs;
	this.waehrung = waehrung;
	this.nurdiese = nurdiese;
	this.usegrenze = usegrenze;
	this.watchonly = watchonly;
	prozgrenze = grenze;
	boersenplatz = platz;

	kursdatum = "";
	
	setupValues();
	setColors();
}


private void setupValues() {
	gewinngrenze = (prozgrenze == 0L) ? 0L : (getKaufkurs()*(Waehrungen.PRECISION*100L+prozgrenze))/(Waehrungen.PRECISION*100L);
	
	if (kaufdatum == null) kaufdatum = new ADate();

	int jahr = kaufdatum.getYear();
	int monat = kaufdatum.getMonth() + 6;
	int tag = kaufdatum.getDay() + 1;

	if (monat > ADate.DECEMBER)
	{
		monat -= 12;
		jahr++;
	}
	
	if (tag > ADate.getDays(jahr,monat))
	{
		tag = 1;
		monat ++;
		
		if (monat > ADate.DECEMBER)
		{
			monat = ADate.JANUARY;
			jahr++;
		}
	}

	steuerfrei = new ADate(jahr,monat,tag);
}


private void setColors() {
	farbeSteuerfrei = Color.yellow.darker().darker();
	farbeSelected = Color.lightGray;
	clearStatusRequesting();
}


public String getProzentString() {
	return (prozgrenze == 0L) ? "" : AktienMan.get00String(prozgrenze);
}


private String datum2String(String datum) {
	if (datum == null) return "";
	if (datum.length() == 0) return "";
	
	return "(" + datum + ")";
}


public synchronized String getKursdatumString() {
	return datum2String(kursdatum);
}


public long getHochkurs() {
	return hochkurs;
}


public String getHochkursString() {
	return (getHochkurs() == 0L) ? "" : AktienMan.get00String(getHochkurs());
}


public long getTiefkurs() {
	return tiefkurs;
}


public String getTiefkursString() {
	return (getTiefkurs() == 0L) ? "" : AktienMan.get00String(getTiefkurs());
}


public ADate getKaufdatum() {
	return (kaufdatum == null) ? new ADate() : kaufdatum;
}


public String getWKNString() {
	return wkn;
}


public boolean isBoerseFixed() {
	return nurdiese;
}


public boolean isBoerseFondsOnly() {
	return boersenplatz.isFondsOnly();
}


public boolean doUseGrenze() {
	return usegrenze;
}


public boolean nurBeobachten() {
	return watchonly;
}


public synchronized String getVortageskursString() {
	return kurs2String(vortageskurs);
}


public synchronized String getEroeffnungskursString() {
	return kurs2String(eroeffnungskurs);
}


public synchronized String getHoechstkursString() {
	return kurs2String(hoechstkurs);
}


public synchronized String getTiefstkursString() {
	return kurs2String(tiefstkurs);
}


public synchronized String getHandelsvolumenString() {
	return new Long(handelsvolumen).toString();
}


public synchronized String getWatchStartString() {
	if (watchstart == null)
	{
		return "";
	}
	else
	{
		return watchstart.toString();
	}
}


public synchronized String getWatchHoechstString() {
	return kurs2String(watchhoechst);
}


public synchronized String getWatchHoechstDatumString() {
	return datum2String(watchhdate);
}


public synchronized String getWatchTiefstString() {
	return kurs2String(watchtiefst);
}


public synchronized String getWatchTiefstDatumString() {
	return datum2String(watchtdate);
}


public int getWKN() {
	int wkni = 0;
	
	try
	{
		wkni = Integer.parseInt(getWKNString());
	}
	catch (NumberFormatException e) {}

	return wkni;
}


public String getBoerse() {
	return boersenplatz.getKurz();
}


public String getRequest(String boerse) {
	if ((isBoerseFixed()) || (boerse == null))
	{
		boerse = getBoerse();
	}
	else if (boerse.length() == 0)
	{
		boerse = getBoerse();
	}

	return getWKNString()+"."+boerse;
}


public synchronized String getName(boolean kurz) {
	if (name.length() == 0)
	{
		return (getKurs() < 0L) ? STR_ERROR : STR_MISSING;
	}
	else
	{
		return (kurz) ? getKurzName(name) : name;
	}
}


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
	
	return n;
}


public int getWaehrung() {
	return waehrung;
}


public synchronized long getStueckzahl() {
	return stueckzahl;
}


public synchronized String getStueckzahlString() {
	return new Long(getStueckzahl()).toString();
}


public synchronized void decStueckzahl(long delta) {
	if (delta > 0L)
	{
		stueckzahl -= delta;

		if (stueckzahl < 0L) stueckzahl = 0L;
	}
}


public synchronized void splitStueckzahl(long neu) {
	stueckzahl = (neu < 0L) ? 0L : neu;
	kurs = VALUE_MISSING;
}


public synchronized long getKurs() {
	return kurs;
}


public synchronized String getRawKursString() {
	if (getKurs() <= 0L)
	{
		return "0";
	}
	else
	{
		return AktienMan.get00String(getKurs());
	}
}


private String kurs2String(long k) {
	if (k == VALUE_MISSING)
	{
		return STR_MISSING;
	}
	else if (k == VALUE_NA)
	{
		return STR_NA;
	}
	else if (k <= VALUE_ERROR)
	{
		return STR_ERROR;
	}
	else
	{
		return Waehrungen.getString(k,Waehrungen.DEM);
	}
}


public synchronized String getKursString() {
	return kurs2String(getKurs());
}


public synchronized long getWert() {
	if (getKurs() < 0L)
	{
		return 0L;
	}
	else
	{
		if (nurBeobachten())
		{
			return getKurs();
		}
		else
		{
			return getKurs() * getStueckzahl();
		}
	}
}


public long getKaufkurs() {
	return kaufkurs;
}


public String getKaufkursString() {
	return kurs2String(getKaufkurs());
}


public boolean isEqual(String wkn, String platz, boolean compPlatz) {
	return isEqual(wkn,"",platz,compPlatz);
}


public boolean isEqual(String wkn, String kurz, String platz, boolean compPlatz) {
	if (compPlatz)
	{
		return isEqual(wkn,kurz,platz);
	}
	else
	{
		return isEqual(wkn,kurz);
	}
}


private boolean isEqual(String wkn, String kurz, String platz) {
	boolean valid;
	
	if (kurz.length() == 0)
	{
		valid = false;
	}
	else
	{
		valid = kurz.equalsIgnoreCase(getWKNString());
	}

	return (valid || wkn.equalsIgnoreCase(getWKNString())) && platz.equalsIgnoreCase(getBoerse());
}


private boolean isEqual(String wkn, String kurz) {
	boolean valid;
	
	if (kurz.length() == 0)
	{
		valid = false;
	}
	else
	{
		valid = kurz.equalsIgnoreCase(getWKNString());
	}
	
	return (valid || wkn.equalsIgnoreCase(getWKNString()));
}


public boolean istSteuerfrei() {
	return heute.after(steuerfrei);
}


public synchronized void setValues(long kurs) {
	setValues("",kurs);
}


public synchronized void setValues(String name, long kurs) {
	setValues(name,kurs,"",VALUE_NA,VALUE_NA,VALUE_NA,VALUE_NA,0L,getWaehrung());
}


public synchronized void setValues(String name, long kurs, String kursdatum,
									long vortageskurs, long eroeffnungskurs,
									long hoechstkurs, long tiefstkurs,
									long handelsvolumen, int kurswaehrung) {
	if (name.length() > 0)
	{
		if ((this.name.length() == 0) || AktienMan.properties.getBoolean("Konfig.Aktiennamen"))
		{
			this.name = name;
		}
	}

	// kurswaehrung beachten!

	this.kurs = kurs;
	this.kursdatum = kursdatum;
	this.vortageskurs = vortageskurs;
	this.eroeffnungskurs = eroeffnungskurs;
	this.hoechstkurs = hoechstkurs;
	this.tiefstkurs = tiefstkurs;
	this.handelsvolumen = handelsvolumen;
	
	if ((kaufkurs == VALUE_MISSING) && nurBeobachten())
	{
		kaufkurs = kurs;
		kaufdatum = new ADate();
		setupValues();
	}

	if ((kurs > VALUE_MISSING) && (watchstart == null))
	{
		watchstart = new ADate();
	}

	if ((hoechstkurs > VALUE_MISSING) && ((hoechstkurs > watchhoechst) || (watchhoechst == VALUE_MISSING)))
	{
		watchhoechst = hoechstkurs;
		watchhdate = kursdatum;
	}
		
	if ((tiefstkurs > VALUE_MISSING) && ((tiefstkurs < watchtiefst) || (watchtiefst == VALUE_MISSING)))
	{
		watchtiefst = tiefstkurs;
		watchtdate = kursdatum;
	}
	
	clearStatusRequesting();
	infoDialogSetValues(true);
}


public synchronized void changeValues(String newName, String newWKN, Boersenplatz newBp,
								boolean newNurDiese, ADate newDate, long newKaufkurs,
								long newAnz, long newHoch, long newTief, long newGrenze,
								boolean newUseGrenze, boolean newWatchonly) {

	boolean reset = ((!newWKN.equalsIgnoreCase(getWKNString())) || (!newBp.getKurz().equalsIgnoreCase(getBoerse())));
	
	name = newName;
	wkn = newWKN;
	boersenplatz = newBp;
	nurdiese = newNurDiese;
	kaufdatum = newDate;
	kaufkurs = newKaufkurs;
	stueckzahl = newAnz;
	hochkurs = newHoch;
	tiefkurs = newTief;
	prozgrenze = newGrenze;
	usegrenze = newUseGrenze;
	watchonly = newWatchonly;

	setupValues();
	
	if (reset)
	{
		kurs = VALUE_MISSING;
		kursdatum = "";
	}
	
	infoDialogSetValues(true);
}


public void setStatusRequestingAndRepaint() {
	farbeName = Color.blue;
	l1.setForeground(farbeName);
	l1.repaint();
}


public void setStatusErrorAndRepaint() {
	farbeName = Color.red;
	l1.setForeground(farbeName);
	l1.repaint();
}


public void clearStatusRequesting() {
	farbeName = Color.black;
}


public synchronized boolean isSelected() {
	return selected;
}


private void setColorAndRepaint(Color c) {
	l1.setBackground(c);
	l2.setBackground(c);
	l11.setBackground(c);
	l12.setBackground(c);
	l3.setBackground(c);
	l4.setBackground(c);
	l5.setBackground(c);
	l6.setBackground(c);
	l7.setBackground(c);
	l8.setBackground(c);
	l9.setBackground(c);
	l10.setBackground(c);
	
	l1.repaint();
	l2.repaint();
	l11.repaint();
	l12.repaint();
	l3.repaint();
	l4.repaint();
	l5.repaint();
	l6.repaint();
	l7.repaint();
	l8.repaint();
	l9.repaint();
	l10.repaint();
}


public synchronized void Select() {
	if (!isSelected())
	{
		selected = true;
		setColorAndRepaint(farbeSelected);
	}
}


public synchronized void Unselect() {
	if (isSelected())
	{
		selected = false;
		setColorAndRepaint(farbeHintergrund);
	}
}


public synchronized void Toggle() {
	if (isSelected())
	{
		Unselect();
	}
	else
	{
		Select();
	}
}


private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
	in.defaultReadObject();
	this.setColors();
}


private static String fixHTMLSpaces(String s) {
	int i = s.indexOf(" ");
	
	while (i >= 0)
	{
		s = s.substring(0,i) + "&nbsp;" + s.substring(i+1);
		i = s.indexOf(" ");
	}

	return s;
}


private static String text2HTML(String s) {
	int i = 0;
	
	while (i < s.length())
	{
		char c = s.charAt(i);
		
		if (c == '<')
		{
			s = s.substring(0,i) + "&lt;" + s.substring(i+1);
			i += 4;
		}
		else if (c == '>')
		{
			s = s.substring(0,i) + "&gt;" + s.substring(i+1);
			i += 4;
		}
		else if (c == '&')
		{
			s = s.substring(0,i) + "&amp;" + s.substring(i+1);
			i += 5;
		}
		else if (c == '\u00e4')
		{
			s = s.substring(0,i) + "&auml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00f6')
		{
			s = s.substring(0,i) + "&ouml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00fc')
		{
			s = s.substring(0,i) + "&uuml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00c4')
		{
			s = s.substring(0,i) + "&Auml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00d6')
		{
			s = s.substring(0,i) + "&Ouml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00dc')
		{
			s = s.substring(0,i) + "&Uuml;" + s.substring(i+1);
			i += 6;
		}
		else if (c == '\u00df')
		{
			s = s.substring(0,i) + "&szlig;" + s.substring(i+1);
			i += 7;
		}
		else
		{
			i++;
		}
	}

	return s;
}


public synchronized void saveHTML(BufferedWriter out, boolean namenKurz, boolean nameSteuerfrei) {
	long   aktKurs      = getKurs();
	long   tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	long   diff         = 0L;
	String kursString   = getKursString();
	String s,sk;
	long   pabs;

	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD ALIGN=LEFT>");
		out.write(text2HTML(getName(namenKurz)));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=RIGHT>");
		if (!nurBeobachten()) out.write(text2HTML(getStueckzahlString()));
		out.write("</TD>");
		out.newLine();
		
		out.write("  <TD ALIGN=RIGHT>");
		s = getKaufkursString();
		if (nurBeobachten() && (getKaufkurs() > 0L))
		{
			s = "(" + s + ")";
		}
		out.write(fixHTMLSpaces(text2HTML(s)));
		out.write("</TD>");
		out.newLine();
		if ((aktKurs > 0L) && (!nurBeobachten()))
		{
			diff = getKaufkurs() * getStueckzahl();
			kaufsumme += diff;
		}

		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(kursString)));
		out.write("</TD>");
		out.newLine();

		if (kursdatum != null)
		{
			s = kursdatum;
		}
		else
		{
			s = "";
		}
		out.write("  <TD ALIGN=CENTER>");
		out.write(text2HTML(s));
		out.write("</TD>");
		out.newLine();

		if (nurBeobachten())
		{
			sk = "";
		}
		else if (aktKurs > 0L)
		{
			pabs = getWert();
			sk = Waehrungen.getString(pabs,Waehrungen.DEM);
			
			aktsumme += pabs;
			diff = pabs - diff;
		}
		else
		{
			sk = kursString;
		}
		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(sk)));
		out.write("</TD>");
		out.newLine();

		if (aktKurs > 0L)
		{
			if (nurBeobachten())
			{
				sk = "(" + Waehrungen.getString(aktKurs-getKaufkurs(),Waehrungen.DEM) + ")";
			}
			else
			{
				sk = Waehrungen.getString(diff,Waehrungen.DEM);
			}
		}
		else
		{
			sk = kursString;
		}
		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(sk)));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=RIGHT>");
		if (nameSteuerfrei && istSteuerfrei() && (!nurBeobachten()))
		{
			out.write("steuerfrei");
		}
		else
		{
			out.write(text2HTML(getLaufzeitMonateString()));
		}
		out.write("</TD>");
		out.newLine();

		if (aktKurs > 0L)
		{
			pabs = (aktKurs * 10000L) / getKaufkurs() - 10000L;
			
			long kabs = pabs;
			if (kabs > 0L) kabs += 5L;
			else if (kabs < 0L) kabs -= 5L;
			kabs /= 10L;
			sk = new Double((double)kabs/10.0).toString();

			if (pabs > 0L) sk = "+" + sk;
		}
		else
		{
			pabs = 0L;
			sk = kursString;
		}
		out.write("  <TD ALIGN=RIGHT>");
		out.write(text2HTML(sk));
		out.write("</TD>");
		out.newLine();

		if (tageLaufzeit >= 360L)
		{
			if (aktKurs > 0L)
			{
				pabs = (pabs * 360L) / tageLaufzeit;
				if (pabs > 0L) pabs += 5L;
				else if (pabs < 0L) pabs -= 5L;
				pabs /= 10L;
				sk = new Double((double)pabs/10.0).toString();

				if (pabs > 0L) sk = "+" + sk;
			}
			else
			{
				pabs = 0L;
				sk = kursString;
			}
		}
		out.write("  <TD ALIGN=RIGHT>");
		out.write(text2HTML(sk));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=RIGHT>");
		out.write(text2HTML(getKaufdatum().toString()));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=CENTER>");
		out.write(text2HTML(getWKNString())+"<BR>"+text2HTML(getBoerse()));
		out.write("</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (IOException e) {}
}


public static void saveHeaderHTML(BufferedWriter out, String aktualisierung) {
	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN="+HTMLCOLS+">");
		out.write(text2HTML(aktualisierung));
		out.write("</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();

		out.write("<TR>");
		out.newLine();

		out.write("  <TH ALIGN=LEFT>");
		out.write("Aktienname");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("St&uuml;ck");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("Kaufkurs");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER COLSPAN=2>");
		out.write("akt. Kurs");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("akt. Wert");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("Differenz");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("Laufzeit");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("%<BR>absolut");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("%<BR>Jahr");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("Kaufdatum");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=CENTER>");
		out.write("WKN<BR>B&ouml;rse");
		out.write("</TH>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (IOException e) {}

	heute = new ADate();
	kaufsumme = 0L;
	aktsumme = 0L;
}


public static void saveFooterHTML(BufferedWriter out) {
	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=2 ALIGN=RIGHT>Summe Kaufwert:</TD>");
		out.newLine();
		
		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(Waehrungen.getString(kaufsumme,Waehrungen.DEM))));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN=2 ALIGN=RIGHT>Summe aktuell:</TD>");
		out.newLine();

		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(Waehrungen.getString(aktsumme,Waehrungen.DEM))));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN="+(HTMLCOLS-6)+"></TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
		
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=6 ALIGN=RIGHT>Differenz zum Kaufwert:</TD>");
		out.newLine();

		out.write("  <TD ALIGN=RIGHT>");
		out.write(fixHTMLSpaces(text2HTML(Waehrungen.getString(aktsumme-kaufsumme,Waehrungen.DEM))));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN="+(HTMLCOLS-7)+"></TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (IOException e) {}
}


public static void addSummen(Panel pTxt, String akt, String dif, boolean isRed) {
	AFrame.constrain(pTxt,new Label("Summe aktuell:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	AFrame.constrain(pTxt,new Label(akt),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	AFrame.constrain(pTxt,new Label("Differenz zum Kaufwert:"),2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,18,0,0);
	Label l = new Label(dif);
	if (isRed) l.setForeground(Color.red);
	AFrame.constrain(pTxt,l,3,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,2,0,0);
}


public static void addFooterToPanel(Panel p, int y, Panel pTxt) {
	String akt = Waehrungen.getString(aktsumme,Waehrungen.DEM);
	AFrame.constrain(p,new Label("Summe aktuell:",Label.RIGHT),3,y,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(akt,Label.RIGHT),5,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);

	AFrame.constrain(p,new Label("Summe Kaufwert:",Label.RIGHT),0,y,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(Waehrungen.getString(kaufsumme,Waehrungen.DEM),Label.RIGHT),2,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);

	AFrame.constrain(p,new Label("Differenz zum Kaufwert:",Label.RIGHT),3,y+1,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);
	
	long d = aktsumme-kaufsumme;
	String dif = Waehrungen.getString(d,Waehrungen.DEM);
	Label l = new Label(dif,Label.RIGHT);
	if (d < 0L)
	{
		l.setForeground(Color.red);
	}
	else if (d > 0L)
	{
		l.setForeground(Color.green.darker());
	}
	AFrame.constrain(p,l,6,y+1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);
	
	addSummen(pTxt,akt,dif,(d<0.0));
}


public static void setLastUpdateAndRepaint(String aktualisierung) {
	if (lupdate != null)
	{
		lupdate.setText(aktualisierung);
		lupdate.repaint();
	}
}


public static int addHeadingsToPanel(Panel p, String aktualisierung) {
	lupdate = new Label(aktualisierung);
	AFrame.constrain(p,lupdate,0,0,9,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,0,2,0);

	AFrame.constrain(p,new Label(" Aktienname"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  St\u00fcck",Label.RIGHT),1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  Kaufkurs",Label.RIGHT),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("akt. Kurs",Label.CENTER),3,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTH,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  akt. Wert",Label.RIGHT),5,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  Differenz",Label.RIGHT),6,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  Laufzeit",Label.RIGHT),7,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  %absolut",Label.RIGHT),8,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  %Jahr",Label.RIGHT),9,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  Kaufdatum ",Label.RIGHT),10,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  WKN.B\u00f6rse "),11,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,HEADERABSTAND,0);

	heute = new ADate();
	kaufsumme = 0L;
	aktsumme = 0L;
	farbeHintergrund = p.getBackground();
	
	return HEADROWS;
}


private String getLaufzeitTageString(long tageLaufzeit) {
	return new Long(tageLaufzeit).toString() + ((tageLaufzeit == 1L) ? " Tag" : " Tage");
}


private String getLaufzeitMonateString() {
	ADate kdate = getKaufdatum();
	
	int kaufjahr = kdate.getYear();
	int kaufmonat = kdate.getMonth();
	int kauftag = kdate.getDay();

	int jahr = heute.getYear();
	int monat = heute.getMonth();
	int tag = heute.getDay();

	int monate = 0;
	int tage = 0;

	if (kaufjahr < jahr)
	{
		if (jahr-kaufjahr > 1) monate += (jahr-kaufjahr-1)*12;
		
		monate += monat + (ADate.DECEMBER-kaufmonat);
	}
	else
	{
		if (monat-kaufmonat > 1) monate += (monat-kaufmonat-1);
	}
	
	if (tag >= kauftag)
	{
		if (kaufmonat != monat) monate++;

		tage += (tag-kauftag);
	}
	else
	{
		tage += (ADate.getDays(kaufjahr,kaufmonat) - kauftag) + tag;
	}
	
	String tstr = (new Integer(tage).toString())+((tage==1)?" Tag":" Tage");
	
	if (monate == 0)
	{
		return tstr;
	}
	else
	{
		return (new Integer(monate).toString())+((monate==1)?" Monat ":" Monate ")+tstr;
	}
}


public synchronized void addToPanel(Panel p, int y, boolean namenKurz, boolean nameSteuerfrei) {
	long   aktKurs = getKurs();
	long   tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	long   diff = 0L;
	int    row = y - HEADROWS;
	String kursString = getKursString();

	String s,sk;
	Label l;
	long pabs;
	
	/* (0) Aktienname: */
	
	l1 = new BALabel(" "+getName(namenKurz),row,Label.LEFT);
	l1.setForeground(farbeName);
	AFrame.constrain(p,l1,0,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (1) StŸckzahl: */

	if (nurBeobachten())
	{
		s = "";
	}
	else
	{
		s = getStueckzahlString();
	}
	l3 = new BALabel("  " + s,row);
	AFrame.constrain(p,l3,1,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);

	/* (2) Kaufkurs: */

	l8 = new BALabel("  "+getKaufkursString(),row);
	if ((aktKurs > 0L) && (!nurBeobachten()))
	{
		diff = getKaufkurs() * getStueckzahl();
		kaufsumme += diff;
	}
	else
	{
		l8.setForeground(Color.gray);
	}
	AFrame.constrain(p,l8,2,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (3,4) akt. Kurs: */

	l2 = new BALabel("  "+kursString,row);
	if (aktKurs > 0L)
	{
		if ((getTiefkurs() > 0L) && (aktKurs <= getTiefkurs()))
		{
			l2.setForeground(Color.red);
		}
		else if ((!doUseGrenze()) && (getHochkurs() > 0L) && (aktKurs >= getHochkurs()))
		{
			l2.setForeground(Color.green.darker());
		}
		else if ((doUseGrenze()) && (gewinngrenze > 0L) && (aktKurs >= gewinngrenze))
		{
			l2.setForeground(Color.green.darker());
		}
	}
	AFrame.constrain(p,l2,3,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	l11 = new BALabel(" "+getKursdatumString(),row);
	AFrame.constrain(p,l11,4,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (5) akt. Wert: */

	if (nurBeobachten())
	{
		sk = "";
	}
	else if (aktKurs > 0L)
	{
		pabs = getWert();
		sk = Waehrungen.getString(pabs,Waehrungen.DEM);
		
		aktsumme += pabs;
		diff = pabs - diff;
	}
	else
	{
		sk = kursString;
	}
	l4 = new BALabel("  "+sk,row);
	AFrame.constrain(p,l4,5,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (6) Differenz */

	if (aktKurs > 0L)
	{
		if (nurBeobachten())
		{
			sk = Waehrungen.getString(aktKurs-getKaufkurs(),Waehrungen.DEM);
		}
		else
		{
			sk = Waehrungen.getString(diff,Waehrungen.DEM);
		}
	}
	else
	{
		sk = kursString;
	}
	l12 = new BALabel("  "+sk,row);
	if (nurBeobachten())
	{
		l12.setForeground(Color.gray);
	}
	else if (diff > 0L)
	{
		l12.setForeground(Color.green.darker());
	}
	else if (diff < 0L)
	{
		l12.setForeground(Color.red);
	}
	AFrame.constrain(p,l12,6,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (7) Laufzeit: */

	boolean frei = istSteuerfrei() && (!nurBeobachten());
	if (nameSteuerfrei && frei)
	{
		l5 = new BALabel("  steuerfrei",row);
		l5.setForeground(farbeSteuerfrei);
	}
	else
	{
		l5 = new BALabel("  "+getLaufzeitMonateString(),row);
		if (frei) l5.setForeground(farbeSteuerfrei);
	}
	AFrame.constrain(p,l5,7,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (8) % absolut: */

	if (aktKurs > 0L)
	{
		pabs = (aktKurs * 10000L) / getKaufkurs() - 10000L;
		
		long kabs = pabs;
		if (kabs > 0L) kabs += 5L;
		else if (kabs < 0L) kabs -= 5L;
		kabs /= 10L;
		sk = new Double((double)kabs/10.0).toString();

		if (pabs > 0L) sk = "+" + sk;
	}
	else
	{
		pabs = 0L;
		sk = kursString;
	}
	l6 = new BALabel("  "+sk,row);
	if (pabs > 0L)
	{
		l6.setForeground(Color.green.darker());
	}
	else if (pabs < 0L)
	{
		l6.setForeground(Color.red);
	}
	AFrame.constrain(p,l6,8,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (9) % Jahr: */
	
	if (tageLaufzeit >= 360L)
	{
		if (aktKurs > 0L)
		{
			pabs = (pabs * 360L) / tageLaufzeit;
			if (pabs > 0L) pabs += 5L;
			else if (pabs < 0L) pabs -= 5L;
			pabs /= 10L;
			sk = new Double((double)pabs/10.0).toString();

			if (pabs > 0L) sk = "+" + sk;
		}
		else
		{
			pabs = 0L;
			sk = kursString;
		}
	}
	else
	{
		// Wert von sk/l6 Ÿbernehmen
	}
	l7 = new BALabel("  "+sk,row);
	if (tageLaufzeit < 360L)
	{
		l7.setForeground(Color.gray);
	}
	else
	{
		if (pabs > 0L)
		{
			l7.setForeground(Color.green.darker());
		}
		else if (pabs < 0L)
		{
			l7.setForeground(Color.red);
		}
	}
	AFrame.constrain(p,l7,9,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (10) Kaufdatum: */
	
	l9 = new BALabel("  "+getKaufdatum().toString()+" ",row);
	if (nurBeobachten())
	{
		l9.setForeground(Color.gray);
	}
	AFrame.constrain(p,l9,10,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (11) WKN.Bšrse: */
	
	l10 = new BALabel("  "+getWKNString()+"."+getBoerse()+" ",row,Label.LEFT);
	AFrame.constrain(p,l10,11,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* */
	
	Unselect();
}


public synchronized void infoDialogOpen() {
	if (infodialog == null)
	{
		infodialog = new AktieInfo(this);

		infoDialogSetValues(false);

		if (infodialog != null)
		{
			infodialog.show();
			AktienMan.hauptdialog.windowToFront(infodialog);
		}
	}
	else
	{
		infodialog.toFront();
	}
}


public synchronized void infoDialogSetValues(boolean draw) {
	if (infodialog != null)
	{
		infodialog.setValues(draw);
	}
}


public synchronized void infoDialogClosed() {
	infodialog = null;
}

}
