/**
 @author Thomas Much
 @version 1998-11-02
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
	Dimension d = size();

	g.clearRect(0,0,d.width,d.height);

	g.drawString(Lang.LOADIMAGE,20,35);
	
	if (chartviewer.chartImage != null) g.drawImage(chartviewer.chartImage,0,0,this);
}


public Dimension getPreferredSize() {
	return new Dimension(300,100);
}

}
