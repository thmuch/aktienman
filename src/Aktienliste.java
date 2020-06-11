/**
 @author Thomas Much
 @version 1999-06-18
*/

import java.awt.*;
import java.util.*;
import java.io.*;




public class Aktienliste extends Vector implements Serializable {

static final long serialVersionUID = 1978100400000L;

private Choice choice;




public Aktienliste() {
	super(60);
	setupList();
	updateChoice();
}



public synchronized void setupList() {}



public synchronized void add(Listeneintrag eintrag) {
	addElement(eintrag);
}



public synchronized Aktie getAktie(int index) {
	return (Aktie)elementAt(index);
}



public synchronized boolean isMember(int wkn) {
	for (int i=0; i < size(); i++)
	{
		if (getAktie(i).getWKN() == wkn) return true;
	}

	return false;
}



public synchronized Choice getChoice(boolean immerNeu) {
	if (immerNeu)
	{
		updateChoice();
	}
	
	return choice;
}



public synchronized void updateChoice() {
	choice = new Choice();
	
	for (int i=0; i < size(); i++)
	{
		choice.addItem(((Listeneintrag)elementAt(i)).toString());
	}
}

}
