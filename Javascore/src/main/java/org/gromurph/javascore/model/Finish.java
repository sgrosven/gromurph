// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Finish.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.beans.PropertyChangeEvent;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class Finish extends BaseObject implements Constants {
	private static final long serialVersionUID = 1L;
	protected transient String aSail; // purely for debug ease of read
	protected transient String aRace; // purely for debug ease of read
	private Race fRace;
	private Entry fEntry;

	private FinishPosition fPosition;  
		// raw finish position as entered into scoring program by RC
	private long fFinishTime;
	private Penalty fPenalty;

	public transient static final String FINISHPOSITION_PROPERTY = "Pos";
	public transient static final String PENALTY_PROPERTY = "Penalty";
	public transient static final String ENTRY_PROPERTY = "Ent";
	public transient static final String RACE_PROPERTY = "Race";
	public transient static final String FINISHTIME_PROPERTY = "Time";

	public Finish() {
		this(null, null, SailTime.NOTIME, new FinishPosition(NOFINISH), new Penalty(NOFINISH));
	}

	public Finish(Race inRace, Entry inEntry) {
		this(inRace, inEntry, SailTime.NOTIME, new FinishPosition(NOFINISH), new Penalty(NOFINISH));
	}

	public Finish(Race inRace, Entry inEntry, long inTime, FinishPosition inOrder, Penalty inPenalty) {
		super();
		setRace(inRace);
		setEntry(inEntry);
		fFinishTime = inTime;
		fPosition = inOrder;
		fPenalty = ((inPenalty == null) ? new Penalty() : inPenalty);
		if (fPenalty.isFinishPenalty()) {
			fPosition = new FinishPosition(fPenalty.getPenalty());
		}
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		setPenalty(new Penalty()); // blank out the penalty on a read
		Race race = (Race) rootObject;
		setRace(race);

		String value = n.getAttribute(ENTRY_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			if (race != null) {
				setEntry(race.getRegatta().getEntry(id));
			}
		}

		value = n.getAttribute(FINISHTIME_PROPERTY);
		if (value != null) {
			setFinishTime(SailTime.forceToLong(value));
		}

		value = n.getAttribute(FINISHPOSITION_PROPERTY);
		if (value != null) {
			setFinishPosition(new FinishPosition(FinishPosition.parseString(value)));
			if (Penalty.isFinishPenalty(fPosition.longValue())) {
				setPenalty(new Penalty(fPosition.longValue()));
			}
		}

		PersistentNode n2 = n.getElement(PENALTY_PROPERTY);
		if (n2 != null) {
			Penalty p = new Penalty();
			p.xmlRead(n2, rootObject);
			setPenalty(p);
		}

	}

	@Override public void xmlWrite(PersistentNode e) {

		if (getEntry() != null) e.setAttribute(ENTRY_PROPERTY, Integer.toString(getEntry().getId()));
		if (getRace() != null) e.setAttribute(RACE_PROPERTY, Integer.toString(getRace().getId()));

		if (getFinishPosition() != null) e.setAttribute(FINISHPOSITION_PROPERTY, getFinishPosition().toString());

		if (getFinishTime() != SailTime.NOTIME) {
			e.setAttribute(FINISHTIME_PROPERTY, SailTime.toString(getFinishTime()));
		}

		if ((getPenalty() != null)
				&& ((getPenalty().getPenalty() != NO_PENALTY) || (getPenalty().getNote().length() > 0))) {
			getPenalty().xmlWrite(e.createChildElement(PENALTY_PROPERTY));
		}
		// return e;
	}

	/**
	 * sorts based on finishes WITHOUT regard to penalites except for non-finishing penalties
	 **/
	public int compareTo(Object obj) {
		Finish that = (Finish) obj;

		if (this.getCorrectedTime() != SailTime.NOTIME && that.getCorrectedTime() != SailTime.NOTIME) {
			// have finish times for both boats, use that
			long delta = that.getCorrectedTime() - this.getCorrectedTime();
			if (delta < 0) return 1;
			else if (delta > 0) return -1;
			else return 0;
		} else {
			return fPosition.compareTo(that.fPosition);
		}
	}

	public static String databaseSelect() {
		return "";
	}

	public static Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return FinishPosition.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return Penalty.class;
		case 4:
			return Double.class;
		case 5:
			return Boolean.class;
		}
		return null;
	}

	public static int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int c) {
		switch (c) {
		case 0:
			return fPosition;
		case 1:
			return ((fEntry == null) ? "" : fEntry.getBoat().getSailId().toString());
		case 2:
			return SailTime.toString(fFinishTime);
		case 3:
			return fPenalty;
		}
		return "";
	}

	public void setValueAt(Object obj, int c) {
		switch (c) {
		case 0:
			setFinishPosition((FinishPosition) obj);
			break;
		case 1: {
			Entry e = null;
			if (obj instanceof Entry) {
				e = (Entry) obj;
			} else if (obj instanceof String) {
				EntryList l = fRace.getEntries().findId((String) obj);
				if (l.size() == 1) {
					e = l.get(0);
				} else {
					Util.showError(this, "No entries, or more than one possible entry, l=" + l.toString());
				}
			}
			setEntry(e);
			break;
		}
		case 2: {
			try {
				setFinishTime(SailTime.toLong((String) obj));
			} catch (java.text.ParseException e) {} // ignore for now
			break;
		}
		case 3:
			setPenalty((Penalty) obj);
			break;
		default:
			break; // do nothing
		}
	}

	public static String getColumnName(int c) {
		switch (c) {
		case 0:
			return FINISHPOSITION_PROPERTY;
		case 1:
			return ENTRY_PROPERTY;
		case 2:
			return FINISHTIME_PROPERTY;
		case 3:
			return PENALTY_PROPERTY;
		}
		return null;
	}

	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Finish)) return false;
		if (this == obj) return true;

		Finish that = (Finish) obj;

		if ((fEntry == null) ? (that.fEntry != null) : !(fEntry.equals(that.fEntry))) return false;
		if ((fPenalty == null) ? (that.fPenalty != null) : !(fPenalty.equals(that.fPenalty))) return false;
		if (fFinishTime != that.fFinishTime || !fPosition.equals(that.fPosition)) return false;

		return true;
	}

	@Override public Object clone() {
		Finish newGuy = (Finish) super.clone();
		// that will have done BaseObject clone, ratings already cloned as
		// children

		// these three are not in BaseObject children
		try {
			// fEntry is intentionally not cloned
			if (fPenalty != null) newGuy.fPenalty = (Penalty) fPenalty.clone();
		} catch (Exception e) {
			Util.showError(e, true);
		}
		return newGuy;
	}

	public void setFinishPosition(FinishPosition inVal) {
		FinishPosition hold = fPosition;
		fPosition = inVal;
		if (Penalty.isFinishPenalty(inVal.longValue())) {
			getPenalty().setFinishPenalty(inVal.longValue());
		}
		firePropertyChange(FINISHPOSITION_PROPERTY, hold, inVal);
	}

	public FinishPosition getFinishPosition() {
		return fPosition;
	}

	public boolean isNoFinish() {
		return fPosition.isNoFinish();
	}

	public void setPenalty(Penalty inPenalty) {
		if (inPenalty == null) inPenalty = new Penalty();
		if (fPenalty != null) fPenalty.removePropertyChangeListener(this);
		Penalty hold = fPenalty;
		fPenalty = inPenalty;
		if (inPenalty.isFinishPenalty()) {
			setFinishPosition(new FinishPosition(inPenalty.getPenalty() & NOFINISH_MASK));
		}
		if (fPenalty != null) fPenalty.addPropertyChangeListener(this);
		firePropertyChange(PENALTY_PROPERTY, hold, fPenalty);
	}

	@Override public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);

		// if penalty change is to set a finish position penalty,
		// then pass it through to the position
		if (event.getSource() == fPenalty && event.getPropertyName().equals(Penalty.PENALTY_PROPERTY)) {
			long newPen = ((Long) event.getNewValue()).longValue();
			if (Penalty.isFinishPenalty(newPen)) {
				setFinishPosition(new FinishPosition(newPen & NOFINISH_MASK));
			}
		}
	}

	public void setEntry(Entry inEntry) {
		Entry hold = fEntry;
		// if (fEntry != null) fEntry.removePropertyChangeListener(this);
		fEntry = inEntry;
		// if (fEntry != null) fEntry.addPropertyChangeListener(this);
		firePropertyChange(ENTRY_PROPERTY, hold, fEntry);
		aSail = (fEntry == null) ? null : fEntry.getBoat().getSailId().toString();
	}

	private void setRace(Race inRace) {
		Race hold = fRace;
		// if (fRace != null) fRace.removePropertyChangeListener(this);
		fRace = inRace;
		// if (fRace != null) fRace.addPropertyChangeListener(this);
		firePropertyChange(RACE_PROPERTY, hold, fRace);
		aRace = (fRace == null) ? null : fRace.toString();
	}

	public void setFinishTime(long inTime) {
		long hold = fFinishTime;
		fFinishTime = inTime;
		firePropertyChange(FINISHTIME_PROPERTY, new Long(hold), new Long(inTime));
	}

	public long getElapsedTime() {
		if (fRace == null || fEntry == null) return SailTime.NOTIME;
		if (fFinishTime == SailTime.NOTIME) return SailTime.NOTIME;
		Division div = fEntry.getDivision();
		if (fRace.getStartTimeRaw(div) == SailTime.NOTIME) return SailTime.NOTIME;

		long startTime = fEntry.getRating().getStartTime(this); // fRace.getStartTimeAdjusted(div);
		if (startTime == SailTime.NOTIME) return SailTime.NOTIME;

		long finishTime = fFinishTime;
		if (finishTime == SailTime.NOTIME) return SailTime.NOTIME;

		if (fRace.isNextDay(div)) finishTime += SailTime.DAYINMILLIS;
		long elapsed = finishTime - startTime;

		if (fPenalty.hasPenalty(Constants.TME)) {
			long penTime = fPenalty.getTimePenaltyElapsed();
			elapsed = elapsed + penTime;
		} else if (fPenalty.hasPenalty(Constants.TMP)) {
			int pct = fPenalty.getPercent();
			elapsed = (long) (elapsed * (1 + ((double) pct) / 100));
		}

		return elapsed;
	}

	public long getCorrectedTime() {
		// get boats rating, for it's divisions scoring system
		// then call that rating's calccorrected
		long corrected = 0;
		if (fEntry == null) corrected = SailTime.NOTIME;
		else {
			if (fEntry.getRating() == null) corrected = getElapsedTime();
			else corrected = fEntry.getRating().getCorrectedTime(this);

			if (fPenalty.hasPenalty(Constants.TMC)) {
				long penTime = fPenalty.getTimePenaltyCorrected();
				corrected = corrected + penTime;
			}
		}
		return corrected;
	}

	public long getTimeAllowance() {
		// get boats rating, for it's divisions scoring system
		// then call that rating's calccorrected
		if (fEntry == null) return SailTime.NOTIME;
		if (fEntry.getRating() == null) return 0;
		return fEntry.getRating().getTimeAllowance(this);
	}

	public long getStartTime() {
		return fEntry.getRating().getStartTime(this);
	}
	
	public Race getRace() {
		return fRace;
	}

	public Penalty getPenalty() {
		return fPenalty;
	}

	public long getFinishTime() {
		return fFinishTime;
	}

	public Entry getEntry() {
		return fEntry;
	}

	public boolean hasPenalty() {
		return (fPenalty.getPenalty() != NO_PENALTY);
	}

	public boolean hasPenalty(Penalty pen) {
		return (fPenalty.getPenalty() == pen.getPenalty());
	}

	public boolean hasPenalty(long ipen) {
		return (fPenalty.getPenalty() == ipen);
	}

	@Override public String toString() {
		StringBuffer sb = new StringBuffer();
		if (fEntry == null || fEntry.getBoat() == null) {
			sb.append("<null entry>");
			sb.append(" @ ");
			sb.append(SailTime.toString(fFinishTime));
		} else {
			// sb.append(fEntry.toString());
			// sb.append("/ ");
			if (fPosition != null) sb.append(fPosition.toString());
			if (fPenalty != null) {
				sb.append("[");
				sb.append(fPenalty.toString());
				sb.append("]");
			}
			sb.append(" @ ");
			sb.append(SailTime.toString(fFinishTime));
		}
		return sb.toString();
	}

	public String getDivisionString() {
		Entry e = this.getEntry();
		Regatta reg = JavaScoreProperties.getRegatta();
		StringBuffer sb = new StringBuffer(24);

		if (e.getRating().isOneDesign()) {
			if (reg != null && reg.isMultistage()) {
				Race race = this.getRace();
				// have qualifying regatta, want to show this entry's subdivision(s) for this race
				boolean firstDiv = true;
				for (AbstractDivision div : race.getStartingDivisions(true)) {
					if (div.contains(e)) {
						if (!firstDiv) sb.append(", ");
						sb.append(div.toString());
						firstDiv = false;
					}
				}
			} else {
				// not qual, just show the division
				sb.append(e.getDivision().toString());
			}
		} else {
			// not oneD, show div and rating
			sb.append(e.getDivision().toString());
			sb.append("(");
			sb.append(e.getRating().toString(false));
			sb.append(")");
		}

		return sb.toString();
	}

}
/**
 * $Log: Finish.java,v $ Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.14.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.14.2.1 2005/06/26 22:47:17 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.14 2004/04/11 20:41:54 sandyg Bug 773217 PHRF ratings now show time allowance with out finishes
 * 
 * Revision 1.13 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.12 2003/11/27 02:59:35 sandyg saves penalty "notes" even if penalty is blank (bug 691233)
 * 
 * Revision 1.11 2003/04/27 21:03:26 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.10 2003/04/23 00:30:19 sandyg added Time-based penalties
 * 
 * Revision 1.9 2003/04/20 15:43:58 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.8 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.7 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */
