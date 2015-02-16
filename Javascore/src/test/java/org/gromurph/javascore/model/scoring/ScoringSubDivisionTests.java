// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringSubDivisionTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;

/**
 * Unit test scripts for Regatta class
 */
public class ScoringSubDivisionTests extends org.gromurph.javascore.JavascoreTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		reg = loadTestRegatta( "0000-Subdivision-Test.regatta");
	}

	Regatta reg;

	public void testSubDivisionScoring1() throws Exception {
		SubDivision m = reg.getSubDivision("Masters");
		assertNotNull(m);
		m.setScoreSeparately(false);
		reg.scoreRegatta();

		checkScores_ScoreSeparateFalse(reg);
	}

	private void checkScores_ScoreSeparateFalse(Regatta reg) {
		Entry bam = reg.getEntry(106);
		Entry beepbeep = reg.getEntry(117);

		Race race1 = reg.getRace("1");
		Race race2 = reg.getRace("2");

		SubDivision masters = reg.getSubDivision("Masters");
		RacePoints pts = reg.getScoringManager().getRacePointsList().find(race1, bam, masters);
		assertNotNull("bam race 1 points not null", pts);
		assertEquals("bam masters race 1 is 36 pts", 36.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, bam, masters);
		assertNotNull("bam race 2 points not null", pts);
		assertEquals("bam masters race 2 is 5 pts", 5.0, pts.getPoints(), ERR_MARGIN);

		SeriesPointsList masterPts = reg.getScoringManager().getAllRegattaRankings(masters);
		assertNotNull("master series points not null", masterPts);

		SeriesPoints spts = masterPts.find(bam, masters);
		assertNotNull("bam seriespoints not null", spts);
		assertEquals("bam seriespoints is 5 pts", 5.0000, spts.getPoints(), ERR_MARGIN);
		assertEquals("bam seriesposition is 5th", 5, spts.getPosition());

		pts = reg.getScoringManager().getRacePointsList().find(race1, beepbeep, masters);
		assertNotNull("beepbeep race 1 points not null", pts);
		assertEquals("beepbeep masters race 1 is 36 pts", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, beepbeep, masters);
		assertNotNull("beepbeep race 2 points not null", pts);
		assertEquals("beepbeep masters race 2 is 5 pts", 2.0, pts.getPoints(), ERR_MARGIN);

		spts = masterPts.find(beepbeep, masters);
		assertNotNull("beepbeep seriespoints not null", spts);
		assertEquals("beepbeep seriespoints is 1.0001 pts", 1.0001, spts.getPoints(), ERR_MARGIN);
		assertEquals("beepbeep seriesposition is 2nd", 2, spts.getPosition());
	}

	public void testSubDivisionScoring2() throws Exception {
		SubDivision m = reg.getSubDivision("Masters");
		m.setScoreSeparately(true);
		reg.scoreRegatta();

		checkScores_ScoreSeparateTrue(reg);
	}

	private void checkScores_ScoreSeparateTrue(Regatta reg) {
		Entry bam = reg.getEntry(106);
		Entry beepbeep = reg.getEntry(117);

		Race race1 = reg.getRace("1");
		Race race2 = reg.getRace("2");

		SubDivision masters = reg.getSubDivision("Masters");

		RacePoints pts = reg.getScoringManager().getRacePointsList().find(race1, bam, masters);
		assertNotNull("bam race 1 points not null", pts);
		assertEquals("bam masters race 1 is 5 pts", 5.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, bam, masters);
		assertNotNull("bam race 2 points not null", pts);
		assertEquals("bam masters race 2 is 5 pts", 5.0, pts.getPoints(), ERR_MARGIN);

		SeriesPointsList masterPts = reg.getScoringManager().getAllRegattaRankings(masters);
		assertNotNull("master series points not null", masterPts);

		SeriesPoints spts = masterPts.find(bam, masters);
		assertNotNull("bam seriespoints not null", spts);
		assertEquals("bam seriespoints is 5 pts", 5.0000, spts.getPoints(), ERR_MARGIN);
		assertEquals("bam seriesposition is 5th", 5, spts.getPosition());

		pts = reg.getScoringManager().getRacePointsList().find(race1, beepbeep, masters);
		assertNotNull("beepbeep race 1 points not null", pts);
		assertEquals("beepbeep masters race 1 is 1 pts", 1.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, beepbeep, masters);
		assertNotNull("beepbeep race 2 points not null", pts);
		assertEquals("beepbeep masters race 2 is 5 pts", 2.0, pts.getPoints(), ERR_MARGIN);

		spts = masterPts.find(beepbeep, masters);
		assertNotNull("beepbeep seriespoints not null", spts);
		assertEquals("beepbeep seriespoints is 1.0001 pts", 1.0001, spts.getPoints(), ERR_MARGIN);
		assertEquals("beepbeep seriesposition is 2nd", 2, spts.getPosition());
	}

	public void testSubDivisionMasters() throws Exception {
		SubDivision m = reg.getSubDivision("Masters");
		m.setScoreSeparately(true);
		reg.scoreRegatta();

		checkScores_ScoreSeparateTrue(reg);

		m.setRaceAddon(1.0);

		reg.scoreRegatta();

		checkScores_MastersScores(reg);
	}

	private void checkScores_MastersScores(Regatta reg) {
		Entry bam = reg.getEntry(106);
		Entry beepbeep = reg.getEntry(117);

		Race race1 = reg.getRace("1");
		Race race2 = reg.getRace("2");

		SubDivision masters = reg.getSubDivision("Masters");

		RacePoints pts = reg.getScoringManager().getRacePointsList().find(race1, bam, masters);
		assertNotNull("bam race 1 points not null", pts);
		assertEquals("bam masters race 1 off", 6.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, bam, masters);
		assertNotNull("bam race 2 points not null", pts);
		assertEquals("bam masters race 2 off", 6.0, pts.getPoints(), ERR_MARGIN);

		SeriesPointsList masterPts = reg.getScoringManager().getAllRegattaRankings(masters);
		assertNotNull("master series points not null", masterPts);

		SeriesPoints spts = masterPts.find(bam, masters);
		assertNotNull("bam seriespoints not null", spts);
		assertEquals("bam seriespoints off", 6.0000, spts.getPoints(), ERR_MARGIN);
		assertEquals("bam seriesposition off", 5, spts.getPosition());

		pts = reg.getScoringManager().getRacePointsList().find(race1, beepbeep, masters);
		assertNotNull("beepbeep race 1 points not null", pts);
		assertEquals("beepbeep masters race 1 off", 2.0, pts.getPoints(), ERR_MARGIN);

		pts = reg.getScoringManager().getRacePointsList().find(race2, beepbeep, masters);
		assertNotNull("beepbeep race 2 points not null", pts);
		assertEquals("beepbeep masters race 2 off", 3.0, pts.getPoints(), ERR_MARGIN);

		spts = masterPts.find(beepbeep, masters);
		assertNotNull("beepbeep seriespoints not null", spts);
		assertEquals("beepbeep seriespoints off", 2.0001, spts.getPoints(), ERR_MARGIN);
		assertEquals("beepbeep seriesposition off", 2, spts.getPosition());
	}

	public ScoringSubDivisionTests(String name) {
		super(name);
	}

}
/**
 * $Log: ScoringSubDivisionTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.6 2005/05/26 01:46:51 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.5 2005/04/23 21:55:31 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.4 2004/04/10 22:19:38 sandyg Copyright update
 * 
 * Revision 1.3 2003/04/27 21:00:55 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.2 2003/01/04 17:09:28 sandyg Prefix/suffix overhaul
 * 
 */
