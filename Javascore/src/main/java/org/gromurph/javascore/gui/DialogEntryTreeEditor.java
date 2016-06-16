// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogEntryTreeEditor.java,v 1.7 2006/09/03 20:12:21 sandyg Exp $
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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.HelpManager;
import org.gromurph.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for editing BaseLists and their contained BaseObjects
 **/
public class DialogEntryTreeEditor extends JDialog implements BaseEditorContainer, WindowListener, ActionListener,
		TreeSelectionListener, PropertyChangeListener {
	
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected EntryTreeModel fTreeModel = null; // set by setMasterList()

	protected JTree fTree;

	protected JButton fButtonAdd;
	protected JButton fButtonDelete;
	protected JButton fButtonRestore;
	protected JButton fButtonExit;
	protected JButton fButtonHelp;

	protected PanelEntry fPanelEntry;

	JPanel fPanelEditor;
	JPanel fPanelRight;

	JFrame fParent;

	private static String CARD_EMPTY = "Empty";
	private static String CARD_NOSELECT = "NoSelect";
	private static String CARD_OBJECT = "Object";

	public DialogEntryTreeEditor(JFrame parent, String title) {
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

		super(parent, false);
		fParent = parent; // keep for yesnodialog
		setTitle(title);

		getContentPane().setLayout(new BorderLayout(0, 0));

		fPanelRight = new JPanel(new CardLayout());

		JPanel fPanelLeft = new JPanel(new BorderLayout(0, 0));

		fTree = new JTree();
		JScrollPane scroll = new JScrollPane(fTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fPanelLeft.add(scroll, BorderLayout.CENTER);

		JSplitPane fPanelSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, fPanelLeft, fPanelRight);
		getContentPane().add(fPanelSplitter, BorderLayout.CENTER);

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

		Dimension sizeSmallButton = new Dimension(100, 30);
		Dimension sizeLargeButton = new Dimension(120, 30);

		fButtonAdd = new JButton(resUtil.getString("AddButton"));
		if (!Util.isMac()) fButtonAdd.setIcon(Util.getImageIcon(this, Util.ADD_ICON));
		fButtonAdd.setMnemonic(resUtil.getString("AddMnemonic").charAt(0));
		fButtonAdd.setPreferredSize(sizeSmallButton);
		fButtonAdd.setName( "fButtonAdd");
		fPanelSWest.add(fButtonAdd);

		fButtonDelete = new JButton(resUtil.getString("DeleteButton"));
		if (!Util.isMac()) fButtonDelete.setIcon(Util.getImageIcon(this, Util.DELETE_ICON));
		fButtonDelete.setMnemonic(resUtil.getString("DeleteMnemonic").charAt(0));
		fButtonDelete.setDefaultCapable(false);
		fButtonDelete.setPreferredSize(sizeSmallButton);
		fButtonDelete.setName("fButtonDelete");
		fPanelSWest.add(fButtonDelete);

		fButtonHelp = new JButton(resUtil.getString("HelpButton"));
		if (!Util.isMac()) fButtonHelp.setIcon(Util.getImageIcon(this, Util.HELP_ICON));
		fButtonHelp.setMnemonic(resUtil.getString("HelpMnemonic").charAt(0));
		fButtonHelp.setPreferredSize(sizeSmallButton);
		fButtonHelp.setName("fButtonHelp");
		fPanelSEast.add(fButtonHelp);

		fButtonRestore = new JButton(resUtil.getString("RestoreButton"));
		fButtonRestore.setMnemonic(resUtil.getString("RestoreMnemonic").charAt(0));
		fButtonRestore.setDefaultCapable(false);
		fButtonRestore.setPreferredSize(sizeLargeButton);
		fButtonRestore.setEnabled(false);
		fButtonRestore.setName("fButtonRestore");
		//fPanelRSouth.add(fButtonRestore);
		fPanelSEast.add(fButtonRestore);

		fButtonExit = new JButton(resUtil.getString("ExitButton"));
		fButtonExit.setMnemonic(resUtil.getString("ExitMnemonic").charAt(0));
		fButtonExit.setPreferredSize(sizeSmallButton);
		fButtonExit.setName("fButtonExit");
		fPanelSEast.add(fButtonExit);

		fPanelEntry = new PanelEntry( this);
		fPanelEditor.add(fPanelEntry, BorderLayout.CENTER);

		fPanelSplitter.setDividerLocation(SPLITTER_WIDTH);
		setLocation(Util.getLocationToCenterOnScreen(this));

		Dimension psize = fPanelEntry.getPreferredSize();
		psize.width = Math.max(psize.width, fPanelEntry.getPreferredSize().width) + SPLITTER_WIDTH + 120;
		psize.height = Math.max(psize.height, fPanelEntry.getPreferredSize().height) + 200;
		this.setPreferredSize(psize);

	}

	private static int SPLITTER_WIDTH = 230;

	public void start() {

		this.addWindowListener(this);
		fButtonAdd.addActionListener(this);
		fButtonDelete.addActionListener(this);
		fButtonExit.addActionListener(this);
		fButtonRestore.addActionListener(this);
		fButtonHelp.addActionListener(this);

		fTree.addTreeSelectionListener(this);

		updateDefault(null);
		updateEnabled();
	}

	public void stop() {
		this.removeWindowListener(this);
		fButtonAdd.removeActionListener(this);
		fButtonDelete.removeActionListener(this);
		fButtonExit.removeActionListener(this);
		fButtonRestore.removeActionListener(this);
		fButtonHelp.removeActionListener(this);
		fTree.removeTreeSelectionListener(this);
	}

	public DialogEntryTreeEditor(JFrame parent) {
		this(parent, "");
	}

	public DialogEntryTreeEditor() {
		this(null, "");
	}

	public void valueChanged(TreeSelectionEvent event) {
		if (event.getOldLeadSelectionPath() != null && !deletingEntries) {
			fTreeModel.updateEntry(event.getOldLeadSelectionPath().getPath());
		}

		Object o = event.getPath().getLastPathComponent();
		if (o instanceof EntryTreeModel.EntryTreeNode) {
			Entry n = ((EntryTreeModel.EntryTreeNode) o).getEntry();
			setObject(n);
		}
		updateEnabled();
		updateDefault(null);
	}

	Entry fCurrentEntry = null;

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == fCurrentEntry) {
			TreePath tp = fTree.getSelectionPath();
			if (tp != null) {
				Object[] path = tp.getPath();
				fTreeModel.updateEntry(path);
			}

			TreePath newtp = fTreeModel.getPathToEntry(fCurrentEntry);
			if (newtp != null && !newtp.equals(tp)) {
				fTree.setSelectionPath(newtp);
				fTree.scrollPathToVisible(newtp);
			}
		}
	}

	/**
	 * makes the item specified the current object
	 **/
	protected void setObject(Entry obj) {
		showCard(CARD_NOSELECT);
		logger.debug("DialogEntryTreeEditor.setObject: {}", (obj == null) ? "null" : obj.toString());
		if (fCurrentEntry != null) {
			fCurrentEntry.removePropertyChangeListener(this);
			fCurrentEntry.deleteBlankCrew();
		}

		if (obj != null) {
			fPanelEntry.setObject(obj);
			fPanelEntry.startUp();
			showCard(CARD_OBJECT);
		}

		fCurrentEntry = (Entry) obj;
		if (fCurrentEntry != null) fCurrentEntry.addPropertyChangeListener(this);
		updateEnabled();
	}

	public void updateEnabled() {
		if (fPanelEntry != null) fPanelEntry.setEnabled(true);
		fButtonRestore.setEnabled((fPanelEntry != null) && fPanelEntry.changesPending());
		boolean delOK = false;
		boolean addOK = false;
		if (fTree.getSelectionCount() > 0) {
			TreePath[] paths = fTree.getSelectionPaths();
			for (int i = 0; (i < paths.length) && !(delOK && addOK); i++) {
				int depth = paths[i].getPath().length;
				if (depth == 3) {
					delOK = true;
					addOK = true;
				} else if (depth == 2) {
					addOK = true;
				}
			}
		}
		fButtonDelete.setEnabled(delOK);
		fButtonAdd.setEnabled(addOK);

		fTree.setEnabled(true);
	}

	private void showCard(String card) {
		((CardLayout) fPanelRight.getLayout()).show(fPanelRight, card);
	}

	protected EntryTreeModel getTreeModel() {
		return fTreeModel;
	}
	
	private Regatta fRegatta;
	public void setRegatta(Regatta reg) {
		
		//if (fRegatta != null && fRegatta == reg) return;
		fRegatta = reg;
		
		String inLabel = fRegatta.getName();

		fTreeModel = new EntryTreeModel( fRegatta);
		fTree.setModel(fTreeModel);
		if (fTreeModel.getChildCount(fTreeModel.getRoot()) == 0) {
			showCard(CARD_EMPTY);
		} else {
			showCard(CARD_NOSELECT);
		}
		
		setTitle(inLabel);

		if (fRegatta.getNumDivisions() == 1 && fRegatta.getNumEntries() > 0) {
			TreeNode root = (TreeNode) fTreeModel.getRoot();
			TreeNode div1 = null;
			for (int r = 0; (r < root.getChildCount()) && (div1 == null); r++) {
				div1 = root.getChildAt(r);
				if (div1.getChildCount() == 0) div1 = null;
			}
			if (div1 != null) {
				TreeNode ent1 = div1.getChildAt(0);
				// only one division make sure its expanded
				fTree.scrollPathToVisible(new TreePath(new Object[] { root, div1, ent1 }));
			}
		}
	}

	public void windowClosing(WindowEvent event) {
		if (event == null || event.getSource() == this) {
			setVisible(false);

			if (fRegatta != null) fRegatta.getAllEntries().removeBlanks();

			if (fPanelEntry != null) {
				fPanelEntry.exitOK();
			} else {
				JavaScore.backgroundSave();
			}
		}
	}

	public void windowActivated(WindowEvent event) {} // do nothing
	public void windowDeactivated(WindowEvent event) {} // do nothing
	public void windowDeiconified(WindowEvent event) {} // do nothing
	public void windowIconified(WindowEvent event) {} // do nothing
	public void windowOpened(WindowEvent event) {} // do nothing
	public void windowClosed(WindowEvent event) {} // do nothing

	private Point fInitLocation = null;

	@Override public void setVisible(boolean vis) {
		
		
		if (vis) {
			if (!isVisible()) {
				setRegatta( JavaScoreProperties.getRegatta());
				start();
				if (fPanelEntry != null) {
					fPanelEntry.startUp();
				}
			}
		} else {
			if (isVisible()) {
				stop();
				if (fPanelEntry != null) fPanelEntry.shutDown();
			}
		}

		if (fInitLocation == null) {
			pack();
			fInitLocation = Util.getLocationToCenterOnScreen(this);
			setLocation(fInitLocation);
		}

		super.setVisible(vis);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fButtonAdd) fButtonAdd_actionPerformed();
		else if (event.getSource() == fButtonDelete) fButtonDelete_actionPerformed();
		else if (event.getSource() == fButtonExit) fButtonExit_actionPerformed();
		else if (event.getSource() == fButtonRestore) fButtonRestore_actionPerformed();
		else if (event.getSource() == fButtonHelp) fButtonHelp_actionPerformed();
		updateDefault(event);
	}

	public void fButtonExit_actionPerformed() {
		windowClosing(null);
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
			boolean delOK = false;
			if (fTree.getSelectionCount() > 0) {
				TreePath[] paths = fTree.getSelectionPaths();
				for (int i = 0; (i < paths.length) && !delOK; i++) {
					int depth = paths[i].getPath().length;
					if (depth == 3) {
						delOK = true;
					}
				}
			}
			fButtonDelete.setEnabled(delOK);
		}
	}

	protected void fButtonAdd_actionPerformed() {
		try {
			Object[] path = fTree.getSelectionPath().getPath();
			fTreeModel.updateEntry(path);

			EntryTreeModel.EntryTreeNode entNode = fTreeModel.addEntry(path);
			setObject(entNode.getEntry());

			TreePath t = new TreePath(new Object[] { path[0], path[1], entNode });
			fTree.setSelectionPath(t);
			fTree.scrollPathToVisible(t);
			updateEnabled();
		} catch (Exception e) {
			Util.showError(e, true);
		}
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

	boolean deletingEntries = false;

	protected void fButtonDelete_actionPerformed() {
		TreePath[] paths = fTree.getSelectionPaths();
		if (paths.length == 0) return;

		boolean deleteOK = (fRegatta.getNumRaces() == 0); // no confirm needed if no races

		if (!deleteOK) // have races, post confirm message
		{
			StringBuffer msg = new StringBuffer();
			if (paths.length > 1) {
				msg.append(MessageFormat.format(resUtil.getString("DeleteConfirmPlural"), new Object[] { new Integer(
						paths.length) }));
			} else {
				msg.append(resUtil.getString("DeleteConfirmSingular"));
			}

			String footnote = getConfirmDeleteFootnote();
			if (footnote != null) {
				msg.append("\n");
				msg.append(footnote);
			}
			deleteOK = Util.confirm(msg.toString());
		}

		if (deleteOK) {
			deletingEntries = true;
			for (int i = paths.length - 1; i >= 0; i--) {
				try {
					Entry delEntry = ((EntryTreeModel.EntryTreeNode) paths[i].getPath()[2]).getEntry();
					fTreeModel.deleteEntry(delEntry);
				} catch (Exception e) {
					Logger l = LoggerFactory.getLogger(this.getClass());
					l.error( "Exception=" + e.toString(), e);
				}
			}
			deletingEntries = false;
			setObject(null);
		}
		updateEnabled();
	}

	protected void fButtonRestore_actionPerformed() {
		fPanelEntry.restore();
		fButtonRestore.setEnabled(false);
	}

	protected void fButtonHelp_actionPerformed() {
		try {
			HelpManager.getInstance().setHelpTopic(this);
		} catch (Exception e) {
			try {
				HelpManager.getInstance().setHelpTopic(fPanelEntry);
			} catch (Exception e2) {} // do nothing
		}
	}

	// ======= BaseEditorContainer methods

	/**
	 * tells container that some actions have been performed in editor
	 **/
	public void eventOccurred(BaseEditor editor, java.util.EventObject event) {
		//this.repaint();
		fButtonRestore.setEnabled(editor.changesPending());
	}

	/**
	 * for standalone testing only
	 **/
	public static void main(String[] args) {
		JavaScore.initializeEditors();

		try {
			Regatta reg = RegattaManager.readTestRegatta( "Spring_Sampler.regatta");
			DialogEntryTreeEditor panel = new DialogEntryTreeEditor(null);
			panel.setVisible(true);

		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

}
/**
 * $Log: DialogEntryTreeEditor.java,v $ Revision 1.7 2006/09/03 20:12:21 sandyg fixes bug 1551523 about crew data when
 * boat changes without a tab/enter
 * 
 * Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/15 03:25:51 sandyg to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.15.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.15.2.1 2005/08/18 01:26:32 sandyg Feature 585514, delete entries with no prompt if no races
 * 
 * Revision 1.15 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.14 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.13 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.12 2003/05/02 02:41:38 sandyg fixed division update problem in panelentry
 * 
 * Revision 1.11 2003/04/27 21:35:33 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.10 2003/04/27 21:05:58 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.9 2003/03/16 20:38:30 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.8 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
