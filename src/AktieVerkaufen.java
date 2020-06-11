/**
 @author Thomas Much
 @version 1999-06-13
*/

import java.awt.*;
import java.awt.event.*;



public final class AktieVerkaufen extends AktienFrame {

private Button buttonVerkaufen;
private Button buttonAlle;
private TextField anzahl,verkaufskurs,gebuehren;
private Choice banken;
private Label bankGebuehren;
private Checkbox cbTelefon,cbInternet,cbErloes;
private CheckboxGroup tradeGroup;



public AktieVerkaufen(int index, BenutzerAktie ba) {
	super(AktienMan.AMFENSTERTITEL+"Aktie verkaufen",index,ba);
}


public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);

	constrain(panelOben,new Label("Aktie \""+ba.getName(BenutzerListe.useShortNames())+"\":"),0,0,4,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,10,0);
	
	anzahl = new TextField(ba.getStueckzahlString(),8);
	anzahl.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkButtonAlle();
		}
	});
	constrain(panelOben,anzahl,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(panelOben,new Label("von "+ba.getStueckzahlString()),1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

	buttonAlle = new Button("^^ alle ^^");
	buttonAlle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			anzahl.setText(ba.getStueckzahlString());
		}
	});
	constrain(panelOben,buttonAlle,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,2,0,0,0);
	
	constrain(panelOben,new Label("Bank:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,5,0,0,0);

	banken = AktienMan.bankenliste.getChoice(true);
	banken.select(AktienMan.bankenliste.getStandardBank());
	banken.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkTrade();
			checkBankGebuehren();
		}
	});
	constrain(panelOben,banken,1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,5,0,0);
	
	constrain(panelOben,new Label("Verkaufskurs:"),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,15,0,0);

	verkaufskurs = new TextField(ba.getRawVerkaufsKursString(),10);
	verkaufskurs.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkVerkaufskurs();
		}
	});
	constrain(panelOben,verkaufskurs,3,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	constrain(panelOben,new Label("sonst. Geb\u00fchren:"),2,2,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,5,15,0,0);

	gebuehren = new TextField(AktienMan.properties.getString("Konfig.StdGebuehren"),10);
	constrain(panelOben,gebuehren,3,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,5,0,0);

	constrain(panelOben,new Label("Bankgeb\u00fchren:"),2,3,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,5,15,0,0);

	bankGebuehren = new Label();
	constrain(panelOben,bankGebuehren,3,3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,5,0,0);
	
	constrain(panelOben,new Label("Verkauf per"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	Panel panelTrade = new Panel(gridbag);

	tradeGroup = new CheckboxGroup();
	
	cbTelefon = new Checkbox("Telefon",false,tradeGroup);
	cbTelefon.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkBankGebuehren();
		}
	});

	cbInternet = new Checkbox("Internet",false,tradeGroup);
	cbInternet.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkBankGebuehren();
		}
	});

	constrain(panelTrade,cbTelefon,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelTrade,cbInternet,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,4,0,0);
		
	constrain(panelOben,panelTrade,1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,5,0,0);

	Panel panelErloes = new Panel(gridbag);
	
	cbErloes = new Checkbox("Gesamtaufwand berechnen",true);
	constrain(panelErloes,cbErloes,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	constrain(panelOben,panelErloes,2,4,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,15,0,0);
	
	Label achtung = new Label();
	if (!ba.istSteuerfrei())
	{
		achtung.setText("Noch nicht steuerfrei!");
		achtung.setForeground(Color.red);
	}
	constrain(panelButtons,achtung,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	Button buttonAbbruch = new Button(Lang.CANCEL);
	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(panelButtons,buttonAbbruch,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	buttonVerkaufen = new Button(" Verkaufen ");
	buttonVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(panelButtons,buttonVerkaufen,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHEAST,1.0,0.0,15,10,10,10);
	
	checkTrade();
	checkButtonAlle();
}


private void checkTrade() {
	int bindex = banken.getSelectedIndex();
	
	if (bindex < 1)
	{
		cbTelefon.setEnabled(false);
		cbInternet.setEnabled(false);
	}
	else
	{
		cbTelefon.setEnabled(true);

		Bank bank = AktienMan.bankenliste.getAt(bindex);

		if (bank.hasInternetTrade())
		{
			cbInternet.setEnabled(true);
			cbInternet.setState(true);
		}
		else
		{
			cbTelefon.setState(true);
			cbInternet.setEnabled(false);
		}
	}
}


private void checkBankGebuehren() {
	long anz = 0L, vkurs = 0L;
	boolean error = false;

	try
	{
		anz = Long.parseLong(anzahl.getText());
		if (anz < 0L) error = true;
	}
	catch (NumberFormatException e)
	{
		error = true;
	}

	try
	{
		vkurs = Waehrungen.doubleToLong(verkaufskurs.getText());
		if (vkurs < 0L) error = true;
	}
	catch (NumberFormatException e)
	{
		error = true;
	}

	if (error)
	{
		bankGebuehren.setText("(Fehler)");
	}
	else
	{
		Bank bank = AktienMan.bankenliste.getAt(banken.getSelectedIndex());
		bankGebuehren.setText(bank.getGebuehrenString(anz*vkurs,cbInternet.getState()));
	}
}


private void checkVerkaufskurs() {
	long vkurs;
	
	String vstr = verkaufskurs.getText();

	if (vstr.length() > 0)
	{
		try
		{
			vkurs = Waehrungen.doubleToLong(vstr);
		}
		catch (NumberFormatException e)
		{
			verkaufskurs.setText("");
			return;
		}
		
		if (vkurs < 0L)
		{
			verkaufskurs.setText("");
			return;
		}
		
		if (vkurs >= (1000000L * Waehrungen.PRECISION))
		{
			verkaufskurs.setText("999999.99");
			return;
		}
	}
	
	checkBankGebuehren();
}


private void checkButtonAlle() {
	String anzstr = anzahl.getText();
	
	if (anzstr.length() == 0)
	{
		buttonVerkaufen.setEnabled(false);
		buttonAlle.setEnabled(true);
	}
	else
	{
		long anz;
		try
		{
			anz = Long.parseLong(anzstr);
		}
		catch (NumberFormatException e)
		{
			buttonVerkaufen.setEnabled(false);
			buttonAlle.setEnabled(true);
			
			anzahl.setText("");
			return;
		}
		
		boolean changed = false;
		long stueck = ba.getStueckzahl();

		if (anz < 1L)
		{
			anz = 1L;
			changed = true;
		}
		else if (anz > stueck)
		{
			anz = stueck;
			changed = true;
		}
		
		if (anz == stueck)
		{
			buttonAlle.setEnabled(false);
		}
		else
		{
			buttonAlle.setEnabled(true);
		}

		buttonVerkaufen.setEnabled(true);
		
		if (changed) anzahl.setText(""+anz);
	}
	
	checkBankGebuehren();
}


public synchronized void executeOK() {
	long verkaufsKurs = 0L, anz = 0L, geBuehren = 0L;
	
	buttonVerkaufen.setEnabled(false);
	
	try
	{
		verkaufsKurs = Waehrungen.doubleToLong(verkaufskurs.getText());
	}
	catch (NumberFormatException e) {}
	
	String s = gebuehren.getText();
	
	if (s.length() > 0)
	{
		try
		{
			geBuehren = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e) {}
	}
	
	try
	{
		anz = Long.parseLong(anzahl.getText());
	}
	catch (NumberFormatException e) {}

	if (cbErloes.getState())
	{
		Bank bank = AktienMan.bankenliste.getAt(banken.getSelectedIndex());
		geBuehren += bank.getGebuehren(anz*verkaufsKurs,cbInternet.getState());
	}
	else
	{
		geBuehren = 0L;
		verkaufsKurs = 0L;
	}

	AktienMan.hauptdialog.listeAktieVerkaufen(index,anz,verkaufsKurs,geBuehren);
}


public synchronized boolean canOK() {
	long db;
	try
	{
		db = Waehrungen.doubleToLong(verkaufskurs.getText());
	}
	catch (NumberFormatException e)
	{
		new Warnalert(this,"Bitte geben Sie beim Verkaufskurs eine g\u00fcltige Zahl ein.");
		return false;
	}
	
	if (db <= 0L)
	{
		new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Verkaufskurs ein.");
		return false;
	}
	
	String s = gebuehren.getText();
	
	if (s.length() > 0)
	{
		try
		{
			db = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e)
		{
			new Warnalert(this,"Bitte geben Sie bei den Geb\u00fchren eine g\u00fcltige Zahl ein.");
			return false;
		}
		
		if (db < 0L)
		{
			new Warnalert(this,"Bitte geben Sie g\u00fcltige Geb\u00fchren ein.");
			return false;
		}
	}
	
	return true;
}


public void closed() {
	AktienMan.aktieverkaufen = null;
}

}
