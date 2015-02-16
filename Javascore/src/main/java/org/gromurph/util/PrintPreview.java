//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PrintPreview.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.MatteBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fairly generic class that provides a print preview capability
 * Code adapted from the online book "Swing" by Matt ? and Pavel ?
 * I forget the online url
 */
public class PrintPreview extends JFrame
{
    protected int               fPageWidth;
    protected int               fPageHeight;
    protected Printable         fTarget;
    protected JComboBox         fComboBoxScale;
    protected PreviewContainer  fPanelPreview;
    protected ActionListener    fPrintListener;

    public PrintPreview( Printable target)
    {
        this( null, target, "Print Preview");
    }

    public PrintPreview( ActionListener printListener, Printable target, String title)
    {
        super( title);
        setSize( 370, 510);
        fTarget = target;

        JToolBar toolbar = new JToolBar();
        JButton buttonPrint = new JButton( "Print");
		buttonPrint.setMnemonic('p');
		buttonPrint.setToolTipText("Print the preview contents");
        if (printListener == null)
        {
            fPrintListener = new ActionListener() {
                public void actionPerformed( ActionEvent e)
                {
                    try
                    {
                        // Use default printer, no dialog
                        PrinterJob prnJob = PrinterJob.getPrinterJob();
                        prnJob.setPrintable( fTarget);
                        setCursor( Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR));
                        prnJob.print();
                        setCursor( Cursor.getPredefinedCursor(
                        Cursor.DEFAULT_CURSOR));
                        dispose();
                    }
                    catch ( PrinterException ex)
                    {
                		Logger l = LoggerFactory.getLogger(this.getClass());
            			l.error( "Exception=" + ex.toString(), ex);
                    }
                }
            };
        }
        else
        {
            fPrintListener = printListener;
        }
        buttonPrint.addActionListener( fPrintListener);
        toolbar.add( buttonPrint);

        JButton buttonClose = new JButton( "Close");
        buttonClose.setMnemonic('c');
        buttonClose.setToolTipText("Close Preview");
        ActionListener lst = new ActionListener() {
            public void actionPerformed( ActionEvent e)
            {
                 dispose();
            }
        };
        buttonClose.addActionListener( lst);
        toolbar.add( buttonClose);

        String[] scales = { "10 %", "25 %", "50 %", "100 %" };
        fComboBoxScale = new JComboBox( scales);
        fComboBoxScale.setToolTipText("Zoom");

        int scale = 50;
        fComboBoxScale.setSelectedItem( "50 %");

        lst = new ActionListener() {
            public void actionPerformed( ActionEvent e)
            {
                Thread runner = new Thread() {
                	@Override public void run()
                    {
                        String str = fComboBoxScale.getSelectedItem().toString();
                        if ( str.endsWith( "%")) str = str.substring( 0, str.length()-1);
                        str = str.trim();
                        int sc = 0;

                        try
                        {
                            sc = Integer.parseInt( str);
                        }
                        catch ( NumberFormatException ex)
                        {
                            return;
                        }

                        int w = fPageWidth*sc/100;
                        int h = fPageHeight*sc/100;

                        Component[] comps = fPanelPreview.getComponents();
                        for ( int k=0; k<comps.length; k++)
                        {
                            if ( !( comps[k] instanceof PagePreview)) continue;
                            PagePreview pp = ( PagePreview)comps[k];
                            pp.setScaledSize( w, h);
                        }
                        fPanelPreview.doLayout();
                        fPanelPreview.getParent().getParent().validate();
                    }
                };
                runner.start();
            }

        };

        fComboBoxScale.addActionListener( lst);

        Dimension dim = new Dimension( 80, 35);

        buttonPrint.setMaximumSize( dim);
        buttonClose.setMaximumSize( dim);
        fComboBoxScale.setMaximumSize( dim);

        fComboBoxScale.setEditable( true);
        toolbar.addSeparator();
        toolbar.add( fComboBoxScale);
        getContentPane().add( toolbar, BorderLayout.NORTH);

        fPanelPreview = new PreviewContainer();

        PrinterJob prnJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = prnJob.defaultPage();
        if ( pageFormat.getHeight()==0 || pageFormat.getWidth()==0)
        {
    		Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Unable to determine default page size");
            return;
        }

