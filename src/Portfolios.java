/**
 @author Thomas Much
 @version 1999-02-08
*/

import java.awt.*;
import java.io.*;



public final class Portfolios {

private static final String NAME_DEFAULT = "Standard";
private static final int NAME_MAXLEN = 27;

private static final int INDEX_DEFAULT = 0;
private static final int INDEX_OFFSET  = 2;

private static Menu menu = new Menu("\u00d6ffnen");
private static int index = INDEX_DEFAULT;
private static boolean indexValid = false;

private static String[] dateien = null;
private static Hauptdialog hauptdialog = null;



public synchronized static Menu getMenu(Hauptdialog hd) {
	menu.setEnabled(false);
	
	hauptdialog = hd;
	
	return menu;
}


public synchronized static void updateMenu(boolean chgIndex) {
	menu.setEnabled(false);

	menu.removeAll();

	CheckboxMenuItem mi = new CheckboxMenuItem(NAME_DEFAULT);
	mi.addItemListener(new PortfolioListener(INDEX_DEFAULT));
	menu.add(mi);
	
	leseDateien();
	
	if (!indexValid)
	{
		initIndex();
		indexValid = true;
	}
	
	if (getAnzahlDateien() > 0)
	{
		menu.addSeparator();
		
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			mi = new CheckboxMenuItem(dateien[i]);
			mi.addItemListener(new PortfolioListener(INDEX_OFFSET + i));
			menu.add(mi);
		}
	}
	
	if (chgIndex) setIndex(getIndex(),false,true);

	menu.setEnabled(true);
}


public synchronized static void updateMenu() {
	updateMenu(true);
}


private synchronized static int getAnzahlDateien() {
	if (dateien == null) return 0;
	
	return dateien.length;
}


private synchronized static void leseDateien() {
	File amd = new File(FileUtil.getAMDirectory(false));
	
	dateien = amd.list(new PortfolioFilter(FileUtil.EXT_PORTFOLIO));
	
	if (getAnzahlDateien() > 0)
	{
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			dateien[i] = dateien[i].substring(0,dateien[i].length() - FileUtil.EXT_PORTFOLIO.length());
		}
		
		sortiereDateien();
	}
}


private synchronized static void sortiereDateien() {
	if (getAnzahlDateien() > 0)
	{
		for (int i = getAnzahlDateien(); --i >= 0; )
		{
			boolean swapped = false;
			
			for (int j = 0; j<i; j++)
			{
				if (dateien[j].trim().toUpperCase().compareTo(dateien[j+1].trim().toUpperCase()) > 0)
				{
					String temp = dateien[j];
					dateien[j] = dateien[j+1];
					dateien[j+1] = temp;
					
					swapped = true;
				}
			}
			
			if (!swapped) return;
		}
	}
}


private synchronized static void initIndex() {
	String pofoname = AktienMan.properties.getString("Portfolio.StartName").trim();
	
	index = INDEX_DEFAULT;
	
	if (pofoname.length() > 0)
	{
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			if (pofoname.equalsIgnoreCase(dateien[i]))
			{
				index = INDEX_OFFSET + i;
				break;
			}
		}
	}
}


private synchronized static void setIndex(int i, boolean doSave, boolean doLoad) {
	if ((i < INDEX_OFFSET) || (i >= INDEX_OFFSET + getAnzahlDateien()))
	{
		i = INDEX_DEFAULT;
	}

	((CheckboxMenuItem)menu.getItem(i)).setState(false);
	
	/* #Demoversion */
	if (!hauptdialog.main())
	{
		if (i != INDEX_DEFAULT)
		{
			i = INDEX_DEFAULT;
			new Warnalert(hauptdialog,"Mit der Demoversion k\u00f6nnen Sie nur das Standard-Portfolio verwalten.");
		}
	}

	if (!hauptdialog.isLocked(true))
	{
		if (getIndex() != i)
		{
			((CheckboxMenuItem)menu.getItem(getIndex())).setState(false);

			index = i;
			
			if (doLoad)
			{
				hauptdialog.loadPortfolio(doSave);
			}
		}
	}

	((CheckboxMenuItem)menu.getItem(getIndex())).setState(true);
	
	hauptdialog.checkPortfolioMenu();
	hauptdialog.setPortfolioTitle(getCurrentWindowTitle());
	
	AktienMan.properties.setString("Portfolio.StartName",getName(getIndex()));
}


public synchronized static void setIndex(int i) {
	setIndex(i,true,true);
}


public synchronized static void setDefaultIndexDontSave() {
	setIndex(INDEX_DEFAULT,false,true);
}


public synchronized static void setIndexByName(String name) {
	if (name.length() > 0)
	{
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			if (name.equalsIgnoreCase(dateien[i]))
			{
				setIndex(INDEX_OFFSET + i);
				return;
			}
		}
	}
	
	setIndex(INDEX_DEFAULT);
}


public synchronized static void changeIndexByName(String name) {
	if (name.length() > 0)
	{
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			if (name.equalsIgnoreCase(dateien[i]))
			{
				setIndex(INDEX_OFFSET + i,false,false);
				return;
			}
		}
	}

	setIndex(INDEX_DEFAULT,false,false);
}


private synchronized static int getIndex() {
	return index;
}


public synchronized static boolean isDefault() {
	return (getIndex() == INDEX_DEFAULT);
}


private synchronized static String getName(int i) {
	if ((i >= INDEX_OFFSET) && (i < INDEX_OFFSET + getAnzahlDateien()))
	{
		return dateien[i - INDEX_OFFSET].trim();
	}
	else
	{
		return "";
	}
}


public static String fixFilename(String name) {
	name = name.trim();
	
	if (name.length() > NAME_MAXLEN)
	{
		name = name.substring(0,NAME_MAXLEN);
	}
	
	name = name.replace(';','_');
	name = name.replace(':','_');
	name = name.replace(',','_');
	name = name.replace('/','_');
	name = name.replace('\\','_');
	name = name.replace('|','_');
	name = name.replace('"','_');
	name = name.replace('*','_');
	name = name.replace('?','_');
	
	if (name.length() > 0)
	{
		if (name.charAt(0) == '-')
		{
			StringBuffer sb = new StringBuffer(name);
			
			sb.setCharAt(0,'_');
			
			name = sb.toString();
		}
	}
	
	return name;
}


public static String getNewFile(String name) {
	return FileUtil.getAMDirectory(true) + name + FileUtil.EXT_PORTFOLIO;
}


public synchronized static String getCurrentFile() {
	if (getIndex() == INDEX_DEFAULT)
	{
		return FileUtil.getDefaultPortfolioFile();
	}
	else
	{
		return FileUtil.getAMDirectory(true) + getName(getIndex()) + FileUtil.EXT_PORTFOLIO;
	}
}


public synchronized static String getCurrentName() {
	if (getIndex() == INDEX_DEFAULT)
	{
		return NAME_DEFAULT;
	}
	else
	{
		return getName(getIndex());
	}
}


public synchronized static String getCurrentWindowTitle() {
	if (getIndex() == INDEX_DEFAULT)
	{
		return "";
	}
	else
	{
		return " - Portfolio \"" + getName(getIndex()) + "\"";
	}
}

}
