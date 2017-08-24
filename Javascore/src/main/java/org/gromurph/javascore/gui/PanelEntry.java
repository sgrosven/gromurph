// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelEntry.java,v 1.7 2006/09/03 20:12:21 sandyg Exp $
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Person;
import org.gromurph.util.Util;

/**
 * The Entry class handles information related to a entry in a race, the combination of a boat, its crew, skipper
 **/
public class PanelEntry extends BaseEditor<Entry> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	Entry fEntry;
	
	private Regatta getRegatta() { return JavaScoreProperties.getRegatta(); }

	public PanelEntry(BaseEditorContainer parent) {
		super(parent);
		addFields();
	}

	/**
	 * returns the message that should be shown to confirm a deletion The question will start with:
	 * "Are you sure you want to delete this item?" And this footnote will be added to the end of the question
	 * 
	 * Expect subclasses to override this to add additional information
	 */
	public String getConfirmDeleteFootnote() {
		return res.getString("EntryMessageDeleteEntry");
	}

	public Entry getEntry() {
		return fEntry;
	}

	@Override public void setObject(Entry inObj) {
		if (fEntry != inObj) {

			if (fEntry != null) {
				fTableCrew.saveChanges();
				fEntry.removePropertyChangeListener(this);
			}
			
			fEntry = (Entry) inObj;
			
			if (isVisible() && fEntry != null)
				fEntry.addPropertyChangeListener(this);

			fPanelSubDivisions.setEntry(fEntry);
			fTableCrew.setEntry( fEntry);

		}
		super.setObject(inObj);
		
		setFirstFocus();
	}
	
	private void updateDivisionsCombo() {
		// change combo model for div list only if necessary
		DivisionList dlist = null;
		if (getRegatta() == null) {
			dlist = DivisionList.getMasterList();
		} else {
			dlist = getRegatta().getDivisions();
		}

		Vector<Division> combo = new Vector<Division>();
		combo.add(AbstractDivision.NONE);
		combo.addAll(dlist);
		
		fComboDivision.setModel(new DefaultComboBoxModel(combo));
	}

	@Override public void restore(Entry a, Entry b) {
		if (a == b)
			return;
		
		Entry active = (Entry) a;
		Entry backup = (Entry) b;

		active.getBoat().setName(backup.getBoat().getName());
		active.setSailId(backup.getBoat().getSailId());
		try {
			active.setDivision(backup.getDivision());
		} catch (Exception ignored) {
			Util.showError(ignored, true);
		}

		active.getSkipper().setFirst(backup.getSkipper().getFirst());
		active.getSkipper().setLast(backup.getSkipper().getLast());
		active.getSkipper().setSailorId(backup.getSkipper().getSailorId());

		active.getCrew().setFirst(backup.getCrew().getFirst());
		active.getCrew().setLast(backup.getCrew().getLast());
		active.getCrew().setSailorId(backup.getCrew().getSailorId());

		active.setBow(backup.getBow());
		active.setMnaNumber(backup.getMnaNumber());
		active.setRsaNumber(backup.getRsaNumber());
		active.setClub(backup.getClub());

		super.restore(active, backup);
	}

	public void enableFields(boolean b) {
		fTextBow.setEnabled(b);
		fTextSail.setEnabled(b);
		fTextName.setEnabled(b);
		fTableCrew.setEnabled(b);
		fTextRsa.setEnabled(b);
		fTextMna.setEnabled(b);
		fTextClub.setEnabled(b);

		fPanelSubDivisions.setEnabled(b);
	}

	@Override public void updateFields() {

		boolean showbow = false;
		boolean showSubs = false;

		Regatta reg = getRegatta(); // should never be null
		
		updateDivisionsCombo();
		showbow = reg.isUseBowNumbers();
		showSubs = (reg.getNumSubDivisions() > 0);

		if (showSubs) fPanelSubDivisions.updateFields();
		fPanelSubDivisions.setVisible(showSubs);
		fLabelSubDivisions.setVisible(showSubs);

		fTextBow.setVisible(showbow);
		fLabelBow.setVisible(showbow);

		if (fEntry != null) {
			fTextBow.setText(fEntry.getBow().toString());
			fTextName.setText(fEntry.getBoat().getName());
			fTextSail.setText(fEntry.getBoat().getSailId().toString());

			fTextMna.setText(fEntry.getMnaNumber());
			fTextRsa.setText(fEntry.getRsaNumber());
			fTextClub.setText(fEntry.getClub());
			
			Object ediv = fEntry.getDivision();
			Object io;
			for (int i = 0; i < fComboDivision.getModel().getSize(); i++) {
				io = fComboDivision.getModel().getElementAt(i);
			}
			fComboDivision.setSelectedItem(fEntry.getDivision());

			fPanelSubDivisions.setVisible(showSubs);
		} else {
			fTextName.setText(EMPTY);
			fTextBow.setText(EMPTY);
			fTextSail.setText(EMPTY);

			fTextMna.setText(EMPTY);
			fTextRsa.setText(EMPTY);
			fTextClub.setText(EMPTY);
			fComboDivision.setSelectedItem(null);

			fPanelSubDivisions.setVisible(false);

			//enableFields(false);
		}

		fLabelMisc1
				.setText(" " + JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY) + " ");
		fLabelMisc2
				.setText(" " + JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY) + " ");

		updateRatingPanel(false);
	}

	public void vetoableChange(PropertyChangeEvent de) throws PropertyVetoException {
		de.getSource();
		updateFields();
	}

	public void propertyChange(PropertyChangeEvent ev) {
		try {
			vetoableChange(ev);
		} catch (Exception e) {}
	}

	JComboBox fComboDivision;
	JLabel fLabelBow;
	PanelEntrySubDivisions fPanelSubDivisions;
	
	PanelRating fPanelRating;

	JTextFieldSelectAll fTextSail;
	JTextFieldSelectAll fTextBow;
	JTextFieldSelectAll fTextName;

	EntryCrewTable fTableCrew;

	private final static int TABLE_WIDTH = 300; //10 + COLWIDTH_ISAFID + COLWIDTH_LAST + COLWIDTH_FIRST + COLWIDTH_NUM;
	private final static int TABLE_HEIGHT = 70;


	JTextFieldSelectAll fTextRsa;
	JTextFieldSelectAll fTextMna;
	JTextFieldSelectAll fTextClub;
	JLabel fLabelSubDivisions;
	JLabel fLabelMisc1;
	JLabel fLabelMisc2;

	public void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "entries");

		setLayout(new GridBagLayout());
		setGridBagInsets(new java.awt.Insets(2, 2, 2, 2));

		int row = 0;
		gridbagAdd(new JLabel(res.getString("GenDivision")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		JPanel divPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridbagAdd(divPanel, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		fComboDivision = new JComboBox();
		fComboDivision.setName("fComboDivision");
		fComboDivision.setToolTipText(res.getString("EntryClassToolTip"));
		HelpManager.getInstance().registerHelpTopic(fComboDivision, "entries.fComboDivision");
		//gridbagAdd(fComboDivision, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		divPanel.add(fComboDivision);

		fPanelRating = new PanelRating( this.getEditorParent());
		//gridbagAdd(fPanelRatingHolder, 2, row, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		divPanel.add(fPanelRating);

		row++;
		gridbagAdd(new JLabel(res.getString("GenSailNum")), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		JPanel sailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridbagAdd(sailPanel, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		fTextSail = new JTextFieldSelectAll(8);
		fTextSail.setName("fTextSail");
		fTextSail.setToolTipText(res.getString("GenSailToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextSail, "entries.fTextSail");
		//gridbagAdd(fTextSail, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		sailPanel.add(fTextSail);

		fLabelBow = new JLabel(res.getString("GenBowNum"));
		fTextSail.setName("fTextSail");
		//gridbagAdd(fLabelBow, 2, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		sailPanel.add(fLabelBow);

		fTextBow = new JTextFieldSelectAll(4);
		fTextBow.setName("fTextBow");
		fTextBow.setToolTipText(res.getString("GenBowToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextBow, "entries.fTextBow");
		//gridbagAdd(fTextBow, 3, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		sailPanel.add(fTextBow);

		row++;
		gridbagAdd(new JLabel(res.getString("GenBoatName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll(21);
		fTextName.setName("fTextName");
		fTextName.setToolTipText(res.getString("GenBoatNameToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextName, "entries.fTextName");
		gridbagAdd(fTextName, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		gridbagAdd(new JLabel(res.getString("EntryLabelCrew")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTableCrew = new EntryCrewTable();
		fTableCrew.setName("fTableCrew");
		fTableCrew.setToolTipText(res.getString("TableCrewToolTip"));
		JScrollPane scrollCrew = new JScrollPane(fTableCrew);
		scrollCrew.setMinimumSize(new Dimension(TABLE_WIDTH + 20, TABLE_HEIGHT + 20));
		scrollCrew.setPreferredSize(new Dimension(TABLE_WIDTH + 20, TABLE_HEIGHT + 70));

		gridbagAdd(scrollCrew, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);
		HelpManager.getInstance().registerHelpTopic(fTableCrew, "entries.fTableCrew");

		row++;
		String rsa = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY);
		fLabelMisc1 = new JLabel(rsa);
		gridbagAdd(fLabelMisc1, 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		JPanel panelNums = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridbagAdd(panelNums, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		fTextRsa = new JTextFieldSelectAll(8);
		panelNums.add(fTextRsa);
		fTextRsa.setToolTipText(res.getString("PrefsLabelMisc1ToolTip"));
		fTextRsa.setName("fTextRsa");
		HelpManager.getInstance().registerHelpTopic(fTextRsa, "entries.fTextRsa");

		rsa = " " + JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY) + " ";
		fLabelMisc2 = new JLabel(rsa);
		panelNums.add(fLabelMisc2);

		fTextMna = new JTextFieldSelectAll(8);
		fTextMna.setName("fTextMna");
		panelNums.add(fTextMna);
		fTextMna.setToolTipText(res.getString("PrefsLabelMisc2ToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextMna, "entries.fTextMna");

		row++;
		gridbagAdd(new JLabel(res.getString("GenClub")), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fTextClub = new JTextFieldSelectAll(21);
		fTextClub.setName("fTextClub");
		fTextClub.setToolTipText(res.getString("GenClubToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextClub, "entries.fTextClub");
		gridbagAdd(fTextClub, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		fLabelSubDivisions = new JLabel(res.getString("EntryLabelSubDivisionPanel"));
		gridbagAdd(fLabelSubDivisions, 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		fPanelSubDivisions = new PanelEntrySubDivisions();
		gridbagAdd(this, fPanelSubDivisions, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	}

	@Override public void start() {
		fComboDivision.addActionListener(this);
		fTextSail.addActionListener(this);
		fTextBow.addActionListener(this);
		fTextName.addActionListener(this);
		fTextMna.addActionListener(this);
		fTextRsa.addActionListener(this);
		fTextClub.addActionListener(this);
		
		fPanelSubDivisions.startUp();
		fPanelRating.startUp();

		if (fEntry != null) fEntry.addPropertyChangeListener(this);
		fTableCrew.addFocusListener( fTableCrew);

	}

	@Override public void stop() {
		fComboDivision.removeActionListener(this);
		fTextSail.removeActionListener(this);
		fTextBow.removeActionListener(this);
		fTextName.removeActionListener(this);
		fTextMna.removeActionListener(this);
		fTextRsa.removeActionListener(this);
		fTextClub.removeActionListener(this);

		fPanelSubDivisions.shutDown();
		fPanelRating.shutDown();
		if (fEntry != null) fEntry.removePropertyChangeListener(this);
		fTableCrew.removeFocusListener( fTableCrew);

	}

	@Override public void exitOK() {
		super.exitOK();
		fTableCrew.saveChanges();
		JavaScore.backgroundSave();
		JavaScore.subWindowClosing();
	}

	public void actionPerformed(ActionEvent event) {
		lastField = "";
		Object object = event.getSource();
		if (object == fTextSail)
			fTextSail_actionPerformed();
		else if (object == fTextBow)
			fTextBow_actionPerformed();
		else if (object == fTextName)
			fTextName_actionPerformed();
		else if (object == fTextMna)
			fTextMna_actionPerformed();
		else if (object == fTextRsa)
			fTextRsa_actionPerformed();
		else if (object == fTextClub)
			fTextClub_actionPerformed();
		else if (object == fComboDivision)
			fComboDivision_actionPerformed();

		if (getEditorParent() != null)
			getEditorParent().eventOccurred(this, event);
	}

	void updateRatingPanel( boolean gotoRatingField) {
		if ( fEntry == null) return;
		if ( fEntry.getRating().isOneDesign()) {
			fPanelRating.setObject( null);
		} else {
			fPanelRating.setObject( fEntry.getRating());
		}
		if (gotoRatingField) setFirstFocus();
	}

	private void setFirstFocus() {
		if (fEntry != null && fEntry.getRating() != null && 
				fEntry.getRating().isOneDesign()) {
			fTextSail.requestFocusInWindow();
		} else {
			fPanelRating.requestFocusInWindow();
		}
	}

	void fComboDivision_actionPerformed() {
		Division div = (Division) fComboDivision.getSelectedItem();
		if (div != null && fEntry != null && !div.equals(fEntry.getDivision())) {
			Division holdDiv = fEntry.getDivision();
			try {
				fEntry.setDivision(div);
			} catch (RatingOutOfBoundsException ex) {
				String message = MessageFormat.format(res.getString("EntryMessageRatingOutOfBounds"), new Object[] {
						fEntry.getRating().toString(), div.toString() });

				int changeRating = JOptionPane.showConfirmDialog(this, message,
						res.getString("EntryMessageRatingOutOfBoundsTitle"), JOptionPane.YES_NO_OPTION);

				if (changeRating == JOptionPane.YES_OPTION) {
					try {
						fEntry.setRating((Rating) div.getSlowestRating().clone());
						fEntry.setDivision(div);
					} catch (RatingOutOfBoundsException canthappen) {}
				} else {
					fComboDivision.setSelectedItem(holdDiv);
				}
			}

		}
		updateRatingPanel( true);
	}

	String lastField = "";

	void fTextName_actionPerformed() {
		lastField = Entry.BOATNAME_PROPERTY;
		fEntry.setBoatName(fTextName.getText());
	}

	boolean editingSail = false;

	void fTextSail_actionPerformed() {
		lastField = Entry.SAILID_PROPERTY;
		String sail = fTextSail.getText();
		if (sail.equals(fEntry.getBoat().getSailId().toString()))
			return;

		if (!editingSail) {
			editingSail = true;
			if (getRegatta() != null)
				showDuplicateMessage(sail);
		}

		fEntry.setSailId(new SailId(fTextSail.getText()));
		editingSail = false;
	}

	private void showDuplicateMessage(String num) {
		EntryList eList = getRegatta().getAllEntries().findId(num);
		if (eList != null)
			eList.remove(fEntry);
		if (eList != null && eList.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(MessageFormat.format(res.getString("EntryMessageNumberInUse"), new Object[] { num }));
			sb.append(NEWLINE);
			for (int i = 0; i < eList.size(); i++) {
				sb.append("  ");
				sb.append(eList.get(i).toString());
				sb.append(NEWLINE);
			}
			JOptionPane.showMessageDialog(this, sb.toString(), res.getString("EntryTitleInUse"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

	boolean editingBow = false;

	void fTextBow_actionPerformed() {
		String bow = fTextBow.getText();
		if (bow.equals(fEntry.getBow()))
			return;

		if (!editingBow) {
			editingBow = true;
			if (getRegatta().isUseBowNumbers()) {
				showDuplicateMessage(bow);
			}
		}

		fEntry.setBow(fTextBow.getText());
		editingBow = false;
	}

	void fTextMna_actionPerformed() {
		fEntry.setMnaNumber(fTextMna.getText());
	}

	void fTextRsa_actionPerformed() {
		fEntry.setRsaNumber(fTextRsa.getText());
	}

	void fTextClub_actionPerformed() {
		fEntry.setClub(fTextClub.getText());
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Division div = DivisionList.getMasterList().get(1);
		Division div2 = DivisionList.getMasterList().get(2);

		Entry e = new Entry();
		try {
			e.setDivision(div);
		} catch (RatingOutOfBoundsException ex) {
			Util.showError(ex, true);
		}
		e.getBoat().setName("blue baby");
		e.setSailId(new SailId("1234"));

		SubDivision s2 = new SubDivision("Blue", div);
		s2.setMonopoly(true);
		s2.setScoreSeparately(false);

		SubDivision s3 = new SubDivision("Green", div2);
		s3.setMonopoly(false);
		s3.setScoreSeparately(true);

		SubDivision s1 = new SubDivision("White", DivisionList.getMasterList().get(3));
		s1.setMonopoly(false);
		s1.setScoreSeparately(false);

		Regatta reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);

		reg.addDivision(div);
		reg.addDivision(div2);

		reg.addEntry(e);

		reg.addSubDivision(s1);
		reg.addSubDivision(s2);
		reg.addSubDivision(s3);

		Entry b = new Entry();
		b.getBoat().setName("S&M");
		b.setSkipper(new Person("Sandy", "Grosvenor"));
		b.addCrew(new Person("Barbara", "Vosbury"));
		b.addCrew(new Person("Idarae", "Prothero"));
		b.setSailId(new SailId("USA 1044"));

		DialogBaseEditor fFrame = new DialogBaseEditor();
		fFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		fFrame.setObject(b);
		fFrame.setVisible(true);
	}
}
/**
 * $Log: PanelEntry.java,v $ Revision 1.7 2006/09/03 20:12:21 sandyg fixes bug 1551523 about crew data when boat changes
 * without a tab/enter
 * 
 * Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.18.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.18.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.18 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.17 2004/01/17 22:27:37 sandyg First cut at unlimited number of crew, request 512304
 * 
 * Revision 1.16 2004/01/15 02:18:38 sandyg fixed bug 822436
 * 
 * Revision 1.15 2003/05/02 02:41:38 sandyg fixed division update problem in panelentry
 * 
 * Revision 1.14 2003/04/27 21:35:34 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.13 2003/04/20 11:28:50 sandyg starting decent entry panel tests
 * 
 * Revision 1.12 2003/03/19 02:38:23 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.11 2003/03/16 20:38:31 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.10 2003/01/06 00:32:36 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.9 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
