//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RaceList.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.util.BaseList;

/**
 * contains a list of Races and operation that apply specifically to Races
 */
public class RaceList extends BaseList<Race>
{
	@Override
	public Class getContainingClass()
	{
		return Race.class;
	}

    private static final long serialVersionUID = 1L;

    public Race getRace( int id)
    {
        for (Race r : this)
        {
            if (r.getId() == id) return r;
        }
        return null;
    }

    public Race getRace( String name)
    {    	
        for (Race r : this)
        {
            if (r.getName().equals( name)) return r;
        }
        return null;
    }

    @Override public String databaseSelect() { return Race.databaseSelect(); }
    public Class getColumnClass( int c) { return Race.getColumnClass(c); }
    public int getColumnCount() { return Race.getColumnCount(); }
    public String getColumnName( int c) { return Race.getColumnName(c); }

}
/**
 * $Log: RaceList.java,v $
 * Revision 1.4  2006/01/15 21:10:38  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:01  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.6.4.1  2005/11/01 02:36:01  sandyg
 * Java5 update - using generics
 *
 * Revision 1.6  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/04/27 21:03:27  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */

