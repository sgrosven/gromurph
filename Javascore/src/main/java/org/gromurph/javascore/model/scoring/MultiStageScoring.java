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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.StageList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.SubDivisionList;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

/**
 * 
 **/
public class MultiStageScoring extends BaseObject implements RegattaScoringModel, Constants {
	
	static ResourceBundle res = JavaScoreProperties.getResources();

	public String getScoringSystemName() { return ScoringLowPoint.NAME; }
	
	public MultiStageScoring() {
		
		// initialize first stage
		stages = new StageList();
		Stage s = new Stage();
		s.setName(Stage.FLEET);
		addStage(s);
	}

	public static MultiStageScoring createFromSingleStage( SingleStageScoring ss) {
		MultiStageScoring ms = new MultiStageScoring();
		Regatta r = JavaScoreProperties.getRegatta();
		Stage s = ms.getStage(Stage.FLEET);
		s.setModel( ss.getModel());
		s.setCombinedQualifying(true);
		for (AbstractDivision div : r.getDivisions()) {
			s.divisions.add(div);
		}
		return ms;
	}
	
	private Regatta fRegatta;
	
	public Regatta getRegatta() { 
		if (fRegatta == null) fRegatta = JavaScoreProperties.getRegatta();
		return fRegatta;
	}
	
	protected StageList stages;
	public StageList getStages() { return stages; }
	
	public Stage getStage( String stageName) {
		for (Stage s : stages) {
			if (s.getName().equals(stageName)) return s;
		}
		return null;
	}
	public void addStage( Stage s) {
		s.setRegattaScoringModel(this);
		stages.add(s);
	}
	
	public int getNumStages() { return stages.size();}
	
	public Stage getPreviousStage( Stage s) {
		StageList tmp = new StageList(stages);
		Collections.sort( tmp, new Stage.SortBottomToTop());
		int si = tmp.indexOf(s);
		if (si <= 0) return null;
		else return tmp.get(si-1);
	}
	
	public Stage getNextStage( Stage s) {
		StageList tmp = new StageList(stages);
		Collections.sort( tmp, new Stage.SortBottomToTop());
		int si = tmp.indexOf(s);
		if (si < 0 || si == tmp.size()-1) return null;
		else return tmp.get(si+1);
	}
	
	public void addDivisionToStages(Division div) {
		StageList stages = getStages();
		if ( stages.size() == 1) {
			Stage loneStage = stages.get(0);
			loneStage.addDivision(div);
		}
	}
	
	public void removeDivisionFromStages(Division div) {
		for (Stage s : getStages()) {
			s.removeDivision(div);
		}
	}
	

	protected WarningList warnings = new WarningList();

	/**
	 * list of warning messages generated during last scoring run
	 * 
	 * @return list of warnings
	 */
	public WarningList getWarnings() {
		return warnings;
	}

	/**
	 * scores all boats, all races in the regatta
	 * 
	 * @param fRegatta
	 *            Regatta to be scored
	 * @param inRaces
	 *            races to be included in the scoring
	 * @throws ScoringException
	 *             if a problem is encountered
	 */
	public void initializeScoring() {
		clearResults();
		warnings.clear();
	}
	
	public boolean validate() throws ScoringException {
		logger.trace("ScoringManager: validation started...");

		// check for entries with a division not in the getRegatta()
		for (Entry entry : getRegatta().getAllEntries()) {
			if (!getRegatta().hasDivision(entry.getDivision())) {
				warnings.add(MessageFormat.format(res.getString("WarningEntryNotInDivision"), new Object[] { entry
						.toString() }));
			}
		}
		warnings.addAll( ScoringUtilities.validateRegatta(getRegatta()));
		return (warnings.size() == 0);
	}
	
	protected void clearResults() {
		regattaRankings = new SeriesPointsList();
		seriesPoints = new SeriesPointsList();
		
		for (Stage st : stages) {
			st.clearResults();
		}
	}
	
	public RacePointsList getRacePointsList() {
		RacePointsList all = new RacePointsList();
		for (Stage st : stages) {
			all.addAll( st.getRacePointsList());
		}
		return all;
	}

	public static boolean testing = false;

	private SeriesPointsList regattaRankings = new SeriesPointsList();
	private SeriesPointsList seriesPoints = new SeriesPointsList();
	

