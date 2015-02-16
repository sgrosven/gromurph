//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Bow.java,v 1.4 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;


/**
 * Class for containing bow numbers.  Small extension of string to try to sort
 * numerically first before return a string compare
 */
public class Bow
    implements Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    private String fBow;

    public Bow( String inS)
    {
        //super();
        fBow = inS;
    }

    public Bow()
    {
        this("");
    }

    public int compareTo( Object obj)
    {
        if (!(obj instanceof Bow)) return -1;
        if (this.equals( obj)) return 0;

        Bow that = (Bow) obj;

        try
        {
            // try to compare numerically first
            return new Integer( this.fBow).compareTo( new Integer( that.fBow));
        }
        catch (Exception e)
        {
            return this.fBow.compareTo( that.fBow);
        }
    }

    @Override public boolean equals( Object obj)
    {
        if ( !(obj instanceof Bow)) return false;
        if ( this == obj) return true;

        Bow that = (Bow) obj;
        if ( (fBow == null) ? (that.fBow != null) : !(fBow.equalsIgnoreCase( that.fBow)) ) return false;
        return true;
    }

    public void restore( Object inObj)
    {
        if (this == inObj) return;
        //super.restore( inObj);

        Bow that = (Bow) inObj;
        this.fBow = that.fBow;
    }

    @Override public String toString()
    {
        return fBow;
    }

}
/**
 * $Log: Bow.java,v $
 * Revision 1.4  2006/01/15 21:10:37  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:09  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:01  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4  2004/04/10 20:49:28  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/01/04 17:29:09  sandyg
 * Prefix/suffix overhaul
 *
 */
