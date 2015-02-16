//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingOneDesign.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import java.util.ArrayList;
import java.util.List;

import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.WarningList;
import org.gromurph.xml.PersistentNode;

/**
 * Standard one design class
**/
public class RatingOneDesign extends Rating
{
    public static final String SYSTEM = "OneDesign";

    private String fODClassName; 

    /**
     * dynamic list of classes spectified in each run of javascore,
     * its a Vector so it can dynamically work with DefaultComboBoxModel
     */
    private transient static List<String> sKnownODClassNames = new ArrayList<String>(10);

    public static String ODCLASS_PROPERTY = "ClassName";

    public RatingOneDesign()
    {
        this(SYSTEM);
    }

    @Override public String getSystem()
	{
		/**
		 * solves an unknown problem between 3.1.3 and 4.0.1 where rating systems
		 * were saved some as "One Design" and others as "OneDesign"
		 * @return
		 */
		return SYSTEM;
	}

    public RatingOneDesign( String inC)
    {
        super( SYSTEM);
        if (inC.equals(SYSTEM))
        {
            setODClassName( sKnownODClassNames.get(0));
        }
        else
        {
            setODClassName( inC);
        }
    }

    @Override public boolean isOneDesign()
    {
        return true;
    }

    @Override public int compareTo(Object o) throws ClassCastException
    {
    	if (o == null) return -1;
    	
        try
        {
			RatingOneDesign that = (RatingOneDesign) o;
            return this.getODClassName().compareTo( that.getODClassName());
        }
        catch (ClassCastException e)
        {
            return this.getClass().getName().compareTo( o.getClass().getName());
        }
    }

    @Override public long getCorrectedTime( Finish inFinish)
    {
        Division div = inFinish.getEntry().getDivision();
        if ( SailTime.NOTIME == inFinish.getRace().getStartTimeRaw(div))
        {
            // no start time for this race, this class
            return inFinish.getFinishTime();
        }
        else
        {
            return inFinish.getElapsedTime();
        }
    }

    @Override public boolean equals( Object inSys)
    {
        if ( !super.equals( inSys)) return false;
        try
        {
            return fODClassName.equals( ((RatingOneDesign) inSys).fODClassName);
        }
        catch (Exception e) { return false; }
    }

    @Override public String toString()
    {
        return toString( true);
    }

    @Override public String toString( boolean full)
    {
        if (full)
        {
            return "1D( "+fODClassName+")";
        }
        else
        {
            return fODClassName;
        }
    }

    public String getODClassName()
    {
        return fODClassName;
    }

    public void setODClassName( String inC)
    {
        if (!inC.equals( SYSTEM))
        {
            String holdClass = fODClassName;
            fODClassName = inC;
            if (!sKnownODClassNames.contains( inC))
            {
                sKnownODClassNames.add( inC);
            }
            firePropertyChange( ODCLASS_PROPERTY, holdClass, inC);
        }
    }

    public static List<String> getKnownODClassNames()
    {
        return sKnownODClassNames;
    }

    @Override public Rating createMaxRating()
    {
        return new RatingOneDesign(getODClassName());
    }

    @Override public Rating createMinRating()
    {
        return new RatingOneDesign(getODClassName());
    }

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
        super.xmlRead( n, rootObject);
        
        String value = n.getAttribute( ODCLASS_PROPERTY);
        if (value != null) setODClassName( value);        
    }

    @Override public void xmlWrite( PersistentNode e)
    {
		//PersistentNode e = super.xmlWrite( doc, tag);
    	super.xmlWrite( e);
        e.setAttribute( ODCLASS_PROPERTY, getODClassName());
        //return e;
    }

    @Override public void validateRace( Race race, Division div, WarningList warnings)
    {
        // do nothing
    }

}

/**
 * $Log: RatingOneDesign.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.11.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.11.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.11  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.10  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.9  2003/07/10 02:00:30  sandyg
 * Bug 766917, somehow had two variants of system name "OneDesign" and "One Design"
 * now always "OneDesign"
 *
 * Revision 1.8  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.6  2003/01/06 00:32:37  sandyg
 * replaced forceDivision and forceRating statements
 *
 * Revision 1.5  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.4  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
