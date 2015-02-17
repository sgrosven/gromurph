// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseList.java,v 1.6 2006/07/09 03:01:24 sandyg Exp $
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
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentWriter;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.PersistentObject;
import org.gromurph.xml.XmlUtil;

/**
 * Standard wrap combining a Vector of objects, and support for TableModel and ListModel all into one
 * <P>
 * Subclasses need to define: <br>
 * - String databaseSelect() <br>
 * - BaseEditor getEditor() <br>
 * - int getColumnCount()
 * <P>
 * In order to avoid massive wrapping of code. To hook into the table/list models use the followign: getTableModel()
 * getListModel()
 * <P>
 * note that while getTableModel() could be substituted with this...its is NOT recommended.
 **/
public abstract class BaseList<T extends BaseObjectModel> extends java.util.ArrayList<T> implements PersistentObject {
	public static final String ADD_PROPERTY = "AddItem";
	public static final String REMOVE_PROPERTY = "RemoveItem";

	/**
	 * contains a SELECT statement for retrieving info from a database, override to return non-empty string if you care
	 */
	public String databaseSelect() {
		return "";
	}

	private static final long serialVersionUID = 1L;
	String fFileName;
	String fRootTag;
	String fElementTag;

	abstract public Class getContainingClass();

	@Override public boolean add(T obj) {
		boolean bb = super.add(obj);
		if (bb) fPcSupport.firePropertyChange(ADD_PROPERTY, null, obj);
		return bb;
	}

	@Override public void add(int i, T obj) {
		super.add(i, obj);
		fPcSupport.firePropertyChange(ADD_PROPERTY, null, obj);
	}

	public boolean remove(T obj) {
		boolean bb = super.remove(obj);
		if (bb) fPcSupport.firePropertyChange(REMOVE_PROPERTY, obj, null);
		return bb;
	}

	@Override public T remove(int i) {
		T obj = super.remove(i);
		fPcSupport.firePropertyChange(REMOVE_PROPERTY, obj, null);
		return obj;
	}

	@SuppressWarnings("unchecked")
	public <V extends BaseObjectModel> void sort() {
		Collections.sort(this);
	}

	public void setFileName(String inF) {
		fFileName = inF;
	}

	public String getFileName() {
		return fFileName;
	}

	public void setRootTag(String inF) {
		fRootTag = inF;
	}

	public String getRootTag() {
		return fRootTag;
	}

	public void setElementTag(String inF) {
		fElementTag = inF;
	}

	public String getElementTag() {
		return fElementTag;
	}

	public void xmlWriteToFile() throws IOException {
		xmlWriteToFile(null, this.getFileName(), this.getRootTag());
	}

	/**
	 * writes to disk in xml format
	 */
	public void xmlWriteToFile(String fileDir, String fileName, String rootTag) throws IOException {
		try {

			IDocumentWriter dw = XmlUtil.createDocumentWriter(fileDir, fileName);
			PersistentNode root = dw.createRootNode(rootTag);
			//PersistentNode listNode = root.createChildElement(rootTag);
			xmlWrite( root, fElementTag);
			dw.saveObject( root, false);
		}
		catch (DocumentException e) {
			throw new IOException(e.toString());
		}
	}

	//    @Override public void xmlWrite( PersistentNode node)
	//    {    
	//    	xmlWrite( node, fRootTag, fElementTag);
	//    }

	@Override public void xmlWrite(PersistentNode node) {
		if (getElementTag() == null) xmlWrite(node, node.getName() + "Item");
		else xmlWrite(node, getElementTag());
	}

	public void xmlWrite(PersistentNode listNode, String elementTag) {
		Object[] items = null;
		synchronized (this) {
			items = this.toArray();
		}

		for (int i = 0; i < items.length; i++) {
			BaseObjectModel obj = (BaseObjectModel) items[i];
			PersistentNode newnode = listNode.createChildElement( elementTag);
			obj.xmlWrite( newnode);
		}
		//return listNode;
	}

	public boolean xmlReadFromFile() throws IOException, DocumentException {
		return xmlReadFromFile(fFileName);
	}

	public boolean xmlReadFromFile(String fileName) throws IOException, DocumentException {
		File f = Util.getFile( fileName);
		if (f.exists()) {
			Reader r = new FileReader(f);
			PersistentNode root = XmlUtil.readDocument(r);
			xmlRead(root, this);
			return true;
		} else {
			Util.printlnException(this, new FileNotFoundException("Cannot find file: " + fileName), true);
			return false;
		}	
	}

	@SuppressWarnings("unchecked")
	@Override public void xmlRead(PersistentNode rootNode, Object rootObject) {
		PersistentNode[] kids2 = rootNode.getElements();

		for (int k = 0; k < kids2.length; k++) {
			try {
				PersistentNode n2 = kids2[k];
				Class cl = getContainingClass();
				//	        	Object x = cl.newInstance();
				//	        	T obj =  (T) cl.cast(x);
				BaseObjectModel obj = (BaseObjectModel) cl.newInstance();
				obj.xmlRead(n2, rootObject);
				add((T) obj);
			}
			catch (Exception e) {
				Util.printlnException(this, e, true);
			}
		}
	}

	private org.gromurph.javascore.gui.DefaultListModel listModel = null;
	
	public org.gromurph.javascore.gui.DefaultListModel getListModel() {
		if (listModel == null) listModel = new org.gromurph.javascore.gui.DefaultListModel(this);
		return listModel;
	}

	/**
	 * removes any/all members of the list that are .equals() to a default instance of the element class
	 */
	public void removeBlanks() {
		try {
			Collection<T> c = new ArrayList<T>(5);
			for (int i = 0; i < size(); i++) {
				if (((BaseObjectModel) get(i)).isBlank()) c.add(get(i));
			}
			removeAll(c);
		}
		catch (Exception e) {} // do nothing
	}

	//    //=========== Abstract TableModel methods
	//
	//    //Returns the number of columns managed by the data source object.
	//    public int getColumnCount() { return 0;}
	//
	//    //Returns the number of records managed by the data source object.
	//    public int getRowCount()
	//    {
	//        return fSize;
	//    }
	//
	//    //Returns an attribute value for the cell at columnIndex and rowIndex.
	//    public Object getValueAt(int rowIndex, int columnIndex)
	//    {
	//        return ((BaseObjectModel) lm.elementAt( rowIndex)).getColumnValue( columnIndex);
	//    }

	private PropertyChangeSupport fPcSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		fPcSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		fPcSupport.removePropertyChangeListener(listener);
	}

}

