/**
 @author Thomas Much
 @version 2003-02-17

 2003-02-17
    erste Version
*/

import java.awt.*;
import java.awt.event.*;




public class Wechselkurse extends AFrame {

private Button buttonAktualisieren;
private Panel panelRates;




public Wechselkurse() {

	super(AktienMan.AMFENSTERTITEL+"Wechselkurse");

	AktienMan.hauptdialog.windowToFront(this);
}



public void setupElements() {

	buttonAktualisieren = new Button("Aktualisieren");
	buttonAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			werteAktualisieren();
		}
	});

	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	Panel panelButtons = new Panel( new FlowLayout(FlowLayout.RIGHT) );
	
	panelButtons.add(buttonAktualisieren);
	panelButtons.add(buttonOK);

	panelRates = new Panel(gridbag);
	
	fillRatePanel(false);
	
	add(new Label(""), BorderLayout.NORTH);
	add(panelRates, BorderLayout.CENTER);
	add(panelButtons, BorderLayout.SOUTH);
}



private synchronized void fillRatePanel(boolean draw) {

	if (draw) panelRates.removeAll();
	
	int ypos = 0;
	
	for (int i = 0; i < Waehrungen.COUNT; i++)
	{
		if (i != Waehrungen.EUR)
		{
			constrain(panelRates,new Label("1 EUR"),0,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,15,0,0);
			constrain(panelRates,new Label("="),1,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,5);
			constrain(panelRates,new Label(Waehrungen.getExchangeRate(Waehrungen.EUR,i)),2,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,15);
			constrain(panelRates,new Label("(" + Waehrungen.getExchangeDate(Waehrungen.EUR,i) + ")"),3,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,50);

			ypos++;
		}
	}

	constrain(panelRates,new Label(""),0,ypos++,4,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	for (int i = 0; i < Waehrungen.COUNT; i++)
	{
		if (i != Waehrungen.EUR)
		{
			constrain(panelRates,new Label("1 " + Waehrungen.index2Id(i)),0,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,15,0,0);
			constrain(panelRates,new Label("="),1,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,5);
			constrain(panelRates,new Label(Waehrungen.getExchangeRate(i,Waehrungen.EUR)),2,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,15);
			constrain(panelRates,new Label("(" + Waehrungen.getExchangeDate(i,Waehrungen.EUR) + ")"),3,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,50);

			ypos++;
		}
	}

	constrain(panelRates,new Label(""),0,ypos++,4,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	if (draw)
	{
		pack();
		setSize(getSize());
		panelRates.paintAll(getGraphics());
	}

	buttonAktualisieren.setEnabled(true);
}



public void closed() {

	AktienMan.wechselkurse = null;
}



public void update() {

	fillRatePanel(true);
}



private void werteAktualisieren() {

	buttonAktualisieren.setEnabled(false);

	Waehrungen.check(true);
}


}
