//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelPreferences.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * the panel editing the descriptive columns in reports
 */
public class PanelPreferences extends BaseEditor<JavaScoreProperties> implements ActionListener {

	static ResourceBundle res = JavaScoreProperties.getResources();

	// JButton fButtonChooseBrowser;
	JTextFieldSelectAll fTextRsa;
	JTextFieldSelectAll fTextMna;

	JComboBox fChoiceLookAndFeel;
	JComboBox fChoiceLocale;

	JTextFieldSelectAll fTextAFactor;
	JTextFieldSelectAll fTextBFactor;

	JavaScoreProperties fPreferences;

	public PanelPreferences( BaseEditorContainer parent) {
		super( parent);
		setTitle( res.getString("PrefsTitle"));
		setPreferredSize( new java.awt.Dimension( 480, 300));

		addFields();
		updateFields();
	}

	@Override public void setObject( JavaScoreProperties obj) {
		fPreferences = obj;
		super.setObject( obj);
	}

	public void addFields() {
		HelpManager.getInstance().registerHelpTopic( this, "pref");
		setLayout( new GridBagLayout());
		setGridBagInsets( new java.awt.Insets( 5, 5, 5, 5));

		int row = 0;
		// ===== Regional Sailing Association label
		gridbagAdd( this, new JLabel( res.getString( "PrefsLabelMisc1")), 0, row, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE);
		fTextRsa = new JTextFieldSelectAll( 20);
		HelpManager.getInstance().registerHelpTopic( fTextRsa, "pref.rsa");
		gridbagAdd( this, fTextRsa, 1, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		// ===== MN Association label
		row++;
		gridbagAdd( this, new JLabel( res.getString( "PrefsLabelMisc2")), 0, row, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE);
		fTextMna = new JTextFieldSelectAll( 20);
		HelpManager.getInstance().registerHelpTopic( fTextMna, "pref.mna");
		gridbagAdd( this, fTextMna, 1, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		// ===== A Factor label
		row++;
		gridbagAdd( this, new JLabel( res.getString( "PrefsLabelAFactor")), 0, row, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE);
		fTextAFactor = new JTextFieldSelectAll( 20);
		fTextAFactor.setName("fTextAFactor");
		fTextAFactor.setToolTipText( res.getString( "PrefsLabelAFactorToolTip"));
		HelpManager.getInstance().registerHelpTopic( fTextAFactor, "pref.AFactor");
		gridbagAdd( this, fTextAFactor, 1, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);


		// ===== B Factor label
		row++;
		gridbagAdd( this, new JLabel( res.getString( "PrefsLabelBFactor")), 0, row, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE);
		fTextBFactor = new JTextFieldSelectAll( 20);
		fTextBFactor.setName("fTextBFactor");
		fTextBFactor.setToolTipText( res.getString( "PrefsLabelBFactorToolTip"));
		HelpManager.getInstance().registerHelpTopic( fTextBFactor, "pref.BFactor");
		gridbagAdd( this, fTextBFactor, 1, row, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		// add Look and Feel choices
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		String[] lafStrs = new String[lafs.length];
		for ( int i = 0; i < lafs.length; ++i) {
			lafStrs[i] = lafs[i].getName();
		}

		row++;
		JLabel lafLabel = new JLabel( res.getString( "PrefsLabelLookAndFeel"));
		lafLabel.setHorizontalAlignment( SwingConstants.RIGHT);
		gridbagAdd( this, lafLabel, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fChoiceLookAndFeel = new JComboBox( lafStrs);
		gridbagAdd( this, fChoiceLookAndFeel, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		List<Locale> availableLocals = initializeLocales();

		// ar is now sorted by name
		row++;
		JLabel localeLabel = new JLabel( res.getString( "PrefsLocale"));
		localeLabel.setHorizontalAlignment( SwingConstants.RIGHT);
		gridbagAdd( this, localeLabel, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fChoiceLocale = new JComboBox( availableLocals.toArray());
		gridbagAdd( this, fChoiceLocale, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

	}

	private List<Locale> initializeLocales() {
		// --- initialize available locales
		List<Locale> availableLocals = new ArrayList<Locale>( 5); // list of
																	// locales
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>( 5); // list
																			// of
																			// resource
																			// bundles
																			// found

		Locale def = Locale.getDefault();
		for ( int i = 0; i < sLocales.length; i++) {
			Locale loc = sLocales[i];
			ResourceBundle b = ResourceBundle.getBundle( JavaScoreProperties.BASE_BUNDLE, loc);
			if ( localeMatches( loc, def) || localeMatches( loc, Locale.ENGLISH) || (!bundles.contains( b))) {
				// have either default locale or a distinct bundle
				availableLocals.add( loc);
				bundles.add( b);
			}
		}
		java.util.Collections.sort( availableLocals, new java.util.Comparator<Locale>() {
			public int compare( Locale lLeft, Locale lRight) {
				return lLeft.getDisplayName().compareTo( lRight.getDisplayName());
			}
		});
		return availableLocals;
	}

	private static Locale[] sLocales = Locale.getAvailableLocales();

	private boolean localeMatches( Locale left, Locale right) {
		return left.getLanguage().equals( right.getLanguage()) && left.getCountry().equals( right.getCountry())
				&& left.getVariant().equals( right.getVariant());
	}

	@Override public void start() {
		fTextRsa.addActionListener( this);
		fTextMna.addActionListener( this);
		fChoiceLookAndFeel.addActionListener( this);
		fChoiceLocale.addActionListener( this);
		fTextAFactor.addActionListener( this);
		fTextBFactor.addActionListener( this);
	}

	@Override public void stop() {
		fTextRsa.removeActionListener( this);
		fTextMna.removeActionListener( this);
		fChoiceLookAndFeel.removeActionListener( this);
		fChoiceLocale.removeActionListener( this);
		fTextAFactor.removeActionListener( this);
		fTextBFactor.removeActionListener( this);
	}

	@Override public void exitOK() {
		JavaScoreProperties.save();
		JavaScore.subWindowClosing();
	}

	public void propertyChange( PropertyChangeEvent parm1) {
		// really dont expect this to happen much
		updateFields();
	}

	/**
	 * restore supports backing out of editing a regatta so it will restore the
	 * atomic items and the division list.. but not the entries, nor race
	 * objects
	 * 
	 * @param a
	 *            left hand object the one to be changed
	 * @param b
	 *            the right hand object, the supplies data to the left hand
	 *            object
	 */
	@Override public void restore( JavaScoreProperties a, JavaScoreProperties b) {
		if ( a == b) return;
		JavaScoreProperties active = (JavaScoreProperties) a;
		JavaScoreProperties backup = (JavaScoreProperties) b;

		if ( active != null) {
			active.setProperty( JavaScoreProperties.RSA_PROPERTY, backup.getProperty( JavaScoreProperties.RSA_PROPERTY));
			active.setProperty( JavaScoreProperties.MNA_PROPERTY, backup.getProperty( JavaScoreProperties.MNA_PROPERTY));
			active.setProperty( JavaScoreProperties.LOOKANDFEEL_PROPERTY, backup
					.getProperty( JavaScoreProperties.LOOKANDFEEL_PROPERTY));
			active.setProperty( JavaScoreProperties.LOCALE_PROPERTY, backup.getProperty( JavaScoreProperties.LOCALE_PROPERTY));
			super.restore( a, b);
		}
	}

	@Override public void updateFields() {
		if ( fPreferences != null) {
			fTextMna.setText( fPreferences.getProperty( JavaScoreProperties.MNA_PROPERTY));
			fTextRsa.setText( fPreferences.getProperty( JavaScoreProperties.RSA_PROPERTY));

			String aFactor = fPreferences.getProperty( JavaScoreProperties.AFACTOR_PROPERTY);
			if (aFactor == null || aFactor.equals("")) aFactor = Integer.toString( RatingPhrfTimeOnTime.AFACTOR_DEFAULT);
			fTextAFactor.setText( aFactor);
			
			String bFactor = fPreferences.getProperty( JavaScoreProperties.BFACTOR_PROPERTY);
			if (bFactor == null || bFactor.equals("")) bFactor = "";
			fTextBFactor.setText( bFactor);
			
			fChoiceLookAndFeel.setSelectedItem( fPreferences.getProperty( JavaScoreProperties.LOOKANDFEEL_PROPERTY));

			Locale loc = Util.stringToLocale( fPreferences.getProperty( JavaScoreProperties.LOCALE_PROPERTY));
			fChoiceLocale.setSelectedItem( loc);

		}
	}

	public static void main( String[] args) {
		JavaScoreProperties ro = JavaScoreProperties.getProperties();
		DialogBaseEditor dialog = new DialogBaseEditor( null);

		dialog.addWindowListener( new WindowAdapter() {
			@Override public void windowClosing( WindowEvent event) {
				System.exit( 0);
			}
		});

		dialog.setObject( ro);
		dialog.startUp();
		dialog.setVisible( true);
	}

	public void actionPerformed( ActionEvent event) {
		// if (event.getSource() == fButtonChooseBrowser)
		// fButtonChooseBrowser_actionPerformed();
		if ( event.getSource() == fTextRsa) fTextRsa_actionPerformed();
		else if ( event.getSource() == fTextMna) fTextMna_actionPerformed();
		else if ( event.getSource() == fChoiceLookAndFeel) fChoiceLookAndFeel_actionPerformed();
		else if ( event.getSource() == fChoiceLocale) fChoiceLocale_actionPerformed();
		else if ( event.getSource() == fTextAFactor) fTextAFactor_actionPerformed();
		else if ( event.getSource() == fTextBFactor) fTextBFactor_actionPerformed();

		if ( getEditorParent() != null) getEditorParent().eventOccurred( this, event);
	}

	public void fTextRsa_actionPerformed() {
		fPreferences.setProperty( JavaScoreProperties.RSA_PROPERTY, fTextRsa.getText());
	}

	public void fTextAFactor_actionPerformed() {
		fPreferences.setProperty( JavaScoreProperties.AFACTOR_PROPERTY, fTextAFactor.getText());
	}
	public void fTextBFactor_actionPerformed() {
		fPreferences.setProperty( JavaScoreProperties.BFACTOR_PROPERTY, fTextBFactor.getText());
	}
	
	public void fTextMna_actionPerformed() {
		fPreferences.setProperty( JavaScoreProperties.MNA_PROPERTY, fTextMna.getText());
	}

	public void fChoiceLookAndFeel_actionPerformed() {
		String uiname = (String) fChoiceLookAndFeel.getSelectedItem();
		if ( uiname != null) {
			String oldui = fPreferences.getProperty( JavaScoreProperties.LOOKANDFEEL_PROPERTY);
			if ( !uiname.equalsIgnoreCase( oldui)) {
				JOptionPane.showMessageDialog( this, res.getString( "PrefsMessageLookAndFeelChange"), res
						.getString( "PrefsTitleLookAndFeelChange"), JOptionPane.INFORMATION_MESSAGE);

				fPreferences.setProperty( JavaScoreProperties.LOOKANDFEEL_PROPERTY, uiname);
				SwingUtilities.updateComponentTreeUI( this.getParent() == null ? this : this.getParent());
			}
		}
	}

	public void fChoiceLocale_actionPerformed() {
		String lname = fChoiceLocale.getSelectedItem().toString();
		if ( lname != null) {
			String oldl = Locale.getDefault().toString();
			if ( !lname.equalsIgnoreCase( oldl)) {
				JOptionPane.showMessageDialog( this, res.getString( "PrefsMessageLocaleChange"), res
						.getString( "PrefsTitleLocaleChange"), JOptionPane.INFORMATION_MESSAGE);
			}
			fPreferences.setProperty( JavaScoreProperties.LOCALE_PROPERTY, lname);
		}
	}
}
/**
 * $Log: PanelPreferences.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.13.4.3 2005/11/30 02:51:25 sandyg added auto focuslost to
 * JTextFieldSelectAll. Removed focus lost checks on text fields in panels.
 * 
 * Revision 1.13.4.2 2005/11/19 20:34:55 sandyg last of java 5 conversion,
 * created swingworker, removed threads packages.
 * 
 * Revision 1.13.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.13 2005/05/26 01:45:43 sandyg fixing resource access/lookup
 * problems
 * 
 * Revision 1.12 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.11 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.10 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.9 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.8 2003/03/30 00:05:49 sandyg moved to eclipse 2.1
 * 
 * Revision 1.7 2003/03/19 02:38:23 sandyg made start() stop() abstract to
 * BaseEditor, the isStarted check now done in BaseEditor.startUp and
 * BaseEditor.shutDown().
 * 
 * Revision 1.6 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
