//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: JCalendarPopup.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.toedter.calendar.JCalendar;

/**
 * Uses the open-source JCalendar widget and wraps in a popup window.
 */
public class JCalendarPopup extends JButton implements PropertyChangeListener, ActionListener
{
    static ResourceBundle resUtil = Util.getResources();

    public static String DATE_PROPERTY = "Date";

    private Date fDate;
    private static DateFormat sFormat;
    private String fTitle;
    private JCalendar fPopup;

    static
    {
        sFormat = DateFormat.getDateInstance( DateFormat.SHORT);
    }

    public void setDate( Date d)
    {
        fDate = d;

        if ( fDate == null)
        {
            setText( resUtil.getString("GenNone"));
        }
        else
        {
            setText( sFormat.format( fDate));
            Calendar calendar = new java.util.GregorianCalendar();
            calendar.setTime( fDate);
            fPopup.setCalendar( calendar);
        }
    }

    public Date getDate()
    {
        return fDate;
    }

    public JCalendarPopup( String title, Date startDate)
    {
        super();

        fPopup = new JCalendar();
        fPopup.addPropertyChangeListener( this);

        setDate( startDate);
        addActionListener( this);
    }

    public void actionPerformed( ActionEvent event)
    {
        JOptionPane.showMessageDialog(
            this,
            fPopup,
            fTitle,
            JOptionPane.PLAIN_MESSAGE
            );
    }

    public void propertyChange( PropertyChangeEvent event)
    {
        try
        {
            Date oldDate = fDate;
            Calendar calendar = (Calendar) event.getNewValue();
            setDate( calendar.getTime());
            firePropertyChange( DATE_PROPERTY, oldDate, fDate);
        }
        catch (ClassCastException e) {} // ignore
        catch (NullPointerException e) {} // ignore
    }

    public static void main(String[] args)
    {
        JCalendarPopup dateTest1 = new JCalendarPopup( "Race start date", null);

        JOptionPane.showConfirmDialog(null, dateTest1);
        System.exit(0);
    }
}
/**
 * $Log: JCalendarPopup.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
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
