/**
 @author Thomas Much
 @version 2003-04-03
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;




public final class AktienAktualisieren extends AFrame {

public static final int INDEX_DAX30       = 0;
public static final int INDEX_MDAX        = 1;
public static final int INDEX_TECDAX      = 2;
public static final int INDEX_EUROSTOXX50 = 3;
public static final int INDEX_STOXX50     = 4;

private static final int STATUS_START     =  0;
private static final int STATUS_FINISHED  = -1;
private static final int STATUS_ERROR     = -2;

private static final int UPDATE = 10;

private final String[] titel = { "DAX30", "MDAX", "TecDAX", "EuroSTOXX50", "STOXX50" };

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
	setVisible(true);
	
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
	
	constrain(this,new Label("Aktienmen\u00fcs aktualisieren:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelListe,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,0,10);
	constrain(this,buttonOK,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}



private void startThreads() {

	buttonCount = 5;
	
	AktienMan.checkURLs();
	
	new AktienlistenLeser(INDEX_DAX30,this).start();
	new AktienlistenLeser(INDEX_MDAX,this).start();
	new AktienlistenLeser(INDEX_TECDAX,this).start();
	new AktienlistenLeser(INDEX_EUROSTOXX50,this).start();
	new AktienlistenLeser(INDEX_STOXX50,this).start();
}



private synchronized void fillListenPanel(boolean draw) {

	if (draw) panelListe.removeAll();
	
	for (int i = 0; i < 5; i++)
	{
		constrain(panelListe,new Label(titel[i]+":"),0,i,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

		String s;
		
		if (count[i] >= STATUS_START)
		{
			s = "" + count[i];
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

	if ((count[index] % UPDATE) == 0) fillListenPanel(true);
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
}



private synchronized void savePopups() {

	ObjectOutputStream out = null;

	try
	{
		FileOutputStream fos = new FileOutputStream(FileUtil.getPopupFile());
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		out = new ObjectOutputStream(fos);
		out.writeObject(AktienMan.listeDAX30);
		out.writeObject(AktienMan.listeMDAX);
		out.writeObject(AktienMan.listeTecDAX);
		out.writeObject(AktienMan.listeEuroSTOXX50);
		out.writeObject(AktienMan.listeSTOXX50);
		out.flush();
	}
	catch (Exception e)
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
			catch (Exception e) {}
			finally
			{
				out = null;
			}
		}
	}
}



public synchronized static void loadPopups() {

	ObjectInputStream in = null;

	try
	{
		InputStream fis;
		
		try
		{
			fis = new FileInputStream(FileUtil.getPopupFile());
		}
		catch (IOException e)
		{
			fis = ClassLoader.getSystemResourceAsStream(AktienMan.AMNAME + FileUtil.EXT_POPUP);
		}
		
		GZIPInputStream gzis = new GZIPInputStream(fis);
		in = new ObjectInputStream(fis);
		AktienMan.listeDAX30 = (Aktienliste)in.readObject();
		AktienMan.listeMDAX = (Aktienliste)in.readObject();
		AktienMan.listeTecDAX = (Aktienliste)in.readObject();
		AktienMan.listeEuroSTOXX50 = (Aktienliste)in.readObject();
		AktienMan.listeSTOXX50 = (Aktienliste)in.readObject();
	}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Aktienpopups fehlerhaft.");
	}
	catch (Exception e) {}
	finally
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e) {}
			finally
			{
				in = null;
			}
		}
	}
}

}
