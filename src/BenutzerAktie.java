/**
 @author Thomas Much
 @version 1999-06-19
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;




public final class BenutzerAktie implements Serializable {

static final long serialVersionUID = 1997061300002L;

public static final long VALUE_MISSING =  0L;
public static final long VALUE_ERROR   = -1L;
public static final long VALUE_NA      = -2L;

public static final int HTMLCOLS = 12;

private static final String STR_MISSING = "<aktualisieren>";
private static final String STR_ERROR   = "<Fehler>";
private static final String STR_NA      = "n/a";
private static final String STR_1JAHR   = "<1 J.";

private static final int HEADROWS = 2;

private static final int ZEILENABSTAND = 0;
private static final int HEADERABSTAND = 3;
private static final int FOOTERABSTAND = 3;

private static ADate heute = new ADate();

private transient static long aktsumme = 0L;
private transient static long kaufsumme = 0L;
private transient static Label lupdate = null;
private transient static Color farbeHintergrund;
private transient static int konfigspekfrist;

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
private int watchwaehrung = Waehrungen.DEM;

private int waehrung = Waehrungen.DEM; /* KaufwŠhrung */
private int kurswaehrung = Waehrungen.DEM;
private int spekulationsfrist = 12;

private boolean nurdiese = false;
private boolean usegrenze = true;
private boolean watchonly = false;
private boolean dontUpdate = false;




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

	spekulationsfrist = 12/*AktienMan.properties.getInt("Konfig.Spekulationsfrist")*/;
	
	setupValues();
	setColors();
}



public synchronized void destroy() {
	infoDialogClose();
}



private void setupValues() {
	gewinngrenzeBerechnen();
	steuerfreiBerechnen();
}



private synchronized void gewinngrenzeBerechnen() {
	gewinngrenze = (prozgrenze == 0L) ? 0L : (getKaufkurs()*(Waehrungen.PRECISION*100L+prozgrenze))/(Waehrungen.PRECISION*100L);
}



private synchronized void steuerfreiBerechnen() {
	if (kaufdatum == null) kaufdatum = new ADate();
	
	int jahr,monat,tag;

	if (spekulationsfrist == 12)
	{
		jahr = kaufdatum.getYear() + 1;
		monat = kaufdatum.getMonth();
		tag = kaufdatum.getDay() + 1;

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
	}
	else
	{
		jahr = kaufdatum.getYear();
		monat = kaufdatum.getMonth() + 6;
		tag = kaufdatum.getDay() + 1;

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
	}

	steuerfrei = new ADate(jahr,monat,tag);
}



private synchronized void checkSpekulationsfrist(int neu) {
	if (spekulationsfrist != neu)
	{
		spekulationsfrist = neu;

		steuerfreiBerechnen();
	}
}



private void setColors() {
	farbeSteuerfrei = Color.yellow.darker().darker();
	farbeSelected = Color.lightGray;
	clearStatusRequesting();
}



public synchronized String getProzentString() {
	return (prozgrenze == 0L) ? "" : NumUtil.get00String(prozgrenze);
}



private String datum2String(String datum) {
	if (datum == null) return "";
	if (datum.length() == 0) return "";
	
	return "(" + datum + ")";
}



public synchronized String getKursdatumString() {
	return datum2String(kursdatum);
}



public synchronized long getHochkurs() {
	return hochkurs;
}



public synchronized String getHochkursString() {
	return (getHochkurs() == 0L) ? "" : NumUtil.get00String(getHochkurs());
}



public synchronized long getTiefkurs() {
	return tiefkurs;
}



public synchronized String getTiefkursString() {
	return (getTiefkurs() == 0L) ? "" : NumUtil.get00String(getTiefkurs());
}



public synchronized ADate getKaufdatum() {
	return (kaufdatum == null) ? new ADate() : kaufdatum;
}



public synchronized String getWKNString() {
	return wkn;
}



public synchronized boolean isBoerseFixed() {
	return nurdiese;
}



