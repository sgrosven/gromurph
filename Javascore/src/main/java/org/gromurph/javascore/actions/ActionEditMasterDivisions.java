package org.gromurph.javascore.actions;

import org.gromurph.javascore.model.DivisionList;
import org.gromurph.util.BaseList;
import org.gromurph.util.BaseObjectModel;

/**
 * Brings up the dialog to edit divisions. This parent class is subclassed
 * to edit starting divisions, master divisions, fleets and subdivisions
 */
public class ActionEditMasterDivisions extends ActionEditDivisions {

	public ActionEditMasterDivisions() {
		super( "divisionlist", "ActionEditMasterDivisionsTitle", "ActionEditMasterDivisionsMnemonic");
	}

	@Override public BaseList<? extends BaseObjectModel> getList() {
		return DivisionList.getMasterList();
	}
}

