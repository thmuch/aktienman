/**
 @author Thomas Much
 @version 1999-01-21
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;



public final class AktienAktualisieren extends AFrame {

public static final int INDEX_DAX       = 0;
public static final int INDEX_MDAX      = 1;
public static final int INDEX_NMARKT    = 2;
public static final int INDEX_EUROSTOXX = 3;
public static final int INDEX_AUSLAND   = 4;

private static final int STATUS_START    =  0;
private static final int STATUS_FINISHED = -1;
private static final int STATUS_ERROR    = -2;

private String[] titel = {"DAX","MDAX","Neuer Markt","EuroSTOXX50","Ausland"};
private int[] count;
private Panel panelListe;
private Button buttonOK;
private int buttonCount;

private boolean doSave = false;



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
	
	for (int i=0; i<5; i++) count[i] = STATUS_START;
	
	fillListenPanel(false);
	
	buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0)
			{
				new AktienPofosErzeugen();
			}
			
			doCancel();
		}
	});
	buttonOK.setEnabled(false);
	
	constrain(this,new Label("Aktienmen\u00fcs aktualiseren:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelListe,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,0,10);
	constrain(this,buttonOK,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


private void startThreads() {
	buttonCount = 5;
	
	new AktienlistenLeser("DAX",URLs.LISTE_DAX30,"DAX",this,INDEX_DAX).start();
	new AktienlistenLeser("Neuer Markt",URLs.LISTE_NMARKT,"NEUER MARKT",this,INDEX_NMARKT).start();
	new AktienlistenLeser("EuroSTOXX50",URLs.LISTE_EURO50,"STOXX",this,INDEX_EUROSTOXX).start();
	new AktienlistenLeser("Ausland",URLs.LISTE_AUSLAND,"",this,INDEX_AUSLAND).start();
}


private synchronized void fillListenPanel(boolean draw) {
	if (draw) panelListe.removeAll();
	
	for (int i = 0; i < 5; i++)
	{
		constrain(panelListe,new Label(titel[i]+":"),0,i,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

		String s;
		
		if (count[i] >= STATUS_START)
		{
			s = new Integer(count[i]).toString();
		}
		else if (count[i] == STATUS_FINISHED)
		{
			s = "Fertig.";
		}
		else
		{
			s = "<offline>";
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
	count[index] = STATUS_FINISHED;
	fillListenPanel(true);
	doSave = true;
	
	if (--buttonCount == 0)
	{
		savePopups();
		buttonOK.setEnabled(true);
	}
}


public synchronized void setError(int index) {
	count[index] = STATUS_ERROR;
	fillListenPanel(true);
	
	if (--buttonCount == 0)
	{
		if (doSave)
		{
			savePopups();
		}
		
		buttonOK.setEnabled(true);
	}
	
	if (index == INDEX_DAX)
	{
		setError(INDEX_MDAX);
	}
}


private synchronized void savePopups() {
	ObjectOutputStream out = null;

	try
	{
		FileOutputStream fos = new FileOutputStream(FileUtil.getPopupFile());
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		out = new ObjectOutputStream(fos);
		out.writeObject(AktienMan.listeDAX);
		out.writeObject(AktienMan.listeMDAX);
		out.writeObject(AktienMan.listeNMarkt);
		out.writeObject(AktienMan.listeEuroSTOXX);
		out.writeObject(AktienMan.listeAusland);
		out.flush();
	}
	catch (IOException e)
	{
		System.out.println("Fehler beim Speichern der Aktienpopups.");
	}
	finally
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e) {}
		
			out = null;
		}
	}
}


public synchronized static void loadPopups() {
	ObjectInputStream in = null;

	try
	{
		FileInputStream fis = new FileInputStream(FileUtil.getPopupFile());
		GZIPInputStream gzis = new GZIPInputStream(fis);
		in = new ObjectInputStream(fis);
		AktienMan.listeDAX = (Aktienliste)in.readObject();
		AktienMan.listeMDAX = (Aktienliste)in.readObject();
		AktienMan.listeNMarkt = (Aktienliste)in.readObject();
		AktienMan.listeEuroSTOXX = (Aktienliste)in.readObject();
		AktienMan.listeAusland = (Aktienliste)in.readObject();
	}
	catch (IOException e) {}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Aktienpopups fehlerhaft.");
	}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e) {}
		
			in = null;
		}
	}
}

}
