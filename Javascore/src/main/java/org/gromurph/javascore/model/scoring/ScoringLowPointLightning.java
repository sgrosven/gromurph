//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointLightning.java,v 1.5 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.RacePointsList;

public class ScoringLowPointLightning extends ScoringLowPoint
{
    public static final String NAME = "Lightning LowPoint";

	public ScoringLowPointLightning()
	{
		super();
		fOptions.setUserCanChangeTiebreaker( false);
	}
	
	@Override public String toString()
    {
        return res.getString( "ScoringLightning");
    }

	@Override public String getName()
    {
        return NAME;
    }

    /**
     * Overrides the standard ISAF penalties
    **/
	@Override public double getPenaltyPoints( Penalty p, RacePointsList rpList, double basePts)
    {
        int nEntries = 0;
        if (rpList != null) nEntries = rpList.size();

        double returnPoints = super.getPenaltyPoints( p, rpList, basePts);

        // if MAN or RDG, return parent points
        if ( p.hasPenalty( RDG)) return returnPoints;

        // if a DSQ, return DSQ points a be gone
        if ( p.isDsqPenalty() )
        {
            if (p.hasPenalty( RET)) return nEntries+1;
            else return nEntries+2;
        }

        return returnPoints;
    }

    @Override protected TiebreakCalculatorRrs initializeTiebreakCalculator() {
    	return new ScoringTiebreakerLightning();
    }
    
    /**
     * resolve ties among a group of tied boats.  A tie that is breakable
     * should have .01 point increments added as appropriate.
     * Assume that each individual race and series points have calculated, and that
     * throwouts have already be designated in the points objects.
     * <P>
     * @param entries list of tied entries
     * @param points list of points for all races and entries (and maybe more!)
     * @param series map containing series points for the entries, prior to
     * handling ties (and maybe more than just those entries
    **/
    private class ScoringTiebreakerLightning extends TiebreakCalculatorRrs {
    	
    	@Override public void calculateTieBreakers(EntryList entriesIn) {
            // list of racepoints, 1 elist item per tied entry, item is sorted list
            //    of racepoints
            List<RacePointsList> eLists = new ArrayList<RacePointsList>(5);
            EntryList entries = (EntryList) entriesIn.clone();

            // first create separate lists of finishes ordered by points
            // for each of the tied boats.
            Iterator eIter = entries.iterator();
            while (eIter.hasNext())
            {
                Entry e = (Entry) eIter.next();
                RacePointsList ePoints = racePointsList.findAll( e);
                // -- DO NOT DROP THROWOUT - LIGHTING SPECIAL

                // sort the pointslist from lowest to highest
                ePoints.sortPoints();
                eLists.add( ePoints);
            }

            // pull out best of the bunch one at a time
            // after each scan, best is dropped with no more change
            // in points.  Each remaining gets .01 added to total
            // continue til no more left to play
            while (eLists.size() > 1)
            {
                RacePointsList bestPoints = eLists.get(0);

                // loop thru entries, apply tiebreaker method (comparetied)
                // keep the best (winner)
                for (int i = 1; i < eLists.size(); i++)
                {
                    RacePointsList leftPoints = eLists.get(i);
                    int c = comparePointsBestToWorst( leftPoints, bestPoints);
                    if ( c < 0) 
                    {
                        // returns true if left side wins tiebreaker
                        bestPoints = leftPoints;
                        // WARNING WARNING: if for some reason if compareTied returns 0
                        // for tied, this is not suppoed to happen in lowpoint
                        // and I arbitrarily dump it as equal to right winning
                    }
                }
                // bestPoints should now equal the best, drop it from list
                eLists.remove( bestPoints);
                incrementSeriesScores( eLists, TIEBREAK_INCREMENT, seriesPointsList);
            }
        }
    }
}
/**
 * $Log: ScoringLowPointLightning.java,v $
 * Revision 1.5  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.2  2006/01/02 22:30:20  sandyg
 * re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit tests for both
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.9.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.9  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.8  2003/04/27 21:03:29  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/04/20 15:43:59  sandyg
 * added javascore.Constants to consolidate penalty defs, and added
 * new penaltys TIM (time value penalty) and TMP (time percentage penalty)
 *
 * Revision 1.6  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.5  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */
