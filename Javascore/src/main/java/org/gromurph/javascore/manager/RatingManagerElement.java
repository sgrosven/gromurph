//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingManagerElement.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.manager;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class RatingManagerElement extends BaseObject
{
    static java.util.ResourceBundle res = JavaScoreProperties.getResources();

    private static final String SYSTEM_PROPERTY = "System";
    private static final String RATING_PROPERTY = "RatingClass";

    private String fSystemName;
    private String fLongName;
    private String fRatingClass;

    public RatingManagerElement()
    {
        this( "", "");
    }

    public RatingManagerElement( String name, String ratingClass)
    {
        fSystemName = name;
        fRatingClass = ratingClass;
        if (name.length() > 0) fLongName = res.getString( "RatingName" + name);
    }

    public String getSystem() { return fSystemName;}
    public String getRatingClass() { return fRatingClass;}
    public String getLongName() { return fLongName;}

    @Override public boolean equals( Object obj)
    {
        if (this == obj) return true;
        try
        {
            RatingManagerElement that = (RatingManagerElement) obj;
            if ( !Util.equalsWithNull( this.fSystemName, that.fSystemName)) return false;
            if ( !Util.equalsWithNull( this.fRatingClass, that.fRatingClass)) return false;
            if ( !Util.equalsWithNull( this.fLongName, that.fLongName)) return false;
            return true;
        }
        catch (ClassCastException e)
        {
            return false;
        }
    }

    @Override public void xmlWrite( PersistentNode e)
    {
        e.setAttribute( SYSTEM_PROPERTY, fSystemName);
         e.setAttribute( RATING_PROPERTY, fRatingClass);
        //return e;
    }

    private final static String OLD_PACKAGESTART = "org.gromurph.javascore.Rating";
    
    private final static String NEW_PACKAGE = "org.gromurph.javascore.model.ratings";
   
    @Override public void xmlRead( PersistentNode n, Object rootObject)
    {
        String value = n.getAttribute( RATING_PROPERTY);
        if (value != null) {
        	fRatingClass = value;
        	if ( fRatingClass.startsWith( OLD_PACKAGESTART)) {
        		String className = fRatingClass.substring( fRatingClass.lastIndexOf('.')+1);
        		fRatingClass = NEW_PACKAGE + "." + className;
        	}        	
        }
      
        value = n.getAttribute( SYSTEM_PROPERTY);
        if (value != null) fSystemName = value;

        fLongName = res.getString( "RatingName" + fSystemName);
    }

    public int compareTo( Object o)
    {
        try
        {
            return fSystemName.compareTo( ((RatingManagerElement) o).fSystemName);
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
/**
 * $Log: RatingManagerElement.java,v $
 * Revision 1.5  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
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
