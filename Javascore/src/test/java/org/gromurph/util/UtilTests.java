//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: UtilTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

/**
 * Tests on org.gromurph.util.Util
 */
public class UtilTests extends org.gromurph.util.UtilTestCase
{
    public UtilTests( String name)
    {
        super(name);
    }

    private static char sEuroBlank = new Character( (char) 160).charValue();
    
    @Override public void tearDown() throws Exception
    {
    	super.tearDown();
		Util.initLocale( java.util.Locale.US);
    }

    public void testFormatDouble()
    {
        double d = 1.234;
        assertEquals( "1.234", Util.formatDouble( d, 3, 1));

        Util.initLocale( java.util.Locale.FRANCE);
        assertEquals( "1,234", Util.formatDouble( d, 3, 1));

        d = 1345.6781;
        String exp = "1345,678";
        String resp = Util.formatDouble( d, 3);

        assertEquals( exp, resp);
    }

    public void testParseDouble()
    {
        Util.initLocale( java.util.Locale.US);
        String s = "1.234";
        assertEquals( s, 1.234, Util.parseDouble( s), .0000001);

        s = "1,345.678";
        assertEquals( s, 1345.678, Util.parseDouble( s), .0000001);

        Util.initLocale( java.util.Locale.FRANCE);
        s = "1,234";
        assertEquals( s, 1.234, Util.parseDouble( s), .0000001);

        s = "1" + sEuroBlank + "345,678";
        assertEquals( s, 1345.678, Util.parseDouble( s), .0000001);
        
    }

}
/**
 * $Log: UtilTests.java,v $
 * Revision 1.4  2006/01/15 21:08:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:46  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:03  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5  2004/04/10 22:19:41  sandyg
 * Copyright update
 *
 * Revision 1.4  2003/04/27 21:01:17  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.3  2003/03/30 00:04:09  sandyg
 * gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
 *
 * Revision 1.2  2003/01/04 17:13:06  sandyg
 * Prefix/suffix overhaul
 *
*/
