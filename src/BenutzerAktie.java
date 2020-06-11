/**
 @author Thomas Much
 @version 1998-11-03
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
private transient static Color farbeHintergrund;

private transient Color farbeSteuerfrei;
private transient Color farbeName;
private transient Color farbeSelected;
private transient boolean selected = false;
private transient BALabel l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12;

private String name,wkn,kursdatum;
private Boersenplatz boersenplatz;
private ADate kaufdatum;
private ADate steuerfrei;
private long kaufkurs,hochkurs,tiefkurs,gewinngrenze,prozgrenze,stueckzahl;
private long kurs = VALUE_MISSING;
private int waehrung = Waehrungen.DEM;
private boolean nurdiese = false;
private boolean usegrenze = true;



public BenutzerAktie(String name, String wkn, Boersenplatz platz, boolean nurdiese,
						ADate kaufdatum, long kaufkurs, long stueckzahl,
						long hochkurs, long tiefkurs, long grenze, int waehrung,
						boolean usegrenze) {
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
	prozgrenze = grenze;
	boersenplatz = platz;

	kursdatum = "";
	
	setupValues();
	setColors();
}


private void setupValues() {
	gewinngrenze = (prozgrenze == 0L) ? 0L : (getKaufkurs()*(Waehrungen.PRECISION*100L+prozgrenze))/(Waehrungen.PRECISION*100L);

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
	farbeSteuerfrei = Color.yellow.darker();
	farbeSelected = Color.lightGray;
	clearStatusRequesting();
}


public String getProzentString() {
	return  (prozgrenze == 0L) ? "" : AktienMan.get00String(prozgrenze);
}


public long getHochkurs() {
	return hochkurs;
}


public String getHochkursString() {
	return  (getHochkurs() == 0L) ? "" : AktienMan.get00String(getHochkurs());
}


public long getTiefkurs() {
	return tiefkurs;
}


public String getTiefkursString() {
	return  (getTiefkurs() == 0L) ? "" : AktienMan.get00String(getTiefkurs());
}


public ADate getKaufdatum() {
	return kaufdatum;
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


public synchronized String getKursString() {
	long k = getKurs();
	
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


public synchronized long getWert() {
	if (getKurs() < 0L)
	{
		return 0L;
	}
	else
	{
		return getKurs() * getStueckzahl();
	}
}


public long getKaufkurs() {
	return kaufkurs;
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
	setValues(name,kurs,"",getWaehrung());
}


public synchronized void setValues(String name, long kurs, String kursdatum, int kurswaehrung) {
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
	
	clearStatusRequesting();
}


public synchronized void changeValues(String newName, String newWKN, Boersenplatz newBp,
								boolean newNurDiese, ADate newDate, long newKaufkurs,
								long newAnz, long newHoch, long newTief, long newGrenze,
								boolean newUseGrenze) {

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

	setupValues();
	
	if (reset)
	{
		kurs = VALUE_MISSING;
		kursdatum = "";
	}
}


public void setStatusRequesting() {
	farbeName = Color.blue;
}


public void clearStatusRequesting() {
	farbeName = Color.black;
}


public synchronized boolean isSelected() {
	return selected;
}


public synchronized void Select() {
	if (!isSelected())
	{
		selected = true;
		l1.setBackground(farbeSelected);
		l2.setBackground(farbeSelected);
		l11.setBackground(farbeSelected);
		l12.setBackground(farbeSelected);
		l3.setBackground(farbeSelected);
		l4.setBackground(farbeSelected);
		l5.setBackground(farbeSelected);
		l6.setBackground(farbeSelected);
		l7.setBackground(farbeSelected);
		l8.setBackground(farbeSelected);
		l9.setBackground(farbeSelected);
		l10.setBackground(farbeSelected);
	}
}


public synchronized void Unselect() {
	if (isSelected())
	{
		selected = false;
		l1.setBackground(farbeHintergrund);
		l2.setBackground(farbeHintergrund);
		l11.setBackground(farbeHintergrund);
		l12.setBackground(farbeHintergrund);
		l3.setBackground(farbeHintergrund);
		l4.setBackground(farbeHintergrund);
		l5.setBackground(farbeHintergrund);
		l6.setBackground(farbeHintergrund);
		l7.setBackground(farbeHintergrund);
		l8.setBackground(farbeHintergrund);
		l9.setBackground(farbeHintergrund);
		l10.setBackground(farbeHintergrund);
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


public static int addHeadingsToPanel(Panel p, String aktualisierung) {
	AFrame.constrain(p,new Label(aktualisierung),0,0,9,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,2,0);

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


public synchronized void addToPanel(Panel p, int y, boolean namenKurz) {
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

	l3 = new BALabel("  " +getStueckzahlString(),row);
	AFrame.constrain(p,l3,1,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);

	/* (2) Kaufkurs: */

	l8 = new BALabel("  "+Waehrungen.getString(getKaufkurs(),Waehrungen.DEM),row);
	if (aktKurs > 0L)
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
	
	s = "";
	if (kursdatum != null)
	{
		if (kursdatum.length() > 0)
		{
			s = " (" + kursdatum + ")";
		}
	}
	l11 = new BALabel(s,row);
	AFrame.constrain(p,l11,4,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (5) akt. Wert: */

	if (aktKurs > 0L)
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
		sk = Waehrungen.getString(diff,Waehrungen.DEM);
	}
	else
	{
		sk = kursString;
	}
	l12 = new BALabel("  "+sk,row);
	if (diff > 0L)
	{
		l12.setForeground(Color.green.darker());
	}
	else if (diff < 0L)
	{
		l12.setForeground(Color.red);
	}
	AFrame.constrain(p,l12,6,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (7) Laufzeit: */

	l5 = new BALabel("  "+getLaufzeitMonateString(),row);
	if (istSteuerfrei()) l5.setForeground(farbeSteuerfrei);
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
	AFrame.constrain(p,l9,10,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* (11) WKN.Bšrse: */
	
	l10 = new BALabel("  "+getWKNString()+"."+getBoerse()+" ",row,Label.LEFT);
	AFrame.constrain(p,l10,11,y,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,ZEILENABSTAND,0,0,0);
	
	/* */
	
	Unselect();
}

}
