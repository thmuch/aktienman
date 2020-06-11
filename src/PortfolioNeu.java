/**
 @author Thomas Much
 @version 1999-02-05
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;



public final class PortfolioNeu extends AktienFrame {

private Button buttonNeu;
private TextField name;

private boolean angelegt = false;
private BenutzerListe leer = null;



public PortfolioNeu() {
	super(AktienMan.AMFENSTERTITEL+"Neues Portfolio anlegen");
}


public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);

	name = new TextField(27);
	constrain(panelOben,new Label("Name:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,name,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	Button buttonCancel = new Button(Lang.CANCEL);
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(panelButtons,buttonCancel,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	buttonNeu = new Button("  Anlegen  ");
	buttonNeu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(panelButtons,buttonNeu,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);

	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


private String getNewName() {
	return Portfolios.fixFilename(name.getText());
}


public synchronized boolean canOK() {
	String pofoName = getNewName();

	if (pofoName.length() == 0)
	{
		new Warnalert(this,"Bitte geben Sie einen Portfolionamen ein.");
		return false;
	}
	
	File f = new File(Portfolios.getNewFile(pofoName));
	
	if (f.exists())
	{
		new Warnalert(this,"Der Portfolioname wird bereits verwendet!|Bitte geben Sie einen neuen Namen ein.");
		return false;
	}
	
	return true;
}


public synchronized void executeOK() {
	buttonNeu.setEnabled(false);
	
	leer = new BenutzerListe();
	leer.setPortfolioFile(Portfolios.getNewFile(getNewName()));
	BenutzerListe.store(leer);
	
	angelegt = true;
}


public void cleanupAfterUnlock() {
	if (angelegt)
	{
		Portfolios.updateMenu(false);
		Portfolios.setIndexByName(getNewName());
	}
}


public void closed() {
	AktienMan.portfolioneu = null;
}

}
