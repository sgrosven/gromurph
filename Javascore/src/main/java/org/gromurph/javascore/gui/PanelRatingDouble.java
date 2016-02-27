// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: PanelRatingDouble.java,v 1.5 2006/05/19 05:48:43 sandyg Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.ratings.RatingDouble;
import org.gromurph.util.BaseEditor;
import org.gromurph.util.BaseEditorContainer;
import org.gromurph.util.HelpManager;
import org.gromurph.util.JTextFieldSelectAll;
import org.gromurph.util.Util;

/**
 * Panel for editing a rating with a single numerical value.
 **/
public class PanelRatingDouble extends BaseEditor<RatingDouble> implements ActionListener//, FocusListener
{

	protected RatingDouble fRating;

	static ResourceBundle res = JavaScoreProperties.getResources();
	JTextField fTextRating;

	public PanelRatingDouble(BaseEditorContainer parent) {
		super(parent);
		addFields();
	}

	//  should not be able to get an invalid rating going in
	//    public void setObject( BaseObjectModel obj) throws ClassCastException
	//    {
	//        fRating = (RatingDouble) obj;
	//        if (fRating != null && fDivision != null && !fDivision.contains(fRating))
	//        {
	//            fRating.setValue( fDivision.getSlowestRating().getValue());
	//        }
	//        super.setObject(obj);
	//    }

	private double minAllowed = Double.NaN;
	private double maxAllowed = Double.NaN;
	
	public void setDivisionLimits(Division div) {
		if (div == null) {
			minAllowed = Double.NaN;
			maxAllowed = Double.NaN;
		} else {
			minAllowed = div.getSlowestRating().getPrimaryValue();
			maxAllowed = (div.getFastestRating() != null) ? div.getFastestRating().getPrimaryValue() : minAllowed;
		}
		updateFields();
	}

	@Override
	public void setObject(RatingDouble obj) throws ClassCastException {
		fRating = obj;
		super.setObject(obj);
	}

	public void addFields() {
		setLayout(new GridBagLayout());
		HelpManager.getInstance().registerHelpTopic(this, "rating");

		int row = 0;
		fTextRating = new JTextFieldSelectAll(5);
		fTextRating.setName("fTextRating");
		fTextRating.setToolTipText(res.getString("RatingDoubleValueToolTip"));
		HelpManager.getInstance().registerHelpTopic(fTextRating, "rating.fTextRating");
		gridbagAdd(fTextRating, 0, row, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
	}

	
	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		if (fTextRating != null) fTextRating.setToolTipText(text);
	}

	@Override
	public void updateFields() {
		if (fRating != null) {
			fTextRating.setText(Util.formatDouble(fRating.getPrimaryValue(), fRating.getDecs()));
		} else {
			fTextRating.setText("");
		}
	}

	@Override
	public void restore(RatingDouble a, RatingDouble b) {
		if (a == b) return;
		RatingDouble active = (RatingDouble) a;
		RatingDouble backup = (RatingDouble) b;

		active.setPrimaryValue(backup.getPrimaryValue());
		super.restore(active, backup);
	}

	public void propertyChange(PropertyChangeEvent de) {}

	@Override
	public void start() {
		fTextRating.addActionListener(this);
	}

	@Override
	public void stop() {
		fTextRating.removeActionListener(this);
	}

	boolean editing = false;

	public void actionPerformed(ActionEvent event) {
		fTextRating_actionPerformed();
		if (getEditorParent() != null) getEditorParent().eventOccurred(this, event);
	}

	private void fTextRating_actionPerformed() {
		if (fRating == null || editing) return;
		String oldvalue = Util.formatDouble(fRating.getPrimaryValue(), fRating.getDecs());
		try {
			double val = org.gromurph.util.Util.parseDouble(fTextRating.getText());
			boolean valueOK = true;
			if (!Double.isNaN(minAllowed) && val < minAllowed) valueOK = false;
			if (!Double.isNaN(maxAllowed) && val > maxAllowed) valueOK = false;
			if (valueOK) {
				fRating.setPrimaryValue(val);
			} else {
				editing = true;
				String msg = MessageFormat.format(
						res.getString("RatingDoubleMessageOutOfRange"),
						new Object[] {
								Util.formatDouble(minAllowed, fRating.getDecs()),
								Util.formatDouble(maxAllowed, fRating.getDecs()) });
				JOptionPane.showMessageDialog(this, msg);
				fTextRating.setText(oldvalue);
				editing = false;
				fTextRating.requestFocusInWindow();
			}
			 
		}
		catch (NumberFormatException e) {
			editing = true;
			StringBuffer sb = new StringBuffer();
			sb.append(res.getString("RatingDoubleMessageInvalidNumber"));
			sb.append(NEWLINE);
			sb.append(NEWLINE);
			sb.append(e.toString());
			JOptionPane.showMessageDialog(this, sb.toString());
			fTextRating.setText(oldvalue);
			editing = false;
			fTextRating.requestFocusInWindow();
		}
	}

}
/**
 * $Log: PanelRatingDouble.java,v $ Revision 1.5 2006/05/19 05:48:43 sandyg final release 5.1 modifications
 * 
 * Revision 1.4 2006/01/15 21:10:40 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:20:26 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:02 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.11.4.1 2005/11/30 02:51:25 sandyg added auto focuslost to JTextFieldSelectAll. Removed focus lost checks
 * on text fields in panels.
 * 
 * Revision 1.11 2004/04/10 20:49:38 sandyg Copyright year update
 * 
 * Revision 1.10 2003/05/07 01:17:06 sandyg removed unneeded method parameters
 * 
 * Revision 1.9 2003/04/27 21:35:35 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.8 2003/04/27 21:06:00 sandyg lots of cleanup, unit testing for 4.1.1 almost complete
 * 
 * Revision 1.7 2003/03/19 02:38:24 sandyg made start() stop() abstract to BaseEditor, the isStarted check now done in
 * BaseEditor.startUp and BaseEditor.shutDown().
 * 
 * Revision 1.6 2003/03/16 20:38:32 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.5 2003/01/04 17:39:32 sandyg Prefix/suffix overhaul
 * 
 */
