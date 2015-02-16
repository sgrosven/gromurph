//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringModel.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.util.List;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.SeriesPointsList;

/**
 * Interface that any scoring system must implement in order to be part of
 * JavaScore's systems
**/
public interface ScoringModel 
{
    /**
     * returns the name of this scoring system
     */
    public String getName();

    /**
     * Given a Race, and a list of Entries calculates the RacePoints object
     * The entries should be assumed to represent a single class within the Race
     * calculateRace can assume that an Entries without a finish in the Race is DNC
     * but should recognize that the Race may well have finishers not in the Entries.
     * <P>
     * Can assume:
     * (1) that any "non-finish penalties" have been properly passed
     * thru to the FinishPosition.
     * (2) FinishPosition is otherwise sound and matchs finishtimes if any
     * (3) All items in Entry list should be valid racers in Race r
     * (4) None of race, entries, or points is null
     *
     * @param race the Race to be scored
     * @param entries a list of entries participating in the race
     * @param points a list of racepoints in which the points should be stored
     * @param positionOnly when true do NOT recalculate race points, do race position only
    **/
    public void scoreRace( Race race, RacePointsList points, boolean positionOnly)
        throws ScoringException;

    /**
     * 
     * @param div - division whose series is being scored
     * @param entries - all entries in the division
     * @param divPointsList - all race points for all races in the division
     * @return  SeriesPointsList for the entries in the division
     * @throws ScoringException
     */
	public SeriesPointsList scoreSeries(AbstractDivision div, EntryList entries, RacePointsList divPointsList) throws ScoringException;
    
	public void calculateRankings(RacePointsList divPointsList, SeriesPointsList divSeriesPoints);
	
    /**
     * Given a penalty, returns the number of points to be assigned
     * Do NOT handle AVG, it will be dealt with by ScoringManager.
     * Note that the race could be null, and basepts might be 0 or NaN
     * @param p the Penalty to be calculated
     * @param rpList the RacePointsList of the points being calculated
     * @param basepts the points calculated before applying a penalty
    **/
    public double getPenaltyPoints( Penalty p, RacePointsList rpList, double basePts);
    
    /**
     * Sort a list of series points from best to worst
     */
    public void sortSeries( SeriesPointsList seriesPoints);

    /**
     * generates list of notes for series scoring of 
     * specified group of race points (generally a single division, might be whole fleet)
     * @param rpList the list of race points on which to generate notes
     * @return list of strings containing notes, empty list if no notes
     */
    public List<String> getSeriesScoringNotes( RacePointsList rpList);
    
    public ScoringOptions getOptions();
    public void setOptions( ScoringOptions opt);

}
/**
 * $Log: ScoringModel.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.9.4.2  2005/11/26 17:45:01  sandyg
 * implement race weight & nondiscardable, did some gui test cleanups.
 *
 * Revision 1.9.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.9.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.9  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.8  2003/04/27 21:03:29  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.6  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.5  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */
