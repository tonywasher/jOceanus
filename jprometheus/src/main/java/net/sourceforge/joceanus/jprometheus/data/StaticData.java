/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Template for a Static Data item and List.
 * @author Tony Washer
 * @param <T> the data type
 * @param <S> the static class
 * @param <E> the data type enum class
 */
public abstract class StaticData<T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface, E extends Enum<E>>
        extends EncryptedItem<E>
        implements Comparable<T> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StaticData.class.getName());

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataDataName"), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

    /**
     * Enabled Field Id.
     */
    public static final JDataField FIELD_ENABLED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataEnabled"));

    /**
     * Order Field Id.
     */
    public static final JDataField FIELD_ORDER = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataOrder"));

    /**
     * Class Field Id.
     */
    public static final JDataField FIELD_CLASS = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClass"));

    /**
     * BadId error.
     */
    public static final String ERROR_BADID = NLS_BUNDLE.getString("ErrorBadId");

    /**
     * BadName error.
     */
    public static final String ERROR_BADNAME = NLS_BUNDLE.getString("ErrorBadName");

    /**
     * The Enum Class for this Static Data.
     */
    private Class<S> theEnumClass = null;

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
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

    /**
     * Return the name of the Static Data.
     * @return the name
     */
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
    private EncryptedString getNameField() {
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
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Return the sort order of the Static Data.
     * @return the order
     */
    public final int getOrder() {
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
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Return the encrypted name of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted name
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Return the encrypted name field of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted name field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Return the description of the Static Data.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Return the encrypted description of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted description
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Return the encrypted description field of the Static Data.
     * @param pValueSet the valueSet
     * @return the encrypted description field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Return the sort order of the Static Data.
     * @param pValueSet the valueSet
     * @return the order
     */
    public static int getOrder(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ORDER, Integer.class);
    }

    /**
     * Return the Static class of the Static Data.
     * @param pValueSet the Value Set
     * @param <Y> the Enum Type
     * @param pClass the Enum class
     * @return the class
     */
    public static <Y extends Enum<Y> & StaticInterface> Y getStaticClass(final ValueSet pValueSet,
                                                                         final Class<Y> pClass) {
        return pValueSet.getValue(FIELD_CLASS, pClass);
    }

    /**
     * Is the Static item enabled.
     * @param pValueSet the valueSet
     * @return <code>true/false</code>
     */
    public static boolean getEnabled(final ValueSet pValueSet) {
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
     * @throws JOceanusException on error
     */
    private void setValueName(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set the Name.
     * @param pBytes the encrypted name
     * @throws JOceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set the Name field.
     * @param pField the encrypted name
     */
    private void setValueName(final EncryptedString pField) {
        getValueSet().setValue(FIELD_NAME, pField);
    }

    /**
     * Set the Description.
     * @param pValue the description
     * @throws JOceanusException on error
     */
    protected final void setValueDesc(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set the Description.
     * @param pBytes the encrypted description
     * @throws JOceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set the Description field.
     * @param pField the encrypted description
     */
    private void setValueDesc(final EncryptedString pField) {
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
        if (pThat.getEnumClass() != getEnumClass()) {
            /* Compare the classes */
            return getEnumClass().getCanonicalName().compareTo(pThat.getEnumClass().getCanonicalName());
        }

        /* Compare on order */
        int iDiff = getOrder() - pThat.getOrder();
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare on name */
        iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void validate() {
        StaticList<?, ?, ?> myList = (StaticList<?, ?, ?>) getList();
        String myName = getName();
        String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Else check the name */
        } else {
            /* The description must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* Check that the name is unique */
            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* The order must not be negative */
        if (getOrder() < 0) {
            addError(ERROR_NEGATIVE, FIELD_ORDER);
        }

        /* Cannot have duplicate order */
        if (myList.countInstances(getOrder()) > 1) {
            addError(ERROR_DUPLICATE, FIELD_ORDER);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Static Data with
     * @param pSource The static data to copy
     */
    protected StaticData(final StaticList<T, S, E> pList,
                         final T pSource) {
        super(pList, pSource);
        theEnumClass = pSource.getEnumClass();
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws JOceanusException on error
     */
    protected StaticData(final StaticList<T, S, E> pList,
                         final String pValue) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    protected StaticData(final StaticList<T, S, E> pList,
                         final S pClass) throws JOceanusException {
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
        setValueName(pClass.name());
        setValueEnabled(Boolean.TRUE);

        /* Set the DataKeySet */
        setNextDataKeySet();
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    protected StaticData(final StaticList<T, S, E> pList,
                         final DataValues<E> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

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
                parseEnumValue(getName());
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
                | JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Parse enum type.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void parseEnumValue(final String pValue) throws JOceanusException {
        Class<S> myClass = getEnumClass();
        S[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (S myValue : myEnums) {
            /* If this is the desired value */
            if (myValue.name().equalsIgnoreCase(pValue)) {
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
            throw new JPrometheusDataException(ERROR_BADNAME + " " + myClass.getSimpleName() + ": " + pValue);
        }
    }

    /**
     * Set a new name.
     * @param pName the name
     * @throws JOceanusException on error
     */
    public void setName(final String pName) throws JOceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    public void setDescription(final String pDesc) throws JOceanusException {
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
    public boolean applyChanges(final DataItem<?> pData) {
        /* Can only apply changes for Static Data */
        if (!(pData instanceof StaticData)) {
            return false;
        }

        /* Access the data */
        StaticData<?, ?, ?> myData = (StaticData<?, ?, ?>) pData;

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
    protected void applyBasicChanges(final StaticData<?, ?, ?> pData) {
        /* Update the name if required */
        if (!Difference.isEqual(getName(), pData.getName())) {
            setValueName(pData.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), pData.getDesc())) {
            setValueDesc(pData.getDescField());
        }

        /* Update the enabled indication if required */
        if (getEnabled() != pData.getEnabled()) {
            setEnabled(pData.getEnabled());
        }

        /* Update the order indication if required */
        if (getOrder() != pData.getOrder()) {
            setOrder(pData.getOrder());
        }
    }

    /**
     * Represents a list of StaticData objects.
     * @param <T> the item type
     * @param <S> the static data class
     * @param <E> the data type enum class
     */
    public abstract static class StaticList<T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface, E extends Enum<E>>
            extends EncryptedList<T, E> {
        /**
         * Obtain the enumClass.
         * @return the enumClass
         */
        protected abstract Class<S> getEnumClass();

        /**
         * Construct a generic static data list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pItemType the item type
         * @param pStyle the style of the list
         */
        public StaticList(final Class<T> pBaseClass,
                          final DataSet<?, ?> pData,
                          final E pItemType,
                          final ListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StaticList(final StaticList<T, S, E> pSource) {
            super(pSource);
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
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
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
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

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
            List<S> myList = new ArrayList<S>();

            /* Loop through all elements */
            for (S myClass : getEnumClass().getEnumConstants()) {
                /* Locate the element */
                T myItem = findItemById(myClass.getClassId());

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
         * @throws JOceanusException on error
         */
        public T addNewItem(final S pClass) throws JOceanusException {
            /* Create the new item */
            T myItem = newItem(pClass);
            add(myItem);
            return myItem;
        }

        /**
         * Create new Item for class.
         * @param pClass the class to create
         * @return the created class
         * @throws JOceanusException on error
         */
        protected abstract T newItem(final S pClass) throws JOceanusException;

        /**
         * Count the instances of a name.
         * @param pName the name to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Ignore deleted items */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Adjust count */
                if (pName.equals(myCurr.getName())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Count the instances of an Order.
         * @param iOrder the order to check for
         * @return The # of instances of the order
         */
        protected int countInstances(final Integer iOrder) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();

                /* Ignore deleted items */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Adjust count */
                if (iOrder == myCurr.getOrder()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (S myClass : getEnumClass().getEnumConstants()) {
                /* Create new element */
                T myItem = newItem(myClass);

                /* Add the item to the list */
                append(myItem);

                /* Validate the item */
                myItem.validate();

                /* Handle validation failure */
                if (myItem.hasErrors()) {
                    throw new JPrometheusDataException(myItem, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
