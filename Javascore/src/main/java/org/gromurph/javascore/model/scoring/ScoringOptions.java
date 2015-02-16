// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPoint.java,v 1.12 2006/07/09 03:01:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.BaseObject;
import org.gromurph.xml.PersistentNode;

public class ScoringOptions extends BaseObject implements Constants {
	protected static ResourceBundle res = JavaScoreProperties.getResources();

 	/**
	 * option per RRS2013 A9 for different penalties for "long" series If true, the penalties as per A9 will be applied
	 */
	private boolean fIsLongSeries = false;

	/**
	 * throwouts, this vector is minimum number of races for i'th throwout.
	 */
	private List<Integer> fThrowouts = new ArrayList<Integer>();
	private int fThrowoutScheme = THROWOUT_BYNUMRACES;
	private int fThrowoutPerX = 0;
	private int fThrowoutBestX = 0;

	/**
	 * Default percentage penalty for failure to check-in
	 */
	private int fCheckinPercent = 20;

	/**
	 * When true the number of entries for calculating penalties of qualifying divisions will be based on the size of
	 * the largest division
	 */
	private boolean fEntriesLargestDivision = false;

	private int fTiebreaker = TIE_RRS_DEFAULT;
	
	private boolean fUserCanChangeTiebreaker = true;
	
	private double fFirstPlacePoints = 1.0;

	/**
	 * Array of supported Finish Time Limit penalties
	 */
	private int fTimeLimitPenalty = TLE_DNF;

	public ScoringOptions() {
		super();
		fThrowouts.add(new Integer(2));
		fThrowouts.add(new Integer(0));
		fThrowouts.add(new Integer(0));
	}

	public void setAttributes(ScoringOptions options) {
		try {
			ScoringOptions that = (ScoringOptions) options;

			this.fCheckinPercent = that.fCheckinPercent;
			this.fEntriesLargestDivision = that.fEntriesLargestDivision;
			this.fFirstPlacePoints = that.fFirstPlacePoints;
			this.fIsLongSeries = that.fIsLongSeries;
			this.fTimeLimitPenalty = that.fTimeLimitPenalty;

			this.fThrowoutScheme = that.fThrowoutScheme;
			this.fThrowoutBestX = that.fThrowoutBestX;
			this.fThrowoutPerX = that.fThrowoutPerX;

			this.fThrowouts.clear();
			this.fThrowouts.addAll(that.fThrowouts);
			
			this.fTiebreaker = that.fTiebreaker;
			this.fUserCanChangeTiebreaker = that.fUserCanChangeTiebreaker;
		} catch (java.lang.ClassCastException e) {}
	}

	public static final String TIEBREAKER_PROPERTY = "tiebreaker";
	public static final String TIMELIMITPENALTY_PROPERTY = "TimeLimitPenalty";
	public static final String CHECKINPERCENT_PROPERTY = "CheckinPercent";
	public static final String THROWOUT_PROPERTY = "Throwout";
	public static final String LONGSERIES_PROPERTY = "LongSeries";
	public static final String THROWOUT_SCHEME_PROPERTY = "ThrowoutScheme";
	public static final String THROWOUTBESTX_PROPERTY = "ThrowoutBestX";
	public static final String THROWOUTPERX_PROPERTY = "ThrowoutPerX";
	public static final String ENTRIESLARGESTDIVISION_PROPERTY = "EntriesLargestDivision";
	public static final String FIRSTPLACEPOINTS_PROPERTY = "FirstPlacePoints";

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		String value = n.getAttribute(TIMELIMITPENALTY_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setTimeLimitPenalty(id);
		}

