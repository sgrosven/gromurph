//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Penalty.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
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
import org.gromurph.javascore.SailTime;
import org.gromurph.util.BaseObject;
import org.gromurph.xml.PersistentNode;

/**
 * Class for storing penalty settings. NOTE this class is responsible only for
 * specifying the penalty assignments: NOT for determining the points to be
 * assigned. See @ScoringSystems for changing penalties into points.
 * <P>
 * There are three sets of penalties supported in this class: <br>
 * - NonFinishPenalties: penalties that can be assigned to boats that have not
 * finished a race. Examples include DNC, DNS <br>
 * - Disqualification Penalties: penalties that override other penalties and
 * involve some variant of causing a boat's finish to be ignored. Examples
 * include, DSQ, OCS <br>
 * - ScoringPenalties: penalties that may accumulate as various "hits" on a
 * boat's score. Examples include SCP, ZFP
 * <p>
 * Although it is unusual a boat may have more than one penalty applied. For
 * example a boat may get a Z Flag penalty and a 20 Percent penalty. Or a boat
 * may miss a finish time window and still be scored with a 20 Percent penalty.
 * <p>
 * In general, a boat can have a Non-Finish penalty AND any other penalty
 * applied And the scoring penalties can accumulate. But the disqualification
 * penalties do not accumulate and will override other penalties assigned
 */
public class Penalty extends BaseObject implements Constants {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private static final long serialVersionUID = 1L;

	public final static String PENALTY_PROPERTY = "Penalty";
	public final static String PERCENT_PROPERTY = "Percent";
	public final static String POINTS_PROPERTY = "Points";
	public final static String NOTE_PROPERTY = "Note";
	public final static String TIMEPENALTYELAPSEDOLD_PROPERTY = "Time";
	public final static String TIMEPENALTYELAPSED_PROPERTY = "ElapsedTime";
	public final static String TIMEPENALTYCORRECTED_PROPERTY = "CorrectedTime";
	public final static String REDRESSLABEL_PROPERTY = "RedressLabel";

	public static Penalty[] getAllNonFinishPenalties() {
		return new Penalty[] { new Penalty(DNC), new Penalty(DNS), new Penalty(DNF), new Penalty(TLE) };
		// new Penalty( NOFINISH) };
	}

	/**
	 * contains the percentage assigned if a SCP penalty is set
	 */
	private int fPercent;

	/**
	 * contains the Points to be awarded for RDG and MAN penalties
	 */
	private double fPoints;

	/**
	 * contains the amount (if any) of a elapsed time penalty
	 */
	private long fTimePenaltyElapsed;
	private long fTimePenaltyCorrected;

	/**
	 * contains the penalties assigned. This is a "bit-wise" field, each bit
	 * represents a different penalty
	 */
	private long fPenalty;

	/**
	 * contains a user-entered note to be saved in accordance with this penalty
	 */
	private String fNote;

	private String fRedressLabel;

	/**
	 * default constructor, creates and empty penalty
	 */
	public Penalty() {
		this(NO_PENALTY);
	}

	/**
	 * default constructor, creates and empty penalty
	 */
	public Penalty(long pen) {
		super();
		fPenalty = pen;
		fPercent = 0;
		fPoints = 0;
		fTimePenaltyElapsed = 0;
		fTimePenaltyCorrected = 0;
		fNote = "";
		fRedressLabel = "";
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fPenalty = NO_PENALTY;
		fPercent = 0;
		fPoints = 0;
		fTimePenaltyElapsed = 0;
		fTimePenaltyCorrected = 0;
		fNote = "";
		fRedressLabel = "";

		String value = n.getAttribute(PENALTY_PROPERTY);

		if (value != null) {
			try {
				setPenalty(parsePenalty(value).getPenalty());
			} catch (Exception e) {
			}
		}

		value = n.getAttribute(PERCENT_PROPERTY);
		if (value != null) {
			setPercent(Integer.parseInt(value));
		}

		value = n.getAttribute(POINTS_PROPERTY);
		if (value != null) {
			setPoints(Double.parseDouble(value));
		}

		value = n.getAttribute(NOTE_PROPERTY);
		if (value != null) {
			setNote(value);
		}

		value = n.getAttribute(TIMEPENALTYELAPSEDOLD_PROPERTY);
		if (value != null) {
			setTimePenaltyElapsed(SailTime.forceToLong(value));
		}

		value = n.getAttribute(TIMEPENALTYELAPSED_PROPERTY);
		if (value != null) {
			setTimePenaltyElapsed(SailTime.forceToLong(value));
		}

		value = n.getAttribute(TIMEPENALTYCORRECTED_PROPERTY);
		if (value != null) {
			setTimePenaltyCorrected(SailTime.forceToLong(value));
		}

		value = n.getAttribute(REDRESSLABEL_PROPERTY);
		if (value != null) {
			setRedressLabel(value);
		}

	}

