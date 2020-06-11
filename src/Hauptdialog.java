/**
 @author Thomas Much
 @version 1998-11-03
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;



public class Hauptdialog extends AFrame implements ComponentListener {

public static final int MINBREITE = 620;
public static final int MINHOEHE  = 300;

public Window backWindow;

private int locked = 0;
private boolean abenabled = false;

private Button buttonVerkaufen,buttonLoeschen,buttonSplitten,buttonAendern;
private Button buttonDrucken,buttonSpeichern,buttonMaxkurs;
private Button buttonAktFSE,buttonAktXetra,buttonAktualisieren;
private BenutzerListe benutzerliste;
private ScrollPane pane;
private Panel panelText,panelGewinn;
private Listenbereich panelListe;
private PopupMenu aktienpopup;
private MenuItem menuVerkaufen,menuSplitten,menuMaxkurs,fileSave,filePrint;
private Menu menuChart,menuIntraday;
private Choice chErloes,buttonChart;
private int popX,popY;
private BALabel popParent;



public Hauptdialog() {
	super(AktienMan.AMNAME+" "+AktienMan.AMVERSION);
}


public synchronized void Lock () {
	locked++;

	checkLockButtons();
	checkListButtons();
}


public synchronized void Unlock () {
	locked--;

	checkLockButtons();
	checkListButtons();
}


public synchronized boolean isLocked(boolean beep) {
	if (locked > 0)
	{
		if (beep) getToolkit().beep();
		return true;
	}
	else
	{
		return false;
	}
}


private void resolutionCheck() {
	AktienMan.screenSize = getToolkit().getScreenSize();
	/* mind. 640x480 */
}


private synchronized void saveBenutzerAktien() {
	String folder = System.getProperty("user.home");
	String filesep = System.getProperty("file.separator");
	
	benutzerliste.prepare2Save();

	try
	{
		FileOutputStream fos = new FileOutputStream(folder+filesep+AktienMan.getFilenameList());
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(benutzerliste);
		out.flush();
		out.close();
	}
	catch (IOException e)
	{
		System.out.println("Fehler beim Speichern der Aktienliste.");
	}
}


private synchronized void loadBenutzerAktien() {
	String folder = System.getProperty("user.home");
	String filesep = System.getProperty("file.separator");

	benutzerliste = new BenutzerListe();
	
	/* nur laden, wenn portfoliover aktuell oder AM registriert ist */

	try
	{
		FileInputStream fis = new FileInputStream(folder+filesep+AktienMan.getFilenameList());
		GZIPInputStream gzis = new GZIPInputStream(fis);
		ObjectInputStream in = new ObjectInputStream(fis);
		benutzerliste = (BenutzerListe)in.readObject();
		in.close();
	}
	catch (IOException e) {}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Aktienliste fehlerhaft.");
	}
}


public void display() {
	loadBenutzerAktien();
	addErloes(false,0L);
	pack();
	setupSize();
	listeUpdate(false);
	checkListButtons();
}


public void setupFrame() {
	resolutionCheck();
	addComponentListener(this);
	setResizable(true);
}


