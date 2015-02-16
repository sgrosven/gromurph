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
public class MultiStage extends BaseObject implements RegattaScoringModel, Constants {
	
	static ResourceBundle res = JavaScoreProperties.getResources();

	public String getScoringSystemName() { return ScoringLowPoint.NAME; }
	
	public MultiStage() {
		
		// initialize first stage
		stages = new StageList();
		Stage s = new Stage();
		s.setName(Stage.FLEET);
		addStage(s);
	}

	public static MultiStage createFromSingleStage( SingleStage ss) {
		MultiStage ms = new MultiStage();
		Regatta r = JavaScoreProperties.getRegatta();
		Stage s = ms.getStage(Stage.FLEET);
		s.setModel( ss.getModel());
		for (AbstractDivision div : r.getDivisions()) {
			s.divisions.add(div);
		}
		return ms;
	}
	
	private Regatta fRegatta;
	
	private Regatta getRegatta() { 
		if (fRegatta == null) fRegatta = JavaScoreProperties.getRegatta();
		return fRegatta;
	}
	
	private StageList stages;
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
	 * scores all boats, all races in the regatta
	 * 
	 * @param fRegatta
	 *            Regatta to be scored
	 * @param inRaces
	 *            races to be included in the scoring
	 * @throws ScoringException
	 *             if a problem is encountered
	 */
	public void validate() throws ScoringException {
		JavaScoreProperties.acquireScoringLock();
		try {
			logger.trace("ScoringManager: validation started...");

			clearResults();

			if (getRegatta() == null || getRegatta().getNumRaces() == 0 || getRegatta().getNumEntries() == 0) {
				logger.trace("ScoringManager: (empty) done.");
				return;
			}

			// check for entries with a division not in the getRegatta()
			sWarnings.clear();
			for (Entry entry : getRegatta().getAllEntries()) {
				if (!getRegatta().hasDivision(entry.getDivision())) {
					sWarnings.add(MessageFormat.format(res.getString("WarningEntryNotInDivision"), new Object[] { entry
							.toString() }));
				}
			}

			ScoringUtilities.validateRegatta(getRegatta());
			
		} finally {
			JavaScoreProperties.releaseScoringLock();
		}
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

//		Collections.sort( stages, new Stage.SortTopToBottom());
//		EntryList remainingEntries = new EntryList();
//		remainingEntries.addAll(getRegatta().getAllEntries());
//		for (Stage st : stages) {
//			st.scoreStageSeries(remainingEntries);
//		}
		
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
			MultiStage that = (MultiStage) obj;

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


	public void xmlRead_pre600(PersistentNode n, Regatta regatta) {
		stages.clear();
		
		String SYSTEM_PROPERTY = "System";
		String SYSTEMNAME_PROPERTY = "SystemName";

		ScoringModel fModel;
		
		String scoringName = n.getAttribute(SYSTEMNAME_PROPERTY);
		if (scoringName == null) scoringName = ScoringLowPoint.NAME;
		
		PersistentNode scoringNode = n.getElement(SYSTEM_PROPERTY);

		ArrayList<AbstractDivision> qualSubs = new ArrayList<AbstractDivision>();
		SubDivisionList semifinalSubs = new SubDivisionList();
		ArrayList<AbstractDivision> medalSubs = new ArrayList<AbstractDivision>();
		ArrayList<AbstractDivision> otherSubs = new ArrayList<AbstractDivision>();
		Stage firstStage = null;
		Stage medalStage = null;
		
		String FINAL = "Final";
		String MEDAL = "Medal";
		for (SubDivision subdiv : regatta.getSubDivisions()) {
			if (subdiv.getGroup().equals(MEDAL) || subdiv.getName().equalsIgnoreCase(Stage.MEDAL)) medalSubs.add(subdiv);
			else if (!subdiv.isGroupQualifying()) semifinalSubs.add(subdiv);
			else if (subdiv.getName().equalsIgnoreCase(Stage.GOLD)) semifinalSubs.add(subdiv);
			else if (subdiv.getName().equalsIgnoreCase(Stage.SILVER)) semifinalSubs.add(subdiv);
			else if (subdiv.isGroupQualifying()) qualSubs.add( subdiv);
			//else semifinalSubs.add( subdiv);
		}

		
		int so = 1;
		if (qualSubs.size() > 0) {
			Stage stage = new Stage(this);
			stage.setCombinedQualifying(true);
			stage.setName( Stage.QUALIFYING);
			stage.setStageRank(10);
			stage.setScoreCarryOver(Constants.ScoreCarryOver.ALL);
			stage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.ALL);
			stage.setTiebreakCarryOver(Constants.TiebreakCarryOver.ALL);
			
			stage.setModel(scoringName);
			if (scoringNode != null) {
				stage.getModel().getOptions().xmlRead(scoringNode, regatta);
			}
			
			stage.getDivisions().addAll(qualSubs);
			firstStage = stage;
			stages.add(stage);
		}
		
		if (medalSubs.size() > 0) {
			Stage stage = new Stage(this);
			stage.setName( Stage.MEDAL);
			stage.setStageRank(90);
			stage.setScoreCarryOver(Constants.ScoreCarryOver.ALL);
			
			stage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.NONE);
			
			stage.setTiebreakCarryOver(Constants.TiebreakCarryOver.ALL);
			stage.getOptions().setThrowoutScheme(THROWOUT_NONE);
			
			stage.setModel(scoringName);
			if (scoringNode != null) {
				stage.getModel().getOptions().xmlRead(scoringNode, regatta);
			}
			stage.getDivisions().addAll(medalSubs);
			medalStage = stage;
			// add to stage list and set prevstage at end
		}

		// delete any division in which no races have been run
		SubDivisionList finalSubs = new SubDivisionList();
		for (SubDivision sub : semifinalSubs) {
			for (Race r : regatta.getRaces()) {
				if (sub.isRacing(r)) {
					finalSubs.add(sub);
					break;
				}
			}
		}
		
		if (finalSubs.size() == 0 && firstStage == null) {
			// need an initial "fleet series"
			Stage stage = new Stage(this);
			stage.setName( Stage.FLEET);
			stage.setStageRank( 10);
			stage.setScoreCarryOver(Constants.ScoreCarryOver.ALL);
			stage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.ALL);
			stage.setTiebreakCarryOver(Constants.TiebreakCarryOver.ALL);
			
			stage.setModel(scoringName);
			if (scoringNode != null) {
				stage.getModel().getOptions().xmlRead(scoringNode, regatta);
			}
			stage.getDivisions().addAll( regatta.getDivisions());
			stages.add(stage);
			firstStage = stage;
		} else if (finalSubs.size() > 0) {
			finalSubs.sort();
			Collections.reverse(finalSubs);
			int rank = 20;
			for ( SubDivision div : finalSubs) {
    			Stage stage = new Stage(this);
    			stage.setName( div.getName());
    			stage.setStageRank( rank++);
    			stage.setScoreCarryOver(Constants.ScoreCarryOver.ALL);
    			stage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.ALL);
    			stage.setTiebreakCarryOver(Constants.TiebreakCarryOver.ALL);
    			
    			stage.setModel(scoringName);
    			if (scoringNode != null) {
    				stage.getModel().getOptions().xmlRead(scoringNode, regatta);
    			}
    			stage.getDivisions().add(div);
    			stages.add(stage);
    			stage.setPrevStage(firstStage);
			}
		}

		if (medalStage != null) {
			Collections.sort( stages, new Stage.SortTopToBottom());
			medalStage.setPrevStage( stages.get(0));
			stages.add(medalStage);
		}
		
		int TIE_RRS_A82_MEDAL = 3;

		// stage scoring options are different instances but same parameters, see what needs to be adapted
		ScoringOptions options0 = stages.get(0).getModel().getOptions();
		if (options0.getTiebreaker() == TIE_RRS_A82_MEDAL) {
			for (Stage s : stages) {
				if (s.getName().equals( Stage.MEDAL)) {
					s.getOptions().setTiebreaker(TIE_RRS_A82_ONLY);
					s.setTiebreakCarryOver( TiebreakCarryOver.NONE);
				} else {
					s.getOptions().setTiebreaker(TIE_RRS_DEFAULT);
					s.setTiebreakCarryOver( TiebreakCarryOver.ALL);
				}
			}
		}
		
		if (medalStage != null) {
			String MAXONETHROWOUTINFINALS_PROPERTY = "MaxOneThrowoutInFinalSeries";
    		boolean max1Finals = false;
    		String value = scoringNode.getAttribute(MAXONETHROWOUTINFINALS_PROPERTY);
    		if (value != null) {
    			boolean b = value.toString().equalsIgnoreCase("true");
    			try {
    				max1Finals = b;
    			} catch (Exception e) {}
    		}
    		if (max1Finals) medalStage.setThrowoutCarryOver(Constants.ThrowoutCarryOver.MAX1);
		}
		
		stages.sort();
//		for (int i = 0; i < stages.size(); i++) {
//			Stage s = stages.get(i);
//			if (i == 0) s.setPrevStage(null);
//			else s.setPrevStage( stages.get(i-1));
//		}
	}
	public transient static final String STAGERANK_PROPERTY = "StageRank";
	public transient static final String STAGEORDER_PROPERTY = "StageOrder";

//	private String tempLastRaceIdBeforeSplit;
//	private Race tempLastRaceBeforeSplit;
//	
	public void pre600_setLastRaceBeforeSplit( String raceid) {
//		tempLastRaceIdBeforeSplit = raceid;
	}
//	
//	public Race getLastRaceBeforeSplit() {
//		if (tempLastRaceIdBeforeSplit == null) return null;
//		if (tempLastRaceBeforeSplit != null) return tempLastRaceBeforeSplit;
//		if (JavaScoreProperties.getRegatta() == null) return null;
//			
//		tempLastRaceBeforeSplit = JavaScoreProperties.getRegatta().getRaceId(Integer.parseInt(tempLastRaceIdBeforeSplit));
//		return tempLastRaceBeforeSplit;
//	}
//	
//	public void setLastRaceBeforeSplit( Race lr) {
//		tempLastRaceBeforeSplit = lr;
//	}
	
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
/**
 * $Log: ScoringManager.java,v $ Revision 1.12 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
