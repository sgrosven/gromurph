//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Division.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.beans.PropertyChangeEvent;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Implements AbstractDivision for starting classes.
**/
public class Division extends AbstractDivision
{
    /**
     * temporary master division for regattas with all one class
     */
    public static final String MINRATING_PROPERTY = "MinRating";
    public static final String MAXRATING_PROPERTY = "MaxRating";

    private static final long serialVersionUID = 1L;

    private Rating fMaxRating;
    private Rating fMinRating;

    /**
     * checks to see if division is contained in this division. If 'div' is a 
     * Fleet, always returns false, if the 'div' is another Division, returns
     * true ONLY if div is '==', if div is a subdivision, returns true if
     * the subdiv's parent div is '=='
     * @param div
     * @return true if div is in one of the divisions contained in this Fleet
     */
    @Override public boolean contains( AbstractDivision div)
    {
    	if (div == null) return false;
        if (div instanceof Fleet) return false;
        if (div instanceof Division) return this.equals( div);
        if (div instanceof SubDivision) return this.equals( ((SubDivision)div).getParentDivision());
        return false;
    }

    /**
     * basic constructor
     * creates new division with no name, onedesign, no name, undefined min/max ratings
     * @see Rating#getSupportedSystems
    **/
    public Division()
    {
        this( "<none>");
    }

    /**
     * basic constructor
     * creates new one design division
     * @param name name of the division
     * @see Rating#getSupportedSystems
    **/
    public Division( String name)
    {
        this( name, new RatingOneDesign(name), new RatingOneDesign(name));
    }

    /**
     * basic constructor
     * creates new division
     * @param name division's name
     * @param sys rating system for the division, by default the range of ratings
     * is the systems minimum and maximum allowed values
     * @see Rating#getSupportedSystems
    **/
    public Division( String name, String sys)
    {
        this( name, RatingManager.createRating( sys), RatingManager.createRating( sys));
    }

    /**
     * Full Constructor with full arguments, all other constructors should call this one
     * @param inName    String of division name
     * @param minrtg    the minimum rating for the class, this rating's system
     *                      drives the rating system
     * @param maxrtg    the maximum rating contained in this class
     * <P>NOTE: there are no checks to make sure that the same rating system is
     * used for minRtg and maxRtg
    **/
    public Division( String inName, Rating minrtg, Rating maxrtg)
    {
        super(inName);
        setMinRating( minrtg);
        setMaxRating( maxrtg);
    }

    @Override public void xmlWrite( PersistentNode e)
    {
    	super.xmlWrite( e);
        if (fMinRating != null) fMinRating.xmlWrite( e.createChildElement( MINRATING_PROPERTY));
        if (fMaxRating != null) fMaxRating.xmlWrite( e.createChildElement( MAXRATING_PROPERTY));
        //return e;
    }


    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
        super.xmlRead( n, rootObject);

        Rating minR = null;
        Rating maxR = null;
        
        PersistentNode n2 = n.getElement( MINRATING_PROPERTY);
        if (n2 != null)
        {
            minR = RatingManager.createRatingFromXml( n2, rootObject);
        }

        n2 = n.getElement( MAXRATING_PROPERTY);
        if (n2 != null)
        {
            maxR = RatingManager.createRatingFromXml( n2, rootObject);
        }

        if (minR == null)
        {
            minR = new RatingOneDesign( getName());
        }

