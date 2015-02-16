//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: ListenerList.java,v 1.4 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

package org.gromurph.util;


/**
 * Modifies Vector to better support use in event listeners code
 * The problem with java.util.Vector in event listeners
 * is (a) in adding a listener, we do not want to add it if it already is in
 * the list, (b) in removing a listener, we want to remove all occurrences, and (most
 * critically) the selection should be based on a '==' comparison not the less
 * strict .equals().
 *
 * By using a ListenerVector, the listener support code can stay simple (and
 * converting from java.util.Vector is as as easy as changing the declared class
 * and constructor names... and changing addElement to addListener, and removeElement to
 * removeListener  (sorry bout that... addElement and removeElement are 'final' in Vector)
 */

public class ListenerList extends java.util.ArrayList<Object>
{

	/**
	 * adds an object to the list of listeners.  This varies from
	 * Vector.addElement() in that it (a) will not add the object
	 * if it already is in the list (via an '==' comparison not
	 * a .equals())
	 *
	 * @param obj the object to be added to the vector
	 */
	public synchronized void addListener(Object obj)
	{
		for (int i = 0; i < size(); i++)
		{
			if (get(i) == obj) return;
		}
		// if we get here then it does not exist
		add( obj);
	}
	/**
	 * removes an object from listener vector.  This differs from
	 * java.util.Vector.removeElement() in two ways:
	 * (a) removes all occurences, (b) identifies the existence
	 * via '==' not .equals()
	 *
	 * @param obj the object to be removed from the vector
	 */

	public synchronized void removeListener(Object obj)
	{
		for (int i = size()-1; i >= 0; i--)
		{
			if (get(i) == obj) remove(i);
		}
	}

	public synchronized Object[] getListenerList()
	{
		return toArray();
	}
}
/**
 * $Log: ListenerList.java,v $
 * Revision 1.4  2006/01/15 21:10:35  sandyg
 * resubmit at 5.1.02
 *
 * Revision 1.2  2006/01/11 02:27:14  sandyg
 * updating copyright years
 *
 * Revision 1.1  2006/01/01 02:27:02  sandyg
 * preliminary submission to centralize code in a new module
 *
 * Revision 1.4.4.1  2005/11/01 02:36:02  sandyg
 * Java5 update - using generics
 *
 * Revision 1.4  2004/04/10 20:49:39  sandyg
 * Copyright year update
 *
 * Revision 1.3  2003/04/27 21:03:30  sandyg
 * lots of cleanup, unit testing for 4.1.1 almost complete
 *
 * Revision 1.2  2003/01/04 17:53:05  sandyg
 * Prefix/suffix overhaul
 *
*/
