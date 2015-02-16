//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Fleet.java,v 1.6 2006/01/19 01:50:15 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.xml.PersistentNode;

/**
 * a Fleet is a combination of several Divisions of starting classes.
 */
public class Fleet extends AbstractDivision
{
    private List<AbstractDivision> fDivisions;
    private boolean fSameStartSameClass;
    private String fDifferentLengths;

    @Override public String getGender() { return null;}
    
    /**
     * Score races with divisions sailing different lengths as they are without
     * correction
     */
    public static String DIFFLENGTHS_ASIS = "AsIs";

    /**
     * Score races with divisions sailing different lengths on an average speed
     * basis. (corrected times will be proportional to the length of the race)
     */
    // NYI public static String DIFFLENGTHS_AVGSPEED = "AvgSpeed";

    /**
     * If races have divisions sailing different lengths, the race will NOT
     * be included in the fleet's series score. This is the default
     */
    public static String DIFFLENGTHS_DROP = "Drop";

    public static String SAMESTARTSAMECLASS_PROPERTY = "SameStartSameClass";
    public static String DIFFERENTLENGTHS_PROPERTY = "DifferentLengths";
    public static String DIVISION_ADD_PROPERTY = "DivisionAdd";
    public static String DIVISION_REMOVE_PROPERTY = "DivisionRemove";

    public Fleet()
    {
        this( NO_NAME);
    }

    /**
     * creates default fleet with specified name.  Member divisions that start
     * a race at the same time will be scored as a single race, and races of
     * differently lengths for different member divisions will be dropped from
     * the series score.
     *
     * @param name
     */
    public Fleet( String name)
    {
        super( name);

        fDivisions = new ArrayList<AbstractDivision>(5);
        fSameStartSameClass = true;
        fDifferentLengths = DIFFLENGTHS_DROP;
    }

