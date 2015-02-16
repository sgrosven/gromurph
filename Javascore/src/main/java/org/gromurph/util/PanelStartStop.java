// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: BaseEditor.java,v 1.4 2006/01/15 21:10:34 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * PanelStartStop - splitting BaseEditor into a simpler parent
 * 
 **/
public abstract class PanelStartStop extends JPanel {

	/**
	 * updates the fields in the BaseEditor with current information
	 **/
	public abstract void updateFields();

	/**
	 * initiate action/event listeners, will only be called on "new" startup
	 */
	public abstract void start(); // define in subclasses

	/**
	 * delete action/event listeners, will only be called on "new" stop
	 */
	public abstract void stop(); // define in subclasses

	protected boolean isStarted = false;

	/**
	 * fired when the editor is about to become active to the user
	 **/
	public void startUp() {
		updateFields();

		if (!isStarted) {
			isStarted = true;
			start();
		}
		
	}

	/**
	 * fired when the editor is about to become de-active to the user
	 **/
	public void shutDown() {
		if (isStarted) {
			isStarted = false;
			stop();
		}
	}

	private static GridBagConstraints gbc = new GridBagConstraints();

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with widths of
	 * 1,1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent newComp, int x, int y, int anchor, int fill) {
		gridbagAdd(this, newComp, x, y, 1, anchor, fill);
	}

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
	 * specified width, height of 1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent newComp, int x, int y, int width, int anchor, int fill) {
		gridbagAdd(this, newComp, x, y, width, anchor, fill);
	}

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
	 * specified width, height of 1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int width, int anchor, int fill) {
		gridbagAdd(target, newComp, x, y, width, 1, anchor, fill, fInsets);
	}

	/**
	 * a supporting method for children with GridBagLayouts. Adds component into specified x,y location with user
	 * specified width, height of 1, and specified anchor and fill
	 **/
	protected void gridbagAdd(JComponent target, JComponent newComp, int x, int y, int wh, int ht, int anchor,
			int fill, Insets ins) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = wh;
		gbc.gridheight = ht;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = anchor;
		gbc.fill = fill;
		gbc.insets = ins;
		((GridBagLayout) target.getLayout()).setConstraints(newComp, gbc);
		target.add(newComp);
	}

	private Insets fInsets = new Insets(5, 5, 5, 5);

	public void setGridBagInsets(Insets i) {
		fInsets = i;
	}

	/**
	 * For facilitating TESTING, build a list of JComponents defined in the module. Returns Map where the keys of
	 * strings containing the component's defined name in the module, and the objects are the JComponents themselves.
	 * 
	 * @see junit.SeaTestCase for additional details
	 */
	public Map<String, JComponent> findMemberComponents() {
		Map<String, JComponent> mbrs = new HashMap<String, JComponent>();
		try {
			java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					Object o = fields[i].get(this);
					String fname = fields[i].getName();
					if (o instanceof JComponent) {
						mbrs.put(fname, (JComponent) o);
					}
				} catch (Exception e) {
					// private variable of parent class ?
				}
			}
		} catch (Exception e) {}
		return mbrs;
	}

}
/**
 * $Log: BaseEditor.java,v $ Revision 1.4 2006/01/15 21:10:34 sandyg resubmit at 5.1.02
 */
