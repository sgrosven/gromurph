// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogImportTableTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.util.*;

import org.gromurph.javascore.*;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;

/**
 * Dummy template class for create unit test cases
 */
public class DialogImportTableTests extends JavascoreTestCase {

	protected static ResourceBundle res = JavaScoreProperties.getResources();

	public DialogImportTableTests(String name) {
		super(name);
	}

	private void importArray(String[][] rows, String[] cols, DialogImportTable importer) {
		List<List<String>> cells = new ArrayList<List<String>>();
		for (int r = 0; r < rows.length; r++) {
			cells.add(Arrays.asList(rows[r]));
		}
		List fields = Arrays.asList(cols);

		importer.convertTableToRegatta(fields, cells);
	}

	String[] entryfields1 = new String[] { res.getString("GenSail"), res.getString("GenBoatName"),
			res.getString("GenSkipperLast"), res.getString("GenDivision") };

	String[][] entrytable1 = new String[][] { { "1", "Alpha", "Anne", "J22" }, { "2", "Beta", "Bob", "J24" },
			{ "3", "Charlie", "Carrie", "J22" }, { "4", "Delta", "Doug", "J24" } };

	String[] entryfields2 = new String[] { res.getString("GenBoatName"), res.getString("GenSkipperLast"),
			res.getString("GenSail"), res.getString("GenDivision") };

	String[][] entrytable2 = new String[][] { { "Alpha", "Anne", "1", "J22" }, { "Beta", "Bob", "2", "J24" },
			{ "Charlie", "Carrie", "3", "J22" }, { "Delta", "Doug", "4", "J24" } };

	public void testImportEntries() {
		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions(); // wipe out the pre-existing divisions

		importArray(  entrytable1, entryfields1, new DialogImportEntries(null));

		assertEquals("num divs is 2", 2, reg.getNumDivisions());
		assertEquals("num entries is 4", 4, reg.getNumEntries());

		Division j24 = reg.getDivision("J24");
		Division j22 = reg.getDivision("J22");
		assertEquals(2, j24.getNumEntries());
		assertEquals(2, j22.getNumEntries());

		Entry j22e1 = reg.getAllEntries().get(0);
		Entry j24e2 = reg.getAllEntries().get(1);
		Entry j22e3 = reg.getAllEntries().get(2);
		Entry j24e4 = reg.getAllEntries().get(3);

		assertEquals(1, j22e1.getNumRatings()); // j22 sail 1
		assertEquals(1, j24e4.getNumRatings()); // j24 sail 1

		assertTrue(j22.contains(j22e1));
		assertTrue(j24.contains(j24e2));
		assertTrue(j22.contains(j22e3));
		assertTrue(j24.contains(j24e4));

		assertTrue(j22.contains(j22e1.getRating()));
		assertTrue(j24.contains(j24e2.getRating()));
		assertTrue(j22.contains(j22e3.getRating()));
		assertTrue(j24.contains(j24e4.getRating()));
	}

	public void testImportEntriesSailNotFirst() {
		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions(); // wipe out the pre-existing divisions

		importArray( entrytable2, entryfields2, new DialogImportEntries(null));

		assertEquals("num divs is 2", 2, reg.getNumDivisions());
		assertEquals("num entries is 4", 4, reg.getNumEntries());

		Division j24 = reg.getDivision("J24");
		Division j22 = reg.getDivision("J22");
		assertEquals(2, j24.getNumEntries());
		assertEquals(2, j22.getNumEntries());

		Entry j22e1 = reg.getAllEntries().get(0);
		Entry j24e2 = reg.getAllEntries().get(1);
		Entry j22e3 = reg.getAllEntries().get(2);
		Entry j24e4 = reg.getAllEntries().get(3);

		assertEquals(1, j22e1.getNumRatings()); // j22 sail 1
		assertEquals(1, j24e4.getNumRatings()); // j24 sail 1

		assertTrue(j22.contains(j22e1));
		assertTrue(j24.contains(j24e2));
		assertTrue(j22.contains(j22e3));
		assertTrue(j24.contains(j24e4));

		assertTrue(j22.contains(j22e1.getRating()));
		assertTrue(j24.contains(j24e2.getRating()));
		assertTrue(j22.contains(j22e3.getRating()));
		assertTrue(j24.contains(j24e4.getRating()));
	}

	public void testImportResults() {
		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions(); // wipe out the pre-existing divisions

		importArray( entrytable1, entryfields1, new DialogImportEntries(null));

		// import race 1 results, order there is finpos, sail, fintime
		String[] resultfields1 = new String[] { res.getString("GenSail"), "Race 1 " + res.getString("GenFinPos"),
				"Race 1 " + res.getString("GenFinishTime") };
		String[][] resulttable1 = new String[][] { { "4", "1", "12:00:10" }, { "3", "2", "120030" },
				{ "2", "3", "130100" }, };

		DialogImportResults dialog = new DialogImportResults(null);
		importArray(  resulttable1, resultfields1, dialog);

		assertEquals("num races is 1", 1, reg.getNumRaces());

		Race race = reg.getRace("1");
		Division j22 = reg.getDivision("J22");
		Division j24 = reg.getDivision("J24");
		assertNotNull(race);
		assertEquals("race 1, 2 j24 finishers", 2, race.getNumberFinishers(j24));
		assertEquals("race 1, 1 j22 finishers", 1, race.getNumberFinishers(j22));

		// import race 2 results, order there is finpos, sail, fintime
		String[] resultfields2 = new String[] { "Race 2 " + res.getString("GenFinPos"), res.getString("GenSail"),
				"Race 2 " + res.getString("GenFinishTime") };
		String[][] resulttable2 = new String[][] { { "1", "2", "13:03:10" }, { "2", "3", "130330" },
				{ "3", "1", "140100" }, };
		importArray(  resulttable2, resultfields2, dialog);

		assertEquals("num races is 2", 2, reg.getNumRaces());

		race = reg.getRace("2");
		assertNotNull(race);

		assertEquals("race 2, 1 j24 finishers", 1, race.getNumberFinishers(j24));
		assertEquals("race 2, 2 j22 finishers", 2, race.getNumberFinishers(j22));

		// import race 3 results, time, penalty, sail, (no pos)
		String[] resultfields3 = new String[] { "Race 3 " + res.getString("GenFinishTime"),
				"Race 3 " + res.getString("GenPenalty"), res.getString("GenSail") };
		String[][] resulttable3 = new String[][] { { "140310", "DSQ", "2" }, { "140330", "", "3" },
				{ "140500", "", "4" }, { "140500", "TLE", "1" }, };

		importArray(  resulttable3, resultfields3, dialog);

		assertEquals("num races is 3", 3, reg.getNumRaces());

		race = reg.getRace("3");
		assertNotNull(race);

		assertEquals("race 3, 2 j24 finishers", 2, race.getNumberFinishers(j24));
		assertEquals("race 3, 2 j22 finishers", 1, race.getNumberFinishers(j22)); // 1 clean finish, 1 TLE
	}

}
/**
 * $Log: DialogImportTableTests.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.7.2.1 2005/11/01 02:36:58 sandyg java5 using generics
 * 
 * Revision 1.7 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.6 2003/04/27 21:01:09 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.5 2003/03/16 20:39:14 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.4 2003/01/05 21:16:34 sandyg regression unit testing following rating overhaul from entry to boat
 * 
 * Revision 1.3 2003/01/04 17:13:04 sandyg Prefix/suffix overhaul
 * 
 */
