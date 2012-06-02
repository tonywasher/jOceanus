/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.JSortedList;

import java.util.Iterator;

import net.sourceforge.JDataManager.JDataException;
import uk.co.tolcroft.models.data.PreferenceSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceSetChooser;

/**
 * Extension of {@link java.util.List} that provides a sorted list implementation with the ability for objects
 * to be hidden on the list. Objects held in the list must be extensions of the LinkObject class. This
 * requires the object to hold a link that allows fast access to their position on the list, resulting in no
 * need to search the list to find the object, and also automatic knowledge of the index of an object. A
 * reference to the list is also passed with the link node allowing the object to implement a store that
 * allows it to reside in multiple lists. This parameter can be ignored which has the side-effect that objects
 * are uniquely associated with the list and cannot be held in more than one such list. An index map
 * {@link SortedList.indexMap} is also built allowing fast search for objects by index.
 * 
 * <ul>
 * <li>Null objects are not allowed.
 * <li>Duplicate objects are not allowed
 * <li>The semantics of the {@link #add} method are changed such that the element is added at its natural
 * position in the list rather than at the end of the * list.
 * <li>The {@link #subList} method is not supported
 * </ul>
 * @author Tony Washer
 * @param <T> the SortedItem type for this list
 */
public class SortedList<T extends LinkObject<T>> implements java.util.List<T>, PreferenceSetChooser {
    /**
     * Sorted List Preferences
     */
    private SortedListPreferences thePreferences = null;

    /**
     * The first node in the list
     */
    private LinkNode<T> theFirst = null;

    /**
     * The last node in the list
     */
    private LinkNode<T> theLast = null;

    /**
     * Is the search for insert point conducted from the start or end of the list
     */
    private boolean insertFromStart = true;

    /**
     * Do we skip hidden elements
     */
    private boolean doSkipHidden = true;

    /**
     * Index map for list
     */
    private indexMap theIndexMap = null;

    /**
     * Self reference
     */
    private SortedList<T> theList = this;

    /**
     * Class of the objects held in this list
     */
    private Class<T> theClass = null;

    /**
     * Obtain the class of objects in this sorted list
     * @return should we skip hidden elements
     */
    public Class<T> getBaseClass() {
        return theClass;
    }

    /**
     * get setting of option as to whether to skip hidden elements
     * @return should we skip hidden elements
     */
    public boolean getSkipHidden() {
        return doSkipHidden;
    }

    /**
     * Construct a list. Inserts search backwards from the end for the insert point
     * @param pClass the class of the sortedItem
     */
    public SortedList(Class<T> pClass) {
        this(pClass, false);
    }

