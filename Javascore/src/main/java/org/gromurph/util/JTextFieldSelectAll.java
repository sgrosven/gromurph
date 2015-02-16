// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: JTextFieldSelectAll.java,v 1.7 2006/07/09 03:01:24 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.Format;

import javax.swing.*;

/**
 * Extends JTextField to automatically select all the text in the field on gaining focus. Also adds in the
 * tablecelleditor methods
 * 
 * If not formatter present, can use either actionPerformed or propertyChange to save field to object
 * 
 * If adding a formatter, use propertyChange deal with modifications
 * 
 * DONT NEED separate focus lost check anymore!
 */
public class JTextFieldSelectAll extends JFormattedTextField implements FocusListener {
	public JTextFieldSelectAll() {
		super();
		localConstructor();
	}

	public JTextFieldSelectAll(Format format) {
		super(format);
		localConstructor();
	}

	public JTextFieldSelectAll(int columns) {
		this(); //("", columns); - in java 6... causes the "insert" to be permanently disabled
		localConstructor();
		setColumns(columns);
	}

	private void localConstructor() {
		addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		selectAll();
	}

	public void focusLost(FocusEvent e) {
		fireActionPerformed();
	}

	@Override public void setText(String text) {
		super.setText(text);
		if (hasFocus())
			selectAll();
	}

	public static class CellEditor extends DefaultCellEditor {
		public CellEditor() {
			super(new JTextFieldSelectAll());
		}

		//Override to invoke setValue on the formatted text field.
		@Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JTextFieldSelectAll ftfx = (JTextFieldSelectAll) super.getTableCellEditorComponent(table, value,
					isSelected, row, column);
			ftfx.setValue(value);
			return ftfx;
		}
	}

}
/**
 * $Log: JTextFieldSelectAll.java,v $ Revision 1.7 2006/07/09 03:01:24 sandyg removed minor deprecation warnings
 * 
 * Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:10:35 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/14 14:40:35 sandyg added some @suppresswarnings on warnings that I could not code around
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.5.4.1 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks on
 * text fields in panels.
 * 
 * Revision 1.5 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.4 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.3 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
