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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX DefaultHandler that generates a tree of PersistentNode objects
 * based on the contents of the XML document.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public class DocumentContentHandler extends DefaultHandler
{
	private PersistentNode fDocument;
	private PersistentNode fCurrentNode;

	/**
	 * Constructs a new DocumentContentHandler with a null Document.
	 * The Document will be constructed by parsing a document with this
	 * handler.
	**/
	public DocumentContentHandler()
	{
		fDocument = null;
		fCurrentNode = null;
	}

	/**
	 * Returns the root of the tree of PersistentNodes that were generated
	 * after a successful parse.
	 *
	 * @return the root of the parsed document, or null if no parse completed yet
	**/
	public PersistentNode getDocument()
	{
		return fDocument;
	}

	/**
	 * Creates new PersistentNodes and sets their name and attributes.
	**/
	@Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
		PersistentNode node;

		// Create the node object
		if (fDocument == null)
		{
			// This is root element
			fDocument = new DocumentNode(null, qName);
			node = fDocument;
		}
		else
		{
			// Create child node of current node
			node = fCurrentNode.createChildElement(qName);
		}

		for (int i = 0; i < attributes.getLength(); ++i)
		{
			node.setAttribute(attributes.getQName(i), attributes.getValue(i));
		}

		// This node now becomes the current node
		fCurrentNode = node;
    }

	/**
	 * Finishes the current node in the hierarchy and prepares to move to
	 * the next node.
	**/
    @Override public void endElement(String uri, String localName, String qName) throws SAXException
    {
		// Current node is finished, so go to its parent
		fCurrentNode = fCurrentNode.getParent();
    }

	/**
	 * Adds text to the current node.
	**/
    @Override public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (fCurrentNode.getText() == null)
		{
			fCurrentNode.setText(String.valueOf(ch, start, length).trim());
		}
		else
		{
			StringBuffer buf = new StringBuffer(fCurrentNode.getText());
			buf.append(ch, start, length);
			fCurrentNode.setText(buf.toString().trim());
		}
	}
}

//=== Development History ======================================================
//
// $Log: DocumentContentHandler.java,v $
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
// Revision 1.5  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.4  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.2  2002/07/30 20:17:40  jjones_cvs
// Renamed createElement() to createChildElement().
//
// Revision 1.1  2002/07/24 19:11:39  jjones_cvs
// First version
//
//=== End Development History ==================================================
