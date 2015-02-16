//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelPenalty.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;

/**
 * Editor for setting/editting Penalties
 */
public class PanelPenalty extends BaseEditor<Penalty> implements ActionListener, Constants {

	static ResourceBundle res = JavaScoreProperties.getResources();

	/**
	 * for standalone testing only
	 */
	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Penalty b = new Penalty();

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		fFrame.setObject(b);
		fFrame.setVisible(true);
	}

	private Penalty fPenalty;

	JPanel fPanelMain;

	JRadioButton fRadioDNC;
	JRadioButton fRadioDNF;
	JRadioButton fRadioDNS;

	JRadioButton fRadioTLE;
	JRadioButton fRadioNone;
	JRadioButton fRadioDSQ;
	JRadioButton fRadioDNE;
	JRadioButton fRadioOCS;
	JRadioButton fRadioBFD;
	JRadioButton fRadioRET;
	JRadioButton fRadioDGM;
	JRadioButton fRadioNoDsq;

	JRadioButton fRadioLabelRDG;
	JRadioButton fRadioLabelDPI;
	JRadioButton fRadioLabelOther;

	JRadioButton fRadioRedressNone;
	JRadioButton fRadioRedressAVG;
	JRadioButton fRadioRedressPoints;
	JRadioButton fRadioRedressTimeElapsed;
	JRadioButton fRadioRedressTimeCorrected;

	JCheckBox fCheckCNF;
	JCheckBox fCheckZFP;
	JCheckBox fCheckZFP2;
	JCheckBox fCheckZFP3;
	JRadioButton fRadioRedressPctScore;
	JRadioButton fRadioRedressPctTime;

	JLabel fLabelPercent;
	JLabel fLabelLabel;
	JLabel fLabelAmount;

	JTextFieldSelectAll fTextPercent;
	JTextFieldSelectAll fTextLabelOther;

	JTextFieldSelectAll fTextRedressPoints;
	JTextFieldSelectAll fTextRedressTime;

	JTextFieldSelectAll fTextNote;
	public static Penalty last = new Penalty();

	public PanelPenalty(BaseEditorContainer parent) {
		super(parent);
		addFields();
	}

	public void actionPerformed(java.awt.event.ActionEvent event) {
		Object object = event.getSource();

		if (object instanceof JRadioButton) {
			if (((JRadioButton) object).isSelected()) {
				if (object == fRadioDNC)
					finishButton_actionPerformed(DNC);
				else if (object == fRadioDNF)
					finishButton_actionPerformed(DNF);
				else if (object == fRadioDNS)
					finishButton_actionPerformed(DNS);
				else if (object == fRadioTLE)
					finishButton_actionPerformed(TLE);
				else if (object == fRadioDSQ)
					dsqButton_actionPerformed(DSQ);
				else if (object == fRadioDGM)
					dsqButton_actionPerformed(DGM);
				else if (object == fRadioDNE)
					dsqButton_actionPerformed(DNE);
				else if (object == fRadioOCS)
					dsqButton_actionPerformed(OCS);
				else if (object == fRadioBFD)
					dsqButton_actionPerformed(BFD);
				else if (object == fRadioRET)
					dsqButton_actionPerformed(RET);
				else if (object == fRadioNoDsq)
					fRadioNoDsq_actionPeformed();
				else if (object == fRadioNone)
					fRadioNone_actionPeformed();

				else if (object == fRadioRedressAVG)
					redressButton_actionPerformed(event);
				else if (object == fRadioRedressPoints)
					redressButton_actionPerformed(event);
				else if (object == fRadioRedressTimeCorrected)
					redressButton_actionPerformed(event);
				else if (object == fRadioRedressTimeElapsed)
					redressButton_actionPerformed(event);
				else if (object == fRadioRedressNone)
					redressButton_actionPerformed(event);
				else if (object == fRadioLabelRDG)
					fRadioLabelRDG_actionPerformed();
				else if (object == fRadioLabelDPI)
					fRadioLabelDPI_actionPerformed();
				else if (object == fRadioRedressPctScore)
					redressButton_actionPerformed(event);
				else if (object == fRadioRedressPctTime)
					redressButton_actionPerformed(event);
			}
		} else if (object instanceof JCheckBox) {
			if (object == fCheckCNF)
				otherButton_actionPerformed(CNF, event);
			else if (object == fCheckZFP)
				otherButton_actionPerformed(ZFP, event);
			else if (object == fCheckZFP2)
				otherButton_actionPerformed(ZFP2, event);
			else if (object == fCheckZFP3)
				otherButton_actionPerformed(ZFP3, event);
		} else if (object == fTextRedressPoints)
			fTextRedressPoints_actionPeformed();
		else if (object == fTextRedressTime)
			fTextRedressTime_actionPeformed();
		else if (object == fTextPercent)
			fTextPercent_actionPeformed();
		else if (object == fTextNote)
			fTextNote_actionPerformed();
		else if (object == fTextLabelOther)
			fTextLabelOther_actionPerformed();

		updateFields();
		if (getEditorParent() != null)
			getEditorParent().eventOccurred(this, event);
	}

	public void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "penalty");

		setLayout(new java.awt.GridBagLayout());

		int row = 0;
		JPanel fp = createPanelNonFinish();
		gridbagAdd(this, fp, 0, row++, 
				1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		fp = createPanelDisqualified();
		gridbagAdd(this, fp, 0, row++, 
				1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		fp = createPanelPercentStandard();
		gridbagAdd(this, fp, 0, row++, 
				1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		fp = createPanelRedress();
		gridbagAdd(this, fp, 0, row++, 
				1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		fp = createPanelFootnote();
		gridbagAdd(this, fp, 0, row++, 
				1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		addListeners();
	}
	
	java.awt.Insets insets = new java.awt.Insets(0, 0, 0, 0);

	private JPanel createPanelNonFinish() {
		JPanel panelNonFinish = new JPanel(new GridLayout(0, 6, 0, 0));
		panelNonFinish.setBorder(BorderFactory.createTitledBorder(res.getString("PenaltyTitleNonFinish")));
		HelpManager.getInstance().registerHelpTopic(panelNonFinish, "penalty.panelNonFinish");

		ButtonGroup fGroupNonFinish = new ButtonGroup();

		fRadioDNC = new JRadioButton(Penalty.toString(DNC));
		fRadioDNC.setToolTipText(res.getString("PenaltyDNCLongName"));
		fGroupNonFinish.add(fRadioDNC);
		panelNonFinish.add(fRadioDNC);

		fRadioDNS = new JRadioButton(Penalty.toString(DNS));
		fRadioDNS.setToolTipText(res.getString("PenaltyDNSLongName"));
		fGroupNonFinish.add(fRadioDNS);
		panelNonFinish.add(fRadioDNS);

		fRadioDNF = new JRadioButton(Penalty.toString(DNF));
		fRadioDNF.setToolTipText(res.getString("PenaltyDNFLongName"));
		fGroupNonFinish.add(fRadioDNF);
		panelNonFinish.add(fRadioDNF);

		fRadioTLE = new JRadioButton(Penalty.toString(Penalty.TLE));
		fRadioTLE.setToolTipText(res.getString("PenaltyTLELongName"));
		fGroupNonFinish.add(fRadioTLE);
		panelNonFinish.add(fRadioTLE);

		fRadioNone = new JRadioButton(res.getString("PenaltyLabelNoFinishPenalty"));
		fRadioNone.setToolTipText(res.getString("PenaltyLabelNoFinishPenaltyToolTip"));
		fGroupNonFinish.add(fRadioNone);
		panelNonFinish.add(fRadioNone);
		
		return panelNonFinish;
	}
	
	private JPanel createPanelDisqualified() {
		JPanel panelDisqualified = new JPanel(new GridLayout(0, 7, 0, 0));
		panelDisqualified.setBorder(BorderFactory.createTitledBorder(res.getString("PenaltyTitleDisqualification")));
		HelpManager.getInstance().registerHelpTopic(panelDisqualified, "penalty.panelDisqualified");

		ButtonGroup fGroupDisqualified = new ButtonGroup();

		fRadioDSQ = new JRadioButton(Penalty.toString(DSQ));
		fRadioDSQ.setToolTipText(res.getString("PenaltyDSQLongName"));
		fGroupDisqualified.add(fRadioDSQ);
		panelDisqualified.add(fRadioDSQ);

		fRadioDNE = new JRadioButton(Penalty.toString(DNE));
		fRadioDNE.setToolTipText(res.getString("PenaltyDNELongName"));
		fGroupDisqualified.add(fRadioDNE);
		panelDisqualified.add(fRadioDNE);

		fRadioOCS = new JRadioButton(Penalty.toString(OCS));
		fRadioOCS.setToolTipText(res.getString("PenaltyOCSLongName"));
		fGroupDisqualified.add(fRadioOCS);
		panelDisqualified.add(fRadioOCS);

		fRadioBFD = new JRadioButton(Penalty.toString(BFD));
		fRadioBFD.setToolTipText(res.getString("PenaltyBFDLongName"));
		fGroupDisqualified.add(fRadioBFD);
		panelDisqualified.add(fRadioBFD);

		fRadioRET = new JRadioButton(Penalty.toString(RET));
		fRadioRET.setToolTipText(res.getString("PenaltyRETLongName"));
		fGroupDisqualified.add(fRadioRET);
		panelDisqualified.add(fRadioRET);

		fRadioDGM = new JRadioButton(Penalty.toString(DGM));
		fRadioDGM.setToolTipText(res.getString("PenaltyDGMLongName"));
		fGroupDisqualified.add(fRadioDGM);
		panelDisqualified.add(fRadioDGM);

		fRadioNoDsq = new JRadioButton(res.getString("GenNone"));
		fRadioNoDsq.setToolTipText(res.getString("PenaltyLabelNonDisqualificationToolTip"));
		fGroupDisqualified.add(fRadioNoDsq);
		panelDisqualified.add(fRadioNoDsq);
		
		return panelDisqualified;
	}
	
	private JPanel createPanelPercentStandard() {

		JPanel panelPercentStandard = new JPanel(new FlowLayout( FlowLayout.LEFT));
		panelPercentStandard.setBorder(BorderFactory.createTitledBorder(res
				.getString("PenaltyTitlePercentStandardPenalties")));
		HelpManager.getInstance().registerHelpTopic(panelPercentStandard, "penalty.panelPercentStandard");
		
		// == PERCENTAGE box

		fCheckZFP = new JCheckBox("1st " + res.getString("PenaltyZFPLabel"));
		fCheckZFP.setName("fCheckZFP");
		fCheckZFP.setToolTipText(res.getString("PenaltyZFPLongName"));
		fCheckZFP.setActionCommand(Penalty.toString(ZFP));
//		gridbagAdd(panelPercentStandard, fCheckZFP, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
//				insets);

		fCheckZFP2 = new JCheckBox("2nd " + res.getString("PenaltyZFPLabel"));
		fCheckZFP2.setName("fCheckZFP2");
		fCheckZFP2.setToolTipText(res.getString("PenaltyZFPLongName"));
		fCheckZFP2.setActionCommand(Penalty.toString(ZFP2));
//		gridbagAdd(panelPercentStandard, fCheckZFP2, 0, 1, 1, 1, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, insets);

		fCheckZFP3 = new JCheckBox("3rd " + res.getString("PenaltyZFPLabel"));
		fCheckZFP3.setName("fCheckZFP3");
		fCheckZFP3.setToolTipText(res.getString("PenaltyZFPLongName"));
		fCheckZFP3.setActionCommand(Penalty.toString(ZFP3));
//		gridbagAdd(panelPercentStandard, fCheckZFP3, 0, 2, 1, 1, GridBagConstraints.WEST,
//				GridBagConstraints.HORIZONTAL, insets);

		fCheckCNF = new JCheckBox(res.getString("PenaltyCNFLabel"));
		fCheckCNF.setName("fCheckCNF");
		fCheckCNF.setToolTipText(res.getString("PenaltyCNFLongName"));
		fCheckCNF.setActionCommand(Penalty.toString(CNF));
//		gridbagAdd(panelPercentStandard, fCheckCNF, 0, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
//				insets);
		
		panelPercentStandard.add( fCheckZFP);
		panelPercentStandard.add( fCheckZFP2);
		panelPercentStandard.add( fCheckZFP3);
		panelPercentStandard.add( fCheckCNF);

		return panelPercentStandard;
	}
	
	private JPanel createPanelRedress() {
		JPanel panelRedress = new JPanel(new GridBagLayout());
		panelRedress.setBorder(BorderFactory.createTitledBorder(res.getString("PenaltyTitleOtherPenalties")));
		HelpManager.getInstance().registerHelpTopic(panelRedress, "penalty.panelRedress");

		JPanel pc = new JPanel(new FlowLayout( FlowLayout.LEFT, 5, 0));
		gridbagAdd(panelRedress, pc, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				insets);
		
		fRadioRedressNone = new JRadioButton(res.getString("GenNone"));
		fRadioRedressNone.setName("fRadioRedressNone");
		fRadioRedressNone.setSelected(true);
		fRadioRedressNone.setToolTipText(res.getString("PenaltyLabelRedressNoneToolTip"));
		pc.add( fRadioRedressNone);

		fRadioRedressAVG = new JRadioButton(res.getString("PenaltyAVGLabel"));
		fRadioRedressAVG.setName("fRadioRedressAVG");
		fRadioRedressAVG.setToolTipText(res.getString("PenaltyAVGLongName"));
		fRadioRedressAVG.setActionCommand(Penalty.toString(AVG));
		pc.add(fRadioRedressAVG);

		pc = new JPanel(new FlowLayout( FlowLayout.LEFT, 5, 0));
		pc.setBorder(BorderFactory.createTitledBorder(res
				.getString("PenaltyTitlePercentCustomPoints")));
		gridbagAdd(panelRedress, pc, 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				insets);

		fRadioRedressPoints = new JRadioButton(res.getString("PenaltyLabelPoints"));
		fRadioRedressPoints.setName("fRadioRedressPoints");
		fRadioRedressPoints.setToolTipText(res.getString("PenaltyLabelPointsToolTip"));
		fRadioRedressPoints.setActionCommand(Penalty.toString(RDG));
		pc.add( fRadioRedressPoints);

		fTextRedressPoints = new JTextFieldSelectAll(5);
		fTextRedressPoints.setName("fTextRedressPoints");
		fTextRedressPoints.setToolTipText(res.getString("PenaltyLabelPointsToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextRedressPoints, "penalty.fTextPoints");
		pc.add( fTextRedressPoints);

		pc = new JPanel(new FlowLayout( FlowLayout.LEFT, 5, 0));
		pc.setBorder(BorderFactory.createTitledBorder(res
				.getString("PenaltyTitlePercentCustomTime")));
		gridbagAdd(panelRedress, pc, 0, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				insets);

		fRadioRedressTimeElapsed = new JRadioButton(res.getString("PenaltyTIMLabel"));
		fRadioRedressTimeElapsed.setName("fRadioRedressTimeElapsed");
		fRadioRedressTimeElapsed.setToolTipText(res.getString("PenaltyLabelTimePenaltyToolTip"));
		fRadioRedressTimeElapsed.setActionCommand(Penalty.toString(TME));
		pc.add( fRadioRedressTimeElapsed);

		fRadioRedressTimeCorrected = new JRadioButton(res.getString("PenaltyTMCLabel"));
		fRadioRedressTimeCorrected.setName("fRadioRedressTimeCorrected");
		fRadioRedressTimeCorrected.setToolTipText(res.getString("PenaltyTMCToolTip"));
		fRadioRedressTimeCorrected.setActionCommand(Penalty.toString(TMC));
		pc.add( fRadioRedressTimeCorrected);

		fTextRedressTime = new JTextFieldSelectAll(10);
		fTextRedressTime.setName("fTextRedressTime");
		fTextRedressTime.setToolTipText(res.getString("PenaltyLabelTimePenaltyToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextRedressTime, "penalty.fTextTimePenalty");
		pc.add( fTextRedressTime);

		JPanel panelPercentCustom = createPanelPercentCustom();
		gridbagAdd(panelRedress, panelPercentCustom, 0, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		JPanel fp = createPanelScoreCode();
		gridbagAdd(panelRedress, fp, 0, 4, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets);

		ButtonGroup fGroupRedress = new ButtonGroup();
		fGroupRedress.add(fRadioRedressNone);
		fGroupRedress.add(fRadioRedressAVG);
		fGroupRedress.add(fRadioRedressPoints);
		fGroupRedress.add(fRadioRedressTimeElapsed);
		fGroupRedress.add(fRadioRedressTimeCorrected);
		fGroupRedress.add(fRadioRedressPctScore);
		fGroupRedress.add(fRadioRedressPctTime);
		
		return panelRedress;
	}
	
	private JPanel createPanelPercentCustom() {
		JPanel panelPercentCustom = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panelPercentCustom.setBorder(BorderFactory.createTitledBorder(res
				.getString("PenaltyTitlePercentCustomPenalties")));
		HelpManager.getInstance().registerHelpTopic(panelPercentCustom, "penalty.panelPercentCustom");

		// PERCENTAGE SECTION

		fRadioRedressPctScore = new JRadioButton(res.getString("PenaltySCPLabel"));
		fRadioRedressPctScore.setToolTipText(res.getString("PenaltySCPToolTip"));
		fRadioRedressPctScore.setActionCommand(Penalty.toString(SCP));
		panelPercentCustom.add(fRadioRedressPctScore);

		fRadioRedressPctTime = new JRadioButton(res.getString("PenaltyTMPLabel"));
		fRadioRedressPctTime.setToolTipText(res.getString("PenaltyTMPToolTip"));
		fRadioRedressPctTime.setActionCommand(Penalty.toString(TMP));
		panelPercentCustom.add(fRadioRedressPctTime);

		fLabelPercent = new JLabel(res.getString("PenaltyLabelPercentagePanel"));
		panelPercentCustom.add( fLabelPercent);

		fTextPercent = new JTextFieldSelectAll(5);
		fTextPercent.setToolTipText(res.getString("PenaltySCPToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextPercent, "penalty.fTextPercent");
		panelPercentCustom.add( fTextPercent);
		
		return panelPercentCustom;
	}
	
	private JPanel createPanelScoreCode() {
		JPanel fp = new JPanel(new FlowLayout( FlowLayout.LEFT, 5, 0));
		
		fp.setBorder(BorderFactory.createTitledBorder(res
				.getString("PenaltyLabelLabelPenalty")));
		
		fRadioLabelRDG = new JRadioButton(Penalty.toString(RDG, false));
		fRadioLabelRDG.setName("fRadioLabelRDG");
		fRadioLabelRDG.setSelected(true);
		fRadioLabelRDG.setToolTipText(res.getString("PenaltyLabelRDG"));
		fRadioLabelRDG.setActionCommand(Penalty.toString(RDG));
		fp.add( fRadioLabelRDG);

		fRadioLabelDPI = new JRadioButton(Penalty.toString(DPI, false));
		fRadioLabelDPI.setName("fRadioLabelDPI");
		//fRadioLabelDPI.setSelected(true);
		fRadioLabelDPI.setToolTipText(res.getString("PenaltyLabelDPI"));
		fRadioLabelDPI.setActionCommand(Penalty.toString(DPI));
		fp.add( fRadioLabelDPI);

		fRadioLabelOther = new JRadioButton(res.getString("PenaltyLabelOtherPenalty"));
		fRadioLabelOther.setName("fRadioLabelOther");
		fRadioLabelOther.setToolTipText(res.getString("PenaltyLabelOtherToolTip"));
		fRadioLabelOther.setActionCommand("Other");
		fp.add( fRadioLabelOther);

		ButtonGroup fGroupRedressLabels = new ButtonGroup();
		fGroupRedressLabels.add(fRadioLabelRDG);
		fGroupRedressLabels.add(fRadioLabelDPI);
		fGroupRedressLabels.add(fRadioLabelOther);

		fTextLabelOther = new JTextFieldSelectAll(5);
		fTextLabelOther.setName("fTextLabelOther");
		fTextLabelOther.setToolTipText(res.getString("PenaltyLabelOtherToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextLabelOther, "penalty.fTextLabelOther");
		fp.add( fTextLabelOther);
		
		return fp;
	}
	
	private JPanel createPanelFootnote() {

		// FOOT NOTE SECTION

		JPanel fp = new JPanel(new BorderLayout());
		fTextNote = new JTextFieldSelectAll(15);
		fTextNote.setToolTipText(res.getString("PenaltyLabelNoteToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextNote, "penalty.fTextNote");
		fp.add(new JLabel(res.getString("PenaltyLabelNote")), BorderLayout.WEST);
		fp.add(fTextNote, BorderLayout.CENTER);
		
		return fp;
	}
	
	private void addListeners() {
		fRadioDNC.addActionListener(this);
		fRadioDNF.addActionListener(this);
		fRadioDNS.addActionListener(this);
		// fRadioWTH.addActionListener(this);
		fRadioTLE.addActionListener(this);
		fRadioNone.addActionListener(this);

		fRadioDSQ.addActionListener(this);
		fRadioDNE.addActionListener(this);
		fRadioOCS.addActionListener(this);
		fRadioBFD.addActionListener(this);
		fRadioRET.addActionListener(this);
		fRadioDGM.addActionListener(this);
		fRadioNoDsq.addActionListener(this);

		fCheckCNF.addActionListener(this);
		fCheckZFP.addActionListener(this);
		fCheckZFP2.addActionListener(this);
		fCheckZFP3.addActionListener(this);
		fRadioRedressAVG.addActionListener(this);
		fRadioRedressPctScore.addActionListener(this);
		fRadioLabelRDG.addActionListener(this);
		fRadioLabelDPI.addActionListener(this);
		fRadioLabelOther.addActionListener(this);
		fRadioRedressNone.addActionListener(this);
		fRadioRedressPoints.addActionListener(this);
		fRadioRedressTimeCorrected.addActionListener(this);
		fRadioRedressTimeElapsed.addActionListener(this);
		fRadioRedressPctTime.addActionListener(this);

		fTextLabelOther.addActionListener(this);
		fTextRedressPoints.addActionListener(this);
		fTextRedressTime.addActionListener(this);
		fTextPercent.addActionListener(this);
		fTextNote.addActionListener(this);
	}

	
	public void dsqButton_actionPerformed(long ocs) {
		fPenalty.setDsqPenalty(ocs);
		fPenalty.clearPenalty(AVG);
		fPenalty.clearPenalty(SCP);
		fPenalty.clearPenalty(TMP);
		fPenalty.clearPenalty(TME);
		fPenalty.clearPenalty(TMC);
		// fPenalty.clearPenalty( MAN);
	}

	public void finishButton_actionPerformed(long penalty) {
		fPenalty.setFinishPenalty(penalty);

		fPenalty.clearPenalty(SCP);
		fPenalty.clearPenalty(TMP);
		fPenalty.clearPenalty(TME);
		fPenalty.clearPenalty(TMC);
	}

	public void fRadioLabelRDG_actionPerformed() {
		fPenalty.setRedressLabel("RDG");
	}

	public void fRadioLabelDPI_actionPerformed() {
		fPenalty.setRedressLabel("DPI");
	}

	public void fRadioNoDsq_actionPeformed() {
		fPenalty.setDsqPenalty(0);
	}

	public void fRadioNone_actionPeformed() {
		fPenalty.setFinishPenalty(0);
	}

	public void fTextLabelOther_actionPerformed() {
		fPenalty.setRedressLabel(fTextLabelOther.getText());
	}

	public void fTextNote_actionPerformed() {
		fPenalty.setNote(fTextNote.getText());
	}

	public void fTextPercent_actionPeformed() {
		try {
			fPenalty.setPercent(new Integer(fTextPercent.getText()).intValue());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString(), res.getString("PenaltyMessageInvalidInput"),
					JOptionPane.ERROR_MESSAGE);
//			fTextPercent.requestFocusInWindow();
		}
	}

	public void fTextRedressPoints_actionPeformed() {
		try {
			String t = fTextRedressPoints.getText();
			if (t == null || t.trim().length() == 0) fPenalty.setPoints(0);
			else fPenalty.setPoints(new Double(t).doubleValue());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString(), res.getString("PenaltyMessageInvalidInput"),
					JOptionPane.ERROR_MESSAGE);
//			fTextRedressPoints.requestFocusInWindow();
		}
		last = fPenalty;
	}

	public void fTextRedressTime_actionPeformed() {
		try {
			String t = fTextRedressTime.getText();
			long tvalue = 0;
			if (!(t == null || t.trim().length() == 0)) {
				tvalue = SailTime.forceToLong(t);
			}

			if (fRadioRedressTimeCorrected.isSelected()) {
				fPenalty.setTimePenaltyCorrected(tvalue);
				fPenalty.setTimePenaltyElapsed(0);
			} else if (fRadioRedressTimeElapsed.isSelected()) {
				fPenalty.setTimePenaltyElapsed(tvalue);
				fPenalty.setTimePenaltyCorrected(0);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.toString(), res.getString("PenaltyMessageInvalidInput"),
					JOptionPane.ERROR_MESSAGE);
//			fTextRedressPoints.requestFocusInWindow();
		}
		last = fPenalty;
	}

	public Penalty getPenalty() {
		return fPenalty;
	}

	public void otherButton_actionPerformed(long penalty, ActionEvent event) {
		// handles gui processing for various percentages

		AbstractButton button = (AbstractButton) event.getSource();
		if (button.isSelected()) {
			fPenalty.addOtherPenalty(penalty);
		} else {
			fPenalty.clearPenalty(penalty);
		}
	}

	public void redressButton_actionPerformed( ActionEvent event) {
		// handles gui processing for redress and various percentages
		// everything but the dsq, and non-finish penalties
		// - only one of redress penalties can be set (RDG,AVG,TME,TMC) - clicking
		//        one should clear the other penalties
		// - RDG/TME/TMC should also clear out the percentage boxes

		fPenalty.clearPenalty( RDG);
		fPenalty.clearPenalty( TMC);
		fPenalty.clearPenalty( AVG);
		fPenalty.clearPenalty( TME);
		fPenalty.clearPenalty( SCP);
		fPenalty.clearPenalty( TMP);

		AbstractButton button = (AbstractButton) event.getSource();
		if (button == fRadioRedressAVG) {
			fPenalty.addOtherPenalty(AVG);
			setScoringCode( "RDG");
		} else if (button == fRadioRedressPoints) {
			fPenalty.addOtherPenalty(RDG);
			setScoringCode( "RDG");
		} else if (button == fRadioRedressTimeCorrected) {
			fPenalty.addOtherPenalty(TMC);
			setScoringCode( "TMC");
		} else if (button == fRadioRedressTimeElapsed) {
			fPenalty.addOtherPenalty(TME);
			setScoringCode( "TME");
		} else if (button == fRadioRedressPctScore) {
			fPenalty.addOtherPenalty(SCP);
			setScoringCode( "SCP");
		} else if (button == fRadioRedressPctTime) {
			fPenalty.addOtherPenalty(TMP);
			setScoringCode( "TMP");
		}  else if (button == fRadioRedressNone) {
			setScoringCode("");
		}
	}
	
	private void setScoringCode( String pen) {
		if (pen.equals("")) {
			fRadioLabelRDG.setSelected(true);
			fPenalty.setRedressLabel("");
		} else if (pen.equals("RDG")) {
			fRadioLabelRDG.setSelected(true);
			fPenalty.setRedressLabel( pen);
			fTextLabelOther.setText( "");
		} else if (pen.equals("DPI")) {
			fRadioLabelDPI.setSelected(true);
			fTextLabelOther.setText( "");
			fPenalty.setRedressLabel( pen);
		} else {
			if (!fRadioLabelOther.isSelected()) {
    			fRadioLabelOther.setSelected(true);
    			fTextLabelOther.setText( pen);
			}
			fPenalty.setRedressLabel( pen);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == fPenalty) {
			updateFields();
		}
	}

	/**
	 * restore supports backing out of editing a regatta so it will restore the
	 * atomic items and the division list.. but not the entries, nor race
	 * objects
	 **/
	@Override public void restore(Penalty a, Penalty b) {
		if (a == b)
			return;
		Penalty active = (Penalty) a;
		Penalty backup = (Penalty) b;

		active.setPenalty(backup.getPenalty());
		active.setPoints(backup.getPoints());
		active.setPercent(backup.getPercent());
		active.setNote(backup.getNote());
		super.restore(active, backup);
	}

	@Override public void setObject(Penalty obj) {
		fPenalty = obj; // fDivision = null;

		if (fPenalty != null)
			fPenalty.removePropertyChangeListener(this);

		super.setObject(obj);

		if (isVisible() && fPenalty != null)
			fPenalty.addPropertyChangeListener(this);
	}

	@Override public void start() {
	}

	@Override public void stop() {
	}

	/**
	 * reviews the penalty settings and enables/disables fields accordingly
	 */
	public void updateEnabled() {

		if (fPenalty != null) {
			this.setEnabled(true);

			fTextRedressPoints.setEnabled(fRadioRedressPoints.isSelected());
			fTextRedressTime.setEnabled(fRadioRedressTimeCorrected.isSelected()
					|| fRadioRedressTimeElapsed.isSelected());

			boolean isRedress = !fRadioRedressNone.isSelected() ||
					(fRadioRedressAVG.isSelected() || 
					fRadioRedressTimeCorrected.isSelected() || 
					fRadioRedressTimeElapsed.isSelected() || 
					fRadioRedressPoints.isSelected() ||
					fRadioRedressPctScore.isSelected() ||
					fRadioRedressPctTime.isSelected());
			
			fRadioLabelRDG.setEnabled(isRedress);
			fRadioLabelDPI.setEnabled(isRedress);
			fRadioLabelOther.setEnabled(isRedress);
				
			fTextLabelOther.setEnabled( isRedress && 
					fRadioLabelOther.isSelected());

			fTextPercent.setEnabled(fPenalty.hasPenalty(TMP) || 
					fPenalty.hasPenalty(SCP));
			fLabelPercent.setEnabled(fTextPercent.isEnabled());

		} else {
			this.setEnabled(false);
		}
	}
	
	

	@Override public void updateFields() {
		Penalty p = (fPenalty == null) ? new Penalty() : fPenalty;
		if (p.isFinishPenalty()) {
			fRadioDNC.setSelected(p.hasPenalty(DNC));
			fRadioDNF.setSelected(p.hasPenalty(DNF));
			fRadioDNS.setSelected(p.hasPenalty(DNS));
			fRadioTLE.setSelected(p.hasPenalty(TLE));
		} else {
			fRadioNone.setSelected(true);
		}

		if (p.isDsqPenalty()) {
			fRadioDSQ.setSelected(p.hasPenalty(DSQ));
			fRadioDNE.setSelected(p.hasPenalty(DNE));
			fRadioOCS.setSelected(p.hasPenalty(OCS));
			fRadioBFD.setSelected(p.hasPenalty(BFD));
			fRadioRET.setSelected(p.hasPenalty(RET));
			fRadioDGM.setSelected(p.hasPenalty(DGM));
		} else {
			fRadioNoDsq.setSelected(true);
		}

		fCheckCNF.setSelected(p.hasPenalty(CNF));
		fCheckZFP.setSelected(p.hasPenalty(ZFP));
		fCheckZFP2.setSelected(p.hasPenalty(ZFP2));
		fCheckZFP3.setSelected(p.hasPenalty(ZFP3));

		if (p.hasPenalty(AVG)) {
			fRadioRedressAVG.setSelected(true);
		} else if (p.hasPenalty( Penalty.TMC)) {
			fRadioRedressTimeCorrected.setSelected(true);
			fTextRedressTime.setText( SailTime.toString( p.getTimePenaltyCorrected()));
		} else if (p.hasPenalty( Penalty.TME)) {
			fRadioRedressTimeElapsed.setSelected(true);
			fTextRedressTime.setText( SailTime.toString( p.getTimePenaltyElapsed()));
		} else if (p.hasPenalty(RDG)) {
			fRadioRedressPoints.setSelected(p.hasPenalty(RDG));
			fTextRedressPoints.setText(Double.toString(p.getPoints()));
		} else if (p.hasPenalty(TMP)) {
			fRadioRedressPctTime.setSelected(true);
			fTextPercent.setText(Integer.toString(p.getPercent()));
		} else if (p.hasPenalty(SCP)) {
			fRadioRedressPctScore.setSelected(p.hasPenalty(SCP));
			fTextPercent.setText(Integer.toString(p.getPercent()));
		} else {
			fRadioRedressNone.setSelected(true);
		}
		
		if (!fRadioLabelOther.isSelected()) {
    		String label = p.getRedressLabel();
    		if (label.equals("RDG")) {
    			fRadioLabelRDG.setSelected(true);
    		} else if (label.equals("DPI")) {
    			fRadioLabelDPI.setSelected(true);
    		} else {
    			fRadioLabelOther.setSelected(true);
    		}
		}
    	fTextLabelOther.setText(p.getRedressLabel());
		fTextNote.setText(p.getNote());
		
		updateEnabled();
	}
}
/**
 * $Log: PanelPenalty.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/14 14:40:35 sandyg added some @suppresswarnings on
 * warnings that I could not code around
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.13.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to
 * JTextFieldSelectAll. Removed focus lost checks on text fields in panels.
 * 
 * Revision 1.13.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.13.2.1 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121,
 * 1226607, killed Java Web Start startup code
 * 
 * Revision 1.13 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.12 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.11 2003/04/27 21:35:35 sandyg more cleanup of unused variables...
 * ALL unit tests now working
 * 
 * Revision 1.10 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.9 2003/04/23 00:30:21 sandyg added Time-based penalties
 * 
 * Revision 1.8 2003/04/20 15:43:59 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.7 2003/03/19 02:38:23 sandyg made start() stop() abstract to
 * BaseEditor, the isStarted check now done in BaseEditor.startUp and
 * BaseEditor.shutDown().
 * 
 * Revision 1.6 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
