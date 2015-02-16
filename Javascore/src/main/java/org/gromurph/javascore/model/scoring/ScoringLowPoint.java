// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPoint.java,v 1.12 2006/07/09 03:01:39 sandyg Exp $
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

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.SubDivisionList;

/**
 * ISAF LowPoint scoring system currently does ISAF 2001-2004
 **/
public class ScoringLowPoint implements ScoringModel, Constants {
	protected static ResourceBundle res = JavaScoreProperties.getResources();

	public static final String NAME = "ISAF Low Point 2013-2016";
	public static final String ALTNAME3 = "ISAF Low Point 2009-2012";
	public static final String ALTNAME2 = "ISAF Low Point 2005-2008";
	public static final String ALTNAME = "ISAF Low Point 2001-2004";

	public String getName() {
		return NAME;
	}
	
	@Override public String toString() {
		return res.getString("ScoringLowPoint");
	}
	
	protected ScoringOptions fOptions = new ScoringOptions();
	
	public void setOptions( ScoringOptions opt) {
		fOptions = opt;
	}
	public ScoringOptions getOptions() {
		return fOptions;
	}

	/**
	 * Given a Race, and a list of Entries calculates the RacePoints object The entries should be assumed to represent a
	 * single class within the Race calculateRace can assume that an Entries without a finish in the Race is DNC but
	 * should recognize that the Race may well have finishers not in the Entries.
	 * <P>
	 * Also assumes that points is pre-populated, just needs to have finish points assigned
	 * 
	 * @param race
	 *            race to be scored
	 * @param entries
	 *            entries i the race
	 * @param points
	 *            racepointslist in which to store the results
	 * @throws ScoringException
	 *             if there is a problem with the scoring
	 **/
	public void scoreRace(Race race, RacePointsList points, boolean positionOnly)
			throws ScoringException {

		setClassFinishPosition(points);

		// sort points on finishposition sorted top to bottom by finish
		points.sortCorrectedTimePosition();

		// loop thru the race's finishes, for each finish in entry list, set the
		// points
		boolean isFirstPlace = true;
		double pointsIncrement = 1.0;
		double basePoints = fOptions.getFirstPlacePoints();

		int divPosition = 1; // position within the divsion (as opposed to
		// within the fleet)

		// double weight = r.getWeight();
		// if (weight == 0) weight = 1;
		// === see RacePoints.setPoints() to for doing the weighting

		for (RacePoints rp : points) {
			rp.tossable = !race.isNonDiscardable();
			Finish f = rp.getFinish();
			double currentPoints = basePoints;
			rp.setPosition(divPosition++);

			boolean valid = f.getFinishPosition().isValidFinish();
			boolean isdsq = f.getPenalty().isDsqPenalty();
			if (valid && !isdsq) {
				// increment number points to be assigned to next guy if this
				// guy is a valid finisher and not disqualified
				if (isFirstPlace) {
					basePoints = pointsIncrement; 
					isFirstPlace = false;
				} 
				basePoints += pointsIncrement;
			} else if (!valid) {
				long newpos = f.getFinishPosition().longValue();
				rp.setPosition(newpos);
			}

			if (!positionOnly) {
				double weight = (race == null) ? 1 : race.getWeight();
				rp.setPoints( currentPoints * weight);
			}
		}

		if (!positionOnly) {
			// look for ties - must be done with correctedtime
			RacePoints lastrp = null;
			List<RacePoints> tied = new ArrayList<RacePoints>(5);
			for (RacePoints rp : points) {
				if (rp.isTiedFinish(lastrp)) {
					// boats are tied iff neither has a penalty and the current
					// boat
					// has a valid corrected time, and its the same as the last
					// corrected time
					if (tied.size() == 0)
						tied.add(lastrp);
					tied.add(rp);
				} else if (tied.size() > 0) {
					// coming out of set of tied boats, reset their points and
					// clear out
					setTiedPoints(tied);
					tied.clear();
				}
				lastrp = rp;
			}

			// if processing tieds at end of loop
			if (tied.size() > 0)
				setTiedPoints(tied);

			fAddinNotes = new ArrayList<SubDivision>(10);

			// loop through again doing penalties, AND the Subdivision addon
			// points
			for (RacePoints rp : points) {
				double basePts = rp.getPoints();
				Finish f = rp.getFinish();
				if (f.hasPenalty()) {
					double weight = (race == null) ? 1 : race.getWeight();
					basePts = basePts / weight; // remove weighting
					basePts = getPenaltyPoints(f.getPenalty(), points, basePts);
					basePts = basePts * weight; // put it back in
				}

				// add in the subdivision addon if applicable
				// NOTE That the ADDIN is NOT WEIGHTED!
				for (SubDivision sd : race.getRegatta().getSubDivisions()) {
					if (sd.contains(rp.getEntry())) {
						basePts += sd.getRaceAddon();
						if (!fAddinNotes.contains(sd) && sd.getRaceAddon() != 0)
							fAddinNotes.add(sd);
					}
				}

				rp.setPoints(basePts);
			}

		}
	}

