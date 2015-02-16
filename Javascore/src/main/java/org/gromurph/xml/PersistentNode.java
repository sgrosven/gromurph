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

package org.gromurph.xml;

/**
 * Data structure that contains state read or written via the PersistentObject
 * interface. PersistentObjects read and write their state to/from an
 * object of this interface. PersistentNode mirrors the concepts found
 * in XML including attributes and elements.  Elements are PersistentNodes
 * that are children of a given node.  Attributes are optional name/value
 * pairs where the name and value are strings.  A node may also contain
 * a single text value which is meant to represent the node's primary
 * data value, if appropriate.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public interface PersistentNode
{
	/**
	 * Returns the parent node of the current node. Returns null if the
	 * current node is the root node.  This is a read-only property.
	 *
	 * @return parent node of the current node, or null if none
	**/
	public PersistentNode getParent();

	/**
	 * Returns the name of the current node, or empty string if the
	 * node has no name.
	 *
	 * @return name of the current node
	**/
	public String getName();

	/**
	 * Returns the text value contained within the node, or null if the
	 * node does not contain text.
	 *
	 * @return the node's text value
	**/
	public String getText();

	/**
	 * Sets the node's text value. Text is an optional property of a node
	 * and is not required. By default a node contains no text (text is null).
	 *
	 * @param value the new text value
	**/
	public void setText(String value);

	/**
	 * Returns the names of the attributes contained within the node.
	 *
	 * @return array of attribute names
	**/
	public String[] getAttributes();

	/**
	 * Returns the value of the attribute with the specified name, or null
	 * if the specified attribute does not exist in the current node.
	 *
	 * @param  name name of attribute to retrieve
	 * @return value of attribute with specified name
	**/
	public String getAttribute(String name);

	/**
	 * Adds an attribute to the current node with the specified name and value.
	 * Replaces the value of an existing attribute with same name if one exists.
	 * Removes the existing attribute if value is null.
	 *
	 * @param name  name of attribute to set
	 * @param value value to associate with named attribute, or null if want to remove
	**/
	public void setAttribute(String name, String value);

	/**
	 * Returns true if the current node has an attribute with the specified name.
	 *
	 * @param name name of attribute to query
	 * @return true of attribute exists
	**/
	public boolean hasAttribute(String name);

	/**
	 * Returns an array of all child nodes within the current node.
	 * Returns an empty array if no children exist (never returns null).
	 *
	 * @return array of child nodes
	**/
	public PersistentNode[] getElements();

	/**
	 * Returns the child node within the current node that has the specified
	 * name.  Returns null if no such child exists.
	 *
	 * @param name name of child node to get
	 * @return     child node with specified name, or null if none exists
	**/
	public PersistentNode getElement(String name);

	/**
	 * Creates a new child node that has the current node as its parent.
	 *
	 * @return new child node object
	**/
	public PersistentNode createChildElement( String name);

	/**
	 * Returns true if the current node has a child node with the specified
	 * name.  Returns null if no such node exists.
	 *
	 * @param name name of child node to query
	 * @return     true if child node exists
	**/
	public boolean hasElement(String name);
}

//=== Development History ======================================================
//
// $Log: PersistentNode.java,v $
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
// Revision 1.6  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.5  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.3  2002/07/30 20:17:46  jjones_cvs
// Renamed createElement() to createChildElement().
//
// Revision 1.2  2002/07/23 20:27:19  jjones_cvs
// Completed Javadoc
//
// Revision 1.1  2002/07/22 18:58:59  jjones_cvs
// First full commit
//
//=== End Development History ==================================================
