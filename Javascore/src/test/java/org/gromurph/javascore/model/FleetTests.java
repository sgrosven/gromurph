//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: FleetTests.java,v 1.6 2006/01/19 02:27:41 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;

public class FleetTests extends JavascoreTestCase
{

    public FleetTests(String s)
    {
        super(s);
    }

    Fleet f1;
    Fleet f2;

    @Override protected void setUp()
    {
        f1 = new Fleet("f1");

        f2 = new Fleet( "two");
        f2.setHandleDifferentLengths( Fleet.DIFFLENGTHS_DROP);
        f2.setSameStartSameClass( !f2.isSameStartSameClass());
    }

    public void testXml()
    {
        // default fleet goes to/from xml ok
        assertTrue( "f1", xmlEquals( f1));

        // f2 with no divisions, but delta props is OK
        assertTrue( "f2 empty", xmlEquals( f2));

        Division j24blue = new Division( "J24 Blue");
        Division j24green = new Division( "J24 Green");
        //SubDivision j24master = new SubDivision( "masters", j24green, false);
        f2.addDivision( j24blue);
        f2.addDivision( j24green);
        //f2.addDivision( j24master);
        assertTrue( "f2 2divs", xmlEquals( f2));
    }

    public void testAddDivision()
    {
        Fleet f = new Fleet();
        Division div1 = new Division( "J24 Blue");

        f.addDivision(div1);
        assertEquals( "added 1", f.getNumDivisions(), 1);

        f.addDivision( div1);
        assertEquals( "added same, still 1", f.getNumDivisions(), 1);
    }

    public void testRemoveDivision()
    {
        Fleet f = new Fleet();
        Division div1 = new Division( "J24 Blue");
        Division div2 = new Division( "J24 Green");
        Division div3 = new Division( "J24 Pink");

        f.addDivision( div1);
        f.addDivision( div2);
        assertEquals( "start with div1,div2", f.getNumDivisions(), 2);

        f.removeDivision( div2);
        assertEquals( "removed div2", f.getNumDivisions(), 1);

        f.removeDivision( div2);
        assertEquals( "removed div2 again", f.getNumDivisions(), 1);

        f.removeDivision( div3);
        assertEquals( "removed div3", f.getNumDivisions(), 1);

        f.removeDivision(div1);
        assertEquals( "removed div1", f.getNumDivisions(), 0);
    }

	private void forceDivision(Entry e, Division d)
	{
		try
		{
			e.setDivision( d);
		}
		catch (RatingOutOfBoundsException ex)
		{
		}
	}
	
	private void forceRating(Entry e, Rating d)
	{
		try
		{
			e.setRating( d);
		}
		catch (RatingOutOfBoundsException ex)
		{
		}
	}
	
    public void testContains()
    {
        Regatta reg = new Regatta();
        Entry one = new Entry();
        Entry two = new Entry();
        Entry three = new Entry();
        Division j24 = new Division("j24");
        Division PHRFA = new Division( "Phrf A", new RatingPhrf(-9999), new RatingPhrf(0));
        Division PHRFB = new Division( "Phrf B", new RatingPhrf(0), new RatingPhrf(30));

		forceDivision( one, j24);
        forceRating( one, new RatingOneDesign( "j24"));

		forceDivision( two, PHRFA);
        forceRating( two, new RatingPhrf( -10));

		forceDivision( three, PHRFB);
        forceRating( three, new RatingPhrf( 10));

        reg.addEntry( one);
        reg.addEntry( two);
        reg.addEntry( three);

        Fleet f = new Fleet();
        f.addDivision( PHRFA);

        assertTrue( "one is not in the fleet", !f.contains(one));
        assertTrue( "two is in the fleet", f.contains( two));
        assertTrue( "three not in fleet yet", !f.contains(three));

        f.addDivision( PHRFB);
        assertTrue( "three is now in fleet", f.contains(three));

		// TODO complete/improve testContains
        //  d) entry known to be in contained subdiv - return true
        //  e) entry known not to be in subdiv - return false
        //  f) entry in class in Fleet, but not in subdiv return false
    }

    public void testHandleDifferentLengths()
    {
        assertEquals( "f1 default is drop", f1.getHandleDifferentLengths(), Fleet.DIFFLENGTHS_DROP);

        f1.setHandleDifferentLengths( Fleet.DIFFLENGTHS_ASIS);
        assertEquals( "f1 change to asis", f1.getHandleDifferentLengths(), Fleet.DIFFLENGTHS_ASIS);

        // TODO  complete/improve testGetHandleDifferentLengths
        // d) given regatta with 2 races and entries in Fleet
        //      - races same length, resulting series has 2 races in it
        //      - races differently length, test each possibility?
        //
    }


    public void testIsSameStartSameClass()
    {
        assertTrue( "f1 default is true", f1.isSameStartSameClass());

        f1.setSameStartSameClass( false);
        assertTrue( "f1 default is false", !f1.isSameStartSameClass());

        // TODO complete improve  testIsSameStartSameClass
        /*  c) given regatta with 2 race, 4 entries, 2 in divA 2 in divB
         *       Fleet of A&B, race 1 has same starttime for both divs
         *       race 2 has diff startimes
         *      - test numberOfFinishers is correct when SSSC is true and false
         */
        //throw new java.lang.UnsupportedOperationException( "testIsSameStartSameClass not complete");
    }

    public void testshouldScoreRace() throws Exception
    {
            Regatta reg = loadTestRegatta( "0000-FleetPhrf-Test.regatta");

            Fleet fleet = reg.getFleet("PHRF Overall");

            assertTrue( "fleet is Prhf overall", fleet.getName().equals("PHRF Overall"));

            Race race1 = (Race) reg.getRaceIndex(0);

            fleet.setHandleDifferentLengths( Fleet.DIFFLENGTHS_ASIS);
            assertTrue( "fleet.asIs, should score true", fleet.shouldScoreRace( race1));

            fleet.setHandleDifferentLengths( Fleet.DIFFLENGTHS_DROP);
            assertTrue( "fleet.drop, should score false", !fleet.shouldScoreRace( race1));
    }
}
/**
* $Log: FleetTests.java,v $
* Revision 1.6  2006/01/19 02:27:41  sandyg
* fixed several bugs in split fleet scoring
*
* Revision 1.5  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.3  2006/01/15 03:25:51  sandyg
* to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.10  2005/05/26 01:46:51  sandyg
* fixing resource access/lookup problems
*
* Revision 1.9  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.8  2003/04/27 21:00:44  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.7  2003/03/30 00:03:58  sandyg
* gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
*
* Revision 1.6  2003/01/06 00:32:37  sandyg
* replaced forceDivision and forceRating statements
*
* Revision 1.5  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
