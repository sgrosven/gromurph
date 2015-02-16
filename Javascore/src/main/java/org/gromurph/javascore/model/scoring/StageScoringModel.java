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

import java.util.List;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.util.BaseObjectModel;


public interface StageScoringModel extends BaseObjectModel {

	public List<String> getSeriesScoringNotes(RacePointsList rp);
	public ScoringModel getModel();
	public void setModel(ScoringModel scoring);
	public SeriesPointsList getAllSeriesPoints();
	public SeriesPointsList getAllSeriesPoints( AbstractDivision div);
	public SeriesPoints getStageSeriesPoints(Entry entry, AbstractDivision div);	
	public RacePointsList getRacePointsList();
	
	public void scoreRaces() throws ScoringException;
	public void scoreStageSeries() throws ScoringException;
   
}
/**
 * $Log: ScoringManager.java,v $ Revision 1.12 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
