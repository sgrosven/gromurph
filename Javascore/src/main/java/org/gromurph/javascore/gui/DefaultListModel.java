//=== File Prolog===========================================================
// This code was developed as part of the open source regatta scoring
// program, JavaScore.
//
// Version: $Id: DefaultListModel.java,v 1.5 2006/01/15 21:10:35 sandyg Exp $
//
// Copyright Sandy Grosvenor, 2000-2015 
// Email sandy@gromurph.org, www.gromurph.org/javascore
//
// OSI Certified Open Source Software (www.opensource.org)
// This software is licensed under the GNU General Public License,
// available at www.opensource.org/licenses/gpl-license.html
//=== End File Prolog=======================================================

/*
 * Adapted from SUn's DefaultListModel, this implements List via ArrayList
 * as its delegate
 *
 * @(#)DefaultListModel.java	1.21 98/10/06
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

//package javax.swing;
package org.gromurph.javascore.gui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Adapted from SUn's DefaultListModel, this implements List via ArrayList
 * as its delegate
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.21 10/06/98
 * @author Hans Muller
 */
public class DefaultListModel extends javax.swing.AbstractListModel
{
    private ArrayList delegate = null;

    public DefaultListModel( ArrayList inList)
    {
        super();
        delegate = inList;
    }

    /**
     * Returns the number of components in this list.
     * <p>
     * This method is identical to <tt>size()</tt>, which implements the
     * <tt>List</tt> interface defined in the 1.2 Collections framework.
     * This method exists in conjunction with <tt>setSize()</tt> so that
     * "size" is identifiable as a JavaBean property.
     *
     * @return  the number of components in this list.
     * @see #size()
     */
    public int getSize() {
    return delegate.size();
    }

    /**
     * Returns the component at the specified index.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>get(int)</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     * @param      index   an index into this list.
     * @return     the component at the specified index.
     * @exception  ArrayIndexOutOfBoundsException  if the <tt>index</tt>
     *             is negative or not less than the current size of this
     *             list.
     *             given.
     * @see #get(int)
     */
    public Object getElementAt(int index) {
    return delegate.get(index);
    }

    /**
     * Copies the components of this list into the specified array.
     * The array must be big enough to hold all the objects in this list,
     * else an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param   anArray   the array into which the components get copied.
     * @see Vector#copyInto(Object[])
     */
    @SuppressWarnings("unchecked") public void copyInto(Object anArray[]) {
    	
    delegate.toArray(anArray);
    }

    /**
     * Trims the capacity of this list to be the list's current size.
     *
     * @see Vector#trimToSize()
     */
    public void trimToSize() {
    delegate.trimToSize();
    }

    /**
     * Increases the capacity of this list, if necessary, to ensure
     * that it can hold at least the number of components specified by
     * the minimum capacity argument.
     *
     * @param   minCapacity   the desired minimum capacity.
     * @see Vector#ensureCapacity(int)
     */
    public void ensureCapacity(int minCapacity) {
    delegate.ensureCapacity(minCapacity);
    }

//    /**
//     * Sets the size of this list.
//     *
//     * @param   newSize   the new size of this list.
//     * @see Vector#setSize(int)
//     */
//    public void setSize(int newSize) {
//    }

//    /**
//     * Returns the current capacity of this list.
//     *
//     * @return  the current capacity
//     * @see Vector#capacity()
//     */
//    public int capacity() {
//    return delegate.capacity();
//    }

    /**
     * Returns the number of components in this list.
     *
     * @return  the number of components in this list.
     * @see Vector#size()
     */
    public int size() {
    return delegate.size();
    }

    /**
     * Tests if this list has no components.
     *
     * @return  <code>true</code> if and only if this list has
     *          no components, that is, its size is zero;
     *          <code>false</code> otherwise.
     * @see Vector#isEmpty()
     */
    public boolean isEmpty() {
    return delegate.isEmpty();
    }

    /**
     * Returns an enumeration of the components of this list.
     *
     * @return  an enumeration of the components of this list.
     * @see Vector#elements()
     */
    public Enumeration elements() {
        return new IteratorToEnum( delegate);
    }

    public class IteratorToEnum implements Enumeration
    {
        private Iterator iterator;
        public IteratorToEnum( java.util.Collection coll) { iterator = coll.iterator(); }
        public boolean hasMoreElements() { return iterator.hasNext(); }
        public Object nextElement() { return iterator.next(); }
    }

    /**
     * Tests if the specified object is a component in this list.
     *
     * @param   elem   an object.
     * @return  <code>true</code> if the specified object
     *          is the same as a component in this list
     * @see Vector#contains(Object)
     */
    public boolean contains(Object elem) {
    return delegate.contains(elem);
    }

    /**
     * Searches for the first occurence of the given argument.
     *
     * @param   elem   an object.
     * @return  the index of the first occurrence of the argument in this
     *          list; returns <code>-1</code> if the object is not found.
     * @see Vector#indexOf(Object)
     */
    public int indexOf(Object elem) {
    return delegate.indexOf(elem);
    }

    /**
     * Searches for the first occurence of the given argument, beginning
     * the search at <code>index</code>.
     *
     * @param   elem    an object.
     * @param   index   the index to start searching from.
     * @return  the index of the first occurrence of the object argument in
     *          this list at position <code>index</code> or later in the
     *          list; returns <code>-1</code> if the object is not found.
     * @see Vector#indexOf(Object,int)
     */
     public int indexOf(Object elem, int index) {
      int i = delegate.indexOf(elem);
      return ( i > index) ? -1 : i;
    }

    /**
     * Returns the index of the last occurrence of the specified object in
     * this list.
     *
     * @param   elem   the desired component.
     * @return  the index of the last occurrence of the specified object in
     *          this list; returns <code>-1</code> if the object is not found.
     * @see Vector#lastIndexOf(Object)
     */
    public int lastIndexOf(Object elem) {
    return delegate.lastIndexOf(elem);
    }

    /**
     * Searches backwards for the specified object, starting from the
     * specified index, and returns an index to it.
     *
     * @param  elem    the desired component.
     * @param  index   the index to start searching from.
     * @return the index of the last occurrence of the specified object in this
     *          list at position less than <code>index</code> in the
     *          list; <code>-1</code> if the object is not found.
     * @see Vector#lastIndexOf(Object,int)
     */
    public int lastIndexOf(Object elem, int index) {
      int i = delegate.lastIndexOf(elem);
      return ( i < index) ? -1 : i;
    }

    /**
     * Returns the component at the specified index.
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index
     * is negative or not less than the size of the list.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>get(int)</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      index   an index into this list.
     * @return     the component at the specified index.
     * @see #get(int)
     * @see Vector#elementAt(int)
     */
    public Object elementAt(int index) {
    return delegate.get(index);
    }

    /**
     * Returns the first component of this list.
     * Throws a <tt>NoSuchElementException</tt> if this vector has no components     *
     * @return     the first component of this list
     * @see Vector#firstElement()
     */
    public Object firstElement() {
        try
        {
            return delegate.get( 0);
        }
        catch (IndexOutOfBoundsException e) { throw new NoSuchElementException( "lastElement"); }
    }

    /**
     * Returns the last component of the list.
     * Throws a <tt>NoSuchElementException</tt> if this vector has no components     *
     *
     * @return  the last component of the list
     * @see Vector#lastElement()
     */
    public Object lastElement() {
        try
        {
            return delegate.get( delegate.size()-1);
        }
        catch (IndexOutOfBoundsException e) { throw new NoSuchElementException( "lastElement"); }
    }

    /**
     * Sets the component at the specified <code>index</code> of this
     * list to be the specified object. The previous component at that
     * position is discarded.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>set(int,Object)</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      obj     what the component is to be set to.
     * @param      index   the specified index.
     * @see #set(int,Object)
     * @see Vector#setElementAt(Object,int)
     */
    @SuppressWarnings("unchecked") public void setElementAt(Object obj, int index) {
    delegate.set( index, obj);
    fireContentsChanged(this, index, index);
    }

    /**
     * Deletes the component at the specified index.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>remove(int)</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      index   the index of the object to remove.
     * @see #remove(int)
     * @see Vector#removeElementAt(int)
     */
    public void removeElementAt(int index) {
    delegate.remove(index);
    fireIntervalRemoved(this, index, index);
    }

    /**
     * Inserts the specified object as a component in this list at the
     * specified <code>index</code>.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>add(int,Object)</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      obj     the component to insert.
     * @param      index   where to insert the new component.
     * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
     * @see #add(int,Object)
     * @see Vector#insertElementAt(Object,int)
     */
    @SuppressWarnings("unchecked") public void insertElementAt(Object obj, int index) {
    delegate.add( index, obj);
    fireIntervalAdded(this, index, index);
    }

    /**
     * Adds the specified component to the end of this list.
     *
     * @param   obj   the component to be added.
     * @see Vector#addElement(Object)
     */
    @SuppressWarnings("unchecked") public void addElement(Object obj) {
    int index = delegate.size();
    delegate.add(obj);
    fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument
     * from this list.
     *
     * @param   obj   the component to be removed.
     * @return  <code>true</code> if the argument was a component of this
     *          list; <code>false</code> otherwise.
     * @see Vector#removeElement(Object)
     */
    public boolean removeElement(Object obj) {
    int index = indexOf(obj);
    boolean rv = delegate.remove(obj);
    if (index >= 0) {
        fireIntervalRemoved(this, index, index);
    }
    return rv;
    }


    /**
     * Removes all components from this list and sets its size to zero.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <tt>clear()</tt>, which implements the
     *    <tt>List</tt> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @see #clear()
     * @see Vector#removeAllElements()
     */
    public void removeAllElements() {
    int index1 = delegate.size()-1;
    delegate.clear();
    if (index1 >= 0) {
        fireIntervalRemoved(this, 0, index1);
    }
    }


    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */   
    @Override public String toString() {
    return delegate.toString();
    }


    /* The remaining methods are included for compatibility with the
     * JDK1.2 Vector class.
     */

    /**
     * Returns an array containing all of the elements in this list in the
     * correct order.
     * <p>
     * Throws an <tt>ArrayStoreException</tt> if the runtime type of the array
     * a is not a supertype of the runtime type of every element in this list.
     *
     * @param a the array into which the elements of the list are to
     *		be stored, if it is big enough; otherwise, a new array of the
     * 		same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list.
     * @see Vector#toArray()
     */
    public Object[] toArray() {
    Object[] rv = delegate.toArray();
    return rv;
    }

    /**
     * Returns the element at the specified position in this list.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index is out of range
     * (index &lt; 0 || index &gt;= size()).
     *
     * @param index index of element to return.
     */
    public Object get(int index) {
    return delegate.get(index);
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index is out of range
     * (index &lt; 0 || index &gt;= size()).
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     */
    @SuppressWarnings("unchecked") public Object set(int index, Object element) {
    Object rv = delegate.set( index, element);
    fireContentsChanged(this, index, index);
    return rv;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index is out of range
     * (index &lt; 0 || index &gt;= size()).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    @SuppressWarnings("unchecked") public void add(int index, Object element) {
    delegate.add( index, element);
    fireIntervalAdded(this, index, index);
    }

    /**
     * Removes the element at the specified position in this list.
     * Returns the element that was removed from the list.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index is out of range
     * (index &lt; 0 || index &gt;= size()).
     *
     * @param index the index of the element to removed.
     */
    public Object remove(int index) {
    Object rv = delegate.remove(index);
    fireIntervalRemoved(this, index, index);
    return rv;
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns (unless it throws an exception).
     */
    public void clear() {
    int index1 = delegate.size()-1;
    delegate.clear();
    if (index1 >= 0) {
        fireIntervalRemoved(this, 0, index1);
    }
    }

    /**
     * Deletes the components at the specified range of indexes.
     * The removal is inclusive, so specifying a range of (1,5)
     * removes the component at index 1 and the component at index 5,
     * as well as all components in between.
     * <p>
     * Throws an <tt>ArrayIndexOutOfBoundsException</tt> if the index was invalid.
     * Throws an <tt>IllegalArgumentException</tt> if <tt>fromIndex</tt>
     * &gt; <tt>toIndex</tt>.
     *
     * @param      fromIndex the index of the lower end of the range
     * @param      toIndex   the index of the upper end of the range
     * @see	   #remove(int)
     */
    public void removeRange(int fromIndex, int toIndex) {
    for(int i = toIndex; i >= fromIndex; i--) {
        delegate.remove(i);
    }
    fireIntervalRemoved(this, fromIndex, toIndex);
    }

    /*
    public void addAll(Collection c) {
    }

    public void addAll(int index, Collection c) {
    }
    */
}
/**
* $Log: DefaultListModel.java,v $
* Revision 1.5  2006/01/15 21:10:35  sandyg
* resubmit at 5.1.02
*
* Revision 1.3  2006/01/14 14:40:35  sandyg
* added some @suppresswarnings on warnings that I could not code around
*
* Revision 1.2  2006/01/11 02:28:19  sandyg
* updating copyright years
*
* Revision 1.1  2006/01/01 02:27:01  sandyg
* preliminary submission to centralize code in a new module
*
* Revision 1.5  2004/04/10 20:49:28  sandyg
* Copyright year update
*
* Revision 1.4  2003/04/27 21:03:31  sandyg
* lots of cleanup, unit testing for 4.1.1 almost complete
*
* Revision 1.3  2003/01/04 17:29:13  sandyg
* Prefix/suffix overhaul
*
*/
