//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RacePointsTests.java,v 1.7 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Locale;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.util.Util;

/**
 * Dummy template class for create unit test cases
 */
public class RacePointsTests extends JavascoreTestCase implements Constants {

	Regatta regatta;
	Race race1, race3;

	@Override protected void setUp() throws Exception {
		super.setUp();
		Util.initLocale(Locale.US);
		regatta = loadTestRegatta( "0000-Test-Master.regatta");

		race1 = (Race) regatta.getRace("1");
		race3 = (Race) regatta.getRace("3");
	}

	public void testCompareTo() throws Exception {
		regatta = loadTestRegatta( "0000-Test-Master.regatta");
		java.util.Comparator<RacePoints> c = new RacePointsList.ComparatorTimePosition(true);

		Race r1 = (Race) regatta.getRaceIndex(0);
		// Race r2 = (Race) regatta.getRaceIndex(1);
		// Race r3 = (Race) regatta.getRaceIndex(2);

		Entry eee = (Entry) regatta.getAllEntries().findId("E").get(0);
		// Entry ccc = (Entry) regatta.getAllEntries().findId("C").get(0);
		// Entry ddd = (Entry) regatta.getAllEntries().findId("D").get(0);
		// Entry aaa = (Entry) regatta.getAllEntries().findId("A").get(0);
		// Entry bbb = (Entry) regatta.getAllEntries().findId("B").get(0);
		Entry x900 = (Entry) regatta.getAllEntries().findId("900").get(0);

		RacePoints e1 = regatta.getScoringManager().getRacePointsList().find(
				r1, eee, null);
		RacePoints x9001 = regatta.getScoringManager().getRacePointsList()
				.find(r1, x900, null);
		assertEquals(-1, c.compare(x9001, e1));

	}

	public void testEquals() {
		RacePoints rp = (RacePoints) regatta.getScoringManager()
				.getRacePointsList().get(0);
		RacePoints rp2 = (RacePoints) rp.clone();

		assertEquals("=clone()", rp, rp2);

		rp2.setThrowout(!rp2.isThrowout());
		assertTrue("throwout not equal", !rp.equals(rp2));

		rp2.setThrowout(!rp2.isThrowout());
		rp2.setPoints(rp.getPoints() + 0.0001);
		assertTrue("points slightly off", !rp.equals(rp2));
	}

	public void testToString1() {

		Entry e = null;
		RacePoints rp = null;
		String rps = null;

		/* boat 800 in race 1 should be DNC */
		e = (Entry) regatta.getAllEntries().findId("800").get(0);
		rp = regatta.getScoringManager().getRacePointsList().find(race1, e,
				null);
		rps = RacePoints.format(rp, true);
		assertEquals("800/race1/dnc", "[11/DNC]", rps);
		rps = RacePoints.format(rp, false);
		assertEquals("800/race1/dnc", "[DNC]", rps);

		e = (Entry) regatta.getAllEntries().findId("33").get(0);
		rp = (RacePoints) regatta.getScoringManager().getRacePointsList().find(
				race1, e, null).clone();
		rp.setPoints(60);
		assertEquals("toString of 60", "60", RacePoints.format(rp));

		rp.setPoints(60.001);
		assertEquals("toString of 60.001", "60", RacePoints.format(rp));

		assertEquals("local not us", Locale.US, Util.getLocale());
		rp.setPoints(60.75);
		assertEquals("toString of 60.75", "60.75", RacePoints.format(rp));
	}

	public void testToString2() {
		assertEquals("local not us", Locale.US, Locale.getDefault());
		Entry e = (Entry) regatta.getAllEntries().findId("800").get(0);

		RacePoints rp = null;

		// clean finish, 11th place
		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(9),
				new Penalty(NO_PENALTY)));
		rp.setPoints(11.0);
		assertEquals("11", RacePoints.format(rp, true));
		assertEquals("11", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[11]", RacePoints.format(rp, false));

		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(DNF),
				new Penalty(DNF)));
		rp.getFinish().setPenalty(new Penalty(DNF));
		rp.setPoints(11.0);
		assertEquals("11/DNF", RacePoints.format(rp, true));
		assertEquals("DNF", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[11/DNF]", RacePoints.format(rp, true));
		assertEquals("[DNF]", RacePoints.format(rp, false));

		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(DNF),
				new Penalty(DNF)));
		rp.getFinish().setPenalty(new Penalty(DSQ));
		rp.setPoints(11.0);
		assertEquals("11/DSQ", RacePoints.format(rp, true));
		assertEquals("DSQ", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[11/DSQ]", RacePoints.format(rp, true));
		assertEquals("[DSQ]", RacePoints.format(rp, false));

		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(DNF),
				new Penalty(DNF)));
		rp.getFinish().setPenalty(new Penalty(DGM));
		rp.setPoints(11.0);
		assertEquals("11/DGM", RacePoints.format(rp, true));
		assertEquals("DGM", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[11/DGM]", RacePoints.format(rp, true));
		assertEquals("[DGM]", RacePoints.format(rp, false));

		Penalty mm = new Penalty(RDG);
		mm.setPoints(3.3);
		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(9), mm));
		rp.setPoints(3.3);
		assertEquals("3.3/RDG", RacePoints.format(rp, true));
		assertEquals("3.3/RDG", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[3.3/RDG]", RacePoints.format(rp, true));
		assertEquals("[3.3/RDG]", RacePoints.format(rp, false));

		rp = new RacePoints(new Finish(race1, e, 0L, new FinishPosition(9),
				new Penalty(OCS)));
		rp.getFinish().getPenalty().addOtherPenalty(RDG);
		rp.getFinish().getPenalty().setRedressLabel( "MAN");
		
		rp.setPoints(3.3);
		assertEquals("3.3/OCS,MAN", RacePoints.format(rp, true));
		assertEquals("3.3/OCS,MAN", RacePoints.format(rp, false));
		rp.setThrowout(true);
		assertEquals("[3.3/OCS,MAN]", RacePoints.format(rp, true));
		assertEquals("[3.3/OCS,MAN]", RacePoints.format(rp, false));
	}

	public void testXml() {
		RacePoints rp = (RacePoints) regatta.getScoringManager()
				.getRacePointsList().get(0);
		assertTrue("xmltest", xmlEquals(rp));
	}

	public void testCompare() {
		RacePoints rp = (RacePoints) regatta.getScoringManager()
				.getRacePointsList().get(0);
		RacePoints rp2 = (RacePoints) rp.clone();

		int x = rp.compareTo(rp);
		assertTrue("compare rp to rp", (x == 0));
		x = rp.compareTo(rp2);
		assertTrue("compare rp to rp2", (x == 0));

		rp2.setThrowout(!rp2.isThrowout());
		x = rp.compareTo(rp2);
		assertTrue("compare throwout diff", (x == 0));

		rp2.setThrowout(!rp2.isThrowout());
		rp2.setPoints(rp.getPoints() + 0.0001);
		x = rp.compareTo(rp2);
		assertTrue("compare rp2 = rp1+.0001", (x == -1));

		rp2.setPoints(rp.getPoints() - 0.0002);
		x = rp.compareTo(rp2);
		assertTrue("compare rp2 = rp1-.0001", (x == 1));
	}

	public RacePointsTests(String name) {
		super(name);
	}

}
/**
 * $Log: RacePointsTests.java,v $ Revision 1.7 2006/05/19 05:48:43 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.6 2006/01/19 02:27:41 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.5 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.11.2.1 2005/11/01 02:36:58 sandyg java5 using generics
 * 
 * Revision 1.11 2005/08/19 01:52:34 sandyg 4.3.1.03 tests
 * 
 * Revision 1.10 2005/05/26 01:46:51 sandyg fixing resource access/lookup
 * problems
 * 
 * Revision 1.9 2005/04/23 21:55:31 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.8 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.7 2003/05/18 17:21:21 sandyg no message
 * 
 * Revision 1.6 2003/04/27 21:00:47 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.5 2003/04/20 15:44:30 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.4 2003/01/04 17:09:27 sandyg Prefix/suffix overhaul
 * 
 */
