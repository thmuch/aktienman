/**
 @author Thomas Much
 @version 2003-02-27

 2003-04-09
 	aktienPopup verz�gert die Anzeige des Popups wieder etwas, damit die Zeile selektiert dargestellt wird
 2003-02-27
 	Bearbeiten - Wechselkurse
 2002-12-29
   Im Bearbeiten-Men� gibt es nun den Men�punkt "Nach Updates suchen..."
 2002-10-09
   Hauptdialog implementiert nun MRJPrefsHandler (f�r Mac OS X)
 2002-01-14
   setChartChoice kennt nun auch 5- und 10-Jahres-Charts
   listeSelect/aktienPopup synchronisieren nicht mehr w�hrend aktienpopup.show (dadurch h�ngt MOSX nicht mehr)
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.apple.mrj.*;




public final class Hauptdialog extends AFrame implements ComponentListener,MRJQuitHandler,
															MRJAboutHandler,MRJPrefsHandler,KursReceiver {

private static final String FENSTERTITEL = AktienMan.AMNAME+" "+AktienMan.AMVERSION;

public static final int MINBREITE = 620;
public static final int MINHOEHE  = 300;

public Window backWindow;

private static final String CHARTSTR_1D		= "1 Tag";
private static final String CHARTSTR_5D		= "5 Tage";
private static final String CHARTSTR_10D	= "10 Tage";
private static final String CHARTSTR_3M		= "3 Monate";
private static final String CHARTSTR_6M		= "6 Monate";
private static final String CHARTSTR_1Y		= "1 Jahr";
private static final String CHARTSTR_2Y		= "2 Jahre";
private static final String CHARTSTR_3Y		= "3 Jahre";
private static final String CHARTSTR_5Y		= "5 Jahre";
private static final String CHARTSTR_MAX	= "max.";

private int locked = 0;
private boolean abenabled = false;

private BenutzerListe benutzerliste;
private Button buttonVerkaufen,buttonAendern;
private Button buttonMaxkurs,buttonInfo;
private Button buttonAktualisieren;
private ScrollPane pane;
private Panel panelText,panelGewinn,panelIndex;
private Listenbereich panelListe;
private PopupMenu aktienpopup;
private MenuItem menuVerkaufen,menuAendern,menuLoeschen,menuInfo,menuMaxkurs,menuSplitten;
private MenuItem popVerkaufen,popMaxkurs,popSplitten,popAktualisieren,pofoRename,pofoDelete,menuAkt;
private Menu menuAktAlle,menuExport;
private ChartMenu menuChart,popChart,pofoChart;
private Choice chErloes,buttonChart,lwaehrung,sortby,aktChoice;
private ProgressCanvas progressCanvas;




public Hauptdialog() {

	super(FENSTERTITEL);
}



public synchronized void setPortfolioTitle(String title) {

	setTitle(FENSTERTITEL + title);
}



public synchronized void setPortfolioFile(String datei) {

	benutzerliste.setPortfolioFile(datei);
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



private synchronized void resolutionCheck() {

	AktienMan.screenSize = getToolkit().getScreenSize();
	/* mind. 640x480 */
}



public synchronized void loadPortfolio(boolean doSave) {

	if (isLocked(true)) return;

	KursDemon.deleteKursDemon();
	RedrawDemon.getRedrawDemon().clearAllRequests();
	
	if (benutzerliste != null)
	{
		if (doSave) saveBenutzerAktien();

		benutzerliste.destroy();
	}
	
	loadBenutzerAktien();
	benutzerliste.erloesToWaehrung(Waehrungen.getListenWaehrung());

	checkListButtons();
	disableAktienButtons();

	sortby.select(getSortBy());

	listeUpdate(false,false,true,true);
	setErloes(getErloes(),false);

	if (AktienMan.properties.getBoolean("Konfig.KursTimeout")) KursDemon.createKursDemon();
}



public synchronized void saveBenutzerAktien() {

	BenutzerListe.store(benutzerliste);
}



private synchronized void loadBenutzerAktien() {

	benutzerliste = BenutzerListe.restore(Portfolios.getCurrentFile());
}



public void display() {

	loadBenutzerAktien();
	addErloes(false,0L);
	pack();
	setupSize();
	listeRedraw(false);
	checkListButtons();

	sortby.select(getSortBy());
	
	if (SysUtil.isAMac())
	{
		MRJApplicationUtils.registerAboutHandler(this);
	}
	
	if (SysUtil.isMacOSX())
	{
		MRJApplicationUtils.registerQuitHandler(this);
		MRJApplicationUtils.registerPrefsHandler(this);
	}
}



public void handleAbout() {

	callAbout();
}



public void handleQuit() {

	saveAndExit();
}



public void handlePrefs() {

	callKonfiguration();
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

	panelIndex = new Panel(gridbag);
	panelListe = new Listenbereich(gridbag);
	panelText = new Panel(gridbag);
	panelGewinn = new Panel(gridbag);
	
	IndexQuelle.addIndices(panelIndex);

	buttonAktualisieren = new Button(" Aktualisieren ");
	Button buttonKamera = new Button(" DAX-Kamera ");

	buttonAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0)
			{
				portfolioCopyKaufkurs();
			}
			else
			{
				listeAktualisieren();
			}
		}
	});
	
	buttonKamera.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKamera();
		}
	});
	
	aktChoice = AktienMan.boersenliste.getChoiceNoFonds();
	aktChoice.insert("Alle aktualisieren an:",0);

/*	if (LSRTDAX30Quelle.canUseLSRT())
	{
		if (SysUtil.isAMac()) aktChoice.add("-------");

		aktChoice.add("Lang&Schwarz Realtime-DAX30 (BID)");
		aktChoice.add("Lang&Schwarz Realtime-DAX30 (ASK)");
	} TODO */
	
	aktChoice.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {

			int idx   = aktChoice.getSelectedIndex();
			int count = AktienMan.boersenliste.getCountNoFonds();
			
			if (idx > 0)
			{
				if (idx <= count)
				{
					listeAktualisieren(idx-1,KursQuellen.getPlatzKursQuelle());
				}
				else
				{
/*					idx -= (count+1);
					
					if (SysUtil.isAMac()) idx--;
					
					if ((idx >= 0) && (idx <= 1))
					{
						listeAktualisierenAnQuelle((idx == 0) ? KursQuellen.ID_LSRTDAX30BID : KursQuellen.ID_LSRTDAX30ASK);
					} TODO */
				}
				
				aktChoice.select(0);
			}
		}
	});

	constrain(panelOben,buttonAktualisieren,0,0,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1.0,0.0,0,0,0,10);
	constrain(panelOben,aktChoice,2,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1.0,0.0,0,0,0,10);
	constrain(panelOben,buttonKamera,3,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,0.7,0.0,0,10,0,0);

	constrain(panelULinks,new Label("W\u00e4hrung:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	lwaehrung = Waehrungen.getChoice();
	lwaehrung.select(Waehrungen.getListenWaehrung());
	lwaehrung.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			waehrungWechseln();
		}
	});
	constrain(panelULinks,lwaehrung,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1.0,0.0,0,4,0,0);

	constrain(panelULinks,new Label("Sortieren:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	sortby = new Choice();
	sortby.addItem("Name");
	sortby.addItem("%absolut");
	sortby.addItem("Differenz");
	sortby.addItem("Kaufdatum");
	sortby.addItem("fix. Datum");
	sortby.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			sortByWechseln();
		}
	});
	constrain(panelULinks,sortby,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1.0,0.0,0,4,0,0);
	
	Button buttonNeu = new Button(" Aktie kaufen... ");
	buttonVerkaufen = new Button(" Aktie verkaufen... ");
	buttonAendern = new Button(" Aktie \u00e4ndern... ");

	buttonNeu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callNeueAktie();
		}
	});

	buttonVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieVerkaufen();
		}
	});

	buttonAendern.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAendern();
		}
	});
	
	buttonChart = new Choice();
	buttonChart.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {

			int idx = buttonChart.getSelectedIndex();
			
			if (idx != 0)
			{
				String selstr = buttonChart.getItem(idx);
				
				if (selstr.equals(CHARTSTR_1D))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_1D);
				}
				else if (selstr.equals(CHARTSTR_5D))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_5D);
				}
				else if (selstr.equals(CHARTSTR_10D))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_10D);
				}
				else if (selstr.equals(CHARTSTR_3M))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_3M);
				}
				else if (selstr.equals(CHARTSTR_6M))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_6M);
				}
				else if (selstr.equals(CHARTSTR_1Y))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_1Y);
				}
				else if (selstr.equals(CHARTSTR_2Y))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_2Y);
				}
				else if (selstr.equals(CHARTSTR_3Y))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_3Y);
				}
				else if (selstr.equals(CHARTSTR_5Y))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_5Y);
				}
				else if (selstr.equals(CHARTSTR_MAX))
				{
					listeSelektierteAktieChart(ChartQuellen.TIME_MAX);
				}

				buttonChart.select(0);
			}
		}
	});

	buttonMaxkurs = new Button(" Maximalkurs ");
	buttonMaxkurs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieMaxkurs();
		}
	});

	buttonInfo = new Button(" Aktie Info ");
	buttonInfo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieInfo();
		}
	});

	setChartChoice();
	
	constrain(panelUMitte,buttonNeu,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,0,6,0);
	constrain(panelUMitte,buttonInfo,1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,6,0);
	constrain(panelUMitte,buttonChart,2,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,6,0);
	constrain(panelUMitte,buttonVerkaufen,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	constrain(panelUMitte,buttonAendern,1,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,0,0);
	constrain(panelUMitte,buttonMaxkurs,2,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,0,0);
	
	BenutzerAktie.addSummen(panelText,"-----","-----","",false);
	
	pane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
	pane.add(panelListe);
	
	Adjustable vAdjust = pane.getVAdjustable();
	vAdjust.setUnitIncrement(8);

	Adjustable hAdjust = pane.getHAdjustable();
	hAdjust.setUnitIncrement(16);
	
	constrain(this,panelOben,0,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,5,10);
	
	addIndexPanelAndPane(false);

	constrain(this,panelText,0,3,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,10,0,10);
	
	progressCanvas = new ProgressCanvas();
	constrain(this,progressCanvas,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,5,10);
	
	constrain(this,panelGewinn,1,4,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,5,10);

	constrain(this,panelULinks,0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHWEST,0.0,0.0,5,10,15,5);
	constrain(this,panelUMitte,1,5,2,1,GridBagConstraints.NONE,GridBagConstraints.SOUTH,0.0,0.0,5,5,15,10);

	aktienpopup = new PopupMenu();

	MenuItem mi = new MenuItem("Info");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieInfo();
		}
	});
	aktienpopup.add(mi);

	popChart = new ChartMenu();
	aktienpopup.add(popChart);

	popMaxkurs = new MenuItem("Maximalkurs");
	popMaxkurs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieMaxkurs();
		}
	});
	aktienpopup.add(popMaxkurs);

	popAktualisieren = new MenuItem("Aktualisieren");
	popAktualisieren.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAktualisieren();
		}
	});
	aktienpopup.add(popAktualisieren);
	
	aktienpopup.addSeparator();
	
	aktienpopup.add(Portfolios.getPopupCopy(this));
	aktienpopup.add(Portfolios.getPopupMove(this));
	
	aktienpopup.addSeparator();

	popVerkaufen = new MenuItem("Verkaufen...");	
	popVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieVerkaufen();
		}
	});
	aktienpopup.add(popVerkaufen);

	mi = new MenuItem("\u00c4ndern...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAendern();
		}
	});
	aktienpopup.add(mi);

	popSplitten = new MenuItem("Splitten...");
	popSplitten.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieSplitten();
		}
	});
	aktienpopup.add(popSplitten);

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

	Menu fileMenu = new Menu(Lang.FILEMENUTITLE,true);
	menubar.add(fileMenu);
	
	Menu amMenu = new Menu(Lang.EDITMENUTITLE,true);
	menubar.add(amMenu);
	
	Menu aktMenu = new Menu("Aktualisieren",true);
	menubar.add(aktMenu);

	Menu aktieMenu = new Menu("Aktie",true);
	menubar.add(aktieMenu);
	
	Menu pofoMenu = new Menu("Portfolio",true);
	menubar.add(pofoMenu);
	
	menuExport = new Menu("Liste exportieren");

	MenuItem menuSaveCSV = new MenuItem("als CSV-Datei...");
	menuSaveCSV.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichernCSV();
		}
	});
	menuExport.add(menuSaveCSV);

	MenuItem menuSaveHTML = new MenuItem("als HTML-Datei...",new MenuShortcut(KeyEvent.VK_E));
	menuSaveHTML.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichernHTML();
		}
	});
	menuExport.add(menuSaveHTML);

	fileMenu.add(menuExport);
	
	if (!SysUtil.isMacOSX())
	{
		fileMenu.addSeparator();

		mi = new MenuItem("Beenden",new MenuShortcut(KeyEvent.VK_Q));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCancel();
			}
		});
		fileMenu.add(mi);
	}

	mi = new MenuItem("Warnungen...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKonfigurationWarnungen();
		}
	});
	amMenu.add(mi);

	mi = new MenuItem("Indizes...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKonfigurationIndizes();
		}
	});
	amMenu.add(mi);

	mi = new MenuItem("Voreinstellungen...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKonfiguration();
		}
	});
	amMenu.add(mi);
	
	amMenu.addSeparator();

	mi = new MenuItem("Wechselkurse...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callWechselkurse();
		}
	});
	amMenu.add(mi);

	amMenu.addSeparator();
	
	mi = new MenuItem("Nach Updates suchen...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			UpdateManager.checkForUpdates(true);
		}
	});
	amMenu.add(mi);
	
	menuAkt = new MenuItem("An der jeweiligen B\u00f6rse",new MenuShortcut(KeyEvent.VK_A));
	menuAkt.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren();
		}
	});
	aktMenu.add(menuAkt);

	aktMenu.addSeparator();
	
	menuAktAlle = new Menu("Alle Aktien an");
	for (int i = 0; i < AktienMan.boersenliste.size() - 1; i++)
	{
		Boersenplatz bp = AktienMan.boersenliste.getAt(i);
		mi = new MenuItem(bp.toString());
		mi.addActionListener(new BoersenListener(i));
		menuAktAlle.add(mi);
	}
	aktMenu.add(menuAktAlle);
	
	aktMenu.addSeparator();
	
	mi = new MenuItem("DAX-Kamera",new MenuShortcut(KeyEvent.VK_D));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKamera();
		}
	});
	aktMenu.add(mi);
	
	mi = new MenuItem("Kaufen...",new MenuShortcut(KeyEvent.VK_K));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callNeueAktie();
		}
	});
	aktieMenu.add(mi);

	menuVerkaufen = new MenuItem("Verkaufen...");	
	menuVerkaufen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieVerkaufen();
		}
	});
	aktieMenu.add(menuVerkaufen);

	menuAendern = new MenuItem("\u00c4ndern...");
	menuAendern.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAendern();
		}
	});
	aktieMenu.add(menuAendern);

	menuSplitten = new MenuItem("Splitten...");
	menuSplitten.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieSplitten();
		}
	});
	aktieMenu.add(menuSplitten);

	aktieMenu.addSeparator();

	menuLoeschen = new MenuItem("L\u00f6schen...");
	menuLoeschen.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieLoeschen();
		}
	});
	aktieMenu.add(menuLoeschen);
	
	aktieMenu.addSeparator();

	menuInfo = new MenuItem("Info",new MenuShortcut(KeyEvent.VK_I));
	menuInfo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieInfo();
		}
	});
	aktieMenu.add(menuInfo);

	menuChart = new ChartMenu();
	aktieMenu.add(menuChart);

	menuMaxkurs = new MenuItem("Maximalkurs",new MenuShortcut(KeyEvent.VK_M));
	menuMaxkurs.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieMaxkurs();
		}
	});
	aktieMenu.add(menuMaxkurs);

	aktieMenu.addSeparator();
	
	aktieMenu.add(Portfolios.getMenuCopy(this));
	aktieMenu.add(Portfolios.getMenuMove(this));
	
	mi = new MenuItem("Neu...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			portfolioNeu();
		}
	});
	pofoMenu.add(mi);
	
	pofoMenu.add(Portfolios.getMenu(this));

	pofoMenu.addSeparator();

	pofoChart = new PofoChartMenu();
	pofoMenu.add(pofoChart);

	pofoMenu.addSeparator();

	pofoRename = new MenuItem("Umbenennen...");
	pofoRename.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			portfolioUmbenennen();
		}
	});
	pofoMenu.add(pofoRename);

	pofoMenu.addSeparator();

	pofoDelete = new MenuItem("L\u00f6schen...");
	pofoDelete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			portfolioLoeschen();
		}
	});
	pofoMenu.add(pofoDelete);
	
	if (!SysUtil.isAMac())
	{
		Menu hilfeMenu = new Menu("Hilfe",true);
		menubar.add(hilfeMenu);
		menubar.setHelpMenu(hilfeMenu);

		mi = new MenuItem("\u00dcber AktienMan...");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callAbout();
			}
		});
		hilfeMenu.add(mi);
	}

	disableAktienButtons();
	Portfolios.updateMenu();
}



private void setChartChoice() {

	buttonChart.removeAll();
	
	buttonChart.add("Chart:");

	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_1D)) buttonChart.add(CHARTSTR_1D);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_5D)) buttonChart.add(CHARTSTR_5D);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_10D)) buttonChart.add(CHARTSTR_10D);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_3M)) buttonChart.add(CHARTSTR_3M);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_6M)) buttonChart.add(CHARTSTR_6M);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_1Y)) buttonChart.add(CHARTSTR_1Y);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_2Y)) buttonChart.add(CHARTSTR_2Y);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_3Y)) buttonChart.add(CHARTSTR_3Y);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_5Y)) buttonChart.add(CHARTSTR_5Y);
	if (ChartQuellen.hasAnyTime(ChartQuellen.TIME_MAX)) buttonChart.add(CHARTSTR_MAX);
}



public long getErloes() {

	return benutzerliste.getErloes();
}



public int getErloesWaehrung() {

	return benutzerliste.getErloesWaehrung();
}



private void addErloes(boolean draw, long deltaErloes) {

	if (draw) panelGewinn.removeAll();
	
	benutzerliste.addToErloes(deltaErloes);
	
	long erloes = getErloes();
	
	Label titel = new Label("Gesamtaufwand:",Label.RIGHT);
	if (erloes < 0L)
	{
		titel.setForeground(Color.red);
	}
	constrain(panelGewinn,titel,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	
	chErloes = new Choice();
	chErloes.add(" "+Waehrungen.getString(erloes,getErloesWaehrung()));
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



public synchronized void setErloes(long newVal, boolean doSave) {

	benutzerliste.clearErloes();
	addErloes(true,newVal);

	if (doSave) RedrawDemon.getRedrawDemon().incSaveRequests();
}



public synchronized void setErloes(long newVal) {

	setErloes(newVal,true);
}



public synchronized void clearErloes() {

	benutzerliste.clearErloes();
	addErloes(true,0L);
	RedrawDemon.getRedrawDemon().incSaveRequests();
}



public synchronized int getSortBy() {

	return benutzerliste.getSortBy();
}



public synchronized void setSortBy(int neu) {

	benutzerliste.setSortBy(neu);
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



public synchronized void callWechselkurse() {

	if (AktienMan.wechselkurse == null)
	{
		AktienMan.wechselkurse = new Wechselkurse();
	}
	else
	{
		AktienMan.wechselkurse.toFront();
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



private synchronized void callKonfigurationWarnungen() {

	if (AktienMan.konfigurationWarnungen == null)
	{
		AktienMan.konfigurationWarnungen = new KonfigurationWarnungen();
	}
	else
	{
		AktienMan.konfigurationWarnungen.toFront();
	}
}



private synchronized void callKonfigurationIndizes() {

	if (AktienMan.konfigurationIndizes == null)
	{
		AktienMan.konfigurationIndizes = new KonfigurationIndizes();
	}
	else
	{
		AktienMan.konfigurationIndizes.toFront();
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



private synchronized void waehrungWechseln() {

	int neu = lwaehrung.getSelectedIndex();
	
	if (neu != Waehrungen.getListenWaehrung())
	{
		Waehrungen.setListenWaehrung(neu);
		benutzerliste.erloesToWaehrung(neu);

		listeUpdate(false,true,true,false);
		setErloes(getErloes());

		AktienMan.properties.saveParameters();
	}
}



private synchronized void sortByWechseln() {

	int neu = sortby.getSelectedIndex();
	
	if (neu != getSortBy())
	{
		setSortBy(neu);

		listeUpdate(false,true,true,false);

		AktienMan.properties.saveParameters();
	}
}



private void disableAktienButtons() {

	abenabled = false;
	checkAktienButtons(null);
	
	Portfolios.disableMoveCopyMenus();
}



private void enableAktienButtons(BenutzerAktie ba) {

	abenabled = true;
	checkAktienButtons(ba);
	
	Portfolios.enableMoveCopyMenus();
}



private void checkAktienButtons(BenutzerAktie ba) {

	if (abenabled)
	{
		if ((ba.getStueckzahl() > 0L) && (ba.getKurs() > 0L) && (!ba.nurBeobachten()))
		{
			buttonVerkaufen.setEnabled(true);
			menuVerkaufen.setEnabled(true);
		}
		else
		{
			buttonVerkaufen.setEnabled(false);
			menuVerkaufen.setEnabled(false);
		}

		if (ba.getKurs() > 0L)
		{
			setChartChoice();
			buttonChart.setEnabled(true);
			
			menuChart.checkTypes();
			menuChart.setEnabled(true);
		}
		else
		{
			buttonChart.setEnabled(false);
			menuChart.setEnabled(false);
		}
		
		buttonAendern.setEnabled(true);
		buttonInfo.setEnabled(true);

		menuAendern.setEnabled(true);
		menuLoeschen.setEnabled(true);
		menuInfo.setEnabled(true);

		if (ba.isFonds())
		{
			buttonMaxkurs.setEnabled(false);
			menuMaxkurs.setEnabled(false);
			menuSplitten.setEnabled(false);
		}
		else
		{
			buttonMaxkurs.setEnabled(true);
			menuMaxkurs.setEnabled(true);
			menuSplitten.setEnabled(true);
		}
	}
	else
	{
		buttonVerkaufen.setEnabled(false);
		buttonAendern.setEnabled(false);
		buttonChart.setEnabled(false);
		buttonMaxkurs.setEnabled(false);
		buttonInfo.setEnabled(false);

		menuVerkaufen.setEnabled(false);
		menuAendern.setEnabled(false);
		menuSplitten.setEnabled(false);
		menuLoeschen.setEnabled(false);
		menuChart.setEnabled(false);
		menuMaxkurs.setEnabled(false);
		menuInfo.setEnabled(false);
	}
}



private void checkLockButtons() {

	if (isLocked(false))
	{
		menuAkt.setEnabled(false);
		menuAktAlle.setEnabled(false);
		aktChoice.setEnabled(false);
		buttonAktualisieren.setEnabled(false);
	}
	else
	{
		menuAkt.setEnabled(true);
		menuAktAlle.setEnabled(true);
		aktChoice.setEnabled(true);
		buttonAktualisieren.setEnabled(true);
	}
}



public void checkPortfolioMenu() {

	if (Portfolios.isDefault())
	{
		pofoRename.setEnabled(false);
		pofoDelete.setEnabled(false);
	}
	else
	{
		pofoRename.setEnabled(true);
		pofoDelete.setEnabled(true);
	}
}



public synchronized long getAnzahlAktien() {

	return benutzerliste.size();
}



public synchronized BenutzerAktie getAktieNr(int index) {

	return benutzerliste.getAt(index);
}



private void checkListButtons() {

	if (isLocked(false))
	{
		lwaehrung.setEnabled(false);
		sortby.setEnabled(false);
		menuExport.setEnabled(false);
		pofoChart.setEnabled(false);
	}
	else
	{
		lwaehrung.setEnabled(true);
		sortby.setEnabled(true);

		if (getAnzahlAktien() > 0)
		{
			menuExport.setEnabled(true);

			pofoChart.setEnabled(true);
			pofoChart.checkTypes();
		}
		else
		{
			menuExport.setEnabled(false);
			pofoChart.setEnabled(false);

			benutzerliste.clearDate();
		}
	}
}



public synchronized void preferencesChanged() {

	pofoChart.checkTypes();
}



public synchronized void callKamera() {

	if (AktienMan.daxKamera == null) AktienMan.daxKamera = new DAXKamera();

	if (AktienMan.daxKamera != null) AktienMan.daxKamera.showKamera();

	IndexQuelle.call();
}



private synchronized void listeAktualisierenAnQuelle(long qid) {

	listeAktualisieren("",KursQuellen.getKursQuelle(qid));
}



public synchronized void listeAktualisieren() {

	listeAktualisieren("",null);
}



public synchronized void listeAktualisieren(int boersenindex, KursQuelle quelle) {

	listeAktualisieren(AktienMan.boersenliste.getAt(boersenindex).getKurz(),quelle);
}



private boolean nochNichtAngefordert(String cmp, int bis, String boerse) {

	for (int i = 0; i < bis; i++)
	{
		BenutzerAktie ba = getAktieNr(i);

		if ((!ba.doNotUpdate()) && (cmp.equalsIgnoreCase(ba.getRequest(boerse))))
		{
			return false;
		}
	}

	return true;
}



public synchronized void listeAktualisieren(String boerse, KursQuelle quelle) {

	if (!KursDemon.canCallKursDemon(boerse,quelle)) listeAktualisierenAusfuehren(boerse,quelle);
	
	IndexQuelle.call();
}



public synchronized void listeAktualisierenAusfuehren(String boerse, KursQuelle quelle) {

	if (isLocked(true)) return;
	
	String rem = boerse;
	
	if (quelle != null)
	{
		if (rem.length() > 0) rem += "; ";
	
		rem += quelle.getName();
	}
	
	benutzerliste.setDate(boerse,rem);
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);

		if (!ba.doNotUpdate())
		{
			ba.setStatusRequestingAndRepaint();
		}
	}
	
	KursQuelle fonds = KursQuellen.getFondsQuelle();

	if (quelle == null)
	{
		quelle = KursQuellen.getKursQuelle();
	}
	
	int progressCount = 0;
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		String cmp = ba.getRequest(boerse);
		
		if ((nochNichtAngefordert(cmp,i,boerse)) && (!ba.doNotUpdate()))
		{
			progressCount++;
			
			if (ba.isFonds())
			{
				fonds.sendRequest(this,cmp,ba.getWKNString(),ba.getBoerse());
			}
			else
			{
				quelle.sendRequest(this,cmp,ba.getWKNString(),ba.getBoerse());
			}
		}
	}
	
	quelle.flush();
	fonds.flush();
	
	progressCanvas.addMax(progressCount);

	BenutzerAktie.setLastUpdateAndRepaint(benutzerliste.getDateString());
	
	// TODO: nicht hier aufrufen, sondern immer, bevor ein Online-Zugriff erfolgt
	//   -----> das wird sp�ter der Ersatz f�r AktienMan.checkURLs
	AktienMan.doOnlineChecks();
}



private synchronized void listeSelektierteAktieAktualisieren() {

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);

		if ((ba.isSelected()) && (!ba.doNotUpdate()))
		{
			ba.setStatusRequestingAndRepaint();

			progressCanvas.addMax(1);

			if (ba.isFonds())
			{
				KursQuellen.getFondsQuelle().sendSingleRequest(this,ba.getRequest(),ba.getWKNString(),ba.getBoerse());
			}
			else
			{
				KursQuellen.getKursQuelle().sendSingleRequest(this,ba.getRequest(),ba.getWKNString(),ba.getBoerse());
			}
			
			break;
		}
	}
}



public synchronized void listeNeuerAktienkurs(String wkn, String isin, String kurz, String platz,
												String name, long kurs, String kursdatum,
												long vortageskurs, long eroeffnungskurs,
												long hoechstkurs, long tiefstkurs,
												long handelsvolumen, int waehrung,
												boolean sofortZeichnen) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		
		if (ba.hasWKN(wkn))
		{
			ba.setSymbol(kurz);
		}
		
		if ((ba.isEqual(wkn,kurz,platz,compPlatz)) && (!ba.doNotUpdate()))
		{
			/* TODO: W�hrung beachten! (wo? in BenutzerAktie?) */

			ba.setValues(name,isin,kurs,kursdatum,vortageskurs,eroeffnungskurs,
							hoechstkurs,tiefstkurs,handelsvolumen,waehrung);
			valid = true;
		}
	}
	
	if (valid)
	{
		listeUpdate(true,false,sofortZeichnen,false);
		progressCanvas.inc();
	}
}



