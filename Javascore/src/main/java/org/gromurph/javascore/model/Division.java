// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Division.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
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

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Implements AbstractDivision for starting classes.
 **/
public class Division extends AbstractDivision {

	private static final long serialVersionUID = 1L;

	private Rating fastestRating;
	private Rating slowestRating;

	/**
	 * checks to see if division is contained in this division. If 'div' is a Fleet, always returns false, if the 'div'
	 * is another Division, returns true ONLY if div is '==', if div is a subdivision, returns true if the subdiv's
	 * parent div is '=='
	 * 
	 * @param div
	 * @return true if div is in one of the divisions contained in this Fleet
	 */
	@Override
	public boolean contains(AbstractDivision div) {
		if (div == null) return false;
		if (div instanceof Fleet) return false;
		if (div instanceof Division) return this.equals(div);
		if (div instanceof SubDivision) return this.equals(((SubDivision) div).getParentDivision());
		return false;
	}

	/**
	 * basic constructor creates new division with no name, onedesign, no name, undefined min/max ratings
	 * 
	 * @see Rating#getSupportedSystems
	 **/
	public Division() {
		this("<none>");
	}

	/**
	 * basic constructor creates new one design division
	 * 
	 * @param name
	 *            name of the division
	 * @see Rating#getSupportedSystems
	 **/
	public Division(String name) {
		this(name, new RatingOneDesign(name), new RatingOneDesign(name));
	}

	/**
	 * basic constructor creates new division
	 * 
	 * @param name
	 *            division's name
	 * @param sys
	 *            rating system for the division, by default the range of ratings is the systems minimum and maximum
	 *            allowed values
	 * @see Rating#getSupportedSystems
	 **/
	public Division(String name, String sys) {
		this(name, RatingManager.createRating(sys), RatingManager.createRating(sys));
	}

	/**
	 * Full Constructor with full arguments, all other constructors should call this one
	 * 
	 * @param inName
	 *            String of division name
	 * @param fastest
	 *            the slowest rating for the class, this rating's system drives the rating system
	 * @param slowest
	 *            the fastest rating contained in this class
	 *            <P>
	 *            NOTE: there are no checks to make sure that the same rating system is used for slowest and fastest rating
	 **/
	public Division(String inName, Rating fastest, Rating slowest) {
		super(inName);
		setSlowestRating(slowest);
		setFastestRating(fastest);
	}

	public static final String SLOWESTRATING_PROPERTY = "SlowestRating";
	public static final String FASTESTRATING_PROPERTY = "FastestRating";
	
	@Override
	public void xmlWrite(PersistentNode e) {
		super.xmlWrite(e);
		if (slowestRating != null) slowestRating.xmlWrite(e.createChildElement(SLOWESTRATING_PROPERTY));
		if (fastestRating != null) fastestRating.xmlWrite(e.createChildElement(FASTESTRATING_PROPERTY));
		//return e;
	}

	@Override
	public void xmlRead(PersistentNode n, Object rootObject) {
		super.xmlRead(n, rootObject);

		Rating slowest = null;
		Rating fastest = null;

		String OLD_MAXRATING_PROPERTY = "MaxRating";
		String OLD_MINRATING_PROPERTY = "MinRating";

		PersistentNode n2 = n.getElement(SLOWESTRATING_PROPERTY);
		if( n2 == null) n2 = n.getElement(OLD_MAXRATING_PROPERTY);
		if (n2 != null) {
			slowest = RatingManager.createRatingFromXml(n2, rootObject);
		} 

		n2 = n.getElement(FASTESTRATING_PROPERTY);
		if (n2 == null) n2 = n.getElement(OLD_MINRATING_PROPERTY);
		if (n2 != null) {
			fastest = RatingManager.createRatingFromXml(n2, rootObject);
		}

		if (slowest == null) {
			slowest = new RatingOneDesign(getName());
		}

		if (fastest == null) {
			fastest = (Rating) slowest.clone();
		}
		setSlowestRating(slowest);
		setFastestRating(fastest);
	}

