/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.awt.*;



public final class DAXCanvas extends Component {

private DAXKamera dk;



public DAXCanvas(DAXKamera dk) {
	super();
	this.dk = dk;
}


public void paint(Graphics g)
{
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
	
	if (AktienMan.daxImage != null) g.drawImage(AktienMan.daxImage,0,0,this);
}

}
