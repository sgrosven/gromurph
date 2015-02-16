//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: HelpManager.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

package org.gromurph.util;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.gromurph.util.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HelpManager governs all help-related functionality.  HelpManager is built
 * on top of JavaHelp and provides easy access to JavaHelp features.  Both
 * regular help and context-sensitive help are supported.
 * <P>
 * Steps for enabling context-sensitive help:
 * <UL>
 * <LI>Step 1: Use createCSHButton() and createCSHMenuItem() to create
 * components that enable the context-sensitive help feature.  Add these components
 * to a window.</LI>
 * <LI>Step 2: Register help topics for all your components.
 * Use registerHelpTopic() to do this.</LI>
 * <LI>Step 3: Write the help.  Each help topic must exist in the map file
 * of the specified help set.  Each entry points to an HTML file or portion of an
 * HTML file (remember, '#' anchors are supported).</LI>
 * <LI>Step 4: It's also a good idea to use enableWindowHelp() to assign a help topic
 * to each window.  This enables the Help key on the window, but also serves as
 * a suitable default help topic should the user ask for context-sensitive help
 * on a component that is not registered.</LI>
 * </UL>
 *
 * <P>This code was developed by NASA, Goddard Space Flight Center, Code 588
 * for the Scientist's Expert Assistant (SEA) project.
 *
 * @version		08/05/99
 * @author		J. Jones / 588
**/
public class HelpManager
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static String CONTEXTHELP_ICON = "/images/ContextHelpButton.gif";

    /**
     * The shared singleton instance.
    **/
    private static HelpManager	sInstance = null;

    /**
     * The single HelpBroker that manages all the JavaHelp stuff.
    **/
	private HelpBroker			fBroker = null;

    /**
     * Cache of previously loaded HelpSets.
    **/
    private HashMap<String,HelpSet> fHelpSetCache = new HashMap<String,HelpSet>();

    /**
     * Name of current HelpSet.
    **/
    private String				fCurrentHelpSet = null;

    /**
     * The main (default) help set for the application.
    **/
    private String				fMainHelpSet = null;

    /**
     * The custom help cursor to be displayed during context-sensitive help.
     * Note that the cursor image must be the proper size for the OS otherwise
     * the AWT scales the image to fit and it looks ugly.  On Windows
     * cursors are 32x32 pixels.
    **/
    private Cursor				fHelpCursor = null;

    /**
     * Label for context-sensitive help buttons/menuitems.
     * Adopts the Microsoft Windows standard.
    **/
    private static final String	CSH_LABEL = "What's This?";

    /**
     * Returns the Singleton instance.
     *
     * @return	the Singleton instance for this class
    **/
    public static HelpManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized(HelpManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new HelpManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * Returns the "main" HelpSet.  The main help set is the default set
     * that appears when the user selects "Help Contents" from the Help menu.
    **/
    public String getMainHelpSet()
    {
        return fMainHelpSet;
    }
    
    public HelpBroker getBroker()
    {
    	return fBroker;
    }

    /**
     * Sets the "main" HelpSet and loads it as the current help set.
     * The main help set is the default set that appears when the user
     * selects "Help Contents" from the Help menu.
     *
     * @param	helpSet		name of new HelpSet to load and become main
    **/
    public void setMainHelpSet(String helpSet)
    {
        fMainHelpSet = helpSet;
        setHelpSet(helpSet);
    }

    private SwingWorker swInitPresent;

    /**
     * Sets the current help set, causing the files for the help set
     * to be loaded.  This method does not display any help unless the help
     * window is already open.  Use setHelpTopic() to display the new HelpSet.
     *
     * @param	helpSet		name of new HelpSet to load
    **/
    public void setHelpSet(String helpSet)
    {
        // Create the HelpBroker if necessary
        if (fBroker == null)
        {
            createHelpBroker(helpSet);
        }

        if (!helpSet.equals(fCurrentHelpSet))
        {
            // Not the current HelpSet, so try to switch

            //MessageLogger.getInstance().writeDebug(this, "Switching to HelpSet: " + helpSet);

            // Set the HelpSet in the existing broker
            HelpSet set = findHelpSet( null, helpSet);
            if (set != null)
            {
                fBroker.setHelpSet(set);
                swInitPresent = new SwingWorker() {
                	@Override public Object construct()
                    {
                        fBroker.initPresentation();
                        return new Boolean( true);
                    }
                };
                swInitPresent.start();
                fCurrentHelpSet = helpSet;
            }
        }
    }

    /**
     * Adds a help set to the current list of help sets, causing the files for the help set
     * to be loaded.  If setHelpSet is used after addHelpSet, all helps are replaced
     *
     * @param	helpSet		name of new HelpSet to merge with current helpset
    **/
    public void addHelpSet(String helpSet)
    {
        if (fCurrentHelpSet == null)
        {
            setHelpSet( helpSet);
        }
        else if (!helpSet.equals(fCurrentHelpSet))
        {
            // add HelpSet in the existing broker
            HelpSet set = findHelpSet( null, helpSet);
            HelpSet currentHS = findHelpSet( null, fCurrentHelpSet);

            // Not the current HelpSet, so try to switch
            //MessageLogger.getInstance().writeDebug(this, "Adding HelpSet: " + helpSet + " to " + fCurrentHelpSet);

            if (set != null)
            {
                currentHS.add(set);
            }
        }
    }

    /**
     * Displays the specified help topic in the Help window.  Opens the help window
     * if not already open.  Assumes the help topic exists in the main help set as
     * defined by setMainHelpSet().
     *
     * @param	helpTopic	name of help topic to display
    **/
    public void setHelpTopic(String helpTopic)
    {
        setHelpTopic(helpTopic, fMainHelpSet);
    }

    /**
     * Displays the specified help topic in the Help window.  Opens the help window
     * if not already open.  Looks up the help id for the specified component in
     * the main help set.
     *
     * @param	helpTopic	name of help topic to display
    **/
    public void setHelpTopic(Component c)
    {
        String topic = CSH.getHelpIDString(c);
        setHelpTopic( topic , fMainHelpSet);
    }

    public boolean isTopicDefined( String topic)
    {
        setHelpSet(fMainHelpSet);
        // Tell HelpBroker to go to the specified help topic
        // Display if necessary
        if (!fBroker.isDisplayed())
        {
            if (swInitPresent != null)
            {
                try { swInitPresent.get(); } // waits til init thread done
                catch (Exception e) {} // do nothing
            }
        }

        try
        {
            fBroker.setCurrentID(topic);
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }

    /**
     * Displays the specified help topic in the Help window.  Opens the help window
     * if not already open.  The current help set is switched to the specified
     * help set if not already set.
     *
     * @param	helpTopic	name of help topic to display
     * @param	helpSet		HelpSet in which to locate the help topic
    **/
    public void setHelpTopic(String helpTopic, String helpSet)
    {
        setHelpTopic( helpTopic, helpSet, true);
    }

    /**
     * Displays the specified help topic in the Help window.  Opens the help window
     * if not already open.  The current help set is switched to the specified
     * help set if not already set.
     *
     * @param	helpTopic	name of help topic to display
     * @param	helpSet		HelpSet in which to locate the help topic
    **/
    public void setHelpTopic(String helpTopic, String helpSet, boolean showHelp)
    {
        setHelpSet(helpSet);

        if (fBroker != null)
        {
            // Display if necessary
            if (!fBroker.isDisplayed())
            {
                if (swInitPresent != null)
                {
                    try { swInitPresent.get(); } // waits til init thread done
                    catch (Exception e) {} // do nothing
                }
                if (showHelp) fBroker.setDisplayed(true);
            }

            // Tell HelpBroker to go to the specified help topic
            try
            {
                fBroker.setCurrentID(helpTopic);
            }
            catch( Exception e)
            {
                //MessageLogger.getInstance().writeError( this, "Exception looking up topic " +
                //    helpTopic + ", exception=" + e);
                Util.printlnException( this, e, true);
            }
        }
        else
        {
            //MessageLogger.getInstance().writeError(this, "showHelpContent() called with no HelpBroker.");
            Util.printlnException( this, new Exception ("showHelpContent() called with no HelpBroker."), true);
        }
    }

    /**
     * Enables window-level help for the specified window.  When the window is active,
     * if the user presses the Help key (F1 on Windows), the window-level help is displayed.
     * Looks for the specified helpTopic in the main help set.
     *
     * @param	window		enable help for this window
     * @param	helpTopic	name of help topic to display
    **/
    public void enableWindowHelp(JFrame window, Component c)
    {
        HelpSet set = findHelpSet( c, fMainHelpSet);
        String topic = CSH.getHelpIDString(c);
        if (set != null)
        {
            try
            {
                fBroker.enableHelpKey(window.getRootPane(), topic, set);
            }
            catch (NullPointerException npe)
            {
                Util.printlnException( this, new Exception ("enableWindowHelp() called with no help on component c=" + c), false);
            }
        }
    }

    /**
     * Enables window-level help for the specified window.  When the window is active,
     * if the user presses the Help key (F1 on Windows), the window-level help is displayed.
     * Looks for the specified helpTopic in the main help set.
     *
     * @param	window		enable help for this window
     * @param	helpTopic	name of help topic to display
    **/
    public void enableWindowHelp(JFrame window, String helpTopic, Object src)
    {
        HelpSet set = findHelpSet( src, fMainHelpSet);
        if (set != null)
        {
            fBroker.enableHelpKey(window.getRootPane(), helpTopic, set);
        }
    }

    /**
     * Enables window-level help for the specified window.  When the window is active,
     * if the user presses the Help key (F1 on Windows), the window-level help is displayed.
     *
     * @param	window		enable help for this window
     * @param	helpTopic	name of help topic to display
     * @param	helpSet		HelpSet in which to locate the help topic
    **/
    public void enableWindowHelp(JFrame window, String helpTopic, String helpSet, Object c)
    {
        HelpSet set = findHelpSet(c, helpSet);
        if (set != null)
        {
            fBroker.enableHelpKey(window.getRootPane(), helpTopic, set);
        }
    }

    /**
     * Registers a help topic for the specified Component.  The help topic
     * will be displayed if the user requests context-sensitive help for the
     * Component.  Looks for the help topic in the main help set.
     *
     * @param	comp		register help for this component
     * @param	helpTopic	associate this help topic with the component
    **/
    public void registerHelpTopic(Component comp, String helpTopic)
    {
        registerHelpTopic(comp, helpTopic, fMainHelpSet);
    }

    /**
     * Registers a help topic for the specified Component.  The help topic
     * will be displayed if the user requests context-sensitive help for the
     * Component.  The help topic must be in the specified help set.
     *
     * @param	comp		register help for this component
     * @param	helpTopic	associate this help topic with the component
     * @param	helpSet		HelpSet in which to locate the help topic
    **/
    public void registerHelpTopic(Component comp, String helpTopic, String helpSet)
    {
    	if (helpSet == null) return;
        HelpSet set = findHelpSet(comp, helpSet);
        if (set != null)
        {
            try
            {
                CSH.setHelpSet(comp, set);
                CSH.setHelpIDString(comp, helpTopic);
            }
            catch (IllegalArgumentException e)
            {
                logger.error( "HELP WARNING, e={}, key={}, comp={}", 
                		e.toString(), helpTopic, ((comp==null)?"<null>":comp.toString()) );
            }
        }
    }

    /**
     * Creates a new Context-Sensitive Help button that will provide
     * an access point for the context-sensitive help feature.
     *
     * @return	button that provides the context-sensitive help feature
    **/
    public JButton createCSHButton()
    {
    	JButton button = new JButton();
        ImageIcon helpIcon = Util.getImageIcon( button, CONTEXTHELP_ICON);

        // Create toolbar
        if (helpIcon != null)
        {
            button.setIcon( helpIcon);
        }
        else
        {
            button.setText("Help");
        }
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setToolTipText(CSH_LABEL);
        button.addActionListener(new CSHTracker());

        return button;
    }

    /**
     * Creates a new Context-Sensitive Help menu item that will provide
     * an access point for the context-sensitive help feature.

     * @return	menu item that provides the context-sensitive help feature
    **/
    public JMenuItem createCSHMenuItem()
    {
        JMenuItem csHelp = new JMenuItem(CSH_LABEL);
        csHelp.addActionListener(new CSHTracker());

        return csHelp;
    }

    /**
     * Creates the HelpBroker instance for the given default help set.
     *
     * @param	helpSet		name of default HelpSet to assign to the HelpBroker
    **/
    private void createHelpBroker(String helpSet)
    {
        if (fBroker != null)
        {
            fBroker.setDisplayed(false);
        }

        HelpSet set = findHelpSet( null, helpSet);
        if (set != null)
        {
            fBroker = set.createHelpBroker();
            fCurrentHelpSet = helpSet;
        }
        else
        {
            fBroker = null;
            fCurrentHelpSet = null;
        }
    }

    private HelpSet findHelpSet( Object src, String name)
    {
    	if (name == null) return null;
    	
        // First see if this HelpSet has previously been loaded
        HelpSet set = fHelpSetCache.get(name);
        if (set != null)
        {
            // HelpSet found in cache, so just return the existing set
            return set;
        }

        // Try to load the help set with specified name
        if (!noHelpAvailable) try
        {
        	set = tryThisOne( src, name);
        	if (set == null)
        	{        
	        	if (name.startsWith("/"))
	        	{
	        		set = tryThisOne( src, name.substring( 1));
	        		if (set == null) set = tryThisOne( src, "." + name);
	        	}
	        	else
	        	{
	        		set = tryThisOne( src, "/" + name);
	        		if (set == null) set = tryThisOne( src, "./" + name);
	        	}
        	}
        	
	        if (set != null) 
	        {
	        	fHelpSetCache.put( name, set);
	        	return set;
	        }
        	else
        	{
            	logger.warn(" WARNING!!! cannot find help basefile, {}, help is disabled.", name);
            	noHelpAvailable = true;
        	}
        }
        catch ( HelpSetException e)
        {
        	logger.error( "findHelpSet, e={}", e.toString());
        }

        return null;
    }

    private HelpSet tryThisOne( Object src, String name) throws HelpSetException
    {
    	HelpSet set = null;
    
        // Try to load the help set with specified name
        if (!noHelpAvailable) 
        {
            // Load the HelpSet
        	ClassLoader cl = (src == null) ? ClassLoader.getSystemClassLoader() :
        		src.getClass().getClassLoader();
        	
        	URL helpUrl = HelpSet.findHelpSet( cl, name);
        	//URL helpUrl = Util.findResourceUrl( this, name);
        	
        	if (helpUrl != null)
        	{
            	set = new HelpSet( cl, helpUrl);
            	if (set != null) fHelpSetCache.put(name, set);
        	}
       }
        if (set == null) {
        	logger.debug( "   tried help at: {}, null", name);
        } else {
        	logger.info( "Found help at {}", name);
        }      	
    	
    	return set;	
    }

	public void resetHelpAvailable()
	{
		noHelpAvailable = false;
	}
	
    private boolean noHelpAvailable = false;
    
    /**
     * Constructs the Singleton instance.
    **/
    private HelpManager()
    {
    }

    public void setPrimarySource( Object src)
    {
    	srcObject = src;	
    }
    
    Object srcObject;
	
	private Cursor getHelpCursor( Component srcComp)
	{
		if (fHelpCursor == null)
		{
	        // Load the custom help cursor
	        try
	        {
	            Image cursorIcon = Util.findResourceImage( srcComp, "/images/HelpCursor.gif");
	            if (cursorIcon != null)
	            {
	                fHelpCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon, new Point(1, 1), "HelpCursor");
	                
	                if (fHelpCursor == null)
	                {
	                    // Fallback to the ugly JavaHelp cursor
	                    fHelpCursor = (Cursor) UIManager.get("HelpOnItemCursor");
	                }	                
	            }
	        }
	        catch (Exception ex)
	        {
	            Util.printlnException( this, ex, true);
	        }			
		}
		return fHelpCursor;
	}
	
	
    /**
     * Duplicates CSH.DisplayHelpAfterTracking but adds the following enhancements:
     * Change cursor for all open windows, not just the active one, since the CSH
     * operation can work on the other windows.  Also, fix apparent bug
     * in JavaHelp/JDK1.2.2 where cursor does not reset after CSH operation.
    **/
    private class CSHTracker implements ActionListener
    {
        public CSHTracker()
        {
        }

        public void actionPerformed(ActionEvent e)
        {
            // Set cursor to the CSH cursor
            //setGlobalCursor(helpCursor);

            // track the CS Events and display help on the component
            Object obj = CSH.trackCSEvents();
            String helpID = null;
            HelpSet hs = null;
            
            if (obj != null && obj instanceof Component)
            {
            	Component comp = (Component) obj;
                // Get the custom help cursor
                //Cursor helpCursor = 
                getHelpCursor( comp);

                helpID = CSH.getHelpIDString( comp);
                hs = CSH.getHelpSet( comp);
            }
            
            if (hs == null)
            {
                hs = findHelpSet(null, fMainHelpSet);
            }
            
            try
            {
                Map.ID id = Map.ID.create(helpID, hs);
                if (id == null)
                {
                    id = hs.getHomeID();
                }
                fBroker.setCurrentID(id);
                fBroker.setDisplayed(true);
            }
            catch (Exception e2)
            {
                //MessageLogger.getInstance().writeError(this, "Error displaying context-sensitive help, id=" + helpID + ", exception=" + e2.toString());
                Util.printlnException( this, e2, true);
            }

            // Reset the cursor
            //setGlobalCursor(null);
        }

        /**
         * Sets the cursor for all the open windows in the application that
         * have registered with the WindowManager.
         *
         * @param	c	new cursor to set
        private void setGlobalCursor(Cursor c)
        {
            // Loop through all open windows, setting cursor on each
            for (Enumeration enum = WindowManager.getWindows(); enum.hasMoreElements();)
            {
                Window win = (Window) enum.nextElement();
                if (win.isShowing())
                {
                    setCursor(win, c);
                }
            }
        }
        **/

//        /**
//         * Sets the cursor for the specified component and all its children.
//         *
//         * @param	c	new cursor to set
//        **/
//        private void setCursor(Component comp, Cursor c)
//        {
//            comp.setCursor(c);
//
//            if (comp instanceof Container)
//            {
//                Container root = (Container) comp;
//
//                for (int i = 0; i < root.getComponentCount(); ++i)
//                {
//                    Component subComp = root.getComponent(i);
//
//                    subComp.setCursor(c);
//
//                    if (subComp instanceof Container)
//                    {
//                        setCursor(subComp, c);
//                    }
//                }
//            }
//        }
    }
}
/**
 * $Log: HelpManager.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.7.4.2  2005/11/19 20:34:55  sandyg
 * last of java 5 conversion, created swingworker, removed threads packages.
 *
 * Revision 1.7.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.7.2.1  2005/08/14 21:46:01  sandyg
 * Helps in non-English working again
 *
 * Revision 1.7  2005/05/26 01:45:43  sandyg
 * fixing resource access/lookup problems
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
