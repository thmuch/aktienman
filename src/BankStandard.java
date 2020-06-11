// 1998-09-15 tm



public class BankStandard extends Bank {



public BankStandard() {
	this("Standard-Bank");
}


public BankStandard(String name) {
	super(name);
}


public boolean hasInternetTrade() {
	return false;
}


public long getTelefonGebuehren(long wert) {
	return ((wert + Waehrungen.PRECISION/2L) / Waehrungen.PRECISION) + getMaklerCourtage(wert);
}

}
