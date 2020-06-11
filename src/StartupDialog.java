/**
 @author Thomas Much
 @version 1998-11-23
*/

import java.awt.*;



public class StartupDialog extends Frame {


public StartupDialog() {
	super(AktienMan.AMNAME);
	
	setResizable(false);
	
	setLayout(new GridBagLayout());
	
	AFrame.constrain(this,new Label("Initialisierung l\u00e4uft. Bitte warten ..."),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,15,10,15);

	pack();

	Dimension d = getSize();
	setBounds((AktienMan.screenSize.width-d.width)/2,(AktienMan.screenSize.height-d.height)/2,d.width,d.height);
	
	show();
}

}
