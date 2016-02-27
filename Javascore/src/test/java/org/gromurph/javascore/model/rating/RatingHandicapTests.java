//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingHandicapTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.rating;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingIrc;
import org.gromurph.javascore.model.ratings.RatingMorc;
import org.gromurph.javascore.model.ratings.RatingMultihull;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.javascore.model.ratings.RatingPortsmouth;


/**
 * Test class for handicap ratings (and one design)
 */
public class RatingHandicapTests extends org.gromurph.javascore.JavascoreTestCase
{
    public RatingHandicapTests( String name)
    {
        super(name);
    }

    public void testMultihull()
    {
    	Rating r = new RatingMultihull();
        Division div = new Division( "Multi",
            r.createSlowestRating(),
            r.createFastestRating());
        reg.addDivision( div);
        forceDivision( entry,div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("02:00:00"));

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        Rating rtg = new RatingMultihull( 1.000);
        forceRating( entry, rtg);
        assertEquals( "Multihull/1.000 corrected should be 10 hours",
            rtg.getCorrectedTime( finish),
            SailTime.forceToLong( "10:00:00"));

        rtg = new RatingMultihull( 0.900);
        forceRating( entry, rtg);
        assertEquals( "Multihull/0.900 corrected should be 9 hours",
            rtg.getCorrectedTime( finish),
            SailTime.forceToLong( "9:00:00"));

        rtg = new RatingMultihull( 1.100);
        forceRating( entry, rtg);
        assertEquals( "Multihull/1.100 corrected should be 11 hours",
            rtg.getCorrectedTime( finish),
            SailTime.forceToLong( "11:00:00"));

    }

    Boat boat;
    Regatta reg;
    Entry entry;

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
	
    public void testPhrf()
    {
        Division div = new Division( "Phrf",
            new RatingPhrf( -999),
            new RatingPhrf( 999));
        reg.addDivision( div);
        forceDivision( entry,div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("02:00:00"));
        race.setLength( div, 10.0);

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        Rating rtg = new RatingPhrf( 0);
        forceRating( entry, rtg);
        race.setLength( div, 10.0);
        long ct = rtg.getCorrectedTime( finish);
        assertEquals( "Phrf/0 for 10 miles",
            SailTime.forceToLong( "10:00:00"),
            ct);

        rtg = new RatingPhrf( 12);
        forceRating( entry, rtg);
        race.setLength( div, 10.0);
        ct = rtg.getCorrectedTime( finish);
        long ta = rtg.getTimeAllowance( finish);
        assertEquals( "Phrf/12 for 10 miles, time allow wrong",
            SailTime.forceToLong( "00:02:00"),
            ta);
        assertEquals( "Phrf/12 for 10 miles",
            SailTime.forceToLong( "09:58:00"),
            ct);

        rtg = new RatingPhrf( 12);
        forceRating( entry, rtg);
        race.setLength( div, 1.0);
        assertEquals( "Phrf/12 for 1 miles",
            SailTime.forceToLong( "10:00:00") - SailTime.forceToLong( "00:00:12"),
            rtg.getCorrectedTime( finish));

        rtg = new RatingPhrf( 1);
        forceRating( entry, rtg);
        race.setLength( div, 13.33);
        assertEquals( "Phrf/1 for 13.33 miles",
            rtg.getCorrectedTime( finish),
            SailTime.forceToLong( "10:00:00") - SailTime.forceToLong( "00:00:13"),
            rtg.getCorrectedTime( finish));

        rtg = new RatingPhrf( 1);
         forceRating( entry, rtg);
        race.setLength( div, 13.66);
        assertEquals( "Phrf/1 for 13.66 miles",
                "09:59:46.3",
                SailTime.toString( rtg.getCorrectedTime( finish)));

        rtg = new RatingPhrf( -1);
        forceRating( entry, rtg);
        race.setLength( div, 13.33);
        assertEquals( "Phrf/1 for 13.33 miles",
                "10:00:13.3",
                SailTime.toString( rtg.getCorrectedTime( finish)));


        rtg = new RatingPhrf( -1);
        forceRating( entry, rtg);
        race.setLength( div, 13.66);
        assertEquals( "Phrf/1 for 13.66 miles",
                "10:00:13.7",
                SailTime.toString( rtg.getCorrectedTime( finish)));

    }

	private void forceDivision( Entry e, Division d)
	{
		try
		{
			e.setDivision(d);
		}
		catch (RatingOutOfBoundsException ex)
		{
		}
	}
	
