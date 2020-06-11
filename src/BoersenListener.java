/**
 @author Thomas Much
 @version 1999-06-28
*/

import java.awt.event.*;




public final class BoersenListener implements ActionListener {

private int boersenplatz;




public BoersenListener(int boersenplatz) {

	this.boersenplatz = boersenplatz;
}



public void actionPerformed(ActionEvent e) {

	AktienMan.hauptdialog.listeAktualisieren(boersenplatz);
}

}
