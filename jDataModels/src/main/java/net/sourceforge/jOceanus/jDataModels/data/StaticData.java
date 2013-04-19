/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.data;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;

/**
 * Template for a Static Data item and List.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the static class
 */
public abstract class StaticData<T extends StaticData<T, E>, E extends Enum<E> & StaticInterface>
        extends EncryptedItem
        implements Comparable<T> {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(StaticData.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField("Name");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * Enabled Field Id.
     */
    public static final JDataField FIELD_ENABLED = FIELD_DEFS.declareEqualityValueField("isEnabled");

    /**
     * Order Field Id.
     */
    public static final JDataField FIELD_ORDER = FIELD_DEFS.declareDerivedValueField("SortOrder");

    /**
     * Class Field Id.
     */
    public static final JDataField FIELD_CLASS = FIELD_DEFS.declareEqualityValueField("Class");

    /**
     * The Enum Class for this Static Data.
     */
    private Class<E> theEnumClass = null;

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * StaticData Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * StaticData Description length.
     */
    public static final int DESCLEN = 50;

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
    public final E getStaticClass() {
        return getStaticClass(getValueSet(), theEnumClass);
    }

    /**
     * Is the Static item enabled.
     * @return <code>true/false</code>
     */
    public final boolean getEnabled() {
        return getEnabled(getValueSet());
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
    protected final Class<E> getEnumClass() {
        return theEnumClass;
    }

    /**
     * Set the Name.
     * @param pValue the name
     * @throws JDataException on error
     */
    private void setValueName(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set the Name.
     * @param pBytes the encrypted name
     * @throws JDataException on error
     */
    private void setValueName(final byte[] pBytes) throws JDataException {
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
     * @throws JDataException on error
     */
    private void setValueDesc(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set the Description.
     * @param pBytes the encrypted description
     * @throws JDataException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JDataException {
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
    private void setValueEnabled(final Boolean isEnabled) {
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
    private void setValueClass(final E pClass) {
        getValueSet().setValue(FIELD_CLASS, pClass);
    }

    @Override
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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
        int iDiff = getOrder()
                    - pThat.getOrder();
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
        StaticList<?, ?> myList = (StaticList<?, ?>) getList();
        String myName = getName();
        String myDesc = getDesc();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The description must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null)
            && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_NAME);
        }

        /* The order must not be negative */
        if (getOrder() < 0) {
            addError(ERROR_NEGATIVE, FIELD_ORDER);
        }

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
    protected StaticData(final StaticList<T, E> pList,
                         final T pSource) {
        super(pList, pSource);
        theEnumClass = pSource.getEnumClass();
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<T, E> pList,
                         final String pValue) throws JDataException {
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
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<T, E> pList,
                         final E pClass) throws JDataException {
        /* Call super constructor */
        super(pList, 0);

        /* Determine the class */
        theEnumClass = pList.getEnumClass();

        /* Store the class */
        setValueClass(pClass);

        /* Access classId and order */
        setId(pClass.getClassId());
        setValueOrder(pClass.getOrder());

        /* Record the name */
        setValueName(pClass.name());
        setValueEnabled(Boolean.TRUE);
    }

    /**
     * Open constructor.
     * @param pList The list to associate the Static Data with
     * @param pId the id of the new item
     * @param isEnabled is the account type enabled
     * @param pOrder the sort order
     * @param pValue the name of the new item
     * @param pDesc the description of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<T, E> pList,
                         final Integer pId,
                         final Boolean isEnabled,
                         final Integer pOrder,
                         final String pValue,
                         final String pDesc) throws JDataException {
        /* Call super constructor */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the details */
            setValueEnabled(isEnabled);
            setValueOrder(pOrder);
            setValueName(pValue);
            setValueDesc(pDesc);

            /* Determine the class */
            theEnumClass = pList.getEnumClass();
            parseEnumId(pId);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Secure constructor.
     * @param pList The list to associate the Static Data with
     * @param pId the id of the new item
     * @param pControlId the control id of the new item
     * @param isEnabled is the account type enabled
     * @param pOrder the sort order
     * @param pValue the encrypted name of the new item
     * @param pDesc the encrypted description of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<T, E> pList,
                         final Integer pId,
                         final Integer pControlId,
                         final Boolean isEnabled,
                         final Integer pOrder,
                         final byte[] pValue,
                         final byte[] pDesc) throws JDataException {
        /* Call super constructor */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the controlId */
            setControlKey(pControlId);

            /* Store the details */
            setValueEnabled(isEnabled);
            setValueOrder(pOrder);
            setValueName(pValue);
            setValueDesc(pDesc);

            /* Determine the class */
            theEnumClass = pList.getEnumClass();
            parseEnumId(pId);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Parse enum type.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void parseEnumValue(final String pValue) throws JDataException {
        Class<E> myClass = getEnumClass();
        E[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (E myValue : myEnums) {
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
            throw new JDataException(ExceptionClass.DATA, "Invalid value for "
                                                          + myClass.getSimpleName()
                                                          + ": "
                                                          + pValue);
        }
    }

    /**
     * Parse enum id.
     * @param pId the id
     * @throws JDataException on error
     */
    private void parseEnumId(final int pId) throws JDataException {
        Class<E> myClass = getEnumClass();
        E[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (E myValue : myEnums) {
            /* If this is the desired value */
            if (myValue.getClassId() == pId) {
                /* Store the class and details */
                setValueClass(myValue);
                break;
            }
        }

        /* Reject if we didn't find the class */
        if (getStaticClass() == null) {
            throw new JDataException(ExceptionClass.DATA, "Invalid id for "
                                                          + myClass.getSimpleName()
                                                          + ": "
                                                          + pId);
        }
    }

    /**
     * Format a Static Data.
     * @param pData the static data to format
     * @return the formatted data
     */
    public static String format(final StaticData<?, ?> pData) {
        String myFormat;
        myFormat = (pData != null)
                ? pData.getName()
                : "null";
        return myFormat;
    }

    /**
     * Set a new name.
     * @param pName the name
     * @throws JDataException on error
     */
    public void setName(final String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JDataException on error
     */
    public void setDescription(final String pDesc) throws JDataException {
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
        if (!(pData instanceof StaticData)) {
            return false;
        }

        /* Access the data */
        StaticData<?, ?> myData = (StaticData<?, ?>) pData;

        /* Store the current detail into history */
        pushHistory();

        /* Update the name if required */
        if (!Difference.isEqual(getName(), myData.getName())) {
            setValueName(myData.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myData.getDesc())) {
            setValueDesc(myData.getDescField());
        }

        /* Update the enabled indication if required */
        if (getEnabled() != myData.getEnabled()) {
            setEnabled(myData.getEnabled());
        }

        /* Update the order indication if required */
        if (getOrder() != myData.getOrder()) {
            setOrder(myData.getOrder());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Represents a list of StaticData objects.
     * @param <T> the item type
     * @param <E> the static data class
     */
    public abstract static class StaticList<T extends StaticData<T, E>, E extends Enum<E> & StaticInterface>
            extends EncryptedList<T> {
        /**
         * Obtain the enumClass.
         * @return the enumClass
         */
        protected abstract Class<E> getEnumClass();

        /**
         * Construct a generic static data list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pStyle the style of the list
         */
        public StaticList(final Class<T> pBaseClass,
                          final DataSet<?> pData,
                          final ListStyle pStyle) {
            super(pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StaticList(final StaticList<T, E> pSource) {
            super(pSource);
        }

        /**
         * Search for a particular item by class.
         * @param eClass The class of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T findItemByClass(final E eClass) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (myCurr.getStaticClass() == eClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Search for a particular item by Name.
         * @param pName The name of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
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
                if (iOrder == myCurr.getOrder()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }
    }
}
