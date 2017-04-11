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
		String c = RacePoints.format(rp, true);
		assertEquals("3.3/OCS,MAN", c);
		c = RacePoints.format(rp, false);
		assertEquals("3.3/OCS,MAN", c);
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
