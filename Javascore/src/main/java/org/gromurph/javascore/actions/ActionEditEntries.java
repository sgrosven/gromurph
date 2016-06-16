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

import org.gromurph.javascore.gui.DialogEntryTreeEditor;
import org.gromurph.javascore.model.Entry;
import org.gromurph.util.HelpManager;

public class ActionEditEntries extends ActionShowEditor<Entry> {
	
	public ActionEditEntries() {
		super( "entries", "MainButtonEntries", "MainButtonRegattaMnemonic");
		this.putValue( HELP_TOPIC, "main.buttonEntries");
	}

	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogEntryTreeEditor dialog = new DialogEntryTreeEditor(rootParent);
		dialog.setSize(1000, 480);
		HelpManager.getInstance().registerHelpTopic(dialog, "entrylist");
		return dialog;
	}
	
}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

