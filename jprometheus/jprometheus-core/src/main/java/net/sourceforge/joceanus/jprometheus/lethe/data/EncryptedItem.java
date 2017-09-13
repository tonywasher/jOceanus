/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptionGenerator;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Encrypted Data Item and List.
 * @author Tony Washer
 * @param <E> the data type enum class
 */
public abstract class EncryptedItem<E extends Enum<E>>
        extends DataItem<E> {
    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.ENCRYPTED_NAME.getValue(), DataItem.FIELD_DEFS);

    /**
     * Data Key Set Field Id.
     */
    public static final MetisField FIELD_KEYSET = FIELD_DEFS.declareEqualityValueField(DataKeySet.OBJECT_NAME, MetisDataType.LINK);

    /**
     * Error message for bad usage.
     */
    public static final String ERROR_USAGE = PrometheusDataResource.ENCRYPTED_ERROR_USAGE.getValue();

    /**
     * Generator field.
     */
    private MetisEncryptionGenerator theGenerator;

    /**
     * Standard Constructor. This creates a null encryption generator. This will be overridden when
     * a DataKeySet is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pId the Id of the new item (or 0 if not yet known)
     */
    public EncryptedItem(final EncryptedList<?, E> pList,
                         final Integer pId) {
        super(pList, pId);
        theGenerator = new MetisEncryptionGenerator(null);
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
     * Values Constructor. This creates a null encryption generator. This will be overridden when a
     * ControlKey is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pValues the data values
     * @throws OceanusException on error
     */
    public EncryptedItem(final EncryptedList<?, E> pList,
                         final DataValues<E> pValues) throws OceanusException {
        super(pList, pValues);

        /* Access dataKeySet id */
        final Integer myId = pValues.getValue(FIELD_KEYSET, Integer.class);
        if (myId != null) {
            setDataKeySet(myId);
        } else {
            theGenerator = new MetisEncryptionGenerator(null);
        }
    }

    @Override
    public MetisEncryptedValueSet getValueSet() {
        return (MetisEncryptedValueSet) super.getValueSet();
    }

    @Override
    public MetisEncryptedValueSet getOriginalValues() {
        return (MetisEncryptedValueSet) super.getOriginalValues();
    }

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
        final DataKeySet mySet = getDataKeySet();
        return (mySet == null)
                               ? null
                               : mySet.getId();
    }

    /**
     * Get the ControlKey for this item.
     * @param pValueSet the valueSet
     * @return the ControlKey
     */
    public static DataKeySet getDataKeySet(final MetisEncryptedValueSet pValueSet) {
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
     * @throws OceanusException on error
     */
    protected final void setDataKeySet(final Integer pKeySetId) throws OceanusException {
        /* Store the id */
        setValueDataKeySet(pKeySetId);

        /* Resolve the ControlKey */
        final DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        theGenerator = getDataKeySet().getFieldGenerator();
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisField pField,
                                           final Object pValue) throws OceanusException {
        /* Obtain the existing value */
        final MetisEncryptedValueSet myValueSet = getValueSet();
        Object myCurrent = myValueSet.getValue(pField);

        /* Handle switched usage */
        if ((myCurrent != null) && (!MetisEncryptedField.class.isInstance(myCurrent))) {
            myCurrent = null;
        }

        /* Create the new encrypted value */
        final MetisEncryptedField<?> myCurr = (MetisEncryptedField<?>) myCurrent;
        final MetisEncryptedField<?> myField = theGenerator.encryptValue(myCurr, pValue);

        /* Store the new value */
        myValueSet.setValue(pField, myField);
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pEncrypted the encrypted value to set
     * @param pClass the class of the value
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisField pField,
                                           final byte[] pEncrypted,
                                           final Class<?> pClass) throws OceanusException {
        /* Create the new encrypted value */
        final MetisEncryptedField<?> myField = theGenerator.decryptValue(pEncrypted, pClass);

        /* Store the new value */
        getValueSet().setValue(pField, myField);
    }

    /**
     * Determine whether two ValuePair objects differ.
     * @param pCurr The current Pair
     * @param pNew The new Pair
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static MetisDataDifference getDifference(final MetisEncryptedField<?> pCurr,
                                                    final MetisEncryptedField<?> pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return (pNew != null)
                                  ? MetisDataDifference.DIFFERENT
                                  : MetisDataDifference.IDENTICAL;
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return MetisDataDifference.DIFFERENT;
        }

        /* Handle Standard cases */
        return pCurr.differs(pNew);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
    }

    /**
     * Adopt security for all encrypted values.
     * @param pKeySet the new KeySet
     * @param pBase the base item
     * @throws OceanusException on error
     */
    protected void adoptSecurity(final DataKeySet pKeySet,
                                 final EncryptedItem<E> pBase) throws OceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Access underlying values if they exist */
        final MetisEncryptedValueSet myBaseValues = pBase.getValueSet();

        /* Try to adopt the underlying */
        getValueSet().adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Initialise security for all encrypted values.
     * @param pKeySet the new KeySet
     * @throws OceanusException on error
     */
    protected void initialiseSecurity(final DataKeySet pKeySet) throws OceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Initialise security */
        getValueSet().adoptSecurity(theGenerator, null);
    }

    /**
     * Update security for all encrypted values.
     * @param pKeySet the new KeySet
     * @throws OceanusException on error
     */
    protected void updateSecurity(final DataKeySet pKeySet) throws OceanusException {
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
         * Get the active controlKey.
         * @return the active controlKey
         */
        private ControlKey getControlKey() {
            final ControlData myControl = getDataSet().getControl();
            return (myControl == null)
                                       ? null
                                       : myControl.getControlKey();
        }

        /**
         * Obtain the DataKeySet to use.
         * @return the DataKeySet
         */
        private DataKeySet getNextDataKeySet() {
            final ControlKey myKey = getControlKey();
            return (myKey == null)
                                   ? null
                                   : myKey.getNextDataKeySet();
        }

        /**
         * Update Security for items in the list.
         * @param pReport the report
         * @param pControl the control key to apply
         * @throws OceanusException on error
         */
        public void updateSecurity(final MetisThreadStatusReport pReport,
                                   final ControlKey pControl) throws OceanusException {
            /* Declare the new stage */
            pReport.setNewStage(listName());

            /* Count the Number of items */
            pReport.setNumSteps(size());

            /* Loop through the items */
            final Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                final T myCurr = myIterator.next();

                /* Only update if we are using the wrong controlKey */
                if (!pControl.equals(myCurr.getDataKeySet().getControlKey())) {
                    /* Update the security */
                    myCurr.updateSecurity(pControl.getNextDataKeySet());
                }

                /* Report the progress */
                pReport.setNextStep();
            }
        }

        /**
         * Adopt security from underlying list. If a match for the item is found in the underlying
         * list, its security is adopted. If no match is found then the security is initialised.
         * @param pReport the report
         * @param pBase The base list to adopt from
         * @throws OceanusException on error
         */
        protected void adoptSecurity(final MetisThreadStatusReport pReport,
                                     final EncryptedList<?, E> pBase) throws OceanusException {
            /* Declare the new stage */
            pReport.setNewStage(listName());

            /* Count the Number of items */
            pReport.setNumSteps(size());

            /* Obtain DataKeySet list */
            final DataSet<?, ?> myData = getDataSet();
            final DataKeySetList mySets = myData.getDataKeySets();

            /* Create an iterator for our new list */
            final Iterator<T> myIterator = iterator();
            final Class<T> myClass = getBaseClass();

            /* Loop through this list */
            while (myIterator.hasNext()) {
                /* Locate the item in the base list */
                final EncryptedItem<E> myCurr = myIterator.next();
                final EncryptedItem<E> myBase = pBase.findItemById(myCurr.getId());

                /* Access target correctly */
                final T myTarget = myClass.cast(myCurr);

                /* If we have a base */
                if (myBase != null) {
                    /* Access base correctly */
                    final T mySource = myClass.cast(myBase);

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
                pReport.setNextStep();
            }
        }
    }
}
