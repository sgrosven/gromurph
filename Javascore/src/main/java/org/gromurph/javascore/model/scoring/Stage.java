// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringManager.java,v 1.12 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Points;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.StartingDivisionList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.SubDivisionList;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

public class Stage extends BaseObject implements StageScoringModel, Constants {
	
	public Stage() { this(null);}
	
	public Stage(RegattaScoringModel mgr) {
		id = sNextId++;
		parentMgr = mgr;
		stageRank = 1;	
		divisions = new ArrayList<AbstractDivision>();
		scoreCarryOver = ScoreCarryOver.ALL;
		throwoutCarryOver =ThrowoutCarryOver.ALL;
		tiebreakCarryOver = TiebreakCarryOver.ALL;
		prevStage = null;
	}

	private Integer id;
	private static int sNextId = 1;
	public static final String ID_PROPERTY = "StageId";

	private void setId(String iid) {
		id = new Integer(iid);
		if (id >= sNextId)
			sNextId = id + 1;
	}

	public int getId() {
		return id;
	}

	public void setRegattaScoringModel( RegattaScoringModel rsm) {
		parentMgr = rsm;
	}
	public RegattaScoringModel getRegattaScoringModel( ) {
		return parentMgr;
	}

	protected Stage prevStage;
	

	@Override public String toString() { return name; }
	
	public static class SortBottomToTop implements Comparator<Stage> {
		public int compare( Stage left, Stage right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;

			return (( Integer) left.stageRank).compareTo(right.stageRank);
		}
	}
	
	public static class SortTopToBottom implements Comparator<Stage> {
		public int compare( Stage left, Stage right) {
			if (left == null && right == null) return 0;
			if (left == null) return 1;
			if (right == null) return -1;

			return (( Integer) right.stageRank).compareTo(left.stageRank);
		}
	}
	
	// some standardized Stage names, mostly for backward compatibility
	public static final String MEDAL = "Medal";
	public static final String QUALIFYING = "Qualifying";
	public static final String GOLD = "Gold";
	public static final String SILVER = "Silver";
	public static final String FLEET = "Fleet";
	
	protected RegattaScoringModel parentMgr;
	protected String name;
	protected int stageRank;
	protected List<AbstractDivision> divisions;
	protected ScoreCarryOver scoreCarryOver;
	protected ThrowoutCarryOver throwoutCarryOver;
	protected TiebreakCarryOver tiebreakCarryOver;
	protected boolean combinedQualifying;
	
	protected ScoringModel scoringModel = new ScoringLowPoint();
	
	/**
	 * key will be entry and division, value will be series total points
	 */
	protected SeriesPointsList fSeries = new SeriesPointsList();

	static ResourceBundle res = JavaScoreProperties.getResources();

	/**
	 * contain RacePoints objects, one for each entry in each race
	 */
	protected RacePointsList fPointsList = new RacePointsList();

//	public Regatta getRegatta() { return fRegatta; }

	/**
	 * list of strings containing Warning Messages
	 */
	protected static WarningList sWarnings = new WarningList();

	/**
	 * list of warning messages generated during last scoring run
	 * 
	 * @return list of warnings
	 */
	public static WarningList getWarnings() {
		return sWarnings;
	}

	/**
	 * returns list of all racepoints
	 * 
	 * @return RacePointsList
	 */
	public RacePointsList getRacePointsList( ) {
		return fPointsList;
	}
	
	public RacePointsList getRacePointsForEntries( EntryList entries, boolean includeCarryover) {
		RacePointsList l = fPointsList.findAll(entries);
		if (!includeCarryover || scoreCarryOver == ScoreCarryOver.NONE) {
			return l;
		}
		if (scoreCarryOver == ScoreCarryOver.ALL) {	
			if (prevStage != null) l.addAll(prevStage.getRacePointsForEntries( entries, true));
		}
		return l;
	}

	protected Map<String, EntryList> divisionEntries;
	
	public void scoreRaces() throws ScoringException {
		if (combinedQualifying) {
			scoreRaces_Qualifying();
    	} else {
    		scoreRaces_NonQualifying();
    	}
	}
	private void scoreRaces_NonQualifying() throws ScoringException {
		Regatta regatta = JavaScoreProperties.getRegatta();

		for (AbstractDivision div : getDivisions()) {

			divisionEntries.put( div.getName(), new EntryList());

			// calc races points for each race and each division in THIS stage in the race
			for (Race race : regatta.getRaces()) {
				logger.trace( " scoring {}", race.getName());
				RacePointsList divPointsList = scoreDivisionRace(div, race);
				fPointsList.addAll(divPointsList);
			}
		}
	}
	