	private void setClassFinishPosition(RacePointsList points) {
		points.sortElapsedTimePosition();
		int pos = 1;
		for (RacePoints rp : points) {
			if (rp == null || rp.getFinish() == null || rp.getFinish().getFinishPosition() == null) {
				int whoanelly = 1;
			}
			if (rp.getFinish().getFinishPosition().isValidFinish()) {
				rp.setClassFinishPosition(new FinishPosition(pos++));
			} else {
				rp.setClassFinishPosition(
						new FinishPosition(rp.getFinish().getFinishPosition().longValue()));
			}
		}
	}

	private List<SubDivision> fAddinNotes = null;

	private void setTiedPoints(List tied) {
		double pts = 0;
		for (Iterator t = tied.iterator(); t.hasNext();) {
			RacePoints rp = (RacePoints) t.next();
			pts += rp.getPoints();
		}
		pts = pts / tied.size();
		for (Iterator t = tied.iterator(); t.hasNext();) {
			RacePoints rp = (RacePoints) t.next();
			rp.setPoints(pts);
		}
	}
	    
	private ThrowoutCalculator throwoutCalculator;
	
	public void setThrowoutCalculator( ThrowoutCalculator t) {
		throwoutCalculator = t;
	}

	public SeriesPointsList scoreSeries(AbstractDivision div, EntryList entries, RacePointsList divPointsList) throws ScoringException {
		if (divPointsList == null || divPointsList.size() == 0) return new SeriesPointsList(); // nothing to do
		
		SeriesPointsList divSeriesPoints = SeriesPointsList.initPoints(entries, div);
		
		if (throwoutCalculator == null) {
			throwoutCalculator = new ThrowoutCalculator(this);
		}

		// calc throwouts and average points
		for (Entry e : entries) {
			RacePointsList ePoints = divPointsList.findAll(e);
			throwoutCalculator.calculateThrowouts( ePoints);
    		// run thru looking for average points
    		ScoringUtilities.calcAveragePoints(ePoints);
    		SeriesPoints esp = divSeriesPoints.find( e, div);
    		esp.setPoints( ePoints.getPointsTotal(true));
		}

		calculateRankings(divPointsList, divSeriesPoints);

		return divSeriesPoints; // returning these so that a division of finals
		// subdivsions can create its seriespoints too
	}

	protected TiebreakCalculatorRrs initializeTiebreakCalculator() {
		if (fOptions.getTiebreaker() == TIE_RRS_A82_ONLY) {
			return new TieBreakCalculatorA82Only();			
		} else { // fOptions.getTiebreaker() == TIE_RRS_DEFAULT) {
			return new TiebreakCalculatorRrs(); 
		}
	}
	
