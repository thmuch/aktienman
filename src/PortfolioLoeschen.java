/**
 @author Thomas Much
 @version 2001-10-30
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.apple.mrj.*;



public final class PortfolioLoeschen extends AktienFrame {

private Button buttonJa;

private boolean geloescht = false;



public PortfolioLoeschen() {
	super(AktienMan.AMFENSTERTITEL+"Portfolio l\u00f6schen");
}


public void setupElements2() {
	Label label = new Label("Wollen Sie das Portfolio \"" + Portfolios.getCurrentName() + "\" wirklich l\u00f6schen?");
	label.setForeground(Color.red);
	constrain(this,label,0,0,3,1,GridBagConstraints.NONE,GridBagConstraints.NORTHWEST,0.0,0.0,10,10,10,10);

	constrain(this,new Label(""),0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1.0,0.0,0,10,10,0);

	Button buttonNein = new Button(Lang.NO);
	buttonNein.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	});
	constrain(this,buttonNein,1,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,0);

	buttonJa = new Button(Lang.YES);
	buttonJa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			doOK();
		}
	});
	constrain(this,buttonJa,2,1,1,1,GridBagConstraints.NONE,GridBagConstraints.EAST,0.0,0.0,0,10,10,10);
}


public synchronized void executeOK() {
	buttonJa.setEnabled(false);
	
	File alt = new File(Portfolios.getCurrentFile());

	String s = "";

	if (SysUtil.isAMac())
	{
		try
		{
			s = MRJFileUtils.findFolder(MRJFileUtils.kWhereToEmptyTrashFolderType).toString();
		}
		catch (FileNotFoundException e)
		{
			s = "";
		}
	}
	
	if (s.length() > 0)
	{
		File neu = new File(s + alt.getName());

		alt.renameTo(neu);
	}
	
	if (alt.exists()) alt.delete();
	
	geloescht = true;
}


public void cleanupAfterUnlock() {
	if (geloescht)
	{
		Portfolios.setDefaultIndexDontSave();
		Portfolios.updateMenu();
	}
}


public void closed() {
	AktienMan.portfolioloeschen = null;
}

}
