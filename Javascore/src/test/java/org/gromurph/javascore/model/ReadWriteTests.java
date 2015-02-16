// === File Prolog===========================================================
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
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.util.Util;

/**
 * Dummy Boat class for create unit test cases
 */
public class ReadWriteTests extends org.gromurph.javascore.JavascoreTestCase {
	public ReadWriteTests(String name) {
		super(name);
	}

	Regatta reg;
	
	@Override
	protected void setUp() throws Exception {
		// intentionally does not call parent
	}


	public void testReadMasterDivisions() throws ScoringException {
		RatingManager.initializeElements(true);
		assertNotNull( RatingManager.getSupportedSystems());
		
		DivisionList.initializeMasterList();
		assertNotNull( DivisionList.getMasterList());
		assertTrue( DivisionList.getMasterList().size() > 3);
		
		boolean b = RatingManager.saveRatingElements();
		assertTrue(b);
	}
	
	public void testReadRegatta() throws Exception
	{
		Regatta reg2 = loadTestRegatta( "102-Swedish-test.regatta");	
		assertNotNull( reg2);
	}

}
/**
*/
