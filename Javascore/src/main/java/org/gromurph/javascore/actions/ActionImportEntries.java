//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionImportEntries.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.gui.DialogImportEntries;
import org.gromurph.javascore.gui.DialogImportTable;

public class ActionImportEntries extends ActionShowEditor  {

    public ActionImportEntries() {
        super( "importEntries", "MenuImportEntries","MenuImportEntriesMnemonic");
    }

	@Override public JDialog initializeEditor(JFrame rootParent) {
        DialogImportTable dialog = new DialogImportEntries( rootParent);
        return dialog;
    }
	
}
/**
 * $Log: ActionImportEntries.java,v $
 * Revision 1.5  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:42  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.11.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.11  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.10  2003/04/27 21:03:29  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.9  2003/01/04 17:33:05  sandyg
 * Prefix/suffix overhaul
 *
*/
