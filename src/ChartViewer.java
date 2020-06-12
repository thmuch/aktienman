/**
 @author Thomas Much
 @version 2002-01-14
 
 2002-01-14
   setupTimeframeButtons
   setStatusError fŸhrt dispose() nicht mehr synchronized aus (sonst hŠngt MOSX)
*/

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;




public abstract class ChartViewer extends ImageFrame implements ImageObserver,MouseListener {

private static final int STATUS_ERROR    = -1;
private static final int STATUS_EMPTY    =  0;
private static final int STATUS_LOADING  =  1;
private static final int STATUS_FINISHED =  2;

protected static final int WINFIX_X  =  8;
protected static final int WINFIX_Y  = 46;

protected static final int MOSXFIX_X =  0;
protected static final int MOSXFIX_Y = 26;

private Image chartImage = null;

private ChartLoader chartloader = null;

private boolean fondsonly;

private int nextID;
private int type;

private String wknboerse = "";

private int status = STATUS_EMPTY;




public ChartViewer(Image chartImage, String titel, String wknboerse, int type, String ext, String ftype,
								int initWidth, int initHeight, int nextID, boolean fondsonly, boolean canScale) {

	super(AktienMan.AMFENSTERTITEL+titel,"Chart",ext,ftype);
	
	if (SysUtil.isWindows())
	{
		initWidth  += WINFIX_X;
		initHeight += WINFIX_Y;
	}
	else if (SysUtil.isMacOSX())
	{
		initWidth  += MOSXFIX_X;
		initHeight += MOSXFIX_Y;
	}

	this.wknboerse = wknboerse;
	this.nextID = nextID;
	this.fondsonly = fondsonly;
	
	setType(type);

	setImage(chartImage,null);

	addMouseListener(this);
	
	add(BorderLayout.CENTER,new ChartCanvas(this,canScale));
	
	setupTimeframeButtons();

	pack();

	setBounds((AktienMan.screenSize.width-initWidth)/2,(AktienMan.screenSize.height-initHeight)/2,initWidth,initHeight);

	setupSize();

	setVisible(true);

	AktienMan.hauptdialog.windowToFront(this);
}



public void display() {}



protected void setupTimeframeButtons() {}



protected boolean isFonds() {

	return fondsonly;
}



protected synchronized void setType(int type) {

	this.type = type;
}



protected synchronized int getType() {

	return type;
}



protected synchronized void setStatus(int status) {

	this.status = status;
}



private synchronized int getStatus() {

	return status;
}



public synchronized String getStatusString() {

	switch (getStatus())
	{
	case STATUS_ERROR:
		return Lang.CHARTERROR;

	case STATUS_FINISHED:
		return "";
	}

	return Lang.LOADCHART;
}



protected synchronized void setStatusFinished() {

	setStatus(STATUS_FINISHED);

//	nextID = ChartQuellen.CHARTQUELLE_NONE; TODO
}



public void setStatusError() {

	synchronized(this)
	{
		if (getImage() == null)
		{
			setStatus(STATUS_ERROR);
		}
		else
		{
			setStatusFinished();
		}
		
		neuZeichnen();

		if (AktienMan.DEBUG)
		{
			System.out.println("Fehler beim Einlesen von Chart " + wknboerse + "  -> " + nextID);
		}
	}
	
/*	if (nextID != ChartQuellen.CHARTQUELLE_NONE)
	{
		int i = wknboerse.indexOf(".");
		
		if (i > 0)
		{
			String wkn    = wknboerse.substring(0,i);
			String boerse = wknboerse.substring(i+1);
			
			ChartQuellen.getChartQuelle(nextID).displayChart(wkn,boerse,getType(),isFonds(),false);

			setVisible(false);
			dispose();
		}
	} TODO */
}



public synchronized void setStatusEmpty() {

	setStatus(STATUS_EMPTY);
	neuZeichnen();
}



protected String getWKNBoerse() {

	return wknboerse;
}



public synchronized void setImage(Image chartImage, byte[] data) {

	this.chartImage = chartImage;
	
	if (chartImage == null)
	{
		setStatusEmpty();
	}
	else
	{
		this.chartImage.getWidth(this);
		this.chartImage.getHeight(this);

		setStatus(STATUS_LOADING);
		neuZeichnen();

		if (data != null)
		{
			setImageData(data);
		}
	}
	
	System.gc();
}



protected abstract void setImageData(byte[] data);



public synchronized Image getImage() {

	return chartImage;
}



public void setupElements() {

	setLayout(new BorderLayout());
}



private void neuZeichnen() {

	paintAll(getGraphics());
}



public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {

	boolean neuzeichnen = (((infoflags & WIDTH) != 0) && ((infoflags & HEIGHT) != 0));

	if ((infoflags & (ERROR | FRAMEBITS | ALLBITS)) != 0)
	{
		toFront();
		setStatusFinished();
		neuzeichnen = true;
		
		loadingFinished();
	}
	
	if (neuzeichnen)
	{
		neuZeichnen();
	}
	
	return true;
}



protected void loadingFinished() {}



protected void resetChartLoader() {

	if (chartloader != null)
	{
		chartloader.stopLoading();
		chartloader = null;
		
		System.gc();
	}
}



public synchronized void setChartLoader(ChartLoader chartloader) {

	resetChartLoader();

	this.chartloader = chartloader;
}



public void closed() {

	resetChartLoader();
	
	System.gc();
}



protected void checkXY(int x, int y) {}



public void mouseClicked(MouseEvent e) {

	if (getStatus() == STATUS_FINISHED)
	{
		checkXY(e.getX(),e.getY());
	}
}



public void mousePressed(MouseEvent e) {

	if (getStatus() == STATUS_FINISHED)
	{
		checkXY(e.getX(),e.getY());
	}
}



public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}

}
