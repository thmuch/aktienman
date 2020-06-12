/**
 @author Thomas Much
 @version 2000-11-11
*/

import java.awt.*;
import java.awt.event.*;




public final class PofoChartMenu extends ChartMenu {




public PofoChartMenu() {

	super("Chart-\u00dcbersicht");
}



protected void action(int time) {

	AktienMan.hauptdialog.portfolioIntradayCharts(time);
}

}
