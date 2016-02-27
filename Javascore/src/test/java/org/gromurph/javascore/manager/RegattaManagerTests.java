package org.gromurph.javascore.manager;

import org.gromurph.javascore.JavascoreTestCase;
import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.Boat;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.SailId;
import org.gromurph.javascore.model.ratings.RatingPhrf;
import org.gromurph.javascore.model.ratings.RatingPhrfTimeOnTime;

public class RegattaManagerTests extends JavascoreTestCase {
	public RegattaManagerTests(String name) {
		super(name);
	}

	public void testGetInvalidEntries() throws Exception {
		Division a = new Division("a", new RatingPhrf(-999), new RatingPhrf(34));
		Division b = new Division("b", new RatingPhrf(35), new RatingPhrf(999));

		Boat b10 = new Boat("10", new SailId("10"), "");
		Boat b20 = new Boat("20", new SailId("20"), "");
		Boat b30 = new Boat("30", new SailId("30"), "");
		Boat b40 = new Boat("40", new SailId("40"), "");
		Boat b50 = new Boat("50", new SailId("50"), "");
		Boat b60 = new Boat("60", new SailId("60"), "");
		Boat b70 = new Boat("70", new SailId("70"), "");

		Entry e10 = null;
		Entry e20 = null;
		Entry e30 = null;
		Entry e40 = null;
		Entry e50 = null;
		Entry e60 = null;
		Entry e70 = null;

		e10 = new Entry();
		e10.setBoat(b10);
		b10.putRating(new RatingPhrf(10));
		e10.setDivision(a);
		e20 = new Entry();
		e20.setBoat(b20);
		b20.putRating(new RatingPhrf(20));
		e20.setDivision(a);
		e30 = new Entry();
		e30.setBoat(b30);
		b30.putRating(new RatingPhrf(30));
		e30.setDivision(a);

		e40 = new Entry();
		e40.setBoat(b40);
		b40.putRating(new RatingPhrf(40));
		e40.setDivision(b);
		e50 = new Entry();
		e50.setBoat(b50);
		b50.putRating(new RatingPhrf(50));
		e50.setDivision(b);
		e60 = new Entry();
		e60.setBoat(b60);
		b60.putRating(new RatingPhrf(60));
		e60.setDivision(b);
		e70 = new Entry();
		e70.setBoat(b70);
		b70.putRating(new RatingPhrf(70));
		e70.setDivision(b);

		EntryList allEntries = new EntryList();

		allEntries.add(e10);
		allEntries.add(e20);
		allEntries.add(e30);
		allEntries.add(e40);
		allEntries.add(e50);
		allEntries.add(e60);
		allEntries.add(e70);

		assertEquals("should have 7 entries", 7, allEntries.size());

		EntryList badA = RatingManager.getInvalidEntries(a, allEntries);
		assertEquals("badA should have 4 entries", 4, badA.size());

		EntryList goodA = RatingManager.getValidEntries(a, allEntries);
		assertEquals("goodA should have 3 entries", 3, goodA.size());
		
		Division atot = new Division("a", new RatingPhrfTimeOnTime(-999), new RatingPhrfTimeOnTime(34));
		EntryList badAtot = RatingManager.getInvalidEntries(atot, allEntries);
		assertEquals("badAtot should have 4 entries", 4, badA.size());
		

	}

}
