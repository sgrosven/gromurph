// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportSeriesStandings.java,v 1.10 2006/09/03 02:12:30 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.scoring.DailyStage;
import org.gromurph.javascore.model.scoring.DailyStageScoring;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.javascore.model.scoring.StageScoringModel;

/**
 * Generates a report of the series standings
 **/
public class ActionReportSeriesStandingsDailyStage extends ActionReportSeriesStandingsSingleStage {

	private DailyStage currentStage;
	private DailyStageScoring scorer;
	
	@Override protected StageScoringModel getStageScoringModel() { return currentStage; }
	
	@Override protected void generateBodySeries(List<String> linkList, PrintWriter pw2) {
		scorer = (DailyStageScoring) fRegatta.getScoringManager();
		
		for (Stage stage : scorer.getStages()) {

			pw2.print("</p><p class=" + REPORTTITLE_PCLASS + ">");
			String stageTitle = MessageFormat.format(res.getString("ReportDailyStageTitle"), new Object[] { stage.getName() });
			pw2.print( stageTitle);
			pw2.println("</p>");

			currentStage = (DailyStage) stage;
			
			generateBodyForDivisions( linkList, pw2);
			generateBodyForFleets(linkList, pw2);
		}
	}

	@Override protected RaceList getRacesToShow( int startr, AbstractDivision div) {
    	RaceList racesToShow = new RaceList();
     	for (Race r : currentStage.getRaces()) {
    		if ( div.isRacing(r) && !racesToShow.contains(r)) racesToShow.add(r);
    	}
     	return racesToShow; 
	}

	@Override protected RacePointsList getRacePointsForRace(Race race) {
		RacePointsList rpl = getStageScoringModel().getRacePointsList().findAll(race);
		return rpl;
	}

	@Override protected List<String> getScoringNotes(RacePointsList allPoints) {
		return currentStage.getSeriesScoringNotes(allPoints);
	}

}

