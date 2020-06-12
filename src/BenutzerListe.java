/**
 @author Thomas Much
 @version 2003-01-21

 2003-01-21
 	restore lädt bevorzugt XML-Aktienlisten
 	store speichert die Aktienlisten als XML (mit besser .bak-Verwaltung)
 	store lockt nun die zu speichernde Benutzerliste
*/

import java.util.*;
import java.io.*;
import java.util.zip.*;




public final class BenutzerListe extends Vector implements Serializable {

static final long serialVersionUID = 1972011800002L;

private static final int SORT_NONE      = -1;
private static final int SORT_NAME      =  0;
private static final int SORT_ABSPERC   =  1;
private static final int SORT_ABSDIFF   =  2;
private static final int SORT_KAUFDATUM =  3;
private static final int SORT_FIXDATUM  =  4;

private ADate letzteAktualisierung = null;
private String festeBoerse = null;
private String kommentar = null;
private long verkaufserloes = 0L;
private int erloesWaehrung = Waehrungen.EUR;
private int portfoliover = 0;
private int sortBy = SORT_NAME;

private transient String portfolioFile = "";




public BenutzerListe() {

	super(50);
}



public synchronized void destroy() {

	for (int i = 0; i < size(); i++)
	{
		getAt(i).destroy();
	}
	
	removeAllElements();
}



public synchronized void setPortfolioFile(String name) {

	portfolioFile = name;
}



public synchronized String getPortfolioFile() {

	return portfolioFile;
}



public synchronized void add(BenutzerAktie ba) {

	addElement(ba);
}



public synchronized void removeAt(int index) {

	getAt(index).destroy();
	removeElementAt(index);
}



public synchronized BenutzerAktie getAt(int index) {

	return (BenutzerAktie)elementAt(index);
}



public synchronized void setDate(String boerse, String rem) {

	letzteAktualisierung = new ADate();
	festeBoerse = boerse;
	kommentar = rem;
}



public synchronized void clearDate() {

	letzteAktualisierung = null;
}



private synchronized ADate getDate() {

	return letzteAktualisierung;
}



public synchronized String getFesteBoerse() {

	return (festeBoerse == null) ? "" : festeBoerse;
}



private synchronized String getKommentar() {

	if (kommentar == null)
	{
		return "";
	}
	else if (kommentar.length() == 0)
	{
		return "";
	}
	else
	{
		return " ("+kommentar+")";
	}
}



public synchronized String getDateString() {

	String s;
	ADate d = getDate();
	
	if (d == null)
	{
		s = " Bisher noch keine Aktualisierung.";
	}
	else
	{
		s = " Letzte Aktualisierung am "+d.toString()+" um "+d.timeToString()+getKommentar()+".";
	}
	
	return s;
}



public synchronized long getErloes() {

	return verkaufserloes;
}



public synchronized void clearErloes() {

	verkaufserloes = 0L;
}



public synchronized void addToErloes(long delta) {

	verkaufserloes += delta;
}



public synchronized int getErloesWaehrung() {

	return erloesWaehrung;
}



public synchronized void erloesToWaehrung(int neueWaehrung) {

	verkaufserloes = Waehrungen.exchange(verkaufserloes,getErloesWaehrung(),neueWaehrung);
	erloesWaehrung = neueWaehrung;
}



public synchronized int getSortBy() {

	return sortBy;
}



public synchronized void setSortBy(int neu) {

	sortBy = neu;
}



public synchronized void sort(boolean kurz) {

	switch (getSortBy())
	{
	case SORT_ABSPERC:

		sortByAbsPercent();
		break;
	
	case SORT_KAUFDATUM:
	
		sortByKaufdatum();
		break;

	case SORT_FIXDATUM:
	
		sortByFixDatum();
		break;

	case SORT_ABSDIFF:
	
		sortByAbsDiff();
		break;
	
	default:

		sortByName(kurz);
	}
}



private synchronized void sortByFixDatum() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		int maxval = getAt(max).getFixedDate().getSerialDate();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getFixedDate().getSerialDate() < maxval)
			{
				max = j;
				maxval = getAt(max).getFixedDate().getSerialDate();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByKaufdatum() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		int maxval = getAt(max).getKaufdatum().getSerialDate();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getKaufdatum().getSerialDate() < maxval)
			{
				max = j;
				maxval = getAt(max).getKaufdatum().getSerialDate();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByAbsDiff() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		long maxval = getAt(max).getAbsDiff();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getAbsDiff() > maxval)
			{
				max = j;
				maxval = getAt(max).getAbsDiff();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByAbsPercent() {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int max = i;
		long maxval = getAt(max).getAbsPercent();
		
		for (int j = i+1; j < size; j++)
		{
			if (getAt(j).getAbsPercent() > maxval)
			{
				max = j;
				maxval = getAt(max).getAbsPercent();
			}
		}
		
		if (max != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(max),i);
			setElementAt(temp,max);
		}
	}
}



