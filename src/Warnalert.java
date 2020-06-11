/**
 @author Thomas Much
 @version 1998-11-13
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class Warnalert extends AFrame {

private AFrame parent;
private boolean quit;



public Warnalert(AFrame parent, String text) {
	this(parent,text,false);
}


public Warnalert(AFrame parent, String text, boolean quit) {
	super(AktienMan.AMNAME);

	this.parent = parent;
	this.quit = quit;

	if (parent != null)
	{	
		parent.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				doCancel();
			}
		});
	}
	
	int y = 0, top = 10;
	
	StringTokenizer st = new StringTokenizer(text,"|");
	
	while (st.hasMoreTokens())
	{
		Label label = new Label(st.nextToken());
		label.setForeground(Color.red);

		constrain(this,label,0,y++,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,top,10,0,10);
		top = 0;
	}

	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(this,buttonOK,0,y,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,0,10,10);
	
	pack();
	setupSize();
	show();
}


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
