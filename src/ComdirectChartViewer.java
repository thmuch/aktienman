/**
 @author Thomas Much
 @version 2002-01-14
 
 2002-01-14
   checkXY wird nicht mehr Ÿberschrieben
   setupTimeframeButtons, checkTimeframeButtons
*/

import java.awt.*;
import java.awt.event.*;




public final class ComdirectChartViewer extends ChartViewer {

private Image[] comdirectCharts = new Image[URLs.CHART_COUNT];
private byte[][] chartData = new byte[URLs.CHART_COUNT][];

private Button buttonIntra, button3M, button1Y, button5Y, button10Y;

private String relURL;




public ComdirectChartViewer(String wknboerse, boolean isFonds, int type, int nextID) {

	super(null,"Chart "+wknboerse,wknboerse,type,"gif","GIFf",413,477,nextID,isFonds,false);
	
	checkTimeframeButtons();
}



protected void setupTimeframeButtons() {

	Panel p  = new Panel(new BorderLayout());
	Panel p1 = new Panel(new FlowLayout(FlowLayout.RIGHT));
	Panel p2 = new Panel(new FlowLayout(FlowLayout.RIGHT));

	add(BorderLayout.NORTH, p);
	
	p.add(BorderLayout.NORTH, p1);
	p.add(BorderLayout.SOUTH, p2);
	
	buttonIntra = new Button("Intraday");
	buttonIntra.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			switchImage(URLs.CHART_INTRA);
		}
	});
	p1.add(buttonIntra);

	button3M = new Button("3 Monate");
	button3M.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			switchImage(URLs.CHART_3);
		}
	});
	p1.add(button3M);

	button1Y = new Button("1 Jahr");
	button1Y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			switchImage(URLs.CHART_12);
		}
	});
	p1.add(button1Y);

	button5Y = new Button("5 Jahre");
	button5Y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			switchImage(URLs.CHART_60);
		}
	});
	p2.add(button5Y);

	button10Y = new Button("10 Jahre");
	button10Y.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			switchImage(URLs.CHART_120);
		}
	});
	p2.add(button10Y);
}



private void checkTimeframeButtons() {

	buttonIntra.setEnabled(true);
	button3M.setEnabled(true);
	button1Y.setEnabled(true);
	button5Y.setEnabled(true);
	button10Y.setEnabled(true);

	switch(getType())
	{
		case URLs.CHART_INTRA:
			buttonIntra.setEnabled(false);
			break;

		case URLs.CHART_3:
			button3M.setEnabled(false);
			break;

		case URLs.CHART_12:
			button1Y.setEnabled(false);
			break;

		case URLs.CHART_60:
			button5Y.setEnabled(false);
			break;

		case URLs.CHART_120:
			button10Y.setEnabled(false);
			break;
	}
}



public synchronized void setComdirectRelURL(String rel) {

	relURL = rel;
}



protected void loadingFinished() {

	setComdirectImage(getType(),getImage());
}



private synchronized Image getComdirectImage(int type) {

	return comdirectCharts[type];
}



private synchronized void setComdirectImage(int type, Image img) {

	comdirectCharts[type] = img;
}



private synchronized void switchImage(int type) {

	if (type != getType())
	{
		setType(type);

		checkTimeframeButtons();

		Image newChart = getComdirectImage(getType());
		
		setImage(newChart,null);
		
		if ((newChart == null) || (getType() == URLs.CHART_INTRA))
		{
			int charttype = ((isFonds()) || (getType() == URLs.CHART_INTRA)) ? URLs.CHART_LINIE : URLs.CHART_STANDARD;
			
			new ComdirectChartLoader(this,AktienMan.url.getComdirectChartURL(relURL,getType(),charttype),getType()).start();
		}
		else
		{
			resetChartLoader();
			setStatusFinished();
		}
	}
}



/*protected synchronized void checkXY(int x, int y) {

	if (SysUtil.isWindows())
	{
		x -= WINFIX_X;
		y -= WINFIX_Y;
	}
	else if (SysUtil.isMacOSX())
	{
		x -= MOSXFIX_X;
		y -= MOSXFIX_Y;
	}

	if ((y >= 2) && (y <= 23))
	{
		if ((x >= 15) && (x <= 78))
		{
			switchImage(URLs.CHART_INTRA);
		}
		else if ((x >= 79) && (x <= 144))
		{
			switchImage(URLs.CHART_3);
		}
		else if ((x >= 145) && (x <= 210))
		{
			switchImage(URLs.CHART_6);
		}
		else if ((x >= 211) && (x <= 258))
		{
			switchImage(URLs.CHART_12);
		}
		else if ((x >= 259) && (x <= 310))
		{
			switchImage(URLs.CHART_36);
		}
	}
}*/



protected synchronized void setImageData(byte[] data) {

	chartData[getType()] = data;
}



public synchronized byte[] getImageData() {

	return chartData[getType()];
}



public String getDefaultFilename() {

	return getWKNBoerse() + "-" + AktienMan.url.getComdirectChartMonths(getType()) + "-" + new ADate().toTimestamp(false) + "." + getExt();
}
	
}
