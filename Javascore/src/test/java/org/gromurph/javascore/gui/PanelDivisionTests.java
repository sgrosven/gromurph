// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelDivisionTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.ratings.RatingIrc;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.Util;

/**
 * Tests on the Division Panel
 */
public class PanelDivisionTests extends JavascoreTestCase {

	public PanelDivisionTests(String name) {
		super(name);
	}

	public void testRatingPhrfDisplays() {
		Division div = new Division("phrf", new RatingPhrf(33), new RatingPhrf(66));
		
		DialogBaseEditor dialog = showPanel(div);

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);

		assertEquals("min rating wrong", "33", fieldMin.getText());

		JPanel x1 = (JPanel) findComponent(JPanel.class, "fPanelMaxRating", dialog);
		assertNotNull("Cant find maxPanel", x1);
		JPanel x2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, x1);
		assertNotNull("Cant find maxPanel.double", x2);
		JTextField fieldMax = (JTextField) findComponent(JTextField.class, "fTextRating", x2);
		assertNotNull("Cant find fTextRating", fieldMax);

		assertEquals("min rating wrong", "66", fieldMax.getText());

		// now change the division, same system, see if fields are up dated right
		Division div2 = new Division("next", new RatingPhrf(1), new RatingPhrf(100));

		dialog.setObject(div2);

		JTextField fieldName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);

		assertEquals("name wrong", div2.getName(), fieldName.getText());
		
		assertEquals("min rating bad", "1", fieldMin.getText());
		assertEquals("min rating bad", "100", fieldMax.getText());

		// click on Ok, should close window
		JButton buttonOk = (JButton) findComponent(JButton.class, "fButtonExit", dialog);
		assertNotNull("buttonOk is null", buttonOk);

		clickOKButton(dialog);
	}

	public void testRatingPhrfTimeOnTimeDisplays() {
		Division div = new Division("PhrfTimeOnTime", new RatingPhrfTimeOnTime(33), new RatingPhrfTimeOnTime(66));
		DialogBaseEditor dialog = showPanel(div);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		JTextField fieldName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);

		assertEquals("min rating wrong", "33", fieldMin.getText());

		JPanel x1 = (JPanel) findComponent(JPanel.class, "fPanelMaxRating", dialog);
		assertNotNull("Cant find maxPanel", x1);
		JPanel x2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, x1);
		assertNotNull("Cant find maxPanel.double", x2);
		JTextField fieldMax = (JTextField) findComponent(JTextField.class, "fTextRating", x2);
		assertNotNull("Cant find fTextRating", fieldMax);

		assertEquals("min rating wrong", "66", fieldMax.getText());

		// now change the division, same system, see if fields are up dated right
		Division div2 = new Division("next", new RatingPhrfTimeOnTime(1), new RatingPhrfTimeOnTime(100));

		dialog.setObject(div2);

		assertEquals("name wrong", div2.getName(), fieldName.getText());
		assertEquals("min rating bad", "1", fieldMin.getText());
		assertEquals("min rating bad", "100", fieldMax.getText());

		// click on Ok, should close window
		JButton buttonOk = (JButton) findComponent(JButton.class, "fButtonExit", dialog);
		assertNotNull("buttonOk is null", buttonOk);

		clickOKButton(dialog);
	}

	public void testRatingIrcDisplays() {

		double RATING_L1 = 0.01;
		double RATING_L2 = 20.2;
		double RATING_R1 = 999.0;
		double RATING_R2 = 2.1;

		int ircDecs = new RatingIrc(1).getDecs();

		Division div = new Division("irc", new RatingIrc(RATING_L1), new RatingIrc(RATING_R1));
		DialogBaseEditor dialog = showPanel(div);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		JTextField fieldName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);

		String sL1 = Util.formatDouble(RATING_L1, ircDecs);
		assertEquals("min rating wrong", sL1, fieldMin.getText());

		JPanel x1 = (JPanel) findComponent(JPanel.class, "fPanelMaxRating", dialog);
		assertNotNull("Cant find maxPanel", x1);
		JPanel x2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, x1);
		assertNotNull("Cant find maxPanel.double", x2);
		JTextField fieldMax = (JTextField) findComponent(JTextField.class, "fTextRating", x2);
		assertNotNull("Cant find fTextRating", fieldMax);

		String sR1 = Util.formatDouble(RATING_R1, ircDecs);
		assertEquals("min rating wrong", sR1, fieldMax.getText());

		// now change the division, same system, see if fields are up dated right
		Division div2 = new Division("next", new RatingIrc(RATING_L2), new RatingIrc(RATING_R2));

		dialog.setObject(div2);

		String sL2 = Util.formatDouble(RATING_L2, ircDecs);
		String sR2 = Util.formatDouble(RATING_R2, ircDecs);
		assertEquals("name wrong", div2.getName(), fieldName.getText());
		assertEquals("min rating bad", sL2, fieldMin.getText());
		assertEquals("max rating bad", sR2, fieldMax.getText());

		// click on Ok, should close window
		JButton buttonOk = (JButton) findComponent(JButton.class, "fButtonExit", dialog);
		assertNotNull("buttonOk is null", buttonOk);

		clickOKButton(dialog);
	}

	public void testNameAndMinRating() {
		Division div = new Division("phrf", new RatingPhrf(33), new RatingPhrf(66));
		DialogBaseEditor dialog = showPanel(div);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);
		assertEquals("min rating should be 33", 33.0, div.getSlowestRating().getPrimaryValue(), 0.0001);
		
		String newrating = "40";
		sendStringAndEnter(fieldMin, newrating);
		assertEquals("min rating should be 40", 40.0, div.getSlowestRating().getPrimaryValue(), 0.0001);

		// change phrf min rating	
		newrating = "44";
		sendStringAndEnter(fieldMin, newrating);
		assertEquals("min rating should be 44", 44.0, div.getSlowestRating().getPrimaryValue(), 0.0001);

		// change division name, with and with focus change
		JTextField fieldName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);
		assertNotNull("Cant find fTextName", fieldMin);

		String newName = "HiThere";
		sendStringAndEnter(fieldName, newName);
		assertEquals("newName didnt take", newName, div.getName());

		newName = "boo";
		sendStringAndEnter(fieldName, newName);
		assertEquals("newName didnt take", newName, div.getName());

		clickOKButton(dialog);
	}

	public void testSetOK() {
		Division div = new Division("phrf", new RatingPhrf(33), new RatingPhrf(66));
		DialogBaseEditor dialog = showPanel(div);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);

		Container south = (Container) findComponent(JPanel.class, "fPanelSouth", dialog);
		assertNotNull("Cant find panel containing cancel button", south);

		// change phrf min rating	
		String newrating = "44";
		sendStringAndEnter(fieldMin, newrating);
		assertEquals("min rating should be 44", 44.0, div.getSlowestRating().getPrimaryValue(), 0.0001);

		clickOKButton(dialog);


		assertEquals("min rating should be 44", 44.0, div.getSlowestRating().getPrimaryValue(), 0.0001);
	}

	/**
	 * ensures that a display division can be changed, but once cancel is hit the "external" reference to the division
	 * reverts to the original settings
	 */
	public void testSetCancel() {
		Division div = new Division("phrf", new RatingPhrf(33), new RatingPhrf(66));
		DialogBaseEditor dialog = showPanel(div);

		//assertEquals("Number of windows is incorrect", 1, getOpenWindowCount());

		JPanel p1 = (JPanel) findComponent(JPanel.class, "fPanelMinRating", dialog);
		assertNotNull("Cant find minPanel", p1);
		JPanel p2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, p1);
		assertNotNull("Cant find minPanel.double", p2);
		JTextField fieldMin = (JTextField) findComponent(JTextField.class, "fTextRating", p2);
		assertNotNull("Cant find fTextRating", fieldMin);

		JPanel x1 = (JPanel) findComponent(JPanel.class, "fPanelMaxRating", dialog);
		assertNotNull("Cant find maxPanel", x1);
		JPanel x2 = (JPanel) findComponent(JPanel.class, PanelRating.CARD_DOUBLE, x1);
		assertNotNull("Cant find maxPanel.double", x2);
		JTextField fieldMax = (JTextField) findComponent(JTextField.class, "fTextRating", x2);
		assertNotNull("Cant find fTextRating", fieldMax);

		Container south = (Container) findComponent(JPanel.class, "fPanelSouth", dialog);
		assertNotNull("Cant find panel containing cancel button", south);

		JButton cancelButton = (JButton) findComponent(JButton.class, "fButtonCancel", south);
		assertNotNull("Cant find cancel button", cancelButton);

		assertTrue("Cancel should not be enabled", !cancelButton.isEnabled());

		// change phrf min rating	
		String newtext = "44";
		sendStringAndEnter(fieldMin, newtext);
		assertEquals("min rating should be 44", 44.0, div.getSlowestRating().getPrimaryValue(), 0.0001);

		assertTrue("Cancel should now be enabled", cancelButton.isEnabled());

		newtext = "99";
		sendStringAndEnter(fieldMax, newtext);
		assertEquals("max rating should be 99", 99.0, div.getFastestRating().getPrimaryValue(), 0.0001);

		String origName = div.getName();
		newtext = "phrf2";
		JTextField fieldName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);
		assertNotNull("cant find name field", fieldName);

		sendStringAndEnter(fieldName, newtext);
		assertEquals("new name should doesnt take", newtext, div.getName());

		clickOnButton("fButtonCancel");

		assertEquals("min rating should be orig 33", 33.0, div.getSlowestRating().getPrimaryValue(), 0.0001);
		assertEquals("max rating should be orig 66", 66.0, div.getFastestRating().getPrimaryValue(), 0.0001);
		assertEquals("name should be orig name", origName, div.getName());
	}

}
/**
 * $Log: PanelDivisionTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.2.2 2005/11/30 02:52:30 sandyg focuslost commit added to JTextFieldSelectAll
 * 
 * Revision 1.11.2.1 2005/11/26 17:44:21 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.11 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.10 2003/04/27 21:01:10 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.9 2003/04/20 15:44:30 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.8 2003/03/30 00:04:08 sandyg gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
 * 
 * Revision 1.7 2003/03/27 02:47:01 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 * 
 * Revision 1.6 2003/03/19 03:32:26 sandyg cancel in PanelDivision now correctly reverts the division to original
 * 
 * Revision 1.5 2003/03/19 02:40:33 sandyg start() stop() mods, also getting events and ratings to fire correctly
 * 
 * Revision 1.4 2003/03/16 20:39:43 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.3 2003/02/22 13:50:21 sandyg Moved from 'source' hierarchy
 * 
 * Revision 1.2 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
