/*******************************************************************************
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
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.models.data.DataList.ListStyle;

public abstract class StaticData<T extends StaticData<T, E>, E extends Enum<E>> extends EncryptedItem<T> {
    /**
     * Report fields
     */
    protected static final ReportFields theLocalFields = new ReportFields(StaticData.class.getSimpleName(),
            EncryptedItem.theLocalFields);

    /* Called from constructor */
    @Override
    public ReportFields declareFields() {
        return theLocalFields;
    }

    /* Field IDs */
    public static final ReportField FIELD_NAME = theLocalFields.declareEqualityValueField("Name");
    public static final ReportField FIELD_DESC = theLocalFields.declareEqualityValueField("Description");
    public static final ReportField FIELD_ENABLED = theLocalFields.declareEqualityValueField("isEnabled");
    public static final ReportField FIELD_ORDER = theLocalFields.declareDerivedValueField("SortOrder");
    public static final ReportField FIELD_CLASS = theLocalFields.declareEqualityValueField("Class");

    /**
     * The active set of values
     */
    private EncryptedValueSet<T> theValueSet;

    @Override
    public void declareValues(EncryptedValueSet<T> pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /**
     * The Enum Class for this static Data
     */
    private Class<E> theEnumClass = null;

    /**
     * Interface for Static Classes
     */
    public interface StaticInterface {
        public int getClassId();

        public int getOrder();
    }

    @Override
    public String getObjectSummary() {
        return getName();
    }

    /**
     * StaticData Name length
     */
    public final static int NAMELEN = 50;

    /**
     * StaticData Description length
     */
    public final static int DESCLEN = 100;

    /**
     * Return the name of the Static Data
     * @return the name
     */
    public String getName() {
        return getName(theValueSet);
    }

    /**
     * Return the encrypted name of the Static Data
     * @return the encrypted name
     */
    public byte[] getNameBytes() {
        return getNameBytes(theValueSet);
    }

    /**
     * Return the encrypted name of the Static Data
     * @return the encrypted name
     */
    private EncryptedString getNameField() {
        return getNameField(theValueSet);
    }

    /**
     * Return the description of the Static Data
     * @return the description
     */
    public String getDesc() {
        return getDesc(theValueSet);
    }

    /**
     * Return the encrypted description of the Static Data
     * @return the encrypted description
     */
    public byte[] getDescBytes() {
        return getDescBytes(theValueSet);
    }

    /**
     * Return the encrypted name of the Static Data
     * @return the encrypted name
     */
    private EncryptedString getDescField() {
        return getDescField(theValueSet);
    }

    /**
     * Return the sort order of the Static Data
     * @return the order
     */
    public int getOrder() {
        return getOrder(theValueSet);
    }

    /**
     * Return the Static class of the Static Data
     * @return the class
     */
    public E getStaticClass() {
        return getStaticClass(theValueSet, theEnumClass);
    }

    /**
     * Is the Static item enabled
     * @return <code>true/false</code>
     */
    public boolean getEnabled() {
        return getEnabled(theValueSet);
    }

    /**
     * Return the encrypted name of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted description
     */
    public static <X extends StaticData<X, ?>> String getName(EncryptedValueSet<X> pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Return the encrypted name of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted description
     */
    public static <X extends StaticData<X, ?>> byte[] getNameBytes(EncryptedValueSet<X> pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Return the encrypted name of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted name
     */
    private static <X extends StaticData<X, ?>> EncryptedString getNameField(ValueSet<X> pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Return the encrypted description of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted description
     */
    public static <X extends StaticData<X, ?>> String getDesc(EncryptedValueSet<X> pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Return the encrypted description of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted description
     */
    public static <X extends StaticData<X, ?>> byte[] getDescBytes(EncryptedValueSet<X> pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Return the encrypted description of the Static Data
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return the encrypted name
     */
    private static <X extends StaticData<X, ?>> EncryptedString getDescField(ValueSet<X> pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Return the sort order of the Static Data
     * @param <X> the static type
     * @param pValueSet the valueSet
     * @return the order
     */
    public static <X extends StaticData<X, ?>> int getOrder(ValueSet<X> pValueSet) {
        return pValueSet.getValue(FIELD_ORDER, Integer.class);
    }

    /**
     * Return the Static class of the Static Data
     * @param pValueSet the Value Set
     * @param <X> the Static type
     * @param <Y> the Enum Type
     * @param pClass the Enum class
     * @return the class
     */
    public static <X extends StaticData<X, Y>, Y extends Enum<Y>> Y getStaticClass(ValueSet<X> pValueSet,
                                                                                   Class<Y> pClass) {
        return pValueSet.getValue(FIELD_CLASS, pClass);
    }

    /**
     * Is the Static item enabled
     * @param pValueSet the valueSet
     * @param <X> the static type
     * @return <code>true/false</code>
     */
    public static <X extends StaticData<X, ?>> boolean getEnabled(ValueSet<X> pValueSet) {
        return pValueSet.getValue(FIELD_ENABLED, Boolean.class);
    }

    /**
     * Obtain the Enum class of this Static Data
     * @return the class
     */
    protected Class<E> getEnumClass() {
        return theEnumClass;
    }

    private void setValueName(String pName) throws ModelException {
        setEncryptedValue(FIELD_NAME, pName);
    }

    private void setValueName(byte[] pName) throws ModelException {
        setEncryptedValue(FIELD_NAME, pName, String.class);
    }

    private void setValueName(EncryptedString pName) {
        theValueSet.setValue(FIELD_NAME, pName);
    }

    private void setValueDesc(String pDesc) throws ModelException {
        setEncryptedValue(FIELD_DESC, pDesc);
    }

    private void setValueDesc(byte[] pDesc) throws ModelException {
        setEncryptedValue(FIELD_DESC, pDesc, String.class);
    }

    private void setValueDesc(EncryptedString pDesc) {
        theValueSet.setValue(FIELD_DESC, pDesc);
    }

    private void setValueEnabled(Boolean isEnabled) {
        theValueSet.setValue(FIELD_ENABLED, isEnabled);
    }

    private void setValueOrder(Integer pOrder) {
        theValueSet.setValue(FIELD_ORDER, pOrder);
    }

    private void setValueClass(E pClass) {
        theValueSet.setValue(FIELD_CLASS, pClass);
    }

    @Override
    public int compareTo(Object pThat) {
        long result;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is the same class */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the target Static Data */
        StaticData<?, ?> myThat = (StaticData<?, ?>) pThat;

        /* Make sure that the object is the same enumeration class */
        if (myThat.getEnumClass() != this.getEnumClass())
            return -1;

        /* Compare on order */
        if (getOrder() < myThat.getOrder())
            return -1;
        if (getOrder() > myThat.getOrder())
            return 1;

        /* Compare on name */
        result = getName().compareTo(myThat.getName());
        if (result < 0)
            return -1;
        if (result > 0)
            return 1;

        /* Compare on id */
        result = (int) (getId() - myThat.getId());
        if (result == 0)
            return 0;
        else if (result < 0)
            return -1;
        else
            return 1;
    }

    @Override
    public void validate() {
        StaticList<?, ?, ?> myList = (StaticList<?, ?, ?>) getList();

        /* Name must be non-null */
        if (getName() == null) {
            addError("Name must be non-null", FIELD_NAME);
        }

        /* Check that the name is unique */
        else {
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
        if (!hasErrors())
            setValidEdit();
    }

    /**
     * Construct a copy of a Static data.
     * @param pList The list to associate the Static Data with
     * @param pSource The static data to copy
     */
    protected StaticData(StaticList<?, T, E> pList,
                         T pSource) {
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
                if (myOldStyle == ListStyle.EDIT)
                    setId(0);
                pList.setNewId(getItem());
                break;
            case UPDATE:
                setBase(pSource);
                setState(pSource.getState());
                break;
        }
    }

    /**
     * Initial constructor
     * @param pList The list to associate the Static Data with
     * @param pValue the name of the new item
     * @throws ModelException
     */
    protected StaticData(StaticList<?, T, E> pList,
                         String pValue) throws ModelException {
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
     * Clear Text constructor
     * @param pList The list to associate the Static Data with
     * @param uId the id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pValue the name of the new item
     * @param pDesc the description of the new item
     * @throws ModelException
     */
    protected StaticData(StaticList<?, T, E> pList,
                         int uId,
                         boolean isEnabled,
                         int uOrder,
                         String pValue,
                         String pDesc) throws ModelException {
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
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Encrypted constructor
     * @param pList The list to associate the Static Data with
     * @param uId the id of the new item
     * @param uControlId the control id of the new item
     * @param isEnabled is the account type enabled
     * @param uOrder the sort order
     * @param pValue the encrypted name of the new item
     * @param pDesc the encrypted description of the new item
     * @throws ModelException
     */
    protected StaticData(StaticList<?, T, E> pList,
                         int uId,
                         int uControlId,
                         boolean isEnabled,
                         int uOrder,
                         byte[] pValue,
                         byte[] pDesc) throws ModelException {
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
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new ModelException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Parse enum type
     * @param pValue
     * @throws ModelException
     */
    private void parseEnumValue(String pValue) throws ModelException {
        StaticInterface myIFace = null;
        Class<E> myClass = getEnumClass();
        E[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (E myValue : myEnums) {
            /* If this is the desired value */
            if (myValue.toString().equalsIgnoreCase(pValue)) {
                /* Store the class */
                setValueClass(myValue);

                /* If the enum is of the desired type */
                if (myValue instanceof StaticInterface) {
                    /* Access classId and order */
                    myIFace = (StaticInterface) myValue;
                    setId(myIFace.getClassId());
                    setValueOrder(myIFace.getOrder());
                }
                break;
            }
        }

        /* Reject if we didn't find the class */
        if (getStaticClass() == null)
            throw new ModelException(ExceptionClass.DATA, "Invalid value for " + myClass.getSimpleName()
                    + ": " + pValue);

        /* Reject if class was wrong type */
        if (myIFace == null)
            throw new ModelException(ExceptionClass.DATA, "Class: " + myClass.getSimpleName()
                    + " is not valid for StaticData");
    }

    /**
     * Parse enum id
     * @param pId
     * @throws ModelException
     */
    private void parseEnumId(int pId) throws ModelException {
        StaticInterface myIFace = null;
        Class<E> myClass = getEnumClass();
        E[] myEnums = myClass.getEnumConstants();

        /* Loop through the enum constants */
        for (E myValue : myEnums) {
            /* Ensure that the class is of the right type */
            if (!(myValue instanceof StaticInterface))
                throw new ModelException(ExceptionClass.DATA, "Class: " + myClass.getSimpleName()
                        + " is not valid for StaticData");

            /* Access via interface */
            myIFace = (StaticInterface) myValue;

            /* If this is the desired value */
            if (myIFace.getClassId() == pId) {
                /* Store the class and details */
                setValueClass(myValue);
                break;
            }
        }

        /* Reject if we didn't find the class */
        if (getStaticClass() == null)
            throw new ModelException(ExceptionClass.DATA, "Invalid id for " + myClass.getSimpleName() + ": "
                    + pId);
    }

    /**
     * Format a Static Data
     * @param pData the static data to format
     * @return the formatted data
     */
    public static String format(StaticData<?, ?> pData) {
        String myFormat;
        myFormat = (pData != null) ? pData.getName() : "null";
        return myFormat;
    }

    /**
     * Set a new name
     * @param pName the name
     * @throws ModelException
     */
    public void setName(String pName) throws ModelException {
        setValueName(pName);
    }

    /**
     * Set a new description
     * @param pDesc the description
     * @throws ModelException
     */
    public void setDescription(String pDesc) throws ModelException {
        /* Set the appropriate value */
        setValueDesc(pDesc);
    }

    /**
     * Set Enabled indicator
     * @param isEnabled
     */
    public void setEnabled(boolean isEnabled) {
        /* Set the appropriate value */
        setValueEnabled(isEnabled);
    }

    /**
     * Set Order indicator
     * @param iOrder the order
     */
    public void setOrder(int iOrder) {
        /* Set the appropriate value */
        setValueOrder(iOrder);
    }

    @Override
    public boolean applyChanges(DataItem<?> pData) {
        StaticData<?, ?> myData = (StaticData<?, ?>) pData;
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the name if required */
        if (Difference.getDifference(getName(), myData.getName()).isDifferent())
            setValueName(myData.getNameField());

        /* Update the description if required */
        if (Difference.getDifference(getDesc(), myData.getDesc()).isDifferent())
            setValueDesc(myData.getDescField());

        /* Update the enabled indication if required */
        if (getEnabled() != myData.getEnabled())
            setEnabled(myData.getEnabled());

        /* Update the order indication if required */
        if (getOrder() != myData.getOrder())
            setOrder(myData.getOrder());

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
    public abstract static class StaticList<L extends StaticList<L, T, E>, T extends StaticData<T, E>, E extends Enum<E>>
            extends EncryptedList<L, T> {
        /**
         * Obtain the enumClass
         * @return the enumClass
         */
        protected abstract Class<E> getEnumClass();

        /**
         * Construct a generic static data list
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pStyle the style of the list
         */
        public StaticList(Class<L> pClass,
                          Class<T> pBaseClass,
                          DataSet<?> pData,
                          ListStyle pStyle) {
            super(pClass, pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        protected StaticList(L pSource) {
            super(pSource);
        }

        @Override
        public void setNewId(T pItem) {
            super.setNewId(pItem);
        }

        /**
         * Search for a particular item by class
         * @param eClass The class of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T searchFor(E eClass) {
            DataListIterator<T> myIterator;
            T myCurr;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (myCurr.getStaticClass() == eClass)
                    break;
            }

            /* Return to caller */
            return myCurr;
        }

        /**
         * Search for a particular item by Name
         * 
         * @param sName The name of the item to search for
         * @return The Item if present (or <code>null</code> if not found)
         */
        public T searchFor(String sName) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iDiff;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = sName.compareTo(myCurr.getName());
                if (iDiff == 0)
                    break;
            }

            /* Return to caller */
            return myCurr;
        }

        /**
         * Count the instances of a string
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(String pName) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iDiff;
            int iCount = 0;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = pName.compareTo(myCurr.getName());
                if (iDiff == 0)
                    iCount++;
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Count the instances of an Order
         * @param iOrder the order to check for
         * @return The # of instances of the order
         */
        protected int countInstances(int iOrder) {
            DataListIterator<T> myIterator;
            T myCurr;
            int iCount = 0;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (iOrder == myCurr.getOrder())
                    iCount++;
            }

            /* Return to caller */
            return iCount;
        }
    }
}
