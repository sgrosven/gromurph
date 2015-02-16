//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogImportMarkRoundings.java,v 1.4 2006/01/15 21:10:40 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.FlowLayout;
import java.text.MessageFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.SailId;

public class DialogImportMarkRoundings extends DialogImportTable
{
    JComboBox fComboRace;

    @Override public String getDirections()
    {
        return res.getString("ImportRoundingsMessageDirections");
    }

    private int MAXMARKS = 9;

    public DialogImportMarkRoundings( JFrame parent)
    {
        super( parent);
        setTitle( res.getString( "ImportTitlePasteFromClipboard"));

        JPanel subPanel = getSubPanel();
        subPanel.setLayout( new FlowLayout());
        subPanel.add( new JLabel( res.getString("ReportLabelRace")));
        fComboRace = new JComboBox();
        subPanel.add( fComboRace);
    } 
     
    @Override public void setVisible( boolean v) {
    	if (v) {
    		fComboRace.setModel( new DefaultComboBoxModel( getRegatta().getRaces().toArray()));
            fComboRace.setSelectedIndex(0);
    	}
    }

    @Override public void initFieldNames()
    {
        fFieldNames = new String[2 + MAXMARKS];
        int x = 0;
        fFieldNames[x++] = res.getString( "GenBow");
        fFieldNames[x++] = res.getString( "GenSail");
        String mark = res.getString( "GenMark") + " ";
        for (int i = 1; i <= MAXMARKS; i++)
        {
            fFieldNames[x++] = mark + i;
        }
    }

    int   fLastRow = -1;
    Entry fEntry = null;

    @Override public void convertTableToRegatta( )
    {
        fEntry = null;
        Race r = (Race) fComboRace.getSelectedItem();
        r.deleteRoundings();

        super.convertTableToRegatta();
    }

    @Override public void setValue( int row, int fieldIndex, String cell)
    {
        if (getRegatta() == null) return;

        if (fLastRow != row)
        {
            fLastRow = row;
            fEntry = null;
        }

        switch ( fieldIndex)
        {
            case 0:
            {
                EntryList elist = getRegatta().getAllEntries().findBow( cell);
                fEntry = null;
                if (elist.size() == 0)
                {
                    addWarning(
                        MessageFormat.format(
                            res.getString( "ImportMessageNoEntryBow"),
                            new Object[] { cell, new Integer( row)}));
                }
                else if (elist.size() > 1)
                {
                    addWarning(
                        MessageFormat.format(
                            res.getString( "ImportMessageAmbiguousBow"),
                            new Object[] { new Integer( elist.size()), cell, new Integer( row)}));
                }
                else
                {
                    fEntry = elist.get(0);
                }
                break;
            }
            case 1:
            {
                EntryList elist = getRegatta().getAllEntries().findSail( new SailId( cell));
                fEntry = null;
                if (elist.size() == 0)
                {
                    addWarning(
                       MessageFormat.format(
                            res.getString( "ImportMessageNoEntrySail"),
                            new Object[] { cell, new Integer( row)} ));
                }
                else if (elist.size() > 1)
                {
                    addWarning(
                        MessageFormat.format(
                            res.getString( "ImportMessageAmbiguousSail"),
                            new Object[] { new Integer( elist.size()), cell, new Integer( row)}));
                }
                else
                {
                    fEntry = elist.get(0);
                }
                break;
            }
            default:
            {
                if (fEntry != null) parseRounding( fEntry, row, fieldIndex, cell);
                break;
            }
        } // of switch
    }

    private void parseRounding( Entry e, int row, int fieldIndex, String cell)
    {
        Race race = (Race) fComboRace.getSelectedItem();
        String markName = "M" + Integer.toString(fieldIndex - 2);

        // if no race found, create a new race
        if (race == null) return;
        if (cell.trim().length() == 0) return;

        try
        {
            Finish f = new Finish( race, e);
            FinishPosition p = new FinishPosition( Integer.parseInt( cell));
            f.setFinishPosition( p);
            race.getRoundings( markName).add( f);
        }
        catch (Exception exc)
        {
             addWarning(
                MessageFormat.format(
                    res.getString( "ImportRoundingsMessageInvalidRounding"),
                    new Object[] { cell, new Integer( row), exc.toString()} ));
        }

    }

}
/**
 * $Log: DialogImportMarkRoundings.java,v $
 * Revision 1.4  2006/01/15 21:10:40  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:20:26  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.3.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.3  2004/04/10 20:49:30  sandyg
 * Copyright year update
 *
 * Revision 1.2  2003/01/04 17:39:32  sandyg
 * Prefix/suffix overhaul
 *
*/
