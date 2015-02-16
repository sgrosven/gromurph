	
package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;

public class ThrowoutCalculator {
	
	public ThrowoutCalculator( ScoringModel m) {
		scoringModel = m;
	}

	private ScoringModel scoringModel;
	
	private int getThrowoutsWanted(RacePointsList pointsList, ScoringModel wModel) {
		int nThrows = 0;
		int nRaces = pointsList.size();

		switch ( wModel.getOptions().getThrowoutScheme()) {
		case Constants.THROWOUT_NONE:
			nThrows = 0;
			break;
		case Constants.THROWOUT_BYNUMRACES:
			for (int i = 0; i < wModel.getOptions().getThrowouts().size(); i++) {
				int minRaces = wModel.getOptions().getThrowouts().get(i).intValue();
				if (nRaces >= minRaces && minRaces > 0)
					nThrows = i + 1;
			}
			break;
		case Constants.THROWOUT_PERXRACES:
			if (wModel.getOptions().getThrowoutPerX() > 0) {
				nThrows = (nRaces / wModel.getOptions().getThrowoutPerX());
			}
			break;
		case Constants.THROWOUT_BESTXRACES:
			if (wModel.getOptions().getThrowoutBestX() > 0 && nRaces > wModel.getOptions().getThrowoutBestX()) {
				nThrows = nRaces - wModel.getOptions().getThrowoutBestX();
			}
			break;
		}
		return nThrows;
	}

	
	/**
	 * Calculates throwouts... its also the responsibility of the ScoringSystem to manage the setting of throwout
	 * criteria. Assumes that prior throwout flags have been cleared prior to calling this method
	 * <p>
	 * NOTE NOTE: if a boat has more that one race that is equal to their worse race this will select their earliest
	 * "worst races" per RRS starting in 2001
	 * 
	 * @param pointsList
	 *            list of race points on which to calc throwouts
	 */
	public void calculateThrowouts(RacePointsList pointsList) {
		List<Stage> stages = new ArrayList<Stage>(5);
		for ( RacePoints rp : pointsList) {
			if ( rp.stage != null && !stages.contains( rp.stage)) stages.add(rp.stage);
		}
		if (stages.size() <= 1) {
			calculateThrowoutsSingleStage( pointsList);
		} else {
			calculateThrowoutsMultiStages( pointsList, stages);
		}
	}
	
	protected void calculateThrowoutsMultiStages(RacePointsList pointsList, List<Stage> stages) {
		Collections.sort( stages, new Stage.SortTopToBottom());
		RacePointsList pointsLeft = new RacePointsList();
		pointsLeft.addAll(pointsList);
		
		RacePointsList carryList = new RacePointsList();
		
		for (Stage s : stages) {
			RacePointsList sPoints = pointsLeft.findAll(s);
			if ( s.getModel().getOptions().getThrowoutScheme() == Constants.THROWOUT_NONE) {
				carryList.clear();
				pointsLeft.removeAll(sPoints);
			} else if ( s.getThrowoutCarryOver() == Constants.ThrowoutCarryOver.NONE || s.prevStage == null) {
				carryList.addAll( sPoints);
				int n = getThrowoutsWanted(carryList, s.getModel());
				calculateNThrowouts( carryList, n);
				pointsLeft.removeAll(carryList);
				carryList.clear();
			} else {
				carryList.addAll( sPoints);
			}
		}
	}
	
	protected void calculateThrowoutsSingleStage(RacePointsList pointsList) {
		// look through the fThrowouts array and determine how many throwouts
		// to award
		int nThrows = getThrowoutsWanted(pointsList, scoringModel);
		calculateNThrowouts( pointsList, nThrows);
	}

	protected void calculateNThrowouts(RacePointsList pointsList, int nThrows) {

		// clear out what was thrown out before
		for (RacePoints thisRP : pointsList) {
			thisRP.setThrowout(false); 
		}
		
		for (int i = 0; i < nThrows; i++) {
			RacePoints worstRace = null;
			for (RacePoints thisRP : pointsList) {
				boolean eligible = thisRP.tossable;
				// continue to next racepoints if not eligible to be a throwout
				if (thisRP.isThrowout()) eligible=false;
				if (thisRP.getFinish().getPenalty().hasPenalty( Constants.DNE)) eligible=false;
				if (thisRP.getFinish().getPenalty().hasPenalty( Constants.AVG)) eligible=false;
				if (thisRP.getFinish().getPenalty().hasPenalty( Constants.DGM)) eligible=false;
				if (thisRP.getRace().isNonDiscardable()) eligible=false;
				
				if (eligible && 
					(worstRace == null || (thisRP.getPoints() > worstRace.getPoints()))) {
					worstRace = thisRP;
				} 
			}
			if (worstRace != null) {
				worstRace.setThrowout(true);
				checkForStageLimits( worstRace, pointsList);
			}
		}
	}

	private void checkForStageLimits( RacePoints worstRace, RacePointsList pointsList) {
		if (worstRace.stage == null) return;
		Stage s = worstRace.stage;
		if (s.getThrowoutCarryOver() != Constants.ThrowoutCarryOver.MAX1) return;
		
		// have the one max race we allow in this stage, set tossable to false for all remaining in stage
		for (RacePoints rp : pointsList) {
			if (rp.stage == s) rp.tossable = false;
		}
	}
}
