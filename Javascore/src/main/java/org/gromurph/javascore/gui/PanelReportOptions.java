// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelReportOptions.java,v 1.6 2006/05/19 05:48:43 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.actions.ActionReportSeriesStandingsSingleStage;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Person;
import org.gromurph.util.Util;
import org.gromurph.util.swingworker.SwingWorker;

/**
 * the panel editing the descriptive columns in reports
 */
public class PanelReportOptions extends BaseEditor<ReportOptions> implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	JTable fTableSample;
	DefaultTableModel fModelSample;
	String[] fColumnHeads;

	java.util.List<JComboBox> fComboLocations;
	java.util.List<JRadioButton> fRadioNames;
	ButtonGroup fGroupNames;

	JComboBox fComboFontNames;
	JComboBox fComboFontSizes;
	JCheckBox fCheckIncludeOneDesignTimes;
	JCheckBox fCheckShowLastXRaces;
	JCheckBox fCheckHidePenaltyPoints;
	JCheckBox fCheckCombineRaceAndSeries;
	JTextFieldSelectAll fTextLastXRaces;
	JPanel fPanelLastXRaces;

	JButton fButtonChooseTemplate;
	JTextField fTextTemplate;

	Entry fEntry;
	ReportOptions fOptions;

	private static final String LOCATION_ACTION = "LocationAction";
	private static final String NAME_ACTION = "NameAction";

	private static final String[] sFontSizes = new String[] { res.getString("ReportOptionsFontSizeNormal"), "1 ( 8pt)",
			"2 (10pt)", "3 (12pt)", "4 (14pt)", "5 (16pt)", "6 (24pt)", "7 (36pt)" };

	public PanelReportOptions( BaseEditorContainer parent) {
		super(parent);
		setPreferredSize(new java.awt.Dimension(520, 480));
		fColumnHeads = new String[] { "0", "1", "2", "3", "4" };
		fModelSample = new DefaultTableModel(fColumnHeads, 1);

		fEntry = new Entry();
		fEntry.setBoat(new Boat(res.getString("GenBoat"), "1234", new Person(res
				.getString("ReportOptionsSampleSkipFirst"), res.getString("ReportOptionsSampleSkipLast"))));
		fEntry.setBow("10");
		fEntry.setClub(res.getString("GenClub"));
		fEntry.setCrew(new Person(res.getString("ReportOptionsSampleCrewFirst"), res
				.getString("ReportOptionsSampleCrewLast")));
		try {
			fEntry.setDivision(new Division(res.getString("ReportOptionsSampleDiv")));
		}
		catch (RatingOutOfBoundsException e) {}
		fEntry.setMnaNumber(JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY));
		fEntry.setRsaNumber(JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY));
		JavaScoreProperties.getPropertyValue(JavaScoreProperties.MNA_PROPERTY);
		JavaScoreProperties.getPropertyValue(JavaScoreProperties.RSA_PROPERTY);
		fEntry.setSkipper(new Person(res.getString("ReportOptionsSampleSkipFirst"), res
				.getString("ReportOptionsSampleSkipLast")));

		addFields();
		updateFields();

		swFileChooser = new SwingWorker() {
			@Override
			public Object construct() {
				if (sChooserFrame == null) sChooserFrame = new Frame();
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new HtmlFileFilter());
				chooser.setCurrentDirectory(new File(Util.getWorkingDirectory()));
				return chooser;
			}
		};
		swFileChooser.start();
	}

	private static SwingWorker swFileChooser;

	@Override
	public void setObject(ReportOptions obj) {
		fOptions = (ReportOptions) obj;
		super.setObject(obj);

		java.util.List<String> opList = fOptions.getOptionLocationValues();
		for (int c = 0; c < opList.size(); c++) {
			JComboBox box = fComboLocations.get(c);
			if (opList.get(c).toLowerCase().equals("none")) box.setSelectedIndex(0);
			// set to none - whatever the language
			else box.setSelectedItem(opList.get(c));
		}

	}

	private JPanel fPanelColumns;

	public void addFields() {
		setLayout(new GridBagLayout());
		setGridBagInsets(new java.awt.Insets(2, 2, 2, 2));
		HelpManager.getInstance().registerHelpTopic(this, "report");

		// ===== Report Columns panel

		fPanelColumns = new JPanel(new GridLayout(0, 2, 5, 0));
		fPanelColumns.setBorder(BorderFactory.createTitledBorder(res.getString("ReportOptionsTitleIncludedColumns")));
		gridbagAdd(this, fPanelColumns, 0, 0, 1, 3, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new java.awt.Insets(2, 2, 2, 2));

		fComboLocations = new ArrayList<JComboBox>();

		int nOptions = ReportOptions.getNumberOptions();
		for (int r = 0; r < nOptions; r++) {
			String opt = ReportOptions.getOptionStrings().get(r);
			JLabel c = new JLabel(opt);
			fPanelColumns.add(c);

			setComboLocationValue(r, ReportOptions.getOptionLocations().get(0));
		}

		// ===== Name Format Panel

		JPanel panelNames = new JPanel(new GridLayout(1, 0, 0, 0));
		panelNames.setBorder(BorderFactory.createTitledBorder(res.getString("ReportOptionsTitleNameFormat")));
		gridbagAdd(this, panelNames, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		fRadioNames = new ArrayList<JRadioButton>();
		fGroupNames = new ButtonGroup();
		String[] nameFormats = ReportOptions.getNameFormats();
		for (int r = 0; r < nameFormats.length; r++) {
			JRadioButton c = new JRadioButton(nameFormats[r]);
			fRadioNames.add(c);
			panelNames.add(c);
			fGroupNames.add(c);
			c.setActionCommand(NAME_ACTION);
			HelpManager.getInstance().registerHelpTopic(c, "report.panelNames");
		}

		// ===== Font selection

		JPanel panelFonts = new JPanel(new GridBagLayout());
		panelFonts.setBorder(BorderFactory.createTitledBorder(res.getString("ReportOptionsTitleBodyFont")));
		gridbagAdd(this, panelFonts, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String envfonts[] = gEnv.getAvailableFontFamilyNames();

		fComboFontNames = new JComboBox(new DefaultComboBoxModel(envfonts));
		java.awt.Dimension d = fComboFontNames.getPreferredSize();
		d.width = Math.min(160, d.width);
		fComboFontNames.setPreferredSize(d);
		HelpManager.getInstance().registerHelpTopic(fComboFontNames, "report.fComboFontNames");

		fComboFontSizes = new JComboBox(new DefaultComboBoxModel(sFontSizes));
		HelpManager.getInstance().registerHelpTopic(fComboFontSizes, "report.fComboFontSizes");

		gridbagAdd(panelFonts, new JLabel(res.getString("ReportOptionsLabelFont")), 0, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE);
		gridbagAdd(panelFonts, fComboFontNames, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridbagAdd(panelFonts, new JLabel(res.getString("ReportOptionsLabelSize")), 0, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.NONE);
		gridbagAdd(panelFonts, fComboFontSizes, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		// ===== check box for one design time information
		JPanel panelChecks = new JPanel(new GridLayout(4, 1));

		gridbagAdd(this, panelChecks, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		fCheckIncludeOneDesignTimes = new JCheckBox(res.getString("ReportOptionsLabelIncludeOneDesignTimes"));
		panelChecks.add(fCheckIncludeOneDesignTimes);

		fPanelLastXRaces = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		fCheckShowLastXRaces = new JCheckBox(res.getString("ReportOptionsLabelLastXRaces1"));
		fTextLastXRaces = new JTextFieldSelectAll(2);
		JLabel label = new JLabel(" " + res.getString("ReportOptionsLabelLastXRaces2"));
		label.setForeground(fCheckShowLastXRaces.getForeground());
		fPanelLastXRaces.add(fCheckShowLastXRaces);
		fPanelLastXRaces.add(fTextLastXRaces);
		fPanelLastXRaces.add(label);
		panelChecks.add(fPanelLastXRaces);

		fCheckHidePenaltyPoints = new JCheckBox(res.getString("ReportOptionsLabelNoPenaltyPoints"));
		panelChecks.add(fCheckHidePenaltyPoints);

		fCheckCombineRaceAndSeries = new JCheckBox(res.getString("ReportOptionsLabelCombineRaceAndSeries"));
		fCheckCombineRaceAndSeries.setToolTipText(res.getString("ReportOptionsTooltipCombineRaceAndSeries"));
		panelChecks.add(fCheckCombineRaceAndSeries);

		// ===== Sample table name

		fTableSample = new JTable(fModelSample);
		fTableSample.setPreferredScrollableViewportSize(new java.awt.Dimension(400, 20));
		JScrollPane panelSample = new JScrollPane(fTableSample);
		gridbagAdd(this, panelSample, 0, 3, 2, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		// ===== Template File name

		JPanel panelTemplate = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
		panelTemplate.setBorder(BorderFactory.createTitledBorder(res.getString("ReportOptionsTemplateTitle")));
		gridbagAdd(this, panelTemplate, 0, 4, 2, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		panelTemplate.add(new JLabel(res.getString("ReportOptionsLabelFilename")));

		fTextTemplate = new JTextFieldSelectAll(30);
		HelpManager.getInstance().registerHelpTopic(fTextTemplate, "report.fTextTemplate");
		panelTemplate.add(fTextTemplate);

		fButtonChooseTemplate = new JButton(Util.getImageIcon(this, Util.OPEN_ICON));
		// fButtonChooseTemplate.setPreferredSize( new java.awt.Dimension( 18,
		// 18));
		fButtonChooseTemplate.setToolTipText(res.getString("ReportOptionsLabelChooseTemplateToolTip"));
		panelTemplate.add(fButtonChooseTemplate);

	}

	@Override
	public void start() {
		updateFields();
		for (int r = 0; r < fComboLocations.size(); r++) {
			fComboLocations.get(r).addActionListener(this);
		}
		for (int r = 0; r < fRadioNames.size(); r++) {
			fRadioNames.get(r).addActionListener(this);
		}
		fComboFontNames.addActionListener(this);
		fComboFontSizes.addActionListener(this);
		fCheckIncludeOneDesignTimes.addActionListener(this);
		fCheckCombineRaceAndSeries.addActionListener(this);

		fCheckHidePenaltyPoints.addActionListener(this);
		fCheckShowLastXRaces.addActionListener(this);
		fTextLastXRaces.addActionListener(this);

		fTextTemplate.addActionListener(this);
		fButtonChooseTemplate.addActionListener(this);
	}

	@Override
	public void stop() {
		for (int r = 0; r < fComboLocations.size(); r++) {
			fComboLocations.get(r).removeActionListener(this);
		}
		for (int r = 0; r < fRadioNames.size(); r++) {
			fRadioNames.get(r).removeActionListener(this);
		}
		fComboFontNames.removeActionListener(this);
		fComboFontSizes.removeActionListener(this);
		fCheckIncludeOneDesignTimes.removeActionListener(this);
		fCheckCombineRaceAndSeries.removeActionListener(this);

		fCheckHidePenaltyPoints.removeActionListener(this);
		fCheckShowLastXRaces.removeActionListener(this);
		fTextLastXRaces.removeActionListener(this);

		fTextTemplate.removeActionListener(this);
		fButtonChooseTemplate.removeActionListener(this);
	}

	public void propertyChange(PropertyChangeEvent parm1) {
		// really dont expect this to happen much
		updateFields();
	}

	/**
	 * restore supports backing out of editing a regatta so it will restore the atomic items and the division list.. but
	 * not the entries, nor race objects
	 **/
	@Override
	public void restore(ReportOptions a, ReportOptions b) {
		if (a == b) return;
		ReportOptions active = (ReportOptions) a;
		ReportOptions backup = (ReportOptions) b;

		if (active != null && backup != null) {
			active.setNameFormat(backup.getNameFormat());
			active.setOptionLocationValues(backup.getOptionLocationValues());
			active.setFontName(backup.getFontName());
			active.setFontSize(backup.getFontSize());
			active.setTemplateFile(backup.getTemplateFile());
			super.restore(active, backup);
		}
	}

	private void setComboLocationValue(int index, String newValue) {
		if (index >= fComboLocations.size()) {
			int size = fComboLocations.size();
			// need a new combo field
			for (int i = size; i <= index; i++) {
				JComboBox b = new JComboBox(ReportOptions.getOptionLocations().toArray());
				b.setSelectedIndex(0);
				fComboLocations.add(b);
				b.setActionCommand(LOCATION_ACTION);
				fPanelColumns.add(b);
				HelpManager.getInstance().registerHelpTopic(b, "report.panelColumns");
			}
		}
		fComboLocations.get(index).setSelectedItem(newValue);
	}

	@Override
	public void updateFields() {
		if (fOptions != null) {
			int holdFormat = Person.getFormat();
			Person.setFormat(fOptions.getNameFormat());

			// update the drop down column options
			int locationValueSize = fOptions.getOptionLocationValues().size();
			// int comboSize = fComboLocations.size();
			for (int l = 0; l < locationValueSize; l++) {
				setComboLocationValue(l, fOptions.getOptionLocationValues().get(l));
			}

			// update the same matrix
			for (int c = 0; c < fColumnHeads.length; c++) {
				String s = fOptions.getColumnEntry(c, fEntry, false, true);
				fModelSample.setValueAt(s, 0, c);
				fColumnHeads[c] = fOptions.getColumnHeader(c);
			}
			fModelSample.setColumnIdentifiers(fColumnHeads);
			fTableSample.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);

			fRadioNames.get(fOptions.getNameFormat()).setSelected(true);

			fComboFontNames.setSelectedItem(fOptions.getFontName());
			fComboFontSizes.setSelectedIndex(fOptions.getFontSize());
			if (fOptions.getFontSize() >= sFontPts.length) fOptions.setFontSize(0);
			fTableSample.setFont(new Font(fOptions.getFontName(), Font.PLAIN, sFontPts[fOptions.getFontSize()]));

			Person.setFormat(holdFormat);
			fTextTemplate.setText(fOptions.getTemplateFile());
			fCheckIncludeOneDesignTimes.setSelected(fOptions.isIncludeOneDesignTimes());
			fCheckCombineRaceAndSeries.setSelected(fOptions.isCombineRaceAndSeries());

			if (fOptions.getName().equals(ActionReportSeriesStandingsSingleStage.TABNAME)) {
				fPanelLastXRaces.setVisible(true);
				fCheckHidePenaltyPoints.setVisible(true);
				fCheckCombineRaceAndSeries.setVisible(false);
				// TODO turn to  true when ready  to support this

				fCheckHidePenaltyPoints.setSelected(fOptions.isHidePenaltyPoints());
				fCheckShowLastXRaces.setSelected(fOptions.isShowLastXRaces());
				fTextLastXRaces.setText(Integer.toString(fOptions.getLastXRaces()));
				fTextLastXRaces.setEnabled(fOptions.isShowLastXRaces());
			} else {
				fPanelLastXRaces.setVisible(false);
				fCheckHidePenaltyPoints.setVisible(false);
				fCheckCombineRaceAndSeries.setVisible(false);
			}
		}
	}

	private static int[] sFontPts = new int[] { 12, 10, 12, 14, 18, 24, 36 };

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == LOCATION_ACTION) {
			fComboLocations_actionPerformed(event);
		} else if (event.getActionCommand() == NAME_ACTION) {
			fRadioNames_actionPerformed(event);
		} else if (event.getSource() == fComboFontNames) {
			fComboFontNames_actionPerformed();
		} else if (event.getSource() == fComboFontSizes) {
			fComboFontSizes_actionPerformed();
		} else if (event.getSource() == fButtonChooseTemplate) {
			fButtonChooseTemplate_actionPerformed();
		} else if (event.getSource() == fTextTemplate) {
			fTextTemplate_actionPerformed();
		} else if (event.getSource() == fCheckIncludeOneDesignTimes) {
			fCheckIncludeOneDesignTimes_actionPerformed();
		} else if (event.getSource() == fCheckShowLastXRaces) {
			fCheckShowLastXRaces_actionPerformed();
		} else if (event.getSource() == fTextLastXRaces) {
			fTextLastXRaces_actionPerformed();
		} else if (event.getSource() == fCheckHidePenaltyPoints) {
			fCheckHidePenaltyPoints_actionPerformed();
		} else if (event.getSource() == fCheckCombineRaceAndSeries) {
			fCheckCombineRaceAndSeries_actionPerformed();
		}

		if (getEditorParent() != null) getEditorParent().eventOccurred(this, event);
	}

	public void fComboFontSizes_actionPerformed() {
		int size = fComboFontSizes.getSelectedIndex();
		fOptions.setFontSize(size);
		updateFields();
	}

	public void fComboFontNames_actionPerformed() {
		String item = (String) fComboFontNames.getSelectedItem();
		fOptions.setFontName(item);
		updateFields();
	}

	public void fComboLocations_actionPerformed(ActionEvent event) {
		JComboBox cb = (JComboBox) event.getSource();
		int i = fComboLocations.indexOf(cb); // this is index of column headers
		String newLoc = (String) cb.getSelectedItem();
		// substitute back in the english 'none'
		String oldLoc = (i < 0) ? "setToNoneDummy" : fOptions.getOptionLocationValues().get(i);
		if (!newLoc.equals(oldLoc)) {
			fOptions.setOptionLocationValues(i, (i < 0) ? "none" : newLoc);
			updateFields();
		}
	}

	public void fRadioNames_actionPerformed(ActionEvent event) {
		int i = fRadioNames.indexOf(event.getSource());
		fOptions.setNameFormat(i);
		updateFields();
	}

	public void fCheckCombineRaceAndSeries_actionPerformed() {
		fOptions.setCombineRaceAndSeries(fCheckCombineRaceAndSeries.isSelected());
	}

	public void fCheckIncludeOneDesignTimes_actionPerformed() {
		fOptions.setIncludeOneDesignTimes(fCheckIncludeOneDesignTimes.isSelected());
	}

	public void fCheckHidePenaltyPoints_actionPerformed() {
		fOptions.setHidePenaltyPoints(fCheckHidePenaltyPoints.isSelected());
	}

	public void fCheckShowLastXRaces_actionPerformed() {
		boolean isSel = fCheckShowLastXRaces.isSelected();
		fOptions.setShowLastXRaces(fCheckShowLastXRaces.isSelected());
		fTextLastXRaces.setEnabled(isSel);
	}

	public void fTextLastXRaces_actionPerformed() {
		String old = Integer.toString(fOptions.getLastXRaces());
		try {
			String str = fTextLastXRaces.getText();
			int x = Integer.parseInt(str);
			fOptions.setLastXRaces(x);
		}
		catch (Exception e) {
			fTextLastXRaces.setText(old);
		}
	}

	public void fTextTemplate_actionPerformed() {
		fOptions.setTemplateFile(fTextTemplate.getText());
	}

	private static Frame sChooserFrame;
	private static JFileChooser sFileChooser;

	public void fButtonChooseTemplate_actionPerformed() {
		if (sFileChooser == null) {
			try {
				sFileChooser = (JFileChooser) swFileChooser.get();
			}
			catch (Exception e) {}
		}
		int result = sFileChooser.showOpenDialog(sChooserFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			String fileName = sFileChooser.getSelectedFile().getPath();
			fOptions.setTemplateFile(fileName);
			fTextTemplate.setText(fileName);
		}

	}

	public class HtmlFileFilter extends javax.swing.filechooser.FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			String extension = getExtension(f);
			return (extension.equalsIgnoreCase("htm") || extension.equalsIgnoreCase("html") || extension
					.equalsIgnoreCase("asp"));
		}

		@Override
		public String getDescription() {
			return "(*.html, *.htm, *.asp)";
		}

		private String getExtension(File f) {
			String s = f.getName();
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) return s.substring(i + 1).toLowerCase();
			return EMPTY;
		}
	}

}
/**
 * $Log: PanelReportOptions.java,v $ Revision 1.6 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.18.4.3 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.18.4.2 2005/11/19 20:34:55 sandyg last of java 5 conversion, created swingworker, removed threads
 * packages.
 * 
 * Revision 1.18.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.18 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.17 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.16 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.15 2003/05/17 22:45:04 sandyg fixed combo box update problem
 * 
 * Revision 1.14 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.13 2003/04/27 21:35:36 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.12 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.11 2003/03/28 02:34:54 sandyg fixed bug #685307
 * 
 * Revision 1.10 2003/03/19 02:38:24 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.9 2003/01/06 00:32:36 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.8 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
