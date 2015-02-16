//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingPhrfTimeOnTime.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.WarningList;
import java.text.MessageFormat;

/**
 * PHRF Time on Time handicapping... see (as of 3 feb 2013) http://offshore.ussailing.org/PHRF/Time-On-Time_Scoring.htm for
 * description.  This corrected time formula takes a wimpy approach to the
 * A factor, simply using the Bfactor all the time.
**/
public class RatingPhrfTimeOnTime extends RatingPhrf
{
    public final static int BFACTOR_HEAVY = 480;
    public final static int BFACTOR_AVERAGE = 550;
    public final static int BFACTOR_LIGHT = 600;
    public final static int BFACTOR_CUSTOM = -1;

    public final static int AFACTOR_DEFAULT = 650;

    public static final String SYSTEM = "PHRFTimeOnTime";

    public RatingPhrfTimeOnTime()
    {
        super( SYSTEM, 0);
    }

    public RatingPhrfTimeOnTime( int inV)
    {
        super( SYSTEM, inV);
    }

    public RatingPhrfTimeOnTime( String system, int inV)
    {
        super( system, inV);
    }

    /**
     * calculates corrected time
     * @param inFinish
     * @return corrected time
     */
    @Override public long getCorrectedTime( Finish inFinish)
    {
        long elapsed = inFinish.getElapsedTime();
        if (elapsed == SailTime.NOTIME) return SailTime.NOTIME;

        int bfactor = inFinish.getRace().getBFactor();
        int afactor = inFinish.getRace().getAFactor();
        
        if (bfactor < 0) bfactor = BFACTOR_AVERAGE;
        if (afactor < 0) afactor = bfactor;

        double corrected = elapsed * afactor / (bfactor + getPrimaryValue());  // in milliseconds

//        corrected = Math.round( corrected/1000);
//        return (long) corrected * 1000;

        return (long) corrected;
    }

    @Override public long getTimeAllowance( Finish inFinish)
	{
		return inFinish.getElapsedTime() - getCorrectedTime( inFinish);
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
 * $Log: RatingPhrfTimeOnTime.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.7  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.6  2003/04/27 21:36:17  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.5  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.4  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