public void setupElements() {
	setLayout(gridbag);
	
	Panel panelOben = new Panel(gridbag);
	Panel panelULinks = new Panel(gridbag);
	Panel panelUMitte = new Panel(gridbag);

	panelListe = new Listenbereich(gridbag);
	panelText = new Panel(gridbag);
	panelGewinn = new Panel(gridbag);
	
	Button buttonNeu = new Button(" Neue Aktie... ");
	Button buttonKamera = new Button(" DAX-Kamera ");

	buttonAktFSE = new Button(" Akt. FSE ");
	buttonAktXetra = new Button(" Akt. Xetra ");
	buttonAktualisieren = new Button(" Aktualisieren! ");

	buttonNeu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callNeueAktie();
		}
	});

	buttonAktFSE.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren("FSE");
		}
	});

	buttonAktXetra.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren("ETR");
		}
	});

	buttonAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren();
		}
	});
	
	buttonKamera.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKamera();
		}
	});
	
	constrain(panelOben,buttonNeu,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,15);
	
	constrain(panelOben,buttonAktFSE,1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,0,0,10);
	constrain(panelOben,buttonAktXetra,2,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1.0,0.0,0,0,0,10);
	constrain(panelOben,buttonAktualisieren,3,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHEAST,1.0,0.0,0,0,0,0);

	constrain(panelOben,buttonKamera,4,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,15,0,0);

	buttonDrucken = new Button(" Liste drucken... ");
	buttonSpeichern = new Button(" Liste speichern... ");

	buttonDrucken.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAusdrucken();
		}
	});

	buttonSpeichern.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichern();
		}
	});
	
	constrain(panelULinks,buttonDrucken,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,6,0);
	constrain(panelULinks,buttonSpeichern,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHWEST,0.0,0.0,0,0,0,0);
	
	buttonVerkaufen = new Button(" Aktie verkaufen... ");
	buttonLoeschen = new Button(" Aktie l\u00f6schen... ");
	buttonSplitten = new Button(" Aktie splitten... ");
	buttonAendern = new Button(" Daten \u00e4ndern... ");

	buttonVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieVerkaufen();
		}
	});

	buttonLoeschen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieLoeschen();
		}
	});

	buttonAendern.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAendern();
		}
	});

	buttonSplitten.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			/**/
		}
	});
	
	buttonChart = new Choice();
	buttonChart.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			int idx = buttonChart.getSelectedIndex();
			
			switch(idx)
			{
			case 1:
				listeSelektierteAktieChart("6");
				break;

			case 2:
				listeSelektierteAktieChart("12");
				break;

			case 3:
				listeSelektierteAktieChart("24");
				break;

			case 4:
				listeSelektierteAktieIntradayChart("FRA");
				break;

			case 5:
				listeSelektierteAktieIntradayChart("ETR");
				break;
			}

			if (idx != 0) buttonChart.select(0);
		}
	});

	buttonMaxkurs = new Button(" Maximalkurs ");
	buttonMaxkurs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieMaxkurs();
		}
	});

	setChartChoice(true);
	disableAktienButtons();
	
	constrain(panelUMitte,buttonVerkaufen,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,6,0);
	constrain(panelUMitte,buttonLoeschen,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,6,0);
	constrain(panelUMitte,buttonChart,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,6,0);
	constrain(panelUMitte,buttonSplitten,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	constrain(panelUMitte,buttonAendern,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
	constrain(panelUMitte,buttonMaxkurs,2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
	
	BenutzerAktie.addSummen(panelText,"-----","-----",false);
	
	pane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
	pane.add(panelListe);
	
	constrain(this,panelOben,0,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,5,10);
	constrain(this,pane,0,1,3,1,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0,5,10,5,10);

	constrain(this,panelText,0,2,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,10,0,10);
	constrain(this,panelGewinn,0,3,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,5,10);

	constrain(this,panelULinks,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHWEST,0.0,0.0,5,10,15,5);
	constrain(this,panelUMitte,1,4,2,1,GridBagConstraints.NONE,GridBagConstraints.SOUTH,0.0,0.0,5,5,15,10);

	aktienpopup = new PopupMenu();

	MenuItem mi = new MenuItem("Info...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieInfo();
		}
	});
	mi.setEnabled(false); /**/
	aktienpopup.add(mi);

	menuChart = new Menu("Chart");
	aktienpopup.add(menuChart);

	mi = new MenuItem("6 Monate");	
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieChart("6");
		}
	});
	menuChart.add(mi);

	mi = new MenuItem("12 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieChart("12");
		}
	});
	menuChart.add(mi);

	mi = new MenuItem("24 Monate");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieChart("24");
		}
	});
	menuChart.add(mi);
	
	menuChart.addSeparator();

	menuIntraday = new Menu("Intraday");
	menuChart.add(menuIntraday);

	mi = new MenuItem("Frankfurt");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieIntradayChart("FRA");
		}
	});
	menuIntraday.add(mi);

	mi = new MenuItem("Xetra");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieIntradayChart("ETR");
		}
	});
	menuIntraday.add(mi);

	menuMaxkurs = new MenuItem("Maximalkurs");
	menuMaxkurs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieMaxkurs();
		}
	});
	aktienpopup.add(menuMaxkurs);
	
	aktienpopup.addSeparator();

	menuVerkaufen = new MenuItem("Verkaufen...");	
	menuVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieVerkaufen();
		}
	});
	aktienpopup.add(menuVerkaufen);

	menuSplitten = new MenuItem("Splitten...");
	menuSplitten.setEnabled(false); /**/
	/**/
	aktienpopup.add(menuSplitten);

	mi = new MenuItem("\u00c4ndern...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAendern();
		}
	});
	aktienpopup.add(mi);

	aktienpopup.addSeparator();

	mi = new MenuItem("L\u00f6schen...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieLoeschen();
		}
	});
	aktienpopup.add(mi);
	
	add(aktienpopup);
	
	MenuBar menubar = new MenuBar();
	setMenuBar(menubar);

	Menu fileMenu = new Menu(Lang.getFileMenuTitle());
	menubar.add(fileMenu);
	
	Menu amMenu = new Menu(AktienMan.AMNAME);
	menubar.add(amMenu);

	mi = new MenuItem("Neu...",new MenuShortcut(KeyEvent.VK_N));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callNeueAktie();
		}
	});
	fileMenu.add(mi);

	fileMenu.addSeparator();

	fileSave = new MenuItem("Sichern unter...",new MenuShortcut(KeyEvent.VK_S));
	fileSave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichern();
		}
	});
	fileMenu.add(fileSave);
	
	fileMenu.addSeparator();

	filePrint = new MenuItem("Drucken...",new MenuShortcut(KeyEvent.VK_P));
	filePrint.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAusdrucken();
		}
	});
	fileMenu.add(filePrint);
	
	fileMenu.addSeparator();

	mi = new MenuItem("Beenden",new MenuShortcut(KeyEvent.VK_Q));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	fileMenu.add(mi);
	
	mi = new MenuItem("Konfiguration...",new MenuShortcut(KeyEvent.VK_K));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKonfiguration();
		}
	});
	amMenu.add(mi);

	mi = new MenuItem("Aktienlisten aktualisieren...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			new AktienAktualisieren();
		}
	});
	amMenu.add(mi);
	
	amMenu.addSeparator();

	mi = new MenuItem("\u00dcber AktienMan...",new MenuShortcut(KeyEvent.VK_I));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callAbout();
		}
	});
	amMenu.add(mi);
}


