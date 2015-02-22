// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import org.gromurph.javascore.actions.ActionAbout;
import org.gromurph.javascore.actions.ActionEditEntries;
import org.gromurph.javascore.actions.ActionEditFleets;
import org.gromurph.javascore.actions.ActionEditMasterDivisions;
import org.gromurph.javascore.actions.ActionEditPreferences;
import org.gromurph.javascore.actions.ActionEditRaces;
import org.gromurph.javascore.actions.ActionEditRegatta;
import org.gromurph.javascore.actions.ActionEditReportOptions;
import org.gromurph.javascore.actions.ActionEditSubDivisions;
import org.gromurph.javascore.actions.ActionExport;
import org.gromurph.javascore.actions.ActionImportEntries;
import org.gromurph.javascore.actions.ActionImportMarkRoundings;
import org.gromurph.javascore.actions.ActionImportResults;
import org.gromurph.javascore.actions.ActionPostXrr;
import org.gromurph.javascore.actions.ActionShowEditor;
import org.gromurph.javascore.actions.ActionSplitBySeeding;
import org.gromurph.javascore.actions.ActionSplitOnRanking;
import org.gromurph.javascore.gui.PanelDivision;
import org.gromurph.javascore.gui.PanelEntry;
import org.gromurph.javascore.gui.PanelFleet;
import org.gromurph.javascore.gui.PanelPenalty;
import org.gromurph.javascore.gui.PanelPreferences;
import org.gromurph.javascore.gui.PanelRace;
import org.gromurph.javascore.gui.PanelRatingDouble;
import org.gromurph.javascore.gui.PanelRatingOneDesign;
import org.gromurph.javascore.gui.PanelRegatta;
import org.gromurph.javascore.gui.PanelReportOptions;
import org.gromurph.javascore.gui.PanelScoringOptions;
import org.gromurph.javascore.gui.PanelStage;
import org.gromurph.javascore.gui.PanelSubDivision;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.manager.ReportViewer;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Fleet;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ReportOptions;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.ratings.RatingDouble;
import org.gromurph.javascore.model.ratings.RatingOneDesign;
import org.gromurph.javascore.model.scoring.ScoringOptions;
import org.gromurph.javascore.model.scoring.SingleStage;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.EditorManager;
import org.gromurph.util.HelpManager;
import org.gromurph.util.SplashScreen;
import org.gromurph.util.Util;
import org.gromurph.util.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaScore extends JFrame implements ActionListener, WindowListener {
	private static final String SPLASH_GRAPHIC = "/images/SplashGraphic.jpg";
	
	// release 7.3.1
		// DONE fixed (again) crew dialogs
		// DONE A+ NOT reading last year's miami regattas right - losing all after first stage, no series standings

		// TODO A add ability to do series scores by day of race
		// TODO Convert to Github and Maven
			// maven: compile, test seem to work
			//    need to figure out the deploy/izpack
			//    need to clean up excess files, understand where to put samples, aux, test regattas etc

		// DONE implement log file, with option to turn it on in javascore.ini?
	// TODO A+ export test:
	//     series points and position do not export right
	//     need race by race subdivision
	
	// TODO A update release website documentation
	// TODO A copy help files over to website
	// TODO A - (gebhardt 2/20/13) not saving/using last directory saved?
	// TODO A (dykman 2/26/13) not handling order of finish right
	// TODO A review/revise java 7 esp mac install documentation

//  TODO 	B Able to post "blank" event to ISAF
//  TODO 	B Able to validate sailor ids
//  - look for missing
//  - look for duplicates
//  - validate what we have
//  - automate? lookup options for missing/invalid

	// TODO retest ISAF upload
	
	// TODO B size of duplicate sail dialog, also flag boats already finished?
	// TODO B invalid time on race dialog - do popup
	
    // TODO A- Better process info on ISAF upload...
	
	// TODO (done?) finish converting dialog creation to ActionShowEditor's

	// TODO B drop "same time score together??? " from Fleet?

//  TODO B go all "multistage" just hide if a single stage
//	DONE B postSWC - clear out linger group info in div/subdivs.. in stages where possible
//	TODO B postSWC - implement tiebreak by previous stage position
//   TODO 	B Do we want to add ability to set stage in division dialog?
//   TODO 	B ISAF upload, advanced way to tweak XRR before sending

// 	TODO C generic divisions with other divisions

		
	// country flags option
	// -- HOLD XRR import
	// fleet/subdiv finish order (one race)
	// div race length orders get lost

	// 6.0
	// mobile report layouts
	// posting ftp from within js

	private static ResourceBundle res = null;
	private static ResourceBundle resUtil = null;
	
	protected static Logger logger = LoggerFactory.getLogger( JavaScore.class);

	private static long sStartTimeStatic;

	private Regatta fRegatta = null;

	private static SplashScreen sSplash;

	//private DialogEntryTreeEditor sDialogEntries;

	JMenuBar fMenuBar = new JMenuBar();

	JMenu fMenuFile = new JMenu();
	JMenuItem fItemNew = new JMenuItem();
	JMenuItem fItemOpen = new JMenuItem();
	JMenuItem fItemSave = new JMenuItem();
	JMenuItem fItemSaveAs = new JMenuItem();
	JMenuItem fItemRestore = new JMenuItem();
	JMenuItem fItemRecent1 = new JMenuItem();
	JMenuItem fItemRecent2 = new JMenuItem();
	JMenuItem fItemRecent3 = new JMenuItem();

	JMenu fItemImport = new JMenu();
	JMenuItem fItemPreferences; // initialized below
	JMenuItem fItemExit = new JMenuItem();

	JMenu fMenuReports = new JMenu();
	JMenuItem fItemShowReports = new JMenuItem();
	JMenuItem fItemReportOptions;
	
	ActionShowEditor fActionEditReportOptions = new ActionEditReportOptions();

	JMenu fMenuDivisions = new JMenu();
	ActionShowEditor fActionEditMasterDivisions = new ActionEditMasterDivisions();
	ActionShowEditor fActionEditFleets = new ActionEditFleets();
	ActionShowEditor fActionEditSubDivisions = new ActionEditSubDivisions();

	JMenu fMenuHelp = new JMenu();
	JMenu fItemLocale = new JMenu();
	JMenuItem fItemHelp = new JMenuItem();

	Action fActionImportEntries = new ActionImportEntries();
	Action fActionImportResults = new ActionImportResults();
	Action fActionImportMarkRoundings = new ActionImportMarkRoundings();

	Action fActionExport = new ActionExport();
	Action fActionPostXrr = new ActionPostXrr();
	//Action fActionMarkGraph = null;
	Action 	fActionAbout = new ActionAbout();

	Action fActionSplitBySeeding = new ActionSplitBySeeding();
	Action fActionSplitOnRanking = new ActionSplitOnRanking();

	JPanel fToolBar = new JPanel();
	JButton fButtonRegatta;
	JButton fButtonEntries;
	JButton fButtonRaces;
	
	ActionShowEditor fActionEditRegatta = new ActionEditRegatta();
	ActionShowEditor fActionEditRaces = new ActionEditRaces();
	ActionShowEditor fActionEditEntries = new ActionEditEntries();
	ActionShowEditor fActionEditPreferences = new ActionEditPreferences();

	static {
		// Util.setIniVersion( "javascore", VERSION);
		// Util.setDefaultStartupDirectory( "c:/javascore");

		sStartTimeStatic = System.currentTimeMillis();
		ToolTipManager.sharedInstance().setDismissDelay(2000); // reduce to 2
		// seconds

		String iniLocale = JavaScoreProperties.getPropertyValue(JavaScoreProperties.LOCALE_PROPERTY);
		if (iniLocale != null)
			Util.initLocale(iniLocale);

		res = JavaScoreProperties.getResources();
		resUtil = Util.getResources();

		// make sure Look and feel is set, needed for Mac stuff i think
		String laf = JavaScoreProperties.getPropertyValue(JavaScoreProperties.LOOKANDFEEL_PROPERTY);
		if (laf == null) {
			laf = UIManager.getSystemLookAndFeelClassName();
		}
		JavaScore.setLookAndFeel(laf);

		initializeEditors();
	}
	
	public static void initializeEditors() {
		EditorManager.put( Penalty.class, PanelPenalty.class);
		EditorManager.put( Race.class, PanelRace.class);
		EditorManager.put( Regatta.class, PanelRegatta.class);
		EditorManager.put( ReportOptions.class, PanelReportOptions.class);
		EditorManager.put( ScoringOptions.class, PanelScoringOptions.class);
		EditorManager.put( SingleStage.class, PanelScoringOptions.class);
		EditorManager.put( Stage.class, PanelStage.class);
		EditorManager.put( SubDivision.class, PanelSubDivision.class);
		EditorManager.put( Division.class, PanelDivision.class);
		EditorManager.put( JavaScoreProperties.class, PanelPreferences.class);
		EditorManager.put( Fleet.class, PanelFleet.class);
		EditorManager.put( Entry.class, PanelEntry.class);
		EditorManager.put( RatingOneDesign.class, PanelRatingOneDesign.class);
		EditorManager.put( RatingDouble.class, PanelRatingDouble.class);		
	}

	static public void main(String args[]) {
		Util.checkJreVersion("1.7.0", "Java 7.0");

		sSplash = new SplashScreen(SPLASH_GRAPHIC, JavaScoreProperties.getVersion(), JavaScoreProperties.getRelease());
		new Timer(4000, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				hideSplash();
			}
		}).start();

		sInstance = new JavaScore();

		try {
			Util.setDumpTitle(res.getString("MainTitle") + " " + JavaScoreProperties.getVersion());
			if (args.length > 0) {
				try {
					Regatta reg = RegattaManager.readRegattaFromDisk( args[0]);
					getInstance().setRegatta(reg);
				} catch (Exception e) {}
			}
			getInstance().setVisible(true);
		} catch (Exception e) {
			Util.showError(e, true);
		}
	}

	public static JavaScore initializeForTesting() {
		sInstance = new JavaScore();
		return sInstance;
	}
	public static JavaScore getInstance() {
		return sInstance;
	}

	public static boolean hasInstance() {
		return (sInstance != null);
	}

	public static void hideSplash() {
		if (sSplash != null)
			sSplash.setVisible(false);
		sSplash = null;
	}

	private static JavaScore sInstance;

	public static void updateMainTitle() {
		if (sInstance != null) sInstance.updateTitle();
	}
	
	public void updateTitle() {
		StringBuffer title = new StringBuffer();
		title.append(res.getString("MainTitle"));
		title.append(" ");
		title.append(JavaScoreProperties.getVersion());
		title.append(": ");
		if (fRegatta != null) {
			title.append(fRegatta.toString());
		} else {
			title.append(res.getString("MainMessageNoRegattaTag"));
		}
		setTitle(title.toString());
	}

	private void runSwingWorkers() {
		Thread workerThread = new Thread() {
			@Override public void run() {
				List<ActionShowEditor> editors = new ArrayList<ActionShowEditor>(10);
				editors.add( fActionEditRegatta);
				editors.add( fActionEditPreferences);
				editors.add( fActionEditEntries);
				editors.add( fActionEditMasterDivisions);
				editors.add( fActionEditFleets);
				editors.add( fActionEditSubDivisions);
				editors.add( fActionEditRaces);
				editors.add( fActionEditReportOptions);

				for (ActionShowEditor editor : editors) {
					editor.startInitializing();
					//wait for it to finish
					editor.getDialog();
				}
				
				List<SwingWorker> workers = new ArrayList<SwingWorker>(10);
				workers.add(swFileChooser);
				workers.add(swReport);

				for (SwingWorker w : workers) {
					Object wo = null;
					try {
						w.start();
						wo = w.get(); // wait for this one to return
						logger.info("SwingWorker loaded {}", (wo == null ? "null" : wo.getClass().getName()));
					} catch (Exception e) {
						logger.error("SwingWorker failed {}", (wo == null ? "null" : wo.getClass().getName()));
					}
				}

			}
		};
		workerThread.start();
	}

	private JavaScore() {

		getContentPane().setLayout(new BorderLayout(0, 0));

		HelpManager.getInstance().setPrimarySource(this);
		HelpManager.getInstance().setMainHelpSet(JavaScoreProperties.HELP_SET);
		HelpManager.getInstance().enableWindowHelp(this, JavaScoreProperties.HELP_ROOT, this);

		addMenus();
		addToolbar();

		updateEnabled();

		hideSplash();

		// Util.checkJreVersion( "1.4");

		pack();

		int winX = getToolkit().getScreenSize().width;
		int winY = getToolkit().getScreenSize().height;

		// this.setMinimumSize( new Dimension( winX-150, 100));
		int prefX = getSize().width;
		this.setLocation(winX / 2 - prefX / 2, (int) (winY * .10));

		logger.debug( MessageFormat.format(res.getString("MainMessageContructorCompleted"),
				new Object[] { new Long(System.currentTimeMillis() - sStartTimeStatic) }));

		logger.debug( "{}: {}", res.getString("MainMessageLocale"), Locale.getDefault());
		// swPreferences.start();
		// swDivisions.start();

		updateTitle();

		Util.getImageIcon(this, JavaScoreProperties.PROTESTFLAG_ICON);
		Util.getImageIcon(this, HelpManager.CONTEXTHELP_ICON);
		
		addWindowListener(this);
		
	}
	
	public static void subWindowClosing() {
    	if (getInstance() != null)
    		getInstance().updateEnabled();
    }

	public void updateEnabled() {
		boolean haveRegatta = (fRegatta != null);
		fButtonRegatta.setEnabled(true);
		
		fButtonRaces.setEnabled(haveRegatta);
		fButtonEntries.setEnabled(haveRegatta);
		fMenuReports.setEnabled(haveRegatta);
		fItemSave.setEnabled(haveRegatta);
		fItemSaveAs.setEnabled(haveRegatta);
		fItemRestore.setEnabled(haveRegatta);

		fActionExport.setEnabled(haveRegatta && !fRegatta.isMultistage());
		fActionPostXrr.setEnabled(haveRegatta);
		fItemImport.setEnabled(haveRegatta);
		fActionImportEntries.setEnabled(haveRegatta);
		fActionImportResults.setEnabled(haveRegatta);
		fActionImportMarkRoundings.setEnabled(haveRegatta);

		if (fActionEditSubDivisions != null) {
			fActionEditSubDivisions.setEnabled(haveRegatta);
			fActionEditMasterDivisions.setEnabled(true);
			fActionEditFleets.setEnabled(haveRegatta);

			fActionSplitBySeeding.setEnabled(haveRegatta);
			fActionSplitOnRanking.setEnabled(haveRegatta && fRegatta.isMultistage());
		}
	}

	private void addToolbar() {

		getContentPane().add(fToolBar, BorderLayout.CENTER);
		fToolBar.setLayout(new FlowLayout(FlowLayout.CENTER));

		fButtonRegatta = new JButton(fActionEditRegatta);
		fButtonRegatta.setName("fButtonRegatta");
		fButtonRegatta.setEnabled(false); // turned on when filechooser is init
		HelpManager.getInstance().registerHelpTopic(fButtonRegatta, fActionEditRegatta.getHelpTopic());
		fToolBar.add(fButtonRegatta);

		fButtonEntries = new JButton(fActionEditEntries);
		fButtonEntries.setName("fButtonEntries");
		HelpManager.getInstance().registerHelpTopic(fButtonEntries, fActionEditEntries.getHelpTopic());
		fToolBar.add(fButtonEntries);

		fButtonRaces = new JButton( fActionEditRaces);
		fButtonRaces.setName("fButtonRaces");
		HelpManager.getInstance().registerHelpTopic(fButtonRaces, fActionEditRaces.getHelpTopic());
		fToolBar.add(fButtonRaces);

		if (!Util.isMac()) {
			Icon buttonIcon = Util.getImageIcon(this, Util.OPEN_ICON);
			if (buttonIcon != null)
				fButtonRegatta.setIcon(buttonIcon);

			buttonIcon = Util.getImageIcon(this, Util.EDIT_ICON);
			if (buttonIcon != null)
				fButtonEntries.setIcon(buttonIcon);

			buttonIcon = Util.getImageIcon(this, JavaScoreProperties.FINISHES_ICON);
			if (buttonIcon != null)
				fButtonRaces.setIcon(buttonIcon);
		}

		JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JComponent csh = HelpManager.getInstance().createCSHButton();
		csh.setToolTipText(res.getString("MainLabelContextHelpToolTip"));
		getContentPane().add(helpPanel, BorderLayout.EAST);
		helpPanel.add(csh);

		fButtonRegatta.setToolTipText(res.getString("MainButtonRegattaToolTip"));
		fButtonEntries.setToolTipText(res.getString("MainButtonEntriesToolTip"));
		fButtonRaces.setToolTipText(res.getString("RaceButtonToolTip"));
	}

	private void addMenus() {
		addMenuFile();
		addMenuReports();
		addMenuDivisions();
		addMenuHelp();

		setJMenuBar(fMenuBar);
	}

	private void addMenuDivisions() {
		fMenuDivisions.setText(res.getString("MenuDivisions"));
		fMenuDivisions.setMnemonic(res.getString("MenuDivisionsMnemonic").charAt(0));
		fMenuBar.add(fMenuDivisions);

		HelpManager.getInstance().registerHelpTopic(fMenuDivisions, "main.menuMasterFiles");
		fMenuDivisions.setEnabled(true);

		fMenuDivisions.add(fActionEditMasterDivisions);
		fActionEditMasterDivisions.setEnabled(true);

		fMenuDivisions.add(fActionEditFleets);
		fActionEditFleets.setEnabled(false);

		fMenuDivisions.add(fActionEditSubDivisions);
		fActionEditSubDivisions.setEnabled(false);

		fMenuDivisions.add(fActionSplitBySeeding);
		fActionSplitBySeeding.setEnabled(false);
		
		fMenuDivisions.add(fActionSplitOnRanking);
		fActionSplitOnRanking.setEnabled(false);
	}

	private void addMenuFile() {
		fMenuFile.setText(resUtil.getString("FileMenu"));
		fMenuFile.setName("fMenuFile");
		fMenuFile.setMnemonic(resUtil.getString("FileMenuMnemonic").charAt(0));
		HelpManager.getInstance().registerHelpTopic(fMenuFile, "main.fMenuFile");

		fMenuFile.add(fItemNew);
		fItemNew.setName("fItemNew");
		fItemNew.setText(res.getString("MenuNewRegatta"));
		fItemNew.setMnemonic(res.getString("MenuNewRegattaMnemonic").charAt(0));

		fMenuFile.add(fItemOpen);
		fItemOpen.setName("fItemOpen");
		fItemOpen.setText(res.getString("MenuOpenRegatta") + "...");
		fItemOpen.setMnemonic(res.getString("MenuOpenRegattaMnemonic").charAt(0));
		fItemOpen.setEnabled(false); // turned on when filechooser is init

		fMenuFile.add(fItemSave);
		fItemSave.setName("fItemSave");
		fItemSave.setText(res.getString("MenuSaveRegatta"));
		fItemSave.setMnemonic(res.getString("MenuSaveRegattaMnemonic").charAt(0));

		fMenuFile.add(fItemSaveAs);
		fItemSaveAs.setName("fItemSaveAs");
		fItemSaveAs.setText(res.getString("MenuSaveRegattaAs") + "...");
		fItemSaveAs.setMnemonic(res.getString("MenuSaveRegattaAsMnemonic").charAt(0));

		fMenuFile.add(fItemRestore);
		fItemRestore.setName("fItemRestore");
		fItemRestore.setText(res.getString("MenuRestoreRegatta") + "...");
		fItemRestore.setMnemonic(res.getString("MenuRestoreRegattaMnemonic").charAt(0));

		fMenuFile.addSeparator();

		fItemImport.setText(res.getString("MenuImport"));
		fItemImport.setName("fItemImport");
		fItemImport.setMnemonic(res.getString("MenuImportMnemonic").charAt(0));
		fMenuFile.add(fItemImport);

		fItemImport.add(fActionImportEntries);
		fItemImport.add(fActionImportResults);
		fItemImport.add(fActionImportMarkRoundings);

		//	fActionImportXrr = new ActionImportISAF1_3Results();
		//	fItemImport.add(fActionImportXrr);

		fMenuFile.setName("fMenuFile");
		fMenuFile.add(fActionExport);
		fMenuFile.add(fActionPostXrr);

		fMenuFile.addSeparator();

		fItemPreferences = new JMenuItem( fActionEditPreferences);
		fItemPreferences.setName("fItemPreferences");
		fMenuFile.add(fItemPreferences);

		fMenuFile.addSeparator();

		fMenuFile.add(fItemExit);
		fItemExit.setName("fItemExit");
		fItemExit.setText(resUtil.getString("ExitMenu"));
		fItemExit.setMnemonic(resUtil.getString("ExitMenuMnemonic").charAt(0));

		fMenuFile.addSeparator();

		fItemRecent1.setActionCommand("1");
		fItemRecent2.setActionCommand("2");
		fItemRecent3.setActionCommand("3");
		fMenuFile.add(fItemRecent1);
		fMenuFile.add(fItemRecent2);
		fMenuFile.add(fItemRecent3);
		updateRecentMenuItems(null, null);

		fMenuBar.add(fMenuFile);
	}

	private void addMenuReports() {
		fMenuReports.setText(res.getString("MenuReports"));
		fMenuReports.setMnemonic(res.getString("MenuReportsMnemonic").charAt(0));
		fMenuBar.add(fMenuReports);
		HelpManager.getInstance().registerHelpTopic(fMenuReports, "main.fMenuReports");

		fItemShowReports.setText(res.getString("MenuShowReports"));
		fItemShowReports.setMnemonic(res.getString("MenuShowReportsMnemonic").charAt(0));
		fMenuReports.add(fItemShowReports);

		// fActionMarkGraph = new ActionMarkGraph();
		// fMenuReports.add( fActionMarkGraph);

		fItemReportOptions = new JMenuItem( fActionEditReportOptions);
		fItemReportOptions.setName("fItemReportOptions");
		fMenuReports.add(fItemReportOptions);
	}

	private void addMenuHelp() {
		fMenuHelp.setText(resUtil.getString("HelpMenu"));
		fMenuHelp.setMnemonic(resUtil.getString("HelpMnemonic").charAt(0));
		HelpManager.getInstance().registerHelpTopic(fMenuHelp, "main.fMenuHelp");

		fItemHelp.setText(res.getString("MenuHelpJavaScore"));
		fItemHelp.setMnemonic(res.getString("MenuHelpJavaScoreMnemonic").charAt(0));
		fMenuHelp.add(fItemHelp);

		JMenuItem mm = HelpManager.getInstance().createCSHMenuItem();
		mm.setText(res.getString("MenuWhatsThis"));
		mm.setMnemonic(res.getString("MenuWhatsThisMnemonic").charAt(0));
		fMenuHelp.add(mm);

		fMenuHelp.addSeparator();
		fMenuHelp.add(fActionAbout);

		fMenuBar.add(fMenuHelp);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fItemNew)
			fItemNew_actionPerformed();
		else if (event.getSource() == fItemSave)
			fItemSave_actionPerformed(event);
		else if (event.getSource() == fItemSaveAs)
			fItemSaveAs_actionPerformed(event);
		else if (event.getSource() == fItemOpen)
			fItemOpen_actionPerformed();
		else if (event.getSource() == fItemRestore)
			fItemRestore_actionPerformed();
		else if (event.getSource() == fItemHelp)
			fItemHelp_actionPerformed();
		else if (event.getSource() == fItemExit)
			fItemExit_actionPerformed();
		else if (event.getSource() == fItemShowReports)
			fItemShowReports_actionPerformed();
		else if (event.getSource() == fItemRecent1)
			fItemRecent_actionPerformed(event);
		else if (event.getSource() == fItemRecent2)
			fItemRecent_actionPerformed(event);
		else if (event.getSource() == fItemRecent3)
			fItemRecent_actionPerformed(event);

		updateEnabled();
	}

	public void start() {
		runSwingWorkers();
		
		fItemNew.addActionListener(this);
		fItemSave.addActionListener(this);
		fItemSaveAs.addActionListener(this);
		fItemOpen.addActionListener(this);

		fItemRestore.addActionListener(this);
		fItemHelp.addActionListener(this);
		fItemExit.addActionListener(this);

		fItemRecent1.addActionListener(this);
		fItemRecent2.addActionListener(this);
		fItemRecent3.addActionListener(this);

		fItemShowReports.addActionListener(this);
	}

	public void stop() {
		// if (sRegatta != null) sRegatta.removePropertyChangeListener( this);

		fItemNew.removeActionListener(this);
		fItemSave.removeActionListener(this);
		fItemSaveAs.removeActionListener(this);
		fItemOpen.removeActionListener(this);
		fItemRestore.removeActionListener(this);
		fItemHelp.removeActionListener(this);
		fItemExit.removeActionListener(this);

		fItemRecent1.removeActionListener(this);
		fItemRecent2.removeActionListener(this);
		fItemRecent3.removeActionListener(this);

		fItemShowReports.removeActionListener(this);
	}

	private void setRegatta(Regatta newRegatta) {
		
		JavaScoreProperties.setRegatta(newRegatta);
		fRegatta = newRegatta;

		updateTitle();
	}

	ReportViewer fReportViewer = null;

	public static void backgroundSave() {
		if (sInstance == null || JavaScoreProperties.getRegatta() == null)
			return;

		final Regatta regatta = (Regatta) JavaScoreProperties.getRegatta().clone();

		if ((regatta.getSaveName() == null) || regatta.getSaveName().equals(Regatta.NONAME)) {
			// if we do not have a name, have to prompt for one
			// don't do that save in the background
			getInstance().fItemSaveAs_actionPerformed(null);
		} else {
			// have a name, so fire the save off as a separate thread.
			final Throwable tempStack = new Throwable();
			SwingWorker bkgd = new SwingWorker() {
				Regatta lRegatta = regatta;
				Throwable inStack = tempStack;

				@Override public Object construct() {
					try {
						getInstance().regattaSave(lRegatta, lRegatta.getSaveDirectory(), lRegatta.getSaveName());
					} catch (Exception e) {
						tempStack.printStackTrace();
						Util.showError(e, res.getString("MainMessageBackgroundSaveFailed"), true, inStack);
					}
					return null;
				}
			};
			bkgd.start();
		}
	}

	private void fItemRecent_actionPerformed(ActionEvent event) {
		JMenuItem menu = (JMenuItem) event.getSource();
		String num = menu.getActionCommand();
		String dir = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RECENTDIRBASE_PROPERTY + num);
		String regfile = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RECENTBASE_PROPERTY + num);

		if (regfile != null && regfile.length() > 0) {
			if (dir != null && dir.length() > 0)
				Util.setWorkingDirectory(dir);
			openRegatta(regfile);
		}
	}

	private void updateRecentMenuItems(String d, String f) {
		if (d != null)
			JavaScoreProperties.pushRegattaFile(d, f);

		String reg1 = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RECENT1_PROPERTY);
		String reg2 = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RECENT2_PROPERTY);
		String reg3 = JavaScoreProperties.getPropertyValue(JavaScoreProperties.RECENT3_PROPERTY);

		if (reg1.length() > 0) {
			fItemRecent1.setText(reg1);
			fItemRecent1.setVisible(true);
		} else {
			fItemRecent1.setVisible(false);
		}

		if (reg2.length() > 0) {
			fItemRecent2.setText(reg2);
			fItemRecent2.setVisible(true);
		} else {
			fItemRecent2.setVisible(false);
		}

		if (reg3.length() > 0) {
			fItemRecent3.setText(reg3);
			fItemRecent3.setVisible(true);
		} else {
			fItemRecent3.setVisible(false);
		}

	}

	public void updateReports(boolean b) {
		if (fReportViewer == null) {
			try {
				fReportViewer = (ReportViewer) swReport.get();
			} catch (Exception e) {}
		}
		fReportViewer.updateReports(b);
	}

	private void fItemShowReports_actionPerformed() {
		if (fReportViewer == null) updateReports(false);
		fReportViewer.showReports();
	}

	private void fItemHelp_actionPerformed() {
		HelpManager.getInstance().setHelpTopic(JavaScoreProperties.HELP_ROOT);
	}

	private void fItemExit_actionPerformed() {
		exitProgram();
	}
	
	public static void exitProgram() {
		if (hasInstance() && getInstance().fRegatta != null) {
    		int option = JOptionPane.showConfirmDialog(null, res.getString("MainMessageConfirmExitText"), res
    				.getString("MainTitleConfirmExit"), JOptionPane.YES_NO_OPTION);
    
    		if (option == JOptionPane.YES_OPTION) {
    			System.exit(0);
    		} else {
    			getInstance().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    		}
		} else {
    		// if no regatta open, dont sweat the confirm dialog	    
			System.exit(0);
		}
	}

	private void fItemNew_actionPerformed() {
		setRegatta(new Regatta());
		updateEnabled();
		fActionEditRegatta.actionPerformed(null);
	}

	private void fItemRestore_actionPerformed() {
		String fileName = fRegatta.getSaveName();
		if (fileName.startsWith("/"))
			fileName = fileName.substring(1);
		File f = new File(Util.getWorkingDirectory() + fileName + BAK);
		long lastMod = f.lastModified();

		String msg = res.getString("MainMessageDiscardChange");
		if (Util.confirm(MessageFormat.format(msg, new Object[] { new Date(lastMod) }))) {
			try {
				logger.info(res.getString("MainMessageRestoring"));
				Regatta reg = RegattaManager.readRegattaFromDisk( fileName + BAK);
				reg.setSaveName(fileName);
				setRegatta(reg);
				updateReports(true);
				logger.info(res.getString("MainMessageRestoringFinished"));
			} catch (Exception e) {
				Util.showError(e, true);
				setRegatta( new Regatta());
			}
		}
	}

	private void fItemOpen_actionPerformed() {
		openRegatta(null);
	}

	public void openRegatta(String infile) {
		String fileName = infile;
		if (fileName == null) {
			String startDir = Util.getWorkingDirectory();
			fileName = RegattaManager.selectOpenRegattaDialog(res.getString("MenuOpenRegatta"), startDir);
		}

		if ((Util.getWorkingDirectory() != null) && (fileName != null)) {
			try {
				logger.info(res.getString("MainMessageLoadingRegattaStart") + " " + Util.getWorkingDirectory()
						+ fileName);

				// for reading divisions in stages, we need access to regatta
				// in mid-read... tempReg and Reg should be same instance except in case of errs
				
				Regatta reg = RegattaManager.readRegattaFromDisk( fileName);
				if (reg == null) {
					JOptionPane.showMessageDialog(this, MessageFormat.format(res.getString("InvalidRegattaFile"),
							new Object[] { Util.getWorkingDirectory() + fileName }), res
							.getString("InvalidRegattaFileTitle"), JOptionPane.ERROR_MESSAGE);
				} else if (reg.isFinal()) {
					JOptionPane.showMessageDialog(null, res.getString("RegattaMessageFinalOnLoad"), res
							.getString("RegattaTitleFinalOnLoad"), JOptionPane.WARNING_MESSAGE);
				}

				checkVersion(reg);
				
				setRegatta(reg);
				
				regattaBackup();
				logger.info(res.getString("MainMessageFinishedLoading"));

				updateRecentMenuItems(Util.getWorkingDirectory(), fileName);

			} catch (java.io.FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, MessageFormat.format(resUtil.getString("FileNotFound"),
						new Object[] { Util.getWorkingDirectory() + fileName }),
						resUtil.getString("FileNotFoundTitle"), JOptionPane.ERROR_MESSAGE);
				setRegatta( new Regatta());
			} catch (Exception ex) {
				Util.showError(ex, true);
				setRegatta( new Regatta());
			}

		}
		updateEnabled();
		backgroundSave();
	}

	private void fItemSaveAs_actionPerformed(ActionEvent event) {
		String fileName = RegattaManager.selectSaveRegattaDialog(res.getString("MainTitleSaveAs"), fRegatta);

		if (fileName != null) {
			try {
				regattaSave(fRegatta, Util.getWorkingDirectory(), fileName);
				regattaBackup();
				updateRecentMenuItems(Util.getWorkingDirectory(), fileName);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, MessageFormat.format(res.getString("MainMessageSaveFailed"),
						new Object[] { fRegatta.getSaveDirectory() + fRegatta.getSaveName(), e.toString() }), res
						.getString("MainMessageTryAgainMessage"), JOptionPane.ERROR_MESSAGE);
				fItemSaveAs_actionPerformed(event);
			}

		}
	}

	private void fItemSave_actionPerformed(ActionEvent event) {
		if ((fRegatta.getSaveName() == null) || fRegatta.getSaveName().equals(Regatta.NONAME)) {
			fItemSaveAs_actionPerformed(event);
		} else {
			try {
				regattaSave(fRegatta, fRegatta.getSaveDirectory(), fRegatta.getSaveName());
				regattaBackup();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, MessageFormat.format(res.getString("MainMessageSaveFailed"),
						new Object[] { fRegatta.getSaveDirectory() + fRegatta.getSaveName(), e.toString() }), res
						.getString("MainMessageTryAgainMessage"), JOptionPane.ERROR_MESSAGE);
				fItemSaveAs_actionPerformed(event);
			}
		}
	}

	public void regattaSave() throws IOException {
		regattaSave(fRegatta, fRegatta.getSaveDirectory(), fRegatta.getSaveName());
	}

	/**
	 * saves the regatta and regenerates reports, in a separate thread
	 * 
	 * @param reg
	 *            the regatta to be saved
	 * @param dir
	 *            the directory name
	 * @param name
	 *            the file name of the regatta
	 * 
	 * @throws IOException
	 *             if unable to save the regatta
	 */
	private void regattaSave(Regatta reg, String dir, String name) throws IOException {
		logger.info(MessageFormat.format(res.getString("MainMessageSavingRegatta"), new Object[] { dir + name }));
		fItemShowReports.setEnabled(false);
		reg.scoreRegatta();

		if (reg.isFinal()) {
			JOptionPane.showMessageDialog(null, res.getString("RegattaMessageFinalOnSave"), res
					.getString("RegattaTitleFinalOnSave"), JOptionPane.WARNING_MESSAGE);
		} else {
			new RegattaManager(reg).writeRegattaToDisk(dir, name);
			updateReports(false);
			logger.info(res.getString("MainMessageFinishedSaving"));
		}
		fItemShowReports.setEnabled(true);
	}

	public void checkVersion(Regatta reg) {
		String readVersion = reg.getSaveVersion();
		if (readVersion == null)
			return;
		int comp = readVersion.compareTo(JavaScoreProperties.getVersion());
		if (comp > 0) {
			// version being read in is from a LATER version of javascore than
			// this one
			// print a warning that ALL BETS ARE OFF
			String msg = MessageFormat.format(res.getString("RegattaMessageNewerVersion"), new Object[] { readVersion,
					JavaScoreProperties.getVersion() });

			JOptionPane.showMessageDialog(null, msg, res.getString("RegattaTitleNewerVersion"),
					JOptionPane.WARNING_MESSAGE);
			return;
		} else if (comp < 0) {
			// regatta is an earlier version, let the user know that it will be
			// upgraded to current version. (may not open in earlier version
			// again)
			String msg = MessageFormat.format(res.getString("RegattaMessageOlderVersion"), new Object[] { readVersion,
					JavaScoreProperties.getVersion() });

			JOptionPane.showMessageDialog(null, msg, res.getString("RegattaTitleOlderVersion"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		// otherwise version is same, no comment
	}

	private String BAK = ".bak";

	/**
	 * creates the regatta backup file
	 */
	private void regattaBackup() {
		SwingWorker sw = new SwingWorker() {
			Regatta lRegatta = fRegatta;

			@Override public Object construct() {
				String fullName = lRegatta.getSaveDirectory() + lRegatta.getSaveName();
				logger.info(MessageFormat.format(res.getString("MainMessageBackingUp"), new Object[] { fullName
						+ BAK }));
				try {
					FileInputStream fis = new FileInputStream(new File(fullName));
					FileOutputStream fos = new FileOutputStream(new File(fullName + BAK));
					int bufsize = 1024;
					byte[] buffer = new byte[bufsize];
					int n = 0;
					while ((n = fis.read(buffer, 0, bufsize)) >= 0)
						fos.write(buffer, 0, n);
					fos.flush();
					fos.close();
					fis.close();
				} catch (java.io.IOException ex) {
					Util.showError(ex, true);
				}
				return null;
			}
		};
		sw.start();
	}

	@Override public void setVisible(boolean vis) {
		if (vis) {
			if (!isVisible()) {
				start();
			}
		} else {
			if (isVisible()) {
				stop();
			}
		}
		super.setVisible(vis);
	}

	public SwingWorker swFileChooser = new SwingWorker() {
		@Override public Object construct() {
			JFileChooser f = null;
			try {
				f = RegattaManager.getFileChooser();
			} catch (Exception e) {
				Util.showError(e, true);
			}
		    fItemOpen.setEnabled( f != null);
			return f;
		}
	};

	private SwingWorker swReport = new SwingWorker() {
		@Override public Object construct() {
			try {
				ReportViewer report = new ReportViewer();
				return report;
			} catch (Exception e) {
				Util.showError(e, true);
			}
			return null;
		}
	};

	public static void setLookAndFeel(String uiName) {
		// if the new look and feel equals the old then return
		LookAndFeel current = UIManager.getLookAndFeel();
		if (uiName.equals(current.getName()))
			return;

		// look and feel is not the wanted one...
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lafs.length; ++i) {
			if (uiName.equals(lafs[i].getName())) {
				// Change the Look-and-feel
				try {
					// Set new LAF
					UIManager.setLookAndFeel(lafs[i].getClassName());

					// Tell WindowManager to refresh each window's UI
					if (getInstance() != null)
						SwingUtilities.updateComponentTreeUI(getInstance());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, MessageFormat.format(JavaScoreProperties.res
							.getString("MainMessageBadLookAndFeel"), new Object[] { uiName }),
					// Unable to change the look-and-feel. Error: " +
							// exception.toString(),
							JavaScoreProperties.res.getString("MainTitleBadLookAndFeel"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}


	@Override public void windowClosing(WindowEvent e) {			
		exitProgram();
	}
	@Override public void windowOpened(WindowEvent e) {	}
	@Override public void windowClosed(WindowEvent e) {	}
	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowDeactivated(WindowEvent e) {}

	
}


