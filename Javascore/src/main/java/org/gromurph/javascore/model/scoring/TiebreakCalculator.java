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

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * covering abstract class to run tied boats through a tiebreaker looks thru seriespoints lists, gathers groups of tied
 * boats and calls an abstract class to handle that group of tied boats
 */
public abstract class TiebreakCalculator {
	protected static ResourceBundle res = JavaScoreProperties.getResources();
	
	public RacePointsList racePointsList;
	public SeriesPointsList seriesPointsList;

	protected static final double TIEBREAK_INCREMENT = 0.0001;

	public TiebreakCalculator() {
	}

	abstract protected int getTiebreakLevels();
	abstract protected int compareTiebreakerLevel( int level, RacePointsList inLeft, RacePointsList inRight);
	
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

		processTiebreakerLevel( 1, eLists, seriesPointsList);
	}

	protected void processTiebreakerLevel( int level, List<RacePointsList> tiedList, SeriesPointsList series) {

		List<RacePointsList> stillTied = new ArrayList<RacePointsList>(5);
		stillTied.addAll( tiedList);
		
		while( stillTied.size() > 1 && level <= getTiebreakLevels()) {
			List<RacePointsList> best = getBestForTiebreakerLevel( stillTied, level);
			//logger.info( "tiebreak level=" + level + ", nTied=" + tiedList.size() + ", best.size=" + best.size());
			
			// remove best for stillTied
			stillTied.removeAll(best);
			// increment score of still tied
			incrementSeriesScores(stillTied, best.size()*TIEBREAK_INCREMENT, series); 
			level++;
			
			// punch the best thru subsequent ties if needed
			if (best.size() > 1) processTiebreakerLevel( level, best, series);
			// the rest start all over
			if (stillTied.size() > 1) {
				processTiebreakerLevel( 1, stillTied, series);
				stillTied.clear();
			}
		}		
	}
	
	protected List<RacePointsList> getBestForTiebreakerLevel(List<RacePointsList> eLists, int level) {
		List<RacePointsList> tiedWithBest = new ArrayList<RacePointsList>(5);
		RacePointsList bestPoints = eLists.get(0);

		// loop thru entries, apply tiebreaker method (comparetied)
		// keep the best (winner)
		for (int i = 1; i < eLists.size(); i++) {
			RacePointsList leftPoints = eLists.get(i);

			// compares for ties by A8.1
			int c = compareTiebreakerLevel(level, leftPoints, bestPoints);
			if (c < 0) {
				bestPoints = leftPoints;
				tiedWithBest.clear();
			} else if (c == 0) {
				tiedWithBest.add(leftPoints);
			}
		}
		tiedWithBest.add(bestPoints);
		return tiedWithBest;
	}


	protected RacePointsList prepBestToWorst(RacePointsList rpList) {
		return prepBestToWorst( rpList, false);
	}
	
	protected RacePointsList prepBestToWorst(RacePointsList rpList, boolean includeDiscards) {
		RacePointsList ePoints = (RacePointsList) rpList.clone();
		// delete throwouts from the list
		for (Iterator rIter = ePoints.iterator(); rIter.hasNext();) {
			RacePoints p = (RacePoints) rIter.next();
			if (!includeDiscards && p.isThrowout())
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
	protected int comparePointsBestToWorstNoDiscards(RacePointsList inLeft, RacePointsList inRight) {
		return comparePointsBestToWorst( inLeft, inRight, false);
	}
	
	protected int comparePointsBestToWorstWithDiscards(RacePointsList inLeft, RacePointsList inRight) {
		return comparePointsBestToWorst( inLeft, inRight, true);
	}
	
	protected int comparePointsBestToWorst(RacePointsList inLeft, RacePointsList inRight, boolean includeDiscards) {
		RacePointsList left = prepBestToWorst(inLeft, includeDiscards);
		RacePointsList right = prepBestToWorst(inRight, includeDiscards);

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
	 * RRS2001 modified A8.2 "If a tie still remains between two or more boats, they shall be ranked in order
	 * of their scores in the last race. Any remaining ties shall be broken by using the tied boats' scores in the
	 * next-to-last race and so on until all ties are broken. These scores shall be used even if some of them are
	 * excluded scores."
	 * 
	 * @param stillTied
	 *            list of boat scores of the group for which A8.1 does not resolve the tie
	 * @param series
	 *            list of series scores
	 */
	protected int compareWhoBeatWhoLast(RacePointsList inLeft, RacePointsList inRight) {
		inLeft.sortRace();
		inRight.sortRace();
		
		for (int r = inLeft.size()-1; r >= 0; r--) {
			double pLeft = inLeft.get(r).getPoints();
			double pRight = inRight.get(r).getPoints();
			if (pLeft < pRight) return -1;
			else if (pLeft > pRight) return 1;
		}
		return 0;
	}

	/**
	 * A8.1 If there is a series-score tie between two or more boards, they shall be ranked in order
	 * of their best excluded race score.
	 * 
	 * @param inLeft
	 *            racepointslist of lefty
	 * @param inRight
	 *            racepointslist of right
	 * @return -1 if "lefty" wins tiebreaker, 1 if righty wins, 0 if tied.
	 */
	protected int compareExcludedRaces(RacePointsList inLeft, RacePointsList inRight) {
		double leftBestExcluded = Double.MAX_VALUE;
		for (RacePoints rp : inLeft) {
			if (rp.isThrowout() && rp.getPoints() < leftBestExcluded) {
				leftBestExcluded = rp.getPoints();
			}
		}
		
		double rightBestExcluded = Double.MAX_VALUE;
		for (RacePoints rp : inRight) {
			if (rp.isThrowout() && rp.getPoints() < rightBestExcluded) {
				rightBestExcluded = rp.getPoints();
			}
		}

		if (leftBestExcluded < rightBestExcluded) return -1;
		else if (leftBestExcluded > rightBestExcluded) return 1;
		else return 0;
	}

	protected int compareScoreWithExcluded(RacePointsList inLeft, RacePointsList inRight) {
		int left = 0;
		for (RacePoints lp : inLeft) {
			left += lp.getPoints();
		}
		
		int right = 0;
		for (RacePoints rp: inRight) {
			right += rp.getPoints();
		}
		
		if (left < right) return -1;
		if (left > right) return 1;
		return 0;
	}

	protected int compareWhoBeatWhoMost(RacePointsList inLeft, RacePointsList inRight) {
		int leftWins = 0;
		int rightWins = 0;
		for (RacePoints lp : inLeft) {
			Entry entry1 = lp.getEntry();
			Race lr = lp.getRace();
			
			for (RacePoints rp : inRight) {
				if (rp.getRace().equals(lr)) {
					if (lp.getPoints() < rp.getPoints()) leftWins++;
					if (lp.getPoints() > rp.getPoints()) rightWins++;
				}
			}
		}
		if (leftWins > rightWins) return -1;
		if (leftWins < rightWins) return 1;
		return 0;
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
