
package org.gromurph.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.gromurph.xml.jaxb.DocumentReaderImpl_jaxb;
import org.gromurph.xml.jaxb.DocumentWriterImpl_jaxb;

/**
 * The main interface that lets an object work with a standard superclass
 * of editors (BaseEditor) and lists (BaseList)
 */
public class XmlUtil
{
    public static void writeDocument( String directory, String filename, PersistentObject object, String rootTag) throws IOException, DocumentException {
    	IDocumentWriter dw = new DocumentWriterImpl_jaxb( directory, filename);
    	writeDocument( dw, object, rootTag);
    }
    
    public static void writeDocument( Writer w, PersistentObject object, String rootTag) throws IOException, DocumentException {
    	IDocumentWriter dw = new DocumentWriterImpl_jaxb( w);
		writeDocument( dw, object, rootTag);
    }
    
    public static IDocumentWriter createDocumentWriter( String directory, String filename) throws IOException, DocumentException {
    	return new DocumentWriterImpl_jaxb( directory, filename);
    }
    
    private static void writeDocument( IDocumentWriter dw, PersistentObject object, String rootTag) throws IOException, DocumentException {
		PersistentNode root = dw.createRootNode( rootTag);
		object.xmlWrite(root);
		dw.saveObject(root, false);
    }
    
     public static PersistentNode readDocument( Reader r) throws IOException, DocumentException {
    	DocumentReaderImpl_jaxb idr = new DocumentReaderImpl_jaxb( r);
    	return idr.readDocument();
    }

    public static PersistentNode readDocument( InputStream ist) throws IOException, DocumentException {
    	DocumentReaderImpl_jaxb idr = new DocumentReaderImpl_jaxb( ist);
    	return idr.readDocument();
    }


}
