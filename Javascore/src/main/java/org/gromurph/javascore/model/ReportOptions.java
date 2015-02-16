// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ReportOptions.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.actions.ActionReportCheckin;
import org.gromurph.javascore.actions.ActionReportFinish;
import org.gromurph.javascore.actions.ActionReportOneRace;
import org.gromurph.javascore.actions.ActionReportRawFinish;
import org.gromurph.javascore.actions.ActionReportScratch;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsSingleStage;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Person;
import org.gromurph.xml.PersistentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains a set of report options.
 */
public class ReportOptions extends BaseObject {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private static final long serialVersionUID = 59644614711133337L;

	private static int[] sColumnFlags = new int[] { Entry.SHOW_SKIPPER, Entry.SHOW_CREW, Entry.SHOW_BOAT,
			Entry.SHOW_CLUB, Entry.SHOW_MNA, Entry.SHOW_RSA, Entry.SHOW_RATING, Entry.SHOW_FULLRATING,
			Entry.SHOW_DIVISION, Entry.SHOW_SUBDIVISION };

	private static List<String> sOptionLocations = Arrays.asList("none", "1.1", "1.2", "1.3", "1.4", "2.1", "2.2",
			"2.3", "2.4", "3.1", "3.2", "3.3", "3.4", "4.1", "4.2", "4.3", "4.4");

	public static int getNumberOptions() {
		if (NOPTIONS < 0) NOPTIONS = getOptionStrings().size();
		return NOPTIONS;
	}

	private static int NOPTIONS = -1;
	private static List<String> _optionStrings = null;
	private static String MNALABEL = null;
	private static String RSALABEL = null;

	public static Race reportingForRace = null;

