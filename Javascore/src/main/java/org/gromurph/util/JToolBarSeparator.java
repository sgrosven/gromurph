//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: JToolBarSeparator.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Component;
import java.awt.Dimension;

public class JToolBarSeparator extends Component
{
	/** 
	 * Create the separator 
	*/
	public JToolBarSeparator()
	{
	}

	/** 
	 * Return the minimum size for the separator
	 *
	 * @return the Dimension object containing the separator's
	 *         minimum size
	 */
	@Override public Dimension getMinimumSize()
	{
		return new Dimension(10, 5);
	}
	
	/** 
	 * Return the maximum size for the separator
	 *
	 * @return the Dimension object containing the separator's
	 *         maximum size
	 */
	@Override public Dimension getMaximumSize()
	{
		return new Dimension(10, 5);
	}
	
	/** 
	 * Return the preferred size for the separator
	 *
	 * @return the Dimension object containing the separator's
	 *         preferred size
	 */
	@Override public Dimension getPreferredSize()
	{
		return new Dimension(10, 5);
	}
}
/**
 * $Log: JToolBarSeparator.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.3  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
