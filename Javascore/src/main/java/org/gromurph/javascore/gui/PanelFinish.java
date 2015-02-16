// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelFinish.java,v 1.5 2006/05/19 05:48:42 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;

import javax.swing.JLabel;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.PanelStartStop;

/**
 * The Finish class handles a single Finish. It has covering information about the Finish and a list of Finishes for the
 * Finish
 **/
public class PanelFinish extends PanelStartStop implements PropertyChangeListener {
	static ResourceBundle res = JavaScoreProperties.getResources();
	private final static String NONE = res.getString("FinishLabelNone");

	Finish fFinish;
	FinishList fFinishes;

	public PanelFinish() {
		super();
		setLayout( new GridBagLayout());
		setSize(200, 350);
		addFields();
	}

	JLabel fLabelDivision;
	JLabel fLabelSailId;
	JLabel fLabelBoat;
	JLabel fLabelBow;
	JLabel fLabelSkipper;
	JLabel fLabelOrder;
	JLabel fLabelFinishTime;
	JLabel fLabelElapsedTime;
	JLabel fLabelCorrectedTime;
	JLabel fLabelPenalty;

	String EMPTY = "";
	
	public void addFields() {
		setLayout(new GridBagLayout());
		setGridBagInsets(new Insets(0, 2, 0, 29));

		JTextFieldSelectAll dummy = new JTextFieldSelectAll();

		int row = 0;

		JLabel label = new JLabel(res.getString("GenDivision") + "(" + res.getString("GenRating") + ")");
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fLabelDivision = new JLabel(EMPTY);
		fLabelDivision.setFont(dummy.getFont());
		fLabelDivision.setForeground(Color.black);
		gridbagAdd(fLabelDivision, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		fLabelBow = new JLabel(EMPTY);
		if ((JavaScoreProperties.getRegatta() != null) && (JavaScoreProperties.getRegatta().isUseBowNumbers())) {
			label = new JLabel(res.getString("GenBowNum"));
			label.setFont(dummy.getFont());
			gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

			fLabelBow.setFont(dummy.getFont());
			fLabelBow.setForeground(Color.black);
			gridbagAdd(fLabelBow, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
			row++;
		}

		label = new JLabel(res.getString("GenSailNum"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fLabelSailId = new JLabel(EMPTY);
		fLabelSailId.setFont(dummy.getFont());
		fLabelSailId.setForeground(Color.black);
		gridbagAdd(fLabelSailId, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		label = new JLabel(res.getString("GenBoatName"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fLabelBoat = new JLabel(" ");
		fLabelBoat.setFont(dummy.getFont());
		fLabelBoat.setForeground(Color.black);
		gridbagAdd(fLabelBoat, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		label = new JLabel(res.getString("FinishLabelSkipper"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fLabelSkipper = new JLabel(" ");
		fLabelSkipper.setFont(dummy.getFont());
		fLabelSkipper.setForeground(Color.black);
		gridbagAdd(fLabelSkipper, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		label = new JLabel(res.getString("FinishLabelFinishOrder"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fLabelOrder = new JLabel(EMPTY);
		fLabelOrder.setFont(dummy.getFont());
		fLabelOrder.setForeground(Color.black);
		gridbagAdd(fLabelOrder, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		label = new JLabel(res.getString("GenFinishTime"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		fLabelFinishTime = new JLabel(EMPTY);
		fLabelFinishTime.setFont(dummy.getFont());
		fLabelFinishTime.setForeground(Color.black);
		gridbagAdd(fLabelFinishTime, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);

		row++;
		label = new JLabel(res.getString("FinishLabelElapsedTime"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fLabelElapsedTime = new JLabel(" ");
		fLabelElapsedTime.setFont(dummy.getFont());
		fLabelElapsedTime.setForeground(Color.black);
		gridbagAdd(fLabelElapsedTime, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		label = new JLabel(res.getString("FinishLabelCorrectedTime"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fLabelCorrectedTime = new JLabel(" ");
		fLabelCorrectedTime.setFont(dummy.getFont());
		fLabelCorrectedTime.setForeground(Color.black);
		gridbagAdd(fLabelCorrectedTime, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		row++;
		label = new JLabel(res.getString("FinishLabelPenalties"));
		label.setFont(dummy.getFont());
		gridbagAdd(label, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		fLabelPenalty = new JLabel(" ");
		fLabelPenalty.setFont(dummy.getFont());
		fLabelPenalty.setForeground(Color.black);
		gridbagAdd(fLabelPenalty, 1, row, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	}

	public void setFinish(Finish inObj) {

		if (fFinish != null) fFinish.removePropertyChangeListener(this);
		fFinish = inObj;
		if (isVisible() && fFinish != null) fFinish.addPropertyChangeListener(this);

		updateFields();
	}

	@Override
	public void start() {
		if (fFinish != null) fFinish.addPropertyChangeListener(this);
	}

	@Override
	public void stop() {
		if (fFinish != null) fFinish.removePropertyChangeListener(this);
	}

	@Override
	public void updateFields() {
		Regatta reg = JavaScoreProperties.getRegatta();

		if ((fFinish != null) && (fFinish.getEntry() != null)) {
			Entry e = fFinish.getEntry();
			if (e != null) {
				if (e.getBoat() != null) {
					fLabelBoat.setText(e.getBoat().getName());
					fLabelSailId.setText(e.getBoat().getSailId().toString());
				} else {
					fLabelBoat.setText(NONE);
					fLabelSailId.setText(NONE);
				}

				fLabelDivision.setText(fFinish.getDivisionString());
				fLabelSkipper.setText(e.getSkipper().toString());
				if ((reg != null) && reg.isUseBowNumbers()) fLabelBow.setText(e.getBow().toString());
			} else {
				fLabelDivision.setText(EMPTY);
				fLabelBoat.setText(NONE);
				fLabelSailId.setText(NONE);
				fLabelSkipper.setText(NONE);
				if ((JavaScoreProperties.getRegatta() != null) && (JavaScoreProperties.getRegatta().isUseBowNumbers())) {
					fLabelBow.setText(NONE);
				}
			}

			fLabelFinishTime.setText(SailTime.toString(fFinish.getFinishTime()));
			fLabelOrder.setText(fFinish.getFinishPosition().toString());
			fLabelElapsedTime.setText(SailTime.toString(fFinish.getElapsedTime()));
			fLabelCorrectedTime.setText(SailTime.toString(fFinish.getCorrectedTime()));
			fLabelPenalty.setText(Penalty.toString(fFinish.getPenalty()));
		} else {
			fLabelDivision.setText(EMPTY);
			fLabelSailId.setText("                 ");
			fLabelFinishTime.setText(EMPTY);
			fLabelOrder.setText(EMPTY);
			fLabelBow.setText(EMPTY);
			fLabelBoat.setText(EMPTY);
			fLabelSkipper.setText(EMPTY);
			fLabelElapsedTime.setText(EMPTY);
			fLabelCorrectedTime.setText(EMPTY);
			fLabelPenalty.setText(EMPTY);
		}
	}

	public void vetoableChange(PropertyChangeEvent de) throws PropertyVetoException {
		updateFields();
	}

	public void propertyChange(PropertyChangeEvent ev) {
		try {
			vetoableChange(ev);
		}
		catch (Exception e) {}
	}

}
/**
 * $Log: PanelFinish.java,v $ Revision 1.5 2006/05/19 05:48:42 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.9 2003/04/27 21:35:34 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.8 2003/04/27 21:05:59 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/03/19 02:38:23 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.6 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