	/**
	 * returns the list of possible column contents, panelreportoptions should create a checkbox for each of these
	 */
	public static List<String> getOptionStrings() {
		boolean regenerate = (_optionStrings == null);
		if (MNALABEL == null
				|| !MNALABEL.equals(JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY)))
			regenerate = true;
		if (RSALABEL == null
				|| !RSALABEL.equals(JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY)))
			regenerate = true;

		if (regenerate) {
			MNALABEL = JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY);
			RSALABEL = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY);
			_optionStrings = Arrays.asList(res.getString("GenSkipper"), res.getString("GenCrew"),
					res.getString("GenBoat"), res.getString("GenClub"), MNALABEL, RSALABEL, res.getString("GenRating"),
					res.getString("GenFullRating"), res.getString("GenDivision"), res.getString("GenSubdivision"));
		}
		return _optionStrings;
	}

	private static List<String> _optionlabels = null;

	/**
	 * returns the list of possible column contents, panelreportoptions should create a checkbox for each of these
	 */
	public static List<String> getOptionLabelNames() {
		boolean regenerate = (_optionlabels == null);
		if (MNALABEL == null
				|| !MNALABEL.equals(JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY)))
			regenerate = true;
		if (RSALABEL == null
				|| !RSALABEL.equals(JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY)))
			regenerate = true;

		if (regenerate) {
			MNALABEL = JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY);
			RSALABEL = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY);
			_optionlabels = Arrays.asList("GenSkipper", "GenCrew", "GenBoat", "GenClub", MNALABEL, RSALABEL,
					"GenRating", "GenFullRating", "GenDivision", "GenSubdivision");
		}
		return _optionlabels;
	}

	// private static int OPTIONS_LENGTH = 10;

	// these make up a single instance, and/or the default for a new report
	private int fNameOption;
	private List<String> fOptionLocations;
	private String fFontName = "Verdana";
	private int fFontSize = 2;
	private String fTabName;
	private String fTemplate;
	private boolean fIncludeOneDesignTimes;
	private boolean fHidePenaltyPoints;
	private boolean fShowLastXRaces;
	private boolean fCombineRaceAndSeries;
	private int fLastXRaces;

	public static final String NAMEOPTION_PROPERTY = "NameOption";
	public static final String OPTIONLOCATIONS_PROPERTY = "OptionLocations";
	public static final String FONTNAME_PROPERTY = "FontName";
	public static final String FONTSIZE_PROPERTY = "FontSize";
	public static final String TABNAME_PROPERTY = "TabName";
	public static final String TEMPLATE_PROPERTY = "Template";
	public static final String INCLUDEONEDESIGNTIMES_PROPERTY = "Show1DTimes";

	public static final String SHOWLASTXRACES_PROPERTY = "ShowLastXRaces";
	public static final String HIDEPENALTYPOINTS_PROPERTY = "HidePenaltyPoints";
	public static final String LASTXRACES_PROPERTY = "LastXRaces";
	public static final String COMBINERACEANDSERIES_PROPERTY = "CombineRaceAndSeries";

	private static final String ITEM = "item";
	private static final String VALUE = "value";

	@Override
	public void xmlWrite(PersistentNode e) {

		e.setAttribute(NAMEOPTION_PROPERTY, Integer.toString(fNameOption));
		e.setAttribute(FONTNAME_PROPERTY, fFontName);
		e.setAttribute(FONTSIZE_PROPERTY, Integer.toString(fFontSize));
		e.setAttribute(TABNAME_PROPERTY, fTabName);
		e.setAttribute(TEMPLATE_PROPERTY, fTemplate);
		e.setAttribute(INCLUDEONEDESIGNTIMES_PROPERTY, new Boolean(fIncludeOneDesignTimes).toString());
		e.setAttribute(SHOWLASTXRACES_PROPERTY, new Boolean(fShowLastXRaces).toString());
		e.setAttribute(HIDEPENALTYPOINTS_PROPERTY, new Boolean(fHidePenaltyPoints).toString());
		e.setAttribute(LASTXRACES_PROPERTY, Integer.toString(fLastXRaces));
		e.setAttribute(COMBINERACEANDSERIES_PROPERTY, Boolean.toString(fCombineRaceAndSeries));

		PersistentNode eLocs = e.createChildElement( OPTIONLOCATIONS_PROPERTY);
		for (String opt : fOptionLocations) {
			PersistentNode el = eLocs.createChildElement( ITEM);
			el.setAttribute(VALUE, opt);
		}
	}

	public static boolean isValidReport(ReportOptions ro) {
		String tab = ro.getName();
		return (tab.equals(ActionReportCheckin.TABNAME) || tab.equals(ActionReportFinish.TABNAME)
				|| tab.equals(ActionReportOneRace.TABNAME) || tab.equals(ActionReportRawFinish.TABNAME)
				|| tab.equals(ActionReportScratch.TABNAME) || tab
					.equals(ActionReportSeriesStandingsSingleStage.TABNAME));
	}

	@Override
	public void xmlRead(PersistentNode node, Object rootObject) {
		String value = "";

		if ((value = node.getAttribute(NAMEOPTION_PROPERTY)) != null) fNameOption = Integer.parseInt(value);
		if ((value = node.getAttribute(FONTNAME_PROPERTY)) != null) fFontName = value;
		if ((value = node.getAttribute(FONTSIZE_PROPERTY)) != null) fFontSize = Integer.parseInt(value);

		if ((value = node.getAttribute(TABNAME_PROPERTY)) != null) {
			// renaming related to version 2.0.1
			if (value.equals("Entry List")) fTabName = ActionReportScratch.TABNAME;
			else if (value.equals("Finish Sheet")) fTabName = ActionReportFinish.TABNAME;
			else if (value.equals("Check-in")) fTabName = ActionReportCheckin.TABNAME;
			else fTabName = value;
		}

		if ((value = node.getAttribute(TEMPLATE_PROPERTY)) != null) {
			// renaming related to version 2.0.1 - used to write out tabname as
			// the template fields in XML
			if (value.equals("Entry List") || value.equals("Finish Sheet") || value.equals("Check-in")
					|| value.equals("Proofing") || value.equals("Check-in") || value.equals("Race")
					|| value.equals("Series") || value.equals(fTabName)) {
				fTemplate = "";
			} else {
				fTemplate = value;
			}
		}

		if ((value = node.getAttribute(COMBINERACEANDSERIES_PROPERTY)) != null) {
			try {
				boolean b = value.toString().equalsIgnoreCase("true");
				fCombineRaceAndSeries = b;
			}
			catch (Exception e) {}
		}

		if ((value = node.getAttribute(INCLUDEONEDESIGNTIMES_PROPERTY)) != null) {
			try {
				boolean b = value.toString().equalsIgnoreCase("true");
				setIncludeOneDesignTimes(b);
			}
			catch (Exception e) {}
		}

		if ((value = node.getAttribute(SHOWLASTXRACES_PROPERTY)) != null) {
			try {
				boolean b = value.toString().equalsIgnoreCase("true");
				setShowLastXRaces(b);
			}
			catch (Exception e) {}
		}

		if ((value = node.getAttribute(HIDEPENALTYPOINTS_PROPERTY)) != null) {
			try {
				boolean b = value.toString().equalsIgnoreCase("true");
				setHidePenaltyPoints(b);
			}
			catch (Exception e) {}
		}

		if ((value = node.getAttribute(LASTXRACES_PROPERTY)) != null) {
			fLastXRaces = Integer.parseInt(value);
		}

		List<String> rLoc = new ArrayList<String>();
		PersistentNode n1 = node.getElement(OPTIONLOCATIONS_PROPERTY);
		if (n1 != null) {
			PersistentNode[] itemNodes = n1.getElements();
			for (int r = 0; r < itemNodes.length && r < getOptionStrings().size(); r++) {
				value = itemNodes[r].getAttribute(VALUE);
				if (value != null) {
					rLoc.add(value);
				}
			}
		}
		setOptionLocationValues(rLoc);

	}

	public ReportOptions() {
		this("noname");
	}

	public ReportOptions(String tabname) {
		super();
		fTabName = tabname;
		fNameOption = Person.FORMAT_LASTFIRST;
		fTemplate = "";
		fIncludeOneDesignTimes = true;
		fHidePenaltyPoints = false;
		fShowLastXRaces = false;
		fLastXRaces = 5;

		List<String> x = new ArrayList<String>();
		x.addAll(Arrays.asList("2.1", "none", "1.1", "3.2", "none", "3.1"));
		setOptionLocationValues(x);
	}

	@Override
	public String toString() {
		// return fTabName;
		return res.getString("ReportOptionTab" + fTabName);
	}

	public int getMaxColumns() {
		return 5;
	}

	public void setTemplateFile(String f) {
		fTemplate = f;
	}

	public String getTemplateFile() {
		return fTemplate;
	}

	/**
	 * evaluates the current column option settings and returns a header for the specified column number
	 */
	public String getColumnHeader(int col) {
		List<String> labels = getOptionLabelNames();

		if (col == 0) {
			if (JavaScoreProperties.getRegatta() != null && JavaScoreProperties.getRegatta().isUseBowNumbers()) {
				return res.getString("GenBowSail");
			} else {
				return res.getString("GenSail");
			}
		} else {
			StringBuffer sb = new StringBuffer();
			String mna = JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY);
			String rsa = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY);
			int start = (col - 1) * 4 + 1;
			nextItem: for (int i = start; i < start + 4; i++) {
				// looping thru relevant "x.1, x.2, x.3, x.4"
				for (int j = 0; j < fOptionLocations.size(); j++) {

					// loop thru the option settings
					if (fOptionLocations.get(j).equals(sOptionLocations.get(i))) {
						// want this one
						String opt = labels.get(j);
						if (!opt.equals(mna) && !opt.equals(rsa)) {
							opt = res.getString(opt);
						}
						sb.append(addAndTrim(opt));
						continue nextItem;
					}
				}
			}
			String str = sb.toString();
			if (str.length() > 0) return str.substring(1);
			else return "";
		}
	}

	private String addAndTrim(String newStr) {
		String s = newStr.trim();
		if (s.length() > 0) return "/" + s;
		else return "";
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Regatta reg = new Regatta();

		Division div = new Division("Div");
		reg.addDivision(div);

		SubDivision sdiv = new SubDivision("SubD", div);
		reg.addSubDivision(sdiv);

		Entry ent = new Entry();
		ent.setBoat(new Boat("Boat", "1234", new Person("SkipFirst", "SkipLast")));
		ent.setBow("10");
		ent.setClub("Club");
		ent.setCrew(new Person("CrewFirst", "CrewLast"));
		try {
			ent.setDivision(div);
		}
		catch (RatingOutOfBoundsException ex) {}
		ent.setMnaNumber("M123445");
		ent.setRsaNumber("R999999");
		ent.setSkipper(new Person("SkipFirst", "SkipLast"));
		sdiv.addEntry(ent);
		reg.addEntry(ent);

		ReportOptions fOptions = new ReportOptions("TEST");
		Logger l = LoggerFactory.getLogger( ReportOptions.class);
		l.info(fOptions.getColumnHeader(0));
		l.info(fOptions.getColumnHeader(1));
		l.info(fOptions.getColumnHeader(2));
		l.info(fOptions.getColumnHeader(3));

		l.info(fOptions.getColumnEntry(0, ent));
		l.info(fOptions.getColumnEntry(1, ent));
		l.info(fOptions.getColumnEntry(2, ent));
		l.info(fOptions.getColumnEntry(3, ent));
	}

	public String getName() {
		if (fTabName == null) fTabName = "none";
		return fTabName;
	}

	/**
	 * evaluates the current column option settings and returns a header for the specified column number
	 */
	public String getColumnEntry(int col, Entry entry) {
		return getColumnEntry(col, entry, false, false);
	}

	/**
	 * evaluates the current column option settings and returns a value for the entry at the specified column number
	 */
	public String getColumnEntry(int col, Entry entry, boolean doHtml, boolean dummysubdiv) {
		if (entry == null) return "";

		if (col == 0) {
			return entry.toString(Entry.SHOW_BOW, doHtml, null, true, dummysubdiv);
		} else {
			int start = (col - 1) * 4 + 1;
			List<Integer> flags = new ArrayList<Integer>(getNumberOptions());
			nextItem: for (int i = start; i < start + 4; i++) {
				// looping thru relevant "x.1, x.2, x.3, x.4"
				for (int j = 0; j < fOptionLocations.size(); j++) {
					// loop thru the option settings
					if (fOptionLocations.get(j).equals(sOptionLocations.get(i))) {
						// want this one
						flags.add(new Integer(sColumnFlags[j]));
						continue nextItem;
					}
				}
			}
			String s = entry.toString(flags, doHtml, reportingForRace, dummysubdiv);
			if (s.equals(Entry.BLANK)) return "";
			else return s;
		}
	}

	/**
	 * returns array same length as optionstrings containing the current location setting of each of the options
	 */
	public List<String> getOptionLocationValues() {
		return fOptionLocations;
	}

	/**
	 * returns array same length as optionstrings containing the current location setting of each of the options
	 */
	public void setOptionLocationValues(int index, String newVal) {
		int startSize = fOptionLocations.size();
		// fill out array size to index if smaller
		for (int i = startSize; i <= index; i++)
			fOptionLocations.add(i, "none");
		fOptionLocations.set(index, newVal);
	}

	/**
	 * returns array same length as optionstrings containing the current location setting of each of the options
	 */
	public void setOptionLocationValues(List<String> newVal) { // String[]
		// newVal) {
		fOptionLocations = new ArrayList<String>(newVal.size());
		fOptionLocations.addAll(newVal);
		int startSize = fOptionLocations.size();
		// fill out array size to index if smaller
		for (int i = startSize; i < sColumnFlags.length; i++)
			fOptionLocations.add(i, "none");
	}

	/**
	 * returns array same length as optionstrings containing the current location setting of each of the options
	 */
	public void setOptionLocationValues(String[] array) { // String[] newVal) {
		setOptionLocationValues(Arrays.asList(array));
	}

	public static List<String> getOptionLocations() {
		return sOptionLocations;
	}

	public String getFontName() {
		if (fFontName == null) fFontName = "Verdana";
		return fFontName;
	}

	public void setFontName(String str) {
		fFontName = str;
	}

	public int getFontSize() {
		return fFontSize;
	}

	public void setFontSize(int sz) {
		fFontSize = sz;
	}

	public boolean isIncludeOneDesignTimes() {
		return fIncludeOneDesignTimes;
	}

	public void setIncludeOneDesignTimes(boolean b) {
		fIncludeOneDesignTimes = b;
	}

	public static String[] getNameFormats() {
		return Person.getFormatOptions();
	}

	public int getNameFormat() {
		return fNameOption;
	}

	public void setNameFormat(String str) {
		fNameOption = Person.getFormat(str);
	}

	public void setNameFormat(int i) {
		fNameOption = i;
	}

	public void setShowLastXRaces(boolean b) {
		fShowLastXRaces = b;
	}

	public boolean isShowLastXRaces() {
		return fShowLastXRaces;
	}

	public void setLastXRaces(int x) {
		fLastXRaces = x;
	}

	public int getLastXRaces() {
		return fLastXRaces;
	}

	public void setHidePenaltyPoints(boolean b) {
		fHidePenaltyPoints = b;
	}

	public boolean isHidePenaltyPoints() {
		return fHidePenaltyPoints;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof ReportOptions)) return false;

		return this.fTabName.equals(((ReportOptions) obj).fTabName);
	}

	public int compareTo(Object obj) {
		if (!(obj instanceof ReportOptions)) return 1;
		return this.fTabName.compareTo(((ReportOptions) obj).fTabName);
	}

	public boolean isCombineRaceAndSeries() {
		return fCombineRaceAndSeries;
	}

	public void setCombineRaceAndSeries(boolean combineRaceAndSeries) {
		fCombineRaceAndSeries = combineRaceAndSeries;
	}
}
/**
 * $Log: ReportOptions.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.2 2005/11/01 18:05:00 sandyg fixed mark rounding order and lost report options - both problems
 * discovered during 2005 RIWKC
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.12.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.12 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.11 2003/05/07 01:17:07 sandyg removed unneeded method parameters
 * 
 * Revision 1.10 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.9 2003/01/06 00:32:37 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.8 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.7 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
