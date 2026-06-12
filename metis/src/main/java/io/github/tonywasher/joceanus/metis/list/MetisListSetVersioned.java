/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.metis.list;

import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionValues;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedItem;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Versioned ListSet.
 */
public class MetisListSetVersioned
        implements MetisFieldItem, OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListSetVersioned.class);

    /*
     * Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE, MetisListSetVersioned::getStyle);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListSetVersioned::getVersion);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisListSetVersioned::getBase);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisListSetVersioned> theFields;

    /**
     * The listSetType.
     */
    private final MetisListStyle theStyle;

    /**
     * The base listSet.
     */
    private final MetisListSetVersioned theBase;

    /**
     * The versionedList Map.
     */
    private final Map<MetisListKey, MetisListVersioned<? extends MetisFieldVersionedItem>> theListMap;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisListEvent> theEventManager;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     */
    public MetisListSetVersioned() {
        this(MetisListStyle.BASE);
    }

    /**
     * Constructor.
     *
     * @param pStyle the listSet Style
     */
    public MetisListSetVersioned(final MetisListStyle pStyle) {
        this(pStyle, null);
    }

    /**
     * Constructor.
     *
     * @param pStyle the listSet style
     * @param pBase  the base listSet
     */
    public MetisListSetVersioned(final MetisListStyle pStyle,
                                 final MetisListSetVersioned pBase) {
        /* Store parameters */
        theStyle = pStyle;
        theBase = pBase;

        /* Create the new fieldSet */
        theFields = MetisFieldSet.newFieldSet(this);

        /* Create the listMap */
        theListMap = new LinkedHashMap<>();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the style.
     *
     * @return the style
     */
    public MetisListStyle getStyle() {
        return theStyle;
    }

    /**
     * Obtain the base listSet.
     *
     * @return the base listSet
     */
    public MetisListSetVersioned getBase() {
        return theBase;
    }

    /**
     * Obtain the version.
     *
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set version.
     *
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the key iterator.
     *
     * @return the iterator
     */
    public Iterator<MetisListKey> keyIterator() {
        return theListMap.keySet().iterator();
    }

    /**
     * Obtain the reverse key iterator.
     *
     * @return the iterator
     */
    public Iterator<MetisListKey> reverseKeyIterator() {
        final List<MetisListKey> myList = new ArrayList<>(theListMap.keySet());
        return new MetisListReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     *
     * @return the iterator
     */
    public Iterator<MetisListVersioned<? extends MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Declare list.
     *
     * @param pItemType the itemType for the list
     * @param pList     the list
     */
    protected void declareList(final MetisListKey pItemType,
                               final MetisListVersioned<? extends MetisFieldVersionedItem> pList) {
        /* Add to the maps */
        theListMap.put(pItemType, pList);

        /* Create the DataField */
        theFields.declareLocalField(pItemType.getListName(), k -> pList);
    }

    /**
     * Obtain the relevant list.
     *
     * @param <L>      the list type
     * @param pListKey the list key
     * @param pClazz   the list class
     * @return the list (or null)
     */
    public <L extends MetisListVersioned<?>> L getList(final MetisListKey pListKey,
                                                       final Class<L> pClazz) {
        return pClazz.cast(theListMap.get(pListKey));
    }

    /**
     * Is this an empty listSet?
     *
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* All lists are empty */
        return true;
    }

    /**
     * derive differences.
     *
     * @param pListSet the listSet to compare against
     * @return the differences
     */
    public MetisListSetVersioned deriveDifferences(final MetisListSetVersioned pListSet) {
        /* Create the listSet */
        final MetisListSetVersioned myDiffSet = new MetisListSetVersioned(MetisListStyle.DIFFER);

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            final MetisListVersioned<?> myBase = pListSet.getList(myKey, MetisListVersioned.class);
            myList.deriveDifferences(myDiffSet, myBase);
        }

        /* Update version if needed */
        if (!myDiffSet.isEmpty()) {
            myDiffSet.setVersion(1);
        }

        /* Return the listSet */
        return myDiffSet;
    }

    /**
     * derive updates.
     *
     * @return the updates
     */
    public MetisListSetVersioned deriveUpdates() {
        /* Create the listSete */
        final MetisListSetVersioned myUpdateSet = new MetisListSetVersioned(MetisListStyle.UPDATE);

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            myList.deriveUpdates(myUpdateSet);
        }

        /* Update version if needed */
        if (!myUpdateSet.isEmpty()) {
            myUpdateSet.setVersion(1);
        }

        /* Return the listSet */
        return myUpdateSet;
    }

    /**
     * Condense history.
     *
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            /* condense history for the list */
            myList.condenseHistory(pNewVersion);
        }

        /* Record the new version */
        setVersion(pNewVersion);
    }

    /**
     * Re-base the listSet against a database image. This method is used to re-synchronise between two
     * sources. Items that are in this listSet, but not in the base listSet will be viewed as inserted.
     * Items that are in the base listSet but not in this listSet will be viewed as deleted. Items
     * that are in both listSets but differ will be viewed as changed.
     *
     * @param pBase the base listSet
     */
    public void reBase(final MetisListSetVersioned pBase) {
        /* Note whether we have any changes */
        boolean bChanges = false;

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            final MetisListVersioned<?> myBase = pBase.getList(myKey, MetisListVersioned.class);
            bChanges |= myList.reBase(myBase);
        }

        /* If we have changes, set Version to 1 */
        if (bChanges) {
            setVersion(1);

            /* else reset all list versions to zero */
        } else {
            for (MetisListVersioned<?> myList : theListMap.values()) {
                myList.setVersion(0);
            }
        }
    }

    /**
     * deriveChanges on version increment.
     */
    public void deriveChangesOnIncrement() {
        /* Create the listSetChange */
        final MetisListSetChange myEvent = new MetisListSetChange(theVersion);

        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            final MetisListChange<?> myChange = myList.deriveChangesOnIncrement();
            if (myChange != null) {
                myEvent.registerChangedList(myChange);
            }
        }

        /* report the change */
        fireEvent(myEvent);
    }

    /**
     * Rewind items to the required version.
     *
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            myList.rewindToVersion(pVersion);
        }

        /* Adjust version */
        setVersion(pVersion);

        /* report the change */
        fireEvent(new MetisListSetChange(MetisListEvent.REWIND));
    }

    /**
     * Fire event.
     *
     * @param pEvent the event
     */
    private void fireEvent(final MetisListSetChange pEvent) {
        /* If the change is non-empty */
        if (!pEvent.getEventType().hasContent()
                || !pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }

    /**
     * Versioned List.
     *
     * @param <T> the item type
     */
    public abstract static class MetisListVersioned<T extends MetisFieldVersionedItem>
            extends MetisListIndexed<T>
            implements OceanusEventProvider<MetisListEvent> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MetisListVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListVersioned.class);

        /*
         * FieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(MetisListResource.FIELD_ITEMTYPE, MetisListVersioned::getItemType);
            FIELD_DEFS.declareLocalField(MetisListResource.FIELD_LISTSET, MetisListVersioned::getListSet);
            FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListVersioned::getVersion);
        }

        /**
         * The itemType.
         */
        private final MetisListKey theItemType;

        /**
         * The listSet.
         */
        private final MetisListSetVersioned theListSet;

        /**
         * The Event Manager.
         */
        private OceanusEventManager<MetisListEvent> theEventManager;

        /**
         * The version of the list.
         */
        private int theVersion;

        /**
         * Constructor.
         *
         * @param pListSet  the listSet
         * @param pItemType the itemType
         */
        protected MetisListVersioned(final MetisListSetVersioned pListSet,
                                     final MetisListKey pItemType) {
            theListSet = pListSet;
            theItemType = pItemType;
        }

        /**
         * Access the event manager.
         *
         * @return the event manager.
         */
        private OceanusEventManager<MetisListEvent> getEventManager() {
            /* Access the event manager and create it if it does not exist */
            synchronized (this) {
                if (theEventManager == null) {
                    theEventManager = new OceanusEventManager<>();
                }
            }
            return theEventManager;
        }

        @Override
        public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
            return getEventManager().getEventRegistrar();
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the itemType.
         *
         * @return the itemType
         */
        public MetisListKey getItemType() {
            return theItemType;
        }

        /**
         * Obtain the listSet.
         *
         * @return the listSet
         */
        public MetisListSetVersioned getListSet() {
            return theListSet;
        }

        /**
         * Obtain the listStyle.
         *
         * @return the listStyle
         */
        public MetisListStyle getStyle() {
            return theListSet.getStyle();
        }

        /**
         * Set version.
         *
         * @param pVersion the version
         */
        public void setVersion(final int pVersion) {
            theVersion = pVersion;
        }

        /**
         * Obtain the version.
         *
         * @return the version
         */
        public int getVersion() {
            return theVersion;
        }

        /**
         * Condense history.
         *
         * @param pNewVersion the new maximum version
         */
        public void condenseHistory(final int pNewVersion) {
            /* Loop through the elements */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();

                /* If the item has been changed */
                if (myCurr.getValues().getVersion() >= pNewVersion) {
                    /* If the item is deleted but was newly created */
                    if (myCurr.isDeleted()
                            && myCurr.getOriginalValues().getVersion() >= pNewVersion) {
                        /* Remove from list */
                        myIterator.remove();
                        continue;
                    }

                    /* Condense the history */
                    myCurr.condenseHistory(pNewVersion);
                }
            }

            /* Adjust list value */
            setVersion(pNewVersion);
        }

        /**
         * deriveChanges on version increment.
         *
         * @return the changes
         */
        MetisListChange<T> deriveChangesOnIncrement() {
            /* Create the change */
            final MetisListChange<T> myChange = new MetisListChange<>(theItemType, MetisListEvent.INCREMENT);
            myChange.setVersion(theVersion);

            /* Loop through the items */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myItem = myIterator.next();

                /* Ignore items that are unchanged at this version */
                if (myItem.getVersion() != theVersion) {
                    continue;
                }

                /* Register the change correctly */
                final MetisFieldVersionValues myLast = myItem.getValuesHistory().getLastValues();
                if (myLast == null) {
                    myChange.registerAdded(myItem);
                } else if (myItem.isDeleted()) {
                    myChange.registerDeleted(myItem);
                } else if (myLast.isDeletion()) {
                    myChange.registerAdded(myItem);
                } else {
                    myChange.registerChanged(myItem);
                }
            }

            /* Return the change if non-empty */
            return myChange.isEmpty() ? null : myChange;
        }

        /**
         * Rewind items to the required version.
         *
         * @param pVersion the version to rewind to
         */
        void rewindToVersion(final int pVersion) {
            /* Loop through the elements */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();

                /* If the item has been changed */
                if (myCurr.getValues().getVersion() > pVersion) {
                    /* If the item was created after the required version */
                    if (myCurr.getOriginalValues().getVersion() > pVersion) {
                        /* Remove from list */
                        myIterator.remove();
                        continue;
                    }

                    /* Adjust values */
                    myCurr.rewindToVersion(pVersion);
                }
            }

            /* Adjust version */
            setVersion(pVersion);
        }

        /**
         * Re-base the list against a database image. This method is used to re-synchronise between two
         * sources. Items that are in this list, but not in the base list will be viewed as inserted.
         * Items that are in the base list but not in this list will be viewed as deleted. Items
         * that are in both lists but differ will be viewed as changed.
         *
         * @param pBase The base list to re-base on
         * @return are there any changes
         */
        boolean reBase(final MetisListVersioned<? extends MetisFieldVersionedItem> pBase) {
            /* Access an Id Map of the old list */
            final Map<Integer, ? extends MetisFieldVersionedItem> myBase = pBase.copyIdMap();
            boolean bChanges = false;

            /* Loop through this list */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Locate the item in the base list */
                final T myCurr = myIterator.next();
                final MetisFieldVersionedItem myItem = myBase.get(myCurr.getIndexedId());

                /* If the underlying item does not exist */
                if (myItem == null) {
                    /* Mark this as a new item */
                    myCurr.getValues().setVersion(getVersion() + 1);
                    bChanges = true;

                    /* else the item exists in the old list */
                } else {
                    /* if it has changed */
                    if (!myCurr.equals(myItem)) {
                        /* Set correct history */
                        myCurr.setHistory(myItem.getValues());
                        bChanges = true;

                        /* else it is identical */
                    } else {
                        /* Mark this as a clean item */
                        myCurr.clearHistory();
                    }

                    /* Remove the item from the base map */
                    myBase.remove(myItem.getIndexedId());
                }
            }

            /* Loop through the remaining items in the base list */
            for (MetisFieldVersionedItem myCurr : myBase.values()) {
                /* Create a new deleted item and add to the list */
                final T myItem = newItem(myCurr);
                myItem.pushHistory();
                myItem.getValues().setDeletion(true);
                bChanges = true;
            }

            /* set Version to 1 */
            setVersion(1);

            /* Return flag */
            return bChanges;
        }

        /**
         * Derive updates.
         *
         * @param pListSet the update listSet
         */
        protected void deriveUpdates(final MetisListSetVersioned pListSet) {
            /* Obtain an empty list of the correct style */
            final MetisListVersioned<T> myList = newList(pListSet);

            /* Loop through the list */
            final Iterator<? extends MetisFieldVersionedItem> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the item and its state */
                final MetisFieldVersionedItem myCurr = myIterator.next();

                /* If the item has been changed */
                if (myCurr.getValues().getVersion() > 0) {
                    /* Create a copy of the item */
                    final MetisFieldVersionedItem myItem = myList.newItem(myCurr);

                    /* If the item is NEW or DELNEW */
                    if (myCurr.getOriginalValues().getVersion() > 0) {
                        myItem.getValues().setVersion(1);

                        /* else if item is DELETED */
                    } else if (myCurr.isDeleted()) {
                        myItem.getValuesHistory().pushHistory(1);

                        /* else item is CHANGED */
                    } else {
                        myItem.getValuesHistory().setHistory(myCurr.getOriginalValues());
                    }
                }
            }

            /* Add list to listSet if non-empty */
            if (!myList.isEmpty()) {
                myList.setVersion(1);
                pListSet.declareList(theItemType, myList);
            }
        }

        /**
         * Construct a difference extract between two Lists. The difference extract will only have
         * items that differ between the two lists. Items that are in the new list, but not in the old
         * list will be viewed as inserted. Items that are in the old list but not in the new list will
         * be viewed as deleted. Items that are in both lists but differ will be viewed as changed
         *
         * @param pListSet the difference listSet
         * @param pOld     The old list to compare to
         */
        public void deriveDifferences(final MetisListSetVersioned pListSet,
                                      final MetisListVersioned<? extends MetisFieldVersionedItem> pOld) {
            /* Obtain an empty list of the correct style */
            final MetisListVersioned<T> myList = newList(pListSet);

            /* Access an Id Map of the old list */
            final Map<Integer, ? extends MetisFieldVersionedItem> myOld = pOld.copyIdMap();

            /* Loop through the new list */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Locate the item in the old list */
                final T myCurr = myIterator.next();
                MetisFieldVersionedItem myItem = myOld.get(myCurr.getIndexedId());

                /* If the item does not exist in the old list */
                if (myItem == null) {
                    /* Insert a new item */
                    myItem = myList.newItem(myCurr);
                    myItem.getValues().setVersion(1);

                    /* else the item exists in the old list */
                } else {
                    /* If the item has changed */
                    if (!myCurr.equals(myItem)) {
                        /* Copy the item */
                        final MetisFieldVersionedItem myNew = myList.newItem(myCurr);

                        /* Ensure that we record the correct history */
                        myNew.setHistory(myItem.getValues());
                    }

                    /* Remove the item from the map */
                    myOld.remove(myItem.getIndexedId());
                }
            }

            /* Loop through the remaining items in the old list */
            for (MetisFieldVersionedItem myCurr : myOld.values()) {
                /* Insert a new item */
                final MetisFieldVersionedItem myItem = myList.newItem(myCurr);
                myItem.setDeleted(true);
            }

            /* Add list to listSet if non-empty */
            if (!myList.isEmpty()) {
                myList.setVersion(1);
                pListSet.declareList(theItemType, myList);
            }
        }

        /**
         * NewItem creator.
         *
         * @param pItem the item to base new item on
         * @return the new item
         */
        public abstract T newItem(MetisFieldVersionedItem pItem);

        /**
         * NewList creator.
         *
         * @param pListSet the list set
         * @return the new list
         */
        public abstract MetisListVersioned<T> newList(MetisListSetVersioned pListSet);

        /**
         * Fire event.
         *
         * @param pEvent the event
         */
        public void fireEvent(final MetisListChange<T> pEvent) {
            /* If the change is non-empty */
            if (MetisListEvent.REFRESH.equals(pEvent.getEventType())
                    || !pEvent.isEmpty()) {
                getEventManager().fireEvent(pEvent.getEventType(), pEvent);
            }
        }
    }
}
