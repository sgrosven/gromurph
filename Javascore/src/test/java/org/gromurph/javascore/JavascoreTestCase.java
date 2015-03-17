// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: UtilJfcTestCase.java,v 1.7 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore;

import java.util.ResourceBundle;

import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.BaseObject;
import org.gromurph.util.TestUtils;
import org.gromurph.util.Util;
import org.gromurph.util.UtilUispecTestCase;

public class JavascoreTestCase extends UtilUispecTestCase {

	protected static double ERR_MARGIN = TestUtils.ERR_MARGIN;
	
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = Util.getResources();

	public JavascoreTestCase(String name) {
		super(name);
        Util.setTesting(true);
	}

	private static JavaScore jsInstance;
	protected Regatta fRegatta;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (jsInstance == null) jsInstance = JavaScore.initializeForTesting();
		fRegatta = new Regatta();
		JavaScoreProperties.setRegatta( fRegatta);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public Regatta loadTestRegatta( String filename) throws Exception {

		Regatta reg = new Regatta();
		if (filename != null) {
			reg = RegattaManager.readTestRegatta( filename);
			reg.scoreRegatta();
		}
		
		fRegatta = reg;
		JavaScoreProperties.setRegatta( fRegatta);
		return reg;
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

	@Override public void testDummy() {
		// just to avoid a no test error
	}

}
/**
 * $Log: UtilJfcTestCase.java,v $ Revision 1.7 2006/01/15 21:08:39 sandyg resubmit at 5.1.02
 */
