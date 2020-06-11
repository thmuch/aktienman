// 1998-09-10 tm


public class Bankenliste extends Aktienliste {

public static final String STAND = "1.9.1998";

private static final int STANDARDBANK = 0;



public synchronized void setupList() {
	add(new Bank("(keine)"));
	add(new BankAdvance());
	add(new Bank24());
	add(new BankComdirect());
	add(new BankCommerz());
	add(new BankConsors());
	add(new BankDeutsche());
	add(new BankDresdner());
}


public synchronized Bank getAt(int index) {
	return (Bank)elementAt(index);
}


public synchronized Aktie getAktie(int index) {
	return null;
}


public int getStandardBank() {
	int stdb = AktienMan.properties.getInt("Konfig.StdBank");
	
	return ((stdb < 0) ? STANDARDBANK : stdb);
}

}
