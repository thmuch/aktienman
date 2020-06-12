/**
 @author Thomas Much
 @version 1999-06-27
*/

import java.awt.*;


	

public final class IntradayChartViewer extends ChartViewer {

private byte[] chartData;




public IntradayChartViewer(String wknboerse, BenutzerAktie ba) {

	super(null,"Intraday "+ba.getName(true),wknboerse,URLs.CHART_NONE,"gif","GIFf",670,285,ChartQuellen.CHARTQUELLE_NONE,ba.isFonds(),true);
}



protected synchronized void setImageData(byte[] data) {

	chartData = data;

	System.gc();
}



public synchronized byte[] getImageData() {
	return chartData;
}



public String getDefaultFilename() {
	return getWKNBoerse() + "-" + new ADate().toTimestamp(true) + "." + getExt();
}
	
}
