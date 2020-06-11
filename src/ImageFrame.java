/**
 @author Thomas Much
 @version 1999-06-14
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import com.apple.mrj.*;




public abstract class ImageFrame extends AFrame implements ImageObserver {

private String dlgtitle,ext,filetype;




public ImageFrame(String title, String dlgtitle, String ext, String filetype) {
	super(title);
	this.dlgtitle = dlgtitle;
	this.ext = ext;
	this.filetype = filetype;
}



public void setupFrame() {
	setResizable(true);
	
	MenuBar menubar = new MenuBar();
	setMenuBar(menubar);

	Menu fileMenu = new Menu(Lang.FILEMENUTITLE,true);
	menubar.add(fileMenu);

	MenuItem menuSave = new MenuItem("Sichern unter...",new MenuShortcut(KeyEvent.VK_M));
	menuSave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			sichern();
		}
	});
	fileMenu.add(menuSave);
	
	fileMenu.addSeparator();
	
	MenuItem menuPrint = new MenuItem("Drucken...",new MenuShortcut(KeyEvent.VK_P));
	menuPrint.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			drucken();
		}
	});
	fileMenu.add(menuPrint);
	
	if (SysUtil.isMacOS())
	{
		fileMenu.addSeparator();

		MenuItem mi = new MenuItem("Beenden",new MenuShortcut(KeyEvent.VK_Q));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AktienMan.hauptdialog.doCancel();
			}
		});
		fileMenu.add(mi);
	}
}



private void sichern() {
	byte[] imageData = getImageData();
	
	if (imageData == null) return;
	
	FileDialog fd = new FileDialog(this,AktienMan.AMFENSTERTITEL+dlgtitle+" sichern...",FileDialog.SAVE);

	fd.setFile(getDefaultFilename());
	fd.show();
	
	String pfad = fd.getDirectory();
	String datei = fd.getFile();
	
	if ((pfad != null) && (datei != null))
	{
		BufferedOutputStream out = null;
		
		MRJFileUtils.setDefaultFileType(new MRJOSType("????"));
		MRJFileUtils.setDefaultFileCreator(new MRJOSType("????"));

		String filename = pfad + datei;

		if (filename.endsWith("."))
		{
			filename = filename + ext;
		}
		else if (!filename.toUpperCase().endsWith("." + ext.toUpperCase())) {

			filename = filename + "." + ext;
		}

		File f = new File(filename);
		
		if (f.exists())
		{
			File backup = new File(filename + ".bak");
			
			if (backup.exists()) backup.delete();
			
			f.renameTo(backup);
			
			f = new File(filename);
		}

		try
		{
			out = new BufferedOutputStream(new FileOutputStream(f));
			
			out.write(imageData);
			
			out.flush();
		}
		catch (IOException e) {}
		finally
		{
			try
			{
				if (out != null) out.close();
			}
			catch (IOException e) {}
			
			out = null;
		}

		try
		{
			MRJFileUtils.setFileTypeAndCreator(f,new MRJOSType(filetype),new MRJOSType("ogle"));
		}
		catch (Exception e) {}
	}
}



private void drucken() {
	Image image = getImage();
	
	if (image == null) return;
	
	PrintJob job = getToolkit().getPrintJob(this,AktienMan.AMFENSTERTITEL+dlgtitle+" drucken",AktienMan.properties);
	
	if (job == null) return;
	
	Graphics page = job.getGraphics();

	Dimension pagesize = job.getPageDimension();
	
	int breite = image.getWidth(this);
	int hoehe = image.getHeight(this);
	
	if ((breite > 0) && (hoehe > 0))
	{
		if ((breite > pagesize.width) || (hoehe > pagesize.height))
		{
			page.drawImage(image,0,0,(breite > pagesize.width)?pagesize.width:breite,(hoehe > pagesize.height)?pagesize.height:hoehe,this);
		}
		else
		{
			page.drawImage(image,(pagesize.width-breite)/2,0,this);
		}
	}
	else
	{
		page.drawImage(image,0,0,this);
	}	
	
	page.dispose();
	job.end();
}



protected String getExt() {
	return ext;
}



public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {

	return false;
}



public abstract byte[] getImageData();

public abstract Image getImage();

public abstract String getDefaultFilename();

}
