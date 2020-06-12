/**
 @author Thomas Much
 @version 2002-01-14
 
 2002-01-14
   3-Jahres-Charts entfernt
   setupMenu und checkTypes kennen nun auch 5- und 10-Jahres-Charts
*/

import java.awt.*;
import java.awt.event.*;




public class ChartMenu extends Menu {

private MenuItem item6,item24,/*item36,*/item60,item120;
//private Menu popIntraday;




public ChartMenu() {

	this("Chart");
}



protected ChartMenu(String title) {

	super(title);

	setupMenu();
}



private void setupMenu() {

	MenuItem mi = new MenuItem("Intraday");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_INTRA);
		}
	});
	add(mi);

	mi = new MenuItem("3 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_3);
		}
	});
	add(mi);

	item6 = new MenuItem("6 Monate");
	item6.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_6);
		}
	});
	add(item6);

	mi = new MenuItem("1 Jahr");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_12);
		}
	});
	add(mi);

	item24 = new MenuItem("2 Jahre");
	item24.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_24);
		}
	});
	add(item24);

/*	item36 = new MenuItem("3 Jahre");
	item36.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_36);
		}
	});
	add(item36); */
	
	item60 = new MenuItem("5 Jahre");
	item60.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_60);
		}
	});
	add(item60);

	item120 = new MenuItem("10 Jahre");
	item120.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(URLs.CHART_120);
		}
	});
	add(item120);
	
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



protected void action(int type) {

	AktienMan.hauptdialog.listeSelektierteAktieChart(type);
}



public synchronized void checkTypes() {

	ChartQuelle cq = ChartQuellen.getChartQuelle();
	
	item6.setEnabled(   cq.hasType6()   );
	item24.setEnabled(  cq.hasType24()  );
//	item36.setEnabled(  cq.hasType36()  );
	item60.setEnabled(  cq.hasType60()  );
	item120.setEnabled( cq.hasType120() );	
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
