/**
 @author Thomas Much
 @version 1998-11-22
*/

import java.awt.*;



public class AboutCanvas extends Component {

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
