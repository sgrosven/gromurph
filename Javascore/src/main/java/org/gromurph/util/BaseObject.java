// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseObject.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.gromurph.xml.DocumentException;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseObject implements BaseObjectModel, Cloneable, Serializable, PropertyChangeListener,
		Comparable, TreeNode, MutableTreeNode {
	//===  NOTE NOTE: these should be overridden in subclass that
	// want to have table model support
	public Object getColumnValue(int colIndex) {
		return null;
	}

	protected Logger logger = LoggerFactory.getLogger( this.getClass());
	
	public void setColumnValue(Object object, int colIndex) {}

	// ABSTRACT
	// compareTo( object obj) is not defined, but it required

	/** these should also be defined **/
	//public static int getColumnCount()
	//public static Object getColumnName( int colIndex)

	private static final long serialVersionUID = "org.gromurph.BaseObject.100".hashCode();

	private long fLastModified;
	private long fCreateDate;

	public static final String LASTMODIFIEDDATE_PROPERTY = "LastModifiedDate".intern();
	public static final String RESTORE_PROPERTY = "restore";

	@Override
	public abstract boolean equals(Object obj);

	public BaseObject() {
		super();
		long t = System.currentTimeMillis();
		fCreateDate = t;
		fLastModified = t; // want times to be the same
	}

	/**
	 * default implementation, always false
	 */
	public boolean isBlank() {
		return false;
	}

	public String getKey() {
		return Integer.toString(hashCode());
	}

	/**
	 * this is to serve as parent for a "deep" clone concept wherein child objects are cloned
	 **/
	public Object deepClone() {
		Object c = clone();
		return c;
	}

	/**
	 * to be called in Junit tests to perform a "deeper" equality check than we may need for performance purposes
	 * elsewhere. If not overridden by subclasses merely calls the regular equals() method. Notably util.UtilTestCase
	 * calls junitEquals on xmlEquals checks
	 * 
	 * @param obj
	 * @return
	 */
	public boolean junitEquals(Object obj) {
		return equals(obj);
	}

	/**
	 * shallow clone, sub-objects cloned only where necessary for object integrity
	 **/
	@Override
	public Object clone() {
		BaseObject newSO = null;
		try {
			newSO = (BaseObject) super.clone();
			newSO.fCreateDate = this.fCreateDate;
			newSO.fLastModified = this.fLastModified;
		}
		catch (CloneNotSupportedException e) {
			// does nothing
		}

		return newSO;
	}

	public void propertyChange(PropertyChangeEvent ev) {
		// do nothing at this level
	}

	/**
	 * retrieves createDate
	 */
	public long getCreateDate() {
		return fCreateDate;
	}

	/**
	 * should only be used in serialization related stuff hence is protected
	 **/
	protected void setCreateDate(long inDate) {
		fCreateDate = inDate;
	}

	/**
	 * retrieves LastModified date
	 */
	public long getLastModified() {
		return fLastModified;
	}

	/**
	 * sets the lastmodified date to the current system date/time
	 */
	public void setLastModified() {
		setLastModified(System.currentTimeMillis());
	}

	/**
	 * sets the last modified date
	 * 
	 * @param inDate
	 *            new last modified in milleseconds
	 */
	public void setLastModified(long inDate) {
		fLastModified = inDate;
	}

	// ===========
	// PROPERTYCHANGESUPPORT/PROPERTYCHANGELISTENER
	// ===========

	transient private ListenerList listeners;

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be added
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) listeners = new ListenerList();
		listeners.addListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * 
	 * @param listener
	 *            The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) listeners.removeListener(listener);
	}

	/**
	 * Report a bound property update to any registered listeners. No event is fired if old and new are equal and
	 * non-null.
	 * 
	 * @param propertyName
	 *            The programmatic name of the property * that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (oldValue != null && oldValue.equals(newValue)) return;

		ListenerList targets;
		synchronized (this) {
			if (listeners == null) { return; }
			targets = (ListenerList) listeners.clone();
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

		for (int i = 0; i < targets.size(); i++) {
			PropertyChangeListener target = (PropertyChangeListener) targets.get(i);
			target.propertyChange(evt);
		}
	}

	public boolean xmlReadFromReader(Reader rdr) throws IOException {
		return xmlReadFromReader(rdr, this);
	}

	public boolean xmlReadFromReader(Reader rdr, Object toproot) throws IOException {
		try {
			PersistentNode root = XmlUtil.readDocument( rdr);
			xmlRead(root, toproot);
			return true;
		}
		catch (DocumentException de) {
			throw new IOException(de.toString());
		}
	}

	/**
	 * writes to disk in xml format
	 */
	public void xmlWriteToFile(String fileDir, String fileName, String rootTag) throws IOException {
		try {
			XmlUtil.writeDocument( fileDir,  fileName, this, rootTag);
		}
		catch (DocumentException de) {
			throw new IOException(de.toString());
		}
	}

	/**
	 * writes to disk in xml format
	 */
	public void xmlWriteToWriter(Writer w, String rootTag) throws IOException {
		try {
			XmlUtil.writeDocument( w,  this,  rootTag);
		}
		catch (DocumentException de) {
			throw new IOException(de.toString());
		}
	}

	// ==================== Tree Node INTERFACE
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public TreeNode getParent() {
		return null;
	}

	public int getIndex(TreeNode node) {
		return -1;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public Enumeration children() {
		return null;
	}

	public void insert(MutableTreeNode child, int index) {
		// do nothing
	}

	public void remove(int index) {
		// do nothing
	}

	public void remove(MutableTreeNode node) {
		// do nothing
	}

	public void setUserObject(Object object) {
		// do nothing
	}

	public void removeFromParent() {
		// do nothing
	}

	public void setParent(MutableTreeNode newParent) {
		// do nothing
	}

}
/**
 * $Log: BaseObject.java,v $ Revision 1.4 2006/01/15 21:10:34 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.10.2.1 2005/06/26 22:47:22 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.10 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.9 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.8 2003/11/23 23:14:28 sandyg upgraded to j2se 1.4.2, uses built in XML now
 * 
 * Revision 1.7 2003/04/27 21:35:33 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.6 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.5 2003/03/28 03:07:50 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.4 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
