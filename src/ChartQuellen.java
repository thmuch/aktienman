/**
 @author Thomas Much
 @version 2003-02-26

 2003-02-26
 	hasAnyTime, hasAnyType
 	displayChart
 	register
 	index2Id, id2Index
 	getChartQuelleID, setChartQuelleID
 	PRIORITY_*, ID_*, TIME_*, TYP_*
 	getNextID
*/

import java.util.*;
import java.awt.*;




public final class ChartQuellen implements Runnable {

public static final long ID_NONE         = -1;
public static final long ID_YAHOO_DE     =  0;
public static final long ID_COMDIRECT    =  1;
//public static final long ID_DEUTSCHEBANK =  2;

public static final int TIME_1D    =  0;
public static final int TIME_5D    =  1;
public static final int TIME_10D   =  2;
public static final int TIME_3M    =  3;
public static final int TIME_6M    =  4;
public static final int TIME_1Y    =  5;
public static final int TIME_2Y    =  6;
public static final int TIME_3Y    =  7;
public static final int TIME_5Y    =  8;
public static final int TIME_MAX   =  9;
public static final int TIME_COUNT = 10;

public static final int TYPE_BALKEN = 0;
public static final int TYPE_LINIEN = 1;
public static final int TYPE_KERZEN = 2;
public static final int TYPE_OHLC   = 3;
public static final int TYPE_COUNT  = 4;

protected static final int PRIORITY_YAHOO_DE     = 100000;
protected static final int PRIORITY_COMDIRECT    =  50000;
//protected static final int PRIORITY_DEUTSCHEBANK =  40000;
protected static final int PRIORITY_MAX          = Integer.MAX_VALUE-1;
protected static final int PRIORITY_MIN          = Integer.MIN_VALUE+1;
protected static final int PRIORITY_NEVER        = Integer.MIN_VALUE;


private static final long STANDARDQUELLE = ID_YAHOO_DE;

private static Vector quellen = new Vector(10);

private static long chartquelle = ID_NONE;



static
{
	// Workaround für JDK 1.1, sonst wird die Klasse entladen...
	new Thread(new ChartQuellen()).start();
}




private ChartQuellen() {}



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



public static void register(ChartQuelle source) {

	if (source == null) return;

	if (AktienMan.DEBUG)
	{
		System.out.println("    Chart-Plugin " + source.getName() + " wird registriert...");
	}
	
	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			if (((ChartQuelle)quellen.elementAt(i)).getID() == source.getID())
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



public static boolean hasAnyTime(int time) {

	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			if (((ChartQuelle)quellen.elementAt(i)).hasTime(time))
			{
				return true;
			}
		}
	}
	
	return false;
}



public static boolean hasAnyType(int type) {

	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			if (((ChartQuelle)quellen.elementAt(i)).hasType(type))
			{
				return true;
			}
		}
	}
	
	return false;
}



public static void displayChart(String wkn, String boerse, int time, int type) {

	ChartQuelle quelle = getChartQuelle();
	
	ChartReceiver receiver = new OneChartFrame(quelle,wkn,boerse,time,type);

	displayChart(quelle,receiver,wkn,boerse,time,type);
}



public static void displayChart(ChartQuelle quelle, ChartReceiver receiver, String wkn, String boerse, int time, int type) {

	quelle.loadChart(receiver,wkn,boerse,time,type,quelle);
}



public static Choice getChoice() {

	Choice choice = new Choice();
	
	synchronized(quellen)
	{
		for (int i = 0; i < quellen.size(); i++)
		{
			choice.addItem(((ChartQuelle)quellen.elementAt(i)).getName());
		}
	}
	
	return choice;
}



public static ChartQuelle getChartQuelle(long id) {

	synchronized(quellen)
	{
		return (ChartQuelle)quellen.elementAt(getChartQuelleIndex(id));
	}
}



public static ChartQuelle getChartQuelle() {

	return getChartQuelle(getChartQuelleID());
}



public static long getNextID(ChartQuelle first, ChartQuelle current, int time, int type) {

	// TODO: time/type auswerten

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
			ChartQuelle kq = (ChartQuelle)quellen.elementAt(i);
			
			if ((kq.getPriority() > bestPriority) && (kq.getPriority() < maxPriority) && (kq.getID() != first.getID()))
			{
				bestPriority = kq.getPriority();
				bestID       = kq.getID();
			}
		}
	}
	
	return bestID;
}



public static int getChartQuelleIndex() {

	return getChartQuelleIndex(getChartQuelleID());
}



private static int getChartQuelleIndex(long id) {

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



public static void setChartQuelleIndex(int index) {

	setChartQuelleID(index2Id(index));
}



public static int time2Index(int time) {

	// können wir hier so codieren, da die TIME-Konstanten derzeit dem Index entsprechen

	if ((time >= TIME_1D) && (time <= TIME_MAX))
	{
		return time;
	}
	else
	{
		return TIME_1Y;
	}
}



public static int type2Index(int type) {

	// können wir hier so codieren, da die TYPE-Konstanten derzeit dem Index entsprechen

	if ((type >= TYPE_BALKEN) && (type <= TYPE_OHLC))
	{
		return type;
	}
	else
	{
		return TYPE_LINIEN;
	}
}



private static int id2Index(long id)
{
	if (id > ID_NONE)
	{
		synchronized(quellen)
		{
			for (int i = 0; i < quellen.size(); i++)
			{
				if (((ChartQuelle)quellen.elementAt(i)).getID() == id)
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
			return ((ChartQuelle)quellen.elementAt(index)).getID();
		}
		else
		{
			return ID_NONE;
		}
	}
}



private synchronized static long getChartQuelleID() {

	if (chartquelle <= ID_NONE)
	{
		chartquelle = AktienMan.properties.getLong("Konfig.Chartquelle",STANDARDQUELLE);

		if (id2Index(chartquelle) < 0)
		{
			chartquelle = STANDARDQUELLE;
		}
	}
	
	return chartquelle;
}



private synchronized static void setChartQuelleID(long neu) {

	AktienMan.properties.setLong("Konfig.Chartquelle",neu);
	chartquelle = neu;
}

}
