// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelSubDivision.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * Main panel for editting information about a single SubDivision
 **/

public class PanelSubDivision extends BaseEditor<SubDivision> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private SubDivision fSubDivision;

	JTextFieldSelectAll fTextName;
	JTextFieldSelectAll fTextAddon;
	JComboBox fComboParentDivision;
	JCheckBox fCheckMonopoly;
	JCheckBox fCheckScoreSeparately;
	JCheckBox fCheckQualifying;

	public PanelSubDivision(BaseEditorContainer parent) {
		super(parent);
		setPreferredSize(new Dimension(400, 350));
		addFields();
	}

	@Override
	public void setObject(SubDivision obj) {
		fSubDivision = obj;
		super.setObject(obj);
	}

	public void propertyChange(PropertyChangeEvent event) {}

	public void addFields() {
		setLayout(new GridBagLayout());
		HelpManager.getInstance().registerHelpTopic(this, "subdivisions");

		int row = 0;
		gridbagAdd(new JLabel(res.getString("SubDivisionLabelClassName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll(12);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("SubDivisionLabelClassNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "subdivisions.fTextName");
		gridbagAdd(fTextName, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		gridbagAdd(new JLabel(res.getString("SubDivisionLabelParentDivision")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboParentDivision = new JComboBox();
		fComboParentDivision.setName("fComboParentDivision");

		fComboParentDivision.setToolTipText(res.getString("SubDivisionLabelParentDivisionToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboParentDivision, "subdivisions.fComboParentDivision");
		gridbagAdd(fComboParentDivision, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		gridbagAdd(new JLabel(res.getString("SubDivisionLabelAddon")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextAddon = new JTextFieldSelectAll(12);
		fTextAddon.setName("fTextAddon");
		fTextAddon.setToolTipText(res.getString("SubDivisionLabelAddonToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextAddon, "subdivisions.fTextAddon");
		gridbagAdd(fTextAddon, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fCheckScoreSeparately = new JCheckBox(res.getString("SubDivisionLabelScoreSeparately"));
		fCheckScoreSeparately.setName("fCheckScoreSeparately");
		fCheckScoreSeparately.setToolTipText(res.getString("SubDivisionLabelScoreSeparatelyToolTip"));
		HelpManager.getInstance().registerHelpTopic(fCheckScoreSeparately, "subdivisions.fCheckScoreSeparately");
		gridbagAdd(fCheckScoreSeparately, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fCheckMonopoly = new JCheckBox(res.getString("SubDivisionLabelMonopoly"));
		fCheckMonopoly.setName("fCheckMonopoly");
		fCheckMonopoly.setToolTipText(res.getString("SubDivisionLabelMonopolyToolTip"));
		HelpManager.getInstance().registerHelpTopic(fCheckMonopoly, "subdivisions.fCheckMonopoly");
		gridbagAdd(fCheckMonopoly, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fCheckQualifying = new JCheckBox(res.getString("SubDivisionQualLabel"));
		fCheckQualifying.setName("fRadioQualifying");
		fCheckQualifying.setToolTipText(res.getString("SubDivisionQualToolTip"));
		HelpManager.getInstance().registerHelpTopic(fCheckQualifying, "subdivisions.fRadioQualifying");
		gridbagAdd( fCheckQualifying, 0, row, 3, GridBagConstraints.WEST, GridBagConstraints.NONE);
	}

	private void rebuildComboParentDivision() {
		if (isStarted)
			fComboParentDivision.removeActionListener(this);

		Regatta reg = getRegatta();
		Vector<AbstractDivision> divs = new Vector<AbstractDivision>();

		if (reg != null) {
			divs.addAll(reg.getDivisions());
			divs.addAll(reg.getFleets());
		}
		fComboParentDivision.setModel(new DefaultComboBoxModel(divs));

		this.revalidate();

		if (isStarted)
			fComboParentDivision.addActionListener(this);
	}

	@Override
	public void start() {
		fTextName.addActionListener(this);
		fComboParentDivision.addActionListener(this);
		fCheckMonopoly.addActionListener(this);
		fCheckScoreSeparately.addActionListener(this);
		fCheckQualifying.addActionListener(this);
		fTextAddon.addActionListener(this);
	}

	@Override
	public void stop() {
		fTextName.removeActionListener(this);
		fComboParentDivision.removeActionListener(this);
		fCheckMonopoly.removeActionListener(this);
		fCheckScoreSeparately.removeActionListener(this);
		fCheckQualifying.removeActionListener(this);
		fTextAddon.removeActionListener(this);
	}

	@Override
	public void exitOK() {
		super.exitOK();
		JavaScore.backgroundSave();
		JavaScore.subWindowClosing();
	}

	@Override
	public void restore(SubDivision a, SubDivision b) {
		if (a == b)
			return;
		SubDivision active = (SubDivision) a;
		SubDivision backup = (SubDivision) b;

		active.setName(backup.getName());
		active.setParentDivision(backup.getParentDivision());
		active.setScoreSeparately(backup.isScoreSeparately());
		active.setMonopoly(backup.isMonopoly());
		
		active.setGroup(backup.getGroup());
		active.setRaceAddon(backup.getRaceAddon());

		active.clearEntries();
		for (Entry de : backup.getEntries()) {
			active.addEntry(de);
		}

		super.restore(active, backup);
	}

	@Override
	public void updateFields() {
		SubDivision subdiv = fSubDivision;
		if (subdiv == null)
			subdiv = new SubDivision();

		rebuildComboParentDivision();

		fTextName.setText(subdiv.getName());
		fCheckScoreSeparately.setSelected(subdiv.isScoreSeparately());
		fCheckMonopoly.setSelected(!subdiv.isMonopoly());

		fComboParentDivision.setSelectedItem(subdiv.getParentDivision());
		fCheckQualifying.setSelected(subdiv.isGroupQualifying());

		fTextAddon.setText(Double.toString(subdiv.getRaceAddon()));
	}

	public SubDivision getSubDivision() {
		return fSubDivision;
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();

		if (object == fTextName)
			fTextName_actionPerformed();
		else if (object == fComboParentDivision)
			fComboParentDivision_actionPerformed();
		else if (object == fCheckScoreSeparately)
			fCheckScoreSeparately_actionPerformed();
		else if (object == fCheckMonopoly)
			fCheckMonopoly_actionPerformed();
		else if (object == fCheckQualifying)
			fCheckQualifying_actionPerformed();
		else if (object == fTextAddon)
			fTextAddon_actionPerformed();
	}

	boolean editing = false;

	private void fTextAddon_actionPerformed() {
		if (editing)
			return;
		String oldvalue = Util.formatDouble(fSubDivision.getRaceAddon(), 2);
		try {
			double val = org.gromurph.util.Util.parseDouble(fTextAddon.getText());
			fSubDivision.setRaceAddon(val);
		} catch (NumberFormatException e) {
			editing = true;
			StringBuffer sb = new StringBuffer();
			sb.append(res.getString("SubdivisionAddonMessageInvalidNumber"));
			sb.append(NEWLINE);
			sb.append(NEWLINE);
			sb.append(e.toString());
			JOptionPane.showMessageDialog(this, sb.toString());
			fTextAddon.setText(oldvalue);
			editing = false;
//			fTextAddon.requestFocusInWindow();
		}
	}

	void fTextName_actionPerformed() {
		if (fSubDivision == null)
			return;
		fSubDivision.setName(fTextName.getText());
	}

	void fComboParentDivision_actionPerformed() {
		if (fSubDivision == null)
			return;
		fSubDivision.setParentDivision((AbstractDivision) fComboParentDivision.getSelectedItem());
	}

	void fCheckQualifying_actionPerformed() {
		if (fCheckQualifying.isSelected())
			fSubDivision.setGroup(SubDivision.QUALIFYING);
		else
			fSubDivision.setGroup(SubDivision.FINAL);
	}

	void fCheckScoreSeparately_actionPerformed() {
		if (fSubDivision == null)
			return;
		fSubDivision.setScoreSeparately(fCheckScoreSeparately.isSelected());
	}

	void fCheckMonopoly_actionPerformed() {
		if (fSubDivision == null)
			return;
		fSubDivision.setMonopoly(!fCheckMonopoly.isSelected());
	}

	private static Regatta sTestRegatta; // for testing only

	private Regatta getRegatta() {
		if (sTestRegatta == null)
			return JavaScoreProperties.getRegatta();
		else
			return sTestRegatta;
	}

	public static void setTestRegatta(Regatta reg) {
		sTestRegatta = reg;
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Division div = DivisionList.getMasterList().get(1);
		Division div2 = DivisionList.getMasterList().get(2);

		SubDivision s2 = new SubDivision("Blue", div);
		s2.setMonopoly(true);
		s2.setScoreSeparately(false);

		SubDivision s3 = new SubDivision("Green", div2);
		s3.setMonopoly(false);
		s3.setScoreSeparately(true);

		SubDivision s1 = new SubDivision("White", DivisionList.getMasterList().get(3));
		s1.setMonopoly(false);
		s1.setScoreSeparately(false);

		Regatta testRegatta = new Regatta();

		testRegatta.addSubDivision(s1);
		testRegatta.addSubDivision(s2);
		testRegatta.addSubDivision(s3);

		DialogDivisionListEditor d = new DialogDivisionListEditor(null);
		d.setMasterList(testRegatta.getSubDivisions(), "SubDiv Test");
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
 * $Log: PanelSubDivision.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:41 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.9.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks on
 * text fields in panels.
 * 
 * Revision 1.9.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.9 2004/05/06 02:11:51 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.8 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.7 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.6 2003/04/27 21:06:01 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.5 2003/03/19 02:38:25 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.4 2003/03/16 20:38:33 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.3 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
