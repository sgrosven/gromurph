// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ResourceLanguageEditor.java,v 1.5 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility editor for reviewing and editing resource names
 */
public class ResourceLanguageEditor extends JFrame implements ActionListener {
    File fTopDir;
    SortedProperties fResourceList;
    SortedProperties fLanguageList;
    String fResourceFile = "";
    String fLanguageFile = "";
    String fBaseDir = null;
    String fLanguage = null;
    boolean fFileNeedsSaving = false;
    boolean fShowAll = false; // set to true at end of constructor

    static final String QUOTE = "\"";
    static final String COMMA = ",";

    // layout
    // +--getContentPane()-----------------------
    // | +--panelSplitter---+--(main/center)-----
    // | | +--panelList--+  | +----panelEast-----
    // | | | fList-      |  | | fTextKey
    // | | | Resource-   |  | | fTextMainValue
    // | | | Names)      |  | | fTextSubValue
    // | | +-------------+  | +--------------------
    // | +------------------+----------------------
    // | +--panelButtons--(main/south)-------
    // | | fButtonShowAll  fButtonShowMissing
    // | +-----------------------------------
    // +-------------------------------------

    JList<String> fListResourceNames;
    JTextField fTextKey;
    JTextArea fTextMainValue;
    JTextArea fTextSubValue;
    Document fDocSubValue;

    JButton fButtonShowAll;
    JButton fButtonShowMissing;

    JComboBox<String> fComboProperties;
    JComboBox<String> fComboLanguages;

	private static Logger logger = LoggerFactory.getLogger(ResourceLanguageEditor.class);

    public static void main(String[] args) {

	String baseDir = Util.getWorkingDirectory();
	if (new File(baseDir + "/source").exists())
	    baseDir = baseDir + "/source";
		logger.info("BaseDir=" + baseDir);

	ResourceLanguageEditor editor = new ResourceLanguageEditor(baseDir);
	editor.setVisible(true);
    }

    public ResourceLanguageEditor(String topName) {
	super();

	Util.checkJreVersion("1.4", "Java 1.4");

	fBaseDir = topName;
	fTopDir = new File(topName);

	addFields();
	addListeners();

	if (fComboProperties.getItemCount() > 0)
	    fComboProperties.setSelectedIndex(0);
	if (fComboLanguages.getItemCount() > 0)
	    fComboLanguages.setSelectedIndex(0);

	setShowAll(true);
    }

    private void updateTitle() {
	setTitle("Resource editor/browser: <no file open>");

	if (fLanguage != null) {
	    setTitle("Resource editor/browser: " + fResourceFile + ", lang=" + fLanguage);
	} else {
	    setTitle("Resource editor/browser: " + fResourceFile + ", no language file");
	}
    }

    /**
     * opens a alternate language resource file
     * 
     * @param lang
     *            the language suffix - usually 2 or 5 characters such as "en" for English or "en_US" for english,
     *            united states
     */
    private void openLanguageResource(String lang) {
	fLanguage = lang;
	fLanguageList = new SortedProperties();
	fLanguageFile = createLanguageName(fResourceFile, lang);
	try {
	    fLanguageList.load(new FileInputStream(fLanguageFile));
	    logger.info("Language File successfully opened: " + fLanguageFile);

	} catch (Exception exc) {
	    fLanguageList = new SortedProperties();
	    logger.info("Language File created: " + fLanguageFile);
	}
	fListResourceNames_valueChanged();
	fFileNeedsSaving = false;
    }

    private String createLanguageName(String propName, String lang) {
	int dot = propName.indexOf(".properties");
	StringBuffer sb = new StringBuffer();
	sb.append(propName.substring(0, dot));
	sb.append("_");
	sb.append(lang);
	sb.append(".properties");
	return sb.toString();
    }

    String[] fKnownProperties = new String[] { "JavaScore", "GeneralProperties" };

