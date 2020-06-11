/**
 @author Thomas Much
 @version 1999-06-14
*/

import java.awt.*;
import java.awt.image.*;



public final class DAXKamera extends ImageFrame implements ImageObserver {

private static final int KAMERABREITE = 404;
private static final int KAMERAHOEHE  = 308;

private static final String EXT   = "jpg";
private static final String FTYPE = "JPEG";

public static final int S_LOADING =  0;
public static final int S_ERROR   =  1;
public static final int S_OFFLINE =  2;

private int status = S_LOADING;
private DAXKameraLeser kameraLeser = null;

private byte[] kameraDaten = null;




public DAXKamera() {
	super(AktienMan.AMFENSTERTITEL+"DAX-Kamera","DAX-Kamera",EXT,FTYPE);
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
	
	toFront();
}



public void closed() {
	savePos();

	if (kameraLeser != null)
	{
		kameraLeser.stopLoading();
		kameraLeser = null;
	}
	
	AktienMan.daxKamera = null;
}



public void savePos() {
	Rectangle r = getBounds();

	AktienMan.properties.setInt("Kamera.X",r.x);
	AktienMan.properties.setInt("Kamera.Y",r.y);
	AktienMan.properties.setInt("Kamera.Breite",r.width);
	AktienMan.properties.setInt("Kamera.Hoehe",r.height);
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



public synchronized byte[] getImageData() {
	return kameraDaten;
}



public synchronized Image getImage() {
	return AktienMan.daxImage;
}



public String getDefaultFilename() {

	return "DAX-" + new ADate().toTimestamp(true) + "." + EXT;
}

}
