//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SeriesPoints.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * Contains points information on an entry in a race
 * This is separated from the Finish object because when fleet scoring gets
 * implemented an entry could have more than one score for a single finish
**/
public class SeriesPoints extends Points
{
	boolean fTied;

	public SeriesPoints()
	{
		this( null, null);
	}

	public SeriesPoints( Entry entry, AbstractDivision div)
	{
		this( entry, div, Double.NaN, Integer.MAX_VALUE, false);
	}

	public SeriesPoints( Entry entry, AbstractDivision div, double points, int pos, boolean tied)
	{
		super( entry, div, points, pos);
		if (entry != null && entry.getBoat() != null && entry.getBoat().getSailId() != null)
		{        
			aId = entry.getBoat().getSailId().toString();
		}
		fTied = tied;
	}

	@Override public int compareTo(Object obj)
	{
		if (obj == null) return -1;
		if (obj instanceof SeriesPoints)
		{
			SeriesPoints that = (SeriesPoints) obj;
			if ( this.fPosition < that.fPosition) return -1;
			else if (this.fPosition > that.fPosition) return 1;
			else return super.compareTo(obj);
		}
		else {
			return super.compareTo( obj);
		}
	}

	@Override public boolean equals( Object obj)
	{
		if ( this == obj) return true;
		if ( !(obj instanceof SeriesPoints)) return false;

		if (!super.equals(obj)) return false;

		SeriesPoints that = (SeriesPoints) obj;
		return (this.fTied == that.fTied);
	}

	public static final String TIED_PROPERTY = "Tied";

	@Override public void xmlRead( PersistentNode n, Object rootObject)
	{
		super.xmlRead( n, rootObject);

		String value = n.getAttribute( TIED_PROPERTY);
		if (value != null)
		{
			boolean b = value.toString().equalsIgnoreCase("true");
			try { setTied( b); } catch (Exception e) {}
		}
	}

    @Override public void xmlWrite( PersistentNode e)
    {
    	if (getEntry() == null) return;    
		super.xmlWrite( e);
		if (isTied()) e.setAttribute( TIED_PROPERTY, new Boolean( isTied()).toString());
		//return e;
	}

    @Override public String toString()
    {
        String ss = super.toString();
        if (fTied) ss += "T";
        return ss;     
    }        

	public static String format( SeriesPoints sp)
	{
		StringBuffer base = new StringBuffer();
		base.append( Util.formatDouble( sp.getPoints(), 2));
		if ( sp.isTied()) base.append("T");
		return base.toString();
	}

	public void setTied( boolean t)
	{
		Boolean old = new Boolean( t);
		fTied = t;
		firePropertyChange( TIED_PROPERTY, old, new Boolean( fTied));
	}

	public boolean isTied()
	{
		return fTied;
	}

	private static final long serialVersionUID = 1L;

}
/**
 * $Log: SeriesPoints.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.8.2.1  2005/06/26 22:47:19  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.8  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/04/27 21:03:29  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/03/28 03:07:44  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.5  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.4  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */
