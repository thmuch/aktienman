/**
 @author Thomas Much
 @version 2000-02-02
*/

import java.awt.*;
import java.awt.event.*;



public final class About extends AFrame {

public static Image aboutImage;

private static final String AMABOUT = AktienMan.AMNAME + ".gif";



public About() {
	super(AktienMan.AMNAME);
}


public void setupFrame() {
	setResizable(false);
	aboutImage = getToolkit().getImage(ClassLoader.getSystemResource(AMABOUT));
}


public void setupElements() {
	setLayout(gridbag);
	
	constrain(this,new Label("Version "+AktienMan.AMVERSION+" vom "+AktienMan.compDate.toString(),Label.CENTER),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,4,10,0,10);
	constrain(this,new AboutCanvas(),0,1,1,1,GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST,1.0,1.0,0,0,0,0);
	constrain(this,new Label("Copyright \u00a91998-2000 Thomas Much",Label.CENTER),0,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);
	constrain(this,new Label("Ein Programm von AktienMan&Friends.",Label.CENTER),0,3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);
	constrain(this,new Label("http://www.aktienman.de",Label.CENTER),0,4,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);

	Label l;
	
	String nachname = AktienMan.properties.getString("Key.Nachname");
	String vorname = AktienMan.properties.getString("Key.Vorname");
	
	/* #SchlŸssel */
	if ((nachname.length() > 1) && (vorname.length() > 1) && (AktienMan.properties.getString("Key.1").equalsIgnoreCase("AMD")))
	{
		l = new Label("Registriert auf: "+vorname+" "+nachname,Label.CENTER);
	}
	else
	{
		/* #Demoversion */
		l = new Label(Lang.DEMOVERSION,Label.CENTER);
		l.setForeground(Color.red);
	}

	constrain(this,l,0,5,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);
	
	Button buttonOK = new Button(Lang.OK);

	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});

	constrain(this,buttonOK,0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTH,0.0,0.0,5,10,10,10);
}


public void closed() {
	AktienMan.about = null;
}

}
