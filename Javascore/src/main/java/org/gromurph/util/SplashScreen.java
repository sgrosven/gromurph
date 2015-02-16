//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: SplashScreen.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
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
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

/**
 * SplashScreen displays an image and a status message while application
 * initialization occurs.  Use SplashScreen to display an image while the app
 * is loading.  SplashScreen also has the ability to automatically display all
 * the MessageLog messages.
 *
 * <P>This code was developed by NASA, Goddard Space Flight Center, Code 588
 * for the Scientist's Expert Assistant (SEA) project.
 *
 * @version		04/19/99
 * @author		J. Jones / 588
**/
public class SplashScreen extends JWindow //implements MessageLogListener
{
    private String fVersion;
    private String fReleaseDate;

	/**
	 * The component containing the status message text.
	**/
	private JLabel	fStatusBar = null;

	/**
	 * Constructs a new SplashScreen with just a status message and no image.
	**/
	public SplashScreen()
	{
		this(null, "<none>", "<none>");
	}

	/**
	 * Constructs a new SplashScreen.  Attempts to load the image specified by
	 * the image argument.  Note that the constructor automatically displays
	 * the SplashScreen immediately.  No call to setVisible(true) is needed.
	 *
	 * @param	image	location of image to display
	**/
	public SplashScreen(String image, String version, String releaseDate)
	{
		super();
        fVersion = version;
        fReleaseDate = releaseDate;

		// Try to load the image
		ImageIcon splashImage = Util.getImageIcon(this, image); //Utilities.findImage(this, image);

		JPanel p = new JPanel(new BorderLayout(), true);
		//JPanel p = new SplashPanel();
		if (splashImage != null)
		{
			p.add(new JLabel( splashImage), BorderLayout.CENTER);
		}

        StringBuffer b = new StringBuffer( "Version: ");
        b.append( fVersion);
        b.append( ", Released: ");
        b.append( fReleaseDate);

		p.add(fStatusBar = new JLabel( b.toString(), SwingConstants.CENTER),
            BorderLayout.SOUTH);
		p.setBorder(BorderFactory.createRaisedBevelBorder());

		getContentPane().add(p);

		pack();

		// Center on screen
		Dimension size = getSize();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);

		// Immediately display it
		setVisible(true);
		((JComponent) getContentPane()).paintImmediately(getContentPane().getBounds());
	}

    public static void main( String[] args)
    {
        new SplashScreen( "/images/SplashGraphic.jpg", "1.0.0", "August 23, 2000");
        javax.swing.Timer t = new javax.swing.Timer( 6000, new java.awt.event.ActionListener() {
            public void actionPerformed( java.awt.event.ActionEvent e)
            {
                System.exit(0);
            }
        });
        t.start();
    }

	/**
	 * Sets the status message that is displayed to the user.
	 *
	 * @param status	new status message
	**/
	public void showStatus(String status)
	{
		try
		{
			// Update Splash-Screen's status bar in AWT thread
			SwingUtilities.invokeLater(new UpdateStatus(status));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Instructs the SplashScreen to automatically listen for message log events
	 * and display all the non-DEBUG messages as they come in.  Note that this
	 * feature is off by default and must be explicitly enabled with a call to
	 * this method.
	public void monitorMessageLog()
	{
		MessageLogger.getInstance().addMessageLogListener(this);
	}
	**/

	/**
	 * Close and dispose of the SplashScreen window. Use this instead of
	 * setVisible(false).
	**/
	public void close()
	{
		try
		{
			// Close and dispose Window in AWT thread
			SwingUtilities.invokeLater(new CloseSplashScreen());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method is called for each message that is written to the MessageLogger.
	 *
	 * @param event	the event that contains details about the message
    public void messageLogged(MessageLogEvent event)
	{
		// Display all but debug messages
		if (event.getMessageType() != MessageLogger.DEBUG)
		{
			showStatus(event.getMessage());
		}
	}
	**/

	/**
	 * Updates status in a separate thread invoked by SwingUtilities.invokeLater().
	**/
	private class UpdateStatus implements Runnable
	{
		private String newStatus;

		public UpdateStatus(String status)
		{
			newStatus = status;
		}

		public void run()
		{
			fStatusBar.setText(newStatus);
		}
	}

	/**
	 * Closes the window in a separate thread invoked by SwingUtilities.invokeLater().
	**/
	private class CloseSplashScreen implements Runnable
	{
		public void run()
		{
			//MessageLogger.getInstance().removeMessageLogListener(SplashScreen.this);
			setVisible(false);
			dispose();
		}
	}

//    private class VersionLabel extends JLabel
//    {
//        public VersionLabel( Icon image)
//        {
//            super( image);
//        }
//
//        public void paint(Graphics g)
//        {
//            super.paint(g);
//            Graphics2D g2 = (Graphics2D) g;
//
//        	Dimension d = getSize();
//        	int w = d.width;
//        	int h = d.height;
//
//            TextLayout textTl = new TextLayout(
//                "Version " + fVersion,
//                new Font("Helvetica", 1, 18),
//                new FontRenderContext(null, false, false));
//
//	        AffineTransform textAt = new AffineTransform();
//	        textAt.translate(0, (float)textTl.getBounds().getHeight());
//            Shape shape = textTl.getOutline(textAt);
//
//            // Sets the selected Shape to the center of the Canvas.
//            Rectangle r = shape.getBounds();
//            //AffineTransform saveXform = g2.getTransform();
//     	    AffineTransform toCenterAt = new AffineTransform();
//
//            //AffineTransform at = new AffineTransform();
//    	    toCenterAt.setToIdentity();
//            toCenterAt.translate(w/2, h/2);
//            toCenterAt.shear( -0.3, 0.0);
//            //toCenterAt.concatenate( at);
//
//            int x = -(r.width/2);
//            int y = -(r.height/2) + 20;
//            toCenterAt.translate( x,y);
//
//            g2.transform( toCenterAt);
//            g2.fill(shape);
//        }
//    }

}
/**
 * $Log: SplashScreen.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5  2005/05/26 01:45:43  sandyg
 * fixing resource access/lookup problems
 *
 * Revision 1.4  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
