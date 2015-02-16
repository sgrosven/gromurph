package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.RacePointsList;

public class TieBreakCalculatorA82Only extends TiebreakCalculatorRrs {

	/**
	 * handles alternate tiebreaker option. Currently only ability to do just App A8.2 only (best score last race)
	 **/
	@Override public void calculateTieBreakers(EntryList entriesIn) {

		// list of racepoints, 1 elist item per tied entry, item is sorted list
		// of racepoints that are not throwouts
		List<RacePointsList> eLists = new ArrayList<RacePointsList>(entriesIn.size());

		// first create separate lists of finishes for each of the tied boats.
		for (Iterator eIter = entriesIn.iterator(); eIter.hasNext();) {
			Entry e = (Entry) eIter.next();
			RacePointsList ePoints = racePointsList.findAll(e);
			eLists.add(ePoints);
		}

		compareWhoBeatWhoLast(eLists, seriesPointsList);
	}


}
