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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataKeySet.PrometheusDataKeySetList;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls
 * securing of the dataKeys. It maintains a map of the associated DataKeys.
 * @author Tony Washer
 */
public final class PrometheusControlKey
        extends PrometheusDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = PrometheusCryptographyDataType.CONTROLKEY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = PrometheusCryptographyDataType.CONTROLKEY.getListName();

    /**
     * KeySetHash Length.
     */
    public static final int HASHLEN = GordianUtilities.getKeySetHashLen();

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<PrometheusControlKey> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(PrometheusControlKey.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareByteArrayField(PrometheusDataResource.CONTROLKEY_BYTES, HASHLEN);
        FIELD_DEFS.declareDerivedVersionedField(PrometheusDataResource.CONTROLKEY_HASH);
        FIELD_DEFS.declareLocalField(PrometheusDataKeySet.LIST_NAME, PrometheusControlKey::getDataKeySets);
    }

    /**
     * Name of Database.
     */
    public static final String NAME_DATABASE = PrometheusDataResource.CONTROLKEY_DATABASE.getValue();

    /**
     * The DataKeySet.
     */
    private DataKeySetResource theDataKeySet = new DataKeySetResource();

    /**
     * Copy constructor.
     * @param pList the List to add to
     * @param pSource the source key to copy
     */
    private PrometheusControlKey(final PrometheusControlKeyList pList,
                                 final PrometheusControlKey pSource) {
        /* Initialise the item */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private PrometheusControlKey(final PrometheusControlKeyList pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store HashBytes */
            Object myValue = pValues.getValue(PrometheusDataResource.CONTROLKEY_BYTES);
            if (myValue instanceof byte[]) {
                setValueHashBytes((byte[]) myValue);
            }

            /* Store/Resolve Hash */
            myValue = pValues.getValue(PrometheusDataResource.CONTROLKEY_HASH);
            if (myValue instanceof GordianKeySetHash) {
                setValueKeySetHash((GordianKeySetHash) myValue);
            } else if (getHashBytes() != null) {
                /* Access the Security manager */
                final PrometheusDataSet myData = getDataSet();
                final GordianPasswordManager myPasswordMgr = myData.getPasswordMgr();

                /* Resolve the keySetHash */
                final GordianKeySetHash myHash = myPasswordMgr.resolveKeySetHash(getHashBytes(), NAME_DATABASE);

                /* Store the keySetHash */
                setValueKeySetHash(myHash);
            }

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey.
     * <p>
     * This will create a new DataKeySet with a new set of DataKeys.
     * @param pList the list to which to add the key to
     * @throws OceanusException on error
     */
    private PrometheusControlKey(final PrometheusControlKeyList pList) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            final PrometheusDataSet myData = getDataSet();
            final GordianPasswordManager myPasswordMgr = myData.getPasswordMgr();

            /* Create a new keySetHash with new password */
            final GordianKeySetHash myHash = myPasswordMgr.newKeySetHash(NAME_DATABASE);

            /* Store the password hash */
            setValueHashBytes(myHash.getHash());
            setValueKeySetHash(myHash);

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey with the same password.
     * <p>
     * This will create a new DataKeySet with a new cloned set of DataKeys.
     * @param pKey the key to copy
     * @throws OceanusException on error
     */
    private PrometheusControlKey(final PrometheusControlKey pKey) throws OceanusException {
        /* Initialise the item */
        super(pKey.getList(), 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            final PrometheusDataSet myData = getDataSet();
            final GordianPasswordManager myPasswordMgr = myData.getPasswordMgr();

            /* ReSeed the security generator */
            final GordianFactory myFactory = myPasswordMgr.getSecurityFactory();
            myFactory.reSeedRandom();

            /* Create a similar keySetHash */
            final GordianKeySetHash myHash = myPasswordMgr.similarKeySetHash(myData.getKeySetHash());

            /* Store the password Hash */
            setValueHashBytes(myHash.getHash());
            setValueKeySetHash(myHash);

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
     * Obtain the dataKeySetResource.
     * @return the dataKeySets
     */
    private DataKeySetResource getDataKeySets() {
        return theDataKeySet;
    }

    /**
     * Get the HashBytes.
     * @return the hash bytes
     */
    public byte[] getHashBytes() {
        return getValues().getValue(PrometheusDataResource.CONTROLKEY_BYTES, byte[].class);
    }

    /**
     * Get the keySetHash.
     * @return the prime keySetHash
     */
    public GordianKeySetHash getKeySetHash() {
        return getValues().getValue(PrometheusDataResource.CONTROLKEY_HASH, GordianKeySetHash.class);
    }

    /**
     * Set the Prime Hash Bytes.
     * @param pValue the Hash bytes
     * @throws OceanusException on error
     */
    private void setValueHashBytes(final byte[] pValue) throws OceanusException {
        getValues().setValue(PrometheusDataResource.CONTROLKEY_BYTES, pValue);
    }

    /**
     * Set the PrimeKeySetHash.
     * @param pValue the keySetHash
     * @throws OceanusException on error
     */
    private void setValueKeySetHash(final GordianKeySetHash pValue) throws OceanusException {
        getValues().setValue(PrometheusDataResource.CONTROLKEY_HASH, pValue);
    }

    @Override
    public PrometheusControlKey getBase() {
        return (PrometheusControlKey) super.getBase();
    }

    @Override
    public PrometheusControlKeyList getList() {
        return (PrometheusControlKeyList) super.getList();
    }

    /**
     * Obtain the next DataKeySet.
     * @return the next dataKeySet
     */
    PrometheusDataKeySet getNextDataKeySet() {
        return theDataKeySet.getNextDataKeySet();
    }

    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Only sort on id */
        return 0;
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
            theDataKeySet.registerKeySet(mySet);
        }
    }

    /**
     * Delete the old set of ControlKey and DataKeys.
     */
    void deleteControlSet() {
        /* Delete the DataKeySet */
        theDataKeySet.deleteDataKeySets();

        /* Mark this control key as deleted */
        setDeleted(true);
    }

    /**
     * Update password hash.
     * @param pHash the new keySetHash
     * @throws OceanusException on error
     */
    void updatePasswordHash(final GordianKeySetHash pHash) throws OceanusException {
        /* Store the current detail into history */
        pushHistory();

        /* Update the keySetHash */
        setValueKeySetHash(pHash);

        /* Update the hash for the KeySet */
        ensureKeySetHash();

        /* Check for changes */
        checkForHistory();
    }

    /**
     * Ensure keySetHash is updated.
     * @throws OceanusException on error
     */
    void ensureKeySetHash() throws OceanusException {
        /* Access current mode */
        final GordianKeySetHash myHash = getKeySetHash();

        /* Update the hash for the KeySet */
        if (theDataKeySet.updateKeySetHash(myHash)) {
            final PrometheusDataSet myData = getDataSet();
            myData.setVersion(myData.getVersion() + 1);
        }
    }

    /**
     * Register DataKeySet.
     * @param pKeySet the DataKeySet to register
     */
    void registerDataKeySet(final PrometheusDataKeySet pKeySet) {
        /* Store the DataKey into the map */
        theDataKeySet.registerKeySet(pKeySet);
    }

    /**
     * ControlKey List.
     */
    public static class PrometheusControlKeyList
            extends PrometheusDataList<PrometheusControlKey> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusControlKeyList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusControlKeyList.class);

        /**
         * Construct an empty CORE ControlKey list.
         * @param pData the DataSet for the list
         */
        protected PrometheusControlKeyList(final PrometheusDataSet pData) {
            this(pData, PrometheusListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected PrometheusControlKeyList(final PrometheusDataSet pData,
                                           final PrometheusListStyle pStyle) {
            super(PrometheusControlKey.class, pData, PrometheusCryptographyDataType.CONTROLKEY, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PrometheusControlKeyList(final PrometheusControlKeyList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PrometheusControlKeyList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSet<PrometheusControlKey> getItemFields() {
            return PrometheusControlKey.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected PrometheusControlKeyList getEmptyList(final PrometheusListStyle pStyle) {
            final PrometheusControlKeyList myList = new PrometheusControlKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PrometheusControlKeyList deriveList(final PrometheusListStyle pStyle) throws OceanusException {
            return (PrometheusControlKeyList) super.deriveList(pStyle);
        }

        @Override
        public PrometheusControlKeyList deriveDifferences(final PrometheusDataSet pDataSet,
                                                          final PrometheusDataList<?> pOld) {
            return (PrometheusControlKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public PrometheusControlKey addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a ControlKey */
            if (!(pItem instanceof PrometheusControlKey)) {
                return null;
            }

            /* Clone the control key */
            final PrometheusControlKey myKey = new PrometheusControlKey(this, (PrometheusControlKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public PrometheusControlKey addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrometheusControlKey addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the controlKey */
            final PrometheusControlKey myKey = new PrometheusControlKey(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(myKey.getIndexedId())) {
                myKey.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new PrometheusDataException(myKey, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myKey);

            /* Return it */
            return myKey;
        }

        /**
         * Create a new ControlKey (with associated DataKeys).
         * @return the new item
         * @throws OceanusException on error
         */
        public PrometheusControlKey createNewKeySet() throws OceanusException {
            /* Create the key */
            final PrometheusControlKey myKey = new PrometheusControlKey(this);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a cloned ControlKey (with associated DataKeys).
         * @param pSource the source key
         * @return the new item
         * @throws OceanusException on error
         */
        public PrometheusControlKey cloneItem(final PrometheusControlKey pSource) throws OceanusException {
            /* Create the key */
            final PrometheusControlKey myKey = new PrometheusControlKey(pSource);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Initialise Security from a DataBase for a SpreadSheet load.
         * @param pDatabase the DataSet for the Database
         * @throws OceanusException on error
         */
        protected void initialiseSecurity(final PrometheusDataSet pDatabase) throws OceanusException {
            /* Access the active control key from the database */
            final PrometheusDataSet myData = getDataSet();
            final PrometheusControlKey myDatabaseKey = pDatabase.getControlKey();
            final PrometheusControlKey myKey;

            /* If we have an existing security key */
            if (myDatabaseKey != null) {
                /* Clone the Control Key and its DataKeySets */
                myKey = cloneControlKey(myDatabaseKey);

                /* else create a new security set */
            } else {
                /* Create the new security set */
                myKey = createNewKeySet();
            }

            /* Declare the Control Key */
            myData.getControl().setControlKey(myKey);
        }

        /**
         * Delete old controlKeys.
         */
        protected void purgeOldControlKeys() {
            /* Access the current control Key */
            final PrometheusDataSet myData = getDataSet();
            final PrometheusControlKey myKey = myData.getControlKey();

            /* Loop through the controlKeys */
            final Iterator<PrometheusControlKey> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PrometheusControlKey myCurr = myIterator.next();

                /* Delete if this is not the active key */
                if (!myKey.equals(myCurr)) {
                    myCurr.deleteControlSet();
                }
            }
        }

        /**
         * Clone ControlKey from dataBase.
         * @param pControlKey the ControlKey to clone
         * @return the new control key
         * @throws OceanusException on error
         */
        private PrometheusControlKey cloneControlKey(final PrometheusControlKey pControlKey) throws OceanusException {
            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(PrometheusControlKey.OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pControlKey.getIndexedId());
            myValues.addValue(PrometheusDataResource.CONTROLKEY_BYTES, pControlKey.getHashBytes());
            myValues.addValue(PrometheusDataResource.CONTROLKEY_HASH, pControlKey.getKeySetHash());

            /* Clone the control key */
            final PrometheusControlKey myControl = addValuesItem(myValues);

            /* Access the DataKey List */
            final PrometheusDataSet myData = getDataSet();
            final PrometheusDataKeySetList myKeySets = myData.getDataKeySets();

            /* Create a new DataKeySet for this ControlKey */
            final DataKeySetResource mySource = pControlKey.theDataKeySet;
            myControl.theDataKeySet = mySource.cloneDataKeySetResource(myControl, myKeySets);

            /* return the cloned key */
            return myControl;
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
     * DataKeySetResource.
     */
    private static final class DataKeySetResource
            implements MetisFieldItem, MetisDataList<PrometheusDataKeySet> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DataKeySetResource> FIELD_DEFS = MetisFieldSet.newFieldSet(DataKeySetResource.class);

        /*
         * Size Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, DataKeySetResource::size);
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
        DataKeySetResource() {
            theList = new ArrayList<>();
        }

        @Override
        public MetisFieldSet<DataKeySetResource> getDataFieldSet() {
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
        private void registerKeySet(final PrometheusDataKeySet pKeySet) {
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
            if ((theIterator == null)
                    || (!theIterator.hasNext())) {
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
         * Update the Password Hash.
         * @param pHash the new keySetHash
         * @return were there changes? true/false
         * @throws OceanusException on error
         */
        private boolean updateKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
            /* Loop through the KeySets */
            boolean bChanges = false;
            final Iterator<PrometheusDataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PrometheusDataKeySet mySet = myIterator.next();

                /* Update the KeySet */
                bChanges |= mySet.updateKeySetHash(pHash);
            }

            /* return the flag */
            return bChanges;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @param pKeySets the DataKeySetList
         * @return the new DataKeySet
         * @throws OceanusException on error
         */
        private DataKeySetResource cloneDataKeySetResource(final PrometheusControlKey pControlKey,
                                                           final PrometheusDataKeySetList pKeySets) throws OceanusException {
            /* Create a new resource */
            final DataKeySetResource myResource = new DataKeySetResource();

            /* Loop through the KeySets */
            final Iterator<PrometheusDataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PrometheusDataKeySet mySet = myIterator.next();

                /* Create a new DataKeySet for this ControlKey */
                final PrometheusDataKeySet myNewSet = pKeySets.cloneDataKeySet(pControlKey, mySet);
                myResource.registerKeySet(myNewSet);
            }

            /* Return the resource */
            return myResource;
        }
    }
}
