// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogBaseListEditor.java,v 1.5 2006/01/19 01:50:15 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing BaseLists and their contained BaseObjects
 **/
public class DialogBaseListEditor<T extends BaseObject, L extends BaseList<? extends BaseObject>> extends JDialog 
		implements BaseEditorContainer, WindowListener, ActionListener, ListSelectionListener { //, FocusListener {

	protected Logger logger = LoggerFactory.getLogger( this.getClass());
	
	static ResourceBundle resUtil = Util.getResources();
	protected L fBaseList = null; // set by setMasterList()
	private org.gromurph.javascore.gui.DefaultListModel fBaseListModel = null;

	protected JList<T> fList;

	protected JButton fButtonAdd;
	protected JButton fButtonDelete;
	protected JButton fButtonRestore;
	protected JButton fButtonExit;
	protected JButton fButtonHelp;

	protected BaseEditor fPanelObject;

	protected JPanel fPanelEditor;
	private JPanel fPanelRight;

	JFrame fParent;

	private static String CARD_EMPTY = "Empty";
	private static String CARD_NOSELECT = "NoSelect";
	private static String CARD_OBJECT = "Object";

	private JSplitPane fPanelSplitter;

	public DialogBaseListEditor(JFrame parent, String title, boolean modal) {
		/*
		 * BorderLayout at top: Center goes a splitpane left of splitpane is the list, right is the editing hierarchy
		 * South gets the buttons, split into WEST (add, delete) and EAST (OK)
		 * 
		 * Left of splitpane is also borderlayout Center gets the JList
		 * 
		 * Right of splitpane is CardLayout EMPTY card get label with empty list message NOSELECT card gets label with
		 * no selection message OBJECT card gets panel of borderlayout CENTER is the BaseEditor panel for selected
		 * object SOUTH is the restore button
		 */

		super(parent, modal);
		fParent = parent; // keep for yesnodialog
		if (parent == null) 
			logger.warn("*** NULL Parent" + this.getClass().toString());
		if (modal) 
			logger.warn("*** modal true" + this.getClass().toString());
		setTitle(title);
		getContentPane().setLayout(new BorderLayout(0, 0));

		fPanelRight = new JPanel(new CardLayout());

		JPanel fPanelLeft = new JPanel(new BorderLayout(0, 0));

		fList = new JList();
		JScrollPane scroll = new JScrollPane(fList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fPanelLeft.add(scroll, BorderLayout.CENTER);

		fPanelSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, fPanelLeft, fPanelRight);
		getContentPane().add(fPanelSplitter, BorderLayout.CENTER);
		fPanelSplitter.setDividerLocation(getPreferredListWidth());

		JPanel fPanelSouth = new JPanel(new BorderLayout(0, 0));
		getContentPane().add(fPanelSouth, BorderLayout.SOUTH);

		JPanel fPanelSWest = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		fPanelSouth.add(fPanelSWest, BorderLayout.WEST);

		// set up the right hand panel
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(new JLabel(resUtil.getString("ListIsEmpty")));
		fPanelRight.add(p, CARD_EMPTY);

		p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(new JLabel(resUtil.getString("NoItemSelected")));
		fPanelRight.add(p, CARD_NOSELECT);

		fPanelEditor = new JPanel(new BorderLayout());
		fPanelRight.add(new JScrollPane(fPanelEditor), CARD_OBJECT);

		JPanel fPanelSEast = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		fPanelSouth.add(fPanelSEast, BorderLayout.EAST);
		JPanel fPanelRSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		fPanelEditor.add(fPanelRSouth, BorderLayout.SOUTH);

		fButtonAdd = new JButton(resUtil.getString("AddButton"));
		if (!Util.isMac())
			fButtonAdd.setIcon(Util.getImageIcon(this, Util.ADD_ICON));
		fButtonAdd.setMnemonic(resUtil.getString("AddMnemonic").charAt(0));
		fPanelSWest.add(fButtonAdd);

		fButtonDelete = new JButton(resUtil.getString("DeleteButton"));
		if (!Util.isMac())
			fButtonDelete.setIcon(Util.getImageIcon(this, Util.DELETE_ICON));
		fButtonDelete.setMnemonic(resUtil.getString("DeleteMnemonic").charAt(0));
		fButtonDelete.setDefaultCapable(false);
		fPanelSWest.add(fButtonDelete);

		fButtonHelp = new JButton(resUtil.getString("HelpButton"));
		if (!Util.isMac())
			fButtonHelp.setIcon(Util.getImageIcon(this, Util.HELP_ICON));
		fButtonHelp.setMnemonic(resUtil.getString("HelpMnemonic").charAt(0));
		fPanelSEast.add(fButtonHelp);

		fButtonRestore = new JButton(resUtil.getString("RestoreButton"));
		fButtonRestore.setMnemonic(resUtil.getString("RestoreMnemonic").charAt(0));
		fButtonRestore.setDefaultCapable(false);
		fButtonRestore.setEnabled(false);
		fPanelSEast.add(fButtonRestore);

		fButtonExit = new JButton(resUtil.getString("ExitButton"));
		fButtonExit.setMnemonic(resUtil.getString("ExitMnemonic").charAt(0));
		fPanelSEast.add(fButtonExit);

	}

	public void start() {
		
		setObject( (T) fList.getSelectedValue());
		
		this.addWindowListener(this);
		fButtonAdd.addActionListener(this);
		fButtonDelete.addActionListener(this);
		fButtonExit.addActionListener(this);
		fButtonRestore.addActionListener(this);
		fButtonHelp.addActionListener(this);
		fList.addListSelectionListener(this);
		//fList.addFocusListener(this);

		updateDefault(null);
	}

	public void stop() {
		this.removeWindowListener(this);
		fButtonAdd.removeActionListener(this);
		fButtonDelete.removeActionListener(this);
		fButtonExit.removeActionListener(this);
		fButtonRestore.removeActionListener(this);
		fButtonHelp.removeActionListener(this);
		fList.removeListSelectionListener(this);
		//fList.removeFocusListener(this);
	}

	private T fCurrentObject;

	protected T getCurrentObject() {
		return fCurrentObject;
	}

	/**
	 * makes the item specified the current object
	 **/
	protected void setObject(T obj) {
		if (obj != fCurrentObject) {
    		boolean resetSize = (fCurrentObject == null);
    		fCurrentObject = obj;
    
    		if (obj != null) {
    			updateEditorPanel( obj.getClass());
    			if (fPanelObject != null) fPanelObject.setObject(obj);
    			if (resetSize) {
    				setSize(this.getPreferredDialogSize());
    				fPanelSplitter.setDividerLocation(getPreferredListWidth());
    			}			
    		} 
		}
		updateEnabled();
	}
	
	protected Dimension getPreferredDialogSize() {
		Dimension d = getPreferredEditorSize();
		d.width += 150 + getPreferredListWidth(); // for list width and fluff
		d.height += 100; // for bottom buttons and fluff
		return d;
	}

	protected int getPreferredListWidth() {
		return 200;
	}

	protected Dimension getPreferredEditorSize() {
		return (fPanelObject != null) ? fPanelObject.getPreferredSize() : new Dimension(450, 450);
	}

	protected BaseEditor getEditor() {
		return fPanelObject;
	}

	public void updateEnabled() {
		
		if ((fBaseList != null) && (fBaseList.size() > 0)) {
			fList.ensureIndexIsVisible(0);
		}

//
//		if ( fCurrentObject == null) {
//			fList.setSelectedIndex(-1);
//		} else {
//    		int listIndex = fList.getSelectedIndex();
//    		int objIndex = (fBaseList == null) ? -1 : fBaseList.indexOf(fCurrentObject);
//    		if (listIndex != objIndex) fList.setSelectedIndex(objIndex);
//		}

		updateCard();
		
		//if (fPanelObject != null) fPanelObject.setEnabled(true);
		fButtonRestore.setEnabled((fPanelObject != null) && fPanelObject.changesPending());
		fButtonDelete.setEnabled(fList.getSelectedIndices().length > 0);
		fButtonAdd.setEnabled(true);
		fList.setEnabled(true);
	}

	private void updateCard() {
		if (fCurrentObject != null) showCard( CARD_OBJECT);
		else if (fBaseList == null || fBaseList.size() == 0) showCard(CARD_EMPTY);
		else showCard(CARD_NOSELECT);
		
	}

	private void showCard(String card) {
		((CardLayout) fPanelRight.getLayout()).show(fPanelRight, card);
		fPanelRight.repaint();
	}

	public void setMasterList(L inVal, String inLabel) {
		inVal.sort();
		//	if (fBaseList != inVal) {
		fBaseList = inVal;

		if (fBaseList != null) {
			if (fPanelObject != null) {
				fPanelObject.shutDown();
				fPanelEditor.remove(fPanelObject);
			}
			Class cc = fBaseList.getContainingClass();
			updateEditorPanel( cc);
			
			setSize(this.getPreferredDialogSize());
			fBaseListModel = fBaseList.getListModel();
			fList.setModel(fBaseListModel);
		
		} 
		//	}
		updateEnabled();
		setTitle(inLabel);
		
		if (fPanelObject != null) fPanelObject.requestFocusInWindow();
	}
	
	private void updateEditorPanel( Class objectClass) {
		if (objectClass == null) {
			logger.warn("baselist has no containing model class: {}", fBaseList.getClass().toString());
		} else {
			BaseEditor newEditor = EditorManager.lookupEditor( objectClass, this);
			if (newEditor == null) {
				logger.warn("No editor class found for object class: {}", objectClass.toString());
			} else if (fPanelObject == null || fPanelObject != newEditor){
				if (fPanelObject != null) {
					fPanelEditor.remove( fPanelObject);
				}
				fPanelObject = newEditor;
				fPanelEditor.add(fPanelObject, BorderLayout.CENTER);
				fPanelObject.startUp();
			}
		}
	}

	public void windowClosing(WindowEvent event) {
		if (event == null || event.getSource() == this) {
			setVisible(false);

			if (fBaseList != null) {
				fBaseList.removeBlanks();
			}
			if (fPanelObject != null)
				fPanelObject.exitOK();
		}
	}

	public void windowActivated(WindowEvent event) {} // do nothing

	public void windowDeactivated(WindowEvent event) {} // do nothing

	public void windowDeiconified(WindowEvent event) {} // do nothing

	public void windowIconified(WindowEvent event) {} // do nothing

	public void windowOpened(WindowEvent event) {} // do nothing

	public void windowClosed(WindowEvent event) {} // do nothing

	private Point fInitLocation = null;

	protected boolean isStarted = false;
	public void startUp() {
		if (!isStarted) {
			isStarted = true;
			start();
		}		
	}
	public void shutDown() {
		if (isStarted) {
			isStarted = false;
			stop();
		}
	}

	@Override public void setVisible(boolean vis) {
		if (vis) {
			if (!isVisible()) {
				startUp();
				if (fPanelObject != null) fPanelObject.startUp();
			}
		} else {
			if (isVisible()) {
				shutDown();
				if (fPanelObject != null) fPanelObject.shutDown();
			}
		}

		if (fInitLocation == null) {
			fInitLocation = Util.getLocationToCenterOnScreen(this);
			setLocation(fInitLocation);
		}

		super.setVisible(vis);
	}

//	public void focusLost(FocusEvent event) {} // do nothing
//
//	public void focusGained(FocusEvent event) {
//		if (event.getSource() == fList) {
//			setDefaultButton(fButtonExit);
//		}
//	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fButtonAdd)
			fButtonAdd_actionPerformed();
		else if (event.getSource() == fButtonDelete)
			fButtonDelete_actionPerformed();
		else if (event.getSource() == fButtonExit)
			fButtonExit_actionPerformed();
		else if (event.getSource() == fButtonRestore)
			fButtonRestore_actionPerformed();
		else if (event.getSource() == fButtonHelp)
			fButtonHelp_actionPerformed();
		updateDefault(event);
	}

	public void fButtonExit_actionPerformed() {
		windowClosing(null);
	}

	public void valueChanged(ListSelectionEvent event) {
//		if (!event.getValueIsAdjusting()) {
			if (event.getSource() == fList)
				fList_valueChanged();
			updateDefault(null);
//		}
	}

	public void fList_valueChanged() {
		T sel = fList.getSelectedValue();
		if (sel != this.getCurrentObject()) {
			setObject( sel);
		}
	}

	protected void updateDefault(ActionEvent event) {
		if (event != null && event.getSource() == fButtonAdd) {
			setDefaultButton(fButtonAdd);
		} else {
			setDefaultButton(fButtonExit);
		}
	}

	protected void setDefaultButton(JButton newDef) {
		getRootPane().setDefaultButton(newDef);

		if (newDef == fButtonAdd) {
			fButtonDelete.setEnabled(false);
		} else {
			fButtonDelete.setEnabled(fList.getSelectedIndices().length > 0);
		}
	}

	protected void fButtonAdd_actionPerformed() {
		T newObject = null;
		try {
			newObject = (T) fBaseList.getContainingClass().newInstance();
		} catch (Exception e) {
			Util.showError(e, true);
		}
		addObject(newObject);
	}

	protected void addObject(T newObject) {
		fBaseListModel.addElement(newObject); // should fire an "add"
		setObject(newObject);
	}

	/**
	 * returns the message that should be shown to confirm a deletion The question will start with:
	 * "Are you sure you want to delete this item?" And this footnote will be added to the end of the question
	 * 
	 * Expect subclasses to override this to add additional information
	 */
	public String getConfirmDeleteFootnote() {
		return null;
	}

	protected void fButtonDelete_actionPerformed() {
		int idx[] = fList.getSelectedIndices();
		if (idx.length == 0)
			return;

		StringBuffer msg = new StringBuffer();
		if (idx.length > 1) {
			msg.append(MessageFormat.format(resUtil.getString("DeleteConfirmPlural"), new Object[] { new Integer(
					idx.length) }));
		} else {
			msg.append(resUtil.getString("DeleteConfirmSingular"));
		}

		String footnote = getConfirmDeleteFootnote();
		if (footnote != null) {
			msg.append("\n");
			msg.append(footnote);
		}

		if (Util.confirm(msg.toString())) {
			for (int i = idx.length - 1; i >= 0; i--) {
				int x = idx[i];
				Object obj = fBaseList.get(x);
				fBaseListModel.removeElement(obj); // will fire listchanged that
				// will
				// be picked up by JList and cause
				// selected index to be reset
				setObject(null);
			}
		}
	}

	protected void fButtonRestore_actionPerformed() {
		if (fPanelObject != null) fPanelObject.restore();
		fButtonRestore.setEnabled(false);
	}

	protected void fButtonHelp_actionPerformed() {
		try {
			HelpManager.getInstance().setHelpTopic(this);
		} catch (Exception e) {
			try {
				if (fPanelObject != null) HelpManager.getInstance().setHelpTopic(fPanelObject);
			} catch (Exception e2) {} // do nothing
		}
	}

	// ======= BaseEditorContainer methods

	/**
	 * tells container that some actions have been performed in editor
	 **/
	public void eventOccurred(BaseEditor editor, java.util.EventObject event) {
		// fBaseList.fireTableDataChanged();
		//this.repaint();
		fButtonRestore.setEnabled(editor.changesPending());
	}


}
/**
 * $Log: DialogBaseListEditor.java,v $ Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:35 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.12 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.11 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.10 2003/05/07 01:17:05 sandyg removed unneeded method parameters
 * 
 * Revision 1.9 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.8 2003/03/30 00:05:52 sandyg moved to eclipse 2.1
 * 
 * Revision 1.7 2003/03/27 02:46:56 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 * 
 * Revision 1.6 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
