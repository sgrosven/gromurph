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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;
import org.gromurph.util.BaseObjectModel;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * reads/stores and manages a list of "global" preferences, this is a singleton class
 */
public class JavaScoreProperties extends Properties implements BaseObjectModel {

	private static JavaScoreProperties sInstance = new JavaScoreProperties();
	private static Logger logger = LoggerFactory.getLogger(JavaScoreProperties.class);
	
	public static JavaScoreProperties getProperties() {
		return sInstance;
	}
	
	private JavaScoreProperties() {
		super();
		try {
			sFile = Util.getFile(PROPERTIES);
			FileInputStream inStream = new FileInputStream(sFile);
			load(inStream);
			inStream.close();
		}
		catch (FileNotFoundException e) {
			this.clear();
			this.put(RSA_PROPERTY, "CBYRA");
			this.put(MNA_PROPERTY, "USSA #");
			save_();
		}
		catch (IOException e2) {
			Util.showError(this,
					"Unable to read the '" + PROPERTIES + "' file" + Util.NEWLINE + Util.NEWLINE + e2.toString(), true);
		}
	}

	private Regatta fRegatta;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public static URI getRegattaReportUri() {
		return JavaScore.getInstance().fReportViewer.getRegattaReportUri();
	}
	public static String getVersion() {
		return JavaScoreVersion.VERSION;
	}

	public static String getRelease() {
		return JavaScoreVersion.RELEASE;
	}

	public static final String BASE_BUNDLE = "JavaScore";

	static ResourceBundle res = null;
	static ResourceBundle resUtil = null;

	public static synchronized ResourceBundle getResources() {
		if (res == null) {
			res = ResourceBundle.getBundle(BASE_BUNDLE);
			resUtil = Util.getResources();
		}
		return res;
	}

	protected static final String PROPERTIES = "javascore.ini";
	private static File sFile;

	public static final String RSA_PROPERTY = "RsaName";
	public static final String MNA_PROPERTY = "MnaName";
	public static final String LOOKANDFEEL_PROPERTY = "LookAndFeel";
	public static final String LOCALE_PROPERTY = "Locale";
	public static final String AFACTOR_PROPERTY = "AFactor";
	public static final String BFACTOR_PROPERTY = "BFactor";

	public static final String RECENTBASE_PROPERTY = "Regatta";
	public static final String RECENTDIRBASE_PROPERTY = "RegattaDir";
	public static final String RECENT1_PROPERTY = RECENTBASE_PROPERTY + "1";
	public static final String RECENT2_PROPERTY = RECENTBASE_PROPERTY + "2";
	public static final String RECENT3_PROPERTY = RECENTBASE_PROPERTY + "3";
	public static final String RECENT1DIR_PROPERTY = RECENTDIRBASE_PROPERTY + "1";
	public static final String RECENT2DIR_PROPERTY = RECENTDIRBASE_PROPERTY + "2";
	public static final String RECENT3DIR_PROPERTY = RECENTDIRBASE_PROPERTY + "3";
	
	private static Lock sScoringLock = new ReentrantLock();

	public static void acquireScoringLock() {
		sScoringLock.lock();
	}

	public static void releaseScoringLock() {
		sScoringLock.unlock();
	}

	public static final String REGATTA = "properties.regatta";

	public static void setRegatta(Regatta newValue) { 
		if (newValue == null) {
			logger.info("$$$$ Null Regatta ignored, creating empty regatta");
			sInstance.setRegatta_( new Regatta());
		} else {
			sInstance.setRegatta_(newValue); 
		}
	}
	
	private void setRegatta_(Regatta newValue) {
		Object oldValue = fRegatta;
		if (fRegatta == newValue) return;
		
		fRegatta = newValue;
		Util.setDumpObject(fRegatta);
		
		pcs.firePropertyChange( REGATTA, oldValue, newValue);
	}

	public static Regatta getRegatta() {
		if (sInstance.fRegatta == null) sInstance.fRegatta = new Regatta();
		return (sInstance == null) ? null : sInstance.fRegatta;
	}

	/** JavaHelp HelpSet for the */
	public static final String HELP_SET = "/help/JavaScore.hs";

	public final static String PROTESTFLAG_ICON = "/images/dukepenalty16.gif";

	/** Help ID for the root of the ETC help tree */
	public static final String HELP_ROOT = "javascore";

	public final static String FINISHES_ICON = "/images/Finishes24.gif";

	/**
	 * provides defaults to standard properties
	 * 
	 * @param prop
	 *            name of property to retrieve
	 * @return the string for the property
	 */
	public static String getPropertyValue( String prop) {
		return sInstance.getProperty(prop);
	}
	
