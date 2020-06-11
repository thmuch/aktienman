/**
 @author Thomas Much
 @version 1999-02-05
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;



public final class PortfolioUmbenennen extends AktienFrame {

private Button buttonRename;
private TextField neuername;
private String altername;

private boolean umbenannt = false;



public PortfolioUmbenennen() {
	super(AktienMan.AMFENSTERTITEL+"Portfolio umbenennen");
}


public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	altername = Portfolios.getCurrentName();
	
	constrain(panelOben,new Label("Alter Name:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label(altername),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	neuername = new TextField(altername,27);
	constrain(panelOben,new Label("Neuer Name:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,neuername,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	Button buttonCancel = new Button(Lang.CANCEL);
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(panelButtons,buttonCancel,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	buttonRename = new Button(" Umbenennen ");
	buttonRename.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(panelButtons,buttonRename,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);

	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


private String getNewName() {
	return Portfolios.fixFilename(neuername.getText());
}


public synchronized boolean canOK() {
	String pofoName = getNewName();

	if (pofoName.length() == 0)
	{
		new Warnalert(this,"Bitte geben Sie einen Portfolionamen ein.");
		return false;
	}
	
	if (pofoName.equals(altername))
	{
		return true;
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
	buttonRename.setEnabled(false);
	
	String pofoName = getNewName();

	if (!pofoName.equals(altername))
	{
		File f = new File(Portfolios.getNewFile(altername));
		
		String neu = Portfolios.getNewFile(pofoName);
		File fneu = new File(neu);
		
		if (f.renameTo(fneu))
		{
			AktienMan.hauptdialog.setPortfolioFile(neu);
			umbenannt = true;
		}
	}
}


public void cleanupAfterUnlock() {
	if (umbenannt)
	{
		Portfolios.updateMenu(false);
		Portfolios.changeIndexByName(getNewName());
	}
}


public void closed() {
	AktienMan.portfolioumbenennen = null;
}

}
