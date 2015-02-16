//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SubDivision.java,v 1.8 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Iterator;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * a SubDivision is a combination of several Divisions of starting classes.
 */
public class SubDivision extends AbstractDivision {
	public static String PARENT_PROPERTY = "Parent";
	public static String PARENTTYPE_PROPERTY = "ParentType";
	public static String SCORESEPARATELY_PROPERTY = "ScoreSeparately";
	public static String MONOPOLY_PROPERTY = "Monopoly";
	public static String GROUP_PROPERTY = "Group";
	public static String GROUPRANK_PROPERTY = "GroupRank";
	public static String LASTSPLIT_PROPERTY = "LastSplit";
	public static String ENTRYLIST_PROPERTY = "EntryList";
	public static String ENTRY_PROPERTY = "Entry";
	public static String ENTRYID_PROPERTY = "Id";
	public static String ADDON_PROPERTY = "addOn";

	public static final String QUALIFYING = "Qual";
	public static final String SCORING = "Scoring";
	public static final String FINAL = "Final"; // the opposition of qualifying

	private static String typeFleet = "Fleet";
	private static String typeDivision = "Division";

	private AbstractDivision fParentDiv;
	private EntryList fEntries;
	private boolean fScoreSeparately;
	private boolean fMonopoly;
	private String fGroup;
	private boolean fLastSplit;
	private double fRaceAddon;

	private String fGroupRank;

	public SubDivision() {
		this(NO_NAME, null);
	}

	 @Override public String getGender() {
		if (fParentDiv != null)
			return fParentDiv.getGender();
		else
			return null;
	}

	 @Override public String getIfClassId() {
		if (fParentDiv != null)
			return fParentDiv.getIfClassId();
		else
			return null;
	}

	/**
	 * modified so that the group ranks will affect subdivision order
	 */
	 @Override public int compareTo(Object obj) {
		if (!(obj instanceof SubDivision))
			return super.compareTo(obj);
		SubDivision that = (SubDivision) obj;

		int rankOrder = 0;
		try {
			Double thisRank = Double.parseDouble(this.getGroupRank());
			Double thatRank = Double.parseDouble(that.getGroupRank());
			rankOrder = thisRank.compareTo(thatRank);
		} catch (Exception e) {
		} // can bomb if no group rank
		if (rankOrder != 0)
			return rankOrder;
		else
			return super.compareTo(obj);
	}

	/**
	 * checks to see if division is contained in this subdivision.
	 * 
	 * @param div
	 * @return true if div is in one of the divisions contained in this Fleet
	 */
	@Override public boolean contains(AbstractDivision div) {
		// the only way a subdiv can contain another division is if it is itself
		return this.equals(div);
	}

	/**
	 * checks to see if division is contained in this subdivision.
	 * 
	 * @param div
	 * @return true if div is in one of the divisions contained in this Fleet
	 */
	@Override public boolean contains(Rating r) {
		// the only way a subdiv can contain a rating is if its parent does
		return fParentDiv.contains(r);
	}

	/**
	 * Creates a SubDivision of specified parent AbstractDivision and name.
	 * Default values for subdivision is that it will not be scored separately
	 * and members are allowed to sign up for other subdivisions
	 * 
	 * @param name
	 *            the subdivision's name
	 * @param parent
	 *            the parent of the subdivision
	 */
	public SubDivision(String name, AbstractDivision parent) {
		super(name);

		fParentDiv = parent;
		if (parent instanceof SubDivision) {
			((SubDivision) parent).fLastSplit = false;
		}
		fScoreSeparately = false;
		fMonopoly = false;
		fEntries = new EntryList();
		fGroup = FINAL;
		fLastSplit = true;
		fRaceAddon = 0.0;
	}

