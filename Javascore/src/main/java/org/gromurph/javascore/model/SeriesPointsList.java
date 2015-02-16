//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SeriesPointsList.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.gromurph.util.BaseList;
import org.gromurph.util.Util;

public class SeriesPointsList extends BaseList<SeriesPoints> {
	@Override
	public Class getContainingClass() {
		return SeriesPoints.class;
	}

	private static final long serialVersionUID = 1L;

	// public String databaseSelect() { return ""; } // return
	// SeriesPoints.databaseSelect(); }
	// public Class getColumnClass( int c) { return String.class;} // return
	// SeriesPoints.getColumnClass(c); }
	// public int getColumnCount() { return 0; } // return
	// SeriesPoints.getColumnCount(); }
	// public String getColumnName( int c) { return "";} // return
	// SeriesPoints.getColumnName(c); }

	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		try {
			SeriesPointsList that = (SeriesPointsList) obj;
			if (that.size() != this.size())
				return false;

			for (Iterator iter = iterator(); iter.hasNext();) {
				SeriesPoints rpThis = (SeriesPoints) iter.next();
				SeriesPoints rpThat = that.find(rpThis.getEntry(), rpThis
						.getDivision());
				if (rpThat == null)
					return false;
				if (!rpThis.equals(rpThat))
					return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * returns first (and hopefully only) entry in list for specified race and
	 * entry
	 */
	public SeriesPoints find(Entry e, AbstractDivision div) {
		SeriesPointsList divs = findAll( div);
		for (SeriesPoints p : divs) {
			if (Util.equalsWithNull(p.getEntry(), e))
				return p;
		}
		return null;
	}

	public SeriesPointsList findAll(Entry e) {
		SeriesPointsList list = new SeriesPointsList();
		for (SeriesPoints p : this) {
			if ((p.getEntry() != null) && (p.getEntry().equals(e))) {
				list.add(p);
			}
		}
		return list;
	}

	public SeriesPointsList findAll(AbstractDivision d) {
		SeriesPointsList list = new SeriesPointsList();
		for (SeriesPoints p : this) {
			if (p.getDivision() != null) {
				if (p.getDivision().equals(d)) {
					// exact match
					list.add(p);
//				} else if (p.getDivision().equals(d.getParentDivision())) {
//					// d is say "masters" within "mainFleet"
//					// want p, if p.div is mainfleet, and p.entry is in masters.entries
//					if (d.contains( p.getEntry())) {
//						list.add(p);
//					}
				}
			}
		}
		return list;
	}
	
	public SeriesPointsList findAllInSubDivision(SubDivision d) {
		SeriesPointsList list = new SeriesPointsList();
		for (SeriesPoints p : this) {
			if (d.contains(p.getEntry())) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * generates a string of elements
	 */
	@Override public String toString() {
		StringBuffer sb = new StringBuffer("rplist=(");
		for (Iterator iter = iterator(); iter.hasNext();) {
			sb.append(iter.next().toString());
			if (iter.hasNext())
				sb.append(',');
		}
		sb.append(')');
		return sb.toString();
	}

	public void clearAll(Entry e) {
		for (Iterator iter = iterator(); iter.hasNext();) {
			SeriesPoints p = (SeriesPoints) iter.next();
			if ((p.getEntry() != null) && (p.getEntry().equals(e))) {
				iter.remove();
			}
		}
	}

	/**
	 * creates a new set of seriespoints for each entry in entries this
	 */
	public static SeriesPointsList initPoints(EntryList entries, AbstractDivision div) {
		SeriesPointsList rList = new SeriesPointsList();
		for (Entry e : entries) {
			SeriesPoints rp = new SeriesPoints(e, div);
			rList.add(rp);
		}
		return rList;
	}

	public void sortPosition() {
		Collections.sort(this, new ComparatorPosition());
	}

	public static class ComparatorPosition implements Comparator<SeriesPoints> {
		public int compare(SeriesPoints left, SeriesPoints right) {
			if (left == null && right == null)
				return 0;
			if (left == null)
				return -1;
			if (right == null)
				return 1;

			long ileft = left.getPosition();
			long iright = right.getPosition();

			if (ileft < iright)
				return -1;
			if (ileft > iright)
				return 1;
			return 0;
		}
	}

	public void sortPoints() {
		Collections.sort(this, new ComparatorPoints());
	}

	public static class ComparatorPoints implements Comparator<SeriesPoints> {
		public int compare(SeriesPoints left, SeriesPoints right) {
			if (left == null && right == null)
				return 0;
			if (left == null)
				return -1;
			if (right == null)
				return 1;

			double ileft = left.getPoints();
			double iright = right.getPoints();

			if (ileft < iright)
				return -1;
			if (ileft > iright)
				return 1;
			return 0;
		}
	}
}
/**
 * $Log: SeriesPointsList.java,v $ Revision 1.4 2006/01/15 21:10:39 sandyg
 * resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.10 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.9 2003/05/07 01:17:07 sandyg removed unneeded method parameters
 * 
 * Revision 1.8 2003/04/27 21:36:17 sandyg more cleanup of unused variables...
 * ALL unit tests now working
 * 
 * Revision 1.7 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.6 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.5 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
