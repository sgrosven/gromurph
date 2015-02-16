package org.gromurph.xml.jaxb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentReader;
import org.gromurph.xml.PersistentNode;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentReaderImpl_jaxb implements IDocumentReader
{
    public static final String CLASS_PROPERTY = "class";

	/**
	 * Stream containing the XML.
	**/
	private InputSource fInput;

	/**
	 * Creates a new JaxbDocumentReader for reading objects from the
	 * specified input stream.
	 *
	 * @param stream will read objects from this stream
	**/
	public DocumentReaderImpl_jaxb(InputStream stream)
	{
		fInput = new InputSource(stream);
	}

	/**
	 * Creates a new JaxbDocumentReader for reading objects from the
	 * specified URL.
	 *
	 * @param url will read objects from this location
	 * @throws IOException thrown if I/O-related error occurs
	**/
	public DocumentReaderImpl_jaxb(URL url) throws IOException
	{
		fInput = new InputSource( url.openStream());
	}

	/**
	 * Creates a new JaxbDocumentReader for reading objects from the
	 * specified input file.
	 *
	 * @param f will read objects from this file
	 * @throws FileNotFoundException if unable to open input file
	**/
	public DocumentReaderImpl_jaxb(File f) throws FileNotFoundException
	{
		fInput = new InputSource( new BufferedInputStream(new FileInputStream(f)));
	}

	/**
	 * Creates a new JaxbDocumentReader for reading objects from the
	 * specified Reader
	 *
	 * @param f will read objects from this file
	**/
	public DocumentReaderImpl_jaxb(Reader r) 
	{
		fInput = new InputSource( r);
	}

	public PersistentNode readDocument() throws DocumentException, IOException
	{
		try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		dbf.setNamespaceAware(false);
    		DocumentBuilder db  = dbf.newDocumentBuilder();
    		Document doc = db.parse(fInput);
    		return new ElementNode( doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new DocumentException( e.toString());
    	} catch (SAXException e) {
    		e.printStackTrace();
    		throw new DocumentException( e.toString());
    	}
	}
}
