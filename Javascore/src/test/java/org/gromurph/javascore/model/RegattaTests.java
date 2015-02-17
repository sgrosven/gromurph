//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RegattaTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.util.Util;

/**
 * Unit test scripts for Regatta class
 */
public class RegattaTests extends org.gromurph.javascore.JavascoreTestCase
{
	public void test102Swedish()
	{
		assertTrue( doFullCheck( "102-Swedish-test.regatta"));
	}

	public boolean doFullCheck( String rname)
	{
		int i = 0;
		try
		{
			Regatta baseline = loadTestRegatta( rname);
			assertNotNull( "Unable to read: " + rname, baseline);
			
			Regatta tester = loadTestRegatta( rname);
			assertNotNull( "Unable to read: " + rname, tester);
			String n = baseline.getName();

			i++;
			assertEquals( n + " init equals", tester, baseline);
			i++;
			assertEquals( n + " equals clone()", tester, tester.clone());

			i++;
			tester.scoreRegatta();
			readwriteCheck( tester);

			i++; readwriteCheck( baseline);
			return true;
		}
		catch (Exception e)
		{
			logger.error("Exception in RegattaTests.doFullCheck( rname={}), step={}", rname, i);
			e.printStackTrace( System.out);
			return false;
		}
	}

	protected void readwriteCheck( Regatta reg) throws Exception
	{
		new RegattaManager( reg).writeRegattaToDisk( Util.getWorkingDirectory(), "test.regatta");
		
		Regatta reg2 = RegattaManager.readRegattaFromDisk("test.regatta");		
		assertEquals( "readwrite " + reg.getName(), reg, reg2);
	}

	public RegattaTests( String name)
	{
		super(name);
	}

//	public static Test suite()
//	{
//		return new TestSuite( RegattaTests.class);
//	}
//
//	public static void main(String[] args)
//	{
//		org.gromurph.util.Util.setTesting(true);
//		String[] testCases = { RegattaTests.class.getName() };
//		junit.textui.TestRunner.main( testCases);
//	}
}
/**
* $Log: RegattaTests.java,v $
* Revision 1.4  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.2  2006/01/11 02:20:26  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:02  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.8  2005/06/26 22:48:08  sandyg
* Xml overhaul to remove xerces dependence
*
* Revision 1.7  2005/05/26 01:46:51  sandyg
* fixing resource access/lookup problems
*
* Revision 1.6  2005/04/23 21:55:31  sandyg
* JWS mods for release 4.3.1
*
* Revision 1.5  2004/04/10 22:19:38  sandyg
* Copyright update
*
* Revision 1.4  2003/04/27 21:00:52  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/01/04 17:09:27  sandyg
* Prefix/suffix overhaul
*
*/