private synchronized void sortByName(boolean kurz) {

	/* selection sort */
	
	int size = size();

	for (int i = 0; i < size - 1; i++)
	{
		int min = i;
		String minval = getAt(min).getName(kurz).trim().toUpperCase();
		
		for (int j = i+1; j < size; j++)
		{
			if (minval.compareTo(getAt(j).getName(kurz).trim().toUpperCase()) > 0)
			{
				min = j;
				minval = getAt(min).getName(kurz).trim().toUpperCase();
			}
		}
		
		if (min != i)
		{
			BenutzerAktie temp = getAt(i);
			setElementAt(getAt(min),i);
			setElementAt(temp,min);
		}
	}
}



private void writeObject(ObjectOutputStream out) throws IOException {

	portfoliover = 0/*AktienMan.PORTFOLIOVER*/;
	out.defaultWriteObject();
}



public synchronized static boolean useShortNames() {

	return AktienMan.properties.getBoolean("Konfig.Aktiennamen.kuerzen",true);
}



public synchronized static boolean useOnlineNames() {

	return AktienMan.properties.getBoolean("Konfig.Aktiennamen",true);
}



public synchronized static boolean useSteuerfrei() {

	return AktienMan.properties.getBoolean("Konfig.Steuerfrei",true);
}



public synchronized static boolean calcProzJahr() {

	return AktienMan.properties.getBoolean("Konfig.ProzJahr",true);
}



private synchronized void saveXML(PrintWriter out) {

	out.println("<data>");

	out.println("<release>" + AktienMan.RELEASE + "</release>");
	out.println("<aktienman>" + AktienMan.AMVERSION + "</aktienman>");

	out.println("<kommentar>" + XMLUtil.escapeString(kommentar) + "</kommentar>");
	out.println("<festeBoerse>" + XMLUtil.escapeString(festeBoerse) + "</festeBoerse>");
	out.println("<letzteAktualisierung>" + ((letzteAktualisierung == null) ? "" : letzteAktualisierung.toTimestamp(true,true)) + "</letzteAktualisierung>");

	out.println("<verkaufserloes>" + verkaufserloes + "</verkaufserloes>");
	out.println("<erloesWaehrung>" + Waehrungen.index2Id(erloesWaehrung) + "</erloesWaehrung>");

	out.println("<sortBy>" + sortBy + "</sortBy>");

	out.println("</data>");
	out.println();
}



private static BenutzerListe readXML(BufferedReader in) throws Exception {

	/* dieser XML-Leser kann keine allgemeinen wohlgeformten XML-Dokumente
	 * lesen, sondern nur die speziell formatierten AktienMan-xml-Dateien!
	 */

	String s = XMLUtil.nextDataLine(in);

	if (!s.startsWith("<data")) throw new Exception("Unerwartetes Tag: " + s);

	s = XMLUtil.nextDataLine(in);
	
	BenutzerListe bl = new BenutzerListe();

	while (!s.startsWith("</data"))
	{
		if (s.startsWith("<erloesWaehrung"))
		{
			bl.erloesWaehrung = Waehrungen.id2Index( XMLUtil.getValue(s,true) );
		}
		else if (s.startsWith("<festeBoerse"))
		{
			bl.festeBoerse = XMLUtil.unescapeString( XMLUtil.getValue(s,true) );
		}
		else if (s.startsWith("<kommentar"))
		{
			bl.kommentar = XMLUtil.unescapeString( XMLUtil.getValue(s,true) );
		}
		else if (s.startsWith("<letzteAktualisierung"))
		{
			bl.letzteAktualisierung = ADate.parseTimestamp( XMLUtil.getValue(s,true) );
		}
		else if (s.startsWith("<sortBy"))
		{
			bl.sortBy = XMLUtil.getIntValue(s);
		}
		else if (s.startsWith("<verkaufserloes"))
		{
			bl.verkaufserloes = XMLUtil.getLongValue(s);
		}
		
		s = XMLUtil.nextDataLine(in);
	}
	
	return bl;
}



