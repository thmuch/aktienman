/**
 @author Thomas Much
 @version 2000-11-09
*/

import java.util.*;
import java.util.zip.*;
import java.awt.*;
import java.io.*;




public final class IndexQuelle {

private static final int COLUMNS = 3;

private static Panel indexPanel = null;

private static Vector canvasliste = new Vector();
private static Vector usedindices = null;

private static final int[] threadURLs = { URLs.URL_BBBINDEXD,
										  URLs.URL_BBBINDEXEU,
										  URLs.URL_BBBINDEXUS,
										  URLs.URL_BBBINDEXASIA };

private static long currentThreadID = 0L;
private static long nextThreadID    = currentThreadID + 1L;
private static Thread[] leser       = new Thread[threadURLs.length];


private static final String[] iNames = {"DAX 30",
										"DAX 100",
										"NEMAX 50",
										"MDAX",
										"SDAX",
										"EuroSTOXX50",
										"STOXX 50",
										"Dow Indust.",
										"S&P 500",
										"NASDAQ 100",
										"NYSE Comp.",
										"NIKKEI 225"};

public static final String[] iDescr = {	"DAX 30 Performanceindex",
										"DAX 100 Performanceindex",
										"NEMAX 50 Performanceindex",
										"MDAX Performanceindex",
										"SDAX Performanceindex",
										"Dow Jones EuroSTOXX 50 Return Index",
										"Dow Jones STOXX 50 Price Index",
										"Dow Jones Industrials Index",
										"S&P 500 Index",
										"NASDAQ 100 Index",
										"NYSE Composite Index",
										"NIKKEI 225 Index"};

public static final int[] iIndex = {	URLs.STR_INDEX_DAX30,
										URLs.STR_INDEX_DAX100,
										URLs.STR_INDEX_NEMAX50,
										URLs.STR_INDEX_MDAX,
										URLs.STR_INDEX_SDAX,
										URLs.STR_INDEX_EURSTOXX50,
										URLs.STR_INDEX_STOXX50,
										URLs.STR_INDEX_DOWINDUST,
										URLs.STR_INDEX_SP500,
										URLs.STR_INDEX_NASDAQ100,
										URLs.STR_INDEX_NYSECOMP,
										URLs.STR_INDEX_NIKKEI225};

private static final int[] defaultIndices = {0, 2, 7, 8, 9, 11};




public synchronized static void call() {

	if (currentThreadID == nextThreadID)
	{
		for (int i = 0; i < leser.length; i++)
		{
			leser[i].interrupt();
		}
	}
	else
	{
		currentThreadID = nextThreadID;
		
		for (int i = 0; i < leser.length; i++)
		{
			leser[i] = new BBBankIndexLeser(currentThreadID,threadURLs[i]);

			leser[i].start();
		}
	}
}



public synchronized static void clearThread(long tID) {

	if (tID == nextThreadID)
	{
		for (int i = 0; i < leser.length; i++)
		{
			leser[i] = null;
		}
		
		nextThreadID++;
	}
}



private static synchronized void addCanvas(IndexCanvas canvas) {

	canvasliste.addElement(canvas);
}



private synchronized static void setPanel(Panel panel) {

	indexPanel = panel;
}



public static synchronized void checkIndex(String symbol, long punkte, long vortag, String datum) {

	for (int i = 0; i < canvasliste.size(); i++)
	{
		IndexCanvas ic = (IndexCanvas)canvasliste.elementAt(i);
		
		if (ic.hasSymbol(symbol))
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



private synchronized static int index2Array(int index) {

	for (int i = 0; i < iIndex.length; i++)
	{
		if (iIndex[i] == index)
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
		
		int aidx = index2Array(uidx.getIndex());
		
		String[] titles = new String[rows];
		
		int col = i % COLUMNS;
		
		int curridx = col;
		
		for (int j = 0; j < rows; j++)
		{
			if (curridx < usedindices.size())
			{
				int tidx = index2Array(((UsedIndex)(usedindices.elementAt(curridx))).getIndex());
			
				titles[j] = iNames[tidx];
			}
		
			curridx += COLUMNS;
		}
		
		IndexCanvas ic = new IndexCanvas(iNames[aidx],iIndex[aidx],titles,"99999,99",uidx.getPunkte(),uidx.getVortag(),uidx.getDatum());

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
		GZIPInputStream gzis = new GZIPInputStream(fis);
		in = new ObjectInputStream(fis);
		usedindices = (Vector)in.readObject();
	}
	catch (ClassNotFoundException e)
	{
		System.out.println("Gespeicherte Indizes fehlerhaft.");
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
		
			in = null;
		}
		
		if (usedindices == null)
		{
			usedindices = new Vector();

			for (int i = 0; i < defaultIndices.length; i++)
			{
				addIndex(iIndex[defaultIndices[i]]);
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
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		out = new ObjectOutputStream(fos);
		out.writeObject(usedindices);
		out.flush();
	}
	catch (Exception e)
	{
		System.out.println("Fehler beim Speichern der Indizes.");
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
		
			out = null;
		}
	}
}

}
