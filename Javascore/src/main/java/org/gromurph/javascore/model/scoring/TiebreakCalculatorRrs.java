// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringTiebreaker.java,v 1.5 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================

package org.gromurph.javascore.model.scoring;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.util.Util;

/**
 * covering abstract class to run tied boats through a tiebreaker looks thru seriespoints lists, gathers groups of tied
 * boats and calls an abstract class to handle that group of tied boats
 */
public class TiebreakCalculatorRrs {
	protected static ResourceBundle res = JavaScoreProperties.getResources();

	public RacePointsList racePointsList;
	public SeriesPointsList seriesPointsList;

	protected static final double TIEBREAK_INCREMENT = 0.0001;

	public TiebreakCalculatorRrs() {
	}

	public void process() {
		if (seriesPointsList == null || seriesPointsList.size() == 0) return;
		
		EntryList tiedBunch = new EntryList();
		SeriesPoints basePoints = seriesPointsList.get(0);

		for (int i = 1; i < seriesPointsList.size(); i++) {
			SeriesPoints newPoints = seriesPointsList.get(i);

			if (basePoints.getPoints() == newPoints.getPoints()
					&& basePoints.getDivision().equals(newPoints.getDivision())) {
				// have a tie, see if starting a new group
				if (tiedBunch.size() == 0) {
					tiedBunch.add(basePoints.getEntry());
				}
				tiedBunch.add(newPoints.getEntry());
			} else {
				// this one not tie, send bunch to tiebreaker resolution
				if (tiedBunch.size() > 0) {
					calculateTieBreakers(tiedBunch);
					tiedBunch.clear();
				}
				basePoints = newPoints;
			}
		}

		// at end of loop, see if we are tied at the bottom
		if (tiedBunch.size() > 0) {
			calculateTieBreakers(tiedBunch);
		}
	}

	/**
	 * does RRS App 8 without modification
	 **/
	public void calculateTieBreakers(EntryList entriesIn) {
		// list of racepoints, 1 elist item per tied entry, item is sorted list
		// of racepoints that are not throwouts
		List<RacePointsList> eLists = new ArrayList<RacePointsList>(entriesIn.size());
		EntryList entries = (EntryList) entriesIn.clone();

		// first create separate lists of finishes for each of the tied boats.
		for (Iterator eIter = entries.iterator(); eIter.hasNext();) {
			Entry e = (Entry) eIter.next();
			RacePointsList ePoints = racePointsList.findAll(e);
			eLists.add(ePoints);
		}

		processStandardTiebreaker(eLists, seriesPointsList);
	}

