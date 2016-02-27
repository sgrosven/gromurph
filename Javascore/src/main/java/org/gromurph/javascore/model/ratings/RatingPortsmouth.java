//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingPortsmouth.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
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
 * Portsmouth Yardstick support... see www.ussailing.org/portsmouth for
 * description.  This is a first cut at Portsmouth support.  It does not
 * yet support different wind factors (unless you change the rating for
 * a boat to the appropriate wind-based factor).
**/
public class RatingPortsmouth extends RatingYardstick
{
    public static final String SYSTEM = "Portsmouth";

    public RatingPortsmouth()
    {
        super( SYSTEM, RatingYardstick.MAX_RATING);
    }

    public RatingPortsmouth( double inV)
    {
        super( SYSTEM, inV);
    }

    @Override public int getDecs() {return 1;}

    /**
     * Creates a new instance of the maximum/fastest overall rating allowed by the rating system
     * @return
     */
    @Override public Rating createFastestRating()
    {
        return new RatingPortsmouth( 0.0);
    }

    /**
     * Creates a new instance of the minimum/slowest overall rating allowed by the rating system
     * @return
     */
    @Override public Rating createSlowestRating()
    {
        return new RatingPortsmouth( 9999.0);
    }

}
/**
 * $Log: RatingPortsmouth.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8  2005/04/27 02:45:47  sandyg
 * Added Yardstick, and added Yardstick and IRC all to GUI.  Portsmouth now trivial subclass of yardstick
 *
 * Revision 1.7  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.6  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/04/27 21:36:17  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
