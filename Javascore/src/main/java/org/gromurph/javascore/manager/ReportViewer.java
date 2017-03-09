// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.manager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.actions.ActionReport;
import org.gromurph.javascore.actions.ActionReportCheckin;
import org.gromurph.javascore.actions.ActionReportFinish;
import org.gromurph.javascore.actions.ActionReportOneRace;
import org.gromurph.javascore.actions.ActionReportPursuitStartTimes;
import org.gromurph.javascore.actions.ActionReportRawFinish;
import org.gromurph.javascore.actions.ActionReportRegattaTOC;
import org.gromurph.javascore.actions.ActionReportScratch;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsAbstract;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsDailyStage;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsMultiStage;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsSingleStage;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a report of the series standings
 **/
public class ReportViewer {
	/**
	 * The single HelpBroker that manages all the JavaHelp stuff.
	 **/
	private HelpBroker fBroker = null;

	private Regatta fRegatta;

	private Regatta getRegatta() {
		return fRegatta;
	}

	public void updateReports() {
		updateReports(true);
	}

	public void updateReports(boolean doRescore) {
		fRegatta = JavaScoreProperties.getRegatta();
		String dir = getDirectory();
		long starttime = System.currentTimeMillis();
		logger.trace("Writing reports to directory: {}");

		
		try {
			if (doRescore) {
				fRegatta.scoreRegatta();
			}

			JavaScoreProperties.acquireScoringLock();
			ActionReport.clearReportErrors();

			deleteExistingReports();

			createRootFile("report.hs");
			createTocFile("toc.xml");
			createMapFile("map.jhm");

			new ActionReportRegattaTOC().createReportFile(dir, "regatta.html", fRegatta, null);

			ActionReportCheckin checkin = new ActionReportCheckin();
			checkin.setByBow(false);
			checkin.setByDivision(false);
			checkin.createReportFile(dir, "checkinbyfleet.html", fRegatta, null);

			checkin.setByDivision(true);
			checkin.createReportFile(dir, "checkinbydivision.html", fRegatta, null);

			if (fRegatta.isUseBowNumbers()) {
				checkin.setByBow(true);
				checkin.createReportFile(dir, "checkinbydivisionbow.html", fRegatta, null);
			}

			ActionReportScratch scratch = new ActionReportScratch();
			scratch.createReportFile(dir, "scratchbysail.html", fRegatta, null, Entry.SAILID_PROPERTY);
			scratch.createReportFile(dir, "scratchbyskipper.html", fRegatta, null, Entry.SKIPPER_PROPERTY);
			scratch.createReportFile(dir, "scratchbyboatname.html", fRegatta, null, Entry.BOAT_PROPERTY);
			scratch.createReportFile(dir, "scratchbyrating.html", fRegatta, null, Entry.RATING_PROPERTY);
			if (fRegatta.isUseBowNumbers())
				scratch.createReportFile(dir, "scratchbybow.html", fRegatta, null, Entry.BOW_PROPERTY);

			new ActionReportFinish().createReportFile(dir, "finish.html", fRegatta, null);

			ActionReportSeriesStandingsAbstract reporter = null;
			if (fRegatta.isDailyScoring()) {
				reporter = new ActionReportSeriesStandingsDailyStage();
			} else if (fRegatta.isMultistage()) {
				reporter = new ActionReportSeriesStandingsMultiStage();
			} else {
				reporter = new ActionReportSeriesStandingsSingleStage();
			}
			reporter.createReportFile(dir, getRegattaName() + ".html", fRegatta, null);

			ActionReportOneRace raceReport = new ActionReportOneRace();
			for (int i = 0; i < fRegatta.getNumRaces(); i++) {
				raceReport.createReportFile(dir, getRegattaName() + "_race" + Integer.toString(i + 1) + ".html",
						fRegatta, fRegatta.getRaceIndex(i));
			}

			ActionReportRawFinish proofReport = new ActionReportRawFinish();
			for (int i = 0; i < fRegatta.getNumRaces(); i++) {
				proofReport.createReportFile(dir, "proof" + Integer.toString(i + 1) + ".html", fRegatta,
						fRegatta.getRaceIndex(i));
			}
			
			ActionReportPursuitStartTimes pursuitStartReport = new ActionReportPursuitStartTimes();
			for (int i = 0; i < fRegatta.getNumRaces(); i++) {
				if (fRegatta.getRaceIndex(i).isPursuit()) {
					pursuitStartReport.createReportFile(dir, getRegattaName() + "_pursuitStartTimes" + Integer.toString(i + 1) + ".html",
    						fRegatta, fRegatta.getRaceIndex(i));
				}
			}

		} catch (Exception e) {
			Util.showError(e, true);
			try {
				new ActionReportRegattaTOC().createReportFile(dir, "regatta.html", fRegatta, e);
			} catch (Exception e2) {}
		} finally {
			JavaScoreProperties.releaseScoringLock();
		}

		// Try to load the help set with specified name
		try {
			String file = dir + "report.hs";
			HelpSet set = new HelpSet(null, new File(file).toURI().toURL());
			fBroker = set.createHelpBroker();
			fBroker.setHelpSet(set);
			fBroker.initPresentation();
		} catch (Exception e) {
			Util.printlnException(this, e, true);
		}
		logger.trace("  reports done, time elapsed {}", (System.currentTimeMillis() - starttime));
	}

