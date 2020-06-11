/**
 @author Thomas Much
 @version 1999-02-03
*/

import java.awt.event.*;



public class LockedFrame extends AFrame {


public LockedFrame(String title) {
	super(title);
	AktienMan.hauptdialog.Lock();
}


public void cleanupAfterUnlock() {}


public void windowClosed(WindowEvent e) {
	AktienMan.hauptdialog.Unlock();
	cleanupAfterUnlock();

	super.windowClosed(e);
}

}
