/**
 @author Thomas Much
 @version 1999-07-13
*/

import java.awt.*;
import java.awt.event.*;




public final class BAImageCanvas extends Canvas implements MouseListener {

private static final long EQUAL  =  10L;
private static final long UPDOWN = 100L;

private static final int XOFFSET = 5;

private long kurs,vortag;
private int row,height;




public BAImageCanvas(int row, long kurs, long vortag, int vortagwaehrung, Component getheight) {

	super();

	if (vortag > BenutzerAktie.VALUE_MISSING)
	{
		vortag = Waehrungen.exchange(vortag,vortagwaehrung,Waehrungen.getListenWaehrung());
	}

	this.row = row;
	this.kurs = kurs;
	this.vortag = vortag;

	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	addMouseListener(this);
	
	height = getheight.getPreferredSize().height;
}



public synchronized void paint(Graphics g)
{
	super.paint(g);
	
	if ((kurs > BenutzerAktie.VALUE_MISSING) && (vortag > BenutzerAktie.VALUE_MISSING))
	{
		int imgy = (height - Images.ARROW_HEIGHT - 1) / 2;

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

	return new Dimension(XOFFSET+Images.ARROW_WIDTH,height);
}



public Dimension getMinimumSize() {

	return getPreferredSize();
}



public void processMouseEvent(MouseEvent e) {
	if (!e.isConsumed())
	{
		if (e.isPopupTrigger())
		{
			AktienMan.hauptdialog.listeSelect(this,row,e.getX(),e.getY(),1,true);
			e.consume();
		}
	}

	super.processMouseEvent(e);
}



public void mousePressed(MouseEvent e) {
	if (!e.isConsumed())
	{
		AktienMan.hauptdialog.listeSelect(this,row,e.getX(),e.getY(),e.getClickCount(),
				(((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) || e.isControlDown()));
		e.consume();
	}
}



public void mouseClicked(MouseEvent e) {}
public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}

}
