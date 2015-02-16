//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ProgressStatusPanel.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * A user interface component that displays progress information on some task.
 * It contains a message label, a progress bar, and a status message to indicate
 * percent complete.
**/
public class ProgressStatusPanel extends JPanel
{
    private JLabel          fMessageLabel;
    private JLabel          fStatusLabel;
    private JProgressBar    fProgressBar;

    /**
     * for standalone testing only
    **/
    public static void main( String[] args)
    {
        ProgressStatusPanel pb = new ProgressStatusPanel();
        pb.setProgressMinMax( 0, 100);
        pb.setStatus( "status");
        pb.setMessage( "message");
        javax.swing.JOptionPane.showConfirmDialog(null, pb);
        System.exit(0);
    }

    /**
     * Constructs a new ProgressStatusPanel.
    **/
    public ProgressStatusPanel()
    {
        super();

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 2, 5),
                BorderFactory.createLoweredBevelBorder()));

		setLayout(new GridLayout( 3, 1, 0, 0));

        fMessageLabel = new JLabel();
        fMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fMessageLabel.setVerticalAlignment(SwingConstants.TOP);
        fMessageLabel.setForeground(Color.black);
        add(fMessageLabel);

        JPanel progressPanel = new JPanel();
        fProgressBar = new JProgressBar();
        fProgressBar.setMinimum(0);
        fProgressBar.setMaximum(0);
        fProgressBar.setValue(0);
        progressPanel.add(fProgressBar);
        add(progressPanel);

        fStatusLabel = new JLabel();
        fStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fStatusLabel.setVerticalAlignment(SwingConstants.TOP);
        fStatusLabel.setForeground(Color.black);
        add(fStatusLabel);
    }

    /**
     * Returns the current message text.
    **/
    public String getMessage()
    {
        return fMessageLabel.getText();
    }

    /**
     * Sets the message text.
    **/
    public void setMessage(String msg)
    {
        fMessageLabel.setText(msg);
    }

    /**
     * Returns the current status text.
    **/
    public String getStatus()
    {
        return fStatusLabel.getText();
    }

    /**
     * Sets the status text.
    **/
    public void setStatus(String status)
    {
        fStatusLabel.setText(status);
    }

    /**
     * Gets the current minimum value on the progress bar.
    **/
    public int getMinimum()
    {
        return fProgressBar.getMinimum();
    }

    /**
     * Gets the current maximum value on the progress bar.
    **/
    public int getMaximum()
    {
        return fProgressBar.getMaximum();
    }

    /**
     * Sets the minimum and maximum values of the progress bar and resets
     * the current value to zero.
    **/
    public void setProgressMinMax(int min, int max)
    {
        fProgressBar.setMinimum(min);
        fProgressBar.setMaximum(max);
        fProgressBar.setValue(0);
    }

    /**
     * Updates the progress bar by setting its current value.
    **/
    public void updateProgress(int value)
    {
        fProgressBar.setValue(value);
    }
}
/**
 * $Log: ProgressStatusPanel.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.6  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/04/27 21:35:33  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.4  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
