// 1998-09-11 tm

import java.awt.*;
import java.awt.event.*;



public class VerkaufserloesLoeschen extends AktienFrame {

private Button buttonJa;



public VerkaufserloesLoeschen() {
	super(AktienMan.AMFENSTERTITEL+"Verkaufserl\u00f6s l\u00f6schen",-1,null);
}


public void setupElements2() {
	Label label = new Label("Wollen Sie den Verkaufserl\u00f6s wirklich l\u00f6schen?");
	label.setForeground(Color.red);
	constrain(this,label,0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,10,10);

	constrain(this,new Label(""),0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,10,0);

	Button buttonNein = new Button(Lang.NO);
	buttonNein.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(this,buttonNein,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,0);

	buttonJa = new Button(Lang.YES);
	buttonJa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(this,buttonJa,2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,10);
}


public synchronized void executeOK() {
	buttonJa.setEnabled(false);
	AktienMan.hauptdialog.clearErloes();
}


public void closed() {
	AktienMan.erloesloeschen = null;
}

}
