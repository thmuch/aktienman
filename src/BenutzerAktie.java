/**
 @author Thomas Much
 @version 2002-01-13

 2002-01-13
    infoDialogClose ruft vor dispose nun setVisible(false) auf
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;




public final class BenutzerAktie implements Serializable,Cloneable {

static final long serialVersionUID = 1997061300002L;

public static final long VALUE_MISSING =  0L;
public static final long VALUE_ERROR   = -1L;
public static final long VALUE_NA      = -2L;

public static final int HTMLCOLS = 12;

private static final String STR_MISSING   = "<aktualisieren>";
private static final String STR_ERROR     = "<Fehler>";
private static final String STR_NA        = "n/a";
private static final String STR_1JAHR     = "<1 J.";
private static final String STR_DIVIDENDE = "D+";
private static final String STR_SPACE     = "  ";

private static final int HEADROWS = 2;

private static final int ZEILENABSTAND = 0;
private static final int HEADERABSTAND = 3;
private static final int FOOTERABSTAND = 3;

private static final int X_NAME       =  0;
private static final int X_STUECKZAHL =  1;
private static final int X_DIVIDENDE  =  2;
private static final int X_KAUFKURS   =  3;
private static final int X_WARNING    =  4;
private static final int X_AKTKURS    =  5;
private static final int X_KURSDATUM  =  6;
private static final int X_ARROW      =  7;
private static final int X_AKTWERT    =  8;
private static final int X_DIFFERENZ  =  9;
private static final int X_LAUFZEIT   = 10;
private static final int X_PABSOLUT   = 11;
private static final int X_PJAHR      = 12;
private static final int X_KAUFDATUM  = 13;
private static final int X_WKNBOERSE  = 14;

private static final int X_LEN        = X_WKNBOERSE + 1;
private static final int X_KURSSTART  = X_WARNING;
private static final int X_KURSLEN    =  2;

private static ADate heute = new ADate();

private transient static long aktsumme = 0L;
private transient static long kaufsumme = 0L;
private transient static long difsteuer = 0L;
private transient static long diffrei = 0L;
private transient static long dividenden = 0L;

private transient static Label lupdate = null;
private transient static Color farbeHintergrund;
private transient static int konfigspekfrist;

private transient Color farbeSteuerfrei;
private transient Color farbeName;
private transient Color farbeSelected;
private transient boolean selected = false;
private transient AktieInfo infodialog = null;
private transient BALabel l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l15;
private transient BAImageArrowCanvas l13;
private transient BAImageWarnCanvas l14;

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

private long dividende = 0L;
private ADate divdate = null;

private ADate fixDate = null;
private ADate watchstart = null;
private long watchhoechst = VALUE_MISSING;
private long watchtiefst = VALUE_MISSING;
private String watchhdate = null;
private String watchtdate = null;
private int watchwaehrung = Waehrungen.DEM;
private String symbol = null;

private int waehrung = Waehrungen.DEM; /* KaufwŠhrung */
private int kurswaehrung = Waehrungen.DEM;
private int spekulationsfrist = 12;
private int oldWarnType = BAImageWarnCanvas.WARN_INIT;

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
	this.hochkurs = hochkurs;
	this.tiefkurs = tiefkurs;
	this.waehrung = waehrung;
	this.nurdiese = nurdiese;
	this.usegrenze = usegrenze;
	this.watchonly = watchonly;
	prozgrenze = grenze;
	boersenplatz = platz;
	
	setStueckzahl(stueckzahl);
	setSymbol(null);

	kursdatum = "";

	spekulationsfrist = 12/*AktienMan.properties.getInt("Konfig.Spekulationsfrist")*/;
	
	setupValues();
	setColors();
}



