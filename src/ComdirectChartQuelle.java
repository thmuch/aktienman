/**
 @author Thomas Much
 @version 1999-06-27
*/



public class ComdirectChartQuelle extends ChartQuelle {




public ComdirectChartQuelle() {

	super("Comdirect",ChartQuellen.CHARTQUELLE_COMDIRECT,ChartQuellen.CHARTQUELLE_DEUBA);
}



public boolean hasType36() {
	return true;
}



public void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall) {

	if (type == URLs.CHART_24)
	{
		type = URLs.CHART_36;
	}

	new ComdirectChartLeser(wkn,boerse,isFonds,type,getNextID(firstCall)).start();
}

}