	public static void setPropertyValue( String prop, String value) { sInstance.setProperty(prop, value); }
	@Override public String getProperty(String prop) {
		String ret = super.getProperty(prop);
		if (ret == null) {
			if (prop.equalsIgnoreCase(RSA_PROPERTY)) ret = "Rsa#";
			else if (prop.equalsIgnoreCase(MNA_PROPERTY)) ret = "Mna#";
			else ret = "";
		}
		return ret;
	}

	public boolean isBlank() {
		return false;
	}

	public static boolean save() { return sInstance.save_();}
	private boolean save_() {
		try {
			FileOutputStream outStream = new FileOutputStream(sFile);
			store(outStream, "JavaScore Properties");
			outStream.flush();
			outStream.close();
			return true;
		}
		catch (IOException e2) {
			Util.showError(this, "Unable to save the '" + PROPERTIES + "' file\n\n" + e2.toString(), true);
			return false;
		}
	}

	public static void pushRegattaFile(String regdir, String regfile) {
		sInstance.pushRegattaFile_(regdir, regfile);
	}
	private void pushRegattaFile_(String regdir, String regfile) {
		if (regfile == null) return;
		if (regfile.equals(getProperty(RECENT2_PROPERTY))) {
			// leave 3rd item alone, bump 1 into 2, add 1
			setProperty(RECENT2_PROPERTY, getProperty(RECENT1_PROPERTY));
			setProperty(RECENT1_PROPERTY, regfile);
			setProperty(RECENT2DIR_PROPERTY, getProperty(RECENT1DIR_PROPERTY));
			setProperty(RECENT1DIR_PROPERTY, regdir);
			save_();
		} else if (regfile.equals(getProperty(RECENT1_PROPERTY))) {
			// dont need to do anything
		} else {
			setProperty(RECENT3_PROPERTY, getProperty(RECENT2_PROPERTY));
			setProperty(RECENT2_PROPERTY, getProperty(RECENT1_PROPERTY));
			setProperty(RECENT1_PROPERTY, regfile);
			setProperty(RECENT3DIR_PROPERTY, getProperty(RECENT2DIR_PROPERTY));
			setProperty(RECENT2DIR_PROPERTY, getProperty(RECENT1DIR_PROPERTY));
			setProperty(RECENT1DIR_PROPERTY, regdir);
			save_();
		}
	}

	public static boolean haveCustomABFactors() { return sInstance.haveCustomABFactors_(); }
	private boolean haveCustomABFactors_() {
		String prefB = (String) get(JavaScoreProperties.BFACTOR_PROPERTY);
		
		if (prefB == null) return false;
		if ( prefB.equals("") || prefB.equals("-1")) return false;
		
		try {
			int b = Integer.parseInt(prefB);
			return (b > 0);
		} catch (Exception e) {
			return false;
		}
	}
	
	public static int getAFactor() { return sInstance.getAFactor_(); }
	private int getAFactor_() {
		int	afactor = RatingPhrfTimeOnTime.AFACTOR_DEFAULT;
		String prefA = (String) get(AFACTOR_PROPERTY);
		if (prefA != null) try {
			afactor = Integer.parseInt(prefA);
		} catch (Exception e) {}
		return afactor;
	}
	
	public static int getBFactor() { return sInstance.getBFactor_(); }
	private int getBFactor_() {
		int	bfactor = RatingPhrfTimeOnTime.BFACTOR_AVERAGE;
		String prefB = (String) get(BFACTOR_PROPERTY);
		if (prefB != null) try {
			bfactor = Integer.parseInt(prefB);
		} catch (Exception e) {}
		return bfactor;
	}
	
	public Object getColumnValue(int colIndex) {
		return null;
	}

	public void setColumnValue(Object object, int colIndex) {
		Util.showError(this, "JavaScoreProperties.setColumnValue not yet implemented");
	}

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		Util.showError(this, "JavaScoreProperties.xmlRead not yet implemented");
	}

	@Override public void xmlWrite(PersistentNode doc) {
		Util.showError(this, "JavaScoreProperties.xmlWrite not yet implemented");
	}

	/**
	 * Auguments Properties.setProperty with custom action for some properties.
	 * 
	 * @param key
	 *            the key to be placed into this property list.
	 * @param value
	 *            the value corresponding to <tt>key</tt>.
	 * @return property value?
	 * @see #Properties
	 */
	@Override public synchronized Object setProperty(String key, String value) {
		String curValue = getProperty(key);
		if (!curValue.equals(value)) {
			Object x = super.setProperty(key, value);

			if (key.equals(LOOKANDFEEL_PROPERTY)) JavaScore.setLookAndFeel(value);
			if (key.equals(LOCALE_PROPERTY)) {
				Util.initLocale(value);
			}
			return x;
		}
		return curValue;
	}

	public int compareTo(Object o) {
		return 0;
	}

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        sInstance.pcs.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
    	sInstance.pcs.removePropertyChangeListener(listener);
    }

}

