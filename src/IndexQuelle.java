/**
 @author Thomas Much
 @version 2003-03-27

 2003-03-27
    TecDAX und NIKKEI225
 2003-02-21
 	loadValues/saveValues serialisieren nun sicherer
*/

import java.util.*;
import java.util.zip.*;
import java.awt.*;
import java.io.*;




public final class IndexQuelle {

public static final int ID_NONE        = -1;
public static final int ID_DAX30       =  0;
public static final int ID_DAX100      =  1;
public static final int ID_TECDAX      =  2;
public static final int ID_NEMAX50     =  3;
public static final int ID_MDAX        =  4;
public static final int ID_SDAX        =  5;
public static final int ID_EUROSTOXX50 =  6;
public static final int ID_STOXX50     =  7;
public static final int ID_DOWJONES    =  8;
public static final int ID_SP500       =  9;
public static final int ID_NASDAQ100   = 10;
public static final int ID_NASDAQ      = 11;
public static final int ID_NIKKEI      = 12;
public static final int ID_NIKKEI225   = 13;
public static final int COUNT          = ID_NIKKEI225 + 1;


private static final int COLUMNS = 3;

private static Panel indexPanel = null;

private static Vector canvasliste = new Vector();
private static Vector usedindices = null;

private static long currentThreadID = 0L;
private static long nextThreadID    = currentThreadID + 1L;
private static Thread leser         = null;

private static final String[] iNames = { "DAX 30",
										 "DAX 100",
										 "TecDAX",
										 "NEMAX 50",
										 "MDAX",
										 "SDAX",
										 "EuroSTOXX 50",
										 "STOXX 50",
										 "Dow Jones",
										 "S&P 500",
										 "NASDAQ 100",
										 "NASDAQ",
										 "NIKKEI",
										 "NIKKEI 225" };

public static final String[] iDescr = {	"DAX/DAX 30 Index",
										"HDAX/DAX 100 Index",
										"TecDAX Index",
										"NEMAX 50 Index",
										"MDAX Index",
										"SDAX Index",
										"Dow Jones Euro STOXX 50 Index",
										"Dow Jones STOXX 50 Index",
										"Dow Jones Industrials Index",
										"S&P 500 Index",
										"NASDAQ 100 Index",
										"NASDAQ/NMS Composite Index",
										"NIKKEI/ISE 50 Index",
										"NIKKEI 225 Index" };

private static final int[] defaultIndices = { ID_DAX30, ID_TECDAX, ID_DOWJONES, ID_SP500, ID_NASDAQ, ID_NIKKEI225 };




public synchronized static void call() {

	if (currentThreadID == nextThreadID)
	{
		leser.interrupt();
	}
	else
	{
		currentThreadID = nextThreadID;
		
		Runnable r = new IndexLeser(currentThreadID);
		
		leser = new Thread(r);
		
		leser.start();
	}
}



public synchronized static void clearThread(long tID) {

	if (tID == nextThreadID)
	{
		leser = null;
		
		nextThreadID++;
	}
}



private static synchronized void addCanvas(IndexCanvas canvas) {

	canvasliste.addElement(canvas);
}



private synchronized static void setPanel(Panel panel) {

	indexPanel = panel;
}



public static synchronized void checkIndex(int index, long punkte, long vortag, String datum) {

	for (int i = 0; i < canvasliste.size(); i++)
	{
		IndexCanvas ic = (IndexCanvas)canvasliste.elementAt(i);
		
		if (ic.getIndex() == index)
		{
			ic.setValues(punkte,vortag,datum);
			ic.repaint();
			
			int uidx = index2Used(ic.getIndex());
			
			if (uidx >= 0)
			{
				((UsedIndex)(usedindices.elementAt(uidx))).setValues(punkte,vortag,datum);
			}
		}
	}
}



public synchronized static void updateFinished() {

	if (indexPanel != null)
	{
		indexPanel.validate();
		indexPanel.repaint();
	}
	
	saveValues();
}



public static boolean autoIndexOn() {

	return AktienMan.properties.getBoolean("Konfig.Index",false);
}



public synchronized static void addIndex(int index) {

	if (isIndexOn(index)) return;
	
	usedindices.addElement(new UsedIndex(index));
}



public synchronized static void removeIndex(int index) {

	int idx = index2Used(index);
	
	if (idx >= 0)
	{
		usedindices.removeElementAt(idx);
	}
}



public synchronized static boolean isIndexOn(int index) {

	return (index2Used(index) >= 0);
}



private synchronized static int index2Used(int index) {

	for (int i = 0; i < usedindices.size(); i++)
	{
		if (((UsedIndex)(usedindices.elementAt(i))).getIndex() == index)
		{
			return i;
		}
	}

	return -1;
}



public synchronized static void addIndices(Panel panel) {
	
	setPanel(panel);

	loadValues();
	
	int rows = (usedindices.size() + COLUMNS - 1) / COLUMNS;
	
	for (int i = 0; i < usedindices.size(); i++)
	{
		UsedIndex uidx = (UsedIndex)(usedindices.elementAt(i));
		
		int aidx = uidx.getIndex();
		
		String[] titles = new String[rows];
		
		int col = i % COLUMNS;
		
		int curridx = col;
		
		for (int j = 0; j < rows; j++)
		{
			if (curridx < usedindices.size())
			{
				int tidx = ((UsedIndex)(usedindices.elementAt(curridx))).getIndex();
			
				titles[j] = iNames[tidx];
			}
		
			curridx += COLUMNS;
		}
		
		IndexCanvas ic = new IndexCanvas(iNames[aidx],aidx,titles,"99999,99",uidx.getPunkte(),uidx.getVortag(),uidx.getDatum());

		AFrame.constrain(panel,ic,col,i / COLUMNS,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,0,0,0);
		
		addCanvas(ic);
	}
}



public synchronized static void renewIndices() {

	if (indexPanel == null) return;
	
	indexPanel.setVisible(false);

	indexPanel.removeAll();
	canvasliste = new Vector();
	
	addIndices(indexPanel);

	AktienMan.hauptdialog.addIndexPanelAndPane(true);

	indexPanel.setVisible(true);
}



public synchronized static void loadValues() {

	if (usedindices != null) return;

	ObjectInputStream in = null;

	try
	{
		FileInputStream fis = new FileInputStream(FileUtil.getIndexFile());
		in = new ObjectInputStream(new BufferedInputStream(fis));
		usedindices = (Vector)in.readObject();
		
		// Workaround, damit sichergestellt ist, dass alte Indexdateien ignoriert werden...
		// TODO: wird ŸberflŸssig, sobald die Indizes optimiert und als XML gespeichert werden!
		for (int i = 0; i < usedindices.size(); i++)
		{
			int aidx = ((UsedIndex)(usedindices.elementAt(i))).getIndex();
			
			if ((aidx < 0) || (aidx >= COUNT))
			{
				usedindices = null;
				break;
			}
		}
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
		}
		
		if (usedindices == null)
		{
			usedindices = new Vector();

			for (int i = 0; i < defaultIndices.length; i++)
			{
				addIndex(defaultIndices[i]);
			}
		}
	}
}



public synchronized static void saveValues() {

	if (usedindices == null) return;

	ObjectOutputStream out = null;

	try
	{
		FileOutputStream fos = new FileOutputStream(FileUtil.getIndexFile());
		out = new ObjectOutputStream(new BufferedOutputStream(fos));
		out.writeObject(usedindices);
		out.flush();
	}
	catch (Exception e) {}
	finally
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (Exception e) {}
		}
	}
}

}
