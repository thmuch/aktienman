/**
 @author Thomas Much
 @version 1999-06-28
*/



public interface KursReceiver {



public void listeNeuerAktienkurs(String wkn, String kurz, String platz,
									String name, long kurs, String kursdatum,
									long vortageskurs, long eroeffnungskurs,
									long hoechstkurs, long tiefstkurs,
									long handelsvolumen, int waehrung,
									boolean sofortZeichnen);

public void listeAktienkursNA(String wkn, String kurz, String platz, String name,
									boolean sofortZeichnen);

public void listeAnfrageFalsch(String wkn, String platz, boolean sofortZeichnen);

public void listeAnfrageFehler(String request, String wkn, String platz,
									boolean sofortZeichnen, int nextID);
	
}
