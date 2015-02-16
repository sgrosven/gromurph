package org.gromurph.javascore.actions;

import java.util.Map;
import java.util.TreeMap;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Regatta;
import org.sailing.util.SailingServices;

public class Xrr1_3ManagerTest {
//	extends JavascoreTestCase {
//
//	
//	public Xrr1_3ManagerTest(String name) {
//		super(name);
//	}

//	public void testParse() throws Exception {
//		// parsing postponed until we expand XRR for import a bit more
////		String filebase = "2009RolexMiamiOCRStar";	
////		String filename = filebase + ".xml";
////		Xrr1_3Manager xrr = new Xrr1_3Manager();
////		xrr.setFilename( BASEDIR + filename);
////		xrr.parse();
////
////		Regatta r = xrr.getRegatta();
////		assertNotNull(r);
////
////		new RegattaManager(r).writeRegattaToDisk(r.getSaveDirectory(), "xrrTest_" + filebase + ".regatta");
////
////		int i = 0;
////		for ( String w : xrr.warnings){
////			logger.info( (i++) + ": " + w);
////		}
////
////		assertEquals( 0, xrr.warnings.size());
////		fail("more tests needed");
//	}
//
//	public void testPost() throws Exception {
//		// parsing postponed until we expand XRR for import a bit more
//		String regattaName = "2013SWCMiami_Finn.regatta";
//		Regatta regatta = loadTestRegatta( regattaName);
//		
//		Xrr1_3Manager mgr = new Xrr1_3Manager();
//		Map<String,String> elist = new TreeMap<String,String>();
//		boolean isValid = mgr.postXrr( regatta, elist);
//		
//		String validation = elist.get( Xrr1_3Manager.VALIDATION_ERRORS);
//		String reply = elist.get( Xrr1_3Manager.ISAF_REPLY);
//		logger.info( "testPost: validation: " + validation);
//		logger.info( "testPost: reply: " + reply);
//		
//		assertTrue( isValid);
//		assertNull( validation);
//		assertNotNull( reply);
//		assertEquals( SailingServices.UPLOAD_SUCCESSFUL, reply);
//	}


}
