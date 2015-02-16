//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: FinishPosition.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ResourceBundle;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.BaseObject;
import org.gromurph.xml.PersistentNode;

/**
 * Class for storing finish position numbers. This is mostly an integer with the
 * raw finish order, but also handles non-finish values such as DNC, and DNF.
 * NOTE this class is responsible only for specifying the finish numbers, NOT
 * for determining the points to be assigned. See @ScoringSystems for changing
 * penalties into points.
 * <P>
 * See also the @Penalty class for handling of penalties assigned to boats
 * (whether or not they have a valid finish).
 * 
 * to set a new finish position, recreate the instance
 */
public class FinishPosition extends BaseObject implements Constants {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private static final long serialVersionUID = 1L;

	private long fFinishPosition;

	public FinishPosition(long inPen) {
		if ((inPen & NOFINISH_MASK) != 0) // setting to non-finish penalty...
		// mask out other bits
		{
			fFinishPosition = inPen & NOFINISH;
		} else {
			fFinishPosition = inPen;
		}
	}

	public static long parseString(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			try {
				return Penalty.parsePenalty(value).getPenalty();
			} catch (Exception x) {
				return NOFINISH;
			}
		}
	}

	private final static String POS_PROPERTY = "Pos";

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		String value = n.getAttribute(POS_PROPERTY);
		if (value != null)
			fFinishPosition = parseString(value);
	}

	@Override public void xmlWrite(PersistentNode e) {

		e.setAttribute(POS_PROPERTY, toString());
		// return e;
	}

	public int compareTo(Object obj) {
		if (!(obj instanceof FinishPosition))
			return -1;

		FinishPosition that = (FinishPosition) obj;
		if (this.fFinishPosition < that.fFinishPosition)
			return -1;
		else if (this.fFinishPosition > that.fFinishPosition)
			return 1;
		else
			return 0;
	}

	public boolean isFinisher() {
		return ((fFinishPosition < HIGHEST_FINISH) && (fFinishPosition > 0));
	}

	public boolean isNoFinish() {
		return fFinishPosition == NOFINISH;
	}

	@Override public boolean equals(Object obj) {
		if (!(obj instanceof FinishPosition))
			return false;
		return fFinishPosition == ((FinishPosition) obj).fFinishPosition;
	}

	public long longValue() {
		return fFinishPosition;
	}

	public boolean isValidFinish() {
		return isValidFinish(fFinishPosition);
	}

	// ==================
	// Static methods
	// ==================

	public static boolean isValidFinish(long order) {
		return (order <= HIGHEST_FINISH);
	}

	@Override public String toString() {
		return toString(fFinishPosition, true);
	}

	public static String toString(long order) {
		return toString(order, true);
	}

	public static String toString(long order, boolean doShort) {
		if (doShort) {
			if (order == NOFINISH)
				return res.getString("PenaltyNoFin");
			else if (order == DNC)
				return res.getString("PenaltyDNC");
			else if (order == DNS)
				return res.getString("PenaltyDNS");
			else if (order == DNF)
				return res.getString("PenaltyDNF");
			else if (order == TLE)
				return res.getString("PenaltyTLE");
			// else if (order == WTH) return "WTH";
			else
				return Long.toString(order);
		} else {
			if (order == NOFINISH)
				return res.getString("PenaltyNoFinLongName");
			else if (order == DNC)
				return res.getString("PenaltyDNCLongName");
			else if (order == DNS)
				return res.getString("PenaltyDNSLongName");
			else if (order == DNF)
				return res.getString("PenaltyDNFLongName");
			// else if (order == WTH) return "Withdrew before Finishing";
			else if (order == TLE)
				return res.getString("PenaltyTLELongName");
			else
				return Long.toString(order);
		}
	}
}
/**
 * $Log: FinishPosition.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.7.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.7.2.1 2005/06/26 22:47:17 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.7 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.6 2003/04/20 15:43:58 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.5 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.4 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */
