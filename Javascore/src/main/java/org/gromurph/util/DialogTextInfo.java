//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogTextInfo.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.*;


/**
 * Dialog and supporting features for displaying, previewing and printing an html report
**/
public class DialogTextInfo extends JDialog implements Printable
{

    JScrollPane fScrollPane;
    JTextPane   fTextInfo;

    JButton     fPrintButton;
    JButton     fPreviewButton;
    JButton     fSaveToDiskButton;

    String      fReportTitle;
    String      fReportBody;

    ArrayList   fPages;
    PageFormat  fCurrentPageFormat;
    Font        fFont;

    public static final String PRINT_ACTION = "Print".intern();
    
    public static final String PRINTER_ICON = "/images/Printer16.gif";
    public static final String PREVIEW_ICON = "/images/Preview16.gif";
    public static final String SAVETODISK_ICON = "/images/SaveToDisk16.gif";

    public DialogTextInfo()
    {
        super();
        setSize(640,480);

        getContentPane().setLayout(new BorderLayout(0,0));

        fScrollPane = new JScrollPane();
        fScrollPane.setOpaque(true);
        fScrollPane.setPreferredSize( new Dimension(
            getInsets().left + getInsets().right + 600,
            getInsets().top + getInsets().bottom + 480));
        getContentPane().add( fScrollPane, BorderLayout.CENTER);

        fTextInfo = new JTextPane();
        fTextInfo.setEditorKit( new javax.swing.text.html.HTMLEditorKit());
        fTextInfo.setEditable(false);

        fScrollPane.getViewport().add(fTextInfo);

        JToolBar toolbar = new JToolBar();
        getContentPane().add( toolbar, BorderLayout.NORTH);

        ImageIcon icon = Util.getImageIcon(this, PRINTER_ICON);
        if (icon != null)
        {
            fPrintButton = new JButton( icon);
        }
        else
        {
            fPrintButton = new JButton( "Print");
        }
        fPrintButton.setToolTipText( PRINT_ACTION);
        fPrintButton.addActionListener(  new ActionListener() {
            public void actionPerformed( ActionEvent event)
            {
                (new PrintAction( DialogTextInfo.this)).print();
            }
        });

        toolbar.add( fPrintButton);

        icon = Util.getImageIcon(this, PREVIEW_ICON);
        if (icon != null)
        {
            fPreviewButton = new JButton( icon);
        }
        else
        {
            fPreviewButton = new JButton( "Preview");
        }
        fPreviewButton.setToolTipText( "Preview");
        fPreviewButton.addActionListener(  new ActionListener() {
            public void actionPerformed( ActionEvent event)
            {
                // do nothing(new PrintAction( DialogTextInfo.this)).preview();
            }
        });
        toolbar.add( fPreviewButton);

        icon = Util.getImageIcon(this, SAVETODISK_ICON);
        if (icon != null)
        {
            fSaveToDiskButton = new JButton( icon);
        }
        else
        {
            fSaveToDiskButton = new JButton( "Save to Disk");
        }
        fSaveToDiskButton.setToolTipText( "Save To Disk");
        fSaveToDiskButton.addActionListener(  new ActionListener() {
            public void actionPerformed( ActionEvent event)
            {
                // do nothing(new PrintAction( DialogTextInfo.this)).SaveToDisk();
            }
        });
        toolbar.add( fSaveToDiskButton);

        // til they get implemented
        fSaveToDiskButton.setEnabled( false);
        fPreviewButton.setEnabled( false);

    }

    public String getReportTitle()
    {
        return fReportTitle;
    }

    public void setReportTitle( String title)
    {
        fReportTitle = title;
        refreshReport();
    }

    public String getReportBody()
    {
        return fReportBody;
    }

    public void setReportBody( String inText)
    {
        fReportBody = inText;
        refreshReport();
    }

    public void refreshReport()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "<html></title>");
        sb.append( fReportTitle);
        sb.append("</title><body><h1><b>");
        sb.append( fReportTitle);
        sb.append("</b></h1><p>");

        sb.append( fReportBody);
        sb.append( "</body></html>");

        fTextInfo.setText( sb.toString());
    }

    /**
     * Part of printing support, returns the number of pages to be printed
     * Override this is subclasses to return something other than 1
     */
    public int getPageCount(PageFormat pf)
    {
        // calculate pagecount based on pf and articles
        ArrayList pgs = repaginate (pf);
        return pgs.size();
    }

    protected Object				fPrintTask;
    protected ProgressStatusPanel   fProgressPanel;
    protected boolean				fNewPrint;
    protected double				fPrintOffsetX;
    protected double				fPrintOffsetY;

    /**
     * performs initialization work (if any) at start of a print job
     */
    public void printStart( Object taskObject, ProgressStatusPanel progressPanel)
    {
        fPrintTask = taskObject;
        fNewPrint = true;
        fPrintOffsetX = 0.0;
        fPrintOffsetY = 0.0;
        fProgressPanel = progressPanel;

        fPages = null;
    }


    public int print (Graphics g, PageFormat pf, int idx)
          // throws PrinterException
    {
        // Printable's method implementation
        fCurrentPageFormat = pf;

        if (fPages == null) fPages = repaginate (pf);

        if (idx >= fPages.size ())
        {
            return Printable.NO_SUCH_PAGE;
        }

        g.setFont ( fFont);
        g.setColor (Color.black);

        renderPage( g, pf, idx);
        return Printable.PAGE_EXISTS;
    }


    ArrayList repaginate (PageFormat pf)
    {
        // step through articles, creating pages of lines
        int maxh = (int) pf.getImageableHeight ();
        int lineh = fFont.getSize ();

        ArrayList< ArrayList<String>> pgs = new ArrayList< ArrayList<String>>();
        ArrayList<String> page = new ArrayList<String>();
        int pageh = 0;

        // headers - no header on first page
        //page.add ( fReportTitle + ", page " + Integer.toString( pgs.size()+1));
        //page.add (" ");
        //pageh += (lineh * 2);

        // body
        StringTokenizer st = new StringTokenizer( fTextInfo.getText(), "\n");
        while (st.hasMoreTokens ())
        {
            String line = st.nextToken ();
            if (pageh + lineh > maxh)
            {
                // need new page
                pgs.add( page);
                page = new ArrayList<String>();
                page.add ( fReportTitle + ", page " + Integer.toString( pgs.size()+1));
                page.add (" ");
                pageh = (lineh * 2);
            }
            page.add (line);
            pageh += lineh;
        }
        pgs.add (page);

        return pgs;
    }

    void renderPage (Graphics g, PageFormat pf, int idx)
    {
        // render the lines from the pages list
        int xo = (int) pf.getImageableX();
        int yo = (int) pf.getImageableY();
        int y = fFont.getSize();

        ArrayList page = (ArrayList) fPages.get( idx);

        Iterator it = page.iterator();
        while (it.hasNext())
        {
            String line = (String) it.next();
            g.drawString(line, xo, y + yo);
            y += fFont.getSize();
        }
    }

}
/**
 * $Log: DialogTextInfo.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.7.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.7  2005/05/26 01:45:43  sandyg
 * fixing resource access/lookup problems
 *
 * Revision 1.6  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.5  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.4  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