public synchronized void listeAktienkursNA(String wkn, String kurz, String platz,
											String name, boolean sofortZeichnen) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		
		if ((ba.isEqual(wkn,kurz,platz,compPlatz)) && (!ba.doNotUpdate()))
		{
			ba.setValues(name,BenutzerAktie.VALUE_NA);
			valid = true;
		}
	}

	if (valid)
	{
		listeUpdate(true,false,sofortZeichnen,false);
		progressCanvas.inc();
	}
}



public synchronized void listeAnfrageFehler(String request, String wkn, String platz, boolean sofortZeichnen, KursQuelle first, KursQuelle current) {

	long nextID = KursQuellen.getNextID(first,current);

	if (AktienMan.DEBUG)
	{
		System.out.println("Fehler beim Einlesen der Kursdaten von "+wkn+"."+platz+"  -> "+nextID);
	}

	if (nextID == KursQuellen.ID_NONE)
	{
		if (wkn.length() == 0) return;
		if (platz.length() == 0) return;

		boolean valid = false;
		boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);

		for (int i = 0; i < getAnzahlAktien(); i++)
		{
			BenutzerAktie ba = getAktieNr(i);
			
			if ((ba.isEqual(wkn,platz,compPlatz)) && (!ba.doNotUpdate()))
			{
				ba.setStatusErrorAndRepaint();
				valid = true;
			}
		}
		
		if (valid)
		{
			progressCanvas.inc();
		}
	}
	else
	{
		KursQuellen.getKursQuelle(nextID).sendSingleRequest(this,request,wkn,platz,sofortZeichnen,first);
	}
}



