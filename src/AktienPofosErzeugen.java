/**
 @author Thomas Much
 @version 2003-04-01
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;




public final class AktienPofosErzeugen extends AFrame {

private Button buttonJa;
private Label label;




public AktienPofosErzeugen() {

	super(AktienMan.AMFENSTERTITEL+"Portfolios erzeugen");
}



public void setupElements() {

	setLayout(gridbag);

	label = new Label("Sollen die Standard-Portfolios jetzt erzeugt werden?");
	label.setForeground(Color.red);
	constrain(this,label,0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,10,10);

	constrain(this,new Label(""),0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,10,0);

	Button buttonNein = new Button(Lang.NO);
	buttonNein.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(this,buttonNein,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,0);

	buttonJa = new Button(Lang.YES);
	buttonJa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(this,buttonJa,2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,10);
}



public synchronized void executeOK() {

	buttonJa.setEnabled(false);
	
	createPortfolio("DAX30",AktienMan.listeDAX30);
	createPortfolio("MDAX",AktienMan.listeMDAX);
	createPortfolio("TecDAX",AktienMan.listeTecDAX);
	createPortfolio("EuroSTOXX50",AktienMan.listeEuroSTOXX50);
	createPortfolio("STOXX50",AktienMan.listeSTOXX50);
	
	System.out.println("Fertig.");
}



private void createPortfolio(String name, Aktienliste liste) {

	String datei = FileUtil.getWorkingDirectory() + name + FileUtil.EXT_PORTFOLIO;

	System.out.println("Erzeuge Portfolio \"" + datei + "\" ...");

	BenutzerListe poplist = new BenutzerListe();
	poplist.setPortfolioFile(datei);

	Boersenplatz bp = AktienMan.boersenliste.getAt(AktienMan.boersenliste.getStandardBoerse());
	
	long gewinnGrenze = 0L;
	try
	{
		gewinnGrenze = Waehrungen.doubleToLong(AktienMan.properties.getString("Konfig.StdGewinn"));
	}
	catch (NumberFormatException e) {}
	
	int kaufwaehrung = Waehrungen.getStandardKaufwaehrung();

	for (int i=0; i < liste.size(); i++)
	{
		Aktie a = liste.getAktie(i);

		BenutzerAktie ba = new BenutzerAktie(a.getName(),a.getWKNString(),bp,false,
		                                     new ADate(),0L,1L,0L,0L,gewinnGrenze,
		                                     kaufwaehrung,true,true);

		poplist.add(ba);
	}

	System.out.println("Speichern ...");
	BenutzerListe.store(poplist);
}

}
