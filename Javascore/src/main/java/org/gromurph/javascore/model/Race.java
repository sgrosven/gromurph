// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Race.java,v 1.8 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class Race extends BaseObject implements Constants {
	static ResourceBundle res = JavaScoreProperties.getResources();

	private static final long serialVersionUID = 1L;

	private String fName;
	private FinishList fFinishList;
	private Map<String, FinishList> fRoundingList; // with String as key, string is mark "name",
	private Regatta fRegatta;
	private Date fStartDate;
	private boolean fLongDistance;
	private int fBFactor;
	private int fAFactor;
	private String fComment;
	private RaceDivisionInfo fDivInfo;
	private boolean fNonDiscardable;
	private double fWeight;
	private boolean fMedalRace;
	
	public boolean isCarryOverRace = false;

	public final transient static String NAME_PROPERTY = "Name";
	public final transient static String DIVINFO_PROPERTY = "DivInfo";
	public final transient static String STARTDATE_PROPERTY = "StartDate";
	public final transient static String LONGDISTANCE_PROPERTY = "LongDistance";
	public final transient static String BFACTOR_PROPERTY = "BFactor";
	public final transient static String AFACTOR_PROPERTY = "AFactor";
	public final transient static String COMMENT_PROPERTY = "Comment";
	public final transient static String WEIGHT_PROPERTY = "Weight";
	public final transient static String NONDISCARDABLE_PROPERTY = "NonDiscardable";
	public final transient static String MEDALRACE_PROPERTY = "MedalRace";

	public final transient static int MAX_ROUNDINGS = 10;

	private int fId;
	private static int sNextId = 1;
	public static final String ID_PROPERTY = "RaceId";

	public static final double DEFAULT_WEIGHT = 1.00;

	private void setId(String id) {
		fId = new Integer(id).intValue();
		if (fId >= sNextId) sNextId = fId + 1;
	}

	public int getId() {
		return fId;
	}

	// normally just string of the mark number
	// public final transient static String STARTTIME_PROPERTY = "StartTime";
	// public final transient static String LENGTH_PROPERTY = "Length";

	public Race() {
		this(null, "");
	}

	public Race(Regatta inReg, String inName) {
		super();
		fId = sNextId++;
		
		fRegatta = (inReg != null) ? inReg : JavaScoreProperties.getRegatta();
		
		fName = inName;
		fDivInfo = new RaceDivisionInfo((inReg == null) ? true : !inReg.isMultistage());
		fFinishList = new FinishList();
		fRoundingList = null;
		fStartDate = new Date(System.currentTimeMillis());
		fLongDistance = false;
		fMedalRace = false;
		if (JavaScoreProperties.haveCustomABFactors()) { 
    		fBFactor = JavaScoreProperties.getBFactor();
    		fAFactor = JavaScoreProperties.getAFactor();
		} else {
    		fBFactor = RatingPhrfTimeOnTime.BFACTOR_AVERAGE;
    		fAFactor = RatingPhrfTimeOnTime.AFACTOR_DEFAULT;
		}
		fComment = "";
		fWeight = DEFAULT_WEIGHT;
		
		fNonDiscardable = false;
	}

	private static final String FINISHLIST_PROPERTY = "FinishList";
	private static final String ROUNDINGLIST_PROPERTY = "RoundingList";
	private static final String FINISH_PROPERTY = "Fin";
	private static final String ROUNDING_PROPERTY = "Rdg";

	private static final DateFormat sXmlDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

	@Override public void xmlWrite(PersistentNode e) {
		
		if (isCarryOverRace) return;// not writing out carryover races
		
		e.setAttribute(ID_PROPERTY, Integer.toString(getId()));
		e.setAttribute(NAME_PROPERTY, fName);
		e.setAttribute(COMMENT_PROPERTY, fComment);

		e.setAttribute(WEIGHT_PROPERTY, Double.toString(fWeight));
		e.setAttribute(NONDISCARDABLE_PROPERTY, new Boolean(fNonDiscardable).toString());
		e.setAttribute(MEDALRACE_PROPERTY, new Boolean(fMedalRace).toString());

		if (fStartDate != null) {
			e.setAttribute(STARTDATE_PROPERTY, sXmlDateFormat.format(fStartDate));
		}
		e.setAttribute(LONGDISTANCE_PROPERTY, new Boolean(fLongDistance).toString());
		e.setAttribute(BFACTOR_PROPERTY, Integer.toString(getBFactor()));
		e.setAttribute(AFACTOR_PROPERTY, Integer.toString(getAFactor()));

		fDivInfo.xmlWrite(e.createChildElement(DIVINFO_PROPERTY));
		if (fFinishList.size() > 0) fFinishList.xmlWrite(e.createChildElement(FINISHLIST_PROPERTY), FINISH_PROPERTY);

		if (fRoundingList != null && fRoundingList.size() > 0) {
			PersistentNode roundingListNode = e.createChildElement( ROUNDINGLIST_PROPERTY);
			for (String key : fRoundingList.keySet()) {
				FinishList fl = fRoundingList.get(key);
				if (fl.size() > 0) fl.xmlWrite(roundingListNode.createChildElement( key), ROUNDING_PROPERTY);
			}
		}
		// return e;
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fRegatta = (Regatta) rootObject;

		String value = n.getAttribute(ID_PROPERTY);
		if (value != null) setId(value);

		value = n.getAttribute(NAME_PROPERTY);
		if (value != null) setName(value);

		value = n.getAttribute(COMMENT_PROPERTY);
		if (value != null) setComment(value);

		value = n.getAttribute(WEIGHT_PROPERTY);
		if (value != null) {
			try {
				fWeight = Double.parseDouble(value);
			} catch (Exception e) {
				fWeight = DEFAULT_WEIGHT;
			}
		} else {
			fWeight = DEFAULT_WEIGHT;
			;
		}

		setStartDate(null); // 2.1 compatibility - don't override a null startdate
		value = n.getAttribute(STARTDATE_PROPERTY);
		if (value != null) {
			try {
				if (!value.equals(SailTime.NODATE_STRING)) {
					Date dd = sXmlDateFormat.parse(value);
					setStartDate(dd);
				}
			} catch (java.text.ParseException e) {
				logger.warn(" date parsing exception, value={}",value);
			}
		}

		value = n.getAttribute(BFACTOR_PROPERTY);
		if (value != null) setBFactor(Integer.parseInt(value));

		value = n.getAttribute(AFACTOR_PROPERTY);
		if (value != null) setAFactor(Integer.parseInt(value));
		else setAFactor( fBFactor); // for pre-7.0.0 compatibility

		value = n.getAttribute(LONGDISTANCE_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				fLongDistance = b;
			} catch (Exception e) {}
		}

		value = n.getAttribute(NONDISCARDABLE_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				fNonDiscardable = b;
			} catch (Exception e) {}
		}

		value = n.getAttribute(MEDALRACE_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				fMedalRace = b;
			} catch (Exception e) {}
		}

		PersistentNode n2 = n.getElement(DIVINFO_PROPERTY);
		if (n2 != null) {
			fDivInfo.xmlRead(n2, this);
		}

		n2 = n.getElement(FINISHLIST_PROPERTY);
		if (n2 != null) {
			fFinishList = new FinishList();
			fFinishList.xmlRead(n2, this);
		}

		n2 = n.getElement(ROUNDINGLIST_PROPERTY);
		if (n2 != null) {
			fRoundingList = new TreeMap<String, FinishList>();
			PersistentNode[] nodes = n2.getElements();

			if (nodes != null) {
				for (PersistentNode node : nodes) {
					String key = node.getName();
					FinishList rlist = new FinishList();
					rlist.xmlRead(node, this);
					fRoundingList.put(key, rlist);
				}
			}
		}
	}

	public long getStartTimeRaw(AbstractDivision div) {
		return fDivInfo.getStartTime(div);
	}

	public long getStartTimeAdjusted(AbstractDivision div) {
		return fDivInfo.getStartTimeAdjusted(div);
	}

	public String formatStartDateTime(AbstractDivision div) {
		long time = fDivInfo.getStartTimeAdjusted(div);
		if (time == SailTime.NOTIME) return SailTime.NOTIME_STRING;
		long date = fStartDate.getTime();
		Date dt = new Date(date + time);

		StringBuffer sb = new StringBuffer(32);
		sb.append(DateFormat.getDateInstance(DateFormat.FULL).format(dt));
		sb.append(" ");
		sb.append(SailTime.toString(time));
		return sb.toString();
	}

	public Date getStartDate() {
		return fStartDate;
	}

	public Date geDivStartDate(AbstractDivision div) {
		if (fDivInfo.isNextDay(div)) return new Date(fStartDate.getTime() + SailTime.DAYINMILLIS);
		else return fStartDate;
	}

	public void setStartDate(Date inD) {
		fStartDate = inD;
	}

	public boolean isLongDistance() {
		return fLongDistance;
	}

	public void setLongDistance(boolean b) {
		fLongDistance = b;
	}

	public int getBFactor() {
		return fBFactor;
	}

	public void setBFactor(int b) {
		fBFactor = b;
	}

	public int getAFactor() {
		return fAFactor;
	}

	public void setAFactor(int b) {
		fAFactor = b;
	}

	public long getEarliestStartTime() {
		return getDivInfo().getEarliestStartTime();
	}

