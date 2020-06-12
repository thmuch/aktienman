/**
 @author Thomas Much
 @version 2000-03-06
*/

import java.awt.event.*;
import java.awt.*;




public final class PortfolioCopyMoveListener implements ItemListener {

private int index;
private boolean move;
private Hauptdialog hauptdialog;




public PortfolioCopyMoveListener(int index, Hauptdialog hauptdialog, boolean move) {

	this.index = index;
	this.move = move;
	this.hauptdialog = hauptdialog;
}



public void itemStateChanged(ItemEvent e) {

	((CheckboxMenuItem)e.getItemSelectable()).setState(false);

	hauptdialog.listeSelektierteAktieCopyMove(index,move);
}

}
