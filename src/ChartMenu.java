/**
 @author Thomas Much
 @version 2003-02-25

 2003-02-25
 	Anpassung an neue ChartQuellen-Zeitkonstanten 
 2002-01-14
   3-Jahres-Charts entfernt
   setupMenu und checkTypes kennen nun auch 5- und 10-Jahres-Charts
*/

import java.awt.*;
import java.awt.event.*;




public class ChartMenu extends Menu {

private MenuItem item1d, item5d, item10d, item3m, item6m, item1y, item2y, item3y, item5y, itemmax;




public ChartMenu() {

	this("Chart");
}



protected ChartMenu(String title) {

	super(title);

	setupMenu();
}



private void setupMenu() {

	item1d = new MenuItem("1 Tag");
	item1d.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_1D);
		}
	});
	add(item1d);

	item5d = new MenuItem("5 Tage");
	item5d.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_5D);
		}
	});
	add(item5d);

	item10d = new MenuItem("10 Tage");
	item10d.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_10D);
		}
	});
	add(item10d);

	item3m = new MenuItem("3 Monate");
	item3m.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_3M);
		}
	});
	add(item3m);
	
	item6m = new MenuItem("6 Monate");
	item6m.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_6M);
		}
	});
	add(item6m);

	item1y = new MenuItem("1 Jahr");
	item1y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_1Y);
		}
	});
	add(item1y);

	item2y = new MenuItem("2 Jahre");
	item2y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_2Y);
		}
	});
	add(item2y);

	item3y = new MenuItem("3 Jahre");
	item3y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_3Y);
		}
	});
	add(item3y);

	item5y = new MenuItem("5 Jahre");
	item5y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_5Y);
		}
	});
	add(item5y);

	itemmax = new MenuItem("max.");
	itemmax.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			action(ChartQuellen.TIME_MAX);
		}
	});
	add(itemmax);
}



protected void action(int time) {

	AktienMan.hauptdialog.listeSelektierteAktieChart(time);
}



public synchronized void checkTypes() {

	item1d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_1D) );
	item5d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_5D) );
	item10d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_10D) );
	item3m.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_3M) );
	item6m.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_6M) );
	item1y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_1Y) );
	item2y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_2Y) );
	item3y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_3Y) );
	item5y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_5Y) );
	itemmax.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_MAX) );
}


}
