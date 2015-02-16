//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingMorc.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;


/**
 * Standard Morc handicaping
 *
 * <P>This code was developed by Sandy Grosvenor for use with non-commercial
 *    scoring of sailboat regattas.
 *    Copyright 1997/1998.  All rights reserved.
 *
 * @version	Nov 1997
 *
**/
public class RatingMorc extends RatingPhrf //RatingDouble
{
    public static final String SYSTEM = "MORC";

    public RatingMorc()
    {
        super( SYSTEM, 0);
    }

    public RatingMorc( int inV)
    {
        super( SYSTEM, inV);
    }

    @Override public int getDecs() {return 2;}

    //public static double sBase41 = 1 / ((1.25 * Math.pow( 41, 0.5)) + 0.2);

    /**
     * Taken from the MORC Handbook (Apr 15, 1999), downloaded from www.morc.org
     * Uses 41' as the rating of the "largest" boat as per the MORC tables
     * @param inFinish
     * @return corrected time
     * TESTED THEN COMMENTED OUT - Morc is using a time on distance formula now
    public long getCorrectedTime( Finish inFinish)
    {
        double hoursPerMile = ( 1 / ((1.25 * Math.pow( getValue(), 0.5)) + 0.2)) - sBase41;
        double raceLength = inFinish.getRace().getDivInfo().getLength( inFinish.getEntry().getDivision());
        double allowance = (raceLength * hoursPerMile * 3600.0 * 1000.0);
        return inFinish.getElapsedTime() - (long) allowance;
    }
     */

}

/**
 * $Log: RatingMorc.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.3  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.2  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
