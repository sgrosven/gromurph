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

import org.gromurph.xml.PersistentNode;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Given a PersistentNode and a SAX DefaultHandler, traverses the contents
 * of the node and generates SAX events on the DefaultHandler.  This allows
 * the DefaultHandler to perform its functions on the contents of the node
 * as if it were an XML document.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public class NodeSAXEventGenerator
{
	/**
	 * Constructs a new NodeSAXEventGenerator.
	 * NodeSAXEventGenerator stores no state, so multiple calls to
	 * process are allowed.
	**/
	public NodeSAXEventGenerator()
	{
	}

	/**
	 * Traverses a PersistentNode and its children, generating SAX
	 * events by calling methods on the DefaultHandler.
	 *
	 * @param node    traverses this node and its children
	 * @param handler fires events on this handler
	 * @throws SAXException possibly thrown by the handler
	**/
	public void process(PersistentNode node, DefaultHandler handler, boolean elementOnly) throws SAXException
	{
		if (!elementOnly) handler.startDocument();

		processElement(node, handler);

		handler.endDocument();
	}

	/**
	 * Processes an individual element by generating SAX events for its contents.
	 *
	 * @param element  traverses this element and its children
	 * @param handler  fires events on this handler
	 * @throws SAXException possibly thrown by the handler
	**/
	public void processElement(PersistentNode element, DefaultHandler handler) throws SAXException
	{
		String name = element.getName();

		handler.startElement(null, name, name, createAttributes(element));

		// Process text
		if (element.getText() != null)
		{
			handler.characters(element.getText().toCharArray(), 0, element.getText().length());
		}

		// Process all the child nodes
		PersistentNode[] children = element.getElements();
		for (int i = 0; i < children.length; ++i)
		{
			processElement(children[i], handler);
		}

		handler.endElement(null, name, name);
	}

	/**
	 * Creates a SAX Attributes object containing the attributes of a PersistentNode.
	 *
	 * @param element creates Attributes representing this node
	 * @return        new object implementing the SAX Attributes interface.
	**/
	protected Attributes createAttributes(PersistentNode element)
	{
		String[] attrNames = element.getAttributes();
		AttributesImpl attrs = new AttributesImpl();
		for (int i = 0; i < attrNames.length; ++i)
		{
			attrs.addAttribute("", attrNames[i], attrNames[i], "", element.getAttribute(attrNames[i]));
		}
		return attrs;
	}
}

//=== Development History ======================================================
//
// $Log: NodeSAXEventGenerator.java,v $
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
// Revision 1.5  2004/03/08 16:47:41  jjones_cvs
// Fixed all Javadoc warnings.
//
// Revision 1.4  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.2  2002/07/23 15:59:43  jjones_cvs
// DocumentWriter and supporting classes now pass tests.
//
// Revision 1.1  2002/07/22 18:58:57  jjones_cvs
// First full commit
//
//=== End Development History ==================================================
