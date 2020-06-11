// 1998-08-16 tm

import java.io.*;



public class Listeneintrag implements Serializable {

static final long serialVersionUID = 1978090400000L;

private String name;
private String kurz;



public Listeneintrag(String name, String kurz) {
	this.name = name;
	this.kurz = kurz;
}


public String getName() {
	return name;
}


public String getKurz() {
	return kurz;
}


public Object getKey() {
	return kurz;
}


public String toString() {
	if (kurz.length() == 0)
	{
		return name;
	}
	else
	{
		return name+" ("+kurz+")";
	}
}

}
