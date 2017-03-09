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
     * Overrides the standard WS penalties as per Annapolis Yacht Club's
     * Wednesday Night penalties:
     * 1.2	RRS 44.3(c) and RRS/US Appendix T2 are changed such that all percentage penalties will be 
     * 			based on the number of boats that finished in the boat's class.
     * 1.3	At the time of the incident, the penalties will be:
     * a)	For breaking a rule of Part 2 or RRS 30.2 at the time of the incident, 
     * 			the percentage will be 20%, but not less than 2 places.
     * b)	For breaking RRS 31 (Touching a Mark) at the time of the incident, 
     * 			the percentage will be 10%, but not less than 1 place. 
     * c)	For penalties after racing, RRS/US Appendix T2 will apply, changed such that 
     * 			the percentage will be 30%, but not less than 3 places.
     * d)	For a boat that complies with some, but not all of the requirements of 44.3(a) and 44.3(b) 
     * 			the percentage will be 30%, but not less than 3 places. 
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
		double newPoints = getPenaltyPointsForEntryBase(p, entryPointList, basePts, nFinishers);
		if ( p.getPenalty() == Penalty.SCP) {
			double p1 = newPoints - basePts;
			if (p.getPercent() == 10 && p1 < 1) p1 = 1;
			if (p.getPercent() == 20 && p1 < 2) p1 = 2;
			if (p.getPercent() == 30 && p1 < 3) p1 = 3;
			newPoints = basePts + p1;
		}
		return newPoints;
	}

}
