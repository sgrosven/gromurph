// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRaceAdvanced.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelRaceAdvanced extends BaseEditor<Race> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	public static final String RES_TITLE = res.getString("RaceAdvancedTitle");

	private static NumberFormat sNdNN = new DecimalFormat("#0.00");

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		DialogBaseEditor fFrame = new DialogBaseEditor();

		Race r = new Race();
		fFrame.setObject(r, new PanelRaceAdvanced( fFrame));
		fFrame.setVisible(true);
	}

	Race fRace;

	JCheckBox fCheckLongDistance;
	JCheckBox fCheckNonDiscardable;
	JCheckBox fCheckMedalRace;
	JCheckBox fCheckPursuitRace;
	JCheckBox fCheckPursuitShortened;

	JPanel fPanelTimeOnTime;
	JPanel fPanelComment = null;

	JRadioButton fRadioAverage;
	JRadioButton fRadioHeavy;
	JRadioButton fRadioLight;
	JRadioButton fRadioCustom;

	JTextPane fTextComment = null;
	JTextFieldSelectAll fTextWeight;
	JTextFieldSelectAll fTextBFactor;
	JTextFieldSelectAll fTextAFactor;

	public PanelRaceAdvanced(BaseEditorContainer parent) {
		super(parent);
		setMinimumSize(new Dimension(250, 300));
		addFields();
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == fTextWeight) fTextWeight_actionPerformed();
		else if (object == fCheckNonDiscardable) fCheckNonDiscardable_actionPerformed();
		else if (object == fCheckLongDistance) fCheckLongDistance_actionPerformed();
		else if (object == fCheckMedalRace) fCheckMedalRace_actionPerformed();
		else if (object == fCheckPursuitRace) fCheckPursuitRace_actionPerformed();
		else if (object == fCheckPursuitShortened) fCheckPursuitShortened_actionPerformed();
		else if (object == fTextAFactor) fTextAFactor_actionPerformed();
		else if (object == fTextBFactor) fTextBFactor_actionPerformed();
		else if (object == fRadioHeavy) fRadioHeavy_actionPerformed();
		else if (object == fRadioAverage) fRadioAverage_actionPerformed();
		else if (object == fRadioLight) fRadioLight_actionPerformed();
		else if (object == fRadioCustom) fRadioCustom_actionPerformed();

		if (getEditorParent() != null) getEditorParent().eventOccurred(this, event);
	}

	public void addFields() {
		setTitle(RES_TITLE);

		try {
			HelpManager.getInstance().registerHelpTopic(this, "race");
		}
		catch (NullPointerException e) {
			logger.error(" PanelRaceAdvanced.addFields - HelpManager generates NPE");
			// should only happen when testing
		}
		setLayout(new BorderLayout());

		JPanel panelCenter = new JPanel(new GridBagLayout());
		panelCenter.setName("panelCenter");

		add(panelCenter, BorderLayout.CENTER);

		int row = 0;
		fCheckLongDistance = new JCheckBox(res.getString("RaceLabelLongDistance"));
		fCheckLongDistance.setToolTipText(res.getString("RaceLabelLongDistanceToolTip"));
		fCheckLongDistance.setName("fCheckLongDistance");
		HelpManager.getInstance().registerHelpTopic(fCheckLongDistance, "race.fCheckLongDistance");
		gridbagAdd(panelCenter, fCheckLongDistance, 0, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		row++;
		fCheckPursuitRace = new JCheckBox(res.getString("PursuitRaceLabel"));
		fCheckPursuitRace.setToolTipText(res.getString("PursuitRaceLabelToolTip"));
		fCheckPursuitRace.setName("fCheckPursuitRace");
		HelpManager.getInstance().registerHelpTopic(fCheckPursuitRace, "race.fCheckPursuitRace");
		gridbagAdd(panelCenter, fCheckPursuitRace, 0, row, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		fCheckPursuitShortened = new JCheckBox(res.getString("PursuitShortenedLabel"));
		fCheckPursuitShortened.setToolTipText(res.getString("PursuitShortenedLabelToolTip"));
		fCheckPursuitShortened.setName("fCheckPursuitShortened");
		HelpManager.getInstance().registerHelpTopic(fCheckPursuitShortened, "race.fCheckPursuitShortened");
		gridbagAdd(panelCenter, fCheckPursuitShortened, 1, row, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		row++;
		fCheckMedalRace = new JCheckBox(res.getString("RaceLabelMedalRace"));
		fCheckMedalRace.setToolTipText(res.getString("RaceLabelMedalRaceToolTip"));
		fCheckMedalRace.setName("fCheckMedalRace");
		HelpManager.getInstance().registerHelpTopic(fCheckMedalRace, "race.fCheckMedalRace");
		gridbagAdd(panelCenter, fCheckMedalRace, 0, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		row++;
		fCheckNonDiscardable = new JCheckBox(res.getString("RaceLabelNonDiscardable"));
		fCheckNonDiscardable.setToolTipText(res.getString("RaceLabelNonDiscardableToolTip"));
		fCheckNonDiscardable.setName("fCheckNonDiscardable");
		HelpManager.getInstance().registerHelpTopic(fCheckNonDiscardable, "race.fCheckNonDiscardable");
		gridbagAdd(panelCenter, fCheckNonDiscardable, 0, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		row++;
		JPanel panelWeight = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelWeight.add(new JLabel(res.getString("RaceLabelWeight")));
		fTextWeight = new JTextFieldSelectAll(5);
		fTextWeight.setToolTipText(res.getString("RaceLabelWeightToolTip"));
		fTextWeight.setName("fTextWeight");
		HelpManager.getInstance().registerHelpTopic(fTextWeight, "race.fTextWeight");
		panelWeight.add(fTextWeight);
		gridbagAdd(panelCenter, panelWeight, 0, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		// Panel BFACTOR
		row++;
		fPanelTimeOnTime = addFieldsTimeOnTimePanel();
		gridbagAdd(panelCenter, fPanelTimeOnTime, 0, row, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));

		initializeListeners();
	}

	private JPanel addFieldsTimeOnTimePanel() {
		
		JPanel toTPanel = new JPanel(new GridBagLayout());
		toTPanel.setBorder(BorderFactory.createTitledBorder(res.getString("RaceLabelTimeOnTimePanel")));
		toTPanel.setName("ToTPanel");

		int row = 0;
		gridbagAdd( toTPanel, new JLabel(res.getString("RaceLabelAFactor")), 
				0, row++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		fTextAFactor = new JTextFieldSelectAll(5);
		fTextAFactor.setToolTipText(res.getString("RaceLabelAFactorToolTip"));
		fTextAFactor.setName("fTextAFactor");
		HelpManager.getInstance().registerHelpTopic(fTextAFactor, "race.fTextAFactor");
		gridbagAdd( toTPanel, fTextAFactor, 0, row++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		
		gridbagAdd( toTPanel, new JLabel(res.getString("RaceLabelBFactor")), 
				0, row++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		fTextBFactor = new JTextFieldSelectAll(5);
		fTextBFactor.setToolTipText(res.getString("RaceLabelBFactorToolTip"));
		fTextBFactor.setName("fTextBFactor");
		HelpManager.getInstance().registerHelpTopic(fTextBFactor, "race.fTextBFactor");
		gridbagAdd( toTPanel, fTextBFactor, 0, row++, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		
		JPanel bPanel = new JPanel( new GridBagLayout());
		bPanel.setToolTipText(res.getString("RaceLabelBFactorPanelToolTip"));
		bPanel.setBorder(BorderFactory.createTitledBorder(res.getString("RaceLabelBFactorPanel")));
		bPanel.setName("bPanel");
		gridbagAdd( toTPanel, bPanel, 1, 0, 1, row, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));

		ButtonGroup groupBFactor = new ButtonGroup();
		fRadioLight = new JRadioButton(res.getString("RaceLabelBFactorLight"));
		fRadioLight.setMnemonic(res.getString("RaceLabelBFactorLightMnemonic").charAt(0));
		fRadioLight.setToolTipText(res.getString("RaceLabelBFactorLightToolTip"));
		fRadioLight.setName("fRadioLight");
		groupBFactor.add(fRadioLight);
		gridbagAdd(bPanel, fRadioLight, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioLight, "race.groupBFactor");

		fRadioAverage = new JRadioButton(res.getString("RaceLabelBFactorAverage"));
		fRadioAverage.setMnemonic(res.getString("RaceLabelBFactorAverageMnemonic").charAt(0));
		fRadioAverage.setToolTipText(res.getString("RaceLabelBFactorAverageToolTip"));
		fRadioAverage.setName("fRadioAverage");
		groupBFactor.add(fRadioAverage);
		gridbagAdd(bPanel, fRadioAverage, 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioAverage, "race.groupBFactor");

		fRadioHeavy = new JRadioButton(res.getString("RaceLabelBFactorHeavy"));
		fRadioHeavy.setMnemonic(res.getString("RaceLabelBFactorHeavyMnemonic").charAt(0));
		fRadioHeavy.setToolTipText(res.getString("RaceLabelBFactorHeavyToolTip"));
		fRadioHeavy.setName("fRadioHeavy");
		groupBFactor.add(fRadioHeavy);
		gridbagAdd(bPanel, fRadioHeavy, 0, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioHeavy, "race.groupBFactor");

		fRadioCustom = new JRadioButton(res.getString("RaceLabelBFactorCustom"));
		fRadioCustom.setMnemonic(res.getString("RaceLabelBFactorCustomMnemonic").charAt(0));
		fRadioCustom.setToolTipText(res.getString("RaceLabelBFactorCustomToolTip"));
		fRadioCustom.setName("fRadioCustom");
		groupBFactor.add(fRadioCustom);
		gridbagAdd(bPanel, fRadioCustom, 0, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioCustom, "race.groupBFactor");

		return toTPanel;
	}

	@Override public void exitOK() {
		super.exitOK();
		JavaScore.backgroundSave();
		JavaScore.subWindowClosing();
	}

	void fCheckLongDistance_actionPerformed() {
		fRace.setLongDistance(fCheckLongDistance.isSelected());
		SailTime.setLongDistance(fRace.isLongDistance());
	}

	void fCheckMedalRace_actionPerformed() {
		boolean isMedal = fCheckMedalRace.isSelected();

		fRace.setMedalRace(isMedal);
		updateMedalFields();
	}

	void fCheckPursuitRace_actionPerformed() {
		boolean isP = fCheckPursuitRace.isSelected();

		fRace.setPursuit(isP);
		
		if (!isP) {
			fCheckPursuitShortened.setSelected(false);
			fRace.setPursuitShortened(false);
		}
		fCheckPursuitShortened.setEnabled( isP);	
	}

	void fCheckPursuitShortened_actionPerformed() {
		boolean isP = fCheckPursuitShortened.isSelected();

		fRace.setPursuitShortened(isP);
	}

	void fCheckNonDiscardable_actionPerformed() {
		fRace.setNonDiscardable(fCheckNonDiscardable.isSelected());
	}

	private void setStandardBFactor( int b) {
   		fRace.setBFactor(b);
   		fTextBFactor.setText( Integer.toString(b));
		fTextBFactor.setEnabled( false);
	}
	public void fRadioAverage_actionPerformed() {
		setStandardBFactor( RatingPhrfTimeOnTime.BFACTOR_AVERAGE);
	}

	public void fRadioHeavy_actionPerformed() {
		setStandardBFactor( RatingPhrfTimeOnTime.BFACTOR_HEAVY);
	}

	public void fRadioLight_actionPerformed() {
		setStandardBFactor( RatingPhrfTimeOnTime.BFACTOR_LIGHT);
	}

	public void fRadioCustom_actionPerformed() {
		fTextBFactor.setEnabled(true);
	}

	void fTextAFactor_actionPerformed() {
		String old = Integer.toString(fRace.getAFactor());
		try {
			String text = fTextAFactor.getText();
			if (fTextAFactor.getText().length() > 0) {
				int num = Integer.parseInt(text);
				if (num < 0) throw new Exception(res.getString("RaceMessageInvalidAFactor"));
				fRace.setAFactor(num);
			}
		}
		catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			sb.append(res.getString("RaceMessageInvalidAFactor"));
			sb.append(NEWLINE);
			sb.append(NEWLINE);
			sb.append(e.toString());
			JOptionPane.showMessageDialog(this, sb.toString());
			fTextAFactor.setText(old);
			//fTextWeight.requestFocusInWindow();
		}
	}

	void fTextBFactor_actionPerformed() {
		String old = Integer.toString(fRace.getBFactor());
		try {
			String text = fTextBFactor.getText();
			if (fTextBFactor.getText().length() > 0) {
				int num = Integer.parseInt(text);
				if (num < 0) throw new Exception(res.getString("RaceMessageInvalidBFactor"));
				fRace.setBFactor(num);
			}
		}
		catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			sb.append(res.getString("RaceMessageInvalidBFactor"));
			sb.append(NEWLINE);
			sb.append(NEWLINE);
			sb.append(e.toString());
			JOptionPane.showMessageDialog(this, sb.toString());
			fTextBFactor.setText(old);
			//fTextWeight.requestFocusInWindow();
		}
	}

	void fTextWeight_actionPerformed() {
		String old = Double.toString(fRace.getWeight());
		try {
			String text = fTextWeight.getText();
			if (fTextWeight.getText().length() > 0) {
				Number num = sNdNN.parse(text);
				fRace.setWeight(num.doubleValue());
			}
		}
		catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			sb.append(res.getString("RaceMessageInvalidWeight"));
			sb.append(NEWLINE);
			sb.append(NEWLINE);
			sb.append(e.toString());
			JOptionPane.showMessageDialog(this, sb.toString());
			fTextWeight.setText(old);
			//fTextWeight.requestFocusInWindow();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		
	}

	@Override public void restore(Race a, Race b) {
		if (a == b) return;
		Race active = (Race) a;
		Race backup = (Race) b;

		active.setBFactor(backup.getBFactor());
		active.setLongDistance(backup.isLongDistance());
		active.setNonDiscardable(backup.isNonDiscardable());
		active.setWeight(backup.getWeight());
		active.setPursuit(backup.isPursuit());

		super.restore(active, backup);
	}

	@Override public void setObject(Race inObj) {
		if (inObj == fRace) return;

		fRace = inObj;

		super.setObject(inObj);

	}

	@Override public void start() {
		updateFields();
		fTextWeight.addActionListener(this);
		fCheckLongDistance.addActionListener(this);
		fCheckNonDiscardable.addActionListener(this);
		fCheckMedalRace.addActionListener(this);
		fCheckPursuitRace.addActionListener(this);
		fCheckPursuitShortened.addActionListener(this);

		fRadioHeavy.addActionListener(this);
		fRadioLight.addActionListener(this);
		fRadioAverage.addActionListener(this);
		fRadioCustom.addActionListener(this);

		if (fRace != null) fRace.addPropertyChangeListener(this);
	}

	private void initializeListeners() {
	}

	@Override public void stop() {
		if (fRace != null) fRace.removePropertyChangeListener(this);

		fTextWeight.removeActionListener(this);
		fCheckLongDistance.removeActionListener(this);
		fCheckNonDiscardable.removeActionListener(this);
		fCheckMedalRace.removeActionListener(this);
		fCheckPursuitRace.removeActionListener(this);
		fCheckPursuitShortened.removeActionListener(this);

		fRadioHeavy.removeActionListener(this);
		fRadioLight.removeActionListener(this);
		fRadioAverage.removeActionListener(this);
		fRadioCustom.removeActionListener(this);

	}

	public void updateMedalFields() {

		if (fRace != null) {
			fCheckMedalRace.setSelected(fRace.isMedalRace());
			fCheckNonDiscardable.setSelected(fRace.isNonDiscardable());
			fTextWeight.setText(sNdNN.format(fRace.getWeight()));

			fCheckNonDiscardable.setEnabled(!fRace.isMedalRace());
			fTextWeight.setEnabled(!fRace.isMedalRace());
		} else {
			fCheckNonDiscardable.setSelected(false);
			fCheckMedalRace.setSelected(false);
			fRadioAverage.setSelected(true);
		}
	}

	@Override public void updateFields() {

		if (fRace != null) {
			fCheckLongDistance.setSelected(fRace.isLongDistance());
			SailTime.setLongDistance(fRace.isLongDistance());

			fCheckPursuitRace.setSelected( fRace.isPursuit());
			fCheckPursuitShortened.setSelected( fRace.isPursuitShortened());
			
			int bf = fRace.getBFactor();
			fTextBFactor.setEnabled(false);
			switch (bf) {
    			case RatingPhrfTimeOnTime.BFACTOR_AVERAGE:
    				fRadioAverage.setSelected(true);
    				break;
    			case RatingPhrfTimeOnTime.BFACTOR_HEAVY:
    				fRadioHeavy.setSelected(true);
    				break;
    			case RatingPhrfTimeOnTime.BFACTOR_LIGHT:
    				fRadioLight.setSelected(true);
    				break;
    			default:
    				fRadioCustom.setSelected(true);
    				fTextBFactor.setEnabled(true);
    				break;
			}
			fTextBFactor.setText( Integer.toString(bf));
			fTextAFactor.setText( Integer.toString( fRace.getAFactor()));
			
		} else {
			fTextBFactor.setText( Integer.toString(RatingPhrfTimeOnTime.BFACTOR_AVERAGE));
			fTextBFactor.setEnabled(false);
			fTextAFactor.setText( Integer.toString(RatingPhrfTimeOnTime.AFACTOR_DEFAULT));
			fTextWeight.setText("1.00");
			fCheckLongDistance.setSelected(false);
			SailTime.setLongDistance(false);
			fCheckPursuitRace.setSelected( false);
			fCheckPursuitShortened.setSelected( false);
			fCheckPursuitShortened.setEnabled(false);

		}

		updateMedalFields();

	}
}
