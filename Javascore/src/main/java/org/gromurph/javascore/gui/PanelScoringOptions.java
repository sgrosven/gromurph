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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.scoring.ScoringModel;
import org.gromurph.javascore.model.scoring.ScoringOptions;
import org.gromurph.javascore.model.scoring.ScoringUtilities;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.javascore.model.scoring.StageScoringModel;
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
public class PanelScoringOptions extends BaseEditor<StageScoringModel> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	StageScoringModel fStageModel;
	ScoringOptions fOptions;

	private static String[] nthArray = new String[] { res.getString("GenNth1"), res.getString("GenNth2"),
			res.getString("GenNth3"), res.getString("GenNth4"), res.getString("GenNth5") };

	private static java.util.List<String> sTimeLimitPenalties;
	private static java.util.List<String> sTimeLimitMnemonics;

	static {
		sTimeLimitPenalties = new ArrayList<String>(5);
		sTimeLimitPenalties.add(res.getString("PenaltyDneDidNotFinish"));
		sTimeLimitPenalties.add(res.getString("PenaltyDnePlus1"));
		sTimeLimitPenalties.add(res.getString("PenaltyDnePlus2"));
		sTimeLimitPenalties.add(res.getString("PenaltyDneAverage"));
		sTimeLimitPenalties.add(res.getString("PenaltyTlePlus2MaxEntries"));

		sTimeLimitMnemonics = new ArrayList<String>(5);
		sTimeLimitMnemonics.add(res.getString("PenaltyDneDidNotFinishMnemonic"));
		sTimeLimitMnemonics.add(res.getString("PenaltyDnePlus1Mnemonic"));
		sTimeLimitMnemonics.add(res.getString("PenaltyDnePlus2Mnemonic"));
		sTimeLimitMnemonics.add(res.getString("PenaltyDneAverageMnemonic"));
		sTimeLimitMnemonics.add(res.getString("PenaltyTlePlus2MaxEntriesMnemonic"));
	}

	public PanelScoringOptions(BaseEditorContainer parent) {
		super(parent);
		addFields();
		setPreferredSize(new Dimension(400, 300));
	}

	@Override public void setObject(StageScoringModel inObj) {
		super.setObject(inObj);

		setScoringOptions( inObj);

		updateFields();
	}
	
	private void setScoringOptions( StageScoringModel inObj) {
		fStageModel = inObj;
		if (inObj == null) fOptions = null;
		else fOptions= inObj.getModel().getOptions();
	}

	private void setScoringName(String scoringName) {
		ScoringModel scoring = ScoringUtilities.createScoringModel(scoringName);
		setScoringSystem(scoring);
	}

	private void setScoringSystem( ScoringModel scoring) {
		if (fOptions == scoring) return;

		if (fOptions != null) fOptions.removePropertyChangeListener(this);
		
		fStageModel.setModel(scoring);
		ScoringOptions holdOptions = fOptions;
		fOptions = (ScoringOptions) fStageModel.getModel().getOptions();
		fOptions.setAttributes( holdOptions);
		
		if (isVisible() && fOptions != null) {
			fOptions.addPropertyChangeListener(this);
			fRadioTieA82Only.setEnabled(fOptions.canUserChangeTiebreaker());
			fRadioTieB8.setEnabled(fOptions.canUserChangeTiebreaker());
			fRadioTieNobreaker.setEnabled(fOptions.canUserChangeTiebreaker());
		}
	}

	@Override public void restore(StageScoringModel a, StageScoringModel b) {
		if (a == b) return;

		ScoringOptions active = (ScoringOptions) a.getModel().getOptions();
		ScoringOptions backup = (ScoringOptions) b.getModel().getOptions();

		active.setThrowoutScheme(backup.getThrowoutScheme());
		active.getThrowouts().clear();
		active.getThrowouts().addAll(backup.getThrowouts());
		active.setThrowoutPerX(backup.getThrowoutPerX());
		active.setThrowoutBestX(backup.getThrowoutBestX());
		active.setTimeLimitPenalty(backup.getTimeLimitPenalty());
		active.setCheckinPercent(backup.getCheckinPercent());
		active.setLongSeries(backup.isLongSeries());

		super.restore(a, b);
	}

	private void updateEnabled() {
		if (fRadioThrowoutByNumRaces.isSelected()) {
			fTextThrowoutBestX.setEnabled(false);
			fTextThrowoutPerX.setEnabled(false);
			for (int i = 0; i < fOptions.getThrowouts().size(); i++) {
				fTextThrowouts.get(i).setEnabled(true);
			}
		} else if (fRadioThrowoutPerXRaces.isSelected()) {
			fTextThrowoutBestX.setEnabled(false);
			fTextThrowoutPerX.setEnabled(true);
			for (int i = 0; i < fOptions.getThrowouts().size(); i++) {
				fTextThrowouts.get(i).setEnabled(false);
			}
		} else if (fRadioThrowoutBestXRaces.isSelected()) {
			fTextThrowoutBestX.setEnabled(true);
			fTextThrowoutPerX.setEnabled(false);
			for (int i = 0; i < fOptions.getThrowouts().size(); i++) {
				fTextThrowouts.get(i).setEnabled(false);
			}
		} else if (fRadioThrowoutNone.isSelected()) {
			fTextThrowoutBestX.setEnabled(false);
			fTextThrowoutPerX.setEnabled(false);
			for (int i = 0; i < fOptions.getThrowouts().size(); i++) {
				fTextThrowouts.get(i).setEnabled(false);
			}
		}
		
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg != null && reg.isMultistage() && !fRadioThrowoutNone.isSelected()) {
			fCheckEntriesLargestDivision.setEnabled(true);
		} else {
			fCheckEntriesLargestDivision.setEnabled(false);
		}

	}

	@Override public void updateFields() {

		Regatta reg = JavaScoreProperties.getRegatta();
		if (fOptions != null) {
			
			String scoringName = fStageModel.getModel().getName();
			fComboScoringSystem.setSelectedItem(scoringName);

			fTextCheckin.setText(Integer.toString(fOptions.getCheckinPercent()));
			fTextPointsForFirst.setText( Double.toString( fOptions.getFirstPlacePoints()));

			int tlm = fOptions.getTimeLimitPenalty();
			fRadioTimeLimits.get(tlm).setSelected(true);

			for (int i = 0; i < fOptions.getThrowouts().size(); i++) {
				// note this does not check mutual lengths of arrays
				// presumes equal length arrays!
				fTextThrowouts.get(i).setText(fOptions.getThrowouts().get(i).toString());
			}
			fTextThrowoutPerX.setText(Integer.toString(fOptions.getThrowoutPerX()));
			fTextThrowoutBestX.setText(Integer.toString(fOptions.getThrowoutBestX()));
			switch (fOptions.getThrowoutScheme()) {
				case Constants.THROWOUT_NONE:
					fRadioThrowoutNone.setSelected(true);
					break;
				case Constants.THROWOUT_BESTXRACES:
					fRadioThrowoutBestXRaces.setSelected(true);
					break;
				case Constants.THROWOUT_PERXRACES:
					fRadioThrowoutPerXRaces.setSelected(true);
					break;
				case Constants.THROWOUT_BYNUMRACES:
					fRadioThrowoutByNumRaces.setSelected(true);
					break;
				default:
					fRadioThrowoutByNumRaces.setSelected(true);
					break;
			}

			fRadioLongSeriesYes.setSelected(fOptions.isLongSeries());
			fRadioTieRrsDefault.setSelected(fOptions.getTiebreaker() == Constants.TIE_RRS_DEFAULT);
			fRadioTieA82Only.setSelected(fOptions.getTiebreaker() == Constants.TIE_RRS_A82_ONLY);
			fRadioTieB8.setSelected(fOptions.getTiebreaker() == Constants.TIE_RRS_B8);
			fRadioTieNobreaker.setSelected(fOptions.getTiebreaker() == Constants.TIE_NOTIEBREAKER);

			if (reg != null && reg.isMultistage()) {
				fCheckEntriesLargestDivision.setEnabled(true);
				fCheckEntriesLargestDivision.setSelected(fOptions.isEntriesLargestDivision());
			} else {
				fCheckEntriesLargestDivision.setEnabled(false);
				fCheckEntriesLargestDivision.setSelected(false);
			}

			fRadioTieA82Only.setEnabled(fOptions.canUserChangeTiebreaker());
			fRadioTieB8.setEnabled(fOptions.canUserChangeTiebreaker());
			fRadioTieNobreaker.setEnabled(fOptions.canUserChangeTiebreaker());
			
		} else {
			fComboScoringSystem.setSelectedItem(0);
			fTextCheckin.setText(EMPTY);
			fTextPointsForFirst.setText("1.0");
			fRadioTimeLimits.get(0).setSelected(true);
			for (int i = 0; i < fTextThrowouts.size(); i++) {
				fTextThrowouts.get(i).setText(EMPTY);
			}
			fRadioLongSeriesNo.setSelected(true);
			fRadioTieRrsDefault.setSelected(true);
			fRadioTieA82Only.setEnabled(false);
			fRadioTieB8.setEnabled(false);
			fRadioTieNobreaker.setEnabled(false);
		}
		updateEnabled();
	}

	public void vetoableChange(PropertyChangeEvent de) throws PropertyVetoException {
		updateFields();
	}

	public void propertyChange(PropertyChangeEvent ev) {
		try {
			vetoableChange(ev);
		} catch (Exception e) {}
	}

	JCheckBox fCheckEntriesLargestDivision;
	JTextFieldSelectAll fTextPointsForFirst;
	JTextFieldSelectAll fTextCheckin;
	JTextFieldSelectAll fTextThrowoutPerX;
	JTextFieldSelectAll fTextThrowoutBestX;
	ButtonGroup fGroupTimeLimits;
	java.util.List<JRadioButton> fRadioTimeLimits;
	java.util.List<JTextFieldSelectAll> fTextThrowouts;
	JRadioButton fRadioLongSeriesYes;
	JRadioButton fRadioLongSeriesNo;
	
	JTabbedPane fTabPanel;

	public void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "lowpoint");
		setLayout(new BorderLayout());
		java.awt.Insets insets = new java.awt.Insets(2, 2, 2, 2);
		setGridBagInsets(insets);

		setTitle(res.getString("ScoringOptionsTitle"));

		fTabPanel = new JTabbedPane();
		fTabPanel.setName("tabPane");
		this.add(fTabPanel, BorderLayout.CENTER);

		JPanel pointsPanel = createPointsPanel();
		pointsPanel.setName("penaltyPanel");

		JPanel generalPanel = createGeneralPanel();
		generalPanel.setName("generalPanel");

		JPanel throwoutPanel = createThrowoutPanel();
		throwoutPanel.setName("throwoutPanel");

		JPanel tiebreakerPanel = createTiebreakerPanel();
		tiebreakerPanel.setName("tiebreakerPanel");

		addTabbedPanel( res.getString("ScoringOptionsTabGeneral"), generalPanel); // tab
		// 0
		addTabbedPanel( res.getString("ScoringOptionsTabPenalties"), pointsPanel); // tab
		// 1
		addTabbedPanel( res.getString("ScoringOptionsTabThrowouts"), throwoutPanel); // tab
		// 2
		addTabbedPanel( res.getString("ScoringOptionsTabTiebreakers"), tiebreakerPanel); // tab
		// 3
	}

	public JTabbedPane getTabbedPanel() {
		return fTabPanel;
	}
	public void addTabbedPanel( String label, JPanel panel) {
		fTabPanel.add( label, panel);
	}
	
	JComboBox fComboScoringSystem;

	private JPanel createGeneralPanel() {
		JPanel generalPanel = new JPanel(new GridBagLayout());
		setGridBagInsets(new Insets(1, 1, 1, 1));

		int row = 0;
		gridbagAdd(generalPanel, new JLabel(res.getString("RegattaLabelScoring")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fComboScoringSystem = new JComboBox(new DefaultComboBoxModel(ScoringUtilities.getSupportedModels()));
		fComboScoringSystem.setSelectedIndex(0);
		fComboScoringSystem.setName("fComboScoringSystem");
		fComboScoringSystem.setToolTipText(res.getString("RegattaLabelScoringSustemToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboScoringSystem, "regatta.fComboScoringSystem");
		gridbagAdd(generalPanel, fComboScoringSystem, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		JLabel longseries = new JLabel(res.getString("LowPointLabelLongSeries"));
		longseries.setToolTipText(res.getString("LowPointLabelLongSeriesToolTip"));
		gridbagAdd(generalPanel, longseries, 0, row, 3, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		JPanel yesno = new JPanel(new FlowLayout());
		gridbagAdd(generalPanel, yesno, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		ButtonGroup groupLongSeries = new ButtonGroup();
		fRadioLongSeriesNo = new JRadioButton(resUtil.getString("No"));
		fRadioLongSeriesNo.setName("fRadioLongSeriesNo");
		fRadioLongSeriesNo.setToolTipText(res.getString("LowPointLabelLongSeriesToolTip"));
		fRadioLongSeriesNo.setMnemonic(resUtil.getString("NoMnemonic").charAt(0));
		groupLongSeries.add(fRadioLongSeriesNo);
		HelpManager.getInstance().registerHelpTopic(fRadioLongSeriesNo, "lowpoint.fRadioLongSeries");
		yesno.add(fRadioLongSeriesNo);

		fRadioLongSeriesYes = new JRadioButton(resUtil.getString("Yes"));
		fRadioLongSeriesYes.setName("fRadioLongSeriesYes");
		fRadioLongSeriesYes.setToolTipText(res.getString("LowPointLabelLongSeriesToolTip"));
		fRadioLongSeriesYes.setMnemonic(resUtil.getString("YesMnemonic").charAt(0));
		HelpManager.getInstance().registerHelpTopic(fRadioLongSeriesYes, "lowpoint.fRadioLongSeries");
		groupLongSeries.add(fRadioLongSeriesYes);
		yesno.add(fRadioLongSeriesYes);

		row++;
		gridbagAdd(generalPanel, new JLabel(""), 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		return generalPanel;
	}

	private JPanel createPointsPanel() {

		JPanel limitPanel = new JPanel(new GridBagLayout());
		setGridBagInsets(new Insets(1, 1, 1, 1));

		limitPanel.setBorder(BorderFactory.createTitledBorder(res.getString("LowPointTitleTimeLimit")));

		ButtonGroup groupTimeLimits = new ButtonGroup();
		fRadioTimeLimits = new ArrayList<JRadioButton>(); // list of checkboxes
		// for time limit selection
		for (int i = 0; i < sTimeLimitPenalties.size(); i++) {
			String tleName = sTimeLimitPenalties.get(i);
			JRadioButton check = new JRadioButton(tleName);
			char tleMne = sTimeLimitMnemonics.get(i).charAt(0);
			check.setMnemonic(tleMne);
			check.setName( tleName);
			HelpManager.getInstance().registerHelpTopic(check, "lowpoint.fCheckTimeLimits");
			groupTimeLimits.add(check);
			fRadioTimeLimits.add(check);
			gridbagAdd(limitPanel, check, 0, i, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
					5, 0, 5));
		}

		fCheckEntriesLargestDivision = new JCheckBox(res.getString("PenaltyEntriesLargestDivision"));
		fCheckEntriesLargestDivision.setName("fCheckEntriesLargestDivision");
		fCheckEntriesLargestDivision.setToolTipText(res.getString("PenaltyEntriesLargestDivisionToolTip"));
		HelpManager.getInstance().registerHelpTopic(fCheckEntriesLargestDivision,
				"lowpoint.fCheckEntriesLargestDivision");

		JPanel panelCnf = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		panelCnf.add(new JLabel(res.getString("PenaltyCNFLabel")));
		fTextCheckin = new JTextFieldSelectAll(5);
		fTextCheckin.setName("fTextCheckin");
		fTextCheckin.setToolTipText(res.getString("PenaltyCNFToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextCheckin, "lowpoint.fTextCheckin");
		panelCnf.add(fTextCheckin);
		panelCnf.add(new JLabel("%"));

		JPanel panelFirstPlace = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		panelFirstPlace.add(new JLabel(res.getString("PointsForFirstLabel")));
		fTextPointsForFirst = new JTextFieldSelectAll(4);
		fTextPointsForFirst.setName("fTextPointsForFirst");
		fTextPointsForFirst.setToolTipText(res.getString("PointsForFirstToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextPointsForFirst, "lowpoint.fTextPointForFirst");
		panelFirstPlace.add(fTextPointsForFirst);

		JPanel penaltyPanel = new JPanel(new GridBagLayout());

		int row = 0;

		gridbagAdd(penaltyPanel, panelFirstPlace, 0, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
				3, 5, 3, 5));

		row++;
		gridbagAdd(penaltyPanel, limitPanel, 0, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 5, 1, 5));
		row++;
		gridbagAdd(penaltyPanel, fCheckEntriesLargestDivision, 0, row, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(1, 5, 1, 5));
		
		row++;
		gridbagAdd(penaltyPanel, panelCnf, 0, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
				1, 5, 1, 5));

		return penaltyPanel;
	}

	JRadioButton fRadioThrowoutByNumRaces;
	JRadioButton fRadioThrowoutPerXRaces;
	JRadioButton fRadioThrowoutBestXRaces;
	JRadioButton fRadioThrowoutNone;

	private JPanel createThrowoutPanel() {
		JPanel throwoutPanel = new JPanel(new GridBagLayout());
		// throwoutPanel.setBorder( BorderFactory.createTitledBorder(
		// res.getString("LowPointTitleThrowouts")));

		setGridBagInsets(new Insets(1, 1, 1, 1));

		ButtonGroup throwoutGroup = new ButtonGroup();

		fRadioThrowoutByNumRaces = new JRadioButton(res.getString("LowPointLabelThrowoutByNumRaces"));
		fRadioThrowoutByNumRaces.setMnemonic(res.getString("LowPointLabelThrowoutByNumRacesMnemonic").charAt(0));
		fRadioThrowoutByNumRaces.setName("fRadioThrowoutByNumRaces");
		HelpManager.getInstance().registerHelpTopic(fRadioThrowoutByNumRaces, "lowpoint.fRadioThrowoutByNumRaces");
		throwoutGroup.add(fRadioThrowoutByNumRaces);
		
		int row = 0;
		int fullrow = 6;
		gridbagAdd(throwoutPanel, fRadioThrowoutByNumRaces, 0, row, fullrow, GridBagConstraints.WEST, GridBagConstraints.NONE);

		// for now supporting up to 3 throwouts
		row++;
		JLabel tLabel = new JLabel(res.getString("LowPointLabelThrowoutNumber"));
		JLabel rLabel = new JLabel(res.getString("LowPointLabelMinRaces"));				
		
		gridbagAdd(throwoutPanel, new JLabel("   "), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridbagAdd(throwoutPanel, tLabel, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridbagAdd(throwoutPanel, new JLabel("   "), 0, row+1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridbagAdd(throwoutPanel, rLabel, 1, row+1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		gridbagAdd(throwoutPanel, new JLabel(""), 5, row+1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridbagAdd(throwoutPanel, new JLabel(""), 5, row+1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fTextThrowouts = new ArrayList<JTextFieldSelectAll>(); 
		
		// array of textfields, one per throwout
		//o  'throwout#'  '1'   '2'    '3'
		//o   min # races  [  ]  [  ]  [  ]
		for (int i = 0; i < 3; i++) {
			String si = Integer.toString(i+1);
			gridbagAdd(throwoutPanel, new JLabel(si), i+2, row, 1, GridBagConstraints.CENTER,
					GridBagConstraints.NONE);

			JTextFieldSelectAll text = new JTextFieldSelectAll(4);
			text.setName("fTextThrowout" + si);
			text.setToolTipText(MessageFormat.format(res.getString("LowPointLabelMinRacesToolTip"),
					new Object[] { nthArray[i] }));
			HelpManager.getInstance().registerHelpTopic(text, "lowpoint.fTextThrowouts");
			
			fTextThrowouts.add(text);
			gridbagAdd(throwoutPanel, text, i+2, row+1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		}
		row+= 2;
		
		fRadioThrowoutPerXRaces = new JRadioButton(res.getString("LowPointLabelThrowoutPerXRaces1"));
		fRadioThrowoutPerXRaces.setMnemonic(res.getString("LowPointLabelThrowoutPerXRacesMnemonic").charAt(0));
		fRadioThrowoutPerXRaces.setName("fRadioThrowoutPerXRaces");
		HelpManager.getInstance().registerHelpTopic(fRadioThrowoutPerXRaces, "lowpoint.fRadioThrowoutPerXRaces");
		throwoutGroup.add(fRadioThrowoutPerXRaces);
		fTextThrowoutPerX = new JTextFieldSelectAll(3);
		fTextThrowoutPerX.setName("fTextThrowoutPerX");
		HelpManager.getInstance().registerHelpTopic(fTextThrowoutPerX, "lowpoint.fRadioThrowoutPerXRaces");
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridbagAdd(throwoutPanel, panel, 0, row, fullrow, GridBagConstraints.WEST, GridBagConstraints.NONE);
		panel.add(fRadioThrowoutPerXRaces);
		panel.add(fTextThrowoutPerX);
		JLabel label = new JLabel(" " + res.getString("LowPointLabelThrowoutPerXRaces2"));
		label.setForeground(fRadioThrowoutPerXRaces.getForeground());
		panel.add(label);

		row++;
		fRadioThrowoutBestXRaces = new JRadioButton(res.getString("LowPointLabelThrowoutBestXRaces1"));
		fRadioThrowoutBestXRaces.setMnemonic(res.getString("LowPointLabelThrowoutBestXRacesMnemonic").charAt(0));
		fRadioThrowoutBestXRaces.setName("fRadioThrowoutBestXRaces");
		HelpManager.getInstance().registerHelpTopic(fRadioThrowoutBestXRaces, "lowpoint.fRadioThrowoutBestXRaces");
		throwoutGroup.add(fRadioThrowoutBestXRaces);
		fTextThrowoutBestX = new JTextFieldSelectAll(3);
		fTextThrowoutBestX.setName("fTextThrowoutBestX");
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridbagAdd(throwoutPanel, panel, 0, row, fullrow, GridBagConstraints.WEST, GridBagConstraints.NONE);
		panel.add(fRadioThrowoutBestXRaces);
		panel.add(fTextThrowoutBestX);
		HelpManager.getInstance().registerHelpTopic(fTextThrowoutBestX, "lowpoint.fRadioThrowoutBestXRaces");
		label = new JLabel(" " + res.getString("LowPointLabelThrowoutBestXRaces2"));
		label.setForeground(fRadioThrowoutPerXRaces.getForeground());
		panel.add(label);

		row++;
		fRadioThrowoutNone = new JRadioButton(res.getString("LowPointLabelThrowoutNone"));
		fRadioThrowoutNone.setMnemonic(res.getString("LowPointLabelThrowoutNoneMnemonic").charAt(0));
		fRadioThrowoutNone.setName("fRadioThrowoutNone");
		HelpManager.getInstance().registerHelpTopic(fRadioThrowoutBestXRaces, "lowpoint.fRadioThrowoutNone");
		throwoutGroup.add(fRadioThrowoutNone);
		gridbagAdd(throwoutPanel, fRadioThrowoutNone, 0, row, fullrow, GridBagConstraints.WEST, GridBagConstraints.NONE);

		
		return throwoutPanel;
	}

	JRadioButton fRadioTieRrsDefault;
	JRadioButton fRadioTieA82Only;
	JRadioButton fRadioTieB8;
	JRadioButton fRadioTieNobreaker;

	private JPanel createTiebreakerPanel() {
		JPanel tiePanel = new JPanel(new GridBagLayout());

		setGridBagInsets(new Insets(1, 1, 1, 1));

		ButtonGroup tieGroup = new ButtonGroup();

		fRadioTieRrsDefault = new JRadioButton(res.getString("LowPointLabelTieRrsDefault"));
		fRadioTieRrsDefault.setMnemonic(res.getString("LowPointLabelTieRrsDefaultMnemonic").charAt(0));
		fRadioTieRrsDefault.setName("fRadioTieRrsDefault");
		fRadioTieRrsDefault.setToolTipText(res.getString("LowPointToolTipTieRrsDefault"));
		HelpManager.getInstance().registerHelpTopic(fRadioTieRrsDefault, "lowpoint.fRadioTieRrsDefault");
		tieGroup.add(fRadioTieRrsDefault);
		int row = 0;
		gridbagAdd(tiePanel, fRadioTieRrsDefault, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fRadioTieA82Only = new JRadioButton(res.getString("LowPointLabelTieA82Only"));
		fRadioTieA82Only.setMnemonic(res.getString("LowPointLabelTieA82OnlyMnemonic").charAt(0));
		fRadioTieA82Only.setName("fRadioTieA82Only");
		fRadioTieA82Only.setToolTipText(res.getString("LowPointToolTipTieA82Only"));
		HelpManager.getInstance().registerHelpTopic(fRadioTieA82Only, "lowpoint.fRadioTieA82Only");
		tieGroup.add(fRadioTieA82Only);
		gridbagAdd(tiePanel, fRadioTieA82Only, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fRadioTieB8 = new JRadioButton(res.getString("LowPointLabelTieB8"));
		fRadioTieB8.setMnemonic(res.getString("LowPointLabelTieB8Mnemonic").charAt(0));
		fRadioTieB8.setName("fRadioTieB8");
		fRadioTieB8.setToolTipText(res.getString("LowPointToolTipTieB8"));
		HelpManager.getInstance().registerHelpTopic(fRadioTieB8, "lowpoint.fRadioTieB8");
		tieGroup.add(fRadioTieB8);
		gridbagAdd(tiePanel, fRadioTieB8, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		fRadioTieNobreaker = new JRadioButton(res.getString("LowPointLabelTieNobreaker"));
		fRadioTieNobreaker.setMnemonic(res.getString("LowPointLabelTieNobreakerMnemonic").charAt(0));
		fRadioTieNobreaker.setName("fRadioTieNobreaker");
		fRadioTieNobreaker.setToolTipText(res.getString("LowPointToolTipTieNobreaker"));
		HelpManager.getInstance().registerHelpTopic(fRadioTieNobreaker, "lowpoint.fRadioTieNobreaker");
		tieGroup.add(fRadioTieNobreaker);
		gridbagAdd(tiePanel, fRadioTieNobreaker, 0, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		return tiePanel;
	}

	@Override public void start() {
		fComboScoringSystem.addActionListener(this);
		fTextCheckin.addActionListener(this);
		fTextPointsForFirst.addActionListener(this);
		fRadioLongSeriesYes.addActionListener(this);
		fRadioLongSeriesNo.addActionListener(this);

		for (Iterator iter = fRadioTimeLimits.iterator(); iter.hasNext();) {
			((JRadioButton) iter.next()).addActionListener(this);
		}

		for (Iterator iter = fTextThrowouts.iterator(); iter.hasNext();) {
			JTextFieldSelectAll text = (JTextFieldSelectAll) iter.next();
			text.addActionListener(this);
		}

		fTextThrowoutBestX.addActionListener(this);
		fTextThrowoutPerX.addActionListener(this);
		fCheckEntriesLargestDivision.addActionListener(this);
		fRadioThrowoutByNumRaces.addActionListener(this);
		fRadioThrowoutPerXRaces.addActionListener(this);
		fRadioThrowoutBestXRaces.addActionListener(this);
		fRadioThrowoutNone.addActionListener(this);
		fRadioTieA82Only.addActionListener(this);
		fRadioTieB8.addActionListener(this);
		fRadioTieNobreaker.addActionListener(this);
		fRadioTieRrsDefault.addActionListener(this);
	}

	@Override public void stop() {
		fComboScoringSystem.removeActionListener(this);
		fTextCheckin.removeActionListener(this);
		fTextPointsForFirst.removeActionListener(this);
		fRadioLongSeriesYes.removeActionListener(this);
		fRadioLongSeriesNo.removeActionListener(this);

		Iterator iter = fRadioTimeLimits.iterator();
		while (iter.hasNext())
			((JRadioButton) iter.next()).removeActionListener(this);

		iter = fTextThrowouts.iterator();
		while (iter.hasNext()) {
			JTextFieldSelectAll text = (JTextFieldSelectAll) iter.next();
			text.removeActionListener(this);
		}

		fTextThrowoutBestX.removeActionListener(this);
		fTextThrowoutPerX.removeActionListener(this);

		fRadioThrowoutByNumRaces.removeActionListener(this);
		fRadioThrowoutPerXRaces.removeActionListener(this);
		fRadioThrowoutBestXRaces.removeActionListener(this);
		fRadioThrowoutNone.removeActionListener(this);
		fRadioTieA82Only.removeActionListener(this);
		fRadioTieB8.removeActionListener(this);
		fRadioTieNobreaker.removeActionListener(this);
		fRadioTieRrsDefault.removeActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == fTextCheckin) fTextCheckin_actionPerformed();
		else if (object == fTextPointsForFirst) fTextPointsForFirst_actionPerformed();
		else if (object == fRadioLongSeriesYes) fRadioLongSeries_actionPerformed();
		else if (object == fRadioLongSeriesNo) fRadioLongSeries_actionPerformed();
		else if (fRadioTimeLimits.contains(object)) fRadioTimeLimits_actionPerformed(event);
		else if (fTextThrowouts.contains(object)) fTextThrowouts_actionPerformed(event);
		else if (object == fTextThrowoutBestX) fTextThrowoutBestX_actionPerformed(event);
		else if (object == fTextThrowoutPerX) fTextThrowoutPerX_actionPerformed(event);
		else if (object == fRadioThrowoutByNumRaces) fRadioThrowoutByNumRaces_actionPerformed();
		else if (object == fRadioThrowoutPerXRaces) fRadioThrowoutPerXRaces_actionPerformed();
		else if (object == fRadioThrowoutBestXRaces) fRadioThrowoutBestXRaces_actionPerformed();
		else if (object == fRadioThrowoutNone) fRadioThrowoutNone_actionPerformed();
		else if (object == fRadioTieRrsDefault) fRadioTies_actionPerformed();
		else if (object == fRadioTieA82Only) fRadioTies_actionPerformed();
		else if (object == fRadioTieB8) fRadioTies_actionPerformed();
		else if (object == fRadioTieNobreaker) fRadioTies_actionPerformed();
		else if (object == fComboScoringSystem) fComboScoringSystem_actionPerformed();
		else if (object == fCheckEntriesLargestDivision) fCheckEntriesLargestDivision_actionPerformed();

		if (getEditorParent() != null) getEditorParent().eventOccurred(this, event);
	}

	void fCheckEntriesLargestDivision_actionPerformed() {
		fOptions.setEntriesLargestDivision(fCheckEntriesLargestDivision.isSelected());
	}

	void fComboScoringSystem_actionPerformed() {
		String scoringName = (String) fComboScoringSystem.getSelectedItem();
		setScoringName(scoringName);
	}

	boolean handlingError = false;

	void fTextCheckin_actionPerformed() {
		if (handlingError) return;
		String old = Integer.toString(fOptions.getCheckinPercent());
		try {
			int p = Integer.parseInt(fTextCheckin.getText());
			if (p >= 0 && p <= 100) fOptions.setCheckinPercent(p);
			else {
				handlingError = true; // done to avoid a lostfocus handling
				// while error handling
				JOptionPane.showMessageDialog(this, res.getString("LowPointMessagePercentNot0To100"));
				fTextCheckin.setText(old);
				handlingError = false;
			}
		} catch (Exception e) {
			handlingError = true; // done to avoid a lostfocus handling while
			// error handling
			JOptionPane.showMessageDialog(this, res.getString("LowPointMessagePercentNotANumber") + BLANK
					+ e.toString());
			fTextCheckin.setText(old);
			handlingError = false;
		}
	}

	void fTextPointsForFirst_actionPerformed() {
		if (handlingError) return;
		String old = Double.toString(fOptions.getFirstPlacePoints());
		try {
			double p = Double.parseDouble(fTextPointsForFirst.getText());
			if (p >= 0) fOptions.setFirstPlacePoints(p);
			else {
				handlingError = true; // done to avoid a lostfocus handling
				// while error handling
				JOptionPane.showMessageDialog(this, res.getString("LowPointFirstPlaceNotANumber"));
				fTextPointsForFirst.setText(old);
				handlingError = false;
			}
		} catch (Exception e) {
			handlingError = true; // done to avoid a lostfocus handling while
			// error handling
			JOptionPane.showMessageDialog(this, res.getString("LowPointFirstPlaceNotANumber") + BLANK
					+ e.toString());
			fTextPointsForFirst.setText(old);
			handlingError = false;
		}
	}

	void fRadioTimeLimits_actionPerformed(EventObject event) {
		int index = fRadioTimeLimits.indexOf(event.getSource());
		fOptions.setTimeLimitPenalty(index);
	}

	void fRadioLongSeries_actionPerformed() {
		fOptions.setLongSeries(fRadioLongSeriesYes.isSelected());
	}

	void fRadioTies_actionPerformed() {
		if (fRadioTieRrsDefault.isSelected()) fOptions.setTiebreaker(Constants.TIE_RRS_DEFAULT);
		else if (fRadioTieA82Only.isSelected()) fOptions.setTiebreaker(Constants.TIE_RRS_A82_ONLY);
		else if (fRadioTieB8.isSelected()) fOptions.setTiebreaker(Constants.TIE_RRS_B8);
		else if (fRadioTieNobreaker.isSelected()) fOptions.setTiebreaker(Constants.TIE_NOTIEBREAKER);
	}

	void fTextThrowoutBestX_actionPerformed(EventObject event) {
		if (handlingError) return;
		JTextFieldSelectAll field = (JTextFieldSelectAll) event.getSource();

		String old = Integer.toString(fOptions.getThrowoutBestX());
		String msg = res.getString("LowPointMessageBestXError");

		int min = textToInteger(field, old, msg);
		if (min >= 0) {
			fOptions.setThrowoutBestX(min);
		}
	}

	void fTextThrowoutPerX_actionPerformed(EventObject event) {
		if (handlingError) return;
		JTextFieldSelectAll field = (JTextFieldSelectAll) event.getSource();

		String old = Integer.toString(fOptions.getThrowoutPerX());
		String msg = res.getString("LowPointMessagePerXError");

		int min = textToInteger(field, old, msg);
		if (min >= 0) {
			fOptions.setThrowoutPerX(min);
		}
	}

	void fRadioThrowoutBestXRaces_actionPerformed() {
		fOptions.setThrowoutScheme(Constants.THROWOUT_BESTXRACES);
		updateEnabled();
	}

	void fRadioThrowoutPerXRaces_actionPerformed() {
		fOptions.setThrowoutScheme(Constants.THROWOUT_PERXRACES);
		updateEnabled();
	}

	void fRadioThrowoutNone_actionPerformed() {
		fOptions.setThrowoutScheme(Constants.THROWOUT_NONE);
		updateEnabled();
	}

	void fRadioThrowoutByNumRaces_actionPerformed() {
		fOptions.setThrowoutScheme(Constants.THROWOUT_BYNUMRACES);
		updateEnabled();
	}

	private int textToInteger(JTextField field, String oldtext, String msg) {
		try {
			int retInt = Integer.parseInt(field.getText());
			if (retInt <= 0) retInt = 0;
			return retInt;
		} catch (Exception e) {
			handlingError = true; // done to avoid a lostfocus handling while
			// error handling
			JOptionPane.showMessageDialog(this, msg);
			field.setText(oldtext);
			handlingError = false; // done to avoid a lostfocus handling while
			// error handling
			return -1;
		}
	}

	void fTextThrowouts_actionPerformed(EventObject event) {
		if (handlingError) return;
		int index = fTextThrowouts.indexOf(event.getSource());
		JTextFieldSelectAll field = (JTextFieldSelectAll) event.getSource();

		String old = fOptions.getThrowouts().get(index).toString();
		String msg = MessageFormat.format(res.getString("LowPointMessageThrowoutError"),
				new Object[] { nthArray[index] });

		int min = textToInteger(field, old, msg);
		if (min >= 0) {
			fOptions.setThrowout(index, min);
		}
	}

	public static void main(String[] args) {
		//Util.setTesting(true);
		JavaScore.initializeEditors();

		Regatta r = new Regatta();
		JavaScoreProperties.setRegatta(r);
		StageScoringModel sm = (SingleStageScoring) r.getScoringManager();
		ScoringOptions s = sm.getModel().getOptions() ; //(ScoringLowPoint) r.getScoringManager().getModel();

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		fFrame.setObject(sm);
		fFrame.setVisible(true);

	    LoggerFactory.getLogger( "main").debug( "checkin=" + s.getCheckinPercent() + ", tlm="
				+ sTimeLimitPenalties.get(s.getTimeLimitPenalty()) + ", throws=" + s.getThrowouts() + ", islong="
				+ s.isLongSeries() + ", scheme=" + s.getThrowoutScheme() + ", perx=" + s.getThrowoutPerX() + ", bestx="
				+ s.getThrowoutBestX() + ", tie=" + s.getTiebreaker());
		System.exit(0);
	}
}
/**
 * $Log: PanelScoringOptions.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/02 22:30:21 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.1 2006/01/01 22:40:42 sandyg Renamed ScoringLowPoint to ScoringOptions, add gui unit tests
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.12 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.11 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.10 2003/04/27 21:35:36 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.9 2003/03/30 00:05:50 sandyg moved to eclipse 2.1
 * 
 * Revision 1.8 2003/03/19 02:38:24 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.7 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
