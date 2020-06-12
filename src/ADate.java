/**
 @author Thomas Much
 @version 2000-06-05
*/

import java.util.*;
import java.io.*;




public final class ADate implements Serializable {

static final long serialVersionUID = 1979090400000L;

public static final int JANUARY   =  1;
public static final int FEBRUARY  =  2;
public static final int MARCH     =  3;
public static final int APRIL     =  4;
public static final int MAY       =  5;
public static final int JUNE      =  6;
public static final int JULY      =  7;
public static final int AUGUST    =  8;
public static final int SEPTEMBER =  9;
public static final int OCTOBER   = 10;
public static final int NOVEMBER  = 11;
public static final int DECEMBER  = 12;

private static TimeZone timezone = TimeZone.getTimeZone("ECT");

private int year,month,day;
private int hour,minute,second;
private int serialDate;




public ADate() {

	Calendar c = Calendar.getInstance(timezone);
	set(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DATE),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
}



public ADate(int year, int month, int day) {

	set(year,month,day);
}



public ADate(int serialdate) {

	set(serialdate);
}



public void set(int year, int month, int day) {

	set(year,month,day,0,0,0);
}



public void set(int year, int month, int day, int hour, int minute, int second) {

	this.year   = fixYear(year);
	this.month  = month;
	this.day    = day;

	this.hour   = hour;
	this.minute = minute;
	this.second = second;
	
	calculateSerialDate();
}



public void set(int serialdate) {

	if (serialdate < 0L) return;

	serialDate = serialdate;
	
	int y = 1900;
	
	int d = getDays(y);
	
	while (serialdate >= d)
	{
		serialdate -= d;

		d = getDays(++y);
	}
	
	int m = JANUARY;
	
	d = getDays(y,m);
	
	while (serialdate >= d)
	{
		serialdate -= d;

		d = getDays(y,++m);
	}
	
	this.year   = y;
	this.month  = m;
	this.day    = serialdate + 1;

	this.hour   = 0;
	this.minute = 0;
	this.second = 0;
}



public int getSerialDate() {

	return serialDate;
}



public int getYear() {

	return year;
}



public int getMonth() {

	return month;
}



public int getDay() {

	return day;
}



public int getHour() {

	return hour;
}



public int getMinute() {

	return minute;
}



public int getSecond() {

	return second;
}



public boolean before(ADate adate) {

	return (adate.getSerialDate() > getSerialDate());
}



public boolean after(ADate adate) {

	return (adate.getSerialDate() < getSerialDate());
}



public boolean equals(ADate adate) {

	return (adate.getSerialDate() == getSerialDate());
}



public String toString() {

	return "" + getDay() + "." + getMonth() + "." + getYear();
}



public String timeToString() {

	int m = getMinute();
	return "" + getHour() + ":" + ((m<10)?"0":"") + m;
}



public String toTimestamp(boolean time) {

	int minute = getMinute();
	int hour = getHour();
	int month = getMonth();
	int day = getDay();
	
	String t = "" + getYear() + ((month<10)?"0":"") + month + ((day<10)?"0":"") + day;
	
	if (time)
	{
		t += "-" + ((hour<10)?"0":"") + hour + ((minute<10)?"0":"") + minute;
	}
	
	return t;
}



public static boolean isLeapYear(int year) {

	year = fixYear(year);
	
	if ((year % 4) == 0)
	{
		if ((year % 100) == 0)
		{
			if ((year % 400) == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	else
	{
		return false;
	}
}



public static int getDays(int year) {

	if (isLeapYear(year))
	{
		return 366;
	}
	else
	{
		return 365;
	}
}



public static int getDays(int year, int month) {

	if (month == FEBRUARY)
	{
		if (isLeapYear(year))
		{
			return 29;
		}
		else
		{
			return 28;
		}
	}
	else
	{
		switch (month) {
		case JANUARY:
		case MARCH:
		case MAY:
		case JULY:
		case AUGUST:
		case OCTOBER:
		case DECEMBER:
			return 31;
		case APRIL:
		case JUNE:
		case SEPTEMBER:
		case NOVEMBER:
			return 30;
		default:
			return 0;
		}
	}
}



public static ADate parse(String s) throws Exception {

	StringTokenizer st = new StringTokenizer(s.trim(),".,/- ");
	
	if (st.hasMoreTokens())
	{
		String str_tag = st.nextToken().trim();
		
		if (st.hasMoreTokens())
		{
			String str_monat = st.nextToken().trim();
			String str_jahr;

			if (st.hasMoreTokens())
			{
				str_jahr = st.nextToken().trim();
				
				if (st.hasMoreTokens())
				{
					throw new Exception();
				}
			}
			else
			{
				str_jahr = "" + (new ADate().getYear());
			}
			
			int jahr  = Integer.parseInt(str_jahr);
			int monat = Integer.parseInt(str_monat);
			int tag   = Integer.parseInt(str_tag);
			
			if ((((jahr>=0) && (jahr<=99)) || ((jahr>=1900) && (jahr<=2199)))
				&& (monat>=1) && (monat<=12) && (tag>=1))
			{
				if (tag<=getDays(jahr,monat))
				{
					return new ADate(jahr,monat,tag);
				}
				else
				{
					throw new Exception();
				}
			}
			else
			{
				throw new Exception();
			}
		}
		else
		{
			throw new Exception();
		}
	}
	else
	{
		throw new Exception();
	}
}



private static int fixYear(int year) {

	if (year < 100)
	{
		if (year < 80)
		{
			year += 2000;
		}
		else
		{
			year += 1900;
		}
	}
	
	return year;
}



private void calculateSerialDate() {

	int jahr = getYear();
	int monat = getMonth();
	
	serialDate = getDay() - 1; /* 01.01.1900 = 0 */

	if (jahr < 1900) jahr = 1900;

	int y = 1900;
	
	while (y < jahr)
	{
		serialDate += getDays(y);
		y++;
	}
	
	int m = 1;
	
	while (m < monat)
	{
		serialDate += getDays(jahr,m);
		m++;
	}
}

}
