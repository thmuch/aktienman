/**
 @author Thomas Much
 @version 1998-11-01
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;



public class BALabel extends Label implements Serializable,MouseListener {

static final long serialVersionUID = 1998061300000L;

private int row;



public BALabel(String s) {
	this(s,-1,Label.LEFT);
}


public BALabel(String s, int r) {
	this(s,r,Label.RIGHT);
}


public BALabel(String s, int row, int align) {
	super(s,align);
	this.row = row;
	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	addMouseListener(this);
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
