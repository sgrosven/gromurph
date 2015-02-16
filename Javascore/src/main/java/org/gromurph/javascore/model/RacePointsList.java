// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RacePointsList.java,v 1.6 2006/07/27 01:40:24 sandyg Exp $
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

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.BaseList;

public class RacePointsList extends BaseList<RacePoints> implements Constants {
	@Override
	public Class getContainingClass() {
		return RacePoints.class;
	}

	private static final long serialVersionUID = 1L;

	// public String databaseSelect() { return ""; } // return
	// RacePoints.databaseSelect(); }
	// public Class getColumnClass( int c) { return String.class;} // return
	// RacePoints.getColumnClass(c); }
	// public int getColumnCount() { return 0; } // return
	// RacePoints.getColumnCount(); }
	// public String getColumnName( int c) { return "";} // return
	// RacePoints.getColumnName(c); }

	@Override public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		try {
			RacePointsList that = (RacePointsList) obj;
			if (that.size() != this.size()) return false;

			for (Iterator iter = this.iterator(); iter.hasNext();) {
				RacePoints rpThis = (RacePoints) iter.next();
				RacePoints rpThat = that.find(rpThis.getRace(), rpThis.getEntry(), rpThis.getDivision());
				if (rpThat == null) return false;
				if (!rpThis.equals(rpThat)) return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * returns first (and hopefully only) entry in list for specified race and entry
	 */
	public RacePoints find(Race r, Entry e, AbstractDivision d) {
		return find(r, e, d, true);
	}
	public RacePoints find(Race r, Entry e, AbstractDivision d, boolean includeSubs) {
		for (RacePoints p : this) {
			Entry pe = p.getEntry();
			if ((e == null) || (pe != null) && (pe.equals(e))) {
				if ((r == null) || (p.getRace() != null) && (p.getRace().equals(r))) {
					if (d == null) return p;

					if (p.getDivision() != null) {
						// when we get here, we have same entry, same race and a
						// real division
						if (p.getDivision().equals(d)) {
							return p;
						} else if (includeSubs && p.getDivision().getParentDivision().equals(d)) {
							return p;
						}
					}
				}
			}
		}
		return null;
	}

//	/**
//	 * returns first (and hopefully only) entry in list for specified race and entry
//	 */
//	public RacePoints find(Race r, Entry e, SubDivision d) {
//		for (RacePoints p : this) {
//			Entry pe = p.getEntry();
//			if ((e == null) || (pe != null) && (pe.equals(e))) {
//				if ((r == null) || (p.getRace() != null) && (p.getRace().equals(r))) {
//					if (d == null) return p;
//
//					if (p.getDivision() != null) {
//						// when we get here, we have same entry, same race and a
//						// real division
//						if (p.getDivision().contains(d) && r.isSailing(d)) {
//							return p;
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}

	public EntryList getAllEntries() {
		EntryList entries = new EntryList();
		for (RacePoints p : this) {
			if (!entries.contains(p.getEntry())) entries.add( p.getEntry());
		}
		return entries;
	}

	public RacePointsList findAll(EntryList entries) {
		RacePointsList list = new RacePointsList();
		for (RacePoints p : this) {
			if (entries.contains(p.getEntry())) {
				list.add(p);
			}
		}
		return list;
	}
	
	public RacePointsList findAll(Entry entry, AbstractDivision div) {
		RacePointsList list = new RacePointsList();
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getEntry() != null) && (p.getEntry().equals(entry))) {
				if (p.getDivision() != null) {
					// when we get here, we have same entry, same race and a
					// real division
					if (p.getDivision().equals(div)) {
						list.add(p);
					} else if (div instanceof SubDivision && p.getDivision().contains(div)) {
						list.add(p);
					}
				}
			}
		}
		return list;
	}

	public RacePointsList findAll(Entry entry) {
		RacePointsList list = new RacePointsList();
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getEntry() != null) && (p.getEntry().equals(entry))) {
				list.add(p);
			}
		}
		return list;
	}

	public RacePointsList findAll(Stage s) {
		RacePointsList list = new RacePointsList();
		for (RacePoints p : this) {
			if (p.stage == s) list.add(p);
		}
		return list;
	}

	public RacePointsList findAll(Race race) {
		RacePointsList list = new RacePointsList();
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getRace() != null) && (p.getRace().equals(race))) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * finds all the members that are scored for the specified abstract division
	 * 
	 * @param div
	 *            abstract division
	 * @return
	 */
	public RacePointsList findAll(AbstractDivision div) {
		RacePointsList list = new RacePointsList();
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getDivision() != null) && (p.getDivision().equals(div))) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * finds all members where the entry's starting division matches (the points item may reflect scores from a fleet or
	 * subfleet)
	 * 
	 * @param div
	 * @return
	 */
	public RacePointsList findAllEntered(AbstractDivision div) {
		RacePointsList list = new RacePointsList();
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if (div.contains(p.getEntry())) list.add(p);
		}
		return list;
	}

	/**
	 * calculates number of valid finishers in this list of race points NOTE: if any of the finishes are null, returns 0
	 * NOTE: this is computationally intensive, if you can go straight to the raw finish list, that is better
	 */
	public int getNumberFinishers() {
		int n = 0;
		for (Iterator iter = iterator(); iter.hasNext();) {
			// Finish f = ((RacePoints) iter.next()).getFinish();
			RacePoints pts = (RacePoints) iter.next();
			if (pts.getRace() == null) {
				// if race is null, then must be series standings, assume all
				// valid
				n++;
			} else {
				Finish f = pts.getRace().getFinish(pts.getEntry());
				if (f != null && f.getFinishPosition() != null && f.getFinishPosition().isValidFinish()) {
					n++;
				}
			}
		}
		return n;
	}
	
	public int getNumberThrowouts() {
		int num = 0;
		for (RacePoints p : this) if (p.isThrowout()) num++;
		return num;
	}

	/**
	 * generates a string of elements
	 */
	@Override public String toString() {
		StringBuffer sb = new StringBuffer("rplist=(");
		for (Iterator iter = iterator(); iter.hasNext();) {
			sb.append(iter.next().toString());
			if (iter.hasNext()) sb.append(',');
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * calculates number of valid starters in this list of race points NOTE: if any of the finishes are null, returns 0
	 * NOTE: this is computationally intensive, if you can go straight to the raw finish list, that is better
	 */
	public int getNumberStarters() {
		int n = 0;
		for (Iterator iter = iterator(); iter.hasNext();) {
			// Finish f = ((RacePoints) iter.next()).getFinish();
			RacePoints pts = (RacePoints) iter.next();
			if (pts.getRace() == null) {
				// if race is null, then must be series standings, assume all
				// valid
				n++;
			} else {
				Finish f = pts.getRace().getFinish(pts.getEntry());
				if (f != null && f.getFinishPosition() != null) {
					if (f.getFinishPosition().isValidFinish()) {
						n++;
					} else if (!(f.getPenalty().hasPenalty(DNC) || f.getPenalty().hasPenalty(DNS))) {
						n++;
					}
				}
			}
		}
		return n;
	}

	/**
	 * calculates number of racers with specified penalty NOTE: if any of the finishes are null, returns 0 NOTE: this is
	 * computationally intensive, if you can go straight to the raw finish list, that is better
	 */
	public int getNumberWithPenalty(long dnc) {
		int n = 0;
		for (Iterator<RacePoints> iter = iterator(); iter.hasNext();) {
			// Finish f = ((RacePoints) iter.next()).getFinish();
			RacePoints pts = iter.next();
			try {
				Finish f = pts.getRace().getFinish(pts.getEntry());
				if (f.hasPenalty(dnc)) {
					n++;
				}
			} catch (NullPointerException e) {} // trop and ignore
		}
		return n;
	}

	public double getPointsTotal( boolean includingThrowouts) {
		double tot = 0;
		for (RacePoints p : this) {
			if (includingThrowouts && !p.isThrowout()) tot += (p.getPoints());
		}
		return tot;		
	}
	
	public void clearAll(Entry e) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getEntry() != null) && (p.getEntry().equals(e))) {
				iter.remove();
			}
		}
	}

	public void clearAll(Race e) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			RacePoints p = (RacePoints) iter.next();
			if ((p.getRace() != null) && (p.getRace().equals(e))) {
				iter.remove();
			}
		}
	}

	/**
	 * clears old points for race, and creates a new set of them, returns a RacePointsList of points for this race.. AND
	 * autoamtically adds DNC finishes for entries without finishes
	 */
	public RacePointsList initPoints(Race r, EntryList entries, Division div) {
		clearAll(r);
		RacePointsList rList = new RacePointsList();
		for (Iterator iter = entries.iterator(); iter.hasNext();) {
			Entry e = (Entry) iter.next();
			Finish f = r.getFinish(e);
			if (f == null) {
				f = new Finish(r, e);
				f.setFinishPosition(new FinishPosition(DNC));
				f.setPenalty(new Penalty(DNC));
				r.setFinish(f);
			}
			RacePoints rp = new RacePoints(f.getRace(), f.getEntry(), div, Double.NaN, false);
			this.add(rp);
			rList.add(rp);
		}
		return rList;
	}

	public void sortPointsPositionRounding() {
		Collections.sort(this, new ComparatorPointsPositionRounding());
	}

	public static class ComparatorPointsPositionRounding implements Comparator<RacePoints> {
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;
			double l = left.getPoints();
			double r = right.getPoints();

			if (l != r) return ((l > r) ? 1 : -1);

			long li = left.getFinish().getFinishPosition().longValue();
			long ri = right.getFinish().getFinishPosition().longValue();

			if (li != ri) return ((li > ri) ? 1 : -1);

			Race race = left.getRace();
			if (race.getAllRoundings() != null) {
				for (Iterator mIter = race.getAllRoundings().keySet().iterator(); mIter.hasNext();) {
					String markName = (String) mIter.next();
					FinishList marks = race.getRoundings(markName);
					if (marks == null) return 0;

					Entry el = left.getEntry();
					Entry er = right.getEntry();
					if (el == null) return 1;
					else if (er == null) return -1;

					Finish ml = marks.findEntry(el);
					Finish mr = marks.findEntry(er);
					li = ((ml == null) ? 99999 : ml.getFinishPosition().longValue());
					ri = ((mr == null) ? 99999 : mr.getFinishPosition().longValue());
					if (li != ri) return ((li > ri) ? 1 : -1);
				}
			}
			return 0;
		}
	}

	public void sortPosition() {
		Collections.sort(this, new ComparatorPosition());
	}

	public static class ComparatorPosition implements Comparator<RacePoints> {
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;

			return left.getFinish().getFinishPosition().compareTo(right.getFinish().getFinishPosition());
		}
	}

	public void sortCorrectedTimePosition() {
		Collections.sort(this, new ComparatorTimePosition(true));
	}

	public void sortElapsedTimePosition() {
		Collections.sort(this, new ComparatorTimePosition(false));
	}

	public static class ComparatorTimePosition implements Comparator<RacePoints> {
		public ComparatorTimePosition( boolean useCorrected) {
			_useCorrected = useCorrected;
		}
		boolean _useCorrected;
		
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;

			if (left.getFinish() == null) return -1;
			if (right.getFinish() == null) return 1;

			long ileft;
			long iright;
			if (_useCorrected) {
				ileft = left.getFinish().getCorrectedTime();
				iright = right.getFinish().getCorrectedTime();
			} else {
				ileft = left.getFinish().getElapsedTime();
				iright = right.getFinish().getElapsedTime();
			}

			if (ileft != SailTime.NOTIME && iright != SailTime.NOTIME) {
				if (ileft < iright) return -1;
				if (ileft > iright) return 1;
			}

			return left.getFinish().getFinishPosition().compareTo(right.getFinish().getFinishPosition());
		}
	}

	public void sortDivisionPoints() {
		Collections.sort(this, new ComparatorDivisionPoints());
	}

	public static class ComparatorDivisionPoints implements Comparator<RacePoints> {
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;
			RacePoints r1 = left;
			RacePoints r2 = right;

			if (!r1.getDivision().equals(r2.getDivision())) {
				return r1.getDivision().compareTo(r2.getDivision());
			}
			return r1.compareTo(r2);
		}
	}

	public void sortRace() {
		Collections.sort(this, new ComparatorRace());
	}

	public static class ComparatorRace implements Comparator<RacePoints> {
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;
			try {
				return left.getRace().compareTo(right.getRace());
			} catch (NullPointerException e) {
				return 0;
			}
		}
	}

	public void sortPoints() {
		Collections.sort(this, new ComparatorPoints());
	}

	public static class ComparatorPoints implements Comparator<RacePoints> {
		public int compare(RacePoints left, RacePoints right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;
			int c1 = left.compareTo(right);
			if (c1 != 0) return c1;

			return left.getFinish().getFinishPosition().compareTo(right.getFinish().getFinishPosition());
		}
	}
}
/**
 * $Log: RacePointsList.java,v $ Revision 1.6 2006/07/27 01:40:24 sandyg Corrected Snipe Jr Nats bug:
 * racepointslist.find() was returning racepoints of parent division.
 * 
 * Revision 1.5 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.12 2005/03/06 03:09:01 sandyg Finished fixing bug 1074695, wierd finish position/ordering on single race
 * when some boats OCS others DNC
 * 
 * Revision 1.11 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.10 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.9 2003/05/07 01:17:07 sandyg removed unneeded method parameters
 * 
 * Revision 1.8 2003/04/27 21:36:17 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.7 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.6 2003/04/20 15:43:59 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.5 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.4 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
