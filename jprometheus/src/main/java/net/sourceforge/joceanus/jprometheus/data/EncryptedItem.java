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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.EncryptionGenerator;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jprometheus.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Encrypted Data Item and List.
 * @author Tony Washer
 * @param <E> the data type enum class
 */
public abstract class EncryptedItem<E extends Enum<E>>
        extends DataItem<E> {
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
     * Data Key Set Field Id.
     */
    public static final JDataField FIELD_KEYSET = FIELD_DEFS.declareEqualityValueField(DataKeySet.OBJECT_NAME);

    /**
     * Error message for bad usage.
     */
    public static final String ERROR_USAGE = NLS_BUNDLE.getString("ErrorUsage");

    /**
     * Generator field.
     */
    private EncryptionGenerator theGenerator = null;

    /**
     * Get the DataKeySet for this item.
     * @return the DataKeySet
     */
    public final DataKeySet getDataKeySet() {
        return getDataKeySet(getValueSet());
    }

    /**
     * Get the DataKeySetId for this item.
     * @return the DataKeySetId
     */
    public final Integer getDataKeySetId() {
        DataKeySet mySet = getDataKeySet();
        return (mySet == null)
                              ? null
                              : mySet.getId();
    }

    /**
     * Get the ControlKey for this item.
     * @param pValueSet the valueSet
     * @return the ControlKey
     */
    public static DataKeySet getDataKeySet(final EncryptedValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYSET, DataKeySet.class);
    }

    /**
     * Set the DataKeySet for this item.
     * @param pSet the dataKeySet
     */
    private void setValueDataKeySet(final DataKeySet pSet) {
        getValueSet().setValue(FIELD_KEYSET, pSet);
        if (pSet != null) {
            theGenerator = pSet.getFieldGenerator();
        }
    }

    /**
     * Set the KeySet id for this item.
     * @param pId the keySet id
     */
    private void setValueDataKeySet(final Integer pId) {
        getValueSet().setValue(FIELD_KEYSET, pId);
    }

    @Override
    public EncryptedList<?, E> getList() {
        return (EncryptedList<?, E>) super.getList();
    }

    /**
     * Standard Constructor. This creates a null encryption generator. This will be overridden when a DataKeySet is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pId the Id of the new item (or 0 if not yet known)
     */
    public EncryptedItem(final EncryptedList<?, E> pList,
                         final Integer pId) {
        super(pList, pId);
        theGenerator = new EncryptionGenerator(null);
    }

    /**
     * Copy Constructor. This picks up the generator from the source item.
     * @param pList the list that this item is associated with
     * @param pSource the source item
     */
    public EncryptedItem(final EncryptedList<?, E> pList,
                         final EncryptedItem<E> pSource) {
        super(pList, pSource);
        theGenerator = pSource.theGenerator;
    }

    /**
     * Values Constructor. This creates a null encryption generator. This will be overridden when a ControlKey is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pValues the data values
     * @throws JOceanusException on error
     */
    public EncryptedItem(final EncryptedList<?, E> pList,
                         final DataValues<E> pValues) throws JOceanusException {
        super(pList, pValues);

        /* Access dataKeySet id */
        Integer myId = pValues.getValue(FIELD_KEYSET, Integer.class);
        if (myId != null) {
            setDataKeySet(myId);
        } else {
            theGenerator = new EncryptionGenerator(null);
        }
    }

    /**
     * Set DataKeySet.
     * @param pKeySet the DataKeySet
     */
    protected final void setDataKeySet(final DataKeySet pKeySet) {
        setValueDataKeySet(pKeySet);
    }

    /**
     * Set Next DataKeySet.
     */
    protected final void setNextDataKeySet() {
        setDataKeySet(getList().getNextDataKeySet());
    }

    /**
     * Set DataKeySet id.
     * @param pKeySetId the KeySet Id
     * @throws JOceanusException on error
     */
    protected final void setDataKeySet(final Integer pKeySetId) throws JOceanusException {
        /* Store the id */
        setValueDataKeySet(pKeySetId);

        /* Resolve the ControlKey */
        DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        theGenerator = getDataKeySet().getFieldGenerator();
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    protected final void setEncryptedValue(final JDataField pField,
                                           final Object pValue) throws JOceanusException {
        /* Obtain the existing value */
        EncryptedValueSet myValueSet = getValueSet();
        Object myCurrent = myValueSet.getValue(pField);

        /* Handle switched usage */
        if ((myCurrent != null) && (!EncryptedField.class.isInstance(myCurrent))) {
            myCurrent = null;
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
    protected final void setEncryptedValue(final JDataField pField,
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
        /* Resolve the ControlKey */
        DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
    }

    /**
     * Adopt security for all encrypted values.
     * @param pKeySet the new KeySet
     * @param pBase the base item
     * @throws JOceanusException on error
     */
    protected void adoptSecurity(final DataKeySet pKeySet,
                                 final EncryptedItem<E> pBase) throws JOceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Access underlying values if they exist */
        EncryptedValueSet myBaseValues = pBase.getValueSet();

        /* Try to adopt the underlying */
        getValueSet().adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Initialise security for all encrypted values.
     * @param pKeySet the new KeySet
     * @throws JOceanusException on error
     */
    protected void initialiseSecurity(final DataKeySet pKeySet) throws JOceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Initialise security */
        getValueSet().adoptSecurity(theGenerator, null);
    }

    /**
     * Update security for all encrypted values.
     * @param pKeySet the new KeySet
     * @throws JOceanusException on error
     */
    protected void updateSecurity(final DataKeySet pKeySet) throws JOceanusException {
        /* Ignore call if we have the same keySet */
        if (pKeySet.equals(getDataKeySet())) {
            return;
        }

        /* Store the current detail into history */
        pushHistory();

        /* Set the DataKeySet */
        setDataKeySet(pKeySet);

        /* Update all elements */
        getValueSet().updateSecurity(theGenerator);
    }

    /**
     * Encrypted DataList.
     * @param <T> the item type
     * @param <E> the data type enum class
     */
    public abstract static class EncryptedList<T extends EncryptedItem<E> & Comparable<? super T>, E extends Enum<E>>
            extends DataList<T, E> {
        /**
         * Get the active controlKey.
         * @return the active controlKey
         */
        private ControlKey getControlKey() {
            ControlData myControl = getDataSet().getControl();
            return (myControl == null)
                                      ? null
                                      : myControl.getControlKey();
        }

        /**
         * Obtain the DataKeySet to use.
         * @return the DataKeySet
         */
        private DataKeySet getNextDataKeySet() {
            ControlKey myKey = getControlKey();
            return (myKey == null)
                                  ? null
                                  : myKey.getNextDataKeySet();
        }

        /**
         * Construct an empty CORE encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pItemType the item type
         */
        protected EncryptedList(final Class<T> pBaseClass,
                                final DataSet<?, ?> pData,
                                final E pItemType) {
            this(pBaseClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Construct a generic encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pItemType the list type
         * @param pStyle the style of the list
         */
        public EncryptedList(final Class<T> pBaseClass,
                             final DataSet<?, ?> pData,
                             final E pItemType,
                             final ListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EncryptedList(final EncryptedList<T, E> pSource) {
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
                myCurr.updateSecurity(pControl.getNextDataKeySet());

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
                                        final EncryptedList<?, E> pBase) throws JOceanusException {
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

            /* Obtain DataKeySet list */
            DataSet<?, ?> myData = getDataSet();
            DataKeySetList mySets = myData.getDataKeySets();

            /* Create an iterator for our new list */
            Iterator<T> myIterator = iterator();
            Class<T> myClass = getBaseClass();

            /* Loop through this list */
            while (myIterator.hasNext()) {
                /* Locate the item in the base list */
                EncryptedItem<E> myCurr = myIterator.next();
                EncryptedItem<E> myBase = pBase.findItemById(myCurr.getId());

                /* Access target correctly */
                T myTarget = myClass.cast(myCurr);

                /* If we have a base */
                if (myBase != null) {
                    /* Access base correctly */
                    T mySource = myClass.cast(myBase);

                    /* Obtain required KeySet */
                    DataKeySet mySet = myBase.getDataKeySet();
                    mySet = mySets.findItemById(mySet.getId());

                    /* Adopt the security */
                    myTarget.adoptSecurity(mySet, mySource);
                } else {
                    /* Initialise the security */
                    myTarget.initialiseSecurity(getNextDataKeySet());
                }

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
