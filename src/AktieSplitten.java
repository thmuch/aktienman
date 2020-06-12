/**
 @author Thomas Much
 @version 2000-07-29
*/

import java.awt.*;
import java.awt.event.*;




public final class AktieSplitten extends AktienFrame {

private static final int AUS_MIN    =  1;
private static final int AUS_MAX    = 10;
private static final int WERDEN_MIN =  2;
private static final int WERDEN_MAX = 10;

private Button buttonSplitten;
private Choice choiceAus, choiceWerden;




public AktieSplitten(int index, BenutzerAktie ba) {

	super(AktienMan.AMFENSTERTITEL+"Aktie splitten",index,ba);
}



public void setupElements2() {

	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);

	constrain(panelOben,new Label("Aktie \""+ba.getName(BenutzerListe.useShortNames())+"\":"),0,0,5,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,10,0);
	
	choiceAus = new Choice();
	choiceWerden = new Choice();

	for (int i = AUS_MIN; i <= AUS_MAX; i++)
	{
		choiceAus.addItem(""+i);
	}

	for (int i = WERDEN_MIN; i <= WERDEN_MAX; i++)
	{
		choiceWerden.addItem(""+i);
	}

	constrain(panelOben,new Label("Aus"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,5);
	constrain(panelOben,choiceAus,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label("St\u00fcck(en) werden"),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,5,0,5);
	constrain(panelOben,choiceWerden,3,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label("St\u00fccke."),4,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,5,0,0);
	
	choiceAus.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		
			int aus = AUS_MIN + choiceAus.getSelectedIndex();
			int werden = WERDEN_MIN + choiceWerden.getSelectedIndex();
			
			if (aus > werden)
			{
				choiceWerden.select(aus - WERDEN_MIN);
			}
		}
	});

	choiceWerden.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		
			int aus = AUS_MIN + choiceAus.getSelectedIndex();
			int werden = WERDEN_MIN + choiceWerden.getSelectedIndex();
			
			if (werden < aus)
			{
				choiceAus.select(werden - AUS_MIN);
			}
		}
	});

	choiceAus.select(0);
	choiceWerden.select(0);
	
	constrain(panelButtons,new Label(""),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);

	Button buttonAbbruch = new Button(Lang.CANCEL);
	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(panelButtons,buttonAbbruch,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	buttonSplitten = new Button(" Splitten ");
	buttonSplitten.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(panelButtons,buttonSplitten,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHEAST,1.0,0.0,15,10,10,10);
}



public synchronized void executeOK() {

	double faktor = (double)(WERDEN_MIN + choiceWerden.getSelectedIndex()) / (double)(AUS_MIN + choiceAus.getSelectedIndex());

	ba.split(faktor);

	AktienMan.hauptdialog.listeUpdate(true,true,true,false);
}



public void closed() {

	AktienMan.aktiesplitten = null;
}

}
