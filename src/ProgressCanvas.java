/**
 @author Thomas Much
 @version 1999-06-15
*/

import java.awt.*;




public final class ProgressCanvas extends Component {

private static final int BREITE = 200;
private static final int HOEHE  =  10;

private Color blau = Color.blue.darker();
private Color grau = Color.lightGray;

private int anz,max;




public ProgressCanvas() {
	super();
	setValues(0,0);
}



public synchronized void setValues(int anz, int max) {
	this.max = (max < 0) ? 0 : max;
	
	if (anz > this.max)
	{
		anz = this.max;
	}

	this.anz = (anz < 0) ? 0 : anz;
}



public synchronized void paint(Graphics g)
{
	Dimension d = getSize();
	
	if (max <= 0)
	{
		g.setColor(Color.white);
		g.fillRect(0,0,d.width-1,d.height-1);

		g.setColor(grau);
		g.drawRect(0,0,d.width-1,d.height-1);
	}
	else
	{
		g.setColor(Color.white);
		g.fillRect(0,0,d.width-1,d.height-1);

		g.setColor(Color.black);
		g.drawRect(0,0,d.width-1,d.height-1);
		
		g.setColor(blau);
		g.fillRect(0,0,((d.width*anz)/max)-1,d.height-1);
	}
}



public Dimension getPreferredSize() {
	return new Dimension(BREITE,HOEHE);
}



public Dimension getMinimumSize() {
	return getPreferredSize();
}



public synchronized void addMax(int mehr) {

	setValues(0,max+mehr-anz);
	paint(getGraphics());
}



public synchronized void inc() {
	anz++;
	paint(getGraphics());
	
	if (anz >= max)
	{
		RedrawDemon.getRedrawDemon().interrupt();
		reset();
	}
}



public synchronized void reset() {
	
	setValues(0,0);

	try
	{
		wait(500);
	}
	catch (InterruptedException e) {}
	
	paint(getGraphics());
}

}