    @Override public boolean equals( Object obj)
    {
        if (!super.equals(obj)) return false;
        try
        {
            Fleet that = (Fleet) obj;
            if ( !this.fDifferentLengths.equals( that.fDifferentLengths)) return false;
            if ( this.fSameStartSameClass != that.fSameStartSameClass) return false;
            if ( !this.fDivisions.equals( that.fDivisions)) return false;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Determines if divisions that start together are scored together. Default
     * is true.
     * @param b
     */
    public void setSameStartSameClass( boolean b)
    {
//        boolean hold = fSameStartSameClass;
        fSameStartSameClass = b;
//        firePropertyChange( SAMESTARTSAMECLASS_PROPERTY, new Boolean(hold),
//            new Boolean( fSameStartSameClass));
    }

    /**
     * Determines how the series score will handle races where different fleet
     * members sail different length courses.  The valid options are:
     * DIFFLENGTHS_ASIS, DIFFLENGTHS_AVGSPEED, DIFFLENGTHS_DROP
     * @param value
     */
    public void setHandleDifferentLengths( String value)
    {
//        String hold = fDifferentLengths;
        fDifferentLengths = value;
//        firePropertyChange( DIFFERENTLENGTHS_PROPERTY, hold, fDifferentLengths);
    }

    @Override public boolean isOneDesign()
    {
        for (Iterator m = members(); m.hasNext();)
        {
            if ( !((AbstractDivision) m.next()).isOneDesign()) return false;
        }
        return true;
    }

    /**
     * default is true, @see setHandleDifferentLengths
     * @return true if classes started together are scored together
     */
    public boolean isSameStartSameClass()
    {
        return fSameStartSameClass;
    }

    /**
     * returns how races with divisions of different lengths are scored.
     * @see setHandleDifferentLengths
     * @return one of DIFFLENGTHS_ASIS, DIFFLENGTHS_AVGSPEED, DIFFLENGTHS_DROP
     */
    public String getHandleDifferentLengths()
    {
        return fDifferentLengths;
    }

    /**
     * returns true if this race should be scored (and included in series totals)
     * @param r
     * @return
     */
    public boolean shouldScoreRace( Race race)
    {
        if ( fDifferentLengths.equals( DIFFLENGTHS_ASIS)) return true;

        double length = -1;
        for (Iterator i = members(); i.hasNext();)
        {
            Division div = (Division) i.next();
            if ( div.isRacing( race))
            {
                if (length < 0)
                {
                    // first division, set length
                    length = race.getLength( div);
                }
                else
                {
                    if (length != race.getLength(div)) return false;
                }
            }
        }
        return true;
    }


    /**
     * @return an Iterator through the AbstractDivision members of this fleet
     */
    public Iterator<AbstractDivision> members()
    {
        return fDivisions.iterator();
    }

    /**
     * add a new Division to the Fleet.  May not only add
     * Divisions or SubDivisions.
     * @param div
     */
    public void addDivision( Division div)
    {
        if (!fDivisions.contains(div))
        {
            fDivisions.add( div);
            firePropertyChange( DIVISION_ADD_PROPERTY, null, div);
        }
    }

//	/**
//	 * add a new SubDivision to the Fleet.  May not only add
//	 * Divisions or SubDivisions.
//	 * @param div
//	 */
//	public void addDivision( SubDivision div)
//	{
//		if (!fDivisions.contains(div))
//		{
//			fDivisions.add( div);
//			firePropertyChange( DIVISION_ADD_PROPERTY, null, div);
//		}
//	}

    /**
     * remove the abstractdivision from the fleet
     * @param div
     */
//	public void removeDivision( AbstractDivision div)
    public void removeDivision( Division div)
    {
        fDivisions.remove( div);
        firePropertyChange( DIVISION_REMOVE_PROPERTY, null, div);
    }

    /**
     * returns the number of divisions in the fleet
     * @return
     */
    public int getNumDivisions()
    {
        return fDivisions.size();
    }

    public void clearDivisions()
    {
        fDivisions.clear();
    }

    /**
     * creates a set of subfleets incorporating the sametimesameclass concept
     * Used to group a set of finishers for scoring
     * @param r the Race on which to group the divisions
     * @return a List of subfleets, each element in the list is itself a list of divisions
     * that should be scored together
     */
    public List<List<AbstractDivision>> getSubFleets( Race r)
    {
        List<List<AbstractDivision>> subFleets = new ArrayList<List<AbstractDivision>>();

        List<AbstractDivision> fleetDivs = new ArrayList<AbstractDivision>( this.getNumDivisions());
        for( AbstractDivision div : r.getStartingDivisions(true))
        {
        	if (this.contains(div)) fleetDivs.add( div);
        }

        // until this is empty run the loop
        while (fleetDivs.size() > 0)
        {
            List<AbstractDivision> minifleet = new ArrayList<AbstractDivision>();
            subFleets.add( minifleet);

            AbstractDivision div = fleetDivs.get(0);
            long starttime = r.getStartTimeRaw(div);
            minifleet.add( div);
            fleetDivs.remove( div);

            for (int d = 0; d < fleetDivs.size(); d++)
            {
                div = fleetDivs.get(d);
                if (starttime == r.getStartTimeRaw(div))
                {
                    minifleet.add( div);
                    fleetDivs.remove( div);
                }
            }
        }
        return subFleets;
    }


    @Override
	public EntryList getEntries() {
    	EntryList el = new EntryList();
    	Set<Entry> eset = new HashSet<Entry>();
        for( AbstractDivision div : fDivisions) {
            eset.addAll(div.getEntries());
        }
        el.addAll(eset);
		return el;
	}

	@Override public boolean isRacing( Race race, boolean orParent, boolean orChild) {
		if (super.isRacing(race, orParent, orParent)) return true;
		if (orChild) {
			for (AbstractDivision div : fDivisions) {
				if (div.isRacing(race, orParent, orChild)) return true;
			}
		}
		return false;
	}

	/**
     * checks to see if entry is contained in this fleet
     * @param entry
     * @return true if entry is in one of the divisions contained in this Fleet
     */
    @Override public boolean contains(Entry entry)
    {
        for( AbstractDivision div : fDivisions) {
            if ( div.contains( entry)) return true;
        }
        return false;
    }

    /**
     * checks to see if division is contained in this fleet
     * @param div
     * @return true if div is in one of the divisions contained in this Fleet
     */
    @Override public boolean contains( AbstractDivision div)
    {
    	if (div instanceof SubDivision)
    	{
    		for (AbstractDivision adiv : fDivisions)
    		{
    			if (adiv.equals(div)) return true;
    			if (adiv.contains( div)) return true;
    		}
    		return false;
    	}
    	else
    	{
    		return ( equals(div)  || fDivisions.contains( div));
    	}
    }

    /**
     * checks to see if entry is contained in this fleet
     * @param entry
     * @return true if entry is in one of the divisions contained in this Fleet
     */
    @Override public boolean contains(Rating r)
    {
        for( Iterator iter = fDivisions.iterator(); iter.hasNext();)
        {
            AbstractDivision div = (AbstractDivision) iter.next();
            if ( div.contains( r)) return true;
        }
        return false;
    }

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
        super.xmlRead( n, rootObject);
        fDivisions.clear();

        String value = n.getAttribute( DIFFERENTLENGTHS_PROPERTY);
        if (value != null) fDifferentLengths = value;

        value = n.getAttribute( SAMESTARTSAMECLASS_PROPERTY);
        if (value != null) fSameStartSameClass = value.toString().equalsIgnoreCase("true");

        PersistentNode divlistNode = n.getElement( DIVISIONLIST_PROPERTY);
        if (divlistNode == null) divlistNode = n.getElement( OLDDIVLIST_PROPERTY);
        
        if (divlistNode != null)
        {
            List nodes = Arrays.asList( divlistNode.getElements());
            Regatta reg = (Regatta) rootObject;

            for (Iterator iter = nodes.iterator(); iter.hasNext();)
            {
            	PersistentNode divNode = (PersistentNode) iter.next();            	
                String name = divNode.getAttribute( FNAME_PROPERTY);
                if (reg != null && name != null)
                {
                    Division div = reg.getDivision( name);
                    SubDivision sdiv = reg.getSubDivision( name);
                    if (div != null)
                    {
                        fDivisions.add( div);
                    }
                    else if (sdiv != null)
                    {
                        fDivisions.add( sdiv);
                    }
                    else
                    {
                        div = new Division( name, RatingOneDesign.SYSTEM);
                        fDivisions.add( div);
                    }
                }
                else if (name != null)
                {
                    Division div = DivisionList.getMasterList().find( name);
                    if (div != null)
                    {
                        fDivisions.add( div);
                    }
                    else
                    {
                        fDivisions.add( new Division( name));
                    }
                }
            }
        }
    }

    private transient static final String DIVISIONLIST_PROPERTY = "FleetDivList";
    private transient static final String OLDDIVLIST_PROPERTY = "DivisionList";
    private transient static final String DIVISION_PROPERTY = "Div";
    private transient static final String FNAME_PROPERTY = "Name";

    @Override public void xmlWrite( PersistentNode e)
    {
    	super.xmlWrite(e);

        e.setAttribute( DIFFERENTLENGTHS_PROPERTY, fDifferentLengths);
        e.setAttribute( SAMESTARTSAMECLASS_PROPERTY, new Boolean( fSameStartSameClass).toString());

        PersistentNode eDiv = e.createChildElement( DIVISIONLIST_PROPERTY);
        for (Iterator iter = fDivisions.iterator(); iter.hasNext();)
        {
            AbstractDivision obj = (AbstractDivision) iter.next();
            PersistentNode child = eDiv.createChildElement( DIVISION_PROPERTY);
            child.setAttribute( FNAME_PROPERTY, obj.getName());
        }
    }

	/**
	 * Should return the number of registered boats for the specified division 
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
    @Override public int getNumEntries()
	{
		int n = 0;
		for (AbstractDivision div : fDivisions)
		{
			n += div.getNumEntries();
		}
		return n;
	}
	

	/**
	 * Should return the number of boats registered for this fleet and able to race in the 
	 * specified race.  
	 * @param regatta
	 * @param div
	 * @param race
	 * @return
	 */
    @Override public int getNumEntries( Race race)
	{
		int n = 0;
		for (AbstractDivision div : fDivisions)
		{
			n += div.getNumEntries( race);
		}
		return n;
	}
	

}
/**
 * $Log: Fleet.java,v $
 * Revision 1.6  2006/01/19 01:50:15  sandyg
 * fixed several bugs in split fleet scoring
 *
 * Revision 1.5  2006/01/15 21:10:38  sandyg
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
 * Revision 1.12.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.12.2.1  2005/06/26 22:47:17  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.12  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.11  2003/04/27 21:36:13  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.10  2003/04/27 21:03:27  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.9  2003/03/30 00:05:43  sandyg
 * moved to eclipse 2.1
 *
 * Revision 1.8  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.7  2003/03/28 01:23:54  sandyg
 * Request #704967, xml division list tag changed to FleetDivList
 *
 * Revision 1.6  2003/03/16 20:39:15  sandyg
 * 3.9.2 release: encapsulated changes to division list in Regatta,
 * fixed a bad bug in PanelDivsion/Rating
 *
 * Revision 1.5  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.4  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */
