// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SubDivisionTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;

public class SubDivisionTests extends JavascoreTestCase {

	public SubDivisionTests(String s) {
		super(s);
	}

	Regatta reg;
	SubDivision sd1;
	SubDivision sd2;
	Division div;
	Entry e1;
	Entry e2;
	Entry e3;

	private void forceDivision(Entry ent, Division d) {
		try {
			ent.setDivision(d);
		} catch (RatingOutOfBoundsException e) {}
	}

	@Override protected void setUp() {
		sd1 = new SubDivision();

		sd2 = new SubDivision();
		div = new Division("d1");

		sd2.setName("sd2");
		sd2.setMonopoly(!sd2.isMonopoly());
		sd2.setParent(div);
		sd2.setScoreSeparately(!sd2.isScoreSeparately());

		e1 = new Entry();
		forceDivision(e1, div);
		e2 = new Entry();
		forceDivision(e2, div);

		// e3 will NOT be in subdivision
		e3 = new Entry();
		forceDivision(e3, new Division("whocares"));

		reg = new Regatta();
		reg.addDivision(div);
		reg.addSubDivision(sd1);
		reg.addSubDivision(sd2);
		reg.addEntry(e1);
		reg.addEntry(e2);
		reg.addEntry(e3);
	}

	public void testXml() {
		// default Division goes to/from xml ok
		assertTrue("sd1", xmlEquals(sd1));

		// sd2 with all non default
		sd2.addEntry(e1);
		sd2.addEntry(e2);
		sd2.addEntry(e3);
		assertTrue("sd2", xmlEquals(sd2, reg));
	}

	public void testContains() {
		sd2.addEntry(e1);
		sd2.addEntry(e2);

		assertTrue("sd2 has e1", sd2.contains(e1));

		assertTrue("sd2 not have e3", !sd2.contains(e3));
	}

	public void testEntries() {
		assertEquals("sd2 no entries", sd2.getNumEntries(), 0);

		sd2.addEntry(e1);
		assertEquals("sd2 has one entry", sd2.getNumEntries(), 1);

		sd2.addEntry(e1); // adding e1 again should have no effect
		assertEquals("sd2 still one entry", sd2.getNumEntries(), 1);

		sd2.addEntry(e2);
		assertEquals("sd2 now has 2 entries", sd2.getNumEntries(), 2);

		sd2.removeEntry(e2);
		assertEquals("sd2 back to 1 entry", sd2.getNumEntries(), 1);

		sd2.removeEntry(e2);
		assertEquals("sd2 still at 1 entry", sd2.getNumEntries(), 1);

		sd2.removeEntry(e1);
		assertEquals("sd2 back to 0", sd2.getNumEntries(), 0);
	}

	public void testMonopoly() {
		// default is false
		assertTrue("sd1 not a monopoly", !sd1.isMonopoly());

		//change and retest
		sd1.setMonopoly(true);
		assertTrue("sd1 is monopoly", sd1.isMonopoly());
	}

	public void testScoreSeparately() {
		// default is false
		assertTrue("sd1 not scored separately", !sd1.isScoreSeparately());

		//change and retest
		sd1.setScoreSeparately(true);
		assertTrue("sd1 is separate", sd1.isScoreSeparately());
	}

	public void testQualifying() {
		// default is qual
		assertTrue("sd1 should be final", !sd1.isGroupQualifying());
		assertTrue("sd1 shoudl not be qualifying", !sd1.isGroupQualifying());

		//change and retest
		sd1.setGroup(SubDivision.QUALIFYING);
		assertTrue("sd1 should not be final", sd1.isGroupQualifying());
		assertTrue("sd1 should be qualifying", sd1.isGroupQualifying());
	}

}
/**
 * $Log: SubDivisionTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.9 2004/05/06 00:25:22 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.8 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.7 2003/03/16 20:40:13 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.6 2003/01/06 00:32:37 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.5 2003/01/04 17:09:28 sandyg Prefix/suffix overhaul
 * 
 */
