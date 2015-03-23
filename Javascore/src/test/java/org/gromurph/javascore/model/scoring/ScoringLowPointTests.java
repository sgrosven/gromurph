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
public class ScoringLowPointTests extends JavascoreTestCase
{

    public ScoringLowPointTests(String s)
    {
        super(s);
    }

    Regatta reg;
    Division div;
    Entry e1;
    Entry e2;
    Entry e3;

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
	
	@Override protected void setUp()
    {
		reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);
		reg.removeAllDivisions();
    }

	public void testLongSeries()
	{

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

	public void testTiedWithPenalties()
	{
//		10 boat fleet, 2 boats, A and B, tied for 2nd place...
//
//		B also has a 20% penalty.
//
//		Javascore gives both boats 3.5 points
//		A should get 2.5
//		B probably "should" get 4.5 (2.5 + 2) but the literal
//		rule 44 could be interpreted as giving "fin place" plus
//		20% or (2 + 2).
		
		// set up reg with 1 div, 3 entries, and 9 empty races
		div = new Division("j24");
		reg.addDivision( div);
		
		EntryList entries = new EntryList();
		for (int i = 0; i < 10; i++)
		{
			Entry e = new Entry();
			e.setBoatName( Integer.toString(i));
			e.setSailId( new SailId( Integer.toString(i)));
			forceDivision( e, div);
			reg.addEntry( e);
			entries.add(e);
		}
		
		Race r = new Race( reg, "1");
	
		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);
		reg.addRace(r);
	
		for (int i = 0; i < 10; i++)
		{
			if (i != 1 && i != 2)
			{
				r.setFinish( new Finish( r, entries.get(i), SailTime.NOTIME, new FinishPosition(i+1), new Penalty(Penalty.NO_PENALTY)));
			}
		}
		r.setFinish( new Finish( r, entries.get(1), SailTime.forceToLong("10:00:00"), new FinishPosition(1), 
			new Penalty(Penalty.NO_PENALTY)));
		r.setFinish( new Finish( r, entries.get(2), SailTime.forceToLong("10:00:00"), new FinishPosition(2), 
			Penalty.parsePenalty( "P20")));
		
		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		Entry a = entries.get(1);
		Entry b = entries.get(2);
		assertEquals( "bad points B", 4.5, rpl.find( r, b, div).getPoints(), 0.000001);
		assertEquals( "bad points A", 2.5, rpl.find( r, a, div).getPoints(), 0.000001);
		
	}
	
	public void testOneDesignTies()
	{
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

		Race race1 = new Race( reg, "1");
		reg.addRace( race1);

		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);
		(( ScoringLowPoint) ((SingleStageScoring) reg.getScoringManager()).getModel()).getOptions().setLongSeries( true);
		
		race1.setFinish( new Finish( race1, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e2, SailTime.forceToLong( "12:05:10"), new FinishPosition(2), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e3, SailTime.forceToLong( "12:05:10"), new FinishPosition(3), new Penalty(Penalty.NO_PENALTY)));
		race1.setFinish( new Finish( race1, e4, SailTime.NOTIME, new FinishPosition(4), new Penalty(Penalty.NO_PENALTY)));
		// 6 of 7 boats came to the starting area, 1 did not
		
		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		assertEquals( "e1/1st points wrong", 		1.0, rpl.find( race1, e1, div).getPoints(), 0.000001);
		assertEquals( "e2/tie 2.5, points wrong", 	2.5, rpl.find( race1, e2, div).getPoints(), 0.000001);
		assertEquals( "e3/tie 2.5 points wrong",	2.5, rpl.find( race1, e3, div).getPoints(), 0.000001);
		assertEquals( "e4/4st points wrong",		4.0, rpl.find( race1, e4, div).getPoints(), 0.000001);

	}

    public void testXml()
    {
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

		for ( int r = 0; r < 9; r++)
		{
			Race race = new Race( reg, Integer.toString(r));
			reg.addRace( race);
		}
		reg.scoreRegatta();

        ScoringOptions sd1 = ((SingleStageScoring) reg.getScoringManager()).getModel().getOptions();

        // default Division goes to/from xml ok
        assertTrue( "sd1", xmlEquals( sd1));

        // muck with sd1
        sd1.setThrowoutScheme( ScoringLowPoint.THROWOUT_BESTXRACES);
        sd1.setThrowoutBestX( 7);

        reg.scoreRegatta();
        
        assertTrue( "xml equals sd1 bestx", xmlEquals( sd1));
        RacePointsList rpl = reg.getScoringManager().getRacePointsList();
        rpl = rpl.findAll(e1);
        int numThrow = rpl.getNumberThrowouts();
        assertEquals( "with best 7", 2, numThrow);

        sd1.setThrowoutScheme( ScoringLowPoint.THROWOUT_PERXRACES);
        sd1.setThrowoutPerX( 3);
        reg.scoreRegatta();

        assertTrue( "xml equals sd1 perx", xmlEquals( sd1));
        assertEquals( "per 3, 9 races", 3, reg.getScoringManager().getRacePointsList().findAll(e1).getNumberThrowouts());

        sd1.setThrowoutPerX( 5);
        reg.scoreRegatta();
        
        assertEquals( "per 5, 9 races", 1, reg.getScoringManager().getRacePointsList().findAll(e1).getNumberThrowouts());

        sd1.setThrowoutScheme( ScoringLowPoint.THROWOUT_BYNUMRACES);
        sd1.getThrowouts().set( 0, new Integer(3));
        sd1.getThrowouts().set( 1, new Integer(5));
        assertTrue( "xml equals sd1 bynumraces, 1@2, 2@5", xmlEquals( sd1));

        Race r = new Race( reg, "10");
        reg.addRace( r);
        reg.scoreRegatta();

        assertEquals( "per 5, 10 races", 2, reg.getScoringManager().getRacePointsList().findAll(e1).getNumberThrowouts());
    }
    
    public void testZFPPenalties()
	{
		// set up reg with 1 div, 3 entries, and 9 empty races
		div = new Division("j24");
		reg.addDivision( div);
	
		Race race1 = new Race( reg, "1");
		reg.addRace( race1);
		((SingleStageScoring) reg.getScoringManager()).setModel( ScoringLowPoint.NAME);

		int NN = 8;
		Entry[] entries = new Entry[NN];
		Finish[] finishes = new Finish[NN];
		for( int i = 0; i < NN; i++) {
			Entry e = new Entry();
			e.setSailId( new SailId( Integer.toString(i)));
			forceDivision( e, div);
			reg.addEntry( e);
			
			Finish f = new Finish( race1, e, SailTime.NOTIME, new FinishPosition( i+1), new Penalty(Penalty.NO_PENALTY));
			finishes[i]=f;
			race1.setFinish( f);
			entries[i]=e;
		}
		finishes[1].getPenalty().addOtherPenalty( Constants.ZFP);
		
		finishes[2].getPenalty().addOtherPenalty( Constants.ZFP);
		finishes[2].getPenalty().addOtherPenalty( Constants.ZFP2);
		
		finishes[3].getPenalty().addOtherPenalty( Constants.ZFP2);
		finishes[3].getPenalty().addOtherPenalty( Constants.ZFP3);
		
		Penalty pct40 = new Penalty( Constants.SCP);
		pct40.setPercent(40);
		finishes[4].setPenalty( pct40);
		
		reg.scoreRegatta();
		RacePointsList rpl = reg.getScoringManager().getRacePointsList();
		
		double[] points = new double[NN];
		for( int i = 0; i < NN; i++) {
			points[i] = rpl.find( race1, entries[i], div).getPoints();
		}
		
		double pct20 = Math.round(  NN*0.2);
		double p40 = Math.round(  NN*0.4);
		assertEquals( 1.00, points[0], 0.000001);
		assertEquals( 2.00 + pct20, points[1], 0.000001);
		assertEquals( 3.00 + pct20 + pct20, points[2], 0.000001);
		assertEquals( 4.00 + pct20 + pct20, points[3], 0.000001);
		assertEquals( 5.00 + p40, points[4], 0.000001);

	}

}

