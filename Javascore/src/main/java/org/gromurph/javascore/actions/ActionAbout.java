//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.gromurph.javascore.JavaScoreProperties;


/**
 * Brings up the About dialog
 */
public class ActionAbout extends AbstractAction
{
    static ResourceBundle res = JavaScoreProperties.getResources(); 
    private static final String LINEBREAK = "<br>";
    private static final String SPACE = " ";

    public ActionAbout()
    {
        super( res.getString("MenuAbout"));
        putValue( Action.MNEMONIC_KEY,
            new Integer( res.getString( "MenuAboutMnemonic").charAt(0)));
    }

    private static JTabbedPane sPanel;

    public void actionPerformed(ActionEvent parm1)
    {
    	
        if (sPanel == null) buildPanel();

        JOptionPane.showMessageDialog(
            null,
            sPanel,
            res.getString("AboutTitle"),
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void buildPanel()
    {
        /**
         * Main panel has a tabbed panel with currently 2 subpanels
         * first is primary version information
         * second is the credits panel
         */

        sPanel = new JTabbedPane();
        sPanel.setPreferredSize( new Dimension( 380, 300));

        // VERSION tab
        JPanel panelVersion = createVersionPanel();
        sPanel.add( res.getString("AboutTabVersion"), new JScrollPane(panelVersion));

        // CREDITS panel ----------------

        JPanel panelCredits = createCreditsPanel();
        sPanel.add( res.getString("AboutTabCredits"), new JScrollPane(panelCredits));
        
        // DEBUG panel ----------------

        JPanel panelDebug = createDebugPanel();
        sPanel.add( "Debug", new JScrollPane(panelDebug));                
    }

    private JPanel createVersionPanel()
    {
        JPanel panel = new JPanel( new BorderLayout());

        StringBuffer sb = new StringBuffer( 512);
        sb.append("<html><font color=black face=Verdana size=2><br>");
        sb.append(res.getString("AboutVersionStart"));
        sb.append(SPACE);
        sb.append( JavaScoreProperties.getVersion());
        sb.append( ", JVM: "); sb.append( System.getProperty("java.vm.version"));
        sb.append(LINEBREAK);
        sb.append( "Locale: "); sb.append( java.util.Locale.getDefault().toString());
        sb.append(LINEBREAK);
        sb.append(res.getString("AboutCopyright"));
        sb.append(LINEBREAK);
        sb.append(res.getString("AboutEmail"));
        sb.append(LINEBREAK);
        sb.append(LINEBREAK);
        sb.append(res.getString("AboutOpenSource"));
        
//        try {
//        	sb.append(LINEBREAK);
//            URI regattaReportUri = JavaScoreProperties.getRegattaReportUri();
//            sb.append("<br>regattaReportUri.toString(): ");
//            sb.append( regattaReportUri.toString());
//        } catch (Exception e) {}
       

        sb.append("</font></html>");

        JLabel aboutLabel = new JLabel();
        aboutLabel.setText( sb.toString());
        aboutLabel.setForeground( Color.black);
        aboutLabel.setVerticalAlignment( SwingConstants.TOP);
        aboutLabel.setHorizontalAlignment( SwingConstants.LEFT);
        panel.add( aboutLabel, SwingConstants.CENTER);
        
        return panel;
    }
    
    private JPanel createCreditsPanel()
    {
    	JPanel panel = new JPanel( new BorderLayout());

        StringBuffer sb = new StringBuffer( 512);
        sb.append("<html><font color=black face=Verdana size=2><br>");
        sb.append(res.getString("AboutCreditsAuthors"));
        sb.append(LINEBREAK);
        sb.append(LINEBREAK);
        sb.append(res.getString("AboutCreditsTesters"));
        sb.append(LINEBREAK);
        sb.append(LINEBREAK);
        sb.append(res.getString("AboutCreditsLanguages"));
        sb.append("<br></font></html>");

        JLabel creditLabel = new JLabel();
        creditLabel.setText( sb.toString());
        creditLabel.setForeground( Color.black);
        creditLabel.setVerticalAlignment( SwingConstants.TOP);
        creditLabel.setHorizontalAlignment( SwingConstants.LEFT);
        panel.add( creditLabel, SwingConstants.CENTER);    	
        
        return panel;
    }

    private JPanel createDebugPanel()
    {
    	JPanel panel = new JPanel( new BorderLayout());

        StringBuffer sb = new StringBuffer( 512);
        sb.append("<html><font color=black face=Verdana size=2><br>");
        
        String[] vars = new String[] {"java.class.path", "java.library.path",
        	"java.ext.dirs", "user.home", "user.dir" };
        
        for (int i = 0; i < vars.length; i++)
        {
        	String v = vars[i];
        	sb.append( "<br>");
        	sb.append( v);
        	sb.append( "=");
        	sb.append( System.getProperty( v));
        	sb.append( "<br>");
        }   
        

        sb.append("<br></font></html>");
        
        JLabel label = new JLabel();
        label.setText( sb.toString());
        label.setForeground( Color.black);
        label.setVerticalAlignment( SwingConstants.TOP);
        label.setHorizontalAlignment( SwingConstants.LEFT);
        panel.add( label, SwingConstants.CENTER);    	
        
        return panel;

    }
    
    public static void main( String[] args)
    {
        ActionAbout a = new ActionAbout();
        a.actionPerformed( null);
        System.exit(0);
    }
}

