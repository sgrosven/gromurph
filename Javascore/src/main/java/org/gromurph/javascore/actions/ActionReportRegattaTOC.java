//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportRegattaTOC.java,v 1.7 2006/04/15 23:39:23 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.io.PrintWriter;
import java.text.MessageFormat;

import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.ReportOptions;

/**
 * Generates a report of the series standings
 **/
public class ActionReportRegattaTOC extends ActionReport {
	@Override public String getTabName() {
		return null;
	}

	@Override public String getReportName() {
		return res.getString( "ReportRegattaTocTitle");
	}

	private static ReportOptions sReportOptions;

	public static ReportOptions getDefaultReportOptions() {
		if ( sReportOptions == null) {
			sReportOptions = new ReportOptions( "RegattaReports");
		}
		return sReportOptions;
	}

	@Override public void generateBody( PrintWriter pw, Object obj) {
		if ( obj != null) {
			pw.print( "<b>");
			pw.print( MessageFormat.format( res.getString( "RegattaTOCLabelWarning"), new Object[] { obj.toString() }));
			pw.println( "</b>");
		}

		String regattaBaseFileName = fRegatta.getBaseRegattaName();

		if ( fRegatta.getNumRaces() > 0) {
			if ( fRegatta.getComment() != null && fRegatta.getComment().trim().length() > 0) {
				pw.println( "<p class=" + COMMENTS_PCLASS + ">");
				pw.print( fRegatta.getComment());
				pw.println( "</p><br>");
			}

			pw.print( "<a href=\"");
			pw.print( regattaBaseFileName);
			pw.print( ".html\">");
			pw.print( res.getString( "RegattaTOCLabelRegattaStandings"));
			pw.print( "</a>");
			
			// title info row
			if ( fRegatta.getNumRaces() == 1) {
				pw.println( res.getString( "RegattaTOCLabel1RaceScored"));
			} else {
				pw.println( MessageFormat.format( res.getString( "RegattaTOCLabelRacesScored"), new Object[] { new Integer(
						fRegatta.getNumRaces()) }));
			}

			pw.println( "<P><table class=" + TOC_TABLECLASS + ">");
			
			// header row
			pw.println( "<tr>");
			
			pw.println( "<td align=\"center\">");
			pw.print( res.getString( "RegattaTOCLabelIndividualRaceResults"));
			pw.println( "</td>");
			
			pw.print( "<td align=\"center\">");
			pw.println( res.getString( "RegattaTOCLabelProofingResults"));
			pw.println( "</td>");
			
			boolean hasPursuit = false;
			for ( Race r : fRegatta.getRaces()) {
				hasPursuit = hasPursuit || r.isPursuit();
			}
			
			if (hasPursuit) {
    			pw.print( "<td align=\"center\">");
    			pw.println( res.getString( "RegattaTOCLabelPursuitStartTimes"));
    			pw.println( "</td>");
			}
			pw.println( "</tr>");
			
			// body rows, one for each race
			for ( int i = 0; i < fRegatta.getNumRaces(); i++) {
				Race race = fRegatta.getRaceIndex(i);
				String num = Integer.toString( i + 1);
				
				pw.print( "<tr>");
				
				pw.print( "<td align=\"center\"><a href=\"");
				pw.print( regattaBaseFileName + "_race");
				pw.print( num);
				pw.print( ".html\">");
				pw.print( race.toString());
				pw.print( "</a></td>");
				
				pw.print( "<td align=\"center\"><a href=\"proof");
				pw.print( num);
				pw.print( ".html\">");
				pw.print( race.toString());
				pw.print( "</a></td>");
				
				if (race.isPursuit()) {
					pw.print( "<td align=\"center\"><a href=\"");
					pw.print( regattaBaseFileName + "_pursuitStartTimes");
					pw.print( num);
					pw.print( ".html\">");
					pw.print( race.toString());
					pw.print( "</a></td>");
				}
				
				pw.print( "</tr>");
			}
			pw.println( "</table><br>");
		} else {
			pw.print( res.getString( "RegattaTOCLabelNoRacesYet"));
			pw.println( "<p>");
		}

		pw.print( res.getString( "RegattaTOCLabelEntryLists"));
		pw.println( "</a><ul>");
		if ( fRegatta.isUseBowNumbers()) {
			pw.print( "<li><a href=\"scratchbybow.html\">");
			pw.print( res.getString( "RegattaTOCLabelByBowNumber"));
			pw.println( "</a></li>");
		}
		pw.print( "<li><a href=\"scratchbysail.html\">");
		pw.print( res.getString( "RegattaTOCLabelBySailNumber"));
		pw.println( "</a></li>");

		pw.print( "<li><a href=\"scratchbyboatname.html\">");
		pw.print( res.getString( "RegattaTOCLabelByBoatName"));
		pw.println( "</a></li>");

		pw.print( "<li><a href=\"scratchbyskipper.html\">");
		pw.print( res.getString( "RegattaTOCLabelBySkipper"));
		pw.println( "</a></li>");

		pw.print( "<li><a href=\"scratchbyrating.html\">");
		pw.print( res.getString( "RegattaTOCLabelByRating"));
		pw.println( "</a></li>");

		pw.println( "</ul><P>");

		pw.print( res.getString( "RegattaTOCLabelCheckin"));
		pw.print( "<br><ul>");

		pw.print( "<li><a href=\"checkinbyfleet.html\">");
		pw.print( res.getString( "RegattaTOCLabelByFleet"));
		pw.println( "</a></li>");

		pw.print( "<li><a href=\"checkinbydivision.html\">");
		pw.print( res.getString( "RegattaTOCLabelByDivision"));
		pw.println( "</a></li>");

		if ( fRegatta.isUseBowNumbers()) {
			pw.print( "<li><a href=\"checkinbydivisionbow.html\">");
			pw.print( res.getString( "RegattaTOCLabelByDivisionBow"));
			pw.println( "</a></li></ul><P>");
		}

		pw.println( "</ul><P>");

		pw.print( "<a href=\"finish.html\">");
		pw.print( res.getString( "RegattaTOCLabelFinishSheet"));
		pw.println( "</a><P>");
	}
}
/**
 * $Log: ActionReportRegattaTOC.java,v $ Revision 1.7 2006/04/15 23:39:23 sandyg
 * report tweaking for Miami OCR, division splits
 * 
 * Revision 1.6 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.10 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.9 2003/05/06 21:32:10 sandyg added checkin report by class, by bow
 * 
 * Revision 1.8 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.7 2003/01/04 17:33:06 sandyg Prefix/suffix overhaul
 * 
 */
