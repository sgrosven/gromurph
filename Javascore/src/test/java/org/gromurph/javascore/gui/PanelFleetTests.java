package org.gromurph.javascore.gui;

import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Fleet;

public class PanelFleetTests extends JavascoreTestCase {

	private static int fnum = 0;

	public PanelFleetTests(String test) {
		super(test);		
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// drop from master divisions list to just 2
		Object[] divs = fRegatta.getAllDivisions().toArray();
		for ( int i = Math.min(2,  divs.length-1); i < divs.length; i++) {
			fRegatta.removeDivision( (Division) divs[i]);
		}
	}
	
	public void testHandleDiffs() {
		//assertEquals("Number of windows is incorrect", 0, getOpenWindowCount());

		String fleetName = "Fleet " + (fnum++);
		Fleet fleet = new Fleet(fleetName);
		JDialog dialog = showPanel(fleet);

		// ======== NAME

		JTextField fTextName = (JTextField) findComponent(JTextField.class, "fTextName", dialog);
		assertNotNull(fTextName);

		assertEquals(fleetName, fleet.getName());
		assertEquals(fTextName.getText(), fleet.getName());

		String newName = "boo";
		sendStringAndEnter(fTextName, newName);
		assertEquals(newName, fleet.getName());

		// ======== HANDLE DIFFS

		JRadioButton fRadioHandleDiffAsIs = (JRadioButton) findComponent(JRadioButton.class, "fRadioHandleDiffAsIs",
				dialog);

		JRadioButton fRadioHandleDiffDrop = (JRadioButton) findComponent(JRadioButton.class, "fRadioHandleDiffDrop",
				dialog);

		assertNotNull(fRadioHandleDiffAsIs);
		assertNotNull(fRadioHandleDiffDrop);

		fRadioHandleDiffDrop.setSelected(true); // make sure its on drop

		fRadioHandleDiffAsIs.doClick();
		assertEquals(Fleet.DIFFLENGTHS_ASIS, fleet.getHandleDifferentLengths());

		fRadioHandleDiffDrop.doClick();
		assertEquals(Fleet.DIFFLENGTHS_DROP, fleet.getHandleDifferentLengths());

		// =========== CHECKSTARTSAME
		assertEquals(true, fleet.isSameStartSameClass());

		// first click should reverse
		clickOnCheckBox("fCheckStartSameScoreSame");
		assertEquals(false, fleet.isSameStartSameClass());

		// second click should put it back
		clickOnCheckBox("fCheckStartSameScoreSame");
		assertEquals(true, fleet.isSameStartSameClass());

		clickOKButton(dialog);
	}

}
