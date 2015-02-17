// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: Util.java,v 1.6 2006/01/15 21:10:35 sandyg Exp $
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.borland.dbswing.IntlSwingSupport;

/**
 * all static class of generic utilities
 **/
public class Util {
	public final static String ADD_ICON = "/images/Add16.gif";
	public final static String DELETE_ICON = "/images/Delete16.gif";
	public final static String HELP_ICON = "/images/Help16.gif";
	public final static String ASCENDSORT_ICON = "/images/AscendSort.gif";
	public final static String DESCENDSORT_ICON = "/images/DescendSort.gif";
	public final static String OPEN_ICON = "/images/Open24.gif";
	public final static String EDIT_ICON = "/images/Edit24.gif";
	public final static String ROWINSERTBEFORE_ICON = "/images/RowInsertBefore16.gif";
	public final static String ROWDELETE_ICON = "/images/RowDelete16.gif";
	public final static String FIND_ICON = "/images/Find16.gif";

	private static final String BUNDLE_NAME = "GeneralProperties";

	public static final String NEWLINE = System.getProperty("line.separator");

	static ResourceBundle resUtil = null;
	public static IntlSwingSupport sIntlSwingSupport = null;
	private static Locale sLocale;

	private static String sWorkingDirectory = null;
	private static String sDefaultStartupDirectory = System.getProperty("user.dir");

	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	public static File getFile(String endName) {
		if (endName.startsWith("/")) endName = endName.substring(1);
		File file = getFile( getWorkingDirectory(), endName);
		if (file.exists()) return file;
				
		if (sTesting) {
			// look in maven's test-classes directory
			file = getFile( getWorkingDirectory() + "target/test-classes/", endName);
			if (file.exists()) return file;
			file = getFile( getWorkingDirectory() + "target/classes/", endName);
			if (file.exists()) return file;
		}
		return file;
	}

	public static File getFile(String dir, String file) {
		return new File(dir, file);
	}

	public static void setWorkingDirectory(String dir) {
		if (dir.endsWith("/"))
			dir = dir.substring(0, dir.length() - 1);
		sWorkingDirectory = dir;
		File wdir = new File(sWorkingDirectory);
		if (!wdir.exists()) {
			wdir.mkdirs();
		}
		sWorkingDirectory = sWorkingDirectory + "/";
	}

	/**
	 * Returns the directory from which the current VM was launched.
	 * 
	 * @return VM launch directory
	 */
	public static String getWorkingDirectory() {
		if (sWorkingDirectory == null) {
			setWorkingDirectory(sDefaultStartupDirectory);
			logger.info("Setting initial working directory to: {}", sWorkingDirectory);
		}
		return sWorkingDirectory;
	}

	public static Locale getLocale() {
		return sLocale;
	}

	public static void initLocale(String value) {
		Locale ll = null;
		if (value == null || value.equals(""))
			ll = Locale.getDefault();
		else
			ll = Util.stringToLocale(value);
		Util.initLocale(ll);
	}

	public static void initLocale(Locale locale) {
		if (sLocale != null && sLocale.equals(locale))
			return;
		logger.debug("Initializing locale to {}", locale);

		sLocale = locale;
		Locale.setDefault(locale);
		resUtil = null;
		getResources();
		if (sIntlSwingSupport == null)
			sIntlSwingSupport = new IntlSwingSupport();
		sIntlSwingSupport.setLocale(locale);
	}

	public static ResourceBundle getResources() {
		if (resUtil == null) {
			Locale defLocale = Locale.getDefault();
			if (sLocale == null)
				initLocale(defLocale);
			resUtil = ResourceBundle.getBundle(BUNDLE_NAME, sLocale);
		}
		return resUtil;
	}

	public static final boolean DO_TRACE = true;
	public static final boolean NO_TRACE = false;

	public static JOptionPane sConfirmDialog = null;

	public static String sDumpTitle = null;