		value = n.getAttribute(THROWOUT_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value.substring(value.length() - 1, value.length()));
			setThrowout(id - 1, Integer.parseInt(value));
		}

		value = n.getAttribute(THROWOUT_SCHEME_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setThrowoutScheme(id);
		} else {
			setThrowoutScheme(THROWOUT_BYNUMRACES);
		}

		value = n.getAttribute(THROWOUTBESTX_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setThrowoutBestX(id);
		}

		value = n.getAttribute(THROWOUTPERX_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setThrowoutPerX(id);
		}

		setTiebreaker( TIE_RRS_DEFAULT);
		value = n.getAttribute(TIEBREAKER_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setTiebreaker(id);
		}

		value = n.getAttribute(CHECKINPERCENT_PROPERTY);
		if (value != null) {
			int id = Integer.parseInt(value);
			setCheckinPercent(id);
		}

		value = n.getAttribute(LONGSERIES_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setLongSeries(b);
			} catch (Exception e) {}
		}

		value = n.getAttribute(ENTRIESLARGESTDIVISION_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setEntriesLargestDivision(b);
			} catch (Exception e) {}
		}

		fThrowouts.clear();
		int i = 0;
		do {
			value = n.getAttribute(THROWOUT_PROPERTY + (i + 1));
			if (value != null) {
				int id = Integer.parseInt(value);
				setThrowout(i, id);
			}
			i++;
		} while (value != null || i < 3);
		
		value = n.getAttribute(FIRSTPLACEPOINTS_PROPERTY);
		if (value != null) {
			double id = Double.parseDouble(value);
			setFirstPlacePoints(id);
		}


	}

	@Override public void xmlWrite(PersistentNode e) {

		e.setAttribute(TIMELIMITPENALTY_PROPERTY, Integer.toString(fTimeLimitPenalty));
		e.setAttribute(ENTRIESLARGESTDIVISION_PROPERTY, new Boolean(fEntriesLargestDivision).toString());
		e.setAttribute(LONGSERIES_PROPERTY, new Boolean(fIsLongSeries).toString());
		e.setAttribute(CHECKINPERCENT_PROPERTY, Integer.toString(fCheckinPercent));
		e.setAttribute(THROWOUT_SCHEME_PROPERTY, Integer.toString(fThrowoutScheme));
		e.setAttribute(THROWOUTPERX_PROPERTY, Integer.toString(fThrowoutPerX));
		e.setAttribute(THROWOUTBESTX_PROPERTY, Integer.toString(fThrowoutBestX));
		e.setAttribute(TIEBREAKER_PROPERTY, Integer.toString(fTiebreaker));
		e.setAttribute(FIRSTPLACEPOINTS_PROPERTY, Double.toString(fFirstPlacePoints));
		for (int n = 0; n < fThrowouts.size(); n++) {
			e.setAttribute(THROWOUT_PROPERTY + (n + 1), fThrowouts.get(n).toString());
		}
	}

	public void setLongSeries(boolean b) {
		fIsLongSeries = b;
	}

	public boolean isLongSeries() {
		return fIsLongSeries;
	}

	public boolean isEntriesLargestDivision() {
		return fEntriesLargestDivision;
	}

	public void setEntriesLargestDivision(boolean tf) {
		fEntriesLargestDivision = tf;
	}

	public List<Integer> getThrowouts() {
		return fThrowouts;
	}

	public void setThrowout(int nthrow, int min) {
		if (fThrowouts.size() <= nthrow) {
			for (int i = fThrowouts.size(); i <= nthrow; i++) {
				fThrowouts.add(i, new Integer(0));
			}
		}
		fThrowouts.set(nthrow, new Integer(min));
		firePropertyChange(THROWOUT_PROPERTY, null, fThrowouts);
	}

	public void setThrowouts(List<Integer> t) {
		List old = fThrowouts;
		fThrowouts = t;
		firePropertyChange(THROWOUT_PROPERTY, old, fThrowouts);
	}

	public void setThrowoutScheme(int scheme) {
		Integer old = new Integer(fThrowoutScheme);
		fThrowoutScheme = scheme;
		firePropertyChange(THROWOUT_SCHEME_PROPERTY, old, new Integer(fThrowoutScheme));
	}

	public int getThrowoutScheme() {
		return fThrowoutScheme;
	}

	public void setThrowoutPerX(int x) {
		Integer old = new Integer(fThrowoutPerX);
		fThrowoutPerX = x;
		firePropertyChange(THROWOUTPERX_PROPERTY, old, new Integer(fThrowoutPerX));
	}

	public int getThrowoutPerX() {
		return fThrowoutPerX;
	}

	public void setThrowoutBestX(int x) {
		Integer old = new Integer(fThrowoutBestX);
		fThrowoutBestX = x;
		firePropertyChange(THROWOUTBESTX_PROPERTY, old, new Integer(fThrowoutBestX));
	}

	public int getThrowoutBestX() {
		return fThrowoutBestX;
	}

	public int getTimeLimitPenalty() {
		return fTimeLimitPenalty;
	}

	public void setTimeLimitPenalty(int p) {
		Integer old = new Integer(fTimeLimitPenalty);
		fTimeLimitPenalty = p;
		firePropertyChange(TIMELIMITPENALTY_PROPERTY, old, new Integer(fTimeLimitPenalty));
	}

	public int getCheckinPercent() {
		return fCheckinPercent;
	}

	public void setCheckinPercent(int p) {
		Integer old = new Integer(fCheckinPercent);
		fCheckinPercent = p;
		firePropertyChange(CHECKINPERCENT_PROPERTY, old, new Integer(fCheckinPercent));
	}

	public double getFirstPlacePoints() {
		return fFirstPlacePoints;
	}

	public void setFirstPlacePoints(double p) {
		Double old = new Double(fFirstPlacePoints);
		fFirstPlacePoints = p;
		firePropertyChange(FIRSTPLACEPOINTS_PROPERTY, old, new Double(fFirstPlacePoints));
	}

	/**
	 * trivial implementation, doesnt really sort at all
	 * 
	 * @param obj
	 * @return int
	 **/
	public int compareTo(Object obj) {
		return this.toString().compareTo(obj.toString());
	}

	/**
	 * compares two lowpoint systems for equality of their optional settings
	 * 
	 * @param obj
	 * @return int
	 */
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof ScoringOptions))
			return false;
		if (this == obj)
			return true;

		ScoringOptions that = (ScoringOptions) obj;
		if (this.fCheckinPercent != that.fCheckinPercent) return false;
		if (this.fTimeLimitPenalty != that.fTimeLimitPenalty) return false;
		if (this.fEntriesLargestDivision != that.fEntriesLargestDivision) return false;
		if (this.fFirstPlacePoints != that.fFirstPlacePoints) return false;
		if (this.fIsLongSeries != that.fIsLongSeries) return false;
		if (this.fThrowoutBestX != that.fThrowoutBestX) return false;
		if (this.fThrowoutPerX != that.fThrowoutPerX) return false;
		if (this.fThrowoutScheme != that.fThrowoutScheme) return false;
		if (this.fTiebreaker != that.fTiebreaker) return false;
		if (this.fUserCanChangeTiebreaker != that.fUserCanChangeTiebreaker) return false;

		return this.fThrowouts.equals(that.fThrowouts);
	}

	public int getTiebreaker() {
		return fTiebreaker;
	}

	public void setTiebreaker(int tiebreaker) {
		fTiebreaker = tiebreaker;
	}

	protected void setUserCanChangeTiebreaker(boolean tf) {
		fUserCanChangeTiebreaker = tf;
	}

	public boolean canUserChangeTiebreaker() {
		return fUserCanChangeTiebreaker;
	}
}

