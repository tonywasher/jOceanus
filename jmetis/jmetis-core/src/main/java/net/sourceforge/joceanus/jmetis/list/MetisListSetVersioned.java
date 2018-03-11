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
package net.sourceforge.joceanus.jmetis.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldPairedItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Versioned ListSet.
 */
public class MetisListSetVersioned
        implements MetisFieldItem, TethysEventProvider<MetisListEvent> {
    /**
     * The number of bits for the itemType.
     */
    private static final int ITEMTYPEBITS = 6;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListSetVersioned.class);

    /**
     * Version Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListSetVersioned::getVersion);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisListSetVersioned> theFields;

    /**
     * The listSetType.
     */
    private final MetisListSetType theType;

    /**
     * The listKey Map.
     */
    private final Map<Integer, MetisListKey> theKeyMap;

    /**
     * The versionedList Map.
     */
    private final Map<MetisListKey, MetisListVersioned<MetisFieldVersionedItem>> theListMap;

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
    private final TethysEventManager<MetisListEvent> theEventManager;

    /**
     * The Underlying list (if any).
     */
    private final MetisListSetVersioned theBaseListSet;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListSetType the type
     */
    protected MetisListSetVersioned(final MetisListSetType pListSetType) {
        this(pListSetType, null);
    }

    /**
     * Constructor.
     * @param pListSetType the type
     * @param pBaseListSet the baseListSet (if any)
     */
    protected MetisListSetVersioned(final MetisListSetType pListSetType,
                                    final MetisListSetVersioned pBaseListSet) {
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
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
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
    public MetisListSetVersioned getBaseListSet() {
        return theBaseListSet;
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
    public Iterator<MetisListKey> keyIterator() {
        return theListMap.keySet().iterator();
    }

    /**
     * Obtain the reverse key iterator.
     * @return the iterator
     */
    public Iterator<MetisListKey> reverseKeyIterator() {
        final List<MetisListKey> myList = new ArrayList<>(theListMap.keySet());
        return new MetisListReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     * @return true/false
     */
    public Iterator<MetisListVersioned<MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param <T> the item type
     * @param pListKey the list key
     * @return the list (or null)
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldVersionedItem> MetisListVersioned<T> getList(final MetisListKey pListKey) {
        return (MetisListVersioned<T>) theListMap.get(pListKey);
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     */
    public void declareList(final MetisListKey pItemType) {
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
        declareList(pItemType, new MetisListVersioned<>(this, pItemType));
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     * @param pList the list
     */
    protected void declareList(final MetisListKey pItemType,
                               final MetisListVersioned<MetisFieldVersionedItem> pList) {
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
        for (MetisListVersioned<MetisFieldVersionedItem> myList : theListMap.values()) {
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
    protected void fireEvent(final MetisListSetChange pEvent) {
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
     * Obtain item for ItemId.
     * @param pId the itemId
     * @return the item
     */
    protected MetisFieldVersionedItem getItemForId(final Integer pId) {
        /* Split Id into the two parts */
        final int myItemType = pId >>> (Integer.SIZE - ITEMTYPEBITS);
        final int myItemId = pId & (-1 >>> ITEMTYPEBITS);

        /* Determine the list */
        final MetisListKey myKey = theKeyMap.get(myItemType);
        final MetisListVersioned<MetisFieldVersionedItem> myList = theListMap.get(myKey);

        /* Access the item */
        final MetisFieldVersionedItem myItem = myList == null
                                                              ? null
                                                              : myList.getItemById(myItemId);
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
        final MetisListKey myKey = (MetisListKey) pItem.getItemType();
        final int myItemType = myKey.getItemId();
        final int myItemId = pItem.getIndexedId();

        /* Check that the IDs are valid */
        if (!theKeyMap.containsKey(myItemType)
            || myItemId != (myItemId & (-1 >>> ITEMTYPEBITS))) {
            throw new IllegalArgumentException("Unable to generate Id");
        }

        /* Build the id */
        return myItemId | myItemType << (Integer.SIZE - ITEMTYPEBITS);
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
            final Iterator<Map.Entry<Long, MetisFieldPairedItem>> myIterator = thePairedMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                final Map.Entry<Long, MetisFieldPairedItem> myEntry = myIterator.next();

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
    protected void clonePairedItems(final MetisListSetVersioned pSource) {
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
    protected void rebasePairedItems(final MetisListSetVersioned pSource) {
        /* Iterate through the paired map entries */
        final Iterator<Map.Entry<Long, MetisFieldPairedItem>> myIterator = pSource.thePairedMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Map.Entry<Long, MetisFieldPairedItem> myEntry = myIterator.next();

            /* If the entry does not currently exist */
            if (!thePairedMap.containsKey(myEntry.getKey())) {
                /* Add the entry */
                thePairedMap.put(myEntry.getKey(), myEntry.getValue());
            }
        }

        /* Iterate through the paired reference map entries */
        final Iterator<Map.Entry<Integer, MetisFieldVersionedItem>> myRefIterator = pSource.thePairedReferenceMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            final Map.Entry<Integer, MetisFieldVersionedItem> myEntry = myRefIterator.next();

            /* If the entry does not currently exist */
            if (!thePairedReferenceMap.containsKey(myEntry.getKey())) {
                /* Add the entry */
                thePairedReferenceMap.put(myEntry.getKey(), myEntry.getValue());
            }
        }
    }
}