public synchronized Object clone() {

	/* Achtung, derzeit nur "shallow copy", da clone nur von listeSelektierteAktieCopyMove verwendet wird */

	BenutzerAktie ba;

	try
	{
		ba = (BenutzerAktie)super.clone();
	}
	catch (Exception e)
	{
		ba = null;
	}

	return ba;
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



private String datum2String(String datum, boolean klammern) {

	if (datum == null) return "";
	if (datum.length() == 0) return "";
	
	if (klammern)
	{
		datum = "(" + datum + ")";
	}
	
	return datum;
}



public synchronized String getKursdatumString() {

	return datum2String(kursdatum,true);
}



public synchronized String getKursdatumRawString() {

	return datum2String(kursdatum,false);
}



public synchronized String getFixedDateString() {

	return (fixDate == null) ? kursdatum : fixDate.toString();
}



public synchronized ADate getFixedDate() {

	return (fixDate == null) ? heute : fixDate;
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



public synchronized boolean hasWKN(String cmp) {

	return wkn.equalsIgnoreCase(cmp);
}



public synchronized void setSymbol(String sym) {

	if (sym == null)
	{
		symbol = null;
		return;
	}
	
	sym = sym.trim();
	
	if (sym.length() == 0)
	{
		symbol = null;
		return;
	}
	
	symbol = sym;
}



public synchronized String getSymbol() {

	return (symbol == null) ? "" : symbol;
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



public synchronized long getVortageskurs() {

	return vortageskurs;
}



public synchronized String getVortageskursString() {

	return kurs2String(getVortageskurs(),getKurswaehrung());
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

	return datum2String(watchhdate,true);
}



public synchronized String getWatchTiefstString() {

	return kurs2String(watchtiefst,watchwaehrung);
}



public synchronized String getWatchTiefstDatumString() {

	return datum2String(watchtdate,true);
}



public synchronized int getWKN() {

	int wkni = 0;
	
	try
	{
		wkni = Integer.parseInt(getWKNString());
	}
	catch (Exception e) {}

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
	else if ((boerse.length() == 0) || (isFonds()))
	{
		return getBoerse();
	}
	
	return boerse;
}



public synchronized String getRequest() {

	return getRequest("");
}



public synchronized String getRequest(String boerse) {

	return getRequestWKN() + getBoerse(boerse);
}



public synchronized String getRequestWKN() {

	String req = getSymbol();
	
	if (req.length() == 0)
	{
		req = getWKNString();
	}

	return req + ".";
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
		setStueckzahl(getStueckzahl() - delta);
	}
}



public synchronized void setStueckzahl(long stueckzahl) {

	this.stueckzahl = (stueckzahl < 0L) ? 0L : stueckzahl;
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



public synchronized long getDividende() {

	return dividende;
}



public synchronized String getDividendeString() {

	return (getDividende() == 0L) ? "" : NumUtil.get00String(getDividende());
}



public synchronized String getDividendeDatum() {

	return (divdate == null) ? "" : divdate.toString();
}



private synchronized void setDividende(long div, ADate divdate) {

	dividende = (div < 0L) ? 0L : div;
	
	this.divdate = divdate;
}



private synchronized boolean hasDividende() {

	if (divdate == null) return false;
	if (nurBeobachten()) return false;
	if (getDividende() <= 0L) return false;
	
	if (getKaufdatum().after(divdate)) return false;
	
	return (!heute.before(divdate));
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

	if ((doNotUpdate()) && (fixDate != null))
	{
		return fixDate.after(steuerfrei);
	}
	else
	{
		return heute.after(steuerfrei);
	}
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
			if ((this.name.length() == 0) || BenutzerListe.useOnlineNames())
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
								boolean newDontUpdate, long newAktKurs, ADate newAktDate,
								long newdiv, ADate newdivdate) {

	boolean reset = ((!newWKN.equalsIgnoreCase(getWKNString())) || (!newBp.getKurz().equalsIgnoreCase(getBoerse())));
	
	name = newName;
	wkn = newWKN;
	boersenplatz = newBp;
	nurdiese = newNurDiese;
	kaufdatum = newDate;
	kaufkurs = newKaufkurs;
	hochkurs = newHoch;
	tiefkurs = newTief;
	prozgrenze = newGrenze;
	usegrenze = newUseGrenze;
	watchonly = newWatchonly;
	dontUpdate = newDontUpdate;
	waehrung = neueWaehrung;
	fixDate = newAktDate;
	
	setStueckzahl(newAnz);
	setSymbol(null);
	setDividende(newdiv,newdivdate);

	setupValues();
	
	if (dontUpdate)
	{
		fixAktie(fixDate,newAktKurs,getKaufwaehrung());
	}
	else if (reset)
	{
		kurs = VALUE_MISSING;
		kursdatum = "";
	}
	
	infoDialogSetValues(true);
}



public synchronized void changeKaufkurs(long neuerKaufkurs, int neueKaufwaehrung, ADate neuesKaufdatum) {

	if (neuerKaufkurs > 0L)
	{
		kaufkurs  = neuerKaufkurs;
		kaufdatum = neuesKaufdatum;
		waehrung  = neueKaufwaehrung;

		setupValues();

		infoDialogSetValues(true);
	}

	/* neue Anzeige muss von Au§erhalb erfolgen! */
}



public synchronized void split(double faktor) {

	if (faktor > 1.0)
	{
		setStueckzahl((long)((double)getStueckzahl() * faktor));
		
		kaufkurs = Math.round((double)getKaufkurs() / faktor);
		hochkurs = Math.round((double)getHochkurs() / faktor);
		tiefkurs = Math.round((double)getTiefkurs() / faktor);
		
		kurs = VALUE_MISSING;
		kursdatum = "";

		vortageskurs = VALUE_MISSING;
		eroeffnungskurs = VALUE_MISSING;
		hoechstkurs = VALUE_MISSING;
		tiefstkurs = VALUE_MISSING;

		watchhoechst = VALUE_MISSING;
		watchtiefst = VALUE_MISSING;
		watchhdate = null;
		watchtdate = null;
		watchstart = null;
	}

	/* neue Anzeige muss von Au§erhalb erfolgen! */
}



public synchronized void fixAktie(ADate fixDatum, long fixKurs, int fixWaehrung) {

	dontUpdate = true;
	fixDate = fixDatum;

	if (fixKurs > VALUE_MISSING)
	{
		kurs = Waehrungen.exchange(fixKurs,fixWaehrung,getKurswaehrung());
		
		if (fixDate != null)
		{
			kursdatum = fixDate.toString();
		}
		else
		{
			kursdatum = "";
		}
	}
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
	l15.setBackground(c);
	l14.setBackground(c);
	l2.setBackground(c);
	l11.setBackground(c);
	l12.setBackground(c);
	l13.setBackground(c);
	l3.setBackground(c);
	l4.setBackground(c);
	l5.setBackground(c);
	l6.setBackground(c);
	l7.setBackground(c);
	l8.setBackground(c);
	l9.setBackground(c);
	l10.setBackground(c);
	
	l1.repaint();
	l15.repaint();
	l14.repaint();
	l2.repaint();
	l11.repaint();
	l12.repaint();
	l13.repaint();
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



public synchronized void saveCSV(BufferedWriter out, boolean namenKurz, boolean calcJahr) {

	long aktKurs = Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
	long tageLaufzeit;
	
	if ((doNotUpdate()) && (fixDate != null))
	{
		tageLaufzeit = fixDate.getSerialDate() - getKaufdatum().getSerialDate();
	}
	else
	{
		tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	}

	try
	{
		out.write("\"" + getName(namenKurz) + "\";");

		if (!nurBeobachten())
		{
			out.write(getStueckzahlString());
		}
		out.write(";");
		
		long kaufkurs = Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung());
		if (kaufkurs > 0L)
		{
			out.write(("" + (kaufkurs / 100.0)).replace('.',','));
		}
		out.write(";");

		if (aktKurs > 0L)
		{
			out.write(("" + (aktKurs / 100.0)).replace('.',','));
		}
		out.write(";");
		
		if ((kursdatum != null) && (kursdatum.length() > 0))
		{
			out.write("\"(" + kursdatum + ")\"");
		}
		out.write(";");

		if ((kaufkurs > 0L) && (aktKurs > 0L) && (!nurBeobachten()))
		{
			out.write(("" + (getWert() / 100.0)).replace('.',','));
		}
		out.write(";");

		if ((kaufkurs > 0L) && (aktKurs > 0L))
		{
			long stueck = nurBeobachten() ? 1L : getStueckzahl();
			
			long diff = getWert() - stueck * Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung());

			out.write(("" + (diff / 100.0)).replace('.',','));
		}
		out.write(";");
		
		long pabs = 0L;

		if (aktKurs > 0L)
		{
			try
			{
				pabs = (aktKurs * 10000L) / Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) - 10000L;
				
				long kabs = pabs;
				if (kabs > 0L) kabs += 5L;
				else if (kabs < 0L) kabs -= 5L;
				kabs /= 10L;

				out.write(("" + (kabs / 10.0)).replace('.',','));
			}
			catch (Exception e) {}
		}
		out.write(";");

		if (calcJahr || (tageLaufzeit >= 360L))
		{
			if (aktKurs > 0L)
			{
				pabs = (pabs * 360L) / (tageLaufzeit+1L);
				if (pabs > 0L) pabs += 5L;
				else if (pabs < 0L) pabs -= 5L;
				pabs /= 10L;
				
				out.write(("" + (pabs / 10.0)).replace('.',','));
			}
		}
		out.write(";");

		out.write(getKaufdatum().toString() + ";");

		out.write("\"" + getWKNString() + "." + getBoerse() + "\"");
		
		out.newLine();
	}
	catch (Exception e) {}
}



public synchronized void saveHTML(BufferedWriter out, boolean namenKurz, boolean nameSteuerfrei, boolean calcJahr) {

	long   aktKurs      = Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
	long   diff         = 0L;
	String kursString   = getKursString();
	String s,sk;
	long   pabs,tageLaufzeit;
	
	if ((doNotUpdate()) && (fixDate != null))
	{
		tageLaufzeit = fixDate.getSerialDate() - getKaufdatum().getSerialDate();
	}
	else
	{
		tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	}

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
		
		if (hasDividende())
		{
			dividenden += Waehrungen.exchange(getDividende(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();
		}
		
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
				
				if (istSteuerfrei())
				{
					diffrei += diff;
				}
				else
				{
					difsteuer += diff;
				}
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
			catch (Exception e)
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
		out.write(HTMLUtil.toHTML(getWKNString())+" "+HTMLUtil.toHTML(getBoerse()));
		out.write("</TD>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (Exception e) {}
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
		out.write("WKN B&ouml;rse");
		out.write("</TH>");
		out.newLine();

		out.write("</TR>");
		out.newLine();
		out.newLine();
	}
	catch (Exception e) {}

	heute = new ADate();
	kaufsumme = 0L;
	aktsumme = 0L;
	difsteuer = 0L;
	diffrei = 0L;
	dividenden = 0L;
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
		
		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=\"6\" ALIGN=\"right\">davon steuerfrei (*):</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(diffrei,Waehrungen.getListenWaehrung())));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN=\""+(HTMLCOLS-7)+"\">&nbsp;</TD>");
		out.newLine();
		
		out.write("</TR>");
		out.newLine();
		out.newLine();

		out.write("<TR>");
		out.newLine();

		out.write("  <TD COLSPAN=\"6\" ALIGN=\"right\">davon zu versteuern:</TD>");
		out.newLine();

		out.write("  <TD ALIGN=\"right\">");
		out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(difsteuer,Waehrungen.getListenWaehrung())));
		out.write("</TD>");
		out.newLine();

		out.write("  <TD COLSPAN=\""+(HTMLCOLS-7)+"\">&nbsp;</TD>");
		out.newLine();
		
		out.write("</TR>");
		out.newLine();
		out.newLine();

		if (dividenden > 0L)
		{
			out.write("<TR>");
			out.newLine();

			out.write("  <TD COLSPAN=\"6\" ALIGN=\"right\">Dividenden:</TD>");
			out.newLine();

			out.write("  <TD ALIGN=\"right\">");
			out.write(HTMLUtil.toNbspHTML(Waehrungen.getString(dividenden,Waehrungen.getListenWaehrung())));
			out.write("</TD>");
			out.newLine();

			out.write("  <TD COLSPAN=\""+(HTMLCOLS-7)+"\">&nbsp;</TD>");
			out.newLine();

			out.write("</TR>");
			out.newLine();
			out.newLine();
		}
	}
	catch (Exception e) {}
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
	AFrame.constrain(p,new Label("Summe aktuell:",Label.RIGHT),X_KURSSTART,y,X_AKTWERT-X_KURSSTART,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(akt,Label.RIGHT),X_AKTWERT,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);

	AFrame.constrain(p,new Label("Summe Kaufwert:",Label.RIGHT),0,y,X_KAUFKURS,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	AFrame.constrain(p,new Label(Waehrungen.getString(kaufsumme,Waehrungen.getListenWaehrung()),Label.RIGHT),X_KAUFKURS,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,FOOTERABSTAND,10,0,0);
	
	int xval   = X_DIFFERENZ;
	int xstart = X_KURSSTART;
	int xlen   = xval - xstart;
	
	AFrame.constrain(p,new Label("Differenz zum Kaufwert:",Label.RIGHT),xstart,y+1,xlen,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);

	AFrame.constrain(p,new Label("davon steuerfrei (*):",Label.RIGHT),xstart,y+2,xlen,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);
	AFrame.constrain(p,new Label("davon zu versteuern:",Label.RIGHT),xstart,y+3,xlen,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);

	if (dividenden > 0L)
	{
		AFrame.constrain(p,new Label("Dividenden:",Label.RIGHT),xstart,y+4,xlen,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,10,0,0);
		AFrame.constrain(p,new Label(Waehrungen.getString(dividenden,Waehrungen.getListenWaehrung()),Label.RIGHT),xval,y+4,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	}
	
	long d = aktsumme-kaufsumme;
	String dif = Waehrungen.getString(d,Waehrungen.getListenWaehrung());
	Label l = new Label(STR_SPACE + dif,Label.RIGHT);
	if (d < 0L)
	{
		l.setForeground(Color.red);
	}
	else if (d > 0L)
	{
		l.setForeground(Color.green.darker());
	}
	AFrame.constrain(p,l,xval,y+1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	
	String percdif = "";

	if (kaufsumme > 0L)
	{
		long kdif = ((aktsumme * 10000L) / kaufsumme) - 10000L;
		if (kdif > 0L) kdif += 5L;
		else if (kdif < 0L) kdif -= 5L;
		kdif /= 10L;
		percdif = "" + ((double)kdif/10.0);
		if (kdif > 0L) percdif = "+" + percdif;
		
		l = new Label(STR_SPACE + percdif,Label.RIGHT);
		if (kdif > 0L)
		{
			l.setForeground(Color.green.darker());
		}
		else if (kdif < 0L)
		{
			l.setForeground(Color.red);
		}
		AFrame.constrain(p,l,X_PABSOLUT,y+1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	}
	
	String steuer = Waehrungen.getString(diffrei,Waehrungen.getListenWaehrung());
	l = new Label(STR_SPACE + steuer,Label.RIGHT);
	if (diffrei < 0L)
	{
		l.setForeground(Color.red);
	}
	else if (diffrei > 0L)
	{
		l.setForeground(Color.green.darker());
	}
	AFrame.constrain(p,l,xval,y+2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);

	steuer = Waehrungen.getString(difsteuer,Waehrungen.getListenWaehrung());
	l = new Label(STR_SPACE + steuer,Label.RIGHT);
	if (difsteuer < 0L)
	{
		l.setForeground(Color.red);
	}
	else if (difsteuer > 0L)
	{
		l.setForeground(Color.green.darker());
	}
	AFrame.constrain(p,l,xval,y+3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);
	
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
	AFrame.constrain(p,lupdate,0,0,X_LEN,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,0,2,0);

	AFrame.constrain(p,new Label(" Aktienname"),X_NAME,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"St\u00fcck",Label.RIGHT),X_STUECKZAHL,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"Kaufkurs",Label.RIGHT),X_KAUFKURS,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"akt. Kurs",Label.RIGHT),X_KURSSTART,1,X_KURSLEN,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"akt. Wert",Label.RIGHT),X_AKTWERT,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"Differenz",Label.RIGHT),X_DIFFERENZ,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"Laufzeit",Label.RIGHT),X_LAUFZEIT,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"%absolut",Label.RIGHT),X_PABSOLUT,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"%Jahr",Label.RIGHT),X_PJAHR,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"Kaufdatum ",Label.RIGHT),X_KAUFDATUM,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,0,0,HEADERABSTAND,0);
	AFrame.constrain(p,new Label(STR_SPACE+"WKN.B\u00f6rse "),X_WKNBOERSE,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,HEADERABSTAND,0);

	heute = new ADate();

	kaufsumme = 0L;
	aktsumme = 0L;
	difsteuer = 0L;
	diffrei = 0L;
	dividenden = 0L;

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
	
	int jahr,monat,tag;

	if ((doNotUpdate()) && (fixDate != null))
	{
		jahr = fixDate.getYear();
		monat = fixDate.getMonth();
		tag = fixDate.getDay();
	}
	else
	{
		jahr = heute.getYear();
		monat = heute.getMonth();
		tag = heute.getDay();
	}

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
	
	if (monate > 0)
	{
		tstr = "" + monate + ((monate==1)?" Monat ":" Monate ") + tstr;
	}
	
	if (istSteuerfrei() && (!nurBeobachten()))
	{
		tstr += " (*)";
	}
	
	return tstr;
}



