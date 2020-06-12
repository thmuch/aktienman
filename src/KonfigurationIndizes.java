/**
 @author Thomas Much
 @version 2000-08-13
*/

import java.awt.*;
import java.awt.event.*;




public final class KonfigurationIndizes extends AFrame {

private Checkbox   cbAuto;
private Checkbox[] icb;




public KonfigurationIndizes() {

	super(AktienMan.AMFENSTERTITEL+"Indizes");
}



public void setupElements() {

	setLayout(gridbag);

	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	IndexQuelle.loadValues();
	
	cbAuto = new Checkbox("Indizes regelm\u00e4\u00dfig aktualisieren",IndexQuelle.autoIndexOn());

	constrain(panelOben,cbAuto,0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,15,0);

	constrain(panelOben,new Label("Folgende Indizes werden angezeigt:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Button buttonAlle = new Button(" alle ");
	Button buttonKeine = new Button(" keine ");

	buttonAlle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			for (int i = 0; i < IndexQuelle.iIndex.length; i++)
			{
				icb[i].setState(true);
			}
		}
	});
	
	buttonKeine.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			for (int i = 0; i < IndexQuelle.iIndex.length; i++)
			{
				icb[i].setState(false);
			}
		}
	});

	constrain(panelOben,buttonAlle,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,15,0,0);
	constrain(panelOben,buttonKeine,2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	icb = new Checkbox[IndexQuelle.iIndex.length];
	
	for (int i = 0; i < IndexQuelle.iIndex.length; i++)
	{
		icb[i] = new Checkbox(IndexQuelle.iDescr[i],IndexQuelle.isIndexOn(IndexQuelle.iIndex[i]));

		constrain(panelOben,icb[i],0,2+i,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	}
	
//	Label achtung = new Label("\u00c4nderungen werden erst nach dem n\u00e4chsten Programmstart wirksam!");
//	achtung.setForeground(Color.red);
//	constrain(panelOben,achtung,0,2+IndexQuelle.iIndex.length,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,2,0,0,0);
	
	Button buttonOK = new Button(Lang.OK);
	Button buttonAbbruch = new Button(Lang.CANCEL);

	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});

	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(panelButtons,buttonAbbruch,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonOK,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,5,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,10,10,10);
}



public void executeOK() {

	for (int i = 0; i < IndexQuelle.iIndex.length; i++)
	{
		if (icb[i].getState())
		{
			IndexQuelle.addIndex(IndexQuelle.iIndex[i]);
		}
		else
		{
			IndexQuelle.removeIndex(IndexQuelle.iIndex[i]);
		}
	}
	
	IndexQuelle.saveValues();

	AktienMan.properties.setBoolean("Konfig.Index",cbAuto.getState());
	AktienMan.properties.saveParameters();
	
	IndexQuelle.renewIndices();
}



public void closed() {

	AktienMan.konfigurationIndizes = null;
}

}
