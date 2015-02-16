// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelSplitFleet.java,v 1.10 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.StageList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.MultiStage;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelSplitQualifying extends BaseEditor<Regatta> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	JComboBox fComboDivision;
	JComboBox fComboStage;
	JLabel fLabelStage;
	
	List<JTextFieldSelectAll> fTextDivName = new ArrayList<JTextFieldSelectAll>(2); // JTextFieldSelectAll
	JTextFieldSelectAll fTextNumSplits;

	JPanel fPanelMain;
	JPanel fPanelDivs;

	private int fNumSplits = 2;

	@Override public void start() {
		fTextNumSplits.addActionListener(this);

		// make sure fields are exist thru length of div list
		for (int i = 0; i < fTextDivName.size(); i++) {
			JTextField textName = fTextDivName.get(i);
			textName.addActionListener(this);
		}

	}

	@Override public void stop() {
		fTextNumSplits.removeActionListener(this);

		// make sure fields are exist thru length of div list
		for (int i = 0; i < fTextDivName.size(); i++) {
			JTextField textName = fTextDivName.get(i);
			textName.removeActionListener(this);
		}
	}

	@Override public void updateFields() {
		if (fRegatta != null && fRegatta.isMultistage()) {
			StageList qstages = new StageList();
			StageList stages = ( (MultiStage) fRegatta.getScoringManager()).getStages();
			for (Stage s : stages) {
				if (s.isCombinedQualifying()) qstages.add(s);
			}
			fComboStage.setModel(new DefaultComboBoxModel(qstages.toArray()));
		} else {
			
		}
	}
