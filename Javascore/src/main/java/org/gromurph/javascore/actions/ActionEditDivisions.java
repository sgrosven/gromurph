//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionEditDivisions.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
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

import org.gromurph.javascore.gui.DialogDivisionListEditor;
import org.gromurph.util.BaseList;
import org.gromurph.util.BaseObjectModel;

/**
 * Brings up the dialog to edit divisions. This parent class is subclassed to
 * edit starting divisions, master divisions, fleets and subdivisions
 */
public abstract class ActionEditDivisions extends ActionShowEditor {
	
	/**
	 * subclass should have constructor that calls this
	 */
	protected ActionEditDivisions( String nickname, String resTitle, String resMnem) {
		super(nickname, resTitle, resMnem);
	}

	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogDivisionListEditor dialog = new DialogDivisionListEditor( rootParent, false);
		dialog.setSize( 580, 400);
		BaseList<? extends BaseObjectModel> list = getList();
		if (list != null) {
			dialog.setHelpKey(fKey);
		}
		return dialog;
	}

}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 * final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.7.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.7 2005/04/27 02:46:14 sandyg fixed help pointer so shows fleets
 * and subdivisions when appropriate
 * 
 * Revision 1.6 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.5 2003/05/14 02:23:21 sandyg fixing double run of javascore
 * constructor, odd button enabling disabling. Javascore.sRegatta is back to
 * being static
 * 
 * Revision 1.4 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.3 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
