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

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jdatamanager.EncryptedValueSet;
import net.sourceforge.joceanus.jdatamanager.EncryptionGenerator;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Encrypted Data Item and List.
 * @author Tony Washer
 */
public abstract class EncryptedItem
        extends DataItem {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EncryptedItem.class.getName());

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataItem.FIELD_DEFS);

    @Override
    public EncryptedValueSet getValueSet() {
        return (EncryptedValueSet) super.getValueSet();
    }

    @Override
    public EncryptedValueSet getOriginalValues() {
        return (EncryptedValueSet) super.getOriginalValues();
    }

    /**
     * Control Key Field Id.
     */
    public static final JDataField FIELD_CONTROL = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataKey"));

    /**
     * Error message for bad usage.
     */
    public static final String ERROR_USAGE = NLS_BUNDLE.getString("ErrorUsage");

    /**
     * Generator field.
     */
    private EncryptionGenerator theGenerator = null;

    /**
     * Get the ControlKey for this item.
     * @return the ControlKey
     */
    public ControlKey getControlKey() {
        return getControlKey(getValueSet());
    }

    /**
     * Get the ControlKeyId for this item.
     * @return the ControlKeyId
     */
    public Integer getControlKeyId() {
        ControlKey myKey = getControlKey();
        return (myKey == null)
                ? null
                : myKey.getId();
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
        getValueSet().setValue(FIELD_CONTROL, pKey);
        if (pKey != null) {
            theGenerator = pKey.getFieldGenerator();
        }
    }

    /**
     * Set the control Key id for this item.
     * @param pId the control key id
     */
    private void setValueControlKey(final Integer pId) {
        getValueSet().setValue(FIELD_CONTROL, pId);
    }

    /**
     * Standard Constructor. This creates a null encryption generator. This will be overridden when a ControlKey is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pId the Id of the new item (or 0 if not yet known)
     */
    public EncryptedItem(final EncryptedList<?> pList,
                         final Integer pId) {
        super(pList, pId);
        theGenerator = new EncryptionGenerator(null);
    }

    /**
     * Copy Constructor. This picks up the generator from the source item.
     * @param pList the list that this item is associated with
     * @param pSource the source item
     */
    public EncryptedItem(final EncryptedList<?> pList,
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
     * @param pControlId the Control Id
     * @throws JOceanusException on error
     */
    protected void setControlKey(final Integer pControlId) throws JOceanusException {
        /* Store the id */
        setValueControlKey(pControlId);

        /* Look up the Control keys */
        DataSet<?, ?> myData = getDataSet();
        ControlKeyList myKeys = myData.getControlKeys();

        /* Look up the ControlKey */
        ControlKey myControl = myKeys.findItemById(pControlId);
        if (myControl == null) {
            addError(ERROR_UNKNOWN, FIELD_CONTROL);
            throw new JPrometheusDataException(this, ERROR_RESOLUTION);
        }

        /* Store the ControlKey */
        setValueControlKey(myControl);
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    protected void setEncryptedValue(final JDataField pField,
                                     final Object pValue) throws JOceanusException {
        /* Obtain the existing value */
        EncryptedValueSet myValueSet = getValueSet();
        Object myCurrent = myValueSet.getValue(pField);

        /* Handle bad usage */
        if ((myCurrent != null)
            && (!EncryptedField.class.isInstance(myCurrent))) {
            throw new IllegalArgumentException(ERROR_USAGE
                                               + " "
                                               + pField.getName());
        }

        /* Create the new encrypted value */
        EncryptedField<?> myCurr = (EncryptedField<?>) myCurrent;
        EncryptedField<?> myField = theGenerator.encryptValue(myCurr, pValue);

        /* Store the new value */
        myValueSet.setValue(pField, myField);
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pEncrypted the encrypted value to set
     * @param pClass the class of the value
     * @throws JOceanusException on error
     */
    protected void setEncryptedValue(final JDataField pField,
                                     final byte[] pEncrypted,
                                     final Class<?> pClass) throws JOceanusException {
        /* Create the new encrypted value */
        EncryptedField<?> myField = theGenerator.decryptValue(pEncrypted, pClass);

        /* Store the new value */
        getValueSet().setValue(pField, myField);
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
            return (pNew != null)
                    ? Difference.DIFFERENT
                    : Difference.IDENTICAL;
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return Difference.DIFFERENT;
        }

        /* Handle Standard cases */
        return pCurr.differs(pNew);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        DataSet<?, ?> myData = getDataSet();
        ControlKeyList myKeys = myData.getControlKeys();
        ValueSet myValues = getValueSet();

        /* Adjust ControlKey */
        Object myKey = myValues.getValue(FIELD_CONTROL);
        if (myKey instanceof ControlKey) {
            myKey = ((ControlKey) myKey).getId();
        }
        if (myKey instanceof Integer) {
            setValueControlKey(myKeys.findItemById((Integer) myKey));
        }
    }

    /**
     * Initialise security for all encrypted values.
     * @param pControl the new Control Key
     * @param pBase the base item
     * @throws JOceanusException on error
     */
    protected void adoptSecurity(final ControlKey pControl,
                                 final EncryptedItem pBase) throws JOceanusException {
        /* Set the Control Key */
        setValueControlKey(pControl);

        /* Access underlying values if they exist */
        EncryptedValueSet myBaseValues = null;
        if (pBase != null) {
            myBaseValues = pBase.getValueSet();
        }

        /* Try to adopt the underlying */
        getValueSet().adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Update security for all encrypted values.
     * @param pControl the new Control Key
     * @throws JOceanusException on error
     */
    protected void updateSecurity(final ControlKey pControl) throws JOceanusException {
        /* Ignore call if we have the same control key */
        if (pControl.equals(getControlKey())) {
            return;
        }

        /* Store the current detail into history */
        pushHistory();

        /* Set the Control Key */
        setControlKey(pControl);

        /* Update all elements */
        getValueSet().updateSecurity(theGenerator);
    }

    /**
     * Encrypted DataList.
     * @param <T> the item type
     */
    public abstract static class EncryptedList<T extends EncryptedItem & Comparable<? super T>>
            extends DataList<T> {
        /**
         * Get the active controlKey.
         * @return the active controlKey
         */
        public ControlKey getControlKey() {
            return getDataSet().getControl().getControlKey();
        }

        /**
         * Construct an empty CORE encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         */
        protected EncryptedList(final Class<T> pBaseClass,
                                final DataSet<?, ?> pData) {
            super(pBaseClass, pData, ListStyle.CORE);
        }

        /**
         * Construct a generic encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        public EncryptedList(final Class<T> pBaseClass,
                             final DataSet<?, ?> pData,
                             final ListStyle pStyle) {
            super(pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EncryptedList(final EncryptedList<T> pSource) {
            super(pSource);
        }

        /**
         * Update Security for items in the list.
         * @param pTask the task control
         * @param pControl the control key to apply
         * @return Continue <code>true/false</code>
         * @throws JOceanusException on error
         */
        public boolean updateSecurity(final TaskControl<?> pTask,
                                      final ControlKey pControl) throws JOceanusException {
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
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Return to caller */
            return true;
        }

        /**
         * Adopt security from underlying list. If a match for the item is found in the underlying list, its security is adopted. If no match is found then the
         * security is initialised.
         * @param pTask the task control
         * @param pControl the control key to initialise from
         * @param pBase The base list to adopt from
         * @return Continue <code>true/false</code>
         * @throws JOceanusException on error
         */
        protected boolean adoptSecurity(final TaskControl<?> pTask,
                                        final ControlKey pControl,
                                        final EncryptedList<?> pBase) throws JOceanusException {
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
                T mySource = (myBase == null)
                        ? null
                        : myClass.cast(myBase);
                T myTarget = myClass.cast(myCurr);

                /* Adopt/initialise the security */
                myTarget.adoptSecurity(pControl, mySource);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Return to caller */
            return true;
        }
    }
}
