/**
 @author Thomas Much
 @version 1998-10-22
*/

import java.awt.*;
import java.awt.event.*;



public class Konfiguration extends AFrame {

private Choice plaetze,waehrung,bank;
private Checkbox cbAktualisieren,cbKamera,cbAktiennamen,cbKuerzen;
private TextField tfStdGewinn,tfStdGebuehren;



public Konfiguration() {
	super(AktienMan.AMFENSTERTITEL+"Konfiguration");
}


public void setupElements() {
	setLayout(gridbag);

	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	cbAktualisieren = new Checkbox("Liste beim Programmstart automatisch aktualisieren",AktienMan.properties.getBoolean("Konfig.Aktualisieren"));
	cbKamera = new Checkbox("DAX-Kamera beim Programmstart automatisch anzeigen",AktienMan.properties.getBoolean("Konfig.Kamera"));
	cbAktiennamen = new Checkbox("Aktiennamen aus Onlinedaten \u00fcbernehmen",AktienMan.properties.getBoolean("Konfig.Aktiennamen"));
	cbKuerzen = new Checkbox("Aktiennamen k\u00fcrzen",BenutzerListe.useShortNames());
	
	tfStdGewinn = new TextField(AktienMan.properties.getString("Konfig.StdGewinn"),6);
	tfStdGebuehren = new TextField(AktienMan.properties.getString("Konfig.StdGebuehren"),6);
	
	constrain(panelOben,cbAktualisieren,0,0,4,1);
	constrain(panelOben,cbKamera,0,1,4,1);
	constrain(panelOben,cbAktiennamen,0,2,4,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,6,0,0,0);
	constrain(panelOben,cbKuerzen,0,3,4,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,2,0);

	constrain(panelOben,new Label("Standard-B\u00f6rse:"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,6,5,0,0);

	plaetze = AktienMan.boersenliste.getChoice();
	plaetze.select(AktienMan.boersenliste.getStandardBoerse());
	constrain(panelOben,plaetze,1,4,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,6,5,0,0);

	constrain(panelOben,new Label("Standard-Gewinngrenze:"),0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,5,0,0);
	constrain(panelOben,tfStdGewinn,1,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelOben,new Label("%"),2,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,new Label("Standard-W\u00e4hrung:"),0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,5,0,0);

	waehrung = AktienMan.waehrungen.getChoice();
	waehrung.select(Waehrungen.getStandardWaehrung());
	constrain(panelOben,waehrung,1,6,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelOben,new Label("Standard-Bank:"),0,7,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,5,0,0);

	bank = AktienMan.bankenliste.getChoice();
	bank.select(AktienMan.bankenliste.getStandardBank());
	constrain(panelOben,bank,1,7,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,new Label("(Stand: "+Bankenliste.STAND+")"),3,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,new Label("Standard-Geb\u00fchren (sonst.):"),0,8,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,5,0,0);
	constrain(panelOben,tfStdGebuehren,1,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
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
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,5,5);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,10,10,10);
}



public void executeOK() {
	AktienMan.properties.setBoolean("Konfig.Aktualisieren",cbAktualisieren.getState());
	AktienMan.properties.setBoolean("Konfig.Kamera",cbKamera.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen",cbAktiennamen.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen.kuerzen",cbKuerzen.getState());
	AktienMan.properties.setString("Konfig.StdGewinn",tfStdGewinn.getText());
	AktienMan.properties.setString("Konfig.StdGebuehren",tfStdGebuehren.getText());
	AktienMan.properties.setInt("Konfig.StdBoerse",plaetze.getSelectedIndex());
	AktienMan.properties.setInt("Konfig.StdWaehrung",waehrung.getSelectedIndex());
	AktienMan.properties.setInt("Konfig.StdBank",bank.getSelectedIndex());
	
	AktienMan.properties.saveParameters();
	
	AktienMan.hauptdialog.listeUpdate(false);
}


public void closed() {
	AktienMan.konfiguration = null;
}

}