private void setChartChoice(boolean intraday) {
	buttonChart.removeAll();
	
	buttonChart.add("Chart:");
	
	buttonChart.add("6 Monate");
	buttonChart.add("12 Monate");
	buttonChart.add("24 Monate");
	
	if (intraday)
	{
		buttonChart.add("Intraday FSE");
		buttonChart.add("Intraday ETR");
	}
}


public long getErloes() {
	return benutzerliste.getErloes();
}


private void addErloes(boolean draw, long deltaErloes) {
	if (draw) panelGewinn.removeAll();
	
	benutzerliste.addToErloes(deltaErloes);
	
	long erloes = getErloes();
	
	Label titel = new Label("Verkaufserl\u00f6s:",Label.RIGHT);
	if (erloes < 0L)
	{
		titel.setForeground(Color.red);
	}
	constrain(panelGewinn,titel,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	
	chErloes = new Choice();
	chErloes.add(" "+Waehrungen.getString(erloes,Waehrungen.DEM));
	chErloes.add("Setzen...");
	if (erloes != 0L) chErloes.add("L\u00f6schen...");

	chErloes.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			int idx = chErloes.getSelectedIndex();
			
			if (idx == 1)
			{
				if (AktienMan.erloessetzen == null)
				{
					if (!isLocked(true))
					{
						AktienMan.erloessetzen = new VerkaufserloesSetzen();
					}
				}
				else
				{
					AktienMan.erloessetzen.toFront();
				}
			}
			else if (idx == 2)
			{
				if (AktienMan.erloesloeschen == null)
				{
					if (!isLocked(true))
					{
						AktienMan.erloesloeschen = new VerkaufserloesLoeschen();
					}
				}
				else
				{
					AktienMan.erloesloeschen.toFront();
				}
			}
			
			if (idx != 0) chErloes.select(0);
		}
	});

	constrain(panelGewinn,chErloes,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,0,0);
		
	if (draw) {
		panelGewinn.validate();
		panelGewinn.paintAll(getGraphics());
	}
}


public void setErloes(long newVal) {
	benutzerliste.clearErloes();
	addErloes(true,newVal);
	saveBenutzerAktien();
}


public void clearErloes() {
	benutzerliste.clearErloes();
	addErloes(true,0L);
	saveBenutzerAktien();
}


public void setupSize() {
	int w = (4*AktienMan.screenSize.width)/5;
	int h = AktienMan.screenSize.height/2;
	
	if (w < MINBREITE) w = MINBREITE;
	if (h < MINHOEHE)  h = MINHOEHE;
	
	int posX = (AktienMan.screenSize.width-w)/2;
	int posY = (AktienMan.screenSize.height-h)/2;

	int oldx = AktienMan.properties.getInt("Haupt.X");
	int oldy = AktienMan.properties.getInt("Haupt.Y");
	int oldwidth = AktienMan.properties.getInt("Haupt.Breite");
	int oldheight = AktienMan.properties.getInt("Haupt.Hoehe");
	
	if ((oldx < 0) || (oldy < 0) || (oldwidth <= 0) || (oldheight <= 0))
	{
		setBounds(posX,posY,w,h);
	}
	else
	{
		setBounds(oldx,oldy,oldwidth,oldheight);
	}
}


public synchronized void callAbout() {
	if (AktienMan.about == null)
	{
		AktienMan.about = new About();
	}
	else
	{
		AktienMan.about.toFront();
	}
}


private synchronized void callKonfiguration() {
	if (AktienMan.konfiguration == null)
	{
		AktienMan.konfiguration = new Konfiguration();
	}
	else
	{
		AktienMan.konfiguration.toFront();
	}
}


private synchronized void callNeueAktie() {
	if (AktienMan.neueaktie == null)
	{
		if (!isLocked(true))
		{
			AktienMan.neueaktie = new NeueAktie();
		}
	}
	else
	{
		AktienMan.neueaktie.toFront();
	}
}


private void disableAktienButtons() {
	abenabled = false;
	checkAktienButtons(null);
}


private void enableAktienButtons(BenutzerAktie ba) {
	abenabled = true;
	checkAktienButtons(ba);
}


private void checkAktienButtons(BenutzerAktie ba) {
	if (abenabled)
	{
		if ((ba.getStueckzahl() > 0L) && (ba.getKurs() > 0L))
		{
			buttonVerkaufen.setEnabled(true);
			buttonSplitten.setEnabled(false); /**/
		}
		else
		{
			buttonVerkaufen.setEnabled(false);
			buttonSplitten.setEnabled(false);
		}

		if (ba.getKurs() > 0L)
		{
			setChartChoice(AktienMan.listeDAX.isMember(ba.getWKN()));
			buttonChart.setEnabled(true);
		}
		else
		{
			buttonChart.setEnabled(false);
		}
		
		buttonAendern.setEnabled(true);
		buttonLoeschen.setEnabled(true);

		if (ba.isBoerseFondsOnly())
		{
			buttonMaxkurs.setEnabled(false);
		}
		else
		{
			buttonMaxkurs.setEnabled(true);
		}
	}
	else
	{
		buttonVerkaufen.setEnabled(false);
		buttonSplitten.setEnabled(false);
		buttonAendern.setEnabled(false);
		buttonLoeschen.setEnabled(false);
		buttonChart.setEnabled(false);
		buttonMaxkurs.setEnabled(false);
	}
}


private void checkLockButtons() {
	if (isLocked(false))
	{
		buttonAktFSE.setEnabled(false);
		buttonAktXetra.setEnabled(false);
		buttonAktualisieren.setEnabled(false);
	}
	else
	{
		buttonAktFSE.setEnabled(true);
		buttonAktXetra.setEnabled(true);
		buttonAktualisieren.setEnabled(true);
	}
}


private void checkListButtons() {
	if (isLocked(false))
	{
		buttonDrucken.setEnabled(false);
		buttonSpeichern.setEnabled(false);
		
		fileSave.setEnabled(false);
		filePrint.setEnabled(false);
	}
	else
	{
		if (benutzerliste.size() > 0)
		{
			buttonDrucken.setEnabled(false/*true*/);
			buttonSpeichern.setEnabled(false/*true*/);

			fileSave.setEnabled(false/*true*/);
			filePrint.setEnabled(false/*true*/);
		}
		else
		{
			buttonDrucken.setEnabled(false);
			buttonSpeichern.setEnabled(false);

			fileSave.setEnabled(false);
			filePrint.setEnabled(false);

			benutzerliste.clearDate();
		}
	}
}


public synchronized void callKamera() {
	if (AktienMan.daxKamera == null) AktienMan.daxKamera = new DAXKamera();
	if (AktienMan.daxKamera != null) AktienMan.daxKamera.showKamera();
}


public synchronized void listeAktualisieren() {
	listeAktualisieren("");
}


private boolean nochNichtAngefordert(String cmp, int bis, String boerse) {
	for (int i = 0; i < bis; i++)
	{
		if (cmp.equalsIgnoreCase(benutzerliste.getAt(i).getRequest(boerse))) return false;
	}

	return true;
}


public synchronized void listeAktualisieren(String boerse) {
	if (isLocked(true)) return;
	
	if (new ADate().after(new ADate(1998,12,1))) System.exit(0);
	
	benutzerliste.setDate(boerse);
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		benutzerliste.getAt(i).setStatusRequesting();
	}
	listeUpdate(false);

	if (new ADate().after(new ADate(1998,12,2))) return;

	for (int i = 0; i < benutzerliste.size(); i++)
	{
		String cmp = benutzerliste.getAt(i).getRequest(boerse);
		
		if (nochNichtAngefordert(cmp,i,boerse))
		{
			new ComdirectLeser(cmp).start();
		}
	}
}


public synchronized void listeNeuerAktienkurs(String wkn, String kurz, String platz,
												String name, long kurs, String kursdatum, int waehrung) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		BenutzerAktie ba = benutzerliste.getAt(i);
		
		if (ba.isEqual(wkn,kurz,platz,compPlatz))
		{
			ba.setValues(name,kurs,kursdatum,waehrung);
			valid = true;
		}
	}
	
	if (valid) listeUpdate(true);
}


public synchronized void listeAktienkursNA(String wkn, String kurz, String platz, String name) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		BenutzerAktie ba = benutzerliste.getAt(i);
		
		if (ba.isEqual(wkn,kurz,platz,compPlatz))
		{
			ba.setValues(name,BenutzerAktie.VALUE_NA);
			valid = true;
		}
	}

	if (valid) listeUpdate(true);
}


public synchronized void listeAnfrageFalsch(String wkn, String platz) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);

	for (int i = 0; i < benutzerliste.size(); i++)
	{
		BenutzerAktie ba = benutzerliste.getAt(i);
		
		if (ba.isEqual(wkn,platz,compPlatz))
		{
			ba.setValues(BenutzerAktie.VALUE_ERROR);
			valid = true;
		}
	}
	
	if (valid) listeUpdate(true);
}


public synchronized void listeNeueAktie(BenutzerAktie ba) {
	benutzerliste.add(ba);
	listeUpdate(true);
	checkListButtons();

	if (benutzerliste.size() == 1)
	{
		Thread t = new Thread() {
			public synchronized void run() {
				try
				{
					wait(200);
				}
				catch (InterruptedException e) {}

				AktienMan.hauptdialog.listeUpdate(false);
			}
		};

		t.start();
	}
}


public synchronized void listeSelect(BALabel bal, int row, int mX, int mY,
												int clicks, boolean ispop) {
	if (row >= 0)
	{
		if (isLocked(true)) return;
		
		BenutzerAktie ba = benutzerliste.getAt(row);
		
		if (ba.isSelected())
		{
			if (clicks == 2)
			{
				listeSelektierteAktieInfo();
				return;
			}
			
			if (!ispop)
			{
				ba.Unselect();
				disableAktienButtons();
			}
			else
			{
				aktienPopup(ba,bal,mX,mY);
			}
		}
		else
		{
			for (int i = 0; i < benutzerliste.size(); i++)
			{
				benutzerliste.getAt(i).Unselect();
			}
			
			ba.Select();
			enableAktienButtons(ba);

			if (clicks == 2)
			{
				listeSelektierteAktieInfo();
			}
			else if (ispop)
			{
				aktienPopup(ba,bal,mX,mY);
			}
		}
	}
}


private synchronized void aktienPopup(BenutzerAktie ba, BALabel bal, int mX, int mY) {
	if ((ba.getStueckzahl() > 0L) && (ba.getKurs() > 0L))
	{
		menuVerkaufen.setEnabled(true);
		menuSplitten.setEnabled(false); /**/
	}
	else
	{
		menuVerkaufen.setEnabled(false);
		menuSplitten.setEnabled(false);
	}
	
	if (ba.getKurs() > 0L)
	{
		menuChart.setEnabled(true);
		
		if (AktienMan.listeDAX.isMember(ba.getWKN()))
		{
			menuIntraday.setEnabled(true);
		}
		else
		{
			menuIntraday.setEnabled(false);
		}
	}
	else
	{
		menuChart.setEnabled(false);
	}
	
	if (ba.isBoerseFondsOnly())
	{
		menuMaxkurs.setEnabled(false);
	}
	else
	{
		menuMaxkurs.setEnabled(true);
	}

	popParent = bal;
	popX = mX;
	popY = mY;

	Thread t = new Thread() {
		public synchronized void run() {
			try
			{
				wait(200);
			}
			catch (InterruptedException e) {}

			AktienMan.hauptdialog.displayAktienPopup();
		}
	};

	t.start();
}


public synchronized void displayAktienPopup() {
	aktienpopup.show(popParent,popX,popY);
}


public synchronized void listeUpdate(boolean save) {
	disableAktienButtons();
	panelListe.removeAll();
	panelText.removeAll();

	if (benutzerliste.size() > 0)
	{
		boolean kurz = BenutzerListe.useShortNames();

		benutzerliste.sortByName(kurz);
		
		int i, yoffs = BenutzerAktie.addHeadingsToPanel(panelListe,benutzerliste.getDateString());
		
		for (i = 0; i < benutzerliste.size(); i++)
		{
			benutzerliste.getAt(i).addToPanel(panelListe,i+yoffs,kurz);
		}
		
		BenutzerAktie.addFooterToPanel(panelListe,i+yoffs,panelText);
	}
	
	panelText.validate();
	panelListe.validate();
	pane.validate();

	panelText.paintAll(getGraphics());
	pane.paintAll(getGraphics());
	
	if (save) saveBenutzerAktien();
}


public synchronized void windowToFront(Window w) {
	backWindow = w;

	Thread t = new Thread() {
		public synchronized void run() {
			try
			{
				wait(200);
			}
			catch (InterruptedException e) {}

			backWindow.toFront();
		}
	};

	t.start();
}


private synchronized void listeSelektierteAktieMaxkurs() {
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			new AktieMaximalkurs(benutzerliste.getAt(i));
			break;
		}
	}
}


private synchronized void listeSelektierteAktieChart(String monate) {
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			new ChartLeser(benutzerliste.getAt(i).getRequest(""),monate).start();
			break;
		}
	}
}


private synchronized void listeSelektierteAktieIntradayChart(String boerse) {
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			new IntradayChartLeser(boerse,benutzerliste.getAt(i)).start();
			break;
		}
	}
}


private synchronized void listeSelektierteAktieInfo() {
	/**/
}