	public void calculateRankings(RacePointsList divPointsList, SeriesPointsList divSeriesPoints) {
		// now run through looking for clumps of tied boats
		// pass the clumps of tied boats on to scoringmodel for resolution
		divSeriesPoints.sortPoints();

		TiebreakCalculatorRrs tiebreaker = initializeTiebreakCalculator();
		tiebreaker.racePointsList = divPointsList;
		tiebreaker.seriesPointsList = divSeriesPoints;
		tiebreaker.process();

		// now set series position
		divSeriesPoints.sortPoints();
		int position = 1;
		double lastpoints = 0;
		boolean tied = false;
		for (int e = 0; e < divSeriesPoints.size(); e++) {
			SeriesPoints sp = divSeriesPoints.get(e);
			double thispoints = sp.getPoints();
			double nextpoints = ((e + 1 < divSeriesPoints.size()) ? divSeriesPoints.get(e + 1).getPoints() : 99999999.0);
			tied = !((thispoints != lastpoints) && (thispoints != nextpoints));
			if (!tied) {
				position = e + 1;
			} else {
				// position is same if tied with last
				if (thispoints != lastpoints)
					position = e + 1;
			}
			sp.setPosition(position);
			sp.setTied(tied);
			lastpoints = thispoints;
		}
	}

	
//	/**
//	 * resolve ties among a group of tied boats. A tie that is breakable should have .01 point increments added as
//	 * appropriate. Assume that each individual race and series points have calculated, and that throwouts have already
//	 * been designated in the points objects.
//	 * <P>
//	 * 
//	 * @param entriesIn
//	 *            list of tied entries
//	 * @param points
//	 *            list of points for relevant races and entries 
//	 * @param series
//	 *            map containing series points for the entries, prior to handling ties (and maybe more than just those
//	 *            entries
//	 **/
//	public void calculateTieBreakers( EntryList entriesIn, RacePointsList points, SeriesPointsList series) {
//		if (fOptions.getTiebreaker() == TIE_RRS_DEFAULT) {
//			calculateTieBreakersDefault(entriesIn, points, series);
//		} else if (fOptions.getTiebreaker() == TIE_RRS_A82_ONLY) {
//			calculateTieBreakerA82Only( entriesIn, points, series, false);
//		} else if (fOptions.getTiebreaker() == TIE_RRS_A82_MEDAL) {
//			calculateTieBreakerA82Only( entriesIn, points, series, true);
//		}
//	}


	/**
	 * sorts a points list as on points ascending
	 * 
	 * @param series
	 *            pointslist to be sorted
	 */
	public void sortSeries(SeriesPointsList series) {
		series.sortPoints();
	}

	/**
	 * Given a penalty, returns the number of points to be assigned (or added)
	 * 
	 * @param p
	 *            penalty to be calculated, should never be null
	 * @param entryPointList
	 *            racepointslist for whole race
	 * @param basePts
	 *            starting points level in case penalty is based on non-penalty points
	 * @return points to be assigned for the penalty
	 */
	public double getPenaltyPoints(Penalty p, RacePointsList entryPointList, double basePts) {

		// if MAN or RDG, return fixed points and be gone
		if (p.hasPenalty(RDG))
			return p.getPoints();

		double initialPoints = getPenaltyPointsWithoutManual(p, entryPointList, basePts);
		return initialPoints;
	}

	/**
	 * Given a penalty, returns the number of points to be assigned (or added)
	 * 
	 * @param p
	 *            penalty to be calculated, should never be null
	 * @param entryPointList
	 *            racepointslist for whole race
	 * @param basePts
	 *            starting points level in case penalty is based on non-penalty points
	 * @return points to be assigned for the penalty
	 */
	protected double getPenaltyPointsWithoutManual(Penalty p, RacePointsList entryPointList, double basePts) {
		int nEntries = 0;
		int nEntriesPlus1 = 0;

		if (entryPointList != null) {
			if (!fOptions.isEntriesLargestDivision()) {
				nEntries = entryPointList.size();
			} else {
				nEntries = calculateEntriesInLargestDivision(entryPointList);
			}
			nEntriesPlus1 = nEntries + 1;
		}

		if (p.hasPenalty(DNC) || p.hasPenalty(NOFINISH))
			return nEntriesPlus1;

		if (p.isDsqPenalty() || p.hasPenalty(DNS) || (p.isFinishPenalty() && !p.hasPenalty(TLE))) {
			if (fOptions.isLongSeries()) {

				// A9 RACE SCORES IN A SERIES LONGER THAN A REGATTA
				// "For a series that is held over a period of time longer than
				// a regatta, a boat that
				// came to the starting area but did not start (DNS), did not
				// finish (DNF), retired (RET)
				// or was disqualified (allDSQ) shall be scored points for the
				// finishing place one more than
				// the number of boats that came to the starting area. A boat
				// that did not come to
				// the starting area (DNC) shall be scored points for the
				// finishing place one more than the
				// number of boats entered in the series."

				if (entryPointList == null)
					return 0;
				else
					return nEntries - entryPointList.getNumberWithPenalty(DNC)
							- entryPointList.getNumberWithPenalty(NOFINISH) + 1;
			} else {
				return nEntriesPlus1;
			}
		}

		if (p.hasPenalty(TLE)) {
			int nFinishers = (entryPointList == null) ? 0 : entryPointList.getNumberFinishers();
			// set the basepts to the appropriate TLE points
			switch (fOptions.getTimeLimitPenalty()) {
			case TLE_DNF:
				basePts = getPenaltyPoints(new Penalty(DNF), entryPointList, basePts);
				break;
			case TLE_FINISHERSPLUS1:
				basePts = nFinishers + 1;
				break;
			case TLE_FINISHERSPLUS2:
				basePts = nFinishers + 2;
				break;
			case TLE_AVERAGE:
				basePts = nFinishers + ((((double) nEntries) - nFinishers) / 2.0);
				break;
			default:
				basePts = getPenaltyPoints(new Penalty(DNF), entryPointList, basePts);
				break;
			}
		}

		// ADD in other non-finish penalties
		double dsqPoints = getPenaltyPoints(new Penalty(DSQ), entryPointList, basePts);
		if (p.hasPenalty(CNF))
			basePts = calculatePercent(fOptions.getCheckinPercent(), basePts, nEntries, dsqPoints);
		if (p.hasPenalty(ZFP))
			basePts = calculatePercent(20, basePts, nEntries, dsqPoints);
		if (p.hasPenalty(ZFP2))
			basePts = calculatePercent(20, basePts, nEntries, dsqPoints);
		if (p.hasPenalty(ZFP3))
			basePts = calculatePercent(20, basePts, nEntries, dsqPoints);
		if (p.hasPenalty(SCP))
			basePts = calculatePercent(p.getPercent(), basePts, nEntries, dsqPoints);

		return basePts;
	}

	private int calculateEntriesInLargestDivision(RacePointsList pointsList) {

		// make sure qualifying regatta
		Regatta regatta = JavaScoreProperties.getRegatta();
		if (regatta == null)
			return pointsList.size();
		if (!regatta.isMultistage())
			return pointsList.size();
		if (pointsList.size() == 0)
			return 0;

		RacePoints rp0 = pointsList.get(0);

		// get all the subdivisions in the regatta
		SubDivisionList subdivs = regatta.getSubDivisions();
		int maxEntries = pointsList.size();

		for (SubDivision sd : subdivs) {
			// make sure race is part of qualifying series
			if ( sd.isRacing(rp0.getRace()) && sd.isGroupQualifying()) {
				maxEntries = Math.max(maxEntries, sd.getNumEntries());
			}
		}
		return maxEntries;
	}

	/**
	 * returns percent of number of entries, to nearest 10th, .5 going up with a maximum points of those for DNC
	 * 
	 * @param pct
	 *            the percent to be assigned
	 * @param basePts
	 *            initial number of points
	 * @param nEntries
	 *            number of entries in race
	 * @param maxPoints
	 *            max points to be awarded
	 * @return new points value
	 */
	public double calculatePercent(int pct, double basePts, double nEntries, double maxPoints) {
		// this gives points * 10
		double points = Math.round(nEntries * (pct / 10.0));
		points = points / 10.0;
		double newPoints = basePts + Math.round(points);
		if (newPoints > maxPoints)
			newPoints = maxPoints;
		return newPoints;
	}

