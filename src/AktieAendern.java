/**
 @author Thomas Much
 @version 1999-12-12
*/

import java.awt.*;
import java.awt.event.*;




public final class AktieAendern extends AktienFrame {

private TextField neuername,aktienWKN,kaufdatum,kaufkurs;
private TextField stueckzahl,hochkurs,tiefkurs,gewinngrenze;
private TextField aktkurs,aktdatum;
private Choice plaetze,waehrung;
private Checkbox boerseNurDiese,gewinnAbs,gewinnProz,watchOnly,dontUpdate;
private CheckboxGroup gewinnGruppe;
private Button buttonChange,buttonDelete;




public AktieAendern(int index, BenutzerAktie ba) {

	super(AktienMan.AMFENSTERTITEL+"Aktiendaten \u00e4ndern",index,ba);
	kaufkurs.requestFocus();
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
	
	if (BenutzerListe.useOnlineNames())
	{
		Label l = new Label("Achtung: Der Name wird beim Aktualisieren \u00fcberschrieben!");
		l.setForeground(Color.red);
		constrain(panelOben,l,1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	}
	
	aktienWKN = new TextField(ba.getWKNString(),7);
	constrain(panelMitte,new Label("WKN:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelMitte,aktienWKN,1,0,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	
	plaetze = AktienMan.boersenliste.getChoice(true);
	plaetze.select(AktienMan.boersenliste.getBoersenIndex(ba.getBoerse()));
	plaetze.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkNurDiese();
		}
	});
	constrain(panelMitte,new Label("B\u00f6rsenplatz:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelMitte,plaetze,1,1,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);

	boerseNurDiese = new Checkbox("Nur an dieser B\u00f6rse",ba.isBoerseFixed());
	constrain(panelMitte,boerseNurDiese,1,2,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,2,0,0);
	
	constrain(panelMitte,new Label("Kaufw\u00e4hrung:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	waehrung = AktienMan.waehrungen.getChoice(true);
	waehrung.select(ba.getKaufwaehrung());
	constrain(panelMitte,waehrung,1,3,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,2,0,0);

	watchOnly = new Checkbox("nur beobachten",ba.nurBeobachten());
	constrain(panelMitte,watchOnly,1,4,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,20,2,0,0);
	
	dontUpdate = new Checkbox("nicht aktualisieren",ba.doNotUpdate());
	dontUpdate.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			checkAktKurs();
		}
	});
	constrain(panelMitte,dontUpdate,1,5,2,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,20,2,0,0);
	
	constrain(panelMitte,new Label("akt. Kurs"),1,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);
	constrain(panelMitte,new Label("Datum"),2,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,5,0,0);
	
	String astr = "";
	if (ba.getKurs() > BenutzerAktie.VALUE_MISSING)
	{
		astr = NumUtil.get00String(Waehrungen.exchange(ba.getKurs(),ba.getKurswaehrung(),ba.getKaufwaehrung()));
	}
	aktkurs = new TextField(astr,8);
	constrain(panelMitte,aktkurs,1,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	aktdatum = new TextField(ba.getFixedDateString(),10);
	constrain(panelMitte,aktdatum,2,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelRest,new Label("Kaufdatum"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("Kaufkurs"),1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelRest,new Label("St\u00fcckzahl"),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,5,0,0,0);
	
	constrain(panelRest,new Label("Gewinngrenze"),0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	constrain(panelRest,new Label("Stop Loss"),1,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);

	kaufdatum = new TextField(ba.getKaufdatum().toString(),10);
	constrain(panelRest,kaufdatum,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	String kstr = "";
	if (ba.getKaufkurs() > BenutzerAktie.VALUE_MISSING)
	{
		kstr = NumUtil.get00String(ba.getKaufkurs());
	}
	kaufkurs = new TextField(kstr,8);
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

	constrain(panelRest,new Label("Angaben in Kaufw\u00e4hrung!",Label.CENTER),0,7,2,1,GridBagConstraints.NONE,GridBagConstraints.NORTH,0.0,0.0,2,0,0,0);

	Button buttonCancel = new Button(Lang.CANCEL);
	buttonChange = new Button(Lang.CHANGE);
	buttonDelete = new Button(Lang.DELETE);
	
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

	buttonDelete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doDelete();
		}
	});

	constrain(panelButtons,buttonCancel,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonDelete,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonChange,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

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
	
	checkAktKurs();
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



public void checkAktKurs() {

	if (dontUpdate.getState())
	{
		aktkurs.setEnabled(true);
		aktdatum.setEnabled(true);
	}
	else
	{
		aktkurs.setEnabled(false);
		aktdatum.setEnabled(false);
	}
}



public synchronized void executeOK() {

	String s;
	
	buttonDelete.setEnabled(false);
	buttonChange.setEnabled(false);

	String name = neuername.getText().trim();
	String wkn = aktienWKN.getText().trim();

	Boersenplatz bp = AktienMan.boersenliste.getAt(plaetze.getSelectedIndex());

	boolean nurdiese = boerseNurDiese.getState();
	boolean watchonly = watchOnly.getState();
	boolean dontupdate = dontUpdate.getState();
	
	ADate kdate = new ADate();
	s = kaufdatum.getText().trim();
	if ((s.length() > 0) || (!watchonly))
	{
		try
		{
			kdate = ADate.parse(s);
		}
		catch (Exception e) {}
	}

	long kkurs = BenutzerAktie.VALUE_MISSING;
	s = kaufkurs.getText().trim();
	if ((s.length() > 0) || (!watchonly))
	{
		try
		{
			kkurs = Waehrungen.doubleToLong(s);
		}
		catch (NumberFormatException e) {}
	}

	long anzaktien = 1L;
	s = stueckzahl.getText().trim();
	if ((s.length() > 0) || (!watchonly))
	{
		try
		{
			anzaktien = Long.parseLong(s);
		}
		catch (NumberFormatException e) {}
	}

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
	
	long aktKurs = BenutzerAktie.VALUE_MISSING;
	ADate aktDate = null;

	if (dontupdate)
	{
		s = aktkurs.getText().trim();
		if (s.length() > 0)
		{
			try
			{
				aktKurs = Waehrungen.doubleToLong(s);
			}
			catch (NumberFormatException e) {}
		}
		
		String aktdate = aktdatum.getText().trim();

		try
		{
			aktDate = ADate.parse(aktdate);
		}
		catch (Exception e) {}
	}

	ba.changeValues(name,wkn,bp,nurdiese,kdate,kkurs,anzaktien,khoch,ktief,ggrenze,
					waehrung.getSelectedIndex(),usegrenze,watchonly,dontupdate,aktKurs,aktDate);

	AktienMan.hauptdialog.listeUpdate(true,true,true,false);
}



public synchronized boolean canOK() {

	boolean watchonly = watchOnly.getState();

	if (neuername.getText().trim().length() == 0)
	{
		new Warnalert(this,"Bitte geben Sie einen Aktiennamen ein.");
		return false;
	}

	String s = aktienWKN.getText().trim();
	
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
			new Warnalert(this,"Bitte geben Sie beim Kaufkurs eine g\u00fcltige Zahl ein.");
			return false;
		}
		
		if (db <= 0L)
		{
			new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Kaufkurs ein.");
			return false;
		}
	}
	
	if (dontUpdate.getState())
	{
		s = aktkurs.getText().trim();

		if (s.length() > 0)
		{
			long db;
			try
			{
				db = Waehrungen.doubleToLong(s);
			}
			catch (NumberFormatException e)
			{
				new Warnalert(this,"Bitte geben Sie beim aktuellen Kurs eine g\u00fcltige Zahl ein.");
				return false;
			}
			
			if (db <= 0L)
			{
				new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen aktuellen Kurs ein.");
				return false;
			}
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
			new Warnalert(this,"Bitte geben Sie bei der St\u00fcckzahl eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (i <= 0)
		{
			new Warnalert(this,"Bitte geben Sie eine g\u00fcltige St\u00fcckzahl ein.");
			return false;
		}
	}
	
	ADate kaufd = null;
	s = kaufdatum.getText().trim();
	if ((!watchonly) || (s.length() > 0))
	{
		try
		{
			kaufd = ADate.parse(s);
			
			if (kaufd.after(new ADate()))
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
	}
	
	if (dontUpdate.getState())
	{
		s = aktdatum.getText().trim();
		
		if (s.length() > 0)
		{
			try
			{
				ADate aktd = ADate.parse(s);
				
				if (kaufd.after(aktd))
				{
					new Warnalert(this,"Das Kursdatum muss nach dem Kaufdatum liegen.");
					return false;
				}
			}
			catch (Exception e)
			{
				new Warnalert(this,"Bitte geben Sie ein g\u00fcltiges Kursdatum ein.");
				return false;
			}
		}
		else
		{
			new Warnalert(this,"Bitte geben Sie ein g\u00fcltiges Kursdatum ein.");
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
			new Warnalert(this,"Bitte geben Sie bei der Gewinngrenze eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0L)
		{
			new Warnalert(this,"Bitte geben Sie eine g\u00fcltige Gewinngrenze ein oder lassen Sie das Feld leer.");
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
			new Warnalert(this,"Bitte geben Sie beim Tiefkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (tk <= 0L)
		{
			new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Tiefkurs ein oder lassen Sie das Feld leer.");
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
			new Warnalert(this,"Bitte geben Sie beim Hochkurs eine g\u00fcltige Zahl ein.");
			return false;
		}

		if (db <= 0L)
		{
			new Warnalert(this,"Bitte geben Sie einen g\u00fcltigen Hochkurs ein oder lassen Sie das Feld leer.");
			return false;
		}
		
		if ((tk > 0L) && (db <= tk))
		{
			new Warnalert(this,"Der Hochkurs mu\u00df h\u00f6her als der Tiefkurs liegen.");
			return false;
		}
	}

	return true;
}



private void doDelete() {

	buttonDelete.setEnabled(false);
	buttonChange.setEnabled(false);
	
	AktienMan.hauptdialog.listeAktieLoeschen(index);

	dispose();
}



public void closed() {

	AktienMan.aktieaendern = null;
}

}
