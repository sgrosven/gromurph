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

import org.gromurph.javascore.gui.DialogStageEditor;
import org.gromurph.javascore.model.StageList;
import org.gromurph.javascore.model.scoring.MultiStage;
import org.gromurph.javascore.model.scoring.Stage;

public class ActionEditStages extends ActionShowEditor<Stage> {
	
	public ActionEditStages() {
		super( "stages", "RegattaButtonStages", "RegattaButtonEditStagesMnemonic");
		this.putValue( HELP_TOPIC, "regatta.fButtonStages");
	}

	@Override
	public StageList getList() {
		if (getRegatta().isMultistage()) {
			return ( (MultiStage) getRegatta().getScoringManager()).getStages();
		} else {
			return null;
		}
	} 
	
	public void setScoringManager( MultiStage mgr) {
		DialogStageEditor dialog = (DialogStageEditor) getDialog();
		dialog.setScoringManager(mgr);
	}
	
	@Override public JDialog initializeEditor(JFrame rootParent) {
		DialogStageEditor dialog = new DialogStageEditor( rootParent);
		dialog.setSize(640,  400);
		return dialog;
	}
	
}
/**
 * $Log: ActionEditDivisions.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg
 */