	private void scoreRaces_Qualifying() throws ScoringException {
		Regatta regatta = JavaScoreProperties.getRegatta();

		// calc races points for each race and each division in THIS stage in the race
		for (Race race : regatta.getRaces()) {
			logger.trace( " scoring {}", race.getName());
			RacePointsList divPointsList = scoreQualifyingRace(race);
			fPointsList.addAll(divPointsList);
		}  
	}
	
	private List<SubDivisionList> getQualifyingMiniFleets(List<AbstractDivision> divs, Race race) {
		List<SubDivisionList> qualGroups = new ArrayList<SubDivisionList>(10);

		SubDivisionList quals = new SubDivisionList();
		for (AbstractDivision adiv : race.getStartingDivisions(true)) {
			if (adiv instanceof SubDivision && adiv.isGroupQualifying()) quals.add( (SubDivision) adiv);
		}

		// until this is empty run the loop
		while (quals.size() > 0) {
			SubDivisionList minifleet = new SubDivisionList();
			qualGroups.add(minifleet);

			SubDivision subdiv = quals.get(0);
			long starttime = race.getStartTimeAdjusted(subdiv);
			minifleet.add(subdiv);
			quals.remove(subdiv);

			for (int d = 0; d < quals.size(); d++) {
				subdiv = quals.get(d);
				long subdivStart = race.getStartTimeAdjusted(subdiv);
				if (starttime != SailTime.NOTIME && subdivStart != SailTime.NOTIME && starttime == subdivStart) {
					minifleet.add(subdiv);
					//quals.remove(subdiv);
				}
			}
			quals.removeAll(minifleet);
		}
		return qualGroups;
	}

	private RacePointsList scoreQualifyingRace(  Race race) throws ScoringException {
		List<AbstractDivision> divs = getDivisions();
		
		RacePointsList divPointsList = new RacePointsList();
		
		// build list of subdivs starting this race in this stage
		StartingDivisionList startingDivs = new StartingDivisionList();
		for (AbstractDivision d : race.getStartingDivisions(true)) {
			if (this.getDivisions().contains(d)) startingDivs.add( d);
		}
		
		List<SubDivisionList> miniFleet = getQualifyingMiniFleets(startingDivs, race);
		Regatta regatta = JavaScoreProperties.getRegatta();

		// handling combo minifleets first
		for (SubDivisionList subDivs : miniFleet) {
			RacePointsList racePoints = new RacePointsList();

			for (SubDivision subdiv : subDivs) {
				EntryList sEntries = subdiv.getEntries();
				
    			for (Entry e : sEntries) {
    				racePoints.add(newRacePoints(race, e, subdiv));
    			}
    			
    			// post entries to div entries for series calculations
				String pName = subdiv.getParentDivision().getName();
				EntryList divEntries = divisionEntries.get( pName);
				if (divEntries == null) {
					divEntries = new EntryList();
					divisionEntries.put( pName,  divEntries);
				}
				divEntries.addAll(sEntries);
				startingDivs.remove(subdiv);
			}

			if (racePoints.size() > 0) {
				scoringModel.scoreRace(race, racePoints, false);
			}

			divPointsList.addAll(racePoints);
		}
		
		// now look for what is left
		for (AbstractDivision subdiv : startingDivs) {
			
			EntryList sEntries = subdiv.getEntries();
			
			RacePointsList racePoints = new RacePointsList();
			for (Entry e : sEntries) {
				racePoints.add(newRacePoints(race, e, subdiv));
			}
			
			// post entries to div entries for series calculations
			String pName = subdiv.getParentDivision().getName();
			EntryList divEntries = divisionEntries.get( pName);
			if (divEntries == null) {
				divEntries = new EntryList();
				divisionEntries.put( pName,  divEntries);
			}
			divEntries.addAll(sEntries);

			if (racePoints.size() > 0) {
				scoringModel.scoreRace(race, racePoints, false);
			}

			divPointsList.addAll(racePoints);
			
		}
		
		return divPointsList;
	}
	
	public void scoreStageSeries() throws ScoringException {
		if (combinedQualifying) scoreStageSeries_Qualifying();
		else scoreStageSeries_NonQualifying();
	}
	private void scoreStageSeries_Qualifying() throws ScoringException {	
		List<AbstractDivision> scoringdivs = new ArrayList<AbstractDivision>();
		for (AbstractDivision ad : getDivisions()) {
			RacePointsList divPointsList = new RacePointsList();
			if (ad instanceof SubDivision) {
				if (!scoringdivs.contains(ad.getParentDivision())) scoringdivs.add(ad.getParentDivision());
			} else {
				if (!scoringdivs.contains(ad)) scoringdivs.add( ad);
			}
		}
		
		EntryList el =  fPointsList.getAllEntries();
		for (AbstractDivision pdiv : scoringdivs) {
			
			RacePointsList divPointsList = new RacePointsList();
			for ( RacePoints rp : fPointsList) {
				if (pdiv.contains( rp.getDivision())) divPointsList.add(rp);
			}
 			scoreSeries(pdiv, divPointsList, el);
		}

	}
	private void scoreStageSeries_NonQualifying() throws ScoringException {
		
		EntryList el = fPointsList.getAllEntries();
		for (AbstractDivision div : getDivisions()) {
    		// nothin' in here to score a series on
    		if (fPointsList.size() == 0) return;  
    		RacePointsList divPointsList = fPointsList.findAll(div);
    		scoreSeries(div, divPointsList, el);
		}

	}

	protected RacePointsList scoreDivisionRace(AbstractDivision div, Race race)
			throws ScoringException {
		
		Regatta regatta = JavaScoreProperties.getRegatta();

		// the division has no subfleets racing in it
		RacePointsList racePoints = new RacePointsList();

		if ( div.isRacing(race, combinedQualifying)) {
			EntryList entries = div.getEntries();

    		for (Entry e : entries) {
    			racePoints.add( newRacePoints( race, e, div));
    		}
			scoringModel.scoreRace(race, racePoints, false);
			
			EntryList divEntries = divisionEntries.get( div.getName());
    		ScoringUtilities.mergeEntries(divEntries, entries);
		}
		return racePoints;
	}
		
	protected RacePoints newRacePoints( Race r, Entry e, AbstractDivision d) {
		RacePoints rp = new RacePoints( r, e, d, Double.NaN, false);
		rp.stage = this;
		return rp;
	}
	
	/**
	 * returns the list of series points for an entry
	 * 
	 * @param entry
	 *            entry whose points are wanted
	 * @param div
	 *            division in which an entry's points are sought
	 * @return list of seriespoints
	 */
	public SeriesPoints getStageSeriesPoints(Entry entry, AbstractDivision div) {
		return fSeries.find(entry, div);
	}

	/**
	 * returns the list of series points for all enries in div
	 * 
	 * @param entry
	 *            entry whose points are wanted
	 * @return list of seriespoints
	 */
	public SeriesPointsList getAllSeriesPoints(AbstractDivision div) {
		SeriesPointsList myDivOnly = new SeriesPointsList();
//		if (divisions.contains(div)) {
//			return fSeries.findAll(div);
//		} else {
			for (AbstractDivision ad : divisions) {
				if (ad instanceof Fleet &&  ( (Fleet) ad).contains(div)) {
					SeriesPointsList wholeFleet = fSeries.findAll(ad);
					
					for ( SeriesPoints sp : wholeFleet) {
						if (div.contains( sp.getEntry())) myDivOnly.add(sp);
					}
					
				} else if (ad instanceof SubDivision &&  div.contains(ad)) {
					myDivOnly.addAll( fSeries.findAll(ad));
				} else if (div == ad) {
					myDivOnly.addAll( fSeries.findAll(ad));
				}
			}
//		}
		return myDivOnly;
	}

	/**
	 * returns list of all seriespoints
	 * 
	 * @return SeriesPointsList
	 */
	public SeriesPointsList getAllSeriesPoints() {
		return fSeries;
	}
	
	public List<String> getSeriesScoringNotes(RacePointsList rp) {
		return scoringModel.getSeriesScoringNotes(rp);
	}

	protected SeriesPointsList scoreSeries(AbstractDivision div, RacePointsList divPointsList, EntryList remainingEntries) throws ScoringException {
		
		logger.trace("scoring stage series for {}", div.getName());
		
		EntryList allEntries = divisionEntries.get( div.getName());
		EntryList entries = new EntryList();
		for (RacePoints p : divPointsList) {
			Entry e = p.getEntry();
			if (remainingEntries.contains(e)) {
				entries.add(e);
				remainingEntries.remove(e);
			}
		}
		
		// when we get here can assume that fPointsList contains race points for all divisions and all races
		// for this stage, to calc series, need to create list of all race points for each this division
		// and for any carryover divisions.
		
		if ( this.scoreCarryOver == ScoreCarryOver.ALL && prevStage != null) {
			// can assume that scoreDivisions set the prevstage
			// want to pull in previous race points for entries in 'div'
			RacePointsList prevPts = prevStage.getRacePointsForEntries( entries, true);
			divPointsList.addAll( prevPts);
		} else if (this.scoreCarryOver == ScoreCarryOver.SEEDINGRACE) {
			//throw new ScoringException("SeedingRace carryover not yet implemented");
		}

		SeriesPointsList divSeriesPoints = scoringModel.scoreSeries( div, entries, divPointsList);
		for (Points sp : divSeriesPoints) sp.stage = this;
		fSeries.addAll(divSeriesPoints);
		
		return divSeriesPoints; // returning these so that a division of finals
		// subdivisions can create its seriespoints too
	}

	public void clearResults() {
		fPointsList.clear();
		fSeries.clear();		
		divisionEntries = new HashMap<String, EntryList>();
	}
	
	protected static int sCounter = 1;
	
	public transient static final String NAME_PROPERTY = "Name";
	public transient static final String SCORINGSYSTEM_PROPERTY = "ScoringSystem";
	public transient static final String SCORINGOPTIONS_PROPERTY = "ScoringOptions";
	public transient static final String STAGERANK_PROPERTY = "StageRank";
	public transient static final String DIVISIONS_PROPERTY = "Divisions";
	public transient static final String DIVISIONID_PROPERTY = "DivisionId";
	public transient static final String SCORECARRYOVER_PROPERTY = "ScoreCarryOver";
	public transient static final String THROWOUTCARRYOVER_PROPERTY = "ThrowoutCarryOver";
	public transient static final String TIEBREAKCARRYOVER_PROPERTY = "TiebreakCarryOver";
	public transient static final String PREVIOUSSTAGE_PROPERTY = "PreviousStage";
	public transient static final String COMBINEDQUALIFYING_PROPERTY = "CombinedQualifying";
	
	protected static final String SYSTEM_PROPERTY = "System";
	protected static final String SYSTEMNAME_PROPERTY = "SystemName";
	protected static final String RACEPOINTSLIST_PROPERTY = "RacePointsList";
	protected static final String SERIESPOINTSLIST_PROPERTY = "SeriesPointsList";
	protected static final String RACEPOINTS_PROPERTY = "rp";
	protected static final String SERIESPOINTS_PROPERTY = "sp";

	@Override public void xmlWrite(PersistentNode e) {
		
		e.setAttribute( ID_PROPERTY, Integer.toString(getId()));
		e.setAttribute( SYSTEMNAME_PROPERTY, scoringModel.getName());
		e.setAttribute( NAME_PROPERTY, getName());
		e.setAttribute( STAGERANK_PROPERTY, Integer.toString(getStageRank()));
		e.setAttribute( SCORECARRYOVER_PROPERTY,  getScoreCarryOver().name());
		e.setAttribute( TIEBREAKCARRYOVER_PROPERTY,  getTiebreakCarryOver().name());
		e.setAttribute( THROWOUTCARRYOVER_PROPERTY,  getThrowoutCarryOver().name());
		e.setAttribute( COMBINEDQUALIFYING_PROPERTY, Boolean.toString(isCombinedQualifying()));
		
		if ( prevStage != null) {
			e.setAttribute( PREVIOUSSTAGE_PROPERTY, Integer.toString(prevStage.getId()));
			// note: this is read back in by MultiStage !
		}
		
		PersistentNode childNode = e.createChildElement( DIVISIONS_PROPERTY);
		for (AbstractDivision div : divisions) {
			PersistentNode dd = childNode.createChildElement( DIVISIONID_PROPERTY);
			dd.setText( div.getName());
		}
		
		scoringModel.getOptions().xmlWrite(e.createChildElement(SYSTEM_PROPERTY));

		if (fPointsList != null && fPointsList.size() > 0) {
			for (Points sp : fPointsList) sp.stage = this;
			fPointsList.xmlWrite(e.createChildElement(RACEPOINTSLIST_PROPERTY), RACEPOINTS_PROPERTY);
		}
		
		if (fSeries != null && fSeries.size() > 0) {
			for (Points sp : fSeries) sp.stage = this;
			fSeries.xmlWrite(e.createChildElement( SERIESPOINTSLIST_PROPERTY), SERIESPOINTS_PROPERTY);
		}
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fPointsList.clear();
		fSeries.clear();
		scoringModel = null;

		String value = n.getAttribute(ID_PROPERTY);
		if (value != null) setId(value);

		value = n.getAttribute(NAME_PROPERTY);
		if (value != null) setName(value);
		
		value = n.getAttribute(COMBINEDQUALIFYING_PROPERTY);
		if (value != null) setCombinedQualifying(Boolean.parseBoolean(value));
		
		// only miami 2013 regatta will have stageorder
		String STAGEORDER_PROPERTY = "StageOrder";
		value = n.getAttribute(STAGEORDER_PROPERTY);
		if (value == null) value = n.getAttribute(STAGERANK_PROPERTY);
		if (value != null) setStageRank( Integer.parseInt(value));

		value = n.getAttribute(SCORECARRYOVER_PROPERTY);
		if (value != null) setScoreCarryOver( ScoreCarryOver.valueOf(value));

		value = n.getAttribute(THROWOUTCARRYOVER_PROPERTY);
		if (value != null) setThrowoutCarryOver( ThrowoutCarryOver.valueOf(value));

		value = n.getAttribute(TIEBREAKCARRYOVER_PROPERTY);
		if (value != null) setTiebreakCarryOver( TiebreakCarryOver.valueOf(value));

		PersistentNode child;
		
		// divisions, subdivision, fleets should already have been read in...
		if ((child = n.getElement(DIVISIONS_PROPERTY)) != null) {
			Regatta reg = (Regatta) rootObject;
			divisions = new ArrayList<AbstractDivision>();
			for (PersistentNode divnode : child.getElements()) {
				if (divnode.getName().equals(DIVISIONID_PROPERTY)) {
    				String divId = divnode.getText();
    				AbstractDivision div = null;
    				div = reg.getDivision(divId);
    				if (div == null) div = reg.getSubDivision(divId);
    				if (div == null) div = reg.getFleet(divId);
    				if (div != null) divisions.add(div);
    			}
    		}
		}

		value = n.getAttribute(SYSTEMNAME_PROPERTY);
		if (value != null) setModel(value);

		if (scoringModel == null) setModel(ScoringLowPoint.NAME);

		PersistentNode n2 = null;
		n2 = n.getElement(SYSTEM_PROPERTY);
		if (n2 != null) {
			scoringModel.getOptions().xmlRead(n2, rootObject);
		}

		n2 = n.getElement(RACEPOINTSLIST_PROPERTY);
		if (n2 != null) {
			fPointsList.xmlRead(n2, rootObject);
			for (Points sp : fPointsList) sp.stage = this;
		}

		n2 = n.getElement(SERIESPOINTSLIST_PROPERTY);
		if (n2 != null) {
			fSeries.xmlRead(n2, rootObject);
			for (Points sp : fSeries) sp.stage = this;
		}

	}


	protected String tempLastRaceIdBeforeSplit;
	protected Race tempLastRaceBeforeSplit;
	
	public void pre600_setLastRaceBeforeSplit( String raceid) {
		tempLastRaceIdBeforeSplit = raceid;
	}
	
	public Race getLastRaceBeforeSplit() {
		if (tempLastRaceIdBeforeSplit == null) return null;
		if (tempLastRaceBeforeSplit != null) return tempLastRaceBeforeSplit;
		if (JavaScoreProperties.getRegatta() == null) return null;
			
		tempLastRaceBeforeSplit = JavaScoreProperties.getRegatta().getRaceId(Integer.parseInt(tempLastRaceIdBeforeSplit));
		return tempLastRaceBeforeSplit;
	}
	
	public void setLastRaceBeforeSplit( Race lr) {
		tempLastRaceBeforeSplit = lr;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String newv) {
		String oldv = name;
		this.name = newv;
		firePropertyChange( NAME_PROPERTY, oldv, newv);
	}

	public int getStageRank() {
		return stageRank;
	}

	// this is the overall ranking of stages... gold ranks over silver etc
	// used for final rankings
	public void setStageRank(int newValue) {
		int oldValue = stageRank;
		this.stageRank = newValue;
		firePropertyChange( STAGERANK_PROPERTY, oldValue, newValue);
	}

	public List<AbstractDivision> getDivisions() {
		return divisions;
	}

	public void addDivision( AbstractDivision div) {
		if (!hasDivision(div.getName())) divisions.add(div);
	}

	public void removeDivision( AbstractDivision div) {
		if (hasDivision(div.getName())) divisions.remove(div);
	}

	public boolean hasDivision(String name) {
		for (AbstractDivision div : divisions) {
			if (div.getName().equals(name)) return true;
		}
		return false;
	}
	public AbstractDivision find(String name) {
		for (AbstractDivision div : divisions) {
			if (div.getName().equals(name)) return div;
		}
		return null;
	}

	public ScoreCarryOver getScoreCarryOver() {
		return scoreCarryOver;
	}

	public void setScoreCarryOver( ScoreCarryOver newv) {
		ScoreCarryOver oldv = scoreCarryOver;
		this.scoreCarryOver = newv;
		firePropertyChange( SCORECARRYOVER_PROPERTY, oldv, newv);
	}

	public ThrowoutCarryOver getThrowoutCarryOver() {
		return throwoutCarryOver;
	}

	public void setThrowoutCarryOver(ThrowoutCarryOver newValue) {
		ThrowoutCarryOver oldValue = throwoutCarryOver;
		this.throwoutCarryOver = newValue;
		firePropertyChange( THROWOUTCARRYOVER_PROPERTY, oldValue, newValue);
	}

	public TiebreakCarryOver getTiebreakCarryOver() {
		return tiebreakCarryOver;
	}

	public void setTiebreakCarryOver(TiebreakCarryOver newValue) {
		TiebreakCarryOver oldValue = tiebreakCarryOver;
		this.tiebreakCarryOver = newValue;
		firePropertyChange(TIEBREAKCARRYOVER_PROPERTY, oldValue, newValue);
	}

	/**
	 * trivial implementation
	 * 
	 * @param obj
	 * @return int
	 */
	public int compareTo(Object obj) {
		if (!(obj instanceof Stage)) return -1;
		Stage that = (Stage) obj;
		if (this.stageRank < that.stageRank) return -1;
		if (this.stageRank >= that.stageRank) return 1;
		
		return this.id.compareTo(that.id);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		try {
			Stage that = (Stage) obj;

			// if (!Util.equalsWithNull( this.fRaces, that.fRaces)) return
			// false;
			if (!Util.equalsWithNull(this.name, that.name))
				return false;
			// if (!Util.equalsWithNull( this.fPointsList, that.fPointsList))
			// return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * sets the active scoring model
	 * 
	 * @param modelName
	 *            name of model to be added
	 */
	public void setModel(String modelName) {
		ScoringLowPoint newModel = (ScoringLowPoint)ScoringUtilities.createScoringModel(modelName);
		// copy over the throwouts and other stuff
		if (scoringModel != null && newModel != null)
			newModel.getOptions().setAttributes(scoringModel.getOptions());
		setModel( newModel);
	}
	
	public void setModel(ScoringModel newv) {
		ScoringModel oldv = scoringModel;
		scoringModel = newv;
		firePropertyChange( SCORINGSYSTEM_PROPERTY, oldv, newv);
	}
	
	public void setOptions( ScoringOptions opt) {
		if (scoringModel != null) scoringModel.setOptions( opt);
	}

	/**
	 * returns the active scoring model
	 * 
	 * @return current ScoringModel
	 */
	public ScoringModel getModel() {
		return scoringModel;
	}
	
	public ScoringOptions getOptions() {
		return (scoringModel == null) ? null : scoringModel.getOptions();
	}

	public boolean isCombinedQualifying() {
		return combinedQualifying;
	}

	public void setCombinedQualifying(boolean newv) {
		boolean oldv = combinedQualifying;
		combinedQualifying = newv;
		firePropertyChange( COMBINEDQUALIFYING_PROPERTY, oldv, newv);
	}

	public Stage getPrevStage() {
		return prevStage;
	}

	public void setPrevStage(Stage newv) {
		Stage oldv = prevStage;
		this.prevStage = newv;
		firePropertyChange( PREVIOUSSTAGE_PROPERTY, oldv, newv);
	}


}
/**
 * $Log: ScoringManager.java,v $ Revision 1.12 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
