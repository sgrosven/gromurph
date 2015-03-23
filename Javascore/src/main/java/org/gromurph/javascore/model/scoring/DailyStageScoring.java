package org.gromurph.javascore.model.scoring;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.StageList;

public class DailyStageScoring extends MultiStageScoring {

	@Override
	public boolean validate() throws ScoringException {
		boolean ok = super.validate();
		// check for a race date in all races
		
		for (Race race : getRegatta().getRaces()) {
			if (race.getStartDate() == null) {
				warnings.add(MessageFormat.format(res.getString("WarningRaceRequiresStartDate"), 
						new Object[] { race.toString() }));
				ok = false;
			}
		}
		return ok;
	}

	public static DailyStageScoring createFromSingleStage( SingleStageScoring ss) {
		DailyStageScoring ms = new DailyStageScoring();
		Regatta r = JavaScoreProperties.getRegatta();
		Stage s = ms.getStage(Stage.FLEET);
		s.setName( Stage.ALLDAYS);
		s.setModel( ss.getModel());
		for (AbstractDivision div : r.getDivisions()) {
			s.divisions.add(div);
		}
		return ms;
	}
		
	
	@Override
	public void initializeScoring() {
		// when we get here, we should know we have at least one
		// race and that all races have a start date
		initializeDailyStages();	
		
		super.initializeScoring();
	}

	@Override
	public void scoreRegatta() throws ScoringException {
		
		// turn multi-stage scoring lose on the whole chebang
		super.scoreRegatta();
	}


	private void initializeDailyStages() {
		Regatta reg = getRegatta();
		
		// drop all previous stages
		stages = new StageList();
		
		// make list of race dates
		List<Date> dates = new ArrayList<Date>(10);
		for (Race r : reg.getRaces()) {
			if (!dates.contains(r.getStartDate())) {
				dates.add( r.getStartDate());
			}
		}
		// sort that list
		Object[] sortedDates = dates.toArray();
		Arrays.sort( sortedDates);
		
		// create an allday stage
		Stage a = new DailyStage( this, (Date) sortedDates[0], (Date) sortedDates[ sortedDates.length-1]);
		a.setName( Stage.ALLDAYS);
		stages.add(a);
		
		// recreate a new stage for each day of with racing
		for ( int d = 0; d < sortedDates.length; d++) {
			
			Stage s = new DailyStage( this, (Date) sortedDates[d]);
			stages.add(s);
		}
	}

}
