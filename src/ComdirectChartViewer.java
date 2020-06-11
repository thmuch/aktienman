/**
 @author Thomas Much
 @version 1999-06-21
*/

import java.awt.*;




public final class ComdirectChartViewer extends ChartViewer {

private Image[] comdirectCharts = new Image[TYPE_COUNT];
private byte[][] chartData = new byte[TYPE_COUNT][];

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



public String getTypeComdirectString(int type) {

	switch (type)
	{
	case TYPE_INTRA:
		return "1";

	case TYPE_3:
		return "2";

	case TYPE_6:
		return "3";

	case TYPE_12:
		return "4";
	}
	
	return "5";
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
		
		if ((newChart == null) || (getType() == TYPE_INTRA))
		{
			int charttype = ((isFonds()) || (getType() == TYPE_INTRA)) ? URLs.CHART_LINIE : URLs.CHART_STANDARD;
			
			new ComdirectChartLoader(this,AktienMan.url.getComdirectChartURL(relURL,getTypeComdirectString(getType()),charttype),getType()).start();
		}
		else
		{
			resetChartLoader();
			setStatusFinished();
		}
	}
}



protected synchronized void checkXY(int x, int y) {

	if ((y >= 2) && (y <= 23+WINFIX))
	{
		if ((x >= 15) && (x <= 69))
		{
			switchImage(TYPE_INTRA);
		}
		else if ((x >= 72) && (x <= 131))
		{
			switchImage(TYPE_3);
		}
		else if ((x >= 134) && (x <= 194))
		{
			switchImage(TYPE_6);
		}
		else if ((x >= 196) && (x <= 236))
		{
			switchImage(TYPE_12);
		}
		else if ((x >= 240) && (x <= 287))
		{
			switchImage(TYPE_36);
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
	return getWKNBoerse() + "-" + getTypeComdirectString(getType()) + "-" + new ADate().toTimestamp(false) + "." + getExt();
}
	
}
