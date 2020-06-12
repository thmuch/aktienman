/**
 @author Thomas Much
 @version 2000-07-25
*/

import java.awt.*;



public final class Images {


public static final int ARROW_WIDTH   = 10;
public static final int ARROW_HEIGHT  = 10;

public static final Image ARROW_HI    = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("arrow-hi.gif"));
public static final Image ARROW_UP    = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("arrow-up.gif"));
public static final Image ARROW_EQUAL = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("arrow-equal.gif"));
public static final Image ARROW_DOWN  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("arrow-down.gif"));
public static final Image ARROW_LO    = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("arrow-lo.gif"));

public static final int WARN_WIDTH    = 10;
public static final int WARN_HEIGHT   = 10;

public static final Image WARN_RED    = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("warn-red.gif"));
public static final Image WARN_GREEN  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("warn-green.gif"));

}
