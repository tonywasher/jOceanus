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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataIndexedItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisListKey;

/**
 * Set of VersionedLists.
 */
public class MetisVersionedListSet
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisVersionedListSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisVersionedListSet.class);

    /**
     * Version Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisVersionedListSet::getVersion);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisVersionedListSet> theFields;

    /**
     * The VersionedList Map.
     */
    private final Map<MetisListKey, MetisVersionedList<MetisFieldVersionedItem>> theListMap;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     */
    protected MetisVersionedListSet() {
        theListMap = new LinkedHashMap<>();
        theFields = MetisFieldSet.newFieldSet(this);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
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
     * Produce a new list of the same type.
     * @return the new list
     */
    protected MetisVersionedListSet newListSet() {
        return new MetisVersionedListSet();
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
        return new MetisReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     * @return true/false
     */
    private Iterator<MetisVersionedList<MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param pListKey the list key
     * @return the list (or null)
     */
    public MetisVersionedList<MetisFieldVersionedItem> getList(final MetisListKey pListKey) {
        return theListMap.get(pListKey);
    }

    /**
     * is the listSet empty?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisVersionedList<MetisFieldVersionedItem> myList : theListMap.values()) {
            /* Check whether the list is empty */
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* listSet is empty */
        return true;
    }

    /**
     * Declare list.
     * @param pKey the key of the list
     * @param pList the list
     */
    protected void declareList(final MetisListKey pKey,
                               final MetisVersionedList<MetisFieldVersionedItem> pList) {
        /* Add to the list map */
        theListMap.put(pKey, pList);

        /* Create the DataField */
        theFields.declareLocalField(pKey.getListName(), k -> pList);
    }

    /**
     * Check reWind version.
     * @param pVersion the version to reWind to
     */
    protected void checkReWindVersion(final int pVersion) {
        /* Version must be less than current version and positive */
        if (theVersion < pVersion
            || pVersion < 0) {
            throw new IllegalArgumentException("Invalid Version");
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pVersion the version to reWind to
     */
    protected void doReWindToVersion(final int pVersion) {
        /* Loop through the lists */
        final Iterator<MetisVersionedList<MetisFieldVersionedItem>> myIterator = listIterator();
        while (myIterator.hasNext()) {
            final MetisVersionedList<MetisFieldVersionedItem> myList = myIterator.next();

            /* If the list needs reWinding */
            if (myList.getVersion() > pVersion) {
                /* ReWind it */
                myList.doReWindToVersion(pVersion);
            }
        }

        /* Set the version correctly */
        theVersion = pVersion;
    }

    /**
     * reLink Items.
     * @param pIterator the iterator
     */
    protected void reLinkItems(final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Iterate through the items, reLinking */
        while (pIterator.hasNext()) {
            final MetisFieldVersionedItem myItem = pIterator.next();
            reLinkItem(myItem);
        }
    }

    /**
     * reLink values.
     * @param pItem the item
     */
    private void reLinkItem(final MetisFieldVersionedItem pItem) {
        /* Access details */
        final MetisFieldVersionValues myValues = pItem.getValueSet();
        final MetisFieldSetDef myFields = pItem.getDataFieldSet();

        /* Loop through the fields */
        final Iterator<MetisFieldDef> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field and value */
            final MetisFieldDef myField = myIterator.next();
            Object myValue = myField.getStorage().isVersioned()
                                                                ? myValues.getValue(myField)
                                                                : null;

            /* If the value is an IndexedItem */
            if (myValue instanceof MetisDataIndexedItem) {
                /* Obtain the reLinked value and store the new value */
                myValue = reLinkValue((MetisDataIndexedItem) myValue);
                myValues.setUncheckedValue(myField, myValue);
            }
        }
    }

    /**
     * reLink value.
     * @param pValue the value
     * @return the reLinked value
     */
    private MetisDataIndexedItem reLinkValue(final MetisDataIndexedItem pValue) {
        /* Determine the list for the item */
        final MetisVersionedList<MetisFieldVersionedItem> myList = determineListForItem(pValue);

        /* If we found the list */
        final MetisDataIndexedItem myNew = myList != null
                                                      ? myList.getItemById(pValue.getIndexedId())
                                                      : null;

        /* Return the item */
        return myNew != null
                             ? myNew
                             : pValue;
    }

    /**
     * determine list for Item.
     * @param pItem the item
     * @return the corresponding list (or null)
     */
    private MetisVersionedList<MetisFieldVersionedItem> determineListForItem(final MetisDataIndexedItem pItem) {
        /* Loop through the lists */
        final Iterator<MetisVersionedList<MetisFieldVersionedItem>> myIterator = listIterator();
        while (myIterator.hasNext()) {
            final MetisVersionedList<MetisFieldVersionedItem> myList = myIterator.next();

            /* If this is the correct class */
            if (myList.getClazz().isInstance(pItem)) {
                return myList;
            }
        }

        /* No Match */
        return null;
    }
}
