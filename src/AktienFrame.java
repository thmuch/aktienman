/**
 @author Thomas Much
 @version 2000-07-28
*/




public abstract class AktienFrame extends LockedFrame {

protected int index;
protected BenutzerAktie ba;




public AktienFrame(String title, int index, BenutzerAktie ba) {

	super(title);
	
	this.index = index;
	this.ba = ba;

	setupElements2();
	
	pack();
	setupSize();
	setVisible(true);
}



public AktienFrame(String title) {

	this(title,-1,null);
}



public void setupElements() {

	setLayout(gridbag);
}



public void setupElements2() {}


public void display() {}

}
