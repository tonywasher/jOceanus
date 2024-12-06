/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataKeySet.PrometheusDataKeySetList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList.PrometheusListStyle;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * ControlKeySet definition and list. The controlKeySet secures a set of dataKeySets.
 * @author Tony Washer
 */
public class PrometheusControlKeySet
        extends PrometheusDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = PrometheusCryptographyDataType.CONTROLKEYSET.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = PrometheusCryptographyDataType.CONTROLKEYSET.getListName();

    /**
     * KeySetWrapLength.
     */
    public static final int WRAPLEN = GordianUtilities.getMaximumKeySetWrapLength();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<PrometheusControlKeySet> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(PrometheusControlKeySet.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(PrometheusCryptographyDataType.CONTROLKEY);
        FIELD_DEFS.declareByteArrayField(PrometheusDataResource.KEYSET_KEYSETDEF, WRAPLEN);
        FIELD_DEFS.declareDerivedVersionedField(PrometheusDataResource.KEYSET_KEYSET);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAKEYSET_LIST, PrometheusControlKeySet::getDataKeySets);
    }

    /**
     * The Security Factory.
     */
    private GordianFactory theSecurityFactory;

    /**
     * The DataKeySetCache.
     */
    private DataKeySetCache theKeySetCache = new DataKeySetCache();

    /**
     * Copy Constructor.
     * @param pList the list the copy belongs to
     * @param pSource The Key to copy
     */
    protected PrometheusControlKeySet(final PrometheusControlKeySetList pList,
                                      final PrometheusControlKeySet pSource) {
        /* Set standard values */
        super(pList, pSource);

        /* Switch on the LinkStyle */
        if (Objects.requireNonNull(getStyle()) == PrometheusListStyle.CLONE) {
            theSecurityFactory = pSource.theSecurityFactory;
            final GordianKeySet myKeySet = pSource.getKeySet();
            setValueKeySet(myKeySet);
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private PrometheusControlKeySet(final PrometheusControlKeySetList pList,
                                    final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the DataSet */
        final PrometheusDataSet myData = getDataSet();

        /* Store the ControlKey */
        Object myValue = pValues.getValue(PrometheusCryptographyDataType.CONTROLKEY);
        if (myValue instanceof Integer) {
            /* Store the integer */
            final Integer myInt = (Integer) myValue;
            setValueControlKey(myInt);

            /* Resolve the ControlKey */
            resolveDataLink(PrometheusCryptographyDataType.CONTROLKEY, myData.getControlKeys());
        } else if (myValue instanceof PrometheusControlKey) {
            /* Store the controlKey */
            setValueControlKey((PrometheusControlKey) myValue);
        }

        /* Access the controlKey */
        final PrometheusControlKey myControl = getControlKey();
        theSecurityFactory = myControl.getFactoryLock().getFactory();

        /* Store the WrappedKeySetDef */
        myValue = pValues.getValue(PrometheusDataResource.KEYSET_KEYSETDEF);
        if (myValue instanceof byte[]) {
            setValueSecuredKeySetDef((byte[]) myValue);
        }

        /* Store/Resolve the keySet */
        myValue = pValues.getValue(PrometheusDataResource.KEYSET_KEYSET);
        if (myValue instanceof GordianKeySet) {
            setValueKeySet((GordianKeySet) myValue);
        } else if (getSecuredKeySetDef() != null) {
            final GordianKeySet myKeySet = theSecurityFactory.getEmbeddedKeySet().deriveKeySet(getSecuredKeySetDef());
            setValueKeySet(myKeySet);
        }

        /* Register the DataKeySet */
        myControl.registerControlKeySet(this);
    }

    /**
     * Constructor for a new DataKeySet.
     * @param pList the list to which to add the keySet to
     * @param pControlKey the control key
     * @throws OceanusException on error
     */
    protected PrometheusControlKeySet(final PrometheusControlKeySetList pList,
                                      final PrometheusControlKey pControlKey) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Store the Details */
            setValueControlKey(pControlKey);

            /* Access the Security manager */
            final PrometheusDataSet myData = getDataSet();

            /* Record the security factory */
            theSecurityFactory = pControlKey.getFactoryLock().getFactory();

            /* Create the KeySet */
            final GordianKeySetFactory myKeySets = theSecurityFactory.getKeySetFactory();
            final GordianKeySet myKeySet = myKeySets.generateKeySet(getDataSet().getKeySetSpec());
            setValueKeySet(myKeySet);

            /* Set the wrappedKeySetDef */
            setValueSecuredKeySetDef(theSecurityFactory.getEmbeddedKeySet().secureKeySet(myKeySet));

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the security factory.
     * @return the security factory
     */
    GordianFactory getSecurityFactory() {
        return theSecurityFactory;
    }

    /**
     * Obtain the dataKeySetCache.
     * @return the dataKeySets
     */
    private DataKeySetCache getDataKeySets() {
        return theKeySetCache;
    }

    /**
     * Obtain the next DataKeySet.
     * @return the next dataKeySet
     */
    PrometheusDataKeySet getNextDataKeySet() {
        return theKeySetCache.getNextDataKeySet();
    }

    /**
     * Get the ControlKey.
     * @return the controlKey
     */
    public final PrometheusControlKey getControlKey() {
        return getValues().getValue(PrometheusCryptographyDataType.CONTROLKEY, PrometheusControlKey.class);
    }

    /**
     * Get the ControlKeyId for this item.
     * @return the ControlKeyId
     */
    public Integer getControlKeyId() {
        final PrometheusControlKey myKey = getControlKey();
        return myKey == null
                ? null
                : myKey.getIndexedId();
    }

    /**
     * Get the securedKeySetDef.
     * @return the securedKeySetDef
     */
    public final byte[] getSecuredKeySetDef() {
        return getValues().getValue(PrometheusDataResource.KEYSET_KEYSETDEF, byte[].class);
    }

    /**
     * Get the KeySet.
     * @return the keySet
     */
    public GordianKeySet getKeySet() {
        return getValues().getValue(PrometheusDataResource.KEYSET_KEYSET, GordianKeySet.class);
    }

    /**
     * Set the ControlKey Id.
     * @param pId the controlKey id
     * @throws OceanusException on error
     */
    private void setValueControlKey(final Integer pId) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEY, pId);
    }

    /**
     * Set the ControlKey.
     * @param pKey the controlKey
     * @throws OceanusException on error
     */
    private void setValueControlKey(final PrometheusControlKey pKey) throws OceanusException {
        getValues().setValue(PrometheusCryptographyDataType.CONTROLKEY, pKey);
    }

    /**
     * Set the securedKeySetDef.
     * @param pValue the securedKeySetDef
     */
    private void setValueSecuredKeySetDef(final byte[] pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.KEYSET_KEYSETDEF, pValue);
    }

    /**
     * Set the keySet.
     * @param pValue the keySet
     */
    private void setValueKeySet(final GordianKeySet pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.KEYSET_KEYSET, pValue);
    }

    @Override
    public PrometheusControlKeySet getBase() {
        return (PrometheusControlKeySet) super.getBase();
    }

    @Override
    public PrometheusControlKeySetList getList() {
        return (PrometheusControlKeySetList) super.getList();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Only sort on id */
        return 0;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        final PrometheusDataSet myData = getDataSet();
        resolveDataLink(PrometheusCryptographyDataType.CONTROLKEY, myData.getControlKeys());
        final PrometheusControlKey myControlKey = getControlKey();

        /* Register the KeySet */
        myControlKey.registerControlKeySet(this);
    }

    /**
     * Allocate a new DataKeySet.
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private void allocateDataKeySets(final PrometheusDataSet pData) throws OceanusException {
        /* Access the DataKeySet List */
        final PrometheusDataKeySetList mySets = pData.getDataKeySets();
        setNewVersion();

        /* Loop to create sufficient DataKeySets */
        final int myNumKeySets = pData.getNumActiveKeySets();
        for (int i = 0; i < myNumKeySets; i++) {
            /* Allocate the DataKeySet */
            final PrometheusDataKeySet mySet = new PrometheusDataKeySet(mySets, this);
            mySet.setNewVersion();
            mySets.add(mySet);

            /* Register the DataKeySet */
            theKeySetCache.registerDataKeySet(mySet);
        }
    }

    /**
     * Delete the old ControlKeySet and DataKeySets.
     */
    protected void deleteControlKeySet() {
        /* Mark this dataKeySet as deleted */
        setDeleted(true);

        /* Delete the dataKeySets */
        theKeySetCache.deleteDataKeySets();
    }

    /**
     * Register DataKeySet.
     * @param pKeySet the DataKeySet to register
     */
    void registerDataKeySet(final PrometheusDataKeySet pKeySet) {
        /* Store the DataKey into the map */
        theKeySetCache.registerDataKeySet(pKeySet);
    }

    /**
     * DataKeySet List.
     */
    public static class PrometheusControlKeySetList
            extends PrometheusDataList<PrometheusControlKeySet> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusControlKeySetList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusControlKeySetList.class);

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected PrometheusControlKeySetList(final PrometheusDataSet pData) {
            this(pData, PrometheusListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected PrometheusControlKeySetList(final PrometheusDataSet pData,
                                           final PrometheusListStyle pStyle) {
            super(PrometheusControlKeySet.class, pData, PrometheusCryptographyDataType.CONTROLKEYSET, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PrometheusControlKeySetList(final PrometheusControlKeySetList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PrometheusControlKeySetList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSet<PrometheusControlKeySet> getItemFields() {
            return PrometheusControlKeySet.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected PrometheusControlKeySetList getEmptyList(final PrometheusListStyle pStyle) {
            final PrometheusControlKeySetList myList = new PrometheusControlKeySetList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PrometheusControlKeySetList deriveList(final PrometheusListStyle pStyle) throws OceanusException {
            return (PrometheusControlKeySetList) super.deriveList(pStyle);
        }

        @Override
        public PrometheusControlKeySetList deriveDifferences(final PrometheusDataSet pDataSet,
                                                             final PrometheusDataList<?> pOld) {
            return (PrometheusControlKeySetList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public PrometheusControlKeySet addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a DataKeySet */
            if (!(pItem instanceof PrometheusControlKeySet)) {
                return null;
            }

            /* Clone the control key set */
            final PrometheusControlKeySet mySet = new PrometheusControlKeySet(this, (PrometheusControlKeySet) pItem);
            add(mySet);
            return mySet;
        }

        @Override
        public PrometheusControlKeySet addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrometheusControlKeySet addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the dataKeySet */
            final PrometheusControlKeySet mySet = new PrometheusControlKeySet(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(mySet.getIndexedId())) {
                mySet.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new PrometheusDataException(mySet, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySet);

            /* Return it */
            return mySet;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @param pKeySet the DataKeySet to clone
         * @return the new DataKeySet
         * @throws OceanusException on error
         */
        protected PrometheusControlKeySet cloneControlKeySet(final PrometheusControlKey pControlKey,
                                                             final PrometheusControlKeySet pKeySet) throws OceanusException {
            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pKeySet.getIndexedId());
            myValues.addValue(PrometheusCryptographyDataType.CONTROLKEY, pControlKey);
            myValues.addValue(PrometheusDataResource.KEYSET_KEYSETDEF, pKeySet.getSecuredKeySetDef());
            myValues.addValue(PrometheusDataResource.KEYSET_KEYSET, pKeySet.getKeySet());

            /* Clone the controlKeySet */
            final PrometheusControlKeySet mySet = addValuesItem(myValues);

            /* Access the ControlKeySet List */
            final PrometheusDataSet myData = getDataSet();
            final PrometheusDataKeySetList myKeySets = myData.getDataKeySets();

            /* Create a new DataKeySetCache for this ControlKeySet */
            final DataKeySetCache mySource = pKeySet.getDataKeySets();
            mySet.theKeySetCache = mySource.cloneDataKeySetCache(mySet, myKeySets);

            return mySet;
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected PrometheusDataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }

    /**
     * DataKeySetCache.
     */
    private static final class DataKeySetCache
            implements MetisFieldItem, MetisDataList<PrometheusDataKeySet> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DataKeySetCache> FIELD_DEFS = MetisFieldSet.newFieldSet(DataKeySetCache.class);

        /*
         * Size Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, DataKeySetCache::size);
        }

        /**
         * The list.
         */
        private final List<PrometheusDataKeySet> theList;

        /**
         * Iterator.
         */
        private Iterator<PrometheusDataKeySet> theIterator;

        /**
         * Constructor.
         */
        DataKeySetCache() {
            theList = new ArrayList<>();
        }

        @Override
        public MetisFieldSet<DataKeySetCache> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<PrometheusDataKeySet> getUnderlyingList() {
            return theList;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Register the KeySet.
         * @param pKeySet the KeySet to register
         */
        private void registerDataKeySet(final PrometheusDataKeySet pKeySet) {
            /* If this is first registration */
            if (!theList.contains(pKeySet)) {
                /* Add the KeySet */
                theList.add(pKeySet);

                /* Reset any iterator */
                if (theIterator != null) {
                    theIterator = iterator();
                }
            }
        }

        /**
         * Get next DataKeySet.
         * @return the next KeySet
         */
        private PrometheusDataKeySet getNextDataKeySet() {
            /* Handle empty list */
            if (isEmpty()) {
                return null;
            }

            /* Handle initialisation and wrapping */
            if (theIterator == null
                    || !theIterator.hasNext()) {
                theIterator = iterator();
            }

            /* Return the next KeySet */
            return theIterator.next();
        }

        /**
         * Delete the KeySets.
         */
        private void deleteDataKeySets() {
            /* Loop through the KeySets */
            final Iterator<PrometheusDataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PrometheusDataKeySet mySet = myIterator.next();

                /* Delete the KeySet */
                mySet.deleteDataKeySet();
            }
        }

        /**
         * Clone dataKeySet Cache from a DataBase.
         * @param pControlKeySet the ControlKeySet to clone
         * @param pKeySets the DataKeySetList
         * @return the new DataKeySetCache
         * @throws OceanusException on error
         */
        private DataKeySetCache cloneDataKeySetCache(final PrometheusControlKeySet pControlKeySet,
                                                     final PrometheusDataKeySetList pKeySets) throws OceanusException {
            /* Create a new cache */
            final DataKeySetCache myCache = new DataKeySetCache();

            /* Loop through the KeySets */
            final Iterator<PrometheusDataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PrometheusDataKeySet mySet = myIterator.next();

                /* Create a new DataKeySet for this ControlKeySet */
                final PrometheusDataKeySet myNewSet = pKeySets.cloneDataKeySet(pControlKeySet, mySet);
                myCache.registerDataKeySet(myNewSet);
            }

            /* Return the cache */
            return myCache;
        }
    }
}
