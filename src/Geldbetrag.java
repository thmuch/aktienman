/**
 @author Thomas Much
 @version 1999-01-07
*/

/* WIRD DERZEIT NOCH NICHT IM AM-PROJEKT VERWENDET! */



public class Geldbetrag {

private int waehrung;
private long betrag;



public Geldbetrag() {
	this(Waehrungen.EUR);
}


public Geldbetrag(int waehrung) {
	clearBetrag();
	setWaehrung(waehrung);
}


private synchronized void setWaehrung(int waehrung) {
	this.waehrung = waehrung;
}


public synchronized int getWaehrung() {
	return waehrung;
}


private synchronized void setBetrag(long betrag) {
	this.betrag = betrag;
}


public synchronized void setBetrag(Geldbetrag g) {
	setBetrag(g.getBetrag(getWaehrung()));
}


public synchronized void setBetragDEM(long betrag) {
	setBetrag(Waehrungen.exchange(betrag,Waehrungen.DEM,getWaehrung()));
}


public synchronized void setBetragEUR(long betrag) {
	setBetrag(Waehrungen.exchange(betrag,Waehrungen.EUR,getWaehrung()));
}


private synchronized long getBetrag() {
	return betrag;
}


public synchronized long getBetrag(int waehrung) {
	return Waehrungen.exchange(getBetrag(),getWaehrung(),waehrung);
}


public synchronized void clearBetrag() {
	setBetrag(0L);
}


public synchronized void add(Geldbetrag g) {
	setBetrag(getBetrag() + g.getBetrag(getWaehrung()));
}


public synchronized void sub(Geldbetrag g) {
	setBetrag(getBetrag() - g.getBetrag(getWaehrung()));
}


public synchronized String toString() {
	return Waehrungen.getString(getBetrag(),getWaehrung());
}

}