public synchronized boolean isFonds() {
	return boersenplatz.isFondsOnly();
}



public synchronized boolean doUseGrenze() {
	return usegrenze;
}



public synchronized boolean nurBeobachten() {
	return watchonly;
}



public synchronized boolean doNotUpdate() {
	return dontUpdate;
}



public synchronized String getVortageskursString() {
	return kurs2String(vortageskurs,getKurswaehrung());
}



public synchronized String getEroeffnungskursString() {
	return kurs2String(eroeffnungskurs,getKurswaehrung());
}



public synchronized String getHoechstkursString() {
	return kurs2String(hoechstkurs,getKurswaehrung());
}



public synchronized String getTiefstkursString() {
	return kurs2String(tiefstkurs,getKurswaehrung());
}



public synchronized String getHandelsvolumenString() {
	return "" + handelsvolumen;
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
	return kurs2String(watchhoechst,watchwaehrung);
}



public synchronized String getWatchHoechstDatumString() {
	return datum2String(watchhdate);
}



public synchronized String getWatchTiefstString() {
	return kurs2String(watchtiefst,watchwaehrung);
}



public synchronized String getWatchTiefstDatumString() {
	return datum2String(watchtdate);
}



public synchronized int getWKN() {
	int wkni = 0;
	
	try
	{
		wkni = Integer.parseInt(getWKNString());
	}
	catch (NumberFormatException e) {}

	return wkni;
}



public synchronized String getBoerse() {

	return boersenplatz.getKurz();
}



public synchronized String getBoerse(String boerse) {

	if ((isBoerseFixed()) || (boerse == null))
	{
		return getBoerse();
	}
	else if (boerse.length() == 0)
	{
		return getBoerse();
	}
	
	return boerse;
}



public synchronized String getRequest() {

	return getRequest("");
}



public synchronized String getRequest(String boerse) {

	return getWKNString() + "." + getBoerse(boerse);
}



public synchronized String getName(boolean kurz) {
	if (name.length() == 0)
	{
		return (getKurs() < 0L) ? STR_ERROR : STR_MISSING;
	}
	else
	{
		return (kurz) ? Aktienname.getKurzName(name) : name;
	}
}



public synchronized int getKaufwaehrung() {
	return waehrung;
}



public synchronized int getKurswaehrung() {
	return kurswaehrung;
}



public synchronized long getStueckzahl() {
	return stueckzahl;
}



public synchronized String getStueckzahlString() {
	return "" + getStueckzahl();
}



public synchronized void decStueckzahl(long delta) {
	if (delta > 0L)
	{
		stueckzahl -= delta;

		if (stueckzahl < 0L) stueckzahl = 0L;
	}
}



public synchronized long getKurs() {
	return kurs;
}



public synchronized String getRawVerkaufsKursString() {
	if (getKurs() <= 0L)
	{
		return "0";
	}
	else
	{
		return NumUtil.get00String(Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getVerkaufsWaehrung()));
	}
}



private String kurs2String(long k, int w) {
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
		return Waehrungen.getString(Waehrungen.exchange(k,w,Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung());
	}
}



public synchronized String getKursString() {
	return kurs2String(getKurs(),getKurswaehrung());
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
			return Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
		}
		else
		{
			return Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();
		}
	}
}



public synchronized long getKaufkurs() {
	return kaufkurs;
}



public synchronized String getKaufkursString() {
	return kurs2String(getKaufkurs(),getKaufwaehrung());
}



public synchronized boolean isEqual(String wkn, String platz, boolean compPlatz) {
	return isEqual(wkn,"",platz,compPlatz);
}



public synchronized boolean isEqual(String wkn, String kurz, String platz, boolean compPlatz) {
	if (compPlatz)
	{
		return isEqual(wkn,kurz,platz);
	}
	else
	{
		return isEqual(wkn,kurz);
	}
}



private synchronized boolean isEqual(String wkn, String kurz, String platz) {
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



private synchronized boolean isEqual(String wkn, String kurz) {
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



public synchronized boolean istSteuerfrei() {
	return heute.after(steuerfrei);
}



public synchronized void setValues(long kurs) {
	setValues("",kurs);
}



public synchronized void setValues(String name, long kurs) {
	setValues(name,kurs,"",VALUE_NA,VALUE_NA,VALUE_NA,VALUE_NA,0L,getKurswaehrung());
}



public synchronized void setValues(String name, long kurs, String kursdatum,
									long vortageskurs, long eroeffnungskurs,
									long hoechstkurs, long tiefstkurs,
									long handelsvolumen, int kurswaehrung) {
	if (!doNotUpdate())
	{
		if (name.length() > 0)
		{
			if ((this.name.length() == 0) || AktienMan.properties.getBoolean("Konfig.Aktiennamen"))
			{
				this.name = name;
			}
		}

		this.kurswaehrung = kurswaehrung;
		this.kurs = kurs;
		this.kursdatum = kursdatum;
		this.vortageskurs = vortageskurs;
		this.eroeffnungskurs = eroeffnungskurs;
		this.hoechstkurs = hoechstkurs;
		this.tiefstkurs = tiefstkurs;
		this.handelsvolumen = handelsvolumen;
		
		if ((kaufkurs == VALUE_MISSING) && nurBeobachten() && (kurs > VALUE_MISSING))
		{
			kaufkurs = kurs;
			kaufdatum = new ADate();
			
			waehrung = kurswaehrung;
			
			setupValues();
		}

		if ((kurs > VALUE_MISSING) && (watchstart == null))
		{
			watchstart = new ADate();
		}
		
		if (kurswaehrung != watchwaehrung)
		{
			watchhoechst = Waehrungen.exchange(watchhoechst,watchwaehrung,kurswaehrung);
			watchtiefst = Waehrungen.exchange(watchtiefst,watchwaehrung,kurswaehrung);
			watchwaehrung = kurswaehrung;
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
	}
	
	clearStatusRequesting();
	infoDialogSetValues(true);
}



public synchronized void changeValues(String newName, String newWKN, Boersenplatz newBp,
								boolean newNurDiese, ADate newDate, long newKaufkurs,
								long newAnz, long newHoch, long newTief, long newGrenze,
								int neueWaehrung, boolean newUseGrenze, boolean newWatchonly,
								boolean newDontUpdate) {

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
	dontUpdate = newDontUpdate;
	waehrung = neueWaehrung;

	setupValues();
	
	if (reset)
	{
		kurs = VALUE_MISSING;
		kursdatum = "";
	}
	
	infoDialogSetValues(true);
}


public synchronized void setStatusRequestingAndRepaint() {
	farbeName = Color.blue;
	l1.setForeground(farbeName);
	l1.repaint();
}


public synchronized void setStatusErrorAndRepaint() {
	farbeName = Color.red;
	l1.setForeground(farbeName);
	l1.repaint();
}



public synchronized void clearStatusRequesting() {
	farbeName = Color.black;
}



public synchronized boolean isSelected() {
	return selected;
}



private synchronized void setColorAndRepaint(Color c) {
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



public synchronized void saveHTML(BufferedWriter out, boolean namenKurz, boolean nameSteuerfrei, boolean calcJahr) {
	long   aktKurs      = Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
	long   tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	long   diff         = 0L;
	String kursString   = getKursString();
	String s,sk;
	long   pabs;

	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD ALIGN=\"left\">");
		out.write(HTMLUtil.toHTML(getName(namenKurz)));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		if (nurBeobachten())
		{
			out.write("&nbsp;");
		}
		else
		{
			out.write(HTMLUtil.toHTML(getStueckzahlString()));
		}
		out.write("</TD>");
		out.newLine();
		
		out.write("  <TD ALIGN=\"right\">");
		s = getKaufkursString();
		if (nurBeobachten() && (getKaufkurs() > 0L))
		{
			s = "(" + s + ")";
		}
		out.write(HTMLUtil.toNbspHTML(s));
		out.write("</TD>");
		out.newLine();
		if ((aktKurs > 0L) && (!nurBeobachten()))
		{
			diff = Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();
			kaufsumme += diff;
		}

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(kursString));
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
		out.write("  <TD ALIGN=\"center\">");
		out.write(HTMLUtil.toHTML(s));
		out.write("</TD>");
		out.newLine();

		if (nurBeobachten())
		{
			sk = " ";
		}
		else if (aktKurs > 0L)
		{
			pabs = getWert();
			sk = Waehrungen.getString(pabs,Waehrungen.getListenWaehrung());
			
			aktsumme += pabs;
			diff = pabs - diff;
		}
		else
		{
			sk = kursString;
		}
		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(sk));
		out.write("</TD>");
		out.newLine();

		if (aktKurs > 0L)
		{
			if (nurBeobachten())
			{
				sk = "(" + Waehrungen.getString(aktKurs-Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung()) + ")";
			}
			else
			{
				sk = Waehrungen.getString(diff,Waehrungen.getListenWaehrung());
			}
		}
		else
		{
			sk = kursString;
		}
		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(sk));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		if (nameSteuerfrei && istSteuerfrei() && (!nurBeobachten()))
		{
			out.write("steuerfrei");
		}
		else
		{
			out.write(HTMLUtil.toHTML(getLaufzeitMonateString()));
		}
		out.write("</TD>");
		out.newLine();

		if (aktKurs > 0L)
		{
			try
			{
				pabs = (aktKurs * 10000L) / Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) - 10000L;
				
				long kabs = pabs;
				if (kabs > 0L) kabs += 5L;
				else if (kabs < 0L) kabs -= 5L;
				kabs /= 10L;
				sk = "" + ((double)kabs/10.0);

				if (pabs > 0L) sk = "+" + sk;
			}
			catch (ArithmeticException e)
			{
				pabs = 0L;
				sk = STR_NA;
			}
		}
		else
		{
			pabs = 0L;
			sk = kursString;
		}
		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toHTML(sk));
		out.write("</TD>");
		out.newLine();

		if (calcJahr || (tageLaufzeit >= 360L))
		{
			if (aktKurs > 0L)
			{
				pabs = (pabs * 360L) / (tageLaufzeit+1L);
				if (pabs > 0L) pabs += 5L;
				else if (pabs < 0L) pabs -= 5L;
				pabs /= 10L;
				sk = "" + ((double)pabs/10.0);

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
			sk = STR_1JAHR;
		}
		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(sk));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toHTML(getKaufdatum().toString()));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"center\">");
		out.write(HTMLUtil.toHTML(getWKNString())+"<BR>"+HTMLUtil.toHTML(getBoerse()));
		out.write("</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (IOException e) {}
}



public synchronized static void saveHeaderHTML(BufferedWriter out, String aktualisierung) {
	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=\""+HTMLCOLS+"\">");
		out.write(HTMLUtil.toHTML(aktualisierung));
		out.write("</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();

		out.write("<TR>");
		out.newLine();

		out.write("  <TH ALIGN=\"left\">");
		out.write("Aktienname");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("St&uuml;ck");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("Kaufkurs");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\" COLSPAN=\"2\">");
		out.write("akt. Kurs");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("akt. Wert");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("Differenz");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("Laufzeit");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("%<BR>absolut");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("%<BR>Jahr");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
		out.write("Kaufdatum");
		out.write("</TH>");
		out.newLine();

		out.write("  <TH ALIGN=\"center\">");
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



public synchronized static void saveFooterHTML(BufferedWriter out) {
	try
	{
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=\"2\" ALIGN=\"right\">Summe Kaufwert:</TD>");
		out.newLine();
		
		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(kaufsumme,Waehrungen.getListenWaehrung())));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN=\"2\" ALIGN=\"right\">Summe aktuell:</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(aktsumme,Waehrungen.getListenWaehrung())));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN=\""+(HTMLCOLS-6)+"\">&nbsp;</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
		
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=\"6\" ALIGN=\"right\">Differenz zum Kaufwert:</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(aktsumme-kaufsumme,Waehrungen.getListenWaehrung())));
		out.write("</TD>");
		out.newLine();
		
		if (kaufsumme > 0L)
		{
			out.write("  <TD COLSPAN=\"2\" ALIGN=\"right\">");

			long kdif = ((aktsumme * 10000L) / kaufsumme) - 10000L;
			if (kdif > 0L) kdif += 5L;
			else if (kdif < 0L) kdif -= 5L;
			kdif /= 10L;
			String percdif = "" + ((double)kdif/10.0);
			if (kdif > 0L) percdif = "+" + percdif;
			out.write(HTMLUtil.toNbspHTML(percdif));

			out.write("</TD>");
			out.newLine();
			
			out.write("  <TD COLSPAN=\""+(HTMLCOLS-9)+"\">&nbsp;</TD>");
			out.newLine();
		}
		else
		{
			out.write("  <TD COLSPAN=\""+(HTMLCOLS-7)+"\">&nbsp;</TD>");
			out.newLine();
		}

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (IOException e) {}
}



