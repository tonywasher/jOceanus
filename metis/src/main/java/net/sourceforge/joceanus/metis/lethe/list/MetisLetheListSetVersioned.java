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

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldPairedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.metis.list.MetisListResource;
import net.sourceforge.joceanus.metis.list.MetisListReverseIterator;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Versioned ListSet.
 */
public class MetisLetheListSetVersioned
        implements MetisFieldItem, OceanusEventProvider<MetisLetheListEvent> {
    /**
     * The number of bits for the itemType.
     */
    private static final int ITEMTYPEBITS = 6;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisLetheListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisLetheListSetVersioned.class);

    /*
     * Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE, MetisLetheListSetVersioned::getListSetType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisLetheListSetVersioned::getVersion);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisLetheListSetVersioned::getBaseListSet);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_PAIREDITEMS, MetisLetheListSetVersioned::getPairedItems);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_PAIREDREFERENCES, MetisLetheListSetVersioned::getPairedReferences);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisLetheListSetVersioned> theFields;

    /**
     * The listSetType.
     */
    private final MetisListSetType theType;

    /**
     * The listKey Map.
     */
    private final Map<Integer, MetisLetheListKey> theKeyMap;

    /**
     * The versionedList Map.
     */
    private final Map<MetisLetheListKey, MetisLetheListVersioned<MetisFieldVersionedItem>> theListMap;

    /**
     * The pairedItem Map.
     */
    private final Map<Long, MetisFieldPairedItem> thePairedMap;

    /**
     * The pairedItemReference Map.
     */
    private final Map<Integer, MetisFieldVersionedItem> thePairedReferenceMap;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisLetheListEvent> theEventManager;

    /**
     * The Underlying list (if any).
     */
    private final MetisLetheListSetVersioned theBaseListSet;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListSetType the type
     */
    protected MetisLetheListSetVersioned(final MetisListSetType pListSetType) {
        this(pListSetType, null);
    }

    /**
     * Constructor.
     * @param pListSetType the type
     * @param pBaseListSet the baseListSet (if any)
     */
    protected MetisLetheListSetVersioned(final MetisListSetType pListSetType,
                                         final MetisLetheListSetVersioned pBaseListSet) {
        /* Store parameters */
        theType = pListSetType;
        theBaseListSet = pBaseListSet;

        /* Create Maps and fieldSet */
        theKeyMap = new LinkedHashMap<>();
        theListMap = new LinkedHashMap<>();
        thePairedMap = new HashMap<>();
        thePairedReferenceMap = new HashMap<>();
        theFields = MetisFieldSet.newFieldSet(this);

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public OceanusEventRegistrar<MetisLetheListEvent> getEventRegistrar() {
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
     * Obtain the listSetType.
     * @return the listSet type
     */
    public MetisListSetType getListSetType() {
        return theType;
    }

    /**
     * Obtain the baseListSet.
     * @return the baseListSet
     */
    public MetisLetheListSetVersioned getBaseListSet() {
        return theBaseListSet;
    }

    /**
     * Obtain the pairedItems.
     * @return the pairedItems
     */
    private Map<Long, MetisFieldPairedItem> getPairedItems() {
        return thePairedMap;
    }

    /**
     * Obtain the pairedReferences.
     * @return the pairedReferences
     */
    private Map<Integer, MetisFieldVersionedItem> getPairedReferences() {
        return thePairedReferenceMap;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the key iterator.
     * @return the iterator
     */
    public Iterator<MetisLetheListKey> keyIterator() {
        return theListMap.keySet().iterator();
    }

    /**
     * Obtain the reverse key iterator.
     * @return the iterator
     */
    public Iterator<MetisLetheListKey> reverseKeyIterator() {
        final List<MetisLetheListKey> myList = new ArrayList<>(theListMap.keySet());
        return new MetisListReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     * @return the iterator
     */
    public Iterator<MetisLetheListVersioned<MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param <T> the item type
     * @param pListKey the list key
     * @return the list (or null)
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldVersionedItem> MetisLetheListVersioned<T> getList(final MetisLetheListKey pListKey) {
        return (MetisLetheListVersioned<T>) theListMap.get(pListKey);
    }

    /**
     * Obtain the relevant list.
     * @param <T> the item type
     * @param pListKey the list key
     * @return the list (or null)
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldTableItem> MetisListIndexed<T> getIndexedList(final MetisLetheListKey pListKey) {
        return (MetisListIndexed<T>) theListMap.get(pListKey);
    }

    /**
     * Declare list.
     * @param <T> the item type
     * @param pItemType the itemType for the list
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldVersionedItem> MetisLetheListVersioned<T> declareList(final MetisLetheListKey pItemType) {
        /* Check uniqueness and validity */
        final int myItemType = pItemType.getItemId();
        if (theKeyMap.containsKey(myItemType)) {
            throw new IllegalArgumentException("Duplicate itemType " + pItemType);
        }
        if (myItemType < 0
            || myItemType > (1 << ITEMTYPEBITS)) {
            throw new IllegalArgumentException("Invalid itemType " + pItemType);
        }

        /* Create the list and declare it */
        final MetisLetheListVersioned<MetisFieldVersionedItem> myList = new MetisLetheListVersioned<>(this, pItemType);
        declareList(pItemType, myList);
        return (MetisLetheListVersioned<T>) myList;
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     * @param pList the list
     */
    protected void declareList(final MetisLetheListKey pItemType,
                               final MetisLetheListVersioned<MetisFieldVersionedItem> pList) {
        /* Add to the maps */
        theKeyMap.put(pItemType.getItemId(), pItemType);
        theListMap.put(pItemType, pList);

        /* Create the DataField */
        theFields.declareLocalField(pItemType.getListName(), k -> pList);
    }

    /**
     * Is this an empty listSet?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisLetheListVersioned<MetisFieldVersionedItem> myList : theListMap.values()) {
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* All lists are empty */
        return true;
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisLetheListSetChange pEvent) {
        /* If the change is non-empty */
        if (!pEvent.getEventType().hasContent()
            || !pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }

    /**
     * Obtain pairedItem for ItemId.
     * @param pId the itemId
     * @return the item
     */
    protected Object getPairedItemForId(final Long pId) {
        /* Split Id into the two parts */
        final int myChildId = (int) (pId >>> Integer.SIZE);
        final int myItemId = pId.intValue();

        /* If this is a singleton item */
        if (myChildId == 0) {
            return getItemForId(myItemId);
        }

        /* Return an existing item */
        MetisFieldPairedItem myPair = thePairedMap.get(pId);
        if (myPair != null) {
            return myPair;
        }

        /* Protect against exceptions */
        try {
            /* Obtain the Parent and child */
            final MetisFieldVersionedItem myParent = getItemForId(myItemId);
            final MetisFieldVersionedItem myChild = getItemForId(myChildId);

            /* Create and record the new Pair */
            myPair = new MetisFieldPairedItem(myParent, myChild, pId);
            thePairedMap.put(pId, myPair);
            thePairedReferenceMap.put(myItemId, myParent);
            thePairedReferenceMap.put(myChildId, myChild);
            return myPair;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Id: " + Long.toHexString(pId), e);
        }
    }

    /**
     * Obtain id for paired Item.
     * @param pItem the item
     * @return the id
     */
    protected Long getIdForPairedItem(final Object pItem) {
        /* If this is a paired item */
        if (pItem instanceof MetisFieldPairedItem) {
            return ((MetisFieldPairedItem) pItem).getExternalId();
        }

        /* This must be a VersionedItem */
        if (!(pItem instanceof MetisFieldVersionedItem)) {
            throw new IllegalArgumentException("Unable to generate Id");
        }

        /* Return the singleton id */
        final Integer myId = getIdForItem((MetisFieldVersionedItem) pItem);
        return Integer.toUnsignedLong(myId);
    }

    /**
     * Obtain itemTypeId from ItemId.
     * @param pId the itemId
     * @return the itemTypeId
     */
    static Integer getItemTypeFromId(final Integer pId) {
        return pId >>> (Integer.SIZE - ITEMTYPEBITS);
    }

    /**
     * Obtain indexedId from ItemId.
     * @param pId the itemId
     * @return the indexedId
     */
    static Integer getIndexedIdFromId(final Integer pId) {
        return pId & (-1 >>> ITEMTYPEBITS);
    }

    /**
     * Obtain item for ItemId.
     * @param pId the itemId
     * @return the item
     */
    protected MetisFieldVersionedItem getItemForId(final Integer pId) {
        /* Split Id into the two parts */
        final int myItemType = getItemTypeFromId(pId);
        final int myIndexedId = getIndexedIdFromId(pId);

        /* Determine the list */
        final MetisLetheListKey myKey = theKeyMap.get(myItemType);
        final MetisLetheListVersioned<MetisFieldVersionedItem> myList = theListMap.get(myKey);

        /* Access the item */
        final MetisFieldVersionedItem myItem = myList == null
                                                              ? null
                                                              : myList.getItemById(myIndexedId);
        if (myItem == null) {
            throw new IllegalArgumentException("Invalid Id: " + Integer.toHexString(pId));
        }

        /* return the item */
        return myItem;
    }

    /**
     * Obtain id for item.
     * @param pItem the item
     * @return the id
     */
    protected Integer getIdForItem(final MetisFieldVersionedItem pItem) {
        /* Access the two parts of the id */
        final MetisLetheListKey myKey = (MetisLetheListKey) pItem.getItemType();
        final int myItemType = myKey.getItemId();
        final int myItemId = pItem.getIndexedId();

        /* Check that the IDs are valid */
        if (!theKeyMap.containsKey(myItemType)
            || myItemId != (myItemId & (-1 >>> ITEMTYPEBITS))) {
            throw new IllegalArgumentException("Unable to generate Id");
        }

        /* Build the id */
        return buildItemId(myItemType, myItemId);
    }

    /**
     * Obtain itemId from constituent parts.
     * @param pItemType the itemType
     * @param pIndexedId the indexedId
     * @return the itemId
     */
    static Integer buildItemId(final Integer pItemType,
                               final Integer pIndexedId) {
        return pIndexedId | pItemType << (Integer.SIZE - ITEMTYPEBITS);
    }

    /**
     * Obtain itemId from item.
     * @param pItem the item
     * @return the itemId
     */
    static Integer buildItemId(final MetisFieldVersionedItem pItem) {
        final MetisLetheListKey myKey = (MetisLetheListKey) pItem.getItemType();
        return buildItemId(myKey.getItemId(), pItem.getIndexedId());
    }

    /**
     * Cleanup for deleted paired item.
     * @param pItem the item that is to be deleted
     */
    protected void cleanupDeletedItem(final MetisFieldVersionedItem pItem) {
        /* Access the id of the item */
        final Integer myId = getIdForItem(pItem);

        /* If the item is referenced by pairedItems */
        if (thePairedReferenceMap.containsKey(myId)) {
            /* Remove the reference */
            thePairedReferenceMap.remove(myId);

            /* Iterate through the map entries */
            final Iterator<Entry<Long, MetisFieldPairedItem>> myIterator = thePairedMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                final Entry<Long, MetisFieldPairedItem> myEntry = myIterator.next();

                /* If the entry references the Id */
                if (myEntry.getValue().referencesId(myId)) {
                    /* Delete the entry */
                    myIterator.remove();
                }
            }
        }
    }

    /**
     * Reset pairedItems.
     */
    protected void resetPairedItems() {
        thePairedReferenceMap.clear();
        thePairedMap.clear();
    }

    /**
     * Clone pairedItems.
     * @param pSource the source items
     */
    protected void clonePairedItems(final MetisLetheListSetVersioned pSource) {
        /* Reset the existing detail */
        resetPairedItems();

        /* Copy all details from source */
        thePairedMap.putAll(pSource.thePairedMap);
        thePairedReferenceMap.putAll(pSource.thePairedReferenceMap);
    }

    /**
     * reBase pairedItems.
     * @param pSource the source items
     */
    protected void rebasePairedItems(final MetisLetheListSetVersioned pSource) {
        /* Iterate through the paired map entries */
        final Iterator<Entry<Long, MetisFieldPairedItem>> myIterator = pSource.thePairedMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Entry<Long, MetisFieldPairedItem> myEntry = myIterator.next();

            /* If the entry does not currently exist */
            if (!thePairedMap.containsKey(myEntry.getKey())) {
                /* Add the entry */
                thePairedMap.put(myEntry.getKey(), myEntry.getValue());
            }
        }

        /* Iterate through the paired reference map entries */
        final Iterator<Entry<Integer, MetisFieldVersionedItem>> myRefIterator = pSource.thePairedReferenceMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Entry<Integer, MetisFieldVersionedItem> myEntry = myRefIterator.next();

            /* If the entry does not currently exist */
            if (!thePairedReferenceMap.containsKey(myEntry.getKey())) {
                /* Add the entry */
                thePairedReferenceMap.put(myEntry.getKey(), myEntry.getValue());
            }
        }
    }
}
