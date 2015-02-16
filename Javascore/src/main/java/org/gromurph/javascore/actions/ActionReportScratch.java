//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportScratch.java,v 1.5 2006/04/15 23:39:23 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.io.PrintWriter;

import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;

/**
 * Generates a report of the series standings
**/
public class ActionReportScratch extends ActionReport
{
    String fSortOrder = Entry.SAILID_PROPERTY;

    public static String TABNAME = "Entries";
    @Override public String getTabName() { return TABNAME; }

    @Override public String getReportName()
    {
        return java.text.MessageFormat.format(
            res.getString( "ReportScratchLabelEntryList"),
            new Object[] { res.getString( "Gen" + fSortOrder) });
    }

    private static ReportOptions sReportOptions;

    public static ReportOptions getDefaultReportOptions()
    {
        if (sReportOptions == null)
        {
            sReportOptions = new ReportOptions( TABNAME);

            sReportOptions.setOptionLocationValues(
                //             Skip,  Crew,   Boat,  Club,   mna,   rsa,   rating, fullrating
                new String[] { "1.2", "none", "1.1", "3.1", "none", "4.1", "2.1", "none"} );
        }
        return sReportOptions;
    }

    public void createReportFile( String dir, String filename, Regatta regatta, Object obj, String sortName)
        throws java.io.IOException
    {
        fSortOrder = sortName;
        super.createReportFile( dir, filename, regatta, obj);
    }

    @Override public void reportForDivision( PrintWriter pw, AbstractDivision div, EntryList entries, boolean is1D)
    {
        entries.sort( fSortOrder);

        // init table
        pw.println("<table class=" + SCRATCH_TABLECLASS + ">");

        // init header row
        pw.println( "<thead><tr>");
        generateDescriptiveHeaders( pw);
        pw.println("</tr></thead>");

        // loop thru entries add them in
        for (int i = 0; i < entries.size(); i++)
        {
            pw.println("<tr>");
            Entry e = entries.get(i);
            generateDescriptiveCells( pw, e);
            pw.println("</tr>");
        }

        pw.println("</table>");
    }

}
/**
 * $Log: ActionReportScratch.java,v $
 * Revision 1.5  2006/04/15 23:39:23  sandyg
 * report tweaking for Miami OCR, division splits
 *
 * Revision 1.4  2006/01/15 21:10:40  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:42  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.9.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.9  2004/04/10 20:49:30  sandyg
 * Copyright year update
 *
 * Revision 1.8  2003/04/27 21:35:31  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.7  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/01/04 17:33:06  sandyg
 * Prefix/suffix overhaul
 *
*/
