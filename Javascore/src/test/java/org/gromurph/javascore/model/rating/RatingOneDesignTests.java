//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingOneDesignTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.rating;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingOneDesign;

public class RatingOneDesignTests extends org.gromurph.javascore.JavascoreTestCase
{
    public RatingOneDesignTests( String name)
    {
        super(name);
    }

    private static Regatta regatta;

    @Override public void setUp() throws Exception
    {
		try
		{
			regatta = loadTestRegatta( "0000-Test-Master.regatta");
		} catch (Exception e) {}
    }

    public void testEquals()
    {
        RatingOneDesign onea = new RatingOneDesign( "One");
        RatingOneDesign oneb = new RatingOneDesign( "One");
        RatingOneDesign two = new RatingOneDesign("Two");

        assertEquals( "one a==b", onea, oneb);
        assertEquals( "one = one.clone()", onea, onea.clone());
        assertTrue( "one != two", !onea.equals(two));
    }

    public void testCorrectedTime()
    {
        Race race = new Race( regatta, "z");
        Division div = new Division( "J24");
        Entry entry = (Entry) div.getEntries().get(0);

        long threePM = SailTime.forceToLong("15:00:00");
        long noon = SailTime.forceToLong( "12:00:00");
        long threeHours = SailTime.forceToLong( "3:00:00");

        Finish finish = new Finish( race, entry, threePM, new FinishPosition(1), null);

        // start time is undefined, should get 150000 for corrected time
        assertEquals( threePM, finish.getCorrectedTime());

        // with noon start time, should get 3 hours
        race.setStartTime( div, noon);
        assertEquals( threeHours, finish.getCorrectedTime());

    }

}
/**
* $Log: RatingOneDesignTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.6  2005/05/26 01:46:51  sandyg
* fixing resource access/lookup problems
*
* Revision 1.5  2005/04/23 21:55:31  sandyg
* JWS mods for release 4.3.1
*
* Revision 1.4  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.3  2003/04/27 21:00:52  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.2  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
