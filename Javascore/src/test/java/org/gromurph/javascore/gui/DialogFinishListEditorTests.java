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
import javax.swing.JTable;
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
import org.uispec4j.Table;

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
		public int getNumberUnFinished()    	   { return fModelUnFinished.size();}
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
		
	}
	
	private void initialize1() {
		// creates 5 entries;  1,2,3 get finishes finished in race 1,  4 and 5 do not
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
	
	public void testDeleteFinish()
	{
		initialize1();  // have 5 entries, 1,2,3 are finished in race 1,  4 and 5 are not

		// give 4 and 5 finishes
		race.setFinish( new Finish( race, e4, SailTime.NOTIME, new FinishPosition(4), new Penalty(Constants.NO_PENALTY)));
		race.setFinish( new Finish( race, e5, SailTime.NOTIME, new FinishPosition(5), new Penalty(Constants.NO_PENALTY)));

		LocalDialogFinishListEditor editor = new LocalDialogFinishListEditor();
		editor.setRace( race);
		displayDialog( editor);
		
 		assertNotNull( "DialogFinishListEditor is null", editor);

		// get handle to unfinished list
		JList unfinishList = (JList) findComponent( JList.class, "fListUnFinished", editor);
		assertNotNull( "unfinishList is null", unfinishList);
								
		// test size of unfinished list 
		assertEquals( "unfinish jlist should have no elements", 0, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have no elements", 0, editor.getNumberUnFinished());
		
		// get handle to finishtable
		selectTableRow( "fTableFinished", 2);  // should be entry 3
		assertEquals("fTableFinished should have 5 rows", 5, getTableRowCount("fTableFinished"));
		Object cell = getTableContent( "fTableFinished", 2, 1);
		assertNotNull( cell);
		assertEquals( e3.getBoat().getSailId().toString(), cell.toString());

		clickOnButton("fButtonDelete");
		
		// should now have 1 unfinished boat, table row count still 5, last row blank
		assertEquals( "unfinish jlist should have 1 element", 1, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have 1 element", 1, editor.getNumberUnFinished());
		assertEquals("fTableFinished should have 5 rows", 5, getTableRowCount("fTableFinished"));
		cell = getTableContent( "fTableFinished", 4, 1); // zero based for row 5, col 2
		assertNotNull( cell);
		assertEquals( "last row, 2nd col should be empty", "", cell.toString());

		// now select 1st row, and check lists
		selectTableRow( "fTableFinished", 0);  // should be entry 1
		assertEquals( "row 0 not right", e1.getBoat().getSailId().toString(), getTableContent("fTableFinished", 0,1));
		assertEquals( "row 1 not right", e2.getBoat().getSailId().toString(), getTableContent("fTableFinished", 1,1));
		assertEquals( "row 2 not right", e4.getBoat().getSailId().toString(), getTableContent("fTableFinished", 2,1));
		assertEquals( "row 3 not right", e5.getBoat().getSailId().toString(), getTableContent("fTableFinished", 3,1));
		assertEquals( "row 4 not right", "", getTableContent("fTableFinished", 4,1));
		assertEquals("should have 1 unfinished", 1, editor.getNumberUnFinished());
		
		// click on Ok, should close window
		clickOnButton( "fButtonOk", true);
		
		// re-open the window, should still have same status
		editor.setRace( race);
		displayDialog( editor);
		assertEquals( "unfinish jlist should have 1 element", 1, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have 1 element", 1, editor.getNumberUnFinished());
		assertEquals("fTableFinished should have 5 rows", 5, getTableRowCount("fTableFinished"));
		cell = getTableContent( "fTableFinished", 4, 1); // zero based for row 5, col 2
		assertNotNull( cell);
		assertEquals( "last row, 2nd col should be empty", "", cell.toString());
		
		// click on Ok, should close window
		clickOnButton( "fButtonOk", true);
		
	
	}

	public void testFinishRemainingDNC()
	{
		initialize1();  // have 5 entries, 1,2,3 are finished in race 1,  4 and 5 are not

		LocalDialogFinishListEditor editor = new LocalDialogFinishListEditor();
		editor.setRace( race);
		displayDialog( editor);
		
 		assertNotNull( "DialogFinishListEditor is null", editor);

		// get handles to unfinished list
		JList unfinishList = (JList) findComponent( JList.class, "fListUnFinished", editor);
		assertNotNull( "unfinishList is null", unfinishList);
								
		// test current size of unfinishlist
		assertEquals( "unfinish jlist should have 2 elements", 2, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have 2 elements", 2, editor.getNumberUnFinished());

		// show FinishRemainingButton, click DNC
		clickButtonInModalPopup( "fButtonFinishRemaining", "DNC");
		
		// test size of unfinished list 
		assertEquals( "unfinish jlist should have no elements", 0, unfinishList.getModel().getSize());
		assertEquals( "unfinishlist should have no elements", 0, editor.getNumberUnFinished());
		
		// click on Ok, should close window
		clickOnButton( "fButtonOk", true);
	}
			
 }
