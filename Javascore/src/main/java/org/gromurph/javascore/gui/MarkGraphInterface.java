//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: MarkGraphInterface.java,v 1.4 2006/01/15 21:10:40 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

public interface MarkGraphInterface {

	/**
	 * return the mark rounding position for the specifed entry index and mark
	 * number should return -1 if no position is available The entrynumber is by
	 * order of finish
	 */
	public long getMarkPosition(int entrynumber, String markName);

	/**
	 * the number of intermediate marks
	 */
	public int getNumMarks();

	/**
	 * the name of the e'th entry (finish order wise)
	 */
	public String getEntryName(int entrynumber);

	/**
	 * the number of entries
	 */
	public int getNumEntries();

	/**
	 * returns a finish as a string
	 */
	public String getFinishString(int entrynumber);

}
/**
 * $Log: MarkGraphInterface.java,v $ Revision 1.4 2006/01/15 21:10:40 sandyg
 * resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.4 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.3 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
