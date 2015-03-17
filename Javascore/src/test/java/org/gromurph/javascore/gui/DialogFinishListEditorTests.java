//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
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

import javax.swing.JList;
import javax.swing.table.AbstractTableModel;

import org.gromurph.javascore.Constants;
import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.SailTime;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Finish;
import org.gromurph.javascore.model.FinishList;
import org.gromurph.javascore.model.FinishPosition;
import org.gromurph.javascore.model.Penalty;
import org.gromurph.javascore.model.Race;
import org.gromurph.javascore.model.Regatta;
import org.gromurph.javascore.model.ratings.RatingOneDesign;

/**
 * Tests on the Division Panel
 */
public class DialogFinishListEditorTests extends JavascoreTestCase
{
    
	public DialogFinishListEditorTests(String name)
	{
		super(name);
	}
	
	public class LocalDialogFinishListEditor extends DialogFinishListEditor
	{
		public LocalDialogFinishListEditor()       { super( null); }		
		public EntryList getUnFinishedEntries()    { return fUnFinishedEntries;}
		public FinishList getFinishers()           { return fFinishers;}	
		public AbstractTableModel getFinishModel() { return fFinishModel;}
	}
	
	Regatta regatta;
	Division div;
	Entry e1, e2, e3, e4, e5;
	Race race;
	
	@Override public void setUp() throws Exception
	{
		super.setUp();
		
		regatta = new Regatta();
		
		div = new Division( "Laser", new RatingOneDesign("Laser"), new RatingOneDesign("Laser"));
		
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
		
		regatta.addEntry( e1);	
		regatta.addEntry( e2);	
		regatta.addEntry( e3);	
		regatta.addEntry( e4);	
		regatta.addEntry( e5);	
		
		race = new Race( regatta, "1");
		race.setFinish( new Finish( race, e1, SailTime.NOTIME, new FinishPosition(1), new Penalty(Constants.NO_PENALTY)));
		race.setFinish( new Finish( race, e2, SailTime.NOTIME, new FinishPosition(2), new Penalty(Constants.NO_PENALTY)));
		race.setFinish( new Finish( race, e3, SailTime.NOTIME, new FinishPosition(3), new Penalty(Constants.NO_PENALTY)));
	}
	
	public void testUnfinishChanges()
	{
		// have 5 entries, 1,2,3 are finished in race 1,  4 and 5 are not
		LocalDialogFinishListEditor editor = new LocalDialogFinishListEditor();
		editor.setRace( race);
		displayDialog( editor);
		
 		assertNotNull( "DialogFinishListEditor is null", editor);

		// get handles to unfinished list
		JList unfinishList = (JList) findComponent( JList.class, "fListUnFinished", editor);
		assertNotNull( "unfinishList is null", unfinishList);
								
		// test current size of unfinishlist
		assertEquals( "unfinish jlist should have 2 elements", 2, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have 2 elements", 2, editor.getUnFinishedEntries().size());

		// show FinishRemainingButton, click DNC
		clickButtonInModalPopup( "fButtonFinishRemaining", "DNC");
		
		// test size of unfinished list 
		assertEquals( "unfinish jlist should have no elements", 0, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have no elements", 0, editor.getUnFinishedEntries().size());
		
		// click on Ok, should close window
		clickOnButton( "fButtonOk", true);
	}
			
	
 }
/**
 * $Log: DialogFinishListEditorTests.java,v $
 * Revision 1.4  2006/01/15 21:08:39  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:24:48  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.6.2.1  2005/11/26 17:44:21  sandyg
 * implement race weight & nondiscardable, did some gui test cleanups.
 *
 * Revision 1.6  2004/04/10 22:19:41  sandyg
 * Copyright update
 *
 * Revision 1.5  2003/04/30 01:00:28  sandyg
 * moved variables up to class level in prep for more/better tests
 *
 * Revision 1.4  2003/04/27 21:01:09  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.3  2003/04/23 00:30:15  sandyg
 * added Time-based penalties
 *
 * Revision 1.2  2003/04/20 15:44:29  sandyg
 * added javascore.Constants to consolidate penalty defs, and added
 * new penaltys TIM (time value penalty) and TMP (time percentage penalty)
 *
 * Revision 1.1  2003/04/09 01:58:13  sandyg
 * initial implementation
 *
 */