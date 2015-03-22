//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PenaltyTests.java,v 1.5 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Date;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.scoring.RegattaScoringModel;
import org.gromurph.javascore.model.scoring.ScoringLowPoint;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.util.TestUtils;

/**
 * Unit tests on Penalty class
 */
public class PenaltyTests extends JavascoreTestCase implements Constants {

	public PenaltyTests(String name) {
		super(name);
	}

	Regatta reg;

	@Override public void setUp() throws Exception {
		super.setUp();
		SailTime.setLongDistance(false);
		
		reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
	}

	public void testToString() {

		Penalty p = new Penalty();
		assertEquals("", p.toString(false));

		p.setDsqPenalty(DSQ);
		assertEquals("DSQ", p.toString(false));

		p.setDsqPenalty(DGM);
		assertEquals("DGM", p.toString(false));

		p.setDsqPenalty(TLE); // not a valid dsq penalty
		assertEquals("", p.toString(false));
		p = new Penalty(TLE);
		assertEquals("TLE", p.toString(false));

		p = new Penalty(DNF);
		assertEquals("DNF", p.toString(false));

		p.setDsqPenalty(DSQ);
		assertEquals("DNF,DSQ", p.toString(false));

		p = new Penalty(RDG);
		p.setPoints(5.1);
		assertEquals("RDG", p.toString(false));
		assertEquals("RDG/5.1", p.toString(true));

		p = new Penalty(TME);
		p.setTimePenaltyElapsed(SailTime.forceToLong("0:05:22"));
		assertEquals("TME", p.toString(false));
		assertEquals("TME/00:05:22.0", p.toString(true));

		p = new Penalty(TMC);
		p.setTimePenaltyCorrected(SailTime.forceToLong("0:06:22"));
		assertEquals("TMC", p.toString(false));
		assertEquals("TMC/00:06:22.0", p.toString(true));

		p = new Penalty(TMP);
		p.setPercent(10);
		assertEquals("TMP", p.toString(false));
		assertEquals("TMP/10%", p.toString(true));

		p = new Penalty(ZFP);
		assertEquals("ZFP", p.toString(false));
		p = new Penalty(ZFP2);
		assertEquals("ZFP2", p.toString(false));

		p = new Penalty(ZFP3);
		assertEquals("ZFP3", p.toString(false));
		p.addOtherPenalty(ZFP);
		assertEquals("ZFP,ZFP3", p.toString(false));
	}

	public void testPenalty() {
		Penalty p = new Penalty();
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p.setDsqPenalty(DSQ);
		assertTrue(p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p.setDsqPenalty(TLE); // not a valid dsq penalty
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p = new Penalty(TLE);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(p.hasPenalty(TLE));
		assertTrue(!p.isDsqPenalty());
		assertTrue(p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p = new Penalty(DNF);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(p.hasPenalty(DNF));
		assertTrue(!p.isDsqPenalty());
		assertTrue(p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p = new Penalty(DGM);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(p.hasPenalty(DGM));
		assertTrue(p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(!p.isOtherPenalty());

		p = new Penalty(RDG);
		p.setPoints(5.1);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNC));
		assertTrue(!p.hasPenalty(DGM));
		assertTrue(p.hasPenalty(RDG));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());

		p = new Penalty(DNF);
		p.addOtherPenalty(RDG);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(p.hasPenalty(DNF));
		assertTrue(p.hasPenalty(RDG));
		assertTrue(!p.isDsqPenalty());
		assertTrue(p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());

		p = new Penalty(OCS);
		p.addOtherPenalty(AVG);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNF));
		assertTrue(!p.hasPenalty(RDG));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(p.hasPenalty(OCS));
		assertTrue(p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());

