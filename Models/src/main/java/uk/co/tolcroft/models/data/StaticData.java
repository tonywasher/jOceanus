/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.data;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.StaticData.StaticInterface;

/**
 * Template for a Static Data item and List.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the static class
 */
public abstract class StaticData<T extends StaticData<T, E>, E extends Enum<E> & StaticInterface> extends
        EncryptedItem<T> {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(StaticData.class.getSimpleName(),
            EncryptedItem.FIELD_DEFS);

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
     * The active set of values.
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(final EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /**
     * The Enum Class for this Static Data.
     */
    private Class<E> theEnumClass = null;

    /**
     * Interface for Static Classes.
     */
    public interface StaticInterface {
        /**
         * Obtain the class Id.
         * @return the class id
         */
        int getClassId();

        /**
         * Obtain the order.
         * @return the order
         */
        int getOrder();
    }

    @Override
    public String formatObject() {
        return getName();
    }

    /**
     * StaticData Name length.
     */
    public static final int NAMELEN = 50;

    /**
     * StaticData Description length.
     */
    public static final int DESCLEN = 100;

    /**
     * Return the name of the Static Data.
     * @return the name
     */
    public String getName() {
        return getName(theValueSet);
    }

    /**
     * Return the encrypted name of the Static Data.
     * @return the encrypted name
     */
    public byte[] getNameBytes() {
        return getNameBytes(theValueSet);
    }

    /**
     * Return the encrypted name field of the Static Data.
     * @return the encrypted field
     */
    private EncryptedString getNameField() {
        return getNameField(theValueSet);
    }

    /**
     * Return the description of the Static Data.
     * @return the description
     */
    public String getDesc() {
        return getDesc(theValueSet);
    }

    /**
     * Return the encrypted description of the Static Data.
     * @return the encrypted description
     */
    public byte[] getDescBytes() {
        return getDescBytes(theValueSet);
    }

    /**
     * Return the encrypted description field of the Static Data.
     * @return the encrypted name
     */
    private EncryptedString getDescField() {
        return getDescField(theValueSet);
    }

    /**
     * Return the sort order of the Static Data.
     * @return the order
     */
    public int getOrder() {
        return getOrder(theValueSet);
    }

    /**
     * Return the Static class of the Static Data.
     * @return the class
     */
    public E getStaticClass() {
        return getStaticClass(theValueSet, theEnumClass);
    }

    /**
     * Is the Static item enabled.
     * @return <code>true/false</code>
     */
    public boolean getEnabled() {
        return getEnabled(theValueSet);
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
    protected Class<E> getEnumClass() {
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
        theValueSet.setValue(FIELD_NAME, pField);
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
        theValueSet.setValue(FIELD_DESC, pField);
    }

    /**
     * Set the Enabled flag.
     * @param isEnabled TRUE/FALSE
     */
    private void setValueEnabled(final Boolean isEnabled) {
        theValueSet.setValue(FIELD_ENABLED, isEnabled);
    }

    /**
     * Set the Order.
     * @param pOrder the order
     */
    private void setValueOrder(final Integer pOrder) {
        theValueSet.setValue(FIELD_ORDER, pOrder);
    }

    /**
     * Set the Class.
     * @param pClass the class
     */
    private void setValueClass(final E pClass) {
        theValueSet.setValue(FIELD_CLASS, pClass);
    }

    @Override
    public int compareTo(final Object pThat) {
        long result;

        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is the same class */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the target Static Data */
        StaticData<?, ?> myThat = (StaticData<?, ?>) pThat;

        /* Make sure that the object is the same enumeration class */
        if (myThat.getEnumClass() != this.getEnumClass()) {
            return -1;
        }

        /* Compare on order */
        if (getOrder() < myThat.getOrder()) {
            return -1;
        }
        if (getOrder() > myThat.getOrder()) {
            return 1;
        }

        /* Compare on name */
        result = getName().compareTo(myThat.getName());
        if (result < 0) {
            return -1;
        }
        if (result > 0) {
            return 1;
        }

        /* Compare on id */
        result = (int) (getId() - myThat.getId());
        if (result == 0) {
            return 0;
        } else if (result < 0) {
            return -1;
        }
        return 1;
    }

    @Override
    public void validate() {
        StaticList<?, ?, ?> myList = (StaticList<?, ?, ?>) getList();

        /* Name must be non-null */
        if (getName() == null) {
            addError("Name must be non-null", FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The description must not be too long */
            if (getName().length() > NAMELEN) {
                addError("Name is too long", FIELD_NAME);
            }

            if (myList.countInstances(getName()) > 1) {
                addError("Name must be unique", FIELD_NAME);
            }
        }

        /* The order must not be negative */
        if (getOrder() < 0) {
            addError("Order is negative", FIELD_ORDER);
        }

        if (myList.countInstances(getOrder()) > 1) {
            addError("Order must be unique", FIELD_ORDER);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Construct a copy of a Static data.
     * @param pList The list to associate the Static Data with
     * @param pSource The static data to copy
     */
    protected StaticData(final StaticList<?, T, E> pList,
                         final T pSource) {
        super(pList, pSource);
        ListStyle myOldStyle = pSource.getStyle();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Static is based on the original element */
                    setBase(pSource);
                    copyFlags(pSource.getItem());
                    pList.setNewId(getItem());
                    break;
                }

                /* Else this is a duplication so treat as new item */
                setId(0);
                pList.setNewId(getItem());
                break;
            case CLONE:
                reBuildLinks(pList.getData());
            case COPY:
            case CORE:
                /* Reset Id if this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT) {
                    setId(0);
                }
                pList.setNewId(getItem());
                break;
            case UPDATE:
                setBase(pSource);
                setState(pSource.getState());
                break;
            default:
                break;
        }
    }

    /**
     * Initial constructor for unencrypted data.
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<?, T, E> pList,
                         final String pValue) throws JDataException {
        /* Call super constructor */
        super(pList, 0);

        /* Determine the class */
        theEnumClass = pList.getEnumClass();
        parseEnumValue(pValue);

        /* Record the name */
        setValueName(pValue);

        /* Set the new Id */
        pList.setNewId(getItem());
    }

    /**
     * Clear Text constructor.
     * @param pList The list to associate the Static Data with
     * @param uId the id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pValue the name of the new item
     * @param pDesc the description of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<?, T, E> pList,
                         final int uId,
                         final boolean isEnabled,
                         final int uOrder,
                         final String pValue,
                         final String pDesc) throws JDataException {
        /* Call super constructor */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the details */
            setValueEnabled(isEnabled);
            setValueOrder(uOrder);
            setValueName(pValue);
            setValueDesc(pDesc);

            /* Determine the class */
            theEnumClass = pList.getEnumClass();
            parseEnumId(uId);

            /* Set the new Id */
            pList.setNewId(getItem());

            /* Catch Exceptions */
        } catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Encrypted constructor.
     * @param pList The list to associate the Static Data with
     * @param uId the id of the new item
     * @param uControlId the control id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pValue the encrypted name of the new item
     * @param pDesc the encrypted description of the new item
     * @throws JDataException on error
     */
    protected StaticData(final StaticList<?, T, E> pList,
                         final int uId,
                         final int uControlId,
                         final boolean isEnabled,
                         final int uOrder,
                         final byte[] pValue,
                         final byte[] pDesc) throws JDataException {
        /* Call super constructor */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the controlId */
            setControlKey(uControlId);

            /* Store the details */
            setValueEnabled(isEnabled);
            setValueOrder(uOrder);
            setValueName(pValue);
            setValueDesc(pDesc);

            /* Determine the class */
            theEnumClass = pList.getEnumClass();
            parseEnumId(uId);

            /* Set the new Id */
            pList.setNewId(getItem());

            /* Catch Exceptions */
        } catch (Exception e) {
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
            throw new JDataException(ExceptionClass.DATA, "Invalid value for " + myClass.getSimpleName()
                    + ": " + pValue);
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
            throw new JDataException(ExceptionClass.DATA, "Invalid id for " + myClass.getSimpleName() + ": "
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
        myFormat = (pData != null) ? pData.getName() : "null";
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
    public boolean applyChanges(final DataItem<?> pData) {
        StaticData<?, ?> myData = (StaticData<?, ?>) pData;
        boolean bChanged = false;

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
        if (checkForHistory()) {
            /* Mark as changed */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * Represents a list of StaticData objects.
     * @param <L> the list type
     * @param <T> the item type
     * @param <E> the static data class
     */
    public abstract static class StaticList<L extends StaticList<L, T, E>, T extends StaticData<T, E>, E extends Enum<E> & StaticInterface>
            extends EncryptedList<L, T> {
        /**
         * Obtain the enumClass.
         * @return the enumClass
         */
        protected abstract Class<E> getEnumClass();

        /**
         * Construct a generic static data list.
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pStyle the style of the list
         */
        public StaticList(final Class<L> pClass,
                          final Class<T> pBaseClass,
                          final DataSet<?> pData,
                          final ListStyle pStyle) {
            super(pClass, pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StaticList(final L pSource) {
            super(pSource);
        }

        @Override
        public void setNewId(final T pItem) {
            super.setNewId(pItem);
        }

        /**
         * Search for a particular item by class.
         * @param eClass The class of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T searchFor(final E eClass) {
            DataListIterator<T> myIterator;
            T myCurr;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (myCurr.getStaticClass() == eClass) {
                    break;
                }
            }

            /* Return to caller */
            return myCurr;
        }

        /**
         * Search for a particular item by Name.
         * @param sName The name of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T searchFor(final String sName) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iDiff;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = sName.compareTo(myCurr.getName());
                if (iDiff == 0) {
                    break;
                }
            }

            /* Return to caller */
            return myCurr;
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iDiff;
            int iCount = 0;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = pName.compareTo(myCurr.getName());
                if (iDiff == 0) {
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
        protected int countInstances(final int iOrder) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iCount = 0;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (iOrder == myCurr.getOrder()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }
    }
}
