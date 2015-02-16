// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RatingManager.java,v 1.4 2006/01/15 21:10:38 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.manager;

import java.io.IOException;
import java.util.Iterator;

import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.ratings.*;
import org.gromurph.util.BaseList;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

/**
 * manages the list of supported rating systems. Works with 'ratingmanager.xml' to find the list of supported ratings
 * and their affiliate rating classes and editors.
 */
public class RatingManager {

	/**
	 * keys are rating classes, elements are corresponding editor classes
	 **/
	static BaseList<RatingManagerElement> sRatingElements;

	private static BaseList<RatingManagerElement> getMasterList() {
		if (sRatingElements == null) initializeElements();
		return sRatingElements;
	}
	
	public static void initializeElements() {
		initializeElements(false);
	}
	public static void initializeElements(boolean readOnly) {
		sRatingElements = new BaseList<RatingManagerElement>() {
			@Override
			public Class getContainingClass() {
				return RatingManagerElement.class;
			}

		};

		sRatingElements.setFileName(Util.getWorkingDirectory() + "/ratingmanager.xml");
		sRatingElements.setRootTag("RatingManager");
		sRatingElements.setElementTag("Item");
		try {
			sRatingElements.xmlReadFromFile();
		} catch (Exception e3) {
			sRatingElements.clear();
		}

		if (!hasSystem("OneDesign")) {
			sRatingElements.add(new RatingManagerElement("OneDesign", RatingOneDesign.class.getName()));
		}
		if (!hasSystem("PHRF")) {
			sRatingElements.add(new RatingManagerElement("PHRF", RatingPhrf.class.getName()));
		}
		if (!hasSystem("IRC")) {
			sRatingElements.add(new RatingManagerElement("IRC", RatingIrc.class.getName()));
		}
		if (!hasSystem("MORC")) {
			sRatingElements.add(new RatingManagerElement("MORC", RatingMorc.class.getName()));
		}
		if (!hasSystem("Multihull")) {
			sRatingElements.add(new RatingManagerElement("Multihull", RatingMultihull.class.getName()));
		}
		if (!hasSystem("PHRFTimeOnTime")) {
			sRatingElements.add(new RatingManagerElement("PHRFTimeOnTime", RatingPhrfTimeOnTime.class.getName()));
		}
		if (!hasSystem("Yardstick")) {
			sRatingElements.add(new RatingManagerElement("Yardstick", RatingYardstick.class.getName()));
		}
		if (!hasSystem("Portsmouth")) {
			sRatingElements.add(new RatingManagerElement("Portsmouth", RatingPortsmouth.class.getName()));
		}

		if (!readOnly) saveRatingElements();
	}
	
	public static boolean saveRatingElements() {
		try {
			sRatingElements.xmlWriteToFile();
			return true;
		} catch (IOException e3) {
			Util.printlnException(sRatingElements, e3, true);
			return false;
		}		
	}

	private static String getRatingName(String sysName) {
		for (Iterator i = getMasterList().iterator(); i.hasNext();) {
			RatingManagerElement el = (RatingManagerElement) i.next();
			if (el.getSystem().equals(sysName))
				return el.getRatingClass();
		}
		return null;
	}

	private static boolean hasSystem(String system) {
		for (Iterator i = getMasterList().iterator(); i.hasNext();) {
			RatingManagerElement el = (RatingManagerElement) i.next();
			if (el.getSystem().equals(system))
				return true;
		}
		return false;
	}

	public static Object[] getSupportedSystems() {
		Object[] names = new Object[getMasterList().size()];
		int n = 0;
		for (Iterator i = getMasterList().iterator(); i.hasNext();) {
			RatingManagerElement el = (RatingManagerElement) i.next();
			names[n++] = el.getLongName();
		}
		return names;
	}

	public static String getSystemFromLongName(String longname) {
		for (Iterator i = getMasterList().iterator(); i.hasNext();) {
			RatingManagerElement el = (RatingManagerElement) i.next();
			if (el.getLongName().equals(longname))
				return el.getSystem();
		}
		return null;
	}

	public static String getLongNameFromSystem(String sys) {
		for (Iterator i = getMasterList().iterator(); i.hasNext();) {
			RatingManagerElement el = (RatingManagerElement) i.next();
			if (el.getSystem().equals(sys))
				return el.getLongName();
		}
		return null;
	}

	public static Rating createRating(Division div, double value) throws RatingOutOfBoundsException {
		Rating r;
		if (div.isOneDesign()) {
			String cn = ((RatingOneDesign) div.getMinRating()).getODClassName();
			if (cn == null || cn.length() > 0)
				cn = div.getName();
			r = new RatingOneDesign(cn);
		} else {
			r = createRating(div.getSystem(), value);
			if (!div.contains(r))
				throw new RatingOutOfBoundsException();
		}
		return r;
	}

	public static Rating createRating(String system) {
		return createRating(system, Double.NaN);
	}

	public static Rating createRating(String system, double val) {
		Rating rtg = null;
		try {
			String ratingClass = getRatingName(system);

			if (ratingClass == null) {
				rtg = new RatingOneDesign(system);
			} else
				try {
					try {
						rtg = (Rating) (Class.forName(ratingClass)).newInstance();
					} catch (IllegalAccessException ex) {} catch (InstantiationException ex) {}

					rtg.setPrimaryValue(val);
				} catch (ClassCastException cce) {} // do nothing if bombs, dont care
		} catch (Exception e) {
			rtg = null;
		}
		return rtg;
	}

	public static Rating createRatingFromXml(PersistentNode n, Object rootObject) {
		String system = n.getAttribute(Rating.SYSTEM_PROPERTY);

		if (system != null) {
			Rating r = createRating(system);
			r.xmlRead(n, rootObject);
			return r;
		} else {
			return null;
		}
	}

}
/**
 * $Log: RatingManager.java,v $ Revision 1.4 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.11.2.1 2005/06/26 22:47:19 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.11 2005/04/27 02:45:47 sandyg Added Yardstick, and added Yardstick and IRC all to GUI. Portsmouth now
 * trivial subclass of yardstick
 * 
 * Revision 1.10 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.9 2005/02/27 23:23:54 sandyg Added IRC, changed corrected time scores to no longer round to a second
 * 
 * Revision 1.8 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.7 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.6 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.5 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
