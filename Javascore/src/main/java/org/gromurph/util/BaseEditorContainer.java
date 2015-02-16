//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseEditorContainer.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
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
 * Interface to be implemented by classes who want to "contain" a BaseEditor
**/
public interface BaseEditorContainer
{
    /**
     * tells container that some actions have been performed in editor
    **/
    void eventOccurred( BaseEditor editor, java.util.EventObject event);


    /**
     * tells container to check/revise what of its fields should be enabled
     */
    void updateEnabled();
    
    void setTitle( String t);
}
/**
 * $Log: BaseEditorContainer.java,v $
 * Revision 1.4  2006/01/15 21:10:34  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
