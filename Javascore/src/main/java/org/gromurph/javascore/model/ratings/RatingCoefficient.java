//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingCoefficient.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import java.text.MessageFormat;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.WarningList;


/**
 * Covering classes for ratings where the corrected time is a simple coefficient
 * of the elapsed time by the rating value.  Typical of time-on-time formulas
 * a smaller rating value denotes a slower boat
 *
**/
public abstract class RatingCoefficient extends RatingDouble
{
    public RatingCoefficient( String sys, double inV)
    {
        super( sys, inV);
    }

    @Override public int getDecs() {return 3;}
    
    protected double getCoefficient()
    {
    	return getPrimaryValue();
    }

    @Override public boolean isSlower(Rating that) {
    	if (!(that instanceof RatingCoefficient)) return false;
    	return (getPrimaryValue() > ((RatingCoefficient) that).getPrimaryValue());
    }
    @Override public boolean isFaster(Rating that) {
    	if (!(that instanceof RatingCoefficient)) return false;
    	return (getPrimaryValue() < ((RatingCoefficient) that).getPrimaryValue());
    }

    /**
     * Time on time formula
     * @param inFinish Finish object for which corrected time is to be calculated
     * @return corrected time in milliseconds
     */
    @Override public long getCorrectedTime( Finish inFinish)
    {
        long elapMilli = inFinish.getElapsedTime(); 
        if (elapMilli != SailTime.NOTIME) return (long) (elapMilli * getCoefficient());
        else return SailTime.NOTIME;
    }

    @Override public void validateRace( Race race, Division div, WarningList warnings)
    {
        long starttime = race.getStartTimeRaw( div);
        if ( starttime == SailTime.NOTIME)
        {
            warnings.add(
                MessageFormat.format(
                    res.getString( "WarningRaceNeedsStartTime"),
                    new Object[] { race.toString(), div.toString() }
                ));
        }
        validateFinishTimesAfterStartTimes( race, div, warnings);
        validateValidFinishTime( race, div, warnings);
    }
}
/**
 * $Log: RatingCoefficient.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.2  2005/04/27 02:45:47  sandyg
 * Added Yardstick, and added Yardstick and IRC all to GUI.  Portsmouth now trivial subclass of yardstick
 *
 * Revision 1.1  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.8  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.5  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
