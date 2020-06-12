/**
 @author Thomas Much
 @version 2000-11-13
*/

import java.awt.*;




public final class DeubaChartViewer extends ChartViewer {

private Image[] deubaCharts = new Image[URLs.CHART_COUNT];
private byte[][] chartData = new byte[URLs.CHART_COUNT][];

private String relURL;




public DeubaChartViewer(String wknboerse, boolean isFonds, int type, int nextID) {

	super(null,"Chart "+wknboerse,wknboerse,type,"gif","GIFf",400,493,nextID,isFonds,false);
}



public synchronized void setDeubaRelURL(String rel) {
	relURL = rel;
}



protected void loadingFinished() {
	setDeubaImage(getType(),getImage());
}



private synchronized Image getDeubaImage(int type) {
	return deubaCharts[type];
}



private synchronized void setDeubaImage(int type, Image img) {

	deubaCharts[type] = img;

	System.gc();
}



private synchronized void switchImage(int type) {

	if (type != getType())
	{
		setType(type);

		Image newChart = getDeubaImage(getType());
		
		setImage(newChart,null);
		
		if ((newChart == null) || (getType() == URLs.CHART_INTRA))
		{
			new DeubaChartLoader(this,AktienMan.url.getDeubaChartURL(relURL,getType()),getType()).start();
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
	
	if ((y >= 5) && (y <= 27))
	{
		if ((x >= 70) && (x <= 134))
		{
			switchImage(URLs.CHART_INTRA);
		}
		else if ((x >= 135) && (x <= 200))
		{
			switchImage(URLs.CHART_3);
		}
		else if ((x >= 201) && (x <= 266))
		{
			switchImage(URLs.CHART_6);
		}
		else if ((x >= 267) && (x <= 332))
		{
			switchImage(URLs.CHART_12);
		}
		else if ((x >= 334) && (x <= 399))
		{
			switchImage(URLs.CHART_24);
		}
	}
}



protected synchronized void setImageData(byte[] data) {

	chartData[getType()] = data;

	System.gc();
}



public synchronized byte[] getImageData() {
	return chartData[getType()];
}



public String getDefaultFilename() {

	return getWKNBoerse() + "-" + AktienMan.url.getDeubaChartMonths(getType()) + "-" + new ADate().toTimestamp(false) + "." + getExt();
}
	
}
