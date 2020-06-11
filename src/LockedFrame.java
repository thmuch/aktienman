// 1998-09-04 tm

import java.awt.event.*;



public class LockedFrame extends AFrame {


public LockedFrame(String title) {
	super(title);
	AktienMan.hauptdialog.Lock();
}


public void windowClosed(WindowEvent e) {
	AktienMan.hauptdialog.Unlock();
	super.windowClosed(e);
}

}
