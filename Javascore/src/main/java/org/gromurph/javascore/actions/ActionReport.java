// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReport.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.AbstractAction;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.*;
import org.gromurph.util.*;

/**
 * Generates a report of the series standings
 **/
public abstract class ActionReport extends AbstractAction implements ActionListener {
	protected static ResourceBundle res = JavaScoreProperties.getResources();
	protected static ResourceBundle resUtil = org.gromurph.util.Util.getResources();

	protected Regatta fRegatta;
	protected RegattaManager fRegattaManager;
	protected ReportOptions fOptions;

	public abstract String getReportName();

	public abstract String getTabName();

	protected static String sPreTemplate;
	protected static String sPostTemplate;

	private static String REGATTA_KEY = "##REGATTA##";
	private static String REPORT_KEY = "##REPORT##";
	private static String BODY_KEY = "##BODY##";

	public static final String RAWFINISH_TABLECLASS = "rawfinishtable";
	public static final String SERIES_TABLECLASS = "seriestable";
	public static final String CHECKIN_TABLECLASS = "checkintable";
	public static final String SCRATCH_TABLECLASS = "scratchtable";
	public static final String RACE_TABLECLASS = "racetable";
	public static final String TOC_TABLECLASS = "toctable";

	public final static String REGATTATITLE_PCLASS = "regattatitle";
	public final static String COMMENTS_PCLASS = "comments";
	public final static String REPORTTITLE_PCLASS = "reporttitle";
	public final static String INFOHEADER_PCLASS = "infoheader";
	public final static String INFOFOOTER_PCLASS = "infofooter";
	public final static String DIVISIONLINKS_PCLASS = "divisionlinks";
	public final static String DIVISIONHEADER_PCLASS = "divisionheader";
	public final static String VERSIONFOOTER_PCLASS = "versionfooter";
	public final static String FOOTNOTEHEADER_PCLASS = "footnoteheader";
	public final static String FOOTNOTES_TABLECLASS = "footnotes";

	public static final String DIVISIONLABEL_TDCLASS = "divisionlabelrow";
	public static final String FINISHSHEET1_TABLECLASS = "finishsheetrecorder";
	public static final String FINISHSHEET2_TABLECLASS = "finishsheetclasses";
	public static final String FINISHSHEET3_TABLECLASS = "finishsheetfinishes";
	public static final String FINISHSHEET4_TABLECLASS = "finishsheetfleetlist";

	public static final String SERIESDIVHEADER_CELLCLASS = "ssdiv";
	public static final String SERIES_CELLCLASS = "ss";
	public static final String FINISHSHEET1_CELLCLASS = "fsR";
	public static final String FINISHSHEET2_CELLCLASS = "fsC";
	public static final String FINISHSHEET3_CELLCLASS = "fsF";
	public static final String FINISHSHEET4_CELLCLASS = "fsL";

	protected static String sCssTable = null;

	public void reportForDivision(PrintWriter pw, AbstractDivision div, EntryList list, boolean is1D) {
		throw new java.lang.UnsupportedOperationException("ActionReport.reportForDivision should not be executed");
	}

