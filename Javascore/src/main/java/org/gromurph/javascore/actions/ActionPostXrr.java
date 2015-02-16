// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id$
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.actions;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Util;
import org.slf4j.LoggerFactory;

/**
 * Fires off a request to ISAF's web services to validate the sailor ids, and then parses the returned invalid ids
 */
public class ActionPostXrr extends AbstractAction {
	
	static ResourceBundle res = JavaScoreProperties.getResources();

	public ActionPostXrr() {
		super(res.getString("MenuPostXrr"));
		putValue(Action.MNEMONIC_KEY, new Integer(res.getString("MenuPostXrrMnemonic").charAt(0)));
	}

	public void actionPerformed(ActionEvent parm1) {
		String response = "Initializing";
		try {
			Regatta reg = JavaScoreProperties.getRegatta();
			
			Map<String,String> elist = new TreeMap<String,String>();
			Xrr1_3Manager m = new Xrr1_3Manager();
			boolean isValid = m.postXrr( reg, elist);

			if (!isValid) {
				Util.createAndShowErrorPanel("XRR is not valid", elist);
			} else {
    			// show reply to user
				response = elist.get( Xrr1_3Manager.ISAF_REPLY);
    			JOptionPane.showMessageDialog(null, response, 
    					Xrr1_3Manager.ISAF_REPLY, JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (RuntimeException e) {
			Util.showError(e,  true);
		} catch (Exception e) {
			Util.showError(e,  true);
		}
		
		LoggerFactory.getLogger(this.getClass()).debug(response);
	}
}
/**
 * $Log$
 */
