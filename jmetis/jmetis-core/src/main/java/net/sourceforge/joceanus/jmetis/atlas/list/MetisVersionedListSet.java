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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionControl;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;

/**
 * Set of VersionedLists.
 */
public class MetisVersionedListSet
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisVersionedListSet.class);

    /**
     * Version Field Id.
     */
    private static final MetisDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION.getValue());

    /**
     * The Local fields.
     */
    private final MetisDataFieldSet theFields;

    /**
     * The VersionedList Map.
     */
    private final Map<MetisListKey, MetisVersionedList<MetisDataVersionedItem>> theListMap;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     */
    protected MetisVersionedListSet() {
        theListMap = new LinkedHashMap<>();
        theFields = new MetisDataFieldSet(MetisVersionedListSet.class, FIELD_DEFS);
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return theFields;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_VERSION.equals(pField)) {
            return theVersion != 0
                                   ? theVersion
                                   : MetisDataFieldValue.SKIP;
        }

        /* Look for a key of this type */
        final String myName = pField.getName();
        for (Map.Entry<MetisListKey, MetisVersionedList<MetisDataVersionedItem>> myEntry : theListMap.entrySet()) {
            /* If this is the correct value */
            if (myName.equals(myEntry.getKey().getListName())) {
                /* Return the list */
                final MetisVersionedList<?> myList = myEntry.getValue();
                return myList == null || myList.isEmpty()
                                                          ? MetisDataFieldValue.SKIP
                                                          : myList;
            }
        }

        /* Not found */
        return MetisDataFieldValue.UNKNOWN;
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
    private Iterator<MetisVersionedList<MetisDataVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param pListKey the list key
     * @return the list (or null)
     */
    public MetisVersionedList<MetisDataVersionedItem> getList(final MetisListKey pListKey) {
        return theListMap.get(pListKey);
    }

    /**
     * is the listSet empty?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisVersionedList<MetisDataVersionedItem> myList : theListMap.values()) {
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
                               final MetisVersionedList<MetisDataVersionedItem> pList) {
        /* Add to the list map */
        theListMap.put(pKey, pList);

        /* Create the DataField */
        theFields.declareLocalField(pKey.getListName());
    }

    /**
     * Check reWind version.
     * @param pVersion the version to reWind to
     */
    protected void checkReWindVersion(final int pVersion) {
        /* Version must be less than current version and positive */
        if ((theVersion < pVersion)
            || (pVersion < 0)) {
            throw new IllegalArgumentException("Invalid Version");
        }
    }

    /**
     * ReWind the listSet to a particular version.
     * @param pVersion the version to reWind to
     */
    protected void doReWindToVersion(final int pVersion) {
        /* Loop through the lists */
        final Iterator<MetisVersionedList<MetisDataVersionedItem>> myIterator = listIterator();
        while (myIterator.hasNext()) {
            final MetisVersionedList<MetisDataVersionedItem> myList = myIterator.next();

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
    protected void reLinkItems(final Iterator<MetisDataVersionedItem> pIterator) {
        /* Iterate through the items, reLinking */
        while (pIterator.hasNext()) {
            final MetisDataVersionedItem myItem = pIterator.next();
            reLinkItem(myItem);
        }
    }

    /**
     * reLink values.
     * @param pItem the item
     */
    private void reLinkItem(final MetisDataVersionedItem pItem) {
        /* Access details */
        final MetisDataVersionControl myControl = pItem.getVersionControl();
        final MetisDataVersionValues myValues = myControl.getValueSet();
        final MetisDataFieldSet myFields = pItem.getDataFieldSet();

        /* Loop through the fields */
        final Iterator<MetisDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field and value */
            final MetisDataField myField = myIterator.next();
            Object myValue = myField.getStorage().isVersioned()
                                                                ? myValues.getValue(myField)
                                                                : null;

            /* If the value is an IndexedItem */
            if (myValue instanceof MetisIndexedItem) {
                /* Obtain the reLinked value and store the new value */
                myValue = reLinkValue((MetisIndexedItem) myValue);
                myValues.setValue(myField, myValue);
            }
        }
    }

    /**
     * reLink value.
     * @param pValue the value
     * @return the reLinked value
     */
    private MetisIndexedItem reLinkValue(final MetisIndexedItem pValue) {
        /* Determine the list for the item */
        final MetisVersionedList<MetisDataVersionedItem> myList = determineListForItem(pValue);

        /* If we found the list */
        final MetisIndexedItem myNew = myList != null
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
    private MetisVersionedList<MetisDataVersionedItem> determineListForItem(final MetisIndexedItem pItem) {
        /* Loop through the lists */
        final Iterator<MetisVersionedList<MetisDataVersionedItem>> myIterator = listIterator();
        while (myIterator.hasNext()) {
            final MetisVersionedList<MetisDataVersionedItem> myList = myIterator.next();

            /* If this is the correct class */
            if (myList.getTheClazz().isInstance(pItem)) {
                return myList;
            }
        }

        /* No Match */
        return null;
    }
}