public synchronized long getAbsPercent() {

	long aktKurs = getKurs();
	long pabs;

	if (aktKurs > 0L)
	{
		try
		{
			pabs = (aktKurs * 10000L) / Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),getKurswaehrung()) - 10000L;
			
			if (pabs > 0L) pabs += 5L;
			else if (pabs < 0L) pabs -= 5L;
			pabs /= 10L;
		}
		catch (Exception e)
		{
			pabs = 0L;
		}
	}
	else
	{
		pabs = 0L;
	}
	
	return pabs;
}



public synchronized long getAbsDiff() {

	if (getKurs() > 0L)
	{
		if (nurBeobachten())
		{
			return (getKurs() - Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()));
		}
		else
		{
			return (getWert() - Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl());
		}
	}
	else
	{
		return 0L;
	}
}



public synchronized void addToPanel(Panel p, int y, boolean namenKurz, boolean nameSteuerfrei, boolean calcJahr) {

	long   aktKurs = Waehrungen.exchange(getKurs(),getKurswaehrung(),Waehrungen.getListenWaehrung());
	long   diff = 0L;
	int    row = y - HEADROWS;
	String kursString = getKursString();

	String s,sk;
	Label l;
	long pabs,tageLaufzeit;
	
	if ((doNotUpdate()) && (fixDate != null))
	{
		tageLaufzeit = fixDate.getSerialDate() - getKaufdatum().getSerialDate();
	}
	else
	{
		tageLaufzeit = heute.getSerialDate() - getKaufdatum().getSerialDate();
	}
	
	checkSpekulationsfrist(konfigspekfrist);
	
	/* Aktienname: */

	if (l1 == null)
	{
		l1 = new BALabel(" "+getName(namenKurz),row,Label.LEFT);
	}
	else
	{
		l1.setValues(" "+getName(namenKurz),row);
	}
	if (doNotUpdate())
	{
		l1.setForeground(Color.gray);
	}
	else
	{
		l1.setForeground(farbeName);
	}
	AFrame.constrain(p,l1,X_NAME,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* StŸckzahl: */
	
	s = (nurBeobachten()) ? "" : getStueckzahlString();
	if (l3 == null)
	{
		l3 = new BALabel(STR_SPACE + s,row);
	}
	else
	{
		l3.setValues(STR_SPACE + s,row);
	}
	AFrame.constrain(p,l3,X_STUECKZAHL,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* Dividende? */
	
	boolean dividendeVorhanden;
	
	if (hasDividende())
	{
		dividenden += Waehrungen.exchange(getDividende(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();

		dividendeVorhanden = true;
	}
	else
	{
		dividendeVorhanden = false;
	}

	if (l15 == null)
	{
		l15 = new BALabel((dividendeVorhanden) ? STR_SPACE+STR_DIVIDENDE : "",row,Label.LEFT);
	}
	else
	{
		l15.setValues((dividendeVorhanden) ? STR_SPACE+STR_DIVIDENDE : "",row);
	}
	l15.setForeground(Color.gray);
	AFrame.constrain(p,l15,X_DIVIDENDE,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);

	/* Kaufkurs: */

	if (l8 == null)
	{
		l8 = new BALabel(STR_SPACE+getKaufkursString(),row);
	}
	else
	{
		l8.setValues(STR_SPACE+getKaufkursString(),row);
	}
	if ((aktKurs > 0L) && (!nurBeobachten()))
	{
		diff = Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()) * getStueckzahl();
		kaufsumme += diff;
	}
	else
	{
		l8.setForeground(Color.gray);
	}
	AFrame.constrain(p,l8,X_KAUFKURS,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* akt. Kurs: */
	
	int warntype = BAImageWarnCanvas.WARN_NONE;

	if (l2 == null)
	{
		l2 = new BALabel(STR_SPACE+kursString,row);
	}
	else
	{
		l2.setValues(STR_SPACE+kursString,row);
	}
	if (aktKurs > 0L)
	{
		if ((getTiefkurs() > 0L) && (aktKurs <= Waehrungen.exchange(getTiefkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung())))
		{
			l2.setForeground(Color.red);
			warntype = BAImageWarnCanvas.WARN_RED;
		}
		else if ((!doUseGrenze()) && (getHochkurs() > 0L) && (aktKurs >= Waehrungen.exchange(getHochkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung())))
		{
			l2.setForeground(Color.green.darker());
			warntype = BAImageWarnCanvas.WARN_GREEN;
		}
		else if ((doUseGrenze()) && (gewinngrenze > 0L) && (aktKurs >= Waehrungen.exchange(gewinngrenze,getKaufwaehrung(),Waehrungen.getListenWaehrung())))
		{
			l2.setForeground(Color.green.darker());
			warntype = BAImageWarnCanvas.WARN_GREEN;
		}
	}
	
	l14 = new BAImageWarnCanvas(row,warntype,l8);
	AFrame.constrain(p,l14,X_WARNING,y,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,ZEILENABSTAND,0,0,0);
	
	checkWarn(warntype);

	AFrame.constrain(p,l2,X_AKTKURS,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	if (l11 == null)
	{
		l11 = new BALabel(" "+getKursdatumString(),row);
	}
	else
	{
		l11.setValues(" "+getKursdatumString(),row);
	}
	AFrame.constrain(p,l11,X_KURSDATUM,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);

	l13 = new BAImageArrowCanvas(row,aktKurs,getVortageskurs(),getKurswaehrung(),l11);
	AFrame.constrain(p,l13,X_ARROW,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* akt. Wert: */

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
	if (l4 == null)
	{
		l4 = new BALabel(STR_SPACE+sk,row);
	}
	else
	{
		l4.setValues(STR_SPACE+sk,row);
	}
	AFrame.constrain(p,l4,X_AKTWERT,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* Differenz */

	if (aktKurs > 0L)
	{
		if (nurBeobachten())
		{
			sk = Waehrungen.getString(aktKurs-Waehrungen.exchange(getKaufkurs(),getKaufwaehrung(),Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung());
		}
		else
		{
			sk = Waehrungen.getString(diff,Waehrungen.getListenWaehrung());
			
			if (istSteuerfrei())
			{
				diffrei += diff;
			}
			else
			{
				difsteuer += diff;
			}
		}
	}
	else
	{
		sk = kursString;
	}
	if (l12 == null)
	{
		l12 = new BALabel(STR_SPACE+sk,row);
	}
	else
	{
		l12.setValues(STR_SPACE+sk,row);
	}
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
	AFrame.constrain(p,l12,X_DIFFERENZ,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* Laufzeit: */

	boolean frei = istSteuerfrei() && (!nurBeobachten());
	if (l5 == null)
	{
		l5 = new BALabel();
	}
	if (nameSteuerfrei && frei)
	{
		l5.setValues(STR_SPACE+"steuerfrei",row);
		l5.setForeground(farbeSteuerfrei);
	}
	else
	{
		l5.setValues(STR_SPACE+getLaufzeitMonateString(),row);
		if (frei) l5.setForeground(farbeSteuerfrei);
	}
	AFrame.constrain(p,l5,X_LAUFZEIT,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* % absolut: */

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
		catch (Exception e)
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
	if (l6 == null)
	{
		l6 = new BALabel(STR_SPACE+sk,row);
	}
	else
	{
		l6.setValues(STR_SPACE+sk,row);
	}
	if (pabs > 0L)
	{
		l6.setForeground(Color.green.darker());
	}
	else if (pabs < 0L)
	{
		l6.setForeground(Color.red);
	}
	AFrame.constrain(p,l6,X_PABSOLUT,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* % Jahr: */
	
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
	if (l7 == null)
	{
		l7 = new BALabel(STR_SPACE+sk,row);
	}
	else
	{
		l7.setValues(STR_SPACE+sk,row);
	}
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
	AFrame.constrain(p,l7,X_PJAHR,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* Kaufdatum: */
	
	if (l9 == null)
	{
		l9 = new BALabel(STR_SPACE+getKaufdatum().toString()+" ",row);
	}
	else
	{
		l9.setValues(STR_SPACE+getKaufdatum().toString()+" ",row);
	}
	if (nurBeobachten())
	{
		l9.setForeground(Color.gray);
	}
	AFrame.constrain(p,l9,X_KAUFDATUM,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* WKN.Bšrse: */
	
	if (l10 == null)
	{
		l10 = new BALabel(STR_SPACE+getWKNString()+"."+getBoerse()+" ",row,Label.LEFT);
	}
	else
	{
		l10.setValues(STR_SPACE+getWKNString()+"."+getBoerse()+" ",row);
	}
	AFrame.constrain(p,l10,X_WKNBOERSE,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* */
	
	Unselect();
}



private void checkWarn(int type) {

	int warning = KonfigurationWarnungen.getWarnType();

	if ((oldWarnType != BAImageWarnCanvas.WARN_INIT) && (type != BAImageWarnCanvas.WARN_NONE)
		&& (warning != KonfigurationWarnungen.WARNTYPE_DISPLAY) && (type != oldWarnType))
	{
		if (warning == KonfigurationWarnungen.WARNTYPE_ALERT)
		{
			new BeepWarnalert(AktienMan.hauptdialog,type,getName(true),getKursString());
		}
		else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	oldWarnType = type;
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
		infodialog.setVisible(false);
		infodialog.dispose();

		infodialog = null;
	}
}



public synchronized void infoDialogClosed() {

	infodialog = null;
}

}
