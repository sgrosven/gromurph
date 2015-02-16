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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javax.swing.*;

import org.gromurph.javascore.*;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.*;

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

	JPanel fPanelBFactor;
	JPanel fPanelComment = null;

	JRadioButton fRadioAverage;
	JRadioButton fRadioHeavy;
	JRadioButton fRadioLight;
	JRadioButton fRadioCustom;

	JTextPane fTextComment = null;
	JTextFieldSelectAll fTextWeight;

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
		fPanelBFactor = addFieldsBFactorPanel();
		gridbagAdd(panelCenter, fPanelBFactor, 0, row, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));

		initializeListeners();
	}

	private JPanel addFieldsBFactorPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setToolTipText(res.getString("RaceLabelBFactorPanelToolTip"));
		panel.setBorder(BorderFactory.createTitledBorder(res.getString("RaceLabelBFactorPanel")));
		panel.setName("panel");

		ButtonGroup groupBFactor = new ButtonGroup();
		fRadioLight = new JRadioButton(res.getString("RaceLabelBFactorLight"));
		fRadioLight.setMnemonic(res.getString("RaceLabelBFactorLightMnemonic").charAt(0));
		fRadioLight.setToolTipText(res.getString("RaceLabelBFactorLightToolTip"));
		fRadioLight.setName("fRadioLight");
		groupBFactor.add(fRadioLight);
		gridbagAdd(panel, fRadioLight, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioLight, "race.groupBFactor");

		fRadioAverage = new JRadioButton(res.getString("RaceLabelBFactorAverage"));
		fRadioAverage.setMnemonic(res.getString("RaceLabelBFactorAverageMnemonic").charAt(0));
		fRadioAverage.setToolTipText(res.getString("RaceLabelBFactorAverageToolTip"));
		fRadioAverage.setName("fRadioAverage");
		groupBFactor.add(fRadioAverage);
		gridbagAdd(panel, fRadioAverage, 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioAverage, "race.groupBFactor");

		fRadioHeavy = new JRadioButton(res.getString("RaceLabelBFactorHeavy"));
		fRadioHeavy.setMnemonic(res.getString("RaceLabelBFactorHeavyMnemonic").charAt(0));
		fRadioHeavy.setToolTipText(res.getString("RaceLabelBFactorHeavyToolTip"));
		fRadioHeavy.setName("fRadioHeavy");
		groupBFactor.add(fRadioHeavy);
		gridbagAdd(panel, fRadioHeavy, 0, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioHeavy, "race.groupBFactor");

		fRadioCustom = new JRadioButton(res.getString("RaceLabelBFactorCustom"));
		fRadioCustom.setMnemonic(res.getString("RaceLabelBFactorCustomMnemonic").charAt(0));
		fRadioCustom.setToolTipText(res.getString("RaceLabelBFactorCustomToolTip"));
		fRadioCustom.setName("fRadioCustom");
		groupBFactor.add(fRadioCustom);
		gridbagAdd(panel, fRadioCustom, 0, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new java.awt.Insets(0, 0, 0, 0));
		HelpManager.getInstance().registerHelpTopic(fRadioCustom, "race.groupBFactor");

		return panel;
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

	void fCheckNonDiscardable_actionPerformed() {
		fRace.setNonDiscardable(fCheckNonDiscardable.isSelected());
	}

	public void fRadioAverage_actionPerformed() {
		fRace.setBFactor(RatingPhrfTimeOnTime.BFACTOR_AVERAGE);
		fRace.setAFactor(getAFactor());
	}

	public void fRadioHeavy_actionPerformed() {
		fRace.setBFactor(RatingPhrfTimeOnTime.BFACTOR_HEAVY);
		fRace.setAFactor(getAFactor());
	}

	public void fRadioLight_actionPerformed() {
		fRace.setBFactor(RatingPhrfTimeOnTime.BFACTOR_LIGHT);
		fRace.setAFactor(getAFactor());
	}

	public void fRadioCustom_actionPerformed() {
		fRace.setBFactor( getBFactor());
		fRace.setAFactor( getAFactor());
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

	private int fAFactor = -1;
	private int fBFactor = RatingPhrfTimeOnTime.BFACTOR_AVERAGE;
	
	public int getAFactor() {
		if (fAFactor < 0) {
			fAFactor = RatingPhrfTimeOnTime.AFACTOR_DEFAULT;
			String prefA = (String) JavaScoreProperties.getPropertyValue(JavaScoreProperties.AFACTOR_PROPERTY);
			if (prefA != null) try {
				fAFactor = Integer.parseInt(prefA);
			} catch (Exception e) {}
		}
		return fAFactor;
	}
	
	public int getBFactor() {
		if (canBeCustomFactor()) return fBFactor;
		else return -1;
	}
	
	private boolean canBeCustomFactor() {
		return JavaScoreProperties.haveCustomABFactors();
	}
	
	@Override public void updateFields() {

		if (fRace != null) {
			fCheckLongDistance.setSelected(fRace.isLongDistance());
			SailTime.setLongDistance(fRace.isLongDistance());

			boolean canBeCustom = canBeCustomFactor();
			
			fRadioCustom.setVisible(canBeCustom);
			if (canBeCustom) {
				fRadioCustom.setText( res.getString("RaceLabelBFactorCustom") + " A=" + getAFactor() + ", B=" + getBFactor());
			} 
				
			int bf = fRace.getBFactor();
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
    				if (canBeCustom) fRadioCustom.setSelected(true);
    				else fRadioAverage.setSelected(true);
    				break;
			}
		} else {
			fTextWeight.setText("1.00");
			fCheckLongDistance.setSelected(false);
			SailTime.setLongDistance(false);
		}

		updateMedalFields();

	}
}
/**
 * $Log: PanelRaceAdvanced.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/11 02:17:16 sandyg Bug fixes relative to qualify/final race scoring
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.1.2.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks on
 * text fields in panels.
 * 
 * Revision 1.1.2.1 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.19.4.2 2005/11/19 20:34:55 sandyg last of java 5 conversion, created swingworker, removed threads
 * packages.
 * 
 * Revision 1.19.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.19 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.18 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.17 2003/07/10 02:01:39 sandyg Added stack trace looking for penalty problems
 * 
 * Revision 1.16 2003/04/30 00:59:13 sandyg fixed error handling on bad race times, improved unit testing
 * 
 * Revision 1.15 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.14 2003/03/30 00:33:52 sandyg fixed focus problem, completes implementing #691236
 * 
 * Revision 1.13 2003/03/30 00:04:47 sandyg added comments field
 * 
 * Revision 1.12 2003/03/28 03:07:51 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.11 2003/03/28 02:22:06 sandyg Feature #691217 - in Race Dialog, divisions with 0 entries no longer
 * included in start time list
 * 
 * Revision 1.10 2003/03/19 02:38:24 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.9 2003/03/16 21:47:19 sandyg 3.9.2 release: fix bug 658904, time on time condition buttons corrected
 * 
 * Revision 1.8 2003/03/16 20:38:31 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.7 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
