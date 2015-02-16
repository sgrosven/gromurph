// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: UtilTestCase.java,v 1.5 2006/01/19 02:27:41 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * extends Junit testcase to supply some gromurph specific checks
 */
public class UtilTestCase extends TestCase {
	protected static String BASEDIR = TestUtils.BASEDIR;
	protected static double ERR_MARGIN = TestUtils.ERR_MARGIN;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public UtilTestCase(String name) {
		super(name);
	}

	protected boolean xmlEquals(BaseObject obj) {
		return TestUtils.xmlEquals(obj, null);
	}
	protected boolean xmlEquals(BaseObject obj, Object toproot) {
		return TestUtils.xmlEquals(obj, toproot);
	}
	protected String toXml(BaseObject obj) {
		return TestUtils.toXml(obj);
	}
	protected String toFromXml(BaseObject obj) {
		return TestUtils.toFromXml(obj);
	}
	protected boolean xmlEquals(BaseObject obj, BaseObject obj2, Object toproot) {
		return TestUtils.xmlEquals(obj, obj2, toproot);
	}
	public void xmlObjectToObject(BaseObject src, BaseObject dest) throws Exception {
		TestUtils.xmlObjectToObject(src, dest);
	}

}
