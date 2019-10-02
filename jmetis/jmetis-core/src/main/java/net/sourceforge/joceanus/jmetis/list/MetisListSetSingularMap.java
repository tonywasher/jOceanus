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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataSingularItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataSingularValue;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Singular Map.
 */
public class MetisListSetSingularMap {
    /**
     * The underlying listSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * Is this a session uniqueMap?
     */
    private final boolean isSession;

    /**
     * The map of uniqueMaps for this list.
     */
    private final Map<MetisListKey, MetisListSingularMap> theListMap;

    /**
     * Constructor.
     * @param pListSet the owning listSet
     * @param pSession is this a nameMap for a session?
     */
    public MetisListSetSingularMap(final MetisListSetVersioned pListSet,
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
     * Obtain the item for a singular value.
     * @param pValue the value of the item
     * @param pFieldId the fieldId
     * @param pKey the type of the item
     * @return the item (if found)
     */
    public MetisFieldVersionedItem getItemForValue(final Object pValue,
                                                   final MetisDataFieldId pFieldId,
                                                   final MetisListKey pKey) {
        /* Obtain the singularMap for this list */
        final MetisListSingularMap mySingularMap = theListMap.get(pKey);

        /* Return the item */
        return mySingularMap == null ? null : mySingularMap.getItemForValue(pValue, pFieldId);
    }

    /**
     * Is the value valid for this field (non-duplicate)?
     * @param pItem the item
     * @param pFieldId the fieldId
     * @param pKey the list key
     * @return true/false
     */
    public boolean isValidValue(final MetisFieldVersionedItem pItem,
                                final MetisDataFieldId pFieldId,
                                final MetisListKey pKey) {
        /* Obtain the singularMap for this list */
        final MetisListSingularMap mySingularMap = theListMap.get(pKey);

        /* Determine whether the value is valid */
        return mySingularMap == null
                || mySingularMap.isValidValue(pItem, pFieldId);
    }

    /**
     * Is the value available for this itemField?
     * @param pItem the item
     * @param pValue the proposed value
     * @param pFieldId the fieldId
     * @param pKey the list key
     * @return true/false
     */
    public boolean isAvailableValue(final MetisFieldVersionedItem pItem,
                                    final Object pValue,
                                    final MetisDataFieldId pFieldId,
                                    final MetisListKey pKey) {
        /* Handle trivial case of current value */
        final MetisFieldVersionedDef myField = pItem.getVersionedField(pFieldId);
        final Object myValue = myField.getFieldValue(pItem);
        if (pValue.equals(myValue)) {
            return true;
        }

        /* Obtain the singularMap for this list */
        final MetisListSingularMap mySingularMap = theListMap.get(pKey);

        /* Determine whether the value is available */
        return mySingularMap == null
                || mySingularMap.getItemForValue(pValue, pFieldId) == null;
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

            /* If the list has singular fields */
            if (!myKey.getSingularFields().isEmpty()) {
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

            /* If the list has singular fields */
            if (!myKey.getSingularFields().isEmpty()) {
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
        /* Obtain the singularMap for this item */
        final MetisListSingularMap mySingularMap = theListMap.computeIfAbsent(pKey, x -> new MetisListSingularMap(pKey, isSession));

        /* Loop through the unique values */
        for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
            /* Store singular values for item */
            mySingularMap.setFieldValueForItem(pItem, myFieldId);
        }
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
        /* Obtain the singularMap for this item */
        final MetisListSingularMap mySingularMap = theListMap.computeIfAbsent(pKey, x -> new MetisListSingularMap(pKey, false));

        /* Loop through the unique values */
        for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
            /* Change value for item */
            mySingularMap.changeFieldValueForItem(pItem, myFieldId);
        }
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
        /* Obtain the singularMap for this item */
        final MetisListSingularMap mySingularMap = theListMap.get(pKey);

        /* Clear value for item */
        if (mySingularMap != null) {
            /* Loop through the unique values */
            for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
                mySingularMap.clearFieldValueForItem(pItem, myFieldId);
            }
        }
    }

