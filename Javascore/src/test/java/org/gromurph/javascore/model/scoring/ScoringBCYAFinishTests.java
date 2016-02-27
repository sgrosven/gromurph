// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringFleetTests.java,v 1.6 2006/01/19 02:27:41 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.*;
import org.gromurph.javascore.model.ratings.RatingMultihull;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringBCYAFinishTests extends org.gromurph.javascore.JavascoreTestCase {

	Regatta reg;
	Division multis;
	Race race1;

	/*
	 * Pos 	Sail 	Boat 			Skipper 	Rating FOrder 	FTime	Allow 		Corrected 	Behind 		Pts
		1*   131 	CIRCUS		ROB BLESSE 		1.035 	2 	15:29:50.0 	-00:07:41.6 03:47:31.6 	00:00:00.0 1.0
		2T  228 	TRIANGLE 	GARY SPESARD 	1.026 	4 	15:41:15.0 	-00:06:00.8 03:57:15.8 	00:09:44.1 2.5T
		2T  410 	LOLA 		RUSSELL WESDYK 	0.950 	7 	15:59:45.0 	00:12:29.2 	03:57:15.8 	00:09:44.1 2.5T
		4   185 	CURVE 	JOHN NICHOLOSN 	1.008 	5 	15:47:02.0 	-00:01:53.8 03:58:55.8 	00:11:24.1 4.0
		5*   18 	WILDCARD 	TIM LAYNE 		1.170 	3 	15:29:43.0 	-00:37:21.1 04:17:04.1 	00:29:32.5 5.0	
		6   27042 	TEMPLE  	DOUGLAS DYKMAN 	1.037 	6 	15:58:21.0 	-00:09:11.3 04:17:32.3 	00:30:00.7 6.0
		7   OO 1 	TRIPLE  	TIM LYONS 		1.080 	8 	16:00:11.0 	-00:20:00.9 04:30:11.9 	00:42:40.2 7.0
		8   1055 	ENDURANCE 	JEFFREY SHORT 	0.780 	9 	17:45:00.0 	01:18:06.0 	04:36:54.0 	00:49:22.4 8.0
		9   14 		SUNDOG 		PAUL PARKS 		1.351 	1 	15:21:47.0 	-01:14:20.2 04:46:07.2 	00:58:35.5 9.0
		Notes:(1)	Start Date/Time = Saturday, October 22, 2011 11:50:00.0, Length (nm) = 19.20

		Above from bcya, asterisk boats' finish order of #131 and #18 are backwards..
	 */
	public void testBCYAFinishOrderIssue() throws Exception {
		multis = new Division();
		reg.addDivision(multis);

		multis.setSlowestRating(new RatingMultihull(0.001));
		multis.setFastestRating(new RatingMultihull(2.000));
		reg.addDivision(multis);

		Entry circus = addEntry("131", 1.035);
		Entry triangle = addEntry("228", 1.026);
		Entry lola = addEntry("410", 0.950);
		Entry curve = addEntry("185", 1.008);
		Entry wildcard = addEntry("18", 1.170);
		Entry temple = addEntry("27042", 1.037);
		Entry triple = addEntry("001", 1.080);
		Entry endurance = addEntry("1044", 0.780);
		Entry sundog = addEntry("14", 1.351);

		race1 = new Race();
		race1.setStartTime(multis, SailTime.toLong("11:50:00.0"));
		race1.setLength(multis, 19.20);
		reg.addRace(race1);

		addFinish(sundog, "15:21:47.0");
		addFinish(circus, "15:29:50.0");
		addFinish(wildcard, "15:29:43.0");
		addFinish(triangle, "15:41:15.0");
		addFinish(curve, "15:47:02.0");
		addFinish(temple, "15:58:21.0");
		addFinish(lola, "15:59:45.0");
		addFinish(triple, "16:00:11.0");
		addFinish(endurance, "17:45:00.0");

		reg.scoreRegatta();

		Finish finCircus = race1.getFinish(circus);
		Finish finWild = race1.getFinish(wildcard);
		RacePoints rpCircus = reg.getScoringManager().getRacePointsList().find(race1, circus, multis);
		RacePoints rpWild = reg.getScoringManager().getRacePointsList().find(race1, wildcard, multis);

		assertNotNull(finCircus);
		assertNotNull(finWild);

		assertNotNull(finCircus.getFinishPosition());
		assertNotNull(finWild.getFinishPosition());

		assertNotNull(rpCircus.getClassFinishPosition());
		assertNotNull(rpWild.getClassFinishPosition());

		assertEquals("2", finCircus.getFinishPosition().toString());
		assertEquals("3", finWild.getFinishPosition().toString());

		assertEquals("3", rpCircus.getClassFinishPosition().toString());
		assertEquals("2", rpWild.getClassFinishPosition().toString());
	}

	private void addFinish(Entry e, String finTime) throws Exception {
		Finish f = new Finish(race1, e, SailTime.toLong(finTime), new FinishPosition(race1.getNextFinishNumber()),
				new Penalty(Penalty.NO_PENALTY));

		race1.setFinish(f);
	}

	private Entry addEntry(String sail, double rating) throws RatingOutOfBoundsException {
		Entry e = new Entry();
		SailId sid = new SailId(sail);

		e.setSailId(sid);
		e.setRating(new RatingMultihull(rating));
		e.setDivision(multis);
		reg.addEntry(e);
		return e;
	}

	public ScoringBCYAFinishTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		reg = new Regatta();
		JavaScoreProperties.setRegatta(reg);

	}

}
/**
 * $Log: ScoringFleetTests.java,v $
 */

