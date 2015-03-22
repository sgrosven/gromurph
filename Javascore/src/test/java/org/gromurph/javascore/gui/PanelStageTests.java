// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelScoringOptionsTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.Util;

/**
 * Tests on the Division Panel
 */
public class PanelStageTests extends JavascoreTestCase {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	public PanelStageTests(String name) {
		super(name);
	}

	String STAGE1 = "Stage1";

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStageOptions() {

		fRegatta.setMultistage(true);
		MultiStageScoring manager = (MultiStageScoring) fRegatta.getScoringManager();

		assertNotNull(manager);

		Stage fStage = new Stage(manager);
		fStage.setName(STAGE1);
		fStage.setStageRank(1);
		fStage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.ALL);
		fStage.setTiebreakCarryOver(Constants.TiebreakCarryOver.ALL);
		manager.addStage(fStage);

		fStage = manager.getStage(STAGE1);
		assertNotNull(fStage);

		JDialog fPanel = showPanel(fStage);

		// ====== stage name
		JTextField field = (JTextField) findComponent(JTextField.class, "fTextName", fPanel);
		assertNotNull("Cant find fTextName", field);

		String defaultText = fStage.getName();

		String text2 = "two";
		sendStringAndEnter(field, text2);
		assertEquals("fTextName default wrong", text2, fStage.getName());

		// TODO needs tests for qualfying, previous stage
		// TODO needs implement and test enabling/disabling

		// Test overall scoring parameter
		JComboBox combo = (JComboBox) findComponent(JComboBox.class, "fComboScoreCarryOver", fPanel);
		assertNotNull("fComboScoreCarryOver should not be null", combo);

		Object[] comboItems = Constants.ScoreCarryOver.values();
		assertEquals(fStage.getScoreCarryOver(), combo.getSelectedItem());

		// click on new scoring box item,
		String newSelectedText = comboItems[0].toString();
		Object newSelectedObj = clickOnComboBox("fComboScoreCarryOver", newSelectedText);

		assertEquals(newSelectedText, newSelectedObj);
		assertEquals(newSelectedText, fStage.getScoreCarryOver().toString());

		// click on new scoring box item,
		newSelectedText = comboItems[2].toString();
		newSelectedObj = clickOnComboBox("fComboScoreCarryOver", newSelectedText);

		assertEquals(newSelectedText, newSelectedObj);
		assertEquals(newSelectedText, fStage.getScoreCarryOver().toString());

		// Test overall tiebreak parameter
		combo = (JComboBox) findComponent(JComboBox.class, "fComboTiebreakCarryOver", fPanel);
		assertNotNull("fComboTiebreakCarryOver should not be null", combo);

		comboItems = Constants.TiebreakCarryOver.values();
		assertEquals(Constants.TiebreakCarryOver.ALL, combo.getSelectedItem());

		// click on new scoring box item,
		newSelectedText = comboItems[0].toString();
		newSelectedObj = clickOnComboBox("fComboTiebreakCarryOver", newSelectedText);

		assertEquals(newSelectedText, fStage.getTiebreakCarryOver().toString());

		// click on new scoring box item,
		newSelectedText = comboItems[1].toString();
		newSelectedObj = clickOnComboBox("fComboTiebreakCarryOver", newSelectedText);

		assertEquals(newSelectedText, newSelectedObj);
		assertEquals(newSelectedText, fStage.getTiebreakCarryOver().toString());

		// Test overall tiebreak parameter
		combo = (JComboBox) findComponent(JComboBox.class, "fComboThrowoutCarryOver", fPanel);
		assertNotNull("fComboThrowoutCarryOver should not be null", combo);

		comboItems = Constants.ThrowoutCarryOver.values();
		assertEquals(fStage.getThrowoutCarryOver(), combo.getSelectedItem());

		// click on new scoring box item,
		newSelectedText = comboItems[0].toString();
		newSelectedObj = clickOnComboBox("fComboThrowoutCarryOver", newSelectedText);

		assertEquals(newSelectedText, fStage.getThrowoutCarryOver().toString());

		// click on new scoring box item,
		newSelectedText = comboItems[1].toString();
		newSelectedObj = clickOnComboBox("fComboThrowoutCarryOver", newSelectedText);

		assertEquals(newSelectedText, newSelectedObj);
		assertEquals(newSelectedText, fStage.getThrowoutCarryOver().toString());

	}
}
/**
 * $Log: PanelScoringOptionsTests.java,v $
 * 
 */