public static boolean store(BenutzerListe benutzerliste) {

	boolean error = false;
	
	synchronized (benutzerliste)
	{
		String filename = benutzerliste.getPortfolioFile();
		
		/* XML-Datei speichern */

		String xmltempfilename = filename + ".tmp";
		
		File xmltempfile = new File(xmltempfilename);
		
		if (xmltempfile.exists()) xmltempfile.delete();
		
		PrintWriter xmlout = null;
		
		try
		{
			xmlout = new PrintWriter(
						new BufferedWriter(
							new OutputStreamWriter(
								new FileOutputStream(xmltempfilename),XMLUtil.ENCODING)),false);
			
			int size = benutzerliste.size();
			
			XMLUtil.writePrologue(xmlout);
			XMLUtil.writeComment(xmlout,"ACHTUNG: Veraendern Sie diese Datei in keiner Weise, AktienMan kann derzeit noch keine beliebigen XML-Dokumente einlesen!");
			XMLUtil.writeComment(xmlout,new Date().toString());

			/* TODO: externe DTD + css-Stylesheet auf www.aktienman.de/data/ -> standalone=no */

			xmlout.println("<portfolio version=\"0\" length=\"" + size + "\">");
			xmlout.println();
			
			benutzerliste.saveXML(xmlout);

			for (int i = 0; i < size; i++)
			{
				benutzerliste.getAt(i).saveXML(xmlout);
			}

			xmlout.println("</portfolio>");

			xmlout.flush();
		}
		catch (Exception e)
		{
			error = true;
			AktienMan.errlog("Fehler beim Speichern der Aktienliste " + filename + " (xml)", e);
		}
		finally
		{
			if (xmlout != null) xmlout.close();
		}
		
		if (!error)
		{
			File xmlfile = new File(filename);
			
			if (xmlfile.exists())
			{
				if (xmlfile.length() == 0)
				{
					xmlfile.delete();
				}
				else
				{
					String xmlbakfilename = filename + ".bak";

					File xmlbakfile = new File(xmlbakfilename);
					
					if (xmlbakfile.exists()) xmlbakfile.delete();

					xmlfile.renameTo(xmlbakfile);
				}
			}
			
			xmltempfile.renameTo(xmlfile);
			
			if (!xmlfile.exists() || (xmlfile.length() == 0))
			{
				error = true;
				AktienMan.errlog("Fehler beim Umkopieren der temporären Aktienliste nach " + filename, null);
			}
		}

		if (error)
		{
			/* serialisierte Datei speichern */
			// TODO: nur noch Fallback für die 1.99, wird bei Gelegenheit (2.0?) entfernt

			error = false;

			File f = new File(filename);
			
			if (f.exists())
			{
				File backup = new File(filename + ".bak");
				
				if (backup.exists()) backup.delete();
				
				f.renameTo(backup);
			}
			
			ObjectOutputStream out = null;

			f = null;

			try
			{
				FileOutputStream fos = new FileOutputStream(filename);
				GZIPOutputStream gzos = new GZIPOutputStream(fos);
				out = new ObjectOutputStream(fos); // Fehler, muss aber so bleiben...
				out.writeObject(benutzerliste);
				out.flush();
			}
			catch (Exception e)
			{
				error = true;
				AktienMan.errlog("Fehler beim Speichern der Aktienliste " + filename + " (old-ser)", e);
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						error = true;
					}
				}
			}
		}
	}
	
	return error;
}



public static BenutzerListe restore(String datei) {

	BenutzerListe benutzerliste = null;
	
	boolean xmlvalid = false;
	boolean isxml = false;

	BufferedReader xmltest = null;

	try
	{
		xmltest = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(datei),XMLUtil.ENCODING));
		
		XMLUtil.checkValidPrologue(xmltest);
		
		isxml = true;
	}
	catch (Exception e) {}
	finally
	{
		if (xmltest != null)
		{
			try
			{
				xmltest.close();
			}
			catch (IOException e) {}
		}
	}
	
	if (isxml)
	{
		BufferedReader xmlin = null;
		
		try
		{
			xmlin = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(datei),XMLUtil.ENCODING));
			
			XMLUtil.checkValidPrologue(xmlin);
			
			String s = XMLUtil.nextDataLine(xmlin);
			
			if (!s.startsWith("<portfolio")) throw new Exception("Unerwartetes Tag: " + s);

			benutzerliste = readXML(xmlin);
			// TODO: length auswerten und Länge passend initialisieren?

			s = XMLUtil.nextDataLine(xmlin);

			while (s.startsWith("<value"))
			{
				BenutzerAktie ba = BenutzerAktie.readXML(xmlin);
				
				benutzerliste.add(ba);
			
				s = XMLUtil.nextDataLine(xmlin);
			}
			
			if (!s.startsWith("</portfolio")) throw new Exception("Unerwartetes Tag: " + s);
			
			xmlvalid = true;
		}
		catch (Exception e)
		{
			benutzerliste = null;
			AktienMan.errlog("Gespeicherte Aktienliste " + datei + " fehlerhaft (xml)", e);
		}
		finally
		{
			if (xmlin != null)
			{
				try
				{
					xmlin.close();
				}
				catch (IOException e) {}
			}
		}
	}

	if (!xmlvalid)
	{
		// TODO: nur noch Fallback für die 1.99, wird bei Gelegenheit (2.0?) entfernt
	
		ObjectInputStream in = null;

		try
		{
			FileInputStream fis = new FileInputStream(datei);
			GZIPInputStream gzis = new GZIPInputStream(fis);
			in = new ObjectInputStream(fis); // Fehler, muss aber so bleiben...

			benutzerliste = (BenutzerListe)in.readObject();
		}
		catch (Exception e)
		{
			benutzerliste = null;
			AktienMan.errlog("Gespeicherte Aktienliste " + datei + " fehlerhaft (old-ser)", e);
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
			}
		}
	}
	
	if (benutzerliste == null) benutzerliste = new BenutzerListe();

	benutzerliste.setPortfolioFile(datei);

	return benutzerliste;
}

}
