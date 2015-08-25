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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.StartingDivisionList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.RegattaScoringModel;
import org.gromurph.javascore.model.scoring.ScoringUtilities;

/**
 * Generates a report of the series standings
 **/
public class ActionReportOneRace extends ActionReport implements Constants {
	private Race fRace;
	private static ReportOptions sReportOptions;

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

		List<String> linkList = null;
		if (regatta.isMultistage()) {
			linkList = generateBodyMultistage(regatta, new PrintWriter(sw));
		} else {
			linkList = generateBodySingleStage(regatta, new PrintWriter(sw));
		}

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
	private List<String> generateBodySingleStage(Regatta regatta, PrintWriter writer) {
		List<String> linkList = new ArrayList<String>(10);

		// first do inidividual divisions such as they are...
		DivisionList divList = new DivisionList();
		for (Division div : fRegatta.getDivisions()) {
			boolean hasdiv = (div.getNumEntries() > 0);
			if ( div.isRacing(fRace) && hasdiv) {
				RacePointsList racePoints = regatta.getScoringManager().getRacePointsList().findAll(fRace);
				racePoints = racePoints.findAll(div);
				if (racePoints.size() > 0) {
					divList.add(div);
					linkList.add(div.getName());
				}
			}
		}

		for (Division div : divList) {
			int n = div.getNumEntries(); // regatta.getAllEntries().findAll(div).size();
			if (n > 0 && div.isRacing(fRace)) {
				generateDivisionHeader(writer, div.getName(), n, DIVISION);
				RacePointsList racePoints = regatta.getScoringManager().getRacePointsList().findAll(fRace).findAll(div);
				reportForDivision(writer, div, racePoints);
			}
		}

		// now show the fleets...
		for (Fleet fleet : fRegatta.getFleets()) {
			if (fleet.isRacing(fRace)) {
				if (!fleet.isSameStartSameClass()) {
					RacePointsList racePoints = regatta.getScoringManager().getRacePointsList().findAll(fRace).findAll(
							fleet);
					racePoints.sortPointsPositionRounding();

					int n = racePoints.size();
					if (n > 0) {
						generateDivisionHeader(writer, fleet.getName(), n, FLEET);
						reportForDivision(writer, fleet, racePoints);
						linkList.add(fleet.getName());
					}
				} else {
					List<List<AbstractDivision>> miniFleet = fleet.getSubFleets(fRace);
					RacePointsList fleetPoints = regatta.getScoringManager().getRacePointsList().findAll(fRace)
							.findAll(fleet);
					for (Iterator<List<AbstractDivision>> mi = miniFleet.iterator(); mi.hasNext();) {
						List<AbstractDivision> mf = mi.next();
						StringBuffer sbDivs = new StringBuffer();
						RacePointsList racePoints = new RacePointsList();

						for (Iterator<AbstractDivision> iDiv = mf.iterator(); iDiv.hasNext();) {
							Division div = (Division) iDiv.next();
							sbDivs.append(div.getName());
							if (iDiv.hasNext()) sbDivs.append(SUBDIV_DELIMITER);
							racePoints.addAll(fleetPoints.findAllEntered(div));
						}
						racePoints.sortPointsPositionRounding();

						int n = racePoints.size();
						if (n > 0) {
							String comboName = sbDivs.toString();
							generateDivisionHeader(writer, comboName, n, FLEET);
							reportForDivision(writer, racePoints, comboName, fleet.isOneDesign(), true);
							linkList.add(comboName);
						}

					}
				}
			}
		}

		// now do subdivisions
		for (SubDivision div : fRegatta.getSubDivisions()) {
			int n = div.getNumEntries();
			if (n > 0 && div.isRacing(fRace)) {
				generateDivisionHeader(writer, div.getName(), n, SUBDIVISION);
				RacePointsList racePoints = regatta.getScoringManager().getRacePointsList().findAll(fRace).findAll(div);
				reportForDivision(writer, div, racePoints);
				linkList.add(div.getName());
			}
		}

		return linkList;
	}

