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
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.Exporter;
import org.gromurph.util.Util;

public class ExportTabSeparatedMultiStage implements Exporter  {

	/**
	 * INCOMPLETE AND DOES NOT WORK!!!!!
	 * CHECKED IN JUST TO GET RELEASE 7.2.2 - BUG FIXES OUT THE DOOR
	 * 
	 */
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	private Regatta regatta;
	private MultiStageScoring stageManager;

	PrintWriter pw;

	public ExportTabSeparatedMultiStage() {
	}

	public void setFilename( String f) throws IOException {
		pw = new PrintWriter( new FileWriter(f));
	}

	private static String[] sExportHeaders = new String[] {
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

	private static String TAB = "\t";

	private void pushCell( String c) {
		pw.print( c);
		pw.print(TAB);
	}
	private void pushCell( double d) {
		pw.print( Double.toString(d));
		pw.print(TAB);
	}
	private void pushCell( Object o) {
		pw.print( o.toString());
		pw.print(TAB);
	}
	private void pushBlankCells( int i) {
		for ( int x = 0; x < i; x++) {
			pushCell("");
		}
	}
	private void pushEndOfRow() {
		pw.println();
	}
	
	/*
	 * @see org.gromurph.javascore.actions.Exporter#export()
	 */
	public void export( Regatta reg) throws IOException {
		regatta = reg;
		stageManager = (MultiStageScoring) regatta.getScoringManager(); 
		
		exportHeaders();

		exportEntries();

		pw.flush();
		pw.close();
	}

	private void exportHeaders() {
		pushCell( sExportHeaders[0]);

		// print out the row headers first
		if ( regatta.isUseBowNumbers()) {
			pushCell( "Bow");
		}

		for ( int i = 1; i < sExportHeaders.length; i++) {
			pushCell( sExportHeaders[i]);
		}

		int maxNumCrew = calcMaxNumCrew();

		for ( int i = 1; i <= maxNumCrew; i++) {
			pushCell( "CrewLast" + i);
			pushCell( "CrewFirst" + i);
			pushCell( "CrewIsafId" + i);
		}

		// print out race headers
		for ( Race race : regatta.getRaces()) {
			String rName = race.getName();
				
			pushCell( rName + " Stage");
			pushCell( rName + " SubDiv");
			pushCell( rName + " Order");
			pushCell( rName + " FinTime");
			pushCell( rName + " CorTime");
			pushCell( rName + " Penalty");
			pushCell( rName + " Points");
			pushCell( rName = " Throwout");

			if ( race.haveRoundings()) {
				for ( Iterator mIter = race.getAllRoundings().keySet().iterator(); mIter.hasNext();) {
					pushCell( rName + " " + ((String) mIter.next()));
				}
			}
		}

		// line break at end of headers
		pushEndOfRow();
		
	}

	private int calcMaxNumCrew() {
		int maxNumCrew = 0;
		for ( Entry entry : regatta.getAllEntries()) {
			maxNumCrew = Math.max( maxNumCrew, entry.getNumCrew());
		}
		return maxNumCrew;
	}
	
	private void exportEntries() {
		// look for existence of final series subdivisions and races

		// if found, then print only the final subdivisions standings
		// if not, then report the main divisions only

		// regardless print the "scoring" divisions 

		boolean haveFinalsRaces = false;

		// first report the starting divisions
		for (Stage st : stageManager.getStages()) {
			// need 1 stage that is not a qualifying stage
			if (!st.isCombinedQualifying()) {
				haveFinalsRaces = true;
				break;
			}
		}

		// report the Divisions
		for (Division div : regatta.getDivisions()) {
			for (Entry e : div.getEntries()) {
				exportOneEntry( e, div);
			}
		}

		// report the Scoring Subdivisions
		for (SubDivision div : regatta.getSubDivisions()) {
			if (div.isGroupScoring()) {
				for (Entry e : div.getEntries()) {
					exportOneEntry( e, div);
				}
			}	
		}
	}

	private void exportOneEntry( Entry e, AbstractDivision div) {
		pushCell( div);

		if ( regatta.isUseBowNumbers()) {
			pushCell( e.getBow());
		}
		
		// see export headers for headers
		pushCell( e.getDivision());
		pushCell( e.getBoat().getSailId());
		pushCell( e.getBoat().getName());
		pushCell( e.getRating().toString());
		pushCell( e.getSkipper().getLast());
		pushCell( e.getSkipper().getFirst());
		pushCell( e.getSkipper().getSailorId());
		pushCell( e.getClub());
		pushCell( e.getMnaNumber());
		pushCell( e.getRsaNumber());
		
		SeriesPoints sp = regatta.getScoringManager().getRegattaRanking( e, div);
		if ( sp != null) {
			// pull off the series points
			pushCell( sp.getPoints());
			pushCell( sp.getPosition());
			pushCell( sp.isTied());
		} else {
			pushBlankCells(3);
		}

		for ( int i = 0; i < e.getNumCrew(); i++) {
			pushCell( e.getCrew( i).getLast());
			pushCell( e.getCrew( i).getFirst());
			pushCell( e.getCrew( i).getSailorId());
		}

		// run through each race
		for ( Race r : regatta.getRaces()) {

			RacePoints p = regatta.getScoringManager().getRacePointsList().find( r, e, div);
			
			if ( p != null) {
				Finish f = p.getFinish();
				pushCell( ""); //stage name not yet supported
				pushCell( div.getName());
				if (f == null) {
					pushCell("NoFin");
					pushBlankCells(2);
				} else {
    				pushCell( f.getFinishPosition());
    				pushCell( "\"" + SailTime.toString( f.getFinishTime()) + "\"");
    				pushCell( "\"" + SailTime.toString( f.getCorrectedTime()) + "\"");
				}

				pushCell( f.getPenalty());
				pushCell( p.getPoints());
				pushCell( p.isThrowout());
			} else {
				pushBlankCells(8);
			}
			
			if ( r.haveRoundings()) {
				Iterator mIter = r.getAllRoundings().keySet().iterator();
				while ( mIter.hasNext()) {
					FinishList marks = r.getRoundings( (String) mIter.next());
					Finish f = marks.findEntry( e);
					if ( f != null) pushCell( f.getFinishPosition());
				}
			}
		}

		pushEndOfRow();
	}


}
/**
 * $Log: ActionExport.java,v $
 *
 */
