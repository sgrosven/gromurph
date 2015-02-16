// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogDivisionListEditor.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseObject;
import org.gromurph.util.DialogBaseListEditor;
import org.gromurph.util.HelpManager;

/**
 * Dialog for editing BaseLists and their contained BaseObjects
 **/
public class DialogDivisionListEditor extends DialogBaseListEditor {
	String fHelpKey = "divisionlist";

	@Override protected void fButtonHelp_actionPerformed() {
		HelpManager.getInstance().registerHelpTopic(this, fHelpKey);
		super.fButtonHelp_actionPerformed();
	}

	static ResourceBundle res = JavaScoreProperties.getResources();

	public DialogDivisionListEditor() {
		this(null, "", true);
	}

	public DialogDivisionListEditor(JFrame parent) {
		this(parent, "", true);
	}

	public DialogDivisionListEditor(JFrame parent, boolean modal) {
		this(parent, "", modal);
	}

	public DialogDivisionListEditor(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);
	}

	public void setHelpKey(String key) {
		fHelpKey = key;
	}

	@Override protected void addObject(BaseObject newObject) {
		super.addObject(newObject);
		Regatta regatta = JavaScoreProperties.getRegatta();
		if (regatta == null) return;
		
		if (!(newObject instanceof AbstractDivision)) return;
		AbstractDivision div = (AbstractDivision) newObject;
		
		for (Race r : regatta.getRaces()) {
			r.setIsRacing(div, false);
		}
	}

	@Override public void windowClosing(WindowEvent event) {
		super.windowClosing(event);
		try {
			DivisionList.getMasterList().xmlWriteToFile();
			JavaScore.backgroundSave();
		}
		catch (IOException e) {

		}
	}

	@Override protected void setObject(BaseObject obj) {
		super.setObject(obj);
		if (getEditor() instanceof PanelDivision) {
			((PanelDivision) getEditor()).setIsMasterDivision(true);
		}
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		DialogDivisionListEditor dialog = new DialogDivisionListEditor(null);
		dialog.setMasterList(DivisionList.getMasterList(), "Master");
		dialog.setVisible(true);
	}
}
/**
 * $Log: DialogDivisionListEditor.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.13.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.13.2.1 2005/06/26 22:47:22 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.13 2005/04/27 02:46:14 sandyg fixed help pointer so shows fleets and subdivisions when appropriate
 * 
 * Revision 1.12 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.11 2003/04/27 21:05:58 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.10 2003/03/28 03:07:51 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.9 2003/03/28 01:23:50 sandyg Request #704967, xml division list tag changed to FleetDivList
 * 
 * Revision 1.8 2003/03/27 02:47:00 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 * 
 * Revision 1.7 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
