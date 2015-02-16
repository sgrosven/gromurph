//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Person.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.util.ResourceBundle;

import org.gromurph.xml.PersistentNode;

public class Person extends BaseObject
{
    private static final long serialVersionUID = 1L;
    static ResourceBundle resUtil= Util.getResources();

    private static          int     sFormat;
    private                 String  fFirst = "";
    private                 String  fLast = "";
    private                 String  fSailorId = "";

    public static final int    FORMAT_LASTFIRST = 0;
    public static final int    FORMAT_FIRSTLAST = 1;
    public static final int    FORMAT_LASTONLY = 2;

    private static String[] sFormatOptions = new String[] {
        resUtil.getString("PersonLastFirst"),
        resUtil.getString("PersonFirstLast"),
        resUtil.getString("PersonLast")
    };
    public  static String[] getFormatOptions() { return sFormatOptions; }

    public static final String XML_NODENAME = "Person";
    public static final String FORMAT_PROPERTY = "Format";
    public static final String LASTNAME_PROPERTY = "LastName";
    public static final String FIRSTNAME_PROPERTY = "FirstName";
    public static final String SAILORID_PROPERTY = "SailorId";

    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
    	String value = "";    	
    	if ( (value = n.getAttribute( LASTNAME_PROPERTY)) != null) setLast( value);
    	if ( (value = n.getAttribute( FIRSTNAME_PROPERTY)) != null) setFirst( value);
    	if ( (value = n.getAttribute( SAILORID_PROPERTY)) != null) setSailorId( value);        
    }

    @Override public void xmlWrite( PersistentNode e)
    {

        if (fFirst != null) e.setAttribute( FIRSTNAME_PROPERTY, getFirst());
        if (fLast != null) e.setAttribute( LASTNAME_PROPERTY, getLast());
        if (fSailorId != null) e.setAttribute( SAILORID_PROPERTY, getSailorId());
    }

    public Person()
    {
        this( "", "", "");
    }

    public Person( String inFirst, String inLast)
    {
        this( inFirst, inLast, "");
    }

    public Person( String inFirst, String inLast, String id)
    {
        super();
        fFirst = (inFirst == null) ? "" : inFirst;
        fLast = (inLast == null) ? "" : inLast;
        fSailorId = (id == null) ? "" : id;
    }

    public Person( String s)
    {
        // needs to be expanded, only supports harry's F. Last format
        if (s == null)
        {
            fFirst = "";
            fLast = "";
        }
        else
        {
            setName(s);
        }
    }

    public int compareTo( Object obj)
    {
        if (!(obj instanceof Person)) return -1;
        if (this.equals( obj)) return 0;

        Person that = (Person) obj;
        int i = this.fLast.compareTo( that.fLast);
        if (i != 0) return i;

        i = this.fFirst.compareTo( that.fFirst);
        if (i != 0) return i;

        return this.fSailorId.compareTo( that.fSailorId);
    }

    public boolean isEmpty() {
    	if (fFirst != null && fFirst.trim().length() > 0) return false;
       	if (fLast != null && fLast.trim().length() > 0) return false;
       	if (fSailorId != null && fSailorId.trim().length() > 0) return false;       
       	return true;
    }

    @Override public boolean equals( Object obj)
    {
        if ( !(obj instanceof Person)) return false;
        if ( this == obj) return true;

        Person that = (Person) obj;
        if ( (fFirst == null) ? (that.fFirst != null) : !(fFirst.equals( that.fFirst)) ) return false;
        if ( (fLast == null) ? (that.fLast != null) : !(fLast.equals( that.fLast)) ) return false;
        if ( (fSailorId == null) ? (that.fSailorId != null) : !(fSailorId.equals( that.fSailorId)) ) return false;

        return true;
        // dont care about whether Boat Ownership/last skippered is same or not
    }

    @Override public Object clone()
    {
        // now clone it
        Person newS = (Person) super.clone();
        return newS;
    }

    @Override public String toString()
    {
        return getName( sFormat);
    }

    // ============ getters and setters ===========

    public String getFirst()
    {
        return fFirst;
    }

    public static int getFormat()
    {
        return sFormat;
    }

    public String getLast()
    {
        return fLast;
    }

    public String getSailorId()
    {
        return fSailorId;
    }

    public String getName()
    {
        return getName( sFormat);
    }

    public String getName( int format)
    {
        StringBuffer sb = new StringBuffer( 15);
        switch ( format)
        {
            case (FORMAT_FIRSTLAST):
                sb.append( fFirst);
                sb.append( " ");
                sb.append( fLast);
                break;
            case (FORMAT_LASTFIRST):
                sb.append( fLast);
                if (fFirst.length() > 0)
                {
                    sb.append( ", ");
                    sb.append( fFirst);
                }
                break;
            case (FORMAT_LASTONLY):
                sb.append( fLast);
                break;
        }
        return sb.toString();
    }

    private static String sHrefStart="http://www.sailing.org/bio.asp?ID=";

    public static void setHrefStart( String start)
    {
        sHrefStart = start;
    }

    public String toHtml()
    {
        if (fSailorId.length() == 0)
        {
            return getName( sFormat);
        }
        else
        {
            StringBuffer sb = new StringBuffer(30);
            sb.append( "<A target=isafid HREF=\"");
            sb.append( sHrefStart);
            sb.append( fSailorId);
            sb.append( "\">");
            sb.append( getName( sFormat));
            sb.append( "</a>");
            return sb.toString();
        }
    }

    public void setFirst(String s)
    {
        String hold = fFirst;
        fFirst = ( s == null) ? "" : s;
        firePropertyChange( FIRSTNAME_PROPERTY, hold, s);
    }

    public void setSailorId(String s)
    {
        String hold = fSailorId;
        fSailorId = ( s == null) ? "" : s;
        firePropertyChange( SAILORID_PROPERTY, hold, s);
    }

    public static void setFormat( int f)
    {
        //Integer hold = new Integer(sFormat);
        sFormat = f;
        //firePropertyChange( FORMAT_PROPERTY, hold, new Integer(f));
    }

    public static int getFormat( String str)
    {
        for (int i = 0; i < sFormatOptions.length; i++)
        {
            if (str.equalsIgnoreCase( sFormatOptions[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public static void setFormat( String str)
    {
        setFormat( getFormat( str));
    }

    public void setLast(String s)
    {
        String hold = fLast;
        fLast = ( s == null) ? "" : s;
        firePropertyChange( LASTNAME_PROPERTY, hold, s);
    }

    public void setName( String s)
    {
        // needs to be expanded, only supports harry's F. Last format
        int lastSpace = s.lastIndexOf(" ");
        if (lastSpace > -1)
        {
            setFirst( s.substring( 0, lastSpace));
            setLast( s.substring( lastSpace+1));
        }
        else
        {
            setFirst("");
            setLast( s);
        }
    }
}

/**
 * $Log: Person.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.10.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.10.2.1  2005/06/26 22:47:22  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.10  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.9  2004/01/13 02:35:39  sandyg
 * fixed unbalanced quote is isaf sailor id (bug #875560)
 *
 * Revision 1.8  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/03/28 03:07:50  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.6  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
