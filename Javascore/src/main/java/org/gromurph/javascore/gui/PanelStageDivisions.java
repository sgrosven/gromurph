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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.scoring.Stage;
import org.gromurph.util.HelpManager;
import org.gromurph.util.PanelStartStop;

/**
 * Panel for setting which SubDivisions an Entry is entered. Use in PanelEntry
 **/

public class PanelStageDivisions extends PanelStartStop implements ActionListener {
	static ResourceBundle res = JavaScoreProperties.getResources();

	private Regatta regatta = JavaScoreProperties.getRegatta();
	private Stage stage;
	private List<JCheckBox> fCheckDivisions = new ArrayList<JCheckBox>();
	private Map<String, AbstractDivision> allDivisions = new TreeMap<String, AbstractDivision>();

	public PanelStageDivisions() {
		super();
		addFields();

		stage = null;
	}

	public void setStage( Stage s) {
		if (stage != s) {
			if (isStarted) stop();
            stage = s;
            updateFields();
            if (isStarted) start();
		}
    }

	@Override public void updateFields() {
		regatta = JavaScoreProperties.getRegatta();
		updateCheckBoxes();
	}
	
	@Override public void start() {
		addCheckBoxListeners();
	}
	
	@Override public void stop() {
		removeCheckBoxListeners();
	}

	private void addCheckBoxListeners() {
		for (JCheckBox check : fCheckDivisions) {
			check.addActionListener(this);
		}
	}
	private void removeCheckBoxListeners() {
		for (JCheckBox check : fCheckDivisions) {
			check.addActionListener(this);
		}
	}

	private void addFields() {
		HelpManager.getInstance().registerHelpTopic(this, "subdivisions.entries");
		setLayout(new GridBagLayout()); // new FlowLayout( FlowLayout.LEFT));
		updateCheckBoxes();
	}

	public void updateCheckBoxes() {
		if (regatta == null) return;
		
		allDivisions.clear();
		for ( AbstractDivision ad : regatta.getAllDivisions()) {
			allDivisions.put( ad.getName(), ad);
		}

		for (JCheckBox c : fCheckDivisions) {
			c.removeActionListener(this);
			this.remove(c);
		}
		fCheckDivisions.clear();

		// recreate the check boxes
		final int PERROW = 4;
		int i = 0;
		int x = 0;
		int y = 0;

		for (AbstractDivision div : allDivisions.values()) {
			JCheckBox check = new JCheckBox(div.getName());
			if (stage != null && stage.getDivisions().contains(div)) {
				check.setSelected(true);
			} else {
				check.setSelected(false);
			}
			fCheckDivisions.add( check);
			gridbagAdd(check, x, y);
			x++;
			if (x >= PERROW) {
				x = 0;
				y++;
			}
			i++;
		}
		y++;
		
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
		if (regatta == null) return;
		if (stage == null) return;

		JCheckBox check = (JCheckBox) event.getSource();

		String divname = check.getText();
		AbstractDivision div = allDivisions.get(divname);
		
		if (check.isSelected()) {
			stage.addDivision(div);
		} else {
			stage.removeDivision(div);
		}
		updateEnabled();
	}

	@Override public void setEnabled(boolean onoff) {
		super.setEnabled(onoff);

		for (JCheckBox cb : fCheckDivisions) {
			cb.setEnabled( onoff);// false);
		}
		updateEnabled();
	}

	private void updateEnabled() {
		// does nothing, at least for now
	}

}
/**
 * $Log: PanelEntrySubDivisions.java,v $ Revision 1.7 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 */
