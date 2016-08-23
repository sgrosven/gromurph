// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogFinishListEditor.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.EventObject;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * Dialog for editing the finishes in a race
 **/
public class DialogFinishListEditor extends JDialog implements ListSelectionListener, MouseListener, ActionListener,
		WindowListener, Constants // , KeyListener
{
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	public static final String EMPTY = BaseEditor.EMPTY;
	public static final String NEWLINE = BaseEditor.NEWLINE;
	public static final String PERIOD = BaseEditor.PERIOD;

	private class FinishTableModel extends AbstractTableModel {
		FinishList finishList;

		public FinishTableModel(FinishList flist) {
			super();
			finishList = flist;
		}

		@Override public Class<?> getColumnClass(int c) {
			return Finish.getColumnClass(c);
		}

		public int getColumnCount() {
			return Finish.getColumnCount();
		}

		@Override public String getColumnName(int c) {
			return Finish.getColumnName(c);
		}

		public int getRowCount() {
			return finishList.size();
		}

		// Returns an attribute value for the cell at columnIndex and rowIndex.
		public Object getValueAt(int rowIndex, int columnIndex) {
			return finishList.get(rowIndex).getValueAt(columnIndex);
		}

		// Returns an attribute value for the cell at columnIndex and rowIndex.
		@Override public void setValueAt(Object obj, int rowIndex, int columnIndex) {
			finishList.get(rowIndex).setValueAt(obj, columnIndex);
		}

		@Override public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex > 0); // first col not editable, the rest are
		}
	}

	/**
	 * Jlist containing the unfinished entries
	 **/
	JList fListUnFinished;

	DefaultListModel<Entry> fModelUnFinished;

	/**
	 * list if finished boats (makes the tablemodel for fTableFinished
	 **/
	FinishList fFinishers;
	FinishTableModel fFinishModel;

	/**
	 * list of entries racing in a race
	 */
	EntryList fEntries;

	/**
	 * the jtable containing the current finishes
	 **/
	FinishJTable fTableFinished;

	/**
	 * race who's finishes are being edited
	 **/
	Race fRace;
	String fMarkName;

	/**
	 * the finish currently selected
	 **/
	Finish fCurrentFinish;
	int fCurrentRow;

	/**
	 * panel showing finish information on the current finish
	 **/
	PanelFinish fPanelFinish;

	JPanel fPanelUnFinished;
	JPanel fPanelFinishers;

	JFrame fParent;

	JButton fButtonInsert;
	JButton fButtonDelete;
	JButton fButtonFinishRemaining;
	JButton fButtonOk;
	JButton fButtonHelp;
	JButton fButtonFind;

	// JTextField fTextFieldPenalty;

	private final static int FINISH_COLUMN = 0;
	private final static int SAILID_COLUMN = 1;
	private final static int TIME_COLUMN = 2;
	private final static int PENALTY_COLUMN = 3;

	private boolean fIsRounding = false;
	
	private UnfinishEntryRenderer fUnfinishEntryRenderer = new UnfinishEntryRenderer();

	public DialogFinishListEditor(JFrame parent) {
		super(parent, false);
		
		fFinishers = new FinishList();
		fFinishModel = new FinishTableModel(fFinishers);

		setTitle(res.getString("FinishTitleFinishTable"));
		Dimension screenDim = getToolkit().getScreenSize();
		int width = Math.min(screenDim.width, 750);
		int height = Math.min(800, screenDim.height - 50);
		setSize(width, height);
		setLocation(new Point(Math.max(0, (screenDim.width - width) / 2), Math.max(10, (screenDim.height - height) / 2)));
		fParent = parent; // keep for yesno dialog
		HelpManager.getInstance().registerHelpTopic(this, "finish");

		getContentPane().setLayout(new BorderLayout(0, 0));

		fTableFinished = new FinishJTable();
		fTableFinished.setName("fTableFinished");
		HelpManager.getInstance().registerHelpTopic(fTableFinished, "finish.fTableFinished");
		fTableFinished.setToolTipText(res.getString("FinishTableToolTip"));
		// set these two into the appropriate visual component
		fTableFinished.setModel(fFinishModel);

		fPanelFinish = new PanelFinish(  );
		fPanelFinish.setToolTipText(res.getString("FinishPanelToolTip"));
		HelpManager.getInstance().registerHelpTopic(fPanelFinish, "finish.fPanelFinish");

		fModelUnFinished = new javax.swing.DefaultListModel<Entry>();// fUnFinishedEntries.getListModel();

		fListUnFinished = new JList();
		fListUnFinished.setName("fListUnFinished");
		fListUnFinished.setToolTipText(res.getString("FinishLabelUnfinishPanelToolTip"));
		fListUnFinished.setCellRenderer( fUnfinishEntryRenderer);
		HelpManager.getInstance().registerHelpTopic(fListUnFinished, "finish.fListUnFinished");
		fListUnFinished.setModel( fModelUnFinished);

		JPanel finishHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));

		finishHolder.setBorder(BorderFactory.createTitledBorder(res.getString("FinishTitleCurrentFinish")));
		finishHolder.add(fPanelFinish);

		fPanelUnFinished = new JPanel(new BorderLayout(0, 0));
		fPanelUnFinished.setBorder(BorderFactory.createTitledBorder(res.getString("FinishTitleNotYetFinished")));
		fPanelUnFinished.add(new JScrollPane(fListUnFinished), BorderLayout.CENTER);

		JSplitPane eastSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(finishHolder),
				fPanelUnFinished);
		// eastSplitPane.setDividerLocation(200);

		fPanelFinishers = new JPanel(new BorderLayout(0, 0));
		fPanelFinishers.setBorder(BorderFactory.createTitledBorder(res.getString("FinishTitleFinishList")));
		fPanelFinishers.add(new JScrollPane(fTableFinished), BorderLayout.CENTER);

		JSplitPane vertSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fPanelFinishers, eastSplitPane);
		vertSplitPane.setDividerLocation(400);
		getContentPane().add(vertSplitPane, BorderLayout.CENTER);

		fButtonInsert = new JButton(resUtil.getString("InsertButton"));
		if (!Util.isMac()) fButtonInsert.setIcon(Util.getImageIcon(this, Util.ROWINSERTBEFORE_ICON));
		fButtonInsert.setMnemonic(resUtil.getString("InsertMnemonic").charAt(0));
		fButtonInsert.setToolTipText(res.getString("FinishButtonInsertToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonInsert, "finish.fButtonInsert");

		fButtonDelete = new JButton(resUtil.getString("DeleteButton"));
		fButtonDelete.setName("fButtonDelete");
		if (!Util.isMac()) fButtonDelete.setIcon(Util.getImageIcon(this, Util.ROWDELETE_ICON));
		fButtonDelete.setMnemonic(resUtil.getString("DeleteMnemonic").charAt(0));
		fButtonDelete.setToolTipText(res.getString("FinishLabelDeleteFinisherToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonDelete, "finish.fButtonDelete");

		fButtonFind = new JButton(res.getString("FinishButtonFind"));
		if (!Util.isMac()) fButtonFind.setIcon(Util.getImageIcon(this, Util.FIND_ICON));
		fButtonFind.setMnemonic(res.getString("FinishButtonFindMnemonic").charAt(0));
		fButtonFind.setToolTipText(res.getString("FinishButtonFindToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonFind, "finish.fButtonDelete");

		JPanel panelButtons = new JPanel(new BorderLayout(0, 0));
		getContentPane().add(panelButtons, BorderLayout.SOUTH);

		JPanel panelFinishButtons = new JPanel(new FlowLayout());
		panelButtons.add(panelFinishButtons, BorderLayout.WEST);

		panelFinishButtons.add(fButtonInsert);
		panelFinishButtons.add(fButtonDelete);
		panelFinishButtons.add(fButtonFind);

		JPanel fPanelUnFinishedButtons = new JPanel(new FlowLayout());
		panelButtons.add(fPanelUnFinishedButtons, BorderLayout.EAST);

		fButtonHelp = new JButton(resUtil.getString("HelpButton"));
		if (!Util.isMac()) fButtonHelp.setIcon(Util.getImageIcon(this, Util.HELP_ICON));
		fButtonHelp.setMnemonic(resUtil.getString("HelpMnemonic").charAt(0));
		fPanelUnFinishedButtons.add(fButtonHelp);

		fButtonFinishRemaining = new JButton(res.getString("FinishButtonFinishRemaining"));
		fButtonFinishRemaining.setName("fButtonFinishRemaining");
		fButtonFinishRemaining.setMnemonic(res.getString("FinishButtonFinishRemainingMnemonic").charAt(0));
		fButtonFinishRemaining.setToolTipText(res.getString("FinishButtonFinishRemainingToolTip"));
		HelpManager.getInstance().registerHelpTopic(fButtonFinishRemaining, "finish.fButtonRemaining");
		fPanelUnFinishedButtons.add(fButtonFinishRemaining);

		fButtonOk = new JButton(resUtil.getString("OKButton"));
		fButtonOk.setName("fButtonOk");
		fButtonOk.setMnemonic(resUtil.getString("OKMnemonic").charAt(0));
		getRootPane().setDefaultButton(fButtonOk);
		fButtonOk.setToolTipText(res.getString("FinishButtonOKToolTip"));
		fPanelUnFinishedButtons.add(fButtonOk);

		initPenaltyDialog();
		start();
	}

	private void initPenaltyDialog() {
		fDialogPenalty = new DialogBaseEditor();
		fDialogPenalty.setModal(true);
		fDialogPenalty.setObject(new Penalty());
	}

	// August 2016 NOTE:  this dialog is intentionally designed to stay active when not visible!
	
	private void start() {
		fTableFinished.getSelectionModel().addListSelectionListener(this);
		fTableFinished.addMouseListener(this);

		fListUnFinished.addListSelectionListener(this);
		fListUnFinished.addMouseListener(this);

		fButtonInsert.addActionListener(this);
		fButtonDelete.addActionListener(this);
		fButtonFind.addActionListener(this);
		fButtonFinishRemaining.addActionListener(this);
		fButtonOk.addActionListener(this);
		fButtonHelp.addActionListener(this);
		addWindowListener(this);

		if (fFinishers.size() > 0) {
			fTableFinished.setRowSelectionInterval(0, 0);
			JViewport view = (JViewport) fTableFinished.getParent();
			view.setViewPosition(new Point(0, 0));
		}
		updateEnabled();
	}

	public void stop() {
		fTableFinished.getSelectionModel().removeListSelectionListener(this);
		fTableFinished.removeMouseListener(this);

		fListUnFinished.removeListSelectionListener(this);
		fListUnFinished.removeMouseListener(this);

		fButtonInsert.removeActionListener(this);
		fButtonDelete.removeActionListener(this);
		fButtonFind.removeActionListener(this);
		fButtonFinishRemaining.removeActionListener(this);
		fButtonOk.removeActionListener(this);
		fButtonHelp.removeActionListener(this);

		removeWindowListener(this);
		updateEnabled();
	}

	public void windowClosing(WindowEvent event) {
		if (event == null || event.getSource() == this) {
			saveFinishes();
			setVisible(false);
			if (fPanelRace != null) fPanelRace.updateEnabled();

			// test for regatta so unit tests dont barf on fake regattas
			if (JavaScoreProperties.getRegatta() != null) JavaScore.backgroundSave();
		}
	}

	PanelRace fPanelRace = null;

	public void setParentPanel(PanelRace pr) {
		fPanelRace = pr;
	}

	public void windowActivated(WindowEvent event) {} // do nothing
	public void windowDeactivated(WindowEvent event) {} // do nothing
	public void windowDeiconified(WindowEvent event) {} // do nothing
	public void windowIconified(WindowEvent event) {} // do nothing
	public void windowOpened(WindowEvent event) {} // do nothing
	public void windowClosed(WindowEvent event) {} // do nothing

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fButtonInsert) fButtonInsert_actionPerformed();
		else if (event.getSource() == fButtonDelete) fButtonDelete_actionPerformed();
		else if (event.getSource() == fButtonFind) fButtonFind_actionPerformed();
		else if (event.getSource() == fButtonFinishRemaining) fButtonFinishRemaining_actionPerformed();
		else if (event.getSource() == fButtonOk) fButtonExit_actionPerformed();
		else if (event.getSource() == fButtonHelp) fButtonHelp_actionPerformed();
		updateEnabled();
	}

	public void fButtonHelp_actionPerformed() {
		HelpManager.getInstance().setHelpTopic(this);
	}

	private void fButtonExit_actionPerformed() {
		int row = fTableFinished.getEditingRow();
		int col = fTableFinished.getEditingColumn();

		if (row >= 0 || col >= 0) {
			try {
				JTextField c = (JTextField) fTableFinished.getEditorComponent();
				String lastText = c.getText();
				fTableFinished.setValueAt(lastText, row, col);
				this.saveEditedCell(row);
			} catch (ClassCastException e) {
				// hit exit from penalty field, lands here
				fTableFinished.editingStopped(null);
			}
		}

		windowClosing(null);
	}

	private void fButtonInsert_actionPerformed() {
		try {
			if ( fModelUnFinished.size() == 0) {
				beep();
				JOptionPane.showMessageDialog(this, res.getString("FinishMessageInsertError"),
						res.getString("FinishTitleInsertError"), JOptionPane.WARNING_MESSAGE);
				return;
			}

			Finish insertedFinish = findLastEmptyFinish();

			int row = fTableFinished.getSelectedRow();
			if (row >= 0 && insertedFinish != null) {
				Finish base = fFinishers.get(row);
				long finPos = base.getFinishPosition().longValue();
				fFinishers.insertPosition(base);

				insertedFinish.setFinishPosition(new FinishPosition(finPos));
				insertedFinish.setFinishTime(SailTime.NOTIME);
				fFinishers.sortPosition();
				setCurrentFinish(insertedFinish);
				fTableFinished.emptyEditors();
				fFinishModel.fireTableDataChanged();
			}
		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

	private void fButtonDelete_actionPerformed() {
		try {
			int row = fTableFinished.getSelectedRow();
			if (row >= 0) {
				fTableFinished.editingStopped(null);
				Finish f = fFinishers.get(row);
				fTableFinished.emptyEditors();

				if (f.getEntry() != null) {
					fModelUnFinished.addElement(f.getEntry());
				}
				f.setEntry(null);
				f.setFinishPosition(new FinishPosition(fFinishers.size() + 2));
				f.getPenalty().setPenalty(NO_PENALTY);
				f.setFinishTime(SailTime.NOTIME);
				reorderFinishTable();
				
				setCurrentFinish(fFinishers.get(row));
			}
		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

	private void fButtonFind_actionPerformed() {
		if (fTableFinished.isEditing()) fTableFinished.editingCanceled(null);

		String newId = JOptionPane.showInputDialog(DialogFinishListEditor.this,
				res.getString("FinishMessageFindFinisher"), res.getString("FinishTitleFindFinisher"),
				JOptionPane.INFORMATION_MESSAGE);

		if (newId == null || newId.trim().length() == 0) return;

		EntryList eList = fEntries.findId(newId);
		if (eList.size() == 0) // no entries found
		{
			beep();
			JOptionPane.showMessageDialog(this,
					MessageFormat.format(res.getString("FinishMessageNoEntryError"), new Object[] { newId }),
					res.getString("FinishTitleNoEntry"), JOptionPane.WARNING_MESSAGE);
		} else // found at least one found entry
		{
			Entry entryToFind = eList.get(0);

			if (eList.size() > 1) {
				beep();
				dupePanel.setRace(fRace);
				dupePanel.setDuplicateEntries( eList);
				JOptionPane.showConfirmDialog(
						this, dupePanel, res.getString("FinishMessageMoreThanOnePossible"), 
						JOptionPane.OK_OPTION);
				entryToFind = dupePanel.getSelectedEntry();
			}

			Finish fin = fFinishers.findEntry(entryToFind);
			if (fin != null) setCurrentFinish(fin);
		}

	}
	
	private PanelSailDuplicate dupePanel = new PanelSailDuplicate();

	public void beep() {
		// getToolkit().beep();
	}

	public static String RES_FINISHREMAININGTITLE = res.getString("PenaltyDialogTitle");

	private void fButtonFinishRemaining_actionPerformed() {
		if (fTableFinished.isEditing()) fTableFinished.editingCanceled(null);

		Penalty[] penalties = Penalty.getAllNonFinishPenalties();
		beep();
		int result = JOptionPane.showOptionDialog(DialogFinishListEditor.this,
				res.getString("FinishMessageSelectPenalty"), RES_FINISHREMAININGTITLE,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, penalties, penalties[0]);
		if (result == JOptionPane.CLOSED_OPTION || result < 0) return;
		Penalty pen = penalties[result];

		if (pen != null) {
			long intPen = pen.getPenalty();
			for ( int i = 0; i < fModelUnFinished.size(); i++) {
			//for (Iterator<Entry> iter = fUnFinishedEntries.iterator(); iter.hasNext();) {
				Entry e = fModelUnFinished.get(i);
				Finish f = findFirstEmptyFinish();
				f.setEntry(e);
				f.setFinishPosition(new FinishPosition(intPen));
				f.setPenalty(new Penalty(intPen));
				fRace.setFinish(f);
			}
			fModelUnFinished.removeAllElements();
		}
		// reset the finish numbers
		fFinishers.reNumber();
		fFinishModel.fireTableDataChanged();
		fListUnFinished.repaint();
	}

	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource() == fTableFinished || event.getSource() == fTableFinished.getSelectionModel()) 
			fTableFinished_valueChanged(event);
		else if (event.getSource() == fListUnFinished) 
			fListUnFinished_valueChanged();
		updateEnabled();
	}

	public void updateEnabled() {
		if (fModelUnFinished.size() == 0) {
			// no unfinished entries
			fButtonInsert.setEnabled(false);
			fButtonDelete.setEnabled(true);
			fButtonFinishRemaining.setEnabled(false);
		} else {

			fButtonFinishRemaining.setEnabled(true);
			if (fCurrentFinish == null || fCurrentFinish.getEntry() == null) {
				fButtonDelete.setEnabled(false);
				fButtonInsert.setEnabled(false);
			} else {
				fButtonDelete.setEnabled(true);
				fButtonInsert.setEnabled(fCurrentFinish.getFinishPosition().isValidFinish());
			}
		}
	}

	public void mouseClicked(MouseEvent event) {
		int nClicks = event.getClickCount();
		if (event.getSource() == fListUnFinished) {
			if (nClicks == 2) {
				fListUnFinished_doubleClicked();
			}
		} else if (event.getSource() == fTableFinished) {
			fTableFinished_mouseClicked(event);
		}
		updateEnabled();
	}

	protected void fTableFinished_mouseClicked(MouseEvent event) {
		int row = fTableFinished.rowAtPoint(event.getPoint());
		int col = fTableFinished.columnAtPoint(event.getPoint());
		if (col == 0) return; // don't try to edit the "label" col

		fTableFinished.editCellAt(row, col);
		if (fTableFinished.getCellEditor() != null) {
			JComponent c = (JComponent) fTableFinished.getCellEditor().getTableCellEditorComponent(fTableFinished,
					fTableFinished.getValueAt(row, col), true, row, col);
			if (c != null) {
				//c.requestFocusInWindow();
				if (col < PENALTY_COLUMN) {
					((JTextField) c).selectAll();
				}
			}
		}
	}

	/**
	 * updates the currently editted cell if it happens to get changed out on us
	 */
	private void saveEditedCell(int index) {
		if (fTableFinished.getEditingRow() != index) return;
		if (fTableFinished.getEditingColumn() < 0) return;

		Component c = fTableFinished.getEditorComponent();
		Object cell = fTableFinished.getValueAt(fTableFinished.getEditingRow(), fTableFinished.getEditingColumn());

		int col = fTableFinished.getEditingColumn();
		if ((col == SAILID_COLUMN) || (col == FINISH_COLUMN)) {
			((JTextField) c).setText(cell.toString());
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	/**
	 * on "leaving" a race, runs through the finish vector removing out finishes with no entry
	 */
	protected void saveFinishes() {
		fFinishers.syncWithEntries(fRace);
		// reset the finish numbers
		fFinishers.reNumber();

		if (!fIsRounding) {
			for (Finish f : fFinishers) {
				fRace.setFinish(f);
			}
		}
	}

	/**
	 * makes the item specified the current object
	 **/
	protected void setRace(Race race) {
		setRounding( race, null);
	}

	/**
	 * sets the specified race and mark roundings into the dialog
	 **/
	public void setRounding(Race race, String markName) {
		stop();
		
		fRace = race;
		fUnfinishEntryRenderer.setRace( fRace);
		fMarkName = markName;
		
		if (fRace != null) {
			
			if (markName == null) {
    			setTitle(MessageFormat.format(res.getString("FinishTitle"), new Object[] { fRace.getName() }));
    			fIsRounding = false;
    			fRace.syncFinishesWithEntries();
    			updateFinishList( fRace.getAllFinishers());
			} else {
    			StringBuffer sb = new StringBuffer(res.getString("FinishRoundingDialogTitleStart"));
    			sb.append(" ");
    			sb.append(fRace.getName());
    			sb.append(", ");
    			sb.append(markName);
    			setTitle(sb.toString());
    			fIsRounding = true;
    			FinishList rounders = fRace.getRoundings(markName);
    			rounders.syncWithEntries(fRace);
    			updateFinishList( rounders);
			}
		} else {
			setTitle(MessageFormat.format(res.getString("FinishTitle"), new Object[] { "No race" }));
			fIsRounding = false;
			updateFinishList( null);			
		}
		
		start();
	}

	/**
	 * Updates finish and unfinish lists when a Race or Rounding list is initialized The input finishlist (should come
	 * from either setRace or setRounding) should contain all known valid finishers
	 **/
	private void updateFinishList(FinishList finishes) {

		fFinishers.clear();
		fModelUnFinished.removeAllElements();
		EntryList unfinished = new EntryList();

		if (fRace == null) {
			fEntries = new EntryList();
		} else {
			// This is a list of finishes for all entrants, need to check for non-finish penalties in this list
			fEntries = fRace.getEntries();

			if (finishes != null) {
	    		int nextFin = finishes.getNumberFinishers() + 1;
	    		for (Finish f : finishes) {
	    			if (f.getFinishPosition().longValue() == NOFINISH) {
	    				// if is a non-finish, put the entry in unfinishlist
	    				// change the Finish object to blank entry and finishpos to end of list
	    				unfinished.add(f.getEntry());
	    				f.setEntry(null);
	    				f.setFinishPosition(new FinishPosition(nextFin++));
	    				f.getPenalty().setPenalty(NO_PENALTY);
	    			}
	    			// add the finish to the main finish list
	    			fFinishers.add( f);
	    		}
			}

			// set the list of non finished entries
			fFinishers.sortPosition();
			unfinished.sortSailId();
			for (Entry e : unfinished) fModelUnFinished.addElement(e);

			// set col heading to reflect use of bow numbers or not
			TableColumn eCol = fTableFinished.getColumnModel().getColumn(SAILID_COLUMN);
			if (fRace.getRegatta().isUseBowNumbers()) {
				eCol.setHeaderValue(res.getString("GenBowSail"));
			} else {
				eCol.setHeaderValue(res.getString("FinishColumnSailOnly"));
			}

			eCol = fTableFinished.getColumnModel().getColumn(TIME_COLUMN);
			if (fIsRounding) {
				try {
					fTableFinished.removeColumn(fTableFinished.getColumnModel().getColumn(PENALTY_COLUMN));
				} catch (Exception e) {} // if bombs then, we already killed it

				eCol.setHeaderValue(res.getString("FinishColumnRoundingTime"));
				((TitledBorder) fPanelUnFinished.getBorder()).setTitle(res.getString("FinishTitleNotYetRounded"));
				((TitledBorder) fPanelFinishers.getBorder()).setTitle(res.getString("FinishTitleRoundings"));
				fButtonFinishRemaining.setVisible(false);
			} else {
				eCol.setHeaderValue(res.getString("GenFinishTime"));
				((TitledBorder) fPanelUnFinished.getBorder()).setTitle(res.getString("FinishTitleNotYetFinished"));
				((TitledBorder) fPanelFinishers.getBorder()).setTitle(res.getString("GenFinishes"));
				fButtonFinishRemaining.setVisible(true);

				// TableColumn pCol = fTableFinished.getColumnModel().getColumn(
				// PENALTY_COLUMN);
				// pCol.setCellEditor( fEditorPenalty);
				//			
				// pCol = fTableFinished.getColumnModel().getColumn( SAILID_COLUMN);
				// pCol.setCellEditor( fEditorSailId);

			}

			fPanelFinish.setFinish(null);
			if (fFinishers.size() > 0) setCurrentFinish(fFinishers.get(0));
		}
		updateEnabled();			
	}

	private void setCurrentFinish(Finish f) {
		fCurrentFinish = f;
		fPanelFinish.setFinish(fCurrentFinish);

		// the finish may NOT be in fFinishers (eg single click on unfinishlist)
		fCurrentRow = fFinishers.indexOf(f);
		if (fCurrentRow >= 0) {
			fTableFinished.setRowSelectionInterval(fCurrentRow, fCurrentRow);
			Rectangle rect = fTableFinished.getCellRect(fCurrentRow, SAILID_COLUMN, false);
			fTableFinished.scrollRectToVisible(rect);
		}
	}

	/**
	 * called when the selected value of a table changes
	 */
	private void fTableFinished_valueChanged(ListSelectionEvent event) {
		int row = ((ListSelectionModel) event.getSource()).getMinSelectionIndex();
		if (row < 0) {
			setCurrentFinish(null);
		} else {
			setCurrentFinish(fFinishers.get(row));
		}
	}

	/**
	 * called when the selected value of a table changes
	 */
	private void fListUnFinished_valueChanged() {
		Entry e = (Entry) fListUnFinished.getSelectedValue();
		if (e == null) {
			setCurrentFinish(null);
		} else {
			setCurrentFinish(new Finish(fRace, e));
		}
	}

	private Finish findFirstEmptyFinish() {
		for (Finish f : fFinishers) {
			if (f.getEntry() == null) return f;
		}
		return null;
	}

	private Finish findLastEmptyFinish() {
		for (int i = fFinishers.size() - 1; i >= 0; i--) {
			Finish f = fFinishers.get(i);
			if (f.getEntry() == null) return f;
		}
		return null;
	}

	private void fListUnFinished_doubleClicked() {
		Entry ent = (Entry) fListUnFinished.getSelectedValue();
		Finish f = findFirstEmptyFinish();
		if (f != null) {
			f.setEntry(ent);
			int index = fFinishers.indexOf(f);
			fModelUnFinished.removeElement(ent);
			setCurrentFinish(f);
			fTableFinished.emptyEditors();
			saveEditedCell(index);
			fFinishModel.fireTableDataChanged();
		} else {
			Util.showError(new Exception(
					"No empty finishes, in unfin/doubleclick, shouldn't be able to get here, tell sandy"), true);
		}
	}

	/**
	 * called when user finishes editing a sailid cell in the finish table
	 **/
	private void setFinisherSailId(Finish thisFinish, Object obj) {
		boolean valid = true;
		String newId = "";
		if (obj != null) newId = obj.toString();

		if (newId.length() == 0) {
			// sail number is empty, put old finish into unfinish list
			if (thisFinish.getEntry() != null) {
				fModelUnFinished.addElement(thisFinish.getEntry());
				thisFinish.setEntry(null);
			}
		} else if ((thisFinish.getEntry() != null)
				&& newId.equalsIgnoreCase(thisFinish.getEntry().getBoat().getSailId().toString())) {
			// string is same as current finish, don't revalidate
		} else {
			EntryList eList = fEntries.findId(newId);
			if (eList.size() == 0) // no entries found
			{
				beep();
				JOptionPane.showMessageDialog(this,
						MessageFormat.format(res.getString("FinishMessageNoEntryError"), new Object[] { newId }),
						res.getString("FinishTitleNoEntry"), JOptionPane.WARNING_MESSAGE);
				valid = false;
			} else // found at least one found entry
			{
				Entry entryToFinish = eList.get(0);

				if (eList.size() > 1) {
					beep();
					dupePanel.setRace(fRace);
					dupePanel.setDuplicateEntries( eList);
					JOptionPane.showConfirmDialog(
							this, dupePanel, res.getString("FinishMessageMoreThanOnePossible"), 
							JOptionPane.OK_OPTION);
					entryToFinish = dupePanel.getSelectedEntry();
				}

				if (entryToFinish != null) {
					// have single candidate, is he  already finished?
					Finish previousFinish = fFinishers.findEntry(entryToFinish);

					if (previousFinish == null) {
						// no pre-existing finish... if there is an entry in
						// this
						// finish send it to the unfinish list and keep the new
						// one
						if (thisFinish != null && thisFinish.getEntry() != null) {
							// put old entry into unfinished list
							fModelUnFinished.addElement(thisFinish.getEntry());
						}

						// he's not finished, set him, remove him from
						// unfinishlist
						thisFinish.setEntry(entryToFinish);
						fModelUnFinished.removeElement(entryToFinish);
					} else if (previousFinish != thisFinish) {
						beep();
						int option = JOptionPane.showConfirmDialog(
								this,
								MessageFormat.format(res.getString("FinishMessageAlreadyFinished"), new Object[] {
										entryToFinish.toString(), previousFinish.getFinishPosition().toString() }),
								res.getString("FinishMessageAlreadyFinishedTitle"), JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (option == JOptionPane.YES_OPTION) {
							// yep, replace the finish, kill the old finish
							// keep e defined to be set at new location
							if (thisFinish != null && thisFinish.getEntry() != null) {
								// put old entry into unfinished list
								fModelUnFinished.addElement(thisFinish.getEntry());
							}
							fModelUnFinished.removeElement(entryToFinish);
							previousFinish.setEntry(null);
							thisFinish.setEntry(entryToFinish);

							setCurrentFinish(previousFinish);
							setCurrentFinish(thisFinish);
						} else {
							valid = false;
						}
					} // repeat finish
				} // single candidate
			} // at least one
		} // sail id not blank
		if (!valid) {
			thisFinish.setEntry(null);
		}
		fFinishModel.fireTableRowsUpdated(fCurrentRow, fCurrentRow);
	}

	private void setFinisherFinishTime(Finish thisFinish, Object obj) {
		boolean valid = true;
		String s;
		if (obj == null) s = EMPTY;
		else s = obj.toString();
		long lastTime = SailTime.getLastTime();

		if (s.length() == 0) {
			thisFinish.setFinishTime(SailTime.NOTIME);
		} else try {
			long l = SailTime.toLong(s);
			thisFinish.setFinishTime(l);
			if (l < lastTime) {
				beep();
				// popup up time earlier than last warning
				JOptionPane.showMessageDialog(this, res.getString("FinishMessageEarlierTime"),
						res.getString("FinishTitleEarlierTime"), JOptionPane.WARNING_MESSAGE);
			}
		} catch (java.text.ParseException e) {
			beep();
			// not a valid number, show message
			JOptionPane.showMessageDialog(this, res.getString("FinishMessageBadFinishTime"),
					res.getString("FinishTitleBadFinishTime"), JOptionPane.WARNING_MESSAGE);
			valid = false;
		}
		if (!valid) {
			thisFinish.setFinishTime(SailTime.NOTIME);
		}
		fFinishModel.fireTableRowsUpdated(fCurrentRow, fCurrentRow);
	}

	private void reorderFinishTable() {
		fFinishers.sortPosition();
		fFinishers.reNumber();
		fFinishers.sortPosition();
		fFinishModel.fireTableDataChanged();

		setCurrentFinish(fCurrentFinish); // -reset row nums???
	}

	public void setFinisherPenalty(Finish thisFinish, Penalty p) {
		// p may be different now
		if (thisFinish != null) thisFinish.setPenalty(p);
		fFinishModel.fireTableRowsUpdated(fCurrentRow, fCurrentRow);

		if (p.isFinishPenalty()) {
			// have non-finish penalty inbound from thisPen, with a valid finish
			// need to pass finish penalty to finish and renumber & resort other finishers
			thisFinish.setFinishPosition(new FinishPosition(p.getPenalty()));
			thisFinish.setFinishTime( SailTime.NOTIME);
			reorderFinishTable();
		}
	}

	private class CellEditorPenalty extends DefaultCellEditor {
		JPanel fpPanel;
		JTextFieldSelectAll fpText;
		JButton fpButton;
		Penalty fpPenalty;

		public void setPenalty(Penalty p) {
			delegate.setValue(p);
		}

		public CellEditorPenalty() {
			super(new JTextFieldSelectAll(6)); // this is a dummy field
			setBackground(Color.white);

			fpPanel = new JPanel(new BorderLayout(0, 0));

			fpText = (JTextFieldSelectAll) getComponent();
			fpText.setToolTipText(res.getString("FinishColumnPenaltyTextToolTip"));
			fpText.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					fText_actionPerformed();
				}
			});

			ImageIcon pFlag = Util.getImageIcon(fpPanel, JavaScoreProperties.PROTESTFLAG_ICON);
			fpButton = new JButton(pFlag);
			fpButton.setRequestFocusEnabled(false);
			fpButton.setPreferredSize(new Dimension(16, 16));
			fpButton.setBorderPainted(false);
			fpButton.setSelected(true);
			fpButton.setToolTipText(res.getString("FinishButtonPenaltyToolTip"));
			fpButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					fButton_actionPerformed();
				}
			});

			fpPanel.add(fpText, BorderLayout.CENTER);
			fpPanel.add(fpButton, BorderLayout.EAST);

			editorComponent = fpPanel;
			this.clickCountToStart = 1;

			delegate = new EditorDelegate() {
				@Override public void setValue(Object value) {
					// inbound, set the text value
					fpPenalty = (Penalty) value;
					fpText.setText((value != null) ? value.toString() : EMPTY);
				}

				@Override public Object getCellEditorValue() {
					// outbound
					fText_actionPerformed();
					return fpPenalty;
				}
			};
		}

		private void fText_actionPerformed() {
			if (fpPenalty == null) return; // no penalty dont do nothin'
			try {
				Penalty.parsePenalty(fpPenalty, fpText.getText());
			} catch (IllegalArgumentException e) {
				showPenaltyDialog();
				fpText.setText(fpPenalty.toString());
			}
		}

		private void fButton_actionPerformed() {
			if (fpPenalty == null) return; // no penalty dont do nothin'
			try {
				Penalty.parsePenalty(fpPenalty, fpText.getText());
			} catch (IllegalArgumentException e) {}
			showPenaltyDialog();
			fpText.setText(fpPenalty.toString());
		}

		private void showPenaltyDialog() {
			Finish thisFinish = fCurrentFinish; // (Finish) fFinishers.get(
			// row);
			if (thisFinish == null || thisFinish.getEntry() == null) {
				int row = fTableFinished.getEditingRow();
				thisFinish = fFinishers.get(row);
			}

			if (thisFinish != null && thisFinish.getEntry() != null) {
				fDialogPenalty.setObject(fpPenalty);
				fDialogPenalty.setTitle(MessageFormat.format(res.getString("FinishTitlePenalty"),
						new Object[] { fRace.toString(), thisFinish.getEntry().toString() }));
				fDialogPenalty.addWindowListener( new WindowAdapter() {
					@Override public void windowClosed(WindowEvent e) {
						fpText.setText(fpPenalty.toString());
					};
				});
				fDialogPenalty.setVisible(true);
			}
		}

	}

	private class FinishJTable extends JTable {
		CellEditorPenalty editorPenalty = new CellEditorPenalty();
		DefaultCellEditor editorBoatOrTime = new JTextFieldSelectAll.CellEditor();

		public void emptyEditors() {
			editorPenalty.setPenalty(null);
			((JTextField) editorBoatOrTime.getComponent()).setText(EMPTY);
		}

		public FinishJTable() {
			super();
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			getColumnModel().setSelectionModel(new SelectionModelSkipCol0());
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			// make ENTER in the table act like a TAB
			KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
			KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
			registerKeyboardAction(getActionForKeyStroke(tab), enter, getConditionForKeyStroke(tab));

			DefaultTableCellRenderer finLabel = new DefaultTableCellRenderer();
			finLabel.setBackground(Color.lightGray);
			setDefaultRenderer(FinishPosition.class, finLabel);
			
			setDefaultEditor(String.class, editorBoatOrTime);
			setDefaultEditor(Penalty.class, editorPenalty);
			
			DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
			rend.setBackground( new Color( 240, 240, 240));
			setDefaultRenderer(String.class, rend);
			setDefaultRenderer(Penalty.class, rend);
		}

		@Override public boolean editCellAt(int row, int col, EventObject e) {
			setCurrentFinish(fFinishers.get(row));
			boolean b = super.editCellAt(row, col, e);
			return b;
		}

		@Override public void setValueAt(Object obj, int row, int col) {
			// punt validation to sub-methods, but handle
			// navigation changes here
			Finish thisFinish = fFinishers.get(row);

			switch (col) {
			case SAILID_COLUMN:
				setFinisherSailId(thisFinish, obj);
				break;
			case TIME_COLUMN:
				setFinisherFinishTime(thisFinish, obj);
				break;
			case PENALTY_COLUMN:
				setFinisherPenalty(thisFinish, (Penalty) obj);
				break;
			default:
				break;
			}
		}

		@Override public Object getValueAt(int r, int c) {
			Finish f = fFinishers.get(r);
			switch (c) {
			case FINISH_COLUMN:
				return f.getFinishPosition();
			case SAILID_COLUMN:
				if (f.getEntry() == null) return EMPTY;
				else if (fRace.getRegatta().isUseBowNumbers()) {
					String s = f.getEntry().getBow().toString();
					if (s.length() == 0) {
						s = f.getEntry().getBoat().getSailId().toString();
					}
					return s;
				} else {
					return f.getEntry().getBoat().getSailId().toString();
				}
			case TIME_COLUMN:
				if (f.getFinishTime() == SailTime.NOTIME) return EMPTY;
				else return SailTime.toString(f.getFinishTime());
			case PENALTY_COLUMN:
				return f.getPenalty();
			}
			return EMPTY;
		}

		@Override public String getToolTipText(MouseEvent event) {
			int col = columnAtPoint(event.getPoint());
			if (col >= 0) {
				return tips[col];
			} else return EMPTY;
		}

		final String[] tips = { res.getString("FinishColumnPositionToolTip"), res.getString("FinishBowSailToolTip"),
				res.getString("FinishColumnTimeToolTip"), res.getString("FinishColumnPenaltyToolTip") };

	} // of local jtable

	DialogBaseEditor fDialogPenalty = null;

//	@Override public void setVisible(boolean vis) {
//		if (vis) {
//			if (!isVisible()) start();
//		} else {
//			if (isVisible()) stop();
//		}
//		super.setVisible(vis);
//	}

	private class SelectionModelSkipCol0 extends DefaultListSelectionModel {
		public SelectionModelSkipCol0() {
			super();
		}

		int not0(int col) {
			return (col == 0) ? 1 : col;
		}

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

	/**
	 * for standalone testing only
	 **/
	public static void main(String[] args) {
		JavaScore.initializeEditors();

		try {
			Regatta reg = RegattaManager.readTestRegatta("DnfAmidstFinishers.regatta");

			DialogFinishListEditor panel = new DialogFinishListEditor(null);
			// panel.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE);
			panel.addWindowListener(new WindowAdapter() {
				@Override public void windowClosing(WindowEvent event) {
					System.exit(0);
				}
			});

			panel.setRace(reg.getRaceIndex(reg.getNumRaces() - 1));
			panel.setVisible(true);
		} catch (Exception e) {
			Util.showError(e, true);
		}
		// System.exit(0);
	}

}
