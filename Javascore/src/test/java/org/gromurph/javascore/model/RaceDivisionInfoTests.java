//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RaceDivisionInfoTests.java,v 1.5 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.SailTime;



public class RaceDivisionInfoTests extends org.gromurph.javascore.JavascoreTestCase
{
 
    public RaceDivisionInfoTests( String name)
    {
        super(name);
    }

    public void testEarliestStartTime()
    {
        Regatta reg = new Regatta();

        Division div1 = new Division( "One");
        reg.removeAllDivisions();
        reg.addDivision( div1);

        Division div2 = new Division( "Two");
        reg.removeAllDivisions();
        reg.addDivision( div2);

        Race r1 = new Race( reg, "1");

        // no start times, should get 0
        assertEquals( SailTime.NOTIME, r1.getEarliestStartTime());

        // set one start times, make sure we get right earliest
        r1.setStartTime( div2, SailTime.forceToLong("121000"));
        assertEquals( SailTime.forceToLong("121000"), r1.getEarliestStartTime());

        // set 2nd earlier time, make sure we get right earliest
        r1.setStartTime( div1, SailTime.forceToLong("120000"));
        assertEquals( SailTime.forceToLong("120000"), r1.getEarliestStartTime());

        Race r2 = new Race( reg, "2");
        Race r3 = new Race( reg, "3");

        // now set start times, where 3 starts before 2
        r1.setStartTime( div1, SailTime.forceToLong("120000"));
        r3.setStartTime( div1, SailTime.forceToLong("120500"));
        r2.setStartTime( div1, SailTime.forceToLong("121000"));

        // add races to regatta, intentionally setting the wrong order
        RaceList rlist = new RaceList();
        rlist.add( r3);
        rlist.add( r1);
        rlist.add( r2);

        // resort, see if order is OK
        rlist.sort();

        assertEquals(  "1", ( (Race) rlist.get(0)).getName());
        assertEquals(  "3", ( (Race) rlist.get(1)).getName());
        assertEquals(  "2", ( (Race) rlist.get(2)).getName());
    }

    public void testXml() throws java.io.IOException
    {
        RaceDivisionInfo one = new RaceDivisionInfo( true);
        Division d1 = new Division( "J24");
        one.setLength( d1, 3.0);
        one.setStartTime( d1, SailTime.forceToLong("12:01:05"));

        assertTrue( "one xml test", xmlEquals( one));
    }

 }
/**
* $Log: RaceDivisionInfoTests.java,v $
* Revision 1.5  2006/05/19 05:48:43  sandyg
* final release 5.1 modifications
*
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.6  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.5  2003/04/27 21:00:46  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.4  2003/03/16 20:39:44  sandyg
* 3.9.2 release: encapsulated changes to division list in Regatta,
* fixed a bad bug in PanelDivsion/Rating
*
* Revision 1.3  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
