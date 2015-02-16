// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointSnipe.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
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
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPointsList;

/**
 * implements Snipe scoring for 2001-2004. The Snipe class corrected their own errant 2001-2004 scoring publication in
 * July of 2003. This variant of Snipe scoring was implement for release 4.0.2 in time for the 2003 Snipe Nationals.
 */
public class ScoringLowPointSnipe extends ScoringLowPoint {
	public static final String NAME = "Snipe LowPoint 2001-2004";

	public ScoringLowPointSnipe() {
		super();

		// note that panel supports three throwouts
		// note these are further overridden in getPenaltyPoints below
		fOptions.setTimeLimitPenalty(ScoringLowPoint.TLE_FINISHERSPLUS1); // finishers+1
																 // by
																 // default
		fOptions.setCheckinPercent(10);
		fOptions.setUserCanChangeTiebreaker(false);
		fOptions.setFirstPlacePoints(0.75);
	}

	@Override public String toString() {
		return res.getString("ScoringSnipe2001");
	}

	@Override public String getName() {
		return NAME;
	}

	/**
	 * Overrides the standard ISAF penalties
	 **/
	@Override protected double getPenaltyPointsWithoutManual(Penalty p, RacePointsList rpList, double basePts) {
		double nStarters = 0;
		double nFinishers = 0;
		double nRegistered = 0;

		if (rpList != null) {
			nStarters = rpList.getNumberStarters();
			nFinishers = rpList.getNumberFinishers();
			nRegistered = rpList.size();
		}

		// double returnPoints = super.getPenaltyPoints( p, rpList, basePts);
		double returnPoints = p.getPoints();

		if (p.hasPenalty(DSQ) || p.hasPenalty(DGM) || p.hasPenalty(DNE) || p.hasPenalty(BFD) || p.hasPenalty(OCS)) {
			return roundUp(nStarters * 1.10); // by snipe class rules... round
											  // UP
		} else if (p.hasPenalty(DNF) || p.hasPenalty(TLE)) {
			return nFinishers + 1;
		} else if (p.hasPenalty(RET)) {
			return nStarters;
		} else if (p.hasPenalty(CNF)) {
			return roundUp(basePts + nStarters * 0.10); // by snipe class rules... round UP
		} else if (p.hasPenalty(DNC)) {
			return nRegistered;
		} else if (p.hasPenalty(ZFP)) {
			return roundUp(basePts + nRegistered * .20); // by snipe class rules... round UP
		} else if (p.hasPenalty(ZFP2)) {
			return roundUp(basePts + nRegistered * .20); // by snipe class rules... round UP
		} else if (p.hasPenalty(ZFP3)) {
			return roundUp(basePts + nRegistered * .20); // by snipe class rules... round UP
		}

		return returnPoints;
	}

	private double roundUp(double pts) {
		return Math.ceil(pts);
	}

	/**
	 * resolve ties among a group of tied boats. A tie that is breakable should have .01 point increments added as
	 * appropriate. Assume that each individual race and series points have calculated, and that throwouts have already
	 * been designated in the points objects.
	 * <P>
	 * 
	 * @param races
	 *            races involved
	 * @param entriesIn
	 *            list of tied entries
	 * @param racePointsList
	 *            list of points for all races and entries (and maybe more!)
	 * @param seriesPointsList
	 *            map containing series points for the entries, prior to handling ties (and maybe more than just those
	 *            entries
	 **/
    @Override protected TiebreakCalculatorRrs initializeTiebreakCalculator() {
    	return new ScoringTiebreakerSnipe();
    }
    
	private class ScoringTiebreakerSnipe extends TiebreakCalculatorRrs {

		@Override
		public void calculateTieBreakers(EntryList entriesIn) {

			// list of racepoints, 1 elist item per tied entry, item is sorted list
			// of racepoints that are not throwouts
			List<RacePointsList> eLists = new ArrayList<RacePointsList>(5);
			EntryList entries = (EntryList) entriesIn.clone();

			// first create separate lists of finishes for each of the tied boats.
			for (Iterator eIter = entries.iterator(); eIter.hasNext();) {
				Entry e = (Entry) eIter.next();
				RacePointsList ePoints = racePointsList.findAll(e);
				eLists.add(ePoints);
			}

			List<RacePointsList> tiedWithBest = new ArrayList<RacePointsList>(5);
			// pull out best of the bunch one at a time
			// after each scan, best is dropped with no more change
			// in points. Each remaining gets .01 added to total
			// continue til no more left to play
			while (eLists.size() > 1) {
				RacePointsList bestPoints = eLists.get(0);
				tiedWithBest.clear();

				// loop thru entries, apply tiebreaker method (comparetied)
				// keep the best (winner)
				for (int i = 1; i < eLists.size(); i++) {
					RacePointsList leftPoints = eLists.get(i);

					// compares for ties by A8.1
					int c = compareTiedAddThrowout(leftPoints, bestPoints);
					if (c < 0) {
						bestPoints = leftPoints;
						tiedWithBest.clear();
					} else if (c == 0) {
						tiedWithBest.add(leftPoints);
					}
				}
				if (tiedWithBest.size() > 0) {
					// have boats tied after applying A8.1 - so send them into
					// next tiebreakers clauses
					tiedWithBest.add(bestPoints);
					compareWhoBeatWho(tiedWithBest, seriesPointsList, true);
				}
				// bestPoints should now equal the best, drop it from list
				double inc = (tiedWithBest.size() + 1) * TIEBREAK_INCREMENT;
				eLists.remove(bestPoints);
				eLists.removeAll(tiedWithBest);
				incrementSeriesScores(eLists, inc, seriesPointsList);
			}
			// we be done
		}

		/**
		 * compares two sets of race points for tie breaker resolution return -1 if "lefty" wins tiebreaker, 1 if righty
		 * wins, 0 if tied
		 */
		protected int compareTiedAddThrowout(RacePointsList left, RacePointsList right) {
			double lp = 0;
			double rp = 0;

			// (a) add them all up (should include throwouts)
			for (int i = 0; i < left.size(); i++) {
				lp += left.get(i).getPoints();
				rp += right.get(i).getPoints();
			}
			if (lp < rp)
				return -1;
			else if (rp < lp)
				return 1;
			else
				return 0;
		}

		/**
		 * Snipe tiebreakers part: (b) if the tie persist, the boats that scores better than the other(s) more times
		 * shall win the tie; (c) if the tie still persists, the boat that scores better in the last race shall win the
		 * tie."
		 * 
		 * for (b) am using algorithm for the old (and now deleted) RRS 2001 A8.2 and A8.3 except that throwouts are
		 * used in the who beat who calculation... relevant part of old A8.2 read: "If a tie remains between two boats,
		 * it shall be broken in favour of the boat that scored better than the other boat in more races. If more than
		 * two boats are tied, they shall be ranked in order of the number of times each boat scored better than another
		 * of the tied boats."
		 * <P>
		 * old RRS2001 A8.3 (now 8.2): "If a tie still remains between two or more boats, they shall be ranked in order
		 * of their scores in the last race. Any remaining ties shall be broken by using the tied boats' scores in the
		 * next-to-last race and so on until all ties are broken. These scores shall be used even if some of them are
		 * excluded scores."
		 * 
		 * @param stillTied
		 * @param series
		 * @param includeThrowouts
		 */
		protected void compareWhoBeatWho(List<RacePointsList> stillTied, SeriesPointsList series,
				boolean includeThrowouts) {
			int nRaces = stillTied.get(0).size();
			int nTied = stillTied.size();
			EntryList tiedEntries = new EntryList();
			double[] beatenCount = new double[nTied];

			for (Iterator iter = stillTied.iterator(); iter.hasNext();) {
				RacePointsList list = (RacePointsList) iter.next();
				if (list.size() == 0)
					continue;
				list.sortRace();
				tiedEntries.add(list.get(0).getEntry());
			}

			raceLoop: for (int n = 0; n < nRaces; n++) {
				// first populate the list of points for this race
				double[] points = new double[nTied];
				for (int i = 0; i < nTied; i++) {
					RacePointsList list = stillTied.get(i);
					if (list.size() == 0)
						continue;

					RacePoints p = list.get(n);
					if (!includeThrowouts && p.isThrowout())
						continue raceLoop; // if anyones throwout skip to next race

					points[i] = p.getPoints();
				}

				// loop thru and add to the beatenCount each time e(ntry) beats
				// o(ther)
				for (int e = 0; e < nTied; e++) {
					for (int o = 0; o < nTied; o++) {
						if (points[e] < points[o])
							beatenCount[e]++;
					}
				}
			}

			// now have beatenCount, can increment an entries score
			// TIEBREAK_INCREMENT for each
			// boat in the list with a higher beaten count
			for (int e = 0; e < nTied; e++) {
				for (int o = 0; o < nTied; o++) {
					if (beatenCount[e] < beatenCount[o]) {
						incrementSeriesScore(tiedEntries.get(e), TIEBREAK_INCREMENT, series);
					}
				}
			}

			// now look to see if anyone is STILL tied, applying A8.3 now
			// now have beatenCount, can increment an entries score
			// TIEBREAK_INCREMENT for each
			// boat in the list with a higher beaten count
			for (int e = 0; e < nTied; e++) {
				otherLoop: for (int o = 0; o < nTied; o++) {
					if ((e != o) && (beatenCount[e] == beatenCount[o])) {
						//
						for (int r = nRaces - 1; r >= 0; r--) {
							double ePts = stillTied.get(e).get(r).getPoints();
							double oPts = stillTied.get(o).get(r).getPoints();
							if (ePts > oPts)
								incrementSeriesScore(tiedEntries.get(e), TIEBREAK_INCREMENT, series);
							if (ePts != oPts)
								continue otherLoop;
						}
					}
				}
			}
		}
	}

}
/**
 * $Log: ScoringLowPointSnipe.java,v $ Revision 1.7 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.6 2006/02/26 21:02:52 sandyg changed DNF to be finishers plus 1 per Nov 2005 change to snipe scoring
 * 
 * Revision 1.5 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.13.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.13.2.1 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121, 1226607, killed Java Web Start startup
 * code
 * 
 * Revision 1.13 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.12 2003/07/11 02:20:16 sandyg fixed problem with scoring system combo box not getting updated
 * 
 * Revision 1.11 2003/07/08 01:56:29 sandyg fixed Snipe class scoring and tiebreakers to match july 2003 revisions
 * 
 * Revision 1.10 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.9 2003/04/20 15:43:59 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.8 2003/02/12 02:22:18 sandyg Fixed DN tiebreaker, fixed DNC/AVG, updated AVG to A.10a formula (throwout
 * now included)
 * 
 * Revision 1.7 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.6 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
