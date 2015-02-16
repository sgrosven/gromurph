package org.gromurph.javascore.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Race;

public class UnfinishEntryRenderer extends DefaultListCellRenderer {

	private Race fRace;

	public Race getRace() {
		return fRace;
	}

	public void setRace(Race r) {
		this.fRace = r;
	}

	public UnfinishEntryRenderer() {
		super();
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		String entryLabel = getEntryLabel((Entry) value);
		return super.getListCellRendererComponent(list, entryLabel, index, isSelected, cellHasFocus);
	}

	private String getEntryLabel(Entry entry) {
		int flag = Entry.SHOW_BOW + Entry.SHOW_BOAT + Entry.SHOW_SKIPPER;
		if (JavaScoreProperties.getRegatta() != null && JavaScoreProperties.getRegatta().isMultistage()) {
			flag += Entry.SHOW_SUBDIVISION;
		} else {
			flag += Entry.SHOW_DIVISION;
		}

		return entry.toString(flag, false, fRace);
	}

}
