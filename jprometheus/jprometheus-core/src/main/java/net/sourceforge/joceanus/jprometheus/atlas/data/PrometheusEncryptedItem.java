/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.atlas.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataKeySet.PrometheusDataKeySetList;
import net.sourceforge.joceanus.jprometheus.atlas.field.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.atlas.field.PrometheusFieldGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * Encrypted Data Item and List.
 * @author Tony Washer
 */
public abstract class PrometheusEncryptedItem
        extends PrometheusDataItem {
    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.ENCRYPTED_NAME.getValue(), PrometheusDataItem.FIELD_DEFS);

    /**
     * Data Key Set Field Id.
     */
    public static final MetisLetheField FIELD_KEYSET = FIELD_DEFS.declareEqualityValueField(PrometheusDataKeySet.OBJECT_NAME, MetisDataType.LINK);

    /**
     * Error message for bad usage.
     */
    public static final String ERROR_USAGE = PrometheusDataResource.ENCRYPTED_ERROR_USAGE.getValue();

    /**
     * Generator field.
     */
    private PrometheusFieldGenerator theGenerator;

    /**
     * Standard Constructor. This creates a null encryption generator. This will be overridden when
     * a DataKeySet is assigned to the item.
     * @param pList the list that this item is associated with
     * @param pId the Id of the new item (or 0 if not yet known)
     */
    protected PrometheusEncryptedItem(final PrometheusEncryptedList<?> pList,
                                      final Integer pId) {
        super(pList, pId);
        theGenerator = new PrometheusFieldGenerator(pList.getDataSet().getDataFormatter(), null);
    }

    /**
     * Copy Constructor. This picks up the generator from the source item.
     * @param pList the list that this item is associated with
     * @param pSource the source item
     */
    protected PrometheusEncryptedItem(final PrometheusEncryptedList<?> pList,
                                      final PrometheusEncryptedItem pSource) {
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
    protected PrometheusEncryptedItem(final PrometheusEncryptedList<?> pList,
                                      final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);

        /* Access dataKeySet id */
        final Integer myId = pValues.getValue(FIELD_KEYSET, Integer.class);
        if (myId != null) {
            setDataKeySet(myId);
        } else {
            theGenerator = new PrometheusFieldGenerator(pList.getDataSet().getDataFormatter(), null);
        }
    }

    @Override
    public PrometheusEncryptedValueSet getValueSet() {
        return (PrometheusEncryptedValueSet) super.getValueSet();
    }

    @Override
    public PrometheusEncryptedValueSet getOriginalValues() {
        return (PrometheusEncryptedValueSet) super.getOriginalValues();
    }

    /**
     * Get the DataKeySet for this item.
     * @return the DataKeySet
     */
    public final PrometheusDataKeySet getDataKeySet() {
        return getDataKeySet(getValueSet());
    }

    /**
     * Get the DataKeySetId for this item.
     * @return the DataKeySetId
     */
    public final Integer getDataKeySetId() {
        final PrometheusDataKeySet mySet = getDataKeySet();
        return (mySet == null)
                ? null
                : mySet.getId();
    }

    /**
     * Get the ControlKey for this item.
     * @param pValueSet the valueSet
     * @return the ControlKey
     */
    public static PrometheusDataKeySet getDataKeySet(final PrometheusEncryptedValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYSET, PrometheusDataKeySet.class);
    }

    /**
     * Set the DataKeySet for this item.
     * @param pSet the dataKeySet
     */
    private void setValueDataKeySet(final PrometheusDataKeySet pSet) {
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
    public PrometheusEncryptedList<?> getList() {
        return (PrometheusEncryptedList<?>) super.getList();
    }

    /**
     * Set DataKeySet.
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
     * @param pKeySetId the KeySet Id
     * @throws OceanusException on error
     */
    protected final void setDataKeySet(final Integer pKeySetId) throws OceanusException {
        /* Store the id */
        setValueDataKeySet(pKeySetId);

        /* Resolve the ControlKey */
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        theGenerator = getDataKeySet().getFieldGenerator();
    }

    /**
     * Set encrypted value.
     * @param pField the field to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    protected final void setEncryptedValue(final MetisLetheField pField,
                                           final Object pValue) throws OceanusException {
        /* Obtain the existing value */
        final PrometheusEncryptedValueSet myValueSet = getValueSet();
        Object myCurrent = myValueSet.getValue(pField);

        /* Handle switched usage */
        if (myCurrent != null && !(myCurrent instanceof PrometheusEncryptedPair)) {
            myCurrent = null;
        }

        /* Create the new encrypted value */
        final PrometheusEncryptedPair myCurr = (PrometheusEncryptedPair) myCurrent;
        final PrometheusEncryptedPair myField = theGenerator.encryptValue(myCurr, pValue);

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
    protected final void setEncryptedValue(final MetisLetheField pField,
                                           final byte[] pEncrypted,
                                           final Class<?> pClass) throws OceanusException {
        /* Create the new encrypted value */
        final PrometheusEncryptedPair myField = theGenerator.decryptValue(pEncrypted, pClass);

        /* Store the new value */
        getValueSet().setValue(pField, myField);
    }

    /**
     * Determine whether two ValuePair objects differ.
     * @param pCurr The current Pair
     * @param pNew The new Pair
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
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
    }

    /**
     * Adopt security for all encrypted values.
     * @param pKeySet the new KeySet
     * @param pBase the base item
     * @throws OceanusException on error
     */
    protected void adoptSecurity(final PrometheusDataKeySet pKeySet,
                                 final PrometheusEncryptedItem pBase) throws OceanusException {
        /* Set the DataKeySet */
        setValueDataKeySet(pKeySet);

        /* Access underlying values if they exist */
        final PrometheusEncryptedValueSet myBaseValues = pBase.getValueSet();

        /* Try to adopt the underlying */
        getValueSet().adoptSecurity(theGenerator, myBaseValues);
    }

    /**
     * Initialise security for all encrypted values.
     * @param pKeySet the new KeySet
     * @throws OceanusException on error
     */
    protected void initialiseSecurity(final PrometheusDataKeySet pKeySet) throws OceanusException {
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
        getValueSet().updateSecurity(theGenerator);
    }

    /**
     * Encrypted DataList.
     * @param <T> the item type
     */
    public abstract static class PrometheusEncryptedList<T extends PrometheusEncryptedItem>
            extends PrometheusDataList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(PrometheusEncryptedList.class);
        }

        /**
         * Construct an empty CORE encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pItemType the item type
         */
        protected PrometheusEncryptedList(final Class<T> pBaseClass,
                                          final PrometheusDataSet pData,
                                          final PrometheusListKey pItemType) {
            this(pBaseClass, pData, pItemType, PrometheusListStyle.CORE);
        }

        /**
         * Construct a generic encrypted list.
         * @param pBaseClass the class of the underlying object
         * @param pData the DataSet for the list
         * @param pItemType the list type
         * @param pStyle the style of the list
         */
        protected PrometheusEncryptedList(final Class<T> pBaseClass,
                                          final PrometheusDataSet pData,
                                          final PrometheusListKey pItemType,
                                          final PrometheusListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PrometheusEncryptedList(final PrometheusEncryptedList<T> pSource) {
            super(pSource);
        }

        /**
         * Get the active controlKey.
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
         * @param pReport the report
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
         * @param pReport the report
         * @param pBase The base list to adopt from
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
                final PrometheusEncryptedItem myCurr = myIterator.next();
                final PrometheusEncryptedItem myBase = pBase.findItemById(myCurr.getId());

                /* Access target correctly */
                final T myTarget = myClass.cast(myCurr);

                /* If we have a base */
                if (myBase != null) {
                    /* Access base correctly */
                    final T mySource = myClass.cast(myBase);

                    /* Obtain required KeySet */
                    PrometheusDataKeySet mySet = myBase.getDataKeySet();
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
