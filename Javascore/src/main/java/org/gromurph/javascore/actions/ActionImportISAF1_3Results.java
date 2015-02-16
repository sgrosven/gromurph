// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionImportResults.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;

public class ActionImportISAF1_3Results extends AbstractAction {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = org.gromurph.util.Util.getResources();

	public ActionImportISAF1_3Results() {
		super(res.getString("MenuImportISAFResults") + "...");
		putValue(Action.MNEMONIC_KEY, new Integer(res.getString("MenuImportISAFResultsMnemonic").charAt(0)));
	}

	public void actionPerformed(ActionEvent parm1) {
		String startDir = Util.getWorkingDirectory();
		String filename = RegattaManager.selectOpenFileDialog(res.getString("MenuImportISAFResults"), startDir, ".xml");

		// create new regatta
		Xrr1_3Manager xrr = new Xrr1_3Manager();
		try {
			xrr.setFilename(filename);
			Regatta r = xrr.parse();
			JavaScoreProperties.setRegatta(r);
			JavaScore.getInstance().regattaSave();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
/**
 * $Log: ActionImportResults.java,v $
 */
