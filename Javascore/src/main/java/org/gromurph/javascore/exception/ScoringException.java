//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringException.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.exception;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;

/**
 * Covering class for scoring related exceptions
**/
public class ScoringException extends Exception
{
    Entry fEntry;
    Race fRace;

    public ScoringException( String msg)
    {
        this( msg, null, null);
    }

    public ScoringException( String msg, Race race, Entry entry)
    {
        super( msg);
        fEntry = entry;
        fRace = race;
    }

    public Entry getEntry() {return fEntry;}
    public Race getRace() {return fRace;}

    @Override public String toString()
    {
        return super.toString() + ", entry=" + fEntry + ", race" + fRace;
    }
}
/**
 * $Log: ScoringException.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */

