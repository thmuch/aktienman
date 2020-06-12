/**
 @author Thomas Much
 @version 2000-07-25
*/

import java.awt.*;




public final class BAImageArrowCanvas extends BAImageCanvas {

private static final long EQUAL  =  10L;
private static final long UPDOWN = 100L;

private static final int XOFFSET = 5;

private long kurs,vortag;




public BAImageArrowCanvas(int row, long kurs, long vortag, int vortagwaehrung, Component getheight) {

	super(row,getheight);

	if (vortag > BenutzerAktie.VALUE_MISSING)
	{
		vortag = Waehrungen.exchange(vortag,vortagwaehrung,Waehrungen.getListenWaehrung());
	}

	this.kurs = kurs;
	this.vortag = vortag;
}



public void paint(Graphics g) {

	super.paint(g);
	
	if ((kurs > BenutzerAktie.VALUE_MISSING) && (vortag > BenutzerAktie.VALUE_MISSING))
	{
		int imgy = (getHeight() - Images.ARROW_HEIGHT - 1) / 2;

		long proz = (100000L * kurs) / vortag - 100000L;
		
		if (proz > 0L) proz += 5L;
		else if (proz < 0L) proz -= 5L;

		proz /= 10L;
		
		if (proz >= UPDOWN)
		{
			g.drawImage(Images.ARROW_HI,XOFFSET,imgy,this);
		}
		else if (proz >= EQUAL)
		{
			g.drawImage(Images.ARROW_UP,XOFFSET,imgy,this);
		}
		else if (proz <= -UPDOWN)
		{
			g.drawImage(Images.ARROW_LO,XOFFSET,imgy,this);
		}
		else if (proz <= -EQUAL)
		{
			g.drawImage(Images.ARROW_DOWN,XOFFSET,imgy,this);
		}
		else
		{
			g.drawImage(Images.ARROW_EQUAL,XOFFSET,imgy,this);
		}
	}
}



public Dimension getPreferredSize() {

	return new Dimension(XOFFSET+Images.ARROW_WIDTH,getHeight());
}

}
