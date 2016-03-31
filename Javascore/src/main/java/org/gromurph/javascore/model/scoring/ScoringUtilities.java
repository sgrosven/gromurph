// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ScoringManager.java,v 1.12 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.model.scoring;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.ScoringException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RaceList;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;
import org.gromurph.util.WarningList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScoringUtilities implements Constants {
	
	protected transient static Map<String, String> sSupportedSystems;
	static ResourceBundle res = JavaScoreProperties.getResources();

	static {
		sSupportedSystems = new TreeMap<String, String>();
		addSupportedModel(ScoringLowPoint.NAME, ScoringLowPoint.class.getName());
		addSupportedModel(ScoringLowPointAYCWednesday.NAME, ScoringLowPointAYCWednesday.class.getName());
		addSupportedModel(ScoringLowPointDnIceboat.NAME, ScoringLowPointDnIceboat.class.getName());
		addSupportedModel(ScoringLowPointLightning.NAME, ScoringLowPointLightning.class.getName());
		addSupportedModel(ScoringLowPointSnipe.NAME, ScoringLowPointSnipe.class.getName());
	}

	/**
	 * list of all supported scoring systems
	 * 
	 * @return array of scoring models
	 */
	public static Object[] getSupportedModels() {
		return sSupportedSystems.keySet().toArray();
	}

	/**
	 * adds model to list of supported models
	 * 
	 * @param newModel
	 */
	public static void addSupportedModel(String modelName, String modelClass) {
		sSupportedSystems.put(modelName, modelClass);
	}

	protected static Logger logger = LoggerFactory.getLogger( ScoringUtilities.class);

	/**
	 * sets the active scoring model
	 * 
	 * @param modelName
	 *            name of model to be added
	 */
	public static ScoringModel createScoringModel(String modelName) {
		if (modelName.equals(ScoringLowPoint.ALTNAME) || 
				modelName.equals(ScoringLowPoint.ALTNAME2) ||
				modelName.equals(ScoringLowPoint.ALTNAME3)) 
			modelName = ScoringLowPoint.NAME;

		String modelClass = sSupportedSystems.get(modelName);
		ScoringModel newModel = null;

		if (modelClass == null) {
			logger.warn("WARNING!!! Unsupported scoring system requested, set to lowpoint, request={}", modelName);
			newModel = new ScoringLowPoint();
		} else {
			try {
				newModel = (ScoringModel) Class.forName(modelClass).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (newModel == null) {
				logger.warn("WARNING!!! Unsupported scoring class requested, set to lowpoint, request={}", modelClass);
				newModel = new ScoringLowPoint();
			}
		}

		return newModel;
	}
	
	public static WarningList validateRegatta(Regatta reg) throws ScoringException {
		WarningList warnings = new WarningList();
		RaceList inRaces = reg.getRaces();
		// check each race to make sure information needed exists
		// (such as distance for PHRF)
		inRaces.sort();
		for (Race race : inRaces) {
			race.syncFinishesWithEntries();

			for (AbstractDivision adiv : race.getStartingDivisions(true)) {
				if (adiv instanceof Division) {
					Division div = (Division) adiv;
					div.getSlowestRating().validateRace(race, div, warnings);
				}
			}
		}
		return warnings;
	}

	public static void mergeEntries(EntryList existingEntries, EntryList newEntries) {
		for (Entry ent : newEntries) {
			if (!existingEntries.contains(ent))
				existingEntries.add(ent);
		}
	}

	/**
	 * calculates average points as per RRS2001 A10(a) (throwouts included): "points equal to the average, to the
	 * nearest tenth of a point (0.05 to be rounded upward), of her points in all the races in the series except the
	 * race in question;"
	 * <P>
	 * NOTE: this formula assumes that "the race in question" really wants to say the "race(s) in question"
	 * 
	 * @param regatta
	 *            regatta to be scored. All instances of AVG in all races in all divisions in the regatta will be
	 *            scanned and AVG points calculated
	 */
	public static void calcAveragePoints(RacePointsList divRacePoints) {
		calcAveragePoints(divRacePoints, true);
	}

	/**
	 * calculates average points as per RRS2001 A10(a) except that including the throwout (or not) is an optional
	 * 
	 * @param regatta
	 *            regatta to be scored. All instances of AVG in all races in all divisions in the regatta will be
	 *            scanned and AVG points calculated
	 * @param throwoutIsIncluded
	 *            true if throwouts are to be included in calculation
	 */
	public static void calcAveragePoints(RacePointsList divRacePoints, boolean throwoutIsIncluded) {
		EntryList eWithAvg = new EntryList();
		for (int i = 0; i < divRacePoints.size(); i++) {
			RacePoints rp = divRacePoints.get(i);
			if (rp.getFinish().hasPenalty(AVG)) {
				Entry e = rp.getEntry();
				if (!eWithAvg.contains(e))
					eWithAvg.add(e);
			}
		}

		for (Entry e : eWithAvg) {
			RacePointsList list = divRacePoints.findAll(e);
			double pts = 0;
			double n = 0;
			boolean hasAvg = false;

			//			double[] tempPts = new double[list.size()];
			//			long[] tempPen = new long[list.size()];
			//			int t = 0;
			//
			for (RacePoints p : list) {
				Finish finish = p.getRace().getFinish(p.getEntry());

				//				tempPts[t] = p.getPoints();
				//				tempPen[t++] = finish.getPenalty().getPenalty();

				if (finish != null) {
					if ((!p.isThrowout() || throwoutIsIncluded) && !finish.getPenalty().hasPenalty(AVG)) {
						pts = pts + p.getPoints();
						n++;
					} else if (finish.getPenalty().hasPenalty(AVG)) {
						hasAvg = true;
					}
				}
			}

			if (hasAvg) {
				double avg = pts / n;
				avg = Math.round(avg * 10);
				avg = avg / 10.0;
				for (RacePoints p : list) {
					Finish finish = p.getRace().getFinish(p.getEntry());
					if (finish != null && finish.getPenalty().hasPenalty(AVG)) {
						p.setPoints(avg);
					}
				}
			} // loop setting average points
		} // loop thru entries
	}

    /**
     * generates list of notes for series scoring of 
     * specified group of race points (generally a single division, might be whole fleet)
     * @param rpList the list of race points on which to generate notes
     * @return list of strings containing notes, empty list if no notes
     */
	public static List<String> getRaceScoringNotes( RacePointsList rpList) {
		List<String> notes = new ArrayList<String>(5);

		if (rpList.size() == 0)
			return notes;

		RacePoints rp = rpList.get(0);
		AbstractDivision div1 = rp.getDivision();

		if (!(div1 instanceof Division))
			return notes;

		// have division, keep going - they can/should report
		// length/starttime
		Division div = (Division) div1;
		Race r = rp.getRace();

		return getRaceScoringNotes( r, div);
	}
	
	/**
	     * generates list of notes for series scoring of 
	     * specified group of race points (generally a single division, might be whole fleet)
	     * @param rpList the list of race points on which to generate notes
	     * @return list of strings containing notes, empty list if no notes
	     */
	public static List<String> getRaceScoringNotes( Race r, AbstractDivision div) {
		List<String> notes = new ArrayList<String>(5);

		long start = r.getStartTimeRaw(div);
		double length = r.getLength(div);

		if (r.getStartDate() != null || start != SailTime.NOTIME || length > 0) {
			String datetime = res.getString("GenNone");
			String lenString = res.getString("GenNone");

			if (start != SailTime.NOTIME || r.getStartDate() != null) {
				datetime = r.formatStartDateTime(div);
			}

			if (length > 0) {
				lenString = Util.formatDouble(length, 2);
				notes.add(MessageFormat.format(res.getString("ReportLabelRaceInfo"),
						new Object[] { datetime, lenString }));
			} else {
				notes.add(MessageFormat.format(res.getString("ReportLabelRaceInfoNoLength"), new Object[] { datetime }));
			}
		}

		return notes;
	}
    
}
/**
 * $Log: ScoringManager.java,v $ Revision 1.12 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
