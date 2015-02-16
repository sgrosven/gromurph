// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: EntryTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingMorc;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.util.Person;

/**
 * Dummy Entry class for create unit test cases
 */
public class EntryTests extends org.gromurph.javascore.JavascoreTestCase {
	public EntryTests(String name) {
		super(name);
	}

	Entry e1;

	@Override
	protected void setUp() throws ScoringException {
		e1 = new Entry();
		e1.setClub("Test Club");
		Division div = new Division("PHRF A0", new RatingPhrf(-999), new RatingPhrf(40));
		try {
			e1.setDivision(div);
			e1.setRating(new RatingPhrf(-15));
		}
		catch (RatingOutOfBoundsException ex) {
			fail(ex.toString());
		}
		e1.setSkipper(new Person("first", "last"));
		e1.setBoat(new Boat("boatname", "123", "owner"));

	}

	public void testRating() throws Exception {
		Regatta reg = new Regatta();
		reg.removeAllDivisions();

		Division j24 = new Division("J24");
		Division j105 = new Division("J105");
		Division phrfC = new Division("PHRF C", new RatingPhrf(160), new RatingPhrf(255));
		Division phrfA = new Division("PHRF A", new RatingPhrf(0), new RatingPhrf(50));

		Rating phrf168 = new RatingPhrf(168);
		Rating OneDj24 = j24.getMinRating();

		reg.addDivision(j24);
		reg.addDivision(phrfC);
		reg.addDivision(phrfA);

		Entry e2 = new Entry();
		assertEquals(0, e2.getNumRatings());

		reg.addEntry(e2);
		assertEquals(0, e2.getNumRatings());

		try {
			e2.setDivision(j24);
		}
		catch (RatingOutOfBoundsException e) {
			fail("RatingOutOfBoundsException exception setting j24 rating");
		}
		assertEquals(j24, e2.getDivision());
		Rating rating = e2.getRating();
		assertEquals(OneDj24, rating);
		assertEquals(e2.getRating().getSystem(), j24.getSystem());
		assertEquals(1, e2.getNumRatings());

		try {
			e2.setRating(phrf168);
		}
		catch (RatingOutOfBoundsException ex) {
			fail("RatingOutOfBoundsException exception setting phrf168 rating");
		}
		assertEquals(2, e2.getNumRatings());
		assertEquals(j24, e2.getDivision());
		assertEquals(OneDj24, e2.getRating());

		try {
			e2.setDivision(phrfC);
		}
		catch (RatingOutOfBoundsException ex) {
			fail(ex.toString());
		}
		assertEquals(phrfC, e2.getDivision());
		assertEquals(phrf168, e2.getRating());

		try {
			e2.setDivision(phrfA);
			fail("did not get RatingOutOfBoundsException set division to phrfA");
		}
		catch (RatingOutOfBoundsException ex) {}

		Regatta reg2 = new Regatta();
		xmlObjectToObject(reg, reg2);

		assertEquals(1, reg.getNumEntries());
		assertEquals(1, reg2.getNumEntries());

		Entry e3 = (Entry) reg2.getAllEntries().get(0);
		assertEquals(e2, e3);

		assertEquals(2, e3.getNumRatings());
		assertEquals(phrfC, e3.getDivision());
		assertEquals(phrf168, e3.getRating());

		Rating phrf172 = new RatingPhrf(172);
		try {
			e2.setRating(phrf172);
		}
		catch (RatingOutOfBoundsException ex) {
			fail(ex.toString());
		}
		assertEquals(2, e2.getNumRatings());

		Rating phrf10 = new RatingPhrf(10); // NOT in PhrfC bounds
		try {
			e2.setRating(phrf10);
			fail("should have thrown an error");
		}
		catch (Exception RatingOutOfBoundsException) {}

		try {
			e2.setDivision(phrfA);
			fail("should have thrown an error");
		}
		catch (Exception RatingOutOfBoundsException) {}

		try {
			e2.setRating(new RatingOneDesign("J105"));
		}
		catch (RatingOutOfBoundsException ex) {
			fail("should not have thrown an error");
		}
		assertEquals(2, e2.getNumRatings());

		try {
			e2.setDivision(j105);
		}
		catch (RatingOutOfBoundsException ex) {
			fail("should not have thrown an error");
		}
		assertEquals(j105.getSystem(), e2.getRating().getSystem());
		assertEquals(2, e2.getNumRatings());

		try {
			e2.setDivision(j24);
		}
		catch (Exception RatingOutOfBoundsException) {
			fail("should not have thrown an error");
		}
		assertEquals(2, e2.getNumRatings());

		Division morc = new Division("MORC", new RatingMorc(10), new RatingMorc(99));
		try {
			e2.setDivision(morc);
		}
		catch (RatingOutOfBoundsException e) {}

		// morce rating added
		assertEquals(3, e2.getNumRatings());
	}

	public void testXml() {
		//        assertTrue( "xmlequals div", xmlEquals( e1.getDivision()));
		//        assertTrue( "xmlequals skipper", xmlEquals( e1.getSkipper()));
		//        assertTrue( "xmlequals crew", xmlEquals( e1.getCrew()));
		//        assertTrue( "xmlequals boat", xmlEquals( e1.getBoat()));

		assertTrue("xmlequals e1", xmlEquals(e1));
	}

	public void testEquals() {
		Entry e2 = (Entry) e1.clone();
		assertEquals(e1, e2);
	}

	public void testCrew() {
		Entry e2 = (Entry) e1.clone();

		assertEquals(0, e2.getNumCrew());

		e2.addCrew(new Person("one", "Cone"));
		e2.addCrew(new Person("two", "Ctwo"));
		e2.addCrew(new Person("three", "Cthree"));
		assertEquals(3, e2.getNumCrew());

	}

	public void testBowNumbers() {
		try {
			Regatta reg = new Regatta();
			reg.setUseBowNumbers(true);
			Division j22 = new Division("j22");

			Entry e1a = new Entry();
			e1a.setSailId(new SailId("10"));
			e1a.setBow("1");
			e1a.setDivision(j22);
			Entry e2 = new Entry();
			e2.setSailId(new SailId("20"));
			e1a.setBow("2");
			e2.setDivision(j22);
			Entry e3 = new Entry();
			e3.setSailId(new SailId("30"));
			e1a.setBow("3");
			e3.setDivision(j22);

			reg.addEntry(e1a);
			reg.addEntry(e2);
			reg.addEntry(e3);

			e3.setBow("2");
		}
		catch (Exception e) {
			fail(e.toString());
		}
	}

}
/**
 * $Log: EntryTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.13.2.1 2005/11/01 02:36:58 sandyg java5 using generics
 * 
 * Revision 1.13 2005/02/27 23:24:36 sandyg added IRC, changed corrected time calcs to no longer round to a second
 * 
 * Revision 1.12 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.11 2004/01/17 22:27:21 sandyg First cut at unlimited number of crew, request 512304
 * 
 * Revision 1.10 2003/07/10 02:50:25 sandyg overrides a contradictory legacy one-design rating on xmlRead
 * 
 * Revision 1.9 2003/05/18 17:21:21 sandyg no message
 * 
 * Revision 1.8 2003/04/27 21:00:40 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/03/16 20:39:16 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.6 2003/01/06 00:32:37 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.5 2003/01/05 21:16:34 sandyg regression unit testing following rating overhaul from entry to boat
 * 
 * Revision 1.4 2003/01/04 17:09:27 sandyg Prefix/suffix overhaul
 * 
 */
