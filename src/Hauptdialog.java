/**
 @author Thomas Much
 @version 1999-03-14
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.apple.mrj.*;



public final class Hauptdialog extends AFrame implements ComponentListener,MRJAboutHandler {

private static final String FENSTERTITEL = AktienMan.AMNAME+" "+AktienMan.AMVERSION;

public static final int MINBREITE = 620;
public static final int MINHOEHE  = 300;

public Window backWindow;

private int locked = 0;
private boolean abenabled = false;

private BenutzerListe benutzerliste;
private Button buttonVerkaufen,buttonAendern;
private Button buttonSpeichern,buttonMaxkurs,buttonInfo;
private Button buttonAktFSE,buttonAktXetra,buttonAktualisieren;
private ScrollPane pane;
private Panel panelText,panelGewinn;
private Listenbereich panelListe;
private PopupMenu aktienpopup;
private MenuItem menuSave,menuVerkaufen,menuAendern,menuLoeschen,menuInfo,menuMaxkurs;
private MenuItem popVerkaufen,popMaxkurs,pofoRename,pofoDelete;
private ChartMenu menuChart,popChart;
private Choice chErloes,buttonChart,lwaehrung;
private int popX,popY;
private BALabel popParent;



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
	
	MRJApplicationUtils.registerAboutHandler(this);
}


public void handleAbout() {
	callAbout();
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
	
	buttonAktualisieren = new Button(" Aktualisieren! ");
	buttonAktFSE = new Button(" Akt. FSE ");
	buttonAktXetra = new Button(" Akt. Xetra ");
	Button buttonKamera = new Button(" DAX-Kamera ");

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
	
	constrain(panelOben,buttonAktualisieren,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,1.0,0.0,0,0,0,10);
	constrain(panelOben,buttonAktFSE,1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,0.7,0.0,0,0,0,10);
	constrain(panelOben,buttonAktXetra,2,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,0.7,0.0,0,0,0,10);
	constrain(panelOben,buttonKamera,3,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTH,0.4,0.0,0,5,0,0);

	constrain(panelULinks,new Label("W\u00e4hrung:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	lwaehrung = AktienMan.waehrungen.getChoice(true);
	lwaehrung.select(Waehrungen.getListenWaehrung());
	lwaehrung.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			waehrungWechseln();
		}
	});
	constrain(panelULinks,lwaehrung,1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,4,0,0);

	buttonSpeichern = new Button(" Liste exportieren... ");
	buttonSpeichern.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichern();
		}
	});
	constrain(panelULinks,buttonSpeichern,0,1,2,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHWEST,1.0,0.0,4,0,0,0);
	
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

	buttonInfo = new Button(" Aktie Info ");
	buttonInfo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieInfo();
		}
	});

	setChartChoice(true);
	
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
	constrain(this,pane,0,1,3,1,GridBagConstraints.BOTH,GridBagConstraints.CENTER,1.0,1.0,5,10,5,10);

	constrain(this,panelText,0,2,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,10,0,10);
	constrain(this,panelGewinn,0,3,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,5,10);

	constrain(this,panelULinks,0,4,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHWEST,0.0,0.0,5,10,15,5);
	constrain(this,panelUMitte,1,4,2,1,GridBagConstraints.NONE,GridBagConstraints.SOUTH,0.0,0.0,5,5,15,10);

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

	mi = new MenuItem("Aktualisieren");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSelektierteAktieAktualisieren();
		}
	});
	aktienpopup.add(mi);
	
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

	Menu fileMenu = new Menu(Lang.getFileMenuTitle(),true);
	menubar.add(fileMenu);
	
	Menu amMenu = new Menu(Lang.EDITMENUTITLE,true);
	menubar.add(amMenu);
	
	Menu aktMenu = new Menu("Aktualisieren",true);
	menubar.add(aktMenu);

	Menu aktieMenu = new Menu("Aktie",true);
	menubar.add(aktieMenu);
	
	Menu pofoMenu = new Menu("Portfolio",true);
	menubar.add(pofoMenu);

	menuSave = new MenuItem("Liste exportieren...",new MenuShortcut(KeyEvent.VK_E));
	menuSave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeSpeichern();
		}
	});
	fileMenu.add(menuSave);
	
	fileMenu.addSeparator();

	mi = new MenuItem("Beenden",new MenuShortcut(KeyEvent.VK_Q));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	fileMenu.add(mi);
	
	mi = new MenuItem("Aktienmen\u00fcs aktualisieren...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			/* #Demoversion */
			if (!main() && (AktienMan.listeDAX.getChoice(false).getItemCount() > 0)
				&& (AktienMan.listeMDAX.getChoice(false).getItemCount() > 0)
				&& (AktienMan.listeNMarkt.getChoice(false).getItemCount() > 0)
				&& (AktienMan.listeEuroSTOXX.getChoice(false).getItemCount() > 0)
				&& (AktienMan.listeAusland.getChoice(false).getItemCount() > 0))
			{
				new Warnalert(AktienMan.hauptdialog,"Die Demoversion kann die Listen nur einmal aktualisieren.");
			}
			else
			{
				new AktienAktualisieren();
			}
		}
	});
	amMenu.add(mi);

	amMenu.addSeparator();

	mi = new MenuItem("Voreinstellungen...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			callKonfiguration();
		}
	});
	amMenu.add(mi);

	if (!SysUtil.isMacOS())
	{
		amMenu.addSeparator();

		mi = new MenuItem("\u00dcber AktienMan...");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callAbout();
			}
		});
		amMenu.add(mi);
	}
	
	mi = new MenuItem("An der jeweiligen B\u00f6rse",new MenuShortcut(KeyEvent.VK_A));
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren();
		}
	});
	aktMenu.add(mi);

	aktMenu.addSeparator();
	
	mi = new MenuItem("Alle Aktien an FSE");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren("FSE");
		}
	});
	aktMenu.add(mi);

	mi = new MenuItem("Alle Aktien an Xetra");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			listeAktualisieren("ETR");
		}
	});
	aktMenu.add(mi);
	
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
	
	mi = new MenuItem("Neu...");
	mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			portfolioNeu();
		}
	});
	pofoMenu.add(mi);
	
	pofoMenu.add(Portfolios.getMenu(this));

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

	disableAktienButtons();
	Portfolios.updateMenu();
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
			boolean intraday = AktienMan.listeDAX.isMember(ba.getWKN());

			setChartChoice(intraday);
			buttonChart.setEnabled(true);
			
			menuChart.setIntraday(intraday);
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
		}
		else
		{
			buttonMaxkurs.setEnabled(true);
			menuMaxkurs.setEnabled(true);
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
		menuLoeschen.setEnabled(false);
		menuChart.setEnabled(false);
		menuMaxkurs.setEnabled(false);
		menuInfo.setEnabled(false);
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
		buttonSpeichern.setEnabled(false);
		lwaehrung.setEnabled(false);
		menuSave.setEnabled(false);
	}
	else
	{
		lwaehrung.setEnabled(true);

		if (getAnzahlAktien() > 0)
		{
			buttonSpeichern.setEnabled(true);
			menuSave.setEnabled(true);
		}
		else
		{
			buttonSpeichern.setEnabled(false);
			menuSave.setEnabled(false);

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
		if (cmp.equalsIgnoreCase(getAktieNr(i).getRequest(boerse))) return false;
	}

	return true;
}


