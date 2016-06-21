//=== File Prolog===========================================================
// This code was developed as part of the open source fRegatta scoring
// program, JavaScore.
//
// Version: $Id: DialogFinishListEditorTests.java,v 1.4 2006/01/15 21:08:39 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================
package org.gromurph.javascore.gui;

import javax.swing.JButton;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingOneDesign;

/**
 * Tests on the Division Panel
 */
public class DialogEntryTreeEditorTests extends JavascoreTestCase
{
    
	public DialogEntryTreeEditorTests(String name)
	{
		super(name);
	}
	
	public class LocalDialogEntryTreeEditor extends DialogEntryTreeEditor
	{
		public LocalDialogEntryTreeEditor()       { super( null); }	
		public void setDivision( Division div) {
			EntryTreeModel m = this.getTreeModel();
			m.setDivisionNode(div);
		}
		public void setEntry( Entry e) {
			this.setObject(e);
		}
	}
	
	Division div;
	Entry e1, e2, e3, e4, e5;
	Race race;
	
	@Override public void setUp() throws Exception
	{
		super.setUp();
		
	}
	
	private void initializeTest1() {
		assertNotNull( fRegatta);
		
		fRegatta.removeAllDivisions();
		
		div = new Division( "Laser", new RatingOneDesign("Laser"), new RatingOneDesign("Laser"));
		fRegatta.addDivision(div);
		
		e1 = new Entry();  
		Boat b = new Boat( "boat1", "1", "f1 l1"); 
		b.putRating( new RatingOneDesign("Laser"));
		e1.setBoat(b);
		try { e1.setDivision( div); } catch (RatingOutOfBoundsException ex) {}
		
		e2 = new Entry();  
		b = new Boat( "boat2", "2", "f2 l2"); 
		b.putRating( new RatingOneDesign("Laser"));
		e2.setBoat(b);
		try { e2.setDivision( div); } catch (RatingOutOfBoundsException ex) {}
		
		e3 = new Entry();  
		b = new Boat( "boat3", "3", "f3 l3"); 
		b.putRating( new RatingOneDesign("Laser"));
		e3.setBoat(b);
		try { e3.setDivision( div); } catch (RatingOutOfBoundsException ex) {}
		
		e4 = new Entry();  
		b = new Boat( "boat4", "4", "f4 l4"); 
		b.putRating( new RatingOneDesign("Laser"));
		e4.setBoat(b);
		try { e4.setDivision( div); } catch (RatingOutOfBoundsException ex) {}
		
		e5 = new Entry();  
		b = new Boat( "boat5", "5", "f5 l5"); 
		b.putRating( new RatingOneDesign("Laser"));
		e5.setBoat(b);
		try { e5.setDivision( div); } catch (RatingOutOfBoundsException ex) {}
		
		fRegatta.addEntry( e1);	
		fRegatta.addEntry( e2);	
		fRegatta.addEntry( e3);	
		fRegatta.addEntry( e4);	
		fRegatta.addEntry( e5);	
		
		race = new Race( fRegatta, "1");
		race.setFinish( new Finish( race, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Constants.NO_PENALTY)));
		race.setFinish( new Finish( race, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Constants.NO_PENALTY)));
		race.setFinish( new Finish( race, e3, SailTime.NOTIME, new FinishPosition(3), new Penalty(Constants.NO_PENALTY)));
	}
	
	public void testAddDeleteEnabling()
	{
		fRegatta = new Regatta();
		
		// have 5 entries, 1,2,3 are finished in race 1,  4 and 5 are not
		LocalDialogEntryTreeEditor editor = new LocalDialogEntryTreeEditor();
 		assertNotNull( "DialogEntryTreeEditor is null", editor);

 		editor.setRegatta( fRegatta);
		displayDialog( editor);
		
		// no division is selected so no add, no delete
		assertFalse( "fButtonAdd should NOT be enabled", getJButton( "fButtonAdd").isEnabled());
		assertFalse( "fButtonDelete should NOT be enabled", getJButton( "fButtonDelete").isEnabled());

		editor.setDivision( null);
		// no division is selected so no add, no delete
		assertFalse( "fButtonAdd should NOT be enabled", getJButton( "fButtonAdd").isEnabled());
		assertFalse( "fButtonDelete should NOT be enabled", getJButton( "fButtonDelete").isEnabled());
		
	}
			
	
 }