        fPageWidth = ( int)( pageFormat.getWidth());
        fPageHeight = ( int)( pageFormat.getHeight());

        int pageIndex = 0;
        int w = fPageWidth*scale/100;
        int h = fPageHeight*scale/100;

        try
        {
            while ( true)
            {
                BufferedImage img = new BufferedImage( fPageWidth,
                    fPageHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                g.setColor( Color.white);
                g.fillRect( 0, 0, fPageWidth, fPageHeight);
                if ( fTarget.print( g, pageFormat, pageIndex) != Printable.PAGE_EXISTS) break;
                PagePreview pp = new PagePreview( w, h, img);
                fPanelPreview.add( pp);
                pageIndex++;
            }
        }
        catch ( PrinterException e)
        {
    		Logger l = LoggerFactory.getLogger(this.getClass());
			l.error( "Exception=" + e.toString(), e);
	        }

        JScrollPane ps = new JScrollPane( fPanelPreview);
        getContentPane().add( ps, BorderLayout.CENTER);

        setDefaultCloseOperation( DISPOSE_ON_CLOSE);
        setVisible( true);
    }

    class PreviewContainer extends JPanel
    {
        protected int H_GAP = 16;
        protected int V_GAP = 10;

        @Override public Dimension getPreferredSize()
        {
            int n = getComponentCount();
            if ( n == 0) return new Dimension( H_GAP, V_GAP);

            Component comp = getComponent( 0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;

            Dimension dp = getParent().getSize();
            int nCol = Math.max( ( dp.width-H_GAP)/( w+H_GAP), 1);
            int nRow = n/nCol;
            if ( nRow*nCol < n) nRow++;

            int ww = nCol*( w+H_GAP) + H_GAP;
            int hh = nRow*( h+V_GAP) + V_GAP;
            Insets ins = getInsets();

            return new Dimension( ww+ins.left+ins.right,
                hh+ins.top+ins.bottom);
        }

        @Override public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        @Override public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        @Override public void doLayout()
        {
            Insets ins = getInsets();
            int x = ins.left + H_GAP;
            int y = ins.top + V_GAP;

            int n = getComponentCount();
            if ( n == 0) return;

            Component comp = getComponent( 0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;

            Dimension dp = getParent().getSize();
            int nCol = Math.max( ( dp.width-H_GAP)/( w+H_GAP), 1);
            int nRow = n/nCol;

            if ( nRow*nCol < n) nRow++;

            int index = 0;
            for ( int k = 0; k<nRow; k++)
            {
                for ( int m = 0; m<nCol; m++)
                {
                    if ( index >= n) return;
                    comp = getComponent( index++);
                    comp.setBounds( x, y, w, h);
                    x += w+H_GAP;
                }
                y += h+V_GAP;
                x = ins.left + H_GAP;
            }
        }
    }

    class PagePreview extends JPanel
    {
        protected int m_w;
        protected int m_h;
        protected Image m_source;
        protected Image m_img;

        public PagePreview( int w, int h, Image source)
        {
            m_w = w;
            m_h = h;
            m_source= source;
            m_img = m_source.getScaledInstance( m_w, m_h,
                Image.SCALE_SMOOTH);
            m_img.flush();
            setBackground( Color.white);
            setBorder( new MatteBorder( 1, 1, 2, 2, Color.black));
        }

        public void setScaledSize( int w, int h)
        {
            m_w = w;
            m_h = h;
            m_img = m_source.getScaledInstance( m_w, m_h,
                Image.SCALE_SMOOTH);
            repaint();
        }

        @Override public Dimension getPreferredSize()
        {
            Insets ins = getInsets();
            return new Dimension( m_w+ins.left+ins.right,
                m_h+ins.top+ins.bottom);
        }

        @Override public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        @Override public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        @Override public void paint( Graphics g)
        {
            g.setColor( getBackground());
            g.fillRect( 0, 0, getWidth(), getHeight());
            g.drawImage( m_img, 0, 0, this);
            paintBorder( g);
        }
    }
}
/**
 * $Log: PrintPreview.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
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
