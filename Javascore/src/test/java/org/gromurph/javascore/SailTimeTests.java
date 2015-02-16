//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SailTimeTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.util.Util;

/**
 * Dummy template class for create unit test cases
 */
public class SailTimeTests extends JavascoreTestCase
{

	@Override protected void setUp() throws ScoringException, java.io.IOException
    {
		Util.initLocale( Locale.US);
        SailTime.setLongDistance(false);
        SailTime.clearLastTime();
    }

    public void testDecimalSeconds() throws java.text.ParseException
    {
    	// long=286733997, str=3/07:38:531.0

    	long t = 286733997;
    	String badTime="3/07:38:531.0";
    	String goodTime = "3/07:38:54.0";
    	
    	String timeOut = SailTime.toString( t);
    	assertTrue( !timeOut.equals( badTime));
    	assertEquals( goodTime, timeOut);
    }
    
    public void testAmTimes() throws java.text.ParseException
    {

        String t = "03:04:05";
        assertEquals( "03:04:05.0", SailTime.toString(SailTime.toLong(t)));

        t = "1300";
        assertEquals( "03:13:00.0", SailTime.toString(SailTime.toLong(t)));

        t = "131100";
        assertEquals( "13:11:00.0", SailTime.toString(SailTime.toLong(t)));

        t = "03";
        assertEquals( "13:11:03.0", SailTime.toString(SailTime.toLong(t)));
    }

    public void testNegativeTimes() throws java.text.ParseException
    {
    	 SailTime.setLongDistance(false);
        long pos = SailTime.forceToLong( "0:00:10");
        long neg = -pos;

        String str = SailTime.toString( neg);
        assertEquals( "neg 10 not right", "-00:00:10.0", str);
                
		String neg5 = "-00:05:00.0";
		// should be -5*60*1000 = -300000;
		long lneg5 = SailTime.forceToLong( neg5);
		assertEquals( "bad neg 5", -300000, lneg5);                
    }

    public void testPmTimes() throws java.text.ParseException
    {
        SailTime.setLongDistance(true);
        SailTime.clearLastTime();

        String t = "03:04:05";
        String rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/03:04:05.0", rt);

        t = "1/03:04:05";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/03:04:05.0", rt);

        t = "15";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/03:04:15.0", rt);

        t = "5555";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/03:55:55.0", rt);

        t = "27:04:05";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "2/03:04:05.0", rt);

        t = "0/23:04:05";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/23:04:05.0", rt);

        t = "27:04:05";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/03:04:05.0", rt);

        t = "2/12:01:02.0";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( t, rt);

        t = "13:10:10";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "2/13:10:10.0", rt);

        t = "0/12:00:00";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/12:00:00.0", rt);

        t = "27:00:00";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/03:00:00.0", rt);

        t = "12:10:20";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/12:10:20.0", rt);

        t = "28:10:10";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/04:10:10.0", rt);

        t = "30:10:10";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "1/06:10:10.0", rt);

        t = "0/18:55:00";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/18:55:00.0", rt);

        t = "19:00:00";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/19:00:00.0", rt);

        t = "19:10:00";
        rt = SailTime.toString(SailTime.toLong(t));
        assertEquals( "0/19:10:00.0", rt);
    }

    public void testDates() throws java.text.ParseException
    {
    	assertEquals( "local not us", Locale.US, Locale.getDefault());
        String t = "01-Jan-2000";
        try
        {        
        	Date date = SailTime.stringToDate(t);
			String dateString = SailTime.dateToString( date);
			assertEquals( "noon", t, dateString);
        }
        catch (ParseException e)
        {
        	fail( e.toString());
        }
        
    }
    
    public void testBadTimes()
    {
    	String t = "";     	
    	String rt = "";
    	
    	try
    	{        
	    	t="00:13:12";
	    	SailTime.setLongDistance(false);
			rt = SailTime.toString(SailTime.toLong(t));
			assertEquals( "00:13:12.0", rt);	   	

	    	SailTime.setLongDistance(true);
			rt = SailTime.toString(SailTime.toLong(t));
			assertEquals( "0/00:13:12.0", rt);	   	
    	}
		catch (ParseException e)
		{
			fail( e.toString());
		}
	    	
		try
		{        
			t="13:1200";
			rt = SailTime.toString(SailTime.toLong(t));
			fail("should have generated an error");
    	}
		catch (ParseException e)
		{
			// ok to be here
		}
    }

    public SailTimeTests( String name)
    {
        super(name);
    }

}
/**
* $Log: SailTimeTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.8  2005/08/19 01:52:34  sandyg
* 4.3.1.03 tests
*
* Revision 1.7  2005/02/27 23:24:37  sandyg
* added IRC, changed corrected time calcs to no longer round to a second
*
* Revision 1.6  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.5  2003/04/30 00:57:54  sandyg
* added some bad time tests
*
* Revision 1.4  2003/04/27 21:00:54  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/04/23 00:30:21  sandyg
* added Time-based penalties
*
* Revision 1.2  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