public synchronized void listeAktualisieren(String boerse) {
	if (!KursDemon.canCallKursDemon(boerse)) listeAktualisierenAusfuehren(boerse);
}


public synchronized void listeAktualisierenAusfuehren(String boerse) {
	if (isLocked(true)) return;

	/* #Ablaufdatum */
	/* #Demoversion */
	if ((new ADate().after(new ADate(1999,6,9))) && (!main())) System.exit(0);
	
	benutzerliste.setDate(boerse);
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		getAktieNr(i).setStatusRequestingAndRepaint();
	}

	/* #Ablaufdatum */
	/* #Demoversion */
	if ((new ADate().after(new ADate(1999,6,10)))
		&& (RegAM.string(AktienMan.properties.getString("Key.1"),
			AktienMan.properties.getString("Key.2"),
			AktienMan.properties.getString("Key.3")) >= 0)) return;

	KursQuelle quelle = KursQuellen.getKursQuelle();
	KursQuelle fonds = KursQuellen.getFondsQuelle();
	
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		String cmp = ba.getRequest(boerse);
		
		if (nochNichtAngefordert(cmp,i,boerse))
		{
			if (ba.isFonds())
			{
				fonds.sendRequest(cmp,ba.getWKNString(),ba.getBoerse());
			}
			else
			{
				quelle.sendRequest(cmp,ba.getWKNString(),ba.getBoerse());
			}
		}
	}
	
	quelle.flush();
	fonds.flush();

	BenutzerAktie.setLastUpdateAndRepaint(benutzerliste.getDateString());
	
	AktienMan.doOnlineChecks();
}


