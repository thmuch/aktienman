/**
 @author Thomas Much
 @version 1999-06-20
*/



public class ComdirectChartQuelle extends ChartQuelle {




public ComdirectChartQuelle() {

	super("Comdirect",ChartQuellen.CHARTQUELLE_COMDIRECT,ChartQuellen.CHARTQUELLE_DEUBA);
}



public boolean hasType36() {
	return true;
}



public void displayChart(String wkn, String boerse, int type, boolean isFonds, boolean firstCall) {

	if (type == ChartViewer.TYPE_24)
	{
		type = ChartViewer.TYPE_36;
	}

	new ComdirectChartLeser(wkn,boerse,isFonds,type,getNextID(firstCall)).start();
}

}
