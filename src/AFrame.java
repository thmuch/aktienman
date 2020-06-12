/**
 @author Thomas Much
 @version 2000-11-12
*/

import java.awt.*;
import java.awt.event.*;




public class AFrame extends Frame implements WindowListener,KeyListener {

public GridBagLayout gridbag = new GridBagLayout();




public AFrame() {

	this("");
}



public AFrame(String title) {

	super(title);
	
	setResizable(false);

	setupFrame();
	
	addWindowListener(this);
	addKeyListener(this);
	
	setupElements();
	
	display();
}



public void setupFrame() {}



public void setupElements() {}



public void setupSize() {

	Dimension d = getSize();
	setBounds((AktienMan.screenSize.width-d.width)/2,(AktienMan.screenSize.height-d.height)/2,d.width,d.height);
}



public void display() {

	pack();
	setupSize();
	show();
}



public boolean canCancel() {

	return true;
}



public boolean canOK() {

	return true;
}



public void closed() {}



public void executeOK() {}



public void doCancel() {

	if (canCancel()) dispose();
}



public void doOK() {

	if (canOK())
	{
		executeOK();
		dispose();
	}
}



public void windowClosing(WindowEvent e) {

	doCancel();
}



public void windowClosed(WindowEvent e) {

	closed();
}



public void keyPressed(KeyEvent e) {

	if (((SysUtil.isAMac()) && (e.getKeyCode() == KeyEvent.VK_W) && (e.isMetaDown()))
		|| ((e.getKeyCode() == KeyEvent.VK_F4) && (e.isAltDown())))
	{
		e.consume();

		doCancel();
	}
}



public void windowActivated(WindowEvent e) {}
public void windowDeactivated(WindowEvent e) {}
public void windowDeiconified(WindowEvent e) {}
public void windowIconified(WindowEvent e) {}
public void windowOpened(WindowEvent e) {}

public void keyReleased(KeyEvent e) {}
public void keyTyped(KeyEvent e) {}



public static void constrain(Container container, Component component,
							int grid_x, int grid_y, int grid_width, int grid_height,
							int fill, int anchor, double weight_x, double weight_y,
							int top, int left, int bottom, int right) {
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = grid_x;
	c.gridy = grid_y;
	c.gridwidth = grid_width;
	c.gridheight = grid_height;
	c.fill = fill;
	c.anchor = anchor;
	c.weightx = weight_x;
	c.weighty = weight_y;
	if (top+bottom+left+right > 0) c.insets = new Insets(top,left,bottom,right);
	((GridBagLayout)container.getLayout()).setConstraints(component,c);
	container.add(component);
}



public static void constrain(Container container, Component component,
							int grid_x, int grid_y, int grid_width, int grid_height) {
	constrain(container,component,grid_x,grid_y,grid_width,grid_height,
			GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
}



public static void constrain(Container container, Component component,
							int grid_x, int grid_y, int grid_width, int grid_height,
							int top, int left, int bottom, int right) {
	constrain(container,component,grid_x,grid_y,grid_width,grid_height,
			GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,
			top,left,bottom,right);
}

}
