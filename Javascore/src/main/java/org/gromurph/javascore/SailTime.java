// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SailTime.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore;

import java.text.*;
import java.util.Date;

/**
 * all static class for formating dates and times, this allows the times themselves to get passed around as primitive
 * long items.
 * 
 * NOTE: this class is not yet able to deal with dates over 24 hours... the world for it is only 1 day long.
 **/
public class SailTime {
    private static SimpleDateFormat fmt8 = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat fmtMillis = new SimpleDateFormat("S");
    private static DecimalFormat fmtDot1 = new DecimalFormat(".#");

    public static long NOTIME = Long.MAX_VALUE;
    public static String NOTIME_STRING = "No Time";
    /**
     * provides the 'template' for partial times coming in
     */
    public static final long DAYINMILLIS = 24 * 60 * 60 * 1000; // number of milleseconds in a 24 hour day
    private static String sLastTime;
    private static int sLastDays;
    private static long sTimeZero;
    private static boolean sIsLongDistance;

    static {
	sLastTime = "00:00:00";
	sLastDays = 0;
	sTimeZero = SailTime.forceToLong("0/00:00:00");
	sIsLongDistance = false;
    }

    public static void clearLastTime() {
	sLastTime = "00:00:00";
	sLastDays = 0;
    }

    public static long getLastTime() {
	return forceToLong(sLastTime);
    }

    public static void setLongDistance(boolean b) {
	sIsLongDistance = b;
    }

    public static boolean isLongDistance() {
	return sIsLongDistance;
    }

    public static String toSeconds(long dt) {
    	boolean isnegative = (dt < 0);
    	if (isnegative)
    	    dt = -dt;
    
    	StringBuffer sb = new StringBuffer(10);
    
    	if (dt == NOTIME) // || dt == 0)
    	{
    	    sb.append(NOTIME_STRING);
    	} else {
    	    if (isnegative)
    		sb.append("-");
    
    	    if (sIsLongDistance || (dt > DAYINMILLIS)) {
    		// more than 1 day
    		int days = (int) (dt / DAYINMILLIS);
    		dt = dt - (days * DAYINMILLIS);
    		sb.append(days);
    		sb.append("/");
    	    }
    
    	    Date dd = new Date(dt + sTimeZero);
    	    double ms = (Double.parseDouble(fmtMillis.format(dd))) / 1000;
    		// millis do not roll up, tack the decimal millis onto end of string
    		sb.append(fmt8.format(dd));
     	}
    	return sb.toString();
    }
    
     /**
     * converts the long date/time to a string NYI: following a globally set format
     **/
    public static String toString(long dt) {
    	boolean isnegative = (dt < 0);
    	if (isnegative)
    	    dt = -dt;
    
    	StringBuffer sb = new StringBuffer(10);
    
    	if (dt == NOTIME) // || dt == 0)
    	{
    	    sb.append(NOTIME_STRING);
    	} else {
    	    if (isnegative)
    		sb.append("-");
    
    	    if (sIsLongDistance || (dt > DAYINMILLIS)) {
    		// more than 1 day
    		int days = (int) (dt / DAYINMILLIS);
    		dt = dt - (days * DAYINMILLIS);
    		sb.append(days);
    		sb.append("/");
    	    }
    
    	    Date dd = new Date(dt + sTimeZero);
    	    double ms = (Double.parseDouble(fmtMillis.format(dd))) / 1000;
    	    String dot1 = fmtDot1.format(ms);
    
    	    if (dot1.equals("1.0")) {
    		// millis done at 1 dec round up to 1.0 seconds... eg "53.997" show "54.0"
    		sb.append(fmt8.format(new Date(dt + sTimeZero + 1000)));
    		sb.append(".0");
    	    } else {
    		// millis do not roll up, tack the decimal millis onto end of string
    		sb.append(fmt8.format(dd));
    		sb.append(dot1);
    	    }
    	}
    	return sb.toString();
    }

