/**
 @author Thomas Much
 @version 1999-01-15
*/

import java.io.*;



public final class PortfolioFilter implements FilenameFilter {

private String ext;



public PortfolioFilter(String ext) {
	this.ext = ext.toUpperCase();
}


public boolean accept(File dir, String name) {
	return name.toUpperCase().endsWith(ext);
}

}