    /**
     * ListSingularMap for List.
     */
    static class MetisListSingularMap {
        /**
         * The list key.
         */
        private final MetisListKey theListKey;

        /**
         * Is this a session singularMap?
         */
        private final boolean isSession;

        /**
         * The map of singularMaps for this list.
         */
        private final Map<MetisDataFieldId, MetisFieldSingularMap> theFieldMap;

        /**
         * Constructor.
         * @param pKey the listKey
         * @param pSession is this a singularMap for a session?
         */
        MetisListSingularMap(final MetisListKey pKey,
                             final boolean pSession) {
            /* Store parameters */
            theListKey = pKey;
            isSession = pSession;

            /* Create map */
            theFieldMap = new HashMap<>();
        }

        /**
         * Obtain the item for the value.
         * @param pValue the value
         * @param pFieldId the fieldId
         * @return the item (if found)
         */
        MetisFieldVersionedItem getItemForValue(final Object pValue,
                                                final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldSingularMap myFieldMap = theFieldMap.get(pFieldId);

            /* LookUp Item */
            return myFieldMap == null ? null : myFieldMap.getItemForValue(pValue);
        }

        /**
         * Is the value valid (non-duplicate)?
         * @param pItem the item
         * @param pFieldId the fieldId
         * @return true/false
         */
        boolean isValidValue(final MetisFieldVersionedItem pItem,
                             final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldSingularMap myFieldMap = theFieldMap.get(pFieldId);

            /* Check fieldValue */
            return myFieldMap == null || myFieldMap.isValidValue(pItem);
        }

        /**
         * Store the item under its field value.
         *
         * @param pItem    the item
         * @param pFieldId the fieldId
         */
        void setFieldValueForItem(final MetisFieldVersionedItem pItem,
                                  final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldSingularMap myFieldMap = theFieldMap.computeIfAbsent(pFieldId, this::createSingularMap);

            /* Set fieldValue */
            myFieldMap.setValueForItem(pItem);
        }

        /**
         * Change value for item.
         *
         * @param pItem the item
         * @param pFieldId the fieldId
         */
        void changeFieldValueForItem(final MetisFieldVersionedItem pItem,
                                     final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldSingularMap myFieldMap = theFieldMap.computeIfAbsent(pFieldId, this::createSingularMap);

            /* change fieldValue */
            myFieldMap.changeValueForItem(pItem);
        }

        /**
         * Clear value for item.
         * @param pItem the item
         * @param pFieldId the fieldId
         */
        void clearFieldValueForItem(final MetisFieldVersionedItem pItem,
                                    final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldSingularMap myFieldMap = theFieldMap.get(pFieldId);

            /* clear fieldValue */
            if (myFieldMap != null) {
                myFieldMap.clearValueForItem(pItem);
            }
        }

        /**
         * Create the singular Map for field.
         * @param pFieldId the fieldId
         * @return the uniqueMap
         */
        private MetisFieldSingularMap createSingularMap(final MetisDataFieldId pFieldId) {
            final MetisFieldSetDef myFieldSet = MetisFieldSet.lookUpFieldSet(theListKey.getClazz());
            final MetisFieldDef myField = myFieldSet.getField(pFieldId);
            return new MetisFieldSingularMap(myField, isSession);
        }
    }

    /**
     * UniqueMap for List/Field.
     */
    static class MetisFieldSingularMap {
        /**
         * Standard integer ONE.
         */
        private static final Integer ONE = 1;

        /**
         * The field.
         */
        private final MetisFieldDef theField;

        /**
         * The unique map for this list.
         */
        private final Map<Object, MetisFieldVersionedItem> theValueMap;

        /**
         * The reverse map for this list.
         */
        private final Map<Integer, Object> theReverseMap;

        /**
         * The count map for this list.
         */
        private final Map<Object, Integer> theCountMap;

        /**
         * Constructor.
         * @param pField the field
         * @param pSession is this a session map?
         */
        MetisFieldSingularMap(final MetisFieldDef pField,
                              final boolean pSession) {
            theField = pField;
            theValueMap = new HashMap<>();
            theReverseMap = new HashMap<>();
            theCountMap = pSession ? new HashMap<>() : null;
        }

