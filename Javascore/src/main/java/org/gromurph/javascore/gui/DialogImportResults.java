//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogImportResults.java,v 1.5 2006/01/15 21:10:40 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.SailId;

public class DialogImportResults extends DialogImportTable implements Constants {
	public static final int BOW_INDEX = 0;
	public static final int SAIL_INDEX = 1;
	public static final int FINISH_INDEX = 2;

	public static final int POS_OFFSET = 0;
	public static final int TIME_OFFSET = 1;
	public static final int PENALTY_OFFSET = 2;
	public static final int POINTS_OFFSET = 3;

	@Override public String getDirections() {
		return res.getString("ImportResultsMessageDirections");
	}

	private static final int MAXRACES = 19;

	public DialogImportResults(JFrame parent) {
		super(parent);
		setTitle(res.getString("ImportTitlePasteFromClipboard"));
	}

	@Override public void initFieldNames() {
		fFieldNames = new String[2 + MAXRACES * 4];
		int x = 0;
		fFieldNames[x++] = res.getString("GenBow");
		fFieldNames[x++] = res.getString("GenSail");
		String race = res.getString("GenRace") + " ";
		for (int i = 1; i <= MAXRACES; i++) {
			String prefix = race + i + " ";
			fFieldNames[x++] = prefix + res.getString("GenFinPos");
			fFieldNames[x++] = prefix + res.getString("GenFinishTime");
			fFieldNames[x++] = prefix + res.getString("GenPenalty");
			fFieldNames[x++] = prefix + res.getString("GenPoints");
		}
	}

	int fLastRow = -1;
	Entry fEntry = null;
	boolean warned = false;

	@Override public void convertTableToRegatta() {
		fEntry = null;
		fTouchedRaces.clear();

		super.convertTableToRegatta();
	}

	@Override public void setValue(int row, int fieldIndex, String cell) {
		if (getRegatta() == null)
			return;

		if (fLastRow != row) {
			fLastRow = row;
			fEntry = null;
		}
		// String skippy = (fEntry == null)? "" :
		// fEntry.getSkipper().toString();

		switch (fieldIndex) {
		case BOW_INDEX: {
			EntryList elist = getRegatta().getAllEntries().findBow(cell);
			fEntry = null;
			if (elist.size() == 0) {
				addWarning(MessageFormat.format(res
						.getString("ImportMessageNoEntryBow"), new Object[] {
						cell, new Integer(row) }));
			} else if (elist.size() > 1) {
				addWarning(MessageFormat.format(res
						.getString("ImportMessageAmbiguousBow"), new Object[] {
						new Integer(elist.size()), cell, new Integer(row) }));
			} else {
				fEntry = elist.get(0);
			}
			break;
		}
		case SAIL_INDEX: {
			EntryList elist = getRegatta().getAllEntries().findSail(
					new SailId(cell));
			fEntry = null;
			if (elist.size() == 0) {
				addWarning(MessageFormat.format(res
						.getString("ImportMessageNoEntrySail"), new Object[] {
						cell, new Integer(row) }));
			} else if (elist.size() > 1) {
				addWarning(MessageFormat.format(res
						.getString("ImportMessageAmbiguousSail"), new Object[] {
						new Integer(elist.size()), cell, new Integer(row) }));
			} else {
				fEntry = elist.get(0);
			}
			break;
		}
		default: {
			if (cell.trim().length() == 0)
				return;
			if (fEntry != null)
				parseFinish(fEntry, row, fieldIndex, cell);
			break;
		}
		} // of switch
	}

	RaceList fTouchedRaces = new RaceList();

	private void parseFinish(Entry e, int row, int fieldIndex, String cell) {
		final int RACEFIELDS = 4;
		final int PRECEEDING_FIELDS = 2;

		String raceName = new Integer((fieldIndex - PRECEEDING_FIELDS)
				/ RACEFIELDS + 1).toString();
		int finIndex = fieldIndex - PRECEEDING_FIELDS - RACEFIELDS
				* ((fieldIndex - PRECEEDING_FIELDS) / RACEFIELDS);

		Race race = getRegatta().getRace(raceName);

		// if no race found, create a new race
		if (race == null) {
			race = new Race();
			race.setName(raceName);
			getRegatta().addRace(race);
		}

		// if first time we've touched this race, kill all previous finishes for
		// it
		if (fTouchedRaces.indexOf(race) < 0) {
			race.clearAllFinishes();
			fTouchedRaces.add(race);
		}

		Finish f = race.getFinish(e);
		if (f.isNoFinish()) {
			f.setPenalty(new Penalty(NO_PENALTY));
			f.setFinishTime(SailTime.NOTIME);
			race.setFinish(f);
		}

		if (finIndex == POS_OFFSET) // finish position
		{
			try {
				if ((f.isNoFinish()) || (!f.getPenalty().isFinishPenalty())) {
					// only set the finish position if we do NOT already have a
					// finish penalty (like DNF or such) or we don't already
					// have a
					// finish
					FinishPosition p = null;
					try {
						int pos = Integer.parseInt(cell);
						p = new FinishPosition(pos);
					} catch (NumberFormatException ne) {
						p = new FinishPosition(Penalty.parsePenalty(cell)
								.getPenalty());
					}
					f.setFinishPosition(p);
				}
			} catch (Exception exc) {
				addWarning(MessageFormat
						.format(
								res
										.getString("ImportResultsMessageInvalidFinishPosition"),
								new Object[] { cell, new Integer(row),
										exc.toString() }));
			}

		} else if ((finIndex == TIME_OFFSET) && (cell.trim().length() > 0)) {
			try {
				long t = SailTime.toLong(cell);
				f.setFinishTime(t);
				if (!f.getFinishPosition().isFinisher()) {
					// no finish position recorded, set it to row number
					f.setFinishPosition( new FinishPosition(row));
				}
			} catch (ParseException ne) {
				try {
					long p = Penalty.parsePenalty(cell).getPenalty();
					f.getPenalty().setPenalty(p);
				} catch (Exception ez) {
					addWarning(MessageFormat
							.format(
									res
											.getString("ImportResultsMessageInvalidFinishTime"),
									new Object[] { cell, new Integer(row),
											ez.toString() }));
				}
			}
		} else if ((finIndex == PENALTY_OFFSET) && (cell.trim().length() > 0)) {
			try {
				Penalty p = Penalty.parsePenalty(cell);
				f.setPenalty(p);
			} catch (Exception exc) {
				addWarning(MessageFormat
						.format(
								res
										.getString("ImportResultsMessageInvalidFinishPenalty"),
								new Object[] { cell, new Integer(row),
										exc.toString() }));
			}
		} else if (finIndex == POINTS_OFFSET) {
			if (!warned) {
				warned = true;
				JOptionPane.showMessageDialog(this, res
						.getString("ImportResultsMessageRecalc"), res
						.getString("ImportMessagePointsWarning"),
						JOptionPane.WARNING_MESSAGE);
			}
			try {
				if (cell.length() > 0) {
					f.getPenalty().setPoints(
							org.gromurph.util.Util.parseDouble(cell));
				}
			} catch (Exception exc) {
				addWarning(MessageFormat
						.format(
								res
										.getString("ImportResultsMessageInvalidFinishPoints"),
								new Object[] { cell, new Integer(row),
										exc.toString() }));
			}
		}
	}
}
/**
 * $Log: DialogImportResults.java,v $ Revision 1.5 2006/01/15 21:10:40 sandyg
 * resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.5.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.5 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.4 2003/04/27 21:05:58 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.3 2003/04/20 15:43:59 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.2 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
