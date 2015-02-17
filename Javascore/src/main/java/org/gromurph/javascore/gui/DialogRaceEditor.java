//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogRaceEditor.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
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
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.util.BaseObject;
import org.gromurph.util.DialogBaseListEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.Util;

/**
 * Dialog for editing BaseLists and their contained BaseObjects
 **/
public class DialogRaceEditor extends DialogBaseListEditor {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private static final String EMPTY = "";

	public DialogRaceEditor(JFrame parent) {
		super(parent, EMPTY, false);
		HelpManager.getInstance().registerHelpTopic(this, "racelist");
		this.setSize(640, 450);
	}

	@Override protected Dimension getPreferredEditorSize() {
		return new Dimension(500, 400);
	}

	@Override protected int getPreferredListWidth() {
		return 150;
	}

	public void setRaces( RaceList races) {
		setMasterList(races, 
				MessageFormat.format(
						res.getString("MainButtonRacesTitleStart"), 
						new Object[] { JavaScoreProperties.getRegatta().toString() }));
		if (races.size() > 0) {
			this.setObject( races.get(0));
		}
	}
	
	@Override protected void addObject(BaseObject newObject) {
		Race r = (Race) newObject;
		r.setName(Integer.toString(fBaseList.size() + 1));
		super.addObject(r);
	}

//	@Override public void updateEnabled() {
//		if (fPanelObject == null) {
//			super.updateEnabled();
//		} else {
//			PanelRace pr = (PanelRace) fPanelObject;
//			if (pr.isSubWindowOpen()) {
//				fList.setEnabled(false);
//				fButtonAdd.setEnabled(false);
//				fButtonDelete.setEnabled(false);
//				fButtonRestore.setEnabled(false);
//			} else {
//				super.updateEnabled();
//			}
//		}
//	}

	/**
	 * for standalone testing only
	 **/
	public static void main(String[] args) {
		JavaScore.initializeEditors();

		try {
			Regatta reg = RegattaManager.readTestRegatta("0000-Test-Master.regatta");

			DialogRaceEditor panel = new DialogRaceEditor(null);
			StringBuffer sb = new StringBuffer("Races: ");
			sb.append(reg.getName());

			panel.setMasterList(reg.getRaces(), sb.toString());
			panel.setVisible(true);
		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

	@Override
	protected void fButtonAdd_actionPerformed() {
		super.fButtonAdd_actionPerformed();

		// in a qualifying regatta...
		// set the defaults for who's racing based on what makes sense
		Regatta regatta = JavaScoreProperties.getRegatta();
		if (regatta == null || !regatta.isMultistage())
			return;

		Race rr = ((Race) this.getCurrentObject());
		for (Division div : regatta.getDivisions()) {
			boolean haveQualDivs = false;

			// by default, only the finals groups are racing
			for (SubDivision subdiv : regatta.getSubDivisions()) {
				if (!subdiv.isGroupScoring() && div.contains(subdiv)) {
					rr.setIsRacing(subdiv, subdiv.isGroupQualifying());
					haveQualDivs = haveQualDivs
							|| subdiv.isGroupQualifying();
				}
			}
			rr.setIsRacing(div, !haveQualDivs);
		}
	}
}
/**
 * $Log: DialogRaceEditor.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet
 * scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.8 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.7 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.6 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.5 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
