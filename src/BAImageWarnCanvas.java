/**
 @author Thomas Much
 @version 2000-07-26
*/

import java.awt.*;




public final class BAImageWarnCanvas extends BAImageCanvas {

public static final int WARN_INIT  = -1;
public static final int WARN_NONE  =  0;
public static final int WARN_RED   =  1;
public static final int WARN_GREEN =  2;

private static final int XOFFSET = 5;

private int type;




public BAImageWarnCanvas(int row, int type, Component getheight) {

	super(row,getheight);

	this.type = type;
}



public void paint(Graphics g) {

	super.paint(g);
	
	int imgy = (getHeight() - Images.WARN_HEIGHT - 1) / 2;

	if (type == WARN_RED)
	{
		g.drawImage(Images.WARN_RED,XOFFSET,imgy,this);
	}
	else if (type == WARN_GREEN)
	{
		g.drawImage(Images.WARN_GREEN,XOFFSET,imgy,this);
	}
}



public Dimension getPreferredSize() {

	return new Dimension(XOFFSET+Images.WARN_WIDTH,getHeight());
}

}
