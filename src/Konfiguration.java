/**
 @author Thomas Much
 @version 1998-11-23
*/

import java.awt.*;
import java.awt.event.*;



public class Konfiguration extends AFrame {

private Choice plaetze,waehrung,bank;
private Checkbox cbAktualisieren,cbKamera,cbAktiennamen,cbKuerzen,cbSteuerfrei;
private TextField tfStdGewinn,tfStdGebuehren;



public Konfiguration() {
	super(AktienMan.AMFENSTERTITEL+"Konfiguration");
}


public void setupElements() {
	setLayout(gridbag);

	Panel panelOben = new Panel(gridbag);
	Panel panelStandard = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	cbAktualisieren = new Checkbox("Liste beim Programmstart automatisch aktualisieren",AktienMan.properties.getBoolean("Konfig.Aktualisieren"));
	cbKamera = new Checkbox("DAX-Kamera beim Programmstart automatisch anzeigen",AktienMan.properties.getBoolean("Konfig.Kamera"));
	cbAktiennamen = new Checkbox("Aktiennamen aus Onlinedaten \u00fcbernehmen",AktienMan.properties.getBoolean("Konfig.Aktiennamen"));
	cbKuerzen = new Checkbox("Aktiennamen k\u00fcrzen",BenutzerListe.useShortNames());
	cbSteuerfrei = new Checkbox("\"steuerfrei\" statt Laufzeit anzeigen",BenutzerListe.useSteuerfrei());
	
	tfStdGewinn = new TextField(AktienMan.properties.getString("Konfig.StdGewinn"),6);
	tfStdGebuehren = new TextField(AktienMan.properties.getString("Konfig.StdGebuehren"),6);
	
	constrain(panelOben,cbAktualisieren,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,cbKamera,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,cbAktiennamen,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	constrain(panelOben,cbKuerzen,0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,cbSteuerfrei,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);

	constrain(panelStandard,new Label("Standard-B\u00f6rse:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	plaetze = AktienMan.boersenliste.getChoice();
	plaetze.select(AktienMan.boersenliste.getStandardBoerse());
	constrain(panelStandard,plaetze,1,0,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("Standard-Gewinngrenze:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelStandard,tfStdGewinn,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelStandard,new Label("%"),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	constrain(panelStandard,new Label("Standard-W\u00e4hrung:"),0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	waehrung = AktienMan.waehrungen.getChoice();
	waehrung.select(Waehrungen.getStandardWaehrung());
	constrain(panelStandard,waehrung,1,2,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelStandard,new Label("Standard-Bank:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	bank = AktienMan.bankenliste.getChoice();
	bank.select(AktienMan.bankenliste.getStandardBank());
	constrain(panelStandard,bank,1,3,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("(Stand: "+Bankenliste.STAND+")"),3,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("Standard-Geb\u00fchren (sonst.):"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelStandard,tfStdGebuehren,1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
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
	constrain(this,panelStandard,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,5,10,5,10);
	constrain(this,panelButtons,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,10,10,10);
}



public void executeOK() {
	AktienMan.properties.setBoolean("Konfig.Aktualisieren",cbAktualisieren.getState());
	AktienMan.properties.setBoolean("Konfig.Kamera",cbKamera.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen",cbAktiennamen.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen.kuerzen",cbKuerzen.getState());
	AktienMan.properties.setBoolean("Konfig.Steuerfrei",cbSteuerfrei.getState());
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
