//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionExport.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.util.Exporter;
import org.gromurph.util.Util;

public class ExportTabSeparatedSingleStage implements Exporter  {

	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	private Regatta regatta;
	PrintWriter pw;

	public ExportTabSeparatedSingleStage() {
	}

	public void setFilename( String f) throws IOException {
		pw = new PrintWriter( new FileWriter(f));
	}

	private static String TAB = "\t";

	private static String[] sExportHeaders = new String[] {
		Entry.DIVISION_PROPERTY,
		Entry.DIVISION_PROPERTY,
		Boat.SAILID_PROPERTY,
		Boat.NAME_PROPERTY,
		Entry.RATING_PROPERTY,
		"Skipper Last",
		"Skipper First",
		"Skipper Isaf Id",
		Entry.CLUB_PROPERTY,
		Entry.MNANUMBER_PROPERTY,
		Entry.RSANUMBER_PROPERTY,
		"Series Points",
		"Series Position",
		"Tie"
	};

	private int maxNumCrew;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gromurph.javascore.actions.Exporter#export()
	 */
	public void export( Regatta reg) throws IOException {
		regatta = reg;
		
		exportHeaders();

		// run through the entries displaying their info
		for ( Division div : regatta.getDivisions()) {
			EntryList el = div.getEntries();
			for ( Iterator iter = el.iterator(); iter.hasNext();) {
				exportEntry( pw, (Entry) iter.next(), div);
			}
		}
		for ( Fleet div : regatta.getFleets()) {
			EntryList el = div.getEntries();
			for ( Iterator iter = el.iterator(); iter.hasNext();) {
				exportEntry( pw, (Entry) iter.next(), div);
			}
		}
		for ( SubDivision div : regatta.getSubDivisions()) {
			EntryList el = div.getEntries();
			for ( Iterator iter = el.iterator(); iter.hasNext();) {
				exportEntry( pw, (Entry) iter.next(), div);
			}
		}

		pw.flush();
		pw.close();
	}

	private void exportHeaders() {
		pw.print( sExportHeaders[0]);
		pw.print( TAB);

		// print out the row headers first
		if ( regatta.isUseBowNumbers()) {
			pw.print( "Bow");
			pw.print( TAB);
		}

		for ( int i = 1; i < sExportHeaders.length; i++) {
			pw.print( sExportHeaders[i]);
			pw.print( TAB);
		}

		// print out race headers
		for ( Iterator iter = regatta.races(); iter.hasNext();) {
			Race race = (Race) iter.next();

			String rName = race.getName();
			pw.print( rName);
			pw.print( " Order\t");
			pw.print( rName);
			pw.print( " FinTime\t");
			pw.print( rName);
			pw.print( " CorTime\t");
			pw.print( rName);
			pw.print( " Penalty\t");
			pw.print( rName);
			pw.print( " Points\t");
			pw.print( rName);
			pw.print( " Throwout\t");

			if ( race.haveRoundings()) {
				for ( Iterator mIter = race.getAllRoundings().keySet().iterator(); mIter.hasNext();) {
					pw.print( rName);
					pw.print( " ");
					pw.print( (String) mIter.next());
					pw.print( TAB);
				}
			}
		}

		calcMaxNumCrew();

		for ( int i = 1; i <= maxNumCrew; i++) {
			pw.print( "CrewLast" + i);
			pw.print( TAB);
			pw.print( "CrewFirst" + i);
			pw.print( TAB);
			pw.print( "CrewIsafId" + i);
			pw.print( TAB);
		}

		// line break at end of headers
		pw.println();
		
	}

	private void calcMaxNumCrew() {
		maxNumCrew = 0;
		for ( Iterator eiter = regatta.entries(); eiter.hasNext();) {
			maxNumCrew = Math.max( maxNumCrew, ((Entry) eiter.next()).getNumCrew());
		}
	}

	private void exportEntry( PrintWriter pw, Entry e, AbstractDivision div) {
		pw.print( div);
		pw.print( TAB);

		if ( regatta.isUseBowNumbers()) {
			pw.print( e.getBow());
			pw.print( TAB);
		}
		pw.print( e.getDivision());
		pw.print( TAB);
		pw.print( e.getBoat().getSailId());
		pw.print( TAB);
		pw.print( e.getBoat().getName());
		pw.print( TAB);
		pw.print( e.getRating().toString());
		pw.print( TAB);
		pw.print( e.getSkipper().getLast());
		pw.print( TAB);
		pw.print( e.getSkipper().getFirst());
		pw.print( TAB);
		pw.print( e.getSkipper().getSailorId());
		pw.print( TAB);
		pw.print( e.getClub());
		pw.print( TAB);
		pw.print( e.getMnaNumber());
		pw.print( TAB);
		pw.print( e.getRsaNumber());
		pw.print( TAB);

		SeriesPoints sp = regatta.getScoringManager().getRegattaRanking( e, div);
		if ( sp != null) {
			// pull off the series points
			pw.print( sp.getPoints());
			pw.print( TAB);
			pw.print( sp.getPosition());
			pw.print( TAB);
			pw.print( sp.isTied());
			pw.print( TAB);
		} else {
			pw.print( TAB);
			pw.print( TAB);
			pw.print( TAB);
		}

		// run through each race
		for ( Iterator rIter = regatta.races(); rIter.hasNext();) {
			Race r = (Race) rIter.next();
			RacePoints p = regatta.getScoringManager().getRacePointsList().find( r, e, null);
			if ( p != null) {
				Finish f = p.getFinish();
				pw.print( f.getFinishPosition());
				pw.print( TAB);

				pw.print( "\"");
				pw.print( SailTime.toString( f.getFinishTime()));
				pw.print( "\"");
				pw.print( TAB);

				pw.print( "\"");
				pw.print( SailTime.toString( f.getCorrectedTime()));
				pw.print( "\"");
				pw.print( TAB);

				pw.print( f.getPenalty());
				pw.print( TAB);
				pw.print( p.getPoints());
				pw.print( TAB);
				pw.print( p.isThrowout());
				pw.print( TAB);
			}

			if ( r.haveRoundings()) {
				Iterator mIter = r.getAllRoundings().keySet().iterator();
				while ( mIter.hasNext()) {
					FinishList marks = r.getRoundings( (String) mIter.next());
					Finish f = marks.findEntry( e);
					if ( f != null) pw.print( f.getFinishPosition());
					pw.print( TAB);
				}
			}
		}

		for ( int i = 0; i < e.getNumCrew(); i++) {
			pw.print( e.getCrew( i).getLast());
			pw.print( TAB);
			pw.print( e.getCrew( i).getFirst());
			pw.print( TAB);
			pw.print( e.getCrew( i).getSailorId());
			pw.print( TAB);
		}

		pw.println();
	}


}
/**
 * $Log: ActionExport.java,v $
 *
 */
