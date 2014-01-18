/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * Ordered Linked list. Extension of {@link java.util.List} that provides a sorted list implementation. The underlying implementation is that of a linked list,
 * but the {@link java.util.LinkedList} interface is not implemented.
 * <ul>
 * <li>Null objects are not allowed.
 * <li>The semantics of the {@link #add} method are changed such that the element is added at its natural position in the list rather than at the end of the
 * list. Methods that attempt to specify the position of an object are disallowed.
 * <li>The {@link #subList} method is not supported
 * </ul>
 * <p>
 * Because the elements in the list are mutable, changes can be made to the items themselves that result in the list being incorrectly sorted, since the list is
 * unaware of these changes. When this occurs the list is referred to as being <b>dirty</b>, as opposed to <b>clean</b>. The {@link #reSort} method is provided
 * to clean up the list and repair the sort order.
 * <p>
 * A dirty list has the following implications.
 * <ol>
 * <li>If an element is added to a dirty list, the element may not be inserted in the correct position. It will be inserted into <i>a valid</i> position in that
 * it will be correctly positioned with respect to its previous and following element. However, these elements may not be in the correct order.
 * <li>If duplicate items are added to the list, the most recently added item will be added last in the sequence of equal items.
 * <li>If an element is searched for in a dirty list, it may not be found, since the search may be aborted early if the item is not found in its expected
 * location. To alleviate this, the {@link #remove(Object)} method will assume that it is searching a dirty list. The {@link #contains} and
 * {@link #indexOf(Object)} methods assume a clean list, and so may give incorrect results on a dirty list.
 * <li>The {@link #append} method is provided to insert items explicitly at the end of the list, and hence is likely to produce a dirty list. It should be used
 * for bulk additions to the list, followed by a single call to {@link #reSort}.
 * </ol>
 * <p>
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedList<T extends Comparable<? super T>>
        implements List<T>, Cloneable, RandomAccess {
    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 31;

    /**
     * Null Argument message.
     */
    protected static final String NULL_DISALLOWED = "Null elements not allowed";

    /**
     * The first node in the list.
     */
    private transient OrderedNode<T> theFirst = null;

    /**
     * The last node in the list.
     */
    private transient OrderedNode<T> theLast = null;

    /**
     * The modification count.
     */
    private transient int theModCount = 0;

    /**
     * Index map for list.
     */
    private transient OrderedIndex<T> theIndexMap;

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
     * get modification count.
     * @return the modification count
     */
    protected int getModCount() {
        return theModCount;
    }

    /**
     * obtain index.
     * @return the index
     */
    protected OrderedIndex<T> getIndex() {
        return theIndexMap;
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     */
    public OrderedList(final Class<T> pClass) {
        /* Use default index */
        this(pClass, new OrderedIndex<T>());
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndexGranularity the index granularity
     */
    public OrderedList(final Class<T> pClass,
                       final int pIndexGranularity) {
        this(pClass, new OrderedIndex<T>(pIndexGranularity));
    }

    /**
     * Construct a list containing the elements of the passed list.
     * @param pSource the source ordered list
     */
    public OrderedList(final OrderedList<T> pSource) {
        /* Initialise for the correct class */
        this(pSource.getBaseClass(), pSource);
    }

    /**
     * Construct a list containing the elements of the passed list.
     * @param pClass the class of the sortedItem
     * @param pSource the source ordered list
     */
    public OrderedList(final Class<T> pClass,
                       final List<T> pSource) {
        /* Initialise for the correct class */
        this(pClass);

        /* Loop through the source members */
        Iterator<T> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            T myItem = myIterator.next();

            /* Add the item */
            addItem(myItem);
        }
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndex the list index.
     */
    protected OrderedList(final Class<T> pClass,
                          final OrderedIndex<T> pIndex) {
        /* Store the class and index */
        theClass = pClass;
        theIndexMap = pIndex;

        /* Link the index */
        theIndexMap.declareList(this);
    }

    @Override
    public boolean add(final T pItem) {
        /* Call standard method */
        return addItem(pItem);
    }

    /**
     * Add item to list.
     * @param pItem the item to add
     * @return was the item added? true/false
     */
    protected final boolean addItem(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Increment the modification count */
        theModCount++;

        /* Allocate the new node */
        OrderedNode<T> myNode = new OrderedNode<T>(this, pItem);

        /* Register link between object and node */
        theIndexMap.registerLink(myNode);

        /* Insert the node into the list */
        insertNode(myNode);

        /* Return to caller */
        return true;
    }

    /**
     * Append item directly to the end of the list. The list needs to be sorted after this operation.
     * @param pItem the item to append.
     * @return true
     */
    public boolean append(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Increment the modification count */
        theModCount++;

        /* Allocate the new node */
        OrderedNode<T> myNode = new OrderedNode<T>(this, pItem);

        /* Register link between object and node */
        theIndexMap.registerLink(myNode);

        /* Insert the node into the list */
        myNode.addToTail();
        theIndexMap.insertNode(myNode);

        /* Return to caller */
        return true;
    }

    /**
     * Insert node.
     * @param pNode - node to insert into list
     */
    private void insertNode(final OrderedNode<T> pNode) {
        /* If we are adding to an empty list */
        if (theFirst == null) {
            /* Add to head of list */
            pNode.addToHead();

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

        /* Remove the node from the list */
        pNode.remove();
    }

    @Override
    public void clear() {
        /* Increment the modification count */
        theModCount++;

        /* Remove the items in reverse order */
        while (theLast != null) {
            /* Access and unlink the node */
            OrderedNode<T> myNode = theLast;
            theLast = myNode.getNext();

            /* Remove links from node and list */
            theIndexMap.deRegisterLink(myNode);
            myNode.remove();
        }

        /* Clear the map */
        theIndexMap.clear();
    }

    @Override
    public final boolean isEmpty() {
        /* Return details */
        return theFirst == null;
    }

    @Override
    public Iterator<T> iterator() {
        /* Return a new iterator */
        return listIterator();
    }

    @Override
    public OrderedListIterator<T> listIterator() {
        /* Return a new iterator */
        return new OrderedListIterator<T>(this);
    }

    /**
     * obtain a list Iterator for this list initialised to an item.
     * @param pItem the item to initialise to
     * @return List iterator
     */
    public OrderedListIterator<T> listIterator(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* Create a list iterator */
        return new OrderedListIterator<T>(this, myNode);
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
        return new OrderedListIterator<T>(this, myNode);
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
        for (myCurr = theFirst, myOther = myThat.theFirst; (myCurr != null)
                                                           || (myOther != null); myCurr = myCurr.getNext(), myOther = myOther.getNext()) {
            /* If either entry is null then we differ */
            if ((myCurr == null)
                || (myOther == null)) {
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
        for (OrderedNode<?> myCurr = theFirst; myCurr != null; myCurr = myCurr.getNext()) {
            /* Calculate hash */
            myHash *= HASH_PRIME;
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
            iSize = 1 + theLast.getIndex();
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
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndexMap.findUnsortedNodeForObject(myItem);

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
     * Re-sort the list.
     * @return did the list change order true/false
     */
    public boolean reSort() {
        /* Increment the modification count */
        theModCount++;

        /* Perform sort via the indexMap */
        return theIndexMap.reSort();
    }

    @Override
    public int indexOf(final Object o) {
        /* Access the node of the item */
        OrderedNode<T> myNode = getNodeForObject(o);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return -1;
        }

        /* Ensure that we have the first such object */
        myNode = OrderedNode.getFirstNodeInSequence(myNode);

        /* Access the index of the item */
        return myNode.getIndex();
    }

    @Override
    public int lastIndexOf(final Object o) {
        /* Access the node of the item */
        OrderedNode<T> myNode = getNodeForObject(o);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return -1;
        }

        /* Ensure that we have the last such object */
        myNode = OrderedNode.getLastNodeInSequence(myNode);

        /* Access the index of the item */
        return myNode.getIndex();
    }

    /**
     * Get the Node of an object.
     * @param o the object
     * @return the Node
     */
    private OrderedNode<T> getNodeForObject(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Reject if the object is invalid */
        if (!(theClass.isInstance(o))) {
            throw new ClassCastException();
        }

        /* Access as link object */
        T myItem = theClass.cast(o);

        /* Access the node of the item */
        return theIndexMap.findNodeForObject(myItem);
    }

    @Override
    public boolean contains(final Object o) {
        /* Reject if the object is null */
        if (o == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
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
    public T peekFirst() {
        /* Access the first item */
        OrderedNode<T> myNode = getFirst();

        /* Return the next object */
        return (myNode == null)
                ? null
                : myNode.getObject();
    }

    /**
     * Peek at the first item.
     * @return the first item or <code>null</code>
     */
    public T peekLast() {
        /* Access the last item */
        OrderedNode<T> myNode = getLast();

        /* Return the next object */
        return (myNode == null)
                ? null
                : myNode.getObject();
    }

    @Override
    public boolean containsAll(final Collection<?> pCollection) {
        /* Reject if the collection is null */
        if (pCollection == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
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
        OrderedListIterator<?> myIterator = listIterator();
        for (int i = 0; i < iSize; i++) {
            /* Store the next item */
            myArray[i] = myIterator.next();
        }

        /* Return the array */
        return myArray;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X[] toArray(final X[] a) {
        /* Determine the size of the array */
        int iSize = size();

        /* Reject if the sample array is null or wrong type */
        if (a == null) {
            throw new IllegalArgumentException("Null array not allowed");
        }

        /* Get the component type and check we can assign to it */
        Class<?> myClass = a.getClass().getComponentType();
        if (!myClass.isAssignableFrom(theClass)) {
            throw new ArrayStoreException();
        }

        /* Allocate the new array if required */
        X[] myRows = a;
        if (a.length < iSize) {
            myRows = (X[]) Array.newInstance(myClass, iSize);
        }

        /* Access the array as an object array */
        Object[] myArray = myRows;

        /* Loop through the list */
        OrderedListIterator<?> myIterator = listIterator();
        for (int i = 0; i < iSize; i++) {
            /* Store the next item */
            myArray[i] = myIterator.next();
        }

        /* If we have space left in the target array */
        if (iSize < myRows.length) {
            /* Set trailing element to null */
            myRows[iSize] = null;
        }

        /* Return the array */
        return myRows;
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
            append(myItem);
        }

        /* Did we make a change */
        boolean bChanged = theModCount != myModCount;
        if (bChanged) {
            /* Sort the list */
            reSort();
        }

        /* Return indication of change */
        return bChanged;
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
        Iterator<? extends T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* Add it to the list */
            append(myItem);
        }

        /* Did we make a change */
        boolean bChanged = theModCount != myModCount;
        if (bChanged) {
            /* Sort the list */
            reSort();
        }

        /* Return indication of change */
        return bChanged;
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
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* If item is in collection then ignore */
            if (pCollection.contains(myItem)) {
                continue;
            }

            /* Remove it from the list */
            myIterator.remove();
        }

        /* Return indication of change */
        return theModCount != myModCount;
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
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            T myItem = myIterator.next();

            /* If item is not in collection then ignore */
            if (!pCollection.contains(myItem)) {
                continue;
            }

            /* Remove it from the list */
            myIterator.remove();
        }

        /* Return indication of change */
        return theModCount != myModCount;
    }

    /**
     * Get the first node in the sequence.
     * @return the First node
     */
    protected OrderedNode<T> getFirst() {
        return theFirst;
    }

    /**
     * Get the last node in the sequence.
     * @return the Last node
     */
    protected OrderedNode<T> getLast() {
        return theLast;
    }

    /**
     * Set the first node in the sequence.
     * @param pFirst the First node
     */
    protected void setFirst(final OrderedNode<T> pFirst) {
        theFirst = pFirst;
    }

    /**
     * Get the last node in the sequence.
     * @param pLast the Last node
     */
    protected void setLast(final OrderedNode<T> pLast) {
        theLast = pLast;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        /* Clone the underlying object */
        @SuppressWarnings("unchecked")
        OrderedList<T> myResult = (OrderedList<T>) super.clone();

        /* Re-initialise the fields */
        myResult.theClass = theClass;
        myResult.theIndexMap = theIndexMap.newIndex(myResult);

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
        int mySize = size();
        pOutput.writeInt(mySize);
        if (mySize == 0) {
            return;
        }

        /* Write out elements */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            /* Access and write out element */
            T myItem = myIterator.next();
            pOutput.writeObject(myItem);
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

            /* Add to list */
            append(myItem);
        }
    }
}