public synchronized void listeNeueAktie(BenutzerAktie ba) {

	/* #Demoversion */
	if (main() || (getAnzahlAktien() < 3))
	{
		benutzerliste.add(ba);
		listeUpdate(true,false,true,false);
		checkListButtons();
	}

	if (getAnzahlAktien() == 1)
	{
		Thread t = new Thread() {
			public synchronized void run() {
				try
				{
					wait(200);
				}
				catch (InterruptedException e) {}

				AktienMan.hauptdialog.listeUpdate(false,false,true,false);
			}
		};

		t.start();
	}
}



public void listeSelect(Component bal, int row, int mX, int mY,
										int clicks, boolean ispop) {
	if (row >= 0)
	{
		BenutzerAktie ba;
		
		synchronized (this)
		{
			if (isLocked(true)) return;
			
			ba = getAktieNr(row);
			
			if (ba.isSelected())
			{
				if (!ispop)
				{
					ba.Unselect();
					disableAktienButtons();
				}
			}
			else
			{
				for (int i = 0; i < getAnzahlAktien(); i++)
				{
					getAktieNr(i).Unselect();
				}
				
				ba.Select();
				enableAktienButtons(ba);
			}
		}

		if (ispop)
		{
			aktienPopup(ba,bal,mX,mY);
		}
	}
}