		p = new Penalty(TME);
		p.setTimePenaltyElapsed(SailTime.forceToLong("0:05:22"));
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNF));
		assertTrue(p.hasPenalty(TME));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());

		p = new Penalty(TMC);
		p.setTimePenaltyElapsed(SailTime.forceToLong("0:05:22"));
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNF));
		assertTrue(p.hasPenalty(TMC));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());

		p = new Penalty(TMP);
		p.setPercent(10);
		assertTrue(!p.hasPenalty(DSQ));
		assertTrue(!p.hasPenalty(RET));
		assertTrue(!p.hasPenalty(AVG));
		assertTrue(!p.hasPenalty(DNF));
		assertTrue(p.hasPenalty(TMP));
		assertTrue(!p.isDsqPenalty());
		assertTrue(!p.isFinishPenalty());
		assertTrue(p.isOtherPenalty());
	}

	public void testParse() {
		try {
			Penalty p = new Penalty();

			Penalty.parsePenalty(p, "DSQ");
			assertTrue(p.hasPenalty(DSQ));

			Penalty.parsePenalty(p, "DGM");
			assertTrue(p.hasPenalty(DGM));

			Penalty.parsePenalty(p, "dgm");
			assertTrue(p.hasPenalty(DGM));

			Penalty.parsePenalty(p, "DNC");
			assertTrue(p.hasPenalty(DNC));

			Penalty.parsePenalty(p, "DNF");
			assertTrue(p.hasPenalty(DNF));

			Penalty.parsePenalty(p, "dsq");
			assertTrue(p.hasPenalty(DSQ));

			Penalty.parsePenalty(p, "dnc");
			assertTrue(p.hasPenalty(DNC));

			Penalty.parsePenalty(p, "dnf");
			assertTrue(p.hasPenalty(DNF));

			Penalty.parsePenalty(p, "RAF/hhh");
			assertTrue(p.hasPenalty(RET));

			Penalty.parsePenalty(p, "RET/hhh");
			assertTrue(p.hasPenalty(RET));

			Penalty.parsePenalty(p, "AVG");
			assertTrue(p.hasPenalty(AVG));

			Penalty.parsePenalty(p, "RDG/10");
			assertTrue(p.hasPenalty(RDG));
			assertEquals(10.0, p.getPoints(), ERR_MARGIN);

			Penalty.parsePenalty(p, "rdg/5.5");
			assertTrue(p.hasPenalty(RDG));
			assertEquals(5.5, p.getPoints(), ERR_MARGIN);

			Penalty.parsePenalty(p, "dpi/5.5");
			assertTrue(p.hasPenalty(RDG));
			assertEquals("DPI", p.getRedressLabel());
			assertEquals(5.5, p.getPoints(), ERR_MARGIN);

			Penalty.parsePenalty(p, "foo");
			assertTrue(p.hasPenalty(RDG));
			assertEquals("FOO", p.getRedressLabel());
			assertEquals(0, p.getPoints(), ERR_MARGIN);

			Penalty.parsePenalty(p, "P10");
			assertTrue(p.hasPenalty(SCP));
			assertEquals(10, p.getPercent());

			Penalty.parsePenalty(p, "p10");
			assertTrue(p.hasPenalty(SCP));
			assertEquals(10, p.getPercent());

			Penalty.parsePenalty(p, "10%");
			assertTrue(p.hasPenalty(SCP));
			assertEquals(10, p.getPercent());

			Penalty.parsePenalty(p, "TIM/00:05:00.0");
			assertTrue(p.hasPenalty(TME));
			assertEquals("00:05:00.0", SailTime.toString(p.getTimePenaltyElapsed()));

			Penalty.parsePenalty(p, "TME/00:06:00.0");
			assertTrue(p.hasPenalty(TME));
			assertEquals("00:06:00.0", SailTime.toString(p.getTimePenaltyElapsed()));

			Penalty.parsePenalty(p, "TME/-00:06:00.0");
			assertTrue(p.hasPenalty(TME));
			assertEquals("-00:06:00.0", SailTime.toString(p.getTimePenaltyElapsed()));

			Penalty.parsePenalty(p, "TMC/00:06:10.0");
			assertTrue(p.hasPenalty(TMC));
			assertEquals("00:06:10.0", SailTime.toString(p.getTimePenaltyCorrected()));

			Penalty.parsePenalty(p, "TMP/15");
			assertTrue(p.hasPenalty(TMP));
			assertEquals(15, p.getPercent());

			Penalty.parsePenalty(p, "tme/00:05:00");
			assertTrue(p.hasPenalty(TME));
			assertEquals("00:05:00.0", SailTime.toString(p.getTimePenaltyElapsed()));

			Penalty.parsePenalty(p, "tmp/15");
			assertTrue(p.hasPenalty(TMP));
			assertEquals(15, p.getPercent());

			Penalty.parsePenalty(p, "P10,CNF");
			assertTrue(p.hasPenalty(CNF));
			assertTrue(p.hasPenalty(SCP));
			assertEquals(10, p.getPercent());

			Penalty.parsePenalty(p, "DNF,AVG");
			assertTrue(p.hasPenalty(DNF));
			assertTrue(p.hasPenalty(AVG));

			Penalty.parsePenalty(p, "ZFP");
			assertTrue(p.hasPenalty(ZFP));
			assertTrue(!p.hasPenalty(ZFP2));
			assertTrue(!p.hasPenalty(ZFP3));

			Penalty.parsePenalty(p, "ZFP2,ZFP3");
			assertTrue(!p.hasPenalty(ZFP));
			assertTrue(p.hasPenalty(ZFP2));
			assertTrue(p.hasPenalty(ZFP3));
			
			Penalty.parsePenalty( p, "DNC,DPI/5.3");
			assertEquals( "DNC,DPI/5.3", p.toString());
			assertTrue( p.hasPenalty(RDG));
			assertEquals( "DPI", p.getRedressLabel());
			assertEquals( 5.3, p.getPoints(), 0.0001);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testXml() {
		Penalty p1 = new Penalty();
		assertTrue("p1 default", xmlEquals(p1));

		p1.setDsqPenalty(DSQ);
		assertTrue("p1 dsq doesnt go tofrom xml equal", xmlEquals(p1));

		p1.setDsqPenalty(DGM);
		assertTrue("p1 dgm doesnt go tofrom xml equal", xmlEquals(p1));

		p1.setPenalty(TMP);
		p1.setPercent(10);
		assertEquals("TMP/10 doesnt go tofrom xml equal", toXml(p1), toFromXml(p1));

		p1.setPenalty(TME);
		p1.setTimePenaltyElapsed(SailTime.forceToLong("00:20:30"));
		assertEquals("TME/00:20:30 doesnt go tofrom xml equal", toXml(p1), toFromXml(p1));

		p1 = new Penalty();
		p1.setPenalty(TMC);
		p1.setTimePenaltyCorrected(SailTime.forceToLong("01:20:30"));
		String toX = toXml(p1);
		String fromX = toFromXml(p1);
		assertEquals("TMC/01:20:30 doesnt go tofrom xml equal", toX, fromX);

		p1.setPenalty(RDG);
		p1.setPoints(6.5);
		assertEquals("RDG/6.5 doesnt go tofrom xml equal", toXml(p1), toFromXml(p1));

	}

	public void testTIMandTMPPenalties() throws Exception {
			// create two phrf boats, with ratings of 0 (to make things easy)
			Division phrf = new Division("phrf", new RatingPhrf(-20), new RatingPhrf(20));
			reg.addDivision(phrf);

			Boat boat1 = new Boat("one", "1", "1 1");
			boat1.putRating(new RatingPhrf(0));
			Entry e1 = new Entry();
			e1.setDivision(phrf);
			e1.setBoat(boat1);
			reg.addEntry(e1);

			Boat boat2 = new Boat("two", "2", "2 2");
			boat2.putRating(new RatingPhrf(0));
			Entry e2 = new Entry();
			e2.setDivision(phrf);
			e2.setBoat(boat2);
			reg.addEntry(e2);

			Race r = new Race(reg, "r1");
			r.setStartDate(new Date(System.currentTimeMillis()));
			r.setLength(phrf, 10.0);
			r.setStartTime(phrf, SailTime.forceToLong("12:00:00"));

			Finish f1 = new Finish(r, e1, SailTime.forceToLong("14:00:00"), new FinishPosition(1), new Penalty());
			r.setFinish(f1);

			Finish f2 = new Finish(r, e2, SailTime.forceToLong("14:00:00"), new FinishPosition(2), new Penalty());
			r.setFinish(f2);

			reg.addRace(r);
			reg.scoreRegatta();

			assertEquals("e1 ctime wrong", "02:00:00.0", SailTime.toString(f1.getCorrectedTime()));
			assertEquals("e2 prepenalty ctime wrong", "02:00:00.0", SailTime.toString(f2.getCorrectedTime()));

			Penalty tPen = new Penalty();
			tPen.addOtherPenalty(Constants.TME);
			tPen.setTimePenaltyElapsed(SailTime.forceToLong("00:05:00"));
			f2.setPenalty(tPen);

			reg.scoreRegatta();
			assertEquals("e2 postpenalty ctime wrong", "02:05:00.0", SailTime.toString(f2.getCorrectedTime()));

			tPen.setTimePenaltyElapsed(SailTime.forceToLong("-00:05:00"));
			f2.setPenalty(tPen);

			reg.scoreRegatta();
			assertEquals("e2 postpenalty ctime wrong", "01:55:00.0", SailTime.toString(f2.getCorrectedTime()));

			tPen.setPenalty(Constants.TMP);
			tPen.setPercent(10);
			reg.scoreRegatta();
			assertEquals("e2 %time penalty ctime wrong", "02:12:00.0", SailTime.toString(f2.getCorrectedTime()));
	}

	public void testDncAndAveragePoints() {
		
		ScoringLowPoint sd1 = (ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel();
		sd1.getOptions().setThrowoutScheme(ScoringLowPoint.THROWOUT_BYNUMRACES);
		sd1.getOptions().getThrowouts().set(0, new Integer(2));
		sd1.getOptions().getThrowouts().set(1, new Integer(0));

		assertEquals("Scoring sys wrong", ScoringLowPoint.NAME, sd1.getName());

		Division div = new Division("DN");
		reg.removeAllDivisions();
		reg.addDivision(div);

		Entry e1 = null;
		Entry e2 = null;
		try {
			e1 = new Entry();
			e1.setSailId(new SailId("e1"));
			e1.setDivision(div);
			e2 = new Entry();
			e2.setSailId(new SailId("e2"));
			e2.setDivision(div);
		} catch (RatingOutOfBoundsException e) {
		}

		reg.addEntry(e1);
		reg.addEntry(e2);

		Race r1 = new Race(reg, "1");
		Race r2 = new Race(reg, "2");
		Race r3 = new Race(reg, "3");
		Race r4 = new Race(reg, "4");
		Race r5 = new Race(reg, "5");

		reg.addRace(r1);
		reg.addRace(r2);
		reg.addRace(r3);
		reg.addRace(r4);
		reg.addRace(r5);

		r1.setFinish(new Finish(r1, e1, SailTime.NOTIME, new FinishPosition(DNC), new Penalty(DNC)));
		r2.setFinish(new Finish(r2, e1, SailTime.NOTIME, new FinishPosition(1), ManPenalty(11)));
		r3.setFinish(new Finish(r3, e1, SailTime.NOTIME, new FinishPosition(1), ManPenalty(12)));
		r4.setFinish(new Finish(r4, e1, SailTime.NOTIME, new FinishPosition(1), ManPenalty(13)));
		r5.setFinish(new Finish(r5, e1, SailTime.NOTIME, new FinishPosition(1), ManPenalty(14)));

		reg.scoreRegatta();
		RegattaScoringModel scorer = reg.getScoringManager();

		// should have 3 points (2 entrants) for race 1 with a DNC only
		RacePoints points = scorer.getRacePointsList().find(r1, e1, div);
		assertNotNull("Finish points missing", points);
		assertEquals("DNC points should be 3", 3, points.getPoints(), ERR_MARGIN);

		r1.setFinish(new Finish(r1, e1, SailTime.NOTIME, new FinishPosition(DNC), new Penalty(AVG)));
		reg.scoreRegatta();

		// race 1 points should now be AVG points should be (11+12+13+14)/4
		points = scorer.getRacePointsList().find(r1, e1, div);
		assertNotNull(points);
		assertEquals("DNC/AVG points should be 12.5", 12.5, points.getPoints(), ERR_MARGIN);

		r1.setFinish(new Finish(r1, e1, SailTime.NOTIME, new FinishPosition(DNF), new Penalty(AVG)));
		reg.scoreRegatta();

		// race 1 points should now be AVG points should be (11+12+13+14)/4
		points = scorer.getRacePointsList().find(r1, e1, div);
		assertNotNull(points);
		assertEquals("DNF/AVG points should be 12.5", 12.5, points.getPoints(), ERR_MARGIN);
	}

	public void testManPenalty() {
		ScoringLowPoint sd1 = (ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel();
		sd1.getOptions().setThrowoutScheme(ScoringLowPoint.THROWOUT_BYNUMRACES);
		sd1.getOptions().getThrowouts().set(0, new Integer(2));
		sd1.getOptions().getThrowouts().set(1, new Integer(0));

		assertEquals("Scoring sys wrong", ScoringLowPoint.NAME, sd1.getName());

		Division div = new Division("DN");
		reg.removeAllDivisions();
		reg.addDivision(div);

		Entry e1 = null;
		Entry e2 = null;
		Entry e3 = null;
		try {
			e1 = new Entry();
			e1.setSailId(new SailId("e1"));
			e1.setDivision(div);
			e2 = new Entry();
			e2.setSailId(new SailId("e2"));
			e2.setDivision(div);
			e3 = new Entry();
			e3.setSailId(new SailId("e3"));
			e3.setDivision(div);
		} catch (RatingOutOfBoundsException e) {
		}

		// start with 3 entries, 1 race, finishing e1,e2,e3
		// then keep e2's 2nd place, give her a MAN=5... e3 points should still
		// be 3
		// next give e2 a 2nd, and OCS and a MAN=5... now e2=5, but e3=2 points

		reg.addEntry(e1);
		reg.addEntry(e2);
		reg.addEntry(e3);

		Race r1 = new Race(reg, "1");

		reg.addRace(r1);

		r1.setFinish(new Finish(r1, e1, SailTime.NOTIME, new FinishPosition(1), null));
		r1.setFinish(new Finish(r1, e2, SailTime.NOTIME, new FinishPosition(2), null));
		r1.setFinish(new Finish(r1, e3, SailTime.NOTIME, new FinishPosition(3), null));

		reg.scoreRegatta();
		RegattaScoringModel scorer = reg.getScoringManager();

		// should be normal 1,2,3 on finish points
		RacePoints p1 = scorer.getRacePointsList().find(r1, e1, div);
		RacePoints p2 = scorer.getRacePointsList().find(r1, e2, div);
		RacePoints p3 = scorer.getRacePointsList().find(r1, e3, div);

		assertNotNull("Finish points missing", p1);
		assertEquals("part 1 e1 bad points", 1, p1.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p2);
		assertEquals("part 1 e2 bad points", 2, p2.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p3);
		assertEquals("part 1 e3 bad points", 3, p3.getPoints(), ERR_MARGIN);

		// now, keep e2's 2nd place, but set man points to 5... e3 should not
		// move
		r1.setFinish(new Finish(r1, e2, SailTime.NOTIME, new FinishPosition(2), ManPenalty(5)));
		reg.scoreRegatta();

		// should be 1, 5, 3
		p1 = scorer.getRacePointsList().find(r1, e1, div);
		p2 = scorer.getRacePointsList().find(r1, e2, div);
		p3 = scorer.getRacePointsList().find(r1, e3, div);

		assertNotNull("Finish points missing", p1);
		assertEquals("part 2 e1 bad points", 1, p1.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p2);
		assertEquals("part 2 e2 bad points", 5, p2.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p3);
		assertEquals("part 2 e3 bad points", 3, p3.getPoints(), ERR_MARGIN);

		// now, keep e2's 2nd place, set man points to 5...AND add an OCS...
		// e3's points should move up...
		Penalty pen2 = ManPenalty(5);
		pen2.addOtherPenalty(Penalty.OCS);
		Finish f2 = new Finish(r1, e2, SailTime.NOTIME, new FinishPosition(2), pen2);
		r1.setFinish(f2);
		reg.scoreRegatta();

		// should be 1, 5, 3
		p1 = scorer.getRacePointsList().find(r1, e1, div);
		p2 = scorer.getRacePointsList().find(r1, e2, div);
		p3 = scorer.getRacePointsList().find(r1, e3, div);

		assertNotNull("Finish points missing", p1);
		assertEquals("part 3 e1 bad points", 1, p1.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p2);
		assertEquals("part 3 e2 bad points", 5, p2.getPoints(), ERR_MARGIN);
		assertNotNull("Finish points missing", p3);
		assertEquals("part 3 e3 wrong points", 2, p3.getPoints(), ERR_MARGIN);

	}

	private Penalty ManPenalty(double pts) {
		Penalty m = new Penalty(RDG);
		m.setPoints(pts);
		return m;
	}

}
/**
 * $Log: PenaltyTests.java,v $ Revision 1.5 2006/01/15 21:08:39 sandyg resubmit
 * at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.10 2005/08/19 01:52:34 sandyg 4.3.1.03 tests
 * 
 * Revision 1.9 2005/02/27 23:24:37 sandyg added IRC, changed corrected time
 * calcs to no longer round to a second
 * 
 * Revision 1.8 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.7 2003/04/30 01:02:33 sandyg fixed several parsing bugs, notably
 * no support for mixed case
 * 
 * Revision 1.6 2003/04/27 21:00:46 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.5 2003/04/23 00:30:21 sandyg added Time-based penalties
 * 
 * Revision 1.4 2003/04/20 15:44:30 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.3 2003/03/28 03:07:51 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.2 2003/01/04 17:09:27 sandyg Prefix/suffix overhaul
 * 
 */
