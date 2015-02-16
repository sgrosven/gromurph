// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogBaseEditor.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Dialog to wrap a single BaseEditor, contains CANCEL and OK buttons
 **/
public class DialogBaseEditor<T extends BaseObjectModel> extends JDialog implements BaseEditorContainer, ActionListener, WindowListener {

	static ResourceBundle resUtil = Util.getResources();

	JButton fButtonCancel;
	JButton fButtonExit;
	JButton fButtonHelp;
	BaseEditor<T> fPanelEditor;

	public DialogBaseEditor(JFrame parent, boolean modal) {
		this(parent, "", modal);
	}

	public DialogBaseEditor(JFrame parent) {
		this(parent, "", false);
	}

	public DialogBaseEditor() {
		this(null, "", false);
	}

	private Point fInitLocation = null;

	public DialogBaseEditor(JFrame parent, String title, boolean modal) {
		super(parent, modal);
		setTitle(title);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel fPanelSouth = new JPanel(new FlowLayout());
		fPanelSouth.setName("fPanelSouth");
		getContentPane().add(fPanelSouth, BorderLayout.SOUTH);

		fButtonHelp = new JButton(resUtil.getString("HelpButton"));
		if (!Util.isMac()) fButtonHelp.setIcon(Util.getImageIcon(this, Util.HELP_ICON));
		fButtonHelp.setName("fButtonHelp");
		fButtonHelp.setMnemonic(resUtil.getString("HelpMnemonic").charAt(0));
		fPanelSouth.add(fButtonHelp);

		fButtonCancel = new JButton(resUtil.getString("CancelButton"));
		fButtonCancel.setName("fButtonCancel");
		fButtonCancel.setMnemonic(resUtil.getString("CancelMnemonic").charAt(0));
		fPanelSouth.add(fButtonCancel);

		fButtonExit = new JButton(resUtil.getString("ExitButton"));
		fButtonExit.setName("fButtonExit");
		fButtonExit.setMnemonic(resUtil.getString("ExitMnemonic").charAt(0));
		getRootPane().setDefaultButton(fButtonExit);
		fPanelSouth.add(fButtonExit);

	}

	public void updateEnabled() {}

	private boolean started = false;

	public void startUp() {
		if (!started) {
			started = true;
			this.addWindowListener(this);
			fButtonCancel.addActionListener(this);
			fButtonExit.addActionListener(this);
			fButtonHelp.addActionListener(this);
			if (fPanelEditor != null) fPanelEditor.startUp();
			start();
		}
	}

	public void shutDown() {
		if (started) {
			started = false;
			this.removeWindowListener(this);
			fButtonCancel.removeActionListener(this);
			fButtonExit.removeActionListener(this);
			fButtonHelp.removeActionListener(this);
			if (fPanelEditor != null) fPanelEditor.shutDown();
			stop();
		}
	}

	protected void start() {}

	protected void stop() {}

	public void windowActivated(WindowEvent event) {} // do nothing

	public void windowDeactivated(WindowEvent event) {} // do nothing

	public void windowDeiconified(WindowEvent event) {} // do nothing

	public void windowIconified(WindowEvent event) {} // do nothing

	public void windowOpened(WindowEvent event) {} // do nothing

	public void windowClosed(WindowEvent event) {} // do nothing

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fButtonCancel) fButtonCancel_actionPerformed();
		else if (event.getSource() == fButtonExit) fButtonExit_actionPerformed();
		else if (event.getSource() == fButtonHelp) fButtonHelp_actionPerformed();
	}

	public void fButtonHelp_actionPerformed() {
		if (fPanelEditor != null) {
			HelpManager.getInstance().setHelpTopic(fPanelEditor);
		} else {
			try {
				HelpManager.getInstance().setHelpTopic(this);
			}
			catch (Exception e) {} // do nothing

		}
	}

	public void fButtonCancel_actionPerformed() {
		closeWindow();
		if (fPanelEditor != null) {
			fPanelEditor.restore();
			fPanelEditor.exitCancel();
		}
	}

	public void fButtonExit_actionPerformed() {
		closeWindow();
		if (fPanelEditor != null) fPanelEditor.exitOK();
	}

	protected void closeWindow() {
		setVisible(false);
		updateEnabled();
	}

	public void windowClosing(WindowEvent event) {
		if (event.getSource() == this) {
			if (fPanelEditor != null && fPanelEditor.isExitOKOnWindowClose()) {
				fButtonExit_actionPerformed();
			} else {
				fButtonCancel_actionPerformed();
			}
		}
	}

	@Override
	public void setVisible(boolean vis) {
		if (vis) {
			startUp();
		} else {
			shutDown();
		}

		if (fInitLocation == null) {
			pack();
			fInitLocation = Util.getLocationToCenterOnScreen(this);
			setLocation(fInitLocation);
		}

		super.setVisible(vis);
	}

	public BaseEditor getEditor() {
		return fPanelEditor;
	}

	/**
	 * makes the item specified the current object
	 **/
	public void setObject(T obj) {
		setObject(obj, null);
	}
	public void setObject(T obj, BaseEditor<T> preferredEditor) {
		if (obj != null) {
			BaseEditor<T> targetEditor = preferredEditor;
			if (targetEditor == null) targetEditor = EditorManager.lookupEditor(obj, this);
			if (fPanelEditor == null || fPanelEditor.getClass() != targetEditor.getClass()) {
				if (fPanelEditor != null) {
					fPanelEditor.stop();
					getContentPane().remove(fPanelEditor);
				}
				try {
					fPanelEditor = targetEditor;
					Dimension d = fPanelEditor.getPreferredSize();
					d.width += 50;
					d.height += 100;
					setSize(d);
					setTitle(fPanelEditor.getTitle());
				}
				catch (Exception e) {
					Util.showError(e, false);
				}

				getContentPane().add(fPanelEditor, BorderLayout.CENTER);
			}

			fPanelEditor.setObject(obj);
			fPanelEditor.setEnabled(true);
			if (isVisible()) fPanelEditor.start();
		} else {
			if (fPanelEditor != null) {
				getContentPane().remove(fPanelEditor);
				fPanelEditor.stop();
				fPanelEditor = null;
			}
		}
		fButtonCancel.setEnabled(false);
		HelpManager.getInstance().enableWindowHelp(Util.getParentJFrame(this), fPanelEditor);
	}

	// ======= BaseEditorContainer methods

	/**
	 * tells container that some actions have been performed in editor
	 **/
	public void eventOccurred(BaseEditor editor, java.util.EventObject event) {
		fButtonCancel.setEnabled(editor.changesPending());
	}

}
/**
 * $Log: DialogBaseEditor.java,v $ Revision 1.4 2006/01/15 21:10:35 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.14.4.1 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.14 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.13 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.12 2003/05/07 01:17:05 sandyg removed unneeded method parameters
 * 
 * Revision 1.11 2003/03/30 00:05:51 sandyg moved to eclipse 2.1
 * 
 * Revision 1.10 2003/03/27 02:46:56 sandyg Completes fixing [ 584501 ] Can't change division splits in open reg
 * 
 * Revision 1.9 2003/03/19 03:32:22 sandyg cancel in PanelDivision now correctly reverts the division to original
 * 
 * Revision 1.8 2003/03/19 02:38:17 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.7 2003/03/16 20:38:30 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.6 2003/02/12 02:18:59 sandyg added component names prepping for jfcunit testing
 * 
 * Revision 1.5 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
