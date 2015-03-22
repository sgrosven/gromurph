// === File Prolog==========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringQualifyingSeriesTests.java,v 1.8 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.util.List;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SubDivision;

/**
 * Unit test for scoring regattas that are made up of qualifying subdivisions
 */
public class ScoringQualifyingSeriesTests extends org.gromurph.javascore.JavascoreTestCase {

	private Entry getEntry(Regatta r, String sail) {
		return (Entry) r.getAllEntries().findId(sail).get(0);
	}

	public void testQualScoresNoThrowout() throws Exception {
		//		                     19th			20th
		//     						 1200 1300 1400	1300 1400
		//					         1210 1310 1410	1310 1410
		//						       1 	2	3	4	5
		//						       bg	pb	yb	g	g	nt
		//						       yp	yg	gp	s	s
		//1	4	Good Blue	Blue/Gold	1	2	2	3	1	9
		//2	1	Good Pink	Pink/Gold	1	1	2	2	5	11 (a8.1) 
		//3	7	Good Yllw	Yllw/Gold	2	1	1	4	3	11 
		//4	2	OK Pink		Pink/Gold	3	4	4	1	2	14
		//5	10	Good Grey	Grey/Gold	2	2	1	5	6	16
		//6	5	OK Blue		Blue/Gold	3	3	3	6	4	19
		//				
		//1	8	OK Yllw		Yllw/Silver	4	4	4	1	3	16
		//2	11	OK Grey		Grey/Silver	4	3	3	2	5	17
		//3	6	Bad Blue	Blue/Silver	5	5	6	3	1	20
		//4	9	Bad Yllw	Yllw/Silver	6	6	5	6	2	25
		//5	3	Bad Pink	Pink/Silver	5	6	6	5	4	26 (a8.2)
		//6	12	Bad Grey	Grey/Silver	6	5	5	4	6	26

		Regatta reg = loadTestRegatta( "QualifyFinalTests.regatta");
		assertNotNull(reg);

		Race race1 = reg.getRace("1");
		assertNotNull(race1);
		Race race2 = reg.getRace("2");
		assertNotNull(race2);
		Race race3 = reg.getRace("3");
		assertNotNull(race3);
		Race race4 = reg.getRace("4");
		assertNotNull(race4);
		Race race5 = reg.getRace("5");
		assertNotNull(race5);

		Division j22 = reg.getDivision("J22");

		SubDivision blue = reg.getSubDivision("Blue");
		assertNotNull(blue);
		SubDivision pink = reg.getSubDivision("Pink");
		assertNotNull(pink);
		SubDivision yellow = reg.getSubDivision("Yellow");
		assertNotNull(yellow);
		SubDivision grey = reg.getSubDivision("Grey");
		assertNotNull(grey);
		SubDivision gold = reg.getSubDivision("Gold");
		assertNotNull(gold);
		SubDivision silver = reg.getSubDivision("Silver");
		assertNotNull(silver);

		Entry e1 = getEntry(reg, "1");
		assertNotNull(e1);
		Entry e2 = getEntry(reg, "2");
		assertNotNull(e2);
		Entry e3 = getEntry(reg, "3");
		assertNotNull(e3);
		Entry e4 = getEntry(reg, "4");
		assertNotNull(e4);
		Entry e5 = getEntry(reg, "5");
		assertNotNull(e5);
		Entry e6 = getEntry(reg, "6");
		assertNotNull(e6);
		Entry e7 = getEntry(reg, "7");
		assertNotNull(e7);
		Entry e8 = getEntry(reg, "8");
		assertNotNull(e8);
		Entry e9 = getEntry(reg, "9");
		assertNotNull(e9);
		Entry e10 = getEntry(reg, "10");
		assertNotNull(e10);
		Entry e11 = getEntry(reg, "11");
		assertNotNull(e11);
		Entry e12 = getEntry(reg, "12");
		assertNotNull(e12);

		assertTrue( reg.getScoringManager() instanceof MultiStageScoring);
		MultiStageScoring scorer = (MultiStageScoring) reg.getScoringManager();
		
		assertTrue( reg.isMultistage());
		MultiStageScoring mgr = (MultiStageScoring) reg.getScoringManager();
		assertEquals( 3, mgr.getStages().size());

		//		19th			20th
		//		1200	1300	1400	1300	1400
		//		1210	1310	1410	1310	1410
		//		1 	2	3	4	5
		//		bg	pb	yb	g	g	nt
		//		yp	yg	gp	s	s

		SailTime.setLongDistance(false);

		long starttime = race1.getStartTimeAdjusted(blue);
		assertTrue(SailTime.NOTIME != starttime);
		assertEquals("12:00:00.0", SailTime.toString(starttime));

		starttime = race1.getStartTimeAdjusted(pink);
		assertTrue(SailTime.NOTIME != starttime);
		assertEquals("12:10:00.0", SailTime.toString(starttime));

		starttime = race4.getStartTimeAdjusted(gold);
		assertTrue(SailTime.NOTIME != starttime);
		assertEquals("13:00:00.0", SailTime.toString(starttime));

		// eliminate the throw out
		for (Stage s : mgr.getStages()) {
			s.getModel().getOptions().setThrowoutScheme(ScoringLowPoint.THROWOUT_NONE);
		}

		reg.scoreRegatta();

		//	1	4	Good Blue	Blue/Gold	1	2	2	3	1 = 9

		RacePoints pts = scorer.getRacePointsList().find(race1, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 1 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 3 should be 2nd", 2.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 4 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 5 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		SeriesPoints spts = scorer.getRegattaRanking( e4,  j22);
		assertNotNull(spts);
		assertEquals("GoodBlue should have 9 total points", 9.0, spts.getPoints(), ERR_MARGIN);

		assertEquals("GoodBlue should be gold winner", 1, spts.getPosition());

		//	3	7	Good Yllw	Yllw/Gold	2	1	1	4	3	11 

		pts = scorer.getRacePointsList().find(race1, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 1 should be 2nd", 2.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 3 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 4 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 5 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getRegattaRanking(e7, j22);
		assertNotNull(spts);
		assertEquals("GoodYellow should have 11.0001 total points", 11.0001, spts.getPoints(), ERR_MARGIN);

		assertEquals("GoodYellow should be gold 3rd overall", 3, spts.getPosition());

		//	1	8	OK Yllw		Yllw/Silver	4	4	4	1	3	16

		pts = scorer.getRacePointsList().find(race1, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 1 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 3 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 4 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 5 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getAllSeriesPoints().find(e8, silver);
		assertNotNull(spts);
		assertEquals("OKYellow should be silver 1st ", 1, spts.getPosition());
		assertEquals("OKYellow should have 16 total points", 16.0, spts.getPoints(), ERR_MARGIN);

		spts = scorer.getRegattaRanking(e8, j22);
		assertNotNull(spts);
		assertEquals("OKYellow should be 7th overall", 7, spts.getPosition());
		assertEquals("OKYellow should have 16 total points", 16.0, spts.getPoints(), ERR_MARGIN);

		//	5	3	Bad Pink	Pink/Silver	5	6	6	5	4	26 (a8.2)

		pts = scorer.getRacePointsList().find(race1, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 1 should be 5th", 5.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 3 should be 6th", 6.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 4 should be 5th", 5.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 5 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getAllSeriesPoints().find(e3, silver);
		assertNotNull(spts);
		assertEquals("BadPink should have 26 total points", 26.0, spts.getPoints(), ERR_MARGIN);
		assertEquals("BadPink should be silver 5th overall", 5, spts.getPosition());

		//	6	12	Bad Grey	Grey/Silver	6	5	5	4	6	26
		spts = scorer.getAllSeriesPoints().find(e12, silver);
		assertNotNull(spts);
		assertEquals("BadGrey should have 26.0001 total points", 26.0001, spts.getPoints(), ERR_MARGIN);
		assertEquals("BadGrey should be silver 6th overall", 6, spts.getPosition());

	}

	public void testQualScoresWithThrowout() throws Exception {
		Regatta reg = loadTestRegatta( "QualifyFinalTests.regatta");
			assertNotNull(reg);

		Race race1 = reg.getRace("1");
		assertNotNull(race1);
		Race race2 = reg.getRace("2");
		assertNotNull(race2);
		Race race3 = reg.getRace("3");
		assertNotNull(race3);
		Race race4 = reg.getRace("4");
		assertNotNull(race4);
		Race race5 = reg.getRace("5");
		assertNotNull(race5);

		Division j22 = reg.getDivision("J22");

		SubDivision blue = reg.getSubDivision("Blue");
		assertNotNull(blue);
		SubDivision pink = reg.getSubDivision("Pink");
		assertNotNull(pink);
		SubDivision yellow = reg.getSubDivision("Yellow");
		assertNotNull(yellow);
		SubDivision grey = reg.getSubDivision("Grey");
		assertNotNull(grey);
		SubDivision gold = reg.getSubDivision("Gold");
		assertNotNull(gold);
		SubDivision silver = reg.getSubDivision("Silver");
		assertNotNull(silver);

		Entry e1 = getEntry(reg, "1");
		assertNotNull(e1);
		Entry e2 = getEntry(reg, "2");
		assertNotNull(e2);
		Entry e3 = getEntry(reg, "3");
		assertNotNull(e3);
		Entry e4 = getEntry(reg, "4");
		assertNotNull(e4);
		Entry e5 = getEntry(reg, "5");
		assertNotNull(e5);
		Entry e6 = getEntry(reg, "6");
		assertNotNull(e6);
		Entry e7 = getEntry(reg, "7");
		assertNotNull(e7);
		Entry e8 = getEntry(reg, "8");
		assertNotNull(e8);
		Entry e9 = getEntry(reg, "9");
		assertNotNull(e9);
		Entry e10 = getEntry(reg, "10");
		assertNotNull(e10);
		Entry e11 = getEntry(reg, "11");
		assertNotNull(e11);
		Entry e12 = getEntry(reg, "12");
		assertNotNull(e12);

		assertTrue( reg.isMultistage());
		MultiStageScoring scorer = (MultiStageScoring) reg.getScoringManager();
		assertEquals( 3, scorer.getStages().size());

		// put back in 1 throw out
		for (Stage s : scorer.getStages()) {
			ScoringLowPoint scoringModel = (ScoringLowPoint) s.getModel();
			s.getModel().getOptions().setThrowoutScheme(ScoringLowPoint.THROWOUT_NONE);

    		scoringModel.getOptions().setThrowoutScheme(ScoringLowPoint.THROWOUT_BYNUMRACES);
    		scoringModel.getOptions().setThrowout(0, 2);
    		scoringModel.getOptions().setThrowout(1, 0);
    		scoringModel.getOptions().setThrowout(2, 0);
		}

		reg.scoreRegatta();

		//		                       1 	2	3	4	5
		//		                       bg	pb	yb	g	g	nt
		//		                       yp	yg	gp	s	s
		//1	4	Good Blue	Blue/Gold	1	2	2	3x	1	6  (A8.2)
		//2	1	Good Pink	Pink/Gold	1	1	2	2	5x	6 
		//3	7	Good Yllw	Yllw/Gold	2	1	1	4x	3	7 
		//4	10	Good Grey	Grey/Gold	2	2	1	5	6x	10 (A8.1)
		//5	2	OK Pink		Pink/Gold	3	4x	4	1	2	10 
		//6	5	OK Blue		Blue/Gold	3	3	3	6x	4	13
		//					
		//1	8	OK Yllw		Yllw/Silver	4x	4	4	1	3	12 (A8.1)
		//2	11	OK Grey		Grey/Silver	4	3	3	2	5x	12 
		//3	6	Bad Blue	Blue/Silver	5	5	6x	3	1	14
		//4	9	Bad Yllw	Yllw/Silver	6x	6	5	6	2	19
		//5	3	Bad Pink	Pink/Silver	5	6x	6	5	4	20 (A8.2)
		//6	12	Bad Grey	Grey/Silver	6x	5	5	4	6	20

		RacePoints pts = scorer.getRacePointsList().find(race1, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 1 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 3 should be 2nd", 2.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 4 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);
		assertTrue("good blue race 4 should be throwout", pts.isThrowout());

		pts = scorer.getRacePointsList().find(race5, e4, j22);
		assertNotNull(pts);
		assertEquals("good blue race 5 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		SeriesPoints spts = scorer.getRegattaRanking(e4, j22);
		assertNotNull(spts);
		assertEquals("GoodBlue should have 6 total points", 6.0, spts.getPoints(), ERR_MARGIN);

		assertEquals("GoodBlue should be gold winner", 1, spts.getPosition());

		//	3	7	Good Yllw	Yllw/Gold	2	1	1	4x	3	7 

		pts = scorer.getRacePointsList().find(race1, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 1 should be 2nd", 2.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 3 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 4 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);
		assertTrue("GoodYellow race 4 should be throwout", pts.isThrowout());

		pts = scorer.getRacePointsList().find(race5, e7, j22);
		assertNotNull(pts);
		assertEquals("GoodYellow race 5 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getRegattaRanking(e7, j22);
		assertNotNull(spts);
		assertEquals("GoodYellow should have 7.0 total points", 7.0, spts.getPoints(), ERR_MARGIN);

		assertEquals("GoodYellow should be gold 3rd overall", 3, spts.getPosition());

		//	2	8	OK Yllw		Yllw/Silver	4x	4	4	1	3	12

		pts = scorer.getRacePointsList().find(race1, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 1 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);
		assertTrue("GoodYellow race 1 should be throwout", pts.isThrowout());

		pts = scorer.getRacePointsList().find(race3, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 3 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 4 should be 1st", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e8, j22);
		assertNotNull(pts);
		assertEquals("OKYellow race 5 should be 3rd", 3.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getAllSeriesPoints().find(e8, silver);
		assertNotNull(spts);
		assertEquals("OKYellow should have 12.0 total points", 12.0, spts.getPoints(), ERR_MARGIN);
		assertEquals("OKYellow should be silver 1st overall", 1, spts.getPosition());

		//	5	3	Bad Pink	Pink/Silver	5	6x	6	5	4	20 (A8.2)

		pts = scorer.getRacePointsList().find(race1, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 1 should be 5th", 5.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race3, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 3 should be 6th", 6.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race4, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 4 should be 5th", 5.0, pts.getPoints(), ERR_MARGIN);

		pts = scorer.getRacePointsList().find(race5, e3, j22);
		assertNotNull(pts);
		assertEquals("BadPink race 5 should be 4th", 4.0, pts.getPoints(), ERR_MARGIN);

		spts = scorer.getAllSeriesPoints().find(e3, silver);
		assertNotNull(spts);
		assertEquals("BadPink should have 20 total points", 20.0, spts.getPoints(), ERR_MARGIN);
		assertEquals("BadPink should be silver 5th overall", 5, spts.getPosition());

		//	6	12	Bad Grey	Grey/Silver	6x	5	5	4	6	20

		spts = scorer.getAllSeriesPoints().find(e12, silver);
		assertNotNull(spts);
		assertEquals("BadGrey should have 20.0001 total points", 20.0001, spts.getPoints(), ERR_MARGIN);
		assertEquals("BadGrey should be silver 6th overall", 6, spts.getPosition());
	}

	// 21 March 2015 - these tests don't make sense with the MultiStage stuff
	// commenting out, I may not have good alternative test and may wish I had them back
	// just after git conversion, I am killing the respective test regattas
//	public void testQualScoresAfterSplit() throws Exception {
//
//		//	MedalTest_JustAfterSplit / Consolation Div.. before fix...
//		//	Pos Sail           1    2    3         4      Total   Pos 
//		//	 1   110           19   21   20   [31/NoFin]   64.00   1 
//		//	 2   111           20   22   19   [31/NoFin]   66.00   2 
//		//	 3   113           23   24   23   [31/NoFin]   80.00   3 
//
//		//	 SHOULD BE (20 boats in fleet)
//		//
//		//	Pos Sail           1    2    3         4      Total   Pos 
//		//	 1   110           19   21   20   [21/NoFin]   64.00   1 
//		//	 2   111           20   22   19   [21/NoFin]   66.00   2 
//		//	 3   113           23   24   23   [21/NoFin]   80.00   3 
//
//		Regatta reg = loadTestRegatta( "MedalTest_JustAfterSplit.regatta");
//		assertNotNull(reg);
//
//		Race race4 = reg.getRace("4");
//		assertNotNull(race4);
//
//		Race race1 = reg.getRace("1");
//		assertNotNull(race1);
//
//		Entry e110 = getEntry(reg, "110");
//		assertNotNull(e110);
//
//		SubDivision consol = reg.getSubDivision("Consolation");
//		assertNotNull(consol);
//
//		SubDivision medal = reg.getSubDivision("Medal");
//		assertNotNull(medal);
//
//		assertEquals("wrong num entries in Medal", 10, medal.getNumEntries());
//		assertEquals("wrong num entries in Medal", 10, medal.getNumEntries(race4));
//		assertEquals("wrong num entries in Medal", 0, medal.getNumEntries(race1));
//
//		assertEquals("wrong num entries in Consol", 20, consol.getNumEntries());
//		assertEquals("wrong num entries in Consol", 20, consol.getNumEntries(race4));
//		assertEquals("wrong num entries in Consol", 0, consol.getNumEntries(race1));
//
//		reg.scoreRegatta();
//		RegattaScoringModel scorer = reg.getScoringManager();
//
//		//	Pos Sail           1    2    3         4      Total   Pos 
//		//	 1   110           19   21   20   [21/NoFin]   64.00   1
//
//		RacePointsList thisRace = scorer.getRacePointsList().findAll(race4);
//		RacePoints pts = thisRace.find(race4, e110, consol);
//		if (pts != null) {
//			assertEquals("sail 110 in race 4 is DNC, should have 21 points", 21.0, pts.getPoints(), ERR_MARGIN);
//		}
//
//	}
//
//	public void testQualScoresMedalRaceNoConsolation() throws Exception {
//
//		// on Saturday, Dec 5th... 
//
//		// testing for new 3rd Medal race only 8.2 tiebreaker
//
//		// initially testing with tiebreaker - normal, race 4 without new medal race flag set...
//		// we get the following, which is good
//
//		//	Pos	Sail	Boat	Skipper	1	2	3	4m1,2	Total
//		//									Points	Pos
//		//	Medal
//		//	1	104	 	 	[4]	1	4	4	9.00	1
//		//	2	103	 	 	3	[4]	2	6	11.00	2
//		//	3	101	 	 	1	[2]	1	14	16.00	3
//		//	4	109	 	 	9	[10]	9	2	20.00	4
//		//	5	107	 	 	7	[8]	6	8	21.00	5
//		//	6	105	 	 	5	[6]	5	12	22.00	6
//		//	7	102	 	 	2	[3]	3	20	25.00	7
//		//	8	106	 	 	6	[7]	7	16	29.00	8
//		//	9	108	 	 	[8]	5	8	18	31.00	9   
//		//	10	112	 	 	[12]	9	12	10	31.00	10  
//		//	Consolation
//		//	11	111	 	 	11	[12]	10	 	21.00	11
//		//	12	110	 	 	10	[11]	11	 	21.00	12
//		//	13	113	 	 	13	[14]	13	 	26.00	13
//		//	14	116	 	 	[16]	13	16	 	29.00	14  
//		//	15	115	 	 	15	[16]	14	 	29.00	15 
//		//	16	114	 	 	14	[15]	15	 	29.00	16  
//
//		Regatta reg = loadTestRegatta( "MedalTest_Postsplit_MedalRaceOnly.regatta");
//		assertNotNull(reg);
//
//		Race race4 = reg.getRace("4m");
//		Race race1 = reg.getRace("1");
//
//		Entry e111 = getEntry(reg, "111");
//		Entry e110 = getEntry(reg, "110");
//		Entry e113 = getEntry(reg, "113");
//		Entry e104 = getEntry(reg, "104");
//		Entry e103 = getEntry(reg, "103");
//		Entry e101 = getEntry(reg, "101");
//		Entry e102 = getEntry(reg, "102");
//		Entry e108 = getEntry(reg, "108");
//		Entry e112 = getEntry(reg, "112");
//		Entry e116 = getEntry(reg, "116");
//		Entry e115 = getEntry(reg, "115");
//		Entry e114 = getEntry(reg, "114");
//
//
//		SubDivision consol = reg.getSubDivision("Consolation");
//		assertNotNull(consol);
//
//		SubDivision medal = reg.getSubDivision("Medal");
//		assertNotNull(medal);
//
//		AbstractDivision radial = medal.getParentDivision();
//		assertNotNull(radial);
//
//		assertEquals("wrong num entries in Medal", 10, medal.getNumEntries());
//		assertEquals("wrong num entries in Medal", 10, medal.getNumEntries(race4));
//		assertEquals("wrong num entries in Medal", 0, medal.getNumEntries(race1));
//
//		assertEquals("wrong num entries in Consol", 20, consol.getNumEntries());
//		assertEquals("wrong num entries in Consol", 0, consol.getNumEntries(race4));
//		assertEquals("wrong num entries in Consol", 0, consol.getNumEntries(race1));
//
//		// turn medal race scoring off... should be normal yacht race
//		// race 4 is weight 1 and discardable
//		// tiebreaks normal
//
//		race4.setMedalRace(false);  // should reset weigth to 1, discardable to true,
//		assertEquals( 1.0, race4.getWeight());
//		assertEquals( false, race4.isNonDiscardable());
//		
//		MultiStageScoring sm = (MultiStageScoring) reg.getScoringManager();
//		List<Stage> sl = sm.getStages();
//		assertNotNull(sl);
//		assertEquals( 2, sl.size());
//		assertTrue( sl.get(0).getModel() instanceof ScoringLowPoint);
//		
//		for (Stage s : sm.getStages()) {
//			ScoringLowPoint scoring = (ScoringLowPoint) s.getModel();
//			scoring.getOptions().setTiebreaker( ScoringLowPoint.TIE_RRS_DEFAULT);
//		}
//
//		reg.scoreRegatta();
//		RegattaScoringModel scorer = reg.getScoringManager();
//
//		//	Pos	Sail	Boat	Skipper	1	2	3	4m1,2	Total
//		//	Points	Pos    	
//		//Consolation
//		//11	111	 	 	11	[12]	10	 	21.00	11
//		//12	110	 	 	10	[11]	11	 	21.00	12
//		//13	113	 	 	13	[14]	13	 	26.00	13
//		//14	116	 	 	[16]	13	16	 	29.00	14  
//		//15	115	 	 	15	[16]	14	 	29.00	15 
//		//16	114	 	 	14	[15]	15	 	29.00	16  
//
//		RacePointsList race4pts = scorer.getRacePointsList().findAll(race4);
//		RacePointsList race1pts = scorer.getRacePointsList().findAll(race1);
//
//		checkPoints(race1pts, e111, race1, radial, 11.0);
//		checkPoints(race1pts, e110, race1, radial, 10.0);
//		checkPoints(race1pts, e113, race1, radial, 13.0);
//
//		checkPoints(race4pts, e111, race4, radial, Double.NaN);
//		checkPoints(race4pts, e110, race4, radial, Double.NaN);
//		checkPoints(race4pts, e113, race4, radial, Double.NaN);
//
//		// 110, 111 were tied with a boat that made gold, so there end of Fleet
//		// scores are 21.0001 and 0002
//		checkSeriesPoints(e111, radial, 21.0001, scorer);
//		checkSeriesPoints(e110, radial, 21.0002, scorer);
//		checkSeriesPoints(e113, radial, 26.0, scorer);
//		checkSeriesPoints(e116, radial, 29.0000, scorer);
//		checkSeriesPoints(e115, radial, 29.0001, scorer);
//		checkSeriesPoints(e114, radial, 29.0002, scorer);
//
//		//	Pos	Sail	Boat	Skipper	1	2	3	4m1,2	Total
//		//	Points	Pos
//		//Medal
//		//3	101	 	 	1	2	1	[7]		4.00	1
//		//1	104	 	 	[4]	1	4	2		7.00	2
//		//2	103	 	 	3	[4]	2	3		8.00	3
//		//7	102	 	 	2	3	3	[10]	8.00	4
//		//6	105	 	 	5	[6]	5	6		16.00	5
//		//5	107	 	 	7	[8]	6	4		17.00	6
//		//4	109	 	 	9  [10]	9	1		19.00	7
//		//9	108	 	 	8	5	8	[9]		21.00	8   
//		//8	106	 	 	6	7	7	[8]		22.00	9
//		//10 112	   [12]	9	12	5		26.00	10
//
//		checkPoints(race1pts, e101, race1, radial, 1.0);
//		checkPoints(race1pts, e104, race1, radial, 4.0);
//		checkPoints(race1pts, e103, race1, radial, 3.0);
//
//		checkPoints(race4pts, e101, race4, radial, 7.0);
//		checkPoints(race4pts, e104, race4, radial, 2.0);
//		checkPoints(race4pts, e103, race4, radial,3.0);
//
//		checkPoints(race4pts, e101, race4, medal, 7.0);
//		checkPoints(race4pts, e104, race4, medal, 2.0);  
//		checkPoints(race4pts, e103, race4, medal, 3.0);
//
//		// with medal race single weight
//		checkSeriesPoints(e104, radial, 7.0, scorer);
//		checkSeriesPoints(e103, radial, 8.0, scorer);
//		checkSeriesPoints(e101, radial, 9.0, scorer);
//		checkSeriesPoints(e102, radial, 15.0, scorer);
//
//		checkSeriesPoints(e112, radial, 26.0000, scorer);
//		checkSeriesPoints(e108, radial, 22.0000, scorer);
//
//		// now set tiebreaker to A8.2 only (all classes)
//		// and set medal race double weighting
//		race4.setMedalRace(true);
//		assertEquals( 2.0, race4.getWeight());
//		assertEquals( true, race4.isNonDiscardable());
//		
//		for (Stage s : sm.getStages()) {
//			ScoringLowPoint scoring = (ScoringLowPoint) s.getModel();
//			scoring.getOptions().setTiebreaker( ScoringLowPoint.TIE_RRS_A82_ONLY);
//		}
//
//		reg.scoreRegatta();
//
//		//	Medal
//		//	1	104	 	 	[4]	1	4	4	9.00	1
//		//	2	103	 	 	3	[4]	2	6	11.00	2
//		//	3	101	 	 	1	[2]	1	14	16.00	3
//		//	4	109	 	 	9	[10]	9	2	20.00	4
//		//	5	107	 	 	7	[8]	6	8	21.00	5
//		//	6	105	 	 	5	[6]	5	12	22.00	6
//		//	7	102	 	 	2	[3]	3	20	25.00	7
//		//	8	106	 	 	6	[7]	7	16	29.00	8
//		//	9	112	 	  [12]	9	12	10	31.0000	9  
//		//	10	108	 	 	[8]	5	8	18	31.0001	10   
//		//	Consolation
//		//	11	111	 	 	11	[12]  10	 	21.0001	11 // .0001 cuz were tied with 112 at end
//		//	12	110	 	 	10	[11]  11	 	21.0002	12 // .0002  .. of fleet stage
//		//	13	113	 	 	13	[14]  13	 	26.00	13
//		//	15	115	 	 	15	[16]  14	 	29.0000	14 
//		//	16	114	 	 	14	[15]  15	 	29.0001	15  
//		//	14	116	 	 	[16] 13	  16	 	29.0002	16  
//
//
//		// 112 should bean 108
//		checkSeriesPoints(e108, radial, 31.0001, scorer);
//		checkSeriesPoints(e112, radial, 31.0000, scorer);
//		// 115 beats 114 who beats 116
//		checkSeriesPoints(e116, radial, 29.0002, scorer);
//		checkSeriesPoints(e115, radial, 29.0000, scorer);
//		checkSeriesPoints(e114, radial, 29.0001, scorer);
//
//
//
//		// now set tiebreaker to A8.2 medal race only.. 
//		// if race 4 is NOT medal - but explicity weigth 2 and nondisc, 
//				// results should be default RRS
//		race4.setMedalRace(false);
//		race4.setWeight(2);
//		race4.setNonDiscardable(true);
//
//		assertEquals( 2.0, race4.getWeight());
//		assertEquals( true, race4.isNonDiscardable());
//		
//		for (Stage s : sm.getStages()) {
//			ScoringOptions options = ((ScoringLowPoint) s.getModel()).getOptions();
//			options.setTiebreaker( Constants.TIE_RRS_DEFAULT);
//		}
//		
//		ScoringOptions medalOptions = ((ScoringLowPoint)sm.getStage( Stage.MEDAL).getModel()).getOptions();
//		medalOptions.setTiebreaker( Constants.TIE_RRS_A82_ONLY);
//		reg.scoreRegatta();
//
//		checkSeriesPoints(e112, radial, 31.0000, scorer);
//		checkSeriesPoints(e108, radial, 31.0001, scorer);
//		checkSeriesPoints(e116, radial, 29.0000, scorer);
//		checkSeriesPoints(e115, radial, 29.0001, scorer);
//		checkSeriesPoints(e114, radial, 29.0002, scorer);
//
//		// now flag race 4 as medal
//
//		race4.setMedalRace(true);
//		reg.scoreRegatta();
//
//		// 112 should bean 108
//		checkSeriesPoints(e108, radial, 31.0001, scorer);
//		checkSeriesPoints(e112, radial, 31.0000, scorer);
//		// but radial is same as default RRS ties
//		checkSeriesPoints(e116, radial, 29.0000, scorer);
//		checkSeriesPoints(e115, radial, 29.0001, scorer);
//		checkSeriesPoints(e114, radial, 29.0002, scorer);
//	}

	private void checkPoints(RacePointsList rpl, Entry e, Race r, AbstractDivision div, double expectedPoints) {
		RacePoints pts = rpl.find(r, e, div);
		if (Double.isNaN(expectedPoints)) {
			assertNull("Points found but no expected " + e.toString() + "/" + r.toString(), pts);
		} else {
			assertNotNull("Points expected but not found " + e.toString() + "/" + r.toString(), pts);
			assertEquals("Wrong pts for " + e.toString() + "/" + r.toString(), expectedPoints, pts.getPoints(),
					0.000001);
		}
	}

	private void checkSeriesPoints(Entry e, AbstractDivision div, double expectedPoints, RegattaScoringModel scorer) {
		SeriesPoints pts = scorer.getRegattaRanking(e, div);
		assertNotNull("Series Points not found " + e.toString() + "/" + div.toString(), pts);
		assertEquals("Wrong series pts for " + e.toString() + "/" + div.toString(), expectedPoints, pts.getPoints(),
				0.0000001);
	}

	public void testCalculateEntriesInLargestDivision() throws Exception {

		Regatta reg = loadTestRegatta( "TestPenaltiesByLargestDivision.regatta");
			assertNotNull(reg);

		Race race1 = reg.getRace("1");

		Entry g1 = getEntry(reg, "G 1");
		Entry y1 = getEntry(reg, "Y 1");
		Entry p1 = getEntry(reg, "P 1");

		SubDivision pink = reg.getSubDivision("Pink");
		SubDivision green = reg.getSubDivision("Green");
		SubDivision yellow = reg.getSubDivision("Yellow");

		assertEquals("wrong num entries in Pink", 3, pink.getNumEntries());
		assertEquals("wrong num entries in Green", 2, green.getNumEntries());
		assertEquals("wrong num entries in Yellow", 4, yellow.getNumEntries());
		
		// TODO this should be a converted from pre-multistage qualifying event into a multistage regatta
		// converted by loading in to JS 7.1.2, then saving
		// it should have adapted to become a multistage event
		assertTrue( reg.isMultistage());
		MultiStageScoring mgr = (MultiStageScoring) reg.getScoringManager();
		assertEquals( 1, mgr.getStages().size());
		ScoringLowPoint scoring = (ScoringLowPoint) mgr.getStages().get(0).getModel();

		// these should be points under normal scoring

		//    Pos	Sail	Order	Adj	Pts
		//    Green (2 boats) 
		//    1  	G 2	2		1.0
		//    2  	G 1	1	DSQ	3.0
		//    Pink (3 boats) 
		//    1  	P 3	1		1.0
		//    2  	P 2	2		2.0
		//    DNF  	P 1	DNF	DNF	4.0
		//    Yellow (4 boats) 
		//    1  	Y 3	1		1.0
		//    2  	Y 2	2		2.0
		//    3  	Y 4	3		3.0
		//    DNC  	Y 1	DNC	DNC	5.0

		scoring.getOptions().setEntriesLargestDivision(false);
		reg.scoreRegatta();

		RacePointsList race1pts = reg.getScoringManager().getRacePointsList().findAll(race1);

		checkPoints(race1pts, g1, race1, green, 3.0);
		checkPoints(race1pts, p1, race1, pink, 4.0);
		checkPoints(race1pts, y1, race1, yellow, 5.0);

		// turning entries on largest division on... all three should be 5 points
		scoring.getOptions().setEntriesLargestDivision(true);
		reg.scoreRegatta();

		race1pts = reg.getScoringManager().getRacePointsList().findAll(race1);

		checkPoints(race1pts, g1, race1, green, 5.0);
		checkPoints(race1pts, p1, race1, pink, 5.0);
		checkPoints(race1pts, y1, race1, yellow, 5.0);


	}

	public void testCalculateEntriesInLargestDivisionWithFinalsRaces() throws Exception {

		Regatta reg = loadTestRegatta( "TestPenaltiesByLargestDivisionWithFinalsRaces.regatta");
		assertNotNull(reg);

		Race race1 = reg.getRace("1");
		Race race6 = reg.getRace("6");

		Entry g1 = getEntry(reg, "G 1");
		Entry y1 = getEntry(reg, "Y 1");
		Entry p1 = getEntry(reg, "P 1");
		Entry p2 = getEntry(reg, "P 2");

		SubDivision pink = reg.getSubDivision("Pink");
		SubDivision green = reg.getSubDivision("Green");
		SubDivision yellow = reg.getSubDivision("Yellow");
		SubDivision gold = reg.getSubDivision("Gold");
		SubDivision silver = reg.getSubDivision("Silver");
		Division j22 = reg.getDivision("J22");

		assertEquals("wrong num entries in Pink", 3, pink.getNumEntries());
		assertEquals("wrong num entries in Green", 2, green.getNumEntries());
		assertEquals("wrong num entries in Yellow", 4, yellow.getNumEntries());
		
		List<Stage> stages = ((MultiStageScoring) reg.getScoringManager()).getStages();
		assertEquals( 3, stages.size());

		// these should be points under normal scoring

		// turning entries on largest division on... all three should be 5 points
		//	Pos	Sail	1	2	3	4	5	6	Total
		//	Points	Pos
		//	Gold
		//	1	G 2	1		1	1	1	   [3]	1		5.00	1
		//	2	P 3	1		1	2 [4/DSQ]	2	2		8.00	2
		//	3	P 2	2		2	1	2		1	[5/RAF]	8.00	3
		//	4	Y 3	1		1	2	3		[4]	3		10.00	4
		//	Silver
		//	5	G 1	[3/DSQ]	2	2	2		1	2		9.00	5
		//	6	Y 1	[5/DNC]	2	1	4		2	1		10.00	6
		//	7	Y 2	2		3	3	1		3	[4]		12.00	7
		//	8	P 1	4/DNF	3	3	1		4	[6/DNS]	15.00	8
		//	9	Y 4	3		4	4	2		[5]	3		16.00	9

		ScoringLowPoint qualifyingScorer = null;
		for (Stage s : stages) {
			if (s.getName().equals("Qualifying")) qualifyingScorer = (ScoringLowPoint) s.getModel();
		}
		assertNotNull(qualifyingScorer);
		qualifyingScorer.getOptions().setEntriesLargestDivision(false);
		
		reg.scoreRegatta();

		RacePointsList race1pts = reg.getScoringManager().getRacePointsList().findAll(race1);

		checkPoints(race1pts, g1, race1, green, 3.0);
		checkPoints(race1pts, p1, race1, pink, 4.0);
		checkPoints(race1pts, y1, race1, yellow, 5.0);

		RacePointsList race6pts = reg.getScoringManager().getRacePointsList().findAll(race6);
		checkPoints(race6pts, p2, race6, j22, 5.0);
		checkPoints(race6pts, p1, race6, j22, 6.0);

		//	Pos	Sail	1	2	3	4	5	6	Total
		//	Gold
		//	1	G 2	1	1	1	1	[3]	1	5.00	1
		//	2	P 3	1	1	2	[5/DSQ]	2	2	8.00	2
		//	3	P 2	2	2	1	2	1	[5/RAF]	8.00	3
		//	4	Y 3	1	1	2	3	[4]	3	10.00	4
		//	Silver
		//	5	G 1	[5/DSQ]	2	2	2	1	2	9.00	5
		//	6	Y 1	[5/DNC]	2	1	4	2	1	10.00	6
		//	7	Y 2	2	3	3	1	3	[4]	12.00	7
		//	8	P 1	5/DNF	3	3	1	4	[6/DNS]	16.00	8
		//	9	Y 4	3	4	4	2	[5]	3	16.00	9

		qualifyingScorer.getOptions().setEntriesLargestDivision(true);
		reg.scoreRegatta();

		race1pts = reg.getScoringManager().getRacePointsList().findAll(race1);

		checkPoints(race1pts, g1, race1, green, 5.0);
		checkPoints(race1pts, p1, race1, pink, 5.0);
		checkPoints(race1pts, y1, race1, yellow, 5.0);

		race6pts = reg.getScoringManager().getRacePointsList().findAll(race6);
		checkPoints(race6pts, p2, race6, gold, 5.0);
		checkPoints(race6pts, p1, race6, silver, 6.0);


	}

	public ScoringQualifyingSeriesTests(String name) {
		super(name);
	}

}
/**
 * $Log: ScoringQualifyingSeriesTests.java,v $ 
 * 
 * Revision 1.8 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.7 2006/01/19 02:27:41 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.6 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.4 2006/01/14 21:06:56 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.3 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/11 02:17:16 sandyg Bug fixes relative to qualify/final race scoring
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.4 2005/05/26 01:46:51 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.3 2005/04/23 21:55:31 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.2 2005/02/27 23:24:37 sandyg added IRC, changed corrected time calcs to no longer round to a second
 * 
 * Revision 1.1 2004/05/06 02:12:24 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.4 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.3 2003/04/27 21:00:55 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.2 2003/01/04 17:09:28 sandyg Prefix/suffix overhaul
 * 
 */
