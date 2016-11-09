//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringLowPointTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

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
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SailId;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringLongSeriesTests extends JavascoreTestCase
{

    public ScoringLongSeriesTests(String s)
    {
        super(s);
    }

	private void forceDivision( Entry ent, Division d)
	{
		try
		{
			ent.setDivision(d);
		}
		catch (RatingOutOfBoundsException e)
		{
		}
	}
	
	public void testLongSeries()
	{
	    Regatta reg;
	    Division div;
	    Entry e1;
	    Entry e2;
	    Entry e3;

		reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions();

		// set up reg with 1 div, 3 entries, and 9 empty races

		div = new Division("j24");
		reg.addDivision( div);

		e1 = new Entry();
		e1.setSailId( new SailId("1"));
		forceDivision( e1, div);
		reg.addEntry( e1);

		e2 = new Entry();
		e2.setSailId( new SailId("2"));
		forceDivision( e2, div);
		reg.addEntry( e2);

		e3 = new Entry();
		e3.setSailId( new SailId("3"));
		forceDivision( e3, div);
		reg.addEntry( e3);

		Entry e4= new Entry();
		e4.setSailId( new SailId("4"));
		forceDivision( e4, div);
		reg.addEntry( e4);

		Entry e5= new Entry();
		e5.setSailId( new SailId("5"));
		forceDivision( e5, div);
		reg.addEntry( e5);

		Entry e6= new Entry();
		e6.setSailId( new SailId("6"));
		forceDivision( e6, div);
		reg.addEntry( e6);

		Entry e7= new Entry();
		e7.setSailId( new SailId("7"));
		forceDivision( e7, div);
		reg.addEntry( e7);

		Race race1 = new Race( reg, "1");
		reg.addRace( race1);

		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);
		(( ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel()).getOptions().setLongSeries( true);
		
		race1.setFinish( new Finish( race1, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e3, SailTime.NOTIME, new FinishPosition( FinishPosition.DNS), new Penalty(FinishPosition.DNS)));
		race1.setFinish( new Finish( race1, e4, SailTime.NOTIME, new FinishPosition( FinishPosition.DNF), new Penalty(FinishPosition.DNF)));
		race1.setFinish( new Finish( race1, e5, SailTime.NOTIME, new FinishPosition( 3), new Penalty(FinishPosition.RET)));
		race1.setFinish( new Finish( race1, e6, SailTime.NOTIME, new FinishPosition( 4), new Penalty(Penalty.DSQ)));
		race1.setFinish( new Finish( race1, e7, SailTime.NOTIME, new FinishPosition( FinishPosition.DNC), new Penalty(Penalty.DNC)));
		// 6 of 7 boats came to the starting area, 1 did not
		
		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		assertEquals( "e1 points wrong", 		1.0, rpl.find( race1, e1, div).getPoints(), 0.000001);
		assertEquals( "e2 points wrong", 		2.0, rpl.find( race1, e2, div).getPoints(), 0.000001);
		assertEquals( "e3/dns points wrong",	7.0, rpl.find( race1, e3, div).getPoints(), 0.000001);
		assertEquals( "e4/dnf points wrong",	7.0, rpl.find( race1, e4, div).getPoints(), 0.000001);
		assertEquals( "e5/raf points wrong",	7.0, rpl.find( race1, e5, div).getPoints(), 0.000001);
		assertEquals( "e6/dsq points wrong",	7.0, rpl.find( race1, e6, div).getPoints(), 0.000001);
		assertEquals( "e7/dnc points wrong",	8.0, rpl.find( race1, e7, div).getPoints(), 0.000001);

		Entry e8= new Entry();
		e8.setSailId( new SailId("8"));
		forceDivision( e8, div);
		reg.addEntry( e8);

		// 6 of 8 boats came to the starting area, 2 did not
		reg.scoreRegatta();
		
		rpl = reg.getScoringManager().getRacePointsList();
		assertEquals( "e7/dnc points wrong",	9.0, rpl.find( race1, e7, div).getPoints(), 0.000001);
		assertEquals( "e8/nofin points wrong",	9.0, rpl.find( race1, e8, div).getPoints(), 0.000001);
		assertEquals( "e1 points wrong", 		1.0, rpl.find( race1, e1, div).getPoints(), 0.000001);
		assertEquals( "e2 points wrong", 		2.0, rpl.find( race1, e2, div).getPoints(), 0.000001);
		assertEquals( "e3/dns points wrong",	7.0, rpl.find( race1, e3, div).getPoints(), 0.000001);
		assertEquals( "e4/dnf points wrong",	7.0, rpl.find( race1, e4, div).getPoints(), 0.000001);
		assertEquals( "e5/raf points wrong",	7.0, rpl.find( race1, e5, div).getPoints(), 0.000001);
		assertEquals( "e6/dsq points wrong",	7.0, rpl.find( race1, e6, div).getPoints(), 0.000001);
	}

	public void testLongSeriesAvgPoints()
	{

		/**
		 * 
		 * Division: J22 (5 boats) (top)

            Pos	#	Boat		1	2		3		Pts 	Pos
            1	1	one	 		1	1		1		3.00	1
            2	2	two	 		2	2		2		6.00	2
            3	3	three	 	3	3		6/RDG(1)12.00	3
            4	4	four	 	4	5/DNF	5/DNF	14.00	4
            5	5	five	 	5	6/DNC	6/DNC	17.00	5
            Notes
            (1)	Average Points (A10(a))
            (2)	Scoring System is ISAF Low Point 2013-2016
		 */
		// above is wrong:  sail 3 race 3, rdg should be 3 points (3*3/2)
		// set up reg with 1 div, 3 entries, and 9 empty races

	    Regatta reg;
	    Division div;
	    Entry e1;
	    Entry e2;
	    Entry e3;

		reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions();

		div = new Division("j22");
		reg.addDivision( div);

		e1 = new Entry();
		e1.setSailId( new SailId("1"));
		forceDivision( e1, div);
		reg.addEntry( e1);

		e2 = new Entry();
		e2.setSailId( new SailId("2"));
		forceDivision( e2, div);
		reg.addEntry( e2);

		e3 = new Entry();
		e3.setSailId( new SailId("3"));
		forceDivision( e3, div);
		reg.addEntry( e3);

		Entry e4= new Entry();
		e4.setSailId( new SailId("4"));
		forceDivision( e4, div);
		reg.addEntry( e4);

		Entry e5= new Entry();
		e5.setSailId( new SailId("5"));
		forceDivision( e5, div);
		reg.addEntry( e5);

		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);
		(( ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel()).getOptions().setLongSeries( true);
		
		Race race1 = new Race( reg, "1");
		reg.addRace( race1);
		race1.setFinish( new Finish( race1, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e3, SailTime.NOTIME, new FinishPosition(3), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e4, SailTime.NOTIME, new FinishPosition(4), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e5, SailTime.NOTIME, new FinishPosition(5), new Penalty(Penalty.NO_PENALTY)));

		Race race2 = new Race( reg, "2");
		reg.addRace( race2);
		race2.setFinish( new Finish( race2, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Penalty.NO_PENALTY)));
		race2.setFinish( new Finish( race2, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Penalty.NO_PENALTY)));
		race2.setFinish( new Finish( race2, e3, SailTime.NOTIME, new FinishPosition(3), new Penalty(Penalty.NO_PENALTY)));
		race2.setFinish( new Finish( race2, e4, SailTime.NOTIME, new FinishPosition(FinishPosition.DNF), new Penalty(FinishPosition.DNF)));
		race2.setFinish( new Finish( race2, e5, SailTime.NOTIME, new FinishPosition(FinishPosition.DNC), new Penalty(FinishPosition.DNC)));

		Race race3 = new Race( reg, "3");
		reg.addRace( race3);
		race3.setFinish( new Finish( race3, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Penalty.NO_PENALTY)));
		race3.setFinish( new Finish( race3, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Penalty.NO_PENALTY)));
		race3.setFinish( new Finish( race3, e3, SailTime.NOTIME, new FinishPosition(FinishPosition.DNC), new Penalty(FinishPosition.AVG)));
		race3.setFinish( new Finish( race3, e4, SailTime.NOTIME, new FinishPosition(FinishPosition.DNF), new Penalty(FinishPosition.DNF)));
		race3.setFinish( new Finish( race3, e5, SailTime.NOTIME, new FinishPosition(FinishPosition.DNC), new Penalty(FinishPosition.DNC)));

		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		assertEquals( "1, e1 points wrong", 1.0, rpl.find( race1, e1, div).getPoints(), 0.000001);
		assertEquals( "1, e2 points wrong", 2.0, rpl.find( race1, e2, div).getPoints(), 0.000001);
		assertEquals( "1, e3 points wrong",	3.0, rpl.find( race1, e3, div).getPoints(), 0.000001);
		assertEquals( "1, e4 points wrong",	4.0, rpl.find( race1, e4, div).getPoints(), 0.000001);
		assertEquals( "1, e5 points wrong",	5.0, rpl.find( race1, e5, div).getPoints(), 0.000001);

		assertEquals( "2, e1 points wrong", 1.0, rpl.find( race2, e1, div).getPoints(), 0.000001);
		assertEquals( "2, e2 points wrong", 2.0, rpl.find( race2, e2, div).getPoints(), 0.000001);
		assertEquals( "2, e3 points wrong",	3.0, rpl.find( race2, e3, div).getPoints(), 0.000001);
		assertEquals( "2, e4/DNF points wrong",	5.0, rpl.find( race2, e4, div).getPoints(), 0.000001);
		assertEquals( "2, e5/DNC points wrong",	6.0, rpl.find( race2, e5, div).getPoints(), 0.000001);

		assertEquals( "3, e1 points wrong", 1.0, rpl.find( race3, e1, div).getPoints(), 0.000001);
		assertEquals( "3, e2 points wrong", 2.0, rpl.find( race3, e2, div).getPoints(), 0.000001);
		
		RacePoints rp = rpl.find( race3, e3, div);
		assertEquals( "3, e3/AVG points wrong",	3.0, rpl.find( race3, e3, div).getPoints(), 0.000001);
		assertEquals( "3, e4/DNF points wrong",	5.0, rpl.find( race3, e4, div).getPoints(), 0.000001);
		assertEquals( "3, e5/DNC points wrong",	6.0, rpl.find( race3, e5, div).getPoints(), 0.000001);
	}


}

