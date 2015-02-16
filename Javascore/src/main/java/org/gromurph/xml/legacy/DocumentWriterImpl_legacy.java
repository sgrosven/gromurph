// === File Prolog ==============================================================
// This code was developed by NASA, Goddard Space Flight Center, Code 588
// for the Science Goal Monitor (SGM) project.
//
// --- HEADER -------------------------------------------------------------------
//
// $Author: sandyg $
// $Date: 2006/01/15 21:10:48 $
// $Revision: 1.3 $
//
// See additional log/revision history at bottom of file.
//
// --- DISCLAIMER ---------------------------------------------------------------
//
// This software is provided "as is" without any warranty of any kind, either
// express, implied, or statutory, including, but not limited to, any
// warranty that the software will conform to specification, any implied
// warranties of merchantability, fitness for a particular purpose, and
// freedom from infringement, and any warranty that the documentation will
// conform to the program, or any warranty that the software will be error
// free.
//
// In no event shall NASA be liable for any damages, including, but not
// limited to direct, indirect, special or consequential damages, arising out
// of, resulting from, or in any way connected with this software, whether or
// not based upon warranty, contract, tort or otherwise, whether or not
// injury was sustained by persons or property or otherwise, and whether or
// not loss was sustained from or arose out of the results of, or use of,
// their software or services provided hereunder.
//
// === End File Prolog ==========================================================

package org.gromurph.xml.legacy;

import java.io.*;

import org.gromurph.util.Util;
import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentWriter;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.PersistentObject;
import org.xml.sax.SAXException;

/**
 * Writes a new XML document that persists some set of PersistentObjects.
 * <P>
 * There are two methods for writing a document. writeObject() will write a single object and all its contents. In that
 * case the single object's contents become the document root. writeObjects(), on the other hand, allow you to write an
 * array of objects and their contents, but in this case the array tags are wrapped within an enclosing root tag. Each
 * must be paired with its equivalent readObject()/readObjects() method for reading (i.e. they are not interchangeable).
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588 for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
 **/
public class DocumentWriterImpl_legacy implements IDocumentWriter {

	private String fFileDir;
	private String fFileName;

	/**
	 * Creates a new DocumentWriter to write to the specified output file
	 **/
	public DocumentWriterImpl_legacy(String fileDir, String fileName) throws IOException {
		fFileDir = fileDir;
		fFileName = fileName;

	}

	private Writer fWriter = null;

	/**
	 * Creates a new DocumentWriter to write to the specified output file
	 **/
	public DocumentWriterImpl_legacy(Writer w) throws IOException {
		fWriter = w; //new BufferedOutputStream(new FileOutputStream(f));
	}

	public PersistentNode createRootNode(String rootTag) {
		PersistentNode root = new DocumentNode(null, rootTag);
		return root;
	}

	/**
	 * Writes the specified array of objects and all their children to the output stream. The objects will be wrapped in
	 * a root tag with specified tag name. The output stream is closed before returning.
	 * 
	 * @param objects
	 *            array of objects to write
	 * @param rootName
	 *            name of root tag for document
	 * @throws DocumentException
	 *             thrown if document-related error occurs
	 * @throws IOException
	 *             thrown if I/O-related error occurs
	 **/
	public void writeObjects(PersistentObject[] objects, String rootName, String elementName) throws DocumentException,
			IOException {
		// Create the root node
		PersistentNode root = new DocumentNode(null, rootName);

		// Create child PersistentNodes from objects
		PersistentNode[] nodes = new PersistentNode[objects.length];
		for (int i = 0; i < nodes.length; ++i) {
			// Start with a blank child node
			nodes[i] = root.createChildElement(elementName);

			// Then instruct the object to write its state to the node
			objects[i].xmlWrite(nodes[i]);
		}

		saveObject(root, false);
	}

	/**
	 * Writes the specified object and all its children to the output stream. The output stream is closed before
	 * returning.
	 * 
	 * @param object
	 *            object to write
	 * @throws DocumentException
	 *             thrown if document-related error occurs
	 * @throws IOException
	 *             thrown if I/O-related error occurs
	 **/
	public void writeObject(PersistentObject object, String tagName) throws DocumentException, IOException {
		// Create the root node
		PersistentNode root = new DocumentNode(null, tagName);
		object.xmlWrite(root);

		saveObject(root, false);
	}

	private String fEncoding = XMLOutputter.DEFAULT_ENCODING;

	public String getEncoding() {
		return (fEncoding == null) ? XMLOutputter.DEFAULT_ENCODING : fEncoding;
	}

	public void setEncoding(String en) {
		fEncoding = en;
	}

	public void saveObject(PersistentNode root, boolean elementOnly) throws DocumentException, IOException {
		PrintWriter fOutput = null;
		try {
			if (fWriter == null) {

				File file = Util.getFile(fFileDir, fFileName);
				FileOutputStream fos = new FileOutputStream(file);
				fWriter = new OutputStreamWriter(fos, XMLOutputter.DEFAULT_ENCODING);
			}

			fOutput = new PrintWriter(fWriter);

			// XMLOutputter will write the XML
			XMLOutputter handler = new XMLOutputter(fOutput);

			// Traverse the root and its children, generating events
			// on the XMLOutputter, which will write the XML
			NodeSAXEventGenerator generator = new NodeSAXEventGenerator();
			generator.process(root, handler, elementOnly);
		}
		catch (SAXException ex) {
			throw new DocumentException("XML processing error", ex);
		}
		finally {
			if (fOutput != null) fOutput.close();
		}
	}
}

//=== Development History ======================================================
//
// $Log: DocumentWriter.java,v $
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
// Revision 1.9  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.8  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.6  2002/07/30 20:17:44  jjones_cvs
// Added writeObject() which provides different support from writeObjects().
//
// Revision 1.5  2002/07/24 19:12:47  jjones_cvs
// Added comments.
//
// Revision 1.4  2002/07/23 15:59:43  jjones_cvs
// DocumentWriter and supporting classes now pass tests.
//
// Revision 1.3  2002/07/22 18:59:14  jjones_cvs
// First full commit
//
// Revision 1.2  2002/07/17 19:16:35  jjones_cvs
// Added DocumentException
//
// Revision 1.1  2002/07/17 18:16:04  jjones_cvs
// Initial stub version
//
//=== End Development History ==================================================
