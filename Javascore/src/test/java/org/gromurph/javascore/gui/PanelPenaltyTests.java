// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelPenaltyTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Penalty;

/**
 * Tests on the Penalty Panel
 */
public class PanelPenaltyTests extends JavascoreTestCase implements Constants {

	public PanelPenaltyTests(String name) {
		super(name);
	}

	public void testMainFields() {
		Penalty p1 = new Penalty();

		JDialog dialog = showPanel(p1);

		JCheckBox checkZ3 = (JCheckBox) findComponent(JCheckBox.class, "fCheckZFP3", dialog);
		assertNotNull(checkZ3);

		assertTrue(!p1.hasPenalty(ZFP));
		assertTrue(!p1.hasPenalty(ZFP2));
		assertTrue(!p1.hasPenalty(ZFP3));

		clickOnCheckBox("fCheckZFP");
		assertTrue(p1.hasPenalty(ZFP));
		assertTrue(!p1.hasPenalty(ZFP2));

		clickOnCheckBox("fCheckZFP2");
		assertTrue(p1.hasPenalty(ZFP));
		assertTrue(p1.hasPenalty(ZFP2));

		// test redress label
		JRadioButton fRadioRedressAVG = (JRadioButton) findComponent(JRadioButton.class, "fRadioRedressAVG", dialog);
		assertNotNull(fRadioRedressAVG);

		JRadioButton fRadioLabelOther = (JRadioButton) findComponent(JRadioButton.class, "fRadioLabelOther", dialog);
		assertNotNull(fRadioRedressAVG);

		JRadioButton fRadioLabelRDG = (JRadioButton) findComponent(JRadioButton.class, "fRadioLabelRDG", dialog);
		assertNotNull(fRadioRedressAVG);

		JTextField fTextLabelOther = (JTextField) findComponent(JTextField.class, "fTextLabelOther", dialog);
		assertNotNull("Cant find fTextLabelOther", fTextLabelOther);

		clickOn(fRadioRedressAVG);
		assertTrue("fRadioRedressAVG should be selected", fRadioRedressAVG.isSelected());
		assertTrue("AVG points penalty should be assigned", p1.hasPenalty(AVG));

		clickOn(fRadioLabelOther);
		assertTrue("fRadioLabelOther should be selected", fRadioLabelOther.isSelected());
		assertTrue("fTextLabelOther should be enabled", fTextLabelOther.isEnabled());

		String newText = "DPI";
		sendStringAndEnter(fTextLabelOther, newText);
		assertEquals("fTextLabelOther didnt take", newText, p1.getRedressLabel());

		// String pLabel = p1.toString( false);

		clickOn(fRadioLabelRDG);
		assertEquals("redress label should now be empty", "RDG", p1.getRedressLabel());

		clickOKButton(dialog);
	}

	public void testRedress() {
		Penalty p1 = new Penalty();

		JDialog dialog = showPanel(p1);

		JRadioButton radioRedressPoints = (JRadioButton) findComponent(JRadioButton.class, "fRadioRedressPoints",
				dialog);
		assertNotNull(radioRedressPoints);

		JTextField textRedressPoints = (JTextField) findComponent(JTextField.class, "fTextRedressPoints", dialog);
		assertNotNull(textRedressPoints);

		clickOn(radioRedressPoints);
		assertTrue(p1.hasPenalty(RDG));

		// works interactively, june 9, 2013
		String newText = "5.3"; 
		sendStringAndEnter(textRedressPoints, newText);
		assertEquals("textRedressPoints didnt take", Double.parseDouble(newText), p1.getPoints(), 0.001);

		clickOKButton(dialog);
	}

}
/**
 * $Log: PanelPenaltyTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.3.2.1 2005/11/26 17:44:21 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.3 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.2 2003/04/27 21:01:12 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.1 2003/04/23 00:33:41 sandyg initial implementation
 * 
 * 
 */
