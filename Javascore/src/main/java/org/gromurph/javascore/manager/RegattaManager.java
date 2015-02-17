package org.gromurph.javascore.manager;

import java.awt.Frame;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.Constants.ScoreCarryOver;
import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SeriesPoints;
import org.gromurph.javascore.model.SeriesPointsList;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.scoring.MultiStage;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegattaManager {
	private static ResourceBundle res = null;
	private static ResourceBundle resUtil = null;

	static {
		res = JavaScoreProperties.getResources();
		resUtil = Util.getResources();
	}

	private Regatta regatta;

	public RegattaManager(Regatta r) {
		regatta = r;
	}

	public static Regatta readTestRegatta(String inFile) throws IOException {
		return readRegattaFromDisk( "testregattas/" + inFile);
	}

	public static Regatta readRegattaFromDisk(String inFile) throws IOException {
		File file = Util.getFile( inFile);
		
		// try reading as Xml file
		if (file.exists()) {
			Regatta reg = new Regatta();
			JavaScoreProperties.setRegatta(reg);
			
    		boolean readOK = reg.xmlReadFromReader(new FileReader(file));
    
    		if (readOK) {
    			reg.setSaveName(file.getName());
    			reg.setSaveDirectory(file.getParent());
    			return reg;
    		} else {
    			JavaScoreProperties.setRegatta(null);
    			return null;
    		}
		} else {
			logger.error("No file found: " + inFile);
			return null;
		}
	}

	/**
	 * writes regatta to disk using regatta internal directory and name
	 * 
	 * @throws IOException
	 *             if unable to write to disk
	 */
	public void writeRegattaToDisk() throws IOException {
		String fileDir = regatta.getSaveDirectory();
		String fileName = regatta.getSaveName();
		writeRegattaToDisk(fileDir, fileName);
	}

	/**
	 * writes regatta to disk using regatta internal directory and name
	 * 
	 * @throws IOException
	 *             if unable to write to disk
	 */
	public void writeRegattaToDisk(String fileDir, String fileName) throws IOException {

		if (!fileDir.endsWith("/"))
			fileDir = fileDir + "/";

		try {
			JavaScoreProperties.acquireScoringLock();
			regatta.xmlWriteToFile(fileDir, fileName, "Regatta");
		} finally {
			JavaScoreProperties.releaseScoringLock();
		}

		regatta.setSaveName(fileName);
		regatta.setSaveDirectory(fileDir);

	}

	public void scoreRegatta() {
		try {
			
			regatta.getScoringManager().validate();

			JavaScoreProperties.acquireScoringLock();
			try {
				logger.trace("ScoringManager: scoring started...");
				if (regatta == null || regatta.getNumRaces() == 0 || regatta.getNumEntries() == 0) {
					logger.trace("ScoringManager: (empty) done.");
					return;
				}

				regatta.getScoringManager().scoreRegatta();

				logger.trace("ScoringManager: scoring completed.");

			}
			finally {
				JavaScoreProperties.releaseScoringLock();
			}
		}
		catch (Exception e) {
			Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Exception=" + e.toString(), e);
		}
	}	

	protected static Logger logger = LoggerFactory.getLogger( "RegattaManager");
	
	public void splitFleetTopBottom(Stage srcStage, AbstractDivision targetParentDiv, String[] newDivisionNames,
			int[] topPositions, boolean wantCarryOverRace) {
		
		MultiStage mgr = (MultiStage) regatta.getScoringManager();
		AbstractDivision parentDiv = (targetParentDiv instanceof SubDivision) ? targetParentDiv.getParentDivision() : targetParentDiv;

		SubDivision[] subDivs = new SubDivision[newDivisionNames.length];

		Race carryoverRace = null;
		if (wantCarryOverRace) {
			carryoverRace = new Race( regatta, Integer.toString( regatta.getRaces().size()+1) + "/co");
		}
		
		// create new divisions and stages
		for (int n = 0; n < newDivisionNames.length; n++) {
			String newName = newDivisionNames[n];
			
			if (!newName.equals( res.getString(Constants.NOADVANCE))) {
			
				SubDivision div = regatta.getSubDivision( newName);
				if (div == null) {
         			// create division
        			// if "parentdiv" is itself a subdivision, the new subdiv should have same parent's parent
        			div = new SubDivision(newDivisionNames[n], parentDiv);
        			regatta.addSubDivision(div);
				}
     			subDivs[n] = div;

    			// roll through previous races, set the "not racing" flag for these new divisions
    			// may not be necessary
    			for (Race r : regatta.getRaces()) {
    				r.setIsRacing(div, false);
    			}

				// lookup stage
				Stage stage = mgr.getStage(newName);
				if (stage == null) {
					// stage does not exist, create it
	    			int stageRank = srcStage.getStageRank() + 10 - n;
	    			stage = new Stage( mgr);
	    			stage.setName( newName);
	    			stage.setPrevStage( srcStage);
	    			stage.setStageRank( stageRank);
	    			mgr.addStage(stage);
	    			stage.getModel().getOptions().setAttributes(
	    					srcStage.getModel().getOptions());
				}    			
    			stage.getDivisions().add(div);
    			
    			if (wantCarryOverRace) {
    				carryoverRace.setIsRacing( div,  true);
    				carryoverRace.setStartDate( new Date());
    				carryoverRace.setStartTime(div,  new Date().getTime());
    				carryoverRace.setComment("Carryover Race based on boats positions in previous stage");
    				stage.setScoreCarryOver( ScoreCarryOver.SEEDINGRACE);
    			}
   			
			} 
			
		}
		
		if (wantCarryOverRace) {
			regatta.addRace(carryoverRace);
		}

		// now place the entries in new subdivisions
		SeriesPointsList spList;
		if (targetParentDiv instanceof SubDivision) spList = srcStage.getAllSeriesPoints().findAllInSubDivision( (SubDivision)targetParentDiv);
		else spList = mgr.getAllSeriesPoints().findAll(targetParentDiv);
		
		spList.sortPosition();
		// position if a subdivision may not be accurate (subdiv within larger div)
		// so use order based on position
		for ( int s = 0; s < spList.size(); s++) {
			SeriesPoints sp = spList.get(s);
			if (sp != null) {
    			SubDivision finalDiv = null;
    			for (int n = 0; n < topPositions.length; n++) {
     				if (s+1 >= topPositions[n]) {
    					if (n < subDivs.length) finalDiv = subDivs[n];
    					else finalDiv = null;
    				}
    			}
    			if (finalDiv != null) {
    				finalDiv.addEntry( sp.getEntry());
    				if (wantCarryOverRace) {
    					Finish f = new Finish( carryoverRace, sp.getEntry(), SailTime.NOTIME, 
    							new FinishPosition( finalDiv.getNumEntries()), null);
    					carryoverRace.setFinish(f);
    				}
    			}
			}
		}
		
		if (wantCarryOverRace) regatta.scoreRegatta();
	}

	public void splitFleetSeedByPosition(Stage stage, AbstractDivision parentDiv, int fNumSplits, String[] newDivisionNames) {
		// create new qualifying divisions
		AbstractDivision newDivParent = parentDiv;
		SubDivision[] subDivs = new SubDivision[fNumSplits];

		for (int n = 0; n < fNumSplits; n++) {
			SubDivision div = new SubDivision(newDivisionNames[n], newDivParent);
			div.setGroup(SubDivision.QUALIFYING);
			subDivs[n] = div;
			regatta.addSubDivision(div);
			if (stage != null) stage.getDivisions().add( div);

			// roll through previous races, set the "not racing" flag for these new divisions
			for (Race r : regatta.getRaces()) {
				r.setIsRacing(div, false);
			}

		}

		// create list of entries by series position
		EntryList entries = regatta.getAllEntries().findAll(parentDiv);
		SeriesPointsList splist = new SeriesPointsList();

		for (Entry entry : entries) {
			SeriesPoints sp = stage.getStageSeriesPoints( entry, parentDiv); // regatta.getScoringManager().getSeriesPoints(entry, parentDiv);
			splist.add(sp);
		}

		splist.sortPosition();

		// roll through the entries by fleet position and spread them through
		// the divisions
		int n = 0;
		int increment = 1;
		for (Iterator iter = splist.iterator(); iter.hasNext();) {
			SeriesPoints points = (SeriesPoints) iter.next();
			Entry e = points.getEntry();

			SubDivision nextDiv = subDivs[n];
			nextDiv.addEntry(e);

			// update div for next entry
			n = n + increment;
			if (n == subDivs.length) {
				increment = -1;
				n--;
			} else if (n < 0) {
				increment = +1;
				n++;
			}
		}
	}

	public static final int LOAD = 0;
	public static final int SAVE = 1;

	public static String selectOpenRegattaDialog(String title, String startDirectory) {
		return selectOpenFileDialog(title, startDirectory, ".regatta");
	}

	public static String selectSaveRegattaDialog(String title, Regatta regatta) {
		if (regatta == null)
			return null;
		String extension = ".regatta";
		String startDirectory = regatta.getSaveDirectory();

		// Create default filename for saving
		String filename = null;

		filename = regatta.getSaveName();
		if ((filename == null) || filename.equals(Regatta.NONAME)) {
			filename = Util.makeUnicodeIdentifier(regatta.getName());
		}

		if (!(filename.toLowerCase().endsWith(extension))) {
			filename += extension;
		}

		return selectSaveFileDialog(title, startDirectory, extension, filename);
	}

	public static String selectOpenFileDialog(String title, String startDirectory, String extension) {
		fFileChooser = getFileChooser();

		if (startDirectory == null)
			startDirectory = Util.getWorkingDirectory();
		fFileChooser.setCurrentDirectory(new File(startDirectory));
		fFileChooser.setDialogTitle(title);

		int result = fFileChooser.showOpenDialog(sChooserFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			String fileName = fFileChooser.getSelectedFile().getName();
			String directory = fFileChooser.getSelectedFile().getParent();

			if ((directory != null) && (fileName != null)) {
				Util.setWorkingDirectory(directory);
				return fileName;
			}

			return null;
		}
		return null;
	}

	public static String selectSaveFileDialog(String title, String startDirectory, String extension, String startName) {
		fFileChooser = getFileChooser();

		if (startDirectory == null)
			startDirectory = Util.getWorkingDirectory();
		fFileChooser.setCurrentDirectory(new File(startDirectory));
		fFileChooser.setDialogTitle(title);

		fFileChooser.setSelectedFile(new File(startName));
		int result = fFileChooser.showSaveDialog(sChooserFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			String fileName = fFileChooser.getSelectedFile().getName();
			String directory = fFileChooser.getSelectedFile().getParent();

			if ((directory != null) && (fileName != null)) {
				// Automatically add extension if none entered
				if (!(fileName.toLowerCase().endsWith(extension))) {
					fileName += extension;
				}

				File selection = new File(directory + "/", fileName);
				if (selection.exists()) {
					// File already exists. Prompt for overwrite
					if (!confirmFileReplace(selection)) {
						return null;
					}
				}

				Util.setWorkingDirectory(directory);
				return fileName;
			}

			return null;
		}
		return null;
	}

	private static boolean confirmFileReplace(File f) {
		int option = JOptionPane.showConfirmDialog(JavaScore.getInstance(), MessageFormat.format(resUtil
				.getString("GenOverwriteMessage"), new Object[] { f.getPath() }), res.getString("GenOverwriteTitle"),
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			f.delete();
			return true;
		}
		return false;

	}

	private static Frame sChooserFrame;

	public static JFileChooser getFileChooser() {
		if (fFileChooser == null) {
			try {
				sChooserFrame = new Frame();
				JFileChooser chooser = new JFileChooser();
				if (chooser != null) {
					chooser.setFileFilter(new RegattaFileFilter());
				}
				fFileChooser = chooser;
			} catch (Exception e) {
				Util.showError(e, true);
			}
		}
		return fFileChooser;
	};

	private static JFileChooser fFileChooser = null;
	
	public static class RegattaFileFilter extends javax.swing.filechooser.FileFilter {
		@Override public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String extension = getExtension(f);
			return (extension.equals("regatta"));
		}

		@Override public String getDescription() {
			return res.getString("RegattaMessageList");
		}

		private String getExtension(File f) {
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1)
				return s.substring(i + 1).toLowerCase();
			return "";
		}
	}

}