	public static void setDumpTitle(String title) {
		sDumpTitle = title;
	}

	public static BaseObjectModel sDumpObject = null;

	public static void setDumpObject(BaseObjectModel object) {
		sDumpObject = object;
	}

	/**
	 * Pops up a confirmation dialog that requires user to type the word Yes into a text field in order to confirm the
	 * question asked.
	 * 
	 * @param message
	 *           The message to be asked of the user. Should be worded for a yes/no answer.
	 * @returns true if user replied 'yes' or false if they gave any other answer
	 */
	public static boolean confirm(String message) {
		return confirm(message, true);
	}

	/**
	 * Pops up a confirmation dialog that requires user to type the word Yes into a text field in order to confirm the
	 * question asked.
	 * 
	 * @param message
	 *           The message to be asked of the user. Should be worded for a yes/no answer.
	 * @param typeYesNo
	 *           When true, will force the user to actually type "Yes" intead of simply hitting a Yes button
	 * @returns true if user replied 'yes' or false if they gave any other answer
	 */
	public static boolean confirm(String message, boolean typeYesNo) {
		if (typeYesNo) {
			StringBuffer fullMsg = new StringBuffer(message);
			fullMsg.append(NEWLINE);
			fullMsg.append(NEWLINE);
			fullMsg.append(resUtil.getString("YesNoMessage"));
			String answer = javax.swing.JOptionPane.showInputDialog(null, fullMsg.toString(), resUtil
					.getString("YesNoTitle"), javax.swing.JOptionPane.QUESTION_MESSAGE);
			return (answer != null && answer.equalsIgnoreCase(resUtil.getString("Yes")));
		} else {
			int answer = javax.swing.JOptionPane.showConfirmDialog(null, message, resUtil.getString("YesNoTitle"),
					javax.swing.JOptionPane.YES_NO_OPTION);
			return (answer == javax.swing.JOptionPane.YES_OPTION);
		}
	}

	/**
	 * Shows an error message from a source object including a stack trace
	 * 
	 * @param source
	 *           the object generatatin the error
	 * @param message
	 *           the string of the error message
	 */
	public static void showError(Object source, String message) {
		showError(source, message, DO_TRACE, null);
	}

	/**
	 * Shows an error message from a source exception, includes an option to display (or not) a stack trace
	 * 
	 * @param source
	 *           the exception to be displayed
	 * @param wantTrace
	 *           a boolean true if a stack trace should be displayed, false if not
	 */
	public static void showError(Exception source, boolean wantTrace) {
		showError(source, "Exception", wantTrace, null);
	}

	/**
	 * Shows an error message from a source object, with a title message and an option to display (or not) a stack trace
	 * 
	 * @param source
	 *           the exception or object that is the source of the error
	 * @param message
	 *           the string of the error message
	 * @param wantTrace
	 *           a boolean true if a stack trace should be displayed, false if not
	 */
	public static void showError(Object source, String message, boolean wantTrace) {
		showError(source, message, wantTrace, true, sDumpObject, sDumpTitle, null);
	}

	/**
	 * Shows an error message from a source object, with a title message and an option to display (or not) a stack trace
	 * 
	 * @param source
	 *           the exception or object that is the source of the error
	 * @param message
	 *           the string of the error message
	 * @param wantTrace
	 *           a boolean true if a stack trace should be displayed, false if not
	 * @param secondStack
	 *           a second stack trace to be added to the errors
	 */
	public static void showError(Object source, String message, boolean wantTrace, Throwable secondStack) {
		showError(source, message, wantTrace, true, sDumpObject, sDumpTitle, secondStack);
	}

	private static boolean sTesting = false;

	public static void setTesting(boolean b) {
		sTesting = b;
	}

	/**
	 * Shows an error message from a source object, with a title message and an option to display (or not) a stack trace
	 * 
	 * @param source
	 *           the exception or object that is the source of the error
	 * @param message
	 *           the string of the error message
	 * @param wantTrace
	 *           a boolean true if a stack trace should be displayed, false if not
	 * @param dumpOption
	 *           boolean, when true creates a "core dump"
	 * @param dumpObject
	 *           when dumpOption is true, this is an optional Object whose contents will
	 * @param dumpTitle
	 *           a title string, expected to be a program name and version
	 * @param secondStack
	 *           a second stack to be shown in error message be included in the "core dump"
	 */
	private static void showError(Object source, String message, boolean wantTrace, boolean dumpOption,
			Object dumpObject, String dumpTitle, Throwable secondStack) {
		
		if (sTesting) {
			String errMessage = logError(source, message, secondStack);
			throw new java.lang.RuntimeException(errMessage);
		} else {
    		JPanel jp = createErrorPanel(source, message, wantTrace, secondStack);
    
    		if (dumpOption) {
    			String dumpMessage = createDumpString( source,  message,  dumpObject,  dumpTitle);
    			JTextPane tp2 = new JTextPane();
    			tp2.setText( dumpMessage);
    			tp2.setBackground(jp.getBackground());
    			jp.add(tp2, BorderLayout.SOUTH);
    		}
    
    		jp.setPreferredSize(new java.awt.Dimension(500, 350));
    		JOptionPane.showMessageDialog(null, jp, "Error encountered", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static String createDumpString(Object source, String message, Object dumpObject, String dumpTitle) {
		try {
			String dumpFileName = getWorkingDirectory() + "/core" + Long.toString(System.currentTimeMillis())
					+ ".txt";
			FileOutputStream fos = new FileOutputStream(dumpFileName);
			PrintWriter fw = new PrintWriter(fos);

			fw.println(message);
			fw.print("received from ");
			if (source != null)
				fw.println(source.toString());
			else
				fw.println("<null source>");
			fw.println();

			if (dumpTitle != null) {
				fw.println(dumpTitle);
				fw.println();
			}

			fw.println("Stack:");
			Throwable t = null;
			if (source instanceof Throwable) {
				t = (Throwable) source;
			} else {
				t = new Throwable();
			}
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			fw.println(sw.toString());
			fw.println();
			fw.flush();

			System.getProperties().store(fos, "System.properties");
			fos.flush();

			if (dumpObject != null) {
				fw.println();
				fw.print("<<start:");
				fw.print(getObjectIdString(dumpObject));
				fw.println(">>");

				if (dumpObject instanceof BaseObject) {
					((BaseObject) dumpObject).xmlWriteToWriter(fw, "BaseObject");
				} else {
					fw.flush();

					ObjectOutputStream os = new ObjectOutputStream(fos);
					os.writeObject(dumpObject);
					os.flush();
					fos.flush();
				}

				fw.println();
				fw.print("<<end:");
				fw.print(getObjectIdString(dumpObject));
				fw.println(">>");
			}
			fw.flush();
			fw.close();
			fos.flush();
			fos.close();

			String dumpMessage = MessageFormat.format(resUtil.getString("DumpMessage"), new Object[] { dumpFileName });
			return dumpMessage;
		} catch (Exception ex) {
			return "Exception trying to create core file:\n" + ex;
		}
	}

	private static JPanel createErrorPanel(Object source, String message, boolean wantTrace, Throwable secondStack) {
		StringWriter w = new StringWriter();
		w.write(message);
		if (source != null) {
			w.write("\n\nreceived from\n\n");
			w.write(source.toString());
		}

		JTextPane tp = new JTextPane();
		tp.setText(w.toString());

		JScrollPane jpMessage = new JScrollPane(tp, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel jp = new JPanel(new BorderLayout(0, 0));

		if (wantTrace) {
			StringWriter w2 = new StringWriter();
			Throwable t = null;
			if (source instanceof Throwable) {
				t = (Throwable) source;
			} else {
				t = new Throwable();
			}
			t.printStackTrace(new PrintWriter(w2));

			if (secondStack != null) {

				w2.write("\n\n");
				w2.write("Second stack: \n\n");
				secondStack.printStackTrace(new PrintWriter(w2));
			}

			logger.error(w2.toString());
			JTextPane tp2 = new JTextPane();
			tp2.setText(w2.toString());

			JScrollPane jpStack = new JScrollPane(tp2, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			JTabbedPane tabs = new JTabbedPane();
			tabs.add("Error", jpMessage);
			tabs.add("Stack", jpStack);

			jp.add(tabs, BorderLayout.CENTER);
		} else {
			jp.add(jpMessage, BorderLayout.CENTER);
		}
		return jp;
	}

	private static String logError(Object source, String message, Throwable secondStack) {
		StringBuffer sb = new StringBuffer();
		sb.append("showError exception from: ");
		sb.append(source);
		sb.append(", message=");
		sb.append(message);

		if (secondStack != null)
			secondStack.printStackTrace();

		String errMessage = sb.toString();
		logger.error( errMessage);
		return errMessage;
	}

	public static void createAndShowErrorPanel( String title, Map<String,String> messages) {
		JPanel jp = new JPanel(new BorderLayout(0, 0));
		jp.setPreferredSize(new java.awt.Dimension(640, 480));
		
		JTabbedPane tabs = null;
		if (messages.size() > 1) {
			tabs = new JTabbedPane();
			jp.add(tabs);
		}

		for  (String key : messages.keySet()) {
			JTextPane tp = new JTextPane();
			tp.setText( messages.get(key));
			
			JScrollPane jpMessage = new JScrollPane(tp, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			if (messages.size() > 1) {
				tabs.add( key, jpMessage);
			} else {
				jp.add( jpMessage);
			}
    	}

		JOptionPane.showMessageDialog(null, jp, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * quick and dirty error message to standard output.
	 * 
	 * @param source
	 *           the source of the problem
	 * @param e
	 *           the exception encountered
	 * @param doTrace
	 *           true if you want a stack trace
	 */
	public static void printlnException(Object source, Exception e, boolean doTrace) {
		logger.error( "{} encountered in {}", e.toString(), source.toString());
		if (doTrace)
			showStack(e);
	}

	/**
	 * prints a stack trace of a Throwable to standard output
	 * 
	 * @param t
	 *           the Throwable instance to trace
	 */
	public static void showStack(Throwable t) {
		logger.error( "Error t=" + t.toString(), t);
	}

	/**
	 * prints a stack trace of current location (creates a Throwable() on the fly)
	 */
	public static void showStack() {
		showStack(new Throwable());
	}

	/**
	 * given any component, returns a location for the top left corner of the component that will center it on the
	 * screen
	 * 
	 * @param comp
	 *           the Component to find a center for
	 * @return Point the point at which component would be centered
	 */
	public static Point getLocationToCenterOnScreen(Component comp) {
		Dimension screenDim = comp.getToolkit().getScreenSize();
		Dimension compDim = comp.getSize();
		return new Point(Math.max(0, (screenDim.width - compDim.width) / 2), Math.max(0,
				(screenDim.height - compDim.height) / 2));
	}

	/**
	 * Useful for debugging output, returns a String containing the class and hashcode of the specified object. The
	 * class name has the SEA package prefix stripped off it if it begins with that.
	 * 
	 * @param obj
	 * @return a string describing object's address and class name
	 **/
	public static String getObjectIdString(Object obj) {
		String retval;
		if (obj == null) {
			retval = "<null>";
		} else {
			retval = obj.getClass().getName() + "/" + obj.hashCode();
		}
		return retval;
	}

	private static Map<String, ImageIcon> sLoadedImages = new HashMap<String, ImageIcon>();

	private static JButton dummyButton = new JButton();

	public static final ImageIcon getImageIcon(String inName) {
		return getImageIcon(dummyButton, inName);
	}

	public static final ImageIcon getImageIcon(Component srcObject, String inName) {
		ImageIcon icon = sLoadedImages.get(inName);
		if (icon != null)
			return icon;

		Image img = findResourceImage(srcObject, inName);
		if (img == null)
			return null;

		icon = new ImageIcon(img);
		sLoadedImages.put(inName, icon);
		return icon;
	}

	/**
	 * Finds and loads an image based on a string input name. Looks first in JAR file, if any, then on local disk, then
	 * local file
	 * 
	 * @param srcObject
	 *           component in which to load the image
	 * @param inName
	 *           the target filename of the image
	 * @return the Image after loading it
	 **/
	public static Image findResourceImage(Component srcObject, String inName) {
		URL u = findResourceUrl(srcObject, inName);
		return loadImage(srcObject, u);
	} // of findImage

	/**
	 * Finds a target URL. Looks first in JAR file, if any, then on local disk or classpath
	 * 
	 * @param srcObject
	 *           src object, used for class loader base
	 * @param inName
	 *           the target filename of the image
	 * @return the Image after loading it
	 **/
	public static URL findResourceUrl(Object srcObject, String inName) {
		boolean haveLocalAccess = true;  // originally written for servlets, but here always have local access
		URL retUrl = null;

		// first try the Jar file:
		try {
			retUrl = srcObject.getClass().getResource(inName);
		} catch (Exception e) {}

		if ((retUrl == null) && (haveLocalAccess)) // not found in jar, try local disk
		{
			try {
				String fName = null;
				if (inName.startsWith("/")) 
					fName = "." + inName;  // was 'filename', should now be './filename'
				else
					fName = inName;
				retUrl = ClassLoader.getSystemResource(fName);
			} catch (Exception e) {}
		}

		return retUrl;
	} // of findResource

	/**
	 * tries to load image from Url. Returns null if input Url is null, or image cannot be loaded
	 * 
	 * @param srcObject
	 *           component in which to load the image
	 * @param inUrl
	 *           the target URL of the image
	 * @return the Image after loading it
	 * @exception IllegalArgumentException
	 *               if inComp is null
	 **/
	private static Image loadImage(Component srcObject, URL inUrl) {
		try {
		    return ImageIO.read(inUrl);
		} catch (IOException e) {
			showError( e, true);
			return null;
		}
	}

	/**
	 * finds a parent frame for the specified container. If none is found it invents one. Useful for creating new
	 * dialogs that insist on having a parent
	 * 
	 * @param theFrame
	 *           the container whose parent is sought
	 * @return the parent Frame of the container
	 */
	public static Frame getParentFrame(Container theFrame) {
		do {
			theFrame = theFrame.getParent();
		} while ((theFrame != null) && !(theFrame instanceof Frame));
		if (theFrame == null)
			theFrame = new Frame();
		return (Frame) theFrame;
	}

	/**
	 * finds a parent JFrame for the specified container. If none is found it invents one. Useful for creating new
	 * dialogs that insist on having a parent
	 * 
	 * @param theFrame
	 *           the container whose parent is sought
	 * @return the parent JFrame of the container
	 */
	public static JFrame getParentJFrame(Container theFrame) {
		do {
			theFrame = theFrame.getParent();
		} while ((theFrame != null) && !(theFrame instanceof JFrame));
		if (theFrame == null)
			theFrame = new JFrame();
		return (JFrame) theFrame;
	}

	/**
	 * compares to hashtables.. for .equals().. I suppose it would e more proper to subclass the hashtable and add an
	 * equals() to it but I really hate to have to custom subclass such a common utility
	 * 
	 * @param left
	 *           one of the two hashtables
	 * @param right
	 *           the other hashtable
	 * @return true if equal in both length and content, false otherwise
	 **/
	public static boolean hashtableEquals(Hashtable left, Hashtable right) {
		if (left == right)
			return true;
		if (left.size() != right.size())
			return false;

		Enumeration elkeys = left.keys();

		while (elkeys.hasMoreElements()) {
			Object lkey = elkeys.nextElement();

			if (!right.containsKey(lkey))
				return false; // right does have key that left has
			if (!right.get(lkey).equals(left.get(lkey)))
				return false; // right's object not equal left's object
		}

		// do get here all keys were found, all matching values were .equals()
		return true;

	}

	public static boolean equalsWithNull(Object left, Object right) {
		try {
			return left.equals(right);
		} catch (NullPointerException e) {
			return (right == null);
		}
	}

	public static boolean equalsIgnoreCaseWithNull(String left, String right) {
		try {
			return left.equalsIgnoreCase(right);
		} catch (NullPointerException e) {
			return (right == null);
		}
	}

	public static <T extends Comparable<T>> int compareWithNull(T left, T right) {
		if (left == null)
			if (right == null)
				return 0;
			else
				return 1;
		else if (right == null)
			return -1;
		else
			return left.compareTo(right);
	}

	/**
	 * checks the executing version of Java, displays a warning message if the version is less than the specified
	 * argument a Warning and suggestion to upgrade will be displayed
	 * 
	 * @param minVersion
	 *           the minimum version to be allowed without a warning
	 */
	public static void checkJreVersion(String minVersion, String longName) {
		String vmVersion = System.getProperty("java.vm.version");
		if (vmVersion.compareTo(minVersion) < 0) {
			// version is less than desired, show warning box
			String s = MessageFormat.format(resUtil.getString("VMWarningMessage"), new Object[] { vmVersion,
				minVersion, longName });
			JOptionPane.showMessageDialog(null, s, resUtil.getString("VMWarningTitle"), JOptionPane.WARNING_MESSAGE);
		}
	}

	public static double parseDouble(String inStr) {
		try {
			NumberFormat nf = NumberFormat.getInstance(sLocale);
			Number n = nf.parse(inStr);
			return n.doubleValue();
		} catch (ParseException e) {
			return Double.parseDouble(inStr);
		}
	}

	/**
	 * Formats a double value to specified number of decimal places runs through the internationalized NumberFormat, so
	 * may NOT do scientific notation
	 * 
	 * @param inVal
	 *           the input value
	 * @param inDecs
	 *           the number of decimals places to be displayed
	 * @return the string of the double
	 */
	public static String formatDouble(double inVal, int inDecs) {
		return formatDouble(inVal, inDecs, 14);
	}

	/**
	 * Formats a double value to specified number of decimal places runs through the internationalized NumberFormat, so
	 * may NOT do scientific notation
	 * 
	 * @param inVal
	 *           double number to be formatted
	 * @param inDecs
	 *           integer of number of decimal places
	 * @param inLeftOfDec
	 *           integer of max number of places to left of decimal
	 * @return the string of the double
	 */
	public static String formatDouble(double inVal, int inDecs, int inLeftOfDec) {
		String returnVal = "";
		if (Double.isInfinite(inVal)) {
			returnVal = INFINITY;
		} else if (Double.isNaN(inVal)) {
			returnVal = NAN;
		} else {
			NumberFormat nf = (sLocale == null) ? NumberFormat.getInstance() : NumberFormat.getInstance(sLocale);
			nf.setMaximumIntegerDigits(inLeftOfDec);
			nf.setMinimumFractionDigits(inDecs);
			nf.setGroupingUsed(false);
			returnVal = nf.format(inVal);
		}

		return returnVal;
	}

	private static String INFINITY = Double.toString(Double.POSITIVE_INFINITY);
	private static String NAN = Double.toString(Double.NaN);

	// returns log based 10
	public static double log10(double inVal) {
		return Math.log(inVal) / Math.log(10);
	}

	public static double pow10(int n) {
		if (n == 0)
			return 1;
		else if (n > 0) {
			double d = 1;
			for (int i = 0; i < n; i++)
				d = d * 10.;
			return d;
		} else {
			double d = 1;
			for (int i = 0; i > n; i--)
				d = d / 10.;
			return d;
		}
	}

	/**
	 * reads line from reader file and parses it on a delimiter and returns array of cells. Generally used to split a
	 * comma separated set of strings into individual components
	 * 
	 * @param line
	 *           the line to be split
	 * @param delimiter
	 *           the delimiter on which to split the string
	 * @return array of split string elements
	 */
	public static List<String> stringSplit(String line, String delimiter) {
		return stringSplit(line, delimiter, null);
	}

	/**
	 * reads line from reader file and parses it on a delimiter and returns array of cells. Generally used to split a
	 * comma separated set of quotes into individual components with the quotes removed
	 * 
	 * @param line
	 *           the line to be split
	 * @param delimiter
	 *           the delimiter on which to split the string
	 * @param trimmer
	 *           to trim from each end of a string segment
	 * @return array of split string elements
	 */
	public static List<String> stringSplit(String line, String delimiter, String trimmer) {
		List<String> v = new ArrayList<String>(10);
		int left = 0;
		int tloc = 0;
		while ((tloc = line.indexOf(delimiter, left)) >= 0) {
			String cell = trim(line.substring(left, tloc), trimmer);
			v.add(cell);
			left = tloc + 1;
		}

		if (left < line.length() - 1) {
			// have string after last delim
			String cell = trim(line.substring(left, line.length()), trimmer);
			v.add(cell);
		}

		return v;
	}

	/**
	 * replaces all occurences of 'key' in 'origString' with 'newName'.
	 * 
	 * @param origString
	 *           the original string
	 * @param key
	 *           the substring to be replaced
	 * @param newName
	 *           the string to replace all occurences of 'key' with
	 * @return the revised String
	 */
	public static String stringReplace(String origString, String key, String newName) {
		int rloc = origString.indexOf(key);
		if (rloc < 0)
			return origString;

		StringBuffer sb = new StringBuffer(origString.length() + newName.length());

		int lloc = 0;
		while (rloc > 0) {
			sb.append(origString.substring(lloc, rloc));
			sb.append(newName);
			lloc = rloc + key.length();
			rloc = origString.indexOf(key, lloc);
		}
		sb.append(origString.substring(lloc));
		String newString = sb.toString();
		return newString;
	}

	public static Locale stringToLocale(String s) {
		List<String> pieces = Util.stringSplit(s, "_");
		Locale ll = null;
		if (pieces.size() == 1) {
			ll = new Locale(pieces.get(0), "");
		} else if (pieces.size() == 2) {
			ll = new Locale(pieces.get(0), pieces.get(1));
		} else if (pieces.size() == 3) {
			ll = new Locale(pieces.get(0), pieces.get(1), pieces.get(2));
		} else {
			ll = Locale.getDefault();
		}
		return ll;
	}

	public static final String NONAME = "noname";

	public static String makeUnicodeIdentifier(String longName) {
		if (longName == null)
			return NONAME;
		String s = longName.trim();
		if (s.length() == 0)
			return NONAME;

		s = s.trim();
		StringBuffer newName = new StringBuffer(s.length());
		for (char c : s.toCharArray()) {
			if (Character.isUnicodeIdentifierPart(c))
				newName.append(c);
		}
		return newName.toString();
	}

	public static String trim(String orig, String trimmer) {
		String cell = orig;
		if (trimmer == null)
			return cell;

		if (cell.startsWith(trimmer))
			cell = cell.substring(trimmer.length(), cell.length());
		if (cell.endsWith(trimmer))
			cell = cell.substring(0, cell.length() - trimmer.length());
		return cell;
	}

	private static boolean confirmFileReplace(File f) {
		int option = JOptionPane.showConfirmDialog(null, MessageFormat.format(resUtil.getString("GenOverwriteMessage"),
				new Object[] { f.getPath() }), resUtil.getString("OverwriteTitle"), JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			f.delete();
			return true;
		} else {
			return false;
		}
	}

	private static JFileChooser sFileChooser = null;
	private static Frame sChooserFrame = null;

	public static String selectFile(String filename, FileFilter filter, String dialogTitle,
			boolean mustExist, boolean confirmOverwrite) {

		return selectFile(filename, new FileFilter[] { filter }, dialogTitle, mustExist,
				confirmOverwrite);
	}

	public static FileFilter getLastFilter() {
		return ((sFileChooser == null) ? null : sFileChooser.getFileFilter());
	}

	public static String selectFile(String filename, FileFilter[] filters, String dialogTitle,
			boolean mustExist, boolean confirmOverwrite) {

		String startDirectory = Util.getWorkingDirectory();
		
		if (sFileChooser == null) {
			sFileChooser = new JFileChooser();
			sChooserFrame = new Frame();
		}

		sFileChooser.setCurrentDirectory(new File(startDirectory));
		sFileChooser.setSelectedFile(null);
		sFileChooser.setDialogTitle(dialogTitle);
		if (filters.length > 0) {
			sFileChooser.setAcceptAllFileFilterUsed(false);
			sFileChooser.resetChoosableFileFilters();
			for (FileFilter f : filters) {
				sFileChooser.addChoosableFileFilter(f);
			}
			sFileChooser.setFileFilter(filters[0]);
		}

		int result = 0;
		// Create default filename for saving
		sFileChooser.setSelectedFile(new File(filename));
		result = sFileChooser.showSaveDialog(sChooserFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			String fileName = sFileChooser.getSelectedFile().getName();
			String directory = sFileChooser.getSelectedFile().getParent() + "/";

			if (fileName != null) {
				File selection = new File(directory, fileName);
				if (selection.exists()) {
					if (confirmOverwrite && !Util.confirmFileReplace(selection)) {
						return null;
					}
				} else {
					if (mustExist)
						return null;
				}
				return directory + fileName;
			}
			return null;
		} else {
			return null;
		}
	}

}
/**
 * $Log: Util.java,v $ Revision 1.6 2006/01/15 21:10:35 sandyg resubmit at 5.1.02
 * 
 * Revision 1.4 2006/01/11 02:55:26 sandyg updating JRE check to Java 5.0
 * 
 * Revision 1.3 2006/01/11 02:27:14 sandyg updating copyright years
 * 
 * Revision 1.2 2006/01/11 02:17:15 sandyg Bug fixes relative to qualify/final race scoring
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.17.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.17.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.17.2.3 2005/08/14 21:46:01 sandyg Helps in non-English working again
 * 
 * Revision 1.17.2.2 2005/08/13 21:57:06 sandyg Version 4.3.1.03 - bugs 1215121, 1226607, killed Java Web Start startup
 * code
 * 
 * Revision 1.17.2.1 2005/06/26 22:47:22 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.17 2005/06/05 12:28:27 sandyg Added SUBVERSION for tracking patches
 * 
 * Revision 1.16 2005/05/26 01:45:43 sandyg fixing resource access/lookup problems
 * 
 * Revision 1.15 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.14 2004/04/10 20:49:39 sandyg Copyright year update
 * 
 * Revision 1.13 2003/11/23 23:14:28 sandyg upgraded to j2se 1.4.2, uses built in XML now
 * 
 * Revision 1.12 2003/04/27 21:03:30 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.11 2003/04/20 15:43:59 sandyg added javascore.Constants to consolidate penalty defs, and added new
 * penaltys TIM (time value penalty) and TMP (time percentage penalty)
 * 
 * Revision 1.10 2003/03/28 03:07:51 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.9 2003/01/05 21:16:34 sandyg regression unit testing following rating overhaul from entry to boat
 * 
 * Revision 1.8 2003/01/04 17:53:05 sandyg Prefix/suffix overhaul
 * 
 */
