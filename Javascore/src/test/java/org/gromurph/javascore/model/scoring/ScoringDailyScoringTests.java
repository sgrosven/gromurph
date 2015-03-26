//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringDailyScoringTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringDailyScoringTests extends JavascoreTestCase
{

    public ScoringDailyScoringTests(String s) {
        super(s);
    }

	public void testDailySeries() throws Exception {
		
		Regatta reg = loadTestRegatta( "2007_NOOD_DIV_1.regatta");
		assertNotNull(reg);
		
		assertTrue( reg.isDailyScoring());
		assertTrue( reg.getScoringManager() instanceof DailyStageScoring);
		
		// starts without daily scoring, should be singlestage event
		reg.setDailyScoring(false);
		assertFalse( reg.isDailyScoring());
		assertTrue( reg.getScoringManager() instanceof SingleStageScoring);
		
		reg.setDailyScoring(true);
		assertTrue( reg.isDailyScoring());
		assertTrue( reg.getScoringManager() instanceof DailyStageScoring);
		
		DailyStageScoring mgr = (DailyStageScoring) reg.getScoringManager();
		assertEquals( "pre-scoring, should have 1 stage - alldays", 1, mgr.getStages().size());
		
		reg.scoreRegatta();
		assertEquals( "should now have 4 stages - alldays, and fri/sat/sun", 4, mgr.getStages().size());
	}

}

