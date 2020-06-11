/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;



public final class About extends AFrame {

public static Image aboutImage;

private static final String pathsep = System.getProperty("path.separator");
private static final String filesep = System.getProperty("file.separator");
private static final String AMABOUT = "am.gif";
private static String lastFindPath = "";



public About() {
	super(AktienMan.AMNAME);
}


public void setupFrame() {
	setResizable(false);
	aboutImage = getToolkit().getImage(findLocalFile(AMABOUT));
}


public void setupElements() {
	setLayout(gridbag);
	
	constrain(this,new Label("Version "+AktienMan.AMVERSION+" vom "+AktienMan.compDate.toString(),Label.CENTER),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,4,10,0,10);
	constrain(this,new AboutCanvas(),0,1,1,1,GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST,1.0,1.0,0,0,0,0);
	constrain(this,new Label("Copyright \u00a91998,99 Thomas Much",Label.CENTER),0,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);
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


public static String findLocalFile(String filename) {
	if (lastFindPath.length() > 0)
	{
		File f = new File(lastFindPath+filename);
		
		if (f.exists())
		{
			return lastFindPath+filename;
		}
	}
	
	StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"),pathsep);
	
	while (st.hasMoreTokens()) {
		String s = st.nextToken();
		
		if ((!s.endsWith(".zip")) && (!s.endsWith(".jar")))
		{
			if (!s.endsWith(filesep)) s+=filesep;
			
			File f = new File(s+filename);
			
			if (f.length() > 0L)
			{
				lastFindPath = s;				
				return lastFindPath+filename;
			}
		}
	}

	return filename;
}

}
