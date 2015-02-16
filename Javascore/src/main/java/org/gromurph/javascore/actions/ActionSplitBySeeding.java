// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionSplitFleet.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.gui.PanelSplitQualifying;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;

/**
 * Brings up the PanelPreference dialog to edit the master JavaScore preferences.
 */
public class ActionSplitBySeeding extends ActionShowEditor<Regatta> {

	public ActionSplitBySeeding() {
		super( "splitBySeeding", "MenuSplitBySeeding","MenuSplitBySeedingMnemonic");
//		setEnabled(getRegatta() != null);
	}

	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogBaseEditor dialog = new DialogBaseEditor(rootParent, res.getString("SplitBySeedingTitle"), false);
		dialog.setSize(new Dimension(200, 400));
		return dialog;
	}
	
	@Override
	public BaseEditor getBaseEditor(BaseEditorContainer parentDialog) {
		return new PanelSplitQualifying( parentDialog);
	}

	@Override
	public Regatta getObject() {
		return JavaScoreProperties.getRegatta();
	}


}
/**
 * $Log: ActionSplitFleet.java,v $
 * 
 */
