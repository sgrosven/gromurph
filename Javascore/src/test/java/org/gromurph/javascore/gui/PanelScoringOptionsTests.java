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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import junit.framework.AssertionFailedError;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.scoring.ScoringLowPoint;
import org.gromurph.javascore.model.scoring.ScoringOptions;
import org.gromurph.javascore.model.scoring.ScoringUtilities;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.javascore.model.scoring.StageScoringModel;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.Util;

/**
 * Tests on the Division Panel
 */
public class PanelScoringOptionsTests extends JavascoreTestCase {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	public PanelScoringOptionsTests(String name) {
		super(name);
	}

	StageScoringModel fModel;
	DialogBaseEditor fDialog;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		fModel = (SingleStageScoring) fRegatta.getScoringManager();
		assertNotNull(fModel);
		fDialog = (DialogBaseEditor)showPanel(fModel);
		
		GENERAL_PANEL= res.getString("ScoringOptionsTabGeneral");
		PENALTY_PANEL = res.getString("ScoringOptionsTabPenalties");
		THROWOUT_PANEL = res.getString("ScoringOptionsTabThrowouts");
		TIEBREAKER_PANEL = res.getString("ScoringOptionsTabTiebreakers");
		
		TAB_PANE = "tabPane";
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		// click on Ok, should close window
		try { clickOKButton(); } catch (AssertionFailedError afe) {}
	}

	public String GENERAL_PANEL;
	public String PENALTY_PANEL;
	public String THROWOUT_PANEL;
	public String TIEBREAKER_PANEL;
	public String TAB_PANE;

	public void testPointsPenalties() {
		ScoringOptions fOptions = (ScoringOptions) fModel.getModel().getOptions();
		assertNotNull(fOptions);

		JTabbedPane tabPane = (JTabbedPane) findComponent(JTabbedPane.class, TAB_PANE, fDialog);
		assertNotNull("cant find tabPane", tabPane);

		JPanel localPanel = clickOnTabPane(tabPane, PENALTY_PANEL);
		assertNotNull("cant find localPanel", localPanel);

		// ====== POINTS FOR FIRST 
		JTextField field = (JTextField) findComponent(JTextField.class, "fTextPointsForFirst", localPanel);
		assertNotNull("Cant find fTextPointsForFirst", field);

		String defaultText = "1.0";
		assertEquals("fTextPointsForFirst default wrong", defaultText, Double.toString(fOptions.getFirstPlacePoints()));

		// june 9, this works interactively not sure why test barfs
		String text2 = "0.0";
		sendStringAndEnter(field, text2);
		assertEquals("fTextPointsForFirst doesnt take 0 points", text2, Double.toString(fOptions.getFirstPlacePoints()));

		String text3 = "0.75";
		sendStringAndEnter(field, text3);
		assertEquals("fTextCheckin default wrong", text3, Double.toString(fOptions.getFirstPlacePoints()));

		// ====== CHECK IN 
		field = (JTextField) findComponent(JTextField.class, "fTextCheckin", localPanel);
		assertNotNull("Cant find fTextCheckin", field);

		defaultText = "20";
		assertEquals("fTextCheckin default wrong", defaultText, Integer.toString(fOptions.getCheckinPercent()));

		text2 = "10";
		sendStringAndEnter(field, text2);
		assertEquals("fTextCheckin default wrong", text2, Integer.toString(fOptions.getCheckinPercent()));

		// ========= TIME LIMIT PENALTY		
		JRadioButton buttonDnf = (JRadioButton) findComponent(JRadioButton.class,
				res.getString("PenaltyDneDidNotFinish"), localPanel);
		assertNotNull("buttonDnf is null", buttonDnf);

		JRadioButton buttonAvg = (JRadioButton) findComponent(JRadioButton.class, res.getString("PenaltyDneAverage"),
				localPanel);
		assertNotNull("buttonAvg is null", buttonAvg);

		JRadioButton buttonFin1 = (JRadioButton) findComponent(JRadioButton.class, res.getString("PenaltyDnePlus1"),
				localPanel);
		assertNotNull("buttonFin1 is null", buttonFin1);

		JRadioButton buttonFin2 = (JRadioButton) findComponent(JRadioButton.class, res.getString("PenaltyDnePlus2"),
				localPanel);
		assertNotNull("buttonFin2 is null", buttonFin2);

		// default DNF radiobox should be selected
		assertTrue("Time limit DNF should be selected", buttonDnf.isSelected());
		assertEquals("Regatta time limit should be DNF", fOptions.getTimeLimitPenalty(), ScoringLowPoint.TLE_DNF);

		// set different levels
		try {
			// test Fin2
			clickOn(buttonFin2);
			assertTrue("buttonFin2 should be selected", buttonFin2.isSelected());
			assertEquals("regatta TLE should be Fin2", ScoringLowPoint.TLE_FINISHERSPLUS2,
					fOptions.getTimeLimitPenalty());

			// test Fin1
			clickOn(buttonFin1);
			assertTrue("buttonFin1 should be selected", buttonFin1.isSelected());
			assertEquals("regatta TLE should be Fin1", ScoringLowPoint.TLE_FINISHERSPLUS1,
					fOptions.getTimeLimitPenalty());

			// test avg
			clickOn(buttonAvg);
			assertTrue("buttonAvg should be selected", buttonAvg.isSelected());
			assertEquals("regatta TLE should be avg", ScoringLowPoint.TLE_AVERAGE, fOptions.getTimeLimitPenalty());

			// test dnf
			clickOn(buttonDnf);
			assertTrue("buttonDnf should be selected", buttonDnf.isSelected());
			assertEquals("regatta TLE should be dnf", ScoringLowPoint.TLE_DNF, fOptions.getTimeLimitPenalty());

		}
		catch (Exception e) {
			fail("unexpected exception: " + e.toString());
		}
	}

	public void testGeneral() {
		JPanel localPanel = clickOnTabPane(TAB_PANE, GENERAL_PANEL);
		assertNotNull("cant find localPanel", localPanel);

		JRadioButton fRadioLongSeriesYes = (JRadioButton) findComponent(JRadioButton.class, "fRadioLongSeriesYes",
				localPanel);
		JRadioButton fRadioLongSeriesNo = (JRadioButton) findComponent(JRadioButton.class, "fRadioLongSeriesNo",
				localPanel);
		assertNotNull("fRadioLongSeriesYes should not be null", fRadioLongSeriesYes);
		assertNotNull("fRadioLongSeriesNo should not be null", fRadioLongSeriesNo);

		ScoringOptions fOptions = (ScoringOptions) fModel.getModel().getOptions();
		assertNotNull(fOptions);

		assertTrue("fRadioLongSeriesNo should be selected", fRadioLongSeriesNo.isSelected());
		assertTrue("regatta should be long series no", !fOptions.isLongSeries());

		clickOn(fRadioLongSeriesYes);
		assertTrue("fRadioLongSeriesYes should be selected", fRadioLongSeriesYes.isSelected());
		assertTrue("regatta should be long series yes", fOptions.isLongSeries());

		// Test overall scoring parameter
		JComboBox comboScoring = (JComboBox) findComponent(JComboBox.class, "fComboScoringSystem", localPanel);
		assertNotNull("fComboScoringSystem should not be null", comboScoring);

		Object[] scoringSystems = ScoringUtilities.getSupportedModels();
		assertEquals("low point should be default", scoringSystems[0], comboScoring.getSelectedItem());

		// order should be
		//			0 ScoringLowPoint.NAME
		//			1 ScoringLowPointAYCWednesday.NAME
		//			2 ScoringLowPointDnIceboat.NAME
		//			3 ScoringLowPointLightning.NAME
		//			4 ScoringLowPointSnipe.NAME

		// click on new scoring box item,
		String newSystemName = (String) scoringSystems[0];
		Object newSelect = clickOnComboBox("fComboScoringSystem", newSystemName);

		assertEquals("now should be lowpoint", newSystemName, newSelect);
		assertEquals(newSystemName, fRegatta.getScoringManager().getScoringSystemName());

		// click on new scoring box item,
		newSystemName = (String) scoringSystems[2];
		newSelect = clickOnComboBox("fComboScoringSystem", newSystemName);

		assertEquals("now should be lightning", newSystemName, newSelect);
		assertEquals(newSystemName, fRegatta.getScoringManager().getScoringSystemName());
	}

	public void testThrowouts() {
		JPanel localPanel = clickOnTabPane( TAB_PANE, THROWOUT_PANEL);
		assertNotNull("cant find localPanel", localPanel);

		JRadioButton fRadioThrowoutByNumRaces = (JRadioButton) findComponent(JRadioButton.class,
				"fRadioThrowoutByNumRaces", localPanel);
		JRadioButton fRadioThrowoutPerXRaces = (JRadioButton) findComponent(JRadioButton.class,
				"fRadioThrowoutPerXRaces", localPanel);
		JRadioButton fRadioThrowoutBestXRaces = (JRadioButton) findComponent(JRadioButton.class,
				"fRadioThrowoutBestXRaces", localPanel);
		JRadioButton fRadioThrowoutNone = (JRadioButton) findComponent(JRadioButton.class, "fRadioThrowoutNone",
				localPanel);
		JTextField fTextThrowoutPerX = (JTextField) findComponent(JTextField.class, "fTextThrowoutPerX", localPanel);
		JTextField fTextThrowoutBestX = (JTextField) findComponent(JTextField.class, "fTextThrowoutBestX", localPanel);
		JTextField fTextThrowout1 = (JTextField) findComponent(JTextField.class, "fTextThrowout1", localPanel);
		JTextField fTextThrowout2 = (JTextField) findComponent(JTextField.class, "fTextThrowout2", localPanel);
		JTextField fTextThrowout3 = (JTextField) findComponent(JTextField.class, "fTextThrowout3", localPanel);
		JTextField fTextThrowout4 = (JTextField) findComponent(JTextField.class, "fTextThrowout4", localPanel);

		assertNotNull("fRadioThrowoutByNumRaces should not be null", fRadioThrowoutByNumRaces);
		assertNotNull("fRadioThrowoutPerXRaces should not be null", fRadioThrowoutPerXRaces);
		assertNotNull("fRadioThrowoutBestXRaces should not be null", fRadioThrowoutBestXRaces);
		assertNotNull("fRadioThrowoutNone should not be null", fRadioThrowoutNone);
		assertNotNull("fTextThrowoutPerX should not be null", fTextThrowoutPerX);
		assertNotNull("fTextThrowoutBestX should not be null", fTextThrowoutBestX);
		assertNotNull("fTextThrowout1 should not be null", fTextThrowout1);
		assertNotNull("fTextThrowout2 should not be null", fTextThrowout2);
		assertNotNull("fTextThrowout3 should not be null", fTextThrowout3);
		assertNull("fTextThrowout4 should be null", fTextThrowout4);

		// default should be 'by min' with 1=2 and 2 & 3 
		assertTrue("fRadioThrowoutByNumRaces should be selected", fRadioThrowoutByNumRaces.isSelected());
		assertEquals("throwout1 should be 2", "2", fTextThrowout1.getText());
		assertEquals("throwout2 should be 0", "0", fTextThrowout2.getText());
		assertEquals("throwout3 should be 0", "0", fTextThrowout3.getText());

		assertTrue("throwout1 should be enabled", fTextThrowout1.isEnabled());
		assertTrue("throwout2 should be enabled", fTextThrowout2.isEnabled());
		assertTrue("throwout3 should be enabled", fTextThrowout3.isEnabled());
		assertTrue("fTextThrowoutPerX should not be enabled", !fTextThrowoutPerX.isEnabled());
		assertTrue("fTextThrowoutBestX should not be enabled", !fTextThrowoutBestX.isEnabled());

		ScoringOptions fOptions = (ScoringOptions) fModel.getModel().getOptions();
		assertNotNull(fOptions);

		assertEquals("scoring should be bynum", ScoringLowPoint.THROWOUT_BYNUMRACES, fOptions.getThrowoutScheme());
		assertEquals("1st throw should be 2 races", 2, ((Integer) fOptions.getThrowouts().get(0)).intValue());
		assertEquals("2nd throw should be 0 races", 0, ((Integer) fOptions.getThrowouts().get(1)).intValue());
		assertEquals("3rd throw should be 0 races", 0, ((Integer) fOptions.getThrowouts().get(2)).intValue());

		// june 8, 2013 works interactively
		sendStringAndEnter(fTextThrowout1, "3");
		assertEquals("fTextThrowout1 wrong", "3", fTextThrowout1.getText());
		assertEquals("1st throw should be 3 races", 3, ((Integer) fOptions.getThrowouts().get(0)).intValue());

		sendStringAndEnter(fTextThrowout2, "6");
		assertEquals("fTextThrowout1 wrong", "6", fTextThrowout2.getText());
		assertEquals("2nd throw should be 6 races", 6, ((Integer) fOptions.getThrowouts().get(1)).intValue());

		sendStringAndEnter(fTextThrowout3, "9");
		assertEquals("fTextThrowout1 wrong", "9", fTextThrowout3.getText());
		assertEquals("3rd throw should be 9 races", 9, ((Integer) fOptions.getThrowouts().get(2)).intValue());

		// --- change to per x
		clickOn(fRadioThrowoutPerXRaces);
		assertTrue("fRadioThrowoutPerXRaces should be selected", fRadioThrowoutPerXRaces.isSelected());
		assertEquals("Throwout should be perx", ScoringLowPoint.THROWOUT_PERXRACES, fOptions.getThrowoutScheme());
		assertTrue("throwout1 should not be enabled", !fTextThrowout1.isEnabled());
		assertTrue("throwout2 should not be enabled", !fTextThrowout2.isEnabled());
		assertTrue("throwout3 should not be enabled", !fTextThrowout3.isEnabled());
		assertTrue("fTextThrowoutPerX should be enabled", fTextThrowoutPerX.isEnabled());
		assertTrue("fTextThrowoutBestX should not be enabled", !fTextThrowoutBestX.isEnabled());

		assertEquals("fTextThrowoutPerX wrong", "0", fTextThrowoutPerX.getText());
		sendStringAndEnter(fTextThrowoutPerX, "5");
		assertEquals("fTextThrowout1 wrong", "5", fTextThrowoutPerX.getText());
		assertEquals("throwout should be per 5 races", 5, fOptions.getThrowoutPerX());

		// --- change to best x
		clickOn(fRadioThrowoutBestXRaces);
		assertTrue("fRadioThrowoutBestXRaces should be selected", fRadioThrowoutBestXRaces.isSelected());
		assertEquals("Throwout should be Bestx", ScoringLowPoint.THROWOUT_BESTXRACES, fOptions.getThrowoutScheme());
		assertTrue("throwout1 should not be enabled", !fTextThrowout1.isEnabled());
		assertTrue("throwout2 should not be enabled", !fTextThrowout2.isEnabled());
		assertTrue("throwout3 should not be enabled", !fTextThrowout3.isEnabled());
		assertTrue("fTextThrowoutPerX should not be enabled", !fTextThrowoutPerX.isEnabled());
		assertTrue("fTextThrowoutBestX should be enabled", fTextThrowoutBestX.isEnabled());

		assertEquals("fTextThrowoutBestX wrong", "0", fTextThrowoutBestX.getText());
		sendStringAndEnter(fTextThrowoutBestX, "5");
		assertEquals("fTextThrowout1 wrong", "5", fTextThrowoutBestX.getText());
		assertEquals("throwout should be Best 5 races", 5, fOptions.getThrowoutBestX());

		clickOn(fRadioThrowoutNone);
		assertTrue("fRadioThrowoutNone should be selected", fRadioThrowoutNone.isSelected());
		assertEquals("Throwout should be none", ScoringLowPoint.THROWOUT_NONE, fOptions.getThrowoutScheme());
		assertTrue("throwout1 should not be enabled", !fTextThrowout1.isEnabled());
		assertTrue("throwout2 should not be enabled", !fTextThrowout2.isEnabled());
		assertTrue("throwout3 should not be enabled", !fTextThrowout3.isEnabled());
		assertTrue("fTextThrowoutPerX should not be enabled", !fTextThrowoutPerX.isEnabled());
		assertTrue("fTextThrowoutBestX should not be enabled", !fTextThrowoutBestX.isEnabled());

	}

	public void testTiebreakers() {
		JPanel localPanel = clickOnTabPane( TAB_PANE, TIEBREAKER_PANEL);
		assertNotNull("cant find localPanel", localPanel);

		JRadioButton fRadioTieRrsDefault = (JRadioButton) findComponent(JRadioButton.class, "fRadioTieRrsDefault",
				localPanel);
		JRadioButton fRadioTieA82Only = (JRadioButton) findComponent(JRadioButton.class, "fRadioTieA82Only", localPanel);
		assertNotNull("fRadioTieRrsDefault should not be null", fRadioTieRrsDefault);
		assertNotNull("fRadioTieA82Only should not be null", fRadioTieA82Only);

		ScoringOptions fOptions = (ScoringOptions) fModel.getModel().getOptions();
		assertNotNull(fOptions);

		assertTrue("fRadioTieRrsDefault should be selected", fRadioTieRrsDefault.isSelected());
		assertEquals("scoring should be default rrs", ScoringLowPoint.TIE_RRS_DEFAULT, fOptions.getTiebreaker());

		clickOn(fRadioTieA82Only);
		assertTrue("fRadioTieA82Only should be selected", fRadioTieA82Only.isSelected());
		assertEquals("scoring should be A82 only", ScoringLowPoint.TIE_RRS_A82_ONLY, fOptions.getTiebreaker());

		clickOn(fRadioTieRrsDefault);
		assertTrue("fRadioTieRrsDefault should be selected", fRadioTieRrsDefault.isSelected());
		assertEquals("scoring should be default rrs", ScoringLowPoint.TIE_RRS_DEFAULT, fOptions.getTiebreaker());

	}

}
/**
 * $Log: PanelScoringOptionsTests.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:25:08 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * 
 */
