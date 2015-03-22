//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogStageEditor.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.Dimension;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.StageList;
import org.gromurph.javascore.model.scoring.MultiStageScoring;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.DialogBaseListEditor;
import org.gromurph.util.Util;

/**
 * Dialog for editing BaseLists and their contained BaseObjects
 **/
public class DialogStageEditor extends DialogBaseListEditor<Stage, StageList> {
	private static final String EMPTY = "";
	static ResourceBundle res = JavaScoreProperties.getResources();

	public DialogStageEditor(JFrame parent) {
		super(parent, res.getString("RegattaButtonStages"), false);
	}

	@Override protected Dimension getPreferredEditorSize() {
		return new Dimension(500, 400);
	}

	@Override protected int getPreferredListWidth() {
		return 150;
	}

	private MultiStageScoring scoringManager;
	
	public void setScoringManager( MultiStageScoring mgr) {
		scoringManager = mgr;
		StageList sl = mgr.getStages();
		Collections.sort( sl, new Stage.SortBottomToTop());
		setMasterList( sl, res.getString( "DialogStagesTitle"));
	}
	
	@Override protected void addObject(Stage stage) {
		if (scoringManager != null) scoringManager.addStage(stage);
		super.addObject(stage);
	}

	/**
	 * for standalone testing only
	 **/
	public static void main(String[] args) {
		Util.setTesting(true);
		Regatta reg = null;
		try {
			reg = RegattaManager.readTestRegatta("2011MiamiOCRLaser.regatta");
		} catch (Exception e) {
			Util.showError(e, true);
		}

		JavaScore.initializeEditors();
		
		MultiStageScoring mgr = (MultiStageScoring)reg.getScoringManager();
		DialogStageEditor panel = new DialogStageEditor(null);
		
		StringBuffer sb = new StringBuffer("Stages: ");
		sb.append(reg.getName());

		panel.setMasterList(mgr.getStages(), sb.toString());
		panel.setVisible(true);
	}

}
/**
 * $Log: DialogStageEditor.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final
 */
