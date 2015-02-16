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

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.FleetList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.SubDivisionList;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

/**
 * Parent holder of information about scoring the races in a regatta. Provides all the covering information calculating
 * race and series points for a set of entries (a division or fleet) in a set of races.
 * <p>
 * One instance of this class will exist for every "series" in a regatta For now there is only one class in a regatta,
 * so all boats are in all race and there will only be one ScoringManager instance.
 * <p>
 * But when multi-classes and possibly overall fleet results come in, then there will be one of these for each scored
 * class, and each scored fleet.
 **/
public class SingleStage extends BaseObject 
		implements RegattaScoringModel, StageScoringModel, Constants {
	
	public SingleStage( Regatta reg) {
		super();
		setRegatta(reg);
	}
	
	public static SingleStage createFromMultiStage( MultiStage ms) {
		SingleStage ss = new SingleStage(null);
		
		if (ms.getNumStages() > 0) {
			Stage s = ms.getStages().get(0);
			ss.setModel( s.getModel());
		}
		
		return ss;
	}
	
	public int getNumStages() { return 1;}

	public Stage getNextStage( Stage s) { return null;}
	public Stage getPreviousStage( Stage s) { return null;}
	
	static ResourceBundle res = JavaScoreProperties.getResources();

	/**
	 * contain RacePoints objects, one for each entry in each race
	 */
	protected RacePointsList fPointsList = new RacePointsList();

	private Regatta _regatta;
	private Regatta getRegatta() { 
		if (_regatta == null) {
			_regatta = JavaScoreProperties.getRegatta();
		}
		return _regatta;
	}
	private void setRegatta( Regatta r) {
		_regatta = r;
	}

	/**
	 * list of strings containing Warning Messages
	 */
	protected static WarningList sWarnings = new WarningList();

	protected ScoringModel fModel = new ScoringLowPoint();

	public List<String> getSeriesScoringNotes(RacePointsList rp) {
		return fModel.getSeriesScoringNotes(rp);
	}

	/**
	 * key will be entry and division, value will be series total points
	 */
	protected SeriesPointsList fSeries = new SeriesPointsList();

	/**
	 * list of warning messages generated during last scoring run
	 * 
	 * @return list of warnings
	 */
	public static WarningList getWarnings() {
		return sWarnings;
	}

	/**
	 * trivial implementation
	 * 
	 * @param obj
	 * @return int
	 */
	public int compareTo(Object obj) {
		return 0;
	}

	/**
	 * returns list of all racepoints
	 * 
	 * @return RacePointsList
	 */
	public RacePointsList getRacePointsList() {
		return fPointsList;
	}

	/**
	 * scores all boats, all races in the regatta
	 * 
	 * @param regatta
	 *            Regatta to be scored
	 * @param inRaces
	 *            races to be included in the scoring
	 * @throws ScoringException
	 *             if a problem is encountered
	 */
	public void validate() throws ScoringException {
		Regatta regatta = getRegatta();
		JavaScoreProperties.acquireScoringLock();
		try {
			logger.trace("ScoringManager: validation started...");
			if (regatta == null || regatta.getNumRaces() == 0 || regatta.getNumEntries() == 0) {
				logger.trace("ScoringManager: (empty) done.");
				return;
			}

			// check for entries with a division not in the fRegatta
			sWarnings.clear();
			for (Entry entry : regatta.getAllEntries()) {
				if (!regatta.hasDivision(entry.getDivision())) {
					sWarnings.add(MessageFormat.format(res.getString("WarningEntryNotInDivision"), new Object[] { entry
							.toString() }));
				}
			}

			clearResults();
			ScoringUtilities.validateRegatta(regatta);
			
		} finally {
			JavaScoreProperties.releaseScoringLock();
		}
	}
	
	protected void clearResults() {
		fPointsList.clear();
		fSeries.clear();
	}

	public void scoreRegatta() throws ScoringException {
		scoreRaces();
	}
	
	public void scoreRaces() throws ScoringException {
		Regatta regatta = getRegatta();

		DivisionList divisions = regatta.getActiveDivisions();

		FleetList fleets = regatta.getFleets();

		SubDivisionList subdivisions = new SubDivisionList();
		subdivisions.addAll(regatta.getSubDivisions());

		// if 0 divisions, dummy up a single division
		if (divisions.size() == 0)
			divisions.add(new Division("All"));

		// score the remaining divisions
		for (Division div : divisions) {
			scoreDivision(div);
		} // division loop

		// score the remainined fleets
		for (Fleet div : fleets) {
			scoreDivision(div);
		} // fleet loop

		// score the remaining subdivisions last
		for (SubDivision div : subdivisions) {
			scoreSubDivision(div);
		} // division loop
	}
	public void scoreStageSeries() {} // all is done in scoreDivisions 

	public static boolean testing = false;

	private void scoreDivision(AbstractDivision div) throws ScoringException {
		Regatta regatta = getRegatta();

		logger.trace("ScoringManager: scoring races for {}...",div.getName());

		// go to next division if this one is empty
		if (div.getNumEntries() == 0)
			return;

		EntryList entries = div.getEntries();

		// make up list of races for this div
		RaceList divRaces = new RaceList();
		for (Race r : regatta.getRaces()) {
			if (div.isRacing(r)) {
				divRaces.add(r);
			}
		}
		divRaces.sort();

		RacePointsList divPointsList = new RacePointsList();

		// calc races points for each race and each division in a race
		for (Race r : divRaces) {

			if (div instanceof Fleet) {
				if (!((Fleet) div).shouldScoreRace(r))
					continue; // jump to next race
			}

			RacePointsList racePoints = new RacePointsList();
			for (Entry e : entries) {
				racePoints.add(new RacePoints(r, e, div, Double.NaN, false));
			}

			scoreRace(r, racePoints, false);
			divPointsList.addAll(racePoints);
		}

		fPointsList.addAll(divPointsList);
		fSeries.addAll( fModel.scoreSeries(div, entries, divPointsList));
		
	}

	private void scoreSubDivision(SubDivision subdiv) throws ScoringException {
		Regatta regatta = getRegatta();

		logger.trace("ScoringManager: scoring races for{}... ", subdiv.getName());

		// make list of entries in this subdivision
		EntryList entries = subdiv.getEntries();
		if (entries.size() <= 0) return; // no entries

		RacePointsList divPointsList = new RacePointsList();

		// calc races points for each race and each division in a race
		for (Race r : regatta.getRaces()) {
			if ( subdiv.isRacing(r, true, false)) {
				RacePointsList racePoints = new RacePointsList();

				if (subdiv.isScoreSeparately()) {
					for (Entry e : entries) {
						racePoints.add(new RacePoints(r, e, subdiv, Double.NaN, false));
					}
					scoreRace(r, racePoints, false);
				} else {
					if (subdiv.getParentDivision() == null) return;
					RacePointsList parentPoints = getRacePointsList().findAll(
							subdiv.getParentDivision());
					for (Entry e :  entries) {
						RacePoints ppts = parentPoints.find(r, e, subdiv.getParentDivision());
						if (ppts != null) {
							RacePoints pts = new RacePoints(r, e, subdiv, ppts.getPoints(), false);
							racePoints.add(pts);
						}
					}
					scoreRace(r, racePoints, true);
				}
				divPointsList.addAll(racePoints);
			}
		}

		fPointsList.addAll(divPointsList);
		fSeries.addAll( fModel.scoreSeries(subdiv, entries, divPointsList));
	}

	/**
	 * calculates a racepoints array for specified race NOTE that this instance is automatically scored in the hashmap
	 * returns null if the Scoring system is not defined.
	 * 
	 * @param race
	 *            race to be scored
	 * @param entries
	 *            entries in the race
	 * @param points
	 *            points list in which race's points are stored
	 * @throws ScoringException
	 *             if problem occurs
	 */
	protected void scoreRace(Race race, RacePointsList points, boolean positionOnly) throws ScoringException {
		if (fModel == null || race == null)
			return;
		fModel.scoreRace(race, points, positionOnly);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		try {
			SingleStage that = (SingleStage) obj;

			// if (!Util.equalsWithNull( this.fRaces, that.fRaces)) return
			// false;
			if (!Util.equalsWithNull(this.fSeries, that.fSeries))
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
		if (fModel != null && newModel != null) {
			newModel.getOptions().setAttributes(fModel.getOptions());
		}
		fModel = newModel;
	}
	
	public void setModel(ScoringModel newModel) {
		fModel = newModel;
	}

	/**
	 * returns the active scoring model
	 * 
	 * @return current ScoringModel
	 */
	public ScoringModel getModel() {
		return fModel;
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
	public SeriesPoints getRegattaRanking(Entry entry, AbstractDivision div) {
		return fSeries.find(entry, div);
	}

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
		return fSeries.findAll(div);
	}
	public SeriesPointsList getAllRegattaRankings(AbstractDivision div) {
		return fSeries.findAll(div);
	}

	/**
	 * returns list of all seriespoints
	 * 
	 * @return SeriesPointsList
	 */
	public SeriesPointsList getAllSeriesPoints() {
		return fSeries;
	}

	protected static final String SYSTEM_PROPERTY = "System";
	protected static final String SYSTEMNAME_PROPERTY = "SystemName";
	protected static final String RACEPOINTSLIST_PROPERTY = "RacePointsList";
	protected static final String SERIESPOINTSLIST_PROPERTY = "SeriesPointsList";
	protected static final String RACEPOINTS_PROPERTY = "rp";
	protected static final String SERIESPOINTS_PROPERTY = "sp";

	@Override public void xmlWrite(PersistentNode e) {
		e.setAttribute(SYSTEMNAME_PROPERTY, fModel.getName());

		fModel.getOptions().xmlWrite(e.createChildElement( SYSTEM_PROPERTY));

		if (fPointsList.size() > 0)
			fPointsList.xmlWrite(e.createChildElement( RACEPOINTSLIST_PROPERTY), RACEPOINTS_PROPERTY);
		
		if (fSeries.size() > 0)
			fSeries.xmlWrite(e.createChildElement( SERIESPOINTSLIST_PROPERTY), SERIESPOINTS_PROPERTY);
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		setRegatta( (Regatta) rootObject);

		fPointsList.clear();
		fSeries.clear();
		fModel = null;

		String value = n.getAttribute(SYSTEMNAME_PROPERTY);
		if (value != null)
			setModel(value);

		if (fModel == null)
			setModel(ScoringLowPoint.NAME);

		PersistentNode n2 = n.getElement(RACEPOINTSLIST_PROPERTY);
		if (n2 != null) {
			fPointsList.xmlRead(n2, rootObject);
		}

		n2 = n.getElement(SYSTEM_PROPERTY);
		if (n2 != null) {
			fModel.getOptions().xmlRead(n2, rootObject);
		}

		n2 = n.getElement(SERIESPOINTSLIST_PROPERTY);
		if (n2 != null) {
			fSeries.xmlRead(n2, rootObject);
		}

		if (fModel == null)
			setModel(ScoringLowPoint.NAME);
	}

	@Override
	public String getScoringSystemName() {
		return (fModel == null) ? "None" : fModel.getName();
	}

}
/**
 * $Log: ScoringManager.java,v $ Revision 1.12 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
