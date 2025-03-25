/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Template for a Static Data item and List.
 * @author Tony Washer
 */
public abstract class PrometheusStaticDataItem
        extends PrometheusEncryptedDataItem
        implements MetisDataNamedItem {
    /**
     * Report fields.
     */
    private static final PrometheusEncryptedFieldSet<PrometheusStaticDataItem> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(PrometheusStaticDataItem.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_NAME, NAMELEN);
        FIELD_DEFS.declareEncryptedStringField(PrometheusDataResource.DATAITEM_FIELD_DESC, DESCLEN);
        FIELD_DEFS.declareBooleanField(PrometheusDataResource.STATICDATA_ENABLED);
        FIELD_DEFS.declareIntegerField(PrometheusDataResource.STATICDATA_SORT);
        FIELD_DEFS.declareEnumField(PrometheusDataResource.STATICDATA_CLASS);
    }

    /**
     * BadId error.
     */
    public static final String ERROR_BADID = PrometheusDataResource.STATICDATA_ERROR_ID.getValue();

    /**
     * BadName error.
     */
    public static final String ERROR_BADNAME = PrometheusDataResource.STATICDATA_ERROR_NAME.getValue();

    /**
     * The Enum Class for this Static Data.
     */
    private final Class<? extends PrometheusStaticDataClass> theEnumClass;

    /**
     * Copy Constructor.
     * @param pList The list to associate the Static Data with
     * @param pSource The static data to copy
     */
    protected PrometheusStaticDataItem(final PrometheusStaticList<?> pList,
                                       final PrometheusStaticDataItem pSource) {
        super(pList, pSource);
        theEnumClass = pSource.getEnumClass();
        setIndexedId(pSource.getIndexedId());
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws OceanusException on error
     */
    protected PrometheusStaticDataItem(final PrometheusStaticList<?> pList,
                                       final String pValue) throws OceanusException {
        /* Call super constructor */
        super(pList, 0);

        /* Determine the class */
        theEnumClass = pList.getEnumClass();
        parseEnumValue(pValue);

        /* Record the name */
        setValueName(pValue);
        setValueEnabled(Boolean.TRUE);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Static Data with
     * @param pClass the class of the new item
     * @throws OceanusException on error
     */
    protected PrometheusStaticDataItem(final PrometheusStaticList<?> pList,
                                       final PrometheusStaticDataClass pClass) throws OceanusException {
        /* Call super constructor */
        super(pList, 0);

        /* Determine the class */
        theEnumClass = pList.getEnumClass();

        /* Store the class */
        setValueClass(pClass);

        /* Set encryption */
        setNextDataKeySet();

        /* Access classId and order */
        setIndexedId(pClass.getClassId());
        setValueOrder(pClass.getOrder());

        /* Record the name */
        setValueName(pClass.toString());
        setValueEnabled(Boolean.TRUE);

        /* Set the DataKeySet */
        setNextDataKeySet();
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected PrometheusStaticDataItem(final PrometheusStaticList<?> pList,
                                       final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final OceanusDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Determine the class */
            theEnumClass = pList.getEnumClass();

            /* Store the Name */
            Object myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(PrometheusDataResource.DATAITEM_FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the class */
            myValue = pValues.getValue(PrometheusDataResource.STATICDATA_CLASS);
            if (myValue instanceof String) {
                parseEnumValue((String) myValue);
            } else {
                parseEnumValue(getIndexedId());
            }

            /* Store the Order */
            myValue = pValues.getValue(PrometheusDataResource.STATICDATA_SORT);
            if (myValue instanceof Integer) {
                setValueOrder((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueOrder(myFormatter.parseValue((String) myValue, Integer.class));
            }

            /* Store the Enabled flag */
            myValue = pValues.getValue(PrometheusDataResource.STATICDATA_ENABLED);
            if (myValue instanceof Boolean) {
                setValueEnabled((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueEnabled(myFormatter.parseValue((String) myValue, Boolean.class));
            } else {
                setValueEnabled(Boolean.TRUE);
            }

            /* Catch Exceptions */
        } catch (NumberFormatException
                 | OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(pField)) {
            return true;
        }
        if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (PrometheusDataResource.STATICDATA_ENABLED.equals(pField)) {
            return !getEnabled();
        }
        if (PrometheusDataResource.STATICDATA_CLASS.equals(pField)) {
            return !getName().equalsIgnoreCase(getStaticClass().name());
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public final String getName() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_NAME, String.class);
    }

    /**
     * Return the encrypted name of the Static Data.
     * @return the encrypted name
     */
    public final byte[] getNameBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Return the encrypted name field of the Static Data.
     * @return the encrypted field
     */
    private PrometheusEncryptedPair getNameField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_NAME);
    }

    /**
     * Return the description of the Static Data.
     * @return the description
     */
    public final String getDesc() {
        return getValues().getValue(PrometheusDataResource.DATAITEM_FIELD_DESC, String.class);
    }

    /**
     * Return the encrypted description of the Static Data.
     * @return the encrypted description
     */
    public final byte[] getDescBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    /**
     * Return the encrypted description field of the Static Data.
     * @return the encrypted name
     */
    private PrometheusEncryptedPair getDescField() {
        return getValues().getEncryptedPair(PrometheusDataResource.DATAITEM_FIELD_DESC);
    }

    /**
     * Return the sort order of the Static Data.
     * @return the order
     */
    public final Integer getOrder() {
        return getValues().getValue(PrometheusDataResource.STATICDATA_SORT, Integer.class);
    }

    /**
     * Return the Static class of the Static Data.
     * @return the class
     */
    public final PrometheusStaticDataClass getStaticClass() {
        return getValues().getValue(PrometheusDataResource.STATICDATA_CLASS, PrometheusStaticDataClass.class);
    }

    /**
     * Is the Static item enabled.
     * @return <code>true/false</code>
     */
    public final boolean getEnabled() {
        return getValues().getValue(PrometheusDataResource.STATICDATA_ENABLED, Boolean.class);
    }

    @Override
    public boolean isDisabled() {
        return !getEnabled();
    }

    /**
     * Obtain the Enum class of this Static Data.
     * @return the class
     */
    protected final Class<? extends PrometheusStaticDataClass> getEnumClass() {
        return theEnumClass;
    }

    /**
     * Set the Name.
     * @param pValue the name
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pValue);
    }

    /**
     * Set the Name.
     * @param pBytes the encrypted name
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set the Name field.
     * @param pField the encrypted name
     */
    private void setValueName(final PrometheusEncryptedPair pField) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_NAME, pField);
    }

    /**
     * Set the Description.
     * @param pValue the description
     * @throws OceanusException on error
     */
    protected final void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pValue);
    }

    /**
     * Set the Description.
     * @param pBytes the encrypted description
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set the Description field.
     * @param pField the encrypted description
     */
    private void setValueDesc(final PrometheusEncryptedPair pField) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAITEM_FIELD_DESC, pField);
    }

    /**
     * Set the Enabled flag.
     * @param isEnabled TRUE/FALSE
     */
    protected final void setValueEnabled(final Boolean isEnabled) {
        getValues().setUncheckedValue(PrometheusDataResource.STATICDATA_ENABLED, isEnabled);
    }

    /**
     * Set the Order.
     * @param pOrder the order
     */
    private void setValueOrder(final Integer pOrder) {
        getValues().setUncheckedValue(PrometheusDataResource.STATICDATA_SORT, pOrder);
    }

    /**
     * Set the Class.
     * @param pClass the class
     */
    private void setValueClass(final PrometheusStaticDataClass pClass) {
        getValues().setUncheckedValue(PrometheusDataResource.STATICDATA_CLASS, pClass);
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Access as StaticDataItem */
        final PrometheusStaticDataItem myThat = (PrometheusStaticDataItem) pThat;

        /* Make sure that the object is the same enumeration class */
        if (!MetisDataDifference.isEqual(getEnumClass(), myThat.getEnumClass())) {
            /* Order the classes by canonical name */
            return getEnumClass().getCanonicalName().compareTo(myThat.getEnumClass().getCanonicalName());
        }

        /* Compare on order and name */
        final int iDiff = getOrder() - myThat.getOrder();
        return iDiff != 0 ? iDiff : MetisDataDifference.compareObject(getName(), myThat.getName());
    }

    @Override
    public PrometheusStaticList<?> getList() {
        return (PrometheusStaticList<?>) super.getList();
    }

    /**
     * Parse enum type.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void parseEnumValue(final String pValue) throws OceanusException {
        final Class<? extends PrometheusStaticDataClass> myClass = getEnumClass();
        final PrometheusStaticDataClass[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (PrometheusStaticDataClass myValue : myEnums) {
            /* If this is the desired value */
            if (myValue.toString().equalsIgnoreCase(pValue)) {
                /* Store the class */
                setValueClass(myValue);

                /* Access classId and order */
                setIndexedId(myValue.getClassId());
                setValueOrder(myValue.getOrder());
                break;
            }
        }

        /* Reject if we didn't find the class */
        if (getStaticClass() == null) {
            throw new PrometheusDataException(ERROR_BADNAME + " " + myClass.getSimpleName() + ": " + pValue);
        }
    }

    /**
     * Parse enum type.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void parseEnumValue(final Integer pValue) throws OceanusException {
        final Class<? extends PrometheusStaticDataClass> myClass = getEnumClass();
        final PrometheusStaticDataClass[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (PrometheusStaticDataClass myValue : myEnums) {
            /* If this is the desired value */
            if (pValue.equals(myValue.getClassId())) {
                /* Store the class */
                setValueClass(myValue);

                /* Access classId and order */
                setIndexedId(myValue.getClassId());
                setValueOrder(myValue.getOrder());
                break;
            }
        }

        /* Reject if we didn't find the class */
        if (getStaticClass() == null) {
            throw new PrometheusDataException(ERROR_BADNAME + " " + myClass.getSimpleName() + ": " + pValue);
        }
    }

    /**
     * Set a new name.
     * @param pName the name
     * @throws OceanusException on error
     */
    public void setName(final String pName) throws OceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws OceanusException on error
     */
    public void setDescription(final String pDesc) throws OceanusException {
        /* Set the appropriate value */
        setValueDesc(pDesc);
    }

    /**
     * Set Enabled indicator.
     * @param isEnabled TRUE/FALSE
     */
    public void setEnabled(final boolean isEnabled) {
        /* Set the appropriate value */
        setValueEnabled(isEnabled);
    }

    /**
     * Set Order indicator.
     * @param iOrder the order
     */
    public void setOrder(final int iOrder) {
        /* Set the appropriate value */
        setValueOrder(iOrder);
    }

    @Override
    public boolean applyChanges(final PrometheusDataItem pData) {
        /* Can only apply changes for Static Data */
        if (!(pData instanceof PrometheusStaticDataItem)) {
            return false;
        }

        /* Access the data */
        final PrometheusStaticDataItem myData = (PrometheusStaticDataItem) pData;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myData);

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Apply basic changes.
     * @param pData the changed element
     */
    protected void applyBasicChanges(final PrometheusStaticDataItem pData) {
        /* Update the name if required */
        if (!MetisDataDifference.isEqual(getName(), pData.getName())) {
            setValueName(pData.getNameField());
        }

        /* Update the description if required */
        if (!MetisDataDifference.isEqual(getDesc(), pData.getDesc())) {
            setValueDesc(pData.getDescField());
        }

        /* Update the enabled indication if required */
        if (!MetisDataDifference.isEqual(getEnabled(), pData.getEnabled())) {
            setEnabled(pData.getEnabled());
        }

        /* Update the order indication if required */
        if (!MetisDataDifference.isEqual(getOrder(), pData.getOrder())) {
            setOrder(pData.getOrder());
        }
    }

    @Override
    public void adjustMapForItem() {
        final PrometheusStaticList<?> myList = getList();
        final PrometheusStaticDataMap<?> myMap = myList.getDataMap();
        myMap.adjustForItem(myList.getBaseClass().cast(this));
    }

    /**
     * Represents a list of StaticData objects.
     * @param <T> the item type
     */
    public abstract static class PrometheusStaticList<T extends PrometheusStaticDataItem>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(PrometheusStaticList.class);
        }

        /**
         * Construct a generic static data list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pItemType the item type
         * @param pStyle the style of the list
         */
        protected PrometheusStaticList(final Class<T> pBaseClass,
                                       final PrometheusDataSet pData,
                                       final MetisListKey pItemType,
                                       final PrometheusListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PrometheusStaticList(final PrometheusStaticList<T> pSource) {
            super(pSource);
        }

        /**
         * Obtain the enumClass.
         * @return the enumClass
         */
        protected abstract Class<? extends PrometheusStaticDataClass> getEnumClass();

        @Override
        @SuppressWarnings("unchecked")
        public PrometheusStaticDataMap<T> getDataMap() {
            return (PrometheusStaticDataMap<T>) super.getDataMap();
        }

        /**
         * Search for a particular item by class.
         * @param eClass The class of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T findItemByClass(final PrometheusStaticDataClass eClass) {
            /* Look for item by class Id */
            return findItemById(eClass.getClassId());
        }

        @Override
        public T findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        /**
         * Is the list full?
         * @return true/false
         */
        public boolean isFull() {
            /* We can only be full with the correct number of items */
            if (size() < getEnumClass().getEnumConstants().length) {
                return false;
            }

            /* Loop through all elements */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();

                /* If the item is deleted */
                if (myCurr.isDeleted()) {
                    /* Not full */
                    return false;
                }
            }

            /* Must be full */
            return true;
        }

        /**
         * Obtain a list of classes that are missing/deleted.
         * @return The List of classes
         */
        public List<PrometheusStaticDataClass> getMissingClasses() {
            /* Allocate the list */
            final List<PrometheusStaticDataClass> myList = new ArrayList<>();

            /* Loop through all elements */
            for (PrometheusStaticDataClass myClass : getEnumClass().getEnumConstants()) {
                /* Locate the element */
                final T myItem = findItemById(myClass.getClassId());

                /* If the item is missing or deleted */
                if ((myItem == null)
                        || myItem.isDeleted()) {
                    /* Add it to the list */
                    myList.add(myClass);
                }
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add Item for class.
         * @param pClass the class to add
         * @return the added class
         * @throws OceanusException on error
         */
        public T addNewItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the new item */
            return newItem(pClass);
        }

        /**
         * Create new Item for class.
         * @param pClass the class to create
         * @return the created class
         * @throws OceanusException on error
         */
        protected abstract T newItem(PrometheusStaticDataClass pClass) throws OceanusException;

        /**
         * Populate default values.
         * @throws OceanusException on error
         */
        public void populateDefaults() throws OceanusException {
            /* Loop through all elements */
            for (PrometheusStaticDataClass myClass : getEnumClass().getEnumConstants()) {
                /* Create new element */
                final T myItem = newItem(myClass);

                /* Validate the item */
                myItem.validate();

                /* Handle validation failure */
                if (myItem.hasErrors()) {
                    throw new PrometheusDataException(myItem, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }

        @Override
        protected PrometheusDataMapItem allocateDataMap() {
            return new PrometheusStaticDataMap<>();
        }
    }

    /**
     * The dataMap class.
     * @param <T> the item type
     */
    public static class PrometheusStaticDataMap<T extends PrometheusStaticDataItem>
            extends PrometheusDataInstanceMap<T, String> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<PrometheusStaticDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusStaticDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(PrometheusDataResource.STATICDATAMAP_ORDERCOUNTS, PrometheusStaticDataMap::getOrderCountMap);
        }

        /**
         * Map of order counts.
         */
        private final Map<Integer, Integer> theOrderCountMap;

        /**
         * Constructor.
         */
        public PrometheusStaticDataMap() {
            /* Create the maps */
            theOrderCountMap = new HashMap<>();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<? extends PrometheusStaticDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the keyMap.
         * @return the map
         */
        private Map<Integer, Integer> getOrderCountMap() {
            return theOrderCountMap;
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theOrderCountMap.clear();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void adjustForItem(final PrometheusDataItem pItem) {
            /* Access item */
            final T myItem = (T) pItem;

            /* Adjust order count */
            final Integer myOrder = myItem.getOrder();
            final Integer myCount = theOrderCountMap.get(myOrder);
            if (myCount == null) {
                theOrderCountMap.put(myOrder, ONE);
            } else {
                theOrderCountMap.put(myOrder, myCount + 1);
            }

            /* Adjust name count */
            adjustForItem(myItem, myItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public T findItemByName(final String pName) {
            return findItemByKey(pName);
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return validKeyCount(pName);
        }

        /**
         * Check availability of name.
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return availableKey(pName);
        }

        /**
         * Check validity of order.
         * @param pOrder the order to look up
         * @return true/false
         */
        public boolean validOrderCount(final Integer pOrder) {
            final Integer myResult = theOrderCountMap.get(pOrder);
            return ONE.equals(myResult);
        }
    }
}
