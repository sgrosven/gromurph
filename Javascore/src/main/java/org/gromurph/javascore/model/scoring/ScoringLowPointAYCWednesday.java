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
}
/**
 * $Log: ScoringLowPointAYCWednesday.java,v $
 * Revision 1.6  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.5  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.2  2006/01/02 22:30:20  sandyg
 * re-laidout scoring options, added alternate A8.2 only tiebreaker, added unit tests for both
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.7.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.7  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.6  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.5  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */
