//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Regatta.java,v 1.10 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.actions.ActionReportCheckin;
import org.gromurph.javascore.actions.ActionReportFinish;
import org.gromurph.javascore.actions.ActionReportOneRace;
import org.gromurph.javascore.actions.ActionReportRawFinish;
import org.gromurph.javascore.actions.ActionReportScratch;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsSingleStage;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.scoring.DailyStageScoring;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.RegattaScoringModel;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Contains all the information about a single regatta.
 */
public class Regatta extends BaseObject {
	static ResourceBundle res = JavaScoreProperties.getResources();

	private static final long serialVersionUID = 1L;
	private static final int VERSION = 600;
	
	// 600 stages introduced
	// 552 new ARP, TME, TMC penalties
	// 521 adds
	// 420 checks for loading newer versions, release 4.2
	// 300 better versioning, fleets, subdivisions added
	// 201 series points conversion
	// 200 beta testing

	private String fName = "";
	private String fDates = "";
	private String fJuryChair = "";
	private String fPro = "";
	private String fHostClub = "";
	private String fIfEventId = "";

	private boolean fUseBowNumbers = false;
	private DivisionList fDivisions = new DivisionList();
	private FleetList fFleets = new FleetList();
	private SubDivisionList fSubDivisions = new SubDivisionList();
	private RaceList fRaces = new RaceList();
	private EntryList fEntries = new EntryList();
	private transient String fSaveName = NONAME;
	private transient String fSaveDirectory = Util.getWorkingDirectory();
	private RegattaScoringModel fScores;
	private boolean fFinal = false;
	private List<ReportOptions> fReportOptionsList = new ArrayList<ReportOptions>();
	private int fVersion = VERSION;
	private String fSaveVersion = JavaScoreProperties.getVersion();
	// private transient boolean fOverrideFinal = false; // for saving with
	// final true
	private boolean fIsMultiStage = false;
	private String fComment = null;

	/**
	 * if true, then a stage/series score for each day of racing will also be calculated
	 */
	private boolean fDailyScoring = false;

	public transient static final String NAME_PROPERTY = "Name";
	public transient static final String PRO_PROPERTY = "Pro";
	public transient static final String JURYCHAIR_PROPERTY = "JuryChair";
	public transient static final String HOSTCLUB_PROPERTY = "HostClub";
	public transient static final String DATES_PROPERTY = "Dates";
	public transient static final String USEBOWNUMBERS_PROPERTY = "UseBowNumbers";
	public transient static final String FINAL_PROPERTY = "Final";
	public transient static final String MULTISTAGE_PROPERTY = "Multistage";
	public transient static final String COMMENT_PROPERTY = "comment";
	public transient static final String IFEVENTID_PROPERTY = "IFEventID";

	private transient static final String REPORTOPTIONSLIST_PROPERTY = "ReportOptionList";
	private transient static final String REPORTOPTIONS_PROPERTY = "Options";
	private transient static final String DIVISIONLIST_PROPERTY = "DivisionList";
	private transient static final String FLEETLIST_PROPERTY = "FleetList";
	private transient static final String SUBDIVISIONLIST_PROPERTY = "SubDivisionList";
	private transient static final String ENTRYLIST_PROPERTY = "EntryList";
	private transient static final String RACELIST_PROPERTY = "RaceList";
	private transient static final String SCORES_PROPERTY = "Scores";
	private transient static final String ENTRY_PROPERTY = "Entry";
	private transient static final String RACE_PROPERTY = "Race";
	private transient static final String DIVISION_PROPERTY = "Division";
	private transient static final String FLEET_PROPERTY = "Fleet";
	private transient static final String SUBDIVISION_PROPERTY = "SubDivision";
	private transient static final String VERSION_PROPERTY = "Version";
	private transient static final String JAVASCORE_VERSION = "JSVersion";
	private transient static final String DAILYSCORING_PROPERTY = "DailyScoring";

