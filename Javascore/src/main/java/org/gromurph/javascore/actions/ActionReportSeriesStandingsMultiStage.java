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
import java.util.Collections;
import java.util.List;

import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.javascore.model.scoring.StageScoringModel;

/**
 * Generates a report of the series standings
 **/
public class ActionReportSeriesStandingsMultiStage extends ActionReportSeriesStandingsAbstract {

	protected MultiStageScoring stageManager;

	@Override protected void generateBodySeries(List<String> linkList, PrintWriter pw2) {

		stageManager = (MultiStageScoring) fRegatta.getScoringManager();

		// look for existence of final series subdivisions and races

		// if found, then print only the final subdivisions standings
		// if not, then report the main divisions only

		// regardless print the "scoring" divisions 

		boolean haveFinalsRaces = false;

		// first report the starting divisions
		for (Stage st : stageManager.getStages()) {
			// need 1 stage that is not a qualifying stage
			if (!st.isCombinedQualifying()) {
				haveFinalsRaces = true;
				break;
			}
		}

		// report the Divisions
		for (Division div : fRegatta.getDivisions()) {
			int n = div.getNumEntries();
			if (n > 0) {
					linkList.add(div.getName());
					generateDivisionHeader(pw2, div.getName(), n, DIVISION);
					reportForDivision(pw2, div, div.getEntries(), div.isOneDesign(), haveFinalsRaces);
			}
		}

		// report the Scoring Subdivisions
		for (SubDivision div : fRegatta.getSubDivisions()) {
			if (div.isGroupScoring()) {
				int n = div.getNumEntries();
				if (n > 0) {
						linkList.add(div.getName());
						generateDivisionHeader(pw2, div.getName(), n, DIVISION);
						reportForDivision(pw2, div, div.getEntries(), div.isOneDesign(), haveFinalsRaces);
				}
			}
			
		}

	}
	
	private void reportForDivision(PrintWriter pw, AbstractDivision div, EntryList entries, boolean is1D,
			boolean haveFinalsRaces) {
		boolean posOnRight = true;
		String divname = div.getName();
		
		stageManager = (MultiStageScoring) fRegatta.getScoringManager();
		SeriesPointsList rankingPoints = stageManager.getAllRegattaRankings().findAll(div);
		rankingPoints.sortPosition();
		// fRegatta.getScoringManager().getModel().sortSeries( seriesPoints);

		int startr = Math.max(0,  fRegatta.getNumRaces() - fOptions.getLastXRaces());
		initializeNotes();

		pw.println("<table class=" + SERIES_TABLECLASS + ">");

		// init header row
		boolean doRaceLinks = true;

		// figure out which races to display
		RaceList racesToShow = getRacesToShow(startr);
		
		int nCols = generateTableHeads(pw, posOnRight, divname, startr, doRaceLinks, racesToShow);
		
		pw.println("<tbody>");

		// loop thru entries add them in
		RacePointsList allPoints = new RacePointsList();

		Collections.sort( stageManager.getStages(), new Stage.SortTopToBottom());
		
		Stage prevLastStage = null;
		for (SeriesPoints sp : rankingPoints) {

			if (haveFinalsRaces) {
				Stage thisLastStage = getLastStageRaced(sp.getEntry());
				if (thisLastStage != null && thisLastStage != prevLastStage) {
					pw.print("<tr><td class=" + SERIESDIVHEADER_CELLCLASS + " align=left valign=middle colspan="
							+ nCols + ">");
					pw.print(thisLastStage.getName());
					pw.print("</td></tr>");
				}
				prevLastStage = thisLastStage;
			}

			generateTableRow(pw, posOnRight, racesToShow, allPoints, sp);
		}
		pw.println("</tbody></table>");

		postFinalNotes(allPoints);

		formatNotes(pw, fNotes);
	}

	@Override protected RacePoints getRacePointsForRow(SeriesPoints sp, RacePointsList rpl, AbstractDivision div, Race race) {
		RacePoints racepts = null;

		RacePointsList rpl2 = rpl.findAll(sp.getEntry());
		if (rpl2.size() > 0) racepts = rpl2.get(0);
		return racepts;
	}

	/**
	 * looks for the finals subdivision that the entry was a member
	 * 
	 * @param e
	 * @return
	 */
	private Stage getLastStageRaced(Entry e) {
		for ( Stage st : stageManager.getStages()) {
			for ( AbstractDivision div : st.getDivisions()) {
				boolean gotit = div.contains(e);
				if (gotit) return st;
			}
		}
		return null;
	}
}