	public void scoreRegatta() throws ScoringException {
		
		Collections.sort(stages, new Stage.SortBottomToTop());
		for (Stage st : stages) {
			st.scoreRaces();
			st.scoreStageSeries();
		}

		calculateRegattaRankings();
	}
	
	public void calculateRegattaRankings() {
		// after all races and all stage series have been calculated
		
		for (Division div : getRegatta().getDivisions()) {
			// work through stages from "best" to "worst"
			// pull out and set final series ranks from top to bottom in each
			List<Entry> ranked = new ArrayList<Entry>();
			int rank = 1;
			
    		Collections.sort( stages, new Stage.SortTopToBottom());
    		for (Stage stage : stages) {
    			// add to overall series points list
    			seriesPoints.addAll(stage.getAllSeriesPoints());    			
    			
    			SeriesPointsList series = stage.getAllSeriesPoints(div);
    			series.sortPosition();
    			for ( SeriesPoints sp : series) {
    				if (!ranked.contains(sp.getEntry())) {
        				SeriesPoints rp = new SeriesPoints( sp.getEntry(), div, sp.getPoints(), rank++, sp.isTied());
        				regattaRankings.add( rp);
        				ranked.add( sp.getEntry());
    				}
    			}
    		}
    		
    		List<Entry> missing = new ArrayList<Entry>();
    		missing.addAll( div.getEntries());
    		missing.removeAll(ranked);
    		for (Entry e : missing) {
    			SeriesPoints rp = new SeriesPoints( e, div, Double.NaN, rank, missing.size() > 1);
    			regattaRankings.add(rp);
    		}
		}
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		try {
			MultiStageScoring that = (MultiStageScoring) obj;

			// if (!Util.equalsWithNull( this.fRaces, that.fRaces)) return
			// false;
			if (!Util.equalsWithNull(this.stages, that.stages))
				return false;
			// if (!Util.equalsWithNull( this.fPointsList, that.fPointsList))
			// return false;
			return true;
		} catch (Exception e) {
			return false;
		}
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

	protected static final String STAGE_PROPERTY = "Stage";
	
	@Override public void xmlWrite(PersistentNode node) {
		
		if (stages != null && stages.size() > 0) {
			for (Stage s : stages) {
				s.xmlWrite( node.createChildElement( STAGE_PROPERTY));
			}
		}
	}


	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fRegatta = (Regatta) rootObject;
		
		stages = new StageList();
		List<String> prevIds = new ArrayList<String>(10);
		
		PersistentNode[] elements = n.getElements();
		for ( PersistentNode child : elements) {
			if (child.getName().equals(STAGE_PROPERTY)) {
				Stage s = new Stage(this);
				s.xmlRead( child, rootObject);
				stages.add(s);
				String pId = child.getAttribute( Stage.PREVIOUSSTAGE_PROPERTY);
				if (pId == null) pId = "";
				prevIds.add(pId);
			}
		}
		
		for (int i = 0; i < stages.size(); i++) {
			Stage s = stages.get(i);
			String pid = prevIds.get(i);
			if (!pid.isEmpty()) {
				int pi = Integer.parseInt(pid);
    			for (Stage p : stages) {
    				if (p.getId() == pi) s.setPrevStage(p);
    			}
			} else {
				s.setPrevStage( null);
			}
		}
	}


	public transient static final String STAGERANK_PROPERTY = "StageRank";
	public transient static final String STAGEORDER_PROPERTY = "StageOrder";

	public SeriesPoints getSeriesPoints(Entry entry, AbstractDivision div) {
		return seriesPoints.find(entry, div);
	}

	public SeriesPointsList getAllSeriesPoints(AbstractDivision div) {
		return seriesPoints.findAll(div);
	}

	public SeriesPointsList getAllSeriesPoints() {
		return seriesPoints;
	}

	public SeriesPoints getRegattaRanking(Entry entry, AbstractDivision div) {
		return regattaRankings.find(entry, div);
	}

	public SeriesPointsList getAllRegattaRankings(AbstractDivision div) {
		return regattaRankings.findAll(div);
	}

	public SeriesPointsList getAllRegattaRankings() {
		return regattaRankings;
	}

	public List<String> getSeriesScoringNotes(RacePointsList rp) {
		List<String> notes = new ArrayList<String>();
		if (stages == null || stages.size() == 0) return notes;
	
		for (Stage s : stages) {
			for  (String sn : s.getSeriesScoringNotes(rp)) {
				notes.add( s.getName() + ": " + sn);
			}
		}
		return notes;
	}


}
