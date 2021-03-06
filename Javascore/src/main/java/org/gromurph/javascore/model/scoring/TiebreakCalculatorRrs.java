package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPointsList;

public class TiebreakCalculatorRrs extends TiebreakCalculator {

	/**
	 * does RRS App 8 without modification
	 **/

	@Override 
	protected int getTiebreakLevels() { return 2;}
	
	@Override 
	protected int compareTiebreakerLevel( int level, RacePointsList inLeft, RacePointsList inRight) {
		switch (level) {
    		case 1: return comparePointsBestToWorstNoDiscards( inLeft, inRight);
    		case 2: return compareWhoBeatWhoLast( inLeft, inRight);
		}
		return 0;
	}
	
}
