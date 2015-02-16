//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: AbstractDivision.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Parent abstract class for Division, Fleet, and SubDivision
**/
public abstract class AbstractDivision extends BaseObject
{
    static ResourceBundle res = JavaScoreProperties.getResources();

	public abstract EntryList getEntries();

	public boolean isRacing( Race race) {
		return isRacing( race, false, false);
	}
	public boolean isRacing( Race race, boolean orParent) {
		return isRacing( race, orParent, false);
	}
	public boolean isRacing( Race race, boolean orParent, boolean orChild) {
		return race.getDivInfo().isRacing(this);
	}
	public boolean isGroupQualifying() {return false;} // overridden by subdivision
    /**
     * returns true if specified entry is contained in this class
     * @param entry
     * @returns true if entry's rating is contained in this division
     */
    public abstract boolean contains(Entry entry);

    /**
     * returns true if specified division contained in this division
     * @param div
     * @returns 
     */
    public abstract boolean contains( AbstractDivision div);

    /**
     * returns true if specified rating is contained in this division
     * @param div
     * @returns 
     */
    public abstract boolean contains( Rating r);

    /**
     * true when class contains only one design members
     * @return when class contains only one design members
     */
    public abstract boolean isOneDesign();
    
	/**
	 * Should return the number of registered boats for the current regatta
	 * @return
	 */
	public abstract int getNumEntries();
	

	/**
	 * Should return the number of boats registered for the specified race in the 
	 * current regatta.  Note that if the division is not racing in the specified race, this
	 * returns 0.
	 * @param race
	 * @return
	 */
	public abstract int getNumEntries( Race race);

	public AbstractDivision getParentDivision() {
		return this;
	}
	
    /**
     * temporary master division for regattas with all one class
     */
    public static Division NONE = new Division();
    public static final String NO_NAME = res.getString("noname");
    public static final String NAME_PROPERTY = "DivName";
    public static final String GENDER_PROPERTY = "Gender";
    public static final String IFCLASSID_PROPERTY = "IFClassID";

    private static final long serialVersionUID = 1L;

    private String fName;
    private String fGender;
    private String fIfClassId;

    public AbstractDivision()
    {
        this( NO_NAME);
    }

    /**
     * basic constructor
     * @param name name for the abstractdivision
     * @see Rating#getSupportedSystems
    **/
    public AbstractDivision(String name)
    {
        super();
        fName = name;
    }

    /**
     * default implementation, creates a new instance of object
     * with default constructor and compares to it.
     * @returns true if division matches a blank or empty division
     */
    @Override public boolean isBlank()
    {
        return toString().equals(NO_NAME) || equals(NONE);
    }

    @Override public void xmlWrite(PersistentNode e)
    {
    	//PersistentNode e = parentNode.createChildElement();
        e.setAttribute( NAME_PROPERTY, getName());
         
        if (getGender() != null && getGender().length() > 0) e.setAttribute( GENDER_PROPERTY, getGender());
        if (getIfClassId() != null && getIfClassId() .length() > 0) e.setAttribute( IFCLASSID_PROPERTY, getIfClassId() );
               //return e;
    }

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
    	String value = n.getAttribute( NAME_PROPERTY);
    	if (value != null) setName( value);
    	
    	value = n.getAttribute( GENDER_PROPERTY);
    	if (value != null) setGender( value);
    	
    	value = n.getAttribute( IFCLASSID_PROPERTY);
    	if (value != null) setIfClassId( value);
    }

    @Override public boolean equals( Object obj)
    {
        if ( this == obj) return true;
        try
        {
            AbstractDivision that = (AbstractDivision) obj;
            if ( !Util.equalsWithNull( this.fName, that.fName)) return false;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private transient int fHash = 0;
    @Override public int hashCode()
    {
        if (fHash == 0) fHash = getName().hashCode();
        return fHash;
    }

    public int compareTo( Object obj)
    {
        return getName().compareTo( ((AbstractDivision)obj).getName());
    }

    public String getName()
    {
        if (fName == null) fName = NO_NAME;
        return fName;
    }

    public void setName( String newName)
    {
        String oldName = fName;
        fName = newName;
        firePropertyChange( NAME_PROPERTY, oldName, newName);
    }

    @Override public String toString()
    {
        if ( getName().trim().length() == 0) return NO_NAME;
        else return getName();
    }

    /**
     * returns division name
     * @returns a longer descriptive name of the division
    **/
    public String getLongName()
    {
        return getName();
    }


    @Override public Object clone()
    {
        Division newDiv = (Division) super.clone();
        return newDiv;
    }

	public String getGender() {
		return fGender;
	}

	public void setGender( String gender) {
		fGender = gender;
	}

	public String getIfClassId() {
		return fIfClassId;
	}

	public void setIfClassId( String ifClassId) {
		fIfClassId = ifClassId;
	}

}
/**
 * $Log: AbstractDivision.java,v $
 * Revision 1.6  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.5  2006/01/15 21:10:37  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/14 21:06:55  sandyg
 * final bug fixes for 5.01.1.  All tests work
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:01  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.8.2.1  2005/06/26 22:47:16  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.8  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/04/27 21:03:26  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/03/28 03:07:43  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.5  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */
