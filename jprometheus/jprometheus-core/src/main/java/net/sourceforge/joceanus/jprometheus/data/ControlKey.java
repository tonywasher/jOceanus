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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataKeySet.DataKeySetList;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * ControlKey definition and list. The Control Key represents the passwordHash that controls
 * securing of the dataKeys. It maintains a map of the associated DataKeys.
 * @author Tony Washer
 */
public final class ControlKey
        extends DataItem<CryptographyDataType>
        implements Comparable<ControlKey> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.CONTROLKEY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.CONTROLKEY.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * Field ID for Prime keySetHash.
     */
    public static final JDataField FIELD_PRIMEHASH = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_PRIMEHASH.getValue());

    /**
     * Field ID for Alternate keySetHash.
     */
    public static final JDataField FIELD_ALTHASH = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_ALTHASH.getValue());

    /**
     * HashPrime Field Id.
     */
    public static final JDataField FIELD_HASHPRIME = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_PRIME.getValue());

    /**
     * Field ID for PrimeHashBytes.
     */
    public static final JDataField FIELD_PRIMEBYTES = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.CONTROLKEY_PRIMEBYTES.getValue());

    /**
     * Field ID for AltHashBytes.
     */
    public static final JDataField FIELD_ALTBYTES = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.CONTROLKEY_ALTBYTES.getValue());

    /**
     * Field ID for DataKeySet.
     */
    public static final JDataField FIELD_SETS = FIELD_DEFS.declareLocalField(DataKeySet.LIST_NAME);

    /**
     * Name of Database.
     */
    public static final String NAME_DATABASE = PrometheusDataResource.CONTROLKEY_DATABASE.getValue();

    /**
     * KeySetHash Length.
     */
    public static final int HASHLEN = GordianKeySetHash.HASHSIZE;

    /**
     * The DataKeySet.
     */
    private DataKeySetResource theDataKeySet = new DataKeySetResource();

    /**
     * Copy constructor.
     * @param pList the List to add to
     * @param pSource the source key to copy
     */
    private ControlKey(final ControlKeyList pList,
                       final ControlKey pSource) {
        /* Initialise the item */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKeyList pList,
                       final DataValues<CryptographyDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store Prime indication */
        Object myValue = pValues.getValue(FIELD_HASHPRIME);
        if (myValue instanceof Boolean) {
            setValueHashPrime((Boolean) myValue);
        }

        /* Store the Prime/AltHashBytes */
        myValue = pValues.getValue(FIELD_PRIMEBYTES);
        if (myValue instanceof byte[]) {
            setValuePrimeHashBytes((byte[]) myValue);
        }
        myValue = pValues.getValue(FIELD_ALTBYTES);
        if (myValue instanceof byte[]) {
            setValueAltHashBytes((byte[]) myValue);
        }
    }

    /**
     * Constructor for a new ControlKey.
     * <p>
     * This will create a new DataKeySet with a new set of DataKeys.
     * @param pList the list to which to add the key to
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKeyList pList) throws JOceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            GordianHashManager mySecure = myData.getSecurity();

            /* Create a new keySetHash with new password */
            GordianKeySetHash myHash = mySecure.resolveKeySetHash(null, NAME_DATABASE);

            /* Store the password hash */
            setValueHashPrime(Boolean.TRUE);
            setValuePrimeKeySetHash(myHash);

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a new ControlKey with the same password.
     * <p>
     * This will create a new DataKeySet with a new cloned set of DataKeys.
     * @param pKey the key to copy
     * @throws JOceanusException on error
     */
    private ControlKey(final ControlKey pKey) throws JOceanusException {
        /* Initialise the item */
        super(pKey.getList(), 0);

        /* Protect against exceptions */
        try {
            /* Access the Security manager */
            DataSet<?, ?> myData = getDataSet();
            GordianHashManager mySecure = myData.getSecurity();

            /* ReSeed the security generator */
            GordianFactory myFactory = mySecure.getSecurityFactory();
            myFactory.reSeedRandom();

            /* Create a similar keySetHash */
            GordianKeySetHash myHash = mySecure.similarKeySetHash(myData.getKeySetHash());

            /* Store the password Hash */
            setValueHashPrime(Boolean.TRUE);
            setValuePrimeKeySetHash(myHash);

            /* Allocate the DataKeySets */
            allocateDataKeySets(myData);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JPrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SETS.equals(pField)) {
            return theDataKeySet;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Is the prime hash active?
     * @return true/false
     */
    public Boolean isHashPrime() {
        return isHashPrime(getValueSet());
    }

    /**
     * Get the PrimeHashBytes.
     * @return the hash bytes
     */
    public byte[] getPrimeHashBytes() {
        return getPrimeHashBytes(getValueSet());
    }

    /**
     * Get the AltHashBytes.
     * @return the hash bytes
     */
    public byte[] getAltHashBytes() {
        return getAltHashBytes(getValueSet());
    }

    /**
     * Get the Prime keySetHash.
     * @return the prime keySetHash
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash getPrimeKeySetHash() throws JOceanusException {
        GordianKeySetHash myHash = getPrimeKeySetHash(getValueSet());
        return (myHash == null)
                                ? resolvePrimeHash()
                                : myHash;
    }

    /**
     * Get the Alternate keySetHash.
     * @return the alternate keySetHash
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash getAltKeySetHash() throws JOceanusException {
        GordianKeySetHash myHash = getAltKeySetHash(getValueSet());
        return (myHash == null)
                                ? resolveAltHash()
                                : myHash;
    }

    /**
     * Is the prime hash active?
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isHashPrime(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHPRIME, Boolean.class);
    }

    /**
     * Get the PrimeHashBytes for the valueSet.
     * @param pValueSet the ValueSet
     * @return the hash bytes
     */
    public static byte[] getPrimeHashBytes(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRIMEBYTES, byte[].class);
    }

    /**
     * Get the AltHashBytes for the valueSet.
     * @param pValueSet the ValueSet
     * @return the hash bytes
     */
    public static byte[] getAltHashBytes(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ALTBYTES, byte[].class);
    }

    /**
     * Get the Prime keySetHash for the valueSet.
     * @param pValueSet the ValueSet
     * @return the keySetHash
     */
    protected static GordianKeySetHash getPrimeKeySetHash(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRIMEHASH, GordianKeySetHash.class);
    }

    /**
     * Get the Alternate keySetHash for the valueSet.
     * @param pValueSet the ValueSet
     * @return the keySetHash
     */
    protected static GordianKeySetHash getAltKeySetHash(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ALTHASH, GordianKeySetHash.class);
    }

    /**
     * Set the HashPrime indicator.
     * @param pPrime true/false
     */
    private void setValueHashPrime(final Boolean pPrime) {
        getValueSet().setValue(FIELD_HASHPRIME, pPrime);
    }

    /**
     * Set the PrimeKeySetHash.
     * @param pValue the keySetHash
     */
    private void setValuePrimeKeySetHash(final GordianKeySetHash pValue) {
        getValueSet().setValue(FIELD_PRIMEHASH, pValue);
        setValuePrimeHashBytes((pValue == null)
                                                ? null
                                                : pValue.getHash());
    }

    /**
     * Set the AltKeySetHash.
     * @param pValue the keySetHash
     */
    private void setValueAltKeySetHash(final GordianKeySetHash pValue) {
        getValueSet().setValue(FIELD_ALTHASH, pValue);
        setValueAltHashBytes((pValue == null)
                                              ? null
                                              : pValue.getHash());
    }

    /**
     * Set the Prime Hash Bytes.
     * @param pValue the Hash bytes
     */
    private void setValuePrimeHashBytes(final byte[] pValue) {
        getValueSet().setValue(FIELD_PRIMEBYTES, pValue);
    }

    /**
     * Set the Alternate Hash Bytes.
     * @param pValue the Hash bytes
     */
    private void setValueAltHashBytes(final byte[] pValue) {
        getValueSet().setValue(FIELD_ALTBYTES, pValue);
    }

    @Override
    public ControlKey getBase() {
        return (ControlKey) super.getBase();
    }

    @Override
    public ControlKeyList getList() {
        return (ControlKeyList) super.getList();
    }

    /**
     * Obtain the next DataKeySet.
     * @return the next dataKeySet
     */
    protected DataKeySet getNextDataKeySet() {
        return theDataKeySet.getNextDataKeySet();
    }

    /**
     * Obtain the active keySetHash.
     * @return the active keySetHash
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash getKeySetHash() throws JOceanusException {
        return getKeySetHash(isHashPrime());
    }

    /**
     * Obtain the required keySetHash.
     * @param pUsePrime return prime hash (true/false)
     * @return the requested keySetHash
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash getKeySetHash(final Boolean pUsePrime) throws JOceanusException {
        return pUsePrime
                         ? getPrimeKeySetHash()
                         : getAltKeySetHash();
    }

    /**
     * Resolve the active Hash.
     * @throws JOceanusException on error
     */
    protected void resolveHash() throws JOceanusException {
        if (isHashPrime()) {
            resolvePrimeHash();
        } else {
            resolveAltHash();
        }
    }

    /**
     * Resolve prime Hash.
     * @return the resolved Hash
     * @throws JOceanusException on error
     */
    private GordianKeySetHash resolvePrimeHash() throws JOceanusException {
        /* Access the Security manager */
        DataSet<?, ?> myData = getDataSet();
        GordianHashManager mySecure = myData.getSecurity();

        /* Resolve the keySetHash */
        GordianKeySetHash myHash = mySecure.resolveKeySetHash(getPrimeHashBytes(), NAME_DATABASE);

        /* Store the keySetHash */
        setValuePrimeKeySetHash(myHash);
        return myHash;
    }

    /**
     * Resolve alternate Hash.
     * @return the resolved Hash
     * @throws JOceanusException on error
     */
    private GordianKeySetHash resolveAltHash() throws JOceanusException {
        /* Access the Security manager */
        DataSet<?, ?> myData = getDataSet();
        GordianHashManager mySecure = myData.getSecurity();

        /* Resolve the keySetHash */
        GordianKeySetHash myHash = mySecure.resolveKeySetHash(getAltHashBytes(), NAME_DATABASE);

        /* Store the keySetHash */
        setValueAltKeySetHash(myHash);
        return myHash;
    }

    @Override
    public int compareTo(final ControlKey pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the underlying object */
        return super.compareId(pThat);
    }

    /**
     * Allocate a new DataKeySet.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private void allocateDataKeySets(final DataSet<?, ?> pData) throws JOceanusException {
        /* Access the DataKeySet List */
        DataKeySetList mySets = pData.getDataKeySets();
        setNewVersion();

        /* Loop to create sufficient DataKeySets */
        int myNumKeySets = pData.getNumActiveKeySets();
        for (int i = 0; i < myNumKeySets; i++) {
            /* Allocate the DataKeySet */
            DataKeySet mySet = new DataKeySet(mySets, this);
            mySets.add(mySet);

            /* Register the DataKeySet */
            theDataKeySet.registerKeySet(mySet);
        }
    }

    /**
     * Delete the old set of ControlKey and DataKeys.
     */
    private void deleteControlSet() {
        /* Delete the DataKeySet */
        theDataKeySet.deleteDataKeySets();

        /* Mark this control key as deleted */
        setDeleted(true);
    }

    /**
     * Update password hash.
     * @param pHash the new keySetHash
     * @throws JOceanusException on error
     */
    protected void updatePasswordHash(final GordianKeySetHash pHash) throws JOceanusException {
        /* Access current mode */
        Boolean isHashPrime = isHashPrime();

        /* Store the current detail into history */
        pushHistory();

        /* Flip hash Prime */
        isHashPrime = !isHashPrime;
        setValueHashPrime(isHashPrime);

        /* Update the keySetHash */
        if (isHashPrime) {
            setValuePrimeKeySetHash(pHash);
        } else {
            setValueAltKeySetHash(pHash);
        }

        /* Update the hash for the KeySet */
        ensureKeySetHash();

        /* Check for changes */
        checkForHistory();
    }

    /**
     * Ensure keySetHash is updated.
     * @throws JOceanusException on error
     */
    protected void ensureKeySetHash() throws JOceanusException {
        /* Access current mode */
        Boolean isHashPrime = isHashPrime();
        GordianKeySetHash myHash = getKeySetHash();

        /* Update the hash for the KeySet */
        if (theDataKeySet.updateKeySetHash(isHashPrime, myHash)) {
            DataSet<?, ?> myData = getDataSet();
            myData.setVersion(myData.getVersion() + 1);
        }
    }

    /**
     * Register DataKeySet.
     * @param pKeySet the DataKeySet to register
     */
    protected void registerDataKeySet(final DataKeySet pKeySet) {
        /* Store the DataKey into the map */
        theDataKeySet.registerKeySet(pKeySet);
    }

    /**
     * ControlKey List.
     */
    public static class ControlKeyList
            extends DataList<ControlKey, CryptographyDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Construct an empty CORE ControlKey list.
         * @param pData the DataSet for the list
         */
        protected ControlKeyList(final DataSet<?, ?> pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic ControlKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected ControlKeyList(final DataSet<?, ?> pData,
                                 final ListStyle pStyle) {
            super(ControlKey.class, pData, CryptographyDataType.CONTROLKEY, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ControlKeyList(final ControlKeyList pSource) {
            super(pSource);
        }

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return ControlKey.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected ControlKeyList getEmptyList(final ListStyle pStyle) {
            ControlKeyList myList = new ControlKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public ControlKeyList deriveList(final ListStyle pStyle) throws JOceanusException {
            return (ControlKeyList) super.deriveList(pStyle);
        }

        @Override
        public ControlKeyList deriveDifferences(final DataSet<?, ?> pDataSet,
                                                final DataList<?, CryptographyDataType> pOld) {
            return (ControlKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public ControlKey addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a ControlKey */
            if (!(pItem instanceof ControlKey)) {
                return null;
            }

            /* Clone the control key */
            ControlKey myKey = new ControlKey(this, (ControlKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public ControlKey addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ControlKey addValuesItem(final DataValues<CryptographyDataType> pValues) throws JOceanusException {
            /* Create the controlKey */
            ControlKey myKey = new ControlKey(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(myKey.getId())) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JPrometheusDataException(myKey, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myKey);

            /* Return it */
            return myKey;
        }

        /**
         * Create a new ControlKey (with associated DataKeys).
         * @return the new item
         * @throws JOceanusException on error
         */
        public ControlKey createNewKeySet() throws JOceanusException {
            /* Create the key */
            ControlKey myKey = new ControlKey(this);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a cloned ControlKey (with associated DataKeys).
         * @param pSource the source key
         * @return the new item
         * @throws JOceanusException on error
         */
        public ControlKey cloneItem(final ControlKey pSource) throws JOceanusException {
            /* Create the key */
            ControlKey myKey = new ControlKey(pSource);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Initialise Security from a DataBase for a SpreadSheet load.
         * @param pDatabase the DataSet for the Database
         * @throws JOceanusException on error
         */
        protected void initialiseSecurity(final DataSet<?, ?> pDatabase) throws JOceanusException {
            /* Access the active control key from the database */
            DataSet<?, ?> myData = getDataSet();
            ControlKey myDatabaseKey = pDatabase.getControlKey();
            ControlKey myKey;

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
            DataSet<?, ?> myData = getDataSet();
            ControlKey myKey = myData.getControlKey();

            /* Loop through the controlKeys */
            Iterator<ControlKey> myIterator = iterator();
            while (myIterator.hasNext()) {
                ControlKey myCurr = myIterator.next();

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
         * @throws JOceanusException on error
         */
        private ControlKey cloneControlKey(final ControlKey pControlKey) throws JOceanusException {
            /* Build data values */
            DataValues<CryptographyDataType> myValues = new DataValues<CryptographyDataType>(ControlKey.OBJECT_NAME);
            myValues.addValue(ControlKey.FIELD_ID, pControlKey.getId());
            myValues.addValue(ControlKey.FIELD_HASHPRIME, pControlKey.isHashPrime());
            myValues.addValue(ControlKey.FIELD_PRIMEBYTES, pControlKey.getPrimeHashBytes());
            myValues.addValue(ControlKey.FIELD_ALTBYTES, pControlKey.getAltHashBytes());

            /* Clone the control key */
            ControlKey myControl = addValuesItem(myValues);

            /* Access the DataKey List */
            DataSet<?, ?> myData = getDataSet();
            DataKeySetList myKeySets = myData.getDataKeySets();

            /* Create a new DataKeySet for this ControlKey */
            DataKeySetResource mySource = pControlKey.theDataKeySet;
            myControl.theDataKeySet = mySource.cloneDataKeySetResource(myControl, myKeySets);

            /* return the cloned key */
            return myControl;
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected DataMapItem<ControlKey, CryptographyDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }

    /**
     * DataKeySetResource.
     */
    private static final class DataKeySetResource
            extends OrderedIdList<Integer, DataKeySet>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(DataKeySet.LIST_NAME);

        /**
         * Size Field Id.
         */
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Iterator.
         */
        private Iterator<DataKeySet> theIterator;

        /**
         * Constructor.
         */
        private DataKeySetResource() {
            super(DataKeySet.class);
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Register the KeySet.
         * @param pKeySet the KeySet to register
         */
        private void registerKeySet(final DataKeySet pKeySet) {
            /* If this is first registration */
            if (!contains(pKeySet)) {
                /* Add the KeySet */
                append(pKeySet);

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
        private DataKeySet getNextDataKeySet() {
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
            Iterator<DataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                DataKeySet mySet = myIterator.next();

                /* Delete the KeySet */
                mySet.deleteDataKeySet();
            }
        }

        /**
         * Update the Password Hash.
         * @param pPrimeHash this is the prime hash
         * @param pHash the new keySetHash
         * @return were there changes? true/false
         * @throws JOceanusException on error
         */
        private boolean updateKeySetHash(final Boolean pPrimeHash,
                                         final GordianKeySetHash pHash) throws JOceanusException {
            /* Loop through the KeySets */
            boolean bChanges = false;
            Iterator<DataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                DataKeySet mySet = myIterator.next();

                /* Update the KeySet */
                bChanges |= mySet.updateKeySetHash(pPrimeHash, pHash);
            }

            /* return the flag */
            return bChanges;
        }

        /**
         * Clone KeySet from a DataBase.
         * @param pControlKey the ControlKey to clone
         * @param pKeySets the DataKeySetList
         * @return the new DataKeySet
         * @throws JOceanusException on error
         */
        private DataKeySetResource cloneDataKeySetResource(final ControlKey pControlKey,
                                                           final DataKeySetList pKeySets) throws JOceanusException {
            /* Create a new resource */
            DataKeySetResource myResource = new DataKeySetResource();

            /* Loop through the KeySets */
            Iterator<DataKeySet> myIterator = iterator();
            while (myIterator.hasNext()) {
                DataKeySet mySet = myIterator.next();

                /* Create a new DataKeySet for this ControlKey */
                DataKeySet myNewSet = pKeySets.cloneDataKeySet(pControlKey, mySet);
                myResource.registerKeySet(myNewSet);
            }

            /* Return the resource */
            return myResource;
        }
    }
}
