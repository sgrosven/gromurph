package org.gromurph.javascore.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.RegattaScoringModel;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.javascore.model.scoring.StageScoringModel;

public abstract class ActionReportSeriesStandingsAbstract extends ActionReport {

	public static String TABNAME = "Series";
	private static ReportOptions sReportOptions;
	
	@Override
	public String getTabName() {
		return TABNAME;
	}

	public static ReportOptions getDefaultReportOptions() {
		if (sReportOptions == null) {
			sReportOptions = new ReportOptions(TABNAME);
			sReportOptions.setOptionLocationValues(
			// Skip, Crew, Boat, Club, mna, rsa, rating, full rating
					new String[] { "2.1", "none", "1.1", "none", "none", "none", "none", "none" });
		}
		return sReportOptions;
	}

	@Override
	public String getReportName() {
		Regatta reg = fRegatta;
		if (reg == null) reg = JavaScoreProperties.getRegatta();
		if ((reg == null) || (reg.getNumRaces() == 1)) {
			return res.getString("ReportSeriesTitleSingular");
		} else {
			return java.text.MessageFormat.format(res.getString("ReportSeriesTitlePlural"), new Object[] { new Integer(
					reg.getNumRaces()) });
		}
	}

	public ActionReportSeriesStandingsAbstract() {
		super();
	}

	/**
	 * splits up the regatta into a set of division groups, and passes on to specific reports each division and lets via
	 * 'getBaseList(div)' a the baselist type (entries, racepoints, seriespoints) to be
	 * 
	 * @param pw
	 * @param obj
	 */
	@Override public void generateBody(PrintWriter pw, Object obj) {
		
		if (fRegatta.getNumRaces() == 0) {
			pw.print( res.getString( "RegattaTOCLabelNoRacesYet"));
		} else {

    		if (fRegatta.getComment() != null && fRegatta.getComment().trim().length() > 0) {
    			pw.println("<p class=" + COMMENTS_PCLASS + ">");
    			pw.print(fRegatta.getComment());
    			pw.println("</p>");
    		}
    
    		// so we can insert linkList at top do rest of report
    		StringWriter sw = new StringWriter(2048);
    		PrintWriter pw2 = new PrintWriter(sw);
    
    		List<String> linkList = new ArrayList<String>(10);
    
    		generateBodySeries(linkList, pw2);
    
    		// now dump all to the right writer
    		generateDivisionLinks(pw, linkList);
    
    		try {
    			pw2.flush();
    			sw.flush();
    			pw2.close();
    			sw.close();
    		} catch (java.io.IOException e) {}
    		
    		pw.print(sw.toString());
		}
		

	}

	protected RaceList getRacesToShow( int startr) {
		return getRacesToShow(startr, null);
	}
	protected RaceList getRacesToShow( int startr, AbstractDivision div) {
    	// figure out which races to display
    	RaceList racesToShow = new RaceList();
     	for (int i = startr; i < fRegatta.getNumRaces(); i++) {
    		Race r = fRegatta.getRaceIndex(i);
    		racesToShow.add(r);
    	}
     	return racesToShow;
	}

	protected int generateTableHeads(PrintWriter pw, boolean posOnRight, String divname, int startr,
			boolean doRaceLinks, RaceList racesToShow) {
		pw.print("<thead><tr>");
		addTableCell(pw, res.getString("ColHeadPos"), "center");
		int nCols = generateDescriptiveHeaders(pw) + racesToShow.size();
		
		String regattaBaseFileName = fRegatta.getBaseRegattaName();

		// generate the the race column headers
		int i = startr;
		for (Race r : racesToShow) {

			// pad short race names to keep columns from getting too skinny
			// while still leaving max width flexible
			// String spaces = "&nbsp;&nbsp;";
			String racename = r.toString().trim();
			// if (racename.length() < 3) spaces = spaces + "&nbsp;";
			StringBuffer sb = new StringBuffer(64);
			// sb.append( spaces);
			if (doRaceLinks) {
				sb.append("<a href=\"");
				sb.append(regattaBaseFileName);
				sb.append("_race");
				sb.append(++i);
				sb.append(".html#");
				sb.append(divname);
				sb.append("\">");
			}

			sb.append(racename);
			if (r.isNonDiscardable() || r.getWeight() != 1.00) {
				sb.append("<sup>");

				if (r.isNonDiscardable()) {
					// add footnote to race header
					String note = formatNonDiscardableNote(r);
					fNotes.add(note);
					sb.append(fNotes.size());
				}

				if (r.getWeight() != 1.00) {
					// add footnote to race header
					String note = formatWeightedNote(r);
					fNotes.add(note);
					if (r.isNonDiscardable()) sb.append(",");
					sb.append(fNotes.size());
				}

				sb.append("</sup>");
			}

			if (doRaceLinks) {
				sb.append("</a>");
			}
			// sb.append( spaces);
			// sb.append( "&nbsp;&nbsp;</u></b>");
			addTableCell(pw, sb.toString(), "center");
		}
		addTableCell(pw, res.getString("ColHeadTotalPoints"), "center");
		if (posOnRight) addTableCell(pw, res.getString("ColHeadPos"), "center");
		pw.println("");
		pw.println("</tr></thead>");
		return nCols;
	}

	protected void generateTableRow(PrintWriter pw, boolean posOnRight, RaceList racesToShow, RacePointsList allPoints,
			SeriesPoints sp) {
		
		String posString = Long.toString(sp.getPosition());
		if (sp.isTied()) posString = posString + "T";

		pw.print("<tr>");
		addTableCell(pw, posString, "center", SERIES_CELLCLASS);
		generateDescriptiveCells(pw, sp.getEntry());

		// loop thru races display points for each race
		for (Race race : racesToShow) {
			
			RacePointsList rpl = getRacePointsForRace(race);
			allPoints.addAll(rpl);

			RacePoints racepts = getRacePointsForRow(sp, rpl, null, null);

			if (racepts == null) {
				addTableCell(pw, "&nbsp;", "center", SERIES_CELLCLASS);
			} else {
				String fin = RacePoints.format(racepts, !fOptions.isHidePenaltyPoints());
				String note = racepts.getFinish().getPenalty().getNote();
				if ((note == null || note.length() == 0)
						&& racepts.getFinish().getPenalty().hasPenalty(Constants.AVG)) {
					note = res.getString("PenaltyAVGLongName");
				}
				if (note != null && note.length() > 0) {
					fNotes.add(note);
					fin = fin + "<sup>(" + fNotes.size() + ")</sup>";
				}

				addTableCell(pw, fin, "center", SERIES_CELLCLASS);
			}
		}
		addTableCell(pw, SeriesPoints.format(sp), "right", SERIES_CELLCLASS);
		if (posOnRight) addTableCell(pw, posString, "center", SERIES_CELLCLASS);
		pw.println("</tr>");
	}

	protected RacePointsList getRacePointsForRace(Race race) {
		RacePointsList rpl = fRegatta.getScoringManager().getRacePointsList().findAll(race);
		return rpl;
	}

	abstract protected RacePoints getRacePointsForRow(SeriesPoints sp, RacePointsList rpl, AbstractDivision div, Race race);

	@Override protected void initializeNotes() {
		super.initializeNotes();
		int startr = 0;
		if (fOptions.isShowLastXRaces()) {
			startr = fRegatta.getNumRaces() - fOptions.getLastXRaces();
			if (startr < 0) startr = 0;
			if (startr > 0) {
				fNotes.add(MessageFormat.format(res.getString("ReportLabelNoteMaxNRaces"), new Object[] { new Integer(
						fOptions.getLastXRaces()) }));
			}
		}
	}
	
	protected List<String> getScoringNotes(RacePointsList allPoints) {
		return fRegatta.getScoringManager().getSeriesScoringNotes(allPoints);
	}

	protected void postFinalNotes(RacePointsList allPoints) {
		fNotes.addAll( getScoringNotes(allPoints));

		boolean hasThrowouts = false;
		for (RacePoints p : allPoints) if (p.isThrowout()) hasThrowouts = true;
		if (hasThrowouts) {
			fNotes.add(res.getString("ScoringNotesThrowout"));
		}
	}
	
	abstract void generateBodySeries(List<String> linkList, PrintWriter pw2);

}