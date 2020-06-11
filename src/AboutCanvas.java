/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;



public final class AboutCanvas extends Component {

private static final int IMGWIDTH = 300;
private static final int IMGHEIGHT = 255;



public AboutCanvas() {
	super();
}


public Dimension getMinimumSize() {
	return new Dimension(IMGWIDTH,IMGHEIGHT);
}


public Dimension getMaximumSize() {
	return new Dimension(IMGWIDTH,IMGHEIGHT);
}


public Dimension getPreferredSize() {
	return new Dimension(IMGWIDTH,IMGHEIGHT);
}


public Dimension getSize() {
	return new Dimension(IMGWIDTH,IMGHEIGHT);
}


public void paint(Graphics g)
{
	if (About.aboutImage != null) g.drawImage(About.aboutImage,0,-5,this);
}

}
