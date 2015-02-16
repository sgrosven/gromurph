//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: StartingDivisionList.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.gromurph.util.BaseList;
import org.gromurph.xml.PersistentNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * a list of Divisions.  Currently with the simplified division, not much to do here
 */
public class StartingDivisionList extends BaseList<AbstractDivision>
{
    @Override
	public Class getContainingClass()
	{
		return AbstractDivision.class;
	}
    
    private static final long serialVersionUID = 1L;

    public AbstractDivision find( String name)
    {
    	for( AbstractDivision div : this)
        {
            if (div.getName().equals( name)) return div;
        }
        return null;
    }

	Map<Integer, AbstractDivision> classSizes;

	public void sortSize( EntryList entries)
	{
		classSizes = new HashMap<Integer, AbstractDivision>(5);

    	for( AbstractDivision div : this)
        {
			int count = entries.findAll( div).size();
			classSizes.put( new Integer(count), div);
		}

		Collections.sort(
			this,
			new Comparator<AbstractDivision>() {
				public int compare( AbstractDivision left, AbstractDivision right)
				{
					if (left == null && right == null) return 0;
					if (left == null) return -1;
					if (right == null) return 1;

					Object oL = classSizes.get( left);
					Object oR = classSizes.get( right);
					Integer sLeft = (oL == null) ? new Integer( 0): (Integer) oL;
					Integer sRight = (oR == null) ? new Integer( 0) : (Integer) oR;
					return sLeft.compareTo( sRight);
				}
			}
		);

	}


    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
    	// do nothing, shouldnt happen
    }

	public Node xmlWrite(Document doc, String listTag, String elementTag)
	{
		throw new UnsupportedOperationException("AbstractDivisionLists not saveable");
	}

}

/**
 * $Log: StartingDivisionList.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.1.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.1.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.1  2004/05/06 02:11:50  sandyg
 * Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 *
 * Revision 1.11  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.10  2003/05/06 21:32:17  sandyg
 * added checkin report by class, by bow
 *
 * Revision 1.9  2003/05/04 23:26:14  sandyg
 * 2003 CBYRA Phrf splits entered
 *
 * Revision 1.8  2003/04/27 21:03:26  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.6  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */
