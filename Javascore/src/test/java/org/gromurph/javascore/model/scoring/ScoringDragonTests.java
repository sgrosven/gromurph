//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringDragonTests.java,v 1.5 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.SeriesPoints;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringDragonTests extends JavascoreTestCase implements Constants
{
    public final static Class THISCLASS = ScoringDragonTests.class;


    public void testTie1()
    {
        Regatta reg = new Regatta();
        JavaScoreProperties.setRegatta(reg);
        ((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPointDnIceboat.NAME);
        ScoringLowPoint sd1 = (ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel();
        sd1.getOptions().setThrowoutScheme( ScoringLowPoint.THROWOUT_BYNUMRACES);
        sd1.getOptions().getThrowouts().set( 0, new Integer(2));
        sd1.getOptions().getThrowouts().set( 1, new Integer(0));

		assertEquals( "Scoring sys wrong", ScoringLowPointDnIceboat.NAME,
			sd1.getName());

        Division div = new Division( "DN");
        reg.removeAllDivisions();
        reg.addDivision( div);

		Entry e1 = null;
		Entry e2 = null;
		try
		{
			e1 = new Entry(); e1.setSailId( new SailId("10")); e1.setDivision( div);
			e2 = new Entry(); e2.setSailId( new SailId("20")); e2.setDivision( div);
		}
		catch (RatingOutOfBoundsException e)
		{
		}

        reg.addEntry( e1);
        reg.addEntry( e2);

        Race r1 = new Race( reg, "1");
        Race r2 = new Race( reg, "2");
        Race r3 = new Race( reg, "3");
        Race r4 = new Race( reg, "4");
        Race r5 = new Race( reg, "5");

        reg.addRace( r1);
        reg.addRace( r2);
        reg.addRace( r3);
        reg.addRace( r4);
        reg.addRace( r5);

		r1.setFinish( new Finish( r1, e1, SailTime.NOTIME, 
			new FinishPosition(1), ManPenalty( 14)));
		r2.setFinish( new Finish( r2, e1, SailTime.NOTIME, 
			new FinishPosition(1), ManPenalty( 6)));
		r3.setFinish( new Finish( r3, e1, SailTime.NOTIME, 
			new FinishPosition(1), ManPenalty( 16)));
		r4.setFinish( new Finish( r4, e1, SailTime.NOTIME, 
			new FinishPosition(1), ManPenalty( 17)));
		r5.setFinish( new Finish( r5, e1, SailTime.NOTIME, 
			new FinishPosition(1), ManPenalty( 14)));

		r1.setFinish( new Finish( r1, e2, SailTime.NOTIME, 
			new FinishPosition(2), ManPenalty( 6)));
		r2.setFinish( new Finish( r2, e2, SailTime.NOTIME, 
			new FinishPosition(2), ManPenalty( 36)));
		r3.setFinish( new Finish( r3, e2, SailTime.NOTIME, 
			new FinishPosition(2), ManPenalty( 3)));
		r4.setFinish( new Finish( r4, e2, SailTime.NOTIME, 
			new FinishPosition(2), ManPenalty( 5)));
		r5.setFinish( new Finish( r5, e2, SailTime.NOTIME, 
			new FinishPosition(2), ManPenalty( 36)));

        reg.scoreRegatta();

		SeriesPoints e1Total = reg.getScoringManager().getRegattaRanking( e1, div);
		SeriesPoints e2Total = reg.getScoringManager().getRegattaRanking( e2, div);
		
		assertNotNull( e1Total);
		assertNotNull( e2Total);
		
		assertEquals( "e1 should have 50ish pts", 50.0, e1Total.getPoints(), 0.5);
		assertEquals( "e2 should have 50ish pts", 50.0, e2Total.getPoints(), 0.5);
		
		assertEquals( "e1 should be 2nd", 2, e1Total.getPosition());
		assertEquals( "e2 should be 1st", 1, e2Total.getPosition());
    }

	private Penalty ManPenalty( double pts)
	{
		Penalty m = new Penalty( RDG);
		m.setPoints( pts);
		return m;
	}

    public ScoringDragonTests( String name)
    {
        super(name);
    }

}
/**
* $Log: ScoringDragonTests.java,v $
* Revision 1.5  2006/01/15 21:08:39  sandyg
* resubmit at 5.1.02
*
* Revision 1.3  2006/01/15 03:25:51  sandyg
* to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
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
* Revision 1.4  2003/04/27 21:00:54  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/04/20 15:44:30  sandyg
* added javascore.Constants to consolidate penalty defs, and added
* new penaltys TIM (time value penalty) and TMP (time percentage penalty)
*
* Revision 1.2  2003/03/16 20:40:12  sandyg
* 3.9.2 release: encapsulated changes to division list in Regatta,
* fixed a bad bug in PanelDivsion/Rating
*
* Revision 1.1  2003/02/22 13:55:12  sandyg
* no message
*
*/