	/**
	 * looks for a 'template.html' file in the requested reports directory. If not present, then a standard template is
	 * created The template.html is a "master" html file on which all reports will be based. This template file should
	 * be a valid html file, with three "keywords" embedded. If it does not contain the start and end body tags, the
	 * impact is unpredicted.
	 * <P>
	 *'##REGATTA##' will be substituted with the Regatta's name '##REPORT##' will be substituted with the descriptive
	 * name of the relevant report '##BODY##' will be substitued with the body of each report
	 * <P>
	 * The body of the report will contain the standard header/footer information. And the core of the report. The
	 * regatta name and report title key can be embedded as many times as the author of the template file wishes. Only
	 * one occurence of the report body key is expected. If no occurence of the ##BODY## key is found. The report body
	 * will be insert immediately before the "/body" tag.
	 * 
	 * For styles, if the template.html file does not exist, javascore looks for a file called 'javascore.css'... if
	 * found it is read in and embedded into the html report file. If not found, a standard javascore.css will be
	 * created and then used.
	 * 
	 * If a template.html file is specified, javascore will look to see if there is a reference in it to a css style
	 * file, if found, it will use leave it alone. If not found, it includes the javascore.css file as above.
	 * 
	 */
	public static void initializeTemplate(String dir, String inTemplate) {
		try {
			String template = "";
			if (inTemplate != null)
				template = inTemplate.trim();

			if (template.length() == 0)
				template = "template.html";

			Reader reader = null;

			// local in regatta reports directory first...
			File f = Util.getFile(dir, template);
			if (!f.exists()) {
				// if not a regatta specific, is there a system wide one?
				f = Util.getFile("template.html");
			}

			if (f.exists())
				reader = new FileReader(f);

			if (reader != null) {
				// read template.html into buffer
				int BUFSIZE = 1048;
				StringBuffer sb = new StringBuffer(BUFSIZE);
				char[] charBuffer = new char[BUFSIZE];
				int totRead = 0;
				int thisRead = 0;
				boolean haveMore = true;
				while (haveMore) {
					thisRead = reader.read(charBuffer);
					if (thisRead > 0) {
						String str = new String(charBuffer, 0, thisRead);
						sb.append(str);
						totRead += thisRead;
					}
					haveMore = (thisRead == BUFSIZE);
				}
				String buffer = sb.toString();
				// buffer should contain all of template.html file
				// split into pre- and post- body segments
				int loc0 = buffer.indexOf(BODY_KEY);
				int loc1 = loc0 + BODY_KEY.length();
				if (loc0 < 0) {
					loc0 = buffer.indexOf("</body>");
					loc1 = loc0;
				}
				if (loc0 >= 0) {
					sPreTemplate = buffer.substring(0, loc0);
					sPostTemplate = buffer.substring(loc1);
				} else {
					String msg = MessageFormat.format(res.getString("ReportMessageNoBodyError"), new Object[] {
							BODY_KEY, f.getName() });
					processErrorMessage(msg);
					sPreTemplate = buffer;
					sPostTemplate = "";
				}
				reader.close();
			} else {
				StringBuffer sb = new StringBuffer(1024);
				sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" ");
				sb.append(" \"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\"> ");
				sb.append("\n<html>\n");

				sb.append("<style type=\"text/css\">\n  ");

				sb.append(sCssTable);

				sb.append("\n</style>\n<head>\n");
				sb.append("  <meta http-equiv=\"Content-Style-Type\" content=\"text/css\">\n");

				sb.append("<title>\n");
				sb.append(REGATTA_KEY);
				sb.append(" - ");
				sb.append(REPORT_KEY);
				sb.append("</title></head>\n<body>\n");

				sb.append("\n<p class=" + REGATTATITLE_PCLASS + ">");
				sb.append(REGATTA_KEY);
				sb.append("</p><p class=" + REPORTTITLE_PCLASS + ">");
				sb.append(REPORT_KEY);
				sb.append("</p>\n");

				sPreTemplate = sb.toString();

				sPostTemplate = "\n\n</body></html>";
			}
		} catch (Exception e) {
			String msg = MessageFormat.format(res.getString("ReportMessageTemplateParsingError"), new Object[] { e
					.toString() });
			processErrorMessage(msg);

			StringBuffer sb = new StringBuffer(72);
			sb.append("<html><head><title>");
			sb.append(REGATTA_KEY);
			sb.append(" - ");
			sb.append(REPORT_KEY);
			sb.append("</title></head><body>");
			sPreTemplate = sb.toString();

			sPostTemplate = "</body></html>";
		}
	}

	private static void processErrorMessage(String msg) {
		int x = sErrors.indexOf(msg);
		if (x < 0) {
			Util.showError(null, msg, false);
			sErrors.add(msg);
		}
	}

	/**
	 * splits up the regatta into a set of division groups, and passes on to specific reports each division and lets via
	 * 'getBaseList(div)' a the baselist type (entries, racepoints, seriespoints) to be
	 * 
	 * @param pw
	 * @param obj
	 */
	public void generateBody(PrintWriter pw, Object obj) {
		List<String> linkList = new ArrayList<String>(10);

		// so we can insert linkList at top do rest of report
		StringWriter sw = new StringWriter(2048);
		PrintWriter pw2 = new PrintWriter(sw);

		// first report the starting divisions
		for (Division div : fRegatta.getDivisions()) {
			EntryList entries = div.getEntries();
			if (entries.size() > 0) {
				linkList.add(div.getName());
				generateDivisionHeader(pw2, div.getName(), entries.size(), DIVISION);
				reportForDivision(pw2, div, entries, div.isOneDesign());

				reportSubDivisions(pw2, div, linkList);
			}
		}

		// next report the fleets
		for (AbstractDivision div : fRegatta.getFleets()) {
			EntryList entries = div.getEntries();
			if (entries.size() > 0) {
				linkList.add(div.getName());
				generateDivisionHeader(pw2, div.getName(), entries.size(), FLEET);
				reportForDivision(pw2, div, entries, div.isOneDesign());
				reportSubDivisions(pw2, div, linkList);
			}
		}

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

	protected void reportSubDivisions(PrintWriter pw2, AbstractDivision parentDiv, List<String> linkList) {
		// next report the subdivisions of the parentDivision
		for (SubDivision div : fRegatta.getSubDivisions()) {
			if (div.getParentDivision() != null && div.getParentDivision().equals(parentDiv)) {
				EntryList entries = div.getEntries();
				if (entries.size() > 0) {
					linkList.add(div.getName());
					generateDivisionHeader(pw2, div.getName(), entries.size(), SUBDIVISION);
					reportForDivision(pw2, div, entries, div.isOneDesign());
				}
			}
		}
	}

	public static int DIVISION = 1;
	public static int FLEET = 2;
	public static int SUBDIVISION = 3;

	/**
	 * Generates the header for a "class"
	 * 
	 * @param pw
	 *            where to sent the results
	 * @param div
	 *            the division to do headers for
	 * @param oneClass
	 *            true if overall report page contains just one class
	 * @param n
	 *            number of entries in the class
	 */
	protected void generateDivisionHeader(PrintWriter pw, String divName, int n, int classType) {
		pw.print("\n<P class=" + DIVISIONHEADER_PCLASS + "><a name=\"");
		pw.print(divName);
		pw.println("\">");

		String lead = "GenDivision";
		if (classType == DIVISION)
			lead = "GenDivision";
		else if (classType == FLEET)
			lead = "GenFleet";
		else if (classType == SUBDIVISION)
			lead = "GenSubdivision";

		ResourceBundle temp = res; // keep resource checker from barfing
		pw.print(temp.getString(lead));
		pw.print(": ");
		pw.print(divName);
		pw.print("</a> <small><small>");
		pw.print( MessageFormat.format(res.getString("ReportLabelDivisionSize"), new Object[] { new Integer(n) }));
		pw.println(" <a href=\"#top\">(top)</a></small></small></p>");
	}

	protected void generateDivisionLinks(PrintWriter pw, List divList) {
		boolean oneClass = (divList.size() <= 1);
		if (!oneClass) {
			pw.println();
			pw.print("<p class=" + DIVISIONLINKS_PCLASS + "><a name=\"top\">(");
			pw.print(res.getString("GenEntries").trim());
			pw.print("=");
			pw.print(fRegatta.getNumEntries());
			pw.print(") ");

			for (Iterator iDiv = divList.iterator(); iDiv.hasNext();) {
				String divname;
				Object div = iDiv.next();
				try {
					divname = (String) div;
				} catch (ClassCastException e) {
					divname = ((Division) div).getName();
				}

				pw.print(" <a href=\"#");
				pw.print(divname);
				pw.print("\">");
				pw.print(divname);
				pw.print("</a>");
				if (iDiv.hasNext())
					pw.print(" - ");
			}
			pw.println("</p>");
		}

	}

	public int generateDescriptiveHeaders(PrintWriter pw) {
		return generateDescriptiveHeaders(pw, 0, fOptions.getMaxColumns() - 1);
	}

	public int generateDescriptiveHeaders(PrintWriter pw, int startCol) {
		return generateDescriptiveHeaders(pw, startCol, fOptions.getMaxColumns() - 1);
	}

	public int generateDescriptiveHeaders(PrintWriter pw, int startCol, int lastCol) {
		for (int i = startCol; i <= lastCol; i++) {
			String s = fOptions.getColumnHeader(i);
			if (s.length() > 0) {
				StringBuffer sb = new StringBuffer(72);
				// sb.append("<u>");
				sb.append(s);
				// sb.append("</u>");
				addTableCell(pw, sb.toString(), "left");
			}
		}
		return lastCol - startCol + 1;
	}

	public void generateDescriptiveCells(PrintWriter pw, Entry entry) {
		generateDescriptiveCells(pw, entry, 0, fOptions.getMaxColumns() - 1);
	}

	public void generateDescriptiveCells(PrintWriter pw, Entry entry, int startCol) {
		generateDescriptiveCells(pw, entry, startCol, fOptions.getMaxColumns() - 1);
	}

	public void generateDescriptiveCells(PrintWriter pw, Entry entry, int startCol, int lastCol) {
		for (int i = startCol; i <= lastCol; i++) {
			String s = fOptions.getColumnHeader(i);
			if (s.length() > 0) {
				s = fOptions.getColumnEntry(i, entry, true, false);
				if (s.length() == 0)
					s = "&nbsp;";
				addTableCell(pw, s, "left");
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (!regattaExists())
			return;

		DialogTextInfo dialog = new DialogTextInfo();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			createReportStream(pw, fRegatta, null, "");
			dialog.setReportBody(sw.toString());
		} catch (IOException e) {
			dialog.setReportBody("IOException encountered: " + e.toString());
		}
		dialog.setVisible(true);
	}

	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * Wraps the generation of a report for a regatta Handles centralized file creation
	 */
	public void createReportFile(String dir, String file, Regatta regatta, Object obj) throws IOException {
		String filename = dir + file;
		ReportOptions options = null;

		FileOutputStream fos = new FileOutputStream(filename);
		OutputStreamWriter osw = new OutputStreamWriter(fos, DEFAULT_ENCODING);
		PrintWriter pw = new PrintWriter(osw);

		int fontsize = 2;
		String fontFamily = "Verdana";

		if (regatta != null && getTabName() != null) {
			options = regatta.getReportOptions(getTabName());
			if (options != null) {
				fontFamily = options.getFontName();
				fontsize = options.getFontSize();
			}
		}

		if (fontFamily == null)
			fontFamily = "Verdana";

		String tableFontPct = "90";
		switch (fontsize) {
		case 2:
			tableFontPct = "80";
		case 1:
			tableFontPct = "70";
		case 4:
			tableFontPct = "100";
		default:
			tableFontPct = "90";
		}

		if (sCssTable == null)
			sCssTable = getCssString(fontFamily, tableFontPct, dir);

		createReportStream(pw, regatta, obj, dir);

		pw.flush();
		pw.close();
	}

	private static String getCssString(String fontFamily, String tableFontPct, String dir) {
		try {
			String cssFilename = "javascore.css";

			Reader reader = null;

			// local in regatta reports directory first...
			File cssFile = Util.getFile(dir, cssFilename);
			if (!cssFile.exists()) {
				// not in reports directory, look in main working directory
				cssFile = Util.getFile(cssFilename);
			}

			if (cssFile.exists()) {
				// read the javascore.css file and return it's contents as a
				// string
				int BUFSIZE = 1048;
				StringBuffer sb = new StringBuffer(BUFSIZE);
				char[] charBuffer = new char[BUFSIZE];
				int totRead = 0;
				int thisRead = 0;
				boolean haveMore = true;
				reader = new FileReader(cssFile);
				while (haveMore) {
					thisRead = reader.read(charBuffer);
					if (thisRead > 0) {
						String str = new String(charBuffer, 0, thisRead);
						sb.append(str);
						totRead += thisRead;
					}
					haveMore = (thisRead == BUFSIZE);
				}
				reader.close();
				return sb.toString();

			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);

				pw.println("body { font-family:" + fontFamily + "; margin: 4px; color:black; background:white }");
				pw.println("table { bgcolor=#FFFFFF font-family:" + fontFamily
						+ "; font-weight: normal; valign:top; align:center; }");
				pw.println("thead { vertical-align: bottom; font-weight:bold}");
				pw.println("td {padding-left: 5px; padding-right: 5px}");

				pw.println("p." + REGATTATITLE_PCLASS
						+ " { padding: 0; font-weight:bold; font-size:120%; margin-top:0; margin-bottom:0}");
				pw.println("p." + REPORTTITLE_PCLASS
						+ " { font-weight: bold; font-size:110%; margin-top:0; margin-bottom:0}");
				pw.println("p." + INFOHEADER_PCLASS + " { font-size: 90%; margin-top:5px; margin-bottom:0}");
				pw.println("p." + INFOFOOTER_PCLASS + " { font-size: 90%; margin-top:30px; margin-bottom:0}");
				pw.println("p." + DIVISIONLINKS_PCLASS + " { font-size: 90%; margin-top:10px; margin-bottom:5px}");
				pw.println("p." + DIVISIONHEADER_PCLASS
						+ " { font-size: 110%; font-weight: bold; margin-top:30px; margin-bottom:15px}");
				pw.println("p." + VERSIONFOOTER_PCLASS + " { font-size: 70%}");
				pw.println("p." + FOOTNOTEHEADER_PCLASS + " { font-size: 80%; margin-top:5px; margin-bottom:0}");
				pw.println("p." + COMMENTS_PCLASS + " { font-size: 100%; margin-top:10px; margin-bottom:0}");

				pw.println("table." + FOOTNOTES_TABLECLASS + " { font-size: 80%}");

				pw.println("table." + SERIES_TABLECLASS + " { font-size: " + tableFontPct + "%}");
				pw.println("table." + CHECKIN_TABLECLASS + " { font-size: 90%}");
				pw.println("table." + RAWFINISH_TABLECLASS + " { font-size: " + tableFontPct + "%}");
				pw.println("table." + SCRATCH_TABLECLASS + " { font-size: " + tableFontPct + "%}");
				pw.println("table." + RACE_TABLECLASS + " { font-size: " + tableFontPct + "%}");
				pw.println("table." + TOC_TABLECLASS + " { font-size: 100%}");

				pw.println("table." + FINISHSHEET1_TABLECLASS + " { }");
				pw.println("table." + FINISHSHEET2_TABLECLASS + " { }");
				pw.println("table." + FINISHSHEET3_TABLECLASS + " { }");
				pw.println("table." + FINISHSHEET4_TABLECLASS + " { }");

				pw.println("td." + SERIESDIVHEADER_CELLCLASS + " { border-top: 1px solid; border-bottom: 1px solid;");
				pw.println("padding-left: 3px; padding-right: 3px; padding-top: 1px; padding-bottom: 1px;");
				pw.println("font-size: 110%; font-weight:bold; background: #DDD;}");
				pw.println("td." + SERIES_CELLCLASS
						+ " { padding-left: 3px; padding-right: 3px; padding-top: 1px; padding-bottom: 1px}");
				pw.println("td." + FINISHSHEET1_CELLCLASS + " { font-size: 100%; border: 1px solid}");
				pw.println("td." + FINISHSHEET2_CELLCLASS + " { font-size: 100%; border: 1px solid}");
				pw.println("td." + FINISHSHEET3_CELLCLASS + " { font-size: 100%; border: 1px solid}");
				pw.println("td." + FINISHSHEET4_CELLCLASS + " { font-size: 100%; border: 1px solid}");

				pw.println("tr." + DIVISIONLABEL_TDCLASS + " { font-size: 100% font-weight: bold}");

				String cssContents = sw.toString();

				FileWriter fw = new FileWriter(cssFile);
				fw.write(cssContents);
				fw.close();
				return cssContents;
			}
		} catch (Exception e) {
			processErrorMessage(e.toString());
			return "";
		}
	}

	static List<String> sErrors = new ArrayList<String>();

	public static void clearReportErrors() {
		sErrors.clear();
	}

	/**
	 * creates the string of a report's html. Handles generating html header and footer Cuts to abstract generateBody()
	 * for report details Subclasses defining generateBody() can assume that fRegatta is up to date and is not null. The
	 * 'obj' is when some additional paremeter is needed and is solely up to the subclass to cast and handle
	 */
	public void createReportStream(PrintWriter pw, Regatta regatta, Object obj, String dir) throws IOException {
		fRegatta = regatta;
		fRegattaManager = new RegattaManager(fRegatta);

		String noRegatta = res.getString("ReportMessageNoRegatta");

		if (fRegatta == null) {
			String string = Util.stringReplace(sPreTemplate, REGATTA_KEY, noRegatta);
			string = Util.stringReplace(string, REPORT_KEY, noRegatta);
			pw.print(string);

			pw.println(res.getString("ReportMessageNoRegattaLong"));

			string = Util.stringReplace(sPostTemplate, REGATTA_KEY, noRegatta);
			string = Util.stringReplace(string, REPORT_KEY, noRegatta);
			pw.print(string);
		} else {
			fOptions = fRegatta.getReportOptions(getTabName());
			initializeTemplate(dir, fOptions.getTemplateFile());

			int holdFormat = Person.getFormat();
			Person.setFormat(fOptions.getNameFormat());

			String string = Util.stringReplace(sPreTemplate, REGATTA_KEY, fRegatta.getName());
			string = Util.stringReplace(string, REPORT_KEY, getReportName());
			pw.print(string);

			initializeNotes();

			generateHeader(pw);
			generateBody(pw, obj);
			generateFooter(pw);

			string = Util.stringReplace(sPostTemplate, REGATTA_KEY, fRegatta.getName());
			string = Util.stringReplace(string, REPORT_KEY, getReportName());
			pw.print(string);

			Person.setFormat(holdFormat);
		}
	}

	public void generateHeader(PrintWriter pw) {
		pw.print("\n<p class=\"" + INFOHEADER_PCLASS + "\">");
		if (fRegatta.isFinal())
			pw.print(res.getString("ReportMessageInformationFinal"));
		else
			pw.print(res.getString("ReportMessageInformationProv"));
		pw.print("<br>");
		pw.print(res.getString("ReportMessageResultSaved"));
		if (fRegatta.getSaveDate() != null) {
			pw.print(": ");
			pw.print(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault()).format(
					fRegatta.getSaveDate()));
		}
		pw.println("</p><br>");
	}

	protected List<String> fNotes;

	protected void initializeNotes() {
		fNotes = new ArrayList<String>();
	}

	protected void formatNotes(PrintWriter pw, List<String> notes) {
		if (notes.size() > 0) {
			pw.print("<P class=" + FOOTNOTEHEADER_PCLASS + ">");
			pw.print(res.getString("ScoringNotes"));
			pw.println("</P>");

			pw.println("<table class=" + FOOTNOTES_TABLECLASS + ">");
			int i = 1;
			for (String note : notes) {
				pw.print("<tr><td>");
				pw.print("<sup>(" + (i++) + ")</sup></td><td>");
				pw.print(note);
				pw.println("</td></tr>");
			}
			pw.println("</table>");
		}
	}

	protected String formatNonDiscardableNote(Race race) {
		return java.text.MessageFormat.format(res.getString("ReportRaceNoteNonDiscardable"), new Object[] { race
				.getName() });
	}

	protected String formatWeightedNote(Race race) {
		return java.text.MessageFormat.format(res.getString("ReportRaceNoteRaceWeight"), new Object[] { race.getName(),
				race.getWeight() });
	}

	public void generateFooter(PrintWriter pw) {
		pw.println("\n<p class=" + INFOFOOTER_PCLASS + ">");
		if (fRegatta.isFinal())
			pw.print(res.getString("ReportMessageInformationFinal"));
		else
			pw.print(res.getString("ReportMessageInformationProv"));

		pw.print("<br>");
		if (fRegatta.getPro().length() > 0) {
			pw.print("<br>");
			pw.print(MessageFormat.format(res.getString("ReportLabelPro"), new Object[] { fRegatta.getPro() }));
		}
		if (fRegatta.getJuryChair().length() > 0) {
			pw.print("<br>");
			pw.print(MessageFormat.format(res.getString("ReportLabelJuryChair"),
					new Object[] { fRegatta.getJuryChair() }));
		}
		pw.println();
		pw.println("</p>");

		pw.println("<hr>");

		pw.print("<P class= " + VERSIONFOOTER_PCLASS + ">");
		pw.print("Version ");
		pw.print(JavaScoreProperties.getVersion());
		pw.print(": ");
		pw.print(res.getString("ReportLabelByLine"));
		// this intentionally not thru resources, to ensure it makes it
		pw.print("</p>");
	}

	public void addTableCell(PrintWriter pw, String middle) {
		addTableCell(pw, middle, "left", null);
	}

	public void addTableCell(PrintWriter pw, String middle, String align) {
		addTableCell(pw, middle, align, null);
	}

	public void addTableCell(PrintWriter pw, String middle, String align, String cellClass) {
		pw.print("\n<td");
		if (!align.equals("left")) {
			pw.print(" align=");
			pw.print(align);
		}
		if (cellClass != null) {
			pw.print(" class=");
			pw.print(cellClass);
		}
		pw.print(">");
		pw.print(middle);
		pw.print("</td>");
	}

	public ActionReport() {
		super();
	}

	/**
	 * looks to see that variable fRegatta is defined, if not, tries to set it from JavaScoreProperties.getRegatta().
	 * Returns false if all fails
	 */
	protected boolean regattaExists() {
		if (fRegatta == null) {
			fRegatta = JavaScoreProperties.getRegatta();
		}
		return (fRegatta != null);
	}

}
/**
 * $Log: ActionReport.java,v $ Revision 1.7 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.6 2006/04/15 23:39:23 sandyg report tweaking for Miami OCR, division splits
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
 * Revision 1.21 2005/06/05 12:28:27 sandyg Added SUBVERSION for tracking patches
 * 
 * Revision 1.20 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.19 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.18 2005/03/06 22:39:50 sandyg Feature 1157927, gives fleet size on entry sheet
 * 
 * Revision 1.17 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.16 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.15 2004/01/20 23:28:12 sandyg fixed nullpointererror on empty subdivision
 * 
 * Revision 1.14 2004/01/18 19:33:27 sandyg Subdivisions now show just below parent division instead of at end
 * 
 * Revision 1.13 2003/05/07 01:18:18 sandyg bold headings made consistent, fleet/division scoring problem fixed,
 * sail/bow header cleaned up
 * 
 * Revision 1.12 2003/04/27 21:35:31 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.11 2003/03/16 20:38:19 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.10 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
