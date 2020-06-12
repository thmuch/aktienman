/**
 @author Thomas Much
 @version 2000-08-09
*/

import java.awt.*;




public final class StartupDialog extends Frame {

private static Image startImage;

private static final String AMSTART = AktienMan.AMNAME + ".gif";




public StartupDialog() {

	super(AktienMan.AMNAME);
	
	setResizable(false);

	startImage = getToolkit().getImage(ClassLoader.getSystemResource(AMSTART));
	
	setLayout(new GridBagLayout());
	
	AFrame.constrain(this,new Label("Version "+AktienMan.AMVERSION+" vom "+AktienMan.compDate.toString(),Label.CENTER),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,4,10,0,10);
	AFrame.constrain(this,new StartCanvas(),0,1,1,1,GridBagConstraints.BOTH,GridBagConstraints.NORTHWEST,1.0,1.0,0,0,0,0);
	AFrame.constrain(this,new Label("Initialisierung l\u00e4uft. Bitte warten ...",Label.CENTER),0,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,0,10,0,10);

	pack();

	Dimension d = getSize();
	setBounds((AktienMan.screenSize.width-d.width)/2,(AktienMan.screenSize.height-d.height)/2,d.width,d.height);
	
	show();
}




	final class StartCanvas extends Component {

	private static final int IMGWIDTH = 300;
	private static final int IMGHEIGHT = 255;



	public Dimension getMinimumSize() {

		return new Dimension(IMGWIDTH,IMGHEIGHT);
	}


	public Dimension getMaximumSize() {

		return new Dimension(IMGWIDTH,IMGHEIGHT);
	}


	public Dimension getPreferredSize() {

		return new Dimension(IMGWIDTH,IMGHEIGHT);
	}


	public Dimension getSize() {

		return new Dimension(IMGWIDTH,IMGHEIGHT);
	}


	public void paint(Graphics g) {

		if (StartupDialog.startImage != null) g.drawImage(StartupDialog.startImage,0,-5,this);
	}

	}
}