    private void addFields() {

	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
		@Override public void windowClosing(WindowEvent event) {
		exitEditor();
	    }
	});

	this.setSize(800, 600);
	this.setTitle("Resource editor/browser: <no file open>");
	getContentPane().setLayout(new BorderLayout());

	JPanel panelList = new JPanel(new BorderLayout());
	panelList.setBorder(BorderFactory.createTitledBorder("Resource names:"));

	JPanel panelEast = new JPanel(new BorderLayout());

	// flesh out panelNorth
	JMenuBar menubar = addMenus();

	JPanel panelNorth = new JPanel(new GridLayout(2, 1));
	getContentPane().add(panelNorth, BorderLayout.NORTH);

	panelNorth.add(menubar);

	JPanel panelNorth2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panelNorth.add(panelNorth2);

	fComboProperties = new JComboBox<String>(fKnownProperties);
	fComboLanguages = new JComboBox<String>();
	fComboLanguages.setEditable(true);

	panelNorth2.add(new JLabel("Resources: "));
	panelNorth2.add(fComboProperties);
	panelNorth2.add(new JLabel("  Languages: "));
	panelNorth2.add(fComboLanguages);
	panelNorth2.add(new JLabel(" (select one, or type new prefix for new language)"));

	JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelList, panelEast);
	getContentPane().add(splitter, BorderLayout.CENTER);
	splitter.setDividerLocation(200);

	// flesh out panelList
	fListResourceNames = new JList<String>();
	panelList.add(new JScrollPane(fListResourceNames), BorderLayout.CENTER);

	// flesh out panelEast
	JPanel panelKeyValue = new JPanel(new GridBagLayout());
	panelKeyValue.setBorder(BorderFactory.createTitledBorder("Selected resource:"));
	panelEast.add(panelKeyValue, BorderLayout.NORTH);

	fTextKey = new JTextField();
	fTextKey.setEnabled(false);
	Font font = new Font(fTextKey.getFont().getName(), Font.BOLD, fTextKey.getFont().getSize());
	fTextKey.setFont(font);
	fTextKey.setDisabledTextColor(Color.black);
	fTextKey.setBackground(Color.lightGray);

	fTextMainValue = new JTextArea(5, 40);
	fTextMainValue.setLineWrap(true);
	fTextMainValue.setEnabled(false);
	font = new Font(fTextMainValue.getFont().getName(), Font.BOLD, fTextMainValue.getFont().getSize());
	fTextMainValue.setForeground(Color.black);
	fTextMainValue.setDisabledTextColor(Color.black);
	fTextMainValue.setBackground(Color.lightGray);

	fDocSubValue = new PlainDocument();
	fTextSubValue = new JTextArea(fDocSubValue, "", 5, 40);
	fTextSubValue.setLineWrap(true);

	gridbagAdd(panelKeyValue, new JLabel("Key:"), 0, 0);
	gridbagAdd(panelKeyValue, new JLabel("Main Value:"), 0, 1);
	gridbagAdd(panelKeyValue, fTextKey, 1, 0);
	gridbagAdd(panelKeyValue, new JScrollPane(fTextMainValue), 1, 1, 1, 1, GridBagConstraints.WEST,
		GridBagConstraints.BOTH, fInsets);
	gridbagAdd(panelKeyValue, new JLabel("Sub Value:"), 0, 2);
	gridbagAdd(panelKeyValue, new JScrollPane(fTextSubValue), 1, 2, 1, 1, GridBagConstraints.WEST,
		GridBagConstraints.BOTH, fInsets);

	JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
	getContentPane().add(panelButtons, BorderLayout.SOUTH);

	fButtonShowAll = new JButton("Show All");
	fButtonShowMissing = new JButton("Show Missing Only");
	panelButtons.add(fButtonShowAll);
	panelButtons.add(fButtonShowMissing);
    }

    private JMenuBar addMenus() {
	JMenuBar menuBar = new JMenuBar();
	menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

	JMenu menuFile = new JMenu("File");
	menuFile.setMnemonic('F');
	menuBar.add(menuFile);

	//        menuFile.add( new JMenuItem( new ActionOpenResource()));
	//        menuFile.add( new JMenuItem( new ActionSetLanguage()));
	//
	//        menuFile.addSeparator();

	menuFile.add(new JMenuItem(new ActionSave()));
	menuFile.add(new JMenuItem(new ActionExit()));

	return menuBar;
    }

    //    private class ActionOpenResource extends javax.swing.AbstractAction
    //    {
    //        public ActionOpenResource()
    //        {
    //            super( "Open Resource...");
    //        }
    //
    //        public void actionPerformed( ActionEvent event)
    //        {
    //			// first save language file if necessary
    //			if (fLanguageFile != null && fFileNeedsSaving)
    //			{
    //				int option = confirmSaveOrLose( fLanguageFile, "change resources");
    //				if (option == JOptionPane.CANCEL_OPTION)
    //				{
    //					return;
    //				}
    //				else if (option == JOptionPane.YES_OPTION)
    //				{
    //					if (!saveLanguageFile()) return;
    //				}
    //			}
    //
    //			// bring up file dialog
    //			if (fFileChooser == null) initFileChooser( fBaseDir);
    //			int result = fFileChooser.showOpenDialog( ResourceLanguageEditor.this);
    //			if ( result != JFileChooser.APPROVE_OPTION) return;
    //
    //			// process file name
    //			fBaseDir = fFileChooser.getCurrentDirectory().getAbsolutePath();
    //			String propName = fFileChooser.getSelectedFile().getName();
    //
    //			if (!propName.equalsIgnoreCase( fResourceFile))
    //			{
    //				openMainResource( propName);
    //				updateResourceList();
    //			}
    //        }
    //    }

    //    private JFileChooser fFileChooser = null;

    public class PropertiesFileFilter extends javax.swing.filechooser.FileFilter {
    	@Override public boolean accept(File f) {
	    if (f.isDirectory())
		return true;
	    String extension = getExtension(f);
	    return (extension.equals("properties"));
	}

	@Override public String getDescription() {
	    return "Properties";
	}

	private String getExtension(File f) {
	    String s = f.getName();
	    int i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1)
		return s.substring(i + 1).toLowerCase();
	    return "";
	}
    }

    //    private void initFileChooser( String startdir)
    //    {
    //        fFileChooser = new JFileChooser();
    //        fFileChooser.setFileFilter(new PropertiesFileFilter());
    //
    //        fFileChooser.setCurrentDirectory( new File( startdir));
    //        fFileChooser.setDialogTitle( "Choose resource file (no country extensions)");
    //    }

    //    private class ActionSetLanguage extends javax.swing.AbstractAction
    //    {
    //        public ActionSetLanguage()
    //        {
    //            super( "Set Language...");
    //        }
    //
    //        public void actionPerformed( ActionEvent event)
    //        {
    //            String ext = JOptionPane.showInputDialog(
    //                null,
    //                "Enter language extension (eg \"de\" or \"es_SP\")",
    //                "Enter language extension",
    //                JOptionPane.QUESTION_MESSAGE);
    //
    //            if (ext != null)
    //            {
    //                openLanguageResource( ext);
    //            }
    //        }
    //    }

    private class ActionSave extends javax.swing.AbstractAction {
	public ActionSave() {
	    super("Save");
	}

	public void actionPerformed(ActionEvent event) {
	    saveLanguageFile();
	}
    }

    private class ActionExit extends javax.swing.AbstractAction {
	public ActionExit() {
	    super("Exit");
	}

	public void actionPerformed(ActionEvent event) {
	    exitEditor();
	}
    }

    private boolean saveLanguageFile() {
	if (fLanguageList != null) {
	    try {
		FileOutputStream fos = new java.io.FileOutputStream(fLanguageFile, false);
		fLanguageList.store(fos, fLanguageFile);
		fFileNeedsSaving = false;
		return true;
	    } catch (Exception exc) {
		JOptionPane.showMessageDialog(null, "Saving language file FAILED! exc=" + exc.toString(), "BAD SAVE!",
			JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	return true;
    }

    private int confirmSaveOrLose(String langfile, String action) {
	StringBuffer sb = new StringBuffer();
	sb.append("You have unsaved changes in:\n");
	sb.append("  ");
	sb.append(langfile);
	sb.append("\n");
	sb.append("Press YES to save changes.\n");
	sb.append("Press NO to ");
	sb.append(action);
	sb.append(" without saving.\n");
	sb.append("Press CANCEL to do nothing and return to the editor.\n");
	return JOptionPane.showConfirmDialog(null, sb.toString(), "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    private void exitEditor() {
	if (fFileNeedsSaving) {
	    int option = confirmSaveOrLose(fLanguageFile, "exit");
	    if (option == JOptionPane.YES_OPTION) {
		if (saveLanguageFile()) {
		    System.exit(0);
		}
	    } else if (option == JOptionPane.NO_OPTION) {
		System.exit(0);
	    }
	} else {
	    System.exit(0);
	}
    }

    private void setShowAll(boolean b) {
	if (fShowAll != b) {
	    fShowAll = b;
	    fButtonShowAll.setEnabled(!fShowAll);
	    fButtonShowMissing.setEnabled(fShowAll);
	    updateResourceList();
	}
    }

    private void updateResourceList() {
	if (fResourceList == null)
	    return;

	List<String> keys = new ArrayList<String>(100);
	for (Object okey : fResourceList.keySet()) {
	    String key = (String) okey;
	    if (fShowAll || (fLanguageList == null || (fLanguageList.get(key) == null))) {
		keys.add(key);
	    }
	}
	Object[] array = keys.toArray();
	Arrays.sort(array);

	String oldSelected = (String) fListResourceNames.getSelectedValue();

	fListResourceNames.setListData((String[]) array);
	if (oldSelected != null && keys.contains(oldSelected)) {
	    fListResourceNames.setSelectedValue(oldSelected, true);
	}
    }

    public void actionPerformed(ActionEvent event) {
	if (event.getSource() == fComboProperties)
	    fComboProperties_actionPerformed();
	else if (event.getSource() == fComboLanguages)
	    fComboLanguages_actionPerformed();
    }

    private void fComboProperties_actionPerformed() {
	String item = (String) fComboProperties.getSelectedItem();
	if (item != null) {
	    InputStream is = null;
	    try {
		fResourceList = new SortedProperties();
		fResourceFile = fBaseDir + "/" + item + ".properties";

		
		String msg = "";

		if (new File(fResourceFile).exists()) {
		    // first look for file
		    is = new FileInputStream(fResourceFile);
		    msg = "on disk: " + fResourceFile;
		} else {
		    // next look in jar
		    is = this.getClass().getResourceAsStream(item + ".properties");
		    msg = "in jar: " + item + ".properties";
		}

		if (is != null) {
		    logger.info("Resources file found " + msg);
		    fResourceList.load(is);
		    if (fLanguage != null)
			openLanguageResource(fLanguage);
		    is.close();
		} else {
			logger.info("Unable to open stream: " + msg);
		}
		
	    } catch (Exception exc) {
	    	logger.error("Exception=" + exc.toString());
	    	exc.printStackTrace(System.err);
	    }

	    updateLanguageList();
	    updateResourceList();
	}
	updateTitle();
    }

    private void fComboLanguages_actionPerformed() {
	String item = (String) fComboLanguages.getSelectedItem();
	openLanguageResource(item);
	updateResourceList();
	updateTitle();
    }

    private void updateLanguageList() {
	String currLanguage = (String) fComboLanguages.getSelectedItem();
	fComboLanguages.removeActionListener(this);
	fComboLanguages.removeAllItems();

	String resname = (String) fComboProperties.getSelectedItem();
	boolean haveL = false;
	if (resname != null) {
	    File dirFile = new File(fBaseDir);
	    String[] filenames = dirFile.list();

	    String regex = resname + "_..\\.properties";

	    for (int i = 0; i < filenames.length; i++) {
		String name = filenames[i];
		if (name.matches(regex)) {
		    String ext = name.substring(resname.length() + 1, resname.length() + 3);
		    fComboLanguages.addItem(ext);
		    if (currLanguage != null && ext.equals(currLanguage))
			haveL = true;
		}
	    }

	}

	fComboLanguages.addActionListener(this);
	if (haveL)
	    fComboLanguages.setSelectedItem(currLanguage);
    }

    private void addListeners() {
	fComboProperties.addActionListener(this);
	fComboLanguages.addActionListener(this);

	fListResourceNames.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(ListSelectionEvent event) {
		fListResourceNames_valueChanged();
	    }
	});

	fButtonShowAll.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		fButtonShowAll_actionPerformed();
	    }
	});

	fButtonShowMissing.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		fButtonShowMissing_actionPerformed();
	    }
	});

	fTextSubValue.addFocusListener(new FocusListener() {
	    public void focusGained(FocusEvent event) {} // do nothing

	    public void focusLost(FocusEvent event) {
		fTextSubValue_valueChanged();
	    }
	});

	fDocSubValue.addDocumentListener(new DocumentListener() {
	    public void insertUpdate(DocumentEvent event) {
		fTextSubValue_valueChanged();
	    }

	    public void removeUpdate(DocumentEvent event) {
		fTextSubValue_valueChanged();
	    }

	    public void changedUpdate(DocumentEvent event) {
		fTextSubValue_valueChanged();
	    }
	});

    }

    private void fButtonShowAll_actionPerformed() {
	setShowAll(true);
    }

    private void fButtonShowMissing_actionPerformed() {
	setShowAll(false);
    }

    private void fListResourceNames_valueChanged() {
	String key = (String) fListResourceNames.getSelectedValue();
	if (key != null) {
	    fTextKey.setText(key);
	    fTextMainValue.setText((String) fResourceList.get(key));

	    if (fLanguageList != null) {
		String ll = (String) fLanguageList.get(key);

		listIsChanging = true;
		if (ll != null)
		    fTextSubValue.setText(ll);
		else
		    fTextSubValue.setText("");
		listIsChanging = false;
	    }
	}
    }

    private boolean listIsChanging = false;

    private void fTextSubValue_valueChanged() {
	if (listIsChanging)
	    return;

	String key = fTextKey.getText();
	if ((key != null) && (fLanguageList != null)) {
	    String currValue = fLanguageList.getProperty(key);
	    String newValue = fTextSubValue.getText();
	    if (currValue == null || !currValue.equals(newValue)) {
		fLanguageList.setProperty(key, newValue);
		fFileNeedsSaving = true;
	    }
	}
    }

    /**
     * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
     * specified width, height of 1, and specified anchor and fill.
     * 
     * @param target
     *            panel in which to put component
     * @param newComp
     *            component to be inserted
     * @param x
     *            x-grid location
     * @param y
     *            y-grid location
     * @param w
     *            width in cells
     * @param h
     *            height in cells
     **/
    protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int w, int h) {
	gridbagAdd(target, newComp, x, y, w, h, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, fInsets);
    }

    /**
     * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
     * specified width, height of 1, and specified anchor and fill
     * 
     * @param target
     *            panel in which to put component
     * @param newComp
     *            component to be inserted
     * @param x
     *            x-grid location
     * @param y
     *            y-grid location
     **/
    protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y) {
	gridbagAdd(target, newComp, x, y, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, fInsets);
    }

    /**
     * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
     * specified width, height of 1, and specified anchor and fill
     * 
     * @param target
     *            panel in which to put component
     * @param newComp
     *            component to be inserted
     * @param x
     *            x-grid location
     * @param y
     *            y-grid location
     * @param width
     *            width in cells
     * @param anchor
     *            anchor
     * @param fill
     *            filler
     **/
    protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int width, int anchor, int fill) {
	gridbagAdd(target, newComp, x, y, width, 1, anchor, fill, fInsets);
    }

    private GridBagConstraints gbc = new GridBagConstraints();

    /**
     * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
     * specified width, height of 1, and specified anchor and fill
     * 
     * @param target
     *            panel in which to put component
     * @param newComp
     *            component to be inserted
     * @param x
     *            x-grid location
     * @param y
     *            y-grid location
     * @param wh
     *            width in cells
     * @param ht
     *            height in cells
     * @param anchor
     *            anchor
     * @param fill
     *            filler
     * @param ins
     *            insets
     **/
    protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int wh, int ht, int anchor,
	    int fill, Insets ins) {
	gbc.gridx = x;
	gbc.gridy = y;
	gbc.gridwidth = wh;
	gbc.gridheight = ht;
	gbc.weightx = 0.0;
	gbc.weighty = 0.0;
	gbc.anchor = anchor;
	gbc.fill = fill;
	gbc.insets = ins;
	((GridBagLayout) target.getLayout()).setConstraints(newComp, gbc);
	target.add(newComp);
    }

    private Insets fInsets = new Insets(5, 5, 5, 5);

    public class SortedProperties extends Properties {
	/**
	 * oversides parent to apply Set skeys and ordered writing out
	 * 
	 * @param out
	 *            output stream
	 * @param header
	 * @throws IOException
	 *             when output stream cannout be opened
	 */
    	@Override public synchronized void store(OutputStream out, String header) throws IOException {
	    BufferedWriter awriter = new BufferedWriter(new OutputStreamWriter(out, "ISO-8859-1"));

	    if (header != null)
		writeln(awriter, "#" + header);
	    writeln(awriter, "#" + new Date().toString());

	    Set skeys = new TreeSet<Object>(keySet());

	    for (Iterator iter = skeys.iterator(); iter.hasNext();) {
		String key = (String) iter.next();
		String val = (String) get(key);
		key = saveConvert(key, true);

		/*
		 * No need to escape embedded and trailing spaces for value, hence pass false to flag.
		 */
		val = saveConvert(val, false);
		writeln(awriter, key + "=" + val);
	    }
	    awriter.flush();
	}

	/*
	 * cut & paste from private access parent Properties
	 */
	private void writeln(BufferedWriter bw, String s) throws IOException {
	    bw.write(s);
	    bw.newLine();
	}

	/*
	 * cut & paste from private access parent Properties
	 */
	private String saveConvert(String theString, boolean escapeSpace) {
	    int len = theString.length();
	    StringBuffer outBuffer = new StringBuffer(len * 2);

	    for (int x = 0; x < len; x++) {
		char aChar = theString.charAt(x);
		switch (aChar) {
		case ' ':
		    if (x == 0 || escapeSpace)
			outBuffer.append('\\');
		    outBuffer.append(' ');
		    break;
		case '\\':
		    outBuffer.append('\\');
		    outBuffer.append('\\');
		    break;
		case '\t':
		    outBuffer.append('\\');
		    outBuffer.append('t');
		    break;
		case '\n':
		    outBuffer.append('\\');
		    outBuffer.append('n');
		    break;
		case '\r':
		    outBuffer.append('\\');
		    outBuffer.append('r');
		    break;
		case '\f':
		    outBuffer.append('\\');
		    outBuffer.append('f');
		    break;
		default:
		    if ((aChar < 0x0020) || (aChar > 0x007e)) {
			outBuffer.append(aChar);
			//                            outBuffer.append('\\');
			//                            outBuffer.append('u');
			//                            outBuffer.append(toHex((aChar >> 12) & 0xF));
			//                            outBuffer.append(toHex((aChar >>  8) & 0xF));
			//                            outBuffer.append(toHex((aChar >>  4) & 0xF));
			//                            outBuffer.append(toHex( aChar        & 0xF));
		    } else {
			if (specialSaveChars.indexOf(aChar) != -1)
			    outBuffer.append('\\');
			outBuffer.append(aChar);
		    }
		}
	    }
	    return outBuffer.toString();
	}

	//        /**
	//         * Convert a nibble to a hex character
	//         * @param	nibble	the nibble to convert.
	//         * @return hex character
	//         */
	//        private char toHex(int nibble)
	//        {
	//            return hexDigit[(nibble & 0xF)];
	//        }

	//        /** A table of hex digits */
	//        private final char[] hexDigit = {
	//        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	//        };

	private final String specialSaveChars = "=: \t\r\n\f#!";

    }
}
/**
 * $Log: ResourceLanguageEditor.java,v $ Revision 1.5 2006/01/15 21:10:35 sandyg resubmit at 5.1.02
 * 
 * Revision 1.3 2006/01/11 02:55:26 sandyg updating JRE check to Java 5.0
 * 
 * Revision 1.2 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.10 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.9 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.8 2003/11/23 20:34:52 sandyg starting release 4.2, minor cleanup
 * 
 * Revision 1.7 2003/07/10 01:57:18 sandyg Added jdk1.4 runtime check as this is needs 1.4
 * 
 * Revision 1.6 2003/05/18 14:52:04 sandyg updated for easier use
 * 
 * Revision 1.5 2003/05/07 01:17:05 sandyg removed unneeded method parameters
 * 
 * Revision 1.4 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.3 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
