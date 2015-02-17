// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionExport.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
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
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.filechooser.FileFilter;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Exporter;
import org.gromurph.util.Util;

public class ActionExport extends AbstractAction {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	Regatta fRegatta = null;

	public ActionExport() {
		super(res.getString("MenuExport"));
		Object mn = new Integer(res.getString("MenuExportMnemonic").charAt(0));
		putValue(Action.MNEMONIC_KEY, mn);
	}

	public void actionPerformed(ActionEvent parm1) {
		try {
			String outfile = selectFileDialog();

			if (outfile != null) {

				Regatta regatta = JavaScoreProperties.getRegatta();
				if (regatta == null) return;
				
				Exporter exporter = null;
				if (Util.getLastFilter() instanceof IsafFileFilter) {
					exporter = new Xrr1_3Manager();
				} else if (regatta.isMultistage()) {
					//exporter = new ExportTabSeparatedMultiStage();
					return; // multi stage export not supported in 7.2.2
				} else {
					exporter = new ExportTabSeparatedSingleStage();
				}

				exporter.setFilename(outfile);
				exporter.export( regatta);
			}
		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

	/**
	 * 
	 * deterfItemnes the path to a file. This can be used either for a load or a save.
	 * 
	 * @param mode
	 *            Either LOAD or SAVE
	 * @param title
	 *            title for the dialog
	 * @param startDirectory
	 *            the directory that the file dialog should start it
	 * @return true if user approved new path, or false if user cancelled change
	 * 
	 **/
	protected String selectFileDialog() {
		String filename = null;
		if (fRegatta == null) fRegatta = JavaScoreProperties.getRegatta();
		if (fRegatta == null) return null;

		filename = fRegatta.getSaveName();
		if (filename == null) {
			filename = Util.makeUnicodeIdentifier(fRegatta.getName());
		} 
		if (filename.contains(".")) {
			filename = filename.substring(0, filename.indexOf("."));
		}
		if (Util.getLastFilter() instanceof TabFileFilter) {
			filename = filename + ".txt";
		} else { // works for isaf export, and if last choosen was something else
			filename = filename + ".xml";
		}

		String chosenFilename = Util.selectFile(filename, 
			new FileFilter[] { new IsafFileFilter(), new TabFileFilter() }, 
			res.getString("ActionExportMessageSelectExportFile"),
			false, true);

		if (chosenFilename == null)
			return null;

		if (Util.getLastFilter() instanceof IsafFileFilter) {
			if (!chosenFilename.endsWith(".xml")) {
				if (chosenFilename.endsWith(".txt")) {
					chosenFilename = chosenFilename.substring(0, chosenFilename.length() - 4);
				}
				chosenFilename += ".xml";
			}
		}
		return chosenFilename;
	}

	class TabFileFilter extends javax.swing.filechooser.FileFilter {

		@Override public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String ext = getExtension(f);
			String[] extensions = new String[] { "asc", "csv", "prn", "txt", "tab" };
			for (int e = 0; e < extensions.length; e++) {
				if (ext.equals(extensions[e]))
					return true;
			}
			return false;
		}

		@Override public String getDescription() {
			return res.getString("ActionExportLabelTextFileTypes"); // "Text Files (*.txt,*.csv,*.tab,*.asc,*.prn)";
		}

		private String getExtension(File f) {
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1)
				return s.substring(i + 1).toLowerCase();
			return "";
		}
	}

	class IsafFileFilter extends javax.swing.filechooser.FileFilter {

		@Override public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String ext = f.getName();
			boolean acc = ext.toLowerCase().endsWith(".xml");
			return acc;
		}

		@Override public String getDescription() {
			return res.getString("ActionExportLabelXmlFileTypes");
		}
	}

	public static void main(String[] args) {
		ActionExport ae = new ActionExport();
		try {
			Regatta reg = RegattaManager.readTestRegatta(
					"US_SAILING's_Rolex_Miami_OCR_-_470.regatta");
		} catch (Exception e) {}
		ae.actionPerformed(null);
	}

}
/**
 * $Log: ActionExport.java,v $ Revision 1.7 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.6 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.5 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.12 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.11 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.10 2004/04/30 12:22:49 sandyg Bug 945027 fixed, tabs out of whack and was not propertly sorting by
 * divisions
 * 
 * Revision 1.9 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.8 2004/01/17 22:27:37 sandyg First cut at unlimited number of crew, request 512304
 * 
 * Revision 1.7 2003/05/07 01:17:00 sandyg removed unneeded method parameters
 * 
 * Revision 1.6 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
