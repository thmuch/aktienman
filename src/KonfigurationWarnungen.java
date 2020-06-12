/**
 @author Thomas Much
 @version 2000-07-26
*/

import java.awt.*;
import java.awt.event.*;




public final class KonfigurationWarnungen extends AFrame {

public static final int WARNTYPE_DISPLAY = 0;
public static final int WARNTYPE_PING    = 1;
public static final int WARNTYPE_ALERT   = 2;

private static int warntype = -1;

private CheckboxGroup warnGruppe;
private Checkbox displayCheckbox,pingCheckbox,alertCheckbox;




public KonfigurationWarnungen() {

	super(AktienMan.AMFENSTERTITEL+"Warnungen");
}



public void setupElements() {

	setLayout(gridbag);

	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);

	warnGruppe = new CheckboxGroup();

	displayCheckbox = new Checkbox("nur grafisch in der Liste warnen",false,warnGruppe);
	pingCheckbox    = new Checkbox("zus\u00e4tzlich mit einem einzelnen Warnton warnen",false,warnGruppe);
	alertCheckbox   = new Checkbox("mit einem Warndialog und regelm\u00e4\u00dfigen T\u00f6nen warnen",false,warnGruppe);

	constrain(panelOben,new Label("Bei \u00dcber- oder Unterschreiten des festgelegten Grenzkurses..."),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,displayCheckbox,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,pingCheckbox,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,alertCheckbox,0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	
	switch (getWarnType())
	{
	case WARNTYPE_ALERT:
		warnGruppe.setSelectedCheckbox(alertCheckbox);
		break;
	
	case WARNTYPE_PING:
		warnGruppe.setSelectedCheckbox(pingCheckbox);
		break;
	
	default:
		warnGruppe.setSelectedCheckbox(displayCheckbox);
	}
	
	Button buttonOK = new Button(Lang.OK);
	Button buttonAbbruch = new Button(Lang.CANCEL);

	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});

	buttonAbbruch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(panelButtons,buttonAbbruch,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonOK,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);
	
	constrain(this,panelOben,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,5,10);
	constrain(this,panelButtons,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,10,10,10);
}



public void executeOK() {

	int neuertyp;

	Checkbox warn = warnGruppe.getSelectedCheckbox();
	
	if (warn == alertCheckbox)
	{
		neuertyp = WARNTYPE_ALERT;
	}
	else if (warn == pingCheckbox)
	{
		neuertyp = WARNTYPE_PING;
	}
	else
	{
		neuertyp = WARNTYPE_DISPLAY;
	}
	
	setWarnType(neuertyp);

	AktienMan.properties.saveParameters();
}



public void closed() {

	AktienMan.konfigurationWarnungen = null;
}



public synchronized static int getWarnType() {

	if (warntype < 0)
	{
		warntype = AktienMan.properties.getInt("KonfigWarn.Type",WARNTYPE_DISPLAY);
	}
	
	return warntype;
}



private synchronized static void setWarnType(int neu) {

	AktienMan.properties.setInt("KonfigWarn.Type",neu);

	warntype = neu;
}

}
