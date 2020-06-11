/**
 @author Thomas Much
 @version 1999-03-28
*/

import java.awt.*;
import java.awt.event.*;



public final class RegAM extends AFrame {

private Button buttonOK;
private TextField nachname,vorname,key1,key2,key3;



public RegAM() {
	super(AktienMan.AMNAME);
	nachname.requestFocus();
}


public void setupFrame() {
	setResizable(false);
}


public void setupElements() {
	setLayout(gridbag);
	
	Panel panelText = new Panel(gridbag);
	Panel panelOben = new Panel(gridbag);
	Panel panelButtons = new Panel(gridbag);
	
	nachname = new TextField(30);
	nachname.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkAllFields();
		}
	});
	
	vorname = new TextField(30);
	vorname.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkAllFields();
		}
	});

	key1 = new TextField(5);
	key1.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkAllFields();
		}
	});

	key2 = new TextField(8);
	key2.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkAllFields();
		}
	});

	key3 = new TextField(8);
	key3.addTextListener(new TextListener() {
		public void textValueChanged(TextEvent e) {
			checkAllFields();
		}
	});

	Label l = new Label(Lang.DEMOVERSION,Label.CENTER);
	l.setForeground(Color.red);
	constrain(panelText,l,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTH,0.0,0.0,0,0,0,0);
	
	TextArea ta = new TextArea("Bitte geben Sie Ihren Namen und Ihre Registriernummer ein, um aus der Demoversion eine Vollversion zu machen, oder starten Sie AktienMan als Demoversion.",3,35,TextArea.SCROLLBARS_NONE);
	ta.setEditable(false);
	constrain(panelText,ta,0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,5,0,0,0);
	
	constrain(panelOben,new Label("Nachname:"),0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,nachname,1,0,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelOben,new Label("Vorname:"),0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,5,0);
	constrain(panelOben,vorname,1,1,5,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,5,0);

	constrain(panelOben,new Label("Reg.-Nr.:"),0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,key1,1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelOben,new Label(" - "),2,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,key2,3,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,new Label(" - "),4,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelOben,key3,5,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	buttonOK = new Button(" Registrieren ");
	buttonOK.setEnabled(false);
	
	Button buttonAbbruch = new Button(Lang.CANCEL);
	Button buttonDemo = new Button(" Demo starten ");

	buttonDemo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AktienMan.main("");
			dispose();
		}
	});

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
	constrain(panelButtons,buttonDemo,1,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,10);
	constrain(panelButtons,buttonOK,2,0,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,1.0,0.0,0,0,0,0);

	constrain(this,panelText,0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,1.0,0.0,10,10,5,10);
	constrain(this,panelOben,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,10,10);
	constrain(this,panelButtons,0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,10,10,10,10);
}


public void checkAllFields() {
	if ((nachname.getText().trim().length() > 1)
	    && (vorname.getText().trim().length() > 1)
	    && (key1.getText().trim().length() == 3)
	    && (key2.getText().trim().length() == 5)
	    && (key3.getText().trim().length() == 4))
	{
		buttonOK.setEnabled(true);
	}
	else
	{
		buttonOK.setEnabled(false);
	}
}


public boolean canCancel() {
	System.exit(0);
	return true;
}


public static int string(String k1, String k2, String k3) {
	/* #Schlüssel */

	int valid = 5;

	if (k1.equalsIgnoreCase("AMD")) valid--; /* 1 */
	
	long l = 0L;
	try
	{
		l = Long.parseLong(k2);
		valid--; /* 2 */
	}
	catch (NumberFormatException e) {}
	
	char[] k = k3.toCharArray();
	
	if (k.length == 4)
	{
		if (k[0] == ((char) ((l * 31L + 1L) % 26L + 65))) valid--;      /* 3 */
		if (k[1] == ((char) ((l % 11L) * 2 + 65))) valid--;             /* 4 */
		if (k[2] == ((char) ((l % 9L) * 3 + (l+1) % 2L + 65))) valid--; /* 5 */
		if (k[3] == ((char) (l % 13 + l % 11 + l % 3 + 65))) valid--;   /* 6 */
	}
	
	return valid;
}


public boolean canOK() {
	if (string(key1.getText().trim(),key2.getText().trim(),key3.getText().trim().toUpperCase()) >= 0)
	{
		new Warnalert(this,"Bitte geben Sie Ihren Schl\u00fcssel korrekt ein.");
		return false;
	}

	return true;
}


public void executeOK() {
	/* #Schlüssel */

	buttonOK.setEnabled(false);

	AktienMan.properties.setString("Key.Nachname",nachname.getText().trim());
	AktienMan.properties.setString("Key.Vorname",vorname.getText().trim());
	AktienMan.properties.setString("Key.1",key1.getText().trim().toUpperCase());
	AktienMan.properties.setString("Key.2",key2.getText().trim());
	AktienMan.properties.setString("Key.3",key3.getText().trim().toUpperCase());
	
	AktienMan.properties.saveParameters();

	AktienMan.main("");
}

}
