// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingHandicapTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingIrc;
import org.gromurph.javascore.model.ratings.RatingMorc;
import org.gromurph.javascore.model.ratings.RatingMultihull;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.javascore.model.ratings.RatingPortsmouth;

/**
 * Test class for pursuit scoring of a race
 */
public class ScoringPursuitTests extends org.gromurph.javascore.JavascoreTestCase {
	public ScoringPursuitTests(String name) {
		super(name);
	}

	Regatta reg;
	Division div;
	Race race;
	
	Boat boat0;
	Entry entry0;
	Rating rtg0;
	
	Boat boat60;
	Entry entry60;
	Rating rtg60;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		reg = new Regatta();
		reg.removeAllDivisions();
		JavaScoreProperties.setRegatta(reg);
		
		div = new Division("Phrf", new RatingPhrf(-999), new RatingPhrf(999));
		reg.addDivision(div);

		boat0 = new Boat();
		entry0 = new Entry();
		entry0.setBoat(boat0);
		rtg0 = new RatingPhrf(0);
		setRating(entry0, rtg0);
		setDivision(entry0, div);
		reg.addEntry(entry0);
		
		boat60 = new Boat();
		entry60 = new Entry();
		entry60.setBoat(boat60);
		rtg60 = new RatingPhrf(60);
		setRating(entry60, rtg60);
		setDivision(entry60, div);
		reg.addEntry(entry60);

		race = new Race();
		race.setPursuit(true);
		reg.addRace(race);
	}


	public void testPursuitPhrf() {
		
		// race starts at noon 120000 and is 5 miles long
		race.setStartTime(div, SailTime.forceToLong("12:00:00"));
		race.setLength(div, 5.0);

		// start time for a slowest boat that rates 60 should be 12:00
		assertEquals( "earliest start should be 12:00", SailTime.forceToLong("12:00:00"), race.getEarliestStartTime());
		long time = race.getStartTimeAdjusted(div, entry60);
		assertEquals( "start for boat60 should be 12:00", SailTime.forceToLong("12:00:00"), time);
		
		// start time for a boat that rates 0 should be 12:05 
		assertEquals( "start for boat0 should be 12:05", SailTime.forceToLong("12:05:00"), race.getStartTimeAdjusted(div, entry0));
		
		// boat0 finishes at 13:30,  boat60 finishes at 13:29
		Finish finish60 = new Finish(race, entry60, SailTime.forceToLong("13:29:00"), new FinishPosition(Constants.NO_PENALTY), null);
		Finish finish0 = new Finish(race, entry0, SailTime.forceToLong("13:30:00"), new FinishPosition(Constants.NO_PENALTY), null);
		race.setFinish( finish60);
		race.setFinish( finish0);
		
		reg.scoreRegatta();
		
		assertEquals( "elapsed for boat60 should be 1:29:00", SailTime.forceToLong("1:29:00"), race.getFinish(entry60).getElapsedTime());
		assertEquals( "elapsed for boat0 should be 1:25:00", SailTime.forceToLong("1:25:00"), race.getFinish(entry0).getElapsedTime());

		assertEquals( "corrected time for boat60 should be 1:24:00", SailTime.forceToLong("1:24:00"), race.getFinish(entry60).getCorrectedTime());
		assertEquals( "corrected time for boat0 should be 1:25:00", SailTime.forceToLong("1:25:00"), race.getFinish(entry0).getCorrectedTime());

		RacePoints pts = reg.getScoringManager().getRacePointsList().find(race,  entry60,  div);
		assertEquals( "finish position for boat60 should be 1", 1L, pts.getPosition());
		pts = reg.getScoringManager().getRacePointsList().find(race,  entry0,  div);
		assertEquals( "finish position for boat0 should be 2", 2L, pts.getPosition());

	}

	public void testPursuitPhrfShortened() {
		
		// race starts at noon 120000 and is inteded to be 5 miles long
		// boats start, then it is shortened to 3 miles long
		
		race.setStartTime(div, SailTime.forceToLong("12:00:00"));
		race.setLength(div, 5.0);
		
		assertEquals( 5.0, race.getLength(div), 0.0001);
		assertEquals( 5.0, race.getLengthPursuit(div), 0.0001);
		
		race.setPursuitShortened(true);
		race.setLength( div, 3.0);
		assertEquals( 3.0, race.getLength(div), 0.0001);
		assertEquals( 5.0, race.getLengthPursuit(div), 0.0001);

		// start time for a slowest boat that rates 60 should be 12:00
		long time = race.getStartTimeAdjusted(div, entry60);
		assertEquals( "start for boat60 should be 12:00", SailTime.forceToLong("12:00:00"), time);
		
		// start time for a boat that rates 0 and race length of 5 should be 12:05 
		assertEquals( "start for boat0 should be 12:05", SailTime.forceToLong("12:05:00"), race.getStartTimeAdjusted(div, entry0));
		
		// boat0 finishes at 13:30,  boat60 finishes at 13:29
		Finish finish60 = new Finish(race, entry60, SailTime.forceToLong("13:29:00"), new FinishPosition(Constants.NO_PENALTY), null);
		Finish finish0 = new Finish(race, entry0, SailTime.forceToLong("13:30:00"), new FinishPosition(Constants.NO_PENALTY), null);
		race.setFinish( finish60);
		race.setFinish( finish0);
		
		reg.scoreRegatta();
		
		assertEquals( "elapsed for boat60 should be 1:29:00", SailTime.forceToLong("1:29:00"), race.getFinish(entry60).getElapsedTime());
		assertEquals( "elapsed for boat0 should be 1:25:00", SailTime.forceToLong("1:25:00"), race.getFinish(entry0).getElapsedTime());
		
		// with race shortened to 3 miles, entry60 only gets 3 minutes corrective time
		long time60 = race.getFinish(entry60).getCorrectedTime();
		assertEquals( "corrected time for boat60 should be 1:26:00", SailTime.forceToLong("1:26:00"), race.getFinish(entry60).getCorrectedTime());
		assertEquals( "corrected time for boat0 should be 1:25:00", SailTime.forceToLong("1:25:00"), race.getFinish(entry0).getCorrectedTime());

		// with shorter race, boat0 corrects out to first
		RacePoints pts = reg.getScoringManager().getRacePointsList().find(race,  entry60,  div);
		assertEquals( "finish position for boat60 should be 2", 2L, pts.getPosition());
		pts = reg.getScoringManager().getRacePointsList().find(race,  entry0,  div);
		assertEquals( "finish position for boat0 should be 1", 1L, pts.getPosition());

	}


	private void setDivision(Entry e, Division d) {
		try {
			e.setDivision(d);
		} catch (RatingOutOfBoundsException ex) {}
	}
	private void setRating(Entry e, Rating d) {
		try {
			e.setRating(d);
		} catch (RatingOutOfBoundsException ex) {}
	}


}
