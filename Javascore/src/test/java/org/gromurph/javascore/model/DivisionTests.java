//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DivisionTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;

public class DivisionTests extends JavascoreTestCase
{
	public DivisionTests( String name)
	{
		super(name);
	}

	public void testEquals()
	{
		Division onea = new Division("One");
		Division oneb = new Division("One");
		RatingOneDesign j24 = new RatingOneDesign("J24");
		Division j24a = new Division("Two", j24, new RatingOneDesign("J24"));
		Division j24b = new Division("Two", new RatingOneDesign("J24"), new RatingOneDesign("J24"));
		Division j24c = new Division("Two", j24, j24);
		Division phrfa = new Division("Phrf", new RatingPhrf( 0), new RatingPhrf( 30));
		Division phrfb = new Division("Phrf", new RatingPhrf( 0), new RatingPhrf( 30));

		assertEquals( "onea==b", onea, oneb);

		assertEquals( "j24a==b", j24a, j24b);
		assertEquals( "j24a==c", j24a, j24c);
		assertEquals( "j24b==c", j24b, j24c);


		assertEquals( "phrfa==b", phrfa, phrfb);
	}

	public void testXml() throws java.io.IOException
	{
		Division onea = new Division("One");
		RatingOneDesign j24 = new RatingOneDesign("J24");
		Division j24a = new Division("Two", j24, new RatingOneDesign("J24"));
		Division phrfa = new Division("Phrf", new RatingPhrf( 0), new RatingPhrf( 30));

		assertTrue( "onea", xmlEquals( onea));
		assertTrue( "j24a", xmlEquals( j24a));
		assertTrue( "phrfa", xmlEquals( phrfa));
	}
	

}
/**
 * $Log: DivisionTests.java,v $
 * Revision 1.4  2006/01/15 21:08:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:20:26  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.9  2004/04/10 22:19:38  sandyg
 * Copyright update
 *
 * Revision 1.8  2003/04/27 21:00:40  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/03/16 20:39:15  sandyg
 * 3.9.2 release: encapsulated changes to division list in Regatta,
 * fixed a bad bug in PanelDivsion/Rating
 *
 * Revision 1.6  2003/01/04 17:09:27  sandyg
 * Prefix/suffix overhaul
 *
*/
