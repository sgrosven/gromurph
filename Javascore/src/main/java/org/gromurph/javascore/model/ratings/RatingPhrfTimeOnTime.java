// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
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
 * PHRF Time on Time handicapping... see (as of 3 feb 2013) http://offshore.ussailing.org/PHRF/Time-On-Time_Scoring.htm
 * for description. This corrected time formula takes a wimpy approach to the A factor, simply using the Bfactor all the
 * time.
 **/
public class RatingPhrfTimeOnTime extends RatingPhrf {
	public final static int BFACTOR_HEAVY = 480;
	public final static int BFACTOR_AVERAGE = 550;
	public final static int BFACTOR_LIGHT = 600;
	public final static int BFACTOR_CUSTOM = -1;

	public final static int AFACTOR_DEFAULT = 650;

	public static final String SYSTEM = "PHRFTimeOnTime";

	public RatingPhrfTimeOnTime() {
		super(SYSTEM, RatingPhrf.FASTEST_RATING);
	}

	public RatingPhrfTimeOnTime(int inV) {
		super(SYSTEM, inV);
	}

	public RatingPhrfTimeOnTime(String system, int inV) {
		super(system, inV);
	}

	/**
	 * calculates corrected time
	 * 
	 * @param inFinish
	 * @return corrected time
	 */
	@Override
	public long getCorrectedTime(Finish inFinish) {
		long elapsed = inFinish.getElapsedTime();
		if (elapsed == SailTime.NOTIME) return SailTime.NOTIME;

		int bfactor = inFinish.getRace().getBFactor();
		int afactor = inFinish.getRace().getAFactor();

		if (bfactor < 0) bfactor = BFACTOR_AVERAGE;
		if (afactor < 0) afactor = bfactor;

		double corrected = elapsed * afactor / (bfactor + getPrimaryValue()); // in milliseconds
		return (long) corrected;
	}

	@Override
	public long getTimeAllowance(Finish inFinish) {
		return inFinish.getElapsedTime() - getCorrectedTime(inFinish);
	}

	@Override
	public void validateRace(Race race, Division div, WarningList warnings) {
		long starttime = race.getStartTimeRaw(div);
		if (starttime == SailTime.NOTIME) {
			warnings.add(MessageFormat.format(res.getString("WarningRaceNeedsStartTime"),
					new Object[] { race.toString(), div.toString() }));
		}
		if (race.isPursuit()) {
			warnings.add(MessageFormat.format(res.getString("PursuitNotSupportedYet"),
					new Object[] { race.toString(), div.toString() }));
		}
		validateFinishTimesAfterStartTimes(race, div, warnings);
		validateValidFinishTime(race, div, warnings);
	}

}
