/**
 @author Thomas Much
 @version 1999-06-14
*/

import java.util.*;
import java.awt.*;




public final class ChartQuellen extends Vector {

public static final int CHARTQUELLE_NONE      = -1;
public static final int CHARTQUELLE_COMDIRECT =  0;
public static final int CHARTQUELLE_DEUBA     =  1;

private static final int STANDARDQUELLE = CHARTQUELLE_DEUBA;

private static ChartQuellen quellen = new ChartQuellen();

private static int chartquelle = CHARTQUELLE_NONE;




public ChartQuellen() {
	super(5);
	setupList();
}



public synchronized void setupList() {
	add(new ComdirectChartQuelle());
	add(new DeubaChartQuelle());
}



public synchronized void add(ChartQuelle eintrag) {
	addElement(eintrag);
}



public synchronized ChartQuelle getAt(int index) {
	return (ChartQuelle)elementAt(index);
}



public synchronized static Choice getChoice() {
	Choice choice = new Choice();
	
	for (int i=0; i < quellen.size(); i++)
	{
		choice.addItem(quellen.getAt(i).getName());
	}
	
	return choice;
}



public synchronized static ChartQuelle getChartQuelle(int index) {
	return quellen.getAt(index);
}



public synchronized static ChartQuelle getChartQuelle() {
	return getChartQuelle(getChartQuelleIndex());
}



public synchronized static int getChartQuelleIndex() {
	if (chartquelle <= CHARTQUELLE_NONE)
	{
		chartquelle = AktienMan.properties.getInt("Konfig.Chartquelle",STANDARDQUELLE);
	}
	
	return chartquelle;
}



public synchronized static void setChartQuelleIndex(int neu) {
	AktienMan.properties.setInt("Konfig.Chartquelle",neu);
	chartquelle = neu;
}

}
