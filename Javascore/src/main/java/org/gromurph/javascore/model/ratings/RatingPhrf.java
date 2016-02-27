// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingPhrf.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.WarningList;
import java.text.MessageFormat;

/**
 * Standard Phrf handicaping - and the superclass for all one value ratings
 **/
public class RatingPhrf extends RatingDouble {
	public static final String SYSTEM = "PHRF";
	public static final int FASTEST_RATING = -9999;
	public static final int SLOWEST_RATING = 9999;

	public RatingPhrf() {
		super(SYSTEM, FASTEST_RATING);
	}

	public RatingPhrf(int inV) {
		super(SYSTEM, inV);
	}

	public RatingPhrf(String system, int inV) {
		super(system, inV);
	}

	@Override
	public int getDecs() {
		return 0;
	}

	@Override
	public boolean isSlower(Rating that) {
		if (!(that instanceof RatingPhrf)) return false;
		return (getPrimaryValue() > ((RatingPhrf) that).getPrimaryValue());
	}

	@Override
	public boolean isFaster(Rating that) {
		if (!(that instanceof RatingPhrf)) return false;
		return (getPrimaryValue() < ((RatingPhrf) that).getPrimaryValue());
	}

	/**
	 * Creates a new instance of the maximum/fastest overall rating allowed by the rating system
	 * 
	 * @return
	 */
	@Override
	public Rating createFastestRating() {
		Rating r = null;
		try {
			r = getClass().newInstance();
			r.setPrimaryValue(FASTEST_RATING);
		} catch (InstantiationException e) {} catch (IllegalAccessException e) {}
		return r;
	}

	/**
	 * Creates a new instance of the minimum/slowest overall rating allowed by the rating system
	 * 
	 * @return
	 */
	@Override
	public Rating createSlowestRating() {
		Rating r = null;
		try {
			r = getClass().newInstance();
			r.setPrimaryValue(SLOWEST_RATING);
		} catch (InstantiationException e) {} catch (IllegalAccessException e) {}
		return r;
	}

	@Override
	public long getTimeAllowance(Finish inFinish) {
		Division div = inFinish.getEntry().getDivision();
		double length = inFinish.getRace().getLength(div);

		// want time, no longer (effective RRS 2005)
		double secs = getPrimaryValue() * length;
		return (long) (secs * 1000);
	}

	/**
	 * calculates corrected time
	 * 
	 * @param inFinish
	 * @return corrected time
	 */
	@Override
	public long getCorrectedTime(Finish inFinish) {
		long elapsedMillis = inFinish.getElapsedTime();
		if (elapsedMillis == SailTime.NOTIME) elapsedMillis = 0;

		Division div = inFinish.getEntry().getDivision();
		double length = inFinish.getRace().getLength(div);
		if (length <= 0) return (elapsedMillis);

		//        double secs = ( elapsed - (getPrimaryValue() * length * 1000)) / 1000;
		//        return Math.round(secs) * 1000;
		long ta = getTimeAllowance(inFinish);
		long corrected = elapsedMillis - ta;
		return corrected;
	}

	@Override
	public void validateRace(Race race, Division div, WarningList warnings) {
		if (!race.getDivInfo().isRacing(div)) return;

		long starttime = race.getStartTimeRaw(div);
		if (starttime == SailTime.NOTIME) {
			warnings.add(MessageFormat.format(res.getString("WarningRaceNeedsStartTime"),
					new Object[] { race.toString(), div.toString() }));
		}

		double length = race.getLength(div);
		if (length <= 0) {
			warnings.add(MessageFormat.format(res.getString("WarningRaceNeedsLength"),
					new Object[] { race.toString(), div.toString() }));
		}
		validateFinishTimesAfterStartTimes(race, div, warnings);
	}

}
/**
 * $Log: RatingPhrf.java,v $ Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 *
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 *
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 *
 * Revision 1.10.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 *
 * Revision 1.10 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.9 2004/04/11 20:41:54 sandyg Bug 773217 PHRF ratings now show time allowance with out finishes
 *
 * Revision 1.8 2004/04/10 20:49:29 sandyg Copyright year update
 *
 * Revision 1.7 2003/01/05 21:29:29 sandyg fixed bad version/id string
 *
 * Revision 1.6 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 *
 */
