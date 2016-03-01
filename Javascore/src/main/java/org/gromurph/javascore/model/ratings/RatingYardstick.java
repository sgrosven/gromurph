//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingYardstick.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
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
 * IRC system, basic coefficient driven time-on-time

**/
public class RatingYardstick extends RatingCoefficient
{
    public static final String SYSTEM = "Yardstick";
    protected static double SLOWEST_RATING = 0.1;
    protected static double FASTEST_RATING = 999.0;
    

    public RatingYardstick()
    {
        super( SYSTEM, FASTEST_RATING);
    }

    public RatingYardstick( double inV)
    {
        super( SYSTEM, inV);
    }

    public RatingYardstick( String system, double inV)
    {
        super( system, inV);
    }

    @Override public int getDecs() {return 1;}
    
    
    @Override protected double getCoefficient()
    {
    	return 100.0/getPrimaryValue();
    }

    /**
     * Creates a new instance of the fastest overall rating allowed by the rating system
     * @return
     */
    @Override public Rating createFastestRating()
    {
        return new RatingYardstick( FASTEST_RATING);
    }

    /**
     * Creates a new instance of the slowest overall rating allowed by the rating system
     * @return
     */
    @Override public Rating createSlowestRating()
    {
        return new RatingYardstick( SLOWEST_RATING);
    }

}
/**
 * $Log: RatingYardstick.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.1  2005/04/27 02:45:47  sandyg
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
