/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.metis.lethe.list;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;

/**
 * Name Map.
 */
public class MetisListSetNameMap {
    /**
     * List of illegal characters.
     */
    private static final Pattern INVALID_CHARS_PATTERN =
            Pattern.compile("^.*[~#@*+%{}<>\\[\\]|\"_:].*$");

    /**
     * The underlying listSet.
     */
    private final MetisLetheListSetVersioned theListSet;

    /**
     * Is this a session nameMap?
     */
    private final boolean isSession;

    /**
     * The map of nameMaps for this list.
     */
    private final Map<MetisLetheListKey, MetisListNameMap> theListMap;

    /**
     * Constructor.
     * @param pListSet the owning listSet
     * @param pSession is this a nameMap for a session?
     */
    public MetisListSetNameMap(final MetisLetheListSetVersioned pListSet,
                               final boolean pSession) {
        /* Store parameters */
        theListSet = pListSet;
        isSession = pSession;

        /* Create map */
        theListMap = new HashMap<>();

        /* Attach listeners */
        final OceanusEventRegistrar<MetisLetheListEvent> myRegistrar = theListSet.getEventRegistrar();
        myRegistrar.addEventListener(MetisLetheListEvent.REFRESH, e -> processRefreshEvent());
        if (pSession) {
            myRegistrar.addEventListener(MetisLetheListEvent.VERSION, this::processVersionEvent);
        }
    }

    /**
     * Obtain the item for a name.
     * @param pName the name of the item
     * @param pKey the type of the item
     * @return the item (if found)
     */
    public MetisFieldVersionedItem getItemForName(final String pName,
                                                  final MetisLetheListKey pKey) {
        /* Obtain the nameSpace for this list */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.get(myNameSpace);

        /* Return the item */
        return myNameMap == null ? null : myNameMap.getItemForName(pName);
    }

    /**
     * Is the name valid (non-duplicate)?
     * @param pItem the item
     * @param pKey the list key
     * @return true/false
     */
    public boolean isValidName(final MetisFieldVersionedItem pItem,
                               final MetisLetheListKey pKey) {
        /* Obtain the nameSpace for this list */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.get(myNameSpace);

        /* Determine whether the name is valid */
        return myNameMap == null
                || myNameMap.isValidName(pItem);
    }

    /**
     * Is the name available for this item?
     * @param pItem the item
     * @param pName the proposed name
     * @param pKey the list key
     * @return true/false
     */
    public boolean isAvailableName(final MetisFieldVersionedItem pItem,
                                   final String pName,
                                   final MetisLetheListKey pKey) {
        /* Handle trivial case of current name */
        final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
        final String myName = myItem.getName();
        if (pName.equals(myName)) {
            return true;
        }

        /* Obtain the nameSpace for this item */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.get(myNameSpace);

        /* Determine whether the name is available */
        return myNameMap == null
                || myNameMap.getItemForName(pName) == null;
    }

    /**
     * Is the name valid (contents)?
     * @param pName the name
     * @return true/false
     */
    public static boolean isValidName(final String pName) {
        /* Check for null */
        if (pName == null) {
            return false;
        }

        /* Check for invalid characters */
        final Matcher matcher = INVALID_CHARS_PATTERN.matcher(pName);
        return pName.length() > 0
                && !matcher.find();
    }

    /**
     * Obtain a new unique name for the list.
     * @param pKey the list key
     * @return a new unused name
     */
    public String getUniqueName(final MetisLetheListKey pKey) {
        /* Obtain the nameSpace for this item */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.get(myNameSpace);

        /* Build the base name */
        final String myBase = "New" + pKey.getItemName();

        /* Obtain the new unique name */
        return myNameMap == null
                ? myBase
                : myNameMap.getUniqueName(myBase);
    }

