//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Xmlable.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

/**
 * The main interface that lets an object work with a standard superclass
 * of editors (BaseEditor) and lists (BaseList)
 */
public interface Xmlable
{
    public void xmlRead( org.w3c.dom.Node n, Object rootObject);
    public org.w3c.dom.Node xmlWrite( org.w3c.dom.Document doc);
}

/**
 * $Log: Xmlable.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.1.4.2  2005/11/19 20:34:55  sandyg
 * last of java 5 conversion, created swingworker, removed threads packages.
 *
 * Revision 1.1.2.1  2005/08/19 01:51:19  sandyg
 * Change to standard java xml libraries
 *
*/
