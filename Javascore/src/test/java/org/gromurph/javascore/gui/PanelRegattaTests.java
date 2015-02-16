// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRegattaTests.java,v 1.7 2006/01/15 21:08:39 sandyg Exp $
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
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Regatta;

/**
 * Covering unit tests for the PanelRegatta class
 */
public class PanelRegattaTests extends JavascoreTestCase {

	public PanelRegattaTests(String name) {
		super(name);
	}

	public void testMainPanel() {
		Regatta regatta = new Regatta();
		JDialog dialog = showPanel(regatta);

		JTextField field = (JTextField) findComponent(JTextField.class, "fTextHostClub", dialog);
		assertNotNull("Cant find fTextHostClub", field);

		String myc = "My Yacht Club";
		sendStringAndEnter(field, myc);
		assertEquals("Host club didnt react to lost focus", myc, regatta.getHostClub());

		myc = "New Yacht Club";
		sendStringAndEnter(field, myc);
		assertEquals("Host club didnt react to enter", myc, regatta.getHostClub());

		// =========== CHECKBOX SPLITFLEET

		JCheckBox check = (JCheckBox) findComponent(JCheckBox.class, "fCheckBoxMultistage", dialog);
		assertNotNull(check);

		assertEquals(false, regatta.isMultistage());

		// first click should reverse
		clickOn(check);
		assertEquals(true, regatta.isMultistage());

		// second click should put it back
		clickOn(check);
		assertEquals(false, regatta.isMultistage());

		JDialog scoringDialog = (JDialog) getPopupForButtonClick("fButtonStageScoring");
		assertNotNull(scoringDialog);
		
		clickOKButton(scoringDialog);
		clickOKButton(dialog);
	}

	public void testCommentOk() {
		Regatta regatta = new Regatta();

		String comment1 = "";
		String comment2 = "second comment";
		regatta.setComment(comment1);

		JDialog dialog = showPanel(regatta);
		setTextInModalPopup("fButtonEditComment", "fTextComments", "OK", comment2);

		// Race.getComments should now be comment2
		assertEquals("Regatta comment not comment2", comment2, regatta.getComment());
	}

}

/*
 * $Log: PanelRegattaTests.java,v $ Revision 1.7 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.5 2006/01/14 21:06:56 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.4 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.3 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.2 2006/01/01 22:40:42 sandyg Renamed ScoringLowPoint to ScoringOptions, add gui unit tests
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.7.2.1 2005/11/26 17:44:21 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.7 2004/05/06 00:25:19 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.6 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.5 2003/07/11 02:19:42 sandyg fixed problem with scoring system combo box not getting updated
 * 
 * Revision 1.4 2003/04/27 21:01:13 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.3 2003/03/30 00:04:09 sandyg gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
 * 
 * Revision 1.2 2003/03/16 20:39:43 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 */