private void aktienPopup(BenutzerAktie ba, final Component bal, final int mX, final int mY) {

	synchronized(this)
	{
		if ((ba.getStueckzahl() > 0L) && (ba.getKurs() > 0L) && (!ba.nurBeobachten()))
		{
			popVerkaufen.setEnabled(true);
		}
		else
		{
			popVerkaufen.setEnabled(false);
		}
		
		if (ba.getKurs() > 0L)
		{
			popChart.setEnabled(true);
			
			popChart.checkTypes();
		}
		else
		{
			popChart.setEnabled(false);
		}
		
		if (ba.isFonds())
		{
			popMaxkurs.setEnabled(false);
			popSplitten.setEnabled(false);
		}
		else
		{
			popMaxkurs.setEnabled(true);
			popSplitten.setEnabled(true);
		}
		
		popAktualisieren.setEnabled(!ba.doNotUpdate());
	}

	Thread t = new Thread() {
		public synchronized void run() {
			try
			{
				wait(200);
			}
			catch (InterruptedException e) {}
			
			aktienpopup.show(bal,mX,mY);
		}
	};

	t.start();
}



private synchronized void portfolioNeu() {

	if (AktienMan.portfolioneu != null)
	{
		AktienMan.portfolioneu.toFront();
		return;
	}
	
	if (isLocked(true)) return;

	AktienMan.portfolioneu = new PortfolioNeu();
	windowToFront(AktienMan.portfolioneu);
}



public synchronized void portfolioIntradayCharts(int time) {

	new MultiChartFrame(time,ChartQuellen.TYPE_LINIEN);
}



private synchronized void portfolioUmbenennen() {

	if (AktienMan.portfolioumbenennen != null)
	{
		AktienMan.portfolioumbenennen.toFront();
		return;
	}
	
	if (isLocked(true)) return;

	AktienMan.portfolioumbenennen = new PortfolioUmbenennen();
	windowToFront(AktienMan.portfolioumbenennen);
}



private synchronized void portfolioLoeschen() {

	if (AktienMan.portfolioloeschen != null)
	{
		AktienMan.portfolioloeschen.toFront();
		return;
	}
	
	if (isLocked(true)) return;

	AktienMan.portfolioloeschen = new PortfolioLoeschen();
	windowToFront(AktienMan.portfolioloeschen);
}



private synchronized void portfolioCopyKaufkurs() {

	if (AktienMan.portfoliocopykaufkurs != null)
	{
		AktienMan.portfoliocopykaufkurs.toFront();
		return;
	}
	
	if (isLocked(true)) return;

	AktienMan.portfoliocopykaufkurs = new PortfolioCopyKaufkurs();
	windowToFront(AktienMan.portfoliocopykaufkurs);
}



public synchronized void listeCopyKaufkurs() {

	int gestern = (new ADate().getSerialDate()) - 1;

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		
		if (ba.nurBeobachten())
		{
			ba.changeKaufkurs(ba.getKurs(),ba.getKurswaehrung(),new ADate(gestern));
		}
	}
	
	listeUpdate(true,true,true,false);
}



public synchronized void listeUpdate(boolean save, boolean chgInfo, boolean sofort, boolean to00) {

	RedrawDemon.getRedrawDemon().incRedrawRequests(to00);
	
	if (save) RedrawDemon.getRedrawDemon().incSaveRequests();
	if (chgInfo) RedrawDemon.getRedrawDemon().incInfoRequests();
	
	if (sofort) RedrawDemon.getRedrawDemon().interrupt();
}



public synchronized void listeRedraw(boolean to00) {

	disableAktienButtons();
	
	panelListe.setVisible(false);

	panelListe.removeAll();
	panelText.removeAll();

	if (getAnzahlAktien() > 0)
	{
		boolean kurz = BenutzerListe.useShortNames();
		boolean steuerfrei = BenutzerListe.useSteuerfrei();
		boolean prozJahr = !BenutzerListe.calcProzJahr();

		benutzerliste.sort(kurz);
		
		int i, yoffs = BenutzerAktie.addHeadingsToPanel(panelListe,benutzerliste.getDateString());
		
		for (i = 0; i < getAnzahlAktien(); i++)
		{
			getAktieNr(i).addToPanel(panelListe,i+yoffs,kurz,steuerfrei,prozJahr);
		}
		
		BenutzerAktie.addFooterToPanel(panelListe,i+yoffs,panelText);
	}
	
	panelText.validate();
	panelListe.validate();
	pane.validate();

	if (to00 || (getAnzahlAktien() < 1))
	{
		pane.setScrollPosition(new Point(0,0));
	}
	
	panelListe.setVisible(true);

	panelText.paintAll(getGraphics());
	pane.paintAll(getGraphics());
}



public synchronized void listeUpdateInfo() {

	if (getAnzahlAktien() > 0)
	{
		for (int i = 0; i < getAnzahlAktien(); i++)
		{
			getAktieNr(i).infoDialogSetValues(true);
		}
	}
}



public synchronized void addIndexPanelAndPane(boolean doResize) {

	Dimension d = null;
	
	if (doResize)
	{
		d = getSize();
		
		panelListe.setVisible(false);

		panelIndex.invalidate();
		pane.invalidate();
		invalidate();
	}

	if ((!doResize) || (!SysUtil.isMacOSX()))
	{
		constrain(this,panelIndex,0,1,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,10,5,10);
		constrain(this,pane,0,2,3,1,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0,5,10,5,10);
	}
	
	if (doResize)
	{
		pack();
		validate();

		panelListe.setVisible(true);
	
		repaint();
	
		setSize(d);
	}
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

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			new AktieMaximalkurs(getAktieNr(i));
			break;
		}
	}
}



public synchronized void listeSelektierteAktieChart(int time) {

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);

		if (ba.isSelected())
		{
			ChartQuellen.displayChart(ba.getWKNString(),ba.getBoerse(),time,ChartQuellen.TYPE_LINIEN);
			break;
		}
	}
}



private synchronized void listeSelektierteAktieInfo() {

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			getAktieNr(i).infoDialogOpen();
			break;
		}
	}
}



private synchronized void listeSelektierteAktieAendern() {

	if (AktienMan.aktieaendern != null)
	{
		AktienMan.aktieaendern.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			AktienMan.aktieaendern = new AktieAendern(i,getAktieNr(i));
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
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			AktienMan.aktieverkaufen = new AktieVerkaufen(i,getAktieNr(i));
			windowToFront(AktienMan.aktieverkaufen);
			break;
		}
	}
}



