// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringFleetTests.java,v 1.6 2006/01/19 02:27:41 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringFleetTests extends JavascoreTestCase {

	public void testPhrfFleetScoringSameFalse() throws Exception {
		Regatta reg = loadTestRegatta( "311-PhrfFleetTest.regatta");
		Fleet fleet = reg.getFleet("PHRF overall");

		Division diva = reg.getDivision("PHRF A");
		Division divb = reg.getDivision("PHRF B");

		Entry papab = (Entry) reg.getAllEntries().findId("244").get(0);
		Entry spankme = (Entry) reg.getAllEntries().findId("312").get(0);
		Entry tarheel = (Entry) reg.getAllEntries().findId("2502").get(0);
		Entry dixie = (Entry) reg.getAllEntries().findId("153").get(0);

		assertNotNull(fleet);
		assertNotNull(diva);
		assertNotNull(divb);
		assertNotNull(papab);
		assertNotNull(spankme);
		assertNotNull(tarheel);
		assertNotNull(dixie);

		Race race1 = (Race) reg.getRaceIndex(0);
		assertNotNull(race1);

		fleet.setSameStartSameClass(false);
		reg.scoreRegatta();

		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		assertNotNull(rpl);

		RacePoints pts = null;

		// in race 1: class A finish should be 1) papab, 2) spankme
		pts = rpl.find(race1, papab, diva);
		assertNotNull(pts);
		assertEquals("papab, diva", 1.00, pts.getPoints(), ERR_MARGIN);

		pts = rpl.find(race1, spankme, diva);
		assertNotNull(pts);
		assertEquals("spankme, diva", 2.00, pts.getPoints(), ERR_MARGIN);

		// in race 1: class B finish should be 1) dixie, 2) tarheel
		pts = rpl.find(race1, dixie, divb);
		assertNotNull(pts);
		assertEquals("dixie, divb", 1.00, pts.getPoints(), ERR_MARGIN);

		pts = rpl.find(race1, tarheel, divb);
		assertNotNull(pts);
		assertEquals("tarheel, divb", 2.00, pts.getPoints(), ERR_MARGIN);

		// in race 1: Fleet finish should be 1)dixie, 2) papab, 3) spankme, 4) tarheet
		pts = rpl.find(race1, dixie, fleet);
		assertNotNull(pts);
		assertEquals("dixie, divb", 1.00, pts.getPoints(), ERR_MARGIN);

		pts = rpl.find(race1, papab, fleet);
		assertNotNull(pts);
		assertEquals("papab, diva", 2.00, pts.getPoints(), ERR_MARGIN);

		pts = rpl.find(race1, spankme, fleet);
		assertNotNull(pts);
		assertEquals("spankme, diva", 3.00, pts.getPoints(), ERR_MARGIN);

		pts = rpl.find(race1, tarheel, fleet);
		assertNotNull(pts);
		assertEquals("tarheel, divb", 4.00, pts.getPoints(), ERR_MARGIN);
	}

	public void testPhrfFleetScoringSameTrue() throws Exception {
		try {
			Regatta reg = loadTestRegatta( "311-PhrfFleetTest.regatta");

			Fleet fleet = reg.getFleet("PHRF overall");

			Division diva = reg.getDivision("PHRF A");
			Division divb = reg.getDivision("PHRF B");

			Entry papab = (Entry) reg.getAllEntries().findId("244").get(0);
			Entry spankme = (Entry) reg.getAllEntries().findId("312").get(0);
			Entry tarheel = (Entry) reg.getAllEntries().findId("2502").get(0);
			Entry dixie = (Entry) reg.getAllEntries().findId("153").get(0);

			assertNotNull(fleet);
			assertNotNull(diva);
			assertNotNull(divb);
			assertNotNull(papab);
			assertNotNull(spankme);
			assertNotNull(tarheel);
			assertNotNull(dixie);

			Race race1 = (Race) reg.getRaceIndex(0);
			assertNotNull(race1);

			// === now (the sackett bug, change samestart - results should be the same
			// in PHRF - classes start 5 minutes apart
			fleet.setSameStartSameClass(true);
			reg.scoreRegatta();

			RacePointsList rpl = reg.getScoringManager().getRacePointsList();
			assertNotNull(rpl);

			// in race 1: class A finish should be 1) papab, 2) spankme
			RacePoints pts = rpl.find(race1, papab, diva);
			assertNotNull(pts);
			assertEquals("papab, diva", 1.00, pts.getPoints(), ERR_MARGIN);

			pts = rpl.find(race1, spankme, diva);
			assertNotNull(pts);
			assertEquals("spankme, diva", 2.00, pts.getPoints(), ERR_MARGIN);

			// in race 1: class B finish should be 1) dixie, 2) tarheel
			pts = rpl.find(race1, dixie, divb);
			assertNotNull(pts);
			assertEquals("dixie, divb", 1.00, pts.getPoints(), ERR_MARGIN);

			pts = rpl.find(race1, tarheel, divb);
			assertNotNull(pts);
			assertEquals("tarheel, divb", 2.00, pts.getPoints(), ERR_MARGIN);

			// in race 1: Fleet finish should be 1)dixie, 2) papab, 3) spankme, 4) tarheet
			pts = rpl.find(race1, dixie, fleet);
			assertNotNull(pts);
			assertEquals("dixie, divb", 1.00, pts.getPoints(), ERR_MARGIN);

			pts = rpl.find(race1, papab, fleet);
			assertNotNull(pts);
			assertEquals("papab, diva", 2.00, pts.getPoints(), ERR_MARGIN);

			pts = rpl.find(race1, spankme, fleet);
			assertNotNull(pts);
			assertEquals("spankme, diva", 3.00, pts.getPoints(), ERR_MARGIN);

			pts = rpl.find(race1, tarheel, fleet);
			assertNotNull(pts);
			assertEquals("tarheel, divb", 4.00, pts.getPoints(), ERR_MARGIN);

		}
		catch (java.io.IOException e) {
			assertTrue("ioexception!", false);
		}
	}

	public void test1DFleetScoring() throws Exception {
			Regatta reg = loadTestRegatta( "0000-Fleet-Test.regatta");

			Division j24 = reg.getDivision("J24");
			Entry y1 = (Entry) reg.getAllEntries().findId("y 1").get(0);
			Entry y4 = (Entry) reg.getAllEntries().findId("y 4").get(0);
			Entry g1 = (Entry) reg.getAllEntries().findId("g 1").get(0);
			Entry o1 = (Entry) reg.getAllEntries().findId("o 1").get(0);
			Entry p3 = (Entry) reg.getAllEntries().findId("p 3").get(0);
			Entry o3 = (Entry) reg.getAllEntries().findId("o 3").get(0);

			Race r1 = (Race) reg.getRaceIndex(0);

			reg.scoreRegatta();

			// have 4 divs, J24 green pink, orange, and yellow
			// one fleet J24 includes all divs, startsamescoretogether is true
			// yellow has 4 entries, the others all 3
			// race 1 pits yellow and green in start 1, orange and pink in start 2
			// finish is:  y1,y2,y3,g1,g2,o1,g3,p1,o2,p2,o3 & DSQ,p3, y4 is DNC

			// so numentries in fleet should be 13
			// y1, race 1 should have 1 point
			// y4, race 1 should have 8 points for its dnc
			// g1, race 1 should have 4 points
			// o1 (finishing ahead of last 1st start finisher) should have 1 point
			// p3, race should have 5 points ( 6th finish, but o3 dsq)
			// o3, should have 7 pts for dsq

			RacePointsList rpl = reg.getScoringManager().getRacePointsList();

			assertEquals("r1,y1", 1.00, rpl.find(r1, y1, j24).getPoints(), ERR_MARGIN);
			assertEquals("r1,g1", 4.00, rpl.find(r1, g1, j24).getPoints(), ERR_MARGIN);
			assertEquals("r1,o1", 1.00, rpl.find(r1, o1, j24).getPoints(), ERR_MARGIN);
			assertEquals("r1,p3", 5.00, rpl.find(r1, p3, j24).getPoints(), ERR_MARGIN);
			assertEquals("r1,o3", 7.00, rpl.find(r1, o3, j24).getPoints(), ERR_MARGIN);
			assertEquals("r1,y4", 8.00, rpl.find(r1, y4, j24).getPoints(), ERR_MARGIN);

	}


	public ScoringFleetTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (!JavaScore.hasInstance()) {
			JavaScore js = JavaScore.getInstance();
		}
		
	}

}
/**
 * $Log: ScoringFleetTests.java,v $ Revision 1.6 2006/01/19 02:27:41 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.5 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.7 2005/05/26 01:46:51 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.6 2005/04/23 21:55:31 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.5 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.4 2003/04/27 21:00:54 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.3 2003/01/04 17:09:28 sandyg Prefix/suffix overhaul
 * 
 */
