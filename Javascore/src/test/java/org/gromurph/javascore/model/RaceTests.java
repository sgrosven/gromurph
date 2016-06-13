// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RaceTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;

/**
 * Tests for the Race class
 */
public class RaceTests extends JavascoreTestCase implements Constants {

	public RaceTests(String name) {
		super(name);
	}

	Race r1, r2;
	Regatta regatta;
	Division div1, div2;
	Entry e11, e12, e13, e21, e22, e23;

	private void forceDivision(Entry ent, Division d) {
		try {
			ent.setDivision(d);
		}
		catch (RatingOutOfBoundsException e) {}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		regatta = new Regatta();
		div1 = new Division("Div1");
		div2 = new Division("Div2");
		regatta.addDivision(div1);
		regatta.addDivision(div2);

		e11 = new Entry();
		e11.setSailId(new SailId("11"));
		e11.setBoatName("e11");
		forceDivision(e11, div1);
		e12 = new Entry();
		e12.setSailId(new SailId("12"));
		e12.setBoatName("e12");
		forceDivision(e12, div1);
		e13 = new Entry();
		e13.setSailId(new SailId("13"));
		e13.setBoatName("e13");
		forceDivision(e13, div1);
		
		e21 = new Entry();
		e21.setSailId(new SailId("11"));
		e21.setBoatName("e21");
		forceDivision(e21, div2);
		e22 = new Entry();
		e22.setSailId(new SailId("12"));
		e22.setBoatName("e22");
		forceDivision(e22, div2);
		e23 = new Entry();
		e23.setSailId(new SailId("13"));
		e23.setBoatName("e23");
		forceDivision(e23, div2);

		regatta.addEntry(e11);
		regatta.addEntry(e12);
		regatta.addEntry(e13);
		regatta.addEntry(e21);
		regatta.addEntry(e22);
		regatta.addEntry(e23);

		r1 = new Race(regatta, "1");
		r2 = new Race(regatta, "2");

		r1.setComment("Conditions SW 10-15");
		r2.setComment("Clear skies, slight ebb tide");
	}

	public void testXml() {
		// default fleet goes to/from xml ok
		assertTrue("r1", xmlEquals(r1));

		// f2 with no divisions, but delta props is OK
		assertTrue("r2 empty", xmlEquals(r2));

		r2.setLongDistance(!r2.isLongDistance());
		r2.setStartDate(SailTime.forceStringToDate("03/04/2001"));
		r2.setBFactor(RatingPhrfTimeOnTime.BFACTOR_HEAVY);
		assertTrue("r2 moded", xmlEquals(r2));
	}

	public void testNumCompetitors() {
		
		r1.setFinish(new Finish(r1, e11, SailTime.forceToLong("12:10:00"), new FinishPosition(1), null));
		r1.setFinish(new Finish(r1, e12, SailTime.forceToLong("12:10:05"), new FinishPosition(2), null));
		r1.setFinish(new Finish(r1, e13, SailTime.NOTIME, new FinishPosition(5), new Penalty(TLE)));
		
		r1.setFinish(new Finish(r1, e21, SailTime.forceToLong("12:10:15"), new FinishPosition(4), null));
		r1.setFinish(new Finish(r1, e22, SailTime.NOTIME, new FinishPosition(5), new Penalty(DNF)));
		r1.setFinish(new Finish(r1, e23, SailTime.NOTIME, new FinishPosition(6), new Penalty(DNC)));

		regatta.scoreRegatta();

		assertEquals("r1, div1 finishers", 2, r1.getNumberFinishers(div1));
		assertEquals("r1, div2 finishers", 1, r1.getNumberFinishers(div2));
	}

	public void testRaceTime() throws Exception {
		Regatta tester = loadTestRegatta( "Test_5602_Timing.regatta");
		tester.scoreRegatta();

		Entry e1 = tester.getAllEntries().findId("53501").get(0);
		assertNotNull(e1);
		Entry e10 = tester.getAllEntries().findId("USA 9").get(0);
		assertNotNull(e10);
		Entry e8 = tester.getAllEntries().findId("76").get(0);
		assertNotNull(e8);

		Division a = tester.getDivision("Magothy A");
		assertNotNull(a);
		Race r1 = tester.getRace("1, Series 1");
		assertNotNull(r1);
		assertNotNull(r1.getStartDate());
		assertNotNull(r1);
		assertTrue((SailTime.NOTIME != r1.getStartTimeRaw(a)));

		// started 18:15:00
		assertEquals("21-Apr-2010", SailTime.dateToString(r1.getStartDate()));
		assertEquals("18:15:00.0", SailTime.toString(r1.getStartTimeRaw(a)));
		assertEquals("18:15:00.0", SailTime.toString(r1.getStartTimeAdjusted(a)));

		Finish f1 = r1.getFinish(e1);
		assertEquals("19:45:02.0", SailTime.toString(f1.getFinishTime()));
		assertEquals("01:30:02.0", SailTime.toString(f1.getElapsedTime()));
		// e1 fin @ 19:45:02 -   elapsed 1:30:02

		// set the next day flag... should have no effect on elapsed/corrected time
		r1.setNextDay(a, true);
		tester.scoreRegatta();

	}

}
/**
 * $Log: RaceTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.9 2003/04/27 21:00:48 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.8 2003/04/20 15:44:30 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.7 2003/03/30 00:03:59 sandyg gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
 * 
 * Revision 1.6 2003/01/06 00:32:37 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.5 2003/01/04 17:09:27 sandyg Prefix/suffix overhaul
 * 
 */
