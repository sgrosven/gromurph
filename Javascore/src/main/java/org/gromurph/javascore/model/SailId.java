//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SailId.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.io.Serializable;

import org.gromurph.util.Util;

/**
 * Contains a boat's sail number identification.  It is composed of 3 parts, any of which
 * are optional.  A character prefix, a numeric middle, and a character postfix.
 * For example in the identifier  "USA 1234 Y", the USA is the prefix, the 1234 is the
 * number, and the Y is the postfix.  Any of these are optional.
 *
 * Normally a sailid will sort on the middle number first, then on the prefix and postfix.
 * Use setNumericSort() to change this to an all alphanumeric sort which will treat the
 * entire id as a single string and sort it alphabetically.
 *
 * the equals() in this class is NOT case sensitive
 */
public class SailId
    implements Cloneable, Serializable
{
    private static boolean fNumericSort = true;

    private static final long serialVersionUID = 1L;

    private String fPrefix;
    private long fNumber;
    private String fPostfix;

    public SailId( String ss)
    {
        String inS = ss.trim();
        fPrefix = "";
        fNumber = Long.MIN_VALUE;
        fPostfix = "";

        char[] inC = inS.toCharArray();
        int i = 0;
        // put lead letters into prefix
        while ( (i < inC.length) && ( !Character.isDigit( inC[i]))) i++;
        fPrefix = inS.substring( 0, i).trim();
        inS = inS.substring( i);

        inC = inS.toCharArray();
        i = 0;
        // put digits into string for number
        while ( (i < inC.length) && ( Character.isDigit( inC[i]))) i++;
        if (i > 0) fNumber = Long.parseLong( inS.substring( 0, i));

        fPostfix = inS.substring( i).trim();
    }

    public SailId()
    {
        this("");
    }

    public int compareTo( Object obj)
    {
        if (!(obj instanceof SailId)) return -1;
        if (this.equals( obj)) return 0;

        SailId that = (SailId) obj;

        if (fNumericSort)
        {
            if (this.fNumber < that.fNumber)
            {
                return -1;
            }
            else if (this.fNumber > that.fNumber)
            {
                return 1;
            }
            else
            {
                int i = this.fPrefix.compareTo( that.fPrefix);
                if (i != 0) return i;
                return this.fPostfix.compareTo( that.fPostfix);
            }
        }
        else
        {
            return this.toString().compareTo( that.toString());
        }
    }

    @Override public boolean equals( Object obj)
    {
        if ( this == obj) return true;
        try
        {
            SailId that = (SailId) obj;
            if (this.fNumber != that.fNumber) return false;
            if (!Util.equalsIgnoreCaseWithNull( this.fPrefix, that.fPrefix)) return false;
            return Util.equalsIgnoreCaseWithNull( this.fPostfix, that.fPostfix);
        }
        catch (ClassCastException e) {return false;}
    }

    public void restore( Object inObj)
    {
        if (this == inObj) return;
        //super.restore( inObj);

        SailId that = (SailId) inObj;
        this.fPrefix = that.fPrefix;
        this.fPostfix = that.fPostfix;
        this.fNumber = that.fNumber;
    }

    @Override public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (fNumber > Long.MIN_VALUE)
        {
            sb.append( fPrefix);
            if (fPrefix.length() > 0) sb.append( " ");
            sb.append( fNumber);
            if (fPostfix.length() > 0) sb.append( " ");
            sb.append( fPostfix);
        }
        else
        {
            sb.append( fPrefix);
            if (fPrefix.length() > 0 || fPostfix.length() > 0) sb.append( " ");
            sb.append( fPostfix);
        }
        return sb.toString();
    }

    /**
     * returns the leading characters in front of the sail number.  Returns empty
     * string if no prefix exists
     */
    public String getPrefix()
    {
        return fPrefix;
    }

    /**
     * returns the trailing alphabetic portion of the sailid, or an empty string if none
     */
    public String getPostfix()
    {
        return fPostfix;
    }

    /**
     * returns the numeric portion of the sail id
     */
    public long getNumber()
    {
        return fNumber;
    }

    public static void setNumericSort( boolean b)
    {
        fNumericSort = b;
    }

}
/**
 * $Log: SailId.java,v $
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:10  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.6  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/04/27 21:03:28  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.4  2003/01/05 21:29:29  sandyg
 * fixed bad version/id string
 *
 * Revision 1.3  2003/01/04 17:29:10  sandyg
 * Prefix/suffix overhaul
 *
 */
