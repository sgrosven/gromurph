package org.gromurph.javascore.model.scoring;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;

public class DailyStage extends Stage {

	public Date getRaceDate() {
		return raceDate;
	}

	public void setRaceDate(Date raceDate) {
		this.raceDate = raceDate;
		
		if (raceDate != null) {
    		DateFormat fmt = new SimpleDateFormat("EEE dd-MMM");
    		String dayName = fmt.format(raceDate);
    		setName( dayName);
		} else {
			setName( "No date");
		}
	}

	private Date raceDate;
	
	public DailyStage(RegattaScoringModel mgr, Date raceDate) {
		super(mgr);

		setPrevStage( prevStage);
		setCombinedQualifying(false);
		setRaceDate( raceDate);
		
		// add all divisions
		for (AbstractDivision div : mgr.getRegatta().getAllDivisions()) {
			addDivision(div);
		}
		
	}

	@Override
	public void scoreRaces() throws ScoringException {
		Regatta regatta = parentMgr.getRegatta();

		for (AbstractDivision div : getDivisions()) {

			divisionEntries.put( div.getName(), new EntryList());

			// calc races points for each race on this date
			for (Race race : regatta.getRaces()) {
				if (race.getStartDate().equals( this.raceDate)) {
    				logger.trace( " scoring {} in stage ", race.getName(), this.getName());
    				RacePointsList divPointsList = scoreDivisionRace(div, race);
    				fPointsList.addAll(divPointsList);
				}
			}
		}
	}


}