	public static final String NONAME = Util.NONAME;

	private RegattaManager mgr = null;

	/**
	 * @deprecated, use RegattaManager instead
	 */
	public void scoreRegatta() {
		mgr.scoreRegatta();
	}

	public Regatta() {
		super();

		fDivisions.addAll(DivisionList.getMasterList());
		for (Division div : fDivisions) {
			div.addPropertyChangeListener(this);
		}

		fReportOptionsList.add( ActionReportSeriesStandingsSingleStage.getDefaultReportOptions());
		fReportOptionsList.add( ActionReportOneRace.getDefaultReportOptions());
		fReportOptionsList.add( ActionReportScratch.getDefaultReportOptions());
		fReportOptionsList.add( ActionReportCheckin.getDefaultReportOptions());
		fReportOptionsList.add( ActionReportFinish.getDefaultReportOptions());
		fReportOptionsList.add( ActionReportRawFinish.getDefaultReportOptions()); // added

		mgr = new RegattaManager(this);
		
		fIsMultiStage = false;
		fScores = new SingleStageScoring(this);
	}

	public List<ReportOptions> getReportOptionsList() {
		return fReportOptionsList;
	}

	@Override public void xmlWrite(PersistentNode e) {
		fSaveVersion = JavaScoreProperties.getVersion();

		e.setAttribute(JAVASCORE_VERSION, fSaveVersion);
		e.setAttribute(VERSION_PROPERTY, Integer.toString(fVersion));
		e.setAttribute(NAME_PROPERTY, getName());
		e.setAttribute(PRO_PROPERTY, getPro());
		e.setAttribute(JURYCHAIR_PROPERTY, getJuryChair());
		e.setAttribute(HOSTCLUB_PROPERTY, getHostClub());
		e.setAttribute(DATES_PROPERTY, getDates());
		e.setAttribute(FINAL_PROPERTY, new Boolean(isFinal()).toString());
		e.setAttribute(MULTISTAGE_PROPERTY, new Boolean(isMultistage()).toString());
		e.setAttribute(USEBOWNUMBERS_PROPERTY, new Boolean(isUseBowNumbers()).toString());
		e.setAttribute(COMMENT_PROPERTY, fComment);
		e.setAttribute(IFEVENTID_PROPERTY, fIfEventId);
		e.setAttribute(DAILYSCORING_PROPERTY, new Boolean(fDailyScoring).toString());

		if (fDivisions.size() > 0)
			fDivisions.xmlWrite(e.createChildElement(DIVISIONLIST_PROPERTY), DIVISION_PROPERTY);
		if (fEntries.size() > 0)
			fEntries.xmlWrite(e.createChildElement( ENTRYLIST_PROPERTY), ENTRY_PROPERTY);
		if (fFleets.size() > 0)
			fFleets.xmlWrite(e.createChildElement(FLEETLIST_PROPERTY), FLEET_PROPERTY);
		if (fSubDivisions.size() > 0)
			fSubDivisions.xmlWrite(e.createChildElement(SUBDIVISIONLIST_PROPERTY), SUBDIVISION_PROPERTY);
		if (fRaces.size() > 0)
			fRaces.xmlWrite(e.createChildElement( RACELIST_PROPERTY), RACE_PROPERTY);

		fScores.xmlWrite(e.createChildElement( SCORES_PROPERTY));

		PersistentNode ero = e.createChildElement( REPORTOPTIONSLIST_PROPERTY);
		for (ReportOptions ro : getReportOptionsList()) {
			PersistentNode rpn = ero.createChildElement(REPORTOPTIONS_PROPERTY);
			ro.xmlWrite(rpn );
		}

	}

	public String getSaveVersion() {
		return fSaveVersion;
	}

	@SuppressWarnings("unchecked")
	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fVersion = 0;
		fSaveVersion = "";
		removeAllDivisions();

		String value = "";

