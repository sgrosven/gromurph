// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointDnIceboat.java,v 1.6 2006/04/15 23:43:30 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.model.RacePointsList;

/**
 * Handles tiebreakders in accordance with International DN Iceboat regs and customs. Reference for this is website:<br>
 * http://www.eissegeln.de/idniyra/idniyra.htm<br>
 * Section B5:
 * <p>
 * "b. Scoring will be done by assigning points in the following manner: first place, 1 point; second place, 2 points;
 * third place, 3 points; fourth place, 4 points; fifth place, 5 points; etc. - lowest score to win. DNS, DSQ, and DNF
 * one worse than the number of yachts in the fleet after the mini-qualification races."
 * <p>
 */
public class ScoringLowPointDnIceboat extends ScoringLowPoint {
	public static final String NAME = "Intnl DN Iceboat LowPoint";

	@Override public String toString() {
		return res.getString("ScoringDnIceboat");
	}

	@Override public String getName() {
		return NAME;
	}

	/**
	 * need own constructor to override the default number of throwouts as per DN rules: "d. Throwouts: if 5 races are
	 * completed, the points for each yachts poorest race (including DNS, DNF and DSQ) will be eliminated from the
	 * scoring."
	 */
	public ScoringLowPointDnIceboat() {
		super();

		// change first throwout to kick in after five races
		fOptions.getThrowouts().set(1, new Integer(5));
		fOptions.setUserCanChangeTiebreaker(false);
	}

	@Override
	protected TiebreakCalculator getTiebreakCalculator() {
		return new ScoringTiebreakerDnIceboat();
	}

	/**
	 * Handles tiebreakders in accordance with International DN Iceboat regs and customs. Reference for this is website:<br>
	 * http://www.eissegeln.de/idniyra/idniyra.htm<br>
	 * Section B5:
	 * <p>
	 * "b. Scoring will be done by assigning points in the following manner: first place, 1 point; second place, 2
	 * points; third place, 3 points; fourth place, 4 points; fifth place, 5 points; etc. - lowest score to win. DNS,
	 * DSQ, and DNF one worse than the number of yachts in the fleet after the mini-qualification races."
	 * <p>
	 */
	public class ScoringTiebreakerDnIceboat extends TiebreakCalculator {

		@Override 
		protected int getTiebreakLevels() { return 1;}
		
		@Override
		protected int compareTiebreakerLevel( int level, RacePointsList inLeft, RacePointsList inRight) {
			switch (level) {
	    		case 1: return compareWhoBeatWhoMost( inLeft, inRight);
			}
			return 0;
		}
	}

}
