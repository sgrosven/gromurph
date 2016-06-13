// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: FinishList.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.gromurph.util.BaseList;
import org.gromurph.xml.PersistentNode;

public class FinishList extends BaseList<Finish> {
	@Override
	public Class getContainingClass() {
		return Finish.class;
	}

	private static final long serialVersionUID = 1L;

	@Override public String databaseSelect() {
		return Finish.databaseSelect();
	}

	public int findLastValidFinish() {
		for (int i = size() - 1; i >= 0; i--) {
			Finish f = get(i);
			if ((f.getFinishPosition().isValidFinish()) && (f.getEntry() != null))
				return i;
		}
		return -1;
	}

	/**
	 * determines if what cells can be changed
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 1:
			return true; // sail
		case 2:
			return true; // fin time
		case 3:
			return true; // penalty
		default:
			return false; // esp column 0, finish order
		}
	}

	@Override public void xmlWrite(PersistentNode parentNode, String elementTag) {
		removeNoFinishes(null);
		super.xmlWrite(parentNode, elementTag);
	}

	/**
	 * returns a finish if found, otherwise returns null
	 */
	public Finish findEntry(Entry e) {
		if (size() == 0)
			return null;
		for (Iterator<Finish> iter = iterator(); iter.hasNext();) {
			Finish f = iter.next();
			if ((f.getEntry() != null) && (f.getEntry().equals(e))) {
				return f;
			}
		}
		return null;
	}

	public int getNumberFinishers() {
		int n = 0;
		for (Iterator<Finish> iter = iterator(); iter.hasNext();) {
			Finish f = (Finish) iter.next();
			if ((f.getEntry() != null) && (f.getFinishPosition().isValidFinish()))
				n++;
		}
		return n;
	}

	public int getNumberFinishers(AbstractDivision div) {
		int n = 0;
		for (Finish f : this) {
			if ((f.getEntry() != null) && (f.getFinishPosition().isValidFinish())) {
				if (div.contains(f.getEntry())) //.getRating()))
					n++;
			}
		}
		return n;
	}

	/**
	 * starting with the specified finish, slides the finish number of that finish and all below it "down" one, and
	 * returns a new incomplete Finish in the base Finishes spot
	 * 
	 * @param base
	 *            Finish to head the list of finishes moved down
	 */
	public void insertPosition(Finish base) {
		int i = indexOf(base);
		long basePos = base.getFinishPosition().longValue();

		if (i < 0)
			return;

		for (Iterator<Finish> iter = iterator(); iter.hasNext();) {
			Finish f = iter.next();
			FinishPosition pos = f.getFinishPosition();
			if (pos.isValidFinish() && (pos.longValue() >= basePos))
				f.setFinishPosition(new FinishPosition(pos.longValue() + 1));
		}
	}

	/**
	 * compares the input EntryList with the finish list, adds a no-finish finish for every entry missing in the list
	 **/
	// private void addMissingFinishes( Race r)
	// {
	// // make a list of all entries with finishes
	// List finishers = new EntryList();
	// for ( Iterator iter = this.iterator(); iter.hasNext();)
	// {
	// finishers.add( ((Finish) iter.next()).getEntry());
	// }
	//
	// List missingEntries = (EntryList) r.getEntries().clone();
	// missingEntries.removeAll( finishers);
	//
	// for ( Iterator iter = missingEntries.iterator(); iter.hasNext();)
	// {
	// Entry e = (Entry) iter.next();
	// if ( r.isSailing( e))
	// {
	// add(new Finish( r, e));
	// }
	// }
	// }
	/**
	 * remove finishes that have penalty of NoFinish
	 **/
	private void removeNoFinishes(Race r) {
		Collection<Finish> dropem = new ArrayList<Finish>(10);
		for (Finish f : this) {
			if (f.isNoFinish()) {
				dropem.add(f);
			} else if (f.getEntry() == null) {
				dropem.add(f);
			} else if (r != null && !r.isSailing(f.getEntry())) {
				dropem.add(f);
			}
		}
		removeAll(dropem);
	}

	/**
	 * remove finishes that have penalty of NoFinish
	 **/
	// private void removeNoEntry( Race r)
	// {
	// EntryList entries = r.getRegatta().getAllEntries();
	//
	// Collection dropem = new ArrayList(10);
	// for ( Iterator iter = iterator(); iter.hasNext();)
	// {
	// Finish f = (Finish) iter.next();
	// if (f.getEntry() == null)
	// {
	// dropem.add(f);
	// }
	// else if ( !entries.contains( f.getEntry()))
	// {
	// dropem.add(f);
	// }
	// }
	// removeAll(dropem);
	// }
	/**
	 * returns a list of finishes sorted on finishposition contains an element for every entrant in the racing in the
	 * race For entrants without a finish, a NOFINISH finish is included
	 */
	public void syncWithEntries(Race r)
	// -- try making this static and returning a _new_ list of finishes
	{
		FinishList fin2 = new FinishList();
		for (Entry e : r.getRegatta().getAllEntries()) {
			if (r.isSailing(e)) {
				Finish f = findEntry(e);
				if (f == null)
					f = new Finish(r, e);
				fin2.add(f);
			}
		}

		fin2.sortPosition();
		fin2.reNumber();
		fin2.sortPosition();

		clear();
		addAll(fin2);
	}

	/**
	 * ensures that finish positions go from 1 to x, then nonFinish positions
	 */
	public void reNumber() {
		int n = 1;
		for (Iterator<Finish> iter = iterator(); iter.hasNext();) {
			Finish f = iter.next();
			if (f.getPenalty().isFinishPenalty()) {
				f.setFinishPosition(new FinishPosition(f.getPenalty().getFinishPenalty()));
			} else if (f.getFinishPosition().isValidFinish()) {
				if (n != f.getFinishPosition().longValue()) {
					f.setFinishPosition(new FinishPosition(n));
				}
				n++;
			}
		}
	}

	/**
	 * resorts the array by finishposition
	 */
	public void sortPosition() {
		Collections.sort(this, new ComparatorPosition());
	}

	/**
	 * resorts the array by finishposition
	 */
	private static class ComparatorPosition implements Comparator<Finish> {
		public int compare(Finish left, Finish right) {
			if (left == null && right == null)
				return 0;
			if (left == null)
				return -1;
			if (right == null)
				return 1;

			FinishPosition fleft = left.getFinishPosition();
			FinishPosition fright = right.getFinishPosition();
			if (fleft == null && fright == null)
				return 0;
			if (fleft == null)
				return -1;
			if (fright == null)
				return 1;

			return fleft.compareTo(fright);
		}
	}

	/**
	 * resorts the array by finishposition
	 */
	public void sortPursuitStartTime() {
		Collections.sort(this, new ComparatorPursuitStartTime());
	}

	/**
	 * resorts the array by finishposition
	 */
	private static class ComparatorPursuitStartTime implements Comparator<Finish> {
		public int compare(Finish left, Finish right) {
			if (left == null && right == null)
				return 0;
			if (left == null)
				return -1;
			if (right == null)
				return 1;

			Long sleft = left.getStartTime();
			Long sright = right.getStartTime();
			
			if (sleft == sright) { 
				return left.getEntry().getBoat().getSailId().compareTo( right.getEntry().getBoat().getSailId());
			} else {
				return sleft.compareTo(sright);
			}
		}
	}

}
/**
 * $Log: FinishList.java,v $ Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.9.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.9.2.1 2005/06/26 22:47:17 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.9 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.8 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.7 2003/11/23 20:34:45 sandyg starting release 4.2, minor cleanup
 * 
 * Revision 1.6 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.5 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.4 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */
