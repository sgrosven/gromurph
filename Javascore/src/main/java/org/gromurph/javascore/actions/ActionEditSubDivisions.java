package org.gromurph.javascore.actions;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.util.BaseList;
import org.gromurph.util.BaseObjectModel;

/**
 * Brings up the dialog to edit divisions. This parent class is subclassed
 * to edit starting divisions, master divisions, fleets and subdivisions
 */
public class ActionEditSubDivisions extends ActionEditDivisions {

	public ActionEditSubDivisions() {
		super( "subdivisions", "ActionEditSubDivisionsTitle", "ActionEditSubDivisionsMnemonic");
	}

	@Override public BaseList<? extends BaseObjectModel> getList() {
		if (JavaScoreProperties.getRegatta() != null) {
			return JavaScoreProperties.getRegatta().retrieveSubDivisionsForEditing();
		} else {
			return null;
		}
	}
}

