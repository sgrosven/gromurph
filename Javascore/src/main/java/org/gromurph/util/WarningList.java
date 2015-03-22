//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: WarningList.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarningList extends ArrayList<String>
{

    public WarningList()
    {
        this("Warnings");
    }

    public WarningList( String title)
    {
        super(10);
        fTitle = title;
    }

    private String fTitle;
    private String fHeader;

	public void setHeader( String header)
	{
		fHeader = header;
	}
	
    /**
     * Displays the list of warnings in an option pane and waits for a
     * user entered button click before returning.  If no warnings exist,
     * no window is shown
     *
     * @param comp Containing component, may be null
     */
    public void showPopup( Component comp) {
    	showPopup( comp, JOptionPane.WARNING_MESSAGE);
    }
    
    public void showPopup( Component comp, int messageType)
    {
        if (size() > 0)
        {
        	JPanel panel = new JPanel( new BorderLayout());
        	if ( fHeader != null)
        	{
        		JTextPane p = new JTextPane();
        		p.setText( fHeader);
        		panel.add( p, BorderLayout.NORTH);
        	}
        	panel.add( new JScrollPane( new JList( toArray())));
            JOptionPane.showMessageDialog(
                comp,
                panel,
                fTitle,
                messageType);
        }
    }
    
    public void logMessages( Logger logger) {
    	for ( String s : this) {
    		logger.error( s);
    	}
    }
}
