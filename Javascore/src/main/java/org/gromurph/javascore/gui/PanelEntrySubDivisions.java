// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelEntrySubDivisions.java,v 1.7 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import org.gromurph.javascore.*;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.DivisionList;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.SubDivision;
import org.gromurph.javascore.model.SubDivisionList;
import org.gromurph.util.HelpManager;
import org.gromurph.util.PanelStartStop;

/**
 * Panel for setting which SubDivisions an Entry is entered. Use in PanelEntry
 **/

public class PanelEntrySubDivisions extends PanelStartStop implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	Entry fEntry;
	Map<SubDivision, JCheckBox> fCheckSubDivisions = new TreeMap<SubDivision, JCheckBox>();

	public PanelEntrySubDivisions() {
		super();
		addFields();

		fEntry = null;
	}

	public void setEntry(Entry e) {
		fEntry = e;

		Regatta reg = getRegatta();
		if (reg == null) return;

		for (SubDivision sdiv : reg.getSubDivisions()) {
			JCheckBox check = fCheckSubDivisions.get(sdiv);

			if (check != null) check.setSelected(sdiv.contains(fEntry));
		}
		updateEnabled();
	}

	boolean isStarted = false;

	@Override
	public void start() {
		updateFields();
		if (!isStarted) {
			isStarted = true;
			for (Iterator v = fCheckSubDivisions.values().iterator(); v.hasNext();) {
				JCheckBox check = (JCheckBox) v.next();
				check.addActionListener(this);
			}
		}

	}

	@Override
	public void stop() {
		if (isStarted) {
			isStarted = false;
			for (Iterator v = fCheckSubDivisions.values().iterator(); v.hasNext();) {
				JCheckBox check = (JCheckBox) v.next();
				check.removeActionListener(this);
			}
		}
	}

	private void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "subdivisions.entries");
		setLayout(new GridBagLayout()); // new FlowLayout( FlowLayout.LEFT));
		updateFields();
	}

	@Override
	public void updateFields() {
		Regatta reg = getRegatta();
		if (reg == null) return;

		SubDivisionList mDivs = null;

		// first see if checkboxes are already preset
		if (fCheckSubDivisions.size() == reg.getNumSubDivisions()) {
			// right number of boxes, see if right subdivs
			mDivs = new SubDivisionList();
			mDivs.addAll(fCheckSubDivisions.keySet());
			mDivs.removeAll(reg.getSubDivisions());
			// no need to do more!
		}

		boolean wasStarted = isStarted;

		if (mDivs != null && mDivs.size() == 0) {
			// check the subdivs names, but don't recreate
			for (SubDivision div : reg.getSubDivisions()) {
				JCheckBox check = fCheckSubDivisions.get(div);
				if (check != null) check.setText(div.getName());
			}
		} else {
			// recreate the check boxes
			stop();
			final int PERROW = 3;

			fCheckSubDivisions.clear();

			this.removeAll();

			int i = 0;
			int x = 0;
			int y = 0;

			for (SubDivision div : reg.getSubDivisions()) {
				JCheckBox check = new JCheckBox(div.getName());
				fCheckSubDivisions.put(div, check);
				//add( check);
				gridbagAdd(check, x, y);
				x++;
				if (x >= PERROW) {
					x = 0;
					y++;
				}
				i++;
			}
			y++;
			if (wasStarted) start();

		}
		revalidate();
	}

	private GridBagConstraints gbc = new GridBagConstraints();

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
	 * specified width, height of 1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent newComp, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 2, 2, 2);
		((GridBagLayout) this.getLayout()).setConstraints(newComp, gbc);
		this.add(newComp);
	}

	public void actionPerformed(ActionEvent event) {
		Regatta reg = getRegatta();
		if (reg == null) return;

		JCheckBox check = (JCheckBox) event.getSource();

		String divname = check.getText();
		SubDivision div = reg.getSubDivision(divname);

		if (fEntry != null) {
			if (check.isSelected()) {
				validateSelect(div);
			} else {
				validateDeSelect(div);
			}
		}
	}

	/**
	 * handles selection of a subdivision with the monopoly flag turned on
	 */
	private void validateSelect(SubDivision div) {
		Regatta reg = getRegatta();
		if (reg == null) return;

		boolean addit = true;
		if (div.isMonopoly()) {
			if (entryInOtherDivs(div)) {
				int ans = JOptionPane.showConfirmDialog(this, res.getString("PanelEntryMessageNewMonopoly"),
						res.getString("PanelEntryTitleNewMonopoly"), JOptionPane.YES_NO_OPTION);
				addit = (ans == JOptionPane.YES_OPTION);

				// clear out other subdivsion selections
				if (addit) for (SubDivision sub : getRegatta().getSubDivisions()) {

					if (!sub.equals(div)) {
						sub.removeEntry(fEntry);
						JCheckBox check = fCheckSubDivisions.get(sub);
						check.setSelected(false);
					}
				}
			}
		}

		if (addit) {
			// set the entry into the desired subdivision
			div.addEntry(fEntry);
			updateEnabled();
		}
	}

	/**
	 * handles selection of a subdivision with the monopoly flag turned on
	 */
	private void validateDeSelect(SubDivision div) {
		Regatta reg = getRegatta();
		if (reg == null) return;

		// set the entry into the desired subdivision
		div.removeEntry(fEntry);

		updateEnabled();
	}

	private boolean entryInOtherDivs(SubDivision div) {
		for (SubDivision sub : getRegatta().getSubDivisions()) {
			if (!sub.equals(div)) {
				if (sub.contains(fEntry)) return true;
			}
		}
		return false;
	}

	@Override public void setEnabled(boolean onoff) {
		super.setEnabled(onoff);

		if (!onoff) {
			for (Iterator i = fCheckSubDivisions.values().iterator(); i.hasNext();) {
				((JCheckBox) i.next()).setEnabled(false);
			}
		} else {
			updateEnabled();
		}
	}

	private void updateEnabled() {
		// have monopoly and entry set true
		SubDivision entrySub = null;
		Regatta reg = getRegatta();
		if (reg == null) return;

		for (SubDivision sub : getRegatta().getSubDivisions()) {
			if (sub.isMonopoly()) {
				if (sub.contains(fEntry)) entrySub = sub;
			}
		}
		// mono is entry's subdivision with 'isMono" flag or null;

		for (SubDivision checkSub : getRegatta().getSubDivisions()) {
			JCheckBox check = fCheckSubDivisions.get(checkSub);

			if (check != null) {
				boolean ok = (entrySub == null) || (checkSub == entrySub);

				// ok so far, must also be same division as entry
				if (ok) {
    				AbstractDivision par = checkSub.getParentDivision();
    				if (par != null) {
    					ok = ok && par.contains(fEntry);
    				} else {
    					ok = false;
    				}
				}

				check.setEnabled(ok);
			}
		}
	}

	private Regatta getRegatta() {
		return JavaScoreProperties.getRegatta();
	}

	public static void main(String[] args) {
		JavaScore.initializeEditors();

		Division div = DivisionList.getMasterList().get(1);
		Division div2 = DivisionList.getMasterList().get(2);

		Entry e = new Entry();
		try {
			e.setDivision(div);
		}
		catch (RatingOutOfBoundsException ex) {}

		SubDivision s2 = new SubDivision("Blue", div);
		s2.setMonopoly(true);
		s2.setScoreSeparately(false);

		SubDivision s3 = new SubDivision("Green", div2);
		s3.setMonopoly(false);
		s3.setScoreSeparately(true);

		SubDivision s1 = new SubDivision("White", DivisionList.getMasterList().get(3));
		s1.setMonopoly(false);
		s1.setScoreSeparately(false);

		Regatta reg = new Regatta();

		reg.addDivision(div);
		reg.addDivision(div2);

		reg.addEntry(e);

		reg.addSubDivision(s1);
		reg.addSubDivision(s2);
		reg.addSubDivision(s3);
		JavaScoreProperties.setRegatta(reg);

		PanelEntrySubDivisions pb = new PanelEntrySubDivisions();
		pb.setEntry(e);
		pb.start();

		JOptionPane.showConfirmDialog(null, pb);
		System.exit(0);
	}
}
/**
 * $Log: PanelEntrySubDivisions.java,v $ Revision 1.7 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.6 2006/04/15 23:40:13 sandyg folding subdivisions
 * 
 * Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.4.1 2005/11/01 02:36:02 sandyg Java5 update - using generics
 * 
 * Revision 1.11 2005/03/06 04:19:14 sandyg Fixed bug 775405 updated sub-division names in entry panel.
 * 
 * Revision 1.10 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.9 2004/01/17 22:27:37 sandyg First cut at unlimited number of crew, request 512304
 * 
 * Revision 1.8 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/04/23 22:13:21 sandyg documentation clean up
 * 
 * Revision 1.6 2003/01/06 00:32:36 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.5 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
