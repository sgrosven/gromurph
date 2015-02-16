//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportFinish.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.ReportOptions;

/**
 * Generates a report of the series standings
**/
public class ActionReportFinish extends ActionReport
{
    public static String TABNAME = "FinishSheet";
    @Override public String getTabName() { return TABNAME; }

    @Override public String getReportName()
    {
        // sg note, yep this is really the label lookup key.. started as a typo
        // but I dont want to change it just for that reason
        return res.getString( "ReportFinish/LabelFinishSheet");
    }

    private static ReportOptions sReportOptions;

    public static ReportOptions getDefaultReportOptions()
    {
        if (sReportOptions == null)
        {
            sReportOptions = new ReportOptions( TABNAME);

            sReportOptions.setOptionLocationValues(
                //             Skip,  Crew,    Boat, Club,   mna,   rsa, rating, fullrating
                new String[] { "none", "none", "1.1", "none", "none", "none", "none", "none"} );
        }
        return sReportOptions;
    }

    @Override public void generateBody( PrintWriter pw, Object obj)
    {
        EntryList entries = fRegatta.getAllEntries();

        List<String> options = fRegatta.getReportOptions( getTabName()).getOptionLocationValues();
        boolean showList = false;
        for (int i = 0; ( (i < options.size()) && (!showList)); i++)
        {
            showList = !options.get(i).equals("none");
        }

        // init table -this contains race/recorder/date
        pw.println("<br><table class=" + FINISHSHEET1_TABLECLASS + "><tr>");
        pw.print( "<td  width=\"8%\" align=\"right\">");
        	pw.print( res.getString("ReportLabelRace"));
            pw.println( "</td><td class=" + FINISHSHEET1_CELLCLASS + " width=\"10%\">&nbsp;</td>");
        pw.print( "<td  width=\"8%\" align=\"right\">");
        	pw.print( res.getString("ReportFinishLabelRecorder"));
            pw.println( "</td><td class=" + FINISHSHEET1_CELLCLASS + " width=\"20%\">&nbsp;</td>");
        pw.print( "<td  width=\"8%\" align=\"right\">");
        	pw.print( res.getString("ReportFinishLabelDate"));
            pw.println( "</td><td class=" + FINISHSHEET1_CELLCLASS + " width=\"20%\">&nbsp;</td>");
        pw.println("</tr></table>");
        
        pw.println("<br>");
        
        pw.println("<table class=" + FINISHSHEET2_TABLECLASS + " width=\"50%\">");
        pw.print( "<tr><td align=\"right\">");
            pw.print( res.getString("RegattaTitleStartingClasses"));
        pw.print( ": </td><td  align=\"center\">");
            pw.print( res.getString("RaceLabelStartTime"));
        pw.print( ": </td><td  align=\"center\">");
            pw.print( res.getString("RaceLabelLength"));
        pw.println(": </td></tr>");
        
        String blankCell = "<td class=" + FINISHSHEET2_CELLCLASS + " >&nbsp;</td>";
        for (Division div : fRegatta.getDivisions())
        {
        	pw.print("<tr><td align=\"right\">");
        	pw.print( div.getName());
        	pw.print( "</td>");
        	pw.print( blankCell);
        	pw.println( blankCell);
        }
        
        pw.println( "</table>");

        EntryList dupIds = fRegatta.getAllEntries().getDuplicateIds();
        List<Integer> etags = new ArrayList<Integer>();

        etags.add( new Integer( Entry.SHOW_BOW));
        etags.add( new Integer( Entry.SHOW_BOAT));
        etags.add( new Integer( Entry.SHOW_SKIPPER));
        etags.add( new Integer( Entry.SHOW_DIVISION));
        etags.add( new Integer( Entry.SHOW_RATING));

        if (dupIds.size() > 1)
        {
            pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
            pw.print( res.getString("ReportFinishDupIds"));
            pw.println("</p>");

            pw.println("<ul>");
            for (Iterator di = dupIds.iterator(); di.hasNext();)
            {
                Entry e = (Entry) di.next();
                pw.print("  <li>");
                pw.print( res.getString("GenSail"));
                pw.print( " ");
                pw.print( e.toString( 0, true));
                if ( e.getBow() != null && e.getBow().toString().length() > 0)
                {
                    pw.print( ", ");
                    pw.print( res.getString("GenBow"));
                    pw.print( " ");
                    pw.print( e.getBow());
                }
                pw.print( ": ");
                pw.print( e.toString( etags, true, null));
                pw.println( "</li>");
            }
            pw.println("</ol>");
        }

        // init table
        pw.println("<table><tr>");

        // center column
        pw.print( "<td width=\"");
        pw.print( (showList ? "70" : "100"));
        pw.print( "%\" align=\"center\">");

        generateFinishTable( pw, entries);
        pw.println( "</td>");

        // Left column
        if (showList)
        {
            pw.print( "<td  width=\"30%\" valign=\"top\" align=\"left\">");
            if (fRegatta.isUseBowNumbers())
            {
                generateListByBow( pw, entries, false);
            }
            else
            {
                generateListBySail( pw, entries, false);
            }
            pw.println( "</td>");
        }

        /* commented out - right column
        pw.println( "<td valign=\"top\" align=\"left\">");
            pw.print( getBodyFontTag());
        if (fRegatta.isUseBowNumbers())
        {
            // if using bow number, right side should be: by div, by sail
            generateListBySail( pw, entries, true);
        }
        else if (fRegatta.getNonEmptyDivisionList().size() > 1)
        {
            // if non-using bow, and more than one real division
            // show by div, by sail
            generateListBySail( pw, entries, true);
        }
        else
        {
            // only one division, no bows, show by name
            generateListByName( pw, entries);
        }
        pw.println( "</td>");
        */

        pw.println("</tr>");

        pw.println("</table>");
    }