		if ((value = n.getAttribute(VERSION_PROPERTY)) != null) {
			try {
				fVersion = Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		if ((value = n.getAttribute(JAVASCORE_VERSION)) != null)
			fSaveVersion = value;


		if ((value = n.getAttribute(NAME_PROPERTY)) != null)
			setName(value);
		if ((value = n.getAttribute(PRO_PROPERTY)) != null)
			setPro(value);
		if ((value = n.getAttribute(JURYCHAIR_PROPERTY)) != null)
			setJuryChair(value);
		if ((value = n.getAttribute(DATES_PROPERTY)) != null)
			setDates(value);
		if ((value = n.getAttribute(HOSTCLUB_PROPERTY)) != null)
			setHostClub(value);
		if ((value = n.getAttribute(COMMENT_PROPERTY)) != null)
			setComment(value);

		if ((value = n.getAttribute(IFEVENTID_PROPERTY)) != null)
			setIfEventId(value);

		if ((value = n.getAttribute(FINAL_PROPERTY)) != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setFinal(b);
			} catch (Exception e) {
			}
		}
		if ((value = n.getAttribute(USEBOWNUMBERS_PROPERTY)) != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setUseBowNumbers(b);
			} catch (Exception e) {
			}
		}

		PersistentNode n2 = null;

		if ((n2 = n.getElement(DIVISIONLIST_PROPERTY)) != null) {
			fDivisions.xmlRead(n2, this);
			fDivisions.remove(AbstractDivision.NONE); // just in case its
			// hiding here
			for (Division div : fDivisions) {
				div.addPropertyChangeListener(this);
			}
		}
		Collections.sort(fDivisions);


		if ((n2 = n.getElement(ENTRYLIST_PROPERTY)) != null) {
			fEntries.clear();
			fEntries.xmlRead(n2, this);
		}

		if ((n2 = n.getElement(FLEETLIST_PROPERTY)) != null) {
			fFleets.clear();
			fFleets.xmlRead(n2, this);
		}

		if ((n2 = n.getElement(SUBDIVISIONLIST_PROPERTY)) != null) {
			fSubDivisions.clear();
			fSubDivisions.xmlRead(n2, this);
		}

		if ((n2 = n.getElement(REPORTOPTIONSLIST_PROPERTY)) != null) {
			fReportOptionsList.clear();
			PersistentNode[] roKids = n2.getElements();
			for (int k2 = 0; k2 < roKids.length; k2++) {
				PersistentNode kid = roKids[k2];
				ReportOptions roNew = new ReportOptions();
				roNew.xmlRead(kid, this);
				if (ReportOptions.isValidReport(roNew)) {
					int i = findReportIndex(roNew);
					if (i >= 0)
						fReportOptionsList.set(i, roNew);
					else
						fReportOptionsList.add(roNew);
				}
			}
			if (fReportOptionsList.size() < 6) {
				fReportOptionsList.add(ActionReportRawFinish.getDefaultReportOptions());
				// added in 5.5
			}
		}

		if ((n2 = n.getElement(RACELIST_PROPERTY)) != null) {
			fRaces.clear();
			fRaces.xmlRead(n2, this);
		}

