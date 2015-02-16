//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionReportCheckin.java,v 1.8 2006/05/19 05:48:42 sandyg Exp $
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
import java.util.Arrays;

import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.StartingDivisionList;


/**
 * Generates a check-in sheet
**/
public class ActionReportCheckin extends ActionReport
{
    public static String TABNAME = "CheckIn";

    protected boolean fByDivision = true;

    public void setByDivision( boolean bydiv)
    {
        fByDivision = bydiv;
    }

	protected boolean fByBow = false;

	public void setByBow( boolean bybow)
	{
		fByBow = bybow;
	}

	@Override public String getTabName() { return TABNAME; }

    @Override  public String getReportName()
    {
        if (fByDivision) 
        	if (fByBow)
        		return res.getString("ReportCheckInLabelByDivisionBow");
        	else
				return res.getString("ReportCheckInLabelByDivision");
        else return res.getString("ReportCheckInLabelByFleet");
    }

    private static ReportOptions sReportOptions;

    public static ReportOptions getDefaultReportOptions()
    {
        if (sReportOptions == null)
        {
            sReportOptions = new ReportOptions( TABNAME);

            sReportOptions.setOptionLocationValues(
                //             Skip,  Crew,    Boat, Club,   mna,   rsa     rating
                Arrays.asList( new String[] { "none", "none", "1.1", "none", "none", "none", "none", "none"} ));
            sReportOptions.setNameFormat( org.gromurph.util.Person.FORMAT_LASTONLY);
        }
        return sReportOptions;
    }

    @Override public void generateHeader( PrintWriter pw)
    {
        pw.println( "<p class=" + INFOHEADER_PCLASS + ">"+fRegatta.getSaveDate()+"</p>");
        pw.println("<br>");
    }

    @Override public void generateFooter( PrintWriter pw)
    {
    }

    protected int fColumns = 4;

    public int getColumns()
    {
        return fColumns;
    }

    public void setColumns( int c)
    {
        fColumns = c;
    }

    @Override public void generateBody( PrintWriter pw, Object obj)
    {
        EntryList entries = fRegatta.getAllEntries();
        StartingDivisionList divs = null;

        pw.print("<P class = " + DIVISIONLINKS_PCLASS + ">");
        pw.print( res.getString("ReportCheckinLabelEntryCounts"));
        pw.print(": ");
        pw.print( res.getString("ReportCheckinLabelFleet"));
        pw.print( "=");
        pw.println( entries.size());

        if (fRegatta.getNumDivisions() > 1)
        {
            for( Division div : fRegatta.getDivisions())
            {
                int count = entries.findAll( div).size();

                pw.print( "&nbsp;&nbsp;&nbsp;");
                pw.print( div.getName());
                pw.print("=");
                pw.println( count);
            }
        }
        pw.println("</p><br>");

        if ( entries.size() == 0)
        {
            pw.print( res.getString("ReportCheckinLableNoEntries"));
            return;
        }

        if (fByDivision)
        {

            // order divisions by starttime of first race if possible
            // otherwise order them by class size
            if ( (fRegatta.getNumRaces() > 0) &&
                 fRegatta.getRaceIndex(0).hasStartTimes() )
            {
                divs = fRegatta.getRaceIndex(0).getDivisionsByStartOrder(false);
            }
            else
            {
            	divs = new StartingDivisionList();
            	divs.addAll( fRegatta.getDivisions());
                divs.sortSize( fRegatta.getAllEntries());
            }
			
			if ( fRegatta.isUseBowNumbers() && fByBow)
			{
				entries.sortDivisionBow( divs);
			}
			else
			{
				entries.sortDivisionSailId( divs);
			}
        }
        else
        {
            entries.sortSailId();
        }

        pw.println("<table class=" + CHECKIN_TABLECLASS + ">");
		pw.println("<tr>");
        
        if (!fRegatta.isUseBowNumbers())
        {
			pw.print("<td>");
            pw.print( res.getString("GenSailNum"));
			pw.print( "</td>");
        }
        else if (fByBow)
        {
			pw.print("<td>");
			pw.print( res.getString("GenBowNum"));
			pw.print( "</td>");
			
			pw.print("<td>");
			pw.print( res.getString("GenSailNum"));
			pw.print( "</td>");
        }
        else
        {
			pw.print("<td>");
			pw.print( res.getString("GenSailNum"));
			pw.print( "</td>");
			
			pw.print("<td>");
			pw.print( res.getString("GenBowNum"));
			pw.print( "</td>");
        }
		pw.print("<td>");
        pw.print( res.getString("GenDivision"));
        pw.print( "</td><td>");
        pw.print( res.getString("GenBoat"));
        pw.println("</td></tr>");

        Division lastdiv = entries.get(0).getDivision();

        // loop thru entries add them in
        for (int i = 0; i < entries.size(); i++)
        {
            Entry e = entries.get(i);

            if (fByDivision && (e.getDivision() != lastdiv))
            {
                pw.print("<tr><td>[ ]&nbsp;&nbsp;</td></tr>");
                pw.print("<tr><td>[ ]&nbsp;&nbsp;</td></tr>");
                pw.print("<tr><td>&nbsp;</td></tr>");
                lastdiv = e.getDivision();
            }
            pw.print("<tr><td>");
            pw.print( "[ ]&nbsp;&nbsp;" );
            
            if (!fRegatta.isUseBowNumbers())
            {            
            	pw.print( e.getBoat().getSailId().toString());
            }
            else if (fByBow)
            {
				pw.print( e.getBow().toString());
				pw.print( "&nbsp;&nbsp;</td><td>");
				pw.print( e.getBoat().getSailId().toString());
            }
            else
            {
				pw.print( e.getBoat().getSailId().toString());
				pw.print( "&nbsp;&nbsp;</td><td>");
				pw.print( e.getBow().toString());
            }
            pw.print( "&nbsp;&nbsp;</td><td>");
            pw.print( e.getDivision().getName());
            pw.print( "&nbsp;&nbsp;</td><td>");
            pw.print( fOptions.getColumnEntry(1,e));
            pw.println( "</td></tr>");
        }

        pw.println("</table>");
    }

}
/**
 * $Log: ActionReportCheckin.java,v $
 * Revision 1.8  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.7  2006/04/15 23:39:23  sandyg
 * report tweaking for Miami OCR, division splits
 *
 * Revision 1.6  2006/01/19 01:50:15  sandyg
 * fixed several bugs in split fleet scoring
 *
 * Revision 1.5  2006/01/15 21:10:40  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.3  2006/01/15 03:25:51  sandyg
 * to regatta add getRace(i), getNumRaces().. reducing use of getRaces()
 *
 * Revision 1.2  2006/01/11 02:26:42  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.12.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.12  2004/05/06 02:11:50  sandyg
 * Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 *
 * Revision 1.11  2004/04/10 20:49:30  sandyg
 * Copyright year update
 *
 * Revision 1.10  2003/05/06 21:32:09  sandyg
 * added checkin report by class, by bow
 *
 * Revision 1.9  2003/03/16 20:38:21  sandyg
 * 3.9.2 release: encapsulated changes to division list in Regatta,
 * fixed a bad bug in PanelDivsion/Rating
 *
 * Revision 1.8  2003/01/04 17:33:05  sandyg
 * Prefix/suffix overhaul
 *
*/
