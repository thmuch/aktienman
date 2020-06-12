/**
 @author Thomas Much
 @version 2003-02-25
*/

import java.awt.*;




public class MultiChartFrame extends AFrame implements ChartReceiver {

private Component charts;
private Label     statuszeile;

private final int time,type;
private int chartCount;




public MultiChartFrame(int time, int type) {

	super("Charts - " + getTimeString(time) + Portfolios.getCurrentWindowTitle());

	this.time = time;
	this.type = type;

	/* TODO */
	// -> Arrays anlegen

	calculateChartSizes();

	setVisible(true);

	startThreads();	
}



public void display() {

	setupSize();
}



public void setupFrame() {

	int noc = (int)AktienMan.hauptdialog.getAnzahlAktien();
	
	chartCount = 0;
	
	for (int i = 0; i < noc; i++)
	{
		if (!schonVorhanden(i))
		{
			chartCount++;
		}
	}

	setResizable(true);
}



private boolean schonVorhanden(int index) {

	BenutzerAktie bi = AktienMan.hauptdialog.getAktieNr(index);
	
	for (int i = 0; i < index; i++)
	{
		BenutzerAktie ba = AktienMan.hauptdialog.getAktieNr(i);
		
		if (bi.isEqual(ba.getWKNString(),ba.getBoerse(),true))
		{
			return true;
		}
	}
	
	return false;
}



public void setupElements() {
	
	statuszeile = new Label("");
	charts      = new Label(""); // TODO: ChartCanvas o.Š.

	add(BorderLayout.CENTER,charts);
	add(BorderLayout.SOUTH,statuszeile);
}



public void setupSize() {

	setBounds(5,0,AktienMan.screenSize.width-10,AktienMan.screenSize.height-25);
}



private void calculateChartSizes() {

	/* TODO */
}



private void startThreads() {

	AktienMan.checkURLs(); // TODO: nštig, oder machen das die Threads sowieso?
	
	// hier evtl. noch Reg-Check einbauen

	/* TODO */
}



public void setImage(String wkn, String boerse, int time, int type, Image image, byte[] data, ChartQuelle first, ChartQuelle current) {

	/* TODO */
}



public void setError(String wkn, String boerse, int time, int type, int error, ChartQuelle first, ChartQuelle current) {

	/* TODO */
}



private static String getTimeString(int time) {

	switch(time)
	{
	case ChartQuellen.TIME_1D:
	
		return "Intraday";

	case ChartQuellen.TIME_5D:
	
		return "5 Tage";

	case ChartQuellen.TIME_10D:
	
		return "10 Tage";

	case ChartQuellen.TIME_3M:
	
		return "3 Monate";

	case ChartQuellen.TIME_6M:
	
		return "6 Monate";

	case ChartQuellen.TIME_2Y:
	
		return "2 Jahre";

	case ChartQuellen.TIME_3Y:
	
		return "3 Jahre";

	case ChartQuellen.TIME_5Y:
	
		return "5 Jahre";

	case ChartQuellen.TIME_MAX:
	
		return "max. Jahre";

	default:
	
		return "1 Jahr";
	}
}


}
