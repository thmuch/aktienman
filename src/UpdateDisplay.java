/**
 @author Thomas Much
 @version 1999-06-13
*/

import java.awt.*;
import java.awt.event.*;



public final class UpdateDisplay extends AFrame {

public static final int STATUS_LOADING   = 0;
public static final int STATUS_FINISHED  = 1;
public static final int STATUS_ERROR     = 2;
public static final int STATUS_CANCELLED = 3;

private String archiv,filename;
private UpdateLoader loader = null;
private Label progress;
private Panel panelButton;

private int status = STATUS_LOADING;



public UpdateDisplay(String archiv, String filename) {
	super(AktienMan.AMNAME);
	
	this.archiv = archiv;
	this.filename = filename;
	
	setupElements2();

	pack();
	setupSize();
	
	setProgress(0,0);
	
	show();
	
	loader = new UpdateLoader(this,archiv,filename);
	loader.start();
}


public void setupElements() {
	setLayout(gridbag);
}


public void display() {}



public void setupElements2() {
	Panel panelOben = new Panel(gridbag);
	panelButton = new Panel(gridbag);
	
	constrain(panelOben,new Label("Download von \""+archiv+"\":"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	progress = new Label("12345K (100%)  ");
	constrain(panelOben,progress,1,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,0,0);

	Button button = new Button(Lang.CANCEL);

	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	
	constrain(panelButton,button,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);

	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,panelButton,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.SOUTHEAST,1.0,0.0,15,10,10,10);
}


public synchronized void setProgress(int kbytes, int percent) {
	progress.setText("" + kbytes + "K (" + percent + "%)  ");
	progress.repaint();
}


public synchronized void setStatus(int stat) {
	status = stat;
	
	if (status == STATUS_ERROR)
	{
		progress.setText("Fehler.");
		progress.setForeground(Color.red);
		progress.repaint();
	}
	else if (status == STATUS_CANCELLED)
	{
		progress.setText("Abbruch.");
		progress.repaint();
	}
	
	panelButton.removeAll();

	Button button = new Button(Lang.OK);

	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	
	constrain(panelButton,button,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
	
	panelButton.paintAll(getGraphics());
}


public boolean canCancel() {
	return false;
}


public boolean canOK() {
	if (status == STATUS_LOADING)
	{
		if (loader != null) loader.stopDownload();
		return false;
	}
	
	return true;
}

}
