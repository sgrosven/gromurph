// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRace.java,v 1.10 2006/05/19 05:48:42 sandyg Exp
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.actions.ActionEditFinishes;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JCalendarPopup;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;
import org.gromurph.util.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelRace extends BaseEditor<Race> implements ActionListener, 
		PropertyChangeListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	Race fRace;

	PanelRaceStartInfo fPanelDivInfo;
	JTextField	fTextName;
	JCalendarPopup fPopupStartDate;
	JButton fButtonEditFinishes;
	JButton fButtonEditRoundings;
	JButton fButtonEditComment;
	JButton fButtonAdvanced;

	private ActionEditFinishes actionEditFinishes = new ActionEditFinishes();
	
	private DialogBaseEditor fAdvancedEditor = null;
	private SwingWorker swAdvanced;

	public PanelRace(BaseEditorContainer parent) {
		super(parent);
		
		addFields();

		initializeDialogs();
	}

	private void initializeDialogs() {
		actionEditFinishes.startInitializing();
		
		swAdvanced = new SwingWorker() {
			@Override public Object construct() {
				
				DialogBaseEditor dialog = new DialogBaseEditor(Util.getParentJFrame(PanelRace.this),
						PanelRaceAdvanced.RES_TITLE, false);

				return dialog;
			}
		};
		swAdvanced.start();

	}

	@Override public void setObject(Race inObj) {
		if (inObj == fRace) return;
		super.setObject(inObj);

		if (isStarted && fRace != null) fRace.removePropertyChangeListener(this);
		fRace = (Race) inObj;
		actionEditFinishes.setRounding(fRace, null);
		updateFields();
		if (isStarted && fRace != null) fRace.addPropertyChangeListener(this);
	}

	/**
	 * returns the message that should be shown to confirm a deletion The question will start with:
	 * "Are you sure you want to delete this item?" And this footnote will be added to the end of the question
	 * 
	 * Expect subclasses to override this to add additional information
	 */
	public String getConfirmDeleteFootnote() {
		return res.getString("RaceMessageDeleteRace");
	}

	@Override public void restore(Race a, Race b) {
		if (a == b)
			return;
		Race active = (Race) a;
		Race backup = (Race) b;

		active.setName(backup.getName());
		active.setStartDate(backup.getStartDate());

		if (active.getRegatta() != null) {
			// make sure fields are exist thru length of div list
			for (Division div : active.getRegatta().getDivisions()) {
				active.setIsRacing(div, div.isRacing(backup));
				active.setStartTime(div, backup.getStartTimeRaw(div));
				active.setLength(div, backup.getLength(div));
				active.setNextDay( div, backup.isNextDay(div));
			}
		}

		super.restore(active, backup);
	}

	public void enableFields(boolean bb) {
		boolean b = (fRace != null && bb);
		fTextName.setEnabled(b);
		fPopupStartDate.setEnabled(b);
		fButtonEditFinishes.setEnabled(b);
		fButtonAdvanced.setEnabled(b);
		fButtonEditRoundings.setEnabled(b);
		fButtonEditComment.setEnabled(b);
	}

	public boolean isSubWindowOpen() {
		boolean b;
		if (actionEditFinishes == null)
			b = false;
		else
			b = actionEditFinishes.isVisible();
		return b;
	}

	public void updateEnabled() {
//		enableFields(!isSubWindowOpen());
//		if (getEditorParent() != null)
//			getEditorParent().updateEnabled();
	}

	@Override public void updateFields() {

		if (fRace != null) {
			fTextName.setText(fRace.getName());

			// make sure date is not null, set it to today if found null
			fPopupStartDate.setDate(fRace.getStartDate());
		} else {
			fTextName.setText(EMPTY);
		}
		
		fPanelDivInfo.setRace(fRace);
		enableFields( fRace != null);
	}

	public void addFields() {
		try {
			HelpManager.getInstance().registerHelpTopic(this, "race");
		} catch (NullPointerException e) {
			logger.error(" PanelRace.addFields - HelpManager generates NPE");
			// should only happen when testing
		}
		setLayout(new GridBagLayout());

		JPanel panelNorth = new JPanel(new GridBagLayout());
		panelNorth.setName("panelNorth");
		JPanel panelSouth = new JPanel(new GridBagLayout());
		panelSouth.setName("panelSouth");
		JPanel panelCenter = new JPanel(new BorderLayout());
		panelCenter.setName("panelCenter");

		gridbagAdd(this, panelNorth, 0, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
		gridbagAdd(this, panelCenter, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
		gridbagAdd(this, panelSouth, 0, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);

		int row = 0;
		gridbagAdd(panelNorth, new JLabel(res.getString("GenName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll(20);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("GenNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "race.fTextName");
		gridbagAdd(panelNorth, fTextName, 1, row, 2, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		gridbagAdd(panelNorth, new JLabel(res.getString("RaceLabelStartDate")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		// set the value model and validator
		fPopupStartDate = new JCalendarPopup(res.getString("RaceLabelStartDate"), null);
		fPopupStartDate.setName("fPopupStartDate");

		HelpManager.getInstance().registerHelpTopic(fPopupStartDate, "race.fPopupStartDate");
		gridbagAdd(panelNorth, fPopupStartDate, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fButtonAdvanced = new JButton(res.getString("RaceButtonAdvanced"));
		fButtonAdvanced.setName("fButtonAdvanced");
		fButtonAdvanced.setMnemonic(res.getString("RaceButtonAdvancedMnemonic").charAt(0));
		fButtonAdvanced.setName("fButtonAdvanced");
		fButtonAdvanced.setToolTipText(res.getString("RaceButtonAdvancedToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonAdvanced, "race.fButtonAdvanced");
		gridbagAdd(panelNorth, fButtonAdvanced, 2, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		// Center Panel: div inf
		fPanelDivInfo = new PanelRaceStartInfo();
		panelCenter.add( fPanelDivInfo, BorderLayout.CENTER);

		// South panel: EDIT FINISH EDIT ROUNDINGS EDIT COMMENT
		JPanel panelButtons = addFieldsButtonsPanel();
		gridbagAdd(panelSouth, panelButtons, 0, 0, 3, GridBagConstraints.CENTER, GridBagConstraints.NONE);
	}

	private JPanel addFieldsButtonsPanel() {
		JPanel panelButtons = new JPanel(new FlowLayout());
		fButtonEditFinishes = new JButton( actionEditFinishes);
		fButtonEditFinishes.setName("fButtonEditFinishes");
		fButtonEditFinishes.setToolTipText(res.getString("RaceButtonEditFinishToolTip"));
		panelButtons.add(fButtonEditFinishes);

		fButtonEditRoundings = new JButton(res.getString("RaceButtonRoundings"));
		fButtonEditRoundings.setName("fButtonEditRoundings");
		fButtonEditRoundings.setMnemonic(res.getString("RaceButtonEditRoundingsMnemonic").charAt(0));
		fButtonEditRoundings.setToolTipText(res.getString("RaceButtonRoundingsToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonEditRoundings, "race.fButtonEditRoundings");
		panelButtons.add(fButtonEditRoundings);

		fButtonEditComment = new JButton(res.getString("RaceButtonEditComment"));
		fButtonEditComment.setName("fButtonEditComment");
		fButtonEditComment.setMnemonic(res.getString("RaceButtonEditCommentMnemonic").charAt(0));
		fButtonEditComment.setToolTipText(res.getString("RaceButtonEditCommentToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonEditComment, "race.fButtonEditComment");
		panelButtons.add(fButtonEditComment);
		return panelButtons;
	}

	@Override public void start() {

		updateFields();

		SailTime.clearLastTime();
		
		fTextName.addActionListener(this);
		fButtonAdvanced.addActionListener(this);
		fButtonEditRoundings.addActionListener(this);
		fButtonEditComment.addActionListener(this);
		fPopupStartDate.addPropertyChangeListener(this);
		
		fPanelDivInfo.startUp();
	}

	@Override public void stop() {
		
		fTextName.addActionListener(this);
		fButtonAdvanced.removeActionListener(this);
		fButtonEditRoundings.removeActionListener(this);
		fButtonEditComment.removeActionListener(this);
		fPopupStartDate.removePropertyChangeListener(this);

		actionEditFinishes.hide();
		
		fPanelDivInfo.shutDown();
		updateEnabled();
	}

	@Override public void exitOK() {
		super.exitOK();
		JavaScore.backgroundSave();
		JavaScore.subWindowClosing();
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == fTextName)
			fTextName_actionPerformed();
		else if (object == fButtonAdvanced)
			fButtonAdvanced_actionPerformed();
		else if (object == fButtonEditRoundings)
			fButtonEditRoundings_actionPerformed();
		else if (object == fButtonEditComment)
			fButtonEditComment_actionPerformed();

		if (getEditorParent() != null)
			getEditorParent().eventOccurred(this, event);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String propertyName = (event == null) ? "" : event.getPropertyName();
		if (propertyName == null) return;
		if (event.getSource() == this.fPopupStartDate && propertyName.equals("text"))
			fRace.setStartDate(fPopupStartDate.getDate());
	}

	void fButtonAdvanced_actionPerformed() {
		// fRace.synchFinishListWithEntries( );
		if (fAdvancedEditor == null) {
			try {
				fAdvancedEditor = (DialogBaseEditor) swAdvanced.get();
			} catch (Exception e) {
				Logger l = LoggerFactory.getLogger(this.getClass());
				l.error( "Exception=" + e.toString(), e);
			}
			fAdvancedEditor.setObject(fRace, new PanelRaceAdvanced( this.getEditorParent()));
		} else {
			fAdvancedEditor.setObject(fRace, new PanelRaceAdvanced( this.getEditorParent()));
		}
		fAdvancedEditor.setVisible(true);
	}

	void fButtonEditRoundings_actionPerformed() {
		String[] marks = Race.getAllRoundingNames();

		String markName = (String) JOptionPane.showInputDialog(
				this, res.getString("RaceTitleChooseMark"), 
				res.getString("RaceMessageChooseMark"), 
				JOptionPane.QUESTION_MESSAGE, 
				null, marks, marks[0]);

		actionEditFinishes.setRounding(fRace, markName);
		actionEditFinishes.show();
	}

	JPanel fPanelComment = null;
	JTextPane fTextComment = null;
	
	// for testing only
	public JTextPane test_getTextComment() { return fTextComment;}

	public final static String COMMENT_TITLE = res.getString("RaceCommentTitle");

	void fButtonEditComment_actionPerformed() {
		if (fPanelComment == null) {
			JPanel panel = new JPanel(new BorderLayout());
			fTextComment = new JTextPane();
			fTextComment.setText(fRace.getComment());
			fTextComment.setName("fTextComments");
			panel.add(new JScrollPane(fTextComment), BorderLayout.CENTER);
			panel.setPreferredSize(new Dimension(300, 100));
			fPanelComment = panel;
		}

		int answer = JOptionPane.showConfirmDialog(
				this, fPanelComment, COMMENT_TITLE, JOptionPane.OK_CANCEL_OPTION);

		if (answer == JOptionPane.OK_OPTION) {
			fRace.setComment(fTextComment.getText());
		}
	}

	void fTextName_actionPerformed() {
		if (fRace != null && fTextName != null)
			fRace.setName(fTextName.getText());
	}

	private String[] dtFormatStrings = new String[] { "HH:mm", "HH:mm:ss", "HHmmss", "HHmm" };

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		Regatta reg = new Regatta();
		Division tot = new Division("PHRF A", new RatingPhrf(0), new RatingPhrf(100));
		reg.addDivision(tot);

		Division pb = new Division("PHRF b", new RatingPhrf(0), new RatingPhrf(10));
		Division pc = new Division("PHRF c", new RatingPhrf(11), new RatingPhrf(20));
		Division pd = new Division("PHRF d", new RatingPhrf(21), new RatingPhrf(30));
		Division pe = new Division("PHRF e", new RatingPhrf(31), new RatingPhrf(40));
		Division pf = new Division("PHRF f", new RatingPhrf(41), new RatingPhrf(100));

		reg.addDivision(pb);
		reg.addDivision(pc);
		reg.addDivision(pd);
		reg.addDivision(pe);
		reg.addDivision(pf);

		Entry ent = new Entry();
		try {
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(tot);
			ent.setRating(new RatingPhrfTimeOnTime(30));
			reg.addEntry(ent);

			ent = new Entry();
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(pb);
			ent.setRating(new RatingPhrfTimeOnTime(05));
			reg.addEntry(ent);

			ent = new Entry();
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(pc);
			ent.setRating(new RatingPhrfTimeOnTime(15));
			reg.addEntry(ent);

			ent = new Entry();
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(pd);
			ent.setRating(new RatingPhrfTimeOnTime(25));
			reg.addEntry(ent);

			ent = new Entry();
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(pe);
			ent.setRating(new RatingPhrfTimeOnTime(35));
			reg.addEntry(ent);

			ent = new Entry();
			ent.setBoat(new Boat("bname", "123", "big o"));
			ent.setDivision(pf);
			ent.setRating(new RatingPhrfTimeOnTime(45));
			reg.addEntry(ent);

		} catch (RatingOutOfBoundsException e) {
			e.printStackTrace();
		}

		Race race = new Race();
		reg.addRace(race);

		fFrame.setObject(race);
		fFrame.setVisible(true);
	}
}
/**
 * $Log: PanelRace.java,v $ Revision 1.10 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.9 2006/04/15 23:40:45 sandyg fixed mystery resorts of division list
 * 
 * Revision 1.8 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.7 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.5 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.4 2006/01/14 21:06:56 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.3 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/08 04:18:33 sandyg Fixed reporting error on finals divisions, cleaned up gui on qual/final races
 * (hiding divisions that should not have their "participating" flags changed)
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.19.4.4 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.19.4.3 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
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
