package org.gromurph.javascore.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryCrewTable extends JTable implements FocusListener {

	static ResourceBundle res = JavaScoreProperties.getResources();

	private class SelectionModelSkipCol0 extends DefaultListSelectionModel {
		public SelectionModelSkipCol0() { super(); }

		private int not0(int col) { return (col == 0) ? 1 : col; }

		@Override public void setAnchorSelectionIndex(int anchorIndex) {
			super.setAnchorSelectionIndex(not0(anchorIndex));
		}

		@Override public void setLeadSelectionIndex(int anchorIndex) {
			super.setLeadSelectionIndex(not0(anchorIndex));
		}

		@Override public void setSelectionInterval(int i0, int i1) {
			super.setSelectionInterval(not0(i0), not0(i1));
		}
	}

	private class EntryCrewTableModel extends AbstractTableModel {
		
		private Entry fEntry;
		
		public void setEntry( Entry e) {
			fEntry = e;
		}

		public int getRowCount() {
			if (fEntry == null)
				return 0;
			return 2 + fEntry.getNumCrew();
		}

		public int getColumnCount() {
			return 4;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (fEntry == null)
				return "<null>";

			if (rowIndex <= fEntry.getNumCrew()) {

				Person crew = getPerson(rowIndex);

				switch (columnIndex) {
				case 0:
					if (rowIndex == 0)
						return "Skipper";
					else
						return "Crew " + Integer.toString(rowIndex);
				case 1:
					return crew.getLast();
				case 2:
					return crew.getFirst();
				case 3:
					return crew.getSailorId();
				default:
					return "<ciob>";
				}

			} else if (columnIndex == 0) {
				return "*";
			} else {
				return "";
			}
		}

		private Person getPerson(int rowIndex) {
			if (fEntry == null)
				return null;
			if (rowIndex == 0) {
				return fEntry.getSkipper();
			} else if (rowIndex > 0) {
				return fEntry.getCrew(rowIndex - 1);
			} else {
				return null;
			}
		}

		@Override public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (fEntry == null)
				return;
			Person crew = getPerson(rowIndex);
			switch (columnIndex) {
			case 1:
				crew.setLast((String) value);
				break;
			case 2:
				crew.setFirst((String) value);
				break;
			case 3:
				crew.setSailorId((String) value);
				break;
			default:
				break;
			}
		}

		@Override public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "";
			case 1:
				return res.getString("EntryLabelLastName");
			case 2:
				return res.getString("EntryLabelFirstName");
			case 3:
				return res.getString("EntryLabelSailorId");
			default:
				return "<ciob>";
			}
		}

		@Override public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex > 0); // first col not editable, the rest are
		}
	}

	private final static int COLWIDTH_NUM = 45;
	private final static int COLWIDTH_LAST = 90;
	private final static int COLWIDTH_FIRST = 80;
	private final static int COLWIDTH_ISAFID = 60;

	public EntryCrewTable() {
		super();
		
		setModel( new EntryCrewTableModel());

		// setDefaultEditor( String.class, new
		// JTextFieldSelectAll.CellEditor());
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
		getColumnModel().setSelectionModel(new SelectionModelSkipCol0());

		DefaultTableCellRenderer col0renderer = new DefaultTableCellRenderer();
		col0renderer.setBackground(Color.lightGray);
		col0renderer.setMaximumSize(new Dimension(50, col0renderer.getPreferredSize().height));
		getColumnModel().getColumn(0).setCellRenderer(col0renderer);

		DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
		rend.setBackground( new Color( 240, 240, 240));
		getColumnModel().getColumn(1).setCellRenderer(rend);
		getColumnModel().getColumn(2).setCellRenderer(rend);
		getColumnModel().getColumn(3).setCellRenderer(rend);
		
		DefaultCellEditor strEditor = new JTextFieldSelectAll.CellEditor();
		setDefaultEditor(String.class, strEditor);

		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		getColumnModel().getColumn(0).setPreferredWidth(COLWIDTH_NUM);
		getColumnModel().getColumn(1).setPreferredWidth(COLWIDTH_LAST);
		getColumnModel().getColumn(2).setPreferredWidth(COLWIDTH_FIRST);
		getColumnModel().getColumn(3).setPreferredWidth(COLWIDTH_ISAFID);

	}

	public void setEntry( Entry e) {
		saveChanges();
		((EntryCrewTableModel) getModel()).setEntry(e);
	}
	/**
	 * catches lingering changes if user leaves without tabbing out of last cell
	 */
	public void saveChanges() {
		int row = getEditingRow();
		int col = getEditingColumn();

		if (row >= 0 && col >= 0) {
			try {
				JTextField c = (JTextField) getEditorComponent();
				String newText = c.getText();
				Object oldText = getValueAt(row,col);
				if (!newText.equals(oldText)) {
					logger.debug("saveChanges, row={}, col={}, old={}, new={}", row, col, oldText, newText);
					getModel().setValueAt( newText, row, col);
				}
			} catch (ClassCastException e) {
				// hit unexpected exit from penalty field, lands here					
			}
			//editingStopped(null);
			removeEditor();
		}
	}

	protected Logger logger = LoggerFactory.getLogger( this.getClass());

	public void focusGained(FocusEvent e) {
		// does nothing
	}

	public void focusLost(FocusEvent e) {
		logger.trace("PanelEntry.JTableCrew.focusLost");
		saveChanges();
	}
}
