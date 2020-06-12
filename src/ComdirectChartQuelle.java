/**
 @author Thomas Much
 @version 2002-01-13
 
 2002-01-13
   hasType36 wird nicht mehr überschrieben, dafür hasType60 und hasType120
*/



public class ComdirectChartQuelle extends ChartQuelle {




public ComdirectChartQuelle() {

	super("Comdirect",ChartQuellen.CHARTQUELLE_COMDIRECT,ChartQuellen.CHARTQUELLE_DEUBA);
}



public boolean hasType60() {

	return true;
}



public boolean hasType120() {

	return true;
}



public void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall) {

	new ComdirectChartLeser(wkn,boerse,isFonds,type,getNextID(firstCall)).start();
}

}