	@Override public void xmlWrite(PersistentNode e) {
		if (fPercent != 0)
			e.setAttribute(PERCENT_PROPERTY, Long.toString(fPercent));
		if (fPoints != 0)
			e.setAttribute(POINTS_PROPERTY, Double.toString(fPoints));
		if (fNote.length() > 0)
			e.setAttribute(NOTE_PROPERTY, fNote);
		if (fTimePenaltyElapsed != 0)
			e.setAttribute(TIMEPENALTYELAPSED_PROPERTY, SailTime.toString(fTimePenaltyElapsed));
		if (fTimePenaltyCorrected != 0)
			e.setAttribute(TIMEPENALTYCORRECTED_PROPERTY, SailTime.toString(fTimePenaltyCorrected));

		if (this.hasPenalty(AVG)) {
			e.setAttribute(PENALTY_PROPERTY, "AVG"); // override for XML
			// purposes
			if (fRedressLabel.length() > 0)
				e.setAttribute(REDRESSLABEL_PROPERTY, fRedressLabel);
		} else if (this.hasPenalty(RDG)) {
			e.setAttribute(PENALTY_PROPERTY, toString(false));
			if (fRedressLabel.length() > 0)
				e.setAttribute(REDRESSLABEL_PROPERTY, fRedressLabel);
		} else {
			e.setAttribute(PENALTY_PROPERTY, toString(false));
		}
		// return e;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		try {
			Penalty that = (Penalty) obj;
			if (this.fPenalty != that.fPenalty)
				return false;
			if (this.fPercent != that.fPercent)
				return false;
			if (this.fPoints != that.fPoints)
				return false;
			if (this.fTimePenaltyElapsed != that.fTimePenaltyElapsed)
				return false;
			if (this.fTimePenaltyCorrected != that.fTimePenaltyCorrected)
				return false;
			if ((fNote == null) ? (that.fNote != null) : !(fNote.equals(that.fNote)))
				return false;
			if ((fRedressLabel == null) ? (that.fRedressLabel != null) : !(fRedressLabel.equals(that.fRedressLabel)))
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int compareTo(Object obj) {
		if (!(obj instanceof Penalty))
			return -1;
		if (this.equals(obj))
			return 0;

		Penalty that = (Penalty) obj;

		// so far all penalties are equal
		if (that.fPenalty > this.fPenalty)
			return -1;
		else if (that.fPenalty < this.fPenalty)
			return 1;
		else if (that.fPercent > this.fPercent)
			return -1;
		else if (that.fPercent < this.fPercent)
			return 1;
		else if (that.fPoints > this.fPoints)
			return -1;
		else if (that.fPoints < this.fPoints)
			return 1;
		else if (that.fTimePenaltyElapsed > this.fTimePenaltyElapsed)
			return -1;
		else if (that.fTimePenaltyCorrected < this.fTimePenaltyCorrected)
			return 1;
		else {
			int n = this.fNote.compareTo(that.fNote);
			if (n != 0)
				return n;

			return this.fRedressLabel.compareTo(that.fRedressLabel);
		}
	}

	/**
	 * Adds the specified penalty to the set of other penalties applied All
	 * other penalties remain
	 */
	public long addOtherPenalty(long newPen) {
		Long oldP = new Long(fPenalty);
		// newPen = newPen & OTHER_MASK; // mask out stray bits out of Other
		// area
		fPenalty = fPenalty | newPen;
		firePropertyChange(PENALTY_PROPERTY, oldP, new Long(fPenalty));
		return fPenalty;
	}

	/**
	 * replaces the finish penalty leaving others alone
	 */
	public void setFinishPenalty(long newPen) {
		Long oldP = new Long(fPenalty);
		fPenalty = fPenalty & (NOFINISH_MASK ^ 0xFFFF); // clear previous finish
		// bits
		fPenalty = fPenalty | (newPen & NOFINISH_MASK); // add in to finish
		// penalty bits
		firePropertyChange(PENALTY_PROPERTY, oldP, new Long(fPenalty));
	}

	/**
	 * returns the finish penalty only, masking out other penalites (if any)
	 */
	public long getFinishPenalty() {
		return (fPenalty & NOFINISH_MASK);
	}

	/**
	 * replaces the disqualification penalty leaving others alone
	 */
	public void setDsqPenalty(long newPen) {
		Long oldP = new Long(fPenalty);
		fPenalty = fPenalty & (DSQ_MASK ^ 0xFFFFFFFF); // clear previous finish
		// bits
		fPenalty = fPenalty | (newPen & DSQ_MASK); // add in to finish penalty
		// bits
		firePropertyChange(PENALTY_PROPERTY, oldP, new Long(fPenalty));
	}

	/**
	 * Clears the specified penalty in the set of penalties applied
	 */
	public long clearPenalty(long newPen) {
		Long oldP = new Long(fPenalty);
		long notPen = 0xFFFFFFFF ^ newPen;
		fPenalty = (fPenalty & notPen);
		firePropertyChange(PENALTY_PROPERTY, oldP, new Long(fPenalty));
		return fPenalty;
	}

	/**
	 * Replaces the current penalty settings with the specified penalty resets
	 * percentage and manual points to 0
	 */
	public void setPenalty(long newPen) {
		Long oldP = new Long(fPenalty);
		fPenalty = newPen;
		fPercent = 0;
		fPoints = 0;
		firePropertyChange(PENALTY_PROPERTY, oldP, new Long(fPenalty));
	}

	/**
	 * Replaces the current percentage penalty amount with the specified amount
	 * leaves other penalty settings alone, does NOT light the SCP penalty flag
	 */
	public void setPercent(int newPen) {
		Long oldP = new Long(fPercent);
		// addOtherPenalty( SCP);
		fPercent = newPen;
		firePropertyChange(PERCENT_PROPERTY, oldP, new Long(fPercent));
	}

	/**
	 * Replaces the current manual points with the specified points, does NOT
	 * light the MAN or RDG flag
	 */
	public void setPoints(double newPen) {
		Double oldP = new Double(fPoints);
		fPoints = newPen;
		firePropertyChange(POINTS_PROPERTY, oldP, new Double(fPoints));
	}

	/**
	 * Sets the amount of time for an elapsed time penalty or redress
	 */
	public void setTimePenaltyElapsed(long time) {
		Long oldTime = new Long(fTimePenaltyElapsed);
		fTimePenaltyElapsed = time;
		firePropertyChange(TIMEPENALTYELAPSED_PROPERTY, oldTime, new Long(fTimePenaltyElapsed));
	}

	/**
	 * Sets the amount of time for an corrected time penalty or redress
	 */
	public void setTimePenaltyCorrected(long time) {
		Long oldTime = new Long(fTimePenaltyCorrected);
		fTimePenaltyCorrected = time;
		firePropertyChange(TIMEPENALTYELAPSED_PROPERTY, oldTime, new Long(fTimePenaltyCorrected));
	}

	/**
	 * Replaces the current manual points with the specified points, resets
	 * percentage and other penalties to zero
	 */
	public void setNote(String n) {
		String hold = fNote;
		fNote = n;
		firePropertyChange(NOTE_PROPERTY, hold, fNote);
	}

	public boolean hasPenalty(long inPen) {
		// presumes that inPen is a "simple" penalty, no a combination penalty,
		// fPenalty might well be a combo
		if (isOtherPenalty(inPen)) {
			long a = fPenalty;
			a = a & inPen;
			a = a & OTHER_MASK;
			return (fPenalty & inPen & OTHER_MASK) != 0;
			// AND of the two in the other range should return good
		} else if (isDsqPenalty(inPen)) {
			return (fPenalty & DSQ_MASK) == (inPen & DSQ_MASK);
		} else if (isFinishPenalty(inPen)) {
			return (fPenalty & NOFINISH_MASK) == (inPen & NOFINISH_MASK);
		}
		return (inPen == fPenalty);
	}

	public void clear() {
		fPoints = 0;
		fPercent = 0;
		setPenalty(NO_PENALTY);
	}

	public long getPenalty() {
		return fPenalty;
	}

	public int getPercent() {
		return fPercent;
	}

	public double getPoints() {
		return fPoints;
	}

	public long getTimePenaltyElapsed() {
		return fTimePenaltyElapsed;
	}

	public long getTimePenaltyCorrected() {
		return fTimePenaltyCorrected;
	}

	public String getNote() {
		return fNote;
	}

	public void setRedressLabel(String label) {
		fRedressLabel = label;
	}

	public String getRedressLabel() {
		return fRedressLabel;
	}

	// ==================
			// Static methods
	// ==================

	public boolean isFinishPenalty() {
		return isFinishPenalty(fPenalty);
	}

	public boolean isDsqPenalty() {
		return isDsqPenalty(fPenalty);
	}

	public boolean isOtherPenalty() {
		return isOtherPenalty(fPenalty);
	}

	public static boolean isFinishPenalty(long pen) {
		return ((pen & NOFINISH_MASK) != 0);
	}

	public static boolean isDsqPenalty(long pen) {
		return ((pen & DSQ_MASK) != 0);
	}

	public static boolean isOtherPenalty(long pen) {
		return ((pen & OTHER_MASK) != 0);
	}

	@Override public String toString() {
		return toString(this, true);
	}

	public String toString(boolean showPts) {
		return toString(this, showPts);
	}

	public static String toString(long pen) {
		return toString(pen, true);
	}

	public static String toString(Penalty inP) {
		return toString(inP, true);
	}

	public static String toString(long pen, boolean doShort) {
		return toString(new Penalty(pen), doShort);
	}

	public static String toString(Penalty inP, boolean showPts) {
		long pen = inP.getPenalty();
		if (pen == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		if ((inP.getPenalty() & NOFINISH_MASK) != 0) {
			sb.append(FinishPosition.toString(pen & NOFINISH_MASK));
			sb.append(",");
		}

		pen = (inP.getPenalty() & Constants.DSQ_MASK);
		if (pen == DSQ)
			sb.append("DSQ,");
		if (pen == DGM)
			sb.append("DGM,");
		if (pen == DNE)
			sb.append("DNE,");
		if (pen == RET)
			sb.append("RET,");
		if (pen == OCS)
			sb.append("OCS,");
		if (pen == BFD)
			sb.append("BFD,");
		if (pen == UFD)
			sb.append("UFD,");

		pen = (inP.getPenalty() & Constants.OTHER_MASK);
		if ((pen & CNF) != 0)
			sb.append("CNF,");
		if ((pen & ZFP) != 0)
			sb.append("ZFP,");
		if ((pen & ZFP2) != 0)
			sb.append("ZFP2,");
		if ((pen & ZFP3) != 0)
			sb.append("ZFP3,");
		if (pen == DPI)
			sb.append("DPI,");

		if ((pen & SCP) != 0) {
			sb.append(Long.toString(inP.getPercent()));
			sb.append("%,");
		}
		if ((pen & RDG) != 0) {
			String label = "RDG";
			String rlab = inP.getRedressLabel();
			if (rlab != null && rlab.length() > 0) label = rlab; // += ":" + rlab;
			sb.append(label);
			if (showPts) {
				sb.append("/");
				sb.append(Double.toString(inP.getPoints()));
			}
			sb.append(",");
		}
		if ((pen & AVG) != 0) {
			String label = "AVG";
			String rlab = inP.getRedressLabel();
			if (rlab != null && rlab.length() > 0) label = rlab; // += ":" + rlab;
			sb.append(label);
			sb.append(",");
		}

		if ((pen & TME) != 0) {
			sb.append("TME");
			if (showPts) {
				sb.append("/");
				sb.append(SailTime.toString(inP.getTimePenaltyElapsed()));
			}
			sb.append(",");
		}

		if ((pen & TMC) != 0) {
			sb.append("TMC");
			if (showPts) {
				sb.append("/");
				sb.append(SailTime.toString(inP.getTimePenaltyCorrected()));
			}
			sb.append(",");
		}

		if ((pen & TMP) != 0) {
			sb.append("TMP");
			if (showPts) {
				sb.append("/");
				sb.append(inP.getPercent());
				sb.append("%");
			}
			sb.append(",");
		}

		String r = sb.toString();
		if ((r.length() > 0) && (r.substring(r.length() - 1).equals(","))) {
			r = r.substring(0, r.length() - 1);
		}
		return r;
	}

	public static void parsePenalty(Penalty pen, String penString) throws IllegalArgumentException {
		Penalty newpen = parsePenalty(penString);
		pen.fPoints = newpen.fPoints;
		pen.fPenalty = newpen.fPenalty;
		pen.fPercent = newpen.fPercent;
		pen.fTimePenaltyElapsed = newpen.fTimePenaltyElapsed;
		pen.fTimePenaltyCorrected = newpen.fTimePenaltyCorrected;
		pen.fRedressLabel = newpen.fRedressLabel;
	}

	public static Penalty parsePenalty(String origPen) throws IllegalArgumentException {
		String pen = origPen.toUpperCase();

		if (pen.length() == 0)
			return new Penalty(NO_PENALTY);
		if (pen.indexOf(",") >= 0) {
			// have comma(s) recurse thru each comma adding the penalties
			int leftc = 0;
			Penalty newpen = new Penalty();

			while (leftc <= pen.length()) {
				int rightc = pen.indexOf(",", leftc);
				if (rightc < 0)
					rightc = pen.length();
				String sub = pen.substring(leftc, rightc);
				Penalty addpen = parsePenalty(sub);
				if (addpen.isOtherPenalty()) {
					newpen.addOtherPenalty(addpen.getPenalty());
					newpen.setRedressLabel(addpen.getRedressLabel());

					if (addpen.hasPenalty(RDG)) {
						newpen.setPoints(addpen.getPoints());
					}
					if (addpen.hasPenalty(TMP) || addpen.hasPenalty(SCP)) {
						newpen.setPercent(addpen.getPercent());
					}
					if (addpen.hasPenalty(TME)) {
						newpen.setTimePenaltyElapsed(addpen.getTimePenaltyElapsed());
					}
					if (addpen.hasPenalty(TMC)) {
						newpen.setTimePenaltyCorrected(addpen.getTimePenaltyCorrected());
					}
				} else if (addpen.isDsqPenalty()) {
					newpen.setDsqPenalty(addpen.getPenalty());
				} else if (addpen.isFinishPenalty()) {
					newpen.setFinishPenalty(addpen.getPenalty());
				}
				leftc = rightc + 1;
			}
			return newpen;
		}

		// all of the rest should have <pen>/<number>
		// THIS CODE is 1.4 SPECIFIC
		// String[] divided = pen.split("/");
		// pen = divided[0];
		// String val = (divided.length > 1) ? divided[1] : "";

		int slash = pen.indexOf("/");
		String val = "";
		if (slash >= 0) {
			val = pen.substring(slash + 1);
			pen = pen.substring(0, slash);
		}

		if (pen.equals("DSQ"))
			return new Penalty(DSQ);

		if (pen.equals("DGM"))
			return new Penalty(DGM);

		if (pen.equals("DNE"))
			return new Penalty(DNE);
		if (pen.equals("DND"))
			return new Penalty(DNE);

		if (pen.equals("RAF"))
			return new Penalty(RET);
		if (pen.equals("RET"))
			return new Penalty(RET);

		if (pen.equals("OCS"))
			return new Penalty(OCS);
		if (pen.equals("PMS"))
			return new Penalty(OCS);

		if (pen.equals("UFD"))
			return new Penalty(UFD);
		if (pen.equals("BFD"))
			return new Penalty(BFD);
		if (pen.equals("CNF"))
			return new Penalty(CNF);

		if (pen.equals("ZPG"))
			return new Penalty(ZFP);
		if (pen.equals("ZFP"))
			return new Penalty(ZFP);
		if (pen.equals("ZFP2"))
			return new Penalty(ZFP2);
		if (pen.equals("ZFP3"))
			return new Penalty(ZFP3);
		if (pen.equals("ARP"))
			return new Penalty(SCP);

		if (pen.startsWith("AVG")) {
			Penalty p = new Penalty(AVG);
			parseRedressLabel( p, pen);
			return p;
		}

		if (pen.equals("DNC"))
			return new Penalty(DNC);
		if (pen.equals("DNS"))
			return new Penalty(DNS);

		if (pen.equals("DNF"))
			return new Penalty(DNF);
		if (pen.equals("WTH"))
			return new Penalty(DNF); // 2001

		if (pen.equals("TLE"))
			return new Penalty(TLE);
		if (pen.equals("TLM"))
			return new Penalty(TLE);

		if (pen.equals("TIM") || pen.equals("TME")) {
			Penalty penalty = new Penalty(TME);
			penalty.setTimePenaltyElapsed(SailTime.forceToLong(val));
			return penalty;
		}

		if (pen.equals("TMC")) {
			Penalty penalty = new Penalty(TMC);
			penalty.setTimePenaltyCorrected(SailTime.forceToLong(val));
			return penalty;
		}

		if (pen.equals("TMP") || pen.equals("SCP") || pen.equals("PCT")) {
			Penalty penalty = (pen.equals("TMP") ? new Penalty(TMP) : new Penalty(SCP));
			// assume is form "MAN/<pts>"
			try {
				int pct = Integer.parseInt(val);
				penalty.setPercent(pct);
			} catch (Exception e) {
			}
			return penalty;
		}

		if (pen.equals("RDG") || pen.equals("RDR") || pen.equals("MAN") ||
				pen.equals("DPI")) {
			Penalty penalty = new Penalty(RDG);
			parseRedressLabel( penalty, pen);

			// assume is form "MAN/<pts>"
			try {
				double pts = Double.parseDouble(val);
				penalty.setPoints(pts);
			} catch (Exception e) {
			}
			return penalty;
		}

		if (pen.endsWith("%")) {
			Penalty pctPen = new Penalty(SCP);
			try {
				int pct = Integer.parseInt(pen.substring(0, pen.length() - 1));
				pctPen.setPercent(pct);
				return pctPen;
			} catch (Exception dontcare) {
			}

		}

		if (pen.startsWith("P")) {
			Penalty pctPen = new Penalty(SCP);
			try {
				int pct = Integer.parseInt(pen.substring(1));
				pctPen.setPercent(pct);
				return pctPen;
			} catch (Exception dontcare) {
			}
		}

		// assume some discretionary manual score
		Penalty penalty = new Penalty(RDG);
		parseRedressLabel( penalty, pen);
		// assume is form "xxxx/<pts>"
		try {
			double pts = Double.parseDouble(val);
			penalty.setPoints(pts);
		} catch (Exception e) {
		}
		return penalty;
	}
	
	private static void parseRedressLabel( Penalty base, String penString) {
		int br = penString.indexOf( ":");
		if (br >= 0) {
    		String label = penString.substring( br);
    		base.setRedressLabel(label);
		} else {
    		br = penString.indexOf( "/");
    		if (br >= 0) {
        		String label = penString.substring( br);
        		base.setRedressLabel(label);
    		} else {
    			base.setRedressLabel(penString);
    		}
		}
	}
}
