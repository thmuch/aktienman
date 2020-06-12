/**
 @author Thomas Much
 @version 2003-02-26
*/

import java.awt.*;
import java.awt.event.*;




public class OneChartFrame extends ImageFrame implements ChartReceiver {

private CheckboxGroup timeGroup = new CheckboxGroup();
private CheckboxGroup typeGroup = new CheckboxGroup();

private Checkbox time1d, time5d, time10d, time3m, time6m, time1y, time2y, time3y, time5y, timemy;
private Checkbox typeBalken, typeLinien, typeKerzen, typeOHLC;
private ChartComponent chart;

private final String wkn,boerse;
private int time,type;

private Image[][] image = new Image[ChartQuellen.TIME_COUNT][ChartQuellen.TYPE_COUNT];
private ChartQuelle[][] quelle = new ChartQuelle[ChartQuellen.TIME_COUNT][ChartQuellen.TYPE_COUNT];
private byte[][][] data = new byte[ChartQuellen.TIME_COUNT][ChartQuellen.TYPE_COUNT][];
private int[][] status  = new int[ChartQuellen.TIME_COUNT][ChartQuellen.TYPE_COUNT];




public OneChartFrame(ChartQuelle first, final String wkn, final String boerse, int time, int type) {

	super(AktienMan.AMFENSTERTITEL+"Chart "+wkn+"."+boerse,"Chart","gif","GIFf");
	// TODO: woher kommen ext/ftype? -> Typen vom Loader hier mit setImage setzen lassen!
	// =====> aus der ChartQuelle bei setImage lesen! (oder doch vom Loader? ist wohl besser...)

	this.wkn     = wkn;
	this.boerse  = boerse;
	this.time    = time;
	this.type    = type;
	
	for (int i = 0; i < ChartQuellen.TIME_COUNT; i++)
	{
		for (int j = 0; j < ChartQuellen.TYPE_COUNT; j++)
		{
			status[i][j] = STATUS_LOADING;
		}
	}
	
	Panel panelButtons = new Panel(new FlowLayout(FlowLayout.LEFT));
	Panel panelTime1 = new Panel(new GridLayout(5,1));
	Panel panelTime2 = new Panel(new GridLayout(5,1));
	Panel panelTime3 = new Panel(new GridLayout(5,1));
	Panel panelType = new Panel(new GridLayout(5,1));

	chart = new ChartComponent(first.getChartSize(),first.getChartClipping());
	
	time1d  = new Checkbox("1 Tag",false,timeGroup);
	time5d  = new Checkbox("5 Tage",false,timeGroup);
	time10d = new Checkbox("10 Tage",false,timeGroup);
	time3m  = new Checkbox("3 Monate",false,timeGroup);
	time6m  = new Checkbox("6 Monate",false,timeGroup);
	time1y  = new Checkbox("1 Jahr",false,timeGroup);
	time2y  = new Checkbox("2 Jahre",false,timeGroup);
	time3y  = new Checkbox("3 Jahre",false,timeGroup);
	time5y  = new Checkbox("5 Jahre",false,timeGroup);
	timemy  = new Checkbox("maximal",false,timeGroup);
	
	time1d.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_1D);
		}
	});

	time5d.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_5D);
		}
	});

	time10d.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_10D);
		}
	});

	time3m.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_3M);
		}
	});

	time6m.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_6M);
		}
	});

	time1y.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_1Y);
		}
	});

	time2y.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_2Y);
		}
	});

	time3y.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_3Y);
		}
	});

	time5y.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_5Y);
		}
	});

	timemy.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchTime(ChartQuellen.TIME_MAX);
		}
	});

	typeBalken = new Checkbox("Balken",false,typeGroup);
	typeLinien = new Checkbox("Linien",false,typeGroup);
	typeKerzen = new Checkbox("Kerzen",false,typeGroup);
	typeOHLC   = new Checkbox("Open/Hi/Lo/Cls",false,typeGroup);

	typeBalken.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchType(ChartQuellen.TYPE_BALKEN);
		}
	});

	typeLinien.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchType(ChartQuellen.TYPE_LINIEN);
		}
	});

	typeKerzen.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchType(ChartQuellen.TYPE_KERZEN);
		}
	});

	typeOHLC.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) switchType(ChartQuellen.TYPE_OHLC);
		}
	});
	
	panelTime1.add(new Label("Zeitraum:"));
	panelTime1.add(time1d);
	panelTime1.add(time5d);
	panelTime1.add(time10d);
	panelTime1.add(time3m);

	panelTime2.add(new Label(""));
	panelTime2.add(time6m);
	panelTime2.add(time1y);
	panelTime2.add(time2y);
	panelTime2.add(time3y);

	panelTime3.add(new Label(""));
	panelTime3.add(time5y);
	panelTime3.add(timemy);
	panelTime3.add(new Label(""));
	panelTime3.add(new Label(""));
	
	panelType.add(new Label("Typ:"));
	panelType.add(typeBalken);
	panelType.add(typeLinien);
	panelType.add(typeKerzen);
	panelType.add(typeOHLC);

	panelButtons.add(new Label(" "));
	panelButtons.add(panelTime1);
	panelButtons.add(panelTime2);
	panelButtons.add(panelTime3);
	panelButtons.add(new Label("   "));
	panelButtons.add(panelType);
	
	add(panelButtons, BorderLayout.NORTH);
	add(chart,BorderLayout.CENTER);
	
	checkButtons();

	pack();
	setupSize();
	setVisible(true);
}



