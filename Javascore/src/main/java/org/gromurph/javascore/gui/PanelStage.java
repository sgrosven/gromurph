// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelScoringOptions.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
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
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;
import org.slf4j.LoggerFactory;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelStage extends BaseEditor<Stage> implements Constants, ActionListener {
	
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	private Stage fStage;

	public Regatta fRegatta;
	public MultiStageScoring fScoring;

	public PanelStage(BaseEditorContainer parent) {
		super(parent);
		addFields();
		setPreferredSize(new Dimension(600, 400));
	}

	@Override public void setObject(Stage inObj) {
		super.setObject(inObj);

		if (fStage == inObj) return;

		if (fStage != null) fStage.removePropertyChangeListener(this);
		fStage = (Stage) inObj;
		if (isVisible() && fStage != null) {
			fStage.addPropertyChangeListener(this);
		}
		updateFields();
	}

	@Override public void restore(Stage a, Stage b) {
		if (a == b) return;

		Stage active = (Stage) a;
		Stage backup = (Stage) b;

		active.setModel(backup.getModel().getName());
		active.setName(backup.getName());
		active.setPrevStage(backup.getPrevStage());
		active.setScoreCarryOver(backup.getScoreCarryOver());
		active.setStageRank(backup.getStageRank());
		active.setThrowoutCarryOver(backup.getThrowoutCarryOver());
		active.setTiebreakCarryOver(backup.getTiebreakCarryOver());

		super.restore(active, backup);
	}

	private void updateEnabled() {
		// if stage's throwouts are none, throwoutcarryover should be none and disabled

		// if stage's throwouts carryover, then stage and prevstage should have same throwout scheme
		
		// these may not all be implemented yet
		
	}

	@Override public void updateFields() {
		
		fRegatta = JavaScoreProperties.getRegatta();
		if (fRegatta != null) fScoring = (MultiStageScoring) fRegatta.getScoringManager();

		updateComboPreviousStage();
		
		if (fStage != null) {
			fTextName.setText( fStage.getName());
			fCheckIsQualifying.setSelected( fStage.isCombinedQualifying());
			fTextRank.setText( Integer.toString(fStage.getStageRank()));
			if (fStage.getPrevStage() == null) fComboPreviousStage.setSelectedItem(NO_STAGE);
			else fComboPreviousStage.setSelectedItem( fStage.getPrevStage());
			fComboScoreCarryOver.setSelectedItem( fStage.getScoreCarryOver());
			fComboThrowoutCarryOver.setSelectedItem(fStage.getThrowoutCarryOver());
			fComboTiebreakCarryOver.setSelectedItem(fStage.getTiebreakCarryOver());
			fScoringPanel.setObject( fStage);
			fDivisionPanel.setStage(fStage);
		} else {
			fTextName.setText("");
			fTextRank.setText("");
			fCheckIsQualifying.setSelected(false);
			fComboPreviousStage.setSelectedItem(null);
			fComboScoreCarryOver.setSelectedItem(null);
			fComboThrowoutCarryOver.setSelectedItem(null);
			fComboTiebreakCarryOver.setSelectedItem(null);
			fScoringPanel.setObject( null);
			fDivisionPanel.setStage(null);
		}
		

		this.revalidate();
		updateEnabled();
	}

	public void vetoableChange(PropertyChangeEvent de) throws PropertyVetoException {
		updateFields();
	}

	public void propertyChange(PropertyChangeEvent ev) {
		try {vetoableChange(ev); } catch (Exception e) {}
	}

	JTextFieldSelectAll fTextName;
	JCheckBox fCheckIsQualifying;
	JComboBox fComboPreviousStage;
	JComboBox<ScoreCarryOver> fComboScoreCarryOver;
	JComboBox fComboThrowoutCarryOver;
	JComboBox fComboTiebreakCarryOver;
	JTextFieldSelectAll fTextRank;
	
	PanelScoringOptions fScoringPanel;
	PanelStageDivisions fDivisionPanel;
	
	public void addFields() {
		Util.setTesting(true);
		HelpManager.getInstance().registerHelpTopic(this, "stage");
		setLayout(new GridBagLayout());
		java.awt.Insets insets = new java.awt.Insets(2, 2, 2, 2);
		setGridBagInsets(insets);

		setTitle(res.getString("StageTitle"));

		JPanel topleft = new JPanel( new GridBagLayout());
		JPanel topright = new JPanel( new GridBagLayout());
		
		gridbagAdd(this, topleft, 0, 0, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
		gridbagAdd(this, topright, 1, 0, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
		
		int row = 0;
		gridbagAdd( topleft, new JLabel(res.getString("StageLabelName")), 0, row, 1, GridBagConstraints.EAST,
			GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll(12);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("StageLabelNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "stage.fTextName");
		gridbagAdd( topleft, fTextName, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);

		row++;
		fCheckIsQualifying = new JCheckBox(res.getString("StageLabelIsQualifying"));
		fCheckIsQualifying.setName("fCheckIsQualifying");
		fCheckIsQualifying.setToolTipText(res.getString("StageLabelIsQualifyingToolTip"));
		HelpManager.getInstance().registerHelpTopic(fCheckIsQualifying, "stage.fCheckIsQualifying");
		gridbagAdd( topleft, fCheckIsQualifying, 0, row, 2, GridBagConstraints.EAST, 
				GridBagConstraints.NONE);

		row++;
		gridbagAdd( topleft, new JLabel(res.getString("StageLabelRank")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextRank = new JTextFieldSelectAll(12);
		fTextRank.setName("fTextRank");
		fTextRank.setToolTipText(res.getString("StageLabelRankToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextRank, "stage.fTextRank");
		gridbagAdd( topleft, fTextRank, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);

		
		row = 0;
		gridbagAdd(topright, new JLabel(res.getString("StageLabelPreviousStage")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboPreviousStage = new JComboBox();
		fComboPreviousStage.setName("fComboPreviousStage");

		fComboPreviousStage.setToolTipText(res.getString("StageLabelPreviousStageToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboPreviousStage, "stage.fComboPreviousStage");
		gridbagAdd( topright, fComboPreviousStage, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);
		
		row++;
		gridbagAdd(topright, new JLabel(res.getString("StageLabelScoreCarryOver")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboScoreCarryOver = new JComboBox<ScoreCarryOver>();
		fComboScoreCarryOver.setName("fComboScoreCarryOver");

		fComboScoreCarryOver.setToolTipText(res.getString("StageLabelScoreCarryOverToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboScoreCarryOver, "stage.fComboScoreCarryOver");
		gridbagAdd( topright, fComboScoreCarryOver, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fComboScoreCarryOver.setModel( new DefaultComboBoxModel<ScoreCarryOver>( ScoreCarryOver.values()));
		
		row++;
		gridbagAdd(topright, new JLabel(res.getString("StageLabelThrowoutCarryOver")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboThrowoutCarryOver = new JComboBox();
		fComboThrowoutCarryOver.setName("fComboThrowoutCarryOver");

		fComboThrowoutCarryOver.setToolTipText(res.getString("StageLabelThrowoutCarryOverToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboThrowoutCarryOver, "stage.fComboThrowoutCarryOver");
		gridbagAdd( topright, fComboThrowoutCarryOver, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fComboThrowoutCarryOver.setModel( new DefaultComboBoxModel<ThrowoutCarryOver>( ThrowoutCarryOver.values()));

		row++;
		gridbagAdd(topright, new JLabel(res.getString("StageLabelTiebreakCarryOver")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboTiebreakCarryOver = new JComboBox();
		fComboTiebreakCarryOver.setName("fComboTiebreakCarryOver");

		fComboTiebreakCarryOver.setToolTipText(res.getString("StageLabelTiebreakCarryOverToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboTiebreakCarryOver, "stage.fComboTiebreakCarryOver");
		gridbagAdd( topright, fComboTiebreakCarryOver, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fComboTiebreakCarryOver.setModel( new DefaultComboBoxModel<TiebreakCarryOver>( TiebreakCarryOver.values()));
		
		fScoringPanel = new PanelScoringOptions( this.getEditorParent());	
		gridbagAdd(this, fScoringPanel, 0, 1, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH);
		
		fDivisionPanel = new PanelStageDivisions();
		fScoringPanel.addTabbedPanel( res.getString("StageDivisionLabel"), fDivisionPanel);
	}
	
	@Override public void start() {
		fScoringPanel.startUp();
		fDivisionPanel.startUp();
		fTextName.addActionListener(this);
		fTextRank.addActionListener(this);
		fComboPreviousStage.addActionListener(this);
		fCheckIsQualifying.addActionListener(this);
		fComboScoreCarryOver.addActionListener(this);
		fComboThrowoutCarryOver.addActionListener(this);
		fComboTiebreakCarryOver.addActionListener(this);
	}
	
	@Override public void stop() {
		fScoringPanel.shutDown();
		fDivisionPanel.shutDown();
		fTextName.removeActionListener(this);
		fTextRank.removeActionListener(this);
		fComboPreviousStage.removeActionListener(this);
		fCheckIsQualifying.removeActionListener(this);
		fComboScoreCarryOver.removeActionListener(this);
		fComboThrowoutCarryOver.removeActionListener(this);
		fComboTiebreakCarryOver.removeActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {

		Object object = event.getSource();
		if (object == fTextName) fTextName_actionPerformed( event);
		else if (object == fTextRank) fTextRank_actionPerformed( event);
		else if (object == fComboPreviousStage) fComboPreviousStage_actionPerformed( event);
		else if (object == fCheckIsQualifying) fCheckIsQualifying_actionPerformed( event);
		else if (object == fComboScoreCarryOver) fComboScoreCarryOver_actionPerformed( event);
		else if (object == fComboThrowoutCarryOver) fComboThrowoutCarryOver_actionPerformed( event);
		else if (object == fComboTiebreakCarryOver) fComboTiebreakCarryOver_actionPerformed( event);

	}

	private void fTextName_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		String item = (String) fTextName.getText();
		fStage.setName( item);
	}

	private void fTextRank_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		String item = (String) fTextRank.getText();
		int oldRank = fStage.getStageRank();
		try {
			int order = Integer.parseInt(item);
			fStage.setStageRank( order);
		} catch (Exception e) {
			fTextRank.setText( Integer.toString(oldRank));
		}

	}

	private void fCheckIsQualifying_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		Boolean b = fCheckIsQualifying.isSelected();
		fStage.setCombinedQualifying(b);
		updateScoringOptions();

	}

	private void fComboScoreCarryOver_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		ScoreCarryOver item = (ScoreCarryOver) fComboScoreCarryOver.getSelectedItem();
		fStage.setScoreCarryOver( item);
		updateEnabled();

	}

	private void fComboTiebreakCarryOver_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		TiebreakCarryOver item = (TiebreakCarryOver) fComboTiebreakCarryOver.getSelectedItem();
		fStage.setTiebreakCarryOver( item);
		updateEnabled();

	}

	private void fComboThrowoutCarryOver_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		ThrowoutCarryOver item = (ThrowoutCarryOver) fComboThrowoutCarryOver.getSelectedItem();
		fStage.setThrowoutCarryOver( item);
		updateEnabled();
	}

	private Stage NO_STAGE;
	private void fComboPreviousStage_actionPerformed(ActionEvent event) {
		if (fStage == null) return;
		Stage ps = (Stage) fComboPreviousStage.getSelectedItem();
		if (ps == NO_STAGE) fStage.setPrevStage( null);
		else fStage.setPrevStage( ps);
	}


	private void updateScoringOptions() {
		
	}

	private void updateComboPreviousStage() {

//		Vector<String> stageNames = new Vector<String>();
//		stageNames.add( res.getString("StageNone"));
//		
//		if (fScoring != null) {
//			for (Stage s : fScoring.getStages()) {
//				if (s != fStage) {
//					stageNames.add( s.getName());
//				}
//			}
//		}
//		fComboPreviousStage.setModel(new DefaultComboBoxModel(stageNames));
		Vector<Stage> vstage = new Vector<Stage>();
		
		NO_STAGE = new Stage();
		NO_STAGE.setName("None");
		vstage.add(NO_STAGE);
		
		if (fScoring != null) {
			vstage.addAll( fScoring.getStages());
		}
		DefaultComboBoxModel cbm = new DefaultComboBoxModel(vstage);
		
		fComboPreviousStage.setModel(cbm);
	}


	public static void main(String[] args) {
		JavaScore.initializeEditors();

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		Regatta r = new Regatta();
		JavaScoreProperties.setRegatta(r);
		r.setMultistage(true);
		MultiStageScoring scoring = (MultiStageScoring) r.getScoringManager();
		
		Stage qual = new Stage( scoring);
		qual.setName("Qualifying");
		scoring.addStage(qual);
		
		Stage finals = new Stage( scoring);
		finals.setName("Finals");
		finals.setPrevStage(qual);
		scoring.addStage(finals);
		
		fFrame.setObject(scoring);
		fFrame.setVisible(true);

		LoggerFactory.getLogger( "main").debug( "name=" + finals.getName() + ", isQual="
				+ finals.isCombinedQualifying() + ", prev=" 
				+ ((finals.getPrevStage() == null) ? "null" : finals.getPrevStage().getName()) + ", scoreCarry="
				+ finals.getScoreCarryOver() + ", throwCarry=" + finals.getThrowoutCarryOver() + ", tieCarry=" 
				+ finals.getTiebreakCarryOver() + ", scoring="
				+ ((finals.getModel() == null) ? "null" : finals.getModel().getName()));
		
		System.exit(0);
	}

}
/**
 * $Log: PanelScoringOptions.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 */
