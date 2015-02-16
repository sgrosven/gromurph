//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ReportOptionsTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.util.Person;

/**
 * Test cases for the Report Options class
 */
public class ReportOptionsTests extends org.gromurph.javascore.JavascoreTestCase
{
    public ReportOptionsTests( String name)
    {
        super(name);
    }

    public void testEquals()
    {
        ReportOptions one = new ReportOptions();

        ReportOptions two = new ReportOptions("tabx");
        two.setFontName( "x");
        two.setFontSize( 33);
        two.setIncludeOneDesignTimes( !two.isIncludeOneDesignTimes());
        two.setNameFormat( Person.FORMAT_LASTONLY);
        two.setTemplateFile("boo");
        two.setOptionLocationValues( 1, "2.2");

        ReportOptions onep = (ReportOptions) one.clone();
        assertEquals( "one clone", one, onep);
        ReportOptions twop = (ReportOptions) two.clone();
        assertEquals( "two clone", two, twop);
        assertTrue( "one != two", !one.equals(two));
    }

    public void testXml()
    {
        ReportOptions one = new ReportOptions();
        assertTrue( "xmlequals one", xmlEquals(one));

        ReportOptions two = new ReportOptions("tabx");
        two.setFontName( "x");
        two.setFontSize( 33);
        two.setIncludeOneDesignTimes( !two.isIncludeOneDesignTimes());
        two.setNameFormat( Person.FORMAT_LASTONLY);
        two.setTemplateFile("boo");
        two.setOptionLocationValues( 1, "2.2");
        two.setShowLastXRaces( true);
        two.setLastXRaces( 8);
        two.setHidePenaltyPoints( true);

        assertTrue( "xmlequals two", xmlEquals(two));
    }

}
/**
* $Log: ReportOptionsTests.java,v $
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
* Revision 1.4  2003/04/27 21:00:53  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