	private List<String> generateBodyMultistage(Regatta regatta, PrintWriter writer) {
		List<String> linkList = new ArrayList<String>(10);
		RegattaScoringModel mgr = regatta.getScoringManager();
		
		boolean isFinals = true;
		StartingDivisionList racingDivs = fRace.getStartingDivisions(false);
		for ( AbstractDivision d : racingDivs) {
			if (d.isGroupQualifying()) isFinals = false;
		}

		if (isFinals) {
			// want to show the finals subdivisions only
			RacePointsList racePoints = mgr.getRacePointsList().findAll(fRace);
			for (AbstractDivision subdiv : racingDivs) {
				RacePointsList subPoints = new RacePointsList();
				subPoints.addAll(racePoints.findAllEntered(subdiv));

				if (subPoints.size() > 0) {
					subPoints.sortPointsPositionRounding();
					generateDivisionHeader(writer, subdiv.getName(), subPoints.size(), SUBDIVISION);
					reportForDivision(writer, subdiv, subPoints);
					linkList.add(subdiv.getName());
				}
			}
		} else if (fRegatta.getNumQualifyingDivisions() > 0) {
			// want to show the whole division, but grouped by minifleets of
			// subdivs racing together

			List<AbstractDivision> doneDivs = new ArrayList<AbstractDivision>(10);

			for (SubDivision subdiv : regatta.getSubDivisions()) {
				if (subdiv.isRacing(fRace) && !doneDivs.contains(subdiv)) {
					List<List<AbstractDivision>> miniFleet = getSubFleets(fRace);

					RacePointsList divPoints = regatta.getScoringManager().getRacePointsList().findAll(fRace);
					for (Iterator<List<AbstractDivision>> mi = miniFleet.iterator(); mi.hasNext();) {
						List<AbstractDivision> mf = mi.next();
						StringBuffer sbDivs = new StringBuffer();
						RacePointsList racePoints = new RacePointsList();

						for (Iterator<AbstractDivision> iDiv = mf.iterator(); iDiv.hasNext();) {
							AbstractDivision div = iDiv.next();
							sbDivs.append(div.getName());
							if (iDiv.hasNext()) sbDivs.append(SUBDIV_DELIMITER);
							racePoints.addAll(divPoints.findAllEntered(div));
							doneDivs.add(div);
						}
						racePoints.sortPointsPositionRounding();

						int n = racePoints.size();
						if (n > 0) {
							String comboName = sbDivs.toString();
							generateDivisionHeader(writer, comboName, n, DIVISION);
							reportForDivision(writer, racePoints, comboName, true, true);
							linkList.add(comboName);
						}

					}
				}
			}
		} else {
			// no subdivisions, show whole division
			for (Division div : fRegatta.getDivisions()) {
				if (div.isRacing(fRace)) {
					RacePointsList divPoints = regatta.getScoringManager().getRacePointsList().findAll(fRace);
					divPoints.sortPointsPositionRounding();

					int n = divPoints.size();
					if (n > 0) {
						generateDivisionHeader(writer, div.getName(), n, DIVISION);
						reportForDivision(writer, div, divPoints);
						linkList.add(div.getName());
					}

				}
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

	public void reportForDivision(PrintWriter pw, AbstractDivision div, RacePointsList racePoints) {
		String divName = div.getName();
		boolean oneD = div.isOneDesign();
		boolean isSubDiv = div instanceof SubDivision;
		reportForDivision(pw, racePoints, divName, oneD, isSubDiv);
	}

	public void reportForDivision(PrintWriter pw, RacePointsList racePoints, String divName, boolean is1D,
			boolean isSubDiv) {
		racePoints.sortPoints();

		// performance thing... go thru the map iterators once, make an array
		// of all the roundings then use it later on.
		String[][] roundings = new String[0][0];
		String[] roundingNames = new String[0];

		if (fRace.haveRoundings()) {
			Map<String,FinishList> allRoundings = fRace.getAllRoundings();
			roundings = new String[racePoints.size()][allRoundings.size()];
			roundingNames = new String[allRoundings.size()];
			int m = 0;
			for (Iterator<String> mIter = allRoundings.keySet().iterator(); mIter.hasNext();) {
				String markName = mIter.next();
				roundingNames[m] = markName;
				FinishList marks = fRace.getRoundings(markName);

				if (marks.size() > 0) {
					// loop thru entries add them in
					for (int e = 0; e < racePoints.size(); e++) {
						RacePoints rp = racePoints.get(e);
						Finish f = marks.findEntry(rp.getEntry());
						if (f != null) {
							roundings[e][m] = f.getFinishPosition().toString();
						}
					}
				}
				m++;
			}
		}

		pw.println("<table class=" + RACE_TABLECLASS + ">");

		// init header row
		pw.print("<thead><tr>");
		addTableCell(pw, res.getString("ColHeadPos"), "center");
		generateDescriptiveHeaders(pw);

		if (fRace.haveRoundings()) {
			for (int m = 0; m < roundingNames.length; m++) {
				StringBuffer sb = new StringBuffer(64);
				sb.append((roundingNames[m] == null || roundingNames[m].length() == 0) ? "&nbsp;" : roundingNames[m]);
				addTableCell(pw, sb.toString(), "center");
			}
		}

		if (is1D) {

			if (isSubDiv) {
				addTableCell(pw, res.getString("ColHeadSubDivFinishOrder"), "center");
				addTableCell(pw, res.getString("ColHeadDivFinishOrder"), "center");
			} else {
				addTableCell(pw, res.getString("ColHeadFinishOrder"), "center");
			}

			if (fOptions.isIncludeOneDesignTimes()) {
				addTableCell(pw, res.getString("ColHeadFinishTime"), "center");
			}

		} else {
			addTableCell(pw, res.getString("ColHeadFinishOrder"), "center");
			addTableCell(pw, res.getString("ColHeadFinishTime"), "center");
			addTableCell(pw, res.getString("ColHeadTimeAllowance"), "center");
			addTableCell(pw, res.getString("ColHeadCorrectedTime"), "center");
			addTableCell(pw, res.getString("ColHeadTimeBehind"), "center");
		}

		StringBuffer sb = new StringBuffer(64);
		// sb.append( "<u>");
		sb.append(res.getString("ColHeadAdjustments"));
		// sb.append( "</u>");
		addTableCell(pw, sb.toString(), "center");

		sb = new StringBuffer(64);
		sb.append(res.getString("GenPts"));
		addTableCell(pw, sb.toString(), "right");
		pw.println("</tr></thead>");

		long firstTime = SailTime.NOTIME;

		RacePoints rplast = null;
		RacePoints rpnext = null;
		RacePoints rp = null;

		// loop thru entries add them in
		int posNum = 1;
		for (int e = 0; e < racePoints.size(); e++) {
			if (e > 0) rplast = rp;
			if (e < racePoints.size() - 1) {
				rpnext = racePoints.get(e + 1);
			} else {
				rpnext = null;
			}

			rp = racePoints.get(e);
			if (e == 0) firstTime = rp.getFinish().getCorrectedTime();

			pw.print("  <tr>");

			String posString = "";
			if (rp.getPosition() > Constants.HIGHEST_FINISH) {
				posString = rp.getFinish().getPenalty().toString();
			} else if ((rplast != null && rp.isTiedPoints(rplast)) || (rpnext != null && rp.isTiedPoints(rpnext))) {
				posString = Integer.toString(posNum) + "T";
			} else {
				posNum = e + 1;
				posString = Integer.toString(posNum);
				posNum++;
			}
			addTableCell(pw, posString + "&nbsp;&nbsp;", "center");

			generateDescriptiveCells(pw, rp.getEntry());

			if (fRace.haveRoundings()) {
				for (int m = 0; m < roundingNames.length; m++) {
					if (roundings[e][m] != null) {
						addTableCell(pw, roundings[e][m]);
					} else {
						addTableCell(pw, "&nbsp;", "right");
					}
				}
			}

			Finish fin = rp.getFinish();
			String finString = "";

			if (rp.getClassFinishPosition() != null) {
				finString = rp.getClassFinishPosition().toString();
			}
			addTableCell(pw, finString, "center");

			if (is1D) {
				if (isSubDiv) {
					addTableCell(pw, fin.getFinishPosition().toString(), "center");
				}

				if (fOptions.isIncludeOneDesignTimes()) {
					addTableCell(pw, SailTime.toString(fin.getFinishTime()), "center");
				}

			} else {
				addTableCell(pw, SailTime.toString(fin.getFinishTime()), "center");
				addTableCell(pw, SailTime.toString(fin.getTimeAllowance()), "right");

				if (fin.getPenalty().isFinishPenalty() || fin.getCorrectedTime() == SailTime.NOTIME) {
					addTableCell(pw, SailTime.NOTIME_STRING, "right");
					addTableCell(pw, SailTime.NOTIME_STRING, "right");
				} else {
					addTableCell(pw, SailTime.toString(fin.getCorrectedTime()), "right");
					addTableCell(pw, SailTime.toString(fin.getCorrectedTime() - firstTime), "right");
				}
			}

			// adjustments column, gets penalty and subdivision addon (if any)

			// first penalty
			String adjustment = null;
			if (fin.getPenalty().getPenalty() != NOFINISH) {
				adjustment = fin.getPenalty().toString();
				String note = fin.getPenalty().getNote();
				if (note != null && note.length() > 0) {
					fNotes.add(note);
					adjustment = adjustment + "<sup>(" + fNotes.size() + ")</sup>";
				}
			}
			if (fin.getPenalty().hasPenalty(AVG)) {
				adjustment = "RDG";
				fin.getPenalty().toString();
				String note = fin.getPenalty().getNote();
				if (note != null && note.length() > 0) {
					fNotes.add(note);
					adjustment = adjustment + "<sup>(" + fNotes.size() + ")</sup>";
				} else {
					fNotes.add(res.getString("PenaltyAVGLongName"));
					adjustment = adjustment + "<sup>(" + fNotes.size() + ")</sup>";
				}
			}

			// next the addon

			// add in the subdivision addon if applicable
			double addon = 0;
			for (SubDivision sd : fRace.getRegatta().getSubDivisions()) {
				if (sd.contains(rp.getEntry())) {
					addon += sd.getRaceAddon();
				}
			}

			if (addon == 0) {
				if (adjustment == null) adjustment = "&nbsp;";
			} else if (adjustment == null) {
				adjustment = Double.toString(addon);
			} else {
				adjustment = adjustment + "+" + Double.toString(addon);
			}
			addTableCell(pw, adjustment, "center");

			if (fin.getPenalty().hasPenalty(NOFINISH)) {
				addTableCell(pw, FinishPosition.toString(NOFINISH, false), "right");
			} else {
				String ff = Double.toString(rp.getPoints());
				if ((e > 0 && rp.isTiedPoints(racePoints.get(e - 1)))
						|| (e < racePoints.size() - 1 && rp.isTiedPoints(racePoints.get(e + 1)))) {
					ff = ff + "T";
				}
				addTableCell(pw, ff, "right");
			}

			pw.println("</tr>");
		}
		pw.println("</table>");

		if (fRace.isNonDiscardable()) fNotes.add(formatNonDiscardableNote(fRace));
		if (fRace.getWeight() != 1.00) fNotes.add(formatWeightedNote(fRace));

		fNotes.addAll(ScoringUtilities.getRaceScoringNotes(racePoints));
	}

}
/**
 * $Log: ActionReportOneRace.java,v $ Revision 1.6 2006/04/15 23:39:23 sandyg report tweaking for Miami OCR, division
 * splits
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.21.4.2 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.21.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.21.2.1 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121, 1226607, killed Java Web Start startup
 * code
 * 
 * Revision 1.21 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.20 2004/04/10 22:20:44 sandyg Fixed bug 894886, handicap scoring (actually was bug in comparing for tied
 * boats
 * 
 * Revision 1.19 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.18 2003/11/27 02:45:08 sandyg Fixed 1d tied boats (with same time) also minor reporting oddness on
 * positions with tied boats. Bug 836458
 * 
 * Revision 1.17 2003/11/23 20:34:52 sandyg starting release 4.2, minor cleanup
 * 
 * Revision 1.16 2003/05/18 03:12:38 sandyg fixed bug in Alphabet soup single race position numbers and "ties"
 * 
 * Revision 1.15 2003/05/07 01:18:18 sandyg bold headings made consistent, fleet/division scoring problem fixed,
 * sail/bow header cleaned up
 * 
 * Revision 1.14 2003/04/27 21:35:31 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.13 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.12 2003/04/20 15:43:53 sandyg added javascore.Constants to consolidate penalty defs, and added new
 * penaltys TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.11 2003/03/30 00:04:47 sandyg added comments field
 * 
 * Revision 1.10 2003/03/28 02:00:53 sandyg Bug #71150, Feature request 613855 - added real corrected time and pos # to
 * one race report, right hand pos # to series report
 * 
 * Revision 1.9 2003/03/16 20:38:21 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.8 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
