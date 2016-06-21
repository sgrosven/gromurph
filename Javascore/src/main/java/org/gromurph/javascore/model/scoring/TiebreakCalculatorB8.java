package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPointsList;

public class TiebreakCalculatorB8 extends TiebreakCalculator {

	/**
	 * handles Windsurfer tiebreakers - RRS B8 (A8 inside it)
	 **/
	/**
	 * from RRS 2013-2016: 
	 * 
	 * A8.1 If there is a series-score tie between two or more boards, they shall be ranked in order
	 * of their best excluded race score.
	 * 
	 * A8.2 If a tie remains between two or more boards, each board’s race scores, including excluded scores, shall be
	 * listed in order of best to worst, and at the first point(s) where there is a difference the tie shall be broken
	 * in favour of the board(s) with the best score(s). These scores shall be used even if some of them are excluded
	 * scores.
	 * 
	 * A8.3 If a tie still remains between two or more boards, they shall be ranked in order of their scores in the last
	 * race. Any remaining ties shall be broken by using the tied boards’ scores in the next-to-last race and so on
	 * until all ties are broken. These scores shall be used even if some of them are excluded scores.
	 */

	@Override 
	protected int getTiebreakLevels() { return 3;}
	
	@Override
	protected int compareTiebreakerLevel( int level, RacePointsList inLeft, RacePointsList inRight) {
		switch (level) {
    		case 1: return compareExcludedRaces( inLeft, inRight);
    		case 2: return comparePointsBestToWorstWithDiscards( inLeft, inRight);
    		case 3: return compareWhoBeatWhoLast( inLeft, inRight);
		}
		return 0;
	}
	
}
