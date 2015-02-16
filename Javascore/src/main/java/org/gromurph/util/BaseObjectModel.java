//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseObjectModel.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import org.gromurph.xml.PersistentObject;

/**
 * The main interface that lets an object work with a standard superclass
 * of editors (BaseEditor) and lists (BaseList)
 */
public interface BaseObjectModel extends PersistentObject, Comparable
{
    //===  NOTE NOTE: these should be overridden in subclass that
    // want to have table model support
    public Object getColumnValue( int colIndex);
    public void setColumnValue( Object object, int colIndex);

    //public BaseEditor getEditor( BaseEditorContainer parent);

    /**
     * should return true if object is "blank"
     */
    public boolean isBlank();
}

/**
 * $Log: BaseObjectModel.java,v $
 * Revision 1.4  2006/01/15 21:10:34  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.5.2.1  2005/06/26 22:47:22  sandyg
 * Xml overhaul to remove xerces dependence
 *
 * Revision 1.5  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/03/28 03:07:50  sandyg
 * changed toxml and fromxml to xmlRead and xmlWrite
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
