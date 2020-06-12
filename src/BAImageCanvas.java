/**
 @author Thomas Much
 @version 2003-03-02
 
 2003-03-02
 	processMouseEvent wird nicht mehr Ÿberschrieben
*/

import java.awt.*;
import java.awt.event.*;




public abstract class BAImageCanvas extends Canvas implements MouseListener {

private int row,height;




public BAImageCanvas(int row, Component getheight) {

	this.row = row;

	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	addMouseListener(this);
	
	height = getheight.getPreferredSize().height;
}



protected int getHeight() {

	return height;
}



public Dimension getMinimumSize() {

	return getPreferredSize();
}



/*public void processMouseEvent(MouseEvent e) {

	if (!e.isConsumed())
	{
		if (e.isPopupTrigger())
		{
			AktienMan.hauptdialog.listeSelect(this,row,e.getX(),e.getY(),1,true);
			e.consume();
		}
	}

	super.processMouseEvent(e);
} TODO */



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
