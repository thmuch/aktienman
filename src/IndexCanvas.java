/**
 @author Thomas Much
 @version 2000-10-24
*/

import java.awt.*;




public final class IndexCanvas extends Component {

private static final String TITEL = ": ";

private static final long EQUAL   =  10L;
private static final long UPDOWN  = 100L;
private static final long HIGHLOW = 200L;

private FontMetrics metrics = null;

private String symbol = null;
private String title,datum,nummax;
private String[] titlemax;
private long punkte,vortag;
private int symidx;




public IndexCanvas(String title, int symidx) {

	this(title,symidx,new String[] {title},"9,99");
}



public IndexCanvas(String title, int symidx, String[] titlemax, String nummax) {

	this(title,symidx,titlemax,nummax,0L,0L,"");
}



public IndexCanvas(String title, int symidx, String[] titlemax, String nummax, long punkte, long vortag, String datum) {

	super();

	this.title = title;
	this.symidx = symidx;
	this.nummax = nummax;
	this.titlemax = titlemax;
		
	setValues(punkte,vortag,datum);
}



public int getIndex() {

	return symidx;
}



public boolean hasSymbol(String testSymbol) {

	if (symbol == null)
	{
		symbol = AktienMan.url.getString(getIndex());
	}
	
	return symbol.equalsIgnoreCase(testSymbol);
}



public synchronized void setValues(long punkte, long vortag, String datum) {

	this.punkte = punkte;
	this.vortag = vortag;
	this.datum = datum.trim();
}



private int max(int a, int b) {

	return (a > b) ? a : b;
}



private String getTitle() {

	return title + TITEL;
}



private synchronized String getDatum() {

	return " (" + datum + ") ";
}



private synchronized long getProz() {

	long proz = 0L;

	if (vortag > 0L)
	{
		proz = (100000L * punkte) / vortag - 100000L;
		
		if (proz > 0L) proz += 5L;
		else if (proz < 0L) proz -= 5L;

		proz /= 10L;
	}
	
	return proz;
}



private synchronized String getDiff() {

	long diff = punkte - vortag;
	
	String difstr = ((diff<0L)?" ":" +") + NumUtil.get00String(diff);
	
	long proz = getProz();

	return difstr + ((proz<0L)?" ":" +") + NumUtil.get00String(proz) + "%";
}



private synchronized int getWidth() {

	checkMetrics();
	
	int x = 0, breite = 0;
	
	for (int i = 0; i < titlemax.length; i++)
	{
		if (titlemax[i] != null)
		{
			int sw = metrics.stringWidth(titlemax[i] + TITEL);
		
			if (sw > breite) breite = sw;
		}
	}
	
	x += breite;
	
	String pstr = NumUtil.get00String(punkte);
	
	int plen = metrics.stringWidth(pstr);
	
	breite = metrics.stringWidth(nummax) - plen;
	
	if (breite > 0) x += breite;
	
	return x + plen + metrics.stringWidth(getDatum()) + Images.ARROW_WIDTH + metrics.stringWidth(getDiff());
}



public synchronized void paint(Graphics g)
{
	super.paint(g);
	
	checkMetrics();
	
	int x = 0;
	int y = metrics.getAscent();
	int imgy = y + metrics.getDescent() - Images.ARROW_HEIGHT;
	
	g.setColor(Color.black);
	g.drawString(getTitle(),x,y);
	
	int breite = 0;
	
	for (int i = 0; i < titlemax.length; i++)
	{
		if (titlemax[i] != null)
		{
			int sw = metrics.stringWidth(titlemax[i] + TITEL);
		
			if (sw > breite) breite = sw;
		}
	}
	
	x += breite;
	
	String pstr = NumUtil.get00String(punkte);
	
	int plen = metrics.stringWidth(pstr);
	
	breite = metrics.stringWidth(nummax) - plen;
	
	if (breite > 0) x += breite;

	g.drawString(pstr,x,y);
	
	x += plen;
	
	String dstr = getDatum();

	g.drawString(dstr,x,y);
	
	x += metrics.stringWidth(dstr);

	long proz = getProz();
	
	if (proz >= UPDOWN)
	{
		g.drawImage(Images.ARROW_HI,x,imgy,this);
	}
	else if (proz >= EQUAL)
	{
		g.drawImage(Images.ARROW_UP,x,imgy,this);
	}
	else if (proz <= -UPDOWN)
	{
		g.drawImage(Images.ARROW_LO,x,imgy,this);
	}
	else if (proz <= -EQUAL)
	{
		g.drawImage(Images.ARROW_DOWN,x,imgy,this);
	}
	else
	{
		g.drawImage(Images.ARROW_EQUAL,x,imgy,this);
	}
	
	x += Images.ARROW_WIDTH;

	String difstr = getDiff();
	
	if (proz >= HIGHLOW)
	{
		g.setColor(Color.green.darker());
	}
	else if (proz <= -HIGHLOW)
	{
		g.setColor(Color.red);
	}

	g.drawString(difstr,x,y);
}



public Dimension getPreferredSize() {

	checkMetrics();

	return new Dimension(getWidth(),metrics.getHeight());
}



public Dimension getMinimumSize() {

	return getPreferredSize();
}



private void checkMetrics() {

	if (metrics == null)
	{
		Font f = getFont();
		
		if (f == null)
		{
			f = new Font("Dialog",Font.PLAIN,10);
			
			setFont(f);
		}

		metrics = getFontMetrics(f);
	}
}

}
