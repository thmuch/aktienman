/**
 @author Thomas Much
 @version 2003-02-25

 2003-02-25
 	register
 	index2Id, id2Index
 	getKursQuelleID, setKursQuelleID
 	PRIORITY_*, ID_*
 	getNextID
*/

import java.util.*;
import java.awt.*;




public final class KursQuellen implements Runnable {

public static final long ID_NONE         = -1;
public static final long ID_YAHOO_DE     =  0;
public static final long ID_COMDIRECT    =  1;
//public static final long ID_DEUTSCHEBANK =  1;
//public static final long ID_LSRTDAX30BID =  2;
//public static final long ID_LSRTDAX30ASK =  3;

protected static final int PRIORITY_YAHOO_DE     = 100000;
protected static final int PRIORITY_COMDIRECT    =  50000;
//protected static final int PRIORITY_DEUTSCHEBANK =  40000;
protected static final int PRIORITY_MAX          = Integer.MAX_VALUE-1;
protected static final int PRIORITY_MIN          = Integer.MIN_VALUE+1;
protected static final int PRIORITY_NEVER        = Integer.MIN_VALUE;


private static final long STANDARDQUELLE = ID_YAHOO_DE;

private static Vector quellen = new Vector(10);

private static long kursquelle = ID_NONE;



static
{
	// Workaround für JDK 1.1, sonst wird die Klasse entladen...
	new Thread(new KursQuellen()).start();
}




private KursQuellen() {}



public void run() {

	while(true)
	{
		try
		{
			Thread.sleep(1000000L);
		}
		catch (InterruptedException e) {}	
	}
}



public static void register(KursQuelle source) {

	if (source == null) return;

	if (AktienMan.DEBUG)
	{
		System.out.println("    Kurs-Plugin " + source.getName() + " wird registriert...");
	}
	
	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			if (((KursQuelle)quellen.elementAt(i)).getID() == source.getID())
			{
				if (AktienMan.DEBUG)
				{
					System.out.println("    abgelehnt!");
				}

				return;
			}
		}

		quellen.addElement(source);

		if (AktienMan.DEBUG)
		{
			System.out.println("    fertig (" + quellen.size() + ").");
		}
	}
}



public static Choice getChoice() {

	Choice choice = new Choice();
	
	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			choice.addItem(((KursQuelle)quellen.elementAt(i)).getName());
		}
	}
	
	return choice;
}



public static KursQuelle getKursQuelle(long id) {

	synchronized(quellen)
	{
		return (KursQuelle)quellen.elementAt(getKursQuelleIndex(id));
	}
}



public static KursQuelle getKursQuelle() {

	return getKursQuelle(getKursQuelleID());
}



public static KursQuelle getPlatzKursQuelle() {

	long id = getKursQuelleID();
	
	/* TODO: überhaupt noch nötig??? */
	
/*	if ((id == ID_LSRTDAX30BID) || (id == ID_LSRTDAX30ASK))
	{
		id = STANDARDQUELLE;
	} TODO */

	return getKursQuelle(id);
}



public static KursQuelle getFondsQuelle() {

	return getPlatzKursQuelle();
}



public static long getNextID(KursQuelle first, KursQuelle current) {

	int maxPriority;
	
	if (first.getID() == current.getID())
	{
		maxPriority = Integer.MAX_VALUE;
	}
	else
	{
		maxPriority = current.getPriority();
	}
	
	int  bestPriority = PRIORITY_NEVER;
	long bestID       = ID_NONE;

	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			KursQuelle kq = (KursQuelle)quellen.elementAt(i);
			
			if ((kq.getPriority() > bestPriority) && (kq.getPriority() < maxPriority) && (kq.getID() != first.getID()))
			{
				bestPriority = kq.getPriority();
				bestID       = kq.getID();
			}
		}
	}
	
	return bestID;
}



public static int getKursQuelleIndex() {

	return getKursQuelleIndex(getKursQuelleID());
}



private static int getKursQuelleIndex(long id) {

	int index = id2Index(id);
	
	if (index < 0)
	{
		return id2Index(STANDARDQUELLE);
	}
	else
	{
		return index;
	}
}



public static void setKursQuelleIndex(int index) {

	setKursQuelleID(index2Id(index));
}



private static int id2Index(long id)
{
	if (id > ID_NONE)
	{
		synchronized(quellen)
		{
			for (int i = 0; i < quellen.size(); i++)
			{
				if (((KursQuelle)quellen.elementAt(i)).getID() == id)
				{
					return i;
				}
			}
		}
	}
	
	return -1;
}



private static long index2Id(int index) {

	synchronized(quellen)
	{
		if ((index >= 0) && (index < quellen.size()))
		{
			return ((KursQuelle)quellen.elementAt(index)).getID();
		}
		else
		{
			return ID_NONE;
		}
	}
}



private synchronized static long getKursQuelleID() {

	if (kursquelle <= ID_NONE)
	{
		kursquelle = AktienMan.properties.getLong("Konfig.Kursquelle",STANDARDQUELLE);
		
		if (id2Index(kursquelle) < 0)
		{
			kursquelle = STANDARDQUELLE;
		}
	}

	return kursquelle;
}



private synchronized static void setKursQuelleID(long neu) {

	AktienMan.properties.setLong("Konfig.Kursquelle",neu);
	kursquelle = neu;
}

}