		String multiProperty = MULTISTAGE_PROPERTY;
		if ((value = n.getAttribute(multiProperty)) != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setMultistage(b);
			} catch (Exception e) {}
		}
		
		if ((n2 = n.getElement(SCORES_PROPERTY)) != null) {
			fScores.xmlRead(n2, this);
		}			
		
		value = n.getAttribute(DAILYSCORING_PROPERTY);
		if (value != null) {
			boolean b = value.toString().equalsIgnoreCase("true");
			try {
				setDailyScoring(b);
			} catch (Exception e) {}
		}


		if (fVersion < 201) {
			// need to rescore to get the seriespoint initialized correctly
			mgr.scoreRegatta();
		}
		
		// last thing
		fVersion = VERSION;
	}

	/**
	 * finds the index in the reportoptionslist for a specified report
	 * 
	 * @param ro
	 *            report that the index is wanted
	 * @return index of report or -1 if report is not found
	 */
	private int findReportIndex(ReportOptions ro) {
		for (int i = 0; i < fReportOptionsList.size(); i++) {
			ReportOptions roOld = fReportOptionsList.get(i);
			if (roOld.getName().equals(ro.getName()))
				return i;
		}
		return -1;
	}

	public boolean isFinal() {
		return fFinal;
	}

	public void setFinal(boolean b) {
		Boolean oldb = new Boolean(fFinal);
		fFinal = b;
		// fOverrideFinal = true;
		if (fFinal != oldb.booleanValue()) {
			try {
				JavaScore.getInstance().updateReports(false);
				mgr.writeRegattaToDisk();
			} catch (IOException e) {
				Util.showError(e, true);
			}
		}
		// fOverrideFinal = false;
		firePropertyChange(FINAL_PROPERTY, oldb, new Boolean(fFinal));
	}

	public boolean isMultistage() {
		return fIsMultiStage;
	}

	public void setMultistage(boolean b) {
		Boolean oldb = new Boolean(fIsMultiStage);
		fIsMultiStage = b;
		
		if (oldb != b) {
			if (fIsMultiStage) fScores = MultiStageScoring.createFromSingleStage( (SingleStageScoring)fScores);
			else fScores = SingleStageScoring.createFromMultiStage( (MultiStageScoring)fScores);
		}
		firePropertyChange(MULTISTAGE_PROPERTY, oldb, new Boolean(fIsMultiStage));
	}
	
	public void setDailyScoring(boolean b) {
		boolean oldb = fDailyScoring;
		fDailyScoring = b;
		
		if (oldb != b) {
			if (fDailyScoring) fScores = DailyStageScoring.createFromSingleStage( (SingleStageScoring) fScores);
			else fScores = SingleStageScoring.createFromMultiStage( (DailyStageScoring) fScores);
		}
		firePropertyChange( DAILYSCORING_PROPERTY, oldb, fDailyScoring);
	}

	public boolean isDailyScoring() {
		return fDailyScoring;
	}

	

	public int compareTo(Object obj) {
		if (!(obj instanceof Regatta))
			return -1;
		if (this == obj)
			return 0;

		Regatta that = (Regatta) obj;

		// equality of name, owner, sailid are covered by parent
		int i = fName.compareTo(that.fName);
		if (i != 0)
			return i;

		i = fDates.compareTo(that.fDates);
		if (i != 0)
			return i;

		i = fHostClub.compareTo(that.fHostClub);
		if (i != 0)
			return i;

		i = fPro.compareTo(that.fPro);
		if (i != 0)
			return i;

		i = fJuryChair.compareTo(that.fJuryChair);
		return i;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		try {
			Regatta that = (Regatta) obj;

			if (!Util.equalsWithNull(this.fName, that.fName))
				return false;
			if (!Util.equalsWithNull(this.fDates, that.fDates))
				return false;
			if (!Util.equalsWithNull(this.fHostClub, that.fHostClub))
				return false;
			if (!Util.equalsWithNull(this.fJuryChair, that.fJuryChair))
				return false;
			if (!Util.equalsWithNull(this.fPro, that.fPro))
				return false;
			if (!Util.equalsWithNull(this.fDivisions, that.fDivisions))
				return false;
			if (!Util.equalsWithNull(this.fEntries, that.fEntries))
				return false;
			if (!Util.equalsWithNull(this.fRaces, that.fRaces))
				return false;
			if (!Util.equalsWithNull(this.fScores, that.fScores))
				return false;

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * returns the Date the regatta was last saved to disk
	 * 
	 * @return save date
	 */
	public Date getSaveDate() {
		if (fSaveName == null)
			return null;
		File saveFile = getFileName();
		if (!saveFile.exists())
			return null;
		return new Date(saveFile.lastModified());
	}

	/**
	 * returns a File pointing to the regatta on disk
	 * 
	 * @return File containing the regatta's saved directory and name
	 */
	public File getFileName() {
		if (fSaveName == null) {
			return null;
		} else {
			return Util.getFile(fSaveDirectory, fSaveName);
		}
	}

	/**
	 * returns the file name of the regatta, after stripping the '.regatta' if
	 * there
	 */
	public String getBaseRegattaName() {
		// return regatta name or 'temp' if unnamed
		String name = getSaveName();
		if ( name != null) {
			int r = name.indexOf( ".regatta");
			if ( r >= 0) name = name.substring( 0, r);
		} else {
			name = "temp";
		}
		
		return name;
	}

	public int getVersion() {
		return fVersion;
	}

	public String getSaveDirectory() {
		return fSaveDirectory;
	}

	public String getSaveName() {
		return fSaveName;
	}

	/**
	 * sets the direcotry in which the regatta will be saved. This does NOT
	 * cause the regatta to be saved. Also, this value may be overwritten when a
	 * regatta is read in from disk.
	 */
	public void setSaveDirectory(String saveDir) {
		fSaveDirectory = saveDir;
		if (!fSaveDirectory.endsWith("/"))
			fSaveDirectory = fSaveDirectory + "/";
	}

	/**
	 * sets the name of the regatta file (not the directory). This does NOT
	 * cause the regatta to be saved. Also, this value may be overwritten when a
	 * regatta is read in from disk.
	 */
	public void setSaveName(String str) {
		fSaveName = str;
	}

	public String getName() {
		return fName;
	}

	public void setName(String newname) {
		String hold = fName;
		fName = newname;
		firePropertyChange(NAME_PROPERTY, hold, fName);
	}

	@Override public String toString() {
		StringBuffer s = new StringBuffer(getName());
		s.append(": ");
		s.append(MessageFormat.format(res.getString("RegattaNameSuffix"), new Object[] {
			new Integer((fEntries == null) ? 0 : fEntries.size()),
			new Integer((fRaces == null) ? 0 : fRaces.size()) }));
		return s.toString();
	}

	public DivisionList getNonEmptyDivisionList() {
		DivisionList divs = new DivisionList();
		for (Division div : fDivisions) {
			if (fEntries.hasDivision(div)) {
				divs.add(div);
			}
		}
		if (divs.size() == 0)
			divs.add(AbstractDivision.NONE);
		return divs;
	}

	@SuppressWarnings("unchecked")
	public void sortDivisions() {
		Collections.sort(fDivisions);
	}

	// returns NEW list for each call of all divisions, fleets, subdivs in regatta
	public List<AbstractDivision> getAllDivisions() {
		List<AbstractDivision> all = new ArrayList<AbstractDivision>();
		all.addAll( fDivisions);
		all.addAll( fSubDivisions);
		all.addAll( fFleets);
		return all;
	}
	
	public boolean hasDivision(Division div) {
		return fDivisions.contains(div);
	}

	public org.gromurph.javascore.gui.DefaultListModel getDivisionModel() {
		return fDivisions.getListModel();
	}

	public void removeAllDivisions() {
		fDivisions.clear();
	}

	public DivisionList getDivisions() {
		DivisionList list = new DivisionList();
		if (fDivisions != null)
			list.addAll(fDivisions);
		return list;
	}

	public DivisionList getActiveDivisions() {
		DivisionList list = new DivisionList();
		if (fDivisions != null) {
			for (Division div : fDivisions) {
				if (div.getNumEntries() > 0 ) list.add(div);
			}
		}
		return list;
	}

	public Division getDivision(String name) {
		return fDivisions.find(name);
	}

	public int getNumDivisions() {
		return fDivisions.size();
	}

	public void addDivision(Division div) {
		fDivisions.add(div);
		if (isMultistage()) {
			((MultiStageScoring) fScores).addDivisionToStages(div);
		}
		div.addPropertyChangeListener(this);
	}

	public void removeDivision(Division div) {
		div.removePropertyChangeListener(this);
		if (isMultistage()) {
			((MultiStageScoring) fScores).removeDivisionFromStages(div);			
		}
		fDivisions.remove(div);
	}

	@Override public void propertyChange(PropertyChangeEvent event) {
		// Division changes come through here, but nothing (yet)
		// needs to be done.
	}

	/**
	 * @todo deprecate getFleetList
	 * @return
	 */
	public FleetList getFleets() {
		FleetList list = new FleetList();
		if (fFleets != null)
			list.addAll(fFleets);
		return list;
	}

	public FleetList retrieveFleetsForEditing() {
		return fFleets;
	}

	public Fleet getFleet(String name) {
		return fFleets.find(name);
	}

	public int getNumFleets() {
		return fFleets.size();
	}

	public void addFleet(Fleet f) {
		fFleets.add(f);
	}

	public void removeFleet(Fleet f) {
		fFleets.remove(f);
	}

	@SuppressWarnings("unchecked")
	public SubDivisionList getSubDivisions() {
		SubDivisionList list = new SubDivisionList();
		if (fSubDivisions != null)
			list.addAll(fSubDivisions);
		Collections.sort(list); // sort to include rank of subdivisions
		return list;
	}

	/**
	 * @return
	 */
	public SubDivisionList retrieveSubDivisionsForEditing() {
		return fSubDivisions;
	}

	public SubDivision getSubDivision(String name) {
		return fSubDivisions.find(name);
	}

	public void addSubDivision(SubDivision div) {
		fSubDivisions.add(div);
	}

	public void removeSubDivision(SubDivision div) {
		fSubDivisions.remove(div);
	}

	public int getNumSubDivisions() {
		return fSubDivisions.size();
	}

	public int getNumQualifyingDivisions() {
		int i = 0;
		for (SubDivision sub : fSubDivisions) {
			if (sub.isGroupQualifying())
				i++;
		}
		return i;
	}

	public RaceList getRaces() {
		if (fRaces == null)
			fRaces = new RaceList();
		return fRaces;
	}

	public void addRace(Race r) {
		fRaces.add(r);
		r.setParentRegatta(this);
	}

	public Race getRace(String name) {
		if (fRaces == null)
			return null;
		return fRaces.getRace(name);
	}

	public Race getRaceIndex(int i) {
		return fRaces.get(i);
	}

	public Race getRaceId(int i) {
		return fRaces.getRace(i);
	}

	public int getNumRaces() {
		return fRaces.size();
	}

	public Iterator<Race> races() {
		return fRaces.iterator();
	}

	public ReportOptions getReportOptions(String reportName) {
		if (reportName == null)
			return new ReportOptions("");

		ReportOptions ro = null;
		for (ReportOptions rox : getReportOptionsList()) {
			if (rox.getName().equalsIgnoreCase(reportName))
				ro = rox;
		}
		if (ro == null) {
			ro = new ReportOptions(reportName);
			fReportOptionsList.add(ro);
		}
		return ro;
	}

	/**
	 * @todo depreceate getallentries
	 * @return
	 */
	public EntryList getAllEntries() {
		if (fEntries == null)
			fEntries = new EntryList();
		return fEntries;
	}

	public Entry getEntry(int id) {
		return fEntries.getEntry(id);
	}

	public Iterator<Entry> entries() {
		return getAllEntries().iterator();
	}

	public int getNumEntries() {
		return fEntries.size();
	}

	public void addEntry(Entry e) {
		fEntries.add(e);
	}

	public void removeEntry(Entry e) {
		fEntries.remove(e);
	}

	public void setDates(String inDates) {
		String hold = fDates;
		fDates = inDates;
		firePropertyChange(DATES_PROPERTY, hold, fDates);
	}

	public String getDates() {
		return fDates;
	}

	public RegattaScoringModel getScoringManager() {
		return fScores;
	}

	public String getIfEventId() {
		return fIfEventId;
	}

	public void setIfEventId(String id) {
		fIfEventId = id;
	}

	public String getComment() {
		return fComment;
	}

	public void setComment(String comment) {
		fComment = comment;
	}

	public void setJuryChair(String inJuryChair) {
		String hold = fJuryChair;
		fJuryChair = inJuryChair;
		firePropertyChange(JURYCHAIR_PROPERTY, hold, fJuryChair);
	}

	public String getJuryChair() {
		return fJuryChair;
	}

	public void setPro(String inPro) {
		String hold = fPro;
		fPro = inPro;
		firePropertyChange(PRO_PROPERTY, hold, fPro);
	}

	public String getPro() {
		return fPro;
	}

	public void setHostClub(String inHostClub) {
		String hold = fHostClub;
		fHostClub = inHostClub;
		firePropertyChange(HOSTCLUB_PROPERTY, hold, fHostClub);
	}

	public String getHostClub() {
		return fHostClub;
	}

	public void setUseBowNumbers(boolean inB) {
		Boolean hold = new Boolean(fUseBowNumbers);
		fUseBowNumbers = inB;
		firePropertyChange(USEBOWNUMBERS_PROPERTY, hold, new Boolean(fUseBowNumbers));
	}

	public boolean isUseBowNumbers() {
		return fUseBowNumbers;
	}

	/**
	 * returns a list of all finishes found for entry e
	 */
	public Map<Race, Finish> getAllFinishes(Entry e) {
		Map<Race, Finish> map = new HashMap<Race, Finish>();
		for (Race r : fRaces) {
			Finish f = r.getFinish(e);
			if (!f.isNoFinish())
				map.put(r, f);
		}
		return map;
	}

}
/**
 * $Log: Regatta.java,v $ Revision 1.10 2006/05/19 05:48:42 sandyg final release
 * 5.1 modifications
 * 
 * Revision 1.9 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.8 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.6 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.5 2006/01/14 21:06:55 sandyg final bug fixes for 5.01.1. All tests
 * work
 * 
 * Revision 1.4 2006/01/14 14:40:35 sandyg added some @suppresswarnings on
 * warnings that I could not code around
 * 
 * Revision 1.3 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/08 04:18:33 sandyg Fixed reporting error on finals
 * divisions, cleaned up gui on qual/final races (hiding divisions that should
 * not have their "participating" flags changed)
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.19.4.2 2005/11/01 18:05:00 sandyg fixed mark rounding order and
 * lost report options - both problems discovered during 2005 RIWKC
 * 
 * Revision 1.19.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.19.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.19 2005/05/26 01:45:42 sandyg fixing resource access/lookup
 * problems
 * 
 * Revision 1.18 2005/04/27 02:45:47 sandyg Added Yardstick, and added Yardstick
 * and IRC all to GUI. Portsmouth now trivial subclass of yardstick
 * 
 * Revision 1.17 2004/05/06 02:11:50 sandyg Beta support for revised
 * Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.16 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.15 2003/11/23 23:15:10 sandyg Request #838429, checks for newer
 * and older versions of regatta files
 * 
 * Revision 1.14 2003/11/23 20:34:45 sandyg starting release 4.2, minor cleanup
 * 
 * Revision 1.13 2003/07/10 01:55:43 sandyg Better IO checking errors on invalid
 * regatta files
 * 
 * Revision 1.12 2003/04/27 21:03:28 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.11 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.10 2003/03/27 02:47:01 sandyg Completes fixing [ 584501 ] Can't
 * change division splits in open reg
 * 
 * Revision 1.9 2003/03/16 20:39:44 sandyg 3.9.2 release: encapsulated changes
 * to division list in Regatta, fixed a bad bug in PanelDivsion/Rating
 * 
 * Revision 1.8 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.7 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