	private void deleteFile( String reportName) {
		String fullName = getDirectory() + reportName;
		File fullFile = new File( fullName);
		if (fullFile.exists()) fullFile.delete();
	}
	
	private void deleteExistingReports() {
		deleteFile("report.hs");
		deleteFile("toc.xml");
		deleteFile("map.jhm");

		deleteFile( "regatta.html");

		deleteFile( "checkinbyfleet.html");
		deleteFile( "checkinbydivision.html");
		deleteFile( "checkinbydivisionbow.html");
		deleteFile( "scratchbysail.html");
		deleteFile( "scratchbyskipper.html");
		deleteFile( "scratchbyboatname.html");
		deleteFile( "scratchbyrating.html");
		deleteFile( "scratchbybow.html");
		deleteFile( "finish.html");
		deleteFile( getRegattaName() + ".html");

		for (int i = 0; i < 30; i++) {
			deleteFile( getRegattaName() + "_race" + Integer.toString(i + 1) + ".html");
			deleteFile( "proof" + Integer.toString(i + 1) + ".html");
			if (fRegatta.getNumRaces() > i && fRegatta.getRaceIndex(i).isPursuit()) {
				deleteFile( getRegattaName() + "_pursuitStartTimes" + Integer.toString(i + 1) + ".html");
			}
		}
	}

	protected Logger logger = LoggerFactory.getLogger( this.getClass());

	/**
	 * looks to see that variable fRegatta is defined, if not, tries to set it from JavaScoreProperties.getRegatta().
	 * Returns false if all fails
	 */
	private boolean regattaExists() {
		if (fRegatta == null) {
			fRegatta = JavaScoreProperties.getRegatta();
		}
		return (fRegatta != null);
	}

	private void createTocFile(String name) {
		if (!regattaExists()) return;

		try {
			FileOutputStream fos = new FileOutputStream(getDirectory() + name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, ActionReport.DEFAULT_ENCODING);
			PrintWriter writer = new PrintWriter(osw);
			//PrintWriter writer = new PrintWriter( new FileWriter( getDirectory() + name));

			writer.println("<?xml version='1.0' encoding='" + ActionReport.DEFAULT_ENCODING + "'  ?>");
			writer.println("<toc version=\"1.0\">");

			writer.print("<tocitem text=\"");
			writer.print(fRegatta.getName());
			writer.print("\" target=\"top\">");

			if (fRegatta.getNumRaces() > 0) {
				writer.println("  <tocitem text=\"Series Standings\" target=\"series\"/>");

				writer.println("  <tocitem text=\"Individual Race Results\">");

				for (int i = 0; i < fRegatta.getNumRaces(); i++) {
					writer.print("    <tocitem text=\"");
					writer.print(fRegatta.getRaceIndex(i).toString());
					writer.print("\" target=\"race");
					writer.print(Integer.toString(i + 1));
					writer.println("\"/>");
				}
				writer.println("  </tocitem>");
			}

			writer.println("  <tocitem text=\"Entry Lists\">");
			if (fRegatta.isUseBowNumbers()) writer.println("      <tocitem text=\"by Bow\" target=\"scratchbybow\"/>");
			writer.println("      <tocitem text=\"by Sail\" target=\"scratchbysail\"/>");
			writer.println("      <tocitem text=\"by Boat Name\" target=\"scratchbyboatname\"/>");
			writer.println("      <tocitem text=\"by Skipper\" target=\"scratchbyskipper\"/>");
			writer.println("  </tocitem>");

			writer.println("  <tocitem text=\"Check-in Sheets\">");
			writer.println("      <tocitem text=\"Fleet by Sail\" target=\"checkinfleetsail\"/>");
			writer.println("      <tocitem text=\"Class by Sail\" target=\"checkinclasssail\"/>");
			if (fRegatta.isUseBowNumbers())
				writer.println("      <tocitem text=\"Class by Bow\" target=\"checkinclassbow\"/>");
			writer.println("      <tocitem text=\"by Skipper\" target=\"scratchbyskipper\"/>");
			writer.println("  </tocitem>");

			if (fRegatta.getNumRaces() > 0) {
				writer.println("  <tocitem text=\"Order of Finish Proofing Reports\">");

				for (int i = 0; i < fRegatta.getNumRaces(); i++) {
					writer.print("    <tocitem text=\"");
					writer.print(fRegatta.getRaceIndex(i).toString());
					writer.print("\" target=\"proof");
					writer.print(Integer.toString(i + 1));
					writer.println("\"/>");
				}
				writer.println("  </tocitem>");
			}

			writer.println("  <tocitem text=\"Finish Sheets\" target=\"finish\"/>");

			writer.println("</tocitem>");

			writer.println("</toc>");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Util.showError(e, true);
		}
	}