public synchronized void listeSelektierteAktieCopyMove(int pofoToIndex, boolean move) {

	if (isLocked(true)) return;

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			listeAktieCopyMove(i,pofoToIndex,move);
			break;
		}
	}
}



private synchronized void listeAktieCopyMove(int aktieIndex, int pofoToIndex, boolean move) {

	if (pofoToIndex == Portfolios.INDEX_NONE) return;
	
	BenutzerAktie to = (BenutzerAktie)(getAktieNr(aktieIndex).clone());
	
	if (to == null) return;

	BenutzerListe listeTo = BenutzerListe.restore(Portfolios.getFileByIndex(pofoToIndex));
	
	listeTo.add(to);
	
	boolean error = BenutzerListe.store(listeTo);

	if (move && (!error))
	{
		listeAktieLoeschen(aktieIndex);
	}
}



public synchronized void listeAktieVerkaufen(int index, long anzahl, long verkaufskurs, long gebuehren,
												boolean calculate, boolean move, String moveTo, ADate vdatum) {

	if (calculate)
	{
		addErloes(true,anzahl * Waehrungen.exchange(verkaufskurs,Waehrungen.getVerkaufsWaehrung(),getErloesWaehrung())
						- Waehrungen.exchange(gebuehren,Waehrungen.getVerkaufsWaehrung(),getErloesWaehrung()));
	}

	BenutzerAktie ba = getAktieNr(index);
	
	if (anzahl >= ba.getStueckzahl())
	{
		if (move)
		{
			ba.fixAktie(vdatum,verkaufskurs,Waehrungen.getVerkaufsWaehrung());

			listeAktieCopyMove(index,Portfolios.getIndexByName(moveTo),true);
		}
		else
		{
			listeAktieLoeschen(index);
		}
	}
	else
	{
		if (move)
		{
			int pofoToIndex = Portfolios.getIndexByName(moveTo);

			if (pofoToIndex != Portfolios.INDEX_NONE)
			{
				BenutzerAktie to = (BenutzerAktie)(ba.clone());
				
				if (to != null)
				{
					to.setStueckzahl(anzahl);
					to.fixAktie(vdatum,verkaufskurs,Waehrungen.getVerkaufsWaehrung());
					
					BenutzerListe listeTo = BenutzerListe.restore(Portfolios.getFileByIndex(pofoToIndex));
	
					listeTo.add(to);
	
					BenutzerListe.store(listeTo);
				}
			}
		}
		
		ba.decStueckzahl(anzahl);

		listeUpdate(true,false,true,false);
		checkListButtons();
	}
}



public synchronized void listeSelektierteAktieSplitten() {

	if (AktienMan.aktiesplitten != null)
	{
		AktienMan.aktiesplitten.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			AktienMan.aktiesplitten = new AktieSplitten(i,getAktieNr(i));
			windowToFront(AktienMan.aktiesplitten);
			break;
		}
	}
}



private synchronized void listeSelektierteAktieLoeschen() {

	if (AktienMan.aktieloeschen != null)
	{
		AktienMan.aktieloeschen.toFront();
		return;
	}
	
	if (isLocked(true)) return;
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			AktienMan.aktieloeschen = new AktieLoeschen(i,getAktieNr(i));
			windowToFront(AktienMan.aktieloeschen);
			break;
		}
	}
}



public synchronized void listeAktieLoeschen(int index) {

	benutzerliste.removeAt(index);
	listeUpdate(true,false,true,false);
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

	/* falls zu gro�: verkleinern oder mehrere Seiten */
	panelListe.printAll(page);
	
	page.dispose();
	job.end();
}



private synchronized void listeSpeichernHTML() {

	if (isLocked(true)) return;

	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+"HTML-Datei exportieren...",FileDialog.SAVE);

	String dateiname;
	
	if (Portfolios.isDefault())
	{
		dateiname = "Meine Aktien";
	}
	else
	{
		dateiname = Portfolios.getCurrentName();
	}

	fd.setFile(dateiname+".html");
	fd.setVisible(true);
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		BufferedWriter out = null;
		
		if (SysUtil.isAMac())
		{
			MRJFileUtils.setDefaultFileType(new MRJOSType("????"));
			MRJFileUtils.setDefaultFileCreator(new MRJOSType("????"));
		}

		boolean kurz = BenutzerListe.useShortNames();
		boolean steuerfrei = BenutzerListe.useSteuerfrei();
		boolean prozJahr = !BenutzerListe.calcProzJahr();
		
		String filename = pfad+datei;
		
		if (filename.endsWith("."))
		{
			filename = filename + "html";
		}
		else
		{
			String fu = filename.toUpperCase();
			
			if ((!fu.endsWith(".HTM")) && (!fu.endsWith(".HTML")))
			{
				filename = filename + ".html";
			}
		}

		File f = new File(filename);
		
		if (f.exists())
		{
			File backup = new File(filename + ".bak");
			
			if (backup.exists()) backup.delete();
			
			f.renameTo(backup);
			
			f = new File(filename);
		}
		
		try
		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "ISO-8859-15"));

			String nachname = AktienMan.properties.getString("Key.Nachname");
			String vorname = AktienMan.properties.getString("Key.Vorname");

			out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\"");
			out.newLine();
			out.write("    \"http://www.w3.org/TR/REC-html40/strict.dtd\">");
			out.newLine();
			
			out.write("<HTML>");
			out.newLine();
			out.write("<HEAD>");
			out.newLine();
			out.write("<TITLE>");
			out.write(AktienMan.AMNAME+" (");
			if ((nachname.length() > 1) && (vorname.length() > 1))
			{
				out.write(vorname+" "+nachname);
			}
			else
			{
				out.write("DEMOVERSION");
			}
			out.write(")</TITLE>");
			out.newLine();
			out.write("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=ISO-8859-15\">");
			out.newLine();
			out.write("</HEAD>");
			out.newLine();
			out.write("<BODY BGCOLOR=\"#ffffff\">");
			out.newLine();
			out.newLine();
			
			out.write("<H2>"+AktienMan.AMNAME);
			
			String titel = Portfolios.getCurrentWindowTitle();
			
			if (titel.length() == 0)
			{
				titel = " - Standardportfolio";
			}

			out.write(titel+"</H2>");
			out.newLine();
			out.newLine();

			out.write("<TABLE BORDER>");
			out.newLine();
			out.newLine();
			
			BenutzerAktie.saveHeaderHTML(out,benutzerliste.getDateString());

			for (int i = 0; i < getAnzahlAktien(); i++)
			{
				getAktieNr(i).saveHTML(out,kurz,steuerfrei,prozJahr);
			}

			BenutzerAktie.saveFooterHTML(out);

			out.write("</TABLE>");
			out.newLine();
			out.newLine();
			out.write("</BODY>");
			out.newLine();
			out.write("</HTML>");
			out.newLine();
			
			out.flush();
		}
		catch (IOException e) {}
		finally
		{
			try
			{
				if (out != null) out.close();
			}
			catch (IOException e) {}
			
			out = null;
		}
		
		if (SysUtil.isAMac())
		{
			try
			{
				MRJFileUtils.setFileTypeAndCreator(f,new MRJOSType("TEXT"),new MRJOSType("iCAB"));
				/* Netscape = "MOSS" */
				/* IE ber�cksichtigen */
			}
			catch (Exception e) {}

			try
			{
				MRJFileUtils.openURL(f.toString());
			}
			catch (Exception e)
			{
				try
				{
					String params[] = { "open", f.toString() };

					Runtime.getRuntime().exec(params);
				}
				catch (Exception e2)
				{
					System.out.println(e2);
				}
			}
		}
		
