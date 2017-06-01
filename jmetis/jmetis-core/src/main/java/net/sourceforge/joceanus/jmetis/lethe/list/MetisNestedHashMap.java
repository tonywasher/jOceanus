/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.list;

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
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Nested Hash map implementation. Provides hash map functionality using nested child arrays as an
 * expansion method, rather than expansion and rehashing as is performed by
 * {@link java.util.HashMap}.
 * <ul>
 * <li>Null keys/values are supported.
 * <li>Collisions can only occur if hashCodes are identical.
 * <li>Nested ChildArrays are reduced when no longer needed.
 * </ul>
 * @author Tony Washer
 * @param <K> the key type
 * @param <V> the value type
 */
public class MetisNestedHashMap<K, V>
        implements Serializable, Cloneable, Map<K, V> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4094809884578490085L;

    /**
     * The minimum adjustment shift.
     */
    private static final int SHIFT_MIN_BITS = 4;

    /**
     * The maximum adjustment shift.
     */
    private static final int SHIFT_MAX_BITS = 8;

    /**
     * The default adjustment shift.
     */
    private static final int SHIFT_DEF_BITS = 4;

    /**
     * The self reference text.
     */
    private static final String SELF_REF = "(this map)";

    /**
     * The adjustment shift.
     */
    private transient int theShiftBits;

    /**
     * The size of the array.
     */
    private transient int theArraySize;

    /**
     * The self reference.
     */
    private transient MetisNestedHashMap<K, V> theSelf = this;

    /**
     * The number of entries in this hashMap.
     */
    private transient int theSize;

    /**
     * The cached entrySet.
     */
    private transient EntrySet theEntrySet;

    /**
     * The cached keySet.
     */
    private transient KeySet theKeySet;

    /**
     * The cached value collection.
     */
    private transient Values theValueCollection;

    /**
     * The hashMap array.
     */
    private transient ArrayElement theArray;

    /**
     * The modification count.
     */
    private transient volatile int theModCount;

    /**
     * Constructor.
     */
    public MetisNestedHashMap() {
        /* Pass through for default shift */
        this(SHIFT_DEF_BITS);
    }

    /**
     * Constructor.
     * @param pShiftBits the number of shift bits
     */
    public MetisNestedHashMap(final int pShiftBits) {
        /* Ensure that the shift bits are in range */
        if ((pShiftBits < SHIFT_MIN_BITS) || (pShiftBits > SHIFT_MAX_BITS)) {
            throw new IllegalArgumentException("Invalid number of shift bits " + pShiftBits);
        }

        /* Calculate the array size */
        theShiftBits = pShiftBits;
        theArraySize = getArraySize();

        /* Create the array */
        theArray = new ArrayElement(theArraySize);
    }

    /**
     * Constructor from map.
     * @param pMap the source map
     */
    public MetisNestedHashMap(final Map<? extends K, ? extends V> pMap) {
        /* Call standard map */
        this();

        /* Loop through the entry set */
        for (Entry<? extends K, ? extends V> e : pMap.entrySet()) {
            /* Put the entry */
            putEntry(hashKey(e.getKey()), e.getKey(), e.getValue());
        }
    }

    @Override
    public int size() {
        return theSize;
    }

    @Override
    public boolean isEmpty() {
        return theSize == 0;
    }

    /**
     * Calculate array size.
     * @return the array size
     */
    private int getArraySize() {
        return 1 << theShiftBits;
    }

    /**
     * Calculate the hash code.
     * @param pKey the key
     * @return the hash code
     */
    private static int hashKey(final Object pKey) {
        /* Return the hash for the key */
        return (pKey == null)
                              ? 0
                              : pKey.hashCode();
    }

    /**
     * ArrayElement holds the array and data about the array.
     */
    private static final class ArrayElement {
        /**
         * The array.
         */
        private final Object[] theArray;

        /**
         * The number of elements in the array.
         */
        private int theNumElements;

        /**
         * The Parent reference.
         */
        private final ArrayElement theParent;

        /**
         * The Parental index.
         */
        private final int theIndex;

        /**
         * Constructor.
         * @param pSize the array size
         */
        private ArrayElement(final int pSize) {
            theNumElements = 0;
            theParent = null;
            theIndex = -1;
            theArray = new Object[pSize];
        }

        /**
         * Constructor.
         * @param pParent the parent
         * @param iIndex the entry index of the parent
         */
        private ArrayElement(final ArrayElement pParent,
                             final int iIndex) {
            int iArraySize = pParent.theArray.length;
            theNumElements = 0;
            theParent = pParent;
            theParent.theNumElements += iArraySize;
            theIndex = iIndex;
            theArray = new Object[iArraySize];
        }

        /**
         * Collapse the array.
         */
        private void collapseArray() {
            /* Ignore if too busy or already at top */
            if ((theParent == null) || (theNumElements > 1)) {
                return;
            }

            /* Locate the remaining element */
            Object myObject = null;
            int iArraySize = theArray.length;
            for (int i = 0; i < iArraySize; i++) {
                if (theArray[i] != null) {
                    myObject = theArray[i];
                    break;
                }
            }

            /* Place into parent and try to collapse parent */
            theParent.theArray[theIndex] = myObject;
            theParent.theNumElements -= iArraySize;
            theParent.collapseArray();
        }
    }

    @Override
    public V put(final K pKey,
                 final V pValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Put the value into the table */
        return putEntry(iHash, pKey, pValue);
    }

    @Override
    public V get(final Object pKey) {
        /* If we are empty there can be no mapping */
        if (isEmpty()) {
            return null;
        }

        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Return the value */
        return (myEntry != null)
                                 ? myEntry.getValue()
                                 : null;
    }

    @Override
    public V remove(final Object pKey) {
        /* If we are empty there can be no mapping */
        if (isEmpty()) {
            return null;
        }

        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Remove the value if it exists */
        HashEntry<K, V> myEntry = removeEntry(iHash, pKey);

        /* Return the old value */
        return (myEntry != null)
                                 ? myEntry.getValue()
                                 : null;
    }

    @Override
    public void clear() {
        /* Increment modification count */
        theModCount++;

        /* Clear the array */
        clearArray(theArray);

        /* Reset size */
        theSize = 0;
    }

    /**
     * Clear array.
     * @param pArray the array to clear
     */
    private void clearArray(final ArrayElement pArray) {
        /* Loop through the array */
        for (int iIndex = 0; iIndex < theArraySize; iIndex++) {
            /* Access current value and remove from array */
            Object[] myArray = pArray.theArray;
            Object myEntry = myArray[iIndex];
            myArray[iIndex] = null;

            /* If we have an existing entry */
            if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                /* Clear values */
                myHash.clear();

                /* if we have an extension array */
            } else if (myEntry instanceof ArrayElement) {
                /* Clear the array */
                clearArray((ArrayElement) myEntry);
            }
        }
    }

    @Override
    public boolean containsKey(final Object pKey) {
        /* Return whether a mapping exists */
        return getEntry(hashKey(pKey), pKey) != null;
    }

    @Override
    public boolean containsValue(final Object pValue) {
        /* Check the array */
        return containsValue(theArray, pValue);
    }

    /**
     * Check whether the array contains the value.
     * @param pArray the array to check
     * @param pValue the value to check
     * @return true/false
     */
    private boolean containsValue(final ArrayElement pArray,
                                  final Object pValue) {
        /* Determine whether we have a null value */
        boolean isNullValue = pValue == null;

        /* Loop through the array */
        for (int iIndex = 0; iIndex < theArraySize; iIndex++) {
            /* Access current value */
            Object[] myArray = pArray.theArray;
            Object myEntry = myArray[iIndex];

            /* If we have an existing entry */
            if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                if (isNullValue) {
                    /* Check for null value */
                    if (myHash.containsNullValue()) {
                        return true;
                    }

                } else {
                    /* Check for value */
                    if (myHash.containsValue(pValue)) {
                        return true;
                    }
                }

                /* if we have an extension array pass, check its contents */
            } else if ((myEntry instanceof ArrayElement) && (containsValue((ArrayElement) myEntry, pValue))) {
                return true;
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
        MetisNestedHashMap<?, ?> myThat = (MetisNestedHashMap<?, ?>) pThat;

        /* Check size */
        if (theSize != myThat.size()) {
            return false;
        }

        /* Loop through the elements */
        EntryIterator myIterator = new EntryIterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            Entry<K, V> myEntry = myIterator.next();

            /* Check that the entry is contained in the other map and has equal value */
            K myKey = myEntry.getKey();
            V myValue = myEntry.getValue();
            int myHash = hashKey(myKey);
            HashEntry<?, ?> myTest = myThat.getEntry(myHash, myKey);
            if ((myTest == null) || ((myTest.theValue == null)
                                                               ? myValue != null
                                                               : !myTest.theValue.equals(myValue))) {
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
            myBuilder.append((myKey == this)
                                             ? SELF_REF
                                             : myKey);
            myBuilder.append('=');
            myBuilder.append((myValue == this)
                                               ? SELF_REF
                                               : myValue);
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
    private V putEntry(final int pHash,
                       final K pKey,
                       final V pValue) {
        /* Initialise the state */
        int myShift = 0;
        int myMask = theArraySize - 1;
        int myCurHash = pHash;
        ArrayElement myArray = theArray;
        int myIndex;

        /* Loop to locate HashEntry location */
        for (;;) {
            /* Calculate the index into the array */
            myIndex = myCurHash & myMask;

            /* Access current entry */
            Object myEntry = myArray.theArray[myIndex];

            /* If this is a nested array */
            if (myEntry instanceof ArrayElement) {
                /* Adjust array and index */
                myShift += theShiftBits;
                myCurHash >>>= theShiftBits;
                myArray = (ArrayElement) myEntry;

                /* Re-loop */

                /* If this is an empty slot */
            } else if (myEntry == null) {
                /* Create and store the new entry */
                HashEntry<K, V> myHashEntry = new HashEntry<>(pHash, pKey);
                myHashEntry.setValue(pValue);
                myArray.theArray[myIndex] = myHashEntry;

                /* Increment the count of entries and modification count */
                theSize++;
                theModCount++;
                myArray.theNumElements++;

                /* return that there was no previous mapping */
                return null;

                /* If this is an entry */
            } else if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHashEntry = (HashEntry<K, V>) myEntry;

                /* If the hash code is identical */
                if (myHashEntry.theHash == pHash) {
                    /* Obtain the entry for the key */
                    myHashEntry = myHashEntry.accessKeyEntry(pKey);

                    /* If the entry already exists */
                    if (myHashEntry.valueSet()) {
                        /* Set the new value and return old value */
                        return myHashEntry.setValue(pValue);
                    }

                    /* Set the value */
                    myHashEntry.setValue(pValue);

                    /* Increment the count of entries and modification count */
                    theSize++;
                    theModCount++;
                    myArray.theNumElements++;

                    /* Return null as old value */
                    return null;
                }

                /* Hash does not match so we need to extend the map */
                ArrayElement myNewArray = new ArrayElement(myArray, myIndex);

                /* Calculate index of existing entry */
                myShift += theShiftBits;
                int myNewIndex = (myHashEntry.theHash >>> myShift) & myMask;
                myNewArray.theArray[myNewIndex] = myHashEntry;
                myNewArray.theNumElements++;
                myArray.theArray[myIndex] = myNewArray;

                /* Adjust array and index */
                myCurHash >>>= theShiftBits;
                myArray = myNewArray;

                /* Re-loop */
            }
        }
    }

    /**
     * Get an entry from the map.
     * @param pHash the hash
     * @param pKey the key
     * @return the value for the key (or null if not in map)
     */
    private HashEntry<K, V> getEntry(final int pHash,
                                     final Object pKey) {
        /* Initialise the state */
        int myMask = theArraySize - 1;
        int myCurHash = pHash;
        ArrayElement myCurArray = theArray;
        int myIndex;

        /* Loop to locate HashEntry location */
        for (;;) {
            /* Calculate the index into the array */
            myIndex = myCurHash & myMask;

            /* Access current entry */
            Object myEntry = myCurArray.theArray[myIndex];

            /* If this is an extension array */
            if (myEntry instanceof ArrayElement) {
                /* Adjust array and index */
                myCurHash >>>= theShiftBits;
                myCurArray = (ArrayElement) myEntry;

                /* Re-loop */
                continue;

                /* if this is an empty slot */
            } else if (myEntry == null) {
                /* Key not found */
                return null;

                /* If this is an entry */
            } else if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                /* If the hash code is identical */
                if (myHash.theHash == pHash) {
                    /* Find the entry */
                    myHash = myHash.findExistingKey(pKey);

                    /* Return the entry */
                    return myHash;
                }

                /* return that there is no mapping */
                return null;
            }
        }
    }

    /**
     * Remove an entry from the map.
     * @param pHash the hash
     * @param pKey the key
     * @return the entry for the key (or null if not in map)
     */
    private HashEntry<K, V> removeEntry(final int pHash,
                                        final Object pKey) {
        /* Initialise the state */
        int myMask = theArraySize - 1;
        int myCurHash = pHash;
        ArrayElement myArray = theArray;
        int myIndex;

        /* Loop to locate HashEntry location */
        for (;;) {
            /* Calculate the index into the array */
            myIndex = myCurHash & myMask;

            /* Access current entry */
            Object myEntry = myArray.theArray[myIndex];

            /* If this is an extension array */
            if (myEntry instanceof ArrayElement) {
                /* Adjust array and index */
                myCurHash >>>= theShiftBits;
                myArray = (ArrayElement) myEntry;

                /* Re-loop */
                continue;

                /* if this is an empty slot */
            } else if (myEntry == null) {
                /* Key not found */
                return null;

                /* If this is an entry */
            } else if (myEntry instanceof HashEntry) {
                /* Access the Hash Entry */
                @SuppressWarnings("unchecked")
                HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                /* If the hash code is identical */
                if (myHash.theHash == pHash) {
                    /* If the top entry is not a match */
                    if ((pKey == null)
                                       ? myHash.theKey != null
                                       : !pKey.equals(myHash.theKey)) {
                        /* Find/remove the entry */
                        myHash = myHash.removeKey(pKey);

                        /* If not found then return null */
                        if (myHash == null) {
                            return null;
                        }

                        /* else we have found the entry at the top */
                    } else {
                        /* remove the top-level hash */
                        myArray.theArray[myIndex] = myHash.theNext;

                        /* If we no longer have an entry at this position */
                        if (myHash.theNext == null) {
                            /* Decrement number of entries and try to collapse the array */
                            myArray.theNumElements--;
                            myArray.collapseArray();
                        }
                    }

                    /* adjust size and modification count */
                    theSize--;
                    theModCount++;

                    /* return the entry */
                    return myHash;
                }

                /* return that there is no mapping */
                return null;
            }
        }
    }

    /**
     * Entry Class.
     * @param <K> the key type
     * @param <V> the value type
     */
    private static final class HashEntry<K, V>
            implements Entry<K, V> {
        /**
         * The Hash.
         */
        private int theHash;

        /**
         * The Key.
         */
        private final K theKey;

        /**
         * The Value.
         */
        private V theValue;

        /**
         * Has the value been set?.
         */
        private boolean valueSet;

        /**
         * The next entry.
         */
        private HashEntry<K, V> theNext = null;

        /**
         * Constructor.
         * @param pHash the hash code
         * @param pKey the key
         */
        private HashEntry(final int pHash,
                          final K pKey) {
            /* Set the hash, key and value */
            theHash = pHash;
            theKey = pKey;
            valueSet = false;
        }

        /**
         * Is the value set?.
         * @return true/false
         */
        private boolean valueSet() {
            return valueSet;
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
            valueSet = true;

            /* Return the old value */
            return myOld;
        }

        /**
         * Check whether this entry (or any sibling) is a match for the key.
         * @param pKey the key
         * @return the matching entry or null
         */
        private HashEntry<K, V> findExistingKey(final Object pKey) {
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            while (myHash != null) {
                /* Check item */
                if ((pKey == null)
                                   ? myHash.theKey == null
                                   : pKey.equals(myHash.theKey)) {
                    return myHash;
                }

                /* Move to next item */
                myHash = myHash.theNext;
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
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            while (myHash != null) {
                /* Check item */
                if (pValue.equals(myHash.theValue)) {
                    return true;
                }

                /* Move to next item */
                myHash = myHash.theNext;
            }

            /* No matching entry so return false */
            return false;
        }

        /**
         * Check whether this entry (or any sibling) contains the null value.
         * @return true/false
         */
        private boolean containsNullValue() {
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            while (myHash != null) {
                /* Check item */
                if (myHash.theValue == null) {
                    return true;
                }

                /* Move to next item */
                myHash = myHash.theNext;
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
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            for (;;) {
                /* Access next item */
                HashEntry<K, V> myNext = myHash.theNext;

                /* If no next entry, return null */
                if (myNext == null) {
                    return null;
                }

                /* If the next holds the key */
                if ((pKey == null)
                                   ? myNext.theKey == null
                                   : pKey.equals(myNext.theKey)) {
                    /* Unlink the entry and return it */
                    myHash.theNext = myNext.theNext;
                    return myNext;
                }

                /* Move to next item */
                myHash = myNext;
            }
        }

        /**
         * Find (or create) the entry for this key.
         * @param pKey the key
         * @return the entry
         */
        private HashEntry<K, V> accessKeyEntry(final K pKey) {
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            for (;;) {
                /* If we match the key */
                if ((pKey == null)
                                   ? myHash.theKey == null
                                   : pKey.equals(myHash.theKey)) {
                    /* Return it */
                    return myHash;
                }

                /* Access next item */
                HashEntry<K, V> myNext = myHash.theNext;

                /* If no next entry, allocate new entry */
                if (myNext == null) {
                    theNext = new HashEntry<>(theHash, pKey);
                    return theNext;
                }

                /* Move to next item */
                myHash = myNext;
            }
        }

        /**
         * Clear entries.
         */
        private void clear() {
            /* Loop through all the siblings */
            HashEntry<K, V> myHash = this;
            while (myHash != null) {
                /* Access next item */
                HashEntry<K, V> myNext = myHash.theNext;

                /* Clear entry */
                myHash.theNext = null;

                /* Move to next item */
                myHash = myNext;
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

            /* Check that target is same class */
            if (this.getClass() != pThat.getClass()) {
                return false;
            }

            /* Access as HashEntry */
            HashEntry<?, ?> myThat = (HashEntry<?, ?>) pThat;

            /* Check hashCode */
            if (theHash != myThat.theHash) {
                return false;
            }

            /* Check key */
            if ((theKey == null)
                                 ? myThat.theKey != null
                                 : !theKey.equals(myThat.theKey)) {
                return false;
            }

            /* Check value */
            return (theValue == null)
                                      ? myThat.theValue == null
                                      : theValue.equals(myThat.theValue);
        }

        @Override
        public int hashCode() {
            /* Create combined hashCode */
            return (theValue == null)
                                      ? theHash
                                      : theHash ^ theValue.hashCode();
        }

        @Override
        public String toString() {
            return theKey + "=" + theValue;
        }
    }

    /**
     * Array IteratorClass.
     */
    private final class ArrayIterator {
        /**
         * The array to operate on.
         */
        private final ArrayElement theArray;

        /**
         * The current index.
         */
        private int theIndex;

        /**
         * Last Hash entry.
         */
        private HashEntry<K, V> theLast = null;

        /**
         * Next Hash entry to return.
         */
        private HashEntry<K, V> theNext = null;

        /**
         * Nested Array iterator.
         */
        private ArrayIterator theIterator = null;

        /**
         * Constructor.
         * @param pArray the array to iterate.
         */
        private ArrayIterator(final ArrayElement pArray) {
            theArray = pArray;
            theIndex = -1;
        }

        /**
         * Is there another element in the array?
         * @return true/false
         */
        private boolean hasNext() {
            /* If we have an iterator */
            if (theIterator != null) {
                /* Check for further elements */
                if (theIterator.hasNext()) {
                    return true;
                }

                /* else check whether we have more in the current hash */
            } else if (theNext != null) {
                return true;
            }

            /* Loop through the remaining array looking for elements */
            for (int i = theIndex + 1; i < theArraySize; i++) {
                if (theArray.theArray[i] != null) {
                    return true;
                }
            }

            /* No more elements */
            return false;
        }

        /**
         * Obtain the next element in the array.
         * @return the next element
         */
        private HashEntry<K, V> next() {
            /* If we have an iterator */
            if (theIterator != null) {
                /* If we have further elements */
                if (theIterator.hasNext()) {
                    /* Pass the call on */
                    return theIterator.next();
                }

                /* No further use for iterator */
                theIterator = null;

                /* If we have a next entry */
            } else if (theNext != null) {
                /* Record and return it */
                theLast = theNext;
                theNext = theNext.theNext;
                return theLast;
            }

            /* Loop through the remaining array looking for elements */
            for (int i = theIndex + 1; i < theArraySize; i++) {
                /* Access entry */
                Object myEntry = theArray.theArray[i];

                /* If this is an entry */
                if (myEntry instanceof HashEntry) {
                    /* Access the Hash Entry */
                    @SuppressWarnings("unchecked")
                    HashEntry<K, V> myHash = (HashEntry<K, V>) myEntry;

                    /* Record and return it */
                    theIndex = i;
                    theLast = myHash;
                    theNext = myHash.theNext;
                    return myHash;
                }

                /* If this is an extension array */
                if (myEntry instanceof ArrayElement) {
                    /* Create a nested iterator */
                    theIndex = i;
                    theLast = null;
                    theNext = null;
                    theIterator = new ArrayIterator((ArrayElement) myEntry);

                    /* Pass the call on */
                    return theIterator.next();
                }
            }

            /* No more elements */
            return null;
        }

        /**
         * Remove last entry.
         */
        private void remove() {
            /* If we have an iterator */
            if (theIterator != null) {
                /* Pass the call on */
                theIterator.remove();
            }

            /* Reject if we do not have a last element */
            if (theLast == null) {
                throw new IllegalStateException();
            }

            /* Remove the entry */
            removeEntry(theLast.theHash, theLast.theKey);
            theLast = null;
        }
    }

    /**
     * Generic iterator class.
     * @param <E> the type provided by the next function
     */
    private abstract class HashIterator<E>
            implements Iterator<E> {
        /**
         * The next entry to return.
         */
        private final ArrayIterator theIterator;

        /**
         * The expected modification count.
         */
        private int theExpectedModCount = theModCount;

        /**
         * Constructor.
         */
        private HashIterator() {
            /* Allocate the iterator */
            theIterator = new ArrayIterator(theArray);
        }

        @Override
        public final boolean hasNext() {
            return theIterator.hasNext();
        }

        /**
         * Obtain next entry.
         * @return the next entry
         */
        protected final HashEntry<K, V> nextEntry() {
            /* Handle changed list */
            if (theModCount != theExpectedModCount) {
                throw new ConcurrentModificationException();
            }

            /* Return the next element */
            return theIterator.next();
        }

        @Override
        public void remove() {
            /* Handle changed list */
            if (theModCount != theExpectedModCount) {
                throw new ConcurrentModificationException();
            }

            /* Remove the element */
            theIterator.remove();

            /* Adjust modification count */
            theExpectedModCount = theModCount;
        }

        @Override
        public void forEachRemaining(final Consumer<? super E> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }

    /**
     * Iterator over values.
     */
    private final class ValueIterator
            extends HashIterator<V> {
        @Override
        public V next() {
            /* Check validity */
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            /* Return next entry */
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
    private final class Values
            extends AbstractCollection<V> {
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

        @Override
        public void forEach(final Consumer<? super V> pAction) {
            Iterator<V> myIterator = iterator();
            while (myIterator.hasNext()) {
                pAction.accept(myIterator.next());
            }
        }

        @Override
        public boolean removeIf(final Predicate<? super V> pCheck) {
            Iterator<V> myIterator = iterator();
            while (myIterator.hasNext()) {
                if (pCheck.test(myIterator.next())) {
                    myIterator.remove();
                }
            }
            return false;
        }

        @Override
        public Spliterator<V> spliterator() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<V> stream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<V> parallelStream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Iterator over keys.
     */
    private final class KeyIterator
            extends HashIterator<K> {
        @Override
        public K next() {
            /* Check validity */
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            /* Return next entry */
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
    private final class KeySet
            extends AbstractSet<K> {
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
            return theSelf.remove(o) != null;
        }

        @Override
        public void clear() {
            /* Call clear function */
            theSelf.clear();
        }

        @Override
        public void forEach(final Consumer<? super K> pAction) {
            Iterator<K> myIterator = iterator();
            while (myIterator.hasNext()) {
                pAction.accept(myIterator.next());
            }
        }

        @Override
        public boolean removeIf(final Predicate<? super K> pCheck) {
            Iterator<K> myIterator = iterator();
            while (myIterator.hasNext()) {
                if (pCheck.test(myIterator.next())) {
                    myIterator.remove();
                }
            }
            return false;
        }

        @Override
        public Spliterator<K> spliterator() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<K> stream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<K> parallelStream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Iterator over entries.
     */
    private final class EntryIterator
            extends HashIterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            /* Check validity */
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            /* Return next entry */
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
    private final class EntrySet
            extends AbstractSet<Entry<K, V>> {
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
            HashEntry<?, ?> myTest = getEntry(myEntry.theHash, myEntry.theKey);
            return (myTest != null) && ((myEntry.theValue == null)
                                                                   ? myTest.theValue == null
                                                                   : myEntry.theValue.equals(myTest.theValue));
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

        @Override
        public void forEach(final Consumer<? super Entry<K, V>> pAction) {
            Iterator<Entry<K, V>> myIterator = iterator();
            while (myIterator.hasNext()) {
                pAction.accept(myIterator.next());
            }
        }

        @Override
        public boolean removeIf(final Predicate<? super Entry<K, V>> pCheck) {
            Iterator<Entry<K, V>> myIterator = iterator();
            while (myIterator.hasNext()) {
                if (pCheck.test(myIterator.next())) {
                    myIterator.remove();
                }
            }
            return false;
        }

        @Override
        public Spliterator<Entry<K, V>> spliterator() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<Entry<K, V>> stream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<Entry<K, V>> parallelStream() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        /* Clone the underlying object */
        MetisNestedHashMap<K, V> myResult = (MetisNestedHashMap<K, V>) super.clone();

        /* Re-initialise the fields */
        myResult.theShiftBits = theShiftBits;
        myResult.theArraySize = theArraySize;
        myResult.theArray = new ArrayElement(theArraySize);
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

        /* Write out the number of shift bits */
        pOutput.writeInt(theShiftBits);

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

        /* Read in number of shift bits */
        theShiftBits = pInput.readInt();
        theArraySize = getArraySize();
        theArray = new ArrayElement(theArraySize);

        /* Finish initialisation */
        theSelf = this;
        theSize = 0;
        theEntrySet = null;
        theKeySet = null;
        theValueCollection = null;

        /* Read in size number of Mappings */
        int mySize = pInput.readInt();

        /* Read the keys and values, and put the mappings in the HashMap */
        for (int i = 0; i < mySize; i++) {
            K myKey = (K) pInput.readObject();
            V myValue = (V) pInput.readObject();
            put(myKey, myValue);
        }
    }

    @Override
    public V compute(final K pKey,
                     final BiFunction<? super K, ? super V, ? extends V> pReMap) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        V myOldValue = myEntry == null
                                       ? null
                                       : myEntry.getValue();

        /* Determine new value */
        V myNewValue = pReMap.apply(pKey, myOldValue);

        /* If we had an old value */
        if (myOldValue != null) {
            /* If we have a replacement value */
            if (myNewValue != null) {
                /* Store it */
                putEntry(iHash, pKey, myNewValue);

                /* else remove old value */
            } else {
                removeEntry(iHash, pKey);
            }

            /* else no existing value but new value supplied */
        } else if (myNewValue != null) {
            putEntry(iHash, pKey, myNewValue);
        }

        /* Return the new value */
        return myNewValue;
    }

    @Override
    public V computeIfAbsent(final K pKey,
                             final Function<? super K, ? extends V> pMap) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        V myOldValue = myEntry == null
                                       ? null
                                       : myEntry.getValue();

        /* If we have no existing value */
        if (myOldValue == null) {
            /* Map the value */
            V myNewValue = pMap.apply(pKey);

            /* If we have a new value store it */
            if (myNewValue != null) {
                putEntry(iHash, pKey, myNewValue);
            }

            /* Return the new value */
            return myNewValue;
        }

        /* No change */
        return null;
    }

    @Override
    public V computeIfPresent(final K pKey,
                              final BiFunction<? super K, ? super V, ? extends V> pReMap) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        V myOldValue = myEntry == null
                                       ? null
                                       : myEntry.getValue();

        /* If we had an old value */
        if (myOldValue != null) {
            /* Determine new value */
            V myNewValue = pReMap.apply(pKey, myOldValue);

            /* If we have a replacement value */
            if (myNewValue != null) {
                /* Store it */
                putEntry(iHash, pKey, myNewValue);

                /* else remove old value */
            } else {
                removeEntry(iHash, pKey);
            }

            /* Return the new value */
            return myNewValue;
        }

        /* No change */
        return null;
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> pAction) {
        /* Loop through the entry set */
        for (Entry<K, V> myEntry : entrySet()) {
            /* process the entry */
            pAction.accept(myEntry.getKey(), myEntry.getValue());
        }
    }

    @Override
    public V getOrDefault(final Object pKey,
                          final V pValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        return myEntry == null
                               ? pValue
                               : myEntry.getValue();
    }

    @Override
    public V merge(final K pKey,
                   final V pValue,
                   final BiFunction<? super V, ? super V, ? extends V> pReMap) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        V myOldValue = myEntry == null
                                       ? null
                                       : myEntry.getValue();

        /* Calculate new value */
        V myNewValue = myOldValue != null
                                          ? pReMap.apply(myOldValue, pValue)
                                          : pValue;

        /* If we have a replacement value */
        if (myNewValue != null) {
            /* Store it */
            putEntry(iHash, pKey, myNewValue);

            /* else remove old value */
        } else {
            removeEntry(iHash, pKey);
        }

        /* Return the new value */
        return myNewValue;
    }

    @Override
    public V putIfAbsent(final K pKey,
                         final V pValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* Determine old value */
        V myOldValue = myEntry == null
                                       ? null
                                       : myEntry.getValue();

        /* If we have no existing value */
        if (myEntry == null) {
            /* Store new value */
            putEntry(iHash, pKey, pValue);
        }

        /* Return the new value */
        return myOldValue;
    }

    @Override
    public boolean remove(final Object pKey,
                          final Object pValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* If we have a match */
        if ((myEntry != null) && Objects.equals(pValue, myEntry.getValue())) {
            removeEntry(iHash, pKey);
            return true;
        }

        /* NoOp */
        return false;
    }

    @Override
    public V replace(final K pKey,
                     final V pValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* If we have an existing value */
        if (myEntry != null) {
            /* Replace it with the new value */
            return putEntry(iHash, pKey, pValue);
        }

        /* NoOp */
        return null;
    }

    @Override
    public boolean replace(final K pKey,
                           final V pOldValue,
                           final V pNewValue) {
        /* Calculate the hash */
        int iHash = hashKey(pKey);

        /* Locate the value */
        HashEntry<K, V> myEntry = getEntry(iHash, pKey);

        /* If we have a match */
        if ((myEntry != null) && Objects.equals(pOldValue, myEntry.getValue())) {
            putEntry(iHash, pKey, pNewValue);
            return true;
        }

        /* NoOp */
        return false;
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> pReMap) {
        /* Loop through the entry set */
        for (Entry<K, V> myEntry : entrySet()) {
            /* reMap the entry */
            V myNewValue = pReMap.apply(myEntry.getKey(), myEntry.getValue());
            myEntry.setValue(myNewValue);
        }
    }
}
