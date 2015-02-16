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
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;

/**
 * Generates a report of the series standings
 **/
public class ActionReportSeriesStandingsSingleStage extends ActionReport {
	public static String TABNAME = "Series";

	@Override public String getTabName() {
		return TABNAME;
	}

	private static ReportOptions sReportOptions;

	public static ReportOptions getDefaultReportOptions() {
		if (sReportOptions == null) {
			sReportOptions = new ReportOptions(TABNAME);
			sReportOptions.setOptionLocationValues(
			// Skip, Crew, Boat, Club, mna, rsa, rating, full rating
					new String[] { "2.1", "none", "1.1", "none", "none", "none", "none", "none" });
		}
		return sReportOptions;
	}

	@Override public String getReportName() {
		Regatta reg = fRegatta;
		if (reg == null) reg = JavaScoreProperties.getRegatta();
		if ((reg == null) || (reg.getNumRaces() == 1)) {
			return res.getString("ReportSeriesTitleSingular");
		} else {
			return java.text.MessageFormat.format(res.getString("ReportSeriesTitlePlural"), new Object[] { new Integer(
					reg.getNumRaces()) });
		}
	}

	private List<String> fNotes;

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

	private void generateBodySeries(List<String> linkList, PrintWriter pw2) {
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

		fNotes = new ArrayList<String>();
		int startr = 0;
		if (fOptions.isShowLastXRaces()) {
			startr = fRegatta.getNumRaces() - fOptions.getLastXRaces();
			if (startr < 0) startr = 0;
			if (startr > 0) {
				fNotes.add(MessageFormat.format(res.getString("ReportLabelNoteMaxNRaces"), new Object[] { new Integer(
						fOptions.getLastXRaces()) }));
			}
		}

		pw.println("<table class=" + SERIES_TABLECLASS + ">");

		// init header row
		boolean doRaceLinks = true;

		pw.print("<thead><tr>");
		addTableCell(pw, res.getString("ColHeadPos"), "center");
		int nCols = generateDescriptiveHeaders(pw);

		// figure out which races to display
		boolean[] showThisRace = new boolean[fRegatta.getNumRaces()];
		for (int i = startr; i < fRegatta.getNumRaces(); i++) {
			Race r = fRegatta.getRaceIndex(i);
			if ( div.isRacing(r)) {
				showThisRace[i] = true;
				nCols++;
			}
			// showThisRace[i] = true;
		}

		String regattaBaseFileName = fRegatta.getBaseRegattaName();

		// generate the the race column headers
		for (int i = startr; i < fRegatta.getNumRaces(); i++) {
			if (showThisRace[i]) {
				Race r = fRegatta.getRaceIndex(i);

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
					sb.append(i + 1);
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
		}
		addTableCell(pw, res.getString("ColHeadTotalPoints"), "center");
		if (posOnRight) addTableCell(pw, res.getString("ColHeadPos"), "center");
		pw.println("");
		pw.println("</tr></thead>");
		pw.println("<tbody>");

		// loop thru entries add them in
		RacePointsList allPoints = new RacePointsList();

		String posString;

		SubDivision lastSubDiv = null;
		SeriesPointsList seriesPoints = fRegatta.getScoringManager().getAllSeriesPoints().findAll(div);
		seriesPoints.sortPosition();

		for (int e = 0; e < seriesPoints.size(); e++) {
			SeriesPoints sp = seriesPoints.get(e);

			posString = Long.toString(sp.getPosition());
			if (sp.isTied()) posString = posString + "T";

			pw.print("<tr>");
			addTableCell(pw, posString, "center", SERIES_CELLCLASS);
			generateDescriptiveCells(pw, sp.getEntry());

			// loop thru races display points for each race
			for (int r = startr; r < fRegatta.getNumRaces(); r++) {
				if (showThisRace[r]) {
					Race race = fRegatta.getRaceIndex(r);
					RacePointsList rpl = fRegatta.getScoringManager().getRacePointsList().findAll(race);
					allPoints.addAll(rpl);

					RacePoints racepts = null;

					racepts = rpl.find(race, sp.getEntry(), div);
		

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
			}
			addTableCell(pw, SeriesPoints.format(sp), "right", SERIES_CELLCLASS);
			if (posOnRight) addTableCell(pw, posString, "center", SERIES_CELLCLASS);
			pw.println("</tr>");
		}
		pw.println("</tbody></table>");

		fNotes.addAll(fRegatta.getScoringManager().getSeriesScoringNotes(allPoints));

		boolean hasThrowouts = false;
		for (RacePoints p : allPoints) if (p.isThrowout()) hasThrowouts = true;
		if (hasThrowouts) {
			fNotes.add(res.getString("ScoringNotesThrowout"));
		}

		formatNotes(pw, fNotes);
	}

}
/**
 * $Log: ActionReportSeriesStandings.java,v $ Revision 1.10 2006/09/03 02:12:30 sandyg fixes Jeff Borland's Snipe bug...
 * subdivision race points are reported wrong
 * 
 * Revision 1.9 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.8 2006/04/15 23:39:23 sandyg report tweaking for Miami OCR, division splits
 * 
 * Revision 1.7 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.6 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.4 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.3 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/08 04:18:33 sandyg Fixed reporting error on finals divisions, cleaned up gui on qual/final races
 * (hiding divisions that should not have their "participating" flags changed)
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.13.4.3 2006/01/01 01:54:11 sandyg qual fleet fixing
 * 
 * Revision 1.13.4.2 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.13.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.13 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.12 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.11 2003/05/07 01:18:18 sandyg bold headings made consistent, fleet/division scoring problem fixed,
 * sail/bow header cleaned up
 * 
 * Revision 1.10 2003/03/28 02:00:53 sandyg Bug #71150, Feature request 613855 - added real corrected time and pos # to
 * one race report, right hand pos # to series report
 * 
 * Revision 1.9 2003/01/04 17:33:06 sandyg Prefix/suffix overhaul
 * 
 */
