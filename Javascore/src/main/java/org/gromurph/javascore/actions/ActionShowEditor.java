// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionEditPreferences.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.BaseList;
import org.gromurph.util.BaseObjectModel;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.DialogBaseListEditor;
import org.gromurph.util.Util;
import org.gromurph.util.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActionShowEditor<T extends BaseObjectModel> extends AbstractAction 
		implements PropertyChangeListener, WindowListener {
	
	public BaseList<? extends T> getList() {
		return null; // override for listbased editors
	}
	public T getObject() {
		return null; // not all need this
	}
		
	public static String HELP_TOPIC = "HelpTopic";
	public static String LOOKUP_KEY = "LookupKey";
	public static String PANEL_TITLE = "PanelTitle";
	
	protected static ResourceBundle res = JavaScoreProperties.getResources();
	
	protected String fKey;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public ActionShowEditor(String lookupKey, String resourceName, String resourceMnemonic) {
		super(res.getString(resourceName));
		if (resourceMnemonic != null) putValue(Action.MNEMONIC_KEY, new Integer(res.getString(resourceMnemonic).charAt(0)));
		putValue( PANEL_TITLE, res.getString(resourceName));
		fKey = lookupKey;
		JavaScoreProperties.addPropertyChangeListener(this);
	}
	
	public String getHelpTopic() {
		return (String) getValue( HELP_TOPIC);
	}

	public void startInitializing() {
		worker = new SWInitializer();
		worker.start();
	}

	abstract public JDialog initializeEditor(JFrame parent);

	private SWInitializer worker = null;
	
	private class SWInitializer extends SwingWorker {
		@Override public Object construct() {
			try {
				JFrame rootParent = JavaScore.getInstance();
				JDialog dialog = initializeEditor( rootParent);
				return dialog;
			} catch (Exception e) {
				Util.showError(e, true);
			}
			return null;
		}
	};

	private static Map<String, JDialog> editorMap = new TreeMap<String,JDialog>();

	protected JDialog editor;
	
	public JDialog getDialog() {
		JFrame rootParent = JavaScore.getInstance();
		JDialog dialog = editorMap.get( fKey);
		if (dialog == null) {
			Cursor holdC = null;
			if (rootParent != null) holdC = rootParent.getCursor();
			if (rootParent != null) rootParent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			try {
    			if (worker != null) {
    				dialog = (JDialog) worker.get();
    			} else {
    				dialog = initializeEditor(rootParent);
    			}
    			editorMap.put( fKey,  dialog);
				logger.debug( "{} dialog created: {}", fKey, dialog.getClass().getName());
			} catch (Exception e) {
				Util.showError(e, fKey + "dialog FAILED: " + dialog.getClass().getName(), true);
			}
			if (rootParent != null) rootParent.setCursor(holdC);
		}
		editor = dialog;
		return dialog;
	}
	
	
	public void actionPerformed(ActionEvent notused) {
		show();
	}
	
	public boolean isVisible() {
		return (editor != null && editor.isVisible());
	}
	
	// override candidate only needed if the baseobject's geteditor is 
	// NOT the right editor(see ActionSplitBy...)
	public BaseEditor getBaseEditor( BaseEditorContainer parent) {
		return null;
	}
	
	public void show() {
		if (editor == null) {
			editor = getDialog();
		}
		
		if (editor instanceof DialogBaseEditor) {
			DialogBaseEditor dbe = (DialogBaseEditor) editor;
			dbe.setObject(getObject(), getBaseEditor(dbe));
			dbe.startUp();
		} else if (editor instanceof DialogBaseListEditor) {
			DialogBaseListEditor dbl = (DialogBaseListEditor) editor;
			dbl.setMasterList( getList(), (String) getValue( PANEL_TITLE));
			dbl.startUp();
		}

		// the below is a workaround for textfields that dont get their focus
		// java 7, mixed bag of Mac and javascore
	
		editor.setVisible(false);
		editor.setVisible(true);
		
	}
	
	private boolean openingInProgress = false;
	
	public void hide() {
		if (editor == null) return;
		editor.setVisible(false);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals( JavaScoreProperties.REGATTA)) {
			hide();
		} 
	}
	

	protected Regatta getRegatta() { return JavaScoreProperties.getRegatta(); }

	public final void windowClosing(WindowEvent event) {}
	public void windowActivated(WindowEvent event) {} // do nothing
	public void windowDeactivated(WindowEvent event) {} // do nothing
	public void windowDeiconified(WindowEvent event) {} // do nothing
	public void windowIconified(WindowEvent event) {} // do nothing
	public void windowOpened(WindowEvent event) {} // do nothing
	public void windowClosed(WindowEvent event) {} // do nothing
	
}
/**
 * $Log: ActionEditPreferences.java,v $
 */
