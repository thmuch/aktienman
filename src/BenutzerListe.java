/**
 @author Thomas Much
 @version 2000-03-21
*/

import java.util.*;
import java.io.*;
import java.util.zip.*;




public final class BenutzerListe extends Vector implements Serializable {

static final long serialVersionUID = 1972011800002L;

private static final int SORT_NONE      = -1;
private static final int SORT_NAME      =  0;
private static final int SORT_ABSPERC   =  1;
private static final int SORT_ABSDIFF   =  2;
private static final int SORT_KAUFDATUM =  3;
private static final int SORT_FIXDATUM  =  4;

private ADate letzteAktualisierung = null;
private String festeBoerse = "";
private long verkaufserloes = 0L;
private int erloesWaehrung = Waehrungen.DEM;
private int portfoliover = 0;
private int sortBy = SORT_NAME;

private transient String portfolioFile = "";




public BenutzerListe() {

	super(50);
}



public synchronized void destroy() {

	for (int i = 0; i < size(); i++)
	{
		getAt(i).destroy();
	}
	
	removeAllElements();
}



public synchronized void setPortfolioFile(String name) {

	portfolioFile = name;
}



public synchronized String getPortfolioFile() {

	return portfolioFile;
}



public synchronized void add(BenutzerAktie ba) {

	addElement(ba);
}



public synchronized void removeAt(int index) {

	getAt(index).destroy();
	removeElementAt(index);
}



public synchronized BenutzerAktie getAt(int index) {

	return (BenutzerAktie)elementAt(index);
}



public synchronized void setDate(String boerse) {

	letzteAktualisierung = new ADate();
	festeBoerse = boerse;
}



public synchronized void clearDate() {

	letzteAktualisierung = null;
}



private synchronized ADate getDate() {

	return letzteAktualisierung;
}



public synchronized String getFesteBoerse() {

	if (festeBoerse == null)
	{
		return "";
	}
	else if (festeBoerse.length() == 0)
	{
		return "";
	}
	else
	{
		return " ("+festeBoerse+")";
	}
}



public synchronized String getDateString() {

	String s;
	ADate d = getDate();
	
	if (d == null)
	{
		s = " Bisher noch keine Aktualisierung.";
	}
	else
	{
		s = " Letzte Aktualisierung am "+d.toString()+" um "+d.timeToString()+getFesteBoerse()+".";
	}
	
	return s;
}



public synchronized long getErloes() {

	return verkaufserloes;
}



public synchronized void clearErloes() {

	verkaufserloes = 0L;
}



public synchronized void addToErloes(long delta) {

	verkaufserloes += delta;
}



public synchronized int getErloesWaehrung() {

	return erloesWaehrung;
}



public synchronized void erloesToWaehrung(int neueWaehrung) {

	verkaufserloes = Waehrungen.exchange(verkaufserloes,getErloesWaehrung(),neueWaehrung);
	erloesWaehrung = neueWaehrung;
}



public synchronized int getSortBy() {

	return sortBy;
}



public synchronized void setSortBy(int neu) {

	sortBy = neu;
}



public synchronized void sort(boolean kurz) {

	switch (getSortBy())
	{
	case SORT_ABSPERC:

		sortByAbsPercent();
		break;
	
	case SORT_KAUFDATUM:
	
		sortByKaufdatum();
		break;

	case SORT_FIXDATUM:
	
		sortByFixDatum();
		break;

	case SORT_ABSDIFF:
	
		sortByAbsDiff();
		break;
	
	default:

		sortByName(kurz);
	}
}



private synchronized void sortByFixDatum() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		int maxval = getAt(max).getFixedDate().getSerialDate();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getFixedDate().getSerialDate() < maxval)
			{
				max = j;
				maxval = getAt(max).getFixedDate().getSerialDate();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByKaufdatum() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		int maxval = getAt(max).getKaufdatum().getSerialDate();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getKaufdatum().getSerialDate() < maxval)
			{
				max = j;
				maxval = getAt(max).getKaufdatum().getSerialDate();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByAbsDiff() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		long maxval = getAt(max).getAbsDiff();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getAbsDiff() > maxval)
			{
				max = j;
				maxval = getAt(max).getAbsDiff();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByAbsPercent() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		long maxval = getAt(max).getAbsPercent();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getAbsPercent() > maxval)
			{
				max = j;
				maxval = getAt(max).getAbsPercent();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByName(boolean kurz) {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int min = i;
		String minval = getAt(min).getName(kurz).trim().toUpperCase();
		
		for (int j = i+1; j < size; j++)
		{
			if (minval.compareTo(getAt(j).getName(kurz).trim().toUpperCase()) > 0)
			{
				min = j;
				minval = getAt(min).getName(kurz).trim().toUpperCase();
			}
		}
		
		if (min != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(min),i);
			setElementAt(temp,min);
		}
	}
}



private void writeObject(ObjectOutputStream out) throws IOException {

	portfoliover = 0/*AktienMan.PORTFOLIOVER*/;
	out.defaultWriteObject();
}



public synchronized static boolean useShortNames() {

	return AktienMan.properties.getBoolean("Konfig.Aktiennamen.kuerzen",true);
}



public synchronized static boolean useOnlineNames() {

	return AktienMan.properties.getBoolean("Konfig.Aktiennamen",true);
}



public synchronized static boolean useSteuerfrei() {

	return AktienMan.properties.getBoolean("Konfig.Steuerfrei",true);
}



public synchronized static boolean calcProzJahr() {

	return AktienMan.properties.getBoolean("Konfig.ProzJahr",true);
}



public static boolean store(BenutzerListe benutzerliste){

	ObjectOutputStream out = null;

	boolean error = false;
	
	String filename = benutzerliste.getPortfolioFile();

	File f = new File(filename);
	
	if (f.exists())
	{
		File backup = new File(filename + ".bak");
		
		if (backup.exists()) backup.delete();
		
		f.renameTo(backup);
	}
	
	f = null;

	try
	{
		FileOutputStream fos = new FileOutputStream(filename);
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		out = new ObjectOutputStream(fos);
		out.writeObject(benutzerliste);
		out.flush();
	}
	catch (IOException e)
	{
		error = true;
		System.out.println("Fehler beim Speichern der Aktienliste.");
	}
	finally
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				error = true;
			}
		
			out = null;
		}
	}
	
	return error;
}



public static BenutzerListe restore(String datei) {

	ObjectInputStream in = null;
	
	BenutzerListe benutzerliste = new BenutzerListe();

	try
	{
		FileInputStream fis = new FileInputStream(datei);
		GZIPInputStream gzis = new GZIPInputStream(fis);
		in = new ObjectInputStream(fis);
		benutzerliste = (BenutzerListe)in.readObject();
	}
	catch (IOException e) {}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Aktienliste fehlerhaft.");
	}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e) {}
		
			in = null;
		}
	}
	
	benutzerliste.setPortfolioFile(datei);
	
	return benutzerliste;
}

}
