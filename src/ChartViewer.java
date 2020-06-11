/**
 @author Thomas Much
 @version 1998-11-02
*/

import java.awt.*;
import java.awt.image.*;



public class ChartViewer extends AFrame implements ImageObserver {

public Image chartImage;

private int maxWidth;



public ChartViewer(Image chartImage, String request, int maxWidth) {
	super(AktienMan.AMFENSTERTITEL+request);
	this.chartImage = chartImage;
	this.maxWidth = maxWidth;

	this.chartImage.getWidth(this);
	this.chartImage.getHeight(this);

	neuZeichnen();
	
	AktienMan.hauptdialog.windowToFront(this);
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
	if (((infoflags & ImageObserver.WIDTH) > 0) && ((infoflags & ImageObserver.HEIGHT) > 0))
	{
		if (maxWidth > 0) width = maxWidth;

		setBounds((AktienMan.screenSize.width-width)/2,(AktienMan.screenSize.height-height)/2,width,height);
		neuZeichnen();
	}

	return super.imageUpdate(img,infoflags,x,y,width,height);
}

}