	 @Override public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		try {
			SubDivision that = (SubDivision) obj;
			if (!Util.equalsWithNull(this.fParentDiv, that.fParentDiv))
				return false;
			if (this.fScoreSeparately != that.fScoreSeparately)
				return false;
			if (this.fMonopoly != that.fMonopoly)
				return false;
			if (this.fRaceAddon != that.fRaceAddon)
				return false;
			if (!Util.equalsWithNull(this.fEntries, that.fEntries))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * determines if entries in this subdivision will carry the same race and
	 * series points as the parent division or will be rescored as an
	 * independent class
	 * 
	 * @param b
	 *            true to score separately, false to pull from parent. Default
	 *            is false
	 */
	public void setScoreSeparately(boolean b) {
		if (b != fScoreSeparately) {
			boolean hold = fScoreSeparately;
			fScoreSeparately = b;
			firePropertyChange(SCORESEPARATELY_PROPERTY, new Boolean(hold),
					new Boolean(fScoreSeparately));
		}
	}

	 @Override public boolean isOneDesign() {
		if (fParentDiv == null)
			return true;
		else
			return fParentDiv.isOneDesign();
	}

	/**
	 * default is false
	 * 
	 * @see setScoreSeparately
	 * @return whether or not subdivision will be scored independent of its
	 *         parent
	 */
	public boolean isScoreSeparately() {
		return fScoreSeparately;
	}

	/**
	 * determins if entries in this subdivision are allowed to enter another
	 * subdivision.
	 * 
	 * @param b
	 *            true if subdiv is "monopolistic" and entries cannot enter
	 *            another subdivision false if they may enter another
	 *            subdivision
	 */
	public void setMonopoly(boolean b) {
		if (b != fMonopoly) {
			boolean hold = fMonopoly;
			fMonopoly = b;
			firePropertyChange(MONOPOLY_PROPERTY, new Boolean(hold),
					new Boolean(fMonopoly));
		}
	}

	/**
	 * default is false.
	 * 
	 * @see setMonopoly
	 * @return true if subdiv is "monopolistic" and entries cannot enter another
	 *         subdivision
	 */
	public boolean isMonopoly() {
		return fMonopoly;
	}

	/**
	 * adds an entry to the subdivision
	 * 
	 * @param entry
	 * @return
	 */
	public void addEntry(Entry e) {
		if (!fEntries.contains(e))
			fEntries.add(e);
	}

	/**
	 * removes an entry from subdivision
	 * 
	 * @param e
	 */
	public void removeEntry(Entry e) {
		fEntries.remove(e);
	}
	
	public void removeAllEntries() {
		fEntries.clear();
	}

	/**
	 * the number of entries in the subdivision
	 * 
	 * @return
	 */
	 @Override public int getNumEntries() {
		return fEntries.size();
	}

	/**
	 * Should return the number of boats registered for the specified division
	 * and race in the specified regatta. Note that if the division is not
	 * racing in the specified race, this returns 0.
	 * 
	 * @param race
	 * @return
	 */
	 @Override public int getNumEntries(Race race) {
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg == null) return 0;
		if (!isRacing( race)) return 0;

//		if (reg.isMultistage()) {
//			Race lastRace = null; // ((MultiStage) reg.getScoringManager()).getLastRaceBeforeSplit();
//			if (race.isAfter(lastRace)) {
//				// after the split, sum qualify members always 0
//				if (isQualifyingSeriesGroup()) return 0;
//			} else {
//				// is before the split num final members always 0
//				if (isFinalSeriesGroup()) return 0;
//			}
//		}
		return getNumEntries();
	}

	@Override public AbstractDivision getParentDivision() {
		return fParentDiv;
	}

	public void setParentDivision(AbstractDivision div) {
		fParentDiv = div;
	}

	@Override public EntryList getEntries() {
		EntryList el = new EntryList();
		el.addAll( fEntries);
		return el;
	}
	
	public void clearEntries() {
		fEntries.clear();
	}

	@Override public boolean isRacing( Race race, boolean orParent, boolean orChild) {
		if (super.isRacing(race, orParent, orParent)) return true;
		if (orParent) return race.getDivInfo().isRacing( fParentDiv);
		return false;
	}

	/**
	 * checks to see if entry is contained in this SubDivision
	 * 
	 * @param entry
	 * @return true if entry is in one of the divisions contained in this
	 *         SubDivision
	 */
	@Override public boolean contains(Entry entry) {
		boolean haveit = fEntries.contains(entry);
		return haveit;
	}

	 @Override public void xmlRead(PersistentNode n, Object rootObject) {
		super.xmlRead(n, rootObject);

		String value = n.getAttribute(MONOPOLY_PROPERTY);
		if (value != null)
			fMonopoly = value.toString().equalsIgnoreCase("true");

		value = n.getAttribute(SCORESEPARATELY_PROPERTY);
		if (value != null)
			fScoreSeparately = value.toString().equalsIgnoreCase("true");

		// TODO GROUP and GROUPRANK should go away from a subdivision replaced by stage info
		value = n.getAttribute(GROUP_PROPERTY);
		if (value != null)
			fGroup = value;

		value = n.getAttribute(GROUPRANK_PROPERTY);
		if (value != null) {
			fGroupRank = value;
		}

		value = n.getAttribute(LASTSPLIT_PROPERTY);
		if (value != null)
			fLastSplit = value.toString().equalsIgnoreCase("true");

		value = n.getAttribute(ADDON_PROPERTY);
		if (value != null) {
			fRaceAddon = Double.parseDouble(value);
		}

		String parentName = n.getAttribute(PARENT_PROPERTY);
		if (parentName != null && rootObject != null) {
			Regatta regatta = (Regatta) rootObject;
			AbstractDivision pdiv = regatta.getFleet(parentName);
			if (pdiv == null)
				pdiv = regatta.getDivision(parentName);
			fParentDiv = pdiv;
		}

		PersistentNode entryNode = n.getElement(ENTRYLIST_PROPERTY);
		if (entryNode != null) {
			PersistentNode[] nodes = entryNode.getElements();
			Regatta reg = (Regatta) rootObject;
			if (reg != null) {
				for (int i = 0; i < nodes.length; i++) {
					String id = nodes[i].getAttribute(ENTRYID_PROPERTY);
					Entry entry = reg.getAllEntries().getEntry(
							Integer.parseInt(id));
					if (entry != null) {
						fEntries.add(entry);
					}
				}
			}
		}
	}

