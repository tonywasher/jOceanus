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

import net.sourceforge.JDataWalker.Difference;
import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JDataWalker.ReportFields;
import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDataWalker.ReportItem;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;
import uk.co.tolcroft.models.data.EncryptedData.EncryptionGenerator;
import uk.co.tolcroft.models.threads.ThreadStatus;

public abstract class EncryptedItem<T extends EncryptedItem<T>> extends DataItem<T> {
    /**
     * Report fields
     */
    protected static final ReportFields theLocalFields = new ReportFields(
            EncryptedItem.class.getSimpleName(), ReportItem.theLocalFields);

    /* Members */
    private EncryptedValueSet<T> theValueSet;

    /**
     * Declare values
     * @param pValues the values
     */
    public void declareValues(EncryptedValueSet<T> pValues) {
        theValueSet = pValues;
        super.declareValues(pValues);
    }

    @Override
    public EncryptedValueSet<T> getValueSet() {
        return theValueSet;
    }

    /* Field IDs */
    public static final ReportField FIELD_CONTROL = theLocalFields.declareEqualityValueField("ControlKey");
    public static final ReportField FIELD_GENERATOR = theLocalFields.declareLocalField("Generator");

    /**
     * Encrypted Money length
     */
    public final static int MONEYLEN = 10;

    /**
     * Encrypted Units length
     */
    public final static int UNITSLEN = 10;

    /**
     * Encrypted Rate length
     */
    public final static int RATELEN = 10;

    /**
     * Encrypted Price length
     */
    public final static int PRICELEN = 10;

    /**
     * Encrypted Dilution length
     */
    public final static int DILUTELEN = 10;

    /**
     * Generator field
     */
    private EncryptionGenerator theGenerator = null;

    /**
     * Get the ControlKey for this item
     * @return the ControlKey
     */
    public ControlKey getControlKey() {
        return getControlKey(theValueSet);
    }

    /**
     * Get the ControlKey for this item
     * @param pValueSet the valueSet
     * @param <X> the Encrypted type
     * @return the ControlKey
     */
    public static <X extends EncryptedItem<X>> ControlKey getControlKey(EncryptedValueSet<X> pValueSet) {
        return pValueSet.getValue(FIELD_CONTROL, ControlKey.class);
    }

    private void setValueControlKey(ControlKey pKey) {
        theValueSet.setValue(FIELD_CONTROL, pKey);
        theGenerator = pKey.getFieldGenerator();
    }

    private void setValueControlKey(Integer pId) {
        theValueSet.setValue(FIELD_CONTROL, pId);
    }

    /**
     * Constructor
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    public EncryptedItem(EncryptedList<?, T> pList, int uId) {
        super(pList, uId);
        theGenerator = new EncryptionGenerator(null);
    }

    /**
     * Constructor
     * @param pList the list that this item is associated with
     * @param pSource the source item
     */
    public EncryptedItem(EncryptedList<?, T> pList, T pSource) {
        super(pList, pSource);
        theGenerator = pSource.theGenerator;
    }

    /**
     * Set ControlKey
     * @param pControlKey the Control Key
     */
    protected void setControlKey(ControlKey pControlKey) {
        setValueControlKey(pControlKey);
    }

    /**
     * Set ControlKey
     * @param uControlId the Control Id
     * @throws ModelException
     */
    protected void setControlKey(int uControlId) throws ModelException {
        /* Store the id */
        setValueControlKey(uControlId);

        /* Look up the Control keys */
        DataSet<?> myData = ((EncryptedList<?, ?>) getList()).getData();
        ControlKeyList myKeys = myData.getControlKeys();

        /* Look up the ControlKey */
        ControlKey myControl = myKeys.searchFor(uControlId);
        if (myControl == null)
            throw new ModelException(ExceptionClass.DATA, this, "Invalid ControlKey Id");

        /* Store the ControlKey */
        setValueControlKey(myControl);
    }

    /**
     * Set encrypted value
     * @param pField the field to set
     * @param pValue the value to set
     * @throws ModelException
     */
    protected void setEncryptedValue(ReportField pField,
                                     Object pValue) throws ModelException {
        /* Obtain the existing value */
        Object myCurrent = theValueSet.getValue(pField);

        /* Handle bad usage */
        if ((myCurrent != null) && (!EncryptedField.class.isInstance(myCurrent)))
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());

        /* Create the new encrypted value */
        EncryptedField<?> myCurr = (EncryptedField<?>) myCurrent;
        EncryptedField<?> myField = theGenerator.encryptValue(myCurr, pValue);

        /* Store the new value */
        theValueSet.setValue(pField, myField);
    }

    /**
     * Set encrypted value
     * @param pField the field to set
     * @param pEncrypted the encrypted value to set
     * @param pClass the class of the value
     * @throws ModelException
     */
    protected void setEncryptedValue(ReportField pField,
                                     byte[] pEncrypted,
                                     Class<?> pClass) throws ModelException {
        /* Create the new encrypted value */
        EncryptedField<?> myField = theGenerator.decryptValue(pEncrypted, pClass);

        /* Store the new value */
        theValueSet.setValue(pField, myField);
    }

    /**
     * Determine whether two ValuePair objects differ.
     * @param pCurr The current Pair
     * @param pNew The new Pair
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static Difference differs(EncryptedField<?> pCurr,
                                     EncryptedField<?> pNew) {
        /* Handle case where current value is null */
        if (pCurr == null)
            return (pNew != null) ? Difference.Different : Difference.Identical;

        /* Handle case where new value is null */
        if (pNew == null)
            return Difference.Different;

        /* Handle Standard cases */
        return pCurr.differs(pNew);
    }

    /**
     * Rebuild Links to partner data
     * @param pData the DataSet
     */
    protected void reBuildLinks(DataSet<?> pData) {
        ControlKeyList myKeys = pData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.searchFor(myKey.getId());
        setValueControlKey(myNewKey);
    }

    /**
     * Initialise security for all encrypted values
     * @param pControl the new Control Key
     * @param pBase
     * @throws ModelException
     */
    protected void adoptSecurity(ControlKey pControl,
                                 T pBase) throws ModelException {
        /* Set the Control Key */
        setValueControlKey(pControl);

        /* Access underlying values if they exist */
        EncryptedValueSet<T> myBaseValues = null;
        if (pBase != null)
            myBaseValues = pBase.getValueSet();

        /* Try to adopt the underlying */
        theValueSet.adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Update security for all encrypted values
     * @param pControl the new Control Key
     * @throws ModelException
     */
    protected void updateSecurity(ControlKey pControl) throws ModelException {
        /* Ignore call if we have the same control key */
        if (pControl.equals(getControlKey()))
            return;

        /* Store the current detail into history */
        pushHistory();

        /* Set the Control Key */
        setControlKey(pControl);

        /* Update all elements */
        theValueSet.updateSecurity(theGenerator);

        /* Check for changes */
        setState(DataState.CHANGED);
    }

    /**
     * Encrypted DataList
     * @param <L> the list type
     * @param <T> the item type
     */
    public abstract static class EncryptedList<L extends EncryptedList<L, T>, T extends EncryptedItem<T>>
            extends DataList<L, T> {
        private DataSet<?> theData = null;

        public DataSet<?> getData() {
            return theData;
        }

        protected void setData(DataSet<?> pData) {
            theData = pData;
        }

        public ControlKey getControlKey() {
            return theData.getControl().getControlKey();
        }

        /**
         * Construct an empty CORE encrypted list
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         */
        protected EncryptedList(Class<L> pClass, Class<T> pBaseClass, DataSet<?> pData) {
            super(pClass, pBaseClass, ListStyle.CORE, true);
            theData = pData;
        }

        /**
         * Construct a generic encrypted list
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        public EncryptedList(Class<L> pClass, Class<T> pBaseClass, DataSet<?> pData, ListStyle pStyle) {
            super(pClass, pBaseClass, pStyle, true);
            theData = pData;
            setGeneration(pData.getGeneration());
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        protected EncryptedList(L pSource) {
            super(pSource);
            theData = pSource.getData();
        }

        /**
         * Update Security for items in the list
         * @param pThread the thread status
         * @param pControl the control key to apply
         * @return Continue <code>true/false</code>
         * @throws ModelException
         */
        public boolean updateSecurity(ThreadStatus<?> pThread,
                                      ControlKey pControl) throws ModelException {
            DataListIterator<T> myIterator;
            T myCurr;
            int mySteps;
            int myCount = 0;

            /* Declare the new stage */
            if (!pThread.setNewStage(listName()))
                return false;

            /* Access reporting steps */
            mySteps = pThread.getReportingSteps();

            /* Count the Number of items */
            if (!pThread.setNumSteps(sizeAll()))
                return false;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the items */
            while ((myCurr = myIterator.next()) != null) {
                /* Ensure encryption of the item */
                myCurr.updateSecurity(pControl);

                /* Report the progress */
                myCount++;
                if ((myCount % mySteps) == 0)
                    if (!pThread.setStepsDone(myCount))
                        return false;
            }

            /* Return to caller */
            return true;
        }

        /**
         * Adopt security from underlying list. If a match for the item is found in the underlying list, its
         * security is adopted. If no match is found then the security is initialised.
         * @param pThread the thread status
         * @param pControl the control key to initialise from
         * @param pBase The base list to adopt from
         * @return Continue <code>true/false</code>
         * @throws ModelException
         */
        protected boolean adoptSecurity(ThreadStatus<?> pThread,
                                        ControlKey pControl,
                                        EncryptedList<?, ?> pBase) throws ModelException {
            /* Local variables */
            DataListIterator<T> myIterator;
            EncryptedItem<T> myCurr;
            EncryptedItem<?> myBase;
            T mySource;
            T myTarget;
            Class<T> myClass = getBaseClass();
            int mySteps;
            int myCount = 0;

            /* Declare the new stage */
            if (!pThread.setNewStage(listName()))
                return false;

            /* Access reporting steps */
            mySteps = pThread.getReportingSteps();

            /* Count the Number of items */
            if (!pThread.setNumSteps(sizeAll()))
                return false;

            /* Create an iterator for our new list */
            myIterator = listIterator(true);

            /* Loop through this list */
            while ((myCurr = myIterator.next()) != null) {
                /* Locate the item in the base list */
                myBase = pBase.searchFor(myCurr.getId());

                /* Cast the items correctly */
                mySource = (myBase == null) ? null : myClass.cast(myBase);
                myTarget = myClass.cast(myCurr);

                /* Adopt/initialise the security */
                myTarget.adoptSecurity(pControl, mySource);

                /* Report the progress */
                myCount++;
                if ((myCount % mySteps) == 0)
                    if (!pThread.setStepsDone(myCount))
                        return false;
            }

            /* Return to caller */
            return true;
        }
    }
}
