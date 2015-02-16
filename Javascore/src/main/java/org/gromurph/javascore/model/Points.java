//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Points.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.text.DecimalFormat;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Contains bae points information, abstract class implements as RacePoints and
 * SeriesPoints
 **/
public abstract class Points extends org.gromurph.util.BaseObject {
	private Entry fEntry;
	private AbstractDivision fDivision;
	private double fPoints;
	protected long fPosition;
	protected final static DecimalFormat sNumFormat;
	
	// used in scoring only;
	transient public Stage stage = null;

	protected transient String aId; // for debugging so boat's name pops up in
									// debugger listing
	public static final String POSITION_PROPERTY = "Pos";

	static {
		sNumFormat = new DecimalFormat();
		sNumFormat.setMaximumFractionDigits(2);
		sNumFormat.setMinimumFractionDigits(0);
	}

	protected Points(Entry entry, AbstractDivision div, double points, int pos) {
		fEntry = entry;
		fDivision = div;
		fPoints = points;
		fPosition = pos;
		if (fEntry != null && fEntry.getBoat() != null)
			aId = fEntry.getBoat().getSailId().toString();
	}

	@Override public String toString() {
		StringBuffer sb = new StringBuffer(30);
		sb.append(fEntry.getBoat().getSailId().toString());
		sb.append("/");
		sb.append( (fDivision == null ? "<null>" : fDivision.getName()));
		sb.append("/");
		sb.append(fPoints);
		sb.append("/");
		sb.append(fPosition);
		sb.append("/");
		sb.append( (stage == null ? "<null>" : stage.getName()));
		return sb.toString();
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Points))
			return false;

		Points that = (Points) obj;
		if (this.fPoints != that.fPoints)
			return false;
		if (this.fPosition != that.fPosition)
			return false;
		if (!Util.equalsWithNull(this.fEntry, that.fEntry))
			return false;
		return true;
	}

	public static final String POINTS_PROPERTY = "Pts";
	public static final String ENTRY_PROPERTY = "Ent";
	public static final String DIVISION_PROPERTY = "Div";

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fDivision = AbstractDivision.NONE;
		fEntry = null;
		fPoints = 0.0;
		fPosition = 0;

		Regatta reg = (Regatta) rootObject;
		if (reg == null)
			reg = JavaScoreProperties.getRegatta();
		Entry lastEntry = null;
		AbstractDivision lastDiv = null;

		String value = "";

		if ((value = n.getAttribute(ENTRY_PROPERTY)) != null) {
			int id = Integer.parseInt(value);
			if (lastEntry == null || id != lastEntry.getId()) {
				lastEntry = reg.getAllEntries().getEntry(id);
			}
			fEntry = lastEntry;
			if (fEntry != null && fEntry.getBoat() != null)
				aId = fEntry.getBoat().getSailId().toString();
		}

		if ((value = n.getAttribute(POSITION_PROPERTY)) != null) {
			try {
				setPosition(Integer.parseInt(value));
			} catch (Exception e) {
			}
		}

		if ((value = n.getAttribute(DIVISION_PROPERTY)) != null) {
			if ((lastDiv == null) || (!lastDiv.getName().equals(value))) {
				lastDiv = reg.getDivision(value);
			}
			fDivision = lastDiv;
		}

		if ((value = n.getAttribute(POINTS_PROPERTY)) != null) {
			try {
				double pts = Double.parseDouble(value);
				setPoints(pts);
			} catch (Exception e) {
				logger.error("Cant parsedouble({}): {}", value , e);
			}
		}

	}

	@Override public void xmlWrite(PersistentNode e) {
		if (getEntry() == null)
			return;

		e.setAttribute(ENTRY_PROPERTY, Integer.toString(getEntry().getId()));
		if (getDivision() != null)
			e.setAttribute(DIVISION_PROPERTY, getDivision().getName());
		e.setAttribute(POINTS_PROPERTY, Double.toString(getPoints()));
		e.setAttribute(POSITION_PROPERTY, Long.toString(getPosition()));
		// return e;
	}

	public void setPoints(double points) {
		Double old = new Double(fPoints);
		fPoints = points;
		firePropertyChange(POINTS_PROPERTY, old, new Double(fPoints));
	}

	public double getPoints() {
		return fPoints;
	}

	public long getPosition() {
		return fPosition;
	}

	public void setPosition(long pos) {
		Long old = new Long(fPosition);
		fPosition = pos;
		firePropertyChange(POSITION_PROPERTY, old, new Long(fPosition));
	}

	public Entry getEntry() {
		return fEntry;
	}

	public AbstractDivision getDivision() {
		return fDivision;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * returns value based on the points, if points are equal, returns 0 ignores
	 * the throwout
	 */
	public int compareTo(Object obj) {
		if (obj == null)
			return -1;
		try {
			Points that = (Points) obj;
			if (this.fPoints < that.fPoints)
				return -1;
			else if (this.fPoints > that.fPoints)
				return 1;
			else
				return 0;
		} catch (java.lang.ClassCastException e) {
			return -1;
		}
	}

}
/**
 * $Log: Points.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release
 * 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.10.2.1 2005/06/26 22:47:18 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.10 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.9 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.8 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.7 2003/03/16 20:39:43 sandyg 3.9.2 release: encapsulated changes
 * to division list in Regatta, fixed a bad bug in PanelDivsion/Rating
 * 
 * Revision 1.6 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.5 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
