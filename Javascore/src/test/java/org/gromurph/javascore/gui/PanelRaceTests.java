//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRaceTests.java,v 1.6 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

package org.gromurph.javascore.gui;

import javax.swing.JDialog;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.DialogBaseEditor;

/**
 * Covering unit tests for the PanelRace class
 */
public class PanelRaceTests extends JavascoreTestCase {
	public PanelRaceTests(String name) {
		super(name);
	}

	private static final double ERR = 0.000001;

	public void testMainPanel() {
		Race race = new Race();
		DialogBaseEditor racePanel = showPanel(race);

		String newtext = "A";
		sendStringAndEnter("fTextName", newtext);
		assertEquals("Race name didnt react to lost focus", newtext, race.getName());

		newtext = "B";
		sendStringAndEnter("fTextName", newtext);
		assertEquals("Race name didnt react to enter", newtext, race.getName());

		// click on Advanced button, should then have 2 windows showing
		Object ado = getPopupForButtonClick("fButtonAdvanced");
		assertTrue( ado instanceof DialogBaseEditor);
		
		// get handle to Advanced dialog
		DialogBaseEditor advancedDialog = (DialogBaseEditor) ado;
		assertNotNull("advancedpanel is null", advancedDialog);

		// get handle to the OK button
		clickOKButton(advancedDialog);

		clickOKButton(racePanel);
	}

	public void testCommentOk() {
		Race race = new Race();
		String comment1 = "";
		String comment2 = "second comment";
		race.setComment(comment1);

		JDialog raceDialog = showPanel(race);
		setTextInModalPopup("fButtonEditComment", "fTextComments", "OK", comment2);
		
		// race comment should now be comment2
		assertEquals( race.getComment(), comment2);
		
		// close race dialog
		clickOKButton(raceDialog);
	}

	public void testDivFields() {

		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		
		Division tot = new Division("PHRF A", new RatingPhrf(0), new RatingPhrf(100));

		reg.addDivision(tot);

		Entry ent = new Entry();
		try {
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(tot);
			ent.setRating(new RatingPhrfTimeOnTime(30));
			reg.addEntry(ent);
		} catch (RatingOutOfBoundsException e) {
			fail(e.toString());
		}

		Race race = new Race();

		String INIT_STARTTIME = "09:09:09.0";
		Number INIT_LENGTH = new Double(2.3);
		long startTime =  SailTime.forceToLong(INIT_STARTTIME);

		race.setStartTime(tot, startTime);
		race.setLength(tot, INIT_LENGTH.doubleValue());

		reg.addRace(race);

		assertTrue("race and regatta not right", race.getRegatta() == reg);

		JDialog raceDialog = showPanel(race);

		String TEXT_STARTTIME = "fTextStartTime1";
		String CHECK_NEXTDAY = "fCheckNextDay1";
		String TEXT_LENGTH = "fTextLength1";
		
		String newtext = "12:00:00";
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		String startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Start time wrong", newtext + ".0", startString);

		newtext = "12:05:00";
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Starttime wrong", newtext + ".0", startString);

		// testing the next day checkbox
		long oldDateTime = race.getStartTimeAdjusted(tot);
		clickOnCheckBox(CHECK_NEXTDAY);
		
		assertTrue( race.isNextDay(tot));
		long newDateTime = race.getStartTimeAdjusted(tot);
		assertEquals( (newDateTime - oldDateTime), SailTime.DAYINMILLIS);

		newtext = "12:05:00";
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Starttime wrong", newtext + ".0", startString);
		startString = SailTime.toString(race.getStartTimeAdjusted(tot));
		assertEquals("Starttime wrong", "1/" + newtext + ".0", startString);

		String badtext = "12:0500"; // now works, but yields 12 hrs and 500 minutes... 
		sendStringAndEnter(TEXT_STARTTIME, badtext);
		startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Starttime wrong", "12:05:00.0", startString);
		
		// get it back to a valid value
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		
		newtext = "141528"; // now works
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Starttime wrong", "14:15:28.0", startString);

		newtext = "1605"; // now works
		sendStringAndEnter(TEXT_STARTTIME, newtext);
		startString = SailTime.toString(race.getStartTimeRaw(tot));
		assertEquals("Starttime wrong", "16:05:00.0", startString);


		double returnlen = race.getLength(tot);
		assertEquals("length wrong0", INIT_LENGTH.doubleValue(), returnlen, ERR);

		double testlen = 9.0;
		String strlen = "9.0";
		sendStringAndEnter(TEXT_LENGTH, strlen);
		returnlen = race.getLength(tot);
		assertEquals("length wrong1", testlen, returnlen, ERR);

		testlen = 9.3;
		strlen = "9.3";
		sendStringAndEnter(TEXT_LENGTH, strlen);
		returnlen = race.getLength(tot);
		assertEquals("length wrong2", testlen, returnlen, ERR);

		strlen = "9asldkfj";
		sendStringAndEnter(TEXT_LENGTH, strlen);

		returnlen = race.getLength(tot);
		// invalid length, so it does not change, still 9.3
		assertEquals("length wrong3", testlen, returnlen, ERR);

		clickOKButton(raceDialog);

	}
}

//
// $Log: PanelRaceTests.java,v $
// Revision 1.6 2006/01/15 21:08:39 sandyg
// resubmit at 5.1.02
//
// Revision 1.4 2006/01/15 03:25:51 sandyg
// to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
//
// Revision 1.3 2006/01/11 02:25:08 sandyg
// updating copyright years
//
// Revision 1.2 2006/01/11 02:17:16 sandyg
// Bug fixes relative to qualify/final race scoring
//
// Revision 1.1 2006/01/01 02:27:02 sandyg
// preliminary submission to centralize code in a new module
//
// Revision 1.8.2.2 2005/11/30 02:52:30 sandyg
// focuslost commit added to JTextFieldSelectAll
//
// Revision 1.8.2.1 2005/11/26 17:44:21 sandyg
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

