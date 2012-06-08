/*******************************************************************************
 * JSortedList: A random access linked list implementation
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Sorted Linked list. Extension of {@link java.util.List} that provides a sorted linked list implementation
 * with the ability for objects to be hidden on the list.
 * <ul>
 * <li>Null objects are not allowed.
 * <li>Duplicate objects are not allowed
 * <li>The semantics of the {@link #add} method are changed such that the element is added at its natural
 * position in the list rather than at the end of the * list. Methods that attempt to specify the position of
 * an object are disallowed.
 * <li>The {@link #subList} method is not supported
 * </ul>
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedList<T extends Comparable<T>> implements List<T>, Cloneable {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 31;

    /**
     * The first node in the list.
     */
    private transient OrderedNode<T> theFirst = null;

    /**
     * The last node in the list.
     */
    private transient OrderedNode<T> theLast = null;

    /**
     * Do we skip hidden elements.
     */
    private transient boolean doSkipHidden = true;

    /**
     * The modification count.
     */
    private transient int theModCount = 0;

    /**
     * Index map for list.
     */
    private transient OrderedIndex<T> theIndexMap;

    /**
     * Self reference.
     */
    private transient OrderedList<T> theSelf = this;

    /**
     * Class of the objects held in this list.
     */
    private transient Class<T> theClass;

    /**
     * Obtain the class of objects in this sorted list.
     * @return should we skip hidden elements
     */
    public Class<T> getBaseClass() {
        return theClass;
    }

    /**
     * get setting of option as to whether to skip hidden elements.
     * @return should we skip hidden elements
     */
    public boolean getSkipHidden() {
        return doSkipHidden;
    }

    /**
     * get modification count.
     * @return the modification count
     */
    protected int getModCount() {
        return theModCount;
    }

    /**
     * Allocate the index map.
     * @return the indexMap
     */
    protected OrderedIndex<T> allocateIndexMap() {
        return new OrderedIndex<T>(this);
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     */
    public OrderedList(final Class<T> pClass) {
        /* Store the class */
        theClass = pClass;

        /* Create the indexMap */
        theIndexMap = allocateIndexMap();
    }

    /**
     * Set option as to whether to skip hidden elements.
     * @param skipHidden - should we skip hidden elements
     */
    public void setSkipHidden(final boolean skipHidden) {
        doSkipHidden = skipHidden;
    }

    @Override
    public boolean add(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is already a link member of this list */
        if (theIndexMap.findNodeForObject(pItem) != null) {
            return false;
        }

        /* Increment the modification count */
        theModCount++;

        /* Allocate the new node */
        OrderedNode<T> myNode = new OrderedNode<T>(this, pItem);

        /* Insert the node into the list */
        insertNode(myNode, false);

        /* Return to caller */
        return true;
    }

    /**
     * Variant on add that looks to add at the end of the list. Used in order to optimise adding of elements
     * that are already in sort order.
     * @param pItem the item to add
     * @return true
     */
    public boolean addAtEnd(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is already a link member of this list */
        if (theIndexMap.findNodeForObject(pItem) != null) {
            return false;
        }

        /* Increment the modification count */
        theModCount++;

        /* Allocate the new node */
        OrderedNode<T> myNode = new OrderedNode<T>(this, pItem);

        /* Register link between object and node */
        theIndexMap.registerLink(myNode);

        /* Insert the node into the list */
        insertNode(myNode, true);

        /* Return to caller */
        return true;
    }

    /**
     * Insert node.
     * @param pNode - node to insert into list
     * @param atEnd - optimise insert at end
     */
    protected void insertNode(final OrderedNode<T> pNode,
                              final boolean atEnd) {
        /* If we are adding to an empty list */
        if (theFirst == null) {
            /* Add to head of list */
            pNode.addToHead();

            /* else if we are adding at the end */
        } else if (atEnd) {
            /* Search for insert point */
            OrderedNode<T> myPoint = theIndexMap.findNodeBefore(pNode);

            /* If we have an insert point, insert there */
            if (myPoint != null) {
                pNode.addAfterNode(myPoint);

                /* else add to head of list */
            } else {
                pNode.addToHead();
            }

            /* If we are adding normally */
        } else {
            /* Search for insert point */
            OrderedNode<T> myPoint = theIndexMap.findNodeAfter(pNode);

            /* If we have an insert point, insert there */
            if (myPoint != null) {
                pNode.addBeforeNode(myPoint);

                /* else add to tail of list */
            } else {
                pNode.addToTail();
            }
        }

        /* Adjust first and last if necessary */
        if (pNode.getPrev(false) == null) {
            theFirst = pNode;
        }
        if (pNode.getNext(false) == null) {
            theLast = pNode;
        }

        /* Adjust the indexMap */
        theIndexMap.insertNode(pNode);
    }

    /**
     * Remove node from list.
     * @param pNode - node to remove from list
     */
    protected void removeNode(final OrderedNode<T> pNode) {
        /* Remove the node from the index map */
        theIndexMap.removeNode(pNode);

        /* Adjust first and last indicators if required */
        if (theFirst == pNode) {
            theFirst = pNode.getNext(false);
        }
        if (theLast == pNode) {
            theLast = pNode.getPrev(false);
        }

        /* Remove the node from the list */
        pNode.remove();
    }

    /**
     * set an object as hidden/visible.
     * @param pItem - the relevant object
     * @param isHidden - is the object hidden
     */
    public void setHidden(final T pItem,
                          final boolean isHidden) {
        /* Reject if these object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if ((myNode == null) || (myNode.getList() != theSelf)) {
            return;
        }

        /* If we are changing things */
        if (isHidden != myNode.isHidden()) {
            /* Increment the modification count */
            theModCount++;

            /* set the hidden value */
            myNode.setHidden(isHidden);
        }
    }

    @Override
    public void clear() {
        /* Increment the modification count */
        theModCount++;

        /* Remove the items in reverse order */
        while (theLast != null) {
            /* Access and unlink the node */
            OrderedNode<T> myNode = theLast;
            theLast = myNode.getNext(false);

            /* Remove links from node and list */
            theIndexMap.deRegisterLink(myNode);
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
     * is the list empty of all (including hidden) items.
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
    public OrderedListIterator<T> listIterator() {
        /* Return a new iterator */
        return listIterator(false);
    }

    /**
     * obtain a list Iterator for this list.
     * @param bShowAll show all items in the list
     * @return List iterator
     */
    public OrderedListIterator<T> listIterator(final boolean bShowAll) {
        /* Return a new iterator */
        return new OrderedListIterator<T>(this, bShowAll);
    }

    /**
     * obtain a list Iterator for this list initialised to an item.
     * @param pItem the item to initialise to
     * @param bShowAll show all items in the list
     * @return List iterator
     */
    public OrderedListIterator<T> listIterator(final T pItem,
                                               final boolean bShowAll) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* If the item is hidden and we are not showing all then ignore */
        if ((!bShowAll) && (myNode.isHidden())) {
            return null;
        }

        /* Create a list iterator */
        return new OrderedListIterator<T>(this, myNode, bShowAll);
    }

    @Override
    public OrderedListIterator<T> listIterator(final int iIndex) {
        /* Reject if the index is negative */
        if (iIndex < 0) {
            throw new IndexOutOfBoundsException();
        }

        /* Reject if the index is too large */
        if (iIndex >= size()) {
            throw new IndexOutOfBoundsException();
        }

        /* Access the node */
        OrderedNode<T> myNode = theIndexMap.getNodeAtIndex(iIndex);

        /* Note if we did not find the item */
        if (myNode == null) {
            throw new IndexOutOfBoundsException();
        }

        /* Create a list iterator */
        return new OrderedListIterator<T>(this, myNode, false);
    }

    @Override
    public boolean equals(final Object pThat) {
        OrderedNode<?> myCurr;
        OrderedNode<?> myOther;
        OrderedList<?> myThat;

        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is an OrderedList */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target list */
        myThat = (OrderedList<?>) pThat;

        /* Make sure that the object is the same data class */
        if (myThat.theClass != this.theClass) {
            return false;
        }

        /* Loop through the list */
        for (myCurr = theFirst, myOther = myThat.theFirst; (myCurr != null) || (myOther != null); myCurr = myCurr
                .getNext(false), myOther = myOther.getNext(false)) {
            /* If either entry is null then we differ */
            if ((myCurr == null) || (myOther == null)) {
                return false;
            }

            /* If the entries differ in hidden character */
            if (myCurr.isHidden() != myOther.isHidden()) {
                return false;
            }

            /* If the entries differ then the lists differ */
            if (!myCurr.getObject().equals(myOther.getObject())) {
                return false;
            }
        }

        /* We are identical */
        return true;
    }

    @Override
    public int hashCode() {
        /* Initial hash mode */
        int myHash = 1;

        /* Loop through the list */
        for (OrderedNode<?> myCurr = theFirst; myCurr != null; myCurr = myCurr.getNext(false)) {
            /* Calculate hash */
            myHash *= HASH_PRIME;
            myHash += myCurr.getObject().hashCode();
            if (myCurr.isHidden()) {
                myHash++;
            }
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
     * obtain the full size of the list (including hidden items).
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
     * obtain the size of the list (not including hidden items).
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
    public T get(final int iIndex) {
        /* Reject if the index is negative */
        if (iIndex < 0) {
            throw new IndexOutOfBoundsException();
        }

        /* Reject if the index is too large */
        if (iIndex >= size()) {
            throw new IndexOutOfBoundsException();
        }

        /* Access the node */
        OrderedNode<T> myNode = theIndexMap.getNodeAtIndex(iIndex);

        /* Note if we did not find the item */
        if (myNode == null) {
            throw new IndexOutOfBoundsException();
        }

        /* Return the item */
        return myNode.getObject();
    }

    @Override
    public T remove(final int iIndex) {
        /* Reject if the index is negative */
        if (iIndex < 0) {
            throw new IndexOutOfBoundsException();
        }

        /* Reject if the index is too large */
        if (iIndex >= size()) {
            throw new IndexOutOfBoundsException();
        }

        /* Access the node */
        OrderedNode<T> myNode = theIndexMap.getNodeAtIndex(iIndex);

        /* Note if we did not find the item */
        if (myNode == null) {
            throw new IndexOutOfBoundsException();
        }

        /* Remove the node */
        removeNode(myNode);

        /* Remove the link between node and item */
        theIndexMap.deRegisterLink(myNode);

        /* Return the item */
        return myNode.getObject();
    }

    @Override
    public boolean remove(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(myItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return false;
        }

        /* Increment the modification count */
        theModCount++;

        /* Remove the item */
        removeNode(myNode);

        /* Remove the link between node and item */
        theIndexMap.deRegisterLink(myNode);

        /* Return the success/failure */
        return true;
    }

    /**
     * re-sort the specified item by removing it from the list and re-adding it.
     * @param o the item to resort
     */
    public void reSort(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Cast the object correctly */
        T myItem = theClass.cast(o);

        /* Find the node for the object */
        OrderedNode<T> myNode = theIndexMap.findUnsortedNodeForObject(myItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return;
        }

        /* Increment the modification count */
        theModCount++;

        /* Remove the object from the list */
        removeNode(myNode);

        /* Add the item back into the list */
        insertNode(myNode, false);
    }

    @Override
    public int indexOf(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(myItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return -1;
        }

        /* Access the index of the item */
        return myNode.getIndex(doSkipHidden);
    }

    /**
     * obtain the index within the list of the object.
     * @param o the object to find the index of
     * @return the index within the list (or -1 if not visible/present in the list)
     */
    public int indexAllOf(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(myItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return -1;
        }

        /* Return the index of the item */
        return myNode.getIndex(false);
    }

    @Override
    public int lastIndexOf(final Object o) {
        /* Objects cannot be duplicate so redirect to indexOf */
        return indexOf(o);
    }

    @Override
    public boolean contains(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new NullPointerException();
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(myItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return false;
        }

        /* Return that the object belongs */
        return true;
    }

    /**
     * Peek at the first item.
     * @return the first item or <code>null</code>
     */
    protected T peekFirst() {
        /* Access the first item */
        OrderedNode<T> myNode = getFirst();

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the first item.
     * @return the first item or <code>null</code>
     */
    protected T peekLast() {
        /* Access the last item */
        OrderedNode<T> myNode = getLast();

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the next item.
     * @param pItem the item from which to find the next item
     * @return the next item or <code>null</code>
     */
    public T peekNext(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* Access the next node */
        myNode = myNode.getNext(doSkipHidden);

        /* Return the next object */
        return (myNode == null) ? null : myNode.getObject();
    }

    /**
     * Peek at the previous item.
     * @param pItem the item from which to find the previous item
     * @return the previous item or <code>null</code>
     */
    public T peekPrevious(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new NullPointerException();
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* Access the previous node */
        myNode = myNode.getPrev(doSkipHidden);

        /* Return the previous object */
        return (myNode == null) ? null : myNode.getObject();
    }

    @Override
    public boolean containsAll(final Collection<?> pCollection) {
        /* Reject if the collection is null */
        if (pCollection == null) {
            throw new NullPointerException();
        }

        /* Loop through the collection */
        for (Object o : pCollection) {
            /* If the item is not in the list return false */
            if (!contains(o)) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    @Override
    public Object[] toArray() {
        /* Determine the size of the array */
        int iSize = size();

        /* Allocate an array list of the estimated size */
        Object[] myArray = new Object[iSize];

        /* Loop through the list */
        OrderedListIterator<?> myIterator = listIterator(false);
        for (int i = 0; i < iSize; i++) {
            /* Store the next item */
            myArray[i] = myIterator.next();
        }

        /* Return the array */
        return myArray;
    }

    @Override
    public <X> X[] toArray(final X[] a) {
        /* Determine the size of the array */
        int iSize = size();

        /* Reject if the sample array is null or wrong type */
        if (a == null) {
            throw new NullPointerException();
        }
        Class<?> myClass = a[0].getClass();
        if (!myClass.isAssignableFrom(theClass)) {
            throw new ArrayStoreException();
        }

        /* Allocate an array list of the estimated size */
        List<X> myList = new ArrayList<X>(iSize);

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
     * @return exception
     */
    @Override
    public OrderedList<T> subList(final int iFromIndex,
                                  final int iToIndex) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Set the contents of the item at index. Disallowed.
     * @param iIndex index of item to set
     * @param o object to set
     * @return exception
     */
    @Override
    public T set(final int iIndex,
                 final T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add at element at an explicit location. Disallowed.
     * @param iIndex index of item to add after
     * @param o object to add
     */
    @Override
    public void add(final int iIndex,
                    final T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add All elements in the collection.
     * @param pCollection collection of items to add
     * @return did the list change as a result true/false
     */
    @Override
    public boolean addAll(final Collection<? extends T> pCollection) {
        /* Obtain the current modification count */
        int myModCount = theModCount;

        /* Obtain an iterator over the collection */
        Iterator<? extends T> myIterator = pCollection.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* Add it to the list */
            add(myItem);
        }

        /* Return indication of change */
        return (theModCount != myModCount);
    }

    /**
     * Add All elements in the list.
     * @param pList list of items to add
     * @return did the list change as a result true/false
     */
    public boolean addAll(final OrderedList<? extends T> pList) {
        /* Obtain the current modification count */
        int myModCount = theModCount;

        /* Obtain an iterator over the collection */
        OrderedListIterator<? extends T> myIterator = pList.listIterator(true);
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* Add it to the list */
            addAtEnd(myItem);

            /* If the item is hidden record it */
            if (myIterator.wasHidden()) {
                setHidden(myItem, true);
            }
        }

        /* Return indication of change */
        return (theModCount != myModCount);
    }

    /**
     * Add All elements in the collection at an index.
     * @param iIndex index of item to add after
     * @param pCollection collection of items to add
     * @return did the list change as a result true/false
     */
    @Override
    public boolean addAll(final int iIndex,
                          final Collection<? extends T> pCollection) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Retain All elements in the collection.
     * @param pCollection collection of items to retain
     * @return did the list change as a result true/false
     */
    @Override
    public boolean retainAll(final Collection<?> pCollection) {
        /* Obtain the current modification count */
        int myModCount = theModCount;

        /* Obtain an iterator over the collection */
        Iterator<T> myIterator = listIterator(true);
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* If item is in collection then ignore */
            if (pCollection.contains(myItem)) {
                continue;
            }

            /* Remove it from the list */
            remove(myItem);
        }

        /* Return indication of change */
        return (theModCount != myModCount);
    }

    /**
     * Remove All elements in the collection.
     * @param pCollection collection of items to remove
     * @return did the list change as a result true/false
     */
    @Override
    public boolean removeAll(final Collection<?> pCollection) {
        /* Obtain the current modification count */
        int myModCount = theModCount;

        /* Obtain an iterator over the collection */
        Iterator<T> myIterator = listIterator(true);
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* If item is not in collection then ignore */
            if (!pCollection.contains(myItem)) {
                continue;
            }

            /* Remove it from the list */
            remove(myItem);
        }

        /* Return indication of change */
        return (theModCount != myModCount);
    }

    /**
     * Get the first visible node in the sequence.
     * @return the First visible node
     */
    protected OrderedNode<T> getFirst() {
        /* Get the first item */
        OrderedNode<T> myFirst = theFirst;

        /* Skip to next visible item if required */
        if ((myFirst != null) && (myFirst.isHidden()) && (doSkipHidden)) {
            myFirst = myFirst.getNext(true);
        }

        /* Return to caller */
        return myFirst;
    }

    /**
     * Get the last visible node in the sequence.
     * @return the Last visible node
     */
    protected OrderedNode<T> getLast() {
        /* Get the last item */
        OrderedNode<T> myLast = theLast;

        /* Skip to previous visible item if required */
        if ((myLast != null) && (myLast.isHidden()) && (doSkipHidden)) {
            myLast = myLast.getPrev(true);
        }

        /* Return to caller */
        return myLast;
    }

    /**
     * Get the first node in the sequence.
     * @return the First node
     */
    protected OrderedNode<T> getHead() {
        return theFirst;
    }

    /**
     * Get the last node in the sequence.
     * @return the Last visible node
     */
    protected OrderedNode<T> getTail() {
        return theLast;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        /* Clone the underlying object */
        @SuppressWarnings("unchecked")
        OrderedList<T> myResult = (OrderedList<T>) super.clone();

        /* Re-initialise the fields */
        myResult.theSelf = myResult;
        myResult.doSkipHidden = doSkipHidden;
        myResult.theClass = theClass;
        myResult.theIndexMap = allocateIndexMap();

        /* Copy all the entries */
        myResult.addAll(this);
        myResult.theModCount = 0;

        /* Return the cloned object */
        return myResult;
    }

    /**
     * Write this list to an object output stream.
     * @param pOutput the object output stream
     * @throws IOException on error
     */
    private void writeObject(final ObjectOutputStream pOutput) throws IOException {
        /* Note expected modification count */
        int myExpectedModCount = theModCount;

        /* Write out the default stuff */
        pOutput.defaultWriteObject();

        /* Write out number of Mappings */
        int mySize = sizeAll();
        pOutput.writeInt(mySize);
        if (mySize == 0) {
            return;
        }

        /* Write out keys and values (alternating) */
        OrderedListIterator<T> myIterator = listIterator(false);
        T myItem;
        while ((myItem = myIterator.next()) != null) {
            /* Write out hidden status and object */
            pOutput.writeObject(myItem);
            pOutput.writeBoolean(myIterator.wasHidden());
        }

        /* Throw exception if modifications occurred */
        if (theModCount != myExpectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Read the list from an object input stream.
     * @param pInput the object input stream
     * @throws IOException on error
     * @throws ClassNotFoundException on error
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream pInput) throws IOException, ClassNotFoundException {
        /* Read in the default stuff */
        pInput.defaultReadObject();

        /* Read in size number of elements */
        int mySize = pInput.readInt();

        /* Read the keys and values, and put the mappings in the HashMap */
        for (int i = 0; i < mySize; i++) {
            /* Read the values in */
            T myItem = (T) pInput.readObject();
            boolean isHidden = pInput.readBoolean();

            /* Add to list */
            addAtEnd(myItem);

            /* If the item is hidden record it */
            if (isHidden) {
                setHidden(myItem, true);
            }
        }
    }
}
