/**
 @author Thomas Much
 @version 2002-01-13
 
 2002-01-13
   hasType6, hasType60, hasType120
*/




public abstract class ChartQuelle {

private String name;
private int id, nextID;




public ChartQuelle(String name, int id, int nextID) {

	this.name = name;
	this.id = id;
	this.nextID = nextID;
}



public String getName() {

	return name;
}



public int getID() {

	return id;
}



public int getNextID(boolean firstCall) {

	return (firstCall ? nextID : ChartQuellen.CHARTQUELLE_NONE);
}



public boolean hasType6() {

	return false;
}



public boolean hasType24() {

	return false;
}



public boolean hasType36() {

	return false;
}



public boolean hasType60() {

	return false;
}



public boolean hasType120() {

	return false;
}



public abstract void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall);

}
