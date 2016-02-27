//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Entry.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Person;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class Entry extends BaseObject {
	private static final long serialVersionUID = 1L;
	public static String BLANK = "<blank entry>";

	protected transient String aSail; // just for easy look at debug stacks
	protected transient String aDiv; // just for easy look at debug stacks

	private Bow fBow;
	private Division fDivision;
	private Boat fBoat;
	private Person fSkipper;
	private List<Person> fCrewList; // crewlist is fudged, stored as arraylist,
	// but only 1 element implemented
	private String fMnaNumber;
	private String fRsaNumber;
	private String fClub;
	// private Rating fRating;

	private int fId;
	private static int sNextId = 1;
	public static final String ID_PROPERTY = "EntryId";

	private void setId(String id) {
		fId = new Integer(id).intValue();
		if (fId >= sNextId)
			sNextId = fId + 1;
	}

	public int getId() {
		return fId;
	}

	public transient static final String DIVISION_PROPERTY = "Division";
	public transient static final String BOW_PROPERTY = "Bow";
	public transient static final String BOAT_PROPERTY = "Boat";
	public transient static final String BOATNAME_PROPERTY = "BoatName";
	public transient static final String SKIPPER_PROPERTY = "Skipper";
	public transient static final String CREW_PROPERTY = "Crew";
	public transient static final String CREWLIST_PROPERTY = "CrewList";
	public transient static final String MNANUMBER_PROPERTY = "MnaNumber";
	public transient static final String RSANUMBER_PROPERTY = "RsaNumber";
	public transient static final String CLUB_PROPERTY = "Club";
	public transient static final String RATING_PROPERTY = "Rating";
	public transient static final String SAILID_PROPERTY = "SailId";

	// private static Division sLastDiv = Division.NONE;

	// Flags to including data in toString();

	public static int SHOW_BOW = 0x0001;
	public static int SHOW_BOAT = 0x0002;
	public static int SHOW_SKIPPER = 0x0004;
	public static int SHOW_CREW = 0x0008;
	public static int SHOW_DIVISION = 0x0010;
	public static int SHOW_CLUB = 0x0020;
	public static int SHOW_MNA = 0x0040;
	public static int SHOW_RSA = 0x0080;
	public static int SHOW_RATING = 0x0100;
	public static int SHOW_FULLRATING = 0x0200;
	public static int SHOW_SUBDIVISION = 0x0400;

	private static int sShowFlag = SHOW_BOW + SHOW_BOAT + SHOW_SKIPPER + SHOW_DIVISION;

	public Entry() {
		super();
		fId = sNextId++;
		setSkipper(new Person());
		setBoat(new Boat());
		setBow(new Bow(""));
		// forceDivision( sLastDiv);
		// fRating = null;
		setClub("");

		fCrewList = new ArrayList<Person>();

		setMnaNumber("");
		setRsaNumber("");
	}

	/**
	 * default implementation, creates a new instance of object with default
	 * constructor and compares to it.
	 */
	@Override
	public boolean isBlank() {
		String name = toString(Entry.SHOW_BOW + Entry.SHOW_BOAT + Entry.SHOW_SKIPPER, false);
		return name.equals(BLANK);
	}

	@Override public void xmlWrite(PersistentNode e) {
		e.setAttribute(ID_PROPERTY, Integer.toString(getId()));

		if (fBow != null)
			e.setAttribute(BOW_PROPERTY, getBow().toString());
		if (fDivision != null)
			e.setAttribute(DIVISION_PROPERTY, getDivision().getName());
		if (fMnaNumber.length() > 0)
			e.setAttribute(MNANUMBER_PROPERTY, getMnaNumber());
		if (fRsaNumber.length() > 0)
			e.setAttribute(RSANUMBER_PROPERTY, getRsaNumber());
		if (fClub.length() > 0)
			e.setAttribute(CLUB_PROPERTY, getClub());

		// if (getRating() != null) e.appendChild( getRating().xmlWrite( doc,
		// RATING_PROPERTY));
		if (getBoat() != null)
			getBoat().xmlWrite(e.createChildElement(BOAT_PROPERTY));
		if (getSkipper() != null)
			getSkipper().xmlWrite(e.createChildElement(SKIPPER_PROPERTY));

		PersistentNode crewNode = e.createChildElement( CREWLIST_PROPERTY);
		deleteBlankCrew();
		for (Iterator it = fCrewList.iterator(); it.hasNext();) {
			Person crew = (Person) it.next();
			if (crew != null) crew.xmlWrite(crewNode.createChildElement(CREW_PROPERTY));
		}
		// return e;
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		Rating legacyRating = null;
		fCrewList.clear();

		String value = n.getAttribute(BOW_PROPERTY);
		if (value != null)
			setBow(value);

		value = n.getAttribute(ID_PROPERTY);
		if (value != null)
			setId(value);

		value = n.getAttribute(MNANUMBER_PROPERTY);
		if (value != null)
			setMnaNumber(value);

		value = n.getAttribute(RSANUMBER_PROPERTY);
		if (value != null)
			setRsaNumber(value);

		value = n.getAttribute(CLUB_PROPERTY);
		if (value != null)
			setClub(value);

		value = n.getAttribute(DIVISION_PROPERTY);
		if (value != null) {
			Division div = null;
			Regatta reg = (Regatta) rootObject;
			if (reg == null) {
				div = new Division(value);
			} else {
				div = reg.getDivision(value);
			}
			if (div == null)
				div = new Division(value);

			try {
				setDivision(div);
			} catch (RatingOutOfBoundsException e) {
			}
		}

		PersistentNode child = n.getElement(BOAT_PROPERTY);
		if (child != null) {
			Boat b = new Boat();
			b.xmlRead(child, rootObject);
			setBoat(b);
		}

		child = n.getElement(SKIPPER_PROPERTY);
		if (child != null) {
			Person p = new Person();
			p.xmlRead(child, rootObject);
			setSkipper(p);
		}

		child = n.getElement(CREW_PROPERTY);
		if (child != null) {
			Person p = new Person();
			p.xmlRead(child, rootObject);
			addCrew(p);
		}

		child = n.getElement(CREWLIST_PROPERTY);
		if (child != null) {
			PersistentNode[] crewlist = child.getElements();
			for (int i = 0; i < crewlist.length; i++) {
				Person p = new Person();
				p.xmlRead(crewlist[i], rootObject);
				if (!p.isEmpty())
					addCrew(p);
			}
		}

		child = n.getElement(RATING_PROPERTY);
		if (child != null) {
			// before version 3.2, after v3.2 ratings are ALL in boat
			legacyRating = RatingManager.createRatingFromXml(child, rootObject);
		}

		// set this at end so will overwrite a pre-existing rating for this
		// division
		if (legacyRating != null) {
			try {
				if (getDivision().getSlowestRating() instanceof RatingOneDesign) {
					setRating(getDivision().getSlowestRating());
				} else {
					setRating(legacyRating);
				}
			} catch (RatingOutOfBoundsException roe) {
				Util.showError(roe, true);
			}
		}
	}

	public int compareTo(Object obj) {
		if (!(obj instanceof Entry))
			return -1;
		if (this.equals(obj))
			return 0;

		Entry that = (Entry) obj;

		int i = this.fBow.compareTo(that.fBow);
		if (i != 0)
			return i;

		// i = this.fRating.compareTo( that.fRating);
		// if (i != 0) return i;

		i = this.fBoat.compareTo(that.fBoat);
		if (i != 0)
			return i;

		i = this.fSkipper.compareTo(that.fSkipper);
		if (i != 0)
			return i;

		i = this.fMnaNumber.compareTo(that.fMnaNumber);
		if (i != 0)
			return i;

		i = this.fRsaNumber.compareTo(that.fRsaNumber);
		if (i != 0)
			return i;

		i = this.fClub.compareTo(that.fClub);
		if (i != 0)
			return i;

		return this.getCrew().compareTo(that.getCrew());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		try {
			return (this.fId == ((Entry) obj).fId);
			// Entry that = (Entry) obj;
			//
			// if ( !Util.equalsWithNull( this.fBoat, that.fBoat)) return false;
			// if ( !Util.equalsWithNull( this.fBow, that.fBow)) return false;
			// if ( !Util.equalsWithNull( this.fSkipper, that.fSkipper)) return
			// false;
			// if ( !Util.equalsWithNull( this.fDivision, that.fDivision))
			// return false;
			// if ( !Util.equalsWithNull( this.fRating, that.fRating)) return
			// false;
			// return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return fId;
	}

	public static String databaseSelect() {
		return "";
	}

	public static Class getColumnClass(int c) {
		switch (c) {
		case 0:
			return Division.class;
		case 1:
			return Integer.class;
		case 2:
			return Boat.class;
		case 3:
			return Person.class;
		case 4:
			return Double.class;
		}
		return null;
	}

	public static int getColumnCount() {
		return 5;
	}

	public static String getColumnName(int c) {
		switch (c) {
		case 0:
			return "Division";
		case 1:
			return "Sail/Bow";
		case 2:
			return "Boat";
		case 3:
			return "Skipper";
		case 4:
			return "Tot Pts";
		}
		return null;
	}

	private static String fDelimMain = "/ ";

	/**
	 * returns entry label concatenated and ordered by the array of flags
	 */
	public String toString(List flags, boolean doHtml, Race r) {
		return toString(flags, doHtml, r, false);
	}

	/**
	 * returns entry label concatenated and ordered by the array of flags
	 */
	public String toString(List flags, boolean doHtml, Race r, boolean dummydiv) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < flags.size(); i++) {
			String x = toString(((Integer) flags.get(i)).intValue(), doHtml, r, false, dummydiv);
			// x = addAndTrim( x);
			sb.append(x);
		}
		String x = sb.toString();
		if (x.length() > 0)
			return x.substring(1);
		else
			return BLANK;
	}

	@Override
	public String toString() {
		String s = toString(sShowFlag, false, null, true, false);
		if (s.length() == 0)
			return BLANK;
		else
			return s;
	}

	public String toString(int flag, boolean doHtml) {
		String s = toString(flag, doHtml, null, true, false);
		if (s.length() == 0)
			return BLANK;
		else
			return s;
	}

	public String toString(int flag, boolean doHtml, Race race) {
		return toString(flag, doHtml, race, true, false);
	}

	public String toString(int flag, boolean doHtml, Race race, boolean includeSail, boolean dummysubdiv) {
		StringBuffer sb = new StringBuffer();

		if (includeSail) {
			if ((fBow.toString().length() > 0) && ((flag & SHOW_BOW) != 0)
					&& (JavaScoreProperties.getRegatta() != null)
					&& (JavaScoreProperties.getRegatta().isUseBowNumbers())) {
				sb.append(fBow.toString());
				sb.append(fDelimMain);
			}
			sb.append(fBoat.getSailId().toString());
		}

		if ((flag & SHOW_BOAT) != 0)
			sb.append(addAndTrim(fBoat.getName()));
		if (((flag & SHOW_SKIPPER) != 0) && fSkipper != null)
			sb.append(addAndTrim(personToString(fSkipper, doHtml)));

		if ((flag & SHOW_CREW) != 0) {
			for (int i = 0; i < getNumCrew(); i++)
				sb.append(addAndTrim(personToString(getCrew(i), doHtml), "/"));
		}

		if ((flag & SHOW_CLUB) != 0)
			sb.append(addAndTrim(getClub()));
		if ((flag & SHOW_MNA) != 0)
			sb.append(addAndTrim(getMnaNumber()));
		if ((flag & SHOW_RSA) != 0)
			sb.append(addAndTrim(getRsaNumber()));
		Rating rating = getRating();
		if ((flag & SHOW_RATING) != 0 && rating != null)
			sb.append(addAndTrim(rating.toString(false)));
		if ((flag & SHOW_FULLRATING) != 0 && rating != null)
			sb.append(addAndTrim(rating.toString(true)));

		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg != null) {
			if ((flag & SHOW_DIVISION) != 0) {
				sb.append(fDelimMain);
				sb.append(getDivision().getName());
			}
			boolean firstSubDiv = true;
			if ((flag & SHOW_SUBDIVISION) != 0) {
				sb.append(fDelimMain);
				if (dummysubdiv) {
					sb.append("Subdiv");
				} else {
					for (SubDivision sd : reg.getSubDivisions()) {
						if (!sd.contains(this)) continue;  // skip if entry not in subdiv
						if (race != null) {
							if (!sd.isRacing(race) && !sd.isGroupScoring()) continue; 
							// skip if subdiv not in race 
						} else {
							// without a race, if split regatta include only scoring groups
							if (reg.isMultistage() && !sd.isGroupScoring()) continue;
						}
											
						// want it
						if (!firstSubDiv) sb.append(",");
						sb.append(sd.getName());
						firstSubDiv = false;
					}
				}
			}
		}

		String s = sb.toString();
		return s;
	}

	private String personToString(Person who, boolean doHtml) {
		if (!doHtml)
			return who.toString();
		else
			return who.toHtml();
	}

	private String addAndTrim(String newStr) {
		return addAndTrim(newStr, fDelimMain);
	}

	private String addAndTrim(String newStr, String delim) {
		String s = newStr.trim();
		if (s.length() > 0)
			return delim + s;
		else
			return "";
	}

	/**
	 * returns true if entry's sail id match the specified id - this does NOT
	 * check bow id
	 **/
	public boolean matchesId(SailId id) {
		return fBoat.getSailId().equals(id);
	}

	/**
	 * returns true if entry's sail id (or bow if applicable) match the
	 * specified String
	 **/
	public boolean matchesId(String sail) {
		SailId id = new SailId(sail);

		if (fBoat.getSailId().equals(id)) {
			return true;
		} else if ((fBoat.getSailId().getNumber() == id.getNumber()) && (id.getPrefix().length() == 0)
				&& (id.getPostfix().length() == 0)) {
			// if input id has no prefix or postfix, match on number alone
			return true;
		} else {
			return ((fBow != null) && fBow.equals(new Bow(sail)));
		}
	}

	public Rating getRating() {
		// return fRating;
		return fBoat.getRating(fDivision.getSystem());
	}

	/**
	 * sets (or adds) rating to the Entry (actually the Boat within the entry).
	 * Does NOT change the entry's division
	 * 
	 * @param rtg
	 *            the new rating to be assigned to the Entry
	 * 
	 * @throws RatingOutOfBoundsException
	 *             if Entry's division is same system as as the new rating, and
	 *             that division does NOT contain the rating
	 */
	public void setRating(Rating rtg) throws RatingOutOfBoundsException {
		if ((getDivision() != null) && (fDivision.getSystem().equals(rtg.getSystem())) && (!fDivision.contains(rtg))) {
			throw new RatingOutOfBoundsException(fDivision, rtg);
		}

		Rating oldrtg = fBoat.getRating(fDivision.getSystem());
		fBoat.putRating(rtg);
		firePropertyChange(RATING_PROPERTY, oldrtg, rtg);
	}

	public int getNumRatings() {
		return fBoat.getNumRatings();
	}

	public Bow getBow() {
		return fBow;
	}

	public void setBow(String inBow) {
		setBow(new Bow(inBow));
	}

	public void setBow(Bow inBow) {
		Bow hold = fBow;
		fBow = inBow;
		firePropertyChange(BOW_PROPERTY, hold, fBow);
	}

	public String getMnaNumber() {
		return fMnaNumber;
	}

	public void setMnaNumber(String inMna) {
		String hold = fMnaNumber;
		fMnaNumber = inMna;
		firePropertyChange(MNANUMBER_PROPERTY, hold, fMnaNumber);
	}

	public String getRsaNumber() {
		return fRsaNumber;
	}

	public void setRsaNumber(String inRsa) {
		String hold = fRsaNumber;
		fRsaNumber = inRsa;
		firePropertyChange(RSANUMBER_PROPERTY, hold, fRsaNumber);
	}

	public String getClub() {
		return fClub;
	}

	public void setClub(String inClub) {
		String hold = fClub;
		fClub = inClub;
		firePropertyChange(CLUB_PROPERTY, hold, fClub);
	}

	public Division getDivision() {
		if (fDivision == null)
			fDivision = AbstractDivision.NONE;
		return fDivision;
	}

	public void setDivision(Division inDiv) throws RatingOutOfBoundsException {
		if (inDiv == null)
			inDiv = AbstractDivision.NONE;

		if (!inDiv.equals(AbstractDivision.NONE)) {
			Rating rtg = fBoat.getRating(inDiv.getSystem());

			if (rtg == null) {
				rtg = (Rating) inDiv.getSlowestRating().clone();
				fBoat.putRating(rtg);
			} else if (inDiv.isOneDesign()) {
				// changing out a one-design rating is OK
				fBoat.removeRating(rtg);
				rtg = inDiv.getSlowestRating().createSlowestRating();
				fBoat.putRating(rtg);
			} else if (!inDiv.contains(rtg)) {
				throw new RatingOutOfBoundsException(inDiv, rtg);
			}
		}

		Division hold = fDivision;
		// if (fDivision != null) fDivision.removePropertyChangeListener(this);
		fDivision = inDiv;
		// sLastDiv = inDiv;
		// if (fDivision != null) fDivision.addPropertyChangeListener(this);
		firePropertyChange(DIVISION_PROPERTY, hold, inDiv);
		aDiv = (fDivision == null) ? null : fDivision.toString();
	}

	public Boat getBoat() {
		return fBoat;
	}

	public void setBoat(Boat inBoat) {
		Boat hold = fBoat;
		if (fBoat != null)
			fBoat.removePropertyChangeListener(this);
		fBoat = inBoat;
		if (fBoat != null)
			fBoat.addPropertyChangeListener(this);
		firePropertyChange(BOAT_PROPERTY, hold, fBoat);
		aSail = (fBoat == null) ? null : fBoat.getSailId().toString();
	}

	public void setBoatName(String inBoat) {
		String oldName = fBoat.getName();
		fBoat.setName(inBoat);
		firePropertyChange(BOATNAME_PROPERTY, oldName, inBoat);
	}

	public void setSailId(SailId newId) {
		SailId oldId = fBoat.getSailId();
		fBoat.setSailId(newId);
		firePropertyChange(SAILID_PROPERTY, oldId, newId);
		aSail = (newId == null) ? null : newId.toString();
	}

	public Person getSkipper() {
		return fSkipper;
	}

	public void setSkipper(Person inSkipper) {
		Person hold = fSkipper;
		if (fSkipper != null)
			fSkipper.removePropertyChangeListener(this);
		fSkipper = inSkipper;
		if (fSkipper != null)
			fSkipper.addPropertyChangeListener(this);
		firePropertyChange(SKIPPER_PROPERTY, hold, inSkipper);
	}

	public List<Person> getCrewList() {
		return fCrewList;
	}

	public int getNumCrew() {
		return fCrewList.size();
	}

	public void addCrew(Person crew) {
		fCrewList.add(crew);
	}

	public void deleteBlankCrew() {
		List<Person> blanks = new ArrayList<Person>(10);
		for (Person c : fCrewList) {
			if ( c.getFirst().isEmpty() && c.getLast().isEmpty() && c.getSailorId().isEmpty()) {
				blanks.add(c);
			}
		}
		if (blanks.size() > 0) fCrewList.removeAll(blanks);
	}
	
	public Person getCrew() {
		return getCrew(0);
	}

	public void setCrew(Person crew) {
		setCrew(0, crew);
	}

	public Person getCrew(int index) {
		while (fCrewList.size() <= index) {
			fCrewList.add(new Person());
		}
		return fCrewList.get(index);
	}

	public void setCrew(int index, Person crew) {
		Person oldCrew;

		if (fCrewList.size() <= index) {
			while (fCrewList.size() <= index)
				fCrewList.add(crew);
			oldCrew = null;
		} else {
			oldCrew = fCrewList.get(index);
			fCrewList.set(index, crew);
		}
		firePropertyChange(CREW_PROPERTY, oldCrew, crew);
	}

}
/**
 * $Log: Entry.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release
 * 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.22.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.22.2.1 2005/06/26 22:47:17 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.22 2005/06/05 12:19:23 sandyg Bug 1215116, bad rating on imports
 * (or actually just creating a new Entry()
 * 
 * Revision 1.21 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time
 * scores to no longer round to a second
 * 
 * Revision 1.20 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.19 2004/01/17 22:27:37 sandyg First cut at unlimited number of
 * crew, request 512304
 * 
 * Revision 1.18 2003/07/10 02:50:30 sandyg overrides a contradictory legacy
 * one-design rating on xmlRead
 * 
 * Revision 1.17 2003/05/17 22:43:44 sandyg changing rating to another 1D rating
 * now OK, just replaces old 1D rating
 * 
 * Revision 1.16 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.15 2003/05/02 02:41:38 sandyg fixed division update problem in
 * panelentry
 * 
 * Revision 1.14 2003/04/30 01:01:33 sandyg sets null division to NONE on
 * getDivision()
 * 
 * Revision 1.13 2003/04/27 21:03:26 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.12 2003/03/30 00:05:43 sandyg moved to eclipse 2.1
 * 
 * Revision 1.11 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.10 2003/01/06 00:32:37 sandyg replaced forceDivision and
 * forceRating statements
 * 
 * Revision 1.9 2003/01/05 21:16:31 sandyg regression unit testing following
 * rating overhaul from entry to boat
 * 
 * Revision 1.8 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */
