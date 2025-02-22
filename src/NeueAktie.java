/**
 @author Thomas Much
 @version 2003-04-03
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;




public final class NeueAktie extends LockedFrame {

private TextField kaufdatum,kaufkurs,stueckzahl,aktienWKN,gewinngrenze,hochkurs,tiefkurs;
private CheckboxGroup aktienGruppe,gewinnGruppe;
private Checkbox dax30Checkbox,mdaxCheckbox,tecdaxCheckbox;
private Checkbox eurostoxx50Checkbox,stoxx50Checkbox,wknCheckbox;
private Checkbox boerseNurDiese,gewinnAbs,gewinnProz;
private Choice plaetze,aktienDAX30,aktienMDAX,aktienTecDAX,aktienEuroSTOXX50,aktienSTOXX50,waehrung;
private Button buttonOK,buttonBeobachten;
private boolean watchonly = false;




public NeueAktie() {

	super(AktienMan.AMFENSTERTITEL+"Aktie kaufen");

	aktienWKN.requestFocus();

	if ((AktienMan.listeDAX30.getChoice(false).getItemCount() < 1) ||
		(AktienMan.listeMDAX.getChoice(false).getItemCount() < 1) ||
		(AktienMan.listeTecDAX.getChoice(false).getItemCount() < 1) ||
		(AktienMan.listeEuroSTOXX50.getChoice(false).getItemCount() < 1) ||
		(AktienMan.listeSTOXX50.getChoice(false).getItemCount() < 1))
	{
		new TextWarnalert(this,"Bitte gehen Sie online und rufen dann den Men\u00fcpunkt|\"Aktienmen\u00fcs aktualisieren\" im Men\u00fc \""+Lang.EDITMENUTITLE+"\" auf,|damit Sie Aktien per Name (und nicht nur per WKN)|ausw\u00e4hlen k\u00f6nnen.");
	}
}



public void setupElements() {

	setLayout(gridbag);
	
	Panel panelAktie = new Panel(gridbag);
	Panel panelRest = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	aktienGruppe = new CheckboxGroup();
	gewinnGruppe = new CheckboxGroup();

	constrain(panelAktie,new Label("Aktie"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	dax30Checkbox = new Checkbox("DAX30:",false,aktienGruppe);
	mdaxCheckbox = new Checkbox("MDAX:",false,aktienGruppe);
	tecdaxCheckbox = new Checkbox("TecDAX:",false,aktienGruppe);
	eurostoxx50Checkbox = new Checkbox("EuroSTOXX50:",false,aktienGruppe);
	stoxx50Checkbox = new Checkbox("STOXX50:",false,aktienGruppe);
	wknCheckbox = new Checkbox("per WKN:",false,aktienGruppe);
	
	constrain(panelAktie,dax30Checkbox,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelAktie,mdaxCheckbox,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelAktie,tecdaxCheckbox,0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelAktie,eurostoxx50Checkbox,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelAktie,stoxx50Checkbox,0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelAktie,wknCheckbox,0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	aktienDAX30 = AktienMan.listeDAX30.getChoice(false);
	aktienDAX30.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			aktienWKN.setText( AktienMan.listeDAX30.getAktie(aktienDAX30.getSelectedIndex()).getWKNString() );
			aktienGruppe.setSelectedCheckbox(dax30Checkbox);
		}
	});
	if (aktienDAX30.getItemCount() < 1) dax30Checkbox.setEnabled(false);
	constrain(panelAktie,aktienDAX30,1,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);
	
	aktienMDAX = AktienMan.listeMDAX.getChoice(false);
	aktienMDAX.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			aktienWKN.setText( AktienMan.listeMDAX.getAktie(aktienMDAX.getSelectedIndex()).getWKNString() );
			aktienGruppe.setSelectedCheckbox(mdaxCheckbox);
		}
	});
	if (aktienMDAX.getItemCount() < 1) mdaxCheckbox.setEnabled(false);
	constrain(panelAktie,aktienMDAX,1,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	aktienTecDAX = AktienMan.listeTecDAX.getChoice(false);
	aktienTecDAX.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			aktienWKN.setText( AktienMan.listeTecDAX.getAktie(aktienTecDAX.getSelectedIndex()).getWKNString() );
			aktienGruppe.setSelectedCheckbox(tecdaxCheckbox);
		}
	});
	if (aktienTecDAX.getItemCount() < 1) tecdaxCheckbox.setEnabled(false);
	constrain(panelAktie,aktienTecDAX,1,3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	aktienEuroSTOXX50 = AktienMan.listeEuroSTOXX50.getChoice(false);
	aktienEuroSTOXX50.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			aktienWKN.setText( AktienMan.listeEuroSTOXX50.getAktie(aktienEuroSTOXX50.getSelectedIndex()).getWKNString() );
			aktienGruppe.setSelectedCheckbox(eurostoxx50Checkbox);
		}
	});
	if (aktienEuroSTOXX50.getItemCount() < 1) eurostoxx50Checkbox.setEnabled(false);
	constrain(panelAktie,aktienEuroSTOXX50,1,4,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	aktienSTOXX50 = AktienMan.listeSTOXX50.getChoice(false);
	aktienSTOXX50.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			aktienWKN.setText( AktienMan.listeSTOXX50.getAktie(aktienSTOXX50.getSelectedIndex()).getWKNString() );
			aktienGruppe.setSelectedCheckbox(stoxx50Checkbox);
		}
	});
	if (aktienSTOXX50.getItemCount() < 1) stoxx50Checkbox.setEnabled(false);
	constrain(panelAktie,aktienSTOXX50,1,5,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);

	aktienWKN = new TextField(7);
	aktienWKN.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			aktienGruppe.setSelectedCheckbox(wknCheckbox);
		}
	});
	constrain(panelAktie,aktienWKN,1,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	plaetze = AktienMan.boersenliste.getChoice(true);
	plaetze.select(AktienMan.boersenliste.getStandardBoerse());
	plaetze.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkNurDiese();
		}
	});
	constrain(panelAktie,new Label("B\u00f6rsenplatz:"),0,7,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,5,0,0,0);
	constrain(panelAktie,plaetze,1,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);

	boerseNurDiese = new Checkbox("Nur an dieser B\u00f6rse");
	constrain(panelAktie,boerseNurDiese,1,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	constrain(panelAktie,new Label("Kaufw\u00e4hrung:"),0,9,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,5,0,0,0);
	waehrung = Waehrungen.getChoice();
	waehrung.select(Waehrungen.getStandardKaufwaehrung());
	constrain(panelAktie,waehrung,1,9,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);
	
	constrain(panelRest,new Label("Kaufdatum"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("Kaufkurs"),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("St\u00fcckzahl"),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);
	
	constrain(panelRest,new Label("Gewinngrenze"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	constrain(panelRest,new Label("Stop Loss"),1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	
	kaufdatum = new TextField(8);
	Button buttonHeute = new Button("^ heute ^");
	
	buttonHeute.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			kaufdatum.setText(new ADate().toString());
		}
	});

	constrain(panelRest,kaufdatum,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,buttonHeute,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,2,0,0,0);
	
	kaufkurs = new TextField(8);
	constrain(panelRest,kaufkurs,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	stueckzahl = new TextField(8);
	constrain(panelRest,stueckzahl,1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel gewinnPanel = new Panel(gridbag);

	gewinnAbs = new Checkbox("",false,gewinnGruppe);
	gewinnProz = new Checkbox("",true,gewinnGruppe);

	hochkurs = new TextField(8);
	hochkurs.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			gewinnGruppe.setSelectedCheckbox(gewinnAbs);
		}
	});
	constrain(gewinnPanel,gewinnAbs,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,hochkurs,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	gewinngrenze = new TextField(AktienMan.properties.getString("Konfig.StdGewinn"),8);
	gewinngrenze.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			gewinnGruppe.setSelectedCheckbox(gewinnProz);
		}
	});
	constrain(gewinnPanel,gewinnProz,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,gewinngrenze,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,new Label("%"),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	constrain(panelRest,gewinnPanel,0,5,1,2,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel verlustPanel = new Panel(gridbag);
	
	tiefkurs = new TextField(8);
	constrain(verlustPanel,tiefkurs,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(verlustPanel,new Label(),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(panelRest,verlustPanel,1,5,1,2,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(panelRest,new Label("Angaben in Kaufw\u00e4hrung!",Label.CENTER),0,7,2,1,GridBagConstraints.NONE,GridBagConstraints.NORTH,0.0,0.0,2,0,0,0);

	buttonOK = new Button("  Kaufen  ");
	buttonBeobachten = new Button(" Beobachten ");
	Button buttonAbbruch = new Button(Lang.CANCEL);
	
	buttonBeobachten.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			watchonly = true;
			doOK();
		}
	});

	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			watchonly = false;
			doOK();
		}
	});

	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});

	Button menuesAktualisieren = new Button("Aktienmen\u00fcs online aktualisieren...");
	menuesAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			new AktienAktualisieren();
			doCancel();
		}
	});

	Panel menuPanel = new Panel(gridbag);
	constrain(menuPanel,menuesAktualisieren,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(panelButtons,menuPanel,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,0,50);
	constrain(panelButtons,buttonAbbruch,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonBeobachten,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonOK,3,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	
	constrain(this,panelAktie,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,5,10);
	constrain(this,panelRest,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHEAST,0.0,0.0,10,10,5,10);
	constrain(this,panelButtons,0,1,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);

	if (aktienDAX30.getItemCount() > 0)
	{
		aktienGruppe.setSelectedCheckbox(dax30Checkbox);
	}
	else if (aktienMDAX.getItemCount() > 0)
	{
		aktienGruppe.setSelectedCheckbox(mdaxCheckbox);
	}
	else if (aktienTecDAX.getItemCount() > 0)
	{
		aktienGruppe.setSelectedCheckbox(tecdaxCheckbox);
	}
	else if (aktienEuroSTOXX50.getItemCount() > 0)
	{
		aktienGruppe.setSelectedCheckbox(eurostoxx50Checkbox);
	}
	else if (aktienSTOXX50.getItemCount() > 0)
	{
		aktienGruppe.setSelectedCheckbox(stoxx50Checkbox);
	}
	else
	{
		aktienGruppe.setSelectedCheckbox(wknCheckbox);
	}
	
	checkNurDiese();
}



public void checkNurDiese() {

	if (AktienMan.boersenliste.getAt(plaetze.getSelectedIndex()).isFondsOnly())
	{
		boerseNurDiese.setEnabled(false);
	}
	else
	{
		boerseNurDiese.setEnabled(true);
	}
}



public synchronized void executeOK() {
	long anzAktien = 1L, kaufKurs = 0L, hochKurs = 0L, tiefKurs = 0L, gewinnGrenze = 0L;
	ADate kaufDatum = new ADate();
	String s, name = "", wkn = "";
	
	buttonBeobachten.setEnabled(false);
	buttonOK.setEnabled(false);

	Boersenplatz bp = AktienMan.boersenliste.getAt(plaetze.getSelectedIndex());
	
	Checkbox cb = aktienGruppe.getSelectedCheckbox();
	
	if (cb == dax30Checkbox)
	{
		Aktie a = AktienMan.listeDAX30.getAktie(aktienDAX30.getSelectedIndex());
		
		name = a.getName();
		wkn = a.getWKNString();
	}
	else if (cb == mdaxCheckbox)
	{
		Aktie a = AktienMan.listeMDAX.getAktie(aktienMDAX.getSelectedIndex());
		
		name = a.getName();
		wkn = a.getWKNString();
	}
	else if (cb == tecdaxCheckbox)
	{
		Aktie a = AktienMan.listeTecDAX.getAktie(aktienTecDAX.getSelectedIndex());
		
		name = a.getName();
		wkn = a.getWKNString();
	}
	else if (cb == eurostoxx50Checkbox)
	{
		Aktie a = AktienMan.listeEuroSTOXX50.getAktie(aktienEuroSTOXX50.getSelectedIndex());
		
		name = a.getName();
		wkn = a.getWKNString();
	}
	else if (cb == stoxx50Checkbox)
	{
		Aktie a = AktienMan.listeSTOXX50.getAktie(aktienSTOXX50.getSelectedIndex());
		
		name = a.getName();
		wkn = a.getWKNString();
	}
	else if (cb == wknCheckbox)
	{
		wkn = aktienWKN.getText().trim().toUpperCase();
	}

	s = stueckzahl.getText().trim();
	if ((s.length() == 0) && watchonly)
	{
		anzAktien = 1L;
	}
	else
	{
		try
		{
			anzAktien = Long.parseLong(s);
		}
		catch (NumberFormatException e) {}
	}

	s = kaufkurs.getText().trim();
	if ((s.length() == 0) && watchonly)
	{
		kaufKurs = BenutzerAktie.VALUE_MISSING;
	}
	else
	{
		try
		{
			kaufKurs = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e) {}
	}
	
	s = kaufdatum.getText().trim();
	if ((s.length() == 0) && watchonly)
	{
		kaufDatum = new ADate();
	}
	else
	{
		try
		{
			kaufDatum = ADate.parse(s);
		}
		catch (Exception e) {}
	}

	try
	{
		hochKurs = Waehrungen.doubleToLong(hochkurs.getText());
	}
	catch (NumberFormatException e) {}

	try
	{
		tiefKurs = Waehrungen.doubleToLong(tiefkurs.getText());
	}
	catch (NumberFormatException e) {}

	try
	{
		gewinnGrenze = Waehrungen.doubleToLong(gewinngrenze.getText());
	}
	catch (NumberFormatException e) {}
	
	BenutzerAktie ba = new BenutzerAktie(name,wkn,bp,boerseNurDiese.getState(),kaufDatum,
											kaufKurs,anzAktien,hochKurs,tiefKurs,gewinnGrenze,
											waehrung.getSelectedIndex(),gewinnProz.getState(),
											watchonly);

	AktienMan.hauptdialog.listeNeueAktie(ba);
}



public synchronized boolean canOK() {
	String s;

	/* #Demoversion */
	if (!AktienMan.hauptdialog.main())
	{
		if (AktienMan.hauptdialog.getAnzahlAktien() > 2)
		{
			new TextWarnalert(this,"Die Demoversion kann maximal drei Aktien verwalten.");
			return false;
		}
	}
	
	if (wknCheckbox.getState())
	{
		s = aktienWKN.getText().trim();
		
		if (s.length() == 0)
		{
			new TextWarnalert(this,"Bitte geben Sie die WKN ein oder w\u00e4hlen Sie eine Aktie aus.");
			return false;
		}
		
		if (Character.isDigit(s.charAt(0)))
		{
			if (s.length() != 6)
			{
				new TextWarnalert(this,"WKN ung\u00fcltig. Die WKN mu\u00df aus exakt sechs Ziffern bestehen.");
				return false;			
			}
			
			for (int i=0; i<6; i++)
			{
				if (!Character.isDigit(s.charAt(i)))
				{
					new TextWarnalert(this,"WKN ung\u00fcltig. Die WKN mu\u00df aus exakt sechs Ziffern bestehen.");
					return false;			
				}
			}
		}
		else if (Character.isLetter(s.charAt(0)))
		{
			if (s.length() < 3)
			{
				new TextWarnalert(this,"K\u00fcrzel ung\u00fcltig. Ein K\u00fcrzel mu\u00df mindestens drei Zeichen lang sein.");
				return false;			
			}

			for (int i=0; i<s.length(); i++)
			{
				if (!Character.isLetterOrDigit(s.charAt(i)))
				{
					new TextWarnalert(this,"K\u00fcrzel ung\u00fcltig. Ein K\u00fcrzel darf nur aus Buchstaben und Ziffern bestehen.");
					return false;
				}
			}
		}
		else
		{
			new TextWarnalert(this,"Bitte geben sie einen g\u00fcltigen Wert im Feld \"WKN\" ein.");
			return false;
		}
	}

	s = kaufkurs.getText().trim();
	if ((!watchonly) || (s.length() > 0))
	{
		long db;
		try
		{
			db = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e)
		{
			new TextWarnalert(this,"Bitte geben Sie beim Kaufkurs eine g\u00fcltige Zahl ein.");
			return false;
		}
		
		if (db <= 0L)
		{
			new TextWarnalert(this,"Bitte geben Sie einen g\u00fcltigen Kaufkurs ein.");
			return false;
		}
	}
	
	s = stueckzahl.getText().trim();
	if ((!watchonly) || (s.length() > 0))
	{
		int i;
		try
		{
			i = Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			new TextWarnalert(this,"Bitte geben Sie bei der St\u00fcckzahl eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (i <= 0)
		{
			new TextWarnalert(this,"Bitte geben Sie eine g\u00fcltige St\u00fcckzahl ein.");
			return false;
		}
	}
	
	s = kaufdatum.getText().trim();
	if ((!watchonly) || (s.length() > 0))
	{
		ADate d;
		try
		{
			d = ADate.parse(s);
			
			if (d.after(new ADate()))
			{
				new TextWarnalert(this,"Ein Kaufdatum in der Zukunft ist nicht erlaubt.");
				return false;
			}
		}
		catch (Exception e)
		{
			new TextWarnalert(this,"Bitte geben Sie ein g\u00fcltiges Kaufdatum ein.");
			return false;
		}
	}

	s = gewinngrenze.getText().trim();
	if (s.length() > 0)
	{
		long db;
		try
		{
			db = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e)
		{
			new TextWarnalert(this,"Bitte geben Sie bei der Gewinngrenze eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0L)
		{
			new TextWarnalert(this,"Bitte geben Sie eine g\u00fcltige Gewinngrenze ein oder lassen Sie das Feld leer.");
			return false;
		}
	}

	long tk = 0L;
	s = tiefkurs.getText().trim();
	if (s.length() > 0)
	{
		try
		{
			tk = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e)
		{
			new TextWarnalert(this,"Bitte geben Sie beim Tiefkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (tk <= 0L)
		{
			new TextWarnalert(this,"Bitte geben Sie einen g\u00fcltigen Tiefkurs ein oder lassen Sie das Feld leer.");
			return false;
		}
	}

	s = hochkurs.getText().trim();
	if (s.length() > 0)
	{
		long db;
		try
		{
			db = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e)
		{
			new TextWarnalert(this,"Bitte geben Sie beim Hochkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0L)
		{
			new TextWarnalert(this,"Bitte geben Sie einen g\u00fcltigen Hochkurs ein oder lassen Sie das Feld leer.");
			return false;
		}
		
		if ((tk > 0L) && (db <= tk))
		{
			new TextWarnalert(this,"Der Hochkurs mu\u00df h\u00f6her als der Tiefkurs liegen.");
			return false;
		}
	}

	return true;
}



public void closed() {
	AktienMan.neueaktie = null;
}

}
