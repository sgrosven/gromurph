// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ResourceTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs through all java files in a directory's hierarchy and checks for missing resources strings, creates an output
 * file MissingResources.csv of the missing links
 */
public class ResourceTests extends UtilTestCase {

    String currentResourceFile = "";
    ResourceBundle mainBundle;
    ResourceBundle utilBundle;
    File currentFile = null;
    String fBaseDir = null;
    int missingCount = 0;
    Collection<String> fExcluded;
    String fSearchDir;
    String fPropName;
    PrintWriter writer = null;

    public ResourceTests(String name) {
	super(name);
    }

    public int getMissingCount() {
	return missingCount;
    }

    @Override public void setUp() {
	fExcluded = new ArrayList<String>(10);

	currentResourceFile = "";
	mainBundle = null;
	utilBundle = null;
	currentFile = null;
	missingCount = 0;
	writer = null;
    }

    public void addExcluded(String exc) {
	fExcluded.add(exc);
    }

    public void testPrimary() {
	fBaseDir = "/javascore/source/";
	fSearchDir = "org/gromurph/javascore";
	fPropName = "JavaScore";

	addExcluded("RatingName");
	doTest();

	int missing = getMissingCount();
	assertEquals("Missing resources, see console", 0, missing);
    }

    private void doTest() {
	try {
	    File topDir = new File(fBaseDir, fSearchDir);
	    mainBundle = ResourceBundle.getBundle(fPropName);
	    utilBundle = ResourceBundle.getBundle("GeneralProperties");

	    writer = new PrintWriter(new FileWriter("missingresources.csv"));

	    processFile(topDir);

	    writer.close();

	} catch (Exception e) {
		Logger l = LoggerFactory.getLogger(this.getClass());
		l.error( "Exception=" + e.toString(), e);
	}

    }

    public void processFile(File f) throws IOException {
	if (f.isDirectory()) {
	    File[] files = f.listFiles();
	    for (int i = 0; i < files.length; i++) {
		processFile(files[i]);
	    }
	} else {
	    if (f.getName().endsWith(".java"))
		findResources(f);
	}
    }

    public void findResources(File f) throws IOException {
	currentFile = f;
	Reader rdr = new BufferedReader(new FileReader(f));
	StreamTokenizer stream = new StreamTokenizer(rdr);

	while (stream.nextToken() != StreamTokenizer.TT_EOF) {
	    if (stream.ttype == StreamTokenizer.TT_WORD) {
		if (stream.sval.equals("res.getString"))
		    parseResourceString("main", mainBundle, stream);
		else if (stream.sval.equals("resUtil.getString"))
		    parseResourceString("util", utilBundle, stream);
	    }
	}
    }

    public void parseResourceString(String header, ResourceBundle bundle, StreamTokenizer stream) throws IOException {
	// looking for:  res.getString("<<resourcestring>>");

	stream.nextToken();
	if (stream.ttype != '(')
	    return;

	stream.nextToken();
	String resName = stream.sval;

	try {
	    bundle.getString(resName);
	} catch (Exception e) {
	    if (!fExcluded.contains(resName)) {
	    	StringBuffer sb = new StringBuffer(64);
    		missingCount++;
    		sb.append("Missing " + header + " resource: " + resName + " in " + currentFile);
    		sb.append(QUOTE);
    		sb.append(currentResourceFile);
    		sb.append(QUOTE);
    		sb.append(COMMA);
    		sb.append(QUOTE);
    		sb.append(resName);
       		sb.append(QUOTE);
       	    logger.error( sb.toString());
	    }
	}
    }

    static final String QUOTE = "\"";
    static final String COMMA = ",";

}
/**
 * $Log: ResourceTests.java,v $ Revision 1.4 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:27:46 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:03 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.4.2.1 2005/11/01 02:36:58 sandyg java5 using generics
 * 
 * Revision 1.4 2005/05/26 01:46:51 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.3 2004/04/10 22:19:41 sandyg Copyright update
 * 
 * Revision 1.2 2003/04/27 21:01:15 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.1 2003/04/23 22:12:27 sandyg Renamed Help and Resource Tests, now works as standalone Junit tests
 * 
 * Revision 1.4 2003/01/04 17:13:06 sandyg Prefix/suffix overhaul
 * 
 */
