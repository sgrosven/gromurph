//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RacePoints.java,v 1.8 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Contains points information on an entry in a race This is separated from the
 * Finish object because when fleet scoring gets implemented an entry could have
 * more than one score for a single finish
 **/
public class RacePoints extends Points {
	Race fRace;
	boolean fThrowout;
	
	// not saved
	transient Finish fFinish;
	
	// used in scoring only;
	transient public boolean tossable = true;

	transient private FinishPosition fClassPosition; 
	// actual finish position within a class - this is transient and not saved to disk
	// int fPosition; // unadjusted finish position within the division

	public RacePoints() {
		this( null, null, null, Double.NaN, false);
	}

	public RacePoints( Race race, Entry entry, AbstractDivision div) {
		this( race, entry, div, Double.NaN, false);
	}

	public RacePoints( Finish f) {
		this( f.getRace(), f.getEntry(), f.getEntry().getDivision(), Double.NaN, false);
		fFinish = f;
	}

	public RacePoints( Race race, Entry entry, AbstractDivision div, double points, boolean throwout) {
		super( entry, div, points, 0);
		if ( entry != null && entry.getBoat() != null && entry.getBoat().getSailId() != null) {
			aId = entry.getBoat().getSailId().toString();
		}
		fRace = race;
		fThrowout = throwout;
		if (fRace != null) fFinish = fRace.getFinish(entry);
		fClassPosition = null;
	}
	
	@Override public String toString() {
		StringBuffer sb = new StringBuffer( 32);
		sb.append( super.toString());
		sb.append( "/");
		sb.append( fRace.getName());
		sb.append( "/");
		sb.append( fThrowout);
		return sb.toString();
	}

	@Override public boolean equals( Object obj) {
		if ( this == obj) return true;
		if ( !(obj instanceof RacePoints)) return false;

		if ( !super.equals( obj)) return false;

		RacePoints that = (RacePoints) obj;
		if ( this.fThrowout != that.fThrowout) return false;
		if ( !Util.equalsWithNull( this.fRace, that.fRace)) return false;
		return true;
	}

	public static final String THROWOUT_PROPERTY = "Toss";
	public static final String RACE_PROPERTY = "Race";

	@Override public void xmlRead( PersistentNode n, Object rootObject) {
		super.xmlRead( n, rootObject);
		Regatta reg = (Regatta) rootObject;
		if ( reg == null) reg = JavaScoreProperties.getRegatta();
		Race lastRace = null;

		String value = "";
		if ( (value = n.getAttribute( RACE_PROPERTY)) != null) {
			int id = Integer.parseInt( value);
			if ( lastRace == null || id != lastRace.getId()) {
				lastRace = reg.getRaceId( id);
			}
			fRace = lastRace;
		}

		if ( (value = n.getAttribute( THROWOUT_PROPERTY)) != null) {
			boolean b = value.toString().equalsIgnoreCase( "true");
			try {
				setThrowout( b);
			} catch (Exception e) {
			}
		}
		
	}

	@Override public void xmlWrite( PersistentNode e) {
		if ( getEntry() == null) return;
		super.xmlWrite( e);

		if ( getRace() != null) e.setAttribute( RACE_PROPERTY, Integer.toString( getRace().getId()));
		if ( isThrowout()) e.setAttribute( THROWOUT_PROPERTY, new Boolean( isThrowout()).toString());
		// return e;
	}

	public static String format( RacePoints rp) {
		return format( rp, true);
	}

	public static String format( RacePoints rp, boolean showPts) {

		Finish finish = rp.getFinish();
		Penalty penalty = finish.getPenalty();
		StringBuffer base = new StringBuffer();

		boolean didPts = false;
		if ( showPts || !finish.hasPenalty() || (penalty.isOtherPenalty())) {
			base.append( sNumFormat.format( rp.getPoints()));
			didPts = true;
		}

		if ( penalty.isDsqPenalty()) {
			Penalty ptemp = (Penalty) finish.getPenalty().clone();
			ptemp.clearPenalty( Constants.NOFINISH_MASK);
			if ( didPts) base.append( "/");
			base.append( ptemp.toString( false));
		} else if ( penalty.hasPenalty( Constants.AVG)) {
			if ( didPts) base.append( "/");
			base.append( "RDG");
		} else if ( finish.hasPenalty()) {
			if ( didPts) base.append( "/");
			base.append( penalty.toString( false));
		}

		if ( rp.isThrowout()) {
			base.insert( 0, '[');
			base.append( ']');
		}
		return base.toString();
	}

	public FinishPosition getClassFinishPosition() {
		return fClassPosition;
	}

	public void setClassFinishPosition(FinishPosition inVal) {
		fClassPosition = inVal;
	}

	public void setThrowout( boolean throwout) {
		Boolean old = new Boolean( fThrowout);
		fThrowout = throwout;
		firePropertyChange( THROWOUT_PROPERTY, old, new Boolean( fThrowout));
	}

	public boolean isThrowout() {
		return fThrowout;
	}

	public boolean isTiedPoints( RacePoints lastrp) {
		if ( lastrp == null) return false;
		return (Math.abs( lastrp.getPoints() - this.getPoints()) < 0.0000001);
	}

	public boolean isTiedFinish( RacePoints lastrp) {
		if ( lastrp == null) return false;

		Finish thisF = getFinish();
		Finish lastF = lastrp.getFinish();

		if ( thisF.getCorrectedTime() == SailTime.NOTIME) {
			if ( lastF.getCorrectedTime() == SailTime.NOTIME) {
				// both are "no time" match on penalty
				return thisF.getParent() == lastF.getPenalty();
			} else {
				return false;
			}
		} else if ( lastF.getCorrectedTime() == SailTime.NOTIME) {
			return false;
		} else {
			long lastTime = lastF.getCorrectedTime();
			long thisTime = thisF.getCorrectedTime();
			return (lastTime == thisTime);
		}
	}

	public Race getRace() {
		return fRace;
	}

	public Finish getFinish() {
		if ( fFinish == null) {
			if ( fRace == null) return null;
			if ( getEntry() == null) return null;
			fFinish = fRace.getFinish( getEntry());
		}
		return fFinish;
	}

	private static final long serialVersionUID = 1L;

}
/**
 * $Log: RacePoints.java,v $ Revision 1.8 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.7 2006/04/15 23:43:58 sandyg final Miami OCR gold/silver/medal
 * fixes
 * 
 * Revision 1.6 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.5 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i),
 * getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:26:09 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.14.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.14.2.2 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121,
 * 1226607, killed Java Web Start startup code
 * 
 * Revision 1.14.2.1 2005/06/26 22:47:18 sandyg Xml overhaul to remove xerces
 * dependence
 * 
 * Revision 1.14 2004/04/10 22:20:47 sandyg Fixed bug 894886, handicap scoring
 * (actually was bug in comparing for tied boats
 * 
 * Revision 1.13 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.12 2003/11/27 02:45:08 sandyg Fixed 1d tied boats (with same time)
 * also minor reporting oddness on positions with tied boats. Bug 836458
 * 
 * Revision 1.11 2003/05/18 03:12:41 sandyg fixed bug in Alphabet soup single
 * race position numbers and "ties"
 * 
 * Revision 1.10 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.9 2003/04/20 15:43:59 sandyg added javascore.Constants to
 * consolidate penalty defs, and added new penaltys TIM (time value penalty) and
 * TMP (time percentage penalty)
 * 
 * Revision 1.8 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead
 * and xmlWrite
 * 
 * Revision 1.7 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.6 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
