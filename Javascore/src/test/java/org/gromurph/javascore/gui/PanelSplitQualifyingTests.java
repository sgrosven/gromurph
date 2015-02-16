
package org.gromurph.javascore.gui;

import java.util.ResourceBundle;

import javax.swing.*;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.*;

public class PanelSplitQualifyingTests extends JavascoreTestCase
{
    static ResourceBundle res = JavaScoreProperties.getResources();
    static ResourceBundle resUtil = Util.getResources();

    public PanelSplitQualifyingTests(String test)
    {
        super(test);
    }

     public void testFields()
	{
     	DialogBaseEditor dialog = getBaseDialog();
     	PanelSplitQualifying editor = new PanelSplitQualifying( dialog);
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
				        
    	}
			
    	assertTrue( "Cancel SHOULD be enabled here", cancel.isEnabled());
       	
    	clickOnButton( "Cancel");
    }
}
