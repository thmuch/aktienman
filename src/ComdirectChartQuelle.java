/**
 @author Thomas Much
 @version 2003-02-25
*/

import java.awt.*;




public class ComdirectChartQuelle extends ChartQuelle {



static
{
	if (AktienMan.DEBUG)
	{
		System.out.println("  Plugin: ComdirectChartQuelle wird initialisiert.");
	}

	ChartQuellen.register( new ComdirectChartQuelle() );
}




private ComdirectChartQuelle() {

	super("Comdirect",ChartQuellen.ID_COMDIRECT,ChartQuellen.PRIORITY_COMDIRECT);
}



public boolean hasType(int type) {

	switch(type)
	{
	case ChartQuellen.TYPE_BALKEN:
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
	case ChartQuellen.TIME_10D:
	case ChartQuellen.TIME_3M:
	case ChartQuellen.TIME_6M:
	case ChartQuellen.TIME_1Y:
	case ChartQuellen.TIME_5Y:
	case ChartQuellen.TIME_MAX:

		return true;
	}

	return false;
}



public Dimension getChartSize() {

	return new Dimension(400,461);
}



public Insets getChartClipping() {

	return new Insets(0,0,0,0);
}



public void loadChart(ChartReceiver receiver, String wkn, String boerse, int time, int type, ChartQuelle first) {

	Runnable leser = new ComdirectChartLeser(this,receiver,wkn,boerse,time,type,first);
	
	new Thread(leser).start();
}


}
