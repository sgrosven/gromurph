//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SortedJTable.java,v 1.5 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

// Original code:
// Written by Kong Eu Tak for Swing Connection Article
// Email: konget@cheerful.com
// Homepage: http://www.singnet.com.sg/~kongeuta/

package org.gromurph.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;


/**
 * A JTable that allows the user to sort the table by clicking on the
 * column headers.  An icon is shown in the sort column to indicate the
 * sort.  Ascending and descending sorting is supported.
 * <P>
 * Sorting depends on the java.lang.Comparable interface.  SortedJTable
 * will attempt to compare the cell contents using Comparable.  If the
 * contents are not Comparable, then their toString() values are compared.
 * Note that all the Java primitive wrapper objects (Integer, Double, etc)
 * implement Comparable, so you'll get better sorting (1, 5, 10 instead of
 * 1, 10, 5) if you use, for example, Integers instead of Strings.
 *
 * <P>This code was developed by NASA, Goddard Space Flight Center, Code 588
 * for the Scientist's Expert Assistant (SEA) project.
 *
 * @version		10/27/99
 * @author		J. Jones / 588
 * @see			java.lang.Comparable
**/
public class SortedJTable extends JTable implements MouseListener
{
	private AbstractTableModel	fRealModel;
	private int					fColumnToSort;
	private int					fSortType;
	private int					fMapToSorted[];
	private int					fNumColumns;

	// These are here (instead of in CustomHeaderRenderer) so that they can be static.
	// They're static so that they're only loaded once.
	private static ImageIcon	sAscendingIcon = null;
	private static ImageIcon	sDescendingIcon = null;

	/**
	 * Ascending sort type.
	**/
	public static final int		ASCENDING = 0;

	/**
	 * Descending sort type.
	**/
	public static final int		DESCENDING = 1;