private synchronized void listeSelektierteAktieAktualisieren() {
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);

		if (ba.isSelected())
		{
			ba.setStatusRequestingAndRepaint();

			/* #Ablaufdatum */
			/* #Demoversion */
			if ((new ADate().after(new ADate(1999,6,9))) && (!main())) return;

			if (ba.isFonds())
			{
				KursQuellen.getFondsQuelle().sendSingleRequest(ba.getRequest(""),ba.getWKNString(),ba.getBoerse());
			}
			else
			{
				KursQuellen.getKursQuelle().sendSingleRequest(ba.getRequest(""),ba.getWKNString(),ba.getBoerse());
			}
			
			break;
		}
	}
}


public synchronized void listeNeuerAktienkurs(String wkn, String kurz, String platz,
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
		
		if (ba.isEqual(wkn,kurz,platz,compPlatz))
		{
			ba.setValues(name,kurs,kursdatum,vortageskurs,eroeffnungskurs,
							hoechstkurs,tiefstkurs,handelsvolumen,waehrung);
			valid = true;
		}
	}
	
	if (valid) listeUpdate(true,false,sofortZeichnen,false);
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
		
		if (ba.isEqual(wkn,kurz,platz,compPlatz))
		{
			ba.setValues(name,BenutzerAktie.VALUE_NA);
			valid = true;
		}
	}

	if (valid) listeUpdate(true,false,sofortZeichnen,false);
}


public synchronized void listeAnfrageFalsch(String wkn, String platz, boolean sofortZeichnen) {
	if (wkn.length() == 0) return;
	if (platz.length() == 0) return;

	boolean valid = false;
	boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);

	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		BenutzerAktie ba = getAktieNr(i);
		
		if (ba.isEqual(wkn,platz,compPlatz))
		{
			ba.setValues(BenutzerAktie.VALUE_ERROR);
			valid = true;
		}
	}
	
	if (valid) listeUpdate(true,false,sofortZeichnen,false);
}


