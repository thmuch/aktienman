/**
 @author Thomas Much
 @version 2000-07-27
*/

import java.awt.*;
import java.awt.event.*;




public abstract class Warnalert extends AFrame {

private AFrame parent;
private boolean quit;
private String text;




public Warnalert(AFrame parent, String text, boolean quit, boolean doshow) {

	super(AktienMan.AMNAME);

	this.parent = parent;
	this.quit = quit;
	this.text = text;

	if (parent != null)
	{	
		parent.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				doCancel();
			}
		});
	}
	
	int y = addElements();

	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(this,buttonOK,0,y,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,0,10,10);
	
	pack();
	setupSize();
	
	if (doshow)
	{
		show();
	}
}



protected String getText() {

	return text;
}



protected abstract int addElements();



public void setupElements() {

	setLayout(gridbag);
}



public void setupSize() {

	if (parent == null)
	{
		super.setupSize();
	}
	else
	{
		Point pp = parent.getLocation();
		Dimension dp = parent.getSize();

		Dimension d = getSize();
		
		setBounds(pp.x+((dp.width-d.width)/2),pp.y+((dp.height-d.height)/2),d.width,d.height);
	}
}



public void display() {}



public void closed() {

	if (quit) System.exit(0);
}

}
