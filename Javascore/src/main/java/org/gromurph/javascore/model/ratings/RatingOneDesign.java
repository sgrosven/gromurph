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
import org.gromurph.javascore.model.Entry;
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

    public boolean isSameOneDesign(Rating that) {
    	if (that == null) return false;
    	if (!that.isOneDesign()) return false;
    	return !(this.getODClassName().equals( ((RatingOneDesign) that).getODClassName()));
    }
    @Override public boolean isSlower(Rating that) {
    	return isSameOneDesign( that);
    }
    @Override public boolean isFaster(Rating that) {
    	return isSameOneDesign( that);
    }

    @Override public long getTimeAllowance(Entry e, Race r) {
		return 0;
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

    @Override public Rating createFastestRating()
    {
        return new RatingOneDesign(getODClassName());
    }

    @Override public Rating createSlowestRating()
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

	@Override
	public long getTimeAllowanceForDistance(double distance) {
		return 0;
	}

    
}
