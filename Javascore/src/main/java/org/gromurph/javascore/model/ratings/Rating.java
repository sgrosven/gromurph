//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Rating.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.BaseObject;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

/**
 * Abstract class for supporting Rating definitions
 * All underlying classes should support the following two methods:
 *      boolean isOneDesign(): determines whether or not corrected times
 *          are involved based on finish time.
 *      SailTime calcCorrected( Finish inFinish):
 *          calculates corrected time from Finish object.
 *          @see calcCorrected
 *
 * Abstract class supports a single double value for the Rating, mostly to
 * simplify early development since all initially supported systems (morc, phrf, multihull)
 * have a double, except one-design who doesnt care
**/

public abstract class Rating extends BaseObject implements Comparable
{
    public static final String SYSTEM_PROPERTY = "System";

    private String fSystem;

    public abstract boolean isSlower(Rating that);
    public abstract boolean isFaster(Rating that);
    
    public abstract String toString( boolean full);

    /**
     * returns true if system is onedesign (no corrected times)
     * false otherwise
    **/
    public abstract boolean isOneDesign();

    /**
     * Creates a new instance of the slowest overall rating allowed by the rating system
     * @return
     */
    public abstract Rating createSlowestRating();

    /**
     * Creates a new instance of the fastest overall rating allowed by the rating system
     * @return
     */
    public abstract Rating createFastestRating();

    /**
     * evaluates a Race to see if it contains all the information necessary
     * to accurately score the a boat with this rating
     *
     * @param race the race to be evaluated
     * @param warnings a WarningList in which to list any problems
     */
    public abstract void validateRace( Race race, Division div, WarningList warnings);

    /**
     * calculates corrected time
     * @param inFinish, Finish object on which time is to be corrected
     * @return corrected time, currently supports only PHRF and One-Designs
     */
    public abstract long getCorrectedTime( Finish inFinish);
    
    public Rating()
    {
        this("");
    }

    public Rating( String name)
    {
        super();
        fSystem = name;
    }

    public long getTimeAllowance( Finish inFinish)
    {
    	if (inFinish != null) return inFinish.getElapsedTime() - getCorrectedTime( inFinish);
    	else return SailTime.NOTIME;
    }
    
    public String getSystem()
    {
        return fSystem;
    }

    /**
     * supported here to avoid lots of type casting later
     * @param v a single double value
     */
    public void setPrimaryValue( double v) {}

    /**
     * override if want something real
     * @return a single double value of the rating
     */
    public double getPrimaryValue() { return Double.NaN;}

    public int compareTo(Object o) throws ClassCastException
    {
    	int classCompare = this.getClass().getName().compareTo( o.getClass().getName());
    	if (classCompare != 0) return classCompare;

        Rating that = (Rating) o;
        if ( this.isSlower(that)) return -1;
        else if ( this.isFaster(that)) return 1;
        else return 0;
    }

    @Override public boolean equals( Object obj)
    {
        if (this == obj) return true;
        try
        {
            Rating that = (Rating) obj;
            if (!org.gromurph.util.Util.equalsWithNull( this.fSystem, that.fSystem)) return false;
            return true;
        }
        catch (ClassCastException e)
        {
            return false;
        }
    }

    @Override public int hashCode()
    {
        int hash = fSystem.hashCode();
        return hash;
    }

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
        String value = n.getAttribute( SYSTEM_PROPERTY);
        if ( value != null) fSystem = value;        
    }

    @Override public void xmlWrite( PersistentNode e)
    {
        e.setAttribute( SYSTEM_PROPERTY, getSystem());
        //return e;
    }
}
/**
 * $Log: Rating.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.10.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.10.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.10  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.9  2004/04/11 20:41:54  sandyg
 * Bug 773217  PHRF ratings now show time allowance with out finishes
 *
 * Revision 1.8  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.6  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.5  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