	 @Override public void xmlWrite(PersistentNode e) {
		// PersistentNode e = doc.createChildElement();
		super.xmlWrite(e);

		if (fParentDiv != null) {
			e.setAttribute(PARENTTYPE_PROPERTY,
					(fParentDiv instanceof Division) ? typeDivision : typeFleet);
		}

		e.setAttribute(MONOPOLY_PROPERTY, new Boolean(fMonopoly).toString());
		e.setAttribute(SCORESEPARATELY_PROPERTY,
				new Boolean(fScoreSeparately).toString());
		e.setAttribute(GROUP_PROPERTY, fGroup);

		e.setAttribute(GROUPRANK_PROPERTY, fGroupRank);
		e.setAttribute(LASTSPLIT_PROPERTY, new Boolean(fLastSplit).toString());
		e.setAttribute(ADDON_PROPERTY, Double.toString(fRaceAddon));

		if (fParentDiv != null) {
			e.setAttribute(PARENT_PROPERTY, fParentDiv.getName());
		}

		PersistentNode eEnt = e.createChildElement( ENTRYLIST_PROPERTY);
		for (Iterator iter = fEntries.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			PersistentNode child = eEnt.createChildElement( ENTRY_PROPERTY);
			child.setAttribute(ENTRYID_PROPERTY,
					Integer.toString(entry.getId()));
		}

		// return e;
	}

	/**
	 * use isQualifyingSeriesGroup to get any group that is part of a qualifying series
	 * (currently only one group, but the future may never know)
	 * use isGroupQualifying to get specifically a QUALIFYING group
	 * @return
	 */
	@Override public boolean isGroupQualifying() {
		return fGroup.equals(QUALIFYING);
	}

	public boolean isGroupScoring() {
		return fGroup.equals(SCORING);
	}

	public String getGroup() {  // use isGroupQualifying or isGroupScoring where possible
		return fGroup;
	}

	public void setGroup(String b) {
		if (!b.equals(fGroup)) {
			String hold = fGroup;
			fGroup = b;
			firePropertyChange(GROUP_PROPERTY, hold, fGroup);
		}
	}

	/**
	 * A ranking of finals groups for overall division order. Closer to zero is
	 * higher, so if you split into two groups, say Gold / Silver - Gold gets
	 * 0.1, Silver 0.2, if Gold subsequently gets split into Medal and Gold
	 * Consolation, then Medal gets 0.11 and Medal consolation gets 0.12.
	 * Overall fleet postions in ascending order Medal/0.11, Medal
	 * Consolation/0.12, Silver/0.2, and so on.
	 * 
	 * @return
	 */
	public String getGroupRank() {
		return fGroupRank;
	}

	public void setGroupRank(String groupRank) {
		fGroupRank = groupRank;
	}

	public boolean isLastSplit() {
		return fLastSplit;
	}

	public void setLastSplit(boolean fLastSplit) {
		this.fLastSplit = fLastSplit;
	}

	
	/**
	 * The addon points will be added to the race score for every boat in the
	 * subdivision (Used to do the "Youngster" penalties in Laser Masters
	 * Scoring)
	 */
	public double getRaceAddon() {
		return fRaceAddon;
	}

	public void setRaceAddon(double a) {
		fRaceAddon = a;
	}
}
/**
 * $Log: SubDivision.java,v $ Revision 1.8 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.7 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.6 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.4 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests
 * work
 * 
 * Revision 1.3 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/11 02:17:16 sandyg Bug fixes relative to qualify/final
 * race scoring
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.7.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.7.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.7 2004/05/06 02:11:50 sandyg Beta support for revised
 * Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.6 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.5 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.4 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.3 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