	private void createRootFile(String name) {
		if (!regattaExists()) return;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(getDirectory() + name));

			writer.println("<?xml version='1.0' encoding='UTF-8' ?>");
			writer.println("");
			writer.println("<helpset version=\"1.0\">");
			writer.println("");
			writer.println("  <!-- title -->");
			writer.println("  <title>JavaScore Reports</title>");
			writer.println("");
			writer.println("  <!-- maps -->");
			writer.println("  <maps>");
			writer.println("     <homeID>top</homeID>");
			writer.println("     <mapref location=\"map.jhm\"/>");
			writer.println("  </maps>");
			writer.println("");
			writer.println("  <!-- views -->");
			writer.println("  <view>");
			writer.println("    <name>TOC</name>");
			writer.println("    <label>Table Of Contents</label>");
			writer.println("    <type>javax.help.TOCView</type>");
			writer.println("    <data>toc.xml</data>");
			writer.println("  </view>");
			writer.println("");
			writer.println("</helpset>");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			Util.showError(e, true);
		}
	}

	private void createMapFile(String name) {
		if (!regattaExists()) return;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(getDirectory() + name));

			writer.println("<?xml version='1.0' encoding='UTF-8'  ?>");
			writer.println("<map version=1.0>");

			writer.println("<mapID target=\"top\" url=\"regatta.html\" />");
			writer.println("<mapID target=\"series\" url=\"" + getRegattaName() + ".html\" />");
			writer.println("<mapID target=\"scratchbysail\" url=\"scratchbysail.html\" />");
			writer.println("<mapID target=\"scratchbybow\" url=\"scratchbybow.html\" />");
			writer.println("<mapID target=\"scratchbyboatname\" url=\"scratchbyboatname.html\" />");
			writer.println("<mapID target=\"scratchbyskipper\" url=\"scratchbyskipper.html\" />");
			writer.println("<mapID target=\"checkinfleetsail\" url=\"checkinbyfleet.html\" />");
			writer.println("<mapID target=\"checkinclasssail\" url=\"checkinbydivision.html\" />");
			if (fRegatta.isUseBowNumbers()) {
				writer.println("<mapID target=\"checkinclassbow\" url=\"checkinbydivisionbow.html\" />");
			}
			writer.println("<mapID target=\"finish\" url=\"finish.html\" />");
			for (int i = 0; i < fRegatta.getNumRaces(); i++) {
				writer.print("<mapID target=\"race");
				writer.print(Integer.toString(i + 1));
				writer.print("\" url=\"race");
				writer.print(Integer.toString(i + 1));
				writer.println(".html\" />");

				writer.print("<mapID target=\"proof");
				writer.print(Integer.toString(i + 1));
				writer.print("\" url=\"proof");
				writer.print(Integer.toString(i + 1));
				writer.println(".html\" />");
			}
			writer.println("</map>");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			Util.showError(e, true);
		}
	}

	/**
	 * returns the directory name for storing all reports. Creates the "reports/" subdirectory if necessary from the
	 * launch directory of the program
	 */
	private File getDirectoryAsFile() {
		try {
			String dir = fRegatta.getSaveDirectory() + "reports/";

			// make sure we have a reports directory
			File f = new File(dir);
			if (!f.exists()) {
				// no reports directory, create it
				f.mkdir();
			} else if (!f.isDirectory()) {
				// reports exists but is not a directory - brutally rename it
				f.renameTo(new File(dir + ".bak"));
				f = new File(dir);
				f.mkdir();
			}

			dir = dir + getRegattaName() + "/";

			f = new File(dir);
			if (f.isFile()) {
				// clobber the new directory rudely
				f.delete();
				f.mkdir();
			} else if (!f.isDirectory()) {
				f.mkdir();
			}

			return f;
		} catch (Exception e) {
			Util.showError(e, true);
			return null;
		}
	}

	private String getDirectory() {
		String xx = getDirectoryAsFile().toString() + "/";
		return xx;
	}

	private String getRegattaName() {
		return fRegatta.getBaseRegattaName();
	}

	public File getRegattaReportFile() {
		File dir = new File(getDirectory());
		File r = new File(dir, "regatta.html");
		return r;
	}

	public URI getRegattaReportUri() {
		try {
			File dir = new File(getDirectory());
			File r = new File(dir, "regatta.html");
			String uri = r.toURI().toASCIIString();
			Path p = Paths.get(r.toURI());
			URI xx = p.toUri();
			return xx;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Displays the report "help" window. Opens the help window if not already open.
	 **/
	public void showReports() {
		try {
			File r = getRegattaReportFile();
			if (r.exists()) {
				Desktop.getDesktop().browse(getRegattaReportUri());
			} else {
				Util.printlnException(this, new Exception("Reports requested but not initialized."), true);
			}
		} catch (Exception e) {
			Util.printlnException(this, e, true);
		}
	}

}
