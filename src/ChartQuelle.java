/**
 @author Thomas Much
 @version 2003-02-25
 
 2003-02-25
 	getChartSize, getChartClipping
 	displayChart entfernt, dafür gibt's loadChart
 	priority, getPriority
 2002-01-13
   hasType6, hasType60, hasType120
*/

import java.awt.*;




public abstract class ChartQuelle {

private String name;
private long id;
private int priority;




public ChartQuelle(String name, long id, int priority) {

	this.name     = name;
	this.id       = id;
	this.priority = priority;
}



public String getName() {

	return name;
}



public int getPriority() {

	return priority;
}



public long getID() {

	return id;
}



public abstract boolean hasType(int type);
public abstract boolean hasTime(int time);

public abstract Dimension getChartSize();

public abstract Insets getChartClipping();

public abstract void loadChart(ChartReceiver receiver, String wkn, String boerse, int time, int type, ChartQuelle first);


}
