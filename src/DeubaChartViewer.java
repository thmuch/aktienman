/**
 @author Thomas Much
 @version 1999-06-20
*/

import java.awt.*;




public final class DeubaChartViewer extends ChartViewer {

private Image[] deubaCharts = new Image[TYPE_COUNT];
private byte[][] chartData = new byte[TYPE_COUNT][];

private String dbstr1,dbstr2;




public DeubaChartViewer(String wknboerse, boolean isFonds, int type, int nextID) {

	super(null,"Chart "+wknboerse,wknboerse,type,"gif","GIFf",400,493,nextID,isFonds,false);
}



public synchronized void setDeubaStrings(String s1, String s2) {
	dbstr1 = s1;
	dbstr2 = s2;
}



protected void loadingFinished() {
	setDeubaImage(getType(),getImage());
}



public String getTypeDeubaString(int type) {

	switch (type)
	{
	case TYPE_INTRA:
		return "intra";

	case TYPE_3:
		return "3";

	case TYPE_6:
		return "6";

	case TYPE_12:
		return "12";
	}
	
	return "24";
}



private synchronized Image getDeubaImage(int type) {
	return deubaCharts[type];
}



private synchronized void setDeubaImage(int type, Image img) {
	deubaCharts[type] = img;
}



private synchronized void switchImage(int type) {

	if (type != getType())
	{
		setType(type);

		Image newChart = getDeubaImage(getType());
		
		setImage(newChart,null);
		
		if ((newChart == null) || (getType() == TYPE_INTRA))
		{
			new DeubaChartLoader(this,dbstr1 + getTypeDeubaString(getType()) + dbstr2,getType()).start();
		}
		else
		{
			resetChartLoader();
			setStatusFinished();
		}
	}
}



protected synchronized void checkXY(int x, int y) {
	
	if ((y >= 7) && (y <= 26+WINFIX))
	{
		if ((x >= 70) && (x <= 134))
		{
			switchImage(TYPE_INTRA);
		}
		else if ((x >= 136) && (x <= 200))
		{
			switchImage(TYPE_3);
		}
		else if ((x >= 202) && (x <= 266))
		{
			switchImage(TYPE_6);
		}
		else if ((x >= 267) && (x <= 332))
		{
			switchImage(TYPE_12);
		}
		else if ((x >= 334) && (x <= 398))
		{
			switchImage(TYPE_24);
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

	return getWKNBoerse() + "-" + getTypeDeubaString(getType()) + "-" + new ADate().toTimestamp(false) + "." + getExt();
}
	
}
