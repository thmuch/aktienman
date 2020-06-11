/**
 @author Thomas Much
 @version 1998-10-27
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;



public class AktienAktualisieren extends AFrame {

private String[] titel = {"DAX","MDAX","Neuer Markt","EuroSTOXX50","Ausland"};
private int[] count;
private Panel panelListe;
private Button buttonOK;
private int buttonCount;



public AktienAktualisieren() {
	super(AktienMan.AMNAME);

	setupElements2();
	
	pack();
	setupSize();
	show();
	
	startThreads();
}


public void setupElements() {
	setLayout(gridbag);
}


public void display() {}


public synchronized void setupElements2() {
	panelListe = new Panel(gridbag);
	
	count = new int[5];
	
	for (int i=0; i<5; i++) count[i] = 0;
	
	fillListenPanel(false);
	
	buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	buttonOK.setEnabled(false);
	
	constrain(this,new Label("Aktienlisten aktualiseren:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelListe,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,0,10);
	constrain(this,buttonOK,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


private void startThreads() {
	buttonCount = 5;
	
	new AktienlistenLeser("DAX",URLs.LISTE_DAX30,"DAX",this,0,true).start();
	new AktienlistenLeser("Neuer Markt",URLs.LISTE_NMARKT,"NEUER MARKT",this,2,false).start();
	new AktienlistenLeser("EuroSTOXX50",URLs.LISTE_EURO50,"STOXX",this,3,false).start();
	new AktienlistenLeser("Ausland",URLs.LISTE_AUSLAND,"",this,4,false).start();
}


private synchronized void fillListenPanel(boolean draw) {
	if (draw) panelListe.removeAll();
	
	for (int i = 0; i < 5; i++)
	{
		constrain(panelListe,new Label(titel[i]+":"),0,i,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

		String s;
		
		if (count[i] < 0)
		{
			s = "Fertig.";
		}
		else
		{
			s = new Integer(count[i]).toString();
		}
		
		constrain(panelListe,new Label(s),1,i,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.EAST,1.0,0.0,0,10,0,0);
	}
	
	if (draw) panelListe.paintAll(getGraphics());
}


public synchronized void incCount(int index) {
	count[index]++;
	if ((count[index] % 20)== 0) fillListenPanel(true);
}


public synchronized void finishCounting(int index) {
	count[index] = -1;
	fillListenPanel(true);
	
	if (--buttonCount == 0)
	{
		savePopups();
		buttonOK.setEnabled(true);
	}
}


private synchronized void savePopups() {
	String folder = System.getProperty("user.home");
	String filesep = System.getProperty("file.separator");

	try
	{
		FileOutputStream fos = new FileOutputStream(folder+filesep+AktienMan.getFilenamePopups());
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(AktienMan.listeDAX);
		out.writeObject(AktienMan.listeMDAX);
		out.writeObject(AktienMan.listeNMarkt);
		out.writeObject(AktienMan.listeEuroSTOXX);
		out.writeObject(AktienMan.listeAusland);
		out.flush();
		out.close();
	}
	catch (IOException e)
	{
		System.out.println("Fehler beim Speichern der Aktienpopups.");
	}
}


public static void loadPopups() {
	String folder = System.getProperty("user.home");
	String filesep = System.getProperty("file.separator");

	try
	{
		FileInputStream fis = new FileInputStream(folder+filesep+AktienMan.getFilenamePopups());
		GZIPInputStream gzis = new GZIPInputStream(fis);
		ObjectInputStream in = new ObjectInputStream(fis);
		AktienMan.listeDAX = (Aktienliste)in.readObject();
		AktienMan.listeMDAX = (Aktienliste)in.readObject();
		AktienMan.listeNMarkt = (Aktienliste)in.readObject();
		AktienMan.listeEuroSTOXX = (Aktienliste)in.readObject();
		AktienMan.listeAusland = (Aktienliste)in.readObject();
		in.close();
	}
	catch (IOException e) {}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Aktienpopups fehlerhaft.");
	}
}

}
