
package org.gromurph.javascore.gui;

import java.util.ResourceBundle;

import javax.swing.*;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.*;

public class PanelSplitRankingTests extends JavascoreTestCase
{
    static ResourceBundle res = JavaScoreProperties.getResources();
    static ResourceBundle resUtil = Util.getResources();

    public PanelSplitRankingTests(String test)
    {
        super(test);
    }

     public void testFields()
	{
    	DialogBaseEditor dialog = getBaseDialog();
    	PanelSplitRanking editor = new PanelSplitRanking( dialog);
		showPanel( new Regatta(), editor);
 
	   	JButton cancel = (JButton) findComponent( JButton.class, "fButtonCancel", dialog);
    	assertNotNull( cancel);
    	assertFalse( "Cancel should not be enabled here", cancel.isEnabled());
    	
		for (int i = 0; i < 2; i++)
    	{
	        JTextField field = (JTextField) findComponent( JTextFieldSelectAll.class,
	                "fTextDivName"+i, editor);
	        assertNotNull( field);
	        assertEquals("divname should start blank", "", field.getText());
	        
	        String name = "div"+i;
			sendStringAndEnter( field, name);
			assertEquals( "divname" + i + " not right", name, editor.getDivisionName(i));
			
	        field = (JTextField) findComponent( JTextFieldSelectAll.class,
                "fTextTopPosition"+i, editor);
	        assertNotNull( field);
	        String t = field.getText();
	        assertEquals("pos[" + i + "] should start with default of " + (i+1), Integer.toString(i+1), t);
	        
			sendStringAndEnter( field, "5");
			assertEquals( "pos" + i + " not right", 5, editor.getTopPosition(i));		
    	}
			
    	assertTrue( "Cancel SHOULD be enabled here", cancel.isEnabled());
   	
    	clickOnButton( "Cancel");
    }
}
