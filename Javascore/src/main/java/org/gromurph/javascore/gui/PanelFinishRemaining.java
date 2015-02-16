// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRace.java,v 1.10 2006/05/19 05:48:42 sandyg Exp
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.PanelStartStop;

/**
 * The Race class handles a single Race. It has covering information about the race and a list of Finishes for the race
 **/
public class PanelFinishRemaining extends PanelStartStop {
	static ResourceBundle res = JavaScoreProperties.getResources();

	public PanelFinishRemaining() {
		super();
		addFields();
	}

	private void addFields() {
		this.setLayout(new BorderLayout());
	}

	@Override public void updateFields() {
		this.revalidate();
	}

	@Override public void start() {
	}

	@Override public void stop() {
	}

}
/**
 * $Log: PanelRace.java,v $ Revision 1.10 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
