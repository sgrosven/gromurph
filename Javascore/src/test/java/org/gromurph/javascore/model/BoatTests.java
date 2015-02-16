//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BoatTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.util.Person;

/**
 * Dummy Boat class for create unit test cases
 */
public class BoatTests extends org.gromurph.javascore.JavascoreTestCase
{
    public BoatTests( String name)
    {
        super(name);
    }

    Boat one;
    Boat two;
    Boat three;

    @Override protected void setUp() throws ScoringException
    {
        one = new Boat();
        two = new Boat();
        three = new Boat();
        three.setName( "Road Trip");
        three.setOwner( new Person("Mark", "Murphy"));
        three.setSailId( new SailId("USA 1044"));
        three.putRating( new RatingPhrf( 33));
        three.putRating( new RatingOneDesign("J22"));
    }

    public void testEquals()
    {
        assertEquals( "empty one=two", one, two);
        Boat onep = (Boat) one.clone();
        assertEquals( "one clone", one, onep);
        Boat threep = (Boat) three.clone();
        assertEquals( "three clone", three, threep);
        assertTrue( "one != three", !one.equals(three));
    }

    public void testXml()
    {
        assertTrue( "xmlequals one", xmlEquals( one));
        assertTrue( "xmlequals one", xmlEquals( three));
    }

}
/**
* $Log: BoatTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.5  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.4  2003/04/27 21:00:35  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