//	/**
//	 * returns true if this race occurs after the specified race. Returns false if either race is not in the list of
//	 * races for the regatta.
//	 * 
//	 * @param r
//	 * @return
//	 */
//	public boolean isAfter(Race r) {
//		if (fRegatta == null) return false;
//
//		RaceList orderedRaces = new RaceList();
//		orderedRaces.addAll(fRegatta.getRaces());
//		orderedRaces.sort();
//
//		int myNum = orderedRaces.indexOf(this);
//		int yourNum = orderedRaces.indexOf(r);
//
//		return (myNum >= 0 && yourNum >= 0 && myNum > yourNum);
//	}

	/**
	 * sorts based on finishes WITHOUT regard to penalites except for non-finishing penalties
	 **/
	public int compareTo(Object obj) {
		if (!(obj instanceof Race)) return -1;
		if (this.equals(obj)) return 0;
		Race that = (Race) obj;

		// -- very first, start date
		int i = Util.compareWithNull(fStartDate, that.fStartDate);
		if (i != 0) return i;

		// -- next sort on earliest start time in the race
		long thisStart = this.getEarliestStartTime();
		long thatStart = that.getEarliestStartTime();
		if (thisStart != SailTime.NOTIME && thatStart != SailTime.NOTIME) {
    		if (thisStart < thatStart) return -1;
    		else if (thisStart > thatStart) return 1;
		}

		// -- first parse the name, sort on 1st word numerically, rest alpha
		String inS = this.getName().trim();
		long numLeft = 0;

		char[] inC = inS.toCharArray();
		i = 0;
		// put digits into string for number
		while ((i < inC.length) && (Character.isDigit(inC[i])))
			i++;
		if (i > 0) numLeft = Long.parseLong(inS.substring(0, i));
		String strLeft = inS.substring(i).trim();

		inS = that.getName().trim();
		long numRight = 0;

		inC = inS.toCharArray();
		i = 0;
		// put digits into string for number
		while ((i < inC.length) && (Character.isDigit(inC[i])))
			i++;
		if (i > 0) numRight = Long.parseLong(inS.substring(0, i));
		String strRight = inS.substring(i).trim();

		if (numLeft < numRight) return -1;
		else if (numLeft > numRight) return 1;

		// if we get here 1st word is either not a number or is equal
		int x = strLeft.compareTo(strRight);
		if (x != 0) return x;

		// if we get here name is same
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		try {
			return (this.fId == ((Race) obj).fId);
			// Race that = (Race) obj;
			// if (!Util.equalsWithNull( this.fName, that.fName)) return false;
			// if (!Util.equalsWithNull( this.fDivInfo, that.fDivInfo)) return false;
			// return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = fName.hashCode();
		return hash;
	}

	public static String databaseSelect() {
		return "";
	}

	public static Class getColumnClass(int c) {
		switch (c) {
		case 0:
			return String.class;
		case 1:
			return Double.class;
		case 2:
			return Long.class;
		case 3:
			return String.class; // Division.class;
		}
		return null;
	}

	public static int getColumnCount() {
		return 4;
	}

	public static String getColumnName(int c) {
		switch (c) {
		case 0:
			return "Race";
		case 1:
			return "Length";
		case 2:
			return "Start";
		case 3:
			return "Division";
		}
		return null;
	}

	public RaceDivisionInfo getDivInfo() {
		return fDivInfo;
	}

	public void setName(String name) {
		String old = fName;
		fName = name;
		firePropertyChange(NAME_PROPERTY, old, fName);
	}

	public int getNextFinishNumber() {
		int n = fFinishList.size() + 1;
		Iterator<Finish> iter = fFinishList.iterator();
		while (iter.hasNext()) {
			Finish f = (Finish) iter.next();
			if (!f.getFinishPosition().isValidFinish()) {
				n--;
			}
		}
		return n;
	}

	public void deleteRoundings() {
		if (fRoundingList != null) fRoundingList.clear();
	}

	public FinishList getRoundings(String markName) {
		if (fRoundingList == null) {
			fRoundingList = new TreeMap<String, FinishList>();
		}
		FinishList retList = fRoundingList.get(markName);
		if (retList == null) {
			retList = new FinishList();
			fRoundingList.put(markName, retList);
		}
		retList.syncWithEntries(this);
		return retList;
	}

	public static String[] getAllRoundingNames() {
		String[] ret = new String[MAX_ROUNDINGS];
		for (int m = 0; m < MAX_ROUNDINGS; m++) {
			ret[m] = "M" + Integer.toString(m + 1);
		}
		return ret;
	}

	public Map<String,FinishList> getAllRoundings() {
		return fRoundingList;
	}

	public boolean haveRoundings() {
		return (fRoundingList != null);
	}

	public String getName() {
		return fName;
	}

	public Regatta getRegatta() {
		return fRegatta;
	}

	/**
	 * returns the entries elegible to race in this race. Currently returns entries in the regatta, but this should
	 * evolve into entries in this race when multiple divisions are implemented
	 */
	public EntryList getEntries() {
		EntryList elist = (EntryList) fRegatta.getAllEntries().clone();
		Collection<Entry> dropem = new ArrayList<Entry>(10);
		Iterator<Entry> iter = elist.iterator();
		while (iter.hasNext()) {
			Entry e = (Entry) iter.next();
			if (!isSailing(e)) dropem.add(e);
		}
		elist.removeAll(dropem);
		return elist;
	}

	/**
	 * return true if the specified entry should be sailing in the race
	 **/
	public boolean isSailing(Entry e) {
		Division div = e.getDivision();
		if ((e.getDivision()).isRacing( this,  false,  true)) return true;
		// if entry's division is racing, we're done
		
		// if entry division is not racing, we need to see if it
		// is in a subdivision that is racing
		for (AbstractDivision adiv : getStartingDivisions(true)) {
			if (adiv instanceof SubDivision) {
				SubDivision sub = (SubDivision) adiv;
				if (fDivInfo.isRacing(sub) && sub.contains(e)) return true;
			}
		}

		return false;
	}

	public boolean hasQualifyingGroups() {
		for (AbstractDivision d : getStartingDivisions(false)) {
			if (d.isGroupQualifying() && d.isRacing(this)) return true;
		}
		return false;
	}

	public AbstractDivision getDivisionForEntry( Entry entry) {
		for (AbstractDivision div : getStartingDivisions(true)) {
				if (div.contains(entry)) return div;
		}
		return null;
	}
	
	// should ONLY be called from Regatta
	public void setParentRegatta(Regatta reg) {
		fRegatta = reg;
	}

	@Override
	public String toString() {
		if (getName().length() == 0) return res.getString("noname");
		else return getName();
	}

	public void syncFinishesWithEntries() {
		fFinishList.syncWithEntries(this);
	}

	/**
	 * removes all finishes (permanently) from this race
	 */
	public void clearAllFinishes() {
		fFinishList.clear();
	}

	/**
	 * returns tne finish for entry e in this race May return null if entry e was not a valid entrant in this race If e
	 * is valid entrant but does not hae a finish, a finish with FinishPosition of NOFINISH is created and returned
	 */
	public Finish getFinish(Entry e) {
		if (!isSailing(e)) return null;
		Finish f = fFinishList.findEntry(e);
		if (f == null) {
			f = new Finish(this, e);
			f.setPenalty(new Penalty(NOFINISH));
		}
		return f;
	}

	/**
	 * adds or replaces the finish for the f.getEntry() in this race ignores the finish if e is not valid entrant
	 */
	public void setFinish(Finish f) {
		Entry e = f.getEntry();
		if (e == null || !isSailing(e)) return;
		Finish oldFinish = fFinishList.findEntry(e);
		if (oldFinish != null) {
			fFinishList.remove(oldFinish);
		}
		fFinishList.add(f);
	}

	public Iterator<Finish> finishers() {
		return fFinishList.iterator();
	}

	/**
	 * returns number of finishers in divsions... just a front to the finishlists' call, but avoids the sorting/synching
	 * that happens with getallfinishers call
	 */
	public int getNumberFinishers(AbstractDivision div) {
		return fFinishList.getNumberFinishers(div);
	}

	public boolean hasStartTimes() {
		return fDivInfo.hasStartTimes();
	}
	
	/**
	 * returns true if the race has one or more divisions with a valid starttime
	 */

	public StartingDivisionList getStartingDivisions(boolean racingOnly) {
		Regatta reg = getRegatta();
		if (reg == null) return new StartingDivisionList(); // returns empty list

		if (reg.isMultistage()) {
			return fDivInfo.getStartingDivisions(racingOnly);
		} else {
    		// add all subdivs for a split event
    		StartingDivisionList divs = new StartingDivisionList();
    		for (SubDivision d : reg.getSubDivisions()) {
    			if (!d.isGroupScoring() && reg.getAllEntries().findAll(d).size() > 0) {
    				divs.add(d);
    			}
    		} 
    		for (Division d : reg.getDivisions()) {
    			if (reg.getAllEntries().findAll(d).size() > 0) {
    				divs.add(d);
    			}
    		}
    		return divs;
		}
	}
	
	public StartingDivisionList getDivisionsByStartOrder(boolean racingOnly) {
		StartingDivisionList divs = getStartingDivisions(racingOnly);
		if (comparatorStartTime == null) comparatorStartTime = new ComparatorStartTime(this);
		Collections.sort(divs, comparatorStartTime);
		return divs;
	}

	private ComparatorStartTime comparatorStartTime;
	
	public static class ComparatorStartTime implements Comparator<AbstractDivision> {
		private Race _race;
		private RaceDivisionInfo _divInfo;
		
		public ComparatorStartTime( Race r) {
			_race = r;
			_divInfo = _race.getDivInfo();
		}
		public int compare(AbstractDivision left, AbstractDivision right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;

			if (_divInfo.isRacing(left)) {
				if (_divInfo.isRacing(right)) {
					long sLeft = _race.getStartTimeAdjusted(left);
					long sRight = _race.getStartTimeAdjusted(right);
					if (sLeft < sRight) return -1;
					else if (sLeft > sRight) return 1;
					else return 0;
				} else {
					return -1;
				}
			} else if (_divInfo.isRacing(right)) {
				return 1;
			} else {
				return left.compareTo(right);
			}

		}
	};

	public String getComment() {
		return fComment;
	}

	public void setComment(String comment) {
		fComment = comment;
	}

	/**
	 * @return Returns the isNonDiscardable.
	 */
	public boolean isNonDiscardable() {
		return fNonDiscardable;
	}

	/**
	 * @param isNonDiscardable
	 *            The isNonDiscardable to set.
	 */
	public void setNonDiscardable(boolean isNonDiscardable) {
		fNonDiscardable = isNonDiscardable;
	}

	/**
	 * @return Returns the isMedalRace.
	 */
	public boolean isMedalRace() {
		return fMedalRace;
	}

	/**
	 * @param isMedalRace
	 *            The isMedalRace to set.
	 */
	public void setMedalRace(boolean isMedalRace) {
		fMedalRace = isMedalRace;
		if (fMedalRace) {
			fNonDiscardable = true;
			fWeight = 2.00;
		} else {
			fNonDiscardable = false;
			fWeight = 1.00;
		}
	}

	/**
	 * @return Returns the weight.
	 */
	public double getWeight() {
		return fWeight;
	}

	/**
	 * @param weight
	 *            The weight to set.
	 */
	public void setWeight(double weight) {
		fWeight = weight;
	}

	public void setIsRacing(AbstractDivision div, boolean b) {
		fDivInfo.setIsRacing(div, b);
	}

	public void setStartTime(AbstractDivision div, long inTime) {
		long time = inTime;
		// make sure its time since midnight
		if (time != SailTime.NOTIME && time > SailTime.DAYINMILLIS) {
			int ndays = (int) (time / SailTime.DAYINMILLIS);
			time = ndays * SailTime.DAYINMILLIS;
		}
		fDivInfo.setStartTime(div, time);
	}

	public double getLength(AbstractDivision div) {
		return fDivInfo.getLength(div);
	}

	public void setLength(AbstractDivision div, double len) {
		fDivInfo.setLength(div, len);
	}

	public boolean isNextDay(AbstractDivision div) {
		return fDivInfo.isNextDay(div);
	}

	public void setNextDay(AbstractDivision div, boolean nd) {
		fDivInfo.setNextDay(div, nd);
	}

}
/**
 * $Log: Race.java,v $ Revision 1.8 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.7 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.6 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.4 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests work
 * 
 * Revision 1.3 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/08 04:18:33 sandyg Fixed reporting error on finals divisions, cleaned up gui on qual/final races
 * (hiding divisions that should not have their "participating" flags changed)
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.18.4.4 2006/01/01 01:54:10 sandyg qual fleet fixing
 * 
 * Revision 1.18.4.3 2005/11/26 17:45:01 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.18.4.2 2005/11/01 18:05:00 sandyg fixed mark rounding order and lost report options - both problems
 * discovered during 2005 RIWKC
 * 
 * Revision 1.18.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.18.2.1 2005/06/26 22:47:18 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.18 2005/06/12 20:31:30 sandyg fixed bug in saving mark roundings
 * 
 * Revision 1.17 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.16 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.15 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.14 2003/04/20 15:43:59 sandyg added javascore.Constants to consolidate penalty defs, and added new
 * penaltys TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.13 2003/03/30 00:04:47 sandyg added comments field
 * 
 * Revision 1.12 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.11 2003/03/28 02:22:12 sandyg Feature #691217 - in Race Dialog, divisions with 0 entries no longer
 * included in start time list
 * 
 * Revision 1.10 2003/03/16 20:39:44 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.9 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.8 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
