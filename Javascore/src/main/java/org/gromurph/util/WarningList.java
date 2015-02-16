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
    public void showPopup( Component comp)
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
                JOptionPane.WARNING_MESSAGE);
        }
    }
}
/**
 * $Log: WarningList.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.5  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/01/06 00:30:19  sandyg
 * add header string above main scrolling list
 *
 * Revision 1.3  2003/01/04 17:53:06  sandyg
 * Prefix/suffix overhaul
 *
*/
