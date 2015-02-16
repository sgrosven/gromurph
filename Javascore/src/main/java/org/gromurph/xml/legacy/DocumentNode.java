//=== File Prolog ==============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 588
//	for the Science Goal Monitor (SGM) project.
//
//--- HEADER -------------------------------------------------------------------
//
//  $Author: sandyg $
//  $Date: 2006/01/15 21:10:48 $
//  $Revision: 1.3 $
//
//  See additional log/revision history at bottom of file.
//
//--- DISCLAIMER ---------------------------------------------------------------
//
//	This software is provided "as is" without any warranty of any kind, either
//	express, implied, or statutory, including, but not limited to, any
//	warranty that the software will conform to specification, any implied
//	warranties of merchantability, fitness for a particular purpose, and
//	freedom from infringement, and any warranty that the documentation will
//	conform to the program, or any warranty that the software will be error
//	free.
//
//	In no event shall NASA be liable for any damages, including, but not
//	limited to direct, indirect, special or consequential damages, arising out
//	of, resulting from, or in any way connected with this software, whether or
//	not based upon warranty, contract, tort or otherwise, whether or not
//	injury was sustained by persons or property or otherwise, and whether or
//	not loss was sustained from or arose out of the results of, or use of,
//	their software or services provided hereunder.
//
//=== End File Prolog ==========================================================

package org.gromurph.xml.legacy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gromurph.xml.PersistentNode;

