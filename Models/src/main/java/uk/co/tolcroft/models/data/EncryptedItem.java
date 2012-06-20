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

import java.util.Iterator;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedField;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import net.sourceforge.JGordianKnot.EncryptionGenerator;
import uk.co.tolcroft.models.data.ControlKey.ControlKeyList;

/**
 * Encrypted Data Item and List.
 * @author Tony Washer
 */
public abstract class EncryptedItem extends DataItem {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EncryptedItem.class.getSimpleName(),
            DataItem.FIELD_DEFS);

    /**
     * Value set for item.
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(final ValueSet pValues) {
        theValueSet = (EncryptedValueSet) pValues;
        super.declareValues(pValues);
    }

    @Override
    public EncryptedValueSet getValueSet() {
        return theValueSet;
    }

    @Override
    public ValueSet allocateValueSet() {
        /* Allocate initial value set */
        return new EncryptedValueSet(getItem());
    }

    /**
     * Control Key Field Id.
     */
    public static final JDataField FIELD_CONTROL = FIELD_DEFS.declareEqualityValueField("ControlKey");

    /**
     * Generator field.
     */
    private EncryptionGenerator theGenerator = null;

    /**
     * Get the ControlKey for this item.
     * @return the ControlKey
     */
    public ControlKey getControlKey() {
        return getControlKey(theValueSet);
    }

    /**
     * Get the ControlKey for this item.
     * @param pValueSet the valueSet
     * @return the ControlKey
     */
    public static ControlKey getControlKey(final EncryptedValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CONTROL, ControlKey.class);
    }

    /**
     * Set the control Key for this item.
     * @param pKey the control key
     */
    private void setValueControlKey(final ControlKey pKey) {
        theValueSet.setValue(FIELD_CONTROL, pKey);
        theGenerator = pKey.getFieldGenerator();
    }

    /**
     * Set the control Key id for this item.
     * @param pId the control key id
     */
    private void setValueControlKey(final Integer pId) {
        theValueSet.setValue(FIELD_CONTROL, pId);
    }

    /**
     * Constructor. This creates a null encryption generator. This will be overridden when a ControlKey is
     * assigned to the item.
     * @param pList the list that this item is associated with
     * @param uId the Id of the new item (or 0 if not yet known)
     */
    public EncryptedItem(final EncryptedList<?, ?> pList,
                         final int uId) {
        super(pList, uId);
        theGenerator = new EncryptionGenerator(null);
    }

    /**
     * Constructor. This picks up the generator from the source item.
     * @param pList the list that this item is associated with
     * @param pSource the source item
     */
    public EncryptedItem(final EncryptedList<?, ?> pList,
                         final EncryptedItem pSource) {
        super(pList, pSource);
        theGenerator = pSource.theGenerator;
    }

    /**
     * Set ControlKey.
     * @param pControlKey the Control Key
     */
    protected void setControlKey(final ControlKey pControlKey) {
        setValueControlKey(pControlKey);
    }

    /**
     * Set ControlKey id.
     * @param uControlId the Control Id
     * @throws JDataException on error
     */
    protected void setControlKey(final int uControlId) throws JDataException {
        /* Store the id */
        setValueControlKey(uControlId);

        /* Look up the Control keys */
        DataSet<?> myData = ((EncryptedList<?, ?>) getList()).getData();
        ControlKeyList myKeys = myData.getControlKeys();

        /* Look up the ControlKey */
        ControlKey myControl = myKeys.findItemById(uControlId);
        if (myControl == null) {
            throw new JDataException(ExceptionClass.DATA, this, "Invalid ControlKey Id");
        }

        /* Store the ControlKey */
        setValueControlKey(myControl);
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pValue the value to set
     * @throws JDataException on error
     */
    protected void setEncryptedValue(final JDataField pField,
                                     final Object pValue) throws JDataException {
        /* Obtain the existing value */
        Object myCurrent = theValueSet.getValue(pField);

        /* Handle bad usage */
        if ((myCurrent != null) && (!EncryptedField.class.isInstance(myCurrent))) {
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());
        }

        /* Create the new encrypted value */
        EncryptedField<?> myCurr = (EncryptedField<?>) myCurrent;
        EncryptedField<?> myField = theGenerator.encryptValue(myCurr, pValue);

        /* Store the new value */
        theValueSet.setValue(pField, myField);
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pEncrypted the encrypted value to set
     * @param pClass the class of the value
     * @throws JDataException on error
     */
    protected void setEncryptedValue(final JDataField pField,
                                     final byte[] pEncrypted,
                                     final Class<?> pClass) throws JDataException {
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
    public static Difference getDifference(final EncryptedField<?> pCurr,
                                           final EncryptedField<?> pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return (pNew != null) ? Difference.Different : Difference.Identical;
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return Difference.Different;
        }

        /* Handle Standard cases */
        return pCurr.differs(pNew);
    }

    /**
     * Rebuild Links to partner data.
     * @param pData the DataSet
     */
    protected void reBuildLinks(final DataSet<?> pData) {
        ControlKeyList myKeys = pData.getControlKeys();

        /* Update to use the local copy of the ControlKeys */
        ControlKey myKey = getControlKey();
        ControlKey myNewKey = myKeys.findItemById(myKey.getId());
        setValueControlKey(myNewKey);
    }

    /**
     * Initialise security for all encrypted values.
     * @param pControl the new Control Key
     * @param pBase the base item
     * @throws JDataException on error
     */
    protected void adoptSecurity(final ControlKey pControl,
                                 final EncryptedItem pBase) throws JDataException {
        /* Set the Control Key */
        setValueControlKey(pControl);

        /* Access underlying values if they exist */
        EncryptedValueSet myBaseValues = null;
        if (pBase != null) {
            myBaseValues = pBase.getValueSet();
        }

        /* Try to adopt the underlying */
        theValueSet.adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Update security for all encrypted values.
     * @param pControl the new Control Key
     * @throws JDataException on error
     */
    protected void updateSecurity(final ControlKey pControl) throws JDataException {
        /* Ignore call if we have the same control key */
        if (pControl.equals(getControlKey())) {
            return;
        }

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
     * Encrypted DataList.
     * @param <L> the list type
     * @param <T> the item type
     */
    public abstract static class EncryptedList<L extends EncryptedList<L, T>, T extends EncryptedItem & Comparable<T>>
            extends DataList<L, T> {
        /**
         * The owning data set.
         */
        private DataSet<?> theData = null;

        /**
         * Get the owning data set.
         * @return the data set
         */
        public DataSet<?> getData() {
            return theData;
        }

        /**
         * Set the owning data set.
         * @param pData the data set
         */
        protected void setData(final DataSet<?> pData) {
            theData = pData;
        }

        /**
         * Get the active controlKey.
         * @return the active controlKey
         */
        public ControlKey getControlKey() {
            return theData.getControl().getControlKey();
        }

        /**
         * Construct an empty CORE encrypted list.
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         */
        protected EncryptedList(final Class<L> pClass,
                                final Class<T> pBaseClass,
                                final DataSet<?> pData) {
            super(pClass, pBaseClass, ListStyle.CORE);
            theData = pData;
        }

        /**
         * Construct a generic encrypted list.
         * @param pClass the class
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        public EncryptedList(final Class<L> pClass,
                             final Class<T> pBaseClass,
                             final DataSet<?> pData,
                             final ListStyle pStyle) {
            super(pClass, pBaseClass, pStyle);
            theData = pData;
            setGeneration(pData.getGeneration());
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EncryptedList(final L pSource) {
            super(pSource);
            theData = pSource.getData();
        }

        /**
         * Update Security for items in the list.
         * @param pTask the task control
         * @param pControl the control key to apply
         * @return Continue <code>true/false</code>
         * @throws JDataException on error
         */
        public boolean updateSecurity(final TaskControl<?> pTask,
                                      final ControlKey pControl) throws JDataException {
            /* Declare the new stage */
            if (!pTask.setNewStage(listName())) {
                return false;
            }

            /* Access reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the Number of items */
            if (!pTask.setNumSteps(size())) {
                return false;
            }

            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items */
            while (myIterator.hasNext()) {
                /* Ensure encryption of the item */
                T myCurr = myIterator.next();
                myCurr.updateSecurity(pControl);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Return to caller */
            return true;
        }

        /**
         * Adopt security from underlying list. If a match for the item is found in the underlying list, its
         * security is adopted. If no match is found then the security is initialised.
         * @param pTask the task control
         * @param pControl the control key to initialise from
         * @param pBase The base list to adopt from
         * @return Continue <code>true/false</code>
         * @throws JDataException on error
         */
        protected boolean adoptSecurity(final TaskControl<?> pTask,
                                        final ControlKey pControl,
                                        final EncryptedList<?, ?> pBase) throws JDataException {
            /* Declare the new stage */
            if (!pTask.setNewStage(listName())) {
                return false;
            }

            /* Access reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the Number of items */
            if (!pTask.setNumSteps(size())) {
                return false;
            }

            /* Create an iterator for our new list */
            Iterator<T> myIterator = iterator();
            Class<T> myClass = getBaseClass();

            /* Loop through this list */
            while (myIterator.hasNext()) {
                /* Locate the item in the base list */
                EncryptedItem myCurr = myIterator.next();
                EncryptedItem myBase = pBase.findItemById(myCurr.getId());

                /* Cast the items correctly */
                T mySource = (myBase == null) ? null : myClass.cast(myBase);
                T myTarget = myClass.cast(myCurr);

                /* Adopt/initialise the security */
                myTarget.adoptSecurity(pControl, mySource);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Return to caller */
            return true;
        }
    }
}
