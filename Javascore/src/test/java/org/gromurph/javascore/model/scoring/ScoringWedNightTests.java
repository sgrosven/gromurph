//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SeriesPointsList;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringWedNightTests extends JavascoreTestCase
{

    public ScoringWedNightTests(String s)
    {
        super(s);
    }

	private void forceDivision( Entry ent, Division d)
	{
		try
		{
			ent.setDivision(d);
		}
		catch (RatingOutOfBoundsException e)
		{
		}
	}
	
	@Override protected void setUp() throws Exception
    {
    }

	public void testScoringPenalties() throws Exception
	{
		
		/* with bug reported May 2015
		 * 	issue is with harbor20 #340 in race 2
		 * finished 3rd, took a 10% scp, and gets 5 pts
		 * 10 finishers, 17 entrants
		 * 
		 * WNR rules are 10% of finishers (10) (not entries) a minimum of 1 point
		 * Under WNR, should get 1 point (10% of 10 = 1)
		 * Under low point, he should get 2 points (10% of 17 = 1.7 rounds up to 2
		 */
		
	    Regatta reg;
	    Division div;
	    Entry e340;
	    Entry e2;
	    Entry e3;

		reg = loadTestRegatta( "2015WNR_test.regatta");

/*
Pos, Sail, Boat, Finish, Adj, Pts
1  , 339, H20, 1,  , 1.0
2  , 311, SKIMMER, 2,  , 2.0
3  , 325, Puffin, 4,  , 4.0
4T  , 340, Velella, 3, 10%, 5.0T
4T  , 321, MEADEOR, 5,  , 5.0T
6  , 332, Queen Ann(e), 6,  , 6.0
7  , 162, Mofongo, 7,  , 7.0
8  , 319, Fuzzy Logic, 8,   , 8.0
9  , 341, Bullforg, 9,  , 9.0
10  , 205, Elixir, 10,  , 10.0
and 7 boats all DNC  , 132, 11 points
 */
		
		// BUGS (while trying to investigate AYC Wed Penalty issue
		//  Finish dialog
		//      putting 'nofin' on the finish side!
		//      very buggy tracking of who has finished and who hasnt
		//      close dialog, and return does not give same results
		//  AYC scoring penalties wrong: 
		//		10% of 7 yielding 0 pt penalty (should be 1);  
		//      original complaint from 7.2.2: 10% 0f 10 yielding 2 pts (should be 1)

		div = reg.getDivision("Harbor 20");
		assertNotNull( div);
		
		EntryList h20s = reg.getAllEntries().findAll(div);
		assertEquals( 17, h20s.size());
		
		EntryList l = reg.getAllEntries().findSail( new SailId("340"));
		assertTrue( l.size() == 1);
		e340 = l.get(0);
		assertNotNull( e340);
		
		// set scoring to standard low point

		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);
		(( ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel()).getOptions().setLongSeries( true);
		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		
		Race race = reg.getRace("2");
		assertNotNull( race);
		
		assertEquals( 10, race.getNumberFinishers(div));
				
		// 340 should have finished 3rd in the race
		Finish fin1 = race.getFinish( e340);
		assertNotNull( fin1);
		FinishPosition fp1 = fin1.getFinishPosition();
		assertNotNull( fp1);
		assertTrue( fp1.isFinisher());
		assertEquals( 3, fp1.longValue());
		
		// 340 should have a 10% scoring penalty
		Penalty pen1 = fin1.getPenalty();
		assertNotNull( pen1);
		assertTrue( pen1.isOtherPenalty());
		assertEquals( Penalty.SCP, pen1.getPenalty());
		assertEquals( 10, pen1.getPercent());
		
		// 340 should have 5 points
		RacePoints pts1 = rpl.find( race,  e340, div);
		assertNotNull( pts1);
		assertEquals( 5.0, pts1.getPoints(), 0.00001);

		
		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPointAYCWednesday.NAME);
		reg.scoreRegatta();

		// now our boy should have 4 points
		pts1 = rpl.find( race,  e340, div);
		assertNotNull( pts1);
		assertEquals( 4.0, pts1.getPoints(), 0.00001);


	}

	
	public void testSeriesAndRaceScoring() throws Exception
	{
		
		/* with bug reported May 2015
		 * 	issue is with harbor20 #340 in race 2
		 * finished 3rd, took a 10% scp, and gets 5 pts
		 * 10 finishers, 17 entrants
		 * 
		 * WNR rules are 10% of finishers (10) (not entries) a minimum of 1 point
		 * Under WNR, should get 1 point (10% of 10 = 1)
		 * Under low point, he should get 2 points (10% of 17 = 1.7 rounds up to 2
		 */
		
	    Regatta reg;
	    Division div;
	    Entry e340;
	    Entry e2;
	    Entry e3;

		reg = loadTestRegatta( "20152016FrostbiteSeriesSecondHalf.regatta");
		
		reg.scoreRegatta();
		SeriesPointsList splAll = reg.getScoringManager().getAllSeriesPoints();
		assertTrue( splAll.size() > 0);
		
		div = reg.getDivision("Cal 25");
		assertNotNull( div);
		
		EntryList cal25s = reg.getAllEntries().findAll(div);
		assertEquals( 11, cal25s.size()); // should be 11
		
		SeriesPointsList spl = reg.getScoringManager().getAllSeriesPoints(div);
		assertNotNull(spl);
		assertEquals( cal25s.size(), spl.size());
	}
}

