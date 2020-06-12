/**
 @author Thomas Much
 @version 2000-03-13
*/

import java.awt.*;
import java.awt.event.*;




public final class Konfiguration extends AFrame {

private Choice plaetze,waehrung,bank,quelle,charts,connections;
private Checkbox cbAktualisieren,cbKamera,cbAktiennamen,cbKuerzen,cbSteuerfrei,cbTimeout,cbJahr,cbIndex;
//private Checkbox rb6Monate,rb12Monate;
private TextField tfStdGewinn,tfStdGebuehren,tfMinuten;




public Konfiguration() {

	super(AktienMan.AMFENSTERTITEL+"Voreinstellungen");
}



public void setupElements() {

	setLayout(gridbag);

	Panel panelOben = new Panel(gridbag);
	Panel panelStandard = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	cbAktualisieren = new Checkbox("Liste beim Programmstart automatisch aktualisieren",AktienMan.properties.getBoolean("Konfig.Aktualisieren"));
	cbKamera = new Checkbox("DAX-Kamera beim Programmstart automatisch anzeigen",AktienMan.properties.getBoolean("Konfig.Kamera"));
	cbAktiennamen = new Checkbox("Aktiennamen aus Onlinedaten \u00fcbernehmen",BenutzerListe.useOnlineNames());
	cbKuerzen = new Checkbox("Aktiennamen k\u00fcrzen",BenutzerListe.useShortNames());
	cbSteuerfrei = new Checkbox("\"steuerfrei\" statt Laufzeit anzeigen",BenutzerListe.useSteuerfrei());
	cbTimeout = new Checkbox("Liste automatisch aktualisieren alle",AktienMan.properties.getBoolean("Konfig.KursTimeout"));
	cbJahr = new Checkbox("%Jahr erst nach 360 Tagen Laufzeit berechnen",BenutzerListe.calcProzJahr());
	cbIndex = new Checkbox("Indizes regelm\u00e4\u00dfig aktualisieren",IndexQuelle.autoIndexOn());
	
	tfStdGewinn = new TextField(AktienMan.properties.getString("Konfig.StdGewinn"),6);
	tfStdGebuehren = new TextField(AktienMan.properties.getString("Konfig.StdGebuehren"),6);
	tfMinuten = new TextField(KursDemon.getTimeoutMinutenString(),3);
	
	constrain(panelOben,cbAktualisieren,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,cbKamera,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel timeoutPanel = new Panel(gridbag);
	constrain(timeoutPanel,cbTimeout,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(timeoutPanel,tfMinuten,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(timeoutPanel,new Label("Minuten"),2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelOben,timeoutPanel,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	constrain(panelOben,cbIndex,0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel quellePanel = new Panel(gridbag);

	quelle = KursQuellen.getChoice();
	quelle.select(KursQuellen.getKursQuelleIndex());
	constrain(quellePanel,quelle,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(quellePanel,new Label("als Quelle f\u00fcr die Online-Kursdaten verwenden"),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	charts = ChartQuellen.getChoice();
	charts.select(ChartQuellen.getChartQuelleIndex());
	constrain(quellePanel,charts,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(quellePanel,new Label("als Quelle f\u00fcr die Online-Charts verwenden"),1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelOben,quellePanel,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	
	Panel connPanel = new Panel(gridbag);
	
	connections = Connections.getPopup();

	constrain(connPanel,new Label("Gleichzeitige Kursanfragen:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(connPanel,connections,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,connPanel,0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	
	constrain(panelOben,cbAktiennamen,0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	constrain(panelOben,cbKuerzen,0,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

/*	Panel spekuPanel = new Panel(gridbag);
	CheckboxGroup spekuGroup = new CheckboxGroup();

	rb6Monate = new Checkbox("6 Monate",false,spekuGroup);
	rb12Monate = new Checkbox("12 Monate",false,spekuGroup);
	if (AktienMan.properties.getInt("Konfig.Spekulationsfrist") == 12)
	{
		spekuGroup.setSelectedCheckbox(rb12Monate);
	}
	else
	{
		spekuGroup.setSelectedCheckbox(rb6Monate);
	}

	constrain(spekuPanel,new Label("Spekulationsfrist:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(spekuPanel,rb6Monate,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
	constrain(spekuPanel,rb12Monate,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelOben,spekuPanel,0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0); */

	constrain(panelOben,cbSteuerfrei,0,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,8,0,0,0);
	constrain(panelOben,cbJahr,0,9,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	constrain(panelStandard,new Label("Vorgaben f\u00fcr neu zu kaufende Aktien:"),0,0,4,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelStandard,new Label("Standard-B\u00f6rse:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	plaetze = AktienMan.boersenliste.getChoice(true);
	plaetze.select(AktienMan.boersenliste.getStandardBoerse());
	constrain(panelStandard,plaetze,1,1,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("Standard-Gewinngrenze:"),0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelStandard,tfStdGewinn,1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelStandard,new Label("%"),2,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	constrain(panelStandard,new Label("Standard-Kaufw\u00e4hrung:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	waehrung = AktienMan.waehrungen.getChoice(true);
	waehrung.select(Waehrungen.getStandardKaufwaehrung());
	constrain(panelStandard,waehrung,1,3,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelStandard,new Label("Standard-Bank:"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	bank = AktienMan.bankenliste.getChoice(true);
	bank.select(AktienMan.bankenliste.getStandardBank());
	constrain(panelStandard,bank,1,4,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("(Stand: "+Bankenliste.STAND+")"),3,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelStandard,new Label("Standard-Geb\u00fchren (sonst.):"),0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelStandard,tfStdGebuehren,1,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
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



public boolean canOK() {

	String s = tfMinuten.getText().trim();

	int i;
	try
	{
		i = Integer.parseInt(s);
	}
	catch (NumberFormatException e)
	{
		new Warnalert(this,"Bitte geben Sie bei den Minuten eine g\u00fcltige Zahl ein.");
		return false;
	}

	if (i <= 0)
	{
		new Warnalert(this,"Bitte geben Sie eine Minutenanzahl gr\u00f6\u00dfer Null ein.");
		return false;
	}

	return true;
}



public void executeOK() {

	AktienMan.properties.setBoolean("Konfig.Aktualisieren",cbAktualisieren.getState());
	AktienMan.properties.setBoolean("Konfig.Kamera",cbKamera.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen",cbAktiennamen.getState());
	AktienMan.properties.setBoolean("Konfig.Aktiennamen.kuerzen",cbKuerzen.getState());
	AktienMan.properties.setBoolean("Konfig.Steuerfrei",cbSteuerfrei.getState());
	AktienMan.properties.setBoolean("Konfig.ProzJahr",cbJahr.getState());
	AktienMan.properties.setString("Konfig.StdGewinn",tfStdGewinn.getText());
	AktienMan.properties.setString("Konfig.StdGebuehren",tfStdGebuehren.getText());
	AktienMan.properties.setInt("Konfig.StdBoerse",plaetze.getSelectedIndex());
	AktienMan.properties.setInt("Konfig.StdWaehrung",waehrung.getSelectedIndex());
	AktienMan.properties.setInt("Konfig.StdBank",bank.getSelectedIndex());
	AktienMan.properties.setBoolean("Konfig.Index",cbIndex.getState());
//	AktienMan.properties.setInt("Konfig.Spekulationsfrist",(rb12Monate.getState()) ? 12 : 6);
	
	KursQuellen.setKursQuelleIndex(quelle.getSelectedIndex());
	ChartQuellen.setChartQuelleIndex(charts.getSelectedIndex());
	
	Connections.setMaxConnections(connections.getSelectedIndex());
	
	boolean timeoutAktiv = cbTimeout.getState();
	AktienMan.properties.setBoolean("Konfig.KursTimeout",timeoutAktiv);
	
	try
	{
		KursDemon.setTimeoutMinuten(Integer.parseInt(tfMinuten.getText().trim()));
	}
	catch (NumberFormatException e) {}
	
	AktienMan.properties.saveParameters();
	
	AktienMan.hauptdialog.listeUpdate(false,false,true,false);
	
	KursDemon.deleteKursDemon();
	if (timeoutAktiv) KursDemon.createKursDemon();
}



public void closed() {

	AktienMan.konfiguration = null;
}

}
