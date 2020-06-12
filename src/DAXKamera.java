/**
 @author Thomas Much
 @version 2000-11-10
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;




public final class DAXKamera extends ImageFrame implements ImageObserver {

public static final int INDEX_DJI    = 0;
public static final int INDEX_NASDAQ = 1;
public static final int INDEX_SP500  = 2;

private static final int KAMERABREITE = 404;
private static final int KAMERAHOEHE  = 308;

private static final String EXT   = "jpg";
private static final String FTYPE = "JPEG";

public static final int S_LOADING =  0;
public static final int S_ERROR   =  1;
public static final int S_OFFLINE =  2;

private int status = S_LOADING;

private DAXKameraLeser     kameraLeser = null;
private ThomsonKameraLeser djiLeser    = null;
private ThomsonKameraLeser nasdaqLeser = null;
private ThomsonKameraLeser sp500Leser  = null;

private boolean showDJI    = AktienMan.properties.getBoolean("Kamera.showDJI",true);
private boolean showSP500  = AktienMan.properties.getBoolean("Kamera.showSP500",true);
private boolean showNASDAQ = AktienMan.properties.getBoolean("Kamera.showNASDAQ",true);

private byte[] kameraDaten = null;
private Image daxImage = null;
private Image[] usImages = new Image[3];




public DAXKamera() {

	super(AktienMan.AMFENSTERTITEL+"DAX-Kamera","DAX-Kamera",EXT,FTYPE);
	
	Menu editMenu = getEditMenu();
	
	if (editMenu != null)
	{
		CheckboxMenuItem menuDJI    = new CheckboxMenuItem("\"Dow Jones\" anzeigen");

		menuDJI.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				showDJI = (e.getStateChange() == ItemEvent.SELECTED);

				if (showDJI)
				{
					showKamera();
				}
				else
				{
					if (djiLeser != null)
					{
						djiLeser.stopLoading();
						djiLeser = null;
					}

					setImage(INDEX_DJI,null);
					
					neuZeichnen();
				}
			}
		});
		
		CheckboxMenuItem menuSP500  = new CheckboxMenuItem("\"S&P 500\" anzeigen");

		menuSP500.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				showSP500 = (e.getStateChange() == ItemEvent.SELECTED);

				if (showSP500)
				{
					showKamera();
				}
				else
				{
					if (sp500Leser != null)
					{
						sp500Leser.stopLoading();
						sp500Leser = null;
					}

					setImage(INDEX_SP500,null);
					
					neuZeichnen();
				}
			}
		});
		
		CheckboxMenuItem menuNASDAQ = new CheckboxMenuItem("\"NASDAQ\" anzeigen");

		menuNASDAQ.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				showNASDAQ = (e.getStateChange() == ItemEvent.SELECTED);

				if (showNASDAQ)
				{
					showKamera();
				}
				else
				{
					if (nasdaqLeser != null)
					{
						nasdaqLeser.stopLoading();
						nasdaqLeser = null;
					}

					setImage(INDEX_NASDAQ,null);
					
					neuZeichnen();
				}
			}
		});
		
		editMenu.add(menuDJI);
		editMenu.add(menuSP500);
		editMenu.add(menuNASDAQ);
		
		menuDJI.setState(showDJI);
		menuSP500.setState(showSP500);
		menuNASDAQ.setState(showNASDAQ);
	}
}



public void setupElements() {

	setLayout(new BorderLayout());
	add(BorderLayout.CENTER,new DAXCanvas(this));
}



public void setupSize() {

	int x = AktienMan.screenSize.width-KAMERABREITE-50;
	int y = 40;

	int oldx = AktienMan.properties.getInt("Kamera.X");
	int oldy = AktienMan.properties.getInt("Kamera.Y");
	int oldwidth = AktienMan.properties.getInt("Kamera.Breite");
	int oldheight = AktienMan.properties.getInt("Kamera.Hoehe");
	
	if ((oldx < 0) || (oldy < 0) || (oldwidth <= 0) || (oldheight <= 0))
	{
		setBounds(x,y,KAMERABREITE,KAMERAHOEHE);
	}
	else
	{
		setBounds(oldx,oldy,oldwidth,oldheight);
	}
}



public boolean canOK() {

	return false;
}



public synchronized void showKamera() {

	if (kameraLeser == null)
	{
		kameraLeser = new DAXKameraLeser(this);

		if (kameraLeser != null) kameraLeser.start();		
	}
	else
	{
		kameraLeser.interrupt();
	}
	
	if (showDJI)
	{
		if (djiLeser == null)
		{
			djiLeser = new ThomsonKameraLeser(this,URLs.URL_KAMERADJI,INDEX_DJI);

			if (djiLeser != null) djiLeser.start();		
		}
		else
		{
			djiLeser.interrupt();
		}
	}

	if (showNASDAQ)
	{
		if (nasdaqLeser == null)
		{
			nasdaqLeser = new ThomsonKameraLeser(this,URLs.URL_KAMERANASDAQ,INDEX_NASDAQ);

			if (nasdaqLeser != null) nasdaqLeser.start();		
		}
		else
		{
			nasdaqLeser.interrupt();
		}
	}

	if (showSP500)
	{
		if (sp500Leser == null)
		{
			sp500Leser = new ThomsonKameraLeser(this,URLs.URL_KAMERASP500,INDEX_SP500);

			if (sp500Leser != null) sp500Leser.start();		
		}
		else
		{
			sp500Leser.interrupt();
		}
	}
	
	toFront();
}



public void closed() {

	savePos();

	if (kameraLeser != null)
	{
		kameraLeser.stopLoading();
		kameraLeser = null;
	}
	
	if (djiLeser != null)
	{
		djiLeser.stopLoading();
		djiLeser = null;
	}

	if (nasdaqLeser != null)
	{
		nasdaqLeser.stopLoading();
		nasdaqLeser = null;
	}

	if (sp500Leser != null)
	{
		sp500Leser.stopLoading();
		sp500Leser = null;
	}

	AktienMan.daxKamera = null;
}



public void savePos() {

	Rectangle r = getBounds();

	AktienMan.properties.setInt("Kamera.X",r.x);
	AktienMan.properties.setInt("Kamera.Y",r.y);
	AktienMan.properties.setInt("Kamera.Breite",r.width);
	AktienMan.properties.setInt("Kamera.Hoehe",r.height);

	AktienMan.properties.setBoolean("Kamera.showDJI",showDJI);
	AktienMan.properties.setBoolean("Kamera.showSP500",showSP500);
	AktienMan.properties.setBoolean("Kamera.showNASDAQ",showNASDAQ);
}



public synchronized void setStatus(int stat) {

	status = stat;
	neuZeichnen();
}



public synchronized int getStatus() {

	return status;
}



public void neuZeichnen() {

	paintAll(getGraphics());
}



public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {

	if (((infoflags & WIDTH) != 0) && ((infoflags & HEIGHT) != 0))
	{
		neuZeichnen();
	}
	
	return true;
}



public synchronized void setKameraDaten(byte[] kameraDaten) {

	this.kameraDaten = kameraDaten;
}



public synchronized void setImage(Image daxImage) {

	this.daxImage = daxImage;
}



public synchronized void setImage(int index, Image image) {

	usImages[index] = image;
}



public synchronized byte[] getImageData() {

	return kameraDaten;
}



public synchronized Image getImage() {

	return daxImage;
}



public synchronized Image getIndexImage(int index) {

	return usImages[index];
}



public String getDefaultFilename() {

	return "DAX-" + new ADate().toTimestamp(true) + "." + EXT;
}

}