    public void testMorc()
    {
        Division div = new Division( "Morc",
            new RatingMorc( -999),
            new RatingMorc( 999));
        reg.addDivision( div);
        forceDivision( entry, div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("02:00:00"));
        race.setLength( div, 10.0);

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        Rating rtg = new RatingMorc( 0);
        forceRating(entry, rtg);
        race.setLength( div, 10.0);
        assertEquals( "Morc/0 for 10 miles",
            SailTime.forceToLong( "10:00:00"),
            rtg.getCorrectedTime( finish));

        rtg = new RatingMorc( 12);
        forceRating(entry, rtg);
        race.setLength( div, 10.0);
        assertEquals( "Morc/12 for 10 miles",
            SailTime.forceToLong( "10:00:00")-SailTime.forceToLong( "00:02:00"),
            rtg.getCorrectedTime( finish));

        rtg = new RatingMorc( 12);
        forceRating(entry, rtg);
        race.setLength( div, 1.0);
        assertEquals( "Morc/12 for 1 miles",
            SailTime.forceToLong( "10:00:00") - SailTime.forceToLong( "00:00:12"),
            rtg.getCorrectedTime( finish));

        rtg = new RatingMorc( 1);
        forceRating(entry, rtg);
        race.setLength( div, 13.33);
        assertEquals( "Morc/1 for 13.33 miles",
                "09:59:46.7",
                SailTime.toString( rtg.getCorrectedTime( finish)));

        rtg = new RatingMorc( 1);
        forceRating(entry, rtg);
        race.setLength( div, 13.66);
        assertEquals( "Morc/1 for 13.66 miles",
            "09:59:46.3",
            SailTime.toString( rtg.getCorrectedTime( finish)));

        rtg = new RatingMorc( -1);
        forceRating(entry, rtg);
        race.setLength( div, 13.33);
        assertEquals( "Morc/1 for 13.33 miles",
                "10:00:13.3",
	            SailTime.toString( rtg.getCorrectedTime( finish)));


        rtg = new RatingMorc( -1);
        forceRating(entry, rtg);
        race.setLength( div, 13.66);
        assertEquals( "Morc/1 for 13.66 miles",
        		"10:00:13.7",
	            SailTime.toString( rtg.getCorrectedTime( finish)));

    }

    public void testOneDesign()
    {
        Division div = new Division( "1d");
        reg.addDivision( div);
        forceDivision( entry,div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("02:00:00"));
        race.setLength( div, 10.0);

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        Rating rtg = new RatingOneDesign( "1d");
        forceRating(entry, rtg);
        assertEquals( "1d for undetermined miles",
            finish.getElapsedTime(),
            rtg.getCorrectedTime( finish));

        race.setLength( div, 10.0);
        assertEquals( "1d for 10 miles",
            finish.getElapsedTime(),
            rtg.getCorrectedTime( finish));

        race.setLength( div, 10.0);
        race.setStartTime( div, SailTime.NOTIME);
        assertEquals( "1d for 10 miles no starttime",
            finish.getFinishTime(),
            rtg.getCorrectedTime( finish));

        finish.setFinishTime( SailTime.NOTIME);
        assertEquals( "1d for 10 miles no starttime no finishtime",
            finish.getFinishTime(),
            rtg.getCorrectedTime( finish));

   }

    /**
     Are you telling me to reverse PHRF 2 and PHRF 3? Something seems to be flipped, particularly PHRF 1. The +9999,-9999 looks like a work around for a bug in the code. Here is what I get: 
		PHRF 2 = 118 Min, 150 Max (works) 
		PHRF 3 = 160 Min, 999 Max (works but why isn't the Min 151?) 
		PHRF 1 = +9999 Min, -9999 Max (looks wrong but works in ToD) 
		PHRF 1 = +9999 Min, -9999 Max (ejects the boats in ToT) 
		PHRF 1 = -999 Min, 117 (matches P2 and P3 entry style, but does not work) 
     */
    public void testPhrfTimeOnTimeDivisions() throws Exception
    {
        Division div1 = new Division( "TOT 1",
                new RatingPhrfTimeOnTime( -999),
                new RatingPhrfTimeOnTime( 117));
            reg.addDivision( div1);
            Division div2 = new Division( "TOT 2",
                    new RatingPhrfTimeOnTime( 118),
                    new RatingPhrfTimeOnTime( 150));
                reg.addDivision( div2);
            Division div3 = new Division( "TOT 3",
                    new RatingPhrfTimeOnTime( 160),
                    new RatingPhrfTimeOnTime( 999));
                reg.addDivision( div3);

        Boat b1 = new Boat();
        Entry e1 = new Entry();
        e1.setBoat( b1);
        e1.setRating( new RatingPhrfTimeOnTime( 0));
        
        Boat b2 = new Boat();
        Entry e2 = new Entry();
        e2.setBoat( b2);
        e2.setRating( new RatingPhrfTimeOnTime( 120));
        
        Boat b3 = new Boat();
        Entry e3 = new Entry();
        e3.setBoat( b3);
        e3.setRating( new RatingPhrfTimeOnTime( 170));

        Boat b11 = new Boat();
        Entry e11 = new Entry();
        e11.setBoat( b11);
        e11.setRating( new RatingPhrfTimeOnTime( 115));
        
        e1.setDivision(div1);
        e11.setDivision(div1);
        e2.setDivision(div2);
        e3.setDivision(div3);

        reg.addEntry(e1);
        reg.addEntry(e11);
        reg.addEntry(e2);
        reg.addEntry(e3);

        assertEquals( 2, reg.getAllEntries().findAll(div1).size());
        assertEquals( 1, reg.getAllEntries().findAll(div2).size());
        assertEquals( 1, reg.getAllEntries().findAll(div3).size());

    }

    
    