        /**
         * Obtain the item for the value.
         * @param pValue the value
         * @return the item (if found)
         */
        MetisFieldVersionedItem getItemForValue(final Object pValue) {
            return theValueMap.get(pValue);
        }

        /**
         * Is the value valid (non-duplicate)?
         * @param pItem the item
         * @return true/false
         */
        boolean isValidValue(final MetisFieldVersionedItem pItem) {
            /* Sanity check */
            if (theCountMap == null) {
                throw new IllegalStateException();
            }

            /* Access value of item */
            final MetisDataSingularItem myItem = theField.getFieldValue(pItem, MetisDataSingularItem.class);
            final MetisDataSingularValue myValue = myItem.getSingularValue();

            /* Check count */
            final Integer myResult = theCountMap.get(myValue);
            return myValue.isSingular() ? ONE.equals(myResult) : myResult == null;
        }

        /**
         * Store the item under its value.
         * @param pItem the item
         */
        void setValueForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

            /* Access value of item */
            final MetisDataSingularItem myItem = theField.getFieldValue(pItem, MetisDataSingularItem.class);
            final MetisDataSingularValue myValue = myItem.getSingularValue();

            /* Only proceed if the value is singular */
            if (myValue.isSingular()) {
                /* Sanity checks */
                if (theCountMap == null && theValueMap.get(myValue) != null) {
                    throw new IllegalArgumentException();
                }
                if (theReverseMap.get(myId) != null) {
                    throw new IllegalArgumentException();
                }

                /* Store the links */
                theValueMap.put(myValue, pItem);
                theReverseMap.put(myId, myValue);

                /* Increment count if we are loading */
                if (theCountMap != null) {
                    incrementCount(myValue);
                }
            }
        }

        /**
         * Adjust count for value.
         * @param pValue the value
         */
        private void incrementCount(final Object pValue) {
            /* Adjust count */
            final Integer myCount = theCountMap.get(pValue);
            theCountMap.put(pValue, myCount == null
                                    ? ONE
                                    : myCount + 1);
        }

        /**
         * Change value for item.
         * @param pItem the item
         */
        void changeValueForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

            /* Access value of item */
            final MetisDataSingularItem myItem = theField.getFieldValue(pItem, MetisDataSingularItem.class);
            final MetisDataSingularValue myValue = myItem.getSingularValue();

            /* Lookup current value */
            final Object myCurrentValue = theReverseMap.get(myId);

            /* If the value has changed */
            if (!myCurrentValue.equals(myValue)) {
                /* Only remove link if it still points to this id */
                final MetisFieldVersionedItem myCurrent = theValueMap.get(myCurrentValue);
                final Integer myCurrentId = myCurrent == null ? null : MetisListSetVersioned.buildItemId(myCurrent);
                if (myId.equals(myCurrentId)) {
                    theValueMap.remove(myCurrentValue);
                }

                /* If the new value is Singular */
                if (myValue.isSingular()) {
                    /* Store the links */
                    theValueMap.put(myValue, pItem);
                    theReverseMap.put(myId, myValue);
                } else {
                    theReverseMap.remove(myId);
                }
            }
        }

        /**
         * Clear value for item.
         * @param pItem the item
         */
        void clearValueForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

            /* Access value of item */
            final MetisDataSingularItem myItem = theField.getFieldValue(pItem, MetisDataSingularItem.class);
            final MetisDataSingularValue myValue = myItem.getSingularValue();

            /* Look for existing value */
            final MetisFieldVersionedItem myCurrent = theValueMap.get(myValue);
            if (myCurrent != null) {
                /* Sanity check */
                final Integer myCurrentId = MetisListSetVersioned.buildItemId(pItem);
                if (!myCurrentId.equals(myId)) {
                    throw new IllegalArgumentException();
                }

                /* Clear the value */
                theValueMap.remove(myValue);
            }

            /* Remove the reverse reference */
            theReverseMap.remove(myId);
        }
    }
}

