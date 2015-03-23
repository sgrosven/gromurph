package org.gromurph.javascore.model.scoring;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;

public class DailyStage extends Stage {

	// none of these are saved as they are re-created each time the regatta is scored
	private Date startDate;
	private Date endDate;
	private RaceList races;

	public DailyStage(RegattaScoringModel mgr, Date raceDate) {
		this( mgr, raceDate, raceDate);
	}
	public DailyStage(RegattaScoringModel mgr, Date startDate, Date endDate) {
		super(mgr);

		setPrevStage( null);
		setCombinedQualifying(false);
		setStartDate( startDate);
		setEndDate( endDate);
		setScoreCarryOver( ScoreCarryOver.NONE);
		setTiebreakCarryOver( TiebreakCarryOver.NONE);
		setThrowoutCarryOver(ThrowoutCarryOver.NONE);
		
		// add all divisions
		for (AbstractDivision div : mgr.getRegatta().getAllDivisions()) {
			addDivision(div);
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date raceDate) {
		this.startDate = raceDate;
		updateName();
	}

	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date raceDate) {
		this.endDate = raceDate;
		updateName();
	}

	private void updateName() {
  		DateFormat fmt = new SimpleDateFormat("EEE dd-MMM");
		String sName = (startDate == null) ? "None" : fmt.format(startDate);
		String eName = (endDate == null) ? "None" : fmt.format(endDate);
		
		if (startDate != null) {
			if ( endDate != null) {
				if (startDate == endDate) {
					// one date
					setName( sName);
				} else {
					// both dates - different
					setName( sName + " - " + eName); 
				}
			} else {
				// only a start date
				setName( sName + " and all after");
			}
		} else if (endDate != null) {
			// only an endDate
			setName( "All through " + endDate);
		} else {
			// both null
			setName( "All races");
		}
	}
	
	@Override
	public void scoreRaces() throws ScoringException {
		
		races = new RaceList();

		Regatta regatta = parentMgr.getRegatta();

		for (AbstractDivision div : getDivisions()) {

			divisionEntries.put( div.getName(), new EntryList());

			// calc races points for each race on this date
			for (Race race : regatta.getRaces()) {
				if (startDate == null || !race.getStartDate().before( startDate)) {
					// start is acceptable
					if (endDate == null || !race.getStartDate().after(endDate)) {
						races.add( race);
						// end is acceptable, keep this race
	    				logger.trace( " scoring {} in stage ", race.getName(), this.getName());
	    				RacePointsList divPointsList = scoreDivisionRace(div, race);
	    				fPointsList.addAll(divPointsList);
					}
				}
			}
		}
	}

	public RaceList getRaces() {
		return races;
	}
}
