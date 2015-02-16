//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Boat.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
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
import java.beans.PropertyVetoException;

import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingList;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Person;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class Boat extends BaseObject
{
    private static final long serialVersionUID = 1L;

    private String fName;
    private SailId fSailId;
    private Person fOwner;
    private RatingList fRatingList;

    public transient static String RATINGLIST_PROPERTY = "RatingList";
    public transient static String RATING_PROPERTY = "Rating";
    public transient static String ADDRATING_PROPERTY = "addRating";
    public transient static String REMOVERATING_PROPERTY = "removeRating";
    //public transient static String CHANGERATING_PROPERTY = "changeRating";
    public transient static String OWNER_PROPERTY = "Owner";
    public transient static String SAILID_PROPERTY = "SailId";
    public transient static String NAME_PROPERTY = "BoatName";
    public transient static String XML_NODENAME = "Boat";

    public Boat( String inName, SailId inSail, Person inOwner)
    {
        super();
        fName = inName;
        fSailId = inSail;
        setOwner( inOwner);
        fRatingList = new RatingList( true);
    }

    public Boat( String inName, SailId inSail, String inOwner)
    {
        this( inName, inSail, new Person( inOwner));
    }

    public Boat( String inName, String inSail, Person inOwner)
    {
        this( inName, new SailId( inSail), inOwner);
    }

    public Boat( String inName, String inSail, String inOwner)
    {
        this( inName, new SailId( inSail), new Person( inOwner));
    }

    public Boat()
    {
        this( "", new SailId(), new Person());
    }

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
    	String value = n.getAttribute( NAME_PROPERTY);
    	if (value != null) setName( value);
    	
    	value = n.getAttribute( SAILID_PROPERTY);
    	if (value != null) setSailId( new SailId(value));
    	
    	PersistentNode n2 = n.getElement( OWNER_PROPERTY);
    	if (n2 != null)
		{
            Person p = new Person();
            p.xmlRead( n2, rootObject);
            setOwner( p);
        }
    	
    	n2 = n.getElement( RATINGLIST_PROPERTY);
    	if (n2 != null)
        {
            fRatingList.clear();
            fRatingList.xmlRead( n2, rootObject);
        }
        
    }

    @Override public void xmlWrite( PersistentNode node) //Document doc, String tag)
    {
    	
        //Element e = doc.createElement( tag);
        if (fName != null && fName.length() > 0) node.setAttribute( NAME_PROPERTY, fName);
        if (fSailId != null && fSailId.toString().length() > 0) node.setAttribute( SAILID_PROPERTY, fSailId.toString());
        if (fOwner != null) 
        {
        	fOwner.xmlWrite( node.createChildElement(OWNER_PROPERTY));
        }
        if (fRatingList != null)
        {
            fRatingList.xmlWrite( node.createChildElement(RATINGLIST_PROPERTY), RATING_PROPERTY);
        }     
    }

    /**
     * removes rating
     */
    public void removeRating( Rating inRate)
    {
        if (inRate != null)
        {
            fRatingList.remove( inRate);
            firePropertyChange( REMOVERATING_PROPERTY, null, inRate);
        }
    }

    /**
     * returns rating for specified rating system, will autocreate a missing 1D rating, 
     */
    public Rating getRating( String inSys)
    {
        if (inSys == null) return null;
        int i = fRatingList.indexOfRatingSystem( inSys);
        if (i < 0) 
        {
        	if (inSys.equals( RatingOneDesign.SYSTEM) || 
        		inSys.equals( "One Design"))
        	{
        		Rating rtg = new RatingOneDesign( inSys);
        		putRating( rtg);
        		return rtg;
        	}
        	return null;
        } 
        else
        {
        	return fRatingList.get(i);
        } 
    }

    /**
     * if rating of this system already exists, it is replaced
     * otherwise the rating is added to the list
     */
    public void putRating( Rating inRate)
    {
        int i = fRatingList.indexOfRatingSystem( inRate);
        if (i >= 0) fRatingList.remove(i);
        fRatingList.add( inRate);
    }

    public int getNumRatings()
    {
        return fRatingList.size();
    }

    /*public RatingList getRatingList()
    {
        return fRatingList;
    }*/

    public int compareTo( Object obj)
    {
        if (!(obj instanceof Boat)) return -1;
        if (this.equals( obj)) return 0;

        Boat that = (Boat) obj;

        int i = this.fSailId.compareTo( that.fSailId);
        if (i != 0) return i;

        i = this.fName.compareTo( that.fName);
        if (i != 0) return i;

        return this.fOwner.compareTo( that.fOwner);
    }

    @Override public boolean equals( Object obj)
    {
        if ( !(obj instanceof Boat)) return false;
        if ( this == obj) return true;

        Boat that = (Boat) obj;
        if ( !Util.equalsWithNull( this.fSailId, that.fSailId)) return false;
        if ( !Util.equalsWithNull( this.fName, that.fName)) return false;
        if ( !Util.equalsWithNull( this.fOwner, that.fOwner)) return false;

        // 18 march 2001 - intentionally NOT comparing ratings

        // dont need to compare rating list, super.equals should have done that
        return true;
    }

    /*
    public void removeRating(Rating delRate)
    {
        fRatingList.remove( delRate);
        if (delRate != null) delRate.removePropertyChangeListener(this);
        firePropertyChange( REMOVERATING_PROPERTY, delRate, null);
    }
    */

    @Override public String toString()
    {
        StringBuffer sb = new StringBuffer( fName);
        sb.append( "/ ");
        sb.append( fSailId);
        sb.append( "/ ");
        if (fOwner==null) sb.append( "<None>");
        else sb.append( fOwner.toString());
        return sb.toString();
    }

    public void vetoableChange(PropertyChangeEvent de)
        throws PropertyVetoException
    {
        // listens for changes to its ratinglist, and since rating members are all
        // children, then it also listens for changes to the kids

        /*
        if (de.getSource() == fRatingList)
        {
            String fName = de.getPropertyName();
            Rating newRate = (Rating) de.getNewValue();
            Rating oldRate = (Rating) de.getOldValue();

            if (fName.equals("add"))
            {
                if (newRate != null) newRate.addPropertyChangeListener(this);
                firePropertyChange( ADDRATING_PROPERTY, null, newRate);
            }
            else if (fName.equals("remove"))
            {
                if (oldRate != null) oldRate.removePropertyChangeListener(this);
                firePropertyChange( REMOVERATING_PROPERTY, oldRate, null);
            }
            else if (fName.equals("replace"))
            {
                if (newRate != null) newRate.addPropertyChangeListener(this);
                if (oldRate != null) oldRate.removePropertyChangeListener(this);
                firePropertyChange( CHANGERATING_PROPERTY, oldRate, newRate);
            }
        }
        else */
        if (de.getSource() == fOwner)
        {
            setOwner( (Person) de.getNewValue());
        }
    }

    // -------- getter/setters for properties ----------

    public String getName()
    {
        return fName;
    }

    public void setName( String inName)
    {
        String hold = fName;
        fName = inName;
        firePropertyChange( NAME_PROPERTY, hold, fName);
    }

    /*
    public Rating getRating( String inName)
    {
        return (Rating) fRatingList.get( inName);
    }

    public RatingList getRatingList()
    {
        return fRatingList;
    }
    */

    public Person getOwner()
    {
        return fOwner;
    }

    public void setOwner(Person s)
    {
        Person hold = fOwner;
        fOwner = s;
        firePropertyChange( OWNER_PROPERTY, hold, s);
    }

    /*
    public Person getLastSkipper()
    {
        if (pSkippers == null) return null;
        if (pSkippers.size() == 0) return null;

        return (Person) pSkippers.back();
    }

    public void setLastSkipper(Person s)
    {
        if (pSkippers.size() == 0)
        {
            pSkippers.add(s);
            firePropertyChange( "LastSkipper", null, s);
        }
        else if (! s.equals( pSkippers.back()))
        {
            pSkippers.remove( s); // kills any existing occurrences of s
            pSkippers.add( s);
            firePropertyChange( "LastSkipper", null, s);
        }
        // if lastskippers already is s nothing happens
    }
    */

    public SailId getSailId()
    {
        return fSailId;
    }

    public void setSailId(SailId s)
    {
        SailId hold = fSailId;
        fSailId = s;
        firePropertyChange( SAILID_PROPERTY, hold, fSailId);
    }

    
    public static String databaseSelect()
    {
        return "";
    }

    public static Class getColumnClass( int c)
    {
        switch (c)
        {
            case 0: return String.class;
            case 1: return SailId.class;
            case 2: return Person.class;
            case 3: return RatingList.class;
        }
        return null;
    }

    public static int getColumnCount()
    {
        return 4;
    }

    public static String getColumnName( int c)
    {
        switch (c)
        {
            case 0: return "Name";
            case 1: return "Sail";
            case 2: return "Owner";
            case 3: return "Ratings";
        }
        return null;
    }

    
}
/**
 * $Log: Boat.java,v $
 * Revision 1.4  2006/01/15 21:10:37  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:01  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.10.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.10.2.1  2005/06/26 22:47:17  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.10  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.9  2004/04/10 20:42:18  sandyg
 * Bug 932941, core dump moving boat from PHRF to 1D
 *
 * Revision 1.8  2003/07/10 02:50:30  sandyg
 * overrides a contradictory legacy one-design rating on xmlRead
 *
 * Revision 1.7  2003/04/27 21:03:26  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/03/28 03:07:43  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.5  2003/01/05 21:16:31  sandyg
 * regression unit testing following rating overhaul from entry to boat
 *
 * Revision 1.4  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */

