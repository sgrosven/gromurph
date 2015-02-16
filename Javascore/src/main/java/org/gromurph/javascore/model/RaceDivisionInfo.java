// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: RaceDivisionInfo.java,v 1.6 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gromurph.javascore.SailTime;
import org.gromurph.util.BaseObject;
import org.gromurph.util.Util;
import org.gromurph.xml.PersistentNode;

public class RaceDivisionInfo extends BaseObject {
	public class StartInfo {
		double length;
		long starttime;
		boolean isRacing;
		boolean nextDay;

		public StartInfo() {
			length = 0;
			starttime = SailTime.NOTIME;
			isRacing = fIsRacingDefault;
			nextDay = false;
		}

		@Override public boolean equals(Object o) {
			if (o == null) return false;
			StartInfo that = (StartInfo) o;
			if (this.length != that.length) return false;
			if (this.starttime != that.starttime) return false;
			if (this.isRacing != that.isRacing) return false;
			if (this.nextDay != that.nextDay) return false;
			return true;
		}

		@Override public String toString() {
			StringBuffer sb = new StringBuffer(24);
			sb.append(length);
			sb.append(":");
			sb.append(SailTime.toString(starttime));
			if (nextDay) sb.append("/nextday");
			sb.append(":");
			sb.append(isRacing);
			return sb.toString();
		}
		
		public long getStartTimeAdjusted() {
			// start time in millis after midnight of the main race start date
			if (starttime == SailTime.NOTIME) return SailTime.NOTIME;

			long timeMillis = starttime;
			if (nextDay) timeMillis += SailTime.DAYINMILLIS;

			return timeMillis;
		}

		public int compare(StartInfo left, StartInfo right) {
			if (left == null && right == null) return 0;
			if (left == null) return -1;
			if (right == null) return 1;

			if (left.isRacing && right.isRacing) {
				if (right.isRacing) {
					long sLeft = left.getStartTimeAdjusted();
					long sRight = right.getStartTimeAdjusted();
					if (sLeft < sRight) return -1;
					else if (sLeft > sRight) return 1;
					else return 0;
				} else {
					return -1;
				}
			} else if (right.isRacing) {
				return 1;
			} else {
				return 0;
			}

		}
	};


	private Map<AbstractDivision, StartInfo> fDivInfo;
	private boolean fIsRacingDefault;

	public RaceDivisionInfo() {
		this(true);
	}

	public RaceDivisionInfo(boolean isRacingDefault) {
		fDivInfo = new HashMap<AbstractDivision, StartInfo>(10);
		fIsRacingDefault = isRacingDefault;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) return true;
		try {
			RaceDivisionInfo that = (RaceDivisionInfo) obj;
			if (that == null) return false;
			if (that.fDivInfo.size() != this.fDivInfo.size()) return false;
			Iterator iter = this.fDivInfo.keySet().iterator();
			while (iter.hasNext()) {
				AbstractDivision div = (AbstractDivision) iter.next();
				StartInfo thissi = this.fDivInfo.get(div);
				StartInfo thatsi = that.fDivInfo.get(div);
				if (!Util.equalsWithNull(thissi, thatsi)) return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static final String STARTTIME_PROPERTY = "StartTime";
	public static final String NEXTDAY_PROPERTY = "nextDay";
	public static final String LENGTH_PROPERTY = "Length";
	public static final String ISRACING_PROPERTY = "isRacing";
	public static final String DIVISION_PROPERTY = "Div";
	public static final String NODE_PROPERTY = "DivStart";

	@Override public void xmlRead(PersistentNode n, Object rootObject) {
		fDivisions.clear();
		PersistentNode[] kids = n.getElements();
		for (int k = 0; k < kids.length; k++) {
			PersistentNode n2 = kids[k];
			xmlReadElement(n2, rootObject);
		}
	}

	private void xmlReadElement(PersistentNode n, Object rootObject) {
		AbstractDivision div = null;
		Race race = (Race) rootObject;

		String value = "";

		if ((value = n.getAttribute(DIVISION_PROPERTY)) != null) {
			if ((race != null) && (race.getRegatta() != null)) {
				div = race.getRegatta().getDivision(value);
				if (div == null) div = race.getRegatta().getSubDivision(value);
			} else if (DivisionList.getMasterList() != null) {
				div = DivisionList.getMasterList().find(value);
			} 
			if (div == null) div = new Division(value);
		}
		
		if (div == null) return; //no division element means no si
		
		StartInfo si = getInfo(div);

		if ((value = n.getAttribute(STARTTIME_PROPERTY)) != null) {
			si.starttime = new Long(SailTime.forceToLong(value)).longValue();
		}

		if ((value = n.getAttribute(LENGTH_PROPERTY)) != null) {
			si.length = Double.parseDouble(value);
		}

		if ((value = n.getAttribute(ISRACING_PROPERTY)) != null) {
			si.isRacing = value.toString().equalsIgnoreCase("true");
		}

		if ((value = n.getAttribute(NEXTDAY_PROPERTY)) != null) {
			si.nextDay = value.toString().equalsIgnoreCase("true");
		}

	}

	@Override public void xmlWrite(PersistentNode e) {

		for (AbstractDivision div : fDivisions) {
			StartInfo si = fDivInfo.get(div);

			PersistentNode child = e.createChildElement( NODE_PROPERTY);
			child.setAttribute(DIVISION_PROPERTY, div.getName());
			child.setAttribute(STARTTIME_PROPERTY, SailTime.toString(si.starttime));
			child.setAttribute(LENGTH_PROPERTY, Double.toString(si.length));
			child.setAttribute(ISRACING_PROPERTY, (si.isRacing ? "true" : "false"));
			child.setAttribute(NEXTDAY_PROPERTY, (si.nextDay ? "true" : "false"));
		}

		//return e;
	}

	public long getEarliestStartTime() {
		long earliest = Long.MAX_VALUE;
		for (StartInfo si : fDivInfo.values()) {
			if (si.isRacing) earliest = Math.min(earliest, si.getStartTimeAdjusted());
		}
		return (earliest == Long.MAX_VALUE) ? SailTime.NOTIME : earliest;
	}

	public long getStartTime(AbstractDivision div) {
		StartInfo si = getInfo(div);
		return si.starttime;
	}

	public long getStartTimeAdjusted(AbstractDivision div) {
		return getInfo(div).getStartTimeAdjusted();
	}

	public void setStartTime(AbstractDivision div, long time) {
		StartInfo si = getInfo(div);
		si.starttime = time;
	}
	
	public boolean hasStartTimes() {
		for (StartInfo si : fDivInfo.values()) {
			if ( si.isRacing && si.starttime != SailTime.NOTIME) return true;
		}
		return false;
	}


	public double getLength(AbstractDivision div) {
		StartInfo si = getInfo(div);
		return si.length;
	}

	public void setLength(AbstractDivision div, double len) {
		StartInfo si = getInfo(div);
		si.length = len;
	}

	public boolean isNextDay(AbstractDivision div) {
		StartInfo si = getInfo(div);
		return si.nextDay;
	}

	public void setNextDay(AbstractDivision div, boolean nd) {
		StartInfo si = getInfo(div);
		si.nextDay = nd;
	}

	public boolean isRacing(AbstractDivision div) {
		StartInfo si = getInfo(div);
		return si.isRacing;
	}

	public void setIsRacing(AbstractDivision div, boolean b) {
		StartInfo si = getInfo(div);
		si.isRacing = b;
	}
	
	private StartingDivisionList fDivisions = new StartingDivisionList();

	private StartInfo getInfo(AbstractDivision div) {
		StartInfo si = fDivInfo.get(div);
		if (si == null) {
			si = new StartInfo();
			fDivisions.add(div);
			fDivInfo.put(div, si);
		}
		return si;
	}

	public int compareTo(Object parm1) {
		return 0;
	}

	public StartingDivisionList getStartingDivisions(boolean racingOnly) {
		// add all subdivs for a split event
		if (!racingOnly) return fDivisions;
		else {
    		StartingDivisionList racingDivs = new StartingDivisionList();
    		for (AbstractDivision div : fDivisions) {
    			if (!racingOnly || isRacing(div)) racingDivs.add(div);
    		}
    		return racingDivs;
		}
	}
	

}
/**
 * $Log: RaceDivisionInfo.java,v $ Revision 1.6 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:38 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.12.4.2 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.12.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.12.2.1 2005/06/26 22:47:18 sandyg Xml overhaul to remove xerces dependence
 * 
 * Revision 1.12 2004/05/06 02:11:50 sandyg Beta support for revised Qualifying/Final series for 2004 J22 Worlds
 * 
 * Revision 1.11 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.10 2003/04/27 21:03:27 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.9 2003/03/28 03:07:44 sandyg changed toxml and fromxml to xmlRead and xmlWrite
 * 
 * Revision 1.8 2003/03/16 20:39:44 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.7 2003/01/05 21:29:29 sandyg fixed bad version/id string
 * 
 * Revision 1.6 2003/01/04 17:29:10 sandyg Prefix/suffix overhaul
 * 
 */