/**
 * The default implementation of PersistentNode. This is not a public class.
 * PersistentObjects should never try to call the DocumentNode constructor
 * directly (shouldn't be able to anyway).  Instead, to create new child
 * nodes, call PersistentNode.createElement().
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public class DocumentNode implements PersistentNode
{
	private PersistentNode fParent;
	private String         fName;
	private String         fText;
	private Map<String,String>            fAttributes;

	/**
	 * The list of child elements in the node.
	 * Elements stored as List instead of Map because if in map, Name
	 * would have been stored in two separate places (parent Map and fName).
	**/
	private List<PersistentNode>           fElements;

	/**
	 * Constructs a new DocumentNode that is a child node of the specified parent.
	 * Root nodes may be constructed by passing null as the parent.
	 *
	 * @param parent parent PersistentNode of this node
	**/
	public DocumentNode(PersistentNode parent, String tagName)
	{
		fParent = parent;
		fName = tagName; // should never have null name
		fText = null;
		fAttributes = new TreeMap<String,String>();
		fElements = new ArrayList<PersistentNode>();
	}

	/**
	 * Returns the parent node of the current node. Returns null if the
	 * current node is the root node.  This is a read-only property.
	 *
	 * @return parent node of the current node, or null if none
	**/
	public PersistentNode getParent()
	{
		return fParent;
	}

	/**
	 * Returns the name of the current node, or empty string if the
	 * node has no name.
	 *
	 * @return name of the current node
	**/
	public String getName()
	{
		return fName;
	}

	/**
	 * Returns the text value contained within the node, or null if the
	 * node does not contain text.
	 *
	 * @return the node's text value
	**/
	public String getText()
	{
		return fText;
	}

	/**
	 * Sets the node's text value. Text is an optional property of a node
	 * and is not required. By default a node contains no text (text is null).
	 *
	 * @param value the new text value
	**/
	public void setText(String value)
	{
		fText = value;
	}

	/**
	 * Returns the names of the attributes contained within the node.
	 *
	 * @return array of attribute names
	**/
	public String[] getAttributes()
	{
		Object[] keys = fAttributes.keySet().toArray();
		String[] keyStrings = new String[keys.length];
		for (int i = 0; i < keys.length; ++i)
		{
			keyStrings[i] = (String) keys[i];
		}
		return keyStrings;
	}

	/**
	 * Returns the value of the attribute with the specified name, or null
	 * if the specified attribute does not exist in the current node.
	 *
	 * @param  name name of attribute to retrieve
	 * @return value of attribute with specified name
	**/
	public String getAttribute(String name)
	{
		return (String) fAttributes.get(name);
	}

	/**
	 * Adds an attribute to the current node with the specified name and value.
	 * Replaces the value of an existing attribute with same name if one exists.
	 * Removes the existing attribute if value is null.
	 *
	 * @param name  name of attribute to set
	 * @param value value to associate with named attribute, or null if want to remove
	**/
	public void setAttribute(String name, String value)
	{
		if (value == null)
		{
			fAttributes.remove(name);
		}
		else
		{
			fAttributes.put(name, value);
		}
	}

	/**
	 * Returns true if the current node has an attribute with the specified name.
	 *
	 * @param name name of attribute to query
	 * @return true of attribute exists
	**/
	public boolean hasAttribute(String name)
	{
		return fAttributes.containsKey(name);
	}

	/**
	 * Returns an array of all child nodes within the current node.
	 * Returns an empty array if no children exist (never returns null).
	 *
	 * @return array of child nodes
	**/
	public PersistentNode[] getElements()
	{
		Object[] objs = fElements.toArray();
		PersistentNode[] elements = new PersistentNode[objs.length];
		for (int i = 0; i < elements.length; ++i)
		{
			elements[i] = (PersistentNode) objs[i];
		}
		return elements;
	}

	/**
	 * Returns the child node within the current node that has the specified
	 * name.  Returns null if no such child exists.
	 *
	 * @param name name of child node to get
	 * @return     child node with specified name, or null if none exists
	**/
	public PersistentNode getElement(String name)
	{
		for (Iterator iter = fElements.iterator(); iter.hasNext();)
		{
			PersistentNode node = (PersistentNode) iter.next();
			if (node.getName().equals(name))
			{
				return node;
			}
		}
		return null;
	}

	/**
	 * Creates a new child node that has the current node as its parent.
	 *
	 * @return new child node object
	**/
	public PersistentNode createChildElement( String name)
	{
		PersistentNode newNode = new DocumentNode(this, name);
		fElements.add(newNode);
		return newNode;
	}

	/**
	 * Returns true if the current node has a child node with the specified
	 * name.  Returns null if no such node exists.
	 *
	 * @param name name of child node to query
	 * @return     true if child node exists
	**/
	public boolean hasElement(String name)
	{
		for (Iterator iter = fElements.iterator(); iter.hasNext();)
		{
			if (((PersistentNode) iter.next()).getName().equals(name))
			{
			    return true;
			}
		}
		return false;
	}

	/**
	 * Simple string representation of the node contents.
	 *
	 * @return string representation of node contents
	**/
	@Override public String toString()
	{
		return "Node: " + fName + ", Text: " + fText + ", Attrs: "
				+ fAttributes.size() + ", Elements: " + fElements.size();
	}
}

//=== Development History ======================================================
//
// $Log: DocumentNode.java,v $
// Revision 1.3  2006/01/15 21:10:48  sandyg
// resubmit at 5.1.02
//
// Revision 1.1  2006/01/01 02:27:02  sandyg
// preliminary submission to centralize code in a new module
//
// Revision 1.1.4.2  2005/11/19 20:34:55  sandyg
// last of java 5 conversion, created swingworker, removed threads packages.
//
// Revision 1.1.2.1  2005/08/19 01:51:19  sandyg
// Change to standard java xml libraries
//
// Revision 1.8  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.7  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.5  2003/03/03 18:36:01  jjones_cvs
// Fixed bug where attribute key order was not guaranteed. Now it is.
//
// Revision 1.4  2002/07/30 20:17:42  jjones_cvs
// Renamed createElement() to createChildElement().
//
// Revision 1.3  2002/07/23 20:31:13  jjones_cvs
// Added javadoc. Changed setAttribute to remove if value is null.
//
// Revision 1.2  2002/07/23 15:59:43  jjones_cvs
// DocumentWriter and supporting classes now pass tests.
//
// Revision 1.1  2002/07/22 18:58:56  jjones_cvs
// First full commit
//
//=== End Development History ==================================================
