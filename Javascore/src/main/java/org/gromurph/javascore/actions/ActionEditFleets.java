package org.gromurph.javascore.actions;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.BaseList;
import org.gromurph.util.BaseObjectModel;

/**
 * Brings up the dialog to edit divisions. This parent class is subclassed
 * to edit starting divisions, master divisions, fleets and subdivisions
 */
public class ActionEditFleets extends ActionEditDivisions {

	public ActionEditFleets() {
		super( "fleets", "ActionEditFleetsTitle", "ActionEditFleetsMnemonic");
	}

	@Override public BaseList<? extends BaseObjectModel> getList() {
		if (JavaScoreProperties.getRegatta() != null) {
			return JavaScoreProperties.getRegatta().retrieveFleetsForEditing();
		} else {
			return null;
		}
	}
}

