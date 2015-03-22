// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DivisionList.java,v 1.5 2006/01/15 21:10:37 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.gromurph.javascore.model.ratings.RatingMorc;
import org.gromurph.javascore.model.ratings.RatingMultihull;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.util.BaseList;
import org.gromurph.util.Util;

/**
 * a list of Divisions. Currently with the simplified division, not much to do here
 */
public class DivisionList extends BaseList<Division> {
	@Override
	public Class getContainingClass() {
		return Division.class;
	}

	private static final long serialVersionUID = 1L;

	private static DivisionList sMasterList;

	public static void initializeMasterList() {
		sMasterList = new DivisionList();
		sMasterList.setFileName("divisions.ini");
		sMasterList.setRootTag("Divisions");
		sMasterList.setElementTag("Div");
		
		boolean gotItAsXml = false;
		try {
			gotItAsXml = sMasterList.xmlReadFromFile();
		} catch (Exception e1) { 
			gotItAsXml = false;
		}
		boolean gotIt = gotItAsXml;
		if (!gotItAsXml) try {
			sMasterList.loadFromDisk(sMasterList.getFileName());
			gotIt = true;
		} catch (IOException e2) {
			gotIt = false;
		}
		if (!gotIt) createNewMasterList();
		if (!gotItAsXml) {
			try {
				sMasterList.xmlWriteToFile();
			}
			catch (IOException e3) {
				Util.printlnException(sMasterList, e3, true);
			}
		}
	}

	private static void createNewMasterList() {
		
		// must not have divisions.ini file, make the default
		sMasterList.add(new Division("J22"));
		sMasterList.add(new Division("J24"));
		sMasterList.add(new Division("J80"));
		sMasterList.add(new Division("J30"));
		sMasterList.add(new Division("J105"));
		sMasterList.add(new Division("J35"));
		sMasterList.add(new Division("Farr30"));
		sMasterList.add(new Division("Melges24"));
		sMasterList.add(new Division("Alberg30"));
		sMasterList.add(new Division("Triton"));
		sMasterList.add(new Division("Cal25"));
		sMasterList.add(new Division("Cat27"));
		sMasterList.add(new Division("Etchells"));

		// updated 12/31/2011 for 2012, based on http://www.phrfchesbay.com/splits99.htm
		sMasterList.add(new Division("PHRF A0", new RatingPhrf(-9999), new RatingPhrf(25)));
		sMasterList.add(new Division("PHRF A1", new RatingPhrf(26), new RatingPhrf(70)));
		sMasterList.add(new Division("PHRF A2", new RatingPhrf(71), new RatingPhrf(109)));
		sMasterList.add(new Division("PHRF A", new RatingPhrf(-9999), new RatingPhrf(109)));
		sMasterList.add(new Division("PHRF B", new RatingPhrf(110), new RatingPhrf(145)));
		sMasterList.add(new Division("PHRF C", new RatingPhrf(146), new RatingPhrf(9999)));
		sMasterList.add(new Division("MORC", new RatingMorc(-9999), new RatingMorc(9999)));
		sMasterList.add(new Division("Multihull", new RatingMultihull(0), new RatingMultihull(999)));
		
	}

	public Division find(String name) {
		for (Division r : this) {
			if (r.getName().equals(name)) return r;
		}
		return null;
	}

	public static final DivisionList getMasterList() {
		if (sMasterList == null) initializeMasterList();
		return sMasterList;
	}

	public void loadFromDisk(String inFile) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(new File(inFile)));
		String line = reader.readLine();

		while (line != null) {
			if (line.length() > 0) add(new Division(line));
			line = reader.readLine();
		}
		reader.close();
	}

	/**
	 * looks throught the list of divisions and returns a list of the unique rating systems
	 * 
	 * @return list of unique ratings systems
	 */
	public List<String> getDistinctRatingSystems() {
		ArrayList<String> vSystems = new ArrayList<String>();
		for (Division div : this) {
			String aName = div.getSystem();
			if (!vSystems.contains(aName)) {
				vSystems.add(aName);
			}
		}
		return vSystems;
	}

}
