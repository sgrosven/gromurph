//=== File Prolog===========================================================
//This code was developed as part of the open source regatta scoring
//program, JavaScore.
//
//Version: $Id: UndoHandler.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
//Copyright Sandy Grosvenor, 2000-2015 
//Email sandy@gromurph.org, www.gromurph.org/javascore
//
//OSI Certified Open Source Software (www.opensource.org)
//This software is licensed under the GNU General Public License,
//available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.*;

/**
* Class for managing and processing undo/redo events within the Assistant.
* This class combines and extends the standard classes of 
* javax.swing.undo.UndoManager and UndoableEditSupport.  It also contains
* and manages standard Actions for Undo and Redo for putting into toolbars 
* and menus.
*/
public class UndoHandler extends UndoManager implements UndoableEditListener
{		
	public static final String UNDO_ICON = "/resources/images/Undo16.gif";
	public static final String REDO_ICON = "/resources/images/Redo16.gif";

	private UndoableEditSupport fUndoSupport;
	private Action fActionUndo;
	private Action fActionRedo;
	
	private Logger fLog = Logger.getLogger( getClass().getName());
				
	public UndoHandler()
	{
		super();
		
		fUndoSupport = new UndoableEditSupport();
		fUndoSupport.addUndoableEditListener( this);
		
		fActionUndo = new ActionUndo();
		fActionRedo = new ActionRedo();
		
		refreshUndoEnabled();
	}
		
	/**
	 * @see javax.swing.undo.UndoSupport.addUndoableEditListener
	 */
	public void addUndoableEditListener(UndoableEditListener listener)
	{
		fUndoSupport.addUndoableEditListener( this);
	}
		
	/**
	 * @see javax.swing.undo.UndoSupport.removeUndoableEditListener
	 */
	public void removeUndoableEditListener(UndoableEditListener listener)
	{
		fUndoSupport.removeUndoableEditListener( this);		
	}
		
	/**
	 * @see javax.swing.undo.UndoSupport.getUndoableEditListeners
	 */
	public UndoableEditListener[] getUndoableEditListeners()
	{
		return fUndoSupport.getUndoableEditListeners();	
	}
		
	/**
	 * @see javax.swing.undo.UndoableEditListener
	 */
	@Override public void undoableEditHappened(UndoableEditEvent evt)
	{
		UndoableEdit edit = evt.getEdit();
		addEdit(edit);
	}
		
	/**
	 * Executes and edit action and posts it to the undo queue for subsequent
	 * undo/redo.  This is a bit different from the UndoableEditSupport as it
	 * also PERFORMS the edit (by running the edit's redo().  The standard 
	 * UndoableEditSupport.postEdit only puts the edit in the undo queue. 
	 * @param edit
	 */
	public void postEdit( UndoableEdit edit)
	{
		fLog.info( "postEdit: " + edit.toString());
		fUndoSupport.postEdit(edit);
		edit.redo();
		refreshUndoEnabled();
	}
	
	@Override public void undo()
	{
		UndoableEdit toUndo = editToBeUndone();
		if (toUndo != null) 
		{
			fLog.info( "Undo: " + toUndo.toString());
		}
		super.undo();
	}
		
	@Override public void redo()
	{
		UndoableEdit toRedo = editToBeRedone();
		if (toRedo != null) 
		{
			fLog.info( "Redo: " + toRedo.toString());
		}
		super.redo();
	}
		
	/**
	 * resets the enabling on the undo/redo actions
	 *
	 */
	private void refreshUndoEnabled()
	{
		fActionUndo.setEnabled( canUndo());
		fActionRedo.setEnabled( canRedo());
	}

	public Action getUndoAction()
	{
		return fActionUndo;
	}
	
	public Action getRedoAction()
	{
		return fActionRedo;
	}
	
	private class ActionRedo extends AbstractAction
	{
		public ActionRedo()
		{
			super();
			putValue(Action.NAME, "Redo");
			putValue(Action.SHORT_DESCRIPTION, "Redo");
			putValue(
				Action.LONG_DESCRIPTION,
				"Re-does the most recent undone change in the Assistant");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( 'Y', InputEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, Util.getImageIcon( REDO_ICON));
		}

		public void actionPerformed(ActionEvent evt)
		{
			redo();
			refreshUndoEnabled();
		}		
		
	}
	
	/**
	*  undo action
	*/

	public class ActionUndo extends AbstractAction
	{
		public ActionUndo()
		{
			super();
			putValue(Action.NAME, "Undo");
			putValue(Action.SHORT_DESCRIPTION, "Undo");
			putValue(
				Action.LONG_DESCRIPTION,
				"Undoes the most recent change in the Assistant");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( 'Z', InputEvent.CTRL_MASK));
			putValue(Action.SMALL_ICON, Util.getImageIcon( UNDO_ICON));
		}

		public void actionPerformed(ActionEvent evt)
		{
			undo();
			refreshUndoEnabled();
		}				

	}

	
}



//=== Development History ====================================================
//
//$Log: UndoHandler.java,v $
//Revision 1.4  2006/01/15 21:10:35  sandyg
//resubmit at 5.1.02
//
//Revision 1.2  2006/01/11 02:27:14  sandyg
//updating copyright years
//
//Revision 1.1  2006/01/01 02:27:02  sandyg
//preliminary submission to centralize code in a new module
//
//Revision 1.3  2005/05/26 01:45:43  sandyg
//fixing resource access/lookup problems
//
//Revision 1.2  2004/04/10 20:49:39  sandyg
//Copyright year update
//
//Revision 1.1  2004/01/18 18:32:51  sandyg
//Now NO default on append/replace, must select one or the other.
//
//=== End Development History =================================================
