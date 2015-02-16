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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
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
	protected TiebreakCalculatorRrs initializeTiebreakCalculator() {
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
	public class ScoringTiebreakerDnIceboat extends TiebreakCalculatorRrs {

		@Override
		public void calculateTieBreakers(EntryList entriesIn) {
			compareWhoBeatWhoMost(entriesIn);
		}

		/**
		 * resolves tiebreakers by comparing who beat who most often. The DN rules wording presumes that only two boats
		 * are ever tied. This formula works for that and takes a pretty arbitrary approach to more than 2 tied boats.
		 * It keeps a total count of who among tied boats beat any of the others.
		 * 
		 * @param stillTied
		 * @param series
		 * @param includeThrowouts
		 */
		protected void compareWhoBeatWhoMost(EntryList tiedEntries) {
			int nTied = tiedEntries.size();
			int[] beatenCount = new int[nTied];

			List<Race> races = new ArrayList<Race>();
			for (RacePoints rp : racePointsList) {
				if (!races.contains(rp.getRace()))
					races.add(rp.getRace());
			}
			int nRaces = races.size();

			for (int e1 = 0; e1 < nTied; e1++) {
				Entry entry1 = tiedEntries.get(e1);
				RacePointsList points1 = racePointsList.findAll(entry1);

				for (int e2 = 0; e2 < nTied; e2++)
					if (e2 != e1) {
						Entry entry2 = tiedEntries.get(e2);
						RacePointsList points2 = racePointsList.findAll(entry2);
						for (int r = 0; r < nRaces; r++) {
							Race race = (Race) races.get(r);

							RacePointsList er1 = points1.findAll(race);
							RacePointsList er2 = points2.findAll(race);

							if (er1.size() > 0 && er2.size() > 0) {
								double ep1 = er1.get(0).getPoints();
								double ep2 = er2.get(0).getPoints();
								if (ep1 < ep2)
									beatenCount[e1]++;
							}
						}
					}
			}

			EBeat[] beatList = new EBeat[nTied];
			for (int e = 0; e < nTied; e++) {
				beatList[e] = new EBeat(tiedEntries.get(e), beatenCount[e]);
			}
			Arrays.sort(beatList);

			double increment = 0; //TIEBREAK_INCREMENT;
			for (int e = 1; e < nTied; e++) {
				if (beatList[e].beat != beatList[e - 1].beat) {
					// if beat count is same, use "old" increment, boats will remain tied
					increment = TIEBREAK_INCREMENT * (e + 1);
				}
				incrementSeriesScore(beatList[e].entry, increment, seriesPointsList);
			}
		}

		private class EBeat implements Comparable {
			public Entry entry;
			public int beat;

			public EBeat(Object e, int b) {
				entry = (Entry) e;
				beat = b;
			}

			public int compareTo(Object o) {
				int thatBeat = ((EBeat) o).beat;
				return (thatBeat > beat) ? 1 : ((thatBeat == beat) ? 0 : -1);
			}
		}

	}

}
/**
 * $Log: ScoringLowPointDnIceboat.java,v $ Revision 1.6 2006/04/15 23:43:30 sandyg fix tiebreaker bug
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
 * Revision 1.9.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.9 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.8 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/04/23 00:30:20 sandyg added Time-based penalties
 * 
 * Revision 1.6 2003/02/12 02:22:18 sandyg Fixed DN tiebreaker, fixed DNC/AVG, updated AVG to A.10a formula (throwout
 * now included)
 * 
 * Revision 1.5 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.4 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
