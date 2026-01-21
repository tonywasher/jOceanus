/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.data;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionedSet;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataKeySet.PrometheusDataKeySetList;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.util.Iterator;

/**
 * Encrypted Data Item and List.
 *
 * @author Tony Washer
 */
public abstract class PrometheusEncryptedDataItem
        extends PrometheusDataItem {
    /**
     * Null Encryptor.
     */
    private static final PrometheusEncryptor NULL_ENCRYPTOR = new PrometheusEncryptor(new OceanusDataFormatter(), null);

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<PrometheusEncryptedDataItem> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(PrometheusEncryptedDataItem.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(PrometheusCryptographyDataType.DATAKEYSET);
    }

    /**
     * Standard Constructor. This creates a null encryption generator. This will be overridden when
     * a DataKeySet is assigned to the item.
     *
     * @param pList the list that this item is associated with
     * @param pId   the Id of the new item (or 0 if not yet known)
     */
    protected PrometheusEncryptedDataItem(final PrometheusEncryptedList<?> pList,
                                          final Integer pId) {
        super(pList, pId);
    }

    /**
     * Copy Constructor. This picks up the generator from the source item.
     *
     * @param pList   the list that this item is associated with
     * @param pSource the source item
     */
    protected PrometheusEncryptedDataItem(final PrometheusEncryptedList<?> pList,
                                          final PrometheusEncryptedDataItem pSource) {
        super(pList, pSource);
    }

    /**
     * Values Constructor. This creates a null encryption generator. This will be overridden when a
     * ControlKey is assigned to the item.
     *
     * @param pList   the list that this item is associated with
     * @param pValues the data values
     * @throws OceanusException on error
     */
    protected PrometheusEncryptedDataItem(final PrometheusEncryptedList<?> pList,
                                          final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);

        /* Access dataKeySet id */
        final Integer myId = pValues.getValue(PrometheusCryptographyDataType.DATAKEYSET, Integer.class);
        if (myId != null) {
            setDataKeySet(myId);
        }
    }

    @Override
    protected PrometheusEncryptedValues newVersionValues() {
        return new PrometheusEncryptedValues(this);
    }

    @Override
    public PrometheusEncryptedValues getValues() {
        return (PrometheusEncryptedValues) super.getValues();
    }

    @Override
    public PrometheusEncryptedValues getOriginalValues() {
        return (PrometheusEncryptedValues) super.getOriginalValues();
    }

    /**
     * Get the DataKeySet for this item.
     *
     * @return the DataKeySet
     */
    public final PrometheusDataKeySet getDataKeySet() {
        return getValues().getValue(PrometheusCryptographyDataType.DATAKEYSET, PrometheusDataKeySet.class);
    }

    /**
     * Get the DataKeySetId for this item.
     *
     * @return the DataKeySetId
     */
    public final Integer getDataKeySetId() {
        final PrometheusDataKeySet mySet = getDataKeySet();
        return (mySet == null)
                ? null
                : mySet.getIndexedId();
    }

    /**
     * Set the DataKeySet for this item.
     *
     * @param pSet the dataKeySet
     */
    private void setValueDataKeySet(final PrometheusDataKeySet pSet) {
        getValues().setUncheckedValue(PrometheusCryptographyDataType.DATAKEYSET, pSet);
    }

    /**
     * Set the KeySet id for this item.
     *
     * @param pId the keySet id
     */
    private void setValueDataKeySet(final Integer pId) {
        getValues().setUncheckedValue(PrometheusCryptographyDataType.DATAKEYSET, pId);
    }

    /**
     * Get the Encryptor.
     *
     * @return the encryptor
     */
    public PrometheusEncryptor getEncryptor() {
        final PrometheusDataKeySet myKeySet = getDataKeySet();
        return myKeySet == null ? NULL_ENCRYPTOR : myKeySet.getEncryptor();
    }

    @Override
    public PrometheusEncryptedList<?> getList() {
        return (PrometheusEncryptedList<?>) super.getList();
    }

    /**
     * Set DataKeySet.
     *
     * @param pKeySet the DataKeySet
     */
    protected final void setDataKeySet(final PrometheusDataKeySet pKeySet) {
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
     *
     * @param pKeySetId the KeySet Id
     * @throws OceanusException on error
     */
    protected final void setDataKeySet(final Integer pKeySetId) throws OceanusException {
        /* Store the id */
        setValueDataKeySet(pKeySetId);

        /* Resolve the ControlKey */
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(PrometheusCryptographyDataType.DATAKEYSET, myData.getDataKeySets());
    }

    /**
     * Set encrypted value.
     *
     * @param pFieldId the fieldId to set
     * @param pValue   the value to set
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisDataFieldId pFieldId,
                                           final Object pValue) throws OceanusException {
        /* Obtain the existing value */
        final MetisFieldDef myFieldDef = getDataFieldSet().getField(pFieldId);
        final PrometheusEncryptedValues myValueSet = getValues();
        Object myCurrent = myValueSet.getValue(myFieldDef);

        /* Handle switched usage */
        if (myCurrent != null && !(myCurrent instanceof PrometheusEncryptedPair)) {
            myCurrent = null;
        }

        /* Create the new encrypted value */
        final PrometheusEncryptedPair myCurr = (PrometheusEncryptedPair) myCurrent;
        final PrometheusEncryptedPair myField = getEncryptor().encryptValue(myCurr, pValue);

        /* Store the new value */
        myValueSet.setUncheckedValue(myFieldDef, myField);
    }

    /**
     * Set encrypted value.
     *
     * @param pFieldId   the fieldId to set
     * @param pEncrypted the encrypted value to set
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisDataFieldId pFieldId,
                                           final byte[] pEncrypted) throws OceanusException {
        /* Create the new encrypted value */
        final MetisFieldDef myFieldDef = getDataFieldSet().getField(pFieldId);
        final PrometheusEncryptedPair myField = getEncryptor().decryptValue(pEncrypted, myFieldDef);

        /* Store the new value */
        getValues().setValue(myFieldDef, myField);
    }

    /**
     * Set encrypted value.
     *
     * @param pFieldId   the fieldId to set
     * @param pEncrypted the encrypted value to set
     * @param pClazz     the class to decrypt to
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisDataFieldId pFieldId,
                                           final byte[] pEncrypted,
                                           final Class<?> pClazz) throws OceanusException {
        /* Create the new encrypted value */
        final MetisFieldDef myFieldDef = getDataFieldSet().getField(pFieldId);
        final PrometheusEncryptedPair myField = getEncryptor().decryptValue(pEncrypted, pClazz);

        /* Store the new value */
        getValues().setUncheckedValue(myFieldDef, myField);
    }

    /**
     * Determine whether two ValuePair objects differ.
     *
     * @param pCurr The current Pair
     * @param pNew  The new Pair
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static MetisDataDifference getDifference(final PrometheusEncryptedPair pCurr,
                                                    final PrometheusEncryptedPair pNew) {
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
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(PrometheusCryptographyDataType.DATAKEYSET, myData.getDataKeySets());
    }

    /**
     * Adopt security for all encrypted values.
     *
     * @param pKeySet the new KeySet
     * @param pBase   the base item
     * @throws OceanusException on error
     */
    protected void adoptSecurity(final PrometheusDataKeySet pKeySet,
                                 final PrometheusEncryptedDataItem pBase) throws OceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Access underlying values if they exist */
        final PrometheusEncryptedValues myBaseValues = pBase.getValues();

        /* Try to adopt the underlying */
        getValues().adoptSecurity(myBaseValues);
    }

    /**
     * Initialise security for all encrypted values.
     *
     * @param pKeySet the new KeySet
     * @throws OceanusException on error
     */
    protected void initialiseSecurity(final PrometheusDataKeySet pKeySet) throws OceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Initialise security */
        getValues().adoptSecurity(null);
    }

    /**
     * Update security for all encrypted values.
     *
     * @param pKeySet the new KeySet
     * @throws OceanusException on error
     */
    protected void updateSecurity(final PrometheusDataKeySet pKeySet) throws OceanusException {
        /* Ignore call if we have the same keySet */
        if (pKeySet.equals(getDataKeySet())) {
            return;
        }

        /* Store the current detail into history */
        pushHistory();

        /* Set the DataKeySet */
        setDataKeySet(pKeySet);

        /* Update all elements */
        getValues().updateSecurity();
    }

    /**
     * Encrypted DataList.
     *
     * @param <T> the item type
     */
    public abstract static class PrometheusEncryptedList<T extends PrometheusEncryptedDataItem>
            extends PrometheusDataList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(PrometheusEncryptedList.class);
        }

        /**
         * Construct an empty CORE encrypted list.
         *
         * @param pBaseClass the class of the underlying object
         * @param pData      the DataSet for the list
         * @param pItemType  the item type
         */
        protected PrometheusEncryptedList(final Class<T> pBaseClass,
                                          final PrometheusDataSet pData,
                                          final MetisListKey pItemType) {
            this(pBaseClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Construct a generic encrypted list.
         *
         * @param pBaseClass the class of the underlying object
         * @param pData      the DataSet for the list
         * @param pItemType  the list type
         * @param pStyle     the style of the list
         */
        protected PrometheusEncryptedList(final Class<T> pBaseClass,
                                          final PrometheusDataSet pData,
                                          final MetisListKey pItemType,
                                          final PrometheusListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        protected PrometheusEncryptedList(final PrometheusEncryptedList<T> pSource) {
            super(pSource);
        }

        /**
         * Get the active controlKey.
         *
         * @return the active controlKey
         */
        private PrometheusControlKey getControlKey() {
            final PrometheusControlData myControl = getDataSet().getControl();
            return (myControl == null)
                    ? null
                    : myControl.getControlKey();
        }

        /**
         * Obtain the DataKeySet to use.
         *
         * @return the DataKeySet
         */
        private PrometheusDataKeySet getNextDataKeySet() {
            final PrometheusControlKey myKey = getControlKey();
            return (myKey == null)
                    ? null
                    : myKey.getNextDataKeySet();
        }

        /**
         * Update Security for items in the list.
         *
         * @param pReport  the report
         * @param pControl the control key to apply
         * @throws OceanusException on error
         */
        public void updateSecurity(final TethysUIThreadStatusReport pReport,
                                   final PrometheusControlKey pControl) throws OceanusException {
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
         *
         * @param pReport the report
         * @param pBase   The base list to adopt from
         * @throws OceanusException on error
         */
        protected void adoptSecurity(final TethysUIThreadStatusReport pReport,
                                     final PrometheusEncryptedList<?> pBase) throws OceanusException {
            /* Declare the new stage */
            pReport.setNewStage(listName());

            /* Count the Number of items */
            pReport.setNumSteps(size());

            /* Obtain DataKeySet list */
            final PrometheusDataSet myData = getDataSet();
            final PrometheusDataKeySetList mySets = myData.getDataKeySets();

            /* Create an iterator for our new list */
            final Iterator<T> myIterator = iterator();
            final Class<T> myClass = getBaseClass();

            /* Loop through this list */
            while (myIterator.hasNext()) {
                /* Locate the item in the base list */
                final PrometheusEncryptedDataItem myCurr = myIterator.next();
                final PrometheusEncryptedDataItem myBase = pBase.findItemById(myCurr.getIndexedId());

                /* Access target correctly */
                final T myTarget = myClass.cast(myCurr);

                /* If we have a base */
                if (myBase != null) {
                    /* Access base correctly */
                    final T mySource = myClass.cast(myBase);

                    /* Obtain required KeySet */
                    PrometheusDataKeySet mySet = myBase.getDataKeySet();
                    mySet = mySets.findItemById(mySet.getIndexedId());

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
