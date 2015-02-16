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

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.Util;

public class ActionEditRegatta extends ActionShowEditor<Regatta> {
	
	public ActionEditRegatta() {
		super( "regatta", "MainButtonRegattaTitle", null);
		this.putValue( Action.NAME, res.getString("MainButtonRegatta"));
		this.putValue( HELP_TOPIC, "main.buttonRegatta");
	}

	@Override
	public Regatta getObject() {
		return JavaScoreProperties.getRegatta();
	}
	
	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogBaseEditor dialog = new DialogBaseEditor(rootParent, false);
		dialog.setLocation(Util.getLocationToCenterOnScreen(dialog));
		return dialog;
	}
	
	@Override 
	public void actionPerformed(ActionEvent notused) {
		if (getObject() == null) {
			JavaScore.getInstance().openRegatta(null);
		} else {
			super.actionPerformed(notused);
		}
	}

}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

