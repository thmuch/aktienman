/**
 @author Thomas Much
 @version 1999-03-25
*/

import java.awt.*;



public final class ChartCanvas extends Component {

private ChartViewer chartviewer;
private boolean canScale;



public ChartCanvas(ChartViewer chartviewer, boolean canScale) {
	super();
	this.chartviewer = chartviewer;
	this.canScale = canScale;
}


public void paint(Graphics g)
{
	Dimension d = getSize();
	g.clearRect(0,0,d.width,d.height);

	g.drawString(chartviewer.getStatusString(),20,35);
	
	Image chartImage = chartviewer.getImage();
	
	if (chartImage != null)
	{
		if (canScale)
		{
			g.drawImage(chartImage,0,0,d.width,d.height,this);
		}
		else
		{
			g.drawImage(chartImage,0,0,this);
		}
	}
}


public Dimension getPreferredSize() {
	return new Dimension(300,100);
}

}
