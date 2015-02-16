//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingOutOfBoundsException.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.exception;

import java.util.ResourceBundle;
 
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.ratings.Rating;

/**
 * Exception thrown when trying to add a rating to RatingList when RatingList
 * already contains a rating of the same system
**/
public class RatingOutOfBoundsException extends java.lang.Exception
{
    protected static ResourceBundle res = JavaScoreProperties.getResources();

    Division fDivision;
    Rating  fBadRating;

    public RatingOutOfBoundsException()
    {
        this( null, null);
    }

    public RatingOutOfBoundsException( Division div, Rating rtg)
    {
        fDivision = div;
        fBadRating = rtg;
    }

    @Override public String toString()
    {
        if (fDivision != null && fBadRating != null)
        {
            StringBuffer sb = new StringBuffer( 50);
            sb.append( "Rating, ");
            sb.append( fBadRating);
            sb.append( ", is out of bounds for division, ");
            sb.append( fDivision);
            return sb.toString();
        }
        else
        {
            return "Rating is out of bounds for Division";
        }
    }
}

/**
 * $Log: RatingOutOfBoundsException.java,v $
 * Revision 1.5  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.1  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
