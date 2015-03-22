package org.gromurph.javascore.model.scoring;

import java.util.List;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

public interface RegattaScoringModel {

	/**
	 * returns list of all racepoints
	 * 
	 * @return RacePointsList
	 */
	public RacePointsList getRacePointsList();

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
	public void scoreRegatta() throws ScoringException;

	public void initializeScoring();

	public boolean validate() throws ScoringException;
	
	public void xmlRead(PersistentNode n, Object rootObject);
	
	public void xmlWrite(PersistentNode node);
	
	public String getScoringSystemName();
	
	public int getNumStages();
	
	public SeriesPoints getRegattaRanking(Entry entry, AbstractDivision div);
	public SeriesPointsList getAllRegattaRankings(AbstractDivision div);

	public SeriesPointsList getAllSeriesPoints(AbstractDivision div);
	public SeriesPointsList getAllSeriesPoints();
	
	public List<String> getSeriesScoringNotes( RacePointsList rpList);
	
	public WarningList getWarnings();
	
	public Regatta getRegatta();
}