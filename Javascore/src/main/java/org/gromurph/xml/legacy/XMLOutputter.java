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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLOutputter is a DefaultHandler that uses the event notifications to reconstruct the XML document on the standard
 * output.
 * <P>
 * This class was written for the book "Professional XML" by Jonathan Pinnock, Stephen Mohr, Steve Livingstone, Nik Ozu,
 * Didier Martin, Michael Kay, Brian Loesgen, Peter Stark, Kevin Williams, Bruce Peat. The code was taken from a web
 * excerpt at: <A HREF="http://www.perfectxml.com/wp/3110_Chapter06/31100604.htm"> SAX 1.0: The Simple API for XML (Part
 * 4)</A>. The code was modified to work with SAX 2.0, to take a stream argument, and to improve formatting.
 * <P>
 * This code was developed by NASA's Goddard Space Flight Center, Code 588 for the Science Goal Monitor (SGM) project.
 * 
 * @author Jeremy Jones
 **/
public class XMLOutputter extends DefaultHandler {
	private PrintWriter fWriter = null;
	private int fIndent = 0;
	private String fEncoding = DEFAULT_ENCODING;

	public static final String DEFAULT_ENCODING = "UTF-8";

	private String lastElementStarted = "";

	public XMLOutputter(PrintWriter w, String encoding) throws IOException {
		// Create a new Writer from the output stream
		fWriter = w;
		fEncoding = encoding;
	}

	public XMLOutputter(PrintWriter w) throws IOException {
		this(w, DEFAULT_ENCODING);
	}

	public XMLOutputter(OutputStream stream) throws IOException {
		// Create a new Writer from the output stream
		this(new PrintWriter(stream), DEFAULT_ENCODING);

	}

	/**
	 * Start of the document. Write the XML declaration.
	 */
	@Override
	public void startDocument() throws SAXException {
		fWriter.println(getDocumentStart());
	}

	/**
	 * Start of the document. Write the XML declaration.
	 */
	public String getDocumentStart() throws SAXException {
		StringBuffer sb = new StringBuffer("<?xml version='1.0'");
		if (fEncoding != null) {
			sb.append(" encoding='");
			sb.append(fEncoding);
			sb.append("'");
		}
		sb.append("?>");
		return sb.toString();
	}

	/**
	 * End of the document. Close the output stream.
	 */
	@Override
	public void endDocument() throws SAXException {
		fWriter.close();
	}

	boolean newElement = false;

	/**
	 * Start of an element. Output the start tag, escaping special characters.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		fWriter.println();
		indent();
		fWriter.print("<");
		fWriter.print(qName);

		// output the attributes

		for (int i = 0; i < attributes.getLength(); i++) {
			fWriter.print(" ");
			writeAttribute(attributes.getQName(i), attributes.getValue(i));
		}
		fWriter.print(">");
		//writer.println();
		lastElementStarted = qName;
		++fIndent; // increase indent level
	}

	/**
	 * Write attribute name=value pair
	 */
	protected void writeAttribute(String attname, String value) throws SAXException {
		fWriter.print(attname);
		fWriter.print("='");
		char[] attval = value.toCharArray();
		char[] attesc = new char[value.length() * 8]; // worst case scenario
		int newlen = escape(attval, 0, value.length(), attesc);
		fWriter.write(attesc, 0, newlen);
		fWriter.print("'");
	}

	/**
	 * End of an element. Output the end tag.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		--fIndent; // decrease indent level
		if (!lastElementStarted.equals(qName)) {
			fWriter.println();
			indent();
		}
		fWriter.print("</" + qName + ">");
	}

	/**
	 * Character data.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		//		if (start == 0)
		//		{
		//			indent();
		//		}

		char[] dest = new char[length * 8];
		int newlen = escape(ch, start, length, dest);
		fWriter.write(dest, 0, newlen);

		//		if (ch.length == start + length)
		//		{
		//			writer.println();
		//		}
	}

	/**
	 * Ignorable whitespace: treat it as characters
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		characters(ch, start, length);
	}

	/**
	 * Handle a processing instruction.
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		fWriter.print("<?" + target + ' ' + data + "?>");
	}

	/**
	 * Escape special characters for display.
	 * 
	 * @param ch
	 *            The character array containing the string
	 * @param start
	 *            The start position of the input string within the character array
	 * @param length
	 *            The length of the input string within the character array
	 * @param out
	 *            Character array to receive the output. In the worst case, this should be 8 times the length of the
	 *            input array.
	 * @return The number of characters used in the output array
	 */
	private int escape(char ch[], int start, int length, char[] out) {
		int o = 0;

		if (start + length >= 8 && ch[start] == '<' && ch[start + 1] == '!' && ch[start + 2] == '['
				&& ch[start + 3] == 'C' && ch[start + 4] == 'D' && ch[start + 5] == 'A' && ch[start + 6] == 'T'
				&& ch[start + 7] == 'A') {
			// Do not encode CDATA entries
			for (int i = start; i < start + length; i++) {
				out[o++] = ch[i];
			}
		} else {
			for (int i = start; i < start + length; i++) {
				if (ch[i] == '<') {
					("&lt;").getChars(0, 4, out, o);
					o += 4;
				} else if (ch[i] == '>') {
					("&gt;").getChars(0, 4, out, o);
					o += 4;
				} else if (ch[i] == '&') {
					("&amp;").getChars(0, 5, out, o);
					o += 5;
				} else if (ch[i] == '\"') {
					("&#34;").getChars(0, 5, out, o);
					o += 5;
				} else if (ch[i] == '\'') {
					("&#39;").getChars(0, 5, out, o);
					o += 5;
				} else if (ch[i] < 127) {
					out[o++] = ch[i];
				} else {
					// output character reference
					out[o++] = '&';
					out[o++] = '#';
					String code = Integer.toString(ch[i]);
					int len = code.length();
					code.getChars(0, len, out, o);
					o += len;
					out[o++] = ';';
				}
			}
		}

		return o;
	}

	/**
	 * Output tabs to indent to the current indent level.
	 **/
	private void indent() {
		for (int i = 0; i < fIndent; ++i) {
			fWriter.print('\t');
		}
	}
}

//=== Development History ======================================================
//
// $Log: XMLOutputter.java,v $
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
// Revision 1.8  2004/09/16 19:05:18  sgrosvenor_cvs
// improved XML file formatting to make it a bit easier on the eyes for manual editing.
//
// Revision 1.7  2004/03/09 21:19:42  jjones_cvs
// Javadoc improvements.
//
// Revision 1.6  2004/01/08 22:17:02  sgrosvenor_cvs
// restoring errant delete still
//
// Revision 1.4  2003/09/17 18:56:10  jjones_cvs
// Fixed bug - no longer tries to encode CDATA elements.
//
// Revision 1.3  2002/07/23 20:34:03  jjones_cvs
// Modified to remove BufferedOutputStream wrap since that's
// the responsibility of the caller.
//
// Revision 1.2  2002/07/23 15:59:43  jjones_cvs
// DocumentWriter and supporting classes now pass tests.
//
// Revision 1.1  2002/07/22 18:59:09  jjones_cvs
// First full commit
//
//=== End Development History ==================================================
