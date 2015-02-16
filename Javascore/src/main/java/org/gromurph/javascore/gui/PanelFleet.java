// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelFleet.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.*;

import javax.swing.*;

import org.gromurph.javascore.*;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.FleetList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.util.*;

/**
 * Main panel for editting information about a single Fleet
 **/

public class PanelFleet extends BaseEditor<Fleet> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private Fleet fFleet;

	JTextField fTextName;
	ButtonGroup fGroupHandleDiffs;
	JRadioButton fRadioHandleDiffAsIs;
	JRadioButton fRadioHandleDiffDrop;
	JCheckBox fCheckStartSameScoreSame;
	JPanel fPanelDivs;
	JButton fButtonSelectAll;
	JButton fButtonClearAll;

	Map<Division, JCheckBox> fCheckDivisions;
	DivisionList fDivisions;

	public PanelFleet(BaseEditorContainer parent) {
		super(parent);
		setPreferredSize(new Dimension(300, 300));
		addFields();
	}

	@Override public void setObject(Fleet obj) throws ClassCastException {
		fFleet = obj;
		super.setObject(obj);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Fleet f = fFleet;
		int whoaNelly = 1;
	}

	public void addFields() {
		setLayout(new GridBagLayout());
		HelpManager.getInstance().registerHelpTopic(this, "fleets");

		int row = 0;
		gridbagAdd(new JLabel(res.getString("FleetLabelClassName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll(12);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("FleetLabelClassNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "fleets.fTextName");
		gridbagAdd(fTextName, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fPanelDivs = new JPanel(new GridBagLayout());
		fPanelDivs.setBorder(BorderFactory.createTitledBorder(res.getString("FleetDivisionPanelTitle")));
		gridbagAdd(this, fPanelDivs, 0, row, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0,
				0, 0));
		HelpManager.getInstance().registerHelpTopic(fPanelDivs, "fleets.fPanelDivs");

		fButtonClearAll = new JButton(res.getString("FleetLabelClearAll"));
		fButtonClearAll.setName("fButtonClearAll");
		fButtonClearAll.setMnemonic(res.getString("FleetLabelClearAllMnemonic").charAt(0));
		fButtonClearAll.setToolTipText(res.getString("FleetLabelClearAllToolTip"));
		HelpManager.getInstance().registerHelpTopic(fPanelDivs, "fleets.fButtonClearAll");

		fButtonSelectAll = new JButton(res.getString("FleetLabelSelectAll"));
		fButtonSelectAll.setName("fButtonSelectAll");
		fButtonSelectAll.setMnemonic(res.getString("FleetLabelSelectAllMnemonic").charAt(0));
		fButtonSelectAll.setToolTipText(res.getString("FleetLabelSelectAllToolTip"));
		HelpManager.getInstance().registerHelpTopic(fPanelDivs, "fleets.fButtonSelectAll");

		row++;
		fCheckStartSameScoreSame = new JCheckBox(res.getString("FleetLabelStartSameScoreSame"));
		fCheckStartSameScoreSame.setName("fCheckStartSameScoreSame");
		fCheckStartSameScoreSame.setToolTipText(res.getString("FleetLabelStartSameScoreSameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "fleets.fCheckStartSameScoreSame");
		gridbagAdd(this, fCheckStartSameScoreSame, 0, row, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 0, 0, 0));

		fGroupHandleDiffs = new ButtonGroup();
		fRadioHandleDiffAsIs = new JRadioButton(res.getString("FleetLabelHandleDiffAsIs"));
		fRadioHandleDiffAsIs.setName("fRadioHandleDiffAsIs");
		fRadioHandleDiffAsIs.setMnemonic(res.getString("FleetLabelHandleDiffAsIsMnemonic").charAt(0));
		fRadioHandleDiffAsIs.setToolTipText(res.getString("FleetLabelHandleDiffAsIsToolTip"));
		HelpManager.getInstance().registerHelpTopic(fRadioHandleDiffAsIs, "fleets.fRadioHandleDiff");
		fGroupHandleDiffs.add(fRadioHandleDiffAsIs);

		fRadioHandleDiffDrop = new JRadioButton(res.getString("FleetLabelHandleDiffDrop"));
		fRadioHandleDiffDrop.setName("fRadioHandleDiffDrop");
		fRadioHandleDiffDrop.setMnemonic(res.getString("FleetLabelHandleDiffDropMnemonic").charAt(0));
		fRadioHandleDiffDrop.setToolTipText(res.getString("FleetLabelHandleDiffDropToolTip"));
		HelpManager.getInstance().registerHelpTopic(fRadioHandleDiffDrop, "fleets.fRadioHandleDiff");
		fGroupHandleDiffs.add(fRadioHandleDiffDrop);

		row++;
		gridbagAdd(this, new JLabel(res.getString("FleetLabelHandleDiff")), 0, row, 2, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(10, 0, 0, 0));
		row++;
		gridbagAdd(this, fRadioHandleDiffDrop, 0, row, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 15, 0, 0));
		row++;
		gridbagAdd(this, fRadioHandleDiffAsIs, 0, row, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 15, 0, 0));
	}

	@Override
	public void start() {
		fTextName.addActionListener(this);

		addCheckActions();
		fButtonSelectAll.addActionListener(this);
		fButtonClearAll.addActionListener(this);

		fRadioHandleDiffAsIs.addActionListener(this);
		fRadioHandleDiffDrop.addActionListener(this);
		fCheckStartSameScoreSame.addActionListener(this);
	}

	@Override
	public void stop() {
		fTextName.removeActionListener(this);

		removeCheckActions();
		fButtonSelectAll.removeActionListener(this);
		fButtonClearAll.removeActionListener(this);

		fRadioHandleDiffAsIs.removeActionListener(this);
		fRadioHandleDiffDrop.removeActionListener(this);
		fCheckStartSameScoreSame.removeActionListener(this);
	}

	private void addCheckActions() {
		for (Iterator v = fCheckDivisions.values().iterator(); v.hasNext();) {
			JCheckBox check = (JCheckBox) v.next();
			check.addActionListener(this);
		}
	}

	private void removeCheckActions() {
		for (Iterator v = fCheckDivisions.values().iterator(); v.hasNext();) {
			JCheckBox check = (JCheckBox) v.next();
			check.removeActionListener(this);
		}
	}

	@Override
	public void exitOK() {
		super.exitOK();
		JavaScore.backgroundSave();
		JavaScore.subWindowClosing();
	}

	@Override
	public void restore(Fleet a, Fleet b) {
		if (a == b) return;
		Fleet active = (Fleet) a;
		Fleet backup = (Fleet) b;

		active.setName(backup.getName());
		active.setSameStartSameClass(backup.isSameStartSameClass());
		active.setHandleDifferentLengths(backup.getHandleDifferentLengths());

		active.clearDivisions();
		for (java.util.Iterator m = backup.members(); m.hasNext();) {
			active.addDivision((Division) m.next());
		}

		super.restore(active, backup);
	}

	@Override
	public void updateFields() {
		rebuildDivisionCheckBoxes();

		if (fFleet == null) {
			fTextName.setText("");
			fRadioHandleDiffAsIs.setSelected(false);
			fRadioHandleDiffDrop.setSelected(false);
			fCheckStartSameScoreSame.setSelected(false);
		} else {
			fTextName.setText(fFleet.getName());
			if (fFleet.getHandleDifferentLengths().equals(Fleet.DIFFLENGTHS_ASIS)) {
				fRadioHandleDiffAsIs.setSelected(true);
			} else if (fFleet.getHandleDifferentLengths().equals(Fleet.DIFFLENGTHS_DROP)) {
				fRadioHandleDiffDrop.setSelected(true);
			}
			fCheckStartSameScoreSame.setSelected(fFleet.isSameStartSameClass());

			for (Iterator m = fFleet.members(); m.hasNext();) {
				AbstractDivision div = (AbstractDivision) m.next();
				JCheckBox check = fCheckDivisions.get(div);
				if (check != null) check.setSelected(true);
			}
		}
	}

	private void rebuildDivisionCheckBoxes() {
		if (isStarted) removeCheckActions();

		fCheckDivisions = new TreeMap<Division, JCheckBox>();

		Regatta reg = getRegatta();
		if (reg == null) return;

		fPanelDivs.removeAll();

		int i = 0;
		int x = 0;
		int y = 0;
		for (Division div : reg.getDivisions()) {
			JCheckBox check = new JCheckBox(div.getName());
			fCheckDivisions.put(div, check);
			gridbagAdd(fPanelDivs, check, x, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
					2, 2, 2));
			HelpManager.getInstance().registerHelpTopic(check, "fleets.fPanelDivs");
			x++;
			if (x > 2) {
				x = 0;
				y++;
			}
			i++;
		}
		y++;
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		gridbagAdd(fPanelDivs, panelButtons, 0, y, 3, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
				2, 2, 2, 2));
		panelButtons.add(fButtonSelectAll);
		panelButtons.add(fButtonClearAll);

		this.revalidate();
		if (isStarted) addCheckActions();
	}

	public Fleet getFleet() {
		return fFleet;
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == fTextName) fTextName_actionPerformed();
		else if (fCheckDivisions.containsValue(object)) fCheckDivisions_actionPerformed(event);
		else if (object == fButtonSelectAll) fButtonSelectAll_actionPerformed();
		else if (object == fButtonClearAll) fButtonClearAll_actionPerformed();
		else if (object == fRadioHandleDiffAsIs) fRadioHandleDiff_actionPerformed(event);
		else if (object == fRadioHandleDiffDrop) fRadioHandleDiff_actionPerformed(event);
		else if (object == fCheckStartSameScoreSame) fCheckStartSameScoreSame_actionPerformed();

		if (getEditorParent() != null) getEditorParent().eventOccurred(this, event);
	}

	void fTextName_actionPerformed() {
		if (fFleet == null) return;
		fFleet.setName(fTextName.getText());
	}

	void fCheckDivisions_actionPerformed(ActionEvent event) {
		checkDivision((JCheckBox) event.getSource());
	}

	void checkDivision(JCheckBox check) {
		Regatta reg = getRegatta();
		if (reg == null) return;

		String divname = check.getText();
		Division div = reg.getDivision(divname);

		if (check.isSelected()) {
			fFleet.addDivision(div);
		} else {
			fFleet.removeDivision(div);
		}
	}

	void fButtonSelectAll_actionPerformed() {
		for (Iterator v = fCheckDivisions.values().iterator(); v.hasNext();) {
			JCheckBox check = (JCheckBox) v.next();
			check.setSelected(true);
			checkDivision(check);
		}
	}

	void fButtonClearAll_actionPerformed() {
		for (Iterator v = fCheckDivisions.values().iterator(); v.hasNext();) {
			JCheckBox check = (JCheckBox) v.next();
			check.setSelected(false);
			checkDivision(check);
		}
	}

	void fCheckStartSameScoreSame_actionPerformed() {
		fFleet.setSameStartSameClass(fCheckStartSameScoreSame.isSelected());
	}

	void fRadioHandleDiff_actionPerformed(ActionEvent event) {
		if (event.getSource() == fRadioHandleDiffAsIs) {
			fFleet.setHandleDifferentLengths(Fleet.DIFFLENGTHS_ASIS);
		} else if (event.getSource() == fRadioHandleDiffDrop) {
			fFleet.setHandleDifferentLengths(Fleet.DIFFLENGTHS_DROP);
		}
	}

	private static Regatta sTestRegatta; // for testing only

	private Regatta getRegatta() {
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg == null) reg = sTestRegatta;
		return reg;
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		FleetList fleets = new FleetList();

		Fleet f1 = new Fleet("Opti Fleet");
		Division doa = new Division("Opti A", RatingOneDesign.SYSTEM);
		Division dob = new Division("Opti B", RatingOneDesign.SYSTEM);
		Division doc = new Division("Opti C", RatingOneDesign.SYSTEM);
		f1.addDivision(doa);
		f1.addDivision(dob);
		f1.addDivision(doc);
		f1.setSameStartSameClass(true);
		f1.setHandleDifferentLengths(Fleet.DIFFLENGTHS_ASIS);
		fleets.add(f1);

		Fleet f2 = new Fleet("J22 Fleet");
		Division dja = new Division("J22 A", RatingOneDesign.SYSTEM);
		Division djb = new Division("J22 B", RatingOneDesign.SYSTEM);
		Division djc = new Division("J22 C", RatingOneDesign.SYSTEM);
		f2.addDivision(dja);
		f2.addDivision(djb);
		f2.addDivision(djc);
		f2.setSameStartSameClass(false);
		f2.setHandleDifferentLengths(Fleet.DIFFLENGTHS_DROP);
		fleets.add(f2);

		sTestRegatta = new Regatta();
		sTestRegatta.removeAllDivisions();
		sTestRegatta.addDivision(doa);
		sTestRegatta.addDivision(dob);
		sTestRegatta.addDivision(doc);
		sTestRegatta.addDivision(dja);
		sTestRegatta.addDivision(djb);
		sTestRegatta.addDivision(djc);

		sTestRegatta.addFleet(f1);
		sTestRegatta.addFleet(f2);

		DialogDivisionListEditor d = new DialogDivisionListEditor(null);
		d.setMasterList(fleets, "Fleet Test");
		d.start();
		d.pack();

		d.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		d.setVisible(true);
	}

}
/**
 * $Log: PanelFleet.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.10 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.9 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.8 2003/04/27 21:35:34 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.7 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.6 2003/03/19 02:38:23 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.5 2003/03/16 20:38:31 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.4 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
