// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportOneRace.java,v 1.6 2006/04/15 23:39:23 sandyg Exp $
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
import java.util.ArrayList;
import java.util.List;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.ScoringUtilities;

/**
 * Generates a report of start times for a pursuit race
 **/
public class ActionReportPursuitStartTimes extends ActionReport implements Constants {
	private Race fRace;
	private static ReportOptions sReportOptions;

	// uses the single race report options did NOT add a pursuit tab
	public static String TABNAME = "Race";

	@Override public String getTabName() {
		return TABNAME;
	}

	@Override public String getReportName() {
		return java.text.MessageFormat.format(res.getString("ColHeadRaceName"), new Object[] { ((fRace == null) ? ""
				: fRace.getName()) });
	}

	public static ReportOptions getDefaultReportOptions() {
		if (sReportOptions == null) {
			sReportOptions = new ReportOptions(TABNAME);
			sReportOptions.setOptionLocationValues(
			// Skip, Crew, Boat, Club, mna, rsa, rating, fullrating
					new String[] { "2.1", "none", "1.1", "none", "none", "none", "3.1", "none" });
		}
		return sReportOptions;
	}

	/**
	 * Wraps the generation of a report for a regatta Handles centralized file creation
	 * 
	 * @param dir
	 *            directory in which to store report
	 * @param filename
	 *            name of the report file
	 * @param regatta
	 *            regatta to be reported
	 * @param obj
	 *            the race to be reported
	 * @throws java.io.IOException
	 */
	@Override public void createReportFile(String dir, String filename, Regatta regatta, Object obj) throws java.io.IOException {
		fRace = (Race) obj;
		super.createReportFile(dir, filename, regatta, obj);
	}

	@Override public void generateBody(PrintWriter pw, Object obj) {
		fRace = (Race) obj;
		if (fRace == null) return;

		ReportOptions.reportingForRace = fRace;

		if (fRace.getComment().length() > 0) {
			pw.println("<P class=" + COMMENTS_PCLASS + ">");
			pw.println(fRace.getComment());
			pw.println("</p>");
		}

		Regatta regatta = fRace.getRegatta();
		SailTime.setLongDistance(fRace.isLongDistance());

		// need to come back and insert headers here, so put rest of
		// report out to stringwriter, then append at end
		StringWriter sw = new StringWriter(2048);

		List<String> linkList = generateBody(regatta, new PrintWriter(sw));

		try {
			sw.close();
		} catch (java.io.IOException e) {} // not likely

		generateDivisionLinks(pw, linkList);

		pw.print(sw.toString());

		ReportOptions.reportingForRace = null;

	}

	public static final String SUBDIV_DELIMITER = " & ";
	/**
	 * generates report divs, subdivs and fleets in a normal regatta, sends report body to the PrintWriter and returns a
	 * list of links to put into the report's header
	 * 
	 * @param regatta
	 * @param linkList
	 * @param pw2
	 * @return
	 */
	private List<String> generateBody(Regatta regatta, PrintWriter writer) {
		List<String> linkList = new ArrayList<String>(10);

		// first do individual divisions such as they are...
		DivisionList divList = new DivisionList();
		for (Division div : fRegatta.getDivisions()) {
			boolean hasdiv = (div.getNumEntries() > 0);
			if ( div.isRacing(fRace) && hasdiv) {
				divList.add(div);
				linkList.add(div.getName());
			}
		}

		for (Division div : divList) {
			int n = div.getNumEntries(); // regatta.getAllEntries().findAll(div).size();
			if (n > 0 && div.isRacing(fRace)) {
				generateDivisionHeader(writer, div.getName(), n, DIVISION);
				reportForDivision(writer, div);
			}
		}
		return linkList;
	}

	/**
	 * creates a set of subfleets incorporating the sametimesameclass concept Used to group a set of finishers for
	 * scoring
	 * 
	 * @param r
	 *            the Race on which to group the divisions
	 * @return a List of subfleets, each element in the list is itself a list of divisions that should be scored
	 *         together
	 */
	public List<List<AbstractDivision>> getSubFleets(Race r) {
		List<List<AbstractDivision>> subFleets = new ArrayList<List<AbstractDivision>>();

		List<AbstractDivision> fleetDivs = r.getStartingDivisions(true);

		// until this is empty run the loop
		while (fleetDivs.size() > 0) {
			List<AbstractDivision> minifleet = new ArrayList<AbstractDivision>();
			subFleets.add(minifleet);

			AbstractDivision div = fleetDivs.get(0);
			long starttime = r.getStartTimeAdjusted(div);
			minifleet.add(div);
			fleetDivs.remove(div);

			for (int d = 0; d < fleetDivs.size(); d++) {
				div = fleetDivs.get(d);
				if (starttime == r.getStartTimeAdjusted(div)) {
					minifleet.add(div);
					// fleetDivs.remove( div);
				}
			}
			fleetDivs.removeAll(minifleet);
		}
		return subFleets;
	}

	public void reportForDivision(PrintWriter pw, AbstractDivision div) {
		String divName = div.getName();
		boolean oneD = div.isOneDesign();

		EntryList entries = div.getEntries();
		
		// make temp finishes of all entries, in order to sort on start time
		FinishList startOrder = new FinishList();
		for ( Entry e : entries) {
			Finish f = new Finish( fRace, e);
			startOrder.add(f);
		}
		startOrder.sortPursuitStartTime();
		
		pw.println("<table class=" + RACE_TABLECLASS + ">");

		// init header row
		pw.print("<thead><tr>");
		addTableCell(pw, res.getString("ColHeadPursuitStartOrder"), "center");
		generateDescriptiveHeaders(pw);

		addTableCell(pw, res.getString("ColHeadPursuitStartTime"), "center");
		
		pw.println("</tr></thead>");

		// loop thru entries add them in
		int posNum = 1;
		for (Finish fin : startOrder) {
			Entry e = fin.getEntry();
		
			pw.print("  <tr>");

			String posString = Integer.toString(posNum);
			posNum++;
			
			addTableCell(pw, posString + "&nbsp;&nbsp;", "center");

			generateDescriptiveCells(pw, e);

			long startTime = fin.getRace().getStartTimeAdjusted( e.getDivision(), e);
			addTableCell(pw, SailTime.toString( startTime), "center");

			pw.println("</tr>");
		}
		pw.println("</table>");

		fNotes.addAll(ScoringUtilities.getRaceScoringNotes(fRace, div));
		fNotes.add( formatPursuitNote(fRace, div));
	}

}