/*		if (SysUtil.isMacOS())
		{
			double mrjver;
			
			try
			{
				mrjver = Double.valueOf(System.getProperty("mrj.version")).doubleValue();
			}
			catch (NumberFormatException e)
			{
				mrjver = 0.0;
			}
			
			if (mrjver >= 2.1)
			{
				try
				{
					File cw = MRJFileUtils.findApplication(new MRJOSType("MOSS"));

					String params[] = { cw.toString(), f.toString() };

					Runtime.getRuntime().exec(params);
				}
				catch (Exception e) {}
			}
		} */
	}
}



private synchronized void listeSpeichernCSV() {

	if (isLocked(true)) return;

	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+"CSV-Datei exportieren...",FileDialog.SAVE);

	String dateiname;
	
	if (Portfolios.isDefault())
	{
		dateiname = "Meine Aktien";
	}
	else
	{
		dateiname = Portfolios.getCurrentName();
	}

	fd.setFile(dateiname+".csv");
	fd.setVisible(true);
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		BufferedWriter out = null;
		
		if (SysUtil.isAMac())
		{
			MRJFileUtils.setDefaultFileType(new MRJOSType("????"));
			MRJFileUtils.setDefaultFileCreator(new MRJOSType("????"));
		}

		boolean kurz = BenutzerListe.useShortNames();
		boolean prozJahr = !BenutzerListe.calcProzJahr();
		
		String filename = pfad+datei;
		
		if (filename.endsWith("."))
		{
			filename = filename + "csv";
		}
		else
		{
			String fu = filename.toUpperCase();
			
			if (!fu.endsWith(".CSV"))
			{
				filename = filename + ".csv";
			}
		}

		File f = new File(filename);
		
		if (f.exists())
		{
			File backup = new File(filename + ".bak");
			
			if (backup.exists()) backup.delete();
			
			f.renameTo(backup);
			
			f = new File(filename);
		}
		
		try
		{
			out = new BufferedWriter(new FileWriter(f));
			
			out.write("\"Aktienname\";\"St\u00fcck\";\"Kaufkurs\";\"akt. Kurs\";;\"akt. Wert\";\"Differenz\";\"%absolut\";\"%Jahr\";\"Kaufdatum\";\"ISIN\";\"WKN\";\"B\u00f6rse\"");
			out.newLine();

			for (int i = 0; i < getAnzahlAktien(); i++)
			{
				getAktieNr(i).saveCSV(out,kurz,prozJahr);
			}
			
			out.write("\"W\u00e4hrung: " + Waehrungen.getKuerzel(Waehrungen.getListenWaehrung()) + "\"");
			out.newLine();
			
			out.flush();
		}
		catch (IOException e) {}
		finally
		{
			try
			{
				if (out != null) out.close();
			}
			catch (IOException e) {}
			
			out = null;
		}
		
		if (SysUtil.isAMac())
		{
			try
			{
				MRJFileUtils.setFileTypeAndCreator(f,new MRJOSType("TEXT"),new MRJOSType("XCEL"));
			}
			catch (Exception e) {}
		}
	}
}



public void waitProgress() {

	if (progressCanvas != null)
	{
		progressCanvas.setWaiting();
	}
}



public void resetProgress() {

	if (progressCanvas != null)
	{
		progressCanvas.reset();
	}
}



public boolean canCancel() {
	/* Sicherheitsabfrage */
	return true;
}



public boolean canOK() {

	return false;
}



public boolean main() {
	/* #Schl�ssel */

	char[] k = AktienMan.properties.getString("Key.3").toCharArray();

	if (!AktienMan.properties.getString("Key.1").equalsIgnoreCase("AMD"))
	{
		return false;
	}

	int valid = 3;
	
	if (k.length != 4)
	{
		return false;
	}

	long l = 0L;
	try
	{
		l = Long.parseLong(AktienMan.properties.getString("Key.2"));
	}
	catch (NumberFormatException e)
	{
		return false;
	}
	
	if (k[0] == ((char) ((l * 31L + 1L) % 26L + 65))) valid--;      /* 1 */
	if (k[1] == ((char) ((l % 11L) * 2 + 65))) valid--;             /* 2 */
	if (k[2] == ((char) ((l % 9L) * 3 + (l+1) % 2L + 65))) valid--; /* 3 */
	if (k[3] == ((char) (l % 13 + l % 11 + l % 3 + 65))) valid--;   /* 4 */

	return (valid < 0);
}



public boolean mainr()
{
	if (main())
	{
		long l = 0L;
		try
		{
			l = Long.parseLong(AktienMan.properties.getString("Key.2"));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		boolean v = AktienMan.url.isValidNr(l);
		
		if (!v)
		{
			AktienMan.main(!v);
		}
		
		return v;
	}
	
	return true;
}



private void saveAndExit() {

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



public void closed() {

	saveAndExit();
}



public void componentResized(ComponentEvent e) {

	pane.paintAll(getGraphics());
}



public void componentMoved(ComponentEvent e) {}
public void componentHidden(ComponentEvent e) {}
public void componentShown(ComponentEvent e) {}

}