	/**
	 * generates html text to the printwriter detail scoring conditions for the series at large
	 * 
	 * @param pw
	 *            printwriter on which to print notes
	 */
	public List<String> getSeriesScoringNotes(RacePointsList rp) {
		List<String> notes = new ArrayList<String>(5);

		notes.add(MessageFormat.format(res.getString("ScoringNotesSystem"), new Object[] { this.toString() }));

		// PenaltyDneAverage=Average Non-Finishers
		// PenaltyDneDidNotFinish=Did not Finish
		// PenaltyDnePlus1=Finishers plus 1
		// PenaltyDnePlus2=Finishers plus 2

		// public static final int TLE_DNF = 0;
		// public static final int TLE_FINISHERSPLUS1 = 1;
		// public static final int TLE_FINISHERSPLUS2 = 2;
		// public static final int TLE_AVERAGE = 3;

		String tlPenalty = res.getString("ScoringNotesTimeLimit") + " ";
		if (fOptions.getTimeLimitPenalty() != TLE_DNF) {
			switch (fOptions.getTimeLimitPenalty()) {
			case TLE_DNF:
				tlPenalty = tlPenalty + res.getString("PenaltyDneDidNotFinish");
				break;
			case TLE_FINISHERSPLUS1:
				tlPenalty = tlPenalty + res.getString("PenaltyDnePlus1");
				break;
			case TLE_FINISHERSPLUS2:
				tlPenalty = tlPenalty + res.getString("PenaltyDnePlus2");
				break;
			case TLE_AVERAGE:
				tlPenalty = tlPenalty + res.getString("PenaltyDneAverage");
				break;
			default:
				tlPenalty = tlPenalty + fOptions.getTimeLimitPenalty();
			}
			notes.add(tlPenalty);
		}

		// add note about subdivs addins
		if (fAddinNotes != null && fAddinNotes.size() > 0)
			notes.add(formatAddinNote());

		return notes;
	}



	private String formatAddinNote() {
		// does NOT check for fAddinNotes to be none null and contain something!
		// see above
		StringBuffer addinNote = new StringBuffer(res.getString("AddinNotePrefix"));
		boolean first = true;
		for (SubDivision sd : fAddinNotes) {
			if (!first) {
				addinNote.append(", ");
			}
			addinNote.append(sd.getName());
			addinNote.append(": ");
			addinNote.append(sd.getRaceAddon());
			first = false;
		}
		return addinNote.toString();
	}

}

/**
 * $Log: ScoringLowPoint.java,v $ Revision 1.12 2006/07/09 03:01:39 sandyg fixed note on TLE
 * 
 * Revision 1.11 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.10 2006/04/15 23:44:17 sandyg final Miami OCR gold/silver/medal fixes
 * 
 * Revision 1.9 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.8 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.6 2006/01/15 03:09:51 sandyg fixed bug 1258638, two tied boats, one with a scoring penalty.
 * 
 * Revision 1.5 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.4 2006/01/02 23:01:56 sandyg writeXml/readXml for Tiebreaker
 * 
 * Revision 1.3 2006/01/02 22:30:20 sandyg re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit
 * tests for both
 * 
 * Revision 1.2 2006/01/01 22:40:38 sandyg Renamed ScoringLowPoint to ScoringOptions, add gui unit tests
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.17.4.2 2005/11/26 17:45:01 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * Revision 1.17.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.17.2.2 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121, 1226607, killed Java Web Start startup
 * code
 * 
 * Revision 1.17.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.17 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 * 
 * Revision 1.16 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.15 2003/11/27 02:45:08 sandyg Fixed 1d tied boats (with same time) also minor reporting oddness on
 * positions with tied boats. Bug 836458
 * 
 * Revision 1.14 2003/11/26 03:29:00 sandyg Fixing points with long series and NO_FINISH (bug 839704)
 * 
 * Revision 1.13 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.12 2003/04/20 15:43:59 sandyg added javascore.Constants to consolidate penalty defs, and added new
 * penaltys TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.11 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.10 2003/02/12 02:22:18 sandyg Fixed DN tiebreaker, fixed DNC/AVG, updated AVG to A.10a formula (throwout
 * now included)
 * 
 * Revision 1.9 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.8 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
