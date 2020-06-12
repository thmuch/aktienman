/**
 @author Thomas Much
 @version 2000-07-27
*/

import java.awt.*;
import java.util.*;




public class TextWarnalert extends Warnalert {




public TextWarnalert(AFrame parent, String text) {

	this(parent,text,false);
}



public TextWarnalert(AFrame parent, String text, boolean quit) {

	super(parent,text,quit,true);
}



protected int addElements() {

	int y = 0, top = 10;
	
	StringTokenizer st = new StringTokenizer(getText(),"|");
	
	while (st.hasMoreTokens())
	{
		Label label = new Label(st.nextToken());
		label.setForeground(Color.red);

		constrain(this,label,0,y++,1,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,top,10,0,10);
		top = 0;
	}

	return y;
}

}
