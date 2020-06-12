/**
 @author Thomas Much
 @version 2003-01-28

 2003-01-28
 	es werden keine speziellen Maxkurs-Routinen der Quellen mehr verwendet, sondern die normalen (gecachten!) Routinen
 	Aktualisieren-Button
 	statt "offline" wird "nicht verfügbar" angezeigt
*/

import java.awt.*;
import java.awt.event.*;




public final class AktieMaximalkurs extends AFrame implements KursReceiver {

private long[] kurse,volumen;
private int[] kwaehrung;
private String[] kdatum;
private Panel panelKurse;
private BenutzerAktie ba;
private Button buttonAktualisieren;




public AktieMaximalkurs(BenutzerAktie ba) {

	super(AktienMan.AMFENSTERTITEL+"Maximalkurs");
	
	this.ba = ba;

	setupElements2();
	
	pack();
	setupSize();
	setVisible(true);
	
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
	
	initWerte(false);
	
	buttonAktualisieren = new Button("Aktualisieren");
	buttonAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			werteAktualisieren();
		}
	});

	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	Panel panelButtons = new Panel();
	
	panelButtons.add(buttonAktualisieren);
	panelButtons.add(buttonOK);
	
	constrain(this,new Label("Aktie \""+ba.getName(BenutzerListe.useShortNames())+"\":"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,3,10);
	constrain(this,panelKurse,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,10,0,10);
	constrain(this,panelButtons,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);

	buttonAktualisieren.setEnabled(false);
}



private void initWerte(boolean draw) {
	
	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		kurse[i] = BenutzerAktie.VALUE_MISSING;
		volumen[i] = 0L;
		kwaehrung[i] = Waehrungen.NONE;
		kdatum[i] = "";
	}
	
	fillKursPanel(draw);
}



private void werteAktualisieren() {

	buttonAktualisieren.setEnabled(false);

	initWerte(true);

	startThreads();
}



private synchronized void startThreads() {

	KursQuelle quelle = KursQuellen.getKursQuelle();

	for (int i = 0; i < AktienMan.boersenliste.size(); i++)
	{
		if (!AktienMan.boersenliste.getAt(i).isFondsOnly())
		{
			String wkn   = ba.getWKNString();
			String platz = AktienMan.boersenliste.getAt(i).getKurz();

			quelle.sendRequest(this,ba.getRequestWKN()+platz,wkn,platz,true);
		}
	}
	
	quelle.flush();

	buttonAktualisieren.setEnabled(true);
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
	constrain(panelKurse,new Label("geh. St\u00fcck",Label.RIGHT),4,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,2,0);

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

			int gridlen = 1;
			int hAlign = GridBagConstraints.EAST;
			
			if (k == BenutzerAktie.VALUE_MISSING)
			{
				s = "<Anfrage l\u00e4uft>";
				gridlen = 2;
				hAlign = GridBagConstraints.WEST;
			}
			else if (k == BenutzerAktie.VALUE_NA)
			{
				s = "n/a";
			}
			else if (k < 0L)
			{
				s = "<nicht verf\u00fcgbar>";
				gridlen = 3;
				hAlign = GridBagConstraints.CENTER;
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
			constrain(panelKurse,l,2,ypos,gridlen,1,GridBagConstraints.NONE,hAlign,0.0,0.0,0,10,0,0);

			if (gridlen == 1)
			{
				constrain(panelKurse,new Label(kdatum[i]),3,ypos,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
				constrain(panelKurse,new Label(svol,Label.RIGHT),4,ypos,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,0,0);
			}
			
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



private synchronized void setKurs(int index, long kurs, KursQuelle first, KursQuelle current) {
	
	long nextID = KursQuellen.getNextID(first,current);

	if (AktienMan.DEBUG)
	{
		System.out.println("Fehler beim Einlesen der Maximalkurse  -> "+nextID);
	}

	if ((nextID == KursQuellen.ID_NONE) || (index < 0))
	{
		setKurs(index,kurs);
	}
	else
	{
		String wkn   = ba.getWKNString();
		String platz = AktienMan.boersenliste.getAt(index).getKurz();

		KursQuellen.getKursQuelle(nextID).sendSingleRequest(this,ba.getRequestWKN()+platz,wkn,platz,true,first);
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



public synchronized void listeNeuerAktienkurs(String wkn, String isin, String kurz, String platz,
												String name, long kurs, String kursdatum,
												long vortageskurs, long eroeffnungskurs,
												long hoechstkurs, long tiefstkurs,
												long handelsvolumen, int waehrung,
												boolean sofortZeichnen) {

	/* TODO: Währung beachten (?) */
	/* TODO: ISIN beachten */
	setKurs(getIndex(wkn,platz),kurs,kursdatum,waehrung,handelsvolumen);
}



public synchronized void listeAktienkursNA(String wkn, String kurz, String platz, String name,
											boolean sofortZeichnen) {

	setKurs(getIndex(wkn,platz),BenutzerAktie.VALUE_NA);
}



public synchronized void listeAnfrageFehler(String request, String wkn, String platz,
												boolean sofortZeichnen, KursQuelle first, KursQuelle current) {

	setKurs(getIndex(wkn,platz),BenutzerAktie.VALUE_ERROR,first,current);
}

}
