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
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Nested Hash map implementation. Provides hash map functionality using nested child hash maps as an
 * expansion method rather than expansion an rehashing as is performed by {@link java.util.HashMap}.
 * <ul>
 * <li>Null keys and null values are not supported.
 * <li>Collisions can only occur if hashCodes are identical.
 * <li>Child HashMaps are collapsed once they become empty.
 * <li>The remove operation of iterators is disallowed due to the structural changes that this can cause. This
 * may be revisited in future.
 * </ul>
 * @author Tony Washer
 * @param <K> the key type
 * @param <V> the value type
 */
public class NestedHashMap<K, V> implements Serializable, Cloneable, Map<K, V> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4094809884578490085L;

    /**
     * The adjustment shift.
     */
    private static final int SHIFT_BITS = 4;

    /**
     * The size of the array.
     */
    private static final int ARRAY_SIZE = (1 << SHIFT_BITS);

    /**
     * The self reference.
     */
    private transient NestedHashMap<K, V> theSelf = this;

    /**
     * The number of entries in this hashMap.
     */
    private transient int theSize = 0;

    /**
     * The cached entrySet.
     */
    private transient EntrySet theEntrySet = null;

    /**
     * The cached keySet.
     */
    private transient KeySet theKeySet = null;

    /**
     * The cached value collection.
     */
    private transient Values theValueCollection = null;

    /**
     * The hashMap array.
     */
    private transient Object[] theArray;

    /**
     * The modification count.
     */
    private transient int theModCount = 0;

    @Override
    public int size() {
        return theSize;
    }

    @Override
    public boolean isEmpty() {
        return (theSize == 0);
    }

    /**
     * Constructor.
     */
    public NestedHashMap() {
        /* Create the array */
        theArray = new Object[ARRAY_SIZE];
    }

    /**
     * Constructor for nested map.
     * @param myHash the existing entries
     */
    private NestedHashMap(final HashEntry<K, V> myHash) {
        /* Call standard map */
        this();

        /* Determine adjusted hash */
        int iHash = myHash.getHash() >>> SHIFT_BITS;

        /* Loop through the entries */
        HashEntry<K, V> myEntry = myHash;
        while (myEntry != null) {
            /* Put the entry */
            put(iHash, myEntry.getKey(), myEntry.getValue());

            /* Shift to next entry */
            myEntry = myEntry.getNext();
        }
    }

    /**
     * Constructor from map.
     * @param pMap the source map
     */
    public NestedHashMap(final Map<? extends K, ? extends V> pMap) {
        /* Call standard map */
        this();

        /* Put all the entries */
        putAll(pMap);
    }

    @Override
    public V put(final K pKey,
                 final V pValue) {
        /* Reject null keys/values */
        if ((pKey == null) || (pValue == null)) {
            throw new NullPointerException();
        }

        /* Calculate the hash */
        int iHash = pKey.hashCode();

        /* Put the value into the table */
        return put(iHash, pKey, pValue);
    }

    @Override
    public V get(final Object pKey) {
        /* Reject null keys */
        if (pKey == null) {
            throw new NullPointerException();
        }

        /* If we are empty there can be no mapping */
        if (isEmpty()) {
            return null;
        }

        /* Calculate the hash */
        int iHash = pKey.hashCode();

        /* Locate the value */
        return get(iHash, pKey);
    }

    @Override
    public V remove(final Object pKey) {
        /* Reject null keys */
        if (pKey == null) {
            throw new NullPointerException();
        }

        /* If we are empty there can be no mapping */
        if (isEmpty()) {
            return null;
        }

        /* Calculate the hash */
        int iHash = pKey.hashCode();

        /* Locate the value */
        return remove(iHash, pKey);
    }

    @Override
    public void clear() {
        /* Increment modification count */
        theModCount++;

        /* Loop through the array */
        for (int iIndex = 0; iIndex < ARRAY_SIZE; iIndex++) {
            /* Access current value */
            Object myEntry = theArray[iIndex];
            theArray[iIndex] = null;

            /* Case 1 null entry, just ignore */
            if (myEntry == null) {
                continue;
            }

            /* Case 2 we have an existing entry */
            if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                /* Clear values */
                myHash.clear();

                /* Case 3 we have an existing map */
            } else if (myEntry instanceof NestedHashMap) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) myEntry;

                /* Clear and reset */
                myMap.clear();
            }
        }

        /* Reset size */
        theSize = 0;
    }

    @Override
    public boolean containsKey(final Object pKey) {
        /* Return whether a mapping exists */
        return (get(pKey) == null);
    }

    @Override
    public boolean containsValue(final Object pValue) {
        /* Loop through the array */
        for (int iIndex = 0; iIndex < ARRAY_SIZE; iIndex++) {
            /* Access current value */
            Object myEntry = theArray[iIndex];

            /* Case 1 null entry, just ignore */
            if (myEntry == null) {
                continue;
            }

            /* Case 2 we have an existing entry */
            if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                /* Check for value */
                if (myHash.containsValue(pValue)) {
                    return true;
                }

                /* move to next entry */
                continue;
            }

            /* Case 3 we have an existing map */
            if (myEntry instanceof NestedHashMap) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) myEntry;

                /* Check for value */
                if (myMap.containsValue(pValue)) {
                    return true;
                }
            }
        }

        /* Not found */
        return false;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        /* Loop through the entry set */
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            /* Put the entry */
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Handle different classes */
        if (this.getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as NestedHashMap */
        NestedHashMap<?, ?> myThat = (NestedHashMap<?, ?>) pThat;

        /* Check size */
        if (theSize != myThat.size()) {
            return false;
        }

        /* Loop through the elements */
        EntryIterator myIterator = new EntryIterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            Entry<K, V> myEntry = myIterator.next();

            /* Check that the entry is contained in the other map */
            Object myValue = myThat.get(myEntry.getKey());
            if (!myEntry.getValue().equals(myValue)) {
                return false;
            }
        }

        /* Return true */
        return true;
    }

    @Override
    public int hashCode() {
        /* Initialise the hashCode */
        int iHashCode = 0;

        /* Loop through the elements */
        EntryIterator myIterator = new EntryIterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            Entry<K, V> myEntry = myIterator.next();

            /* Add in the hashCode */
            iHashCode += myEntry.hashCode();
        }

        /* Return the hashCode */
        return iHashCode;
    }

    @Override
    public String toString() {
        /* Handle empty map */
        EntryIterator myIterator = new EntryIterator();
        if (!myIterator.hasNext()) {
            return "{}";
        }

        /* Loop through the entries */
        StringBuilder myBuilder = new StringBuilder();
        while (myIterator.hasNext()) {
            /* Access the entry */
            Entry<K, V> myEntry = myIterator.next();

            /* Add separator if not first item */
            if (myBuilder.length() > 0) {
                myBuilder.append(", ");
            }

            /* Access values */
            K myKey = myEntry.getKey();
            V myValue = myEntry.getValue();

            /* Add to the buffer (handling self reference) */
            myBuilder.append((myKey == this) ? "(this Map)" : myKey);
            myBuilder.append('=');
            myBuilder.append((myValue == this) ? "(this Map)" : myValue);
        }

        /* Add brackets */
        myBuilder.insert(0, '{');
        myBuilder.append('}');

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Put an entry into the map.
     * @param pHash the hash
     * @param pKey the key
     * @param pValue the value
     * @return the old value for the key
     */
    private V put(final int pHash,
                  final K pKey,
                  final V pValue) {
        /* Calculate the index into the array */
        int iIndex = pHash & (ARRAY_SIZE - 1);

        /* Access current value */
        Object myEntry = theArray[iIndex];

        /* Case 1 no existing entry */
        if (myEntry == null) {
            /* Create the new entry */
            theArray[iIndex] = new HashEntry<K, V>(pHash, pKey, pValue);

            /* Increment the count of entries and modification count */
            theSize++;
            theModCount++;

            /* return that there was no previous mapping */
            return null;
        }

        /* Case 2 we have an existing entry */
        if (myEntry instanceof HashEntry) {
            /* Access the Hash Entry */
            @SuppressWarnings("unchecked")
            HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

            /* If the hash code is identical */
            if (myHash.checkHash(pHash)) {
                /* Find the entry */
                myHash = myHash.findKey(pKey);

                /* Set the new value */
                return myHash.setValue(pValue);
            }

            /* Create a new hash map based on this entry */
            myEntry = new NestedHashMap<K, V>(myHash);

            /* Store the entry and drop through */
            theArray[iIndex] = myEntry;
        }

        /* Case 3 we have a nested HashMap */
        if (myEntry instanceof NestedHashMap) {
            /* Access the Hash Map */
            @SuppressWarnings("unchecked")
            NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) myEntry;

            /* Put the entry into the map */
            V myValue = myMap.put((pHash >>> SHIFT_BITS), pKey, pValue);

            /* Increment size and modification count if the entry did not exist */
            if (myValue == null) {
                theSize++;
                theModCount++;
            }

            /* Return the value */
            return myValue;
        }

        /* Help */
        throw new IllegalStateException("Invalid Map state : " + myEntry.getClass());
    }

    /**
     * Get an entry from the map.
     * @param pHash the hash
     * @param pKey the key
     * @return the value for the key (or null if not in map)
     */
    private V get(final int pHash,
                  final Object pKey) {
        /* Calculate the index into the array */
        int iIndex = pHash & (ARRAY_SIZE - 1);

        /* Access current value */
        Object myEntry = theArray[iIndex];

        /* Case 1 no existing entry */
        if (myEntry == null) {
            /* return that there is no mapping */
            return null;
        }

        /* Case 2 we have an existing entry */
        if (myEntry instanceof HashEntry) {
            /* Access the Hash Entry */
            @SuppressWarnings("unchecked")
            HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

            /* If the hash code is identical */
            if (myHash.checkHash(pHash)) {
                /* Find the entry */
                myHash = myHash.checkKey(pKey);

                /* Return the value */
                return (myHash == null) ? null : myHash.getValue();
            }

            /* return that there is no mapping */
            return null;
        }

        /* Case 3 we have a nested HashMap */
        if (myEntry instanceof NestedHashMap) {
            /* Access the Hash Map */
            @SuppressWarnings("unchecked")
            NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) myEntry;

            /* Look for the entry in the nested map */
            return myMap.get((pHash >>> SHIFT_BITS), pKey);
        }

        /* Help */
        throw new IllegalStateException("Invalid Map state : " + myEntry.getClass());
    }

    /**
     * Remove an entry from the map.
     * @param pHash the hash
     * @param pKey the key
     * @return the value for the key (or null if not in map)
     */
    private V remove(final int pHash,
                     final Object pKey) {
        /* Calculate the index into the array */
        int iIndex = pHash & (ARRAY_SIZE - 1);

        /* Access current value */
        Object myEntry = theArray[iIndex];

        /* Case 1 no existing entry */
        if (myEntry == null) {
            /* return that there is no mapping */
            return null;
        }

        /* Case 2 we have an existing entry */
        if (myEntry instanceof HashEntry) {
            /* Access the Hash Entry */
            @SuppressWarnings("unchecked")
            HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

            /* If the hash code is identical */
            if (myHash.checkHash(pHash)) {
                /* If the top entry is not a match */
                if (!myHash.getKey().equals(pKey)) {
                    /* Find/remove the entry */
                    myHash = myHash.removeKey(pKey);
                } else {
                    /* remove the top-level index */
                    theArray[iIndex] = myHash.getNext();
                    myEntry = theArray[iIndex];
                }

                /* If not found then return null */
                if (myHash == null) {
                    return null;
                }

                /* Else return value and adjust size and modification count */
                theSize--;
                theModCount++;
                return myHash.getValue();
            }

            /* return that there is no mapping */
            return null;
        }

        /* Case 3 we have a nested HashMap */
        if (myEntry instanceof NestedHashMap) {
            /* Access the Hash Map */
            @SuppressWarnings("unchecked")
            NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) myEntry;

            /* Remove the entry from the sub-map */
            V myValue = myMap.remove((pHash >>> SHIFT_BITS), pKey);

            /* Adjust size and modification count if the entry existed */
            if (myValue != null) {
                theSize--;
                theModCount++;
            }

            /* If the underlying map is empty, remove it */
            if (myMap.isEmpty()) {
                theArray[iIndex] = null;
            }

            /* Return the value */
            return myValue;
        }

        /* Help */
        throw new IllegalStateException("Invalid Map state : " + myEntry.getClass());
    }

    /**
     * Entry Class.
     * @param <K> the key type
     * @param <V> the value type
     */
    private static final class HashEntry<K, V> implements Entry<K, V> {
        /**
         * The Hash.
         */
        private final int theHash;

        /**
         * The Key.
         */
        private final K theKey;

        /**
         * The Value.
         */
        private V theValue;

        /**
         * The next entry.
         */
        private HashEntry<K, V> theNext = null;

        /**
         * Get the hash code.
         * @return the hash code
         */
        private int getHash() {
            return theHash;
        }

        /**
         * Get the next entry.
         * @return the next entry
         */
        private HashEntry<K, V> getNext() {
            return theNext;
        }

        @Override
        public K getKey() {
            return theKey;
        }

        @Override
        public V getValue() {
            return theValue;
        }

        @Override
        public V setValue(final V pNewValue) {
            /* Pick up the old value */
            V myOld = theValue;

            /* Set the new value */
            theValue = pNewValue;

            /* Return the old value */
            return myOld;
        }

        /**
         * Constructor.
         * @param pHash the hash code
         * @param pKey the key
         * @param pValue the Value
         */
        private HashEntry(final int pHash,
                          final K pKey,
                          final V pValue) {
            /* Set the hash, key and value */
            theHash = pHash;
            theKey = pKey;
            theValue = pValue;
        }

        /**
         * Check whether this entry is a match for the hash.
         * @param pHash the hash code
         * @return is this a match for the hash
         */
        private boolean checkHash(final int pHash) {
            return (pHash == theHash);
        }

        /**
         * Check whether this entry (or any sibling) is a match for the key.
         * @param pKey the key
         * @return the matching entry or null
         */
        private HashEntry<K, V> checkKey(final Object pKey) {
            /* If this is the key, return it */
            if (theKey.equals(pKey)) {
                return this;
            }

            /* If we have further siblings, pass call on */
            if (theNext != null) {
                return theNext.checkKey(pKey);
            }

            /* No matching entry so return null */
            return null;
        }

        /**
         * Check whether this entry (or any sibling) contains this value.
         * @param pValue the value
         * @return true/false
         */
        private boolean containsValue(final Object pValue) {
            /* If the value matches return true */
            if (theValue.equals(pValue)) {
                return true;
            }

            /* If we have further siblings, pass call on */
            if (theNext != null) {
                return theNext.containsValue(pValue);
            }

            /* No matching entry so return false */
            return false;
        }

        /**
         * Remove the entry for this key.
         * @param pKey the key
         * @return the entry
         */
        private HashEntry<K, V> removeKey(final Object pKey) {
            /* If we have no next entry, just return */
            if (theNext == null) {
                return null;
            }

            /* If the next holds the key */
            if (theNext.getKey().equals(pKey)) {
                /* remove from list and return */
                HashEntry<K, V> myHash = theNext;
                theNext = myHash.getNext();
                return myHash;
            }

            /* pass call on */
            return theNext.removeKey(pKey);
        }

        /**
         * Find and remove the entry for this key.
         * @param pKey the key
         * @return the entry
         */
        private HashEntry<K, V> findKey(final K pKey) {
            /* If this is the key, return it */
            if (theKey.equals(pKey)) {
                return this;
            }

            /* If we have further siblings, pass call on */
            if (theNext != null) {
                return theNext.findKey(pKey);
            }

            /* No matching entry so create new entry */
            theNext = new HashEntry<K, V>(theHash, pKey, null);
            return theNext;
        }

        /**
         * Clear entries.
         */
        private void clear() {
            /* If we have no next element just return */
            if (theNext == null) {
                return;
            }

            /* Clear further elements */
            theNext.clear();

            /* Clear entry */
            theNext = null;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check that target is same class */
            if (this.getClass() != pThat.getClass()) {
                return false;
            }

            /* Access as HashEntry */
            HashEntry<?, ?> myThat = (HashEntry<?, ?>) pThat;

            /* Check hashCode */
            if (theHash != myThat.getHash()) {
                return false;
            }

            /* Check key */
            if (theKey.equals(myThat.getKey())) {
                return false;
            }

            /* Check value */
            return (theValue.equals(myThat.getValue()));
        }

        @Override
        public int hashCode() {
            /* Create hashCode */
            return theKey.hashCode() ^ theValue.hashCode();
        }

        @Override
        public String toString() {
            return theKey + "=" + theValue;
        }
    }

    /**
     * Generic iterator class.
     * @param <E> the type provided by the next function
     */
    private abstract class HashIterator<E> implements Iterator<E> {
        /**
         * The next entry to return.
         */
        private Object theNext = null;

        /**
         * The last entry returned.
         */
        private HashEntry<K, V> theLast = null;

        /**
         * Current slot.
         */
        private int theIndex = 0;

        /**
         * The expected modification count.
         */
        private int theExpectedModCount = theModCount;

        /**
         * Constructor.
         */
        private HashIterator() {
            /* If there are elements */
            if (!isEmpty()) {
                /* Shift to first element */
                advanceIndex();
            }
        }

        /**
         * Advance the index to the next slot.
         */
        private void advanceIndex() {
            theNext = null;
            while (theIndex < ARRAY_SIZE) {
                /* Access the next entry */
                theNext = theArray[theIndex++];
                if (theNext != null) {
                    break;
                }
            }
        }

        @Override
        public final boolean hasNext() {
            return theNext != null;
        }

        /**
         * Obtain next entry.
         * @return the next entry
         */
        @SuppressWarnings("unchecked")
        protected final HashEntry<K, V> nextEntry() {
            /* Handle changed list */
            if (theModCount != theExpectedModCount) {
                throw new ConcurrentModificationException();
            }

            /* Handle end of list */
            if (theNext == null) {
                throw new NoSuchElementException();
            }

            /* If the next item is a HashEntry */
            if (theNext instanceof HashEntry) {
                /* Access the next entry */
                theLast = (HashEntry<K, V>) theNext;

                /* Shift entry */
                theNext = theLast.getNext();

                /* If we have reached the end */
                if (theNext == null) {
                    /* Advance the index */
                    advanceIndex();
                }

                /* Return the next element */
                return theLast;
            }

            /* If the next item is a NestedHashMap */
            if (theNext instanceof NestedHashMap) {
                /* Access the map */
                NestedHashMap<K, V> myMap = (NestedHashMap<K, V>) theNext;

                /* Access an iterator and store as next element */
                theNext = myMap.new EntryIterator();
            }

            /* If the next item is an iterator */
            if (theNext instanceof Iterator<?>) {
                /* Access the entry */
                Iterator<HashEntry<K, V>> myIterator = (Iterator<HashEntry<K, V>>) theNext;

                /* Shift entry */
                theLast = myIterator.next();

                /* If we have reached the end */
                if (!myIterator.hasNext()) {
                    /* Advance the index */
                    advanceIndex();
                }

                /* Return the next element */
                return theLast;
            }

            /* Handle invalid state */
            throw new IllegalStateException("Invalid Iterator state : " + theNext.getClass());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Iterator over values.
     */
    private final class ValueIterator extends HashIterator<V> {
        @Override
        public V next() {
            return nextEntry().getValue();
        }
    }

    @Override
    public Collection<V> values() {
        /* If we have a cached valueCollection, return it */
        if (theValueCollection != null) {
            return theValueCollection;
        }

        /* Allocate and return the valueCollection */
        theValueCollection = new Values();
        return theValueCollection;
    }

    /**
     * Values class.
     */
    private final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return theSize;
        }

        @Override
        public boolean contains(final Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
            /* Call clear function */
            theSelf.clear();
        }
    }

    /**
     * Iterator over keys.
     */
    private final class KeyIterator extends HashIterator<K> {
        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    @Override
    public Set<K> keySet() {
        /* If we have a cached keySet, return it */
        if (theKeySet != null) {
            return theKeySet;
        }

        /* Allocate and return the keySet */
        theKeySet = new KeySet();
        return theKeySet;
    }

    /**
     * Key Set class.
     */
    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return theSize;
        }

        @Override
        public boolean contains(final Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(final Object o) {
            return (theSelf.remove(o) != null);
        }

        @Override
        public void clear() {
            /* Call clear function */
            theSelf.clear();
        }
    }

    /**
     * Iterator over entries.
     */
    private final class EntryIterator extends HashIterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            return nextEntry();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        /* If we have a cached entryySet, return it */
        if (theEntrySet != null) {
            return theEntrySet;
        }

        /* Allocate and return the entrySet */
        theEntrySet = new EntrySet();
        return theEntrySet;
    }

    /**
     * Entry Set class.
     */
    private final class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return theSize;
        }

        @Override
        public boolean contains(final Object o) {
            /* Make sure that this is a HashEntry */
            if (!(o instanceof HashEntry)) {
                return false;
            }

            /* Access as entry */
            @SuppressWarnings("unchecked")
            HashEntry<K, V> myEntry = (HashEntry<K, V>) o;

            /* Check that this key exists with this value */
            V myValue = get(myEntry.getKey());
            return myValue != null && myValue.equals(myEntry.getValue());
        }

        @Override
        public boolean remove(final Object o) {
            /* Ignore if entry not in set */
            if (!contains(o)) {
                return false;
            }

            /* Access as entry */
            @SuppressWarnings("unchecked")
            HashEntry<K, V> myEntry = (HashEntry<K, V>) o;

            /* Remove the existing entry */
            theSelf.remove(myEntry.getKey());
            return true;
        }

        @Override
        public void clear() {
            /* Call clear function */
            theSelf.clear();
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        /* Clone the underlying object */
        @SuppressWarnings("unchecked")
        NestedHashMap<K, V> myResult = (NestedHashMap<K, V>) super.clone();

        /* Re-initialise the fields */
        myResult.theArray = new Object[ARRAY_SIZE];
        myResult.theSelf = myResult;
        myResult.theSize = 0;
        myResult.theEntrySet = null;
        myResult.theKeySet = null;
        myResult.theValueCollection = null;

        /* Copy all the entries */
        myResult.putAll(this);
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
        pOutput.writeInt(theSize);
        if (theSize == 0) {
            return;
        }

        /* Write out keys and values (alternating) */
        EntryIterator myIterator = new EntryIterator();
        while (myIterator.hasNext()) {
            /* Access next entry */
            Map.Entry<K, V> myEntry = myIterator.next();

            /* Write out key and value */
            pOutput.writeObject(myEntry.getKey());
            pOutput.writeObject(myEntry.getValue());
        }

        /* Throw exception if modifications occurred */
        if (theModCount != myExpectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Read the map from an object input stream.
     * @param pInput the object input stream
     * @throws IOException on error
     * @throws ClassNotFoundException on error
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream pInput) throws IOException, ClassNotFoundException {
        /* Read in the default stuff */
        pInput.defaultReadObject();

        /* Read in size number of Mappings */
        int mySize = pInput.readInt();

        /* Read the keys and values, and put the mappings in the HashMap */
        for (int i = 0; i < mySize; i++) {
            K myKey = (K) pInput.readObject();
            V myValue = (V) pInput.readObject();
            put(myKey, myValue);
        }
    }
}
