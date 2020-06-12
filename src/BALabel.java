/**
 @author Thomas Much
 @version 2003-03-02
 
 2003-03-02
 	processMouseEvent wird nicht mehr Ÿberschrieben
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;




public final class BALabel extends Label implements Serializable,MouseListener {

static final long serialVersionUID = 1998061300000L;

private static final int NO_ROW = -1;

private int row;




public BALabel() {

	this("",NO_ROW);
}



public BALabel(int align) {

	this("",NO_ROW,align);
}



public BALabel(String text, int row) {

	this(text,row,Label.RIGHT);
}



public BALabel(String text, int row, int align) {

	super(text,align);

	this.row = row;

	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	addMouseListener(this);
}



public synchronized void setValues(String text, int row) {

	this.row = row;
	
	setText(text);
	
	setForeground(Color.black);
	setBackground(Color.white);
}



public synchronized int getRow() {

	return row;
}



/*public void processMouseEvent(MouseEvent e) {

	if (!e.isConsumed())
	{
		if (e.isPopupTrigger())
		{
			AktienMan.hauptdialog.listeSelect(this,getRow(),e.getX(),e.getY(),1,true);
			e.consume();
		}
	}

	super.processMouseEvent(e);
} TODO */



public void mousePressed(MouseEvent e) {

	if (!e.isConsumed())
	{
		AktienMan.hauptdialog.listeSelect(this,getRow(),e.getX(),e.getY(),e.getClickCount(),
				(((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) || e.isControlDown()));
		e.consume();
	}
}



public void mouseClicked(MouseEvent e) {}
public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}

}
