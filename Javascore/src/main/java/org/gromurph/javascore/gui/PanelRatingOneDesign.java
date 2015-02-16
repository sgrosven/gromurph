//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRatingOneDesign.java,v 1.5 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.util.*;

/**
 * Main dialog for editting information about a single Rating
 *
 * <P>This code was developed by Sandy Grosvenor for use with non-commercial
 *    scoring of sailboat regattas.
 *    Copyright 1997/1998.  All rights reserved.
 *
 * @version	Nov 97
 *
 * Dialog for editing a single rating.
**/
public class PanelRatingOneDesign extends BaseEditor<RatingOneDesign>
    implements ActionListener //, FocusListener
{
    RatingOneDesign fRating;

     static ResourceBundle res = JavaScoreProperties.getResources();
	JComboBox fComboOneDesignClass;

    public PanelRatingOneDesign( BaseEditorContainer parent)
    {
        super( parent);
        addFields();
     }

    @Override public void setObject( RatingOneDesign obj) throws ClassCastException
    {
        fRating = (RatingOneDesign) obj;
        super.setObject(obj);
    }

    public void addFields()
    {
        setLayout( new GridBagLayout());
        HelpManager.getInstance().registerHelpTopic(this, "rating");

        int row = 0;
        fComboOneDesignClass  = new JComboBox();
        fComboOneDesignClass.setName( "fComboOneDesignClass");
        fComboOneDesignClass.setEditable(true);
        fComboOneDesignClass.setToolTipText(res.getString("RatingOneDesignODClassToolTip"));
        fComboOneDesignClass.setModel( new DefaultComboBoxModel(
            RatingOneDesign.getKnownODClassNames().toArray()));

        HelpManager.getInstance().registerHelpTopic(fComboOneDesignClass, "rating.fComboOneDesignClass");
        gridbagAdd( fComboOneDesignClass,
            0, row, 1,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
    }

    @Override public void restore( RatingOneDesign a, RatingOneDesign b)
    {
        if (a == b) return;
        RatingOneDesign active = (RatingOneDesign) a;
        RatingOneDesign backup = (RatingOneDesign) b;

        active.setODClassName( backup.getODClassName());
        super.restore( active, backup);
    }


    @Override public void updateFields()
    {
        if (fRating != null)
        {
            fComboOneDesignClass.setSelectedItem( fRating.getODClassName());
        }
    }

    public void propertyChange(PropertyChangeEvent de)
    {
        updateFields();
    }

    @Override public void start()
    {
        fComboOneDesignClass.addActionListener(this);
//        fComboOneDesignClass.addFocusListener(this);
    }

    @Override public void stop()
    {
        fComboOneDesignClass.removeActionListener(this);
//        fComboOneDesignClass.removeFocusListener(this);
    }

    public void actionPerformed( ActionEvent event)
	{
	    if (fRating == null) return;

		String newClass = (String) fComboOneDesignClass.getSelectedItem();
		fRating.setODClassName( newClass);
		
        if (getEditorParent() != null) getEditorParent().eventOccurred( this, event);
	}

//    public void focusLost( FocusEvent event)
//    {
//        actionPerformed( null);
//    }
//
//    public void focusGained( FocusEvent event)
//    {
//        fComboOneDesignClass.requestFocusInWindow();
//    }

}
/**
 * $Log: PanelRatingOneDesign.java,v $
 * Revision 1.5  2006/05/19 05:48:43  sandyg
 * final release 5.1 modifications
 *
 * Revision 1.4  2006/01/15 21:10:40  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:20:26  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.8.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.8  2004/04/10 20:49:38  sandyg
 * Copyright year update
 *
 * Revision 1.7  2003/04/27 21:06:00  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.6  2003/03/19 02:38:24  sandyg
 * made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 *
 * Revision 1.5  2003/03/16 20:38:32  sandyg
 * 3.9.2 release: encapsulated changes to division list in Regatta,
 * fixed a bad bug in PanelDivsion/Rating
 *
 * Revision 1.4  2003/01/04 17:39:32  sandyg
 * Prefix/suffix overhaul
 *
*/
