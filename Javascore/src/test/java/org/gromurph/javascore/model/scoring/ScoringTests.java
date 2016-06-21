//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringTests.java,v 1.8 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.manager.ReportViewer;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringTests extends JavascoreTestCase implements Constants {
	public void test573() throws Exception {
		doFullCheck("0000-Test-v573.regatta", true);
	}

	public void test707() throws Exception {
		doFullCheck("0000-Test-v707.regatta", true);
	}

	private Entry getEntry(Regatta r, String sail) {
		return r.getAllEntries().findId(sail).get(0);
	}

	public void testRaceSortOrder() {
		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions();
		Division div = new Division("J24");
		reg.addDivision(div);

		Entry e1 = null;
		Entry e2 = null;
		try {
			e1 = new Entry();
			e1.setSailId(new SailId("10"));
			e1.setDivision(div);
			e2 = new Entry();
			e2.setSailId(new SailId("20"));
			e2.setDivision(div);
		} catch (RatingOutOfBoundsException e) {
		}

		reg.addEntry(e1);
		reg.addEntry(e2);

		Race r1 = new Race(reg, "1");
		Race r2 = new Race(reg, "2");
		Race r3 = new Race(reg, "3");

		reg.addRace(r1);
		reg.addRace(r3);
		reg.addRace(r2);

		// these were intentionally added in a bad order, when scored, the order
		// should be fixed
		reg.scoreRegatta();

		assertEquals("1", reg.getRaceIndex(0).getName());
		assertEquals("2", reg.getRaceIndex(1).getName());
		assertEquals("3", reg.getRaceIndex(2).getName());

		// now set start times, where 3 starts before 2
		r1.setStartTime(div, SailTime.forceToLong("120000"));
		r3.setStartTime(div, SailTime.forceToLong("120500"));
		r2.setStartTime(div, SailTime.forceToLong("121000"));
		reg.scoreRegatta();

		assertEquals("1", reg.getRaceIndex(0).getName());
		assertEquals("3", reg.getRaceIndex(1).getName());
		assertEquals("2", reg.getRaceIndex(2).getName());

	}

	public void testOCSFinishPosition() throws Exception {
			// bug in 4.0.2, 4.3.1.02 - fixed in 4.3.1.03
			Regatta reg = loadTestRegatta( "OCSPositionTest.regatta");
			((SingleStageScoring) reg.getScoringManager()).setModel(ScoringLowPoint.NAME);

			reg.scoreRegatta();
			Division albergs = reg.getDivision("Alberg30");
			assertNotNull(albergs);

			Division cals = reg.getDivision("Cal25");
			assertNotNull(cals);

			Race race = reg.getRace("1");
			RacePointsList racepts = reg.getScoringManager().getRacePointsList();

			/**
			 * WAS: Pos Sail/Boat Rating Order Time Pen Pts 1 a 1 Alberg30 1 No
			 * Time 1.0 2 a 2 Alberg30 2 No Time 2.0 3 a 5 Alberg30 5 No Time
			 * OCS 4.0 Pos Sail/Boat Rating Order Time Pen Pts 1 c 4 Cal25 2 No
			 * Time 1.0 2 c 6 Cal25 3 No Time 2.0 3 c 3 Cal25 3 No Time OCS 4.0
			 * SHOULD BE: Pos Sail/Boat Rating Order Time Pen Pts 1 a 1 Alberg30
			 * 1 No Time 1.0 2 a 2 Alberg30 2 No Time 2.0 3 a 5 Alberg30 3 No
			 * Time OCS 4.0 Pos Sail/Boat Rating Order Time Pen Pts 1 c 4 Cal25
			 * 2 No Time 1.0 2 c 6 Cal25 3 No Time 2.0 3 c 3 Cal25 1 No Time OCS
			 * 4.0
			 **/

			String sail = "a 5";
			int expectedPos = 3;
			Entry entry = getEntry(reg, sail);
			assertNotNull(entry);
			RacePoints pts = racepts.find(race, entry, albergs);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());

			sail = "a 1";
			expectedPos = 1;
			entry = getEntry(reg, sail);
			assertNotNull(entry);
			pts = racepts.find(race, entry, albergs);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());

			sail = "a 2";
			expectedPos = 2;
			entry = getEntry(reg, sail);
			assertNotNull(entry);
			pts = racepts.find(race, entry, albergs);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());

			sail = "c 4";
			expectedPos = 2;
			entry = getEntry(reg, sail);
			assertNotNull(entry);
			pts = racepts.find(race, entry, cals);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());

			sail = "c 6";
			expectedPos = 3;
			entry = getEntry(reg, sail);
			assertNotNull(entry);
			pts = racepts.find(race, entry, cals);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());

			sail = "c 3";
			expectedPos = 1;
			entry = getEntry(reg, sail);
			assertNotNull(entry);
			pts = racepts.find(race, entry, cals);
			assertNotNull(pts);
			assertEquals(sail + " finish order wrong", expectedPos, pts.getPosition());
	}

	public void testCbyraSampler() throws Exception {
		Regatta reg = loadTestRegatta( "Cbyra_Sampler.regatta");
		((SingleStageScoring) reg.getScoringManager()).setModel(ScoringLowPoint.NAME);

		ScoringOptions scoringOptions = (ScoringOptions) ((SingleStageScoring) reg.getScoringManager()).getModel().getOptions();

		// eliminate the throw out
		scoringOptions.setThrowout(0, 0);
		scoringOptions.setThrowout(1, 0);
		scoringOptions.setThrowout(2, 0);

		reg.scoreRegatta();

		// race 1 has 4 classes, total of 11 entries

		Division p0 = reg.getDivision("PHRF A0");
		RacePointsList racepts = reg.getScoringManager().getRacePointsList();

		// -- what we want
		// Pos Sail Boat Skipper Rating Finish Allowance Corrected Behind
		// Pen Pts
		// 1T 1000 Zippo EVEN, Jack 20 15:25:00 00:03:20 03:21:40 00:00:00
		// 1.5T
		// 1T USA 33 20Somthing YOUNG, Iam 0 15:21:40 00:00:00 03:21:40
		// 00:00:00 1.5T
		// 3 71 Donnybrook MULDOON, Jim -30 15:22:23 -00:05:00 03:27:23
		// 00:05:43 3.0

		// -- what we have in 4.2.1
		// Pos Sail Boat Skipper Rating Finish Allowance Corrected Behind
		// Pen Pts
		// 0T 1000 Zippo EVEN, Jack 0 15:25:00 00:00:00 03:25:00 00:00:00
		// 2.0T
		// 0T 71 Donnybrook MULDOON, Jim -30 15:22:23 -00:05:00 03:27:23
		// 00:02:23 2.0T
		// 0T USA 33 20Somthing YOUNG, Iam 20 15:36:22 00:03:20 03:33:02
		// 00:08:02 2.0T

		Race race = reg.getRace("1");

		Entry zippo = getEntry(reg, "1000");
		assertNotNull(zippo);
		RacePoints penZippo = racepts.find(race, zippo, p0);
		assertNotNull(penZippo);
		assertEquals("zippo race 1 pts not 1.5", 1.5, penZippo.getPoints(), ERR_MARGIN);

		Entry somthin = getEntry(reg, "USA 33");
		assertNotNull(somthin);
		RacePoints pensomthin = racepts.find(race, somthin, p0);
		assertNotNull(pensomthin);
		assertEquals("somthin race 1 pts not 1.5", 1.5, pensomthin.getPoints(), ERR_MARGIN);

		Entry dbrook = getEntry(reg, "71");
		assertNotNull(dbrook);
		RacePoints penDBrook = racepts.find(race, dbrook, p0);
		assertNotNull(penDBrook);
		assertEquals("dbrook race 1 pts not 3", 3.0, penDBrook.getPoints(), ERR_MARGIN);
	}

	public void testMasterRegatta() throws Exception {
		// assertTrue( "skip", false);

		Regatta reg = loadTestRegatta( "0000-Test-Master.regatta");

		// boat 600 has the following finishes, 7, avg, avg, [dsq/11], avg,
		// 2nd+20%)=4, 5
		// avg pts calc now includes the throwout... so
		// races: 7 + 11 + 2 + 5 = 27 divided by 4 is 6.75, round to tenth
		// shoudl be 6.8
		double AVG_PTS = 6.8;

		Entry avge = reg.getAllEntries().findId("600").get(0);
		Race r2 = reg.getRaceIndex(1);
		Race r5 = reg.getRaceIndex(4);

		assertNotNull("avge not null", avge);
		assertNotNull("r2 not null", r2);
		assertNotNull("r5 not null", r5);
		assertEquals("r2 name is 2", r2.getName(), "2");
		assertEquals("r5 name is 2", r5.getName(), "5");

		// RacePoints ptsPre =
		// reg.getScoringManager().getRacePointsList().find(
		// r2, avge, null);
		reg.scoreRegatta();

		RacePoints pts = reg.getScoringManager().getRacePointsList().find(r2, avge, null);
		assertNotNull("r2/avge should have pts", pts);
		assertEquals("r2/avge penalty wrong", AVG, pts.getFinish().getPenalty().getPenalty());
		assertEquals("r2/avge pts wrong", AVG_PTS, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(r5, avge, null);
		assertNotNull("r5/avge should have pts", pts);
		assertEquals("r5/avge penalty wrong", AVG, pts.getFinish().getPenalty().getPenalty());
		assertEquals("r5/avge pts wrong", AVG_PTS, pts.getPoints(), ERR_MARGIN);
	}

	public void doFullCheck(String rname, boolean compareScore) throws Exception {
		int i = 0;

		fRegatta = null;
		fRegatta = loadTestRegatta( rname, false);	
		assertNotNull( fRegatta);
		assertTrue( fRegatta == JavaScoreProperties.getRegatta());
		
		String n = fRegatta.getName();

		assertEquals(n + " num races", 7, fRegatta.getNumRaces());
		assertEquals(n + " num divs", 3, fRegatta.getNumDivisions());
		assertEquals(n + " num entries", 17, fRegatta.getNumEntries());

		scoringTiesDefaultRrs();

		scoringTiesA82Only();

		scoringTiesB8();

		scoringTiesNobreaker();
	}

	public void scoringTiesDefaultRrs() throws ScoringException {
		
		SingleStageScoring mgr = (SingleStageScoring) fRegatta.getScoringManager();
		mgr.setModel(ScoringLowPoint.NAME);
		ScoringLowPoint scorer = (ScoringLowPoint) mgr.getModel();

		assertEquals("scoring model wrong", ScoringLowPoint.NAME, scorer.getName());
		
		scorer.getOptions().setTiebreaker( ScoringLowPoint.TIE_RRS_DEFAULT);

		assertEquals("wrong tiebreaker handler", ScoringLowPoint.TIE_RRS_DEFAULT, scorer.getOptions().getTiebreaker());

		fRegatta.scoreRegatta();
		String n = fRegatta.getName();

		mgr = (SingleStageScoring) fRegatta.getScoringManager();
		scorer = (ScoringLowPoint) mgr.getModel();
		
		assertTrue( scorer == ((SingleStageScoring) fRegatta.getScoringManager()).getModel());

		assertEquals("wrong tiebreaker handler", ScoringLowPoint.TIE_RRS_DEFAULT, 
				scorer.getOptions().getTiebreaker());

		// E's total points should be 21
		EntryList el1 = fRegatta.getAllEntries().findId("E");
		Entry e1 = el1.get(0);
		SeriesPointsList seriesPoints = mgr.getAllSeriesPoints();
		assertNotNull( seriesPoints);
		SeriesPoints epoints = seriesPoints.find(e1, e1.getDivision());
		assertNotNull( epoints);

		// C's total pointts should be 21.0001
		el1 = fRegatta.getAllEntries().findId("C");
		e1 = el1.get(0);
		SeriesPoints cpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// D's total points should be 21.0002
		el1 = fRegatta.getAllEntries().findId("D");
		e1 = el1.get(0);
		SeriesPoints dpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// A's total points should be 21.5
		// may 14, 2001 MODIFIED to support mid-year deletion of A8.2
		el1 = fRegatta.getAllEntries().findId("A");
		e1 = el1.get(0);
		SeriesPoints apoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// B's total points should be 21.5001
		// may 14, 2001 MODIFIED to support mid-year deletion of A8.2
		el1 = fRegatta.getAllEntries().findId("B");
		e1 = el1.get(0);
		SeriesPoints bpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// 2201's total points should be 12
		el1 = fRegatta.getAllEntries().findId("2201");
		e1 = el1.get(0);
		SeriesPoints p2201 = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		el1 = fRegatta.getAllEntries().findId("2202");
		e1 = el1.get(0);
		SeriesPoints p2202 = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		el1 = fRegatta.getAllEntries().findId("2203");
		e1 = el1.get(0);
		SeriesPoints p2203 = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// Under default RRS we should get for E,C and D:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 E eee [7] 4 2 4 3 1 7 21.0000 1 [2 4ths]
		// 2 C ccc 2 1 6 3 [7] 5 4 21.0001 2 [ better last race]
		// 3 D ddd 3 5 1 2 [6] 4 6 21.0002 3

		assertEquals(n + ", boat E pts", 21.0, epoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat C pts", 21.0001, cpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat D pts", 21.0002, dpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat A pts", 21.5001, apoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat B pts", 21.5, bpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat 2201 pts", 12, p2201.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat 2202 pts", 12.0001, p2202.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat 2203 pts", 12.0002, p2203.getPoints(), 0.00001);

	}

	public void scoringTiesA82Only() throws ScoringException {
		fRegatta.scoreRegatta();
		// Under default RRS we get for E,C and D:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 E eee [7] 4 2 4 3 1 7 21.0000 1
		// 2 C ccc 2 1 6 3 [7] 5 4 21.0001 2
		// 3 D ddd 3 5 1 2 [6] 4 6 21.0002 3

		// BUT under 8.2 only we should have:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 C ccc 2 1 6 3 [7] 5 4 21.0000 1
		// 2 D ddd 3 5 1 2 [6] 4 6 21.0001 2
		// 3 E eee [7] 4 2 4 3 1 7 21.0002 3

		SingleStageScoring mgr = (SingleStageScoring)fRegatta.getScoringManager();
		mgr.setModel(ScoringLowPoint.NAME);

		ScoringLowPoint scorer = (ScoringLowPoint) mgr.getModel();
		assertEquals("scoring model wrong", ScoringLowPoint.NAME, scorer.getName());

		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_RRS_A82_ONLY);
		fRegatta.scoreRegatta();
		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_RRS_A82_ONLY);

		String n = fRegatta.getName() + "(2001)";

		// E's total points should be 21
		EntryList el1 = fRegatta.getAllEntries().findId("E");
		Entry e1 = el1.get(0);
		SeriesPoints epoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// C's total pointts should be 21.0001
		el1 = fRegatta.getAllEntries().findId("C");
		e1 = el1.get(0);
		SeriesPoints cpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// D's total points should be 21.0002
		el1 = fRegatta.getAllEntries().findId("D");
		e1 = el1.get(0);
		SeriesPoints dpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		assertEquals(n + ", boat C pts", 21.0000, cpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat D pts", 21.0001, dpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat E pts", 21.0002, epoints.getPoints(), ERR_MARGIN);

	}

	public void scoringTiesNobreaker() throws ScoringException {
		fRegatta.scoreRegatta();
		// Under default RRS we get for E,C and D:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 E eee [7] 4 2 4 3 1 7 21.0000 1
		// 2 C ccc 2 1 6 3 [7] 5 4 21.0001 2
		// 3 D ddd 3 5 1 2 [6] 4 6 21.0002 3

		// BUT under no breaker we should get 

		// Index Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 C ccc 2 1 6 3 [7] 5 4 21T 1T
		// 2 D ddd 3 5 1 2 [6] 4 6 21T 1T
		// 3 E eee [7] 4 2 4 3 1 7 21T 1T

		SingleStageScoring mgr = (SingleStageScoring)fRegatta.getScoringManager();
		mgr.setModel(ScoringLowPoint.NAME);

		ScoringLowPoint scorer = (ScoringLowPoint) mgr.getModel();
		assertEquals("scoring model wrong", ScoringLowPoint.NAME, scorer.getName());

		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_NOTIEBREAKER);
		fRegatta.scoreRegatta();
		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_NOTIEBREAKER);

		String n = fRegatta.getName() + "(2001)";

		// E's total points should be 21
		EntryList el1 = fRegatta.getAllEntries().findId("E");
		Entry e1 = el1.get(0);
		SeriesPoints epoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// C's total pointts should be 21.0001
		el1 = fRegatta.getAllEntries().findId("C");
		e1 = el1.get(0);
		SeriesPoints cpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// D's total points should be 21.0002
		el1 = fRegatta.getAllEntries().findId("D");
		e1 = el1.get(0);
		SeriesPoints dpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		assertEquals(n + ", boat C pts", 21.0000, cpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat D pts", 21.0000, dpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat E pts", 21.0000, epoints.getPoints(), ERR_MARGIN);

	}

	public void scoringTiesB8() throws ScoringException {
		fRegatta.scoreRegatta();
		// Under default RRS we get for E,C and D:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 E eee [7] 4 2 4 3 1 7 21.0000 1
		// 2 C ccc 2 1 6 3 [7] 5 4 21.0001 2
		// 3 D ddd 3 5 1 2 [6] 4 6 21.0002 3

		// BUT under 8.2 only we should have:

		// Pos Sail Boat 1 2 3 4 5 6 7 Total Pos
		// 1 D ddd 3 5 1 2 [6] 4 6 21.0000 1  - better discard
		// 2 E eee [7] 4 2 4 3 1 7 21.0001 2  - two 4ths vs c's 1 4th
		// 3 C ccc 2 1 6 3 [7] 5 4 21.0002 3

		SingleStageScoring mgr = (SingleStageScoring)fRegatta.getScoringManager();
		mgr.setModel(ScoringLowPoint.NAME);

		ScoringLowPoint scorer = (ScoringLowPoint) mgr.getModel();
		assertEquals("scoring model wrong", ScoringLowPoint.NAME, scorer.getName());

		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_RRS_B8);
		fRegatta.scoreRegatta();
		scorer.getOptions().setTiebreaker(ScoringLowPoint.TIE_RRS_B8);

		String n = fRegatta.getName() + "(2001)";

		// E's total points should be 21
		EntryList el1 = fRegatta.getAllEntries().findId("E");
		Entry e1 = el1.get(0);
		SeriesPoints epoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// C's total pointts should be 21.0001
		el1 = fRegatta.getAllEntries().findId("C");
		e1 = el1.get(0);
		SeriesPoints cpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		// D's total points should be 21.0002
		el1 = fRegatta.getAllEntries().findId("D");
		e1 = el1.get(0);
		SeriesPoints dpoints = mgr.getAllSeriesPoints().find(e1, e1.getDivision());

		assertEquals(n + ", boat D pts", 21.0000, dpoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat E pts", 21.0001, epoints.getPoints(), ERR_MARGIN);
		assertEquals(n + ", boat C pts", 21.0002, cpoints.getPoints(), ERR_MARGIN);

	}


	/**
	 * Test of non-discardable race and weighting of races. Implemented for 2006
	 * Miami OCR
	 */
	public void testWeightNonDiscard() throws Exception {
		// Race 4 is non-discardable and its weight is 2.00
		// Expecting the following:
		//
		// Pos Ent R1 R2 R3 R4 Tot
		// 1 B 2 3x 1 1 5.00
		// 2 C 3 2 4x 2 9.00
		// 3 A 1 1 2x 4 10.00
		// 4 E 5x 5 5 3 16.00
		// 5 D 4x 4 3 5 17.00

		Regatta reg = loadTestRegatta( "WeightsAndDiscards.regatta");
		reg.scoreRegatta();

		AbstractDivision div = reg.getDivision("Laser Radial");

		// test race info is as expected
		Race r = reg.getRace("1");
		assertTrue(!r.isNonDiscardable());
		assertEquals(1.00, r.getWeight(), 0.001);

		r = reg.getRace("2");
		assertTrue(!r.isNonDiscardable());
		assertEquals(1.00, r.getWeight(), 0.001);

		r = reg.getRace("3");
		assertTrue(!r.isNonDiscardable());
		assertEquals(1.00, r.getWeight(), 0.001);

		r = reg.getRace("4");
		assertTrue(r.isNonDiscardable());
		assertEquals(2.00, r.getWeight(), 0.001);

		// test point for each boat

		// 3 A 1 1 2x 4 10.00
		EntryList el = reg.getAllEntries().findId("A");
		assertEquals(1, el.size());
		Entry e = el.get(0);
		SeriesPoints tot = reg.getScoringManager().getRegattaRanking(e, div);

		assertEquals(10.00, tot.getPoints(), 0.001);
		assertEquals(3, tot.getPosition());

		// 1 B 2 3x 1 1 5.00
		el = reg.getAllEntries().findId("B");
		assertEquals(1, el.size());
		e = el.get(0);
		tot = reg.getScoringManager().getRegattaRanking(e, div);

		assertEquals(5.00, tot.getPoints(), 0.001);
		assertEquals(1, tot.getPosition());

		// 2 C 3 2 4x 2 9.00
		el = reg.getAllEntries().findId("C");
		assertEquals(1, el.size());
		e = el.get(0);
		tot = reg.getScoringManager().getRegattaRanking(e, div);

		assertEquals(9.00, tot.getPoints(), 0.001);
		assertEquals(2, tot.getPosition());

		// 5 D 4x 4 3 5 17.00
		el = reg.getAllEntries().findId("D");
		assertEquals(1, el.size());
		e = el.get(0);
		tot = reg.getScoringManager().getRegattaRanking(e, div);

		assertEquals(17.00, tot.getPoints(), 0.001);
		assertEquals(5, tot.getPosition());

		// 4 E 5x 5 5 3 16.00
		el = reg.getAllEntries().findId("E");
		assertEquals(1, el.size());
		e = el.get(0);
		tot = reg.getScoringManager().getRegattaRanking(e, div);

		assertEquals(16.00, tot.getPoints(), 0.001);
		assertEquals(4, tot.getPosition());

	}

	public ScoringTests(String name) {
		super(name);
	}

}

