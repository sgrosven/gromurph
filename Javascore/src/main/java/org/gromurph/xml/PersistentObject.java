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

import java.io.Serializable;

/**
 * Defines behavior for persisting an object's state as a node in some
 * collection of elements and attributes, which is ultimately stored as
 * an XML document.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public interface PersistentObject extends Serializable
{
	/**
	 * Reads the state of the object from the contents of some PersistentNode.
	 * The implementer should populate its internal fields from values contained
	 * within the node.
	 *
	 * @param node the node that contains the object's state
	**/
	public void xmlRead(PersistentNode node, Object rootObject);

	/**
	 * creates a child element of the specified parentNode, sets its name
	 * and then writes itself to the child node.  Returns the node (although
	 * node has already been set into the parentNode.
	 *
	 * @param node the new node that will represent the state of the object
	**/
	public void xmlWrite(PersistentNode node);
}

//=== Development History ======================================================
//
// $Log: PersistentObject.java,v $
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
// Revision 1.6  2004/05/30 17:52:46  sgrosvenor_cvs
// Working on getting FIRE1 scenario fully working under DAO
//
// Revision 1.5  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.4  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.2  2002/07/23 20:31:44  jjones_cvs
// Mods to javadoc.
//
// Revision 1.1  2002/07/22 18:59:07  jjones_cvs
// First full commit
//
//=== End Development History ==================================================
