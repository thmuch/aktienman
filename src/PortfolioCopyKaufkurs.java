/**
 @author Thomas Much
 @version 2000-08-15
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.apple.mrj.*;




public final class PortfolioCopyKaufkurs extends AktienFrame {

private Button buttonJa;




public PortfolioCopyKaufkurs() {

	super(AktienMan.AMFENSTERTITEL+"Kaufkurse setzen");
}



public void setupElements2() {

	Label label = new Label("Wollen Sie im Portfolio \"" + Portfolios.getCurrentName() + "\" die Kaufkurse aller nur beobachteten Aktien auf die aktuellen Kurse setzen?");
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
	
	AktienMan.hauptdialog.listeCopyKaufkurs();
}



public void closed() {

	AktienMan.portfoliocopykaufkurs = null;
}

}
