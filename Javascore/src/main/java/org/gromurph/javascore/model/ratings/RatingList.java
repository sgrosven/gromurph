//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingList.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model.ratings;

import java.util.Collection;
import java.util.Iterator;

import org.gromurph.javascore.exception.RatingExistsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.util.BaseList;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * contains a list of ratings.  Two ratings of the same rating system
 * are not allowed
 */
public class RatingList extends BaseList<Rating>
{
	@Override
	public Class getContainingClass()
	{
		return Rating.class;
	}

    boolean fIsOneBoat;  // true if rating list is related to a single boat.

    public RatingList( boolean inB)
    {
        super();
        fIsOneBoat = inB;
    }

    public RatingList()
    {
        this( true);
    }

    public int indexOfRatingSystem( Rating inR)
    {
        return indexOfRatingSystem( inR.getSystem());
    }

    public int indexOfRatingSystem( String inSys)
    {
        for (int i = 0; i < this.size(); i++)
        {
            Rating r = get(i);
            if (r.getSystem().equals( inSys)) return i;
        }
        return -1;
    }

    /**
     * returns true if list is used on a single boat
     * will affect rules for adding a new rating to the list
    public boolean isOneBoat()
    {
        return fIsOneBoat;
    }
    **/

    @Override public void add( int row, Rating that)
    {
        try
        {
            int i = indexOfRatingSystem(that);
            if ( i >= 0) throw new RatingExistsException(i);
            super.add( row, that);
        }
        catch (ClassCastException e)
        {
            Util.showError( e, true);
        }
    }

    @Override  public boolean addAll(int index, Collection<? extends Rating> c)
    {
        try
        {
            Iterator it = c.iterator();
            while( it.hasNext())
            {
                Rating that = (Rating) it.next();
                int i = indexOfRatingSystem(that);
                if ( i >= 0) throw new RatingExistsException(i);
            }
            return super.addAll( index, c);
        }
        catch (ClassCastException e)
        {
            Util.showError( e, true);
        }
        return false;
    }

    @Override public Rating set(int index, Rating that)
    {
        try
        {
            int i = indexOfRatingSystem( that);
            if ( i >= 0) throw new RatingExistsException(i);
            return super.set( index, that);
        }
        catch (ClassCastException e)
        {
            Util.showError( e, true);
        }
        return null;
    }
    
    @Override public void xmlRead( PersistentNode rootNode, Object rootObject)
    {
        PersistentNode[] kids2 = rootNode.getElements();
        
        for (int k = 0; k < kids2.length; k++)
        {
            try
            {
	        	PersistentNode n2 = kids2[k];
	        	Rating r = RatingManager.createRatingFromXml( n2, rootObject);
	        	add( r);
            }
            catch (Exception e)
            {
                Util.printlnException( this, e, true);
            }
        }
    }
    
}
/**
 * $Log: RatingList.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.7.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.7.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.7  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.6  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.5  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
*/
