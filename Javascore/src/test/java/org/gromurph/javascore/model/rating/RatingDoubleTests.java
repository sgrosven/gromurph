//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingDoubleTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.rating;

import org.gromurph.javascore.model.ratings.RatingDouble;
import org.gromurph.javascore.model.ratings.RatingPhrf;

public class RatingDoubleTests extends org.gromurph.javascore.JavascoreTestCase
{

    public RatingDoubleTests( String name)
    {
        super(name);
    }

    public void testEquals()
    {
        RatingDouble onea = new RatingPhrf(0);
        RatingDouble oneb = new RatingPhrf(0);
        RatingDouble onec = new RatingPhrf( RatingPhrf.SYSTEM, 0);
        RatingDouble two = new RatingPhrf(2);
        RatingDouble threea = new RatingPhrf("boo", 0);
        RatingDouble threeb = new RatingPhrf("hiss", 0);

        assertEquals( "one a==b", onea, oneb);
        assertEquals( "one = one.clone()", onea, onea.clone());
        assertEquals( "one a=c", onea, onec);

        assertTrue( "one != two", !onea.equals(two));
        assertTrue( "threea != threeb", !threea.equals(threeb));
    }

}
/**
* $Log: RatingDoubleTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.4  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.3  2003/04/27 21:00:49  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.2  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
