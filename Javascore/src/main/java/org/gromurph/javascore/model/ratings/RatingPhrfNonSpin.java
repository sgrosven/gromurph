//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingPhrfNonSpin.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
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
 * PHRF NonSpin ratings... only diff from PHRF is classname
**/
public class RatingPhrfNonSpin extends RatingPhrf
{
    public static final String SYSTEM = "PHRFNonSpin";

    public RatingPhrfNonSpin()
    {
        super( SYSTEM, 0);
    }

    public RatingPhrfNonSpin( int inRat)
    {
        super( SYSTEM, inRat);
    }

}
/**
 * $Log: RatingPhrfNonSpin.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
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
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
