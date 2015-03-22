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

import org.gromurph.javascore.model.scoring.ScoringOptions;
import org.gromurph.javascore.model.scoring.SingleStageScoring;
import org.gromurph.util.BaseObjectModel;
import org.gromurph.util.DialogBaseEditor;

public class ActionEditScoringOptions extends ActionShowEditor {
	
	public ActionEditScoringOptions() {
		super( "scoringOptions", "RegattaButtonScoringOptions", "RegattaButtonEditScoringOptionsMnemonic");
		this.putValue( HELP_TOPIC, "regatta.fButtonScoringOptions");
	}

	@Override public BaseObjectModel getObject() {
		if (getRegatta().isMultistage()) return null;
		else return (SingleStageScoring) getRegatta().getScoringManager();
	};
	
	@Override public JDialog initializeEditor(JFrame rootParent) {
		JDialog dialog = new DialogBaseEditor<ScoringOptions>( rootParent);
		dialog.setSize(640,  400);
		return dialog;
	}
	
}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

