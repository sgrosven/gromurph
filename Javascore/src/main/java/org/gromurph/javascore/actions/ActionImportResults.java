//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionImportResults.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.*;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.gui.DialogImportResults;


public class ActionImportResults extends AbstractAction
{
    static ResourceBundle res = JavaScoreProperties.getResources();
    static ResourceBundle resUtil = org.gromurph.util.Util.getResources();

    public ActionImportResults()
    {
        super( res.getString("MenuImportResults"));
        putValue( Action.MNEMONIC_KEY,
            new Integer( res.getString( "MenuImportResultsMnemonic").charAt(0)));
    }

    public void actionPerformed(ActionEvent parm1)
    {
        if (JavaScoreProperties.getRegatta().getNumEntries() == 0)
        {
            JOptionPane.showMessageDialog(
                null,
                res.getString( "ImportResultsMessageNoEntries"),
                res.getString( "ImportResultsTitleNoEntries"),
                JOptionPane.WARNING_MESSAGE);
                return;
        }

        DialogImportResults dialog = new DialogImportResults( JavaScore.getInstance());
        dialog.setVisible(true);
    }
}
/**
 * $Log: ActionImportResults.java,v $
 * Revision 1.5  2006/05/19 05:48:42  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.4  2006/01/15 21:10:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:26:42  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.8  2004/04/10 20:49:29  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/01/04 17:33:05  sandyg
 * Prefix/suffix overhaul
 *
*/