	protected void processStandardTiebreaker(List<RacePointsList> eLists, SeriesPointsList series) {

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
				int c = comparePointsBestToWorst(leftPoints, bestPoints);
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
				compareWhoBeatWhoLast(tiedWithBest, series);
			}
			// bestPoints should now equal the best, drop it from list
			double inc = (tiedWithBest.size() + 1) * TIEBREAK_INCREMENT;
			eLists.remove(bestPoints);
			eLists.removeAll(tiedWithBest);
			incrementSeriesScores(eLists, inc, series);
		}
		// we be done
	}

	private RacePointsList prepBestToWorst(RacePointsList rpList) {
		RacePointsList ePoints = (RacePointsList) rpList.clone();
		// delete throwouts from the list
		for (Iterator rIter = ePoints.iterator(); rIter.hasNext();) {
			RacePoints p = (RacePoints) rIter.next();
			if (p.isThrowout())
				rIter.remove();
		}

		ePoints.sortPoints();
		return ePoints;
	}

	/**
	 * compares two sets of race points for tie breaker resolution.
	 * 
	 * <p>
	 * RRS2001 A8.1: "If there is a series score tie between two or more boats, each boat's race scores shall be listed
	 * in order of best to worst, and at the first point(s) where there is a difference the tie shall be broken in
	 * favour of the boat(s) with the best score(s). No excluded scores shall be used."
	 * 
	 * @param races
	 *            races involved
	 * @param inLeft
	 *            racepointslist of lefty
	 * @param inRight
	 *            racepointslist of right
	 * @return -1 if "lefty" wins tiebreaker, 1 if righty wins, 0 if tied.
	 */
	protected int comparePointsBestToWorst(RacePointsList inLeft, RacePointsList inRight) {
		RacePointsList left = prepBestToWorst(inLeft);
		RacePointsList right = prepBestToWorst(inRight);

		double lp = 0;
		double rp = 0;

		// we know they are sorted by finish points, look for first non-equal
		// finish
		for (int i = 0; i < left.size(); i++) {
			lp = left.get(i).getPoints();
			rp = right.get(i).getPoints();
			if (lp < rp)
				return -1;
			else if (rp < lp)
				return 1;
		}
		return 0;
	}

	/**
	 * the old algorithm, including code for the old RRS2001 A8.2 tiebreaker deleted by ISAF 2002 mid year meetings
	 * 
	 * old and now deleted, RRS2001 A8.2: "If a tie remains between two boats, it shall be broken in favour of the boat
	 * that scored better than the other boat in more races. If more than two boats are tied, they shall be ranked in
	 * order of the number of times each boat scored better than another of the tied boats. No race for which a tied
	 * boat's score has been excluded shall be used."
	 * <P>
	 * old RRS2001 A8.3 (now 8.2): "If a tie still remains between two or more boats, they shall be ranked in order of
	 * their scores in the last race. Any remaining ties shall be broken by using the tied boats' scores in the
	 * next-to-last race and so on until all ties are broken. These scores shall be used even if some of them are
	 * excluded scores."
	 * 
	 * @param stillTied
	 * @param series
	 * @param includeThrowouts
	 */
	protected void compareWhoBeatWho_old(List<RacePointsList> stillTied, SeriesPointsList series,
			boolean includeThrowouts) {
		int nRaces = stillTied.get(0).size();
		int nTied = stillTied.size();
		EntryList tiedEntries = new EntryList();
		double[] beatenCount = new double[nTied];

		for (RacePointsList list : stillTied) {
			if (list.size() > 0) {
    			list.sortRace();
    			tiedEntries.add(list.get(0).getEntry());
			}
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

	/**
	 * applying the remaining tiebreakers of RRS2001 A8 to set of boats tied after comparing their list of scores. This
	 * is the new 2002+ formula after ISAF deleted 8.2 and renumbered 8.3 to 8.2
	 * <P>
	 * RRS2001 modified A8.2 (old 8.3): "If a tie still remains between two or more boats, they shall be ranked in order
	 * of their scores in the last race. Any remaining ties shall be broken by using the tied boats' scores in the
	 * next-to-last race and so on until all ties are broken. These scores shall be used even if some of them are
	 * excluded scores."
	 * 
	 * @param stillTied
	 *            list of boat scores of the group for which A8.1 does not resolve the tie
	 * @param series
	 *            list of series scores
	 */
	protected void compareWhoBeatWhoLast(List<RacePointsList> stillTied, SeriesPointsList series) {
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

	protected void incrementSeriesScore(Entry e, double amount, SeriesPointsList series) {
		// find all series points for e, should be exactly 1
		SeriesPoints eSeries = series.findAll(e).get(0);
		// add TIEBREAK_INCREMENT to its score
		eSeries.setPoints(eSeries.getPoints() + amount);
	}

	protected void incrementSeriesScores(List<RacePointsList> eLists, double amount, SeriesPointsList series) {
		// add TIEBREAK_INCREMENT to series points of remaining tied boats
		for (RacePointsList pl : eLists) {
			if (pl.size() == 0) {
				String msg = MessageFormat
						.format(res.getString("ScoringMessageInvalidSeries"), new Object[] { eLists });
				Util.showError(this, msg, false);
			} else {
				// pull entry from 1st element of the list
				Entry entry = pl.get(0).getEntry(); // entry same for all in pl
				incrementSeriesScore( entry, amount, series);
			}
		}
	}
}
/**
 * $Log: ScoringTiebreaker.java,v $ Revision 1.5 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.3.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.3 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.2 2003/04/23 00:30:20 sandyg added Time-based penalties
 * 
 * Revision 1.1 2003/02/12 02:22:18 sandyg Fixed DN tiebreaker, fixed DNC/AVG, updated AVG to A.10a formula (throwout
 * now included)
 * 
 */