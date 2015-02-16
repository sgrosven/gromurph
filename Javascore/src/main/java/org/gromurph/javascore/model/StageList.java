// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: StageList.java,v 1.4 2006/01/15 21:10:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.BaseList;

public class StageList extends BaseList<Stage> {
	
	public StageList() {
		super();
	}
	public StageList( StageList s) {
		super();
		this.addAll(s);
	}
	
	@Override
	public Class getContainingClass() {
		return Stage.class;
	}

	private static final long serialVersionUID = 1L;

	public Stage find(String name) {
		for (Stage sdiv : this) {
			if (sdiv.getName().equals(name))
				return sdiv;
		}
		return null;
	}
	

}
/**
 * $Log: StageList.java,v $
 */
