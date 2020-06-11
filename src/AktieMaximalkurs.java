/**
 @author Thomas Much
 @version 1999-06-13
*/

import java.awt.*;
import java.awt.event.*;



public final class AktieMaximalkurs extends AFrame {

private long[] kurse,volumen;
private int[] kwaehrung;
private String[] kursdatum;
private Panel panelKurse;
private BenutzerAktie ba;



public AktieMaximalkurs(BenutzerAktie ba) {
	super(AktienMan.AMFENSTERTITEL+"Maximalkurs");
	
	this.ba = ba;

	setupElements2();
	
	pack();
	setupSize();
	show();
	
	startThreads();

	AktienMan.hauptdialog.windowToFront(this);
}


public void setupElements() {
	setLayout(gridbag);
}


public void display() {}


public synchronized void setupElements2() {
	panelKurse = new Panel(gridbag);

	kurse = new long[AktienMan.boersenliste.size()];
	volumen = new long[AktienMan.boersenliste.size()];
	kwaehrung = new int[AktienMan.boersenliste.size()];
	kursdatum = new String[AktienMan.boersenliste.size()];
	
	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		kurse[i] = BenutzerAktie.VALUE_MISSING;
		volumen[i] = 0L;
		kwaehrung[i] = Waehrungen.NONE;
		kursdatum[i] = "";
	}
	
	fillKursPanel(false);
	
	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(this,new Label("Aktie \""+ba.getName(BenutzerListe.useShortNames())+"\":"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,3,10);
	constrain(this,panelKurse,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,10,0,10);
	constrain(this,buttonOK,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


private void startThreads() {
	KursQuelle quelle = KursQuellen.getKursQuelle();

	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		if (!AktienMan.boersenliste.getAt(i).isFondsOnly())
		{
			quelle.sendSingleMaxkursRequest(this,i,ba.getWKNString()+"."+AktienMan.boersenliste.getAt(i).getKurz());
		}
	}
}


private synchronized void fillKursPanel(boolean draw) {
	if (draw) panelKurse.removeAll();

	long maxkurs = 1L, minkurs = Long.MAX_VALUE;
	
	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		if (!AktienMan.boersenliste.getAt(i).isFondsOnly())
		{
			if (kurse[i] > maxkurs) maxkurs = kurse[i];
			if ((kurse[i] > 0) && (kurse[i] < minkurs)) minkurs = kurse[i];
		}
	}
	
	constrain(panelKurse,new Label("akt. Kurs"),2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,2,0);
	constrain(panelKurse,new Label("Volumen",Label.RIGHT),4,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,2,0);

	int ypos = 1;

	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		Boersenplatz b = AktienMan.boersenliste.getAt(i);
		
		if (!b.isFondsOnly())
		{
			constrain(panelKurse,new Label(b.getName()),0,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
			constrain(panelKurse,new Label("("+b.getKurz()+"):"),1,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
			
			String s,svol = "";
			long k = kurse[i];
			
			if (k == BenutzerAktie.VALUE_MISSING)
			{
				s = "<Anfrage l\u00e4uft>";
			}
			else if (k == BenutzerAktie.VALUE_NA)
			{
				s = "n/a";
			}
			else if (k < 0L)
			{
				s = "<offline>";
			}
			else
			{
				s = Waehrungen.getString(Waehrungen.exchange(k,kwaehrung[i],Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung());
				svol = "" + volumen[i];
			}
			
			Label l = new Label(s);
			if (k == maxkurs)
			{
				l.setForeground(Color.green.darker());
			}
			else if (k == minkurs)
			{
				l.setForeground(Color.red);
			}
			constrain(panelKurse,l,2,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);

			constrain(panelKurse,new Label(kursdatum[i]),3,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

			constrain(panelKurse,new Label(svol,Label.RIGHT),4,ypos,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,0,0);
			
			ypos++;
		}
	}
	
	if (draw)
	{
		pack();
		setSize(getSize());
		panelKurse.paintAll(getGraphics());
	}
}


public synchronized void setKurs(int index, long kurs, int nextID) {
	if (AktienMan.DEBUG)
	{
		System.out.println("Fehler beim Einlesen der Maximalkurse  -> "+nextID);
	}

	if (nextID == KursQuellen.QUELLE_NONE)
	{
		setKurs(index,kurs);
	}
	else
	{
		KursQuellen.getKursQuelle(nextID).sendSingleMaxkursRequest(this,index,ba.getWKNString()+"."+AktienMan.boersenliste.getAt(index).getKurz(),false);
	}
}


public synchronized void setKurs(int index, long kurs) {
	setKurs(index,kurs,"",Waehrungen.NONE,0L);
}


public synchronized void setKurs(int index, long kurs, String datum, int waehrung, long hvolumen) {
	kurse[index] = kurs;
	kwaehrung[index] = waehrung;
	volumen[index] = hvolumen;

	if (datum.length() > 0) kursdatum[index] = "("+datum+")";
	
	fillKursPanel(true);
}

}
