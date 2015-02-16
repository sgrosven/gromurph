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

import org.gromurph.javascore.gui.DialogFinishListEditor;
import org.gromurph.javascore.model.Race;

public class ActionEditFinishes extends ActionShowEditor<Race> {
	
	public ActionEditFinishes() {
		super( "ActionEditFinishes", "RaceButtonEditFinishes", "RaceButtonEditFinishesMnemonic");
		this.putValue( HELP_TOPIC, "race.fButtonEditFinishes");
	}
	
	private Race race;
	private String mark;
	
	public void setRounding( Race r, String m) { 
		race = r;
		mark = m;
		if (getDialog() != null) {
			((DialogFinishListEditor) getDialog()).setRounding( race, mark);
		}
	}
	
	@Override
	public Race getObject() {
		return race;
	}
	
	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogFinishListEditor dialog = new DialogFinishListEditor(rootParent);
		dialog.setRounding( race, mark);
		return dialog;
	}
	
}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

