/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedString;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Template for a Static Data item and List.
 * @author Tony Washer
 * @param <T> the data type
 * @param <S> the static class
 */
public abstract class StaticDataItem<T extends StaticDataItem<T, S>, S extends Enum<S> & StaticDataClass>
        extends EncryptedItem
        implements Comparable<T>, MetisDataNamedItem {
    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.STATICDATA_NAME.getValue(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final MetisLetheField FIELD_NAME = FIELD_DEFS.declareComparisonEncryptedField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue(), MetisDataType.STRING, NAMELEN);

    /**
     * Description Field Id.
     */
    public static final MetisLetheField FIELD_DESC = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResource.DATAITEM_FIELD_DESC.getValue(), MetisDataType.STRING, DESCLEN);

    /**
     * Enabled Field Id.
     */
    public static final MetisLetheField FIELD_ENABLED = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.STATICDATA_ENABLED.getValue(), MetisDataType.BOOLEAN);

    /**
     * Order Field Id.
     */
    public static final MetisLetheField FIELD_ORDER = FIELD_DEFS.declareComparisonValueField(PrometheusDataResource.STATICDATA_SORT.getValue(), MetisDataType.INTEGER);

    /**
     * Class Field Id.
     */
    public static final MetisLetheField FIELD_CLASS = FIELD_DEFS.declareComparisonValueField(PrometheusDataResource.STATICDATA_CLASS.getValue(), MetisDataType.ENUM);

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
    private Class<S> theEnumClass;

    /**
     * Copy Constructor.
     * @param pList The list to associate the Static Data with
     * @param pSource The static data to copy
     */
    protected StaticDataItem(final StaticList<T, S> pList,
                             final T pSource) {
        super(pList, pSource);
        theEnumClass = pSource.getEnumClass();
        setId(pSource.getId());
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws OceanusException on error
     */
    protected StaticDataItem(final StaticList<T, S> pList,
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
    protected StaticDataItem(final StaticList<T, S> pList,
                             final S pClass) throws OceanusException {
        /* Call super constructor */
        super(pList, 0);

        /* Determine the class */
        theEnumClass = pList.getEnumClass();

        /* Store the class */
        setValueClass(pClass);

        /* Set encryption */
        setNextDataKeySet();

        /* Access classId and order */
        setId(pClass.getClassId());
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
    protected StaticDataItem(final StaticList<T, S> pList,
                             final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Determine the class */
            theEnumClass = pList.getEnumClass();

            /* Store the Name */
            Object myValue = pValues.getValue(FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the class */
            myValue = pValues.getValue(FIELD_CLASS);
            if (myValue instanceof String) {
                parseEnumValue((String) myValue);
            } else {
                parseEnumValue(getId());
            }

            /* Store the Order */
            myValue = pValues.getValue(FIELD_ORDER);
            if (myValue instanceof Integer) {
                setValueOrder((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueOrder(myFormatter.parseValue((String) myValue, Integer.class));
            }

            /* Store the Enabled flag */
            myValue = pValues.getValue(FIELD_ENABLED);
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
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_NAME.equals(pField)) {
            return true;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (FIELD_ENABLED.equals(pField)) {
            return !getEnabled();
        }
        if (FIELD_CLASS.equals(pField)) {
            return !getName().equalsIgnoreCase(getStaticClass().name());
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public final String getName() {
        return getName(getValueSet());
    }

    /**
     * Return the encrypted name of the Static Data.
     * @return the encrypted name
     */
    public final byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Return the encrypted name field of the Static Data.
     * @return the encrypted field
     */
    private MetisEncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Return the description of the Static Data.
     * @return the description
     */
    public final String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Return the encrypted description of the Static Data.
     * @return the encrypted description
     */
    public final byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Return the encrypted description field of the Static Data.
     * @return the encrypted name
     */
    private MetisEncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Return the sort order of the Static Data.
     * @return the order
     */
    public final Integer getOrder() {
        return getOrder(getValueSet());
    }

    /**
     * Return the Static class of the Static Data.
     * @return the class
     */
    public final S getStaticClass() {
        return getStaticClass(getValueSet(), theEnumClass);
    }

    /**
     * Is the Static item enabled.
     * @return <code>true/false</code>
     */
    public final boolean getEnabled() {
        return getEnabled(getValueSet());
    }

    @Override
    public boolean isDisabled() {
        return !getEnabled();
    }

    /**
     * Return the name of the Static Data.
     * @param pValueSet the valueSet
     * @return the name
     */
    public static String getName(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Return the encrypted name of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted name
     */
    public static byte[] getNameBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Return the encrypted name field of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted name field
     */
    private static MetisEncryptedString getNameField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, MetisEncryptedString.class);
    }

    /**
     * Return the description of the Static Data.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Return the encrypted description of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted description
     */
    public static byte[] getDescBytes(final MetisEncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Return the encrypted description field of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted description field
     */
    private static MetisEncryptedString getDescField(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, MetisEncryptedString.class);
    }

    /**
     * Return the sort order of the Static Data.
     * @param pValueSet the valueSet
     * @return the order
     */
    public static Integer getOrder(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ORDER, Integer.class);
    }

    /**
     * Return the Static class of the Static Data.
     * @param pValueSet the Value Set
     * @param <Y> the Enum Type
     * @param pClass the Enum class
     * @return the class
     */
    public static <Y extends Enum<Y> & StaticDataClass> Y getStaticClass(final MetisValueSet pValueSet,
                                                                         final Class<Y> pClass) {
        return pValueSet.getValue(FIELD_CLASS, pClass);
    }

    /**
     * Is the Static item enabled.
     * @param pValueSet the valueSet
     * @return <code>true/false</code>
     */
    public static boolean getEnabled(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENABLED, Boolean.class);
    }

    /**
     * Obtain the Enum class of this Static Data.
     * @return the class
     */
    protected final Class<S> getEnumClass() {
        return theEnumClass;
    }

    /**
     * Set the Name.
     * @param pValue the name
     * @throws OceanusException on error
     */
    private void setValueName(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set the Name.
     * @param pBytes the encrypted name
     * @throws OceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set the Name field.
     * @param pField the encrypted name
     */
    private void setValueName(final MetisEncryptedString pField) {
        getValueSet().setValue(FIELD_NAME, pField);
    }

    /**
     * Set the Description.
     * @param pValue the description
     * @throws OceanusException on error
     */
    protected final void setValueDesc(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set the Description.
     * @param pBytes the encrypted description
     * @throws OceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set the Description field.
     * @param pField the encrypted description
     */
    private void setValueDesc(final MetisEncryptedString pField) {
        getValueSet().setValue(FIELD_DESC, pField);
    }

    /**
     * Set the Enabled flag.
     * @param isEnabled TRUE/FALSE
     */
    protected final void setValueEnabled(final Boolean isEnabled) {
        getValueSet().setValue(FIELD_ENABLED, isEnabled);
    }

    /**
     * Set the Order.
     * @param pOrder the order
     */
    private void setValueOrder(final Integer pOrder) {
        getValueSet().setValue(FIELD_ORDER, pOrder);
    }

    /**
     * Set the Class.
     * @param pClass the class
     */
    private void setValueClass(final S pClass) {
        getValueSet().setValue(FIELD_CLASS, pClass);
    }

    @Override
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is the same enumeration class */
        if (!MetisDataDifference.isEqual(getEnumClass(), pThat.getEnumClass())) {
            /* Order the classes by canonical name */
            return getEnumClass().getCanonicalName().compareTo(pThat.getEnumClass().getCanonicalName());
        }

        /* Compare on order */
        int iDiff = getOrder() - pThat.getOrder();
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare on name */
        iDiff = MetisDataDifference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void validate() {
        final StaticList<T, S> myList = getList();
        final String myName = getName();
        final String myDesc = getDesc();
        final StaticDataMap<T, S> myMap = myList.getDataMap();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Else check the name */
        } else {
            /* The name must not be too long */
            if (DataItem.byteLength(myName) > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* The name must only contain valid characters */
            if (!DataItem.validString(myName, null)) {
                addError(ERROR_BADNAME, FIELD_NAME);
            }

            /* Check that the name is unique */
            if (!myMap.validNameCount(myName)) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if (myDesc != null
                && DataItem.byteLength(myDesc) > DESCLEN) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* The order must not be negative */
        if (getOrder() < 0) {
            addError(ERROR_NEGATIVE, FIELD_ORDER);
        }

        /* Cannot have duplicate order */
        if (!myMap.validOrderCount(getOrder())) {
            addError(ERROR_DUPLICATE, FIELD_ORDER);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public StaticList<T, S> getList() {
        return (StaticList<T, S>) super.getList();
    }

    /**
     * Parse enum type.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void parseEnumValue(final String pValue) throws OceanusException {
        final Class<S> myClass = getEnumClass();
        final S[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (S myValue : myEnums) {
            /* If this is the desired value */
            if (myValue.toString().equalsIgnoreCase(pValue)) {
                /* Store the class */
                setValueClass(myValue);

                /* Access classId and order */
                setId(myValue.getClassId());
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
        final Class<S> myClass = getEnumClass();
        final S[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (S myValue : myEnums) {
            /* If this is the desired value */
            if (pValue.equals(myValue.getClassId())) {
                /* Store the class */
                setValueClass(myValue);

                /* Access classId and order */
                setId(myValue.getClassId());
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
    public boolean applyChanges(final DataItem pData) {
        /* Can only apply changes for Static Data */
        if (!(pData instanceof StaticDataItem)) {
            return false;
        }

        /* Access the data */
        final StaticDataItem<?, ?> myData = (StaticDataItem<?, ?>) pData;

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
    protected void applyBasicChanges(final StaticDataItem<?, ?> pData) {
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
        final StaticList<T, S> myList = getList();
        final StaticDataMap<T, S> myMap = myList.getDataMap();
        myMap.adjustForItem(myList.getBaseClass().cast(this));
    }

    /**
     * Represents a list of StaticData objects.
     * @param <T> the item type
     * @param <S> the static data class
      */
    public abstract static class StaticList<T extends StaticDataItem<T, S>, S extends Enum<S> & StaticDataClass>
            extends EncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(StaticList.class);
        }

        /**
         * Construct a generic static data list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pItemType the item type
         * @param pStyle the style of the list
         */
        protected StaticList(final Class<T> pBaseClass,
                             final DataSet<?> pData,
                             final PrometheusListKey pItemType,
                             final ListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StaticList(final StaticList<T, S> pSource) {
            super(pSource);
        }

        /**
         * Obtain the enumClass.
         * @return the enumClass
         */
        protected abstract Class<S> getEnumClass();

        @Override
        @SuppressWarnings("unchecked")
        protected StaticDataMap<T, S> getDataMap() {
            return (StaticDataMap<T, S>) super.getDataMap();
        }

        /**
         * Search for a particular item by class.
         * @param eClass The class of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T findItemByClass(final S eClass) {
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
        public List<S> getMissingClasses() {
            /* Allocate the list */
            final List<S> myList = new ArrayList<>();

            /* Loop through all elements */
            for (S myClass : getEnumClass().getEnumConstants()) {
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
        public T addNewItem(final S pClass) throws OceanusException {
            /* Create the new item */
            return newItem(pClass);
        }

        /**
         * Create new Item for class.
         * @param pClass the class to create
         * @return the created class
         * @throws OceanusException on error
         */
        protected abstract T newItem(S pClass) throws OceanusException;

        /**
         * Populate default values.
         * @throws OceanusException on error
         */
        public void populateDefaults() throws OceanusException {
            /* Loop through all elements */
            for (S myClass : getEnumClass().getEnumConstants()) {
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
        protected DataMapItem<T> allocateDataMap() {
            return new StaticDataMap<>();
        }
    }

    /**
     * The dataMap class.
     * @param <T> the item type
     * @param <S> the static data class
     */
    protected static class StaticDataMap<T extends StaticDataItem<T, S>, S extends Enum<S> & StaticDataClass>
            extends DataInstanceMap<T, String> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<StaticDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(StaticDataMap.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(PrometheusDataResource.STATICDATAMAP_ORDERCOUNTS, StaticDataMap::getOrderCountMap);
        }

        /**
         * Map of order counts.
         */
        private final Map<Integer, Integer> theOrderCountMap;

        /**
         * Constructor.
         */
        public StaticDataMap() {
            /* Create the maps */
            theOrderCountMap = new HashMap<>();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<? extends StaticDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
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
        public void adjustForItem(final T pItem) {
            /* Adjust order count */
            final Integer myOrder = pItem.getOrder();
            final Integer myCount = theOrderCountMap.get(myOrder);
            if (myCount == null) {
                theOrderCountMap.put(myOrder, ONE);
            } else {
                theOrderCountMap.put(myOrder, myCount + 1);
            }

            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
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
