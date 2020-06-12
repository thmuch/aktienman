/**
 @author Thomas Much
 @version 2000-07-27
*/

import java.awt.*;
import java.util.*;




public class BeepWarnalert extends Warnalert {

private BeepThread hooter;




public BeepWarnalert(AFrame parent, int type, String name, String kurs) {

	super(parent,"",false,false);
	
	Image image;
	String heading;
	
	if (type == BAImageWarnCanvas.WARN_RED)
	{
		image = Images.WARN_RED;
		heading = "Stop Loss unterschritten:";
	}
	else
	{
		image = Images.WARN_GREEN;
		heading = "Gewinngrenze \u00fcberschritten:";
	}
	
	Panel p = new Panel(gridbag);

	constrain(p,new AlertCanvas(image),0,0,1,3,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,10);
	
	Label h = new Label(heading);
	h.setForeground(Color.red);
	constrain(p,h,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);

	constrain(p,new Label("Der Wert \""+name+"\" hat "+kurs+" erreicht."),1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
	
	ADate datum = new ADate();
	
	constrain(p,new Label("("+datum+" "+datum.timeToString()+")"),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,0,0,0,0);
	
	constrain(this,p,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);

	pack();
	setupSize();
	setVisible(true);
	
	hooter = new BeepThread();
	hooter.start();
}



protected int addElements() {

	return 1;
}



public void closed() {

	if (hooter != null)
	{
		hooter.done();
		hooter = null;
	}
}




	class AlertCanvas extends Canvas {
	
	private static final int pixWidth  = 4 * Images.WARN_WIDTH;
	private static final int pixHeight = 4 * Images.WARN_HEIGHT;
	
	private Image image;

	
	public AlertCanvas(Image image) {
	
		this.image = image;
	}


	public void paint(Graphics g) {
	
		super.paint(g);
		
		g.drawImage(image,0,0,pixWidth,pixHeight,this);
	}


	public Dimension getMinimumSize() {

		return getPreferredSize();
	}


	public Dimension getPreferredSize() {

		return new Dimension(pixWidth,pixHeight);
	}

	}




	class BeepThread extends Thread {

	private static final long TIMEOUT = 2000L;
	
	private boolean running = true;

	
	public void run() {

		do
		{
			Toolkit.getDefaultToolkit().beep();
			
			try
			{
				sleep(TIMEOUT);
			}
			catch (InterruptedException e) {}
		}
		while (isRunning());
	}
	

	public synchronized void done() {
	
		running = false;
	}
	

	private synchronized boolean isRunning() {
	
		return running;
	}

	}

}