public void display() {}



private void checkButtons() {

	time1d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_1D) );
	time5d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_5D) );
	time10d.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_10D) );
	time3m.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_3M) );
	time6m.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_6M) );
	time1y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_1Y) );
	time2y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_2Y) );
	time3y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_3Y) );
	time5y.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_5Y) );
	timemy.setEnabled( ChartQuellen.hasAnyTime(ChartQuellen.TIME_MAX) );

	typeBalken.setEnabled( ChartQuellen.hasAnyType(ChartQuellen.TYPE_BALKEN) );
	typeLinien.setEnabled( ChartQuellen.hasAnyType(ChartQuellen.TYPE_LINIEN) );
	typeKerzen.setEnabled( ChartQuellen.hasAnyType(ChartQuellen.TYPE_KERZEN) );
	typeOHLC.setEnabled( ChartQuellen.hasAnyType(ChartQuellen.TYPE_OHLC) );

	Checkbox cb;
	
	switch(time)
	{
	case ChartQuellen.TIME_1D:
	
		cb = time1d;
		break;

	case ChartQuellen.TIME_5D:
	
		cb = time5d;
		break;

	case ChartQuellen.TIME_10D:
	
		cb = time10d;
		break;

	case ChartQuellen.TIME_3M:
	
		cb = time3m;
		break;

	case ChartQuellen.TIME_6M:
	
		cb = time6m;
		break;

	case ChartQuellen.TIME_2Y:
	
		cb = time2y;
		break;

	case ChartQuellen.TIME_3Y:
	
		cb = time3y;
		break;

	case ChartQuellen.TIME_5Y:
	
		cb = time5y;
		break;

	case ChartQuellen.TIME_MAX:
	
		cb = timemy;
		break;

	default:
	
		cb = time1y;
		break;
	}
	
	timeGroup.setSelectedCheckbox(cb);

	switch(type)
	{
	case ChartQuellen.TYPE_BALKEN:
	
		cb = typeBalken;
		break;

	case ChartQuellen.TYPE_KERZEN:
	
		cb = typeKerzen;
		break;

	case ChartQuellen.TYPE_OHLC:
	
		cb = typeOHLC;
		break;

	default:
	
		cb = typeLinien;
		break;
	}

	typeGroup.setSelectedCheckbox(cb);
}



private void checkSizeAndRepaint() {

	int zielTime = ChartQuellen.time2Index(time);
	int zielType = ChartQuellen.type2Index(type);
	
	ChartQuelle quelle = this.quelle[zielTime][zielType];
	
	if (quelle != null)
	{
		chart.setImageSize( quelle.getChartSize(), quelle.getChartClipping() );

		chart.invalidate();
		pack();
	}

	repaint();
}



private synchronized void switchTime(int time) {

	if (time != this.time)
	{
		this.time = time;
		
		checkSizeAndRepaint();
		
		if (getImage() == null)
		{
			ChartQuellen.displayChart(ChartQuellen.getChartQuelle(),this,wkn,boerse,time,type);
		}
	}
}



private synchronized void switchType(int type) {

	if (type != this.type)
	{
		this.type = type;
		
		checkSizeAndRepaint();
		
		if (getImage() == null)
		{
			ChartQuellen.displayChart(ChartQuellen.getChartQuelle(),this,wkn,boerse,time,type);
		}
	}
}



public synchronized void setImage(String wkn, String boerse, int time, int type, Image image, byte[] data, ChartQuelle first, ChartQuelle current) {

	int zielTime = ChartQuellen.time2Index(time);
	int zielType = ChartQuellen.type2Index(type);

	if (image != null)
	{
		this.image[zielTime][zielType] = image;
		this.data[zielTime][zielType]  = data;
		
		status[zielTime][zielType] = STATUS_OK;
		quelle[zielTime][zielType] = current;
		
		if ((time == this.time) && (type == this.type))
		{
			checkSizeAndRepaint();
		}
	}
	else
	{
		setError(wkn,boerse,time,type,STATUS_ERROR,first,current);
	}
}



public synchronized void setError(String wkn, String boerse, int time, int type, int error, ChartQuelle first, ChartQuelle current) {

	int zielTime = ChartQuellen.time2Index(time);
	int zielType = ChartQuellen.type2Index(type);
	
	if (image[zielTime][zielType] == null)
	{
		long nextID = ChartQuellen.getNextID(first,current,time,type);

		if (AktienMan.DEBUG)
		{
			System.out.println("Fehler beim Einlesen der Chartdaten von "+wkn+"."+boerse+"  -> "+nextID);
		}

		if (nextID == ChartQuellen.ID_NONE)
		{
			status[zielTime][zielType] = error;

			if ((time == this.time) && (type == this.type))
			{
				chart.repaint();
			}
		}
		else
		{
			ChartQuellen.getChartQuelle(nextID).loadChart(this,wkn,boerse,time,type,first);
		}
	}
}



public synchronized byte[] getImageData() {

	return data[currentTimeIndex()][currentTypeIndex()];
}



public synchronized Image getImage() {

	return image[currentTimeIndex()][currentTypeIndex()];
}



public String getDefaultFilename() {

	return wkn + "-" + boerse /*+ "-" + TODO: ZEITTYP */ + "-" + new ADate().toTimestamp(false) + "." + getExt();
}



private synchronized int currentTimeIndex() {

	return ChartQuellen.time2Index(time);
}



private synchronized int currentTypeIndex() {

	return ChartQuellen.type2Index(type);
}



private String statusMessage() {

	switch(status[currentTimeIndex()][currentTypeIndex()])
	{
	case STATUS_LOADING:
	
		return "Chart wird geladen...";
	
	case STATUS_ERROR:
	
		return "Chart nicht verf\u00fcgbar.";

	case STATUS_ERROR_TIME:
	
		return "Chart-Zeitraum nicht verf\u00fcgbar.";

	case STATUS_ERROR_TYPE:
	
		return "Chart-Typ nicht verf\u00fcgbar.";
	
	default:
	
		return "";
	}
}



	class ChartComponent extends Component {
	
	private static final int OFFSET = 5;

	private int chartWidth,chartHeight;	
	private Dimension dim;
	private Insets clip;
	
	
	public ChartComponent(Dimension dim, Insets clip) {
	
		setImageSize(dim,clip);
	}
	
	
	public void setImageSize(Dimension dim, Insets clip) {
	
		chartWidth = dim.width;
		chartHeight = dim.height;
	
		this.dim = new Dimension(chartWidth + 2 * OFFSET, chartHeight + 2 * OFFSET);
		this.clip = clip;
	}


	public Dimension getPreferredSize() {

		return dim;
	}


	public void paint(Graphics g) {
	
		Dimension d = getSize();
		g.clearRect(0,0,d.width,d.height);
		
		Image img = getImage();

		if (img != null)
		{
//			g.drawImage(img,OFFSET,OFFSET,d.width-2*OFFSET,d.height-2*OFFSET,this);
			g.drawImage(img,OFFSET,OFFSET,d.width-OFFSET-1,d.height-OFFSET-1,clip.left,clip.top,chartWidth-clip.right-1,chartHeight-clip.bottom-1,this);
		}
		else
		{
			g.drawString(statusMessage(),50,50);
		}
	}
	
	}

}
