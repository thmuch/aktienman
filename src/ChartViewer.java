/**
 @author Thomas Much
 @version 1998-11-16
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;



public class ChartViewer extends AFrame implements ImageObserver,MouseListener {

private static final int TYPE_UNKNOWN   = 0;
public  static final int TYPE_COMDIRECT = 1;
public  static final int TYPE_INTRADAY  = 2;

private static final int STATUS_EMPTY    = 0;
private static final int STATUS_LOADING  = 1;
private static final int STATUS_FINISHED = 2;

private Image[] comdirectCharts = new Image[3];
private Image chartImage = null;
private int initWidth;
private int initHeight;
private String comstr1,comstr2,aktMonate = "";
private int status = STATUS_EMPTY;
private int type = TYPE_UNKNOWN;



public ChartViewer(Image chartImage, String request, String aktMonate,
								int initWidth, int initHeight, int type) {
	super(AktienMan.AMFENSTERTITEL+request);

	this.initWidth = initWidth;
	this.initHeight = initHeight;
	this.type = type;
	this.aktMonate = aktMonate;

	for (int i = 0; i < comdirectCharts.length; i++) comdirectCharts[i] = null;

	setImage(chartImage);

	addMouseListener(this);

	setBounds((AktienMan.screenSize.width-initWidth)/2,(AktienMan.screenSize.height-initHeight)/2,initWidth,initHeight);
	show();

	AktienMan.hauptdialog.windowToFront(this);
}


public void display() {
	pack();
	setupSize();
}


public synchronized void setImage(Image chartImage) {
	this.chartImage = chartImage;
	
	if (chartImage == null)
	{
		status = STATUS_EMPTY;
	}
	else
	{
		this.chartImage.getWidth(this);
		this.chartImage.getHeight(this);

		status = STATUS_LOADING;
	}

	neuZeichnen();
}


public synchronized Image getImage() {
	return chartImage;
}


public void setupFrame() {
	setResizable(true);
}


public void setupElements() {
	setLayout(new BorderLayout());
	add(BorderLayout.CENTER,new ChartCanvas(this));
}


private void neuZeichnen() {
	paintAll(getGraphics());
}


public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
	if (((infoflags & WIDTH) != 0) && ((infoflags & HEIGHT) != 0))
	{
/*		if (maxWidth > 0) width = maxWidth;
		setBounds((AktienMan.screenSize.width-width)/2,(AktienMan.screenSize.height-height)/2,width,height); */
		neuZeichnen();
	}

	if ((infoflags & (ERROR | FRAMEBITS | ALLBITS)) != 0)
	{
		toFront();
		status = STATUS_FINISHED;

		if (type == TYPE_COMDIRECT) setComdirectImage(aktMonate,getImage());
	}

	return super.imageUpdate(img,infoflags,x,y,width,height);
}


public synchronized void setComdirectStrings(String s1, String s2) {
	comstr1 = s1;
	comstr2 = s2;
}


private int monate2Index(String m) {
	int idx;
	
	if (m.equals("12"))
	{
		idx = 1;
	}
	else if (m.equals("24"))
	{
		idx = 2;
	}
	else
	{
		idx = 0;
	}
	
	return idx;
}


private synchronized Image getComdirectImage(String m) {
	return comdirectCharts[monate2Index(m)];
}


private synchronized void setComdirectImage(String m, Image img) {
	comdirectCharts[monate2Index(m)] = img;
}


private synchronized void switchImage(String monate) {
	if (!monate.equals(aktMonate))
	{
		aktMonate = monate;

		Image newChart = getComdirectImage(aktMonate);
		
		setImage(newChart);
		
		if (newChart == null)
		{
			new ChartLoader(this,comstr1+aktMonate+comstr2).start();
		}
		else
		{
			status = STATUS_FINISHED;
		}
	}
}


private synchronized void checkComdirectXY(int x, int y) {
	if ((y >= 10) && (y <= 31))
	{
		if ((x >= 3) && (x <= 74))
		{
			switchImage("6");
		}
		else if ((x >= 75) && (x <= 142))
		{
			switchImage("12");
		}
		else if ((x >= 143) && (x <= 215))
		{
			switchImage("24");
		}
	}
}


public void mouseClicked(MouseEvent e) {
	if (status != STATUS_FINISHED) return;
	
	int y = e.getY();
	int x = e.getX();

	if (type == TYPE_COMDIRECT) checkComdirectXY(x,y);
}


public void mousePressed(MouseEvent e) {
	if (status != STATUS_FINISHED) return;
	
	int y = e.getY();
	int x = e.getX();

	if (type == TYPE_COMDIRECT) checkComdirectXY(x,y);
}


public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}

}