    private void generateFinishTable( PrintWriter pw, EntryList entries)
    {
        pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
        pw.print( res.getString("GenFinishes"));
        pw.println("</p>");
        
		pw.println("<table class=" + FINISHSHEET3_TABLECLASS + ">");
		pw.print("<tr><td width=\"10%\" align=\"right\"> ");
		pw.print( res.getString("GenFin"));
		pw.print( "</td>");
		pw.print("<td width=\"30%\"  align=\"center\">");
		pw.print( res.getString("GenBowSail"));
		pw.print( "</td>");
		pw.print("<td width=\"30%\"  align=\"center\">");
		pw.print( res.getString("GenFinishTime"));
		pw.println( "</td>");
		pw.print("   <td width=\"30%\"  align=\"center\">");
		pw.println( "Notes</td></tr>");

         // loop thru entries add them in with break line between
        String blankCell = "<td class=" + FINISHSHEET3_CELLCLASS + " >&nbsp;</td>";
        for (int i = 0; i < entries.size()+5; i++)
        {
            pw.print( "   <tr><td align=\"right\">");
            if (i == entries.size()-1) pw.print("<<");
            pw.print( i+1);
            if (i == entries.size()-1) pw.print(">>");
            pw.print( "</td>");
            pw.print( blankCell); 
            pw.print( blankCell);
            pw.print( blankCell);
            pw.println( "</tr>");
        }
        pw.println("</table>");
    }

    public String generateDescriptiveString( Entry entry)
    {
        StringBuffer sb = new StringBuffer();
        ReportOptions ro = fRegatta.getReportOptions( getTabName());
        for (int i = 0; i < ro.getMaxColumns(); i++)
        {
            String s = ro.getColumnHeader(i);
            if (s.length() > 0)
            {
                sb.append( ro.getColumnEntry( i, entry));
                sb.append( " ");
            }
        }
        return sb.toString();
    }

    private void generateListBySail( PrintWriter pw, EntryList entries, final boolean byDiv)
    {
        pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
        if (byDiv) pw.print( res.getString( "ReportFinishLabelFleetByClassSail"));
        else pw.print( res.getString("ReportFinishLabelFleetBySail"));
        pw.println("</p><br>");

        if (byDiv) entries.sortDivisionSailId();
        else entries.sortSailId();

        Division lastDiv = null;
         // loop thru entries add them in with break line between
        for (int i = 0; i < entries.size(); i++)
        {
            Entry e = entries.get(i);
            if ( byDiv && !e.getDivision().equals( lastDiv))
            {
                lastDiv = e.getDivision();
                pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
                pw.print(lastDiv);
                pw.print("</p>");
            }
            pw.println( "<br>");
            pw.print( generateDescriptiveString( e));
        }
    }

    private void generateListByBow( PrintWriter pw, EntryList entries, final boolean byDiv)
    {
        pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
        pw.print( res.getString( "ReportFinishLabelFleetByBow"));
        pw.println("</p>");

        if (byDiv) entries.sortDivisionSailId();
        else entries.sortSailId();

        Division lastDiv = null;

         // loop thru entries add them in with break line between
        for (int i = 0; i < entries.size(); i++)
        {
            Entry e = entries.get(i);
            if ( byDiv && !e.getDivision().equals( lastDiv))
            {
                lastDiv = e.getDivision();
                pw.print("<p class=" + DIVISIONHEADER_PCLASS + ">");
                pw.print(lastDiv);
                pw.print("</p>");
            }
            pw.println( "<br>");
            pw.print( generateDescriptiveString( e));
        }
    }

    /**
     * customized to shorten it up, this is a working report not a fancy one
     */
    @Override public void generateHeader( PrintWriter pw)
    {
        // does nothing now
    }

    /**
     * customized to shorten it up, this is a working report not a fancy one
     */
    @Override public void generateFooter( PrintWriter pw)
    {
    	pw.println("<br>");
        pw.println( fRegatta.getSaveDate());
    }

}
/**
 * $Log: ActionReportFinish.java,v $
 * Revision 1.6  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
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
 * Revision 1.11.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.11  2004/04/10 20:49:30  sandyg
 * Copyright year update
 *
 * Revision 1.10  2003/05/07 01:18:18  sandyg
 * bold headings made consistent, fleet/division scoring problem fixed,
 * sail/bow header cleaned up
 *
 * Revision 1.9  2003/04/27 21:35:31  sandyg
 * more cleanup of unused variables... ALL unit tests now working
 *
 * Revision 1.8  2003/04/27 21:03:29  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.7  2003/01/04 17:33:05  sandyg
 * Prefix/suffix overhaul
 *
*/
