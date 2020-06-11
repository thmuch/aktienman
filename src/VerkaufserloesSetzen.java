// 1998-09-21 tm

import java.awt.*;
import java.awt.event.*;



public class VerkaufserloesSetzen extends AktienFrame {

private Button buttonOK;
private TextField neuerErloes;



public VerkaufserloesSetzen() {
	super(AktienMan.AMFENSTERTITEL+"Verkaufserl\u00f6s setzen",-1,null);
}


public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	long erloes = AktienMan.hauptdialog.getErloes();
	
	constrain(panelOben,new Label("bisher:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label(Waehrungen.getString(erloes,Waehrungen.DEM)),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelOben,new Label("neu:"),2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,15,0,0);

	neuerErloes = new TextField(AktienMan.get00String(erloes),8);
	constrain(panelOben,neuerErloes,3,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,new Label("DM"),4,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelButtons,new Label(),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	Button buttonAbbruch = new Button(Lang.CANCEL);
	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(panelButtons,buttonAbbruch,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(panelButtons,buttonOK,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHEAST,1.0,0.0,15,10,10,10);
}


public synchronized void executeOK() {
	buttonOK.setEnabled(false);

	try
	{
		AktienMan.hauptdialog.setErloes(Waehrungen.doubleToLong(neuerErloes.getText()));
	}
	catch (NumberFormatException e) {}
}


public void closed() {
	AktienMan.erloessetzen = null;
}

}
