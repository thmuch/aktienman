/**
 @author Thomas Much
 @version 2000-11-10
*/

import java.awt.*;




public final class DAXCanvas extends Component {

private DAXKamera dk;




public DAXCanvas(DAXKamera dk) {

	this.dk = dk;
}



public void paint(Graphics g) {

	Dimension d = getSize();

	g.clearRect(0,0,d.width,d.height);
	
	switch (dk.getStatus())
	{
		case DAXKamera.S_LOADING:
			g.drawString(Lang.LOADKAMERA,20,35);
			break;

		case DAXKamera.S_ERROR:
			g.drawString("Fehler beim Einlesen der Kameradaten.",20,35);
			break;

		case DAXKamera.S_OFFLINE:
			g.drawString("Derzeit besteht keine Online-Verbindung.",20,35);
			break;
	}
	
	Image daxImage = dk.getImage();
	
	if (daxImage != null)
	{
		if (SysUtil.isWindows())
		{
			g.drawImage(daxImage,0,0,this);
		}
		else
		{
			g.drawImage(daxImage,0,0,d.width,d.height,this);
		}
	}
	
	Image djiImage = dk.getIndexImage(DAXKamera.INDEX_DJI);
	
	if (djiImage != null)
	{
		int iw = djiImage.getWidth(this);
		int ih = djiImage.getHeight(this);
		
		int xpos = 0;
		int ypos = d.height-ih;
		
		g.clearRect(xpos,ypos,iw,ih);

		g.drawImage(djiImage,xpos,ypos,this);
	}
	
	Image nasdaqImage = dk.getIndexImage(DAXKamera.INDEX_NASDAQ);
	
	if (nasdaqImage != null)
	{
		int iw = nasdaqImage.getWidth(this);
		int ih = nasdaqImage.getHeight(this);
		
		int xpos = d.width-iw;
		int ypos = d.height-ih;
		
		g.clearRect(xpos,ypos,iw,ih);

		g.drawImage(nasdaqImage,xpos,ypos,this);
	}

	Image sp500Image = dk.getIndexImage(DAXKamera.INDEX_SP500);
	
	if (sp500Image != null)
	{
		int iw = sp500Image.getWidth(this);
		int ih = sp500Image.getHeight(this);
		
		int xpos = (d.width-iw) / 2;
		int ypos = d.height-ih;
		
		g.clearRect(xpos,ypos,iw,ih);

		g.drawImage(sp500Image,xpos,ypos,this);
	}
}

}
