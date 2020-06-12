/**
 @author Thomas Much
 @version 2000-08-09
*/

import java.awt.*;
import java.awt.event.*;




public final class AktieMaximalkurs extends AFrame implements KursReceiver {

private long[] kurse,volumen;
private int[] kwaehrung;
private String[] kdatum;
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
	kdatum = new String[AktienMan.boersenliste.size()];
	
	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		kurse[i] = BenutzerAktie.VALUE_MISSING;
		volumen[i] = 0L;
		kwaehrung[i] = Waehrungen.NONE;
		kdatum[i] = "";
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
			String wkn   = ba.getWKNString();
			String platz = AktienMan.boersenliste.getAt(i).getKurz();

			quelle.sendSingleMaxkursRequest(this,ba.getRequestWKN()+platz,wkn,platz);
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

			constrain(panelKurse,new Label(kdatum[i]),3,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

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



private synchronized void setKurs(int index, long kurs, int nextID) {

	if (AktienMan.DEBUG)
	{
		System.out.println("Fehler beim Einlesen der Maximalkurse  -> "+nextID);
	}

	if ((nextID == KursQuellen.QUELLE_NONE) || (index < 0))
	{
		setKurs(index,kurs);
	}
	else
	{
		String wkn   = ba.getWKNString();
		String platz = AktienMan.boersenliste.getAt(index).getKurz();

		KursQuellen.getKursQuelle(nextID).sendSingleMaxkursRequest(this,ba.getRequestWKN()+platz,wkn,platz,false);
	}
}


private synchronized void setKurs(int index, long kurs) {

	setKurs(index,kurs,"",Waehrungen.NONE,0L);
}



private synchronized void setKurs(int index, long kurs, String datum, int waehrung, long hvolumen) {

	if (index >= 0)
	{
		kurse[index] = kurs;
		kwaehrung[index] = waehrung;
		volumen[index] = hvolumen;

		if (datum.length() > 0) kdatum[index] = "("+datum+")";
	
		fillKursPanel(true);
	}
}



private int getIndex(String wkn, String platz) {

	if (!wkn.equalsIgnoreCase(ba.getWKNString()))
	{
		return -1;
	}

	return AktienMan.boersenliste.getBoersenIndex(platz,-1);
}



public synchronized void listeNeuerAktienkurs(String wkn, String kurz, String platz,
												String name, long kurs, String kursdatum,
												long vortageskurs, long eroeffnungskurs,
												long hoechstkurs, long tiefstkurs,
												long handelsvolumen, int waehrung,
												boolean sofortZeichnen) {

	setKurs(getIndex(wkn,platz),kurs,kursdatum,waehrung,handelsvolumen);
}



public synchronized void listeAktienkursNA(String wkn, String kurz, String platz, String name,
											boolean sofortZeichnen) {

	setKurs(getIndex(wkn,platz),BenutzerAktie.VALUE_NA);
}



/*public synchronized void listeAnfrageFalsch(String wkn, String platz, boolean sofortZeichnen) {

	setKurs(getIndex(platz),BenutzerAktie.VALUE_ERROR);
}*/



public synchronized void listeAnfrageFehler(String request, String wkn, String platz,
												boolean sofortZeichnen, int nextID) {

	setKurs(getIndex(wkn,platz),BenutzerAktie.VALUE_ERROR,nextID);
}

}