    /**
     * Process a refresh event.
     */
    private void processRefreshEvent() {
        /* Reset the map */
        theListMap.clear();

        final Iterator<MetisLetheListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* If the list has names */
            if (myKey.getNameSpace() != null) {
                /* Access the list */
                final MetisLetheListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Process each item in the list */
                processNewItems(myKey, myList.iterator());
            }
        }
    }

    /**
     * Process a version event.
     * @param pEvent the event
     */
    private void processVersionEvent(final OceanusEvent<MetisLetheListEvent> pEvent) {
        /* Access the change details */
        final MetisLetheListSetChange myChanges = pEvent.getDetails(MetisLetheListSetChange.class);

        /* Loop through the lists */
        final Iterator<MetisLetheListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* If the list has names */
            if (myKey.getNameSpace() != null) {
                /* Obtain the associated change */
                final MetisLetheListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

                /* If there are changes */
                if (myChange != null) {
                    /* handle changes in the base list */
                    processVersionChanges(myKey, myChange);
                }
            }
        }
    }

    /**
     * Process changes as a result of a version change.
     * @param pKey the list key
     * @param pChange the change event
     */
    private void processVersionChanges(final MetisLetheListKey pKey,
                                       final MetisLetheListChange<MetisFieldVersionedItem> pChange) {
        /* Process deleted items */
        processDeletedItems(pKey, pChange.hiddenIterator());
        processDeletedItems(pKey, pChange.deletedIterator());

        /* Process changed items */
        processChangedItems(pKey, pChange.changedIterator());

        /* Process new items */
        processNewItems(pKey, pChange.addedIterator());
        processNewItems(pKey, pChange.restoredIterator());
    }

    /**
     * Process a list of new items.
     * @param pKey the list key
     * @param pIterator the iterator
     */
    private void processNewItems(final MetisLetheListKey pKey,
                                 final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            final MetisFieldVersionedItem myItem = pIterator.next();
            if (!myItem.isDeleted()) {
                processNewItem(pKey, myItem);
            }
        }
    }

    /**
     * Process newItem.
     * @param pKey the list key
     * @param pItem the item
     */
    public void processNewItem(final MetisLetheListKey pKey,
                               final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.computeIfAbsent(myNameSpace, x -> new MetisListNameMap(isSession));

        /* Store name for item */
        myNameMap.setNameForItem(pItem);
    }

    /**
     * Process a list of changed items.
     * @param pKey the list key
     * @param pIterator the iterator
     */
    private void processChangedItems(final MetisLetheListKey pKey,
                                     final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            processChangedItem(pKey, pIterator.next());
        }
    }

    /**
     * Process changedItem.
     * @param pKey the list key
     * @param pItem the item
     */
    private void processChangedItem(final MetisLetheListKey pKey,
                                    final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.computeIfAbsent(myNameSpace, x -> new MetisListNameMap(false));

        /* Change name for item */
        myNameMap.changeNameForItem(pItem);
    }

    /**
     * Process a list of deleted items.
     * @param pKey the list key
     * @param pIterator the iterator
     */
    private void processDeletedItems(final MetisLetheListKey pKey,
                                     final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            processDeletedItem(pKey, pIterator.next());
        }
    }

    /**
     * Process deletedItem.
     * @param pKey the list key
     * @param pItem the item
     */
    private void processDeletedItem(final MetisLetheListKey pKey,
                                    final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisLetheListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.get(myNameSpace);

        /* Clear name for item */
        if (myNameMap != null) {
            myNameMap.clearNameForItem(pItem);
        }
    }

    /**
     * NameMap for List(s).
     */
    static class MetisListNameMap {
        /**
         * Standard integer ONE.
         */
        private static final Integer ONE = 1;

        /**
         * The name map for this list/lists.
         */
        private final Map<String, MetisFieldVersionedItem> theNameMap;

        /**
         * The reverse name map for this list/lists.
         */
        private final Map<Integer, String> theReverseMap;

        /**
         * The count map for this list/lists.
         */
        private final Map<String, Integer> theCountMap;

        /**
         * Constructor.
         * @param pSession is this a session map?
         */
        MetisListNameMap(final boolean pSession) {
            theNameMap = new HashMap<>();
            theReverseMap = new HashMap<>();
            theCountMap = pSession ? new HashMap<>() : null;
        }

        /**
         * Obtain the item for the name.
         * @param pName the name
         * @return the item (if found)
         */
        MetisFieldVersionedItem getItemForName(final String pName) {
            return theNameMap.get(pName);
        }

        /**
         * Is the name valid (non-duplicate)?
         * @param pItem the item
         * @return true/false
         */
        boolean isValidName(final MetisFieldVersionedItem pItem) {
            /* Sanity check */
            if (theCountMap == null) {
                throw new IllegalStateException();
            }

            /* Access name of item */
            final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
            final String myName = myItem.getName();

            /* Check count */
            final Integer myResult = theCountMap.get(myName);
            return ONE.equals(myResult);
        }

        /**
         * Store the item under its name.
         * @param pItem the item
         */
        void setNameForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisLetheListSetVersioned.buildItemId(pItem);

            /* Access name of item */
            final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
            final String myName = myItem.getName();

            /* Sanity checks */
            if (theCountMap == null && theNameMap.get(myName) != null) {
                throw new IllegalArgumentException(myName);
            }
            if (theReverseMap.get(myId) != null) {
                throw new IllegalArgumentException(Integer.toString(myId));
            }

            /* Store the links */
            theNameMap.put(myName, pItem);
            theReverseMap.put(myId, myName);

            /* Increment count if we are loading */
            if (theCountMap != null) {
                incrementCount(myName);
            }
        }

        /**
         * Adjust count for name.
         * @param pName the name
         */
        private void incrementCount(final String pName) {
            /* Adjust count */
            final Integer myCount = theCountMap.get(pName);
            theCountMap.put(pName, myCount == null
                                     ? ONE
                                     : myCount + 1);
        }

        /**
         * Change name for item.
         * @param pItem the item
         */
        void changeNameForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisLetheListSetVersioned.buildItemId(pItem);

            /* Access name of item */
            final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
            final String myName = myItem.getName();

            /* Lookup current name */
            final String myCurrentName = theReverseMap.get(myId);
            if (myCurrentName == null) {
                throw new IllegalArgumentException(myName);
            }

            /* If the name has changed */
            if (!myCurrentName.equals(myName)) {
                /* Only remove name link if it still points to this id */
                final MetisFieldVersionedItem myCurrent = theNameMap.get(myCurrentName);
                final Integer myCurrentId = MetisLetheListSetVersioned.buildItemId(myCurrent);
                if (myCurrentId.equals(myId)) {
                    theNameMap.remove(myCurrentName);
                }

                /* Store the links */
                theNameMap.put(myName, pItem);
                theReverseMap.put(myId, myName);
            }
        }

        /**
         * Clear name for item.
         * @param pItem the item
         */
        void clearNameForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisLetheListSetVersioned.buildItemId(pItem);

            /* Access name of item */
            final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
            final String myName = myItem.getName();

            /* Look for existing name */
            final MetisFieldVersionedItem myCurrent = theNameMap.get(myName);
            if (myCurrent != null) {
                /* Sanity check */
                final Integer myCurrentId = MetisLetheListSetVersioned.buildItemId(pItem);
                if (!myCurrentId.equals(myId)) {
                    throw new IllegalArgumentException(myName);
                }

                /* Remove the name */
                theNameMap.remove(myName);
            }

            /* Remove the reverse reference */
            theReverseMap.remove(myId);
        }

        /**
         * Obtain unique name.
         * @param pBase the base name
         * @return The new name
         */
        String getUniqueName(final String pBase) {
            /* Set up base constraints */
            int iNextId = 1;

            /* Loop until we found a name */
            String myName = pBase;
            for (;;) {
                /* try out the name */
                if (getItemForName(myName) == null) {
                    return myName;
                }

                /* Build next name */
                myName = pBase.concat(Integer.toString(iNextId++));
            }
        }
    }
}