    /**
     * converts a string to a long date NYI: following a globally set format
     * 
     * @throws ParseException
     **/
    public static long toLong(String inDate) throws ParseException {
	String dt = inDate.trim();

	if (dt.equals(NOTIME_STRING))
	    return NOTIME;
	boolean isNegative = dt.startsWith("-");
	if (isNegative)
	    dt = dt.substring(1);

	int days = sLastDays;
	String timeBase = sLastTime;
	int baseindex = 0;
	if (sIsLongDistance) {
	    baseindex = 2;
	} else {
	    days = 0;
	    baseindex = 0;
	}

	int slash = dt.indexOf("/");
	if (slash >= 0) {
	    // look to pull off a number of days
	    String left = dt.substring(0, slash);
	    dt = dt.substring(slash + 1);
	    days = Integer.parseInt(left);
	}

	StringBuffer full = new StringBuffer();
	if (dt.length() == 2) // "ss"
	{
	    full.append(timeBase.substring(baseindex, baseindex + 6));
	    full.append(dt);
	} else if (dt.length() == 5) // "mm:ss"
	{
	    full.append(timeBase.substring(baseindex, baseindex + 3));
	    full.append(dt);
	} else if (dt.length() == 4) // "mmss"
	{
	    full.append(timeBase.substring(baseindex, baseindex + 3));
	    full.append(dt.substring(0, 2));
	    full.append(":");
	    full.append(dt.substring(2));
	} else if (dt.length() == 6) // "hhmmss"
	{
	    full.append(dt.substring(0, 2));
	    full.append(":");
	    full.append(dt.substring(2, 4));
	    full.append(":");
	    full.append(dt.substring(4));
	} else // hh:mm:ss or anything else that feels good
	{
	    full.append(dt);
	}

	String fullTime = full.toString();
	long newtime = fmt8.parse(fullTime).getTime();

	if ((newtime - sTimeZero) > DAYINMILLIS) {
	    // the input string has hours over 24 hours,
	    // adjust the date accordingly but leave the "sLastDays" alone
	    int moredays = (int) ((newtime - sTimeZero) / DAYINMILLIS);
	    newtime = newtime - (moredays * DAYINMILLIS);
	    days = days + moredays;
	} else if (sIsLongDistance) {
	    sLastDays = days;
	}

	if (sIsLongDistance || (days > 0)) {
	    timeBase = Integer.toString(days) + "/";
	    long daysInMilliseconds = DAYINMILLIS * days;
	    newtime = newtime + daysInMilliseconds;
	    full.insert(0, "/");
	    full.insert(0, days);
	}

	sLastTime = full.toString();

	return (newtime - sTimeZero) * (isNegative ? -1 : 1);
    }

    /**
     * convers a string to a date and eats any exception. Returns 0 if an exception was encountered NYI: following a
     * globally set format
     **/
    public static long forceToLong(String dt) {
	try {
	    return toLong(dt);
	} catch (Exception e) {
	    return 0;
	}
    }

    public static String NODATE_STRING = "No Date";

    private static SimpleDateFormat dateFmt = new SimpleDateFormat("dd-MMM-yyyy");

    public static Date stringToDate(String inString) throws ParseException {
	sLastDays = 0;
	return dateFmt.parse(inString);
    }

    public static Date forceStringToDate(String inString) {
	try {
	    return dateFmt.parse(inString);
	} catch (ParseException e) {
	    return null;
	}
    }

    public static String dateToString(Date inDate) {
	if (inDate == null)
	    return NODATE_STRING;
	return dateFmt.format(inDate);
    }
}
/**
 * $Log: SailTime.java,v $ Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.9.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.9.2.1 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121, 1226607, killed Java Web Start startup
 * code
 * 
 * Revision 1.9 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 * 
 * Revision 1.8 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.7 2003/04/27 21:03:28 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.6 2003/04/23 00:30:20 sandyg added Time-based penalties
 * 
 * Revision 1.5 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.4 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
