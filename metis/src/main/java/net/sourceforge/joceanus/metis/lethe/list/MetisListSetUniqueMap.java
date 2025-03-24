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

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;

/**
 * Unique Map.
 */
public class MetisListSetUniqueMap {
    /**
     * Standard integer ONE.
     */
    static final Integer ONE = 1;

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
    private final Map<MetisListKey, MetisListUniqueMap> theListMap;

    /**
     * Constructor.
     * @param pListSet the owning listSet
     * @param pSession is this a uniqueMap for a session?
     */
    public MetisListSetUniqueMap(final MetisListSetVersioned pListSet,
                                 final boolean pSession) {
        /* Store parameters */
        theListSet = pListSet;
        isSession = pSession;

        /* Create map */
        theListMap = new HashMap<>();

        /* Attach listeners */
        final OceanusEventRegistrar<MetisListEvent> myRegistrar = theListSet.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> processRefreshEvent());
        if (pSession) {
            myRegistrar.addEventListener(MetisListEvent.VERSION, this::processVersionEvent);
        }
    }

    /**
     * Obtain the item for a field value.
     * @param pValue the value of the item
     * @param pFieldId the fieldId
     * @param pKey the type of the item
     * @return the item (if found)
     */
    public MetisFieldVersionedItem getItemForValue(final Object pValue,
                                                   final MetisDataFieldId pFieldId,
                                                   final MetisListKey pKey) {
        /* Obtain the uniqueMap for this list */
        final MetisListUniqueMap myUniqueMap = theListMap.get(pKey);

        /* Return the item */
        return myUniqueMap == null ? null : myUniqueMap.getItemForValue(pValue, pFieldId);
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
        /* Obtain the uniqueMap for this list */
        final MetisListUniqueMap myUniqueMap = theListMap.get(pKey);

        /* Determine whether the value is valid */
        return myUniqueMap == null
                || myUniqueMap.isValidValue(pItem, pFieldId);
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

        /* Obtain the uniqueMap for this list */
        final MetisListUniqueMap myUniqueMap = theListMap.get(pKey);

        /* Determine whether the value is available */
        return myUniqueMap == null
                || myUniqueMap.getItemForValue(pValue, pFieldId) == null;
    }

    /**
     * Obtain a new unique Value for the list and field.
     * @param pKey the list key
     * @param pFieldId the fieldId
     * @return a new unused name
     */
    public Object getUniqueValue(final MetisListKey pKey,
                                 final MetisDataFieldId pFieldId) {
        /* Obtain the uniqueMap for this list */
        final MetisListUniqueMap myUniqueMap = theListMap.get(pKey);

        /* Build the base name */
        final Object myBase = getBaseValue(pKey, pFieldId);

        /* Determine whether the name is available */
        return myUniqueMap == null
               ? myBase
               : myUniqueMap.getUniqueValue(myBase, pFieldId);
    }

    /**
     * Obtain the base value for field.
     * @param pKey the list key
     * @param pFieldId the fieldId
     * @return the uniqueMap
     */
    static Object getBaseValue(final MetisListKey pKey,
                               final MetisDataFieldId pFieldId) {
        final MetisFieldSetDef myFieldSet = MetisFieldSet.lookUpFieldSet(pKey.getClazz());
        final MetisFieldDef myField = myFieldSet.getField(pFieldId);
        switch (myField.getDataType()) {
            case STRING:
                return "New" + pFieldId.getId();
            case INTEGER:
                return ONE;
            default:
                throw new IllegalArgumentException();
        }
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

            /* If the list has unique fields */
            if (!myKey.getUniqueFields().isEmpty()) {
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
    private void processVersionEvent(final OceanusEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        final MetisListSetChange myChanges = pEvent.getDetails(MetisListSetChange.class);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list has unique fields */
            if (!myKey.getUniqueFields().isEmpty()) {
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
        /* Obtain the uniqueMap for this item */
        final MetisListUniqueMap myUniqueMap = theListMap.computeIfAbsent(pKey, x -> new MetisListUniqueMap(pKey, isSession));

        /* Loop through the unique values */
        for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
            /* Store unique values for item */
            myUniqueMap.setFieldValueForItem(pItem, myFieldId);
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
        /* Obtain the uniqueMap for this item */
        final MetisListUniqueMap myUniqueMap = theListMap.computeIfAbsent(pKey, x -> new MetisListUniqueMap(pKey, false));

        /* Loop through the unique values */
        for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
            /* Change value for item */
            myUniqueMap.changeFieldValueForItem(pItem, myFieldId);
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
        /* Obtain the uniqueMap for this item */
        final MetisListUniqueMap myUniqueMap = theListMap.get(pKey);

        /* Clear value for item */
        if (myUniqueMap != null) {
            /* Loop through the unique values */
            for (MetisDataFieldId myFieldId : pKey.getUniqueFields()) {
                myUniqueMap.clearFieldValueForItem(pItem, myFieldId);
            }
        }
    }

    /**
     * ListUniqueMap for List.
     */
    static class MetisListUniqueMap {
        /**
         * The list key.
         */
        private final MetisListKey theListKey;

        /**
         * Is this a session uniqueMap?
         */
        private final boolean isSession;

        /**
         * The map of uniqueMaps for this list.
         */
        private final Map<MetisDataFieldId, MetisFieldUniqueMap<?>> theFieldMap;

        /**
         * Constructor.
         * @param pKey the listKey
         * @param pSession is this a uniqueMap for a session?
         */
        MetisListUniqueMap(final MetisListKey pKey,
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
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.get(pFieldId);

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
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.get(pFieldId);

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
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.computeIfAbsent(pFieldId, this::createUniqueMap);

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
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.computeIfAbsent(pFieldId, this::createUniqueMap);

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
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.get(pFieldId);

            /* clear fieldValue */
            if (myFieldMap != null) {
                myFieldMap.clearValueForItem(pItem);
            }
        }

        /**
         * Obtain unique value.
         * @param pBase the base value
         * @param pFieldId the fieldId
         * @return The new value
         */
        Object getUniqueValue(final Object pBase,
                              final MetisDataFieldId pFieldId) {
            /* Access the relevant fieldMap */
            final MetisFieldUniqueMap<?> myFieldMap = theFieldMap.get(pFieldId);

            /* return unique value */
            return myFieldMap == null ? pBase : myFieldMap.getUniqueValue();
        }

        /**
         * Create the relevant Unique Map for field.
         * @param pFieldId the fieldId
         * @return the uniqueMap
         */
        private MetisFieldUniqueMap<?> createUniqueMap(final MetisDataFieldId pFieldId) {
            final MetisFieldSetDef myFieldSet = MetisFieldSet.lookUpFieldSet(theListKey.getClazz());
            final MetisFieldDef myField = myFieldSet.getField(pFieldId);
            switch (myField.getDataType()) {
                case STRING:
                    return new MetisFieldUniqueStringMap(myField, isSession);
                case INTEGER:
                    return new MetisFieldUniqueIntegerMap(myField, isSession);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    /**
     * UniqueMap for List/Field.
     * @param <T> the object type
     */
    abstract static class MetisFieldUniqueMap<T> {
        /**
         * The field.
         */
        private final MetisFieldDef theField;

        /**
         * The object Class.
         */
        private final Class<T> theClazz;

        /**
         * The unique map for this list.
         */
        private final Map<T, MetisFieldVersionedItem> theValueMap;

        /**
         * The reverse map for this list.
         */
        private final Map<Integer, T> theReverseMap;

        /**
         * The count map for this list.
         */
        private final Map<T, Integer> theCountMap;

        /**
         * Constructor.
         * @param pField the field
         * @param pClazz the class of the field
         * @param pSession is this a session map?
         */
        MetisFieldUniqueMap(final MetisFieldDef pField,
                            final Class<T> pClazz,
                            final boolean pSession) {
            theField = pField;
            theClazz = pClazz;
            theValueMap = new HashMap<>();
            theReverseMap = new HashMap<>();
            theCountMap = pSession ? new HashMap<>() : null;
        }

        /**
         * Obtain the fieldId.
         * @return the fieldId.
         */
        MetisDataFieldId getFieldId() {
            return theField.getFieldId();
        }

        /**
         * Obtain the item for the value.
         * @param pValue the value
         * @return the item (if found)
         */
        MetisFieldVersionedItem getItemForValue(final Object pValue) {
            if (theClazz.isInstance(pValue)) {
                throw new IllegalArgumentException();
            }
            return theValueMap.get(theClazz.cast(pValue));
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
            final T myValue = theField.getFieldValue(pItem, theClazz);

            /* Check count */
            final Integer myResult = theCountMap.get(myValue);
            return ONE.equals(myResult);
        }

        /**
         * Store the item under its value.
         * @param pItem the item
         */
        void setValueForItem(final MetisFieldVersionedItem pItem) {
            /* Obtain the id for this item */
            final Integer myId = MetisListSetVersioned.buildItemId(pItem);

            /* Access value of item */
            final T myValue = theField.getFieldValue(pItem, theClazz);

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

        /**
         * Adjust count for value.
         * @param pValue the value
         */
        private void incrementCount(final T pValue) {
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
            final T myValue = theField.getFieldValue(pItem, theClazz);

            /* Lookup current value */
            final T myCurrentValue = theReverseMap.get(myId);
            if (myCurrentValue == null) {
                throw new IllegalArgumentException();
            }

            /* If the value has changed */
            if (!myCurrentValue.equals(myValue)) {
                /* Only remove link if it still points to this id */
                final MetisFieldVersionedItem myCurrent = theValueMap.get(myCurrentValue);
                final Integer myCurrentId = MetisListSetVersioned.buildItemId(myCurrent);
                if (myCurrentId.equals(myId)) {
                    theValueMap.remove(myCurrentValue);
                }

                /* Store the links */
                theValueMap.put(myValue, pItem);
                theReverseMap.put(myId, myValue);
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
            final T myValue = theField.getFieldValue(pItem, theClazz);

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

        /**
         * Obtain unique value.
         * @return The new value
         */
        abstract T getUniqueValue();
    }

    /**
     * UniqueMap for List/StringField.
     */
    static class MetisFieldUniqueStringMap
        extends MetisFieldUniqueMap<String> {
        /**
         * Constructor.
         * @param pField the field
         * @param pSession is this a session map?
         */
        MetisFieldUniqueStringMap(final MetisFieldDef pField,
                                  final boolean pSession) {
            super(pField, String.class, pSession);
        }

        @Override
        String getUniqueValue() {
            /* Set up base constraints */
            int iNextId = 1;

            /* Loop until we found a free value */
            final String myBase = "New" + getFieldId().getId();
            String myValue = myBase;
            for (;;) {
                /* try out the value */
                if (super.getItemForValue(myValue) == null) {
                    return myValue;
                }

                /* Build next value */
                myValue = myBase.concat(Integer.toString(iNextId++));
            }
        }
    }

    /**
     * UniqueMap for List/IntegerField.
     */
    static class MetisFieldUniqueIntegerMap
            extends MetisFieldUniqueMap<Integer> {
        /**
         * Constructor.
         * @param pField the field
         * @param pSession is this a session map?
         */
        MetisFieldUniqueIntegerMap(final MetisFieldDef pField,
                                   final boolean pSession) {
            super(pField, Integer.class, pSession);
        }

        @Override
        Integer getUniqueValue() {
            /* Loop until we found a free value */
            Integer myValue = ONE;
            for (;;) {
                /* try out the value */
                if (super.getItemForValue(myValue) == null) {
                    return myValue;
                }

                /* Build next value */
                myValue++;
            }
        }
    }
}
