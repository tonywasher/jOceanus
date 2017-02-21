/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DataKey definition and list. The Data Key represents a SymmetricKey that is secured via a the
 * ControlKey. For a single control key, one DataKey is allocated for each available SymmetricKey
 * Type and the set forms a CipherSet for encryption purposes.
 * @author Tony Washer
 */
public class DataKey
        extends DataItem<CryptographyDataType>
        implements Comparable<DataKey> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = CryptographyDataType.DATAKEY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = CryptographyDataType.DATAKEY.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * DataKeySet Field Id.
     */
    public static final MetisField FIELD_KEYSET = FIELD_DEFS.declareEqualityValueField(DataKeySet.OBJECT_NAME);

    /**
     * KeyType Field Id.
     */
    public static final MetisField FIELD_KEYTYPE = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEY_TYPE.getValue());

    /**
     * KeyTypeId Field Id.
     */
    public static final MetisField FIELD_KEYTYPEID = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.DATAKEY_TYPEID.getValue());

    /**
     * HashPrime Field Id.
     */
    public static final MetisField FIELD_HASHPRIME = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.CONTROLKEY_PRIME.getValue());

    /**
     * KeyDefinition Field Id.
     */
    public static final MetisField FIELD_KEYDEF = FIELD_DEFS.declareEqualityValueField(PrometheusDataResource.DATAKEY_DEF.getValue());

    /**
     * DataKey Field Id.
     */
    public static final MetisField FIELD_KEY = FIELD_DEFS.declareDerivedValueField(PrometheusDataResource.DATAKEY_KEY.getValue());

    /**
     * Encrypted Symmetric Key Length.
     */
    public static final int KEYLEN = GordianKeySet.WRAPPED_KEYSIZE;

    /**
     * Copy Constructor.
     * @param pList the list to add to
     * @param pSource The Key to copy
     */
    protected DataKey(final DataKeyList pList,
                      final DataKey pSource) {
        /* Set standard values */
        super(pList, pSource);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    @SuppressWarnings("unchecked")
    private DataKey(final DataKeyList pList,
                    final DataValues<CryptographyDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access the Security manager */
        DataSet<?, ?> myData = getDataSet();

        /* Store the PrimeHash indicator */
        Object myValue = pValues.getValue(FIELD_HASHPRIME);
        Boolean isHashPrime = (myValue instanceof Boolean)
                                                           ? (Boolean) myValue
                                                           : Boolean.TRUE;
        setValueHashPrime(isHashPrime);

        /* Store the DataKeySet */
        myValue = pValues.getValue(FIELD_KEYSET);
        if (myValue instanceof Integer) {
            /* Store the integer */
            setValueDataKeySet((Integer) myValue);

            /* Resolve the DataKeySet */
            resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        } else if (myValue instanceof DataKeySet) {
            /* Store the DataKeySet */
            setValueDataKeySet((DataKeySet) myValue);
        }

        /* Resolve the DataKeySet */
        DataKeySet myDataKeySet = getDataKeySet();
        GordianKeySetHash myHash = myDataKeySet.getKeySetHash(isHashPrime);
        GordianKeySet myKeySet = myHash.getKeySet();

        /* Store the KeyType */
        myValue = pValues.getValue(FIELD_KEYTYPE);
        if (myValue instanceof Long) {
            /* Store the integer */
            setValueKeyTypeId((Long) myValue);

            /* Resolve the KeyType */
            setValueKeyType(myKeySet.deriveTypeFromExternalId(getKeyTypeId(), GordianSymKeyType.class));
        } else if (myValue instanceof GordianSymKeyType) {
            /* Store the keyType */
            setValueKeyType((GordianSymKeyType) myValue);

            /* Look for passed id */
            myValue = pValues.getValue(FIELD_KEYTYPEID);
            if (myValue instanceof Long) {
                /* Store the id */
                setValueKeyTypeId((Long) myValue);
            }
        }

        /* Store the KeyDef */
        myValue = pValues.getValue(FIELD_KEYDEF);
        if (myValue instanceof byte[]) {
            /* Access the value */
            byte[] myBytes = (byte[]) myValue;
            setValueSecuredKeyDef(myBytes);

            /* Look for passed key */
            myValue = pValues.getValue(FIELD_KEY);
            if (myValue instanceof GordianKey) {
                setValueDataKey((GordianKey<GordianSymKeyType>) myValue);
            } else {
                /* Create the Symmetric Key from the wrapped data */
                GordianFactory myFactory = myKeySet.getFactory();
                GordianSymKeyType myType = getKeyType();
                GordianKeyGenerator<GordianSymKeyType> myGenerator = myFactory.getKeyGenerator(myType);
                GordianKey<GordianSymKeyType> myKey = myGenerator.deriveKey(myBytes, myType, myKeySet);
                setValueDataKey(myKey);
            }

            /* Register the DataKey */
            myDataKeySet.registerDataKey(this);
        }
    }

    /**
     * Constructor for a new DataKey in a new DataKeySet.
     * @param pList the list to add to
     * @param pKeySet the KeySet to which this key belongs
     * @param pKeyType the Key type of the new key
     * @throws OceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final DataKeySet pKeySet,
                    final GordianSymKeyType pKeyType) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Protect against exceptions */
        try {
            /* Create the new key */
            Boolean isHashPrime = pKeySet.isHashPrime();
            setValueHashPrime(isHashPrime);

            /* Create the new key */
            GordianKeySetHash myHash = pKeySet.getKeySetHash(isHashPrime);
            GordianKeySet myKeySet = myHash.getKeySet();

            /* Store the Details */
            setValueDataKeySet(pKeySet);
            setValueKeyTypeId(myKeySet.deriveExternalIdForType(pKeyType));
            setValueKeyType(pKeyType);

            /* Create the new key */
            GordianFactory myFactory = myKeySet.getFactory();
            GordianKeyGenerator<GordianSymKeyType> myGenerator = myFactory.getKeyGenerator(pKeyType);
            GordianKey<GordianSymKeyType> myKey = myGenerator.generateKey();
            setValueDataKey(myKey);

            /* Store its secured keyDef */
            setValueSecuredKeyDef(myGenerator.secureKey(myKey, myKeySet));

            /* Register the DataKey */
            pKeySet.registerDataKey(this);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new PrometheusDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Constructor for a cloned DataKey in a new DataKeySet.
     * @param pList the list to add to
     * @param pKeySet the ControlKey to which this key belongs
     * @param pDataKey the DataKey to clone
     * @throws OceanusException on error
     */
    private DataKey(final DataKeyList pList,
                    final DataKeySet pKeySet,
                    final DataKey pDataKey) throws OceanusException {
        /* Initialise the item */
        super(pList, 0);

        /* Store the KeySet details */
        setValueDataKeySet(pKeySet);

        /* Copy the key details */
        setValueDataKey(pDataKey.getDataKey());
        setValueHashPrime(pDataKey.isHashPrime());
        setValueSecuredKeyDef(pDataKey.getSecuredKeyDef());
        setValueKeyType(pDataKey.getKeyType());
        setValueKeyTypeId(pDataKey.getKeyTypeId());
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Get the DataKeySet.
     * @return the dataKeySet
     */
    public DataKeySet getDataKeySet() {
        return getDataKeySet(getValueSet());
    }

    /**
     * Get the DataKeySetId for this item.
     * @return the DataKeySetId
     */
    public Integer getDataKeySetId() {
        DataKeySet myKey = getDataKeySet();
        return (myKey == null)
                               ? null
                               : myKey.getId();
    }

    /**
     * Get the Key Type.
     * @return the key type
     */
    public GordianSymKeyType getKeyType() {
        return getKeyType(getValueSet());
    }

    /**
     * Get the Key Type Id.
     * @return the key type id
     */
    public Long getKeyTypeId() {
        return getKeyTypeId(getValueSet());
    }

    /**
     * Is this locked by prime hash.
     * @return true/false
     */
    public Boolean isHashPrime() {
        return isHashPrime(getValueSet());
    }

    /**
     * Get the Key Definition.
     * @return the key definition
     */
    public byte[] getSecuredKeyDef() {
        return getSecuredKeyDef(getValueSet());
    }

    /**
     * Get the DataKey.
     * @return the data key
     */
    protected GordianKey<GordianSymKeyType> getDataKey() {
        return getDataKey(getValueSet());
    }

    /**
     * Get the DataKeySet.
     * @param pValueSet the valueSet
     * @return the dataKeySet
     */
    public static DataKeySet getDataKeySet(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYSET, DataKeySet.class);
    }

    /**
     * Get the Key type.
     * @param pValueSet the valueSet
     * @return the Key type
     */
    public static GordianSymKeyType getKeyType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPE, GordianSymKeyType.class);
    }

    /**
     * Get the Key type Id.
     * @param pValueSet the valueSet
     * @return the Key type Id
     */
    public static Long getKeyTypeId(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYTYPEID, Long.class);
    }

    /**
     * Is this locked by prime hash.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isHashPrime(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HASHPRIME, Boolean.class);
    }

    /**
     * Get the Key Definition .
     * @param pValueSet the valueSet
     * @return the Key Definition
     */
    public static byte[] getSecuredKeyDef(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_KEYDEF, byte[].class);
    }

    /**
     * Get the DataKey.
     * @param pValueSet the valueSet
     * @return the data Key
     */
    @SuppressWarnings("unchecked")
    protected static GordianKey<GordianSymKeyType> getDataKey(final MetisValueSet pValueSet) {
        return (GordianKey<GordianSymKeyType>) pValueSet.getValue(FIELD_KEY, GordianKey.class);
    }

    /**
     * Set the DataKeySet.
     * @param pValue the dataKeySet
     */
    private void setValueDataKeySet(final DataKeySet pValue) {
        getValueSet().setValue(FIELD_KEYSET, pValue);
    }

    /**
     * Set the DataKeySet Id.
     * @param pId the dataKeySet id
     */
    private void setValueDataKeySet(final Integer pId) {
        getValueSet().setValue(FIELD_KEYSET, pId);
    }

    /**
     * Set the Key Type.
     * @param pValue the KeyType
     */
    private void setValueKeyType(final GordianSymKeyType pValue) {
        getValueSet().setValue(FIELD_KEYTYPE, pValue);
    }

    /**
     * Set the KeyType id.
     * @param pId the KeyType id
     */
    private void setValueKeyTypeId(final Long pId) {
        getValueSet().setValue(FIELD_KEYTYPEID, pId);
    }

    /**
     * Set the HashPrime indicator.
     * @param pPrime true/false
     */
    private void setValueHashPrime(final Boolean pPrime) {
        getValueSet().setValue(FIELD_HASHPRIME, pPrime);
    }

    /**
     * Set the Key Definition.
     * @param pValue the KeyDefinition
     */
    private void setValueSecuredKeyDef(final byte[] pValue) {
        getValueSet().setValue(FIELD_KEYDEF, pValue);
    }

    /**
     * Set the DataKey.
     * @param pValue the dataKey
     */
    private void setValueDataKey(final GordianKey<GordianSymKeyType> pValue) {
        getValueSet().setValue(FIELD_KEY, pValue);
    }

    @Override
    public DataKey getBase() {
        return (DataKey) super.getBase();
    }

    @Override
    public DataKeyList getList() {
        return (DataKeyList) super.getList();
    }

    @Override
    public int compareTo(final DataKey pThat) {
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

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Resolve the ControlKey */
        DataSet<?, ?> myData = getDataSet();
        resolveDataLink(FIELD_KEYSET, myData.getDataKeySets());
        DataKeySet myKeySet = getDataKeySet();

        /* Register the Key */
        myKeySet.registerDataKey(this);
    }

    /**
     * Update keySetHash.
     * @param pPrimeHash this is the prime hash
     * @param pHash the new keySetHash
     * @return were there changes? true/false
     * @throws OceanusException on error
     */
    protected boolean updateKeySetHash(final Boolean pPrimeHash,
                                       final GordianKeySetHash pHash) throws OceanusException {
        /* Determine whether we need to update */
        if (!pPrimeHash.equals(isHashPrime())) {
            /* Store the current detail into history */
            pushHistory();

            /* Update the Security Control Key and obtain the new secured KeyDef */
            GordianKeySet myKeySet = pHash.getKeySet();
            setValueHashPrime(pPrimeHash);
            GordianFactory myFactory = myKeySet.getFactory();
            GordianKeyGenerator<?> myGenerator = myFactory.getKeyGenerator(getKeyType());
            setValueSecuredKeyDef(myGenerator.secureKey(getDataKey(), myKeySet));

            /* Check for changes */
            if (checkForHistory()) {
                return true;
            }
        }

        /* No changes */
        return false;
    }

    /**
     * DataKey List.
     */
    public static class DataKeyList
            extends DataList<DataKey, CryptographyDataType> {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * Construct an empty CORE DataKey list.
         * @param pData the DataSet for the list
         */
        protected DataKeyList(final DataSet<?, ?> pData) {
            this(pData, ListStyle.CORE);
        }

        /**
         * Construct an empty generic DataKey list.
         * @param pData the DataSet for the list
         * @param pStyle the style of the list
         */
        protected DataKeyList(final DataSet<?, ?> pData,
                              final ListStyle pStyle) {
            super(DataKey.class, pData, CryptographyDataType.DATAKEY, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DataKeyList(final DataKeyList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return DataKey.FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected DataKeyList getEmptyList(final ListStyle pStyle) {
            DataKeyList myList = new DataKeyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DataKeyList deriveList(final ListStyle pStyle) throws OceanusException {
            return (DataKeyList) super.deriveList(pStyle);
        }

        @Override
        public DataKeyList deriveDifferences(final DataSet<?, ?> pDataSet,
                                             final DataList<?, CryptographyDataType> pOld) {
            return (DataKeyList) super.deriveDifferences(pDataSet, pOld);
        }

        @Override
        public DataKey addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a DataKey */
            if (!(pItem instanceof DataKey)) {
                return null;
            }

            /* Clone the data key */
            DataKey myKey = new DataKey(this, (DataKey) pItem);
            add(myKey);
            return myKey;
        }

        @Override
        public DataKey addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataKey addValuesItem(final DataValues<CryptographyDataType> pValues) throws OceanusException {
            /* Create the dataKey */
            DataKey myKey = new DataKey(this, pValues);

            /* Check that this keyId has not been previously added */
            if (!isIdUnique(myKey.getId())) {
                myKey.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new PrometheusDataException(myKey, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myKey);

            /* Return it */
            return myKey;
        }

        /**
         * Add a new DataKey for the passed KeySet.
         * @param pKeySet the dataKeySet
         * @param pKeyType the KeyType
         * @return the new DataKey
         * @throws OceanusException on error
         */
        public DataKey createNewKey(final DataKeySet pKeySet,
                                    final GordianSymKeyType pKeyType) throws OceanusException {
            /* Create the key */
            DataKey myKey = new DataKey(this, pKeySet, pKeyType);

            /* Add to the list */
            add(myKey);
            return myKey;
        }

        /**
         * Add a clone of the passed DataKey for the passed KeySet.
         * @param pKeySet the KeySet
         * @param pDataKey the DataKey
         * @return the new DataKey
         * @throws OceanusException on error
         */
        public DataKey cloneDataKey(final DataKeySet pKeySet,
                                    final DataKey pDataKey) throws OceanusException {
            /* Build data values */
            DataValues<CryptographyDataType> myValues = new DataValues<>(DataKey.OBJECT_NAME);
            myValues.addValue(DataKey.FIELD_ID, pDataKey.getId());
            myValues.addValue(DataKey.FIELD_KEYSET, pKeySet);
            myValues.addValue(DataKey.FIELD_HASHPRIME, pDataKey.isHashPrime());
            myValues.addValue(DataKey.FIELD_KEYTYPE, pDataKey.getKeyType());
            myValues.addValue(DataKey.FIELD_KEYTYPEID, pDataKey.getKeyTypeId());
            myValues.addValue(DataKey.FIELD_KEYDEF, pDataKey.getSecuredKeyDef());
            myValues.addValue(DataKey.FIELD_KEY, pDataKey.getDataKey());

            /* Clone the dataKey */
            return addValuesItem(myValues);
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Just sort the list */
            reSort();
        }

        @Override
        protected DataMapItem<DataKey, CryptographyDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}
