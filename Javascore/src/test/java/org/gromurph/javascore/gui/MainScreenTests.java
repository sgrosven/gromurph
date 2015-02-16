//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: MainScreenTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

package org.gromurph.javascore.gui;

import org.gromurph.javascore.JavascoreTestCase;




/**
 * Covering unit tests for the PanelRegatta class
 */
public class MainScreenTests extends JavascoreTestCase
{

	public MainScreenTests(String name)
	{
		super(name);
	}

	public void testMainPanel()
	{
//		//Set windows = fHelper.getWindows();
//		assertEquals("Number of showing dialogs should be 0", 0, getOpenWindowCount());
//		assertEquals("Number of windows should be 0", 0, TestHelper.getWindows().size());		
//             
//		JFrame mainFrame = JavaScore.getInstance();
//		assertNotNull( mainFrame);
//		
//		mainFrame.setVisible( true);
//				
//		assertEquals("Number of windows should be 1", 1, TestHelper.getWindows().size());		
//
//		JMenu menuFile = (JMenu) TestHelper.findComponent(
//			new NamedComponentFinder( JMenu.class, "fMenuFile"),
//			mainFrame,
//			0);             
//		assertNotNull( "cant find 'fMenuFile' menu", menuFile);
//		
//		try
//		{
//			fHelper.enterClickAndLeave( new MouseEventData( this, menuFile));
//		}
//		catch (Exception e)
//		{
//			fail(e.toString());
//		}
//
//		JMenuItem fileNew = (JMenuItem) TestHelper.findComponent(
//			new JMenuItemFinder( "New Regatta"),
//			//mainFrame,
//			0);             
//		assertNotNull( "cant find 'New Regatta' menu item", fileNew);
//			
//		try
//		{
//			fHelper.enterClickAndLeave( new MouseEventData( this, fileNew));
//		}
//		catch (Exception e)
//		{
//			fail(e.toString());
//		}
//		awtSleep(2000);
//
//		assertEquals("Wrong number of windows", 2, TestHelper.getWindows().size());		

	}
}

/*
 * $Log: MainScreenTests.java,v $
 * Revision 1.4  2006/01/15 21:08:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:20:26  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.2.2.1  2005/11/26 17:44:21  sandyg
 * implement race weight & nondiscardable, did some gui test cleanups.
 *
 * Revision 1.2  2004/04/10 22:19:38  sandyg
 * Copyright update
 *
 * Revision 1.1  2003/05/18 17:21:21  sandyg
 * no message
 *
 */

