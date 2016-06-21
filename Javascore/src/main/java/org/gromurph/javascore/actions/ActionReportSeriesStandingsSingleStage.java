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
import java.util.List;

import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.javascore.model.scoring.StageScoringModel;

/**
 * Generates a report of the series standings
 **/
public class ActionReportSeriesStandingsSingleStage extends ActionReportSeriesStandingsAbstract {

	protected StageScoringModel getStageScoringModel() { 
		return (SingleStageScoring) fRegatta.getScoringManager();
	}

	@Override protected void generateBodySeries(List<String> linkList, PrintWriter pw2) {
		generateBodyForDivisions( linkList, pw2);
		generateBodyForFleets(linkList, pw2);
	}

	protected void generateBodyForDivisions(List<String> linkList, PrintWriter pw2) {

		// first report the starting divisions
		for (Division div : fRegatta.getDivisions()) {
			int n = div.getNumEntries();
			if (n > 0) {
				EntryList entries = div.getEntries();
				if (entries.size() > 0) {
					linkList.add(div.getName());
					generateDivisionHeader(pw2, div.getName(), n, DIVISION);
					reportForDivision(pw2, div, entries, div.isOneDesign());
					reportSubDivisions(pw2, div, linkList);
				}
			}
		}

	}

	protected void generateBodyForFleets(List<String> linkList, PrintWriter pw2) {
		// next report the fleets
		for (Fleet div : fRegatta.getFleets()) {
			int n = div.getNumEntries();
			if (n > 0) {
				EntryList entries = div.getEntries();
				if (entries.size() > 0) {
					linkList.add(div.getName());
					generateDivisionHeader(pw2, div.getName(), n, FLEET);
					reportForDivision(pw2, div, entries, div.isOneDesign());
					reportSubDivisions(pw2, div, linkList);
				}
			}
		}
	}

	@Override public void reportForDivision(PrintWriter pw, AbstractDivision div, EntryList entries, boolean is1D) {
		boolean posOnRight = true;
		String divname = div.getName();

		int startr = (fOptions.isShowLastXRaces()) ? Math.max(0,  fRegatta.getNumRaces() - fOptions.getLastXRaces()) : 0;
		initializeNotes();

		pw.println("<table class=" + SERIES_TABLECLASS + ">");

		// init header row
		boolean doRaceLinks = true;

		RaceList racesToShow = getRacesToShow(startr, div);

		int nCols = generateTableHeads(pw, posOnRight, divname, startr, doRaceLinks, racesToShow);

		pw.println("<tbody>");

		// loop thru entries add them in
		RacePointsList allPoints = new RacePointsList();

		SeriesPointsList seriesPoints = getStageScoringModel().getAllSeriesPoints().findAll(div);
		seriesPoints.sortPosition();

		for (SeriesPoints sp : seriesPoints) {
			generateTableRow(pw, posOnRight, racesToShow, allPoints, sp);
		}
		
		pw.println("</tbody></table>");

		postFinalNotes(allPoints);
		formatNotes(pw, fNotes);
	}

	@Override protected RacePoints getRacePointsForRow(SeriesPoints sp, RacePointsList rpl, AbstractDivision div, Race race) {
		RacePoints racepts = null;

		racepts = rpl.find(race, sp.getEntry(), div);
		return racepts;
	}

	@Override protected RaceList getRacesToShow( int startr, AbstractDivision div) {
    	// figure out which races to display
    	RaceList racesToShow = new RaceList();
     	for (int i = startr; i < fRegatta.getNumRaces(); i++) {
    		Race r = fRegatta.getRaceIndex(i);
    		boolean orParent = ((div instanceof SubDivision && (div.getParentDivision().isRacing(r))));
    		if ( div.isRacing(r, orParent)) racesToShow.add(r);
    	}
     	return racesToShow; 
	}


}

