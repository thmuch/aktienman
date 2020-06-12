/**
 @author Thomas Much
 @version 1999-12-09
*/

import java.util.*;
import java.io.*;
import java.util.zip.*;




public final class BenutzerListe extends Vector implements Serializable {

static final long serialVersionUID = 1972011800002L;

private static final int SORT_NONE    = -1;
private static final int SORT_NAME    =  0;
private static final int SORT_ABSPERC =  1;

private ADate letzteAktualisierung = null;
private String festeBoerse = "";
private long verkaufserloes = 0L;
private int erloesWaehrung = Waehrungen.DEM;
private int portfoliover = 0;

private transient String portfolioFile = "";
private transient static int listeSortBy = SORT_NONE;




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



public synchronized void sort(boolean kurz) {

	if (getListeSortBy() == SORT_ABSPERC)
	{
		sortByAbsPercent();
	}
	else
	{
		sortByName(kurz);
	}
}



private synchronized void sortByAbsPercent() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getAbsPercent() > getAt(max).getAbsPercent())
			{
				max = j;
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
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(min).getName(kurz).trim().toUpperCase().compareTo(getAt(j).getName(kurz).trim().toUpperCase()) > 0)
			{
				min = j;
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



public static void store(BenutzerListe benutzerliste){

	ObjectOutputStream out = null;

	try
	{
		FileOutputStream fos = new FileOutputStream(benutzerliste.getPortfolioFile());
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		out = new ObjectOutputStream(fos);
		out.writeObject(benutzerliste);
		out.flush();
	}
	catch (IOException e)
	{
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
			catch (IOException e) {}
		
			out = null;
		}
	}
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



public synchronized static int getListeSortBy() {

	if (listeSortBy <= SORT_NONE)
	{
		listeSortBy = AktienMan.properties.getInt("Konfig.ListeSortBy",SORT_NAME);
	}
	
	return listeSortBy;
}



public synchronized static void setListeSortBy(int neu) {

	AktienMan.properties.setInt("Konfig.ListeSortBy",neu);
	listeSortBy = neu;
}

}
