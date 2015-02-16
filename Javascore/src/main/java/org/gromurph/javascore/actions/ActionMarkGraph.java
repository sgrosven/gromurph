//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ActionMarkGraph.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.gui.MarkGraphInterface;
import org.gromurph.javascore.gui.PanelMarkGraph;
import org.gromurph.javascore.manager.RegattaManager;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.RacePoints;
import org.gromurph.javascore.model.RacePointsList;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionMarkGraph extends AbstractAction {
	static ResourceBundle res = JavaScoreProperties.getResources();
	static ResourceBundle resUtil = org.gromurph.util.Util.getResources();

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ActionMarkGraph() {
		super(res.getString("MarkRoundingGraph"));
	}

	public static void main(String s[]) {
		ActionMarkGraph amg = new ActionMarkGraph();
		amg.initFields();
		try {
			amg.logger.info("Opening...{}{}.regatta" , Util.getWorkingDirectory(), s[0]);
			Regatta reg = RegattaManager.readRegattaFromDisk(Util
					.getWorkingDirectory(), s[0] + ".regatta");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(java.awt.event.ActionEvent event) {
		if (fGraph == null) initFields();
		setVisible(true);
	}
	
	PanelMarkGraph fGraph;
	JFrame fFrame;
	RegattaMarkModel fModel;

	public void setVisible(boolean b) {

		Regatta reg = JavaScoreProperties.getRegatta();
		fLabelRegattaTitle.setText(reg.getName());
		fComboRaces.setModel(new DefaultComboBoxModel<Race>((Race[]) reg.getRaces().toArray()));

		fFrame.setVisible(b);
	}

	public void initFields() {
		fGraph = new PanelMarkGraph();
		fModel = null;

		fFrame = new JFrame();
		fFrame.setSize(new java.awt.Dimension(640, 480));

		fFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});
		fFrame.getContentPane().add(fGraph, java.awt.BorderLayout.CENTER);

		JPanel panelNorth = new JPanel(new BorderLayout());
		fFrame.getContentPane().add(panelNorth, java.awt.BorderLayout.NORTH);

		JPanel panelSouth = new JPanel(new FlowLayout());
		fFrame.getContentPane().add(panelSouth, java.awt.BorderLayout.SOUTH);

		fLabelRegattaTitle = new JLabel("", SwingConstants.CENTER);
		fComboRaces = new JComboBox();
		fComboRaces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Race r = (Race) fComboRaces.getSelectedItem();
				fModel = new RegattaMarkModel( JavaScoreProperties.getRegatta(), r);
				fGraph.setModel(fModel);
				fGraph.repaint();
			}
		});
		panelNorth.add(fLabelRegattaTitle, BorderLayout.NORTH);
		JPanel racePanel = new JPanel(new FlowLayout());
		panelNorth.add(racePanel, BorderLayout.SOUTH);
		racePanel.add(new JLabel(res.getString("MarkMessageSelectRace")));
		racePanel.add(fComboRaces);

		fButtonExit = new JButton(resUtil.getString("OKButton"));
		fButtonExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fFrame.setVisible(false);
			}
		});
		panelSouth.add(fButtonExit);
	}

	JLabel fLabelRegattaTitle;
	JComboBox<Race> fComboRaces;
	JButton fButtonExit;

	public class RegattaMarkModel implements MarkGraphInterface {
		Regatta lRegatta;
		RacePointsList fFinishes;
		Race fRace;

		public RegattaMarkModel(Regatta reg, Race r) {
			lRegatta = reg;
			if (r == null) {
				fFinishes = null;
				fRace = null;
			} else {
				fRace = r;
				fFinishes = lRegatta.getScoringManager().getRacePointsList()
						.findAll(fRace);
				fFinishes.sortPointsPositionRounding();
			}
		}

		public long getMarkPosition(int entrynumber, String markName) {
			if (fFinishes != null) {
				Entry e = fFinishes.get(entrynumber).getEntry();
				FinishList fl = fRace.getRoundings(markName);
				Finish f = fl.findEntry(e);
				if (f == null)
					return -1;
				else
					return f.getFinishPosition().longValue();
			} else {
				return -1;
			}
		}

		public int getNumMarks() {
			return fRace.getAllRoundings().size();
		}

		public int getNumEntries() {
			return lRegatta.getNumEntries();
		}

		public String getEntryName(int entrynumber) {
			Entry e = fFinishes.get(entrynumber).getEntry();
			return e.getBoat().getSailId().toString() + " "
					+ e.getSkipper().getLast();
		}

		public String getFinishString(int entrynumber) {
			RacePoints rp = fFinishes.get(entrynumber);
			if (rp == null)
				return "";
			else
				return rp.getFinish().getFinishPosition().toString();
		}

	} // inner RegattaMarkModel

}
/**
 * $Log: ActionMarkGraph.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final
 * release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:39 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:42 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize
 * code in a new module
 * 
 * Revision 1.8.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.8 2005/05/26 01:45:43 sandyg fixing resource access/lookup
 * problems
 * 
 * Revision 1.7 2005/04/23 21:54:07 sandyg JWS mods for release 4.3.1
 * 
 * Revision 1.6 2004/04/10 20:49:29 sandyg Copyright year update
 * 
 * Revision 1.5 2003/04/27 21:03:29 sandyg lots of cleanup, unit testing for
 * 4.1.1 almost complete
 * 
 * Revision 1.4 2003/01/04 17:33:05 sandyg Prefix/suffix overhaul
 * 
 */
