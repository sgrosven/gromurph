// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportRawFinish.java,v 1.5 2006/04/15 23:39:23 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import org.gromurph.javascore.*;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;

import java.util.*;
import java.io.PrintWriter;

/**
 * Generates a report of the series standings
 **/
public class ActionReportRawFinish extends ActionReport implements Constants {

	public static String TABNAME = "Proofing";

	@Override public String getTabName() {
		return TABNAME;
	}

	@Override public String getReportName() {
		return java.text.MessageFormat.format(res.getString("ReportRawFinishTitle"),
				new Object[] { ((fRace == null) ? "" : fRace.getName()) });
	}

	private static ReportOptions sReportOptions;

	public static ReportOptions getDefaultReportOptions() {
		if (sReportOptions == null) {
			sReportOptions = new ReportOptions(TABNAME);

			sReportOptions.setOptionLocationValues(
			//             Skip,  Crew,   Boat,  Club,   mna,    rsa,    rating, full rating, div, subdiv
					new String[] { "2.2", "none", "2.1", "none", "none", "none", "none", "1.1", "3.1", "3.2" });
		}
		return sReportOptions;
	}

	private Race fRace;

	/**
	 * Wraps the generation of a report for a regatta Handles centralized file creation
	 */
	@Override public void createReportFile(String dir, String filename, Regatta regatta, Object obj) throws java.io.IOException {
		fRace = (Race) obj;
		super.createReportFile(dir, filename, regatta, obj);
	}

	//    int eFlag = Entry.SHOW_BOAT | Entry.SHOW_BOW | Entry.SHOW_SKIPPER |
	//        Entry.SHOW_CREW;

	@Override public void generateBody(PrintWriter pw, Object obj) {
		fRace = (Race) obj;
		ReportOptions.reportingForRace = fRace;

		SailTime.setLongDistance(fRace.isLongDistance());

		//finList.sortPosition(); done in getallfinishers

		pw.println("<table class=" + RAWFINISH_TABLECLASS + ">");

		// init header row
		pw.print("<thead><tr>");

		addTableCell(pw, res.getString("ColHeadRecordedFinishOrder"), "center");

		generateDescriptiveHeaders(pw, 0, 0);

		addTableCell(pw, res.getString("ColHeadFinishTime"), "center");
		addTableCell(pw, res.getString("GenPenalty"), "center");

		generateDescriptiveHeaders(pw, 1);
		pw.println("</tr></thead>");

		// loop thru entries add them in
		for (Iterator f = fRace.finishers(); f.hasNext();) {
			Finish fin = (Finish) f.next();

			pw.print("  <tr>");

			addTableCell(pw, fin.getFinishPosition().toString(), "center");

			generateDescriptiveCells(pw, fin.getEntry(), 0, 0);
			addTableCell(pw, SailTime.toString(fin.getFinishTime()), "center");

			String pen = "&nbsp;";
			if (fin.getPenalty().getPenalty() != Constants.NO_PENALTY) {
				pen = fin.getPenalty().toString();
			}
			addTableCell(pw, pen, "center");

			generateDescriptiveCells(pw, fin.getEntry(), 1);

			pw.println("</tr>");
		}
		pw.println("</table>");
		ReportOptions.reportingForRace = null;

	}

}
/**
 * $Log: ActionReportRawFinish.java,v $ Revision 1.5 2006/04/15 23:39:23 sandyg report tweaking for Miami OCR, division
 * splits
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.10 2003/05/07 01:18:18 sandyg bold headings made consistent, fleet/division scoring problem fixed,
 * sail/bow header cleaned up
 * 
 * Revision 1.9 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.8 2003/04/20 15:43:53 sandyg added javascore.Constants to consolidate penalty defs, and added new penaltys
 * TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.7 2003/01/04 17:33:06 sandyg Prefix/suffix overhaul
 * 
 */
