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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.gromurph.javascore.JavaScoreProperties;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.util.Person;
import org.sailing.util.SailingServices;
import org.slf4j.LoggerFactory;

/**
 * Fires off a request to ISAF's web services to validate the sailor ids, and then parses the returned invalid ids
 */
public class ActionValidateSailors extends AbstractAction {
	
	static ResourceBundle res = JavaScoreProperties.getResources();

	public ActionValidateSailors() {
		super(res.getString("MenuValidateSailor"));
		putValue(Action.MNEMONIC_KEY, new Integer(res.getString("MenuValidateSailorMnemonic").charAt(0)));
	}

	private Map<String,Person> sailors = new TreeMap<String,Person>();
	private Map<String,Entry> entries = new TreeMap<String,Entry>();
	
	public void actionPerformed(ActionEvent parm1) {
		Regatta reg = JavaScoreProperties.getRegatta();
		List<String> everyone = new ArrayList<String>();
		
		for( Entry e : reg.getAllEntries()) {
			List<Person> crew = e.getCrewList();
			for( Person p : e.getCrewList()) {
				sailors.put( p.getSailorId(), p);
				entries.put( p.getSailorId(), e);
				everyone.add(p.getSailorId());
			}
		}
		
		List<String> invalidIds = new ArrayList<String>();
		
		SailingServices services = new SailingServices();
		List<org.sailing.xrr.Person> validIds = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			validIds = services.validateSailors( everyone, invalidIds);
		} catch (Exception e) {
			pw.println("An error occured trying to reach ISAF's server:");
			pw.println( e.toString());
		}
		
		if (validIds.size() == everyone.size()) {
			pw.println("All sailorIds are valid");
		} else if (invalidIds.size() > 0) {
			pw.println("The following sailorids are not valid: ");
			for (String s : invalidIds) {
				Person p = sailors.get(s);
				Entry entry = entries.get(s);
				pw.print( "   "); pw.print( s); pw.print(" / ");
				pw.print( p.getFirst()); pw.print(" "); pw.print(p.getLast());
				pw.println();
			}
		}
		pw.close();
		LoggerFactory.getLogger(this.getClass()).debug(sw.toString());
		
	}
}
/**
 * $Log$
 */
