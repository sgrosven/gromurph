//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointAYCWednesday.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.RacePointsList;


public class ScoringLowPointAYCWednesday extends ScoringLowPoint 
{
    //protected static ResourceBundle res = JavaScoreProperties.getResources();

	public ScoringLowPointAYCWednesday()
	{
		super();
		fOptions.setUserCanChangeTiebreaker( false);
	}
	
    public static final String NAME = "LowPoint - AYC Wed Penalties";

    @Override public String toString()
    {
        return res.getString( "ScoringAycWed");
    }

    @Override public String getName()
    {
        return NAME;
    }

    /**
     * Overrides the standard ISAF penalties as per Annapolis Yacht Club's
     * Wednesday Night penalties:
     * <p>SCORING: Each boat
     * <i>starting </i>and <i>finishing </i>and not thereafter retiring, being
     *   penalized or given redress shall be scored points per Appendix A Rule
     *   4.1 of the Racing Rules using the low point system.</p>
     * <p>Appendix
     *   A Rule 9 is replaced by: "A starting boat not finishing, a
     *   boat that did not start, a boat that retired after finishing, or a boat
     *   disqualified shall be scored points for the finishing place one more than
     *   the number of boats that <i>finished</i>."</p>
     **/
    @Override public double getPenaltyPoints( Penalty p, RacePointsList rpList, double basePts)
    {
        double standardPoints = super.getPenaltyPoints( p, rpList, basePts);

        if ( p.isDsqPenalty() ||
             p.isFinishPenalty())
         {
             return (rpList == null) ? 0 : rpList.getNumberFinishers() + 1;
         }
         else
         {
            return standardPoints;
         }
     }
    
    @Override protected double getPenaltyPointsWithoutManual(Penalty p, RacePointsList entryPointList, double basePts) {
    	int nFinishers = 0;
    	if (entryPointList != null) nFinishers = entryPointList.getNumberFinishers();
		return getPenaltyPointsForEntryBase(p, entryPointList, basePts, nFinishers);
	}

}
