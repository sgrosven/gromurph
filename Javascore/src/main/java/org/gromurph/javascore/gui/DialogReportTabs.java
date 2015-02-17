//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DialogReportTabs.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.gromurph.javascore.JavaScore;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.util.DialogBaseEditor;
import org.gromurph.util.HelpManager;
import org.gromurph.util.Util;

/**
 * the panel editing the descriptive columns in reports
 */
public class DialogReportTabs extends DialogBaseEditor {
	static ResourceBundle res = JavaScoreProperties.getResources();
	Vector fOptionList;
	// JButton fButtonExit;
	PanelReportOptions fPanelOptions;
	JComboBox fComboReport;
	ReportOptions fOptions;

	private static final String EMPTY = "";
	private static final String REPORT_ACTION = "ReportAction";

	public DialogReportTabs( JFrame parent, boolean modal) {
		super( parent, modal);
		setTitle( res.getString( "ReportOptionsTabsTitle"));
		HelpManager.getInstance().registerHelpTopic( this, "report");

		JPanel mainPanel = new JPanel( new BorderLayout());
		getContentPane().add( mainPanel, BorderLayout.CENTER);

		// report options go in the middle
		fPanelOptions = new PanelReportOptions( this);
		fPanelOptions.setBorder( new TitledBorder( new EtchedBorder(), EMPTY, TitledBorder.CENTER, TitledBorder.TOP,
				this.getFont(), Color.black));
		mainPanel.add( fPanelOptions, BorderLayout.CENTER);

		// north gets the combo box
		JPanel north = new JPanel( new FlowLayout());
		mainPanel.add( north, BorderLayout.NORTH);
		fComboReport = new JComboBox();
		fComboReport.setActionCommand( REPORT_ACTION);
		north.add( new JLabel( res.getString( "ReportOptionsTitleTabsSelectReport"))); // ,
																						// BorderLayout.WEST);
		north.add( fComboReport); // , BorderLayout.CENTER);

	}

	@Override public void start() {
		Regatta reg = JavaScoreProperties.getRegatta();
		List<ReportOptions> v = reg.getReportOptionsList();
		if ( fOptionList != v && v != null) {
			fComboReport.setModel( new DefaultComboBoxModel( v.toArray()));
			fComboReport.setSelectedIndex( 0);
		}
		
		try {
			if ( fComboReport.getSelectedItem() == null) fComboReport.setSelectedIndex( 0);
		} catch (Exception e) { } // just trying, no big deal if it doesnt work
		updateReportPanel( (ReportOptions) fComboReport.getSelectedItem());
		fComboReport.addActionListener( this);
		// fButtonExit.addActionListener( this);
		fPanelOptions.startUp();
	}

	@Override public void stop() {
		fComboReport.removeActionListener( this);
		// fButtonExit.removeActionListener( this);
		fPanelOptions.shutDown();

		JavaScore.backgroundSave();
	}

	@Override protected void closeWindow() {
		super.closeWindow();
		JavaScore.subWindowClosing();
	}

	/**
	 * Called whenever a tab is clicked. put the right options in the panel
	 */
	@Override
	public void actionPerformed( ActionEvent event) {
		if ( event.getActionCommand().equals( REPORT_ACTION)) {
			updateReportPanel( (ReportOptions) fComboReport.getSelectedItem());
		} else {
			super.actionPerformed( event);
		}
	}

	public void updateReportPanel( ReportOptions ro) {
		if ( ro != fOptions) {
			fOptions = ro;
			StringBuffer sb = new StringBuffer( 30);
			sb.append( fOptions.toString());
			sb.append( " ");
			sb.append( res.getString( "ReportOptionsTitle"));
			((TitledBorder) fPanelOptions.getBorder()).setTitle( sb.toString());
			fPanelOptions.setObject( fOptions);
		}
	}

	public static void main( String[] args) {
		try {

			// Regatta reg = RegattaManager.readFromDisk(
			// Util.getWorkingDirectory(),
			// "1000_Star_Wars.regatta");
			Regatta reg = null;
			try {
				reg = RegattaManager.readTestRegatta("MedalTest_JustAfterSplit.regatta");
			} catch (IOException e) {
				e.printStackTrace();
			}

			DialogReportTabs dialog = new DialogReportTabs( null, true);
			dialog.setVisible( true);
			JavaScore.hideSplash();

		} catch (Exception e) {
			Util.printlnException( "DialogReportTabs", e, true);
			System.exit( -1);
		}
	}

	/*
	 * public void setVisible( boolean vis) { if (vis) { if (!isVisible())
	 * start(); } else { if (isVisible()) stop(); } super.setVisible(vis);
	 * JavaScore.backgroundSave(); JavaScore.subWindowClosing(); }
	 */

}
/**
 * $Log: DialogReportTabs.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.10 2005/05/26 01:45:43 sandyg fixing resource access/lookup
 * problems
 * 
 * Revision 1.9 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.8 2004/04/10 20:49:30 sandyg Copyright year update
 * 
 * Revision 1.7 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.6 2003/03/19 02:38:23 sandyg made start() stop() abstract to
 * BaseEditor, the isStarted check now done in BaseEditor.startUp and
 * BaseEditor.shutDown().
 * 
 * Revision 1.5 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
