/**
 @author Thomas Much
 @version 2000-11-11
*/

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;




public class IntradayChartsPortfolio extends AFrame implements ComponentListener,MouseListener,MouseMotionListener,ImageObserver {

private int chartCount, chartQuelle;

private Component chartcanvas;
private Label     statuszeile;

private int chartPixWidth, chartPixHeight, chartYOffset, chartYEnd, chartXOffset, chartXEnd;
private int chartDisplayWidth, chartDisplayHeight;

private ChartPofoLeser[] threads = new ChartPofoLeser[chartCount];
private Image[] images = new Image[chartCount];
private boolean[] isError = new boolean[chartCount];
private String[] names = new String[chartCount];
private String[] shortnames = new String[chartCount];

private int chartScaleWidth, chartColumns;
private int chartScaleHeight, chartRows;

private int type;

private int bigIndex = -1;




public IntradayChartsPortfolio(int type) {

	super("Charts - " + getType(type) + Portfolios.getCurrentWindowTitle());
	
	this.type = type;
	
	// RM-Popup: Neu laden (Thread neu starten) etc.
	
	calculateChartSizes();

	startThreads();
}



private static String getType(int type) {

	switch(type)
	{
	case URLs.CHART_3:
		return "3 Monate";

	case URLs.CHART_6:
		return "6 Monate";

	case URLs.CHART_12:
		return "1 Jahr";

	case URLs.CHART_24:
		return "2 Jahre";

	case URLs.CHART_36:
		return "3 Jahre";

	default:
		return "Intraday";
	}
}



private void startThreads() {

	int[] baindex = new int[chartCount];

	int noc = (int)AktienMan.hauptdialog.getAnzahlAktien();
	int baidx = 0;

	for (int i = 0; i < noc; i++)
	{
		if (!schonVorhandenOderFonds(i))
		{
			names[baidx] = AktienMan.hauptdialog.getAktieNr(i).getName(false);
			shortnames[baidx] = AktienMan.hauptdialog.getAktieNr(i).getName(true);

			baindex[baidx] = i;
		
			baidx++;
		}
	}

	/* Aktien alphabetisch sortieren */
	
	for (int i = 0; i < chartCount - 1; i++)
	{
		int min = i;
		String minval = AktienMan.hauptdialog.getAktieNr(baindex[min]).getName(false).trim().toUpperCase();
		
		for (int j = i+1; j < chartCount; j++)
		{
			if (minval.compareTo(AktienMan.hauptdialog.getAktieNr(baindex[j]).getName(false).trim().toUpperCase()) > 0)
			{
				min = j;
				minval = AktienMan.hauptdialog.getAktieNr(baindex[min]).getName(false).trim().toUpperCase();
			}
		}
		
		if (min != i)
		{
			int temp = baindex[i];
			baindex[i] = baindex[min];
			baindex[min] = temp;
			
			String name = names[i];
			names[i] = names[min];
			names[min] = name;

			name = shortnames[i];
			shortnames[i] = shortnames[min];
			shortnames[min] = name;
		}
	}
	
	/* Threads erzeugen und starten */

	long timeOffset = 0L;
	long offset = ChartPofoLeser.getTimeoutMillis() / 2L;
	
	if (chartCount > 0)
	{
		timeOffset = ChartPofoLeser.getTimeoutMillis() / (long)chartCount;
	}

	for (int i = 0; i < chartCount; i++)
	{
		BenutzerAktie ba = AktienMan.hauptdialog.getAktieNr(baindex[i]);

		if (chartQuelle == ChartQuellen.CHARTQUELLE_COMDIRECT)
		{
			threads[i] = new ChartPofoComdirectLeser(this,i,ba.getWKNString(),ba.getBoerse(),offset,type);
		}
		else
		{
			threads[i] = new ChartPofoDeubaLeser(this,i,ba.getWKNString(),ba.getBoerse(),offset,type);
		}
		
		threads[i].start();
		
		offset += timeOffset;
	}
}



private synchronized void stopThreads() {

	for (int i = 0; i < chartCount; i++)
	{
		if (threads[i] != null)
		{
			threads[i].parentClosed();
			threads[i] = null;
		}
	}
}



public synchronized void setChartError(int index) {

	threads[index] = null;
	images[index] = null;
	isError[index] = true;

	repaintByIndex(index);
}



public synchronized void setChartImage(int index, Image image) {

	images[index] = image;

	repaintByIndex(index);
	
	if (index == bigIndex)
	{
		repaintBigIndex();
	}
}



private void repaintByIndex(int index) {

	int zeile  = index / chartColumns;
	int spalte = index % chartColumns;
	
	repaint(spalte*chartScaleWidth,zeile*chartScaleHeight,chartScaleWidth+1,chartScaleHeight+1);
}



private void repaintBigIndex() {

	repaint(0,0,chartPixWidth+6,chartPixHeight+6);
}



private synchronized Image getChartImage(int index) {

	return images[index];
}



private synchronized String getChartName(int index) {

	if ((index < 0) || (index >= chartCount) || (names[index] == null))
	{
		return "";
	}
	else
	{
		return names[index];
	}
}



private void calculateChartSizes() {

	Dimension d = chartcanvas.getSize();
	
	if (chartCount < 2)
	{
		chartColumns = chartRows = 1;
		
		chartScaleWidth  = (d.width > chartDisplayWidth)   ? chartDisplayWidth  : d.width;
		chartScaleHeight = (d.height > chartDisplayHeight) ? chartDisplayHeight : d.height;
	}
	else
	{
		long maxChartGroesse = 0L;
		long minRestGroesse  = (long)d.width * (long)d.height;
		
		for (int spalten = 1; spalten <= chartCount; spalten++)
		{
			int zeilen = (chartCount + spalten - 1) / spalten;
			
			/* Platz vollstŠndig in spalten x zeilen Raster einteilen */
			
			int pixw = d.width / spalten;
			int pixh = d.height / zeilen;
		
			if (pixw > chartDisplayWidth)  pixw = chartDisplayWidth;
			if (pixh > chartDisplayHeight) pixh = chartDisplayHeight;
			
			long restRechts = (long)(d.width - pixw*spalten);
			long restUnten  = (long)(d.height - pixh*zeilen);
			
			/* evtl. wei§en Bereich rechts und unten auf das Raster verteilen*/
			
			pixw += restRechts / spalten;
			pixh += restUnten / zeilen;

			if (pixw > chartDisplayWidth)  pixw = chartDisplayWidth;
			if (pixh > chartDisplayHeight) pixh = chartDisplayHeight;
			
			restRechts = (long)(d.width - pixw*spalten);
			restUnten  = (long)(d.height - pixh*zeilen);
			
			/* Vergleichsgrš§en ermitteln */

			long restGroesse  = restRechts * (long)d.height
								+ restUnten * (long)d.width
								- restRechts * restUnten;
			
			long chartGroesse = (long)pixw * (long)pixh;
			
			if ((chartGroesse > maxChartGroesse) && (restGroesse < minRestGroesse))
			{
				maxChartGroesse = chartGroesse;
				minRestGroesse  = restGroesse;
				
				chartColumns = spalten;
				chartRows    = zeilen;

				chartScaleWidth  = pixw;
				chartScaleHeight = pixh;
			}
		}
	}
}



private int getIndexFromPoint(int x, int y) {

	int spalte = x / chartScaleWidth;
	int zeile  = y / chartScaleHeight;
	
	int ret = zeile * chartColumns + spalte;
	
	if ((ret < 0) || (ret >= chartCount))
	{
		return -1;
	}
	else
	{
		return ret;
	}
}



private boolean schonVorhandenOderFonds(int index) {

	BenutzerAktie bi = AktienMan.hauptdialog.getAktieNr(index);
	
	if (bi.isFonds())
	{
		return true;
	}
	
	for (int i = 0; i < index; i++)
	{
		BenutzerAktie ba = AktienMan.hauptdialog.getAktieNr(i);
		
		if (bi.isEqual(ba.getWKNString(),ba.getBoerse(),true))
		{
			return true;
		}
	}
	
	return false;
}



public void setupFrame() {

	AktienMan.checkURLs();

	int noc = (int)AktienMan.hauptdialog.getAnzahlAktien();
	
	chartCount = 0;
	
	for (int i = 0; i < noc; i++)
	{
		if (!schonVorhandenOderFonds(i))
		{
			chartCount++;
		}
	}
	
	chartQuelle = ChartQuellen.getChartQuelleIndex();
	
	if (chartQuelle == ChartQuellen.CHARTQUELLE_COMDIRECT)
	{
		chartPixWidth  = AktienMan.url.getNumber(URLs.NUM_ICCOM_PIXWIDTH);
		chartPixHeight = AktienMan.url.getNumber(URLs.NUM_ICCOM_PIXHEIGHT);
		
		chartYOffset   = AktienMan.url.getNumber(URLs.NUM_ICCOM_YOFFSET);
		chartYEnd      = AktienMan.url.getNumber(URLs.NUM_ICCOM_YEND);
		
		chartXOffset   = AktienMan.url.getNumber(URLs.NUM_ICCOM_XOFFSET);
		chartXEnd      = AktienMan.url.getNumber(URLs.NUM_ICCOM_XEND);
	}
	else
	{
		chartPixWidth  = AktienMan.url.getNumber(URLs.NUM_ICDB_PIXWIDTH);
		chartPixHeight = AktienMan.url.getNumber(URLs.NUM_ICDB_PIXHEIGHT);
		
		chartYOffset   = AktienMan.url.getNumber(URLs.NUM_ICDB_YOFFSET);
		chartYEnd      = AktienMan.url.getNumber(URLs.NUM_ICDB_YEND);
		
		chartXOffset   = AktienMan.url.getNumber(URLs.NUM_ICDB_XOFFSET);
		chartXEnd      = AktienMan.url.getNumber(URLs.NUM_ICDB_XEND);
	}
	
	chartDisplayWidth  = chartXEnd + 1 - chartXOffset;
	chartDisplayHeight = chartYEnd + 1 - chartYOffset;

	setResizable(true);
	
	addComponentListener(this);
	addMouseListener(this);
	addMouseMotionListener(this);
}



public void setupElements() {
	
	statuszeile = new Label("");
	chartcanvas = new ChartPofoCanvas(this);

	add(BorderLayout.CENTER,chartcanvas);
	add(BorderLayout.SOUTH,statuszeile);
}



public void setupSize() {

	setBounds(0,0,AktienMan.screenSize.width,AktienMan.screenSize.height);
}



public void closed() {

	stopThreads();
}



public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {

	return false;
}



public void componentResized(ComponentEvent e) {

	calculateChartSizes();
	repaint();
}



public void componentHidden(ComponentEvent e) {}
public void componentMoved(ComponentEvent e) {}
public void componentShown(ComponentEvent e) {}



public void mouseMoved(MouseEvent e) {

	statuszeile.setText(getChartName(getIndexFromPoint(e.getX(),e.getY())));
}



public void mouseClicked(MouseEvent e) {

	int big = getIndexFromPoint(e.getX(),e.getY());
	
	if ((big >= 0) && (getChartImage(big) == null))
	{
		big = -1;
	}
	
	if (big == bigIndex)
	{
		if (big == -1) return;
		
		big = -1;
	}
	
	bigIndex = big;
	
	repaintBigIndex();
}



public void mouseDragged(MouseEvent e) {}
public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}
public void mousePressed(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}




	private final class ChartPofoCanvas extends Component {
	
	private static final int XOFFSET = 4;
	private static final int YOFFSET = 1;
	
	private IntradayChartsPortfolio container;
	
	private Font font;
	private FontMetrics metrics;
	private Color namebg;
	
	private int yoffset,bgheight;



	public ChartPofoCanvas(IntradayChartsPortfolio container) {

		super();
		
		this.container = container;
		
		font = new Font("SansSerif",Font.BOLD,10);
		metrics = getFontMetrics(font);
		
		yoffset = YOFFSET + metrics.getAscent();
		bgheight = 2*YOFFSET + metrics.getHeight();
		
		namebg = Color.orange.brighter();
	}



	public void paint(Graphics g) {

		Dimension d = getSize();

		g.clearRect(0,0,d.width,d.height);

		if ((d.width >= 2 * container.chartColumns) && (d.height >= 2 * container.chartRows))
		{
			int idx = 0;
			int px = 0, py = 0;
			
			while (idx < container.chartCount)
			{
				Image img = container.getChartImage(idx);
				
				if (img != null)
				{
					g.drawImage(img,px,py,
								px+container.chartScaleWidth-1,py+container.chartScaleHeight-1,
								container.chartXOffset,container.chartYOffset,
								container.chartXEnd,container.chartYEnd,this);
				}
				else
				{
					showError(g,isError[idx],px,py,container.chartScaleWidth-1,container.chartScaleHeight-1);
				}
				
				g.setColor(namebg);
				g.fillRect(px,py,metrics.stringWidth(shortnames[idx])+3*XOFFSET,bgheight);
				
				g.setColor(Color.black);
				g.setFont(font);
				g.drawString(shortnames[idx],px+XOFFSET,py+yoffset);
				
				idx++;
				
				if (idx % container.chartColumns == 0)
				{
					px  = 0;
					py += container.chartScaleHeight;
				}
				else
				{
					px += container.chartScaleWidth;
				}
			}
			
			if (bigIndex >= 0)
			{
				Image img = container.getChartImage(bigIndex);

				if (img != null)
				{
					g.setColor(Color.blue);
					
					g.drawRect(0,0,container.chartPixWidth+5,container.chartPixHeight+5);
					g.drawRect(1,1,container.chartPixWidth+3,container.chartPixHeight+3);
					g.drawRect(2,2,container.chartPixWidth+1,container.chartPixHeight+1);
					
					g.drawImage(img,3,3,this);
				}
			}
		}
	}
	


	private void showError(Graphics g, boolean iserr, int x, int y, int width, int height) {
	
		g.setColor(iserr ? Color.red : Color.green);
		
		g.drawRect(x,y,width-1,height-1);
		g.drawLine(x,y,x+width-1,y+height-1);
		g.drawLine(x+width-1,y,x,y+height-1);
	}

	}

}
