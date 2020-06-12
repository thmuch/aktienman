/**
 @author Thomas Much
 @version 2000-03-14
*/

import java.awt.*;
import java.io.*;



public final class Portfolios {

public static final int INDEX_NONE = -1;

private static final String NAME_DEFAULT = "Standard";
private static final int NAME_MAXLEN = 27;

private static final int INDEX_DEFAULT = 0;
private static final int INDEX_OFFSET  = 2;

private static Menu menu      = new Menu("\u00d6ffnen");
private static Menu menuCopy  = new Menu("Kopieren nach");
private static Menu menuMove  = new Menu("Verschieben nach");
private static Menu popupCopy = new Menu("Kopieren nach");
private static Menu popupMove = new Menu("Verschieben nach");

private static boolean menuEnabled = false;

private static int index = INDEX_DEFAULT;
private static boolean indexValid = false;

private static String[] dateien = null;
private static Hauptdialog hauptdialog = null;




public synchronized static Menu getMenu(Hauptdialog hd) {

	menu.setEnabled(false);
	
	hauptdialog = hd;
	
	return menu;
}



public synchronized static Menu getMenuCopy(Hauptdialog hd) {

	menuCopy.setEnabled(false);
	
	hauptdialog = hd;
	
	return menuCopy;
}



public synchronized static Menu getMenuMove(Hauptdialog hd) {

	menuMove.setEnabled(false);
	
	hauptdialog = hd;
	
	return menuMove;
}



public synchronized static Menu getPopupCopy(Hauptdialog hd) {

	popupCopy.setEnabled(false);
	
	hauptdialog = hd;
	
	return popupCopy;
}



public synchronized static Menu getPopupMove(Hauptdialog hd) {

	popupMove.setEnabled(false);
	
	hauptdialog = hd;
	
	return popupMove;
}



public static void enableMoveCopyMenus() {

	menuEnabled = true;
	
	checkMoveCopyMenus();
}



public static void disableMoveCopyMenus() {

	menuEnabled = false;
	
	checkMoveCopyMenus();
}



private static void checkMoveCopyMenus() {

	if (menuEnabled)
	{
		menuCopy.setEnabled(true);
		menuMove.setEnabled(true);
	}
	else
	{
		menuCopy.setEnabled(false);
		menuMove.setEnabled(false);
	}
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



private synchronized static void updateCopyMove() {

	menuCopy.setEnabled(false);
	menuMove.setEnabled(false);
	popupCopy.setEnabled(false);
	popupMove.setEnabled(false);

	menuCopy.removeAll();
	menuMove.removeAll();
	popupCopy.removeAll();
	popupMove.removeAll();
	
	if (getAnzahlDateien() > 0)
	{
		CheckboxMenuItem mi;

		boolean one = false;
		
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			int actIdx = INDEX_OFFSET + i;
			
			if (getIndex() != actIdx)
			{
				mi = new CheckboxMenuItem(dateien[i]);
				mi.addItemListener(new PortfolioCopyMoveListener(actIdx,hauptdialog,false));
				menuCopy.add(mi);

				mi = new CheckboxMenuItem(dateien[i]);
				mi.addItemListener(new PortfolioCopyMoveListener(actIdx,hauptdialog,false));
				popupCopy.add(mi);

				mi = new CheckboxMenuItem(dateien[i]);
				mi.addItemListener(new PortfolioCopyMoveListener(actIdx,hauptdialog,true));
				menuMove.add(mi);

				mi = new CheckboxMenuItem(dateien[i]);
				mi.addItemListener(new PortfolioCopyMoveListener(actIdx,hauptdialog,true));
				popupMove.add(mi);

				one = true;
			}
		}

		if (one)
		{
			popupCopy.setEnabled(true);
			popupMove.setEnabled(true);

			checkMoveCopyMenus();
		}
	}
}



public synchronized static Choice getChoiceMove() {

	Choice choice = new Choice();

	for (int i = 0; i < getAnzahlDateien(); i++)
	{
		if (getIndex() != INDEX_OFFSET + i)
		{
			choice.add(dateien[i]);
		}
	}

	return choice;
}



private synchronized static int getAnzahlDateien() {

	return (dateien == null) ? 0 : dateien.length;
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

	/* selection sort */

	int size = getAnzahlDateien();

	for (int i = 0; i < size - 1; i++)
	{
		int min = i;
		String minval = dateien[min].trim().toUpperCase();
		
		for (int j = i+1; j < size; j++)
		{
			if (minval.compareTo(dateien[j].trim().toUpperCase()) > 0)
			{
				min = j;
				minval = dateien[min].trim().toUpperCase();
			}
		}
		
		if (min != i)
		{
			String temp = dateien[i];
			dateien[i] = dateien[min];
			dateien[min] = temp;
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
				hauptdialog.resetProgress();
				hauptdialog.loadPortfolio(doSave);
			}
		}

		updateCopyMove();
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

	int idx = getIndexByName(name);
	
	if (idx == INDEX_NONE)
	{
		setIndex(INDEX_DEFAULT);
	}
	else
	{
		setIndex(idx);
	}
}



public synchronized static void changeIndexByName(String name) {

	int idx = getIndexByName(name);
	
	if (idx == INDEX_NONE)
	{
		setIndex(INDEX_DEFAULT,false,false);
	}
	else
	{
		setIndex(idx,false,false);
	}
}



public synchronized static int getIndexByName(String name) {

	if (name.length() > 0)
	{
		for (int i = 0; i < getAnzahlDateien(); i++)
		{
			if (name.equalsIgnoreCase(dateien[i]))
			{
				return INDEX_OFFSET + i;
			}
		}
	}
	
	return INDEX_NONE;
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



public synchronized static String getFileByIndex(int idx) {

	if (idx == INDEX_DEFAULT)
	{
		return FileUtil.getDefaultPortfolioFile();
	}
	else
	{
		return FileUtil.getAMDirectory(true) + getName(idx) + FileUtil.EXT_PORTFOLIO;
	}
}



public synchronized static String getCurrentFile() {

	return getFileByIndex(getIndex());
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
