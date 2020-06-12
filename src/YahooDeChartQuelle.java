/**
 @author Thomas Much
 @version 2003-02-25
*/

import java.awt.*;




public class YahooDeChartQuelle extends ChartQuelle {



static
{
	if (AktienMan.DEBUG)
	{
		System.out.println("  Plugin: YahooDeChartQuelle wird initialisiert.");
	}

	ChartQuellen.register( new YahooDeChartQuelle() );
}




private YahooDeChartQuelle() {

	super("Yahoo.de",ChartQuellen.ID_YAHOO_DE,ChartQuellen.PRIORITY_YAHOO_DE);
}



public boolean hasType(int type) {

	switch(type)
	{
	case ChartQuellen.TYPE_KERZEN:
	case ChartQuellen.TYPE_LINIEN:
	case ChartQuellen.TYPE_OHLC:

		return true;
	}

	return false;
}



public boolean hasTime(int time) {

	switch(time)
	{
	case ChartQuellen.TIME_1D:
	case ChartQuellen.TIME_5D:
	case ChartQuellen.TIME_3M:
	case ChartQuellen.TIME_6M:
	case ChartQuellen.TIME_1Y:
	case ChartQuellen.TIME_2Y:
	case ChartQuellen.TIME_5Y:
	case ChartQuellen.TIME_MAX:
	
		return true;
	}

	return false;
}



public Dimension getChartSize() {

	return new Dimension(512,288);
}



public Insets getChartClipping() {

	return new Insets(0,0,0,0);
}



public void loadChart(ChartReceiver receiver, String wkn, String boerse, int time, int type, ChartQuelle first) {

	Runnable leser = new YahooDeChartLeser(this,receiver,wkn,boerse,time,type,first);
	
	new Thread(leser).start();
}


}
