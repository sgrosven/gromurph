//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ComparatorNulls.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.util.Comparator;

/**
 * provides central sorting/equals handling of possible null objects
 */
public class ComparatorNulls implements Comparator
{

    public ComparatorNulls()
    {
        // do nothing
    }

    public int compare(Object left, Object right)
    {
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
        else return 0;
    }
}

/**
 * $Log: ComparatorNulls.java,v $
 * Revision 1.4  2006/01/15 21:10:34  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
