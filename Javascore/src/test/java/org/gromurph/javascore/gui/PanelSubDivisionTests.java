//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelSubDivisionTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

package org.gromurph.javascore.gui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SubDivision;


/**
 * Covering unit tests for the PanelRace class
 */
public class PanelSubDivisionTests extends JavascoreTestCase
{
	public PanelSubDivisionTests(String name)
	{
		super(name);
	}

	public void testMainPanel()
	{
        Regatta reg = new Regatta();
		reg.addDivision( new Division("Div1"));
		reg.addDivision( new Division("Div2"));
		PanelSubDivision.setTestRegatta(reg);				
		
		SubDivision subdiv = new SubDivision();
		
		JDialog panel = showPanel( subdiv);		
		
		JTextField field = (JTextField) findComponent( JTextField.class, "fTextName", panel);				
		assertNotNull( "Cant find fTextName", field);
		
		JComponent alternateFocusField = (JComponent) findComponent( JCheckBox.class, "fCheckMonopoly", panel);
		assertNotNull( "alternateFocusField is null", alternateFocusField);
		
		String newtext = "A";		
		sendStringAndEnter(field, newtext);
		assertEquals( "Subdiv name didnt react to lost focus", newtext, subdiv.getName());
		
		newtext = "B";
		sendStringAndEnter(field, newtext);
		assertEquals( "Subdiv name didnt react to enter", newtext, subdiv.getName());
		
		field = (JTextField) findComponent( JTextField.class, "fTextAddon", panel);				
		assertNotNull( "Cant find fTextAddon", field);
		
		alternateFocusField = (JComponent) findComponent( JCheckBox.class, "fCheckMonopoly", panel);
		assertNotNull( "alternateFocusField is null", alternateFocusField);
		
		newtext = "2.0";		
		sendStringAndEnter(field, newtext);
		assertEquals( "Subdiv addon didnt react to lost focus", newtext, Double.toString(subdiv.getRaceAddon()));
		
		newtext = "3.0";
		sendStringAndEnter(field, newtext);
		assertEquals( "Subdiv name didnt react to enter", newtext, Double.toString(subdiv.getRaceAddon()));
		
		JCheckBox check = (JCheckBox) findComponent( JCheckBox.class, "fCheckMonopoly", panel);
		assertNotNull( check);

		assertEquals( false, subdiv.isMonopoly());

		// first click should reverse
		check.doClick();
		
		assertEquals( true, subdiv.isMonopoly());

		// second click should put it back
		check.doClick();
		
		assertEquals( false, subdiv.isMonopoly());
	 
		check = (JCheckBox) findComponent( JCheckBox.class, "fCheckScoreSeparately", panel);
		assertNotNull( check);

		assertEquals( false, subdiv.isScoreSeparately());

		// first click should reverse
		check.doClick();
		
		assertEquals( true, subdiv.isScoreSeparately());

		// second click should put it back
		check.doClick();
		
		assertEquals( false, subdiv.isScoreSeparately());
	 
		JCheckBox checkQual = (JCheckBox) findComponent( JCheckBox.class, "fRadioQualifying", panel);
		assertNotNull( "buttonQual is null", checkQual);
		
		// by default, final radiobox should be initially be selected
		assertTrue( "qualifying should not be selected", !checkQual.isSelected());		
		assertTrue( "subdiv should be final", !subdiv.isGroupQualifying());
		
		// set light winds check box
		try
		{
			// set qual
			clickOn( checkQual);
			
			// qual radiobox should be selected
			assertTrue( "qual should be selected", checkQual.isSelected());		
			assertTrue( "subdiv should be qual", subdiv.isGroupQualifying());					

		}
		catch (Exception e)
		{
			fail( "unexpected exception: " + e.toString());
		}
		
 		clickOKButton();
	}


}

//
// $Log: PanelSubDivisionTests.java,v $
// Revision 1.4  2006/01/15 21:08:39  sandyg
// resubmit at 5.1.02
//
// Revision 1.2  2006/01/11 02:25:08  sandyg
// updating copyright years
//
// Revision 1.1  2006/01/01 02:27:02  sandyg
// preliminary submission to centralize code in a new module
//
// Revision 1.1.2.1  2005/11/26 17:44:21  sandyg
// implement race weight & nondiscardable, did some gui test cleanups.
//
// Revision 1.1  2004/05/06 02:12:24  sandyg
// Beta support for revised Qualifying/Final series for 2004 J22 Worlds
//
// Revision 1.7  2004/04/10 22:19:41  sandyg
// Copyright update
//
// Revision 1.6  2003/05/02 02:42:20  sandyg
// leaving cancel test on comment field commented out
//
// Revision 1.5  2003/04/30 00:59:07  sandyg
// fixed error handling on bad race times, improved unit testing
//
// Revision 1.4  2003/04/27 21:01:12  sandyg
// lots of cleanup, unit testing for 4.1.1 almost complete
//
// Revision 1.3  2003/03/30 00:04:08  sandyg
// gui test cleanup, moved fFrame, fPanel to UtilJfcTestCase
//
// Revision 1.2  2003/03/28 03:07:51  sandyg
// changed toxml and fromxml to xmlRead and xmlWrite
//
// Revision 1.1  2003/03/16 21:47:24  sandyg
// 3.9.2 release: fix bug 658904, time on time condition buttons corrected
//
//

