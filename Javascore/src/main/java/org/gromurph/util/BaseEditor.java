// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseEditor.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseEditor - covering class for Panels that edit things. Keeps a saved version of the object being editted and
 * provides support for "restoring" that object.
 * <P>
 * BaseEditors are designed to be put into parent BaseEditorContainers. The BaseEditor may put toolbar and/or menu items
 * into the parent container in their start() and stop() methods. And should rely on the container for
 * Save/Restore/Cancel buttons as parent may need
 * 
 **/
public abstract class BaseEditor<T extends BaseObjectModel> extends PanelStartStop implements PropertyChangeListener {

    protected Logger logger = LoggerFactory.getLogger( this.getClass());

	public static final String EMPTY = "";
	public static final String NEWLINE = Util.NEWLINE;
	public static final String BLANK = " ";
	public static final String PERIOD = ".";

	private String fTitle = "";

	public String getTitle() {
		return fTitle;
	}

	public void setTitle(String t) {
		fTitle = t;
	}

	/**
	 * the backup unchanged version of the object being editted
	 **/
	protected T fObjectBackup;

	/**
	 * the current, maybe unsaved, version of object being editted. Subclasses that keep their own version for casting
	 * purposes should be careful to make sure that the two stay pointed to same object
	 **/
	protected T fObjectCurrent = null;

	public BaseEditor(BaseEditorContainer parent) {
		super();
		//setPreferredSize( new Dimension(
		//    getInsets().left + getInsets().right + 220,
		//    getInsets().top + getInsets().bottom + 130));

		setEditorParent(parent);
		setName(getClass().getName());
	}

	public void restore() {
		if ((fObjectCurrent != null) && (fObjectBackup != null)) {
			restore(fObjectCurrent, fObjectBackup);
			updateFields();
		}
	}

	/**
	 * restores the editable fields. It is a subclass's responsibility to keep restore functional at its level
	 **/
	public void restore(T activeM, T backupM) {
		try {
			BaseObject active = (BaseObject) activeM;
			BaseObject backup = (BaseObject) backupM;
			active.setLastModified(backup.getLastModified());
			active.setCreateDate(backup.getCreateDate());
		} catch (ClassCastException e) {
			Util.showError(e, true);
		} // do nothing
	}

	/**
	 * called when the parent dialog closes with an OK
	 */
	public void exitOK() {}

	private boolean doOKOnWindowClose = true;

	public void setExitOKonWindowClose(boolean doOK) {
		doOKOnWindowClose = doOK;
	}

	public boolean isExitOKOnWindowClose() {
		return doOKOnWindowClose;
	}

	/**
	 * called when parent dialog closes after a CANCEL
	 */
	public void exitCancel() {}

	/**
	 * returns true if backup and current objects are NOT equal
	 **/
	public boolean changesPending() {
		if (fObjectCurrent == null && fObjectBackup == null)
			return false;
		else if (fObjectCurrent == null || fObjectBackup == null)
			return true;
		else
			return !fObjectCurrent.equals(fObjectBackup);
	}

	/**
	 * sets the object, dumps the old backup and sets new backup to clone/restore of current object. Child classes will
	 * usually override this, but should be CAREFUL to not forget to call this parent version
	 **/
	public void setObject(T obj) throws ClassCastException {
		if (obj != null) {
			fObjectCurrent = obj;
			if (fObjectBackup == null) {
				try {
					fObjectBackup = (T) obj.getClass().newInstance();
				} catch (Exception e) {
					logger.warn( "unable to make newinstance of object", e);
				}
			}

			if (fObjectBackup != null) {
				restore(fObjectBackup, fObjectCurrent);
			}
		} else {
			fObjectCurrent = null;
		}
		updateFields();
	}

	public T getObject() {
		return fObjectCurrent;
	}


	protected boolean isStarted = false;

	/**
	 * fired when the editor is about to become active to the user
	 **/
	@Override public void startUp() {
		updateFields();

		if (!isStarted) {
			isStarted = true;
			start();
		}
		
	}

	/**
	 * fired when the editor is about to become de-active to the user
	 **/
	@Override public void shutDown() {
		if (isStarted) {
			isStarted = false;
			stop();
		}
	}

	BaseEditorContainer fEditorParent;

	/**
	 * sets the parent container holding this BaseEditor
	 **/
	public void setEditorParent(BaseEditorContainer parent) {
		if (fEditorParent != null && fEditorParent != parent) {
			logger.warn("*** parent change!" + this.getClass().toString());
		}
		fEditorParent = parent;
		if (fEditorParent == null) {
			logger.warn("*** NULL Parent" + this.getClass().toString());
		}
	}

	/**
	 * returns the parent container.
	 **/
	public BaseEditorContainer getEditorParent() {
		return fEditorParent;
	}

}
/**
 * $Log: BaseEditor.java,v $ Revision 1.4 2006/01/15 21:10:34 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.8.4.2 2005/11/26 17:45:15 sandyg implement race weight & nondiscardable, did some gui test cleanups.
 * 
 * Revision 1.8.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.8 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.7 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.6 2003/03/30 00:05:50 sandyg moved to eclipse 2.1
 * 
 * Revision 1.5 2003/03/19 02:38:17 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.4 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
