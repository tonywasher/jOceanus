/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionHistory;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;

/**
 * Set of VersionedLists.
 * @param <E> the list type identifier
 * @param <L> the list type class
 */
public abstract class MetisVersionedListSet<E extends Enum<E>, L extends MetisVersionedList<MetisDataVersionedItem>>
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisVersionedListSet.class.getSimpleName());

    /**
     * ListType Field Id.
     */
    private static final MetisDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE.getValue());

    /**
     * Version Field Id.
     */
    private static final MetisDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION.getValue());

    /**
     * The Local fields.
     */
    private final MetisDataFieldSet theFields;

    /**
     * The list type.
     */
    private final MetisListType theListType;

    /**
     * The VersionedList Map.
     */
    private final Map<E, L> theListMap;

    /**
     * The enum class of the list.
     */
    private final Class<E> theClass;

    /**
     * Is this a readOnly list.
     */
    private boolean isReadOnly;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pType the listSetType
     * @param pClass the enum class
     * @param pBaseFields the base fields
     */
    protected MetisVersionedListSet(final MetisListType pType,
                                    final Class<E> pClass,
                                    final MetisDataFieldSet pBaseFields) {
        theListType = pType;
        theClass = pClass;
        theListMap = new EnumMap<>(theClass);
        theFields = new MetisDataFieldSet(pBaseFields.getName(), pBaseFields);
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
        if (FIELD_TYPE.equals(pField)) {
            return theListType;
        }
        if (FIELD_VERSION.equals(pField)) {
            return !isReadOnly && theVersion != 0
                                                  ? theVersion
                                                  : MetisDataFieldValue.SKIP;
        }

        /* Look for an enum of this type */
        String myName = pField.getName();
        for (E myEnum : theClass.getEnumConstants()) {
            /* If this is the correct value */
            if (myName.equals(myEnum.toString())) {
                /* Return the list */
                MetisVersionedList<?> myList = theListMap.get(myEnum);
                return myList.isEmpty()
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
     * Obtain the listType.
     * @return the listType
     */
    public MetisListType getListType() {
        return theListType;
    }

    /**
     * Obtain the class.
     * @return the listType
     */
    protected Class<E> getEnumClass() {
        return theClass;
    }

    /**
     * Is the listSet readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
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
     * Obtain the Enum iterator.
     * @return true/false
     */
    public Iterator<E> enumIterator() {
        return Arrays.asList(theClass.getEnumConstants()).iterator();
    }

    /**
     * Obtain the List iterator.
     * @return true/false
     */
    public Iterator<L> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the EntrySet iterator.
     * @return true/false
     */
    public Iterator<Map.Entry<E, L>> entrySetIterator() {
        return theListMap.entrySet().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param pListId the list Id
     * @return the list (or null)
     */
    public L getList(final E pListId) {
        /* Access the list */
        return theListMap.get(pListId);
    }

    /**
     * is the listSet empty?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (L myList : theListMap.values()) {
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
     * @param pId the id of the list
     * @param pList the list
     */
    protected void declareList(final E pId,
                               final L pList) {
        /* Mark ReadOnly if necessary */
        isReadOnly |= pList.isReadOnly();

        /* Add to the list map */
        theListMap.put(pId, pList);

        /* Create the DataField */
        theFields.declareLocalField(pId.toString());
    }

    /**
     * Check reWind version.
     * @param pVersion the version to reWind to
     */
    protected void checkReWindVersion(final int pVersion) {
        /* Not supported for readOnly listSets */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

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
        Iterator<L> myIterator = listIterator();
        while (myIterator.hasNext()) {
            L myList = myIterator.next();

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
            MetisDataVersionedItem myItem = pIterator.next();
            reLinkItem(myItem);
        }
    }

    /**
     * reLink values.
     * @param pItem the item
     */
    private void reLinkItem(final MetisDataVersionedItem pItem) {
        /* Access details */
        MetisDataVersionHistory myHistory = pItem.getVersionHistory();
        MetisDataVersionValues myValues = myHistory.getValueSet();
        MetisDataFieldSet myFields = pItem.getDataFieldSet();

        /* Loop through the fields */
        Iterator<MetisDataField> myIterator = myFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field and value */
            MetisDataField myField = myIterator.next();
            Object myValue = myField.getStorage().isVersioned()
                                                                ? myValues.getValue(myField)
                                                                : null;

            /* If the value is a VersionedItem */
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
        L myList = determineListForItem(pValue);

        /* If we found the list */
        MetisIndexedItem myNew = myList != null
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
    private L determineListForItem(final MetisIndexedItem pItem) {
        /* Loop through the lists */
        Iterator<L> myIterator = listIterator();
        while (myIterator.hasNext()) {
            L myList = myIterator.next();

            /* If this is the correct class */
            if (myList.getTheClass().isInstance(pItem)) {
                return myList;
            }
        }

        /* No Match */
        return null;
    }
}