public synchronized static void addSummen(Panel pTxt, String akt, String dif, String percdif, boolean isRed) {
	AFrame.constrain(pTxt,new Label("Summe aktuell:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	AFrame.constrain(pTxt,new Label(akt),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	AFrame.constrain(pTxt,new Label("Differenz zum Kaufwert:"),2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,18,0,0);
	
	if (percdif.length() > 0)
	{
		dif += "  (" + percdif + "%)";
	}
	
	Label l = new Label(dif);
	if (isRed) l.setForeground(Color.red);
	AFrame.constrain(pTxt,l,3,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,2,0,0);
}



public synchronized static void addFooterToPanel(Panel p, int y, Panel pTxt) {
	String akt = Waehrungen.getString(aktsumme,Waehrungen.getListenWaehrung());
	AFrame.constrain(p,new Label("Summe aktuell:",Label.RIGHT),3,y,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(akt,Label.RIGHT),5,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);

	AFrame.constrain(p,new Label("Summe Kaufwert:",Label.RIGHT),0,y,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(Waehrungen.getString(kaufsumme,Waehrungen.getListenWaehrung()),Label.RIGHT),2,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);

	AFrame.constrain(p,new Label("Differenz zum Kaufwert:",Label.RIGHT),3,y+1,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);
	
	long d = aktsumme-kaufsumme;
	String dif = Waehrungen.getString(d,Waehrungen.getListenWaehrung());
	Label l = new Label("  " + dif,Label.RIGHT);
	if (d < 0L)
	{
		l.setForeground(Color.red);
	}
	else if (d > 0L)
	{
		l.setForeground(Color.green.darker());
	}
	AFrame.constrain(p,l,6,y+1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	
	String percdif = "";

	if (kaufsumme > 0L)
	{
		long kdif = ((aktsumme * 10000L) / kaufsumme) - 10000L;
		if (kdif > 0L) kdif += 5L;
		else if (kdif < 0L) kdif -= 5L;
		kdif /= 10L;
		percdif = "" + ((double)kdif/10.0);
		if (kdif > 0L) percdif = "+" + percdif;
		
		l = new Label("  " + percdif,Label.RIGHT);
		if (kdif > 0L)
		{
			l.setForeground(Color.green.darker());
		}
		else if (kdif < 0L)
		{
			l.setForeground(Color.red);
		}
		AFrame.constrain(p,l,8,y+1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	}
	
	addSummen(pTxt,akt,dif,percdif,(d<0.0));
}



public synchronized static void setLastUpdateAndRepaint(String aktualisierung) {
	if (lupdate != null)
	{
		lupdate.setText(aktualisierung);
		lupdate.repaint();
	}
}



public synchronized static int addHeadingsToPanel(Panel p, String aktualisierung) {
	lupdate = new Label(aktualisierung);
	AFrame.constrain(p,lupdate,0,0,9,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,0,2,0);

	AFrame.constrain(p,new Label(" Aktienname"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  St\u00fcck",Label.RIGHT),1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  Kaufkurs",Label.RIGHT),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label("  akt. Kurs",Label.RIGHT),3,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
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
	konfigspekfrist = 12/*AktienMan.properties.getInt("Konfig.Spekulationsfrist")*/;

	return HEADROWS;
}



private synchronized String getLaufzeitTageString(long tageLaufzeit) {
	return "" + tageLaufzeit + ((tageLaufzeit == 1L) ? " Tag" : " Tage");
}



private synchronized String getLaufzeitMonateString() {
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
		if (jahr-kaufjahr > 1)
		{
			monate += (jahr-kaufjahr-1)*12;
		}
		
		monate += (monat-1) + (ADate.DECEMBER-kaufmonat);
	}
	else
	{
		if (monat-kaufmonat > 1)
		{
			monate += (monat-kaufmonat-1);
		}
	}
	
	if (tag >= kauftag)
	{
		if ((kaufmonat != monat) || (jahr > kaufjahr))
		{
			monate++;
		}

		tage += (tag-kauftag);
	}
	else
	{
		tage += (ADate.getDays(kaufjahr,kaufmonat) - kauftag) + tag;
	}
	
	String tstr = "" + tage + ((tage==1)?" Tag":" Tage");
	
	if (monate == 0)
	{
		return tstr;
	}
	else
	{
		return "" + monate + ((monate==1)?" Monat ":" Monate ") + tstr;
	}
}



public synchronized void addToPanel(Panel p, int y, boolean namenKurz, boolean nameSteuerfrei, boolean calcJahr) {
	long   aktKurs = Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
	long   tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	long   diff = 0L;
	int    row = y - HEADROWS;
	String kursString = getKursString();

	String s,sk;
	Label l;
	long pabs;
	
	checkSpekulationsfrist(konfigspekfrist);
	
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
		diff = Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();
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
		if ((getTiefkurs() > 0L) && (aktKurs <= Waehrungen.exchange(getTiefkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung())))
		{
			l2.setForeground(Color.red);
		}
		else if ((!doUseGrenze()) && (getHochkurs() > 0L) && (aktKurs >= Waehrungen.exchange(getHochkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung())))
		{
			l2.setForeground(Color.green.darker());
		}
		else if ((doUseGrenze()) && (gewinngrenze > 0L) && (aktKurs >= Waehrungen.exchange(gewinngrenze,getKaufwaehrung(),Waehrungen.getListenWaehrung())))
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
		sk = Waehrungen.getString(pabs,Waehrungen.getListenWaehrung());
		
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
			sk = Waehrungen.getString(aktKurs-Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung());
		}
		else
		{
			sk = Waehrungen.getString(diff,Waehrungen.getListenWaehrung());
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
		try
		{
			pabs = (aktKurs * 10000L) / Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) - 10000L;
			
			long kabs = pabs;
			if (kabs > 0L) kabs += 5L;
			else if (kabs < 0L) kabs -= 5L;
			kabs /= 10L;
			sk = "" + ((double)kabs/10.0);

			if (pabs > 0L) sk = "+" + sk;
		}
		catch (ArithmeticException e)
		{
			pabs = 0L;
			sk = STR_NA;
		}
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
	
	if (calcJahr || (tageLaufzeit >= 360L))
	{
		if (aktKurs > 0L)
		{
			pabs = (pabs * 360L) / (tageLaufzeit+1L);
			if (pabs > 0L) pabs += 5L;
			else if (pabs < 0L) pabs -= 5L;
			pabs /= 10L;
			sk = "" + ((double)pabs/10.0);

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
		sk = STR_1JAHR;
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



public synchronized void infoDialogClose() {
	if (infodialog != null)
	{
		infodialog.dispose();
		infodialog = null;
	}
}



public synchronized void infoDialogClosed() {
	infodialog = null;
}

}