        if (maxR == null)
        {
            maxR = (Rating) minR.clone();
        }
        setMinRating( minR);
        setMaxRating( maxR);
    }

	/**
	 * to be equal, the name, minrating and maxrating must all be equal
	 */
    @Override public boolean equals( Object obj)
    {
        if ( this == obj) return true;
        try
        {
            if ( !super.equals( obj)) return false;

            Division that = (Division) obj;
            if ( !Util.equalsWithNull( this.fMinRating, that.fMinRating)) return false;
            if ( this.isOneDesign()) return true;

            if ( !Util.equalsWithNull( this.fMaxRating, that.fMaxRating)) return false;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

	/**
	 * Should return the number of registered boats for this division 
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
    @Override public int getNumEntries()
	{
		Regatta reg = JavaScoreProperties.getRegatta();
		if (reg == null) return 0;
		
		return getEntries().size();
	}	

	/**
	 * Should return the number of boats registered for the specified division and race in the 
	 * specified regatta.  Note that if the division is not racing in the specified race, this
	 * returns 0.
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
    @Override public int getNumEntries( Race race)
	{
		if (isRacing(race)) return getNumEntries();
		else return 0;
	}
	

    public String getSystem()
    {
        if (fMinRating == null) return RatingOneDesign.SYSTEM;
        else return fMinRating.getSystem();
    }

    public void setSystem( String sysName)
    {
        if (!sysName.equals( getSystem()))
        {
            Rating r = RatingManager.createRating( sysName);
            setMinRating( r.createMinRating());
            setMaxRating( r.createMaxRating());
         }
    }

    @Override public String toString()
    {
        if ( getName().trim().length() == 0) return NO_NAME;
        else return getName();
    }

    /**
     * returns a "name( minrating to maxrating)"
     * @returns String
    **/
    @Override public String getLongName()
    {
        StringBuffer sb = new StringBuffer( );
        if (!isOneDesign())
        {
            sb.append( getName());
            sb.append( "(");
            if (fMinRating != null)
            {
                sb.append( fMinRating.getPrimaryValue());
            }
            else
            {
                sb.append( "<none>");
            }
            sb.append( " thru ");

            if (fMaxRating != null)
            {
                sb.append( fMaxRating.getPrimaryValue());
                sb.append( ")");
            }
            else
            {
                sb.append( "<none>");
            }
        }
        else if (fMinRating != null)
        {
        	sb.append( fMinRating.toString(false));
        }
        else
        {
            sb.append( getName());
       }
        return sb.toString();
    }


//	public Object clone()
//	{
//		Division newDiv = (Division) super.clone();
//		if (fMaxRating != null) newDiv.fMaxRating = (Rating) this.fMaxRating.clone();
//		if (fMinRating != null) newDiv.fMinRating = (Rating) this.fMinRating.clone();
//
//		return newDiv;
//	}

    public boolean isMaxRatingValid( Rating inRat)
    {
        if (fMinRating == null)
        {
            //	"Unable to set maximum rating while minimum rating is null.",
            return false;
        }
        else if (fMinRating.compareTo( inRat) < 0)
        {
            //	"Maximum rating may not be less than minimum rating.",
            return false;
        }
        return true;
    }

    public boolean isMinRatingValid( Rating inRat)
    {
        if (fMaxRating.compareTo( inRat) > 0)
        {
            //	"Minimum rating may not be greater than minimum rating.",
            return false;
        }
        return true;
    }


    @Override public boolean isOneDesign()
    {
        if (fMinRating == null) return true;
        else return fMinRating.isOneDesign();
    }

    
    @Override
	public EntryList getEntries() {
    	Regatta reg = JavaScoreProperties.getRegatta();
    	if (reg == null) return new EntryList();
    	else return reg.getAllEntries().findAll(this);
	}

	/**
     * returns true if specified entry is contained in this class
     * @param entry
     * @returns true if entry's rating is contained in this division
     */
    @Override public boolean contains(Entry entry)
    {
        return equals( entry.getDivision());
    }

    /**
     * returns true if specified rating is contained in this class
     * @param inRat
     * @returns true if rating is contained within this division
     */
    @Override public boolean contains(Rating inRat)
    {
        if (inRat == null) return false;

        boolean b = false;
        if (isOneDesign())
        {

            b = (inRat.compareTo( fMinRating) == 0);
        }
        else if (fMaxRating.compareTo( fMinRating) >= 0)
        {
            b = ( (inRat.compareTo( fMinRating) >= 0) &&
                 (inRat.compareTo( fMaxRating) <= 0) );
        }
        else // (fMaxRating.compareTo( fMinRating) < 0)
        {
            b = ( (inRat.compareTo( fMinRating) <= 0) &&
                 (inRat.compareTo( fMaxRating) >= 0) );
        }
        return b;
    }

	/**
	 * given a list of entries, returns a list of all entries who's rating
	 * is contained in this division
	 * 
	 * @param allEntries the initial list of entries
	 * @return list of entries that are valid for the division
	 */
	public EntryList getValidEntries( EntryList allEntries)
	{
		EntryList elist = new EntryList();
		for (Entry e : allEntries)
		{
			Rating rtg = e.getBoat().getRating( getSystem());
			if (rtg != null && contains( rtg)) elist.add( e);
		}
		return elist;
	}

	/**
	 * given a list of entries, returns a list of all entries who's rating
	 * is NOT contained in this division
	 * 
	 * @param allEntries the initial list of entries
	 * @return list of entries that are invalid for the division
	 */
	public EntryList getInvalidEntries( EntryList allEntries)
	{
		EntryList elist = new EntryList();
		for (Entry e : allEntries)
		{
			if ( !contains(e.getRating())) elist.add( e);
		}
		return elist;
	}
	
    /**
     * returns the max rating as a Rating
     * @returns maximum allowed rating for the division
    **/
    public Rating getMaxRating()
    {
        return fMaxRating;
    }

    /**
     * sets the maximum rating for the class, as a double
     * @param inRat   Rating object for division maximum
     *
     * is ignored if minRating is a onedesign
    **/
    public void setMaxRating(Rating inRat)
    {
    	if (fMaxRating != null) fMaxRating.removePropertyChangeListener( this);
        Rating oldRat = fMaxRating;
        fMaxRating = inRat;
    	if (fMaxRating != null) fMaxRating.addPropertyChangeListener( this);
        firePropertyChange( MAXRATING_PROPERTY, oldRat, inRat);
    }

    /**
     * returns the min rating as a Rating
     * @returns minimum allowed rating for the division
    **/
    public Rating getMinRating()
    {
        return fMinRating;
    }

    /**
     * sets the minimum rating for the class, as a double
     * @param inRat minimum rating for class
     *
     * if system is onedesign, it will ignore maxrating
     * otherwise will ensure minrating <= maxrating
    **/
    public void setMinRating(Rating inRat)
    {
    	if (fMinRating != null) fMinRating.removePropertyChangeListener( this);
        Rating oldRat = fMinRating;
        fMinRating = inRat;
    	if (fMinRating != null) fMinRating.addPropertyChangeListener( this);
        firePropertyChange( MINRATING_PROPERTY, oldRat, inRat);
    }

    @Override public void propertyChange( PropertyChangeEvent event)
	{
		if ( event.getSource() == fMinRating)
		{
        	firePropertyChange( MINRATING_PROPERTY, null, fMinRating);
		}
		else if (event.getSource() == fMaxRating)
	    {
        	firePropertyChange( MAXRATING_PROPERTY, null, fMaxRating);
	    }
	}
}
/**
 * $Log: Division.java,v $
 * Revision 1.6  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.5  2006/01/15 21:10:37  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/14 21:06:55  sandyg
 * final bug fixes for 5.01.1.  All tests work
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:01  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.12.4.2  2005/11/30 02:51:25  sandyg
 * added auto focuslost to JTextFieldSelectAll.  Removed focus lost checks on text fields in panels.
 *
 * Revision 1.12.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.12.2.1  2005/06/26 22:47:17  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.12  2005/02/27 23:23:54  sandyg
 * Added IRC, changed corrected time scores to no longer round to a second
 *
 * Revision 1.11  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.10  2003/04/27 21:36:11  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.9  2003/03/28 03:07:43  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.8  2003/03/27 02:47:01  sandyg
 * Completes fixing [ 584501 ] Can't change division splits in open reg
 *
 * Revision 1.7  2003/03/16 20:39:14  sandyg
 * 3.9.2 release: encapsulated changes to division list in Regatta,
 * fixed a bad bug in PanelDivsion/Rating
 *
 * Revision 1.6  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */
