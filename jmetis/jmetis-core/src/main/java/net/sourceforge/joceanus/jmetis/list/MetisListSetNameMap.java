/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.list;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

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
    private final MetisListSetVersioned theListSet;

    /**
     * Is this a session nameMap?
     */
    private final boolean isSession;

    /**
     * The map of nameMaps for this list.
     */
    private final Map<MetisListKey, MetisListNameMap> theListMap;

    /**
     * Constructor.
     * @param pListSet the owning listSet
     * @param pSession is this a nameMap for a session?
     */
    public MetisListSetNameMap(final MetisListSetVersioned pListSet,
                               final boolean pSession) {
        /* Store parameters */
        theListSet = pListSet;
        isSession = pSession;

        /* Create map */
        theListMap = new HashMap<>();

        /* Attach listeners */
        final TethysEventRegistrar<MetisListEvent> myRegistrar = theListSet.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> processRefreshEvent());
        if (pSession) {
            myRegistrar.addEventListener(MetisListEvent.VERSION, this::processVersionEvent);
        }
    }

    /**
     * Obtain the item for a name.
     * @param pName the name of the item
     * @param pKey the type of the item
     * @return the item (if found)
     */
    public MetisFieldVersionedItem getItemForName(final String pName,
                                                  final MetisListKey pKey) {
        /* Obtain the nameSpace for this list */
        final MetisListKey myNameSpace = pKey.getNameSpace();
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
                               final MetisListKey pKey) {
        /* Obtain the nameSpace for this list */
        final MetisListKey myNameSpace = pKey.getNameSpace();
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
                                   final MetisListKey pKey) {
        /* Handle trivial case of current name */
        final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
        final String myName = myItem.getName();
        if (pName.equals(myName)) {
            return true;
        }

        /* Obtain the nameSpace for this item */
        final MetisListKey myNameSpace = pKey.getNameSpace();
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
    public String getUniqueName(final MetisListKey pKey) {
        /* Obtain the nameSpace for this item */
        final MetisListKey myNameSpace = pKey.getNameSpace();
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

        final Iterator<MetisListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list has names */
            if (myKey.getNameSpace() != null) {
                /* Access the list */
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Process each item in the list */
                processNewItems(myKey, myList.iterator());
            }
        }
    }

    /**
     * Process a version event.
     * @param pEvent the event
     */
    private void processVersionEvent(final TethysEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        final MetisListSetChange myChanges = pEvent.getDetails(MetisListSetChange.class);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list has names */
            if (myKey.getNameSpace() != null) {
                /* Obtain the associated change */
                final MetisListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

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
    private void processVersionChanges(final MetisListKey pKey,
                                       final MetisListChange<MetisFieldVersionedItem> pChange) {
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
    private void processNewItems(final MetisListKey pKey,
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
    public void processNewItem(final MetisListKey pKey,
                               final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.computeIfAbsent(myNameSpace, x -> new MetisListNameMap(isSession));

        /* Store name for item */
        myNameMap.setNameForItem(pItem);
    }

    /**
     * Process a list of changed items.
     * @param pKey the list key
     * @param pIterator the iterator
     */
    private void processChangedItems(final MetisListKey pKey,
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
    private void processChangedItem(final MetisListKey pKey,
                                    final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisListKey myNameSpace = pKey.getNameSpace();
        final MetisListNameMap myNameMap = theListMap.computeIfAbsent(myNameSpace, x -> new MetisListNameMap(false));

        /* Change name for item */
        myNameMap.changeNameForItem(pItem);
    }

    /**
     * Process a list of deleted items.
     * @param pKey the list key
     * @param pIterator the iterator
     */
    private void processDeletedItems(final MetisListKey pKey,
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
    private void processDeletedItem(final MetisListKey pKey,
                                    final MetisFieldVersionedItem pItem) {
        /* Obtain the nameSpace for this item */
        final MetisListKey myNameSpace = pKey.getNameSpace();
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
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

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
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

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
                final Integer myCurrentId = MetisListSetVersioned.buildItemId(myCurrent);
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
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

            /* Access name of item */
            final MetisDataNamedItem myItem = (MetisDataNamedItem) pItem;
            final String myName = myItem.getName();

            /* Look for existing name */
            final MetisFieldVersionedItem myCurrent = theNameMap.get(myName);
            if (myCurrent != null) {
                /* Sanity check */
                final Integer myCurrentId = MetisListSetVersioned.buildItemId(pItem);
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
