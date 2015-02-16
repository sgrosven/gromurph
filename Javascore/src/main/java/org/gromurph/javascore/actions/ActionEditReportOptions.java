// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionEditPreferences.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.gui.DialogReportTabs;
import org.gromurph.util.BaseObjectModel;
import org.gromurph.util.Util;

/**
 * Brings up the PanelPreference dialog to edit the master JavaScore preferences.
 */
public class ActionEditReportOptions extends ActionShowEditor {

	public ActionEditReportOptions() {
		super( "reportOptions", "MenuEditReportOptions","MenuEditReportOptionsMnemonic");

		// no help topic?
	}

	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogReportTabs dialog = new DialogReportTabs( rootParent, false);
		dialog.setLocation(Util.getLocationToCenterOnScreen(dialog));
		return dialog;
	}
	
	@Override public BaseObjectModel getObject() {
		return null;	
	}
	

	//fItemReportOptions.setEnabled(haveRegatta && !fRegatta.isFinal());

}
