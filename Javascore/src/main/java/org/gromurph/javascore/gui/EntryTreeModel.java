// === File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: EntryTreeModel.java,v 1.5 2006/01/19 01:50:15 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
// === End File Prolog=======================================================
package org.gromurph.javascore.gui;

import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gromurph.javascore.exception.RatingOutOfBoundsException;
import org.gromurph.javascore.model.AbstractDivision;
import org.gromurph.javascore.model.Division;
import org.gromurph.javascore.model.Entry;
import org.gromurph.javascore.model.EntryList;
import org.gromurph.javascore.model.Regatta;

/**
 * Adapter class that wraps a TreeModel around a Regatta that contains Regatta->Division->Entries
 */
public class EntryTreeModel extends DefaultTreeModel {
	private Regatta fRegatta;
	private DefaultMutableTreeNode fRoot;

	public EntryTreeModel(Regatta reg) {
		super(new DefaultMutableTreeNode(reg.getName()));
		fRegatta = reg;

		fRoot = (DefaultMutableTreeNode) this.getRoot();
		rebuildTree();
	}

	public void rebuildTree() {
		fRoot.removeAllChildren();
		EntryList entriesNoDiv = new EntryList();
		entriesNoDiv.addAll(fRegatta.getAllEntries());

		for (Division div : fRegatta.getDivisions()) {
			DefaultMutableTreeNode divnode = new DivisionTreeNode(div);
			fRoot.add(divnode);

			EntryList thisDiv = div.getEntries();
			entriesNoDiv.removeAll(thisDiv);

			for (Iterator eiter = thisDiv.iterator(); eiter.hasNext();) {
				divnode.add(new EntryTreeNode((Entry) eiter.next()));
			}
		}

		// add none if there are any entries in it
		if (entriesNoDiv.size() > 0) {
			DefaultMutableTreeNode divnode = new DivisionTreeNode(AbstractDivision.NONE);
			fRoot.add(divnode);
			for (Iterator eiter = entriesNoDiv.iterator(); eiter.hasNext();) {
				Entry entry = (Entry) eiter.next();
				try {
					entry.setDivision(AbstractDivision.NONE);
				} catch (RatingOutOfBoundsException e) {}
				divnode.add(new EntryTreeNode(entry));
			}
		}

		this.fireTreeStructureChanged(this, new Object[] { fRoot }, null, null);
	}

	/**
	 * Given an array of objects representing the selection path, creates and adds a new entry in the selected division
	 * if any, and returns that entry
	 */
	public EntryTreeNode addEntry(Object[] path) {
		if (path.length >= 1) {
			// have division selected
			DivisionTreeNode divNode = (DivisionTreeNode) path[1];
			Division div = divNode.getDivision();

			Entry entry = new Entry();
			try {
				entry.setDivision(div);
			} catch (RatingOutOfBoundsException e) {}

			fRegatta.addEntry(entry);

			EntryTreeNode entNode = new EntryTreeNode(entry);
			divNode.add(entNode);

			int entIndex = divNode.getIndex(entNode);

			fireTreeNodesInserted(this, new Object[] { path[0], path[1] }, new int[] { entIndex },
					new Object[] { entNode });
			return entNode;
		} else {
			return null;
		}
	}

	/**
	 * Given an array of objects representing the selection path, creates and adds a new entry in the selected division
	 * if any, and returns that entry
	 */
	public EntryTreeNode deleteEntry(Entry delEntry) {
		Object[] path = getPathToEntry(delEntry).getPath();
		if (path.length >= 2) {
			// have division selected
			DivisionTreeNode divNode = (DivisionTreeNode) path[1];

			EntryTreeNode entNode = (EntryTreeNode) path[2];
			Entry entry = entNode.getEntry();
			int entIndex = divNode.getIndex(entNode);

			fRegatta.removeEntry(entry);

			divNode.remove(entNode);
			fireTreeNodesRemoved(this, new Object[] { path[0], path[1] }, new int[] { entIndex },
					new Object[] { entNode });
			return entNode;
		} else {
			return null;
		}
	}

	boolean handlingDivChange = false;

	public void updateEntry(Object[] path) {
		if (path.length >= 3) {
			// have division selected
			DivisionTreeNode divNode = (DivisionTreeNode) path[1];
			Division div = divNode.getDivision();

			EntryTreeNode entNode = (EntryTreeNode) path[2];
			Entry entry = entNode.getEntry();
			int entIndex = divNode.getIndex(entNode);

			if (entry.getDivision().equals(div)) {
				// division is same, just update the node
				fireTreeNodesChanged(this, new Object[] { path[0], path[1] }, new int[] { entIndex },
						new Object[] { entNode });
			} else if (!handlingDivChange) {
				int newindex = getDivisionNodeIndex(entry.getDivision());
				if (entry.getDivision().equals(AbstractDivision.NONE) && newindex < 0) {
					// entry division changing to NONE, and no NONE previously existed
					fRoot.add(new DivisionTreeNode(AbstractDivision.NONE));
					newindex = getDivisionNodeIndex(entry.getDivision());
				}

				DivisionTreeNode newdivNode = (DivisionTreeNode) fRoot.getChildAt(newindex);

				divNode.remove(entNode);
				newdivNode.add(entNode);

				int oldindex = ((TreeNode) path[0]).getIndex(divNode);

				// now see if we should get rid of NONE division node
				if (divNode.getDivision().equals(AbstractDivision.NONE) && divNode.getChildCount() == 0) {
					fRoot.remove(divNode);
				}

				handlingDivChange = true;
				fireTreeStructureChanged(this, new Object[] { path[0] }, new int[] { oldindex, newindex },
						new Object[] { divNode, newdivNode });
				handlingDivChange = false;
			}
		}
	}

	/**
	 * returns the path to an entry. Returns null if no matching entry found
	 */
	public TreePath getPathToEntry(Entry e) {
		Enumeration enDiv = fRoot.children();
		TreePath tp = new TreePath(fRoot);

		Division div = e.getDivision();
		while (enDiv.hasMoreElements()) {
			DivisionTreeNode divNode = (DivisionTreeNode) enDiv.nextElement();
			if (div.equals(divNode.getDivision())) {
				tp = tp.pathByAddingChild(divNode);
				Enumeration enEnt = divNode.children();
				while (enEnt.hasMoreElements()) {
					EntryTreeNode entNode = (EntryTreeNode) enEnt.nextElement();
					if (entNode.getEntry().equals(e)) {
						tp = tp.pathByAddingChild(entNode);
						return tp;
					}
				}
			}
		}
		return null;
	}

	public DivisionTreeNode getDivisionNode(int i) {
		if (i < 0) return null;
		return (DivisionTreeNode) fRoot.getChildAt(i);
	}

	public DivisionTreeNode getDivisionNode(Division div) {
		int newindex = getDivisionNodeIndex(div);
		return getDivisionNode(newindex);
	}

	public int getDivisionNodeIndex(Division div) {
		if (div == null) return -1;
		java.util.Enumeration iter = fRoot.children();
		int i = 0;
		while (iter.hasMoreElements()) {
			DivisionTreeNode newdivNode = (DivisionTreeNode) iter.nextElement();
			if (div.equals(newdivNode.getDivision())) return i;
			i++;
		}
		return -1;
	}

	public void setDivisionNode( Division div) {
		DivisionTreeNode node = getDivisionNode(div);
		if (node != null) {
			TreeNode[] tp = this.getPathToRoot( node);
			this.updateEntry( tp);
		}
	}
	
	
	/**
	 * Inner class wrapping a MutableTreeNode model around a Division
	 */
	public static class DivisionTreeNode extends DefaultMutableTreeNode {
		Division div;

		public DivisionTreeNode(Division d) {
			div = d;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(div.toString());
			sb.append(" (");
			sb.append(getChildCount());
			sb.append(")");
			return sb.toString();
		}

		public Division getDivision() {
			return div;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			try {
				return div.equals(((DivisionTreeNode) o).getDivision());
			} catch (ClassCastException ex) {
				try {
					return div.equals(o);
				} catch (ClassCastException ex2) {
					return false;
				}
			}
		}
	}

	/**
	 * Inner class wrapping a MutableTreeNode model around a Entry
	 */
	public static class EntryTreeNode extends DefaultMutableTreeNode {
		Entry entry;

		public EntryTreeNode(Entry d) {
			entry = d;
		}

		@Override
		public String toString() {
			return entry.toString(Entry.SHOW_BOW + Entry.SHOW_BOAT + Entry.SHOW_SKIPPER, false);
		}

		public Entry getEntry() {
			return entry;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			try {
				return entry.equals(((EntryTreeNode) o).getEntry());
			} catch (ClassCastException ex) {
				try {
					return entry.equals(o);
				} catch (ClassCastException ex2) {
					return false;
				}
			}
		}
	}

}
/**
 * $Log: EntryTreeModel.java,v $ Revision 1.5 2006/01/19 01:50:15 sandyg fixed several bugs in split fleet scoring
 * 
 * Revision 1.4 2006/01/15 21:10:37 sandyg resubmit at 5.1.02
 * 
 * Revision 1.2 2006/01/11 02:26:10 sandyg updating copyright years
 * 
 * Revision 1.1 2006/01/01 02:27:01 sandyg preliminary submission to centralize code in a new module
 * 
 * Revision 1.10.4.1 2005/11/01 02:36:01 sandyg Java5 update - using generics
 * 
 * Revision 1.10 2004/04/10 20:49:28 sandyg Copyright year update
 * 
 * Revision 1.9 2003/04/27 21:36:13 sandyg more cleanup of unused variables... ALL unit tests now working
 * 
 * Revision 1.8 2003/03/16 20:39:14 sandyg 3.9.2 release: encapsulated changes to division list in Regatta, fixed a bad
 * bug in PanelDivsion/Rating
 * 
 * Revision 1.7 2003/01/06 00:32:37 sandyg replaced forceDivision and forceRating statements
 * 
 * Revision 1.6 2003/01/04 17:29:09 sandyg Prefix/suffix overhaul
 * 
 */
