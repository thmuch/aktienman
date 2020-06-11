// 1998-09-04 tm



public class AktienFrame extends LockedFrame {

protected int index;
protected BenutzerAktie ba;



public AktienFrame(String title, int index, BenutzerAktie ba) {
	super(title);
	
	this.index = index;
	this.ba = ba;

	setupElements2();
	
	pack();
	setupSize();
	show();

}


public void setupElements() {
	setLayout(gridbag);
}


public void setupElements2() {}

public void display() {}

}
