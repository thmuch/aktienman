/**
 @author Thomas Much
 @version 1999-02-04
*/

import java.awt.event.*;




public final class PortfolioListener implements ItemListener {

private int index;




public PortfolioListener(int index) {

	this.index = index;
}



public void itemStateChanged(ItemEvent e) {

	Portfolios.setIndex(index);
}

}