    /**
     * Construct a list
     * @param pClass the class of the sortedItem
     * @param fromStart - should inserts be attempted from start/end of list
     */
    public SortedList(Class<T> pClass,
                      boolean fromStart) {
        insertFromStart = fromStart;
        theClass = pClass;

        /* Access the sortedList preferences */
        thePreferences = (SortedListPreferences) PreferenceManager.getPreferenceSet(this);
        theIndexMap = new indexMap();
    }

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return SortedListPreferences.class;
    }

    /**
     * Set option as to whether to skip hidden elements
     * @param skipHidden - should we skip hidden elements
     */
    public void setSkipHidden(boolean skipHidden) {
        doSkipHidden = skipHidden;
    }

    @Override
    public boolean add(T pItem) {
        LinkNode<T> myNode;

        /* Reject if the object is null */
        if (pItem == null)
            throw new java.lang.NullPointerException();

        /* Reject if the object is already a link member of this list */
        if (pItem.getLinkNode(this) != null)
            return false;

        /* Allocate the new node */
        myNode = new LinkNode<T>(this, pItem);

        /* Insert the node into the list */
        insertNode(myNode);

        /* Return to caller */
        return true;
    }

    /**
     * Insert node
     * @param pNode - node to insert into list
     */
    protected void insertNode(LinkNode<T> pNode) {
        /* Add in the appropriate fashion */
        if (insertFromStart)
            pNode.addFromStart(theFirst, theLast);
        else
            pNode.addFromEnd(theFirst, theLast);

        /* Adjust first and last if necessary */
        if (pNode.getPrev(false) == null)
            theFirst = pNode;
        if (pNode.getNext(false) == null)
            theLast = pNode;

        /* Set the reference to this node in the item */
        pNode.getObject().setLinkNode(this, pNode);

        /* Adjust the indexMap */
        theIndexMap.insertNode(pNode);
    }

    /**
     * Remove node from list
     * @param pNode - node to remove from list
     */
    protected void removeNode(LinkNode<T> pNode) {
        /* Remove the reference to this node in the item */
        pNode.getObject().setLinkNode(this, null);

        /* Remove the node from the index map */
        theIndexMap.removeNode(pNode);

        /* Adjust first and last indicators if required */
        if (theFirst == pNode)
            theFirst = pNode.getNext(false);
        if (theLast == pNode)
            theLast = pNode.getPrev(false);

        /* Remove the node from the list */
        pNode.remove();
    }

    /**
     * set an object as hidden/visible
     * @param pItem - the relevant object
     * @param isHidden - is the object hidden
     */
    public void setHidden(T pItem,
                          boolean isHidden) {
        LinkNode<T> myNode;

        /* Reject if these object is null */
        if (pItem == null)
            throw new java.lang.NullPointerException();

        /* Access the node of the item */
        myNode = pItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return;

        /* If we are changing things */
        if (isHidden != myNode.isHidden()) {
            /* set the hidden value */
            myNode.setHidden(isHidden);
        }
    }

    @Override
    public void clear() {
        LinkNode<T> myNode;

        /* Remove the items in reverse order */
        while (theLast != null) {
            /* Access and unlink the node */
            myNode = theLast;
            theLast = myNode.getNext(false);

            /* Remove links from node and list */
            myNode.getObject().setLinkNode(this, null);
            myNode.remove();
        }

        /* Reset the first item and clear the map */
        theFirst = null;
        theIndexMap.clear();
    }

    @Override
    public boolean isEmpty() {
        /* Return details */
        return (getFirst() == null);
    }

    /**
     * is the list empty of all (including hidden) items
     * @return <code>true/false</code>
     */
    public boolean isEmptyAll() {
        /* Return details */
        return (theFirst == null);
    }

    @Override
    public Iterator<T> iterator() {
        /* Return a new iterator */
        return listIterator();
    }

    @Override
    public SortedListIterator<T> listIterator() {
        /* Return a new iterator */
        return listIterator(false);
    }

    /**
     * obtain a list Iterator for this list
     * @param bShowAll show all items in the list
     * @return List iterator
     */
    public SortedListIterator<T> listIterator(boolean bShowAll) {
        /* Return a new iterator */
        return new SortedListIterator<T>(this, bShowAll);
    }

    /**
     * obtain a list Iterator for this list initialised to an item
     * @param pItem the item to initialise to
     * @param bShowAll show all items in the list
     * @return List iterator
     */
    public SortedListIterator<T> listIterator(T pItem,
                                              boolean bShowAll) {
        LinkNode<T> myNode;
        SortedListIterator<T> myCurr;

        /* Reject if the object is null */
        if (pItem == null)
            throw new java.lang.NullPointerException();

        /* If the item is hidden and we are not showing all then ignore */
        if ((!bShowAll) && (pItem.isHidden()))
            return null;

        /* Access the node of the item */
        myNode = pItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return null;

        /* Create a list iterator */
        myCurr = new SortedListIterator<T>(this, myNode, bShowAll);

        /* Return a new iterator */
        return myCurr;
    }

    @Override
    public SortedListIterator<T> listIterator(int iIndex) {
        SortedListIterator<T> myCurr;
        T myObj;

        /* Reject if the index is negative */
        if (iIndex < 0)
            throw new java.lang.IndexOutOfBoundsException();

        /* Access the item */
        myObj = get(iIndex);

        /* Create a list iterator */
        myCurr = new SortedListIterator<T>(this, myObj.getLinkNode(this), false);

        /* Return a new iterator */
        return myCurr;
    }

    @Override
    public boolean equals(Object pThat) {
        LinkNode<?> myCurr;
        LinkNode<?> myOther;
        SortedList<?> myThat;

        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a SortedList */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the target list */
        myThat = (SortedList<?>) pThat;

        /* Make sure that the object is the same data class */
        if (myThat.theClass != this.theClass)
            return false;

        /* Loop through the list */
        for (myCurr = theFirst, myOther = myThat.theFirst; (myCurr != null) || (myOther != null); myCurr = myCurr
                .getNext(false), myOther = myOther.getNext(false)) {
            /* If either entry is null then we differ */
            if ((myCurr == null) || (myOther == null))
                return false;

            /* If the entries differ then the lists differ */
            if (!myCurr.getObject().equals(myOther.getObject()))
                return false;
        }

        /* We are identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* Initial hash mode */
        int myHash = 1;

        /* Loop through the list */
        for (LinkNode<?> myCurr = theFirst; myCurr != null; myCurr = myCurr.getNext(false)) {
            /* Calculate hash */
            myHash *= 31;
            myHash += myCurr.getObject().hashCode();
        }

        return myHash;
    }

    @Override
    public int size() {
        int iSize = 0;

        /* If we have an element in the list */
        if (theLast != null) {
            /* Get the relevant index and add 1 */
            iSize = 1 + theLast.getIndex(doSkipHidden);
        }

        /* Return the count */
        return iSize;
    }

    /**
     * obtain the full size of the list (including hidden items)
     * @return the number of visible items in the list
     */
    public int sizeAll() {
        int iSize = 0;

        /* If we have an element in the list */
        if (theLast != null) {
            /* Get the full index and add 1 */
            iSize = 1 + theLast.getIndex(false);
        }

        /* Return the count */
        return iSize;
    }

    /**
     * obtain the full size of the list (including hidden items)
     * @return the number of visible items in the list
     */
    public int sizeNormal() {
        int iSize = 0;

        /* If we have an element in the list */
        if (theLast != null) {
            /* Get the hidden index and add 1 */
            iSize = 1 + theLast.getIndex(true);
        }

        /* Return the count */
        return iSize;
    }

    @Override
    public T get(int iIndex) {
        LinkNode<T> myNode;

        /* Reject if the index is negative */
        if (iIndex < 0)
            throw new java.lang.IndexOutOfBoundsException();

        /* Access the node */
        myNode = theIndexMap.getNodeAtIndex(iIndex);

        /* Note if we did not find the item */
        if (myNode == null)
            throw new java.lang.IndexOutOfBoundsException();

        /* Return the item */
        return myNode.getObject();
    }

    @Override
    public T remove(int iIndex) {
        LinkNode<T> myNode;

        /* Reject if the index is negative */
        if (iIndex < 0)
            throw new java.lang.IndexOutOfBoundsException();

        /* Access the node */
        myNode = theIndexMap.getNodeAtIndex(iIndex);

        /* Note if we did not find the item */
        if (myNode == null)
            throw new java.lang.IndexOutOfBoundsException();

        /* Remove the node */
        removeNode(myNode);

        /* Return the item */
        return myNode.getObject();
    }

    @Override
    public boolean remove(Object o) {
        LinkNode<T> myNode;
        T myItem;

        /* Reject if the object is null */
        if (o == null)
            throw new java.lang.NullPointerException();

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o)))
            throw new java.lang.ClassCastException();

        /* Access as link object */
        myItem = theClass.cast(o);

        /* Access the node of the item */
        myNode = myItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return false;

        /* Remove the item */
        removeNode(myNode);

        /* Return the success/failure */
        return true;
    }

    /**
     * re-sort the specified item by removing it from the list and re-adding it
     * @param o the item to resort
     */
    public void reSort(Object o) {
        /* Cast the object correctly */
        T myObject = theClass.cast(o);

        /* Remove the object from the list */
        remove(myObject);

        /* Add the item back into the list */
        add(myObject);
    }

    @Override
    public int indexOf(Object o) {
        int iIndex = 0;
        LinkNode<T> myNode;
        T myItem;

        /* Reject if the object is null */
        if (o == null)
            throw new java.lang.NullPointerException();

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o)))
            throw new java.lang.ClassCastException();

        /* Access as link object */
        myItem = theClass.cast(o);

        /* Access the node of the item */
        myNode = myItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return -1;

        /* Access the index of the item */
        iIndex = myNode.getIndex(doSkipHidden);

        /* Return the index */
        return iIndex;
    }

    /**
     * obtain the index within the list of the object
     * @param o the object to find the index of
     * @return the index within the list (or -1 if not visible/present in the list)
     */
    public int indexAllOf(Object o) {
        int iIndex = 0;
        LinkNode<T> myNode;
        T myItem;

        /* Reject if the object is null */
        if (o == null)
            throw new java.lang.NullPointerException();

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o)))
            throw new java.lang.ClassCastException();

        /* Access as link object */
        myItem = theClass.cast(o);

        /* Access the node of the item */
        myNode = myItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return -1;

        /* Access the index of the item */
        iIndex = myNode.getIndex(false);

        /* Return the index */
        return iIndex;
    }

    @Override
    public int lastIndexOf(Object o) {
        /* Objects cannot be duplicate so redirect to indexOf */
        return indexOf(o);
    }

    @Override
    public boolean contains(Object o) {
        LinkNode<T> myNode;
        T myItem;

        /* Reject if the object is null */
        if (o == null)
            throw new java.lang.NullPointerException();

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o)))
            throw new java.lang.ClassCastException();

        /* Access as link object */
        myItem = theClass.cast(o);

        /* Access the node of the item */
        myNode = myItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return false;

        /* Return that the object belongs */
        return true;
    }

    /**
     * Peek at the first item
     * @return the first item or <code>null</code>
     */
    protected T peekFirst() {
        LinkNode<T> myNode;

        /* Access the first item */
        myNode = getFirst();

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the first item
     * @return the first item or <code>null</code>
     */
    protected T peekLast() {
        LinkNode<T> myNode;

        /* Access the last item */
        myNode = getLast();

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the next item
     * @param pItem the item from which to find the next item
     * @return the next item or <code>null</code>
     */
    public T peekNext(T pItem) {
        LinkNode<T> myNode;

        /* Reject if the object is null */
        if (pItem == null)
            throw new java.lang.NullPointerException();

        /* Access the node of the item */
        myNode = pItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return null;

        /* Access the next node */
        myNode = myNode.getNext(doSkipHidden);

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the previous item
     * @param pItem the item from which to find the previous item
     * @return the previous item or <code>null</code>
     */
    public T peekPrevious(T pItem) {
        LinkNode<T> myNode;

        /* Reject if the object is null */
        if (pItem == null)
            throw new java.lang.NullPointerException();

        /* Access the node of the item */
        myNode = pItem.getLinkNode(this);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theList))
            return null;

        /* Access the previous node */
        myNode = myNode.getPrev(doSkipHidden);

        /* Return the previous object */
        return (myNode == null) ? null : myNode.getObject();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> pCollection) {
        /* Reject if the collection is null */
        if (pCollection == null)
            throw new java.lang.NullPointerException();

        /* Loop through the collection */
        for (Object o : pCollection) {
            /* If the item is not in the list return false */
            if (!contains(o))
                return false;
        }

        /* Return success */
        return true;
    }

    @Override
    public Object[] toArray() {
        int iSize;
        int i;
        SortedListIterator<?> myIterator;
        Object[] myArray;

        /* Determine the size of the array */
        iSize = size();

        /* Allocate an array list of the estimated size */
        myArray = new Object[iSize];

        /* Loop through the list */
        for (i = 0, myIterator = listIterator(false); i < iSize; i++) {
            /* Store the next item */
            myArray[i] = myIterator.next();
        }

        /* Return the array */
        return myArray;
    }

    @Override
    public <X> X[] toArray(X[] a) {
        int iSize;
        java.util.List<X> myList;

        /* Determine the size of the array */
        iSize = size();

        /* Reject if the sample array is null or wrong type */
        if (a == null)
            throw new java.lang.NullPointerException();
        Class<?> myClass = a[0].getClass();
        if (!myClass.isAssignableFrom(theClass))
            throw new java.lang.ArrayStoreException();

        /* Allocate an array list of the estimated size */
        myList = new java.util.ArrayList<X>(iSize);

        /* Loop through the list */
        for (Object myObj : toArray()) {
            /* Store the next item */
            @SuppressWarnings("unchecked")
            X myAdd = (X) myObj;
            myList.add(myAdd);
        }

        /* Return the array */
        return myList.toArray(a);
    }

    /**
     * Create a subList. Disallowed.
     * @param iFromIndex start index of sublist
     * @param iToIndex end index of sublist
     */
    @Override
    public SortedList<T> subList(int iFromIndex,
                                 int iToIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Set the contents of the item at index. Disallowed.
     * @param iIndex index of item to set
     * @param o object to set
     */
    @Override
    public T set(int iIndex,
                 T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add at element at an explicit location. Disallowed.
     * @param iIndex index of item to add after
     * @param o object to add
     */
    @Override
    public void add(int iIndex,
                    T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add All elements in the collection. Disallowed.
     * @param pCollection collection of items to add
     */
    @Override
    public boolean addAll(java.util.Collection<? extends T> pCollection) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add All elements in the collection at an index. Disallowed.
     * @param pCollection collection of items to add
     */
    @Override
    public boolean addAll(int iIndex,
                          java.util.Collection<? extends T> pCollection) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Retain All elements in the collection. Disallowed.
     * @param pCollection collection of items to retain
     */
    @Override
    public boolean retainAll(java.util.Collection<?> pCollection) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Remove All elements in the collection. Disallowed.
     * @param pCollection collection of items to remove
     */
    @Override
    public boolean removeAll(java.util.Collection<?> pCollection) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Get the first node in the sequence
     * @return the First visible node
     */
    protected LinkNode<T> getFirst() {
        LinkNode<T> myFirst;

        /* Get the first item */
        myFirst = theFirst;

        /* Skip to next visible item if required */
        if ((myFirst != null) && (myFirst.isHidden()) && (doSkipHidden))
            myFirst = myFirst.getNext(true);

        /* Return to caller */
        return myFirst;
    }

    /**
     * Get the last node in the sequence
     * @return the Last visible node
     */
    protected LinkNode<T> getLast() {
        LinkNode<T> myLast;

        /* Get the last item */
        myLast = theLast;

        /* Skip to previous visible item if required */
        if ((myLast != null) && (myLast.isHidden()) && (doSkipHidden))
            myLast = myLast.getPrev(true);

        /* Return to caller */
        return myLast;
    }

    /**
     * Get the first node in the sequence
     * @return the First node
     */
    protected LinkNode<T> getHead() {
        return theFirst;
    }

    /**
     * Get the last node in the sequence
     * @return the Last visible node
     */
    protected LinkNode<T> getTail() {
        return theLast;
    }

    /**
     * SortedList Preferences
     */
    public static class SortedListPreferences extends PreferenceSet {
        /**
         * Registry name for Index Granularity
         */
        protected final static String nameIdxGranular = "IndexGranularity";

        /**
         * Registry name for Index Granularity
         */
        protected final static String nameIdxExpand = "IndexExpansion";

        /**
         * Display name for Index Granularity
         */
        protected final static String dispIdxGranular = "Index Granularity";

        /**
         * Display name for Index Expansion
         */
        protected final static String dispIdxExpand = "Index Expansion";

        /**
         * Default Index Granularity
         */
        private final static Integer defIdxGranular = 50;

        /**
         * Default Index Expansion
         */
        private final static Integer defIdxExpand = 5;

        /**
         * Constructor
         * @throws JDataException
         */
        public SortedListPreferences() throws JDataException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            definePreference(nameIdxGranular, PreferenceType.Integer);
            definePreference(nameIdxExpand, PreferenceType.Integer);
        }

        @Override
        protected Object getDefaultValue(String pName) {
            /* Handle default values */
            if (pName.equals(nameIdxGranular))
                return defIdxGranular;
            if (pName.equals(nameIdxExpand))
                return defIdxExpand;
            return null;
        }

        @Override
        protected String getDisplayName(String pName) {
            /* Handle default values */
            if (pName.equals(nameIdxGranular))
                return dispIdxGranular;
            if (pName.equals(nameIdxExpand))
                return dispIdxExpand;
            return null;
        }
    }

    /**
     * IndexMap class for this list. The map class locates its starting search point using the pure index. The
     * map holds a reference to every 50th element, a factor that is controlled by the constant
     * {@link #theGranularity}. If the index required is 251, the search will immediately skip to index 250
     * and start the search from there. If we are skipping hidden items, the element we are looking for is
     * always later in the list than the calculated start point. The map is updated whenever items are added
     * to or removed from the list, and only items later in the map than the affected object are updated.
     */
    private class indexMap {
        /**
         * Expansion rate of map
         */
        private final int theExpansion = thePreferences.getIntegerValue(SortedListPreferences.nameIdxExpand);

        /**
         * Granularity of map
         */
        private final int theGranularity = thePreferences
                .getIntegerValue(SortedListPreferences.nameIdxGranular);

        /**
         * Array of standard indexes
         */
        private LinkNode<T>[] theMap = null;

        /**
         * The length of the map
         */
        private int theMapLength = 0;

        /**
         * Obtain the node at the specified index
         * @param iIndex the index of the node
         * @return the relevant node (or null)
         */
        private LinkNode<T> getNodeAtIndex(int iIndex) {
            int iMapIndex;
            LinkNode<T> myNode;

            /* Calculate the map index */
            iMapIndex = iIndex / theGranularity;

            /* Handle out of range */
            if (iMapIndex > theMapLength - 1)
                return null;

            /* Access the start node for the search */
            myNode = theMap[iMapIndex];

            /* Search for the correct node */
            while (myNode != null) {
                /* Break if we have found the node */
                if (myNode.getIndex(doSkipHidden) == iIndex)
                    break;

                /* Shift to next node */
                myNode = myNode.getNext(doSkipHidden);
            }

            /* Return the correct node */
            return myNode;
        }

        /**
         * Insert a map node
         * @param pNode the node to insert
         */
        @SuppressWarnings("unchecked")
        private void insertNode(LinkNode<T> pNode) {
            int iIndex;
            int iMapIndex;
            LinkNode<T> myNode;

            /* Access the index of the node */
            iIndex = pNode.getIndex(false);

            /* Calculate the map index */
            iMapIndex = iIndex / theGranularity;

            /* If we need to extend the map */
            if (iMapIndex > theMapLength - 1) {
                /* If we have an existing map */
                if (theMap != null) {
                    /* Extend the map by expansion number of entries */
                    theMap = java.util.Arrays.copyOf(theMap, theMapLength + theExpansion);
                }

                /* Else we have no current map */
                else {
                    /* Allocate and initialise the map */
                    theMap = (LinkNode<T>[]) new LinkNode[theExpansion];
                    java.util.Arrays.fill(theMap, null);
                }

                /* Adjust the map length */
                theMapLength += theExpansion;
            }

            /* If this is a mapped node */
            if ((iIndex % theGranularity) == 0) {
                /* Store the node into the map */
                theMap[iMapIndex] = pNode;
            }

            /* For all subsequent nodes */
            while (++iMapIndex < theMapLength) {
                /* Access the node in the map */
                myNode = theMap[iMapIndex];

                /* Break if we have reached the end of the map */
                if (myNode == null)
                    break;

                /* Shift the index to the previous item */
                theMap[iMapIndex] = myNode.getPrev(false);
            }

            /* If the last node has been shifted and needs storing, then store it */
            if ((pNode != theLast) && ((theLast.getIndex(false) % theGranularity) == 0))
                insertNode(theLast);
        }

        /**
         * Remove a map node
         * @param pNode the node to remove
         */
        private void removeNode(LinkNode<T> pNode) {
            int iIndex;
            int iMapIndex;
            LinkNode<T> myNode;

            /* Access the index of the node */
            iIndex = pNode.getIndex(false);

            /* Calculate the map index */
            iMapIndex = iIndex / theGranularity;

            /* Ignore node if it is past end of map */
            if (iMapIndex > theMapLength - 1)
                return;

            /* If this is a mapped node */
            if ((iIndex % theGranularity) == 0) {
                /* Adjust this node explicitly */
                theMap[iMapIndex] = pNode.getNext(false);
            }

            /* For all subsequent nodes */
            while (++iMapIndex < theMapLength) {
                /* Access the node in the map */
                myNode = theMap[iMapIndex];

                /* Break if we have reached the end of the map */
                if (myNode == null)
                    break;

                /* Shift the index to the next item */
                theMap[iMapIndex] = myNode.getNext(false);
            }
        }

        /**
         * Clear the indexMap
         */
        private void clear() {
            /* Reinitialise the map to null */
            if (theMap != null)
                java.util.Arrays.fill(theMap, null);
        }
    }
}
