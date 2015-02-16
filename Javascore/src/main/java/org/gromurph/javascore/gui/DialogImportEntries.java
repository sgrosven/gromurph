// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogImportEntries.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
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
import java.text.MessageFormat;

import javax.swing.*;

import org.gromurph.javascore.*;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.ratings.Rating;

public class DialogImportEntries extends DialogImportTable {

	JComboBox<Division> fComboDefaultDivision;
	JLabel fLabelDivision;

	JRadioButton fRadioReplace;
	JRadioButton fRadioAppend;

	String fRowRating = null;
	Division fRowDiv = null;

	int fLastRow = -1;
	Entry fEntry = null;

	class ActionAppendReplace implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			fActionOk.setEnabled(fRadioReplace.isSelected() || fRadioAppend.isSelected());
		}
	}

	public DialogImportEntries(JFrame parent) {
		super(parent);
		setTitle(res.getString("ImportTitlePasteFromClipboard"));

		JPanel subPanel = getSubPanel();
		subPanel.setLayout(new GridBagLayout());

		JPanel p = new JPanel(new FlowLayout());
		gridbagAdd(subPanel, p, 0, 0, 2, 1, GridBagConstraints.WEST);

		fLabelDivision = new JLabel(res.getString("ImportEntriesLabelDefaultDivision"));
		p.add(fLabelDivision);
		fComboDefaultDivision = new JComboBox<Division>();
		p.add(fComboDefaultDivision);

		ButtonGroup group = new ButtonGroup();
		fRadioReplace = new JRadioButton(res.getString("ImportEntriesLabelReplace"));
		fRadioReplace.setToolTipText(res.getString("ImportEntriesLabelReplaceToolTip"));
		fRadioReplace.setMnemonic(res.getString("ImportEntriesLabelReplaceMnemonic").charAt(0));
		group.add(fRadioReplace);

		fRadioAppend = new JRadioButton(res.getString("ImportEntriesLabelAppend"));
		fRadioAppend.setToolTipText(res.getString("ImportEntriesLabelAppendToolTip"));
		fRadioAppend.setMnemonic(res.getString("ImportEntriesLabelAppendMnemonic").charAt(0));
		group.add(fRadioAppend);

		gridbagAdd(subPanel, new JLabel(res.getString("ImportEntriesSelectOne")), 0, 1, 1, 2, GridBagConstraints.EAST);
		gridbagAdd(subPanel, fRadioReplace, 1, 1);
		gridbagAdd(subPanel, fRadioAppend, 1, 2);

		fRadioReplace.setSelected(false);
		fRadioAppend.setSelected(false);

		ActionAppendReplace handler = new ActionAppendReplace();
		fRadioReplace.addActionListener(handler);
		fRadioAppend.addActionListener(handler);

		fActionOk.setEnabled(fRadioReplace.isSelected() || fRadioAppend.isSelected());

	}

	@Override
	public void setVisible(boolean v) {
		if (v) updateRegattaFields();
		super.setVisible(v);
	}

	private void checkDuplicateIds(Entry e) {
		if (e != null) {
			String sailid = e.getBoat().getSailId().toString();
			EntryList dups = getRegatta().getAllEntries().findId(sailid);
			if (dups.size() > 1) {
				addWarning(MessageFormat.format(res.getString("ImportMessageDuplicateSailId"), new Object[] { sailid,
						dups.get(0).toString(), fEntry.toString() }));
			}
		}
	}

	@Override
	public void setValue(int row, int fieldIndex, String cell) {
		if (fLastRow != row) {
			checkDuplicateIds(fEntry);
			fLastRow = row;

			// starting new Entry
			fEntry = new Entry();
			Division div = (Division) fComboDefaultDivision.getSelectedItem();
			if (div != null) try {
				fEntry.setDivision(div);
			}
			catch (RatingOutOfBoundsException e) {} // shouldnt happen, do nothing

			getRegatta().addEntry(fEntry);
			fRowRating = null;
			fRowDiv = null;
		}

		switch (fieldIndex) {
		case 0:
			parseDivision(fEntry, cell);
			break;
		case 1:
			parseRating(fEntry, cell);
			break; // Rating value
		case 2:
			fEntry.setBow(cell);
			break; // Entry.BOW_PROPERTY
		case 3:
			fEntry.setSailId(new SailId(cell));
			break; // Boat.SAILID_PROPERTY,
		case 4:
			fEntry.getBoat().setName(cell);
			break; // Boat.NAME_PROPERTY,
		case 5:
			fEntry.getSkipper().setFirst(cell);
			break; // Skipper FIRSTNAME_PROPERTY,
		case 6:
			fEntry.getSkipper().setLast(cell);
			break; // Skipper LASTNAME_PROPERTY,
		case 7:
			fEntry.getSkipper().setSailorId(cell);
			break;
		case 8:
			fEntry.setClub(cell);
			break; //         Entry.CLUB_PROPERTY,
		case 9:
			fEntry.setMnaNumber(cell);
			break; // Entry.MNANUMBER_PROPERTY,
		case 10:
			fEntry.setRsaNumber(cell);
			break; // Entry.RSANUMBER_PROPERTY
		case 11:
			fEntry.getCrew(0).setFirst(cell);
			break; // Crew FIRSTNAME_PROPERTY,
		case 12:
			fEntry.getCrew(0).setLast(cell);
			break; // Crew LASTNAME_PROPERTY,
		case 13:
			fEntry.getCrew(0).setSailorId(cell);
			break;
		case 14:
			fEntry.getCrew(1).setFirst(cell);
			break; // Crew FIRSTNAME_PROPERTY,
		case 15:
			fEntry.getCrew(1).setLast(cell);
			break; // Crew LASTNAME_PROPERTY,
		case 16:
			fEntry.getCrew(1).setSailorId(cell);
			break;
		case 17:
			fEntry.getCrew(2).setFirst(cell);
			break; // Crew FIRSTNAME_PROPERTY,
		case 18:
			fEntry.getCrew(2).setLast(cell);
			break; // Crew LASTNAME_PROPERTY,
		case 19:
			fEntry.getCrew(2).setSailorId(cell);
			break;
		case 20:
			fEntry.getCrew(3).setFirst(cell);
			break; // Crew FIRSTNAME_PROPERTY,
		case 21:
			fEntry.getCrew(3).setLast(cell);
			break; // Crew LASTNAME_PROPERTY,
		case 22:
			fEntry.getCrew(3).setSailorId(cell);
			break;
		case 23:
			fEntry.getCrew(4).setFirst(cell);
			break; // Crew FIRSTNAME_PROPERTY,
		case 24:
			fEntry.getCrew(4).setLast(cell);
			break; // Crew LASTNAME_PROPERTY,
		case 25:
			fEntry.getCrew(4).setSailorId(cell);
			break;
		default: // do nothing ?
		} // of switch
	}

	@Override
	public void initFieldNames() {
		fFieldNames = new String[] { res.getString("GenDivision"), res.getString("GenRating"), res.getString("GenBow"),
				res.getString("GenSail"), res.getString("GenBoatName"), res.getString("GenSkipperFirst"),
				res.getString("GenSkipperLast"), res.getString("GenSkipperIsafId"), res.getString("GenClub"),
				JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY),
				JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY),
				res.getString("GenCrewFirst") + "1", res.getString("GenCrewLast") + "1",
				res.getString("GenCrewIsafId") + "1", res.getString("GenCrewFirst") + "2",
				res.getString("GenCrewLast") + "2", res.getString("GenCrewIsafId") + "2",
				res.getString("GenCrewFirst") + "3", res.getString("GenCrewLast") + "3",
				res.getString("GenCrewIsafId") + "3", res.getString("GenCrewFirst") + "4",
				res.getString("GenCrewLast") + "4", res.getString("GenCrewIsafId") + "4",
				res.getString("GenCrewFirst") + "5", res.getString("GenCrewLast") + "5",
				res.getString("GenCrewIsafId") + "5" };
	}

	@Override
	public void convertTableToRegatta() {
		if (fRadioReplace.isSelected()) {
			getRegatta().getAllEntries().clear();
			for (SubDivision subdiv : getRegatta().getSubDivisions()) {
				subdiv.clearEntries();
			}
			getRegatta().getRaces().clear();
		}

		super.convertTableToRegatta();
	}

	@Override
	public String getDirections() {
		return res.getString("ImportEntriesMessageDirections");
	}

	private void updateRegattaFields() {
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg.getNumDivisions() > 0) {
			Division[] divArray = new Division[reg.getDivisions().size()];
			divArray = reg.getDivisions().toArray(divArray);
			fComboDefaultDivision.setModel(new DefaultComboBoxModel<Division>(divArray));
			fComboDefaultDivision.setSelectedIndex(0);
			fComboDefaultDivision.setVisible(true);
			fLabelDivision.setVisible(true);
		} else {
			fComboDefaultDivision.setVisible(false);
			fLabelDivision.setVisible(false);
		}
	}

	private void parseDivision(Entry e, String cell) {
		SubDivision subd = null;
		fRowDiv = getRegatta().getDivision(cell);
		if (fRowDiv == null) {
			// is it a sub-division?
			subd = getRegatta().getSubDivisions().find(cell);
			if (subd != null && subd.getParentDivision() instanceof Division) {
				// it is!
				fRowDiv = (Division) subd.getParentDivision();
			}
		}
		if (fRowDiv == null) {
			fRowDiv = DivisionList.getMasterList().find(cell);
			if (fRowDiv == null) {
				fRowDiv = new Division(cell);
				addWarning(MessageFormat.format(res.getString("ImportEntriesMessageNoClassFound"),
						new Object[] { fRowDiv.toString() }));
			}
			getRegatta().addDivision(fRowDiv);
		}

		try {
			if (fRowDiv.isOneDesign()) e.getBoat().putRating(fRowDiv.getMinRating());
			e.setDivision(fRowDiv);
		}
		catch (RatingOutOfBoundsException ex) {
			addWarning(res.getString("ImportEntriesBadDivisionSetNone"));
			try {
				e.setDivision(AbstractDivision.NONE);
			}
			catch (RatingOutOfBoundsException ex2) {}
		}
		if (!fRowDiv.isOneDesign() && (fRowRating != null)) setRating(e);
		if (subd != null) subd.addEntry(e);
	}

	private void parseRating(Entry e, String cell) {
		fRowRating = cell;
		if (fRowDiv != null && !fRowDiv.isOneDesign()) setRating(e);
	}

	private void setRating(Entry e) {
		double rtg = 0;
		Rating r = null;
		try {
			rtg = org.gromurph.util.Util.parseDouble(fRowRating);
			r = RatingManager.createRating(fRowDiv, rtg);
			e.setRating(r);
		}
		catch (RatingOutOfBoundsException roe) {
			String msg = MessageFormat.format(res.getString("ImportEntriesMessageRatingOutOfBounds"), new Object[] {
					e.getBoat().getSailId(), e.getBoat().getName(), new Double(rtg),
					new Double(fRowDiv.getMinRating().getPrimaryValue()) });
			addWarning(msg);
			try {
				e.setRating(RatingManager.createRating(fRowDiv, e.getDivision().getMinRating().getPrimaryValue()));
			}
			catch (RatingOutOfBoundsException roe2) {} // shouldnt happen so ignore
		}
		catch (ClassCastException e1) {}
		catch (NumberFormatException e2) {
			String msg = MessageFormat.format(res.getString("ImportEntriesMessageRatingNotNumber"), new Object[] {
					e.getBoat().getSailId(), e.getBoat().getName(), fRowRating,
					new Double(e.getDivision().getMinRating().getPrimaryValue()) });
			addWarning(msg);
		}
	}

}
/**
 * $Log: DialogImportEntries.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.14.4.2 2005/12/15 23:44:18 sandyg can import up to 5 crew names now
 * 
 * Revision 1.14.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.14 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.13 2004/01/20 23:28:46 sandyg fixed glitch on import/replace all where subdiv entries were not deleted
 * 
 * Revision 1.12 2004/01/18 18:32:49 sandyg Now NO default on append/replace, must select one or the other.
 * 
 * Revision 1.11 2004/01/17 22:27:37 sandyg First cut at unlimited number of crew, request 512304
 * 
 * Revision 1.10 2003/05/18 14:50:43 sandyg killed extra errant code
 * 
 * Revision 1.9 2003/05/17 22:42:54 sandyg fixed import problems with one designs and rating mixes
 * 
 * Revision 1.8 2003/04/27 21:05:58 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/03/16 20:38:30 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.6 2003/01/06 00:32:36 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.5 2003/01/05 21:16:34 sandyg regression unit testing following rating overhaul from entry to boat
 * 
 * Revision 1.4 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
