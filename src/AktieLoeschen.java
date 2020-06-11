/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;
import java.awt.event.*;



public final class AktieLoeschen extends AktienFrame {

private Button buttonJa;



public AktieLoeschen(int index, BenutzerAktie ba) {
	super(AktienMan.AMFENSTERTITEL+"Aktie l\u00f6schen",index,ba);
}


public void setupElements2() {
	Label label = new Label("Wollen Sie die Aktie \""+ba.getName(BenutzerListe.useShortNames())+"\" wirklich l\u00f6schen?");
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
	AktienMan.hauptdialog.listeAktieLoeschen(index);
}


public void closed() {
	AktienMan.aktieloeschen = null;
}

}
