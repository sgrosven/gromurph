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

import java.io.*;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentReader;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.PersistentObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads a document consisting of one or more PersistentObjects which
 * have been persisted to XML. This class reads XML from some data source
 * and instantiates PersistentObjects based on the contents.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588
 * for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
**/
public class DocumentReaderImpl_legacy implements IDocumentReader
{
    public static final String CLASS_PROPERTY = "class";

	/**
	 * Stream containing the XML.
	**/
	private InputSource fInput;

	/**
	 * Creates a new DocumentReader for reading objects from the
	 * specified input stream.
	 *
	 * @param stream will read objects from this stream
	**/
	public DocumentReaderImpl_legacy(InputStream stream)
	{
		fInput = new InputSource(stream);
	}

	/**
	 * Creates a new DocumentReader for reading objects from the
	 * specified URL.
	 *
	 * @param url will read objects from this location
	 * @throws IOException thrown if I/O-related error occurs
	**/
	public DocumentReaderImpl_legacy(URL url) throws IOException
	{
		fInput = new InputSource( url.openStream());
	}

	/**
	 * Creates a new DocumentReader for reading objects from the
	 * specified input file.
	 *
	 * @param f will read objects from this file
	 * @throws FileNotFoundException if unable to open input file
	**/
	public DocumentReaderImpl_legacy(File f) throws FileNotFoundException
	{
		fInput = new InputSource( new BufferedInputStream(new FileInputStream(f)));
	}

	/**
	 * Creates a new DocumentReader for reading objects from the
	 * specified Reader
	 *
	 * @param f will read objects from this file
	**/
	public DocumentReaderImpl_legacy(Reader r) 
	{
		fInput = new InputSource( r);
	}

	/**
	 * Reads the contents of the document from the input stream,
	 * instantiates objects from the document contents, and returns the new
	 * objects.  The stream is closed before returning.
	 * <P>
	 * The XML document must follow the following form: a single
	 * root tag (any name) containing one or more branches each of which
	 * contains the node information for a single PersistentObject of
	 * class specified as an argument to this method. These PersistentObjects
	 * may contain any configuration of child objects that it wishes
	 * (via its readObject() and writeObject() methods), but DocumentReader
	 * must know the class to instantiate for the top-level objects.
	 *
	 * @param objectType PersistentObject impl to read from the input stream
	 * @return new objects created from contents of input stream
	 * @throws DocumentException thrown if document-related error occurs
	 * @throws IOException thrown if I/O-related error occurs
	**/
	public PersistentObject[] readObjects(Class objectType) throws DocumentException, IOException
	{
		// Ensure objectType is a PersistentObject
		if (!PersistentObject.class.isAssignableFrom(objectType))
		{
			throw new IllegalArgumentException(
					"DocumentReader.readObjects() argument must implement PersistentObject interface");
		}

		// Read the document
		PersistentNode root = readDocument();
		if (root == null)
		{
			throw new DocumentException("Unable to reconstruct document");
		}

		// Create objects from nodes
		PersistentNode[] objNodes = root.getElements();
		PersistentObject[] objects = new PersistentObject[objNodes.length];
		for (int i = 0; i < objNodes.length; ++i)
		{
			objects[i] = createObject(objectType, objNodes[i]);
		}

		return objects;
	}

	/**
	 * Reads the contents of the document from the input stream,
	 * instantiates objects from the document contents, and returns the new
	 * objects.  The stream is closed before returning.
	 * <P>
	 * readObject() differs from readObjects() in that it returns a single
	 * PersistentObject derived from the root of the document tree.
	 * Thus there is no enclosing root tag - the root *is* the PersistentNode.
	 *
	 * @param objectType PersistentObject impl to read from the input stream
	 * @return new object created from contents of input stream
	 * @throws DocumentException thrown if document-related error occurs
	 * @throws IOException thrown if I/O-related error occurs
	**/
	public PersistentObject readObject(Class objectType) throws DocumentException, IOException
	{
		// Ensure objectType is a PersistentObject
		if (!PersistentObject.class.isAssignableFrom(objectType))
		{
			throw new IllegalArgumentException(
					"DocumentReader.readObjects() argument must implement PersistentObject interface");
		}

		// Read the document
		PersistentNode root = readDocument();
		if (root == null)
		{
			throw new DocumentException("Unable to reconstruct document");
		}

		// Create object from node and return it
		return createObject(objectType, root);
	}
    
    /**
     * Reads the contents of the document from the input stream,
     * instantiates objects from the document contents, and returns the new
     * objects.  The stream is closed before returning.
     * The type of the newly created object is determined from the document.
     * <P>
     * readObject() differs from readObjects() in that it returns a single
     * PersistentObject derived from the root of the document tree.
     * Thus there is no enclosing root tag - the root *is* the PersistentNode.
     *
     * @return new object created from contents of input stream
     * @throws DocumentException thrown if document-related error occurs
     * @throws IOException thrown if I/O-related error occurs
    **/
    public PersistentObject readObject() throws DocumentException, IOException
    {
        // Read the document
        PersistentNode root = readDocument();
        if (root == null)
        {
            throw new DocumentException("Unable to reconstruct document");
        }

        if (!root.hasAttribute(CLASS_PROPERTY))
        {
            throw new DocumentException("Document has no root class type defined.");
        }
        
        // Get root object type from class attribute
        Class objectType = null;
        try
        {
            objectType = Class.forName(root.getAttribute(CLASS_PROPERTY));
        }
        catch (ClassNotFoundException e)
        {
            throw new DocumentException("Root class type not found", e);
        }

        // Create object from node and return it
        return createObject(objectType, root);
    }

    private static final String[] sEncodings = new String[] { null, "UTF-8", "ISO-8859-1", "US-ASCII"};
    
    private String fEncoding = XMLOutputter.DEFAULT_ENCODING;  // UTF-8
    
    public String getEncoding()
    {
    	return (fEncoding == null) ? XMLOutputter.DEFAULT_ENCODING : fEncoding;	
    }
    
	/**
	 * Reads the document contents and returns the root PersistentNode.
	 *
	 * @return root node for the document
	 * @throws DocumentException thrown if document-related error occurs
	 * @throws IOException thrown if I/O-related error occurs
	**/
	public PersistentNode readDocument() throws DocumentException, IOException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);

		// Create content handler which makes PersistentNodes
		DocumentContentHandler handler = new DocumentContentHandler();
		Exception lastE = null;
		
	    for (int i = 0; i < sEncodings.length; i++)
	    {
			try
			{
				fEncoding = sEncodings[i];
				fInput.setEncoding( fEncoding);
				SAXParser saxParser = factory.newSAXParser();
				saxParser.parse( fInput, handler);
				// Get PersistentNode root from handler
				PersistentNode node = handler.getDocument();
				lastE = null;
				return node;
			}
			catch (ParserConfigurationException ex)
			{
				Throwable cause = ex.getCause();
				String detail = (cause == null) ? ex.toString() : cause.toString();
				lastE = new DocumentException( detail, ex);
			}
			catch (SAXException ex)
			{
				Throwable cause = ex.getCause();
				String detail = (cause == null) ? ex.toString() : cause.toString();
				lastE = new DocumentException( detail, ex);
			}
	    }
	    throw new IOException( lastE.toString());
	}

	/**
	 * Creates an object of objectType using the data in PersistentNode.
	 *
	 * @param objectType creates instance of this type
	 * @param node       populates with data from this node
	 * @return           new object instance
	 * @throws DocumentException thrown if document-related error occurs
	**/
	protected PersistentObject createObject(Class objectType, PersistentNode node) throws DocumentException
	{
		PersistentObject object = null;

		// Create an instance of objectType
		try
		{
			object = (PersistentObject) objectType.newInstance();
		}
		catch (IllegalAccessException ex)
		{
			throw new DocumentException("Unable to instantiate " + objectType.getName());
		}
		catch (InstantiationException ex)
		{
			throw new DocumentException("Unable to instantiate " + objectType.getName());
		}

		// Pass the object its node, allowing it to populate its contents
		object.xmlRead(node, object);
		return object;
    }
}

//=== Development History ======================================================
//
// $Log: DocumentReader.java,v $
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
// Revision 1.11  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.10  2004/03/01 16:09:59  sgrosvenor_cvs
// Adding AbstractSgmObjectDocumentReader to keep correct read-time xml object cache
//
// Revision 1.9  2004/02/18 20:50:55  jjones_cvs
// Removed initializeCampaign() method and instead retrieve campaign
// details from XML files.
//
// Revision 1.8  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.6  2002/07/30 20:17:43  jjones_cvs
// Added readObject() which provides different support from readObjects().
//
// Revision 1.5  2002/07/24 19:12:46  jjones_cvs
// First working version - passes tests.
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
// Revision 1.1  2002/07/17 18:09:32  jjones_cvs
// Initial stub version
//
//=== End Development History ==================================================
