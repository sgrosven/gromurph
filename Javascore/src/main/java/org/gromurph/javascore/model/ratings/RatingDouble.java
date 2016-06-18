// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingDouble.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import java.text.MessageFormat;
import java.util.Iterator;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

/**
 * superclass for ratings with a single double value
 **/
public abstract class RatingDouble extends Rating {
	static java.util.ResourceBundle res = JavaScoreProperties.getResources();

	/**
	 * should return the precision with which ratings are saved used by toString to display rating, and
	 * PanelRatingDouble to round input
	 */
	abstract public int getDecs();

	public static final String VALUE_PROPERTY = "Value";

	protected double fValue;

	public RatingDouble() {
		this("", Double.NaN);
	}

	public RatingDouble(String name, double inV) {
		super(name);
		fValue = inV;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode() + (int) fValue;
		return hash;
	}

	@Override
	public boolean isOneDesign() {
		return false;
	}

	@Override
	public int compareTo(Object o) throws ClassCastException {
		try {
			RatingDouble r = (RatingDouble) o;
			if (this.getPrimaryValue() < r.getPrimaryValue()) return -1;
			else if (this.getPrimaryValue() == r.getPrimaryValue()) return 0;
			else return 1;
		} catch (ClassCastException e) {
			return this.getClass().getName().compareTo(o.getClass().getName());
		}
	}

	@Override
	public boolean equals(Object inObj) {
		if (this == inObj) return true;
		if (!super.equals(inObj)) return false;
		try {
			RatingDouble that = (RatingDouble) inObj;
			return this.fValue == that.fValue;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public double getPrimaryValue() {
		return fValue;
	}

	/**
	 * should be ONLY used by RatingManager!
	 *
	 * @param inV
	 */
	@Override
	public void setPrimaryValue(double inV) {
		double oldV = fValue;
		fValue = inV;
		firePropertyChange(VALUE_PROPERTY, new Double(oldV), new Double(fValue));
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public String toString(boolean full) {
		StringBuffer sb = new StringBuffer(16);

		if (full) {
			sb.append(getSystem());
			sb.append("/");
		}

		if (fValue == Double.POSITIVE_INFINITY) sb.append(res.getString("RatingMaxValue"));
		else if (fValue == Double.NEGATIVE_INFINITY) sb.append(res.getString("RatingMinValue"));
		else {
			sb.append(Util.formatDouble(fValue, getDecs()));
		}
		return sb.toString();
	}

	@Override
	public void xmlRead(PersistentNode n, Object rootObject) {
		super.xmlRead(n, rootObject);

		String value = n.getAttribute(VALUE_PROPERTY);
		if (value != null) {
			fValue = Double.parseDouble(value);
		}
	}

	@Override
	public void xmlWrite(PersistentNode e) {
		super.xmlWrite(e);
		e.setAttribute(VALUE_PROPERTY, Double.toString(getPrimaryValue()));
		//return e;
	}

	protected void validateFinishTimesAfterStartTimes(Race race, Division div, WarningList warnings) {
		for (Finish fin : race.getAllFinishers()) {
			if (fin.getEntry().getDivision().equals(div)) {
				if (fin.getFinishPosition().isValidFinish() && fin.getFinishTime() != SailTime.NOTIME
						&& fin.getElapsedTime() < 0) {
					warnings.add(MessageFormat.format(res.getString("WarningBoatFinishBeforeStart"),
							new Object[] { fin.getEntry(), race }));
				}
			}
		}
	}

	protected void validateValidFinishTime(Race race, Division div, WarningList warnings) {
		for (Finish fin : race.getAllFinishers()) {
			if (fin.getEntry().getDivision().equals(div)) {
				if (fin.getFinishPosition().isValidFinish() && fin.getFinishTime() == SailTime.NOTIME) {
					warnings.add(MessageFormat.format(res.getString("WarningBoatNeedsFinishTime"),
							new Object[] { fin.getEntry(), race }));
				}
			}
		}
	}

	/**
	 * rounds a double containing time in milliseconds to the nearest whole second, then returns that time in
	 * milliseconds
	 * 
	 * @Deprecated
	 */
	protected long wholeSecondInMillis(double millies) {
		double corrected = Math.round(millies / 1000);
		return (long) corrected * 1000;
	}
}
/**
 * $Log: RatingDouble.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 *
 * Revision 1.4 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 *
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 *
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 *
 * Revision 1.12.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 *
 * Revision 1.12.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces dependence
 *
 * Revision 1.12 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.11 2004/04/10 20:49:28 sandyg Copyright year update
 *
 * Revision 1.10 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.9 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.8 2003/03/27 02:47:01 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 *
 * Revision 1.7 2003/01/05 21:29:29 sandyg fixed bad version/id string
 *
 * Revision 1.6 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 *
 */
