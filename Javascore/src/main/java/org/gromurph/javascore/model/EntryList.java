// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: EntryList.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.BaseList;

public class EntryList extends BaseList<Entry> {
	@Override
	public Class getContainingClass() {
		return Entry.class;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String databaseSelect() {
		return Entry.databaseSelect();
	}

	public Class getColumnClass(int c) {
		return Entry.getColumnClass(c);
	}

	public int getColumnCount() {
		return Entry.getColumnCount();
	}

	public String getColumnName(int c) {
		return Entry.getColumnName(c);
	}

	public EntryList findId(String sail) {
		EntryList list = new EntryList();

		for (Entry e : this) {
			if (e.matchesId(sail)) {
				list.add(e);
			}
		}
		return list;
	}

	public EntryList findSail(SailId sail) {
		EntryList list = new EntryList();

		for (Entry e : this) {
			if (e.matchesId(sail)) {
				list.add(e);
			}
		}
		return list;
	}

	public EntryList findBow(String sBow) {
		EntryList list = new EntryList();
		Bow bow = new Bow(sBow);

		for (Entry e : this) {
			if (e.getBow().equals(bow)) {
				list.add(e);
			}
		}
		return list;
	}

	public Entry getEntry(int id) {
		for (Entry e : this) {
			if (e.getId() == id) return e;
		}
		return null;
	}

	/**
	 * returns true if entrylist has one or more entries in specified division
	 */
	public boolean hasDivision(AbstractDivision division) {
		for (Entry e : this) {
			if (division.contains(e)) return true;
		}
		return false;
	}

	/**
	 * returns a list of entries in the requested division
	 */
	public EntryList findAll(AbstractDivision division) {
		EntryList list = new EntryList();

		for (Entry e : this) {
			if (division.contains(e)) {
				list.add(e);
			}
		}
		return list;
	}

	public void sortDivisionSailId() {
		Collections.sort(this, new Comparator<Entry>() {
			public int compare(Entry left, Entry right) {
				if (left == null && right == null) return 0;
				if (left == null) return -1;
				if (right == null) return 1;

				int i = left.getDivision().compareTo(right.getDivision());
				if (i != 0) return i;

				return left.getBoat().getSailId().compareTo(right.getBoat().getSailId());
			}
		});
	}

	public void sortDivisionSailId(final BaseList divs) {
		Collections.sort(this, new Comparator<Entry>() {
			public int compare(Entry left, Entry right) {
				if (left == null && right == null) return 0;
				if (left == null) return -1;
				if (right == null) return 1;

				int ileft = divs.indexOf(left.getDivision());
				int iright = divs.indexOf(right.getDivision());
				if (ileft < iright) return -1;
				else if (ileft > iright) return 1;

				int c = left.getDivision().compareTo(right.getDivision());
				if (c != 0) return c;

				return left.getBoat().getSailId().compareTo(right.getBoat().getSailId());
			}
		});
	}

	public void sortDivisionBow(final BaseList divs) {
		Collections.sort(this, new Comparator<Entry>() {
			public int compare(Entry left, Entry right) {
				if (left == null && right == null) return 0;
				if (left == null) return -1;
				if (right == null) return 1;

				Entry el = left;
				Entry er = right;

				int ileft = divs.indexOf(el.getDivision());
				int iright = divs.indexOf(er.getDivision());
				if (ileft < iright) return -1;
				else if (ileft > iright) return 1;

				int c = el.getDivision().compareTo(er.getDivision());
				if (c != 0) return c;

				return el.getBow().compareTo(er.getBow());
			}
		});
	}

	public void sortSailId() {
		Collections.sort(this, new Comparator<Entry>() {
			public int compare(Entry left, Entry right) {
				if (left == null && right == null) return 0;
				if (left == null) return -1;
				if (right == null) return 1;
				Entry eLeft = left;
				Entry eRight = right;
				return eLeft.getBoat().getSailId().compareTo(eRight.getBoat().getSailId());
			}
		});
	}

	public void sort(final String property) {
		Collections.sort(this, new Comparator<Entry>() {
			public int compare(Entry left, Entry right) {
				if (left == null && right == null) return 0;
				if (left == null) return -1;
				if (right == null) return 1;
				Entry eleft = left;
				Entry eright = right;

				if (property.equals(Entry.SKIPPER_PROPERTY)) return eleft.getSkipper().compareTo(eright.getSkipper());
				else if (property.equals(Entry.BOW_PROPERTY)) return eleft.getBow().compareTo(eright.getBow());
				else if (property.equals(Entry.BOAT_PROPERTY)) return eleft.getBoat().getName()
						.compareTo(eright.getBoat().getName());
				else if (property.equals(Entry.RATING_PROPERTY)) {
					Rating rleft = eleft.getRating();
					Rating rright = eright.getRating();
					if (rleft == null || rright == null) {
						rleft = eleft.getRating();
						rright = eright.getRating();
					}
					if (rleft == null && rright == null) return 0;
					if (rleft == null) return -1;
					if (rright == null) return 1;
					return rleft.compareTo(rright);
				} else // all that remains is sail or an invalid item
				return eleft.getBoat().getSailId().compareTo(eright.getBoat().getSailId());
			}
		});
	}

	/**
	 * returns a list of duplicate sail/bow ids
	 */
	public EntryList getDuplicateIds() {
		sortSailId();
		EntryList dupList = new EntryList();

		Entry laste = null;
		String lastid = null;
		for (Iterator ei = iterator(); ei.hasNext();) {
			Entry thise = (Entry) ei.next();
			String thisid = thise.getBoat().getSailId().toString();

			if (laste != null) {
				if (lastid.equals(thisid)) {
					if (!dupList.contains(lastid)) dupList.add(laste);
					dupList.add(thise);
				}
			}
			laste = thise;
			lastid = thisid;
		}

		return dupList;
	}
}
/**
 * $Log: EntryList.java,v $ Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.11 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.10 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.9 2004/04/10 20:42:18 sandyg Bug 932941, core dump moving boat from PHRF to 1D
 * 
 * Revision 1.8 2003/07/10 02:02:22 sandyg part of a null pointer error... checking for null on compareTo
 * 
 * Revision 1.7 2003/05/06 21:32:17 sandyg added checkin report by class, by bow
 * 
 * Revision 1.6 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */

