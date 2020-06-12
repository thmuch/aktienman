/**
 @author Thomas Much
 @version 2000-11-13
*/

import java.awt.*;




public final class ComdirectChartViewer extends ChartViewer {

private Image[] comdirectCharts = new Image[URLs.CHART_COUNT];
private byte[][] chartData = new byte[URLs.CHART_COUNT][];

private String relURL;




public ComdirectChartViewer(String wknboerse, boolean isFonds, int type, int nextID) {

	super(null,"Chart "+wknboerse,wknboerse,type,"gif","GIFf",413,477,nextID,isFonds,false);
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



protected synchronized void checkXY(int x, int y) {

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
		if ((x >= 15) && (x <= 78/*69*/))
		{
			switchImage(URLs.CHART_INTRA);
		}
		else if ((x >= 79/*72*/) && (x <= 144/*131*/))
		{
			switchImage(URLs.CHART_3);
		}
		else if ((x >= 145/*134*/) && (x <= 210/*194*/))
		{
			switchImage(URLs.CHART_6);
		}
		else if ((x >= 211/*196*/) && (x <= 258/*236*/))
		{
			switchImage(URLs.CHART_12);
		}
		else if ((x >= 259/*240*/) && (x <= 310/*287*/))
		{
			switchImage(URLs.CHART_36);
		}
	}
}



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
