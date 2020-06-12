/**
 @author Thomas Much
 @version 2003-02-25
*/

import java.awt.*;



public interface ChartReceiver {

public static final int STATUS_ERROR_TYPE = -3;
public static final int STATUS_ERROR_TIME = -2;
public static final int STATUS_ERROR      = -1;
public static final int STATUS_LOADING    =  0;
public static final int STATUS_OK         =  1;


public void setImage(String wkn, String boerse, int time, int type, Image image, byte[] data, ChartQuelle first, ChartQuelle current);
public void setError(String wkn, String boerse, int time, int type, int error, ChartQuelle first, ChartQuelle current);

}
