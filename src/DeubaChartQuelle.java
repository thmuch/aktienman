/**
 @author Thomas Much
 @version 2002-01-13
 
 2002-01-13
   hasType6 wird Ÿberschrieben
*/



public class DeubaChartQuelle extends ChartQuelle {




public DeubaChartQuelle() {

	super("Deutsche Bank",ChartQuellen.CHARTQUELLE_DEUBA,ChartQuellen.CHARTQUELLE_COMDIRECT);
}



public boolean hasType6() {

	return true;
}



public boolean hasType24() {

	return true;
}



public void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall) {

	new DeubaChartLeser(wkn,boerse,isFonds,type,getNextID(firstCall)).start();
}

}
