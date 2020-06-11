/**
 @author Thomas Much
 @version 1998-11-01
*/

import java.awt.*;
import java.awt.event.*;



public class AktieAendern extends AktienFrame {

private TextField neuername,aktienWKN,kaufdatum,kaufkurs;
private TextField stueckzahl,hochkurs,tiefkurs,gewinngrenze;
private Choice plaetze,waehrung;
private Checkbox boerseNurDiese,gewinnAbs,gewinnProz;
private CheckboxGroup gewinnGruppe;
private Button buttonChange;



public AktieAendern(int index, BenutzerAktie ba) {
	super(AktienMan.AMFENSTERTITEL+"Aktiendaten \u00e4ndern",index,ba);
}


public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelMitte = new Panel(gridbag);
	Panel panelRest = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	gewinnGruppe = new CheckboxGroup();

	constrain(panelOben,new Label("Alter Name:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label(ba.getName(false)),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	neuername = new TextField(ba.getName(false),55);
	constrain(panelOben,new Label("Neuer Name:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,neuername,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	
	if (AktienMan.properties.getBoolean("Konfig.Aktiennamen"))
	{
		Label l = new Label("Achtung: Der Name wird beim Aktualisieren \u00fcberschrieben!");
		l.setForeground(Color.red);
		constrain(panelOben,l,1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	}
	
	aktienWKN = new TextField(ba.getWKNString(),7);
	constrain(panelMitte,new Label("WKN:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelMitte,aktienWKN,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	
	plaetze = AktienMan.boersenliste.getChoice();
	plaetze.select(AktienMan.boersenliste.getBoersenIndex(ba.getBoerse()));
	constrain(panelMitte,new Label("B\u00f6rsenplatz:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelMitte,plaetze,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	boerseNurDiese = new Checkbox("Nur an dieser B\u00f6rse",ba.isBoerseFixed());
	constrain(panelMitte,boerseNurDiese,1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	
	constrain(panelMitte,new Label("W\u00e4hrung:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	waehrung = AktienMan.waehrungen.getChoice();
	waehrung.select(Waehrungen.getStandardWaehrung()); /**/
	constrain(panelMitte,waehrung,1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,2,0,0);

	constrain(panelRest,new Label("Kaufdatum"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("Kaufkurs"),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("St\u00fcckzahl"),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);
	
	constrain(panelRest,new Label("Gewinngrenze"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	constrain(panelRest,new Label("Stop Loss"),1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);

	kaufdatum = new TextField(ba.getKaufdatum().toString(),10);
	constrain(panelRest,kaufdatum,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	kaufkurs = new TextField(AktienMan.get00String(ba.getKaufkurs()),8);
	constrain(panelRest,kaufkurs,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	stueckzahl = new TextField(ba.getStueckzahlString(),8);
	constrain(panelRest,stueckzahl,1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel gewinnPanel = new Panel(gridbag);

	gewinnAbs = new Checkbox("",false,gewinnGruppe);
	gewinnProz = new Checkbox("",false,gewinnGruppe);

	hochkurs = new TextField(ba.getHochkursString(),8);
	hochkurs.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			gewinnGruppe.setSelectedCheckbox(gewinnAbs);
		}
	});
	constrain(gewinnPanel,gewinnAbs,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,hochkurs,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	gewinngrenze = new TextField(ba.getProzentString(),8);
	gewinngrenze.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			gewinnGruppe.setSelectedCheckbox(gewinnProz);
		}
	});
	constrain(gewinnPanel,gewinnProz,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,gewinngrenze,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(gewinnPanel,new Label("%"),2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelRest,gewinnPanel,0,5,1,2,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	Panel verlustPanel = new Panel(gridbag);

	tiefkurs = new TextField(ba.getTiefkursString(),8);
	constrain(verlustPanel,tiefkurs,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(verlustPanel,new Label(),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	constrain(panelRest,verlustPanel,1,5,1,2,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	Button buttonCancel = new Button(Lang.CANCEL);
	buttonChange = new Button(Lang.CHANGE);
	
	buttonChange.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});

	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});

	constrain(panelButtons,buttonCancel,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonChange,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);

	constrain(this,panelOben,0,0,2,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelMitte,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,5,10,0,10);
	constrain(this,panelRest,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,5,30,0,10);
	constrain(this,panelButtons,0,2,2,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,20,10,10,10);

	if (ba.doUseGrenze())
	{
		gewinnGruppe.setSelectedCheckbox(gewinnProz);
	}
	else
	{
		gewinnGruppe.setSelectedCheckbox(gewinnAbs);
	}
}


public synchronized void executeOK() {
	buttonChange.setEnabled(false);

	String name = neuername.getText().trim();
	String wkn = aktienWKN.getText();

	Boersenplatz bp = AktienMan.boersenliste.getAt(plaetze.getSelectedIndex());
	boolean nurdiese = boerseNurDiese.getState();

	ADate kdate = null;
	try
	{
		kdate = ADate.parse(kaufdatum.getText());
	}
	catch (Exception e) {}

	long kkurs = 0L;
	try
	{
		kkurs = Waehrungen.doubleToLong(kaufkurs.getText());
	}
	catch (NumberFormatException e) {}

	long anzaktien = 0L;
	try
	{
		anzaktien = Long.parseLong(stueckzahl.getText());
	}
	catch (NumberFormatException e) {}

	long khoch = 0L;
	try
	{
		khoch = Waehrungen.doubleToLong(hochkurs.getText());
	}
	catch (NumberFormatException e) {}

	long ktief = 0L;
	try
	{
		ktief = Waehrungen.doubleToLong(tiefkurs.getText());
	}
	catch (NumberFormatException e) {}

	long ggrenze = 0L;
	try
	{
		ggrenze = Waehrungen.doubleToLong(gewinngrenze.getText());
	}
	catch (NumberFormatException e) {}

	boolean usegrenze = gewinnProz.getState();

	ba.changeValues(name,wkn,bp,nurdiese,kdate,kkurs,anzaktien,khoch,ktief,ggrenze,usegrenze);
	AktienMan.hauptdialog.listeUpdate(true);
}


public synchronized boolean canOK() {
	if (neuername.getText().trim().length() == 0)
	{
		new Warnalert(this,"Bitte geben Sie einen Aktiennamen ein.");
		return false;
	}

	String s = aktienWKN.getText();
	
	if (s.length() == 0)
	{
		new Warnalert(this,"Bitte geben Sie die WKN ein!");
		return false;
	}
	
	if (Character.isDigit(s.charAt(0)))
	{
		if (s.length() != 6)
		{
			new Warnalert(this,"WKN ung\u00fcltig. Die WKN mu\u00df aus exakt sechs Ziffern bestehen.");
			return false;			
		}
		
		for (int i=0; i<6; i++)
		{
			if (!Character.isDigit(s.charAt(i)))
			{
				new Warnalert(this,"WKN ung\u00fcltig. Die WKN mu\u00df aus exakt sechs Ziffern bestehen.");
				return false;			
			}
		}
	}
	else if (Character.isLetter(s.charAt(0)))
	{
		if (s.length() < 3)
		{
			new Warnalert(this,"K\u00fcrzel ung\u00fcltig. Ein K\u00fcrzel mu\u00df mindestens drei Zeichen lang sein.");
			return false;			
		}

		for (int i=0; i<s.length(); i++)
		{
			if (!Character.isLetterOrDigit(s.charAt(i)))
			{
				new Warnalert(this,"K\u00fcrzel ung\u00fcltig. Ein K\u00fcrzel darf nur aus Buchstaben und Ziffern bestehen.");
				return false;
			}
		}
	}
	else
	{
		new Warnalert(this,"Bitte geben sie einen g\u00fcltigen Wert im Feld \"WKN\" ein.");
		return false;
	}
	
	double db;
	try
	{
		db = AktienMan.getDouble(kaufkurs.getText());
	}
	catch (NumberFormatException e)
	{
		new Warnalert(this,"Bitte geben Sie beim Kaufkurs eine g\u00fcltige Zahl ein.");
		return false;
	}
	
	if (db <= 0.0)
	{
		new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Kaufkurs ein.");
		return false;
	}
	
	int i;
	try
	{
		i = Integer.parseInt(stueckzahl.getText());
	}
	catch (NumberFormatException e)
	{
		new Warnalert(this,"Bitte geben Sie bei der St\u00fcckzahl eine g\u00fcltige Zahl ein.");
		return false;
	}

	if (i <= 0)
	{
		new Warnalert(this,"Bitte geben Sie eine g\u00fcltige St\u00fcckzahl ein.");
		return false;
	}
	
	ADate d;
	try
	{
		d = ADate.parse(kaufdatum.getText());
		
		if (d.after(new ADate()))
		{
			new Warnalert(this,"Ein Kaufdatum in der Zukunft ist nicht erlaubt.");
			return false;
		}
	}
	catch (Exception e)
	{
		new Warnalert(this,"Bitte geben Sie ein g\u00fcltiges Kaufdatum ein.");
		return false;
	}

	s = gewinngrenze.getText();
	
	if (s.length() > 0)
	{
		try
		{
			db = AktienMan.getDouble(s);
		}
		catch (NumberFormatException e)
		{
			new Warnalert(this,"Bitte geben Sie bei der Gewinngrenze eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0.0)
		{
			new Warnalert(this,"Bitte geben Sie eine g\u00fcltige Gewinngrenze ein oder lassen Sie das Feld leer.");
			return false;
		}
	}

	double tk = 0.0;
	s = tiefkurs.getText();
	
	if (s.length() > 0)
	{
		try
		{
			tk = AktienMan.getDouble(s);
		}
		catch (NumberFormatException e)
		{
			new Warnalert(this,"Bitte geben Sie beim Tiefkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (tk <= 0.0)
		{
			new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Tiefkurs ein oder lassen Sie das Feld leer.");
			return false;
		}
	}
	
	s = hochkurs.getText();
	
	if (s.length() > 0)
	{
		try
		{
			db = AktienMan.getDouble(s);
		}
		catch (NumberFormatException e)
		{
			new Warnalert(this,"Bitte geben Sie beim Hochkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0.0)
		{
			new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Hochkurs ein oder lassen Sie das Feld leer.");
			return false;
		}
		
		if ((tk > 0.0) && (db <= tk))
		{
			new Warnalert(this,"Der Hochkurs mu\u00df h\u00f6her als der Tiefkurs liegen.");
			return false;
		}
	}

	return true;
}


public void closed() {
	AktienMan.aktieaendern = null;
}

}
