/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;
import java.awt.event.*;



public final class ChartMenu extends Menu {

private Menu popIntraday;



public ChartMenu() {
	super("Chart");
	setupMenu();
}


private void setupMenu() {
	MenuItem mi = new MenuItem("6 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart("6");
		}
	});
	add(mi);

	mi = new MenuItem("12 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart("12");
		}
	});
	add(mi);

	mi = new MenuItem("24 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.hauptdialog.listeSelektierteAktieChart("24");
		}
	});
	add(mi);
	
	addSeparator();

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
	popIntraday.add(mi);
}


public synchronized void setIntraday(boolean state) {
	popIntraday.setEnabled(state);
}


public synchronized void enableIntraday() {
	setIntraday(true);
}


public synchronized void disableIntraday() {
	setIntraday(false);
}

}
