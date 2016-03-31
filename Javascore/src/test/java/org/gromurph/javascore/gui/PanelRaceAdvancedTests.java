// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRaceAdvancedTests.java,v 1.5 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================

package org.gromurph.javascore.gui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.DialogBaseEditor;

/**
 * Covering unit tests for the PanelRace class
 */
public class PanelRaceAdvancedTests extends JavascoreTestCase {
	public PanelRaceAdvancedTests(String name) {
		super(name);
	}

	private static final double ERR = 0.000001;

	public void testFields() {
		Race race = new Race();
		
//		DialogBaseEditor advRacePanel = getTestDialog();
//		showPanel(race, new PanelRaceAdvanced(advRacePanel, race));
		DialogBaseEditor advRacePanel = showPanel(race, new PanelRaceAdvanced(new DialogBaseEditor()));
		assertNotNull(advRacePanel);

		JCheckBox check = (JCheckBox) findComponent(JCheckBox.class, "fCheckLongDistance", advRacePanel);
		assertNotNull("Cant find fCheckLongDistance", check);

		assertTrue(!check.isSelected());

		clickOn(check);
		assertEquals(true, race.isLongDistance());

		clickOn(check);
		assertEquals(false, race.isLongDistance());

		// test non-discardable

		check = (JCheckBox) (JCheckBox) findComponent(JCheckBox.class, "fCheckNonDiscardable", advRacePanel);
		assertNotNull("Cant find fCheckNonDiscardable", check);

		assertTrue(!check.isSelected());

		clickOn(check);
		assertEquals(true, race.isNonDiscardable());

		clickOn(check);
		assertEquals(false, race.isNonDiscardable());

		// test race weight

		JTextField field = (JTextField) findComponent(JTextField.class, "fTextWeight", advRacePanel);
		assertNotNull("Cant find fTextName", field);
		assertEquals("1.00", field.getText());

		String newtext = "1.50";
		sendStringAndEnter(field, newtext);
		assertEquals("Race weight didnt react to enter", 1.5, race.getWeight(), ERR);

		clickOKButton();
	}

	public void testMedalRaceOptions() {
		Race race = new Race();
		DialogBaseEditor advRacePanel = showPanel(race, new PanelRaceAdvanced(new DialogBaseEditor()));
		assertNotNull(advRacePanel);

		JCheckBox checkMedal = (JCheckBox) findComponent(JCheckBox.class, "fCheckMedalRace", advRacePanel);
		assertNotNull("Cant find fCheckMedalRace", checkMedal);
		assertTrue(!checkMedal.isSelected());

		JCheckBox checkNondiscard = (JCheckBox) (JCheckBox) findComponent(JCheckBox.class, "fCheckNonDiscardable",
				advRacePanel);
		assertNotNull("Cant find fCheckNonDiscardable", checkNondiscard);
		assertTrue(!checkNondiscard.isSelected());

		JTextField fieldWeight = (JTextField) findComponent(JTextField.class, "fTextWeight", advRacePanel);
		assertNotNull("Cant find fTextName", fieldWeight);
		assertEquals("1.00", fieldWeight.getText());

		JComponent alternateFocusField = checkMedal;
		assertNotNull("alternateFocusField is null", alternateFocusField);

		// got everything, now turn Medal on and make sure nondiscard and weight change
		clickOn(checkMedal);
		assertEquals(true, race.isNonDiscardable());
		assertEquals(true, race.isMedalRace());
		assertEquals(2.00, race.getWeight(), 0.00001);

		clickOKButton();

		assertEquals(true, race.isNonDiscardable());
		assertEquals(true, race.isMedalRace());
		assertEquals(2.00, race.getWeight(), 0.00001);

	}

	public void testPursuitRaceOptions() {
		Race race = new Race();
		DialogBaseEditor advRacePanel = showPanel(race, new PanelRaceAdvanced(new DialogBaseEditor()));
		assertNotNull(advRacePanel);

		JCheckBox checkPursuit = (JCheckBox) findComponent(JCheckBox.class, "fCheckPursuitRace", advRacePanel);
		assertNotNull("Cant find fCheckPursuitRace", checkPursuit);
		assertTrue(!checkPursuit.isSelected());

		JComponent alternateFocusField = checkPursuit;
		assertNotNull("alternateFocusField is null", alternateFocusField);

		clickOn(checkPursuit);
		assertEquals(true, race.isPursuit());

		clickOKButton();
		assertEquals(true, race.isPursuit());
	}

	public void testBFactor() {
		// initialize regatta and race
		
		// clear out any b/a factors in init
		JavaScoreProperties.setPropertyValue( JavaScoreProperties.AFACTOR_PROPERTY,  "");
		JavaScoreProperties.setPropertyValue( JavaScoreProperties.BFACTOR_PROPERTY,  "");

		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		Division tot = new Division("Atot", new RatingPhrfTimeOnTime(0), new RatingPhrfTimeOnTime(100));

		reg.addDivision(tot);

		Entry ent = new Entry();
		try {
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(tot);
			ent.setRating(new RatingPhrfTimeOnTime(30));
			reg.addEntry(ent);
		}
		catch (RatingOutOfBoundsException e) {
			fail(e.toString());
		}

		Race race = new Race();
		reg.addRace(race);
		assertTrue("race and regatta not right", race.getRegatta() == reg);

		// bring up the panel
		DialogBaseEditor advRacePanel = showPanel(race, new PanelRaceAdvanced(new DialogBaseEditor()));
		assertNotNull(advRacePanel);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		// test the Conditions slot

		JRadioButton buttonAverage = (JRadioButton) findComponent(JRadioButton.class, "fRadioAverage", advRacePanel);
		assertNotNull("buttonAverage is null", buttonAverage);

		JRadioButton buttonHeavy = (JRadioButton) findComponent(JRadioButton.class, "fRadioHeavy", advRacePanel);
		assertNotNull("buttonHeavy is null", buttonHeavy);

		JRadioButton buttonLight = (JRadioButton) findComponent(JRadioButton.class, "fRadioLight", advRacePanel);
		assertNotNull("buttonLight is null", buttonLight);

		// average radiobox should be selected
		assertTrue("average should be selected", buttonAverage.isSelected());
		assertEquals("race conditions should be average", race.getBFactor(), RatingPhrfTimeOnTime.BFACTOR_AVERAGE);

		// set light winds check box
		try {
			clickOn(buttonLight);

			// race's factor should now be light
			assertTrue("light should be selected", buttonLight.isSelected());
			assertEquals("race conditions should be light", RatingPhrfTimeOnTime.BFACTOR_LIGHT, race.getBFactor());

			// set heavy winds
			clickOn(buttonHeavy);

			// test heavy wind
			assertTrue("heavy should be selected", buttonHeavy.isSelected());
			assertEquals("race conditions should be heavy", RatingPhrfTimeOnTime.BFACTOR_HEAVY, race.getBFactor());

			// set average winds
			clickOn(buttonAverage);

			// back to average
			assertTrue("average should be selected", buttonAverage.isSelected());
			assertEquals("race conditions should be average", RatingPhrfTimeOnTime.BFACTOR_AVERAGE, race.getBFactor());
		}
		catch (Exception e) {
			fail("unexpected exception: " + e.toString());
		}

		clickOKButton();
	}

}

//
// $Log: PanelRaceAdvancedTests.java,v $
// Revision 1.5 2006/01/15 21:08:39 sandyg
// resubmit at 5.1.02
//
// Revision 1.3 2006/01/15 03:25:51 sandyg
// to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
//
// Revision 1.2 2006/01/11 02:25:08 sandyg
// updating copyright years
//
// Revision 1.1 2006/01/01 02:27:02 sandyg
// preliminary submission to centralize code in a new module
//
// Revision 1.1.2.1 2005/11/26 17:44:21 sandyg
// implement race weight & nondiscardable, did some gui test cleanups.
//
// Revision 1.8 2005/04/23 21:55:31 sandyg
// JWS mods for release 4.3.1
//
// Revision 1.7 2004/04/10 22:19:41 sandyg
// Copyright update
//
// Revision 1.6 2003/05/02 02:42:20 sandyg
// leaving cancel test on comment field commented out
//
// Revision 1.5 2003/04/30 00:59:07 sandyg
// fixed error handling on bad race times, improved unit testing
//
// Revision 1.4 2003/04/27 21:01:12 sandyg
// lots of cleanup, unit testing for 4.1.1 almost complete
//
// Revision 1.3 2003/03/30 00:04:08 sandyg
// gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
//
// Revision 1.2 2003/03/28 03:07:51 sandyg
// changed toxml and fromxml to xmlRead and xmlWrite
//
// Revision 1.1 2003/03/16 21:47:24 sandyg
// 3.9.2 release: fix bug 658904, time on time condition buttons corrected
//
//