private synchronized void listeSelektierteAktieAendern() {
	if (AktienMan.aktieaendern != null)
	{
		AktienMan.aktieaendern.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			AktienMan.aktieaendern = new AktieAendern(i,benutzerliste.getAt(i));
			windowToFront(AktienMan.aktieaendern);
			break;
		}
	}
}


private synchronized void listeSelektierteAktieVerkaufen() {
	if (AktienMan.aktieverkaufen != null)
	{
		AktienMan.aktieverkaufen.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			AktienMan.aktieverkaufen = new AktieVerkaufen(i,benutzerliste.getAt(i));
			windowToFront(AktienMan.aktieverkaufen);
			break;
		}
	}
}


public synchronized void listeAktieVerkaufen(int index, long anzahl, long verkaufskurs, long gebuehren) {
	addErloes(true,anzahl*verkaufskurs-gebuehren);

	BenutzerAktie ba = benutzerliste.getAt(index);
	
	if (ba.getStueckzahl() == anzahl)
	{
		listeAktieLoeschen(index);
	}
	else
	{
		ba.decStueckzahl(anzahl);

		listeUpdate(true);
		checkListButtons();
	}
}


private synchronized void listeSelektierteAktieLoeschen() {
	if (AktienMan.aktieloeschen != null)
	{
		AktienMan.aktieloeschen.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < benutzerliste.size(); i++)
	{
		if (benutzerliste.getAt(i).isSelected())
		{
			AktienMan.aktieloeschen = new AktieLoeschen(i,benutzerliste.getAt(i));
			windowToFront(AktienMan.aktieloeschen);
			break;
		}
	}
}


public synchronized void listeAktieLoeschen(int index) {
	benutzerliste.removeElementAt(index);
	listeUpdate(true);
	checkListButtons();
}


public synchronized void listeAusdrucken() {
	if (isLocked(true)) return;

	PrintJob job = getToolkit().getPrintJob(this,AktienMan.AMFENSTERTITEL+"Liste drucken",AktienMan.properties);
	
	if (job == null) return;
	
	Graphics page = job.getGraphics();
	
	Dimension size = panelListe.getSize();
	Dimension pagesize = job.getPageDimension();
	
	page.translate((pagesize.width-size.width)/2,(pagesize.height-size.height)/2);
	page.drawRect(-1,-1,size.width+1,size.height+1);
	page.setClip(0,0,size.width,size.height);

//	page.setFont(new Font("Serif",Font.PLAIN,6));

	/* falls zu gro§: verkleinern oder mehrere Seiten */
	panelListe.printAll(page);
	
	page.dispose();
	job.end();
}


public synchronized void listeSpeichern() {
	if (isLocked(true)) return;

	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+"Liste sichern als...",FileDialog.SAVE);
	
	fd.show();
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		BufferedWriter out = null;
		
		try
		{
			out = new BufferedWriter(new FileWriter(pfad+datei));
			
			out.write("Das Speichern funktioniert noch nicht...");
			out.newLine();

			/* speichern */

		}
		catch (IOException e) {}
		finally
		{
			try
			{
				if (out != null) out.close();
			}
			catch (IOException e) {}
		}
	}
}


public boolean canCancel() {
	/* Sicherheitsabfrage */
	return true;
}


public boolean canOK() {
	return false;
}


public void closed() {
	if (AktienMan.daxKamera != null) AktienMan.daxKamera.savePos();
	
	Rectangle r = getBounds();

	AktienMan.properties.setInt("Haupt.X",r.x);
	AktienMan.properties.setInt("Haupt.Y",r.y);
	AktienMan.properties.setInt("Haupt.Breite",r.width);
	AktienMan.properties.setInt("Haupt.Hoehe",r.height);

	saveBenutzerAktien();
	AktienMan.properties.saveParameters();

	System.exit(0);
}


public void componentResized(ComponentEvent e) {
	pane.paintAll(getGraphics());
}


public void componentMoved(ComponentEvent e) {}
public void componentHidden(ComponentEvent e) {}
public void componentShown(ComponentEvent e) {}

}
