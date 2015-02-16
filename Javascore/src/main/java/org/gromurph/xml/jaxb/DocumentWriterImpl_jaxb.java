package org.gromurph.xml.jaxb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.WriterOutputStream;
import org.gromurph.util.Util;
import org.gromurph.xml.DocumentException;
import org.gromurph.xml.IDocumentWriter;
import org.gromurph.xml.PersistentNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocumentWriterImpl_jaxb implements IDocumentWriter {

	private String fFileDir;
	private String fFileName;

	/**
	 * Creates a new DocumentWriter to write to the specified output file
	 **/
	public DocumentWriterImpl_jaxb(String fileDir, String fileName) throws IOException {
		fFileDir = fileDir;
		fFileName = fileName;

		File file = Util.getFile(fFileDir, fFileName);
		fStream = new FileOutputStream(file);

	}

	private OutputStream fStream = null;

	/**
	 * Creates a new DocumentWriter to write to the specified output file
	 **/
	public DocumentWriterImpl_jaxb(Writer w) throws IOException {
		fStream = new WriterOutputStream(w);
	}


	public PersistentNode createRootNode(String rootTag) {
		try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		dbf.setNamespaceAware(false);
    		DocumentBuilder db  = dbf.newDocumentBuilder();
    		Document doc = db.newDocument();
    		Element root = doc.createElement(rootTag);
    		doc.appendChild( root);
    		return new ElementNode(root);
		} catch ( ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		}
	}

	public void saveObject( PersistentNode root, boolean elementOnly) throws DocumentException, IOException {
		if (!(root instanceof AbstractNode)) {
			throw new DocumentException("trying to write node that is not an AbstractNode");
		}
		try {
			Document dom = ((AbstractNode) root).getOwnerDocument();
			
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");

			DOMSource source = new DOMSource(dom);
			StreamResult sr = new StreamResult( fStream);
			tr.transform( source, sr);

		} catch (TransformerException te) {
			throw new DocumentException( te.getMessage());
		} 	
	}

}

