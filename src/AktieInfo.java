/**
 @author Thomas Much
 @version 2003-02-27

 2003-02-27
 	falls verfŸgbar, wird die ISIN angezeigt (sonst "n/a")
 	wenn die Daten nicht in der Online-WŠhrung dargestellt werden, wird diese zusŠtzlich ausgegeben
 	  (inkl. Umrechnungskurs gegenŸber EUR)
*/

import java.awt.*;
import java.awt.event.*;




public final class AktieInfo extends AFrame {

private Panel panelInfo;
private BenutzerAktie ba;




public AktieInfo(BenutzerAktie ba) {

	super(AktienMan.AMFENSTERTITEL+"Info");

	this.ba = ba;

	setupElements2();

	pack();
	setupSize();
}



public void setupElements() {

	setLayout(gridbag);
}



public void display() {}



public synchronized void setupElements2() {

	panelInfo = new Panel(gridbag);
	
	Button buttonOK = new Button(Lang.OK);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	
	constrain(this,panelInfo,0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,0,10);
	constrain(this,buttonOK,0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.SOUTHEAST,0.0,0.0,15,10,10,10);
}



public void closed() {

	ba.infoDialogClosed();
}



public synchronized void setValues(boolean draw) {

	if (draw) panelInfo.removeAll();

	constrain(panelInfo,new Label(ba.getName(false)),0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);

	String symbol = ba.getISIN();
	
	if (symbol.length() == 0) symbol = "n/a";
	
	symbol = "WKN: " + ba.getWKNString() + "   ISIN: " + symbol + "   B\u00f6rse: " + ba.getBoerse();

	constrain(panelInfo,new Label(symbol),0,1,3,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,10,0);
	
	constrain(panelInfo,new Label("Kaufkurs:"),0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getKaufkursString()),1,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);

	constrain(panelInfo,new Label("akt. Kurs:"),0,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getKursString()),1,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelInfo,new Label(ba.getKursdatumString()),2,3,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
	
	String difstr = "";
	
	long kurs = ba.getKurs();
	long vortag = ba.getVortageskurs();
	
	if ((kurs > BenutzerAktie.VALUE_MISSING) && (vortag > BenutzerAktie.VALUE_MISSING))
	{
		long diff = kurs - vortag;

		difstr = Waehrungen.getString(Waehrungen.exchange(diff,ba.getKurswaehrung(),Waehrungen.getListenWaehrung()),Waehrungen.getListenWaehrung());
		
		if (diff >= 0L) difstr = "+" + difstr;
		
		long proz = (100000L * kurs) / vortag - 100000L;
		
		if (proz > 0L) proz += 5L;
		else if (proz < 0L) proz -= 5L;

		proz /= 10L;

		difstr += "  " + ((proz<0L)?"":"+") + NumUtil.get00String(proz) + "%";
	}
	
	constrain(panelInfo,new Label(difstr),2,4,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

	constrain(panelInfo,new Label("Vortag Schlu\u00dfkurs:"),0,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getVortageskursString()),1,5,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelInfo,new Label("Er\u00f6ffnungskurs:"),0,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,10,0);
	constrain(panelInfo,new Label(ba.getEroeffnungskursString()),1,6,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,10,0);
	
	constrain(panelInfo,new Label("H\u00f6chstkurs:"),0,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getHoechstkursString()),1,7,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	constrain(panelInfo,new Label("Tiefstkurs:"),0,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,10,0);
	constrain(panelInfo,new Label(ba.getTiefstkursString()),1,8,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,10,0);

	constrain(panelInfo,new Label("gehandelte St\u00fcck:"),0,9,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,0,0,0);
	constrain(panelInfo,new Label(ba.getHandelsvolumenString()),1,9,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
	
	Panel exchange = null;
	String onlineKurs = ba.getOnlineKursString();

	if (onlineKurs != null)
	{
		int wTo = (ba.getKurswaehrung() == Waehrungen.EUR) ? Waehrungen.getListenWaehrung() : ba.getKurswaehrung();
		
		exchange = new Panel( new GridLayout(2,1) );
		
		exchange.add( new Label(onlineKurs) );
		exchange.add( new Label( Waehrungen.getExchangeRateEUR2X( wTo )));
	}
	
	int ypos;
	
	String seit = ba.getWatchStartString();
	
	if (seit.length() > 0)
	{
		constrain(panelInfo,new Label("Seit "+seit+":"),0,10,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);

		constrain(panelInfo,new Label("H\u00f6chstkurs:"),0,11,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
		constrain(panelInfo,new Label(ba.getWatchHoechstString()),1,11,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
		constrain(panelInfo,new Label(ba.getWatchHoechstDatumString()),2,11,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);

		constrain(panelInfo,new Label("Tiefstkurs:"),0,12,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,0,0,0);
		constrain(panelInfo,new Label(ba.getWatchTiefstString()),1,12,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,5,0,0);
		constrain(panelInfo,new Label(ba.getWatchTiefstDatumString()),2,12,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,0,10,0,0);
		
		ypos = 13;
	}
	else
	{
		ypos = 10;
	}

	if (exchange != null)
	{
		constrain(panelInfo,exchange,0,ypos,3,1,GridBagConstraints.NONE,GridBagConstraints.WEST,0.0,0.0,10,0,0,0);
	}

	pack();
	
	if (draw)
	{
		setSize(getSize());
		panelInfo.paintAll(getGraphics());
	}
	else
	{
		setupSize();
	}
}


}
