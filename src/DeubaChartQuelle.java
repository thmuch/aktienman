/**
 @author Thomas Much
 @version 1999-06-27
*/



public class DeubaChartQuelle extends ChartQuelle {




public DeubaChartQuelle() {

	super("Deutsche Bank",ChartQuellen.CHARTQUELLE_DEUBA,ChartQuellen.CHARTQUELLE_COMDIRECT);
}



public boolean hasType24() {
	return true;
}



public void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall) {

	if (type == URLs.CHART_36)
	{
		type = URLs.CHART_24;
	}

	new DeubaChartLeser(wkn,boerse,isFonds,type,getNextID(firstCall)).start();
}

}
