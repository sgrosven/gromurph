// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: HelpTests.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.xml.PersistentNode;
import org.gromurph.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs through all java files in a directory's hierarchy and checks for missing Helps strings, creates an output file
 * MissingHelps.csv of the missing links
 */
public class HelpTests extends UtilTestCase {
	public HelpTests(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		HelpManager.getInstance().resetHelpAvailable();
	}

	File currentFile = null;
	String fBaseDir = null;
	String fHelpSet = null;
	int missingCount = 0;

	PrintWriter writer = null;

	public int getMissingCount() {
		return missingCount;
	}

	public void testCheckHelpXmlSyntax() {
		fHelpSet = JavaScoreProperties.HELP_SET;
		fBaseDir = Util.getWorkingDirectory() + TestUtils.CLASSES_DIR + "/help";

		File helpDir = new File(fBaseDir);
		assertNotNull(helpDir);
		assertTrue(helpDir.isDirectory());

		fBads.clear();
		checkHelpDir(helpDir);
		if (fBads.size() > 0) {
			logger.info("Bad files: ");
			for (Iterator it = fBads.iterator(); it.hasNext();) {
				logger.info("   " + it.next());
			}
			fail("Bad XML file count=" + fBads.size());
		}
	}

	List<String> fBads = new ArrayList<String>();

	private void checkHelpDir(File dirname) {
		File[] files = dirname.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName();

				return pathname.isDirectory() ||
				//name.endsWith(".html") || 
						name.endsWith(".xml") || name.endsWith(".hs") || name.endsWith(".jhm");
			}
		});
		assertNotNull(files);

		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				checkHelpDir(f);
			} else {
				try {
					FileInputStream fis = new FileInputStream(f);
					PersistentNode root = XmlUtil.readDocument(fis);

					//		            XmlDocument doc = XmlDocument.createXmlDocument (fis, false);
					//	                Element root=doc.getDocumentElement();
					//	                root.normalize();
				}
				catch (Exception e) {
					e.printStackTrace();
					fBads.add("cant read xml file: " + f.getAbsoluteFile() + ": " + e.toString());
				}
			}
		}
	}

	public void testHelps() {
		fHelpSet = JavaScoreProperties.HELP_SET;
		//        fHelpSet = "help/JavaScore.hs";

		fBaseDir = Util.getWorkingDirectory() + "source/"; // "/javascore/source/";
		String searchDir = "org/gromurph/javascore";

		HelpManager.getInstance().setMainHelpSet(fHelpSet);

		assertNotNull("broker is null", HelpManager.getInstance().getBroker());

		try {
			File topDir = new File(fBaseDir + searchDir);
			writer = new PrintWriter(new FileWriter("missingHelps.csv"));

			processFile(topDir);

			writer.close();
		}
		catch (Exception e) {
			Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Exception=" + e.toString(), e);
		}

		int missing = getMissingCount();
		assertEquals("Missing helps", 0, missing);
	}

	public void processFile(File f) throws IOException {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				processFile(files[i]);
			}
		} else {
			if (f.getName().endsWith(".java")) findHelps(f);
		}
	}

	public void findHelps(File f) throws IOException {
		currentFile = f;
		Reader rdr = new BufferedReader(new FileReader(f));
		StreamTokenizer stream = new StreamTokenizer(rdr);

		while (stream.nextToken() != StreamTokenizer.TT_EOF) {
			if (stream.ttype == StreamTokenizer.TT_WORD) {
				// looking for: HelpManager.getInstance().registerHelpTopic(<component>, "<helptag>");
				if (stream.sval.equals("HelpManager.getInstance")) {
					String componentName = null;
					String helpTag = null;

					stream.nextToken();
					if (stream.ttype != '(') return;

					stream.nextToken();
					if (stream.ttype != ')') return;

					stream.nextToken();
					//if (stream.ttype != '.') return;

					stream.nextToken();
					if (!stream.sval.equals("registerHelpTopic")) return;

					stream.nextToken();
					if (stream.ttype != '(') return;

					stream.nextToken();
					componentName = stream.sval;

					stream.nextToken();
					if (stream.ttype != ',') return;

					stream.nextToken();
					helpTag = stream.sval;

					lookupTag(componentName, helpTag);
				}
			}
		}
	}

	public void lookupTag(String comp, String tag) {
		// looking for:  res.getString("<<Helpstring>>");
		//   skipping tag of "fHelpKey" its a variable not a real tag

		if (!tag.equals("fHelpKey") && !HelpManager.getInstance().isTopicDefined(tag)) {
			StringBuffer sb = new StringBuffer(32);
			missingCount++;
			sb.append("Missing Help: " + comp + "/" + tag + " in " + currentFile.toString());
			sb.append(QUOTE);
			sb.append(currentFile.toString());
			sb.append(QUOTE);
			sb.append(COMMA);
			sb.append(QUOTE);
			sb.append(tag);
			sb.append(QUOTE);
			sb.append(COMMA);
			sb.append(QUOTE);
			sb.append(comp);
			sb.append(QUOTE);
			logger.info( sb.toString());
		}
	}

	static final String QUOTE = "\"";
	static final String COMMA = ",";

}
/**
 * $Log: HelpTests.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:27:46 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/11 02:16:46 sandyg updated to use relative class path
 * 
 * Revision 1.1 2006/01/01 02:27:03 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.6.2.1 2005/11/01 02:36:58 sandyg java5 using generics
 * 
 * Revision 1.6 2005/06/26 22:48:08 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.5 2005/05/26 01:46:51 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.4 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.3 2003/04/27 21:34:24 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.2 2003/04/27 21:01:14 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.1 2003/04/23 22:12:27 sandyg Renamed Help and Resource Tests, now works as standalone Junit tests
 * 
 * Revision 1.3 2003/01/04 17:13:06 sandyg Prefix/suffix overhaul
 * 
 */
