/**
 @author Thomas Much
 @version 1999-06-27
*/

import java.awt.*;
import java.awt.event.*;




public final class ChartMenu extends Menu {

private MenuItem item24,item36;
//private Menu popIntraday;




public ChartMenu() {
	super("Chart");
	setupMenu();
}



private void setupMenu() {

	MenuItem mi = new MenuItem("Intraday");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_INTRA);
		}
	});
	add(mi);

	mi = new MenuItem("3 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_3);
		}
	});
	add(mi);

	mi = new MenuItem("6 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_6);
		}
	});
	add(mi);

	mi = new MenuItem("1 Jahr");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_12);
		}
	});
	add(mi);

	item24 = new MenuItem("2 Jahre");
	item24.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_24);
		}
	});
	add(item24);

	item36 = new MenuItem("3 Jahre");
	item36.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart(URLs.CHART_36);
		}
	});
	add(item36);
	
/*	addSeparator();

	popIntraday = new Menu("Intraday");
	add(popIntraday);

	mi = new MenuItem("Frankfurt");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieIntradayChart("FRA");
		}
	});
	popIntraday.add(mi);

	mi = new MenuItem("Xetra");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieIntradayChart("ETR");
		}
	});
	popIntraday.add(mi); */
}



public synchronized void checkTypes() {

	ChartQuelle quelle = ChartQuellen.getChartQuelle();
	
	if (quelle.hasType24())
	{
		item24.setEnabled(true);
	}
	else
	{
		item24.setEnabled(false);
	}
	
	if (quelle.hasType36())
	{
		item36.setEnabled(true);
	}
	else
	{
		item36.setEnabled(false);
	}
}



public synchronized void setIntraday(boolean state) {
//	popIntraday.setEnabled(state);
}



public synchronized void enableIntraday() {
	setIntraday(true);
}



public synchronized void disableIntraday() {
	setIntraday(false);
}

}
