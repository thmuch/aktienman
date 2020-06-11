/**
 @author Thomas Much
 @version 1998-11-20
*/

import java.awt.*;
import java.awt.event.*;



public class AktieInfo extends AFrame {

private Panel panelInfo;
private BenutzerAktie ba;



public AktieInfo(BenutzerAktie ba) {
	super(AktienMan.AMFENSTERTITEL+"Info");
	this.ba = ba;

	setupElements2();

	pack();
	setupSize();
}


public void setupElements() {
	setLayout(gridbag);
}


public void display() {}


public synchronized void setupElements2() {
	panelInfo = new Panel(gridbag);
	
	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(this,panelInfo,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,buttonOK,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


public void closed() {
	ba.infoDialogClosed();
}


public synchronized void setValues(boolean draw) {
	if (draw) panelInfo.removeAll();

	constrain(panelInfo,new Label(ba.getName(false)),0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getWKNString()+"."+ba.getBoerse()),0,1,3,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,10,0);
	
	constrain(panelInfo,new Label("Kaufkurs:"),0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getKaufkursString()),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelInfo,new Label("akt. Kurs:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,10,0);
	constrain(panelInfo,new Label(ba.getKursString()),1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,10,0);
	constrain(panelInfo,new Label(ba.getKursdatumString()),2,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,10,0);

	constrain(panelInfo,new Label("Vortag Schlu\u00dfkurs:"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getVortageskursString()),1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelInfo,new Label("Er\u00f6ffnungskurs:"),0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,10,0);
	constrain(panelInfo,new Label(ba.getEroeffnungskursString()),1,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,10,0);
	
	constrain(panelInfo,new Label("H\u00f6chstkurs:"),0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getHoechstkursString()),1,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelInfo,new Label("Tiefstkurs:"),0,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,10,0);
	constrain(panelInfo,new Label(ba.getTiefstkursString()),1,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,10,0);

	constrain(panelInfo,new Label("Handelsvolumen:"),0,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getHandelsvolumenString()),1,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	String seit = ba.getWatchStartString();
	
	if (seit.length() > 0)
	{
		constrain(panelInfo,new Label("Seit "+seit+":"),0,9,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);

		constrain(panelInfo,new Label("H\u00f6chstkurs:"),0,10,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
		constrain(panelInfo,new Label(ba.getWatchHoechstString()),1,10,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
		constrain(panelInfo,new Label(ba.getWatchHoechstDatumString()),2,10,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

		constrain(panelInfo,new Label("Tiefstkurs:"),0,11,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
		constrain(panelInfo,new Label(ba.getWatchTiefstString()),1,11,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
		constrain(panelInfo,new Label(ba.getWatchTiefstDatumString()),2,11,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
	}

	pack();
	
	if (draw)
	{
		setSize(getSize());
		panelInfo.paintAll(getGraphics());
	}
	else
	{
		setupSize();
	}
}

}
