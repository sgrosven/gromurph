// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelEntryTests.java,v 1.5 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.util.Person;

/**
 * Tests on the Division Panel
 */
public class PanelEntryTests extends JavascoreTestCase {

	public PanelEntryTests(String name) {
		super(name);
	}

	Entry e24;
	Entry ePhrf;
	Boat b;
	Division divJ24;
	Division divPhrf;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		divJ24 = new Division("J24");
		divPhrf = new Division("Phrf", new RatingPhrf(0), new RatingPhrf(10));

		fRegatta.getDivisionModel().clear();
		fRegatta.setUseBowNumbers(true);
		fRegatta.addDivision(divJ24);
		fRegatta.addDivision(divPhrf);

		e24 = new Entry();
		b = new Boat("boatname1", "1234", new Person("ofirst", "olast"));

		e24.setBow("22");
		e24.setBoat(b);
		e24.setSkipper(new Person("sfirst", "slast"));
		e24.setMnaNumber("mna");
		e24.setRsaNumber("rsa");
		e24.setClub("EYC");
		e24.setCrew(new Person("cfirst", "clast"));

		ePhrf = new Entry();
		b = new Boat("boatname2", "3333", new Person("pfirst", "plast"));

		ePhrf.setBow("33");
		ePhrf.setBoat(b);
		ePhrf.setSkipper(new Person("pfirstskip", "plastskip"));
		ePhrf.setMnaNumber("100");
		ePhrf.setRsaNumber("200");
		ePhrf.setClub("AYC");
		ePhrf.setCrew(0, new Person("p1firstcrew", "p1lastcrew", "p1isaf"));
		ePhrf.setCrew(1, new Person("p2firstcrew", "p2lastcrew", "p2isaf"));

		try {
			e24.setDivision(divJ24);
			e24.setRating(new RatingOneDesign("J24"));
			fRegatta.addEntry(e24);

			ePhrf.setRating(new RatingPhrf(5));
			ePhrf.setDivision(divPhrf);
			fRegatta.addEntry(ePhrf);
		}
		catch (RatingOutOfBoundsException e) {
			fail(e.toString());
		}
	}

	public void testCrewTable() {
		Entry entry = ePhrf;

		JDialog dialog = showPanel(entry);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		String table = "fTableCrew";
		assertNotNull(table);

		JButton okButton = (JButton) findComponent(JButton.class, "fButtonExit", dialog);
		assertNotNull(okButton);

		int LAST_COL = 1;
		int FIRST_COL = 2;
		int ISAF_COL = 3;

		String entryNameOld = entry.getSkipper().getFirst();
		String newText = "zfirstskip";
		sendStringAndEnter(table, 0, FIRST_COL, newText);
		String entryNameNew = entry.getSkipper().getFirst();
		assertNotSame("skipper first didnt take", entryNameOld, entryNameNew);

		entryNameOld = entry.getSkipper().getLast();
		newText = "zlastskip";
		sendStringAndEnter(table, 0, LAST_COL, newText);
		entryNameNew = entry.getSkipper().getLast();
		assertNotSame("skipper last didnt take", entryNameOld, entryNameNew);

		entryNameOld = entry.getSkipper().getSailorId();
		newText = "zisafskip";
		sendStringAndEnter(table, 0, ISAF_COL, newText);
		entryNameNew = entry.getSkipper().getSailorId();
		assertNotSame("skipper isaf didnt take", entryNameOld, entryNameNew);

		entryNameOld = entry.getCrew().getLast();
		newText = "zlastcre1";
		sendStringAndEnter(table, 1, LAST_COL, newText);
		entryNameNew = entry.getCrew().getLast();
		assertNotSame("crew last didnt take", entryNameOld, entryNameNew);

		//		newText = "zlastcrew1";
		//		sendStringAndChangeFocus( table, 1, LAST_COL, newText, okButton);
		//		String ec = entry.getCrew().getLast();
		//		assertEquals( "crew1 last didnt take", newText, entry.getCrew().getLast());

		entryNameOld = entry.getCrew().getSailorId();
		newText = "zisafcrew1";
		sendStringAndEnter(table, 1, ISAF_COL, newText);
		entryNameNew = entry.getCrew().getSailorId();
		assertNotSame("crew isaf didnt take", entryNameOld, entryNameNew);

		// click on Ok, should close window
		clickOKButton(dialog);

		assertNotSame("crew isaf didnt take", entryNameOld, entryNameNew);

	}

	public void testTextFields() {
		JDialog dialog = showPanel(e24);

		JButton okButton = (JButton) findComponent(JButton.class, "fButtonExit", dialog);
		assertNotNull("Cant find okButton", okButton);

		JTextField field = (JTextField) findComponent(JTextField.class, "fTextSail", dialog);
		assertNotNull("Cant find fTextSail", field);

		String text2 = "555";
		sendStringAndEnter(field, text2);
		assertEquals("fTextSail wrong after enter", text2, e24.getBoat().getSailId().toString());
		String text1 = "444";
		sendStringAndEnter(field, text1);
		assertEquals("fTextSail wrong after change focus", text1, e24.getBoat().getSailId().toString());

		field = (JTextField) findComponent(JTextField.class, "fTextBow", dialog);
		assertNotNull("Cant find fTextBow", field);
		text2 = "10";
		sendStringAndEnter(field, text2);
		assertEquals("fTextBow wrong after enter", text2, e24.getBow().toString());
		text1 = "12";
		sendStringAndEnter(field, text1);
		assertEquals("fTextBow wrong after change focus", text1, e24.getBow().toString());

		field = (JTextField) findComponent(JTextField.class, "fTextName", dialog);
		assertNotNull("Cant find fTextName", field);
		text2 = "fTextName1";
		sendStringAndEnter(field, text2);
		assertEquals("fTextName wrong after enter", text2, e24.getBoat().getName());
		text1 = "fTextName2";
		sendStringAndEnter(field, text1);
		assertEquals("fTextName wrong after change focus", text1, e24.getBoat().getName());

		field = (JTextField) findComponent(JTextField.class, "fTextRsa", dialog);
		assertNotNull("Cant find fTextRsa", field);
		text2 = "fTextRsa1";
		sendStringAndEnter(field, text2);
		assertEquals("fTextRsa wrong after enter", text2, e24.getRsaNumber());
		text1 = "fTextRsa2";
		sendStringAndEnter(field, text1);
		assertEquals("fTextRsa wrong after change focus", text1, e24.getRsaNumber());

		field = (JTextField) findComponent(JTextField.class, "fTextMna", dialog);
		assertNotNull("Cant find fTextMna", field);
		text2 = "fTextMna1";
		sendStringAndEnter(field, text2);
		assertEquals("fTextMna wrong after enter", text2, e24.getMnaNumber());
		text1 = "fTextMna2";
		sendStringAndEnter(field, text1);
		assertEquals("fTextMna wrong after change focus", text1, e24.getMnaNumber());

		field = (JTextField) findComponent(JTextField.class, "fTextClub", dialog);
		assertNotNull("Cant find fTextClub", field);
		text2 = "fTextClub1";
		sendStringAndEnter(field, text2);
		assertEquals("fTextClub wrong after enter", text2, e24.getClub());
		text1 = "fTextClub2";
		sendStringAndEnter(field, text1);
		assertEquals("fTextClub wrong after change focus", text1, e24.getClub());

		// click on Ok, should close window
		clickOKButton(dialog);
	}

	// --- still to test...
	//		JComboBox           fComboDivision;
	//		JLabel              fLabelBow;
	//		JLabel              fLabelRating;
	//		PanelEntrySubDivisions fPanelSubDivisions;
	//		JPanel              fPanelRatingHolder;
	//		BaseEditor          fPanelRating;

}
/**
 * $Log: PanelEntryTests.java,v $ Revision 1.5 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.6.2.2 2005/11/30 02:52:30 sandyg focuslost commit added to JTextFieldSelectAll
 * 
 * Revision 1.6.2.1 2005/11/26 17:44:21 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.6 2005/04/23 21:55:31 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.5 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.4 2003/05/02 02:41:23 sandyg fixed division update problem in panelentry
 * 
 * Revision 1.3 2003/04/27 21:01:11 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.2 2003/04/20 11:28:51 sandyg starting decent entry panel tests
 * 
 * Revision 1.1 2003/04/09 01:58:13 sandyg initial implementation
 * 
 */