	/**
	 * Constructs a JTable which is initialized with model as the data model,
	 * a default column model, and a default selection model.
	 * Note that SortedJTable takes an AbstractTableModel instead of a
	 * TableModel because the fireTableChanged() method is required.
	 * Since DefaultTableModel is an AbstractTableModel, this should be a
	 * minor limitation.
	 *
	 * @param	model	the data model for the table
	**/
	public SortedJTable(AbstractTableModel model)
	{
		super();

		fRealModel = model;

		fMapToSorted = null;

		// Default to ascending sort on column 0
		fColumnToSort = 0;
		fSortType = ASCENDING;

		// Insert the model wrapper which translates to the sorted rows
		setModel(new ModelWrapper());

		fNumColumns = getColumnCount();

		// Install the header renderers
		for (int i = 0; i < fNumColumns; ++i)
		{
			setCustomHeaderRenderer(i);
		}

		// Install the listener that will automatically sort when new cells are added
		fRealModel.addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				// Automatically resort since the table data has changed
				doSort();

				// If number of columns has changed, reinstall the custom column headers
				if (e.getColumn() > fNumColumns || e.getColumn() == TableModelEvent.ALL_COLUMNS)
				{
					fNumColumns = getColumnCount();

					// Reinstall the header renderers if necessary,
					// preserving the tool tip text of the original header renderer
					for (int i = 0; i < fNumColumns; ++i)
					{
						setCustomHeaderRenderer(i);
					}
				}
			}
		});

		getTableHeader().addMouseListener(this);
        setSortColumn(0);
	}

	/**
	 * Returns the column number of the current sort column.
	 *
	 * @return	current sort column number
	**/
	public int getSortColumn()
	{
		return fColumnToSort;
	}

	/**
	 * Sets the sort column.  The table will be resorted.
	 *
	 * @param	column	new sort column
	**/
	public void setSortColumn(int column)
	{
		if (column < 0 || column >= getColumnCount())
		{
			throw new IllegalArgumentException("Column number is out of range.");
		}

		fColumnToSort = column;

		sortAndUpdate();
	}

	/**
	 * Returns whether the current sort is ASCENDING or DESCENDING.
	 *
	 * @return	ASCENDING or DESCENDING
	**/
	public int getSortType()
	{
		return fSortType;
	}

	/**
	 * Sets the sort type.  Valid values are ASCENDING and DESCENDING.
	 *
	 * @param	type	new sort type
	**/
	public void setSortType(int type)
	{
		if (type != ASCENDING && type != DESCENDING)
		{
			throw new IllegalArgumentException("Invalid sort type (must be ASCENDING or DESCENDING).");
		}

		fSortType = type;

		sortAndUpdate();
	}

	/**
	 * Performs the sort.
	**/
	@SuppressWarnings("unchecked") protected void doSort()
	{
		int i, j, k;

		if (fMapToSorted == null || (fMapToSorted.length < fRealModel.getRowCount()))
		{
			fMapToSorted = new int[((fRealModel.getRowCount()/50)+1)*50];
		}

		for (i = 0; i < fRealModel.getRowCount(); i++)
		{
			fMapToSorted[i] = i;
		}

		Object a;
		Object b;

		boolean moreToSort = true;

		while (moreToSort)
		{
			moreToSort = false;

			for (i=0; i < fRealModel.getRowCount()-1; i++)
			{
				a = fRealModel.getValueAt(fMapToSorted[i],fColumnToSort);
				b = fRealModel.getValueAt(fMapToSorted[i+1],fColumnToSort);

				if (a instanceof Comparable && b instanceof Comparable)
				{
					j = ((Comparable) a).compareTo(b);
				}
				else
				{
					j = a.toString().compareTo(b.toString());
				}

				if ((fSortType == ASCENDING && j>0) || ((fSortType == DESCENDING) && j<0))
				{
					k=fMapToSorted[i];

					fMapToSorted[i]=fMapToSorted[i+1];

					fMapToSorted[i+1]=k;

					moreToSort=true;
				}
			}
		}
	}

	public void mouseEntered(MouseEvent m)
	{
	}

	public void mouseExited(MouseEvent m)
	{
	}

	public void mousePressed(MouseEvent m)
	{
	}

	public void mouseReleased(MouseEvent m)
	{
	}

	/**
	 * If user clicks on column header, perform sort and toggle sort ordering
	 * if the sort column was already the sort column.
	**/
	public void mouseClicked(MouseEvent m)
	{
		int targetCol = convertColumnIndexToModel(getTableHeader().columnAtPoint(m.getPoint()));

		if (targetCol == fColumnToSort)
		{
			fSortType = (fSortType == ASCENDING) ? DESCENDING : ASCENDING;
        }
		else
		{
			fColumnToSort = targetCol;

			fSortType = ASCENDING;
		}

		sortAndUpdate();
	}

	/**
	 * Sorts the table, notifies the model, and redraws the header.
	**/
	protected void sortAndUpdate()
	{
		doSort();

		fRealModel.fireTableChanged(new TableModelEvent(fRealModel));

		getTableHeader().repaint();
	}

	/**
	 * Assigns a new CustomHeaderRenderer to a column,
	 * preserving state of the existing header renderer.
	 *
	 * @param	i	set header for this column
	**/
	protected void setCustomHeaderRenderer(int i)
	{
		if (!(getColumn(getColumnName(i)).getHeaderRenderer() instanceof CustomHeaderRenderer))
		{
			CustomHeaderRenderer newHeader = new CustomHeaderRenderer();

			// Preserve the ToolTipText of the existing header renderer
			if (getColumn(getColumnName(i)).getHeaderRenderer() instanceof DefaultTableCellRenderer)
			{
				newHeader.setToolTipText(((DefaultTableCellRenderer) getColumn(getColumnName(i)).getHeaderRenderer()).getToolTipText());
			}

			getColumn(getColumnName(i)).setHeaderRenderer(newHeader);
		}
	}

	/**
	 * Wraps the original table model by mapping rows to their
	 * sorted row equivalents.
	**/
	protected class ModelWrapper implements TableModel
	{
		public void addTableModelListener(TableModelListener l)
		{
			fRealModel.addTableModelListener(l);
		}

		public Class<?> getColumnClass(int index)
		{
			return fRealModel.getColumnClass(index);
		}

		public int getColumnCount()
		{
			return fRealModel.getColumnCount();
		}

		public String getColumnName(int index)
		{
			return fRealModel.getColumnName(index);
		}

		public int getRowCount()
		{
			return fRealModel.getRowCount();
		}

		public Object getValueAt(int row, int col)
		{
			return fRealModel.getValueAt(fMapToSorted[row], col);
		}

		public boolean isCellEditable(int row, int col)
		{
			return fRealModel.isCellEditable(fMapToSorted[row], col);
		}

		public void removeTableModelListener(TableModelListener l)
		{
			fRealModel.removeTableModelListener(l);
		}

		public void setValueAt(Object value, int row, int col)
		{
			fRealModel.setValueAt(value, fMapToSorted[row], col);
		}
	}

	/**
	 * Renders a header with the appropriate sort icon.
	**/
	protected class CustomHeaderRenderer extends DefaultTableCellRenderer
	{
		public CustomHeaderRenderer()
		{
			super();

			setHorizontalAlignment(SwingConstants.CENTER);
			setHorizontalTextPosition(SwingConstants.RIGHT);
			setVerticalTextPosition(SwingConstants.CENTER);

			if (sAscendingIcon == null)		sAscendingIcon = Util.getImageIcon(this, Util.ASCENDSORT_ICON);
			if (sDescendingIcon == null)	sDescendingIcon = Util.getImageIcon(this, Util.DESCENDSORT_ICON);
		}

		@Override public Component getTableCellRendererComponent(
				JTable table, Object value, boolean selected, boolean hasFocus, int row, int col)
		{
	        JTableHeader header = table.getTableHeader();
	        if (header != null)
	        {
	            setForeground(header.getForeground());
	            setBackground(header.getBackground());
	            setFont(header.getFont());
	        }

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			if (table.convertColumnIndexToModel(col) == fColumnToSort)
			{
				setIcon((fSortType == ASCENDING) ? sAscendingIcon : sDescendingIcon);
			}
			else
			{
				setIcon(null);
			}

			return this;
		}
	}
}
/**
 * $Log: SortedJTable.java,v $
 * Revision 1.5  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/14 14:40:35  sandyg
 * added some @suppresswarnings on warnings that I could not code around
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.6.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.6  2005/05/26 01:45:43  sandyg
 * fixing resource access/lookup problems
 *
 * Revision 1.5  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/04/27 21:35:33  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.3  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
