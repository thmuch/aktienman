/**
 @author Thomas Much
 @version 2000-08-07
*/

import java.awt.event.*;




public final class BoersenListener implements ActionListener {

private int boersenplatz;




public BoersenListener(int boersenplatz) {

	this.boersenplatz = boersenplatz;
}



public void actionPerformed(ActionEvent e) {

	AktienMan.hauptdialog.listeAktualisieren(boersenplatz,KursQuellen.getPlatzKursQuelle());
}

}
