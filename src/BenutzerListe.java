/**
 @author Thomas Much
 @version 1999-01-04
*/

import java.util.*;
import java.io.*;



public class BenutzerListe extends Vector implements Serializable {

static final long serialVersionUID = 1972011800002L;

private ADate letzteAktualisierung = null;
private String festeBoerse = "";
private long verkaufserloes = 0L;
private int erloesWaehrung = Waehrungen.DEM;
private int portfoliover = 0;



public BenutzerListe() {
	super(50);
}


public synchronized void add(BenutzerAktie ba) {
	addElement(ba);
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


public synchronized void sortByName(boolean kurz) {
	for (int i = size(); --i >= 0; )
	{
		boolean swapped = false;
		
		for (int j = 0; j<i; j++)
		{
			if (getAt(j).getName(kurz).trim().toUpperCase().compareTo(getAt(j+1).getName(kurz).trim().toUpperCase()) > 0)
			{
				BenutzerAktie temp = getAt(j);
				setElementAt(getAt(j+1),j);
				setElementAt(temp,j+1);
				
				swapped = true;
			}
		}
		
		if (!swapped) return;
	}
}


public synchronized static boolean useShortNames() {
	return AktienMan.properties.getBoolean("Konfig.Aktiennamen.kuerzen",true);
}


public synchronized static boolean useSteuerfrei() {
	return AktienMan.properties.getBoolean("Konfig.Steuerfrei",true);
}


public synchronized void prepare2Save() {
	portfoliover = AktienMan.PORTFOLIOVER;
}

}
