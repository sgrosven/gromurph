//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRegatta.java,v 1.7 2006/05/19 05:48:43 sandyg Exp $
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.actions.ActionEditFleets;
import org.gromurph.javascore.actions.ActionEditScoringOptions;
import org.gromurph.javascore.actions.ActionEditStages;
import org.gromurph.javascore.actions.ActionEditStartingDivision;
import org.gromurph.javascore.actions.ActionEditSubDivisions;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.scoring.MultiStage;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;

public class PanelRegatta extends BaseEditor<Regatta> implements ActionListener, ListSelectionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private Regatta fRegatta;
	
	ActionEditStartingDivision fActionEditStartingDivision = new ActionEditStartingDivision();
	ActionEditScoringOptions fActionEditScoringOptions = new ActionEditScoringOptions();
	ActionEditStages fActionEditStages = new ActionEditStages();

	public PanelRegatta( BaseEditorContainer parent) {
		super(parent);

		fActionEditStartingDivision.startInitializing();
		fActionEditScoringOptions.startInitializing();
		fActionEditStages.startInitializing();

		addFields();
		
		DivisionList.getMasterList().addPropertyChangeListener(this);
	}

	public Regatta getRegatta() {
		return fRegatta;
	}

	@Override public void setObject(Regatta obj) {
		super.setObject(obj);

		if (fRegatta != null) fRegatta.removePropertyChangeListener(this);
		fRegatta = (Regatta) obj;
		if (isVisible() && fRegatta != null) fRegatta.addPropertyChangeListener(this);
		updateFields();
	}

	/**
	 * restore supports backing out of editing a regatta so it will restore the
	 * atomic items and the division list.. but not the entries, nor race
	 * objects
	 **/
	@Override public void restore(Regatta a, Regatta b) {
		if (a == b)
			return;
		Regatta active = (Regatta) a;
		Regatta backup = (Regatta) b;

		active.setName(backup.getName());
		active.setDates(backup.getDates());
		active.setHostClub(backup.getHostClub());
		active.setJuryChair(backup.getJuryChair());
		active.setPro(backup.getPro());
		active.setComment(backup.getComment());
		active.setUseBowNumbers(backup.isUseBowNumbers());

		super.restore(active, backup);
	}

	@Override public void updateFields() {
		if (fRegatta != null) {
			this.getEditorParent().setTitle( 
					MessageFormat.format(res.getString("MainButtonRegattaTitle"),
					new Object[] { fRegatta.toString() }));
			
			fTextName.setText(fRegatta.getName());
			fTextHostClub.setText(fRegatta.getHostClub());
			fTextDates.setText(fRegatta.getDates());
			fTextPro.setText(fRegatta.getPro());
			fTextJuryChair.setText(fRegatta.getJuryChair());
			fCheckBoxUseBowNumbers.setSelected(fRegatta.isUseBowNumbers());
			fCheckBoxFinal.setSelected(fRegatta.isFinal());
			
			org.gromurph.javascore.gui.DefaultListModel listModelDivs = fRegatta.getDivisionModel();
			fListDivs.setModel(listModelDivs);

			fActionFleets.setEnabled(true);
			fActionSubDivisions.setEnabled(true);
			
			fTextIfEventId.setText( fRegatta.getIfEventId());
		} else {
			fTextName.setText(EMPTY);
			fTextHostClub.setText(EMPTY);
			fTextDates.setText(EMPTY);
			fTextPro.setText(EMPTY);
			fTextJuryChair.setText(EMPTY);
			fCheckBoxUseBowNumbers.setSelected(false);
			fCheckBoxFinal.setSelected(false);
			// fListModelDivs = null;
			// fListDivs.setModel( null);

			fActionFleets.setEnabled(false);
			fActionSubDivisions.setEnabled(false);
			fTextIfEventId.setText("");
		}
		
		updateStageScoringFields();
	}
	
	private void updateStageScoringFields() {
		if (fRegatta == null) {
			fCheckBoxMultistage.setSelected(false);
		} else {
			fCheckBoxMultistage.setSelected( fRegatta.isMultistage());
			if (fRegatta.isMultistage()) {	
        		fButtonStageScoring.setAction( fActionEditStages);
     		} else {
    			fButtonStageScoring.setAction( fActionEditScoringOptions);
    		}
		}	
	}

	/**
	 * Called whenever the value of the selection changes.
	 * 
	 * @param e
	 *            the event that characterizes the change.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() != fListDivs)
			return;

		if (!e.getValueIsAdjusting()) {
			fButtonEditDivision.setEnabled((fListDivs.getSelectedIndices().length == 1));
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		String pname = event.getPropertyName();
		if (event.getSource() == fRegatta) {
			if (pname.equals(Regatta.NAME_PROPERTY)) {
				this.getEditorParent().setTitle( 
						MessageFormat.format(res.getString("MainButtonRegattaTitle"),
						new Object[] { fRegatta.toString() }));
				fTextName.setText(fRegatta.getName());
			} else if (pname.equals(Regatta.HOSTCLUB_PROPERTY)) {
				fTextHostClub.setText(fRegatta.getHostClub());
			} else if (pname.equals(Regatta.DATES_PROPERTY)) {
				fTextDates.setText(fRegatta.getDates());
			} else if (pname.equals(Regatta.PRO_PROPERTY)) {
				fTextPro.setText(fRegatta.getPro());
			} else if (pname.equals(Regatta.JURYCHAIR_PROPERTY)) {
				fTextJuryChair.setText(fRegatta.getJuryChair());
			} else if (pname.equals(Regatta.USEBOWNUMBERS_PROPERTY)) {
				fCheckBoxUseBowNumbers.setSelected(fRegatta.isUseBowNumbers());
			} else if (pname.equals(Regatta.MULTISTAGE_PROPERTY)) {
				updateStageScoringFields();
			} else if (pname.equals(Regatta.FINAL_PROPERTY)) {
				fCheckBoxFinal.setSelected(fRegatta.isFinal());
			}
		}
	}

	public void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "regatta");
		setLayout(new GridBagLayout());
		int row = 0;

		// ======== build parameters panel

		JPanel fPanelParams = new JPanel(new GridBagLayout());
		java.awt.Insets insets = new java.awt.Insets(2,2,2,2);
		setGridBagInsets(insets);

		gridbagAdd(fPanelParams, new JLabel(res.getString("GenName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextName = new JTextFieldSelectAll(20);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("GenNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "regatta.fTextName");
		gridbagAdd(fPanelParams, fTextName, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		gridbagAdd(fPanelParams, new JLabel(res.getString("GenClub")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextHostClub = new JTextFieldSelectAll();
		fTextHostClub.setName("fTextHostClub");
		HelpManager.getInstance().registerHelpTopic(fTextHostClub, "regatta.fTextHostClub");
		gridbagAdd(fPanelParams, fTextHostClub, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		gridbagAdd(fPanelParams, new JLabel(res.getString("RegattaLabelDates")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextDates = new JTextFieldSelectAll();
		HelpManager.getInstance().registerHelpTopic(fTextDates, "regatta.fTextDates");
		gridbagAdd(fPanelParams, fTextDates, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		gridbagAdd(fPanelParams, new JLabel(res.getString("RegattaLabelPro")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);
		fTextPro = new JTextFieldSelectAll();
		HelpManager.getInstance().registerHelpTopic(fTextPro, "regatta.fTextPro");
		gridbagAdd(fPanelParams, fTextPro, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		gridbagAdd(fPanelParams, new JLabel(res.getString("RegattaLabelJuryChair")), 0, row, 1,
				GridBagConstraints.EAST, GridBagConstraints.NONE);
		fTextJuryChair = new JTextFieldSelectAll();
		HelpManager.getInstance().registerHelpTopic(fTextJuryChair, "regatta.fTextJuryChair");
		gridbagAdd(fPanelParams, fTextJuryChair, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		JLabel eventid = new JLabel(res.getString("IFEventIdLabel"));
		eventid.setToolTipText(res.getString("IFEventIdLabelTooltip"));
		gridbagAdd(fPanelParams, eventid, 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fTextIfEventId = new JTextFieldSelectAll(5);
		fTextIfEventId.setName("fTextIfEventId");
		fTextIfEventId.setToolTipText(res.getString("IFEventIdLabelTooltip"));
		// HelpManager.getInstance().registerHelpTopic(fTextIfEventId,
		// "lowpoint.fIfEventId");
		gridbagAdd(fPanelParams, fTextIfEventId, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
	
		// ====== build Other panel

		// JPanel fPanelOther = new JPanel( new GridBagLayout());

		row++;
		fCheckBoxUseBowNumbers = new JCheckBox(res.getString("RegattaLabelUseBowNumbers"));
		fCheckBoxUseBowNumbers.setToolTipText(res.getString("RegattaLabelUseBowNumbersToolTip"));
		fCheckBoxUseBowNumbers.setMnemonic('U');
		HelpManager.getInstance().registerHelpTopic(fCheckBoxUseBowNumbers, "regatta.fCheckBoxUseBowNumbers");
		gridbagAdd(fPanelParams, fCheckBoxUseBowNumbers, 1, row, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL);

		row++;
		fCheckBoxFinal = new JCheckBox(res.getString("RegattaLabelResultsAreFinal"));
		fCheckBoxFinal.setToolTipText(res.getString("RegattaLabelResultsAreFinalToolTip"));
		fCheckBoxFinal.setMnemonic('f');
		HelpManager.getInstance().registerHelpTopic(fCheckBoxFinal, "regatta.fCheckBoxFinal");
		gridbagAdd(fPanelParams, fCheckBoxFinal, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		fCheckBoxMultistage = new JCheckBox(res.getString("RegattaLabelMultistage"));
		fCheckBoxMultistage.setName("fCheckBoxMultistage");
		fCheckBoxMultistage.setToolTipText(res.getString("RegattaLabelMultistageToolTip"));
		fCheckBoxMultistage.setMnemonic('p');
		HelpManager.getInstance().registerHelpTopic(fCheckBoxMultistage, "regatta.fCheckBoxMultistage");
		gridbagAdd(fPanelParams, fCheckBoxMultistage, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		// ====== build DivisionsIn panel

		JPanel fPanelDivisions = new JPanel(new BorderLayout());
		fPanelDivisions.setBorder(BorderFactory.createTitledBorder(res.getString("RegattaTitleStartingClasses")));

		fListDivs = new JList();
		fListDivs.setVisibleRowCount(7);
		fListDivs.setToolTipText(res.getString("RegattaTitleStartingClassesToolTipHtml"));
		fPanelDivisions.add(new JScrollPane(fListDivs), BorderLayout.CENTER);
		fActionEditStartingDivision.setDivisionJList( fListDivs);

		JPanel fDivSouth = new JPanel(new GridBagLayout());

		fPanelDivisions.add(fDivSouth, BorderLayout.SOUTH);
		JPanel buttons = new JPanel(new java.awt.FlowLayout());
		gridbagAdd(fDivSouth, buttons, 0, 0, 2, GridBagConstraints.CENTER, GridBagConstraints.NONE);

		fButtonAddDivision = new JButton(res.getString("RegattaButtonAdd"));
		fButtonAddDivision.setMnemonic('A');
		fButtonAddDivision.setName("fButtonAddDivision");
		fButtonAddDivision.setToolTipText(res.getString("RegattaButtonAddToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonAddDivision, "regatta.fButtonAdd");
		buttons.add(fButtonAddDivision);

		fButtonRemoveDivision = new JButton(res.getString("RegattaButtonRemove"));
		fButtonRemoveDivision.setMnemonic('e');
		fButtonRemoveDivision.setName("fButtonRemoveDivision");
		fButtonRemoveDivision.setToolTipText(res.getString("RegattaButtonRemoveToolTip"));
		buttons.add(fButtonRemoveDivision);
		HelpManager.getInstance().registerHelpTopic(fButtonRemoveDivision, "regatta.fButtonRemove");

		fButtonEditDivision = new JButton( fActionEditStartingDivision);
		fButtonEditDivision.setName("fButtonEditDivision");
		fButtonEditDivision.setToolTipText(res.getString("RegattaButtonEditToolTip"));
		buttons.add(fButtonEditDivision);

		JTextPane pane = new JTextPane();
		pane.setBackground(fDivSouth.getBackground());
		pane.setText(res.getString("RegattaTitleStartingClassesToolTip"));
		Font ff = pane.getFont();
		pane.setFont(new Font(ff.getName(), ff.getStyle(), ff.getSize() - 1));
		pane.setPreferredSize(new Dimension(170, 80));
		gridbagAdd(fDivSouth, pane, 0, 2, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH);

		JPanel panelButtons = new JPanel();

		fButtonStageScoring = new JButton( fActionEditScoringOptions);
		fButtonStageScoring.setName("fButtonStageScoring");
		panelButtons.add(fButtonStageScoring);

		fActionFleets = new ActionEditFleets();
		fActionSubDivisions = new ActionEditSubDivisions();

		JButton button = new JButton(fActionFleets);
		button.setMnemonic(res.getString("ActionEditFleetsMnemonic").charAt(0));
		button.setToolTipText(res.getString("ActionEditFleetsToolTip"));
		panelButtons.add(button);

		button = new JButton(fActionSubDivisions);
		button.setMnemonic(res.getString("ActionEditSubDivisionsMnemonic").charAt(0));
		button.setToolTipText(res.getString("ActionEditSubDivisionsToolTip"));
		panelButtons.add(button);

		fButtonEditComment = new JButton(res.getString("RegattaButtonEditComment"));
		fButtonEditComment.setName("fButtonEditComment");
		fButtonEditComment.setMnemonic(res.getString("RegattaButtonEditCommentMnemonic").charAt(0));
		fButtonEditComment.setToolTipText(res.getString("RegattaButtonEditCommentToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonEditComment, "race.fButtonEditComment");
		panelButtons.add(fButtonEditComment);

		// ======= place the main panels
		gridbagAdd(this, fPanelParams, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets);

		gridbagAdd(this, fPanelDivisions, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets);

		gridbagAdd(this, panelButtons, 0, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets);
	}

	@Override public void start() {
		fTextName.addActionListener(this);

		fTextHostClub.addActionListener(this);
		fTextDates.addActionListener(this);
		fTextPro.addActionListener(this);
		fTextJuryChair.addActionListener(this);
		fTextIfEventId.addActionListener(this);


		fCheckBoxUseBowNumbers.addActionListener(this);
		fCheckBoxFinal.addActionListener(this);
		fCheckBoxMultistage.addActionListener(this);

		fButtonAddDivision.addActionListener(this);
		fButtonRemoveDivision.addActionListener(this);
		fButtonEditComment.addActionListener(this);

		fListDivs.addListSelectionListener(this);

		if (fRegatta != null)
			fRegatta.addPropertyChangeListener(this);

		updateEnabled();
	}

	@Override public void stop() {
		fTextName.removeActionListener(this);

		fTextHostClub.removeActionListener(this);
		fTextDates.removeActionListener(this);
		fTextPro.removeActionListener(this);
		fTextJuryChair.removeActionListener(this);
		fTextIfEventId.removeActionListener(this);

		if (fRegatta != null)
			fRegatta.removePropertyChangeListener(this);
		fCheckBoxUseBowNumbers.removeActionListener(this);
		fCheckBoxFinal.removeActionListener(this);
		fCheckBoxMultistage.removeActionListener(this);

		fButtonStageScoring.removeActionListener(this);
		fButtonAddDivision.removeActionListener(this);
		fButtonRemoveDivision.removeActionListener(this);
		fButtonEditComment.removeActionListener(this);

		fActionEditStages.hide();
		fActionEditScoringOptions.hide();
		fActionEditStartingDivision.hide();
	}

	@Override public void exitCancel() {
		super.exitCancel();

		JavaScore.subWindowClosing();
	}

	@Override public void exitOK() {
		super.exitOK();

		fRegatta.sortDivisions();
		JavaScore.updateMainTitle();
		JavaScore.backgroundSave();

		JavaScore.subWindowClosing();
	}

	JPanel fPanelMain;
	JTextFieldSelectAll fTextName;
	JTextFieldSelectAll fTextHostClub;
	JTextFieldSelectAll fTextDates;
	JTextFieldSelectAll fTextPro;
	JTextFieldSelectAll fTextJuryChair;
	JTextFieldSelectAll fTextIfEventId;
	JButton fButtonEditComment;
	JPanel fPanelDivisionsIn;

	JList<Division> fListDivs;

	JButton fButtonAddDivision;
	JButton fButtonRemoveDivision;
	JButton fButtonEditDivision;

	JCheckBox fCheckBoxUseBowNumbers;
	JCheckBox fCheckBoxFinal;
	JCheckBox fCheckBoxMultistage;
	JButton fButtonStageScoring;
	Action fActionFleets;
	Action fActionSubDivisions;

	public void actionPerformed(java.awt.event.ActionEvent event) {
		Object object = event.getSource();
		if (object == fTextName)
			fTextName_actionPerformed();
		else if (object == fTextHostClub)
			fTextHostClub_actionPerformed();
		else if (object == fTextDates)
			fTextDates_actionPerformed();
		else if (object == fTextPro)
			fTextPro_actionPerformed();
		else if (object == fTextJuryChair)
			fTextJuryChair_actionPerformed();
		else if (object == fCheckBoxUseBowNumbers)
			fCheckBoxUseBowNumbers_actionPerformed();
		else if (object == fCheckBoxFinal)
			fCheckBoxFinal_actionPerformed();
		else if (object == fCheckBoxMultistage)
			fCheckBoxMultistage_actionPerformed();
		else if (object == fButtonAddDivision)
			fButtonAddDivision_actionPerformed();
		else if (object == fButtonRemoveDivision)
			fButtonRemoveDivision_actionPerformed();
		else if (object == fButtonEditComment)
			fButtonEditComment_actionPerformed();
		else if (object == fTextIfEventId) 
			fTextIfEventId_actionPerformed();

		if (getEditorParent() != null)
			getEditorParent().eventOccurred(this, event);
	}

	JPanel fPanelComment = null;
	JTextPane fTextComment = null;

	public final static String COMMENT_TITLE = res.getString("RegattaCommentTitle");

	void fButtonEditComment_actionPerformed() {
		if (fPanelComment == null) {
			JPanel panel = new JPanel(new BorderLayout());
			fTextComment = new JTextPane();
			fTextComment.setText(fRegatta.getComment());
			fTextComment.setName("fTextComments");
			panel.add(new JScrollPane(fTextComment), BorderLayout.CENTER);
			panel.setPreferredSize(new Dimension(300, 100));
			fPanelComment = panel;
		}

		int answer = JOptionPane.showConfirmDialog(this, fPanelComment, COMMENT_TITLE, JOptionPane.OK_CANCEL_OPTION);

		if (answer == JOptionPane.OK_OPTION) {
			fRegatta.setComment(fTextComment.getText());
		}
	}

	void fTextName_actionPerformed() {
		fRegatta.setName(fTextName.getText());
		JavaScore.updateMainTitle();
	}

	void fTextHostClub_actionPerformed() {
		fRegatta.setHostClub(fTextHostClub.getText());
	}

	void fTextDates_actionPerformed() {
		fRegatta.setDates(fTextDates.getText());
	}

	void fTextPro_actionPerformed() {
		fRegatta.setPro(fTextPro.getText());
	}

	void fTextIfEventId_actionPerformed() {
		if (JavaScoreProperties.getRegatta() != null) {
			JavaScoreProperties.getRegatta().setIfEventId(fTextIfEventId.getText());
		}
	}

	void fTextJuryChair_actionPerformed() {
		fRegatta.setJuryChair(fTextJuryChair.getText());
	}

	void fCheckBoxUseBowNumbers_actionPerformed() {
		fRegatta.setUseBowNumbers(fCheckBoxUseBowNumbers.isSelected());
	}

	void fCheckBoxMultistage_actionPerformed() {
		boolean toMulti = fCheckBoxMultistage.isSelected();
		fRegatta.setMultistage( toMulti);
		if (toMulti) {
			fButtonStageScoring.setAction( fActionEditStages);
		} else {
			fButtonStageScoring.setAction( fActionEditScoringOptions);
		}
		updateFields();
	}

	void fCheckBoxFinal_actionPerformed() {
		boolean toFinal = fCheckBoxFinal.isSelected();
		boolean changed = (fRegatta.isFinal() != toFinal);
		if (changed && toFinal) {
			// changing setting to final, confirm
			int option = JOptionPane.showConfirmDialog(this, res.getString("RegattaMessageFinalVerify"), res
					.getString("RegattaTitleFinalVerify"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				fRegatta.setFinal(toFinal);
			} else {
				fCheckBoxFinal.setSelected(false);
			}
		} else if (changed && !toFinal) {
			// changing setting to provisional, confirm
			int option = JOptionPane.showConfirmDialog(this, res.getString("RegattaMessageProvisionalVerify"), res
					.getString("RegattaTitleProvisionalVerify"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				fRegatta.setFinal(toFinal);
			}
		}
	}

	void fButtonAddDivision_actionPerformed() {
		DivisionList divs = (DivisionList) DivisionList.getMasterList().clone();
		divs.removeAll(fRegatta.getDivisions());

		Division newDiv = (Division) JOptionPane.showInputDialog(
				this, 
				res.getString("RegattaMessageNewClass"), 
				res.getString("RegattaTitleNewClass"), 
				JOptionPane.QUESTION_MESSAGE, null, divs.toArray(), divs.get(0));

		if (newDiv != null) {
			fRegatta.addDivision( newDiv);
			fListDivs.setModel( fRegatta.getDivisions().getListModel());
		}

		confirmOneDivision();
		updateEnabled();
	}

	void fButtonRemoveDivision_actionPerformed() {
		EntryList entries = new EntryList();
		for ( Division div : fListDivs.getSelectedValuesList()) {
			entries.addAll(div.getEntries());
		}
		boolean ok = true;
		if (entries.size() > 0) {
			String msg = null;
			if (entries.size() == 1) {
				msg = res.getString("RegattaMessageDeleteClassSingular");
			} else {
				msg = MessageFormat.format(res.getString("RegattaMessageDeleteClassPlural"),
						new Object[] { new Integer(entries.size()) });
			}
			ok = Util.confirm(msg);
		}
		if (ok) {
			for (Division div : fListDivs.getSelectedValuesList()) {
				fRegatta.removeDivision(div);
			}

			moveEntries( entries, AbstractDivision.NONE);
		}
		fListDivs.setModel( fRegatta.getDivisions().getListModel());
		confirmOneDivision();
		updateEnabled();
	}

	protected void updateEnabled() {
		if (fRegatta == null) {
			fButtonRemoveDivision.setEnabled(false);
			fButtonAddDivision.setEnabled(false);
			fButtonStageScoring.setEnabled(false);
		} else {
			fButtonRemoveDivision.setEnabled(fRegatta.getNumDivisions() > 0);

			DivisionList rDivs = fRegatta.getDivisions();

			fButtonAddDivision.setEnabled(!(rDivs.containsAll(DivisionList.getMasterList())));
			fButtonStageScoring.setEnabled(true);
		}
		fButtonEditDivision.setEnabled((fListDivs.getSelectedIndices().length == 1));
		
		if (fRegatta.isMultistage()) {
			// can't take a multi stage regatta with more than one stage back to single stage
			fCheckBoxMultistage.setEnabled( ((MultiStage) fRegatta.getScoringManager()).getNumStages() <= 1);
		} else {
			fCheckBoxMultistage.setEnabled(true);
		}
	}

	private void confirmOneDivision() {
		if (fRegatta.getNumDivisions() == 1) {
			Division div = fRegatta.getDivisions().get(0);
			int answer = JOptionPane.showConfirmDialog(this, MessageFormat.format(res
					.getString("RegattaMessageLastClass"), new Object[] { div.toString() }), res
					.getString("RegattaTitleChangeDivisions"), JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) {
				moveEntries(fRegatta.getAllEntries(), div);
			}
		}
	}

	private void moveEntries(EntryList entries, Division div) {
		WarningList warnings = new WarningList();
		warnings.setHeader(res.getString("RegattaMessageWarningNoDivision"));
		for (Entry entry : entries) {
			try {
				entry.setDivision(div);
			} catch (RatingOutOfBoundsException roe) {
				try {
					Rating rtg = RatingManager.createRating(div, div.getMinRating().getPrimaryValue());
					entry.setRating(rtg);
					entry.setDivision(div);
				} catch (RatingOutOfBoundsException roe2) {
				}

				if (!div.isOneDesign() || !entry.getDivision().isOneDesign()) {
					warnings.add(entry.toString());
				}
			}
		}
		warnings.showPopup(this);
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		
		Regatta b = new Regatta();
		b.setMultistage(true);
		Stage s = new Stage();
		s.setName(Stage.FLEET);
		((MultiStage) b.getScoringManager()).addStage(s);
		
		b.setName("Snipe Intergalactices");
		b.setPro("Sandy Grosvenor");

		fFrame.setObject(b);
		fFrame.setVisible(true);
	}

}
/**
 * $Log: PanelRegatta.java,v $ Revision 1.7 2006/05/19 05:48:43 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.6 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/02 22:30:21 sandyg re-laidout scoring options, added
 * alternate A8.2 only tiebreaker, added unit tests for both
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.21.4.3 2005/11/30 02:51:25 sandyg added auto focuslost to
 * JTextFieldSelectAll. Removed focus lost checks on text fields in panels.
 * 
 * Revision 1.21.4.2 2005/11/19 20:34:55 sandyg last of java 5 conversion,
 * created swingworker, removed threads packages.
 * 
 * Revision 1.21.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.21 2005/04/27 02:45:47 sandyg Added Yardstick, and added Yardstick
 * and IRC all to GUI. Portsmouth now trivial subclass of yardstick
 * 
 * Revision 1.20 2004/05/06 02:11:50 sandyg Beta support for revised
 * Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.19 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.18 2003/07/11 02:20:16 sandyg fixed problem with scoring system
 * combo box not getting updated
 * 
 * Revision 1.17 2003/05/17 22:44:45 sandyg title of open frame fixed
 * 
 * Revision 1.16 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.15 2003/04/27 21:35:35 sandyg more cleanup of unused variables...
 * ALL unit tests now working
 * 
 * Revision 1.14 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.13 2003/03/27 02:47:00 sandyg Completes fixing [ 584501 ] Can't
 * change division splits in open reg
 * 
 * Revision 1.12 2003/03/19 02:38:24 sandyg made start() stop() abstract to
 * BaseEditor, the isStarted check now done in BaseEditor.startUp and
 * BaseEditor.shutDown().
 * 
 * Revision 1.11 2003/03/16 20:38:32 sandyg 3.9.2 release: encapsulated changes
 * to division list in Regatta, fixed a bad bug in PanelDivsion/Rating
 * 
 * Revision 1.10 2003/02/12 02:23:11 sandyg getting ready for jfcunit and
 * editing division in open regatta
 * 
 * Revision 1.9 2003/01/06 00:32:36 sandyg replaced forceDivision and
 * forceRating statements
 * 
 * Revision 1.8 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