	/**
	 * to be equal, the name, slowest and fastest rating must both be equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		try {
			if (!super.equals(obj)) return false;

			Division that = (Division) obj;
			if (!Util.equalsWithNull(this.slowestRating, that.slowestRating)) return false;
			if (this.isOneDesign()) return true;

			if (!Util.equalsWithNull(this.fastestRating, that.fastestRating)) return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Should return the number of registered boats for this division
	 * 
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
	@Override
	public int getNumEntries() {
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg == null) return 0;

		return getEntries().size();
	}

	/**
	 * Should return the number of boats registered for the specified division and race in the specified regatta. Note
	 * that if the division is not racing in the specified race, this returns 0.
	 * 
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
	@Override
	public int getNumEntries(Race race) {
		if (isRacing(race)) return getNumEntries();
		else return 0;
	}

	public String getSystem() {
		if (slowestRating == null) return RatingOneDesign.SYSTEM;
		else return slowestRating.getSystem();
	}

	public void setSystem(String sysName) {
		if (!sysName.equals(getSystem())) {

			Rating r = RatingManager.convertRating(sysName, slowestRating);
			if (r == null) {
				r = RatingManager.createRating(sysName);
				r = r.createSlowestRating();
			}
			setSlowestRating(r);

			r = RatingManager.convertRating(sysName, fastestRating);
			if (r == null) {
				r = RatingManager.createRating(sysName);
				r = r.createFastestRating();
			}
			setFastestRating(r);
		}
	}

	@Override
	public String toString() {
		if (getName().trim().length() == 0) return NO_NAME;
		else return getName();
	}

	/**
	 * returns a "name( slowest to fastest)"
	 * 
	 * @returns String
	 **/
	@Override
	public String getLongName() {
		StringBuffer sb = new StringBuffer();
		if (!isOneDesign()) {
			sb.append(getName());
			sb.append("(");
			if (slowestRating != null) {
				sb.append(slowestRating.getPrimaryValue());
			} else {
				sb.append("<none>");
			}
			sb.append(" thru ");

			if (fastestRating != null) {
				sb.append(fastestRating.getPrimaryValue());
				sb.append(")");
			} else {
				sb.append("<none>");
			}
		} else if (slowestRating != null) {
			sb.append(slowestRating.toString(false));
		} else {
			sb.append(getName());
		}
		return sb.toString();
	}

	@Override
	public boolean isOneDesign() {
		if (slowestRating == null) return true;
		else return slowestRating.isOneDesign();
	}

	@Override
	public EntryList getEntries() {
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg == null) return new EntryList();
		else return reg.getAllEntries().findAll(this);
	}

	/**
	 * returns true if specified entry is contained in this class
	 * 
	 * @param entry
	 * @returns true if entry's rating is contained in this division
	 */
	@Override
	public boolean contains(Entry entry) {
		return equals(entry.getDivision());
	}

	/**
	 * returns true if specified rating is contained in this class
	 * 
	 * @param inRat
	 * @returns true if rating is contained within this division
	 */
	@Override
	public boolean contains(Rating inRat) {
		if (inRat == null) return false;

		boolean b = false;
		if (isOneDesign()) {
			b = (inRat.compareTo(slowestRating) == 0);
		} else if (fastestRating.isSlower(inRat)) return false;
		else if (slowestRating.isFaster(inRat)) return false;
		return true;
	}

	/**
	 * returns the max rating as a Rating
	 * 
	 * @returns maximum allowed rating for the division
	 **/
	public Rating getFastestRating() {
		return fastestRating;
	}

	/**
	 * sets the maximum rating for the class, as a double
	 * 
	 * @param inRat
	 *            Rating object for division maximum
	 *
	 *            is ignored if slowestrating is a onedesign
	 **/
	public void setFastestRating(Rating inRat) {
		if (fastestRating != null) fastestRating.removePropertyChangeListener(this);
		Rating oldRat = fastestRating;
		fastestRating = inRat;
		if (fastestRating != null) fastestRating.addPropertyChangeListener(this);
		firePropertyChange(FASTESTRATING_PROPERTY, oldRat, inRat);
	}

	/**
	 * returns the slowest rating as a Rating
	 * 
	 * @returns slowest allowed rating for the division
	 **/
	public Rating getSlowestRating() {
		return slowestRating;
	}

	/**
	 * sets the fastest rating for the class, as a double
	 * 
	 * @param inRat
	 *            fastest rating for class
	 *
	 *            if system is onedesign, it will ignore fastest rating 
	 **/
	public void setSlowestRating(Rating inRat) {
		if (slowestRating != null) slowestRating.removePropertyChangeListener(this);
		Rating oldRat = slowestRating;
		slowestRating = inRat;
		if (slowestRating != null) slowestRating.addPropertyChangeListener(this);
		firePropertyChange(SLOWESTRATING_PROPERTY, oldRat, inRat);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == slowestRating) {
			firePropertyChange(SLOWESTRATING_PROPERTY, null, slowestRating);
		} else if (event.getSource() == fastestRating) {
			firePropertyChange(FASTESTRATING_PROPERTY, null, fastestRating);
		}
	}
	
	@Override
	public boolean isTimeOnTime() { 
		return (fastestRating instanceof RatingPhrfTimeOnTime); 
	}
}
/**
 * $Log: Division.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 *
 * Revision 1.5 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 *
 * Revision 1.3 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests work
 *
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 *
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 *
 * Revision 1.12.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 *
 * Revision 1.12.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 *
 * Revision 1.12.2.1 2005/06/26 22:47:17 sandyg Xml overhaul to remove xerces dependence
 *
 * Revision 1.12 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.11 2004/04/10 20:49:28 sandyg Copyright year update
 *
 * Revision 1.10 2003/04/27 21:36:11 sandyg more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.9 2003/03/28 03:07:43 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.8 2003/03/27 02:47:01 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 *
 * Revision 1.7 2003/03/16 20:39:14 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 *
 * Revision 1.6 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 *
 */
