package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.RacePointsList;

public class TiebreakCalculatorA82Only extends TiebreakCalculator {

	@Override 
	protected int getTiebreakLevels() { return 1;}
	
	@Override 
	protected int compareTiebreakerLevel( int level, RacePointsList inLeft, RacePointsList inRight) {
		switch (level) {
    		case 1: return compareWhoBeatWhoLast( inLeft, inRight);
		}
		return 0;
	}

}