    public void testPhrfTimeOnTime()
    {
        Division div = new Division( "PhrfTimeOnTime",
            new RatingPhrfTimeOnTime( -999),
            new RatingPhrfTimeOnTime( 999));
        reg.addDivision( div);
        forceDivision( entry,div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("11:00:00"));
        race.setLength( div, 10.0);
        race.setAFactor( -1);
        race.setBFactor( -1);

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        // with rating of 0, corrected = elapsed time
        Rating rtg = new RatingPhrfTimeOnTime( 0);
        forceRating(entry, rtg);
        assertEquals( "PhrfTimeOnTime rating of 0",
            finish.getElapsedTime(),
            rtg.getCorrectedTime( finish));

        // with a bfactor of 0, corrected time will be 0
        rtg = new RatingPhrfTimeOnTime( 1);
        forceRating(entry, rtg);
        race.setBFactor( 0);
        finish.setFinishTime( SailTime.forceToLong( "11:01:00"));
        assertEquals( "PhrfTimeOnTime bfactor of 0",
            0,
            rtg.getCorrectedTime( finish));

        // with a bfactor of 1, corrected time will be elapsed/ (rating+1)
        rtg = new RatingPhrfTimeOnTime( 1);
        forceRating(entry, rtg);
        race.setBFactor( 1);
        finish.setFinishTime( SailTime.forceToLong( "12:00:00"));
        assertEquals( "PhrfTimeOnTime bfactor of 1",
            SailTime.forceToLong( "0:30:00"),
            rtg.getCorrectedTime( finish));
    }

	public void testIrc()
	{
    	Rating r = new RatingIrc();
        Division div = new Division( "Irc",
            r.createSlowestRating(),
            r.createFastestRating());
	    reg.addDivision( div);
	    forceDivision( entry,div);
	
	    Race race = new Race();
	    race.setStartTime( div, SailTime.forceToLong("11:00:00"));
	    race.setLength( div, 10.0);
	
	    long noon = SailTime.forceToLong( "12:00:00");
	    Finish finish = new Finish( race, entry, noon,
	        new FinishPosition( Constants.NO_PENALTY), null);
	
	    // with rating of 0, corrected = elapsed time
	    Rating rtg = new RatingIrc( 0.50);
	    forceRating(entry, rtg);
	    assertEquals( "Irc rating of 0.50",
	        finish.getElapsedTime(),
	        rtg.getCorrectedTime( finish)*2);
	
	    // with rating of 0, corrected = elapsed time
	    rtg = new RatingIrc( 2.00);
	    forceRating(entry, rtg);
	    assertEquals( "Irc rating of 2.00",
	        finish.getElapsedTime(),
	        rtg.getCorrectedTime( finish)/2);
	
	}

    public void testPortsmouth()
    {
        Division div = new Division( "Portsmouth",
            new RatingPortsmouth( -999),
            new RatingPortsmouth( 999));
        reg.addDivision( div);
        forceDivision( entry,div);

        Race race = new Race();
        race.setStartTime( div, SailTime.forceToLong("11:00:00"));
        race.setLength( div, 10.0);

        long noon = SailTime.forceToLong( "12:00:00");
        Finish finish = new Finish( race, entry, noon,
            new FinishPosition( Constants.NO_PENALTY), null);

        // with rating of 1, corrected = elapsed time *100
        Rating rtg = new RatingPortsmouth( 1.0);
        forceRating(entry, rtg);
        assertEquals( "Portsmouth rating of 1.0",
            finish.getElapsedTime() *100,
            rtg.getCorrectedTime( finish));

        // with rating of 2, corrected = elapsed time / 2 * 100
        rtg = new RatingPortsmouth( 2.0);
        forceRating(entry, rtg);
        assertEquals( "Portsmouth rating of 2.0",
            finish.getElapsedTime() * 50,
            rtg.getCorrectedTime( finish));
    }

    @Override  public void setUp() throws Exception
    {
    	super.setUp();
        boat = new Boat();
        entry = new Entry();
        entry.setBoat( boat);

        reg = new Regatta();
        reg.removeAllDivisions();
        JavaScoreProperties.setRegatta(reg);
    }

}
/**
* $Log: RatingHandicapTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.9  2005/02/27 23:24:37  sandyg
* added IRC, changed corrected time calcs to no longer round to a second
*
* Revision 1.8  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.7  2003/04/27 21:00:49  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.6  2003/04/20 15:44:30  sandyg
* added javascore.Constants to consolidate penalty defs, and added
* new penaltys TIM (time value penalty) and TMP (time percentage penalty)
*
* Revision 1.5  2003/03/16 20:39:44  sandyg
* 3.9.2 release: encapsulated changes to division list in Regatta,
* fixed a bad bug in PanelDivsion/Rating
*
* Revision 1.4  2003/01/06 00:32:37  sandyg
* replaced forceDivision and forceRating statements
*
* Revision 1.3  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
