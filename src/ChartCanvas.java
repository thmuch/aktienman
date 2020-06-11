/**
 @author Thomas Much
 @version 1998-12-07
*/

import java.awt.*;



public class ChartCanvas extends Component {

private ChartViewer chartviewer;



public ChartCanvas(ChartViewer chartviewer) {
	super();
	this.chartviewer = chartviewer;
}


public void paint(Graphics g)
{
	Dimension d = getSize();
	g.clearRect(0,0,d.width,d.height);

	g.drawString(chartviewer.getStatusString(),20,35);
	
	Image chartImage = chartviewer.getImage();
	if (chartImage != null) g.drawImage(chartImage,0,0,this);
}


public Dimension getPreferredSize() {
	return new Dimension(300,100);
}

}
