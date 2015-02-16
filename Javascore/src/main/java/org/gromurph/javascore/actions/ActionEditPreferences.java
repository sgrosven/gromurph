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

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.DialogBaseEditor;

/**
 * Brings up the PanelPreference dialog to edit the master JavaScore preferences.
 */
public class ActionEditPreferences extends ActionShowEditor<JavaScoreProperties> {

	public ActionEditPreferences() {
		super( "preferences", "MenuPreferences","MenuPreferencesMnemonic");

		// make sure menuitem has ... on end
		String label = (String) getValue( Action.NAME);
		if (!label.endsWith("...")) label += "...";
		putValue( Action.NAME, label);
		putValue( ActionShowEditor.PANEL_TITLE, res.getString("PrefsTitle"));
		
		// no help topic?
	}

	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogBaseEditor dialog = new DialogBaseEditor( 
				rootParent, 
				(String) getValue( ActionShowEditor.PANEL_TITLE), 
				false);
		return dialog;
	}
	
	@Override
	public JavaScoreProperties getObject() {
		return JavaScoreProperties.getProperties();
	}

}
/**
 * $Log: ActionEditPreferences.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.7.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.7 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.6 2003/04/27 21:35:31 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.5 2003/03/19 02:38:50 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.4 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
