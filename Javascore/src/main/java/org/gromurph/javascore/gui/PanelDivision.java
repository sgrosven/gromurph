//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelDivision.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.manager.RatingManager;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.Rating;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * Main panel for editting information about a single Division
 **/

public class PanelDivision extends BaseEditor<Division> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	private Division fDivision;
	private boolean fIsMasterDivision = false;

	public PanelDivision( BaseEditorContainer parent) {
		super( parent);
		setPreferredSize( new Dimension( 350, 300));
		addFields();
		setName( "PanelDivision");
	}

	JComboBox fChoiceSystem;
	JTextFieldSelectAll fTextName;
	JComboBox fChoiceGender;
	JTextFieldSelectAll fTextIfClassId;

	PanelRating fPanelMinRating;
	PanelRating fPanelMaxRating;
	JLabel fLabelMin;
	JLabel fLabelMax;

	private String CARD_BLANK = "Blank";
	private String CARD_DOUBLE = PanelRatingDouble.class.getName();
	private String CARD_ONEDESIGN = PanelRatingOneDesign.class.getName();
	
	public void addFields() {
		setLayout( new GridBagLayout());
		HelpManager.getInstance().registerHelpTopic( this, "divisions");

		int row = 0;
		gridbagAdd( new JLabel( res.getString( "DivisionLabelClassName")), 0, row, 1, GridBagConstraints.EAST,
				GridBagConstraints.NONE);

		fTextName = new JTextFieldSelectAll( 12);
		fTextName.setName( "fTextName");
		fTextName.setToolTipText( res.getString( "DivisionLabelClassNameToolTip"));
		HelpManager.getInstance().registerHelpTopic( fTextName, "divisions.fTextName");
		gridbagAdd( fTextName, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		gridbagAdd( new JLabel( res.getString( "DivisionLabelSystem")), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fChoiceSystem = new JComboBox();
		fChoiceSystem.setName( "fChoiceSystem");
		fChoiceSystem.setToolTipText( res.getString( "DivisionLabelSystemToolTip"));
		Object[] l = RatingManager.getSupportedSystems();
		fChoiceSystem.setModel( new DefaultComboBoxModel( l));
		HelpManager.getInstance().registerHelpTopic( fChoiceSystem, "divisions.fChoiceSystem");
		gridbagAdd( fChoiceSystem, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		java.awt.Insets i0 = new java.awt.Insets( 0, 0, 0, 0);

		fLabelMin = new JLabel();
		gridbagAdd( this, fLabelMin, 0, row, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, i0);

		fPanelMinRating = new PanelRating( this.getEditorParent());
		fPanelMinRating.setName( "fPanelMinRating");
		gridbagAdd( this, fPanelMinRating, 1, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, i0);
		
		row++;
		fLabelMax = new JLabel();
		gridbagAdd( this, fLabelMax, 0, row, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, i0);

		fPanelMaxRating = new PanelRating( this.getEditorParent());
		fPanelMaxRating.setName( "fPanelMaxRating");
		gridbagAdd( this, fPanelMaxRating, 1, row, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, i0);

		row++;
		gridbagAdd( new JLabel( res.getString( "IfClassIdLabel")), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fTextIfClassId = new JTextFieldSelectAll( 12);
		fTextIfClassId.setName( "fTextIfClassId");
		fTextIfClassId.setToolTipText( res.getString( "IfClassIdLabelTooltip"));
		// HelpManager.getInstance().registerHelpTopic(fTextName,
		// "divisions.fTextName");
		gridbagAdd( fTextIfClassId, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		gridbagAdd( new JLabel( res.getString( "GenderLabel")), 0, row, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);

		fChoiceGender = new JComboBox();
		fChoiceGender.setName( "fChoiceGender");
		fChoiceGender.setToolTipText( res.getString( "fChoiceGenderTooltip"));
		Object[] genders = new String[] { res.getString( "GenderOpen"), res.getString( "GenderMen"), res.getString( "GenderWomen"), res.getString( "GenderMixed") };
		fChoiceGender.setModel( new DefaultComboBoxModel( genders));
		// HelpManager.getInstance().registerHelpTopic(fTextName,
		// "divisions.fTextName");
		gridbagAdd( fChoiceGender, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	}

	@Override public void start() {
		fTextName.addActionListener( this);
		fChoiceSystem.addActionListener( this);

		fTextIfClassId.addActionListener( this);
		fChoiceGender.addActionListener( this);

		fPanelMinRating.startUp();
		fPanelMaxRating.startUp();
	}

	@Override public void stop() {
		fTextName.removeActionListener( this);
		fChoiceSystem.removeActionListener( this);
		fTextIfClassId.removeActionListener( this);
		fChoiceGender.removeActionListener( this);
		fPanelMinRating.shutDown();
		fPanelMaxRating.shutDown();
	}

	@Override public void exitOK() {
		super.exitOK();

		// if called from master divisions, and division is in current
		// regatta, see if changes should be integrated into current regatta
		if ( fIsMasterDivision) {
			if ( fDivisionRegatta != null && !fDivisionRegatta.equals( fDivision)) {
				JTextPane message = new JTextPane();
				message.setBackground( this.getBackground());
				message.setText( MessageFormat.format( res.getString( "DivisionChangeMasterPassThrough"), new Object[] { fDivision
						.getName() }));

				// was equal, now is not - prompt to carry change into the
				// regatta
				int yesno = JOptionPane.showConfirmDialog( this, message, "", JOptionPane.YES_NO_OPTION);

				if ( yesno == JOptionPane.YES_OPTION) {
					fDivisionRegatta.setName( fDivision.getName());
					fDivisionRegatta.setMinRating( (Rating) fDivision.getMinRating().clone());
					fDivisionRegatta.setMaxRating( (Rating) fDivision.getMaxRating().clone());
					processDivisionChanges( fDivisionRegatta);
				}
			}
		} else {
			processDivisionChanges( fDivision);
		}

		setIsMasterDivision( false); // turn it off no matter what
		fDivisionRegatta = null;
	}

	/**
	 * called when parent dialog closes after a CANCEL
	 */
	@Override public void exitCancel() {
		setIsMasterDivision( false); // turn it off no matter what
		super.exitCancel();
	}

	Division fDivisionRegatta = null;

	@Override public void setObject( Division obj) throws ClassCastException {
		if ( fDivision != null) fDivision.removePropertyChangeListener( this);
		fDivision = (Division) obj;

		// if we're here via the Master Division dialog,
		// we want to see if the same division is in the open regatta
		fDivisionRegatta = null;
		if ( fIsMasterDivision && JavaScoreProperties.getRegatta() != null) {
			fDivisionRegatta = JavaScoreProperties.getRegatta().getDivision( fDivision.getName());
			if ( fDivisionRegatta != null && !fDivisionRegatta.equals( fDivision)) {
				// if regdivision exists and is equals() to current division
				// save it for later, if not, we get here and change it to null
				fDivisionRegatta = null;
			}
		}

		super.setObject( obj);
		if ( fDivision != null) fDivision.addPropertyChangeListener( this);

	}

	public void setIsMasterDivision( boolean tf) {
		fIsMasterDivision = tf;
	}

	public void propertyChange( PropertyChangeEvent event) {
		if ( getEditorParent() != null) getEditorParent().eventOccurred( this, event);
	}

	public void processDivisionChanges( Division div) {
		if ( fObjectBackup.equals( div)) return; // no changes

		Regatta reg = JavaScoreProperties.getRegatta();
		if ( reg == null) return; // no regatta, go away

		EntryList divEntries = div.getEntries();
		EntryList entriesOut = div.getInvalidEntries( divEntries);

		EntryList noneEntries = AbstractDivision.NONE.getEntries();
		EntryList entriesIn = div.getValidEntries( noneEntries);

		if ( entriesOut.size() == 0 && entriesIn.size() == 0) return;

		JPanel panel = new JPanel( new BorderLayout());
		JTextPane topLabel = new JTextPane();
		topLabel.setBackground( this.getBackground());
		JTextPane boatList = new JTextPane();
		JTextPane bottomLabel = new JTextPane();
		bottomLabel.setBackground( this.getBackground());

		panel.add( topLabel, BorderLayout.NORTH);
		panel.add( new JScrollPane( boatList), BorderLayout.CENTER);
		panel.add( bottomLabel, BorderLayout.SOUTH);
		panel.setSize( 300, 350);

		if ( entriesOut.size() > 0) {
			topLabel.setText( MessageFormat.format( res.getString( "DivisionChangeEntriesOutTop"), new Object[] { div.getName() }));

			bottomLabel.setText( MessageFormat.format( res.getString( "DivisionChangeEntriesOutBottom"), new Object[] { div
					.getName() }));

			boatList.setText( makeEntryList( entriesOut));

			JOptionPane.showMessageDialog( this, panel);

			for ( Iterator it = entriesOut.iterator(); it.hasNext();) {
				try {
					((Entry) it.next()).setDivision( AbstractDivision.NONE);
				} catch (RatingOutOfBoundsException e) {
				}
			}
		}

		if ( entriesIn.size() > 0) {
			topLabel.setText( MessageFormat.format( res.getString( "DivisionChangeEntriesInTop"), new Object[] { div.getName() }));

			bottomLabel.setText( MessageFormat.format( res.getString( "DivisionChangeEntriesInBottom"), new Object[] { div
					.getName() }));

			boatList.setText( makeEntryList( entriesIn));

			int yesno = JOptionPane.showConfirmDialog( this, panel, "", JOptionPane.YES_NO_OPTION);

			if ( yesno == JOptionPane.YES_OPTION) {
				for ( Iterator it = entriesIn.iterator(); it.hasNext();) {
					try {
						((Entry) it.next()).setDivision( div);
					} catch (RatingOutOfBoundsException e) {
					}
				}
			}
		}
	}

	private List<Integer> eLabels;

	private String makeEntryList( EntryList elist) {
		if ( eLabels == null) {
			eLabels = new ArrayList<Integer>();
			eLabels.add( new Integer( Entry.SHOW_BOAT));
			eLabels.add( new Integer( Entry.SHOW_FULLRATING));
		}

		StringBuffer sb = new StringBuffer( 256);
		for ( Iterator it = elist.iterator(); it.hasNext();) {
			Entry e = (Entry) ((Entry) it.next()).clone();
			try {
				e.setDivision( fDivision);
			} catch (RatingOutOfBoundsException ex) {}
			String ename = e.toString( eLabels, false, null);
			sb.append( ename);
			sb.append( "\n");
		}
		return sb.toString();
	}

	@Override public void restore( Division a, Division b) {
		if ( a == b) return;
		Division active = (Division) a;
		Division backup = (Division) b;

		active.setName( backup.getName());
		active.setMinRating( (Rating) backup.getMinRating().clone());
		active.setMaxRating( (Rating) backup.getMaxRating().clone());
		super.restore( active, backup);
	}

	@Override public void updateFields() {
		if ( fDivision == null) {
			fTextName.setText( "");
			fChoiceSystem.setEnabled( false);
			fLabelMax.setText( " ");
			fLabelMin.setText( " ");

			fTextIfClassId.setText( "");
			fChoiceGender.setSelectedItem( null);

			// old division is null
			fPanelMinRating.setObject(null);
			fPanelMaxRating.setObject(null);
		} else {
			String n = fDivision.getName();
			fTextName.setText( n);
			fTextIfClassId.setText( fDivision.getIfClassId());
			if ( fDivision.getGender() != null && fDivision.getGender().length() > 0) {
				fChoiceGender.setSelectedItem( fDivision.getGender());
			} else {
				fChoiceGender.setSelectedItem( res.getString( "GenderOpen"));
			}

			String s = fDivision.getSystem();
			String longname = RatingManager.getLongNameFromSystem( s);
			if ( !longname.equals( fChoiceSystem.getSelectedItem())) fChoiceSystem.setSelectedItem( longname);
			fChoiceSystem.setEnabled( true);
			
    		if ( fDivision.isOneDesign()) {
    			fLabelMin.setText( res.getString( "DivisionLabelODClassName"));
    			fPanelMinRating.setObject( fDivision.getMinRating());
    			
    			fLabelMax.setText( " ");	
    			fPanelMaxRating.setObject( null);
    		} else {
    			fLabelMin.setText( res.getString( "DivisionLabelMinRating"));
    			fPanelMinRating.setObject( fDivision.getMinRating());
    			
    			String x =  res.getString( "DivisionLabelMaxRating");
    			fLabelMax.setText( res.getString( "DivisionLabelMaxRating"));
    			fPanelMaxRating.setObject( fDivision.getMaxRating());
    			
    			this.revalidate();
    		}
		
		}
	}

	public Division getDivision() {
		return fDivision;
	}

	public void actionPerformed( ActionEvent event) {
		Object object = event.getSource();

		if ( object == fTextName) fTextName_actionPerformed();
		else if ( object == fChoiceSystem) fChoiceSystem_actionPerformed();
		else if ( object == fTextIfClassId) fTextIfClassId_actionPerformed();
		else if ( object == fChoiceGender) fChoiceGender_actionPerformed();

		if ( getEditorParent() != null) getEditorParent().eventOccurred( this, event);
	}

	void fChoiceSystem_actionPerformed() {
		if ( fDivision == null) return;

		String longname = (String) fChoiceSystem.getSelectedItem();
		String sys = RatingManager.getSystemFromLongName( longname);
		try {
			fDivision.setSystem( sys);
			updateFields();
		} catch (Exception e) {
			Util.showError( e, true);
		}
	}

	void fTextName_actionPerformed() {
		if ( fDivision == null) return;
		fDivision.setName( fTextName.getText());
	}

	void fChoiceGender_actionPerformed() {
		if ( fDivision == null) return;

		String gender = (String) fChoiceGender.getSelectedItem();
		fDivision.setGender( gender);
	}

	void fTextIfClassId_actionPerformed() {
		if ( fDivision == null) return;
		fDivision.setIfClassId( fTextIfClassId.getText());
	}

}
/**
 * $Log: PanelDivision.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.12.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to
 * JTextFieldSelectAll. Removed focus lost checks on text fields in panels.
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.12 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.11 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.10 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.9 2003/03/27 02:47:00 sandyg Completes fixing [ 584501 ] Can't
 * change division splits in open reg
 * 
 * Revision 1.8 2003/03/19 03:32:26 sandyg cancel in PanelDivision now correctly
 * reverts the division to original
 * 
 * Revision 1.7 2003/03/19 02:40:32 sandyg start() stop() mods, also getting
 * events and ratings to fire correctly
 * 
 * Revision 1.6 2003/03/16 20:38:31 sandyg 3.9.2 release: encapsulated changes
 * to division list in Regatta, fixed a bad bug in PanelDivsion/Rating
 * 
 * Revision 1.5 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
