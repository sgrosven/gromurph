package org.gromurph.javascore.gui;

import java.awt.BorderLayout;

import javax.swing.JList;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Race;
import org.gromurph.util.PanelStartStop;

public class PanelSailDuplicate extends PanelStartStop {

	public PanelSailDuplicate() {
		super();
		addFields();
	}
	
	private Race fRace;

	private JList<Entry> fListDupes;
	private UnfinishEntryRenderer fRenderer = new UnfinishEntryRenderer();

	public void addFields() {
		this.setLayout( new BorderLayout());
		this.setSize( 200, 250);
		
		fListDupes = new JList<Entry>();
		fListDupes.setCellRenderer(fRenderer);
		fListDupes.setVisibleRowCount(4);
		
		this.add( fListDupes, BorderLayout.CENTER);
	}

	@Override
	public void updateFields() {}

	public Entry getSelectedEntry() {
		return fListDupes.getSelectedValue();
	}

	@Override public void start() {}
	@Override public void stop() {}

	public Race getRace() {
		return fRace;
	}

	public void setRace(Race r) {
		this.fRace = r;
		fRenderer.setRace(r);
	}
	
	public void setDuplicateEntries(EntryList dupeEntries) {
		if (dupeEntries != null) fListDupes.setModel(dupeEntries.getListModel());
		else fListDupes.setModel( null);
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
