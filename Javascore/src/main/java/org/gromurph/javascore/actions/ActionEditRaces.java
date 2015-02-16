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

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.gui.DialogRaceEditor;
import org.gromurph.util.BaseObjectModel;

public class ActionEditRaces extends ActionShowEditor {
	
	public ActionEditRaces() {
		super( "races", "MainButtonRace", null);
		this.putValue( HELP_TOPIC, "main.buttonRaces");
//		if (!Util.isMac()) {
//			putValue( Action.SMALL_ICON, 
//				Util.getImageIcon(this, JavaScoreProperties.FINISHES_ICON));
//			putValue( Action.LARGE_ICON_KEY, 
//					Util.getImageIcon(this, JavaScoreProperties.FINISHES_ICON));
//		}
	}

	@Override
	public org.gromurph.util.BaseList<? extends BaseObjectModel> getList() {
		return JavaScoreProperties.getRegatta().getRaces();
	}
	
	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogRaceEditor dialog = new DialogRaceEditor( rootParent);
		return dialog;
	}
	
//	@Override
//	public boolean isEnabled() {
//		if (!super.isEnabled()) return false;
//		if (JavaScoreProperties.getRegatta() == null) return false;
//		return true;
//	}
}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

