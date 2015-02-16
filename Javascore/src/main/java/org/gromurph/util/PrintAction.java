//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PrintAction.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.gromurph.util.swingworker.SwingWorker;


/**
 * Prints the contents of a Printable
 * If the module has not overridden the default getPrintable() then a screen snap
 * of the module will be displayed (such as the swing components can do that)
 *
**/
public class PrintAction extends AbstractAction implements ActionListener
{
    DialogTextInfo fParent = null;

    public static final String PRINT_ACTION = "Print".intern();
    public static final String PREVIEW_ACTION = "Preview".intern();

    private JDialog             fProgressDialog = null;
    private ProgressStatusPanel fProgressPanel = null;
    //protected Vector 			fListeners = new Vector();

    /**
     * Creates a new PrintAction for the specified Module.
     *
     * @param	parent		will print contents of this Module
    **/
    public PrintAction( DialogTextInfo parent)
    {
        super();
        fParent = parent;
    }

    /**
     * Calls print().
    **/
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand() == PRINT_ACTION)
        {
            print( true);
        }
        else
        {
            preview();
        }
    }

    /**
     * Prints the contents of the current Module image area.
     * Prompts user with standard print dialog boxes first.
    **/
    public void print()
    {
        print( true);
    }

    /**
     * Prints the contents of the current Module image area.
     * Prompts user with standard print dialog boxes first.
    **/
    public void print( boolean showProgress)
    {
        if (fParent == null)
        {
            // Nothing to print
            JOptionPane.showMessageDialog( null,
                    "Unable to print a null",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //String jobName = ( ((fParent == null) || (fParent.getName() == null)) ? "No Name" : fParent.getName());
        // Get a PrinterJob
        final PrinterJob job = PrinterJob.getPrinterJob();
        //job.setJobName( jobName);

        // Get the page format from the user
        PageFormat format = job.pageDialog(job.defaultPage());

        // Create a Book to contain all the page info
        // Pass the canvas to the print job, since canvas is a Printable
        Book document = new Book();
        document.append( fParent, format, fParent.getPageCount(format));
        job.setPageable(document);

        // Put up the dialog box
        if (job.printDialog())
        {
            // Print the job if the user didn't cancel printing

            // Display the progress panel while printing
            startProgressPanel("Printing...", 0, 100);

            // Do the printing in a separate thread since it can take a long time
            new PrintWorker( fParent, job);
        }
    }

    /**
     * Brings up a screen with a Print Preview of the current Module image area.
    **/
    public void preview()
    {
        if (fParent == null)
        {
            // Nothing to print
            JOptionPane.showMessageDialog(fParent,
                    "Unable to print with no image selected.",
                    fParent.getName(),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Thread runner = new Thread() {
        	@Override public void run()
            {
                String title = fParent.getName();
                if (title == null) title = "Print Preview";
                fParent.printStart( null, null); // no task instance, no progress panel
                new PrintPreview( PrintAction.this, fParent, title);
            }
        };
        runner.start();
    }

    private void initializeProgressDialog()
    {
        fProgressDialog = new JDialog();
        fProgressDialog.setTitle("Printing...");
        fProgressDialog.getContentPane().setLayout( new java.awt.BorderLayout());

        Dimension size = new Dimension( 200, 100);
        fProgressDialog.setSize( size);

        // Center on screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        fProgressDialog.setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);

        fProgressPanel = new ProgressStatusPanel();
        fProgressPanel.setBorder( javax.swing.BorderFactory.createEmptyBorder());

        fProgressDialog.getContentPane().add( fProgressPanel, java.awt.BorderLayout.CENTER);
    }

    public void startProgressPanel(String msg, int min, int max)
    {
        if (fProgressDialog == null)
        {
            initializeProgressDialog();
        }

        // Reset the progress status panel
        fProgressPanel.setMessage(msg);
        fProgressPanel.setProgressMinMax(min, max);

        fProgressDialog.setVisible(true);
    }

    public void finishProgressPanel()
    {
        fProgressDialog.setVisible(false);
    }

    /**
     * During an image load, this method updates the progress status panel
     * for every progress update event that is sent.
    protected void updateProgressStatus( ProgressEvent e)
    {
        if (e.getId() == ProgressEvent.TASK_PROGRESS)
        {
            // Progress is number between 0 and 100 indicating percent complete
            // Min and Max have already been set
            fProgressPanel.setStatus((int) Math.floor(e.getBytesRead()) + "%");
            fProgressPanel.updateProgress((int) e.getBytesRead());
        }
    }
    **/


    /**
     * Performs all the print calculations in a separate thread.
     * A progress bar is shown to the user while the printing occurs.
    **/
    protected class PrintWorker extends SwingWorker
    {
        private DialogTextInfo		fModule = null;
        private PrinterJob	fJob = null;

        public PrintWorker(Printable mod, PrinterJob job)
        {
            super();
            fModule = (DialogTextInfo) mod;
            fJob = job;
        }

        @Override public Object construct()
        {
            try
            {
                //MessageLogger.getInstance().writeInfo(fParent, "Printing...");

                //TaskManager.getInstance().registerTask(fParent, "Print Job");
                fModule.printStart( fParent, fProgressPanel);
                fJob.print();
                //MessageLogger.getInstance().writeInfo(fParent, "Finished Printing.");
            }
            catch (Exception ex)
            {
                Util.showError( ex, true);
            }
            finally
            {
                //TaskManager.getInstance().unregisterTask(fParent);
            }

            return null;
        }

        @Override public void finished()
        {
            // Hide the progress indicator and restore the image viewer
            finishProgressPanel();
        }
    }
}
/**
 * $Log: PrintAction.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.5.4.1  2005/11/19 20:34:55  sandyg
 * last of java 5 conversion, created swingworker, removed threads packages.
 *
 * Revision 1.5  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.4  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.3  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
