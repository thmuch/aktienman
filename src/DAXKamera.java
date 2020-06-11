/**
 @author Thomas Much
 @version 1998-11-15
*/

import java.awt.*;
import java.awt.image.*;



public class DAXKamera extends AFrame implements ImageObserver {

public static final int KAMERABREITE = 432;
public static final int KAMERAHOEHE  = 190;

public static final int S_LOADING =  0;
public static final int S_ERROR   =  1;
public static final int S_OFFLINE =  2;

private int status = S_LOADING;
private DAXKameraLeser kameraLeser = null;



public DAXKamera() {
	super(AktienMan.AMFENSTERTITEL+"DAX-Kamera");
}


public void setupFrame() {
	setResizable(true);
}


public void setupElements() {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER,new DAXCanvas(this));
}


public void setupSize() {
	int x = AktienMan.screenSize.width-KAMERABREITE;
	int y = 50;
	int w = KAMERABREITE-15;
	int h = KAMERAHOEHE;

	int oldx = AktienMan.properties.getInt("Kamera.X");
	int oldy = AktienMan.properties.getInt("Kamera.Y");
	int oldwidth = AktienMan.properties.getInt("Kamera.Breite");
	int oldheight = AktienMan.properties.getInt("Kamera.Hoehe");
	
	if ((oldx < 0) || (oldy < 0) || (oldwidth <= 0) || (oldheight <= 0))
	{
		setBounds(x,y,w,h);
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
		kameraLeser = new DAXKameraLeser();

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
		kameraLeser.stop();
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
	if (((infoflags & ImageObserver.WIDTH) > 0) && ((infoflags & ImageObserver.HEIGHT) > 0))
	{
		setSize(12+width,(height*3)/5);
	}

	return super.imageUpdate(img,infoflags,x,y,width,height);
}

}