// TODO check for empty fComboStage, popup do tell user to set qualifying
	
	public PanelSplitQualifying(BaseEditorContainer parent) {
		super(parent);
		HelpManager.getInstance().registerHelpTopic(this, "splitfleetqualifying");
		addFields();
		setName("PanelSplitQualifying");
		setTitle(res.getString("PanelSplitQualTitle"));
		setExitOKonWindowClose(false);
		setPreferredSize(new Dimension(350, 260));
	}

	public void addFields() {
		setLayout(new BorderLayout());
		fPanelMain = new JPanel(new BorderLayout());
		fPanelMain.setName("fPanelSplitQual");

		reInitFields();

		add(fPanelMain, BorderLayout.CENTER);
	}

	private void reInitFields() {
		// deleteOldSplitterFields();

		fPanelMain.removeAll();

		JPanel north = new JPanel(new GridBagLayout());
		fPanelMain.add(north, BorderLayout.NORTH);

		if (fRegatta == null) fRegatta = JavaScoreProperties.getRegatta();
		List<AbstractDivision> divs = null;
		
		if (fRegatta != null) {
			divs = new ArrayList<AbstractDivision>(10);
			for (Entry e : fRegatta.getAllEntries()) {
				if (!divs.contains(e.getDivision())) divs.add(e.getDivision());
			}
			for (SubDivision subdiv : fRegatta.getSubDivisions()) {
				if (!subdiv.isGroupQualifying() && divs.contains(subdiv.getParentDivision())) {
					divs.add(subdiv);
				}
			}
		}

		int startrow = 0;
		fLabelStage = new JLabel(res.getString("SplitFleetComboLabelStage"));
		fComboStage = new JComboBox();
		fComboStage.setName("fComboStage");
		HelpManager.getInstance().registerHelpTopic( fComboStage, "splitqual.srcstage");
		
		gridbagAdd(north, fLabelStage, 0, startrow, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridbagAdd(north, fComboStage, 1, startrow, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		startrow++;

		if (divs != null) {
    		JLabel divlabel = new JLabel(res.getString("SplitFleetComboLabelDivision"));
    		fComboDivision = new JComboBox();
    		fComboDivision.setName("fComboDivision");
    		fComboDivision.setModel(new DefaultComboBoxModel(divs.toArray()));
    		HelpManager.getInstance().registerHelpTopic( fComboDivision, "splitqual.srcdivision");
    
    		gridbagAdd(north, divlabel, 0, startrow, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    		gridbagAdd(north, fComboDivision, 1, startrow, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    		startrow++;
		}

		fTextNumSplits = new JTextFieldSelectAll(3);
		fTextNumSplits.setValue(new Integer(fNumSplits));
		fTextNumSplits.setName("fTextNumSplits");
		HelpManager.getInstance().registerHelpTopic( fTextNumSplits, "splitqual.numsplits");

		gridbagAdd(north, new JLabel(res.getString("SplitFleetNumSplits")), 0, startrow, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE);

		gridbagAdd(north, fTextNumSplits, 1, startrow, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		startrow++;
		fPanelDivs = new JPanel(new GridBagLayout());
		fPanelDivs.setName("fPanelDivs");

		reInitDivFields();

		JScrollPane scroll = new JScrollPane(fPanelDivs);
		// scroll.setHorizontalScrollBarPolicy(
		// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// scroll.setVerticalScrollBarPolicy(
		// ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// gridbagAdd( fPanelMain, scroll, 0, startrow, 2,
		// GridBagConstraints.WEST, GridBagConstraints.BOTH);
		fPanelMain.add(scroll, BorderLayout.CENTER);

	}

	private void reInitDivFields() {
		Insets inset = new Insets(1, 3, 1, 3);

		// first time initialized div fields, add column headers
		if (fTextDivName.size() == 0) {
			gridbagAdd(fPanelDivs, new JLabel(" "), 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, inset);
			gridbagAdd(fPanelDivs, new JLabel(res.getString("SplitSeedingDivName")), 1, 0, 1, 1,
					GridBagConstraints.WEST, GridBagConstraints.NONE, inset);
		}

		// add fields to end of divname/toppos arrays if needed
		for (int i = fTextDivName.size(); i < Math.max(4, fNumSplits); i++) {
			
			JTextFieldSelectAll textName = new JTextFieldSelectAll(8);
			textName.setName("fTextDivName" + i);
			textName.setToolTipText(res.getString("SplitFleetDivNameToolTip"));
			HelpManager.getInstance().registerHelpTopic( textName, "splitqual.newdivname");
			fTextDivName.add(textName);
			textName.addActionListener(this);

			int row = i + 1; // row 0 are the headers
			gridbagAdd(fPanelDivs, new JLabel(" "), 0, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
					inset);
			gridbagAdd(fPanelDivs, textName, 1, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, inset);

		}

		// update visibility
		// ts = fTextDivName.size();
		for (int i = 0; i < fTextDivName.size(); i++) {
			boolean visible = i < fNumSplits;
			((JTextField) fTextDivName.get(i)).setVisible(visible);
		}

		resetDefaultSplitPositions();

	}

	private void resetDefaultSplitPositions() {
		// for hi/lo splits, calculate a default of splitting evenly
		double defaultPosPerSplit = 1;

		if (fRegatta != null && fRegatta.getNumEntries() >= fNumSplits) {
			// int nEntries = fRegatta.getNumEntries();
			defaultPosPerSplit = fRegatta.getNumEntries() / fNumSplits;
		}
	}

	Regatta fRegatta = null;

	@Override
	public void setObject(Regatta obj) throws ClassCastException {
		super.setObject(obj);
		fRegatta = (Regatta) obj;
		reInitDivFields();
		resetDefaultSplitPositions();
		doEventOccurred(null);
	}

	@Override public void exitOK() {
		fRegatta = (Regatta) getObject();
		if (validateData()) {
			if (Util.confirm(res.getString("SplitFleetConfirmMessage"), true)) {
				splitFleet();
				JavaScore.backgroundSave();
			}
		}
		JavaScore.subWindowClosing();
	}

	private boolean validateData() {
		if (fNumSplits < 2) return false;
		if (fRegatta == null) return false;

		List<String> divNames = new ArrayList<String>();
		for (AbstractDivision div : fRegatta.getDivisions()) {
			divNames.add(div.getName().toLowerCase());
		}
		for (Fleet fleet : fRegatta.getFleets()) {
			divNames.add(fleet.getName().toLowerCase());
		}
		for (SubDivision sub : fRegatta.getSubDivisions()) {
			divNames.add(sub.getName().toLowerCase());
		}

		int numEntries = fRegatta.getNumEntries();

		for (int n = 0; n < fNumSplits; n++) {
			String newDiv = getDivisionName(n);
			if (newDiv.trim().length() == 0) {
				JOptionPane.showMessageDialog(this, MessageFormat.format(
						res.getString("SplitFleetErrorEmptyDivisionMessage"), new Object[] { newDiv }), res
						.getString("SplitFleetErrorTitle"), JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (divNames.contains(newDiv.toLowerCase())) {
				JOptionPane.showMessageDialog(this, MessageFormat.format(
						res.getString("SplitFleetErrorDuplicateDivisionMessage"), new Object[] { newDiv }), res
						.getString("SplitFleetErrorTitle"), JOptionPane.ERROR_MESSAGE);
				return false;
			}

		}

		return true;
	}

	private void splitFleet() {
		AbstractDivision parentDiv = (AbstractDivision) fComboDivision.getSelectedItem();
		String[] divNames = new String[fNumSplits];

		for (int n = 0; n < fNumSplits; n++) {
			divNames[n] = getDivisionName(n);
		}

		Stage stage = (Stage) fComboStage.getSelectedItem();
		new RegattaManager(fRegatta).splitFleetSeedByPosition( stage, parentDiv, fNumSplits, divNames);
	}

	public String getDivisionName(int n) {
		return ((JTextField) fTextDivName.get(n)).getText();
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (fTextDivName.contains(object)) fTextDivName_actionPerformed(event);
		else if (fTextNumSplits == object) fTextNumSplits_actionPerformed(event);
	}

	private void setNumSplits(int newSplit) {
		int oldSplit = fNumSplits;
		fNumSplits = newSplit;
		if (fNumSplits != oldSplit) reInitDivFields();
		fPanelMain.revalidate();
	}

	void fTextNumSplits_actionPerformed(AWTEvent event) {

		try {
			fTextNumSplits.commitEdit();
			setNumSplits(((Integer) fTextNumSplits.getValue()));
			doEventOccurred(event);
		}
		catch (ParseException e) {}
	}

	void fTextDivName_actionPerformed(AWTEvent event) {
		doEventOccurred(event);
	}

	private void doEventOccurred(AWTEvent event) {

		if (getEditorParent() != null) {
			getEditorParent().eventOccurred(this, event);
		}
	}

	private static String BASEDIR = Util.getWorkingDirectory() + "/testregattas/";

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Util.setTesting(true);
		Regatta reg = null;
		try {
			reg = RegattaManager.readRegattaFromDisk(BASEDIR, "MultistageOCRLaser.regatta");
    		DialogBaseEditor fFrame = new DialogBaseEditor();
    		fFrame.addWindowListener(new WindowAdapter() {
    			@Override public void windowClosing(WindowEvent event) {
    				System.exit(0);
    			}
    		});
    
    		fFrame.setObject(reg, new PanelSplitQualifying( fFrame));
    		// PanelSplitFleet splitter = dialog.getEditor();
    
    		fFrame.startUp();
    		fFrame.setVisible(true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public boolean changesPending() {
		return true;
	}

	public void propertyChange(PropertyChangeEvent ev) {
		// do nothing
	}

	public int getNumSplits() {
		return fNumSplits;
	}

}
/**
 * $Log: PanelSplitFleet.java,v $ Revision 1.10 2006/05/19 05:48:43 sandyg final release 5.1 modifications

 */
