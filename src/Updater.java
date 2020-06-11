/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;
import java.awt.event.*;



public final class Updater extends AFrame {

private String archiv,text;
private Button buttonDownload;



public Updater(int newrelease, String archiv, String text) {
	super(AktienMan.AMNAME);
	
	this.archiv = archiv;
	this.text = text;
	
	setupElements2();

	pack();
	setupSize();
	show();

	AktienMan.properties.setInt("Update.Release",newrelease);
}


public void setupElements() {
	setLayout(gridbag);
}


public void display() {}



public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	constrain(panelOben,new Label("Auf http://www.aktienman.de gibt es eine neue AktienMan-Version:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	Label l = new Label(text,Label.CENTER);
	l.setForeground(Color.blue);
	constrain(panelOben,l,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,3,0,0,0);
	
	TextArea ta = new TextArea("Sie k\u00f6nnen die neue AktienMan-Version jetzt automatisch herunterladen lassen, oder aber sie besorgen sich die neue Version sp\u00e4ter von der AktienMan-Homepage.",3,30,TextArea.SCROLLBARS_NONE);
	ta.setEditable(false);
	constrain(panelOben,ta,0,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,0,0,0);
	
	buttonDownload = new Button(" Jetzt herunterladen ");
	Button buttonAbbruch = new Button(" Sp\u00e4ter ");

	buttonDownload.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});

	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(panelButtons,buttonAbbruch,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonDownload,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}


public void executeOK() {
	buttonDownload.setEnabled(false);

	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+"Update herunterladen...",FileDialog.SAVE);

	fd.setFile(archiv);
	fd.show();
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		new UpdateDisplay(archiv,pfad+datei);
	}
}

}