public synchronized void listeAnfrageFehler(String request, String wkn, String platz, boolean sofortZeichnen, int nextID) {
	if (nextID == KursQuellen.QUELLE_NONE)
	{
		if (wkn.length() == 0) return;
		if (platz.length() == 0) return;

		boolean compPlatz = (benutzerliste.getFesteBoerse().length() == 0);

		for (int i = 0; i < getAnzahlAktien(); i++)
		{
			BenutzerAktie ba = getAktieNr(i);
			
			if (ba.isEqual(wkn,platz,compPlatz))
			{
				ba.setStatusErrorAndRepaint();
			}
		}
	}
	else
	{
		KursQuellen.getKursQuelle(nextID).sendSingleRequest(request,wkn,platz,sofortZeichnen,false);
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


public synchronized void listeSelect(BALabel bal, int row, int mX, int mY,
												int clicks, boolean ispop) {
	if (row >= 0)
	{
		if (isLocked(true)) return;
		
		BenutzerAktie ba = getAktieNr(row);
		
		if (ba.isSelected())
		{
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
			for (int i = 0; i < getAnzahlAktien(); i++)
			{
				getAktieNr(i).Unselect();
			}
			
			ba.Select();
			enableAktienButtons(ba);

			if (ispop)
			{
				aktienPopup(ba,bal,mX,mY);
			}
		}
	}
}


private synchronized void aktienPopup(BenutzerAktie ba, BALabel bal, int mX, int mY) {
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
		
		if (AktienMan.listeDAX.isMember(ba.getWKN()))
		{
			popChart.enableIntraday();
		}
		else
		{
			popChart.disableIntraday();
		}
	}
	else
	{
		popChart.setEnabled(false);
	}
	
	if (ba.isFonds())
	{
		popMaxkurs.setEnabled(false);
	}
	else
	{
		popMaxkurs.setEnabled(true);
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


public synchronized void listeUpdate(boolean save, boolean chgInfo, boolean sofort, boolean to00) {
	RedrawDemon.getRedrawDemon().incRedrawRequests(to00);
	
	if (save) RedrawDemon.getRedrawDemon().incSaveRequests();
	if (chgInfo) RedrawDemon.getRedrawDemon().incInfoRequests();
	
	if (sofort) RedrawDemon.getRedrawDemon().interrupt();
}


public synchronized void listeRedraw(boolean to00) {
	disableAktienButtons();
	panelListe.removeAll();
	panelText.removeAll();
	
	if (to00 || (getAnzahlAktien() < 1))
	{
		pane.setScrollPosition(new Point(0,0));
	}

	if (getAnzahlAktien() > 0)
	{
		boolean kurz = BenutzerListe.useShortNames();
		boolean steuerfrei = BenutzerListe.useSteuerfrei();

		benutzerliste.sortByName(kurz);
		
		int i, yoffs = BenutzerAktie.addHeadingsToPanel(panelListe,benutzerliste.getDateString());
		
		for (i = 0; i < getAnzahlAktien(); i++)
		{
			getAktieNr(i).addToPanel(panelListe,i+yoffs,kurz,steuerfrei);
		}
		
		BenutzerAktie.addFooterToPanel(panelListe,i+yoffs,panelText);
	}
	
	panelText.validate();
	panelListe.validate();
	pane.validate();

	if (to00) pane.setScrollPosition(new Point(0,0));

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


public synchronized void listeSelektierteAktieChart(String monate) {
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			new ComdirectChartLeser(getAktieNr(i).getRequest(""),monate).start();
			break;
		}
	}
}


public synchronized void listeSelektierteAktieIntradayChart(String boerse) {
	for (int i = 0; i < getAnzahlAktien(); i++)
	{
		if (getAktieNr(i).isSelected())
		{
			new IntradayChartLeser(boerse,getAktieNr(i)).start();
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


public synchronized void listeAktieVerkaufen(int index, long anzahl, long verkaufskurs, long gebuehren) {
	addErloes(true,anzahl * Waehrungen.exchange(verkaufskurs,Waehrungen.getVerkaufsWaehrung(),getErloesWaehrung())
						- Waehrungen.exchange(gebuehren,Waehrungen.getVerkaufsWaehrung(),getErloesWaehrung()));

	BenutzerAktie ba = getAktieNr(index);
	
	if (ba.getStueckzahl() == anzahl)
	{
		listeAktieLoeschen(index);
	}
	else
	{
		ba.decStueckzahl(anzahl);

		listeUpdate(true,false,true,false);
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

	/* falls zu groß: verkleinern oder mehrere Seiten */
	panelListe.printAll(page);
	
	page.dispose();
	job.end();
}


public synchronized void listeSpeichern() {
	if (isLocked(true)) return;

	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+"HTML-Datei exportieren...",FileDialog.SAVE);

	fd.setFile("Meine Aktien.html");
	fd.show();
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		BufferedWriter out = null;
		
		MRJFileUtils.setDefaultFileType(new MRJOSType("????"));
		MRJFileUtils.setDefaultFileCreator(new MRJOSType("????"));

		boolean kurz = BenutzerListe.useShortNames();
		boolean steuerfrei = BenutzerListe.useSteuerfrei();
		
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
			out = new BufferedWriter(new FileWriter(f));

			String nachname = AktienMan.properties.getString("Key.Nachname");
			String vorname = AktienMan.properties.getString("Key.Vorname");
			
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
			out.write("</HEAD>");
			out.newLine();
			out.write("<BODY>");
			out.newLine();
			out.newLine();
			out.write("<TABLE BORDER>");
			out.newLine();
			out.newLine();
			
			BenutzerAktie.saveHeaderHTML(out,benutzerliste.getDateString());

			for (int i = 0; i < getAnzahlAktien(); i++)
			{
				getAktieNr(i).saveHTML(out,kurz,steuerfrei);
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
		
		try
		{
			MRJFileUtils.setFileTypeAndCreator(f,new MRJOSType("TEXT"),new MRJOSType("MOSS"));
			/* IE+CAB berücksichtigen */
		}
		catch (Exception e) {}
		
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


public boolean canCancel() {
	/* Sicherheitsabfrage */
	return true;
}


public boolean canOK() {
	return false;
}


public boolean main() {
	/* #Schlüssel */

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
